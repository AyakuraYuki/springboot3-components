package cc.ayakurayuki.spring.components.starter.rpc.client;

import cc.ayakurayuki.spring.components.rpc.client.CallOptionKeys;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import jakarta.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class RPCClientBeanPostProcessor implements BeanPostProcessor, DisposableBean, ApplicationContextAware, Ordered {

  private final Map<String, Channel>          channelMap = new ConcurrentHashMap<>();
  private final RPCNamingClientChannelFactory channelFactory;
  private       ApplicationContext            context;

  public RPCClientBeanPostProcessor(RPCNamingClientChannelFactory channelFactory) {
    this.channelFactory = channelFactory;
  }

  @Override
  public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
    this.context = applicationContext;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
    Class clazz = bean.getClass();

    do {

      Object target = getTargetBean(bean);

      for (Field field : clazz.getDeclaredFields()) {
        if (!field.isAnnotationPresent(RPCClient.class)) {
          continue;
        }

        RPCClient annotation = AnnotationUtils.getAnnotation(field, RPCClient.class);
        if (annotation == null) {
          continue;
        }

        String name = annotation.value();
        String channelKey = "%s".formatted(name); // copy
        Channel channel = this.channelMap.computeIfAbsent(channelKey, n -> this.channelFactory.createChannel(name));
        Channel interceptedChannel = bindInterceptors(channel, annotation);

        Object instance = determineInstance(name, field, interceptedChannel);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, instance);
      }

      clazz = clazz.getSuperclass();

    } while (clazz != null);

    return bean;
  }

  private Channel bindInterceptors(Channel channel, RPCClient rpcClientAnnotation) {
    Set<ClientInterceptor> interceptorSet = Sets.newLinkedHashSet();
    for (Class<? extends ClientInterceptor> clientInterceptorClass : rpcClientAnnotation.interceptors()) {
      ClientInterceptor clientInterceptor;
      if (this.context.getBeanNamesForType(clientInterceptorClass).length > 0) {
        clientInterceptor = this.context.getBean(clientInterceptorClass);
      } else {
        try {
          clientInterceptor = clientInterceptorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
          throw new BeanCreationException("failed to create interceptor instance: %s".formatted(clientInterceptorClass), e);
        }
      }
      interceptorSet.add(clientInterceptor);
    }
    return ClientInterceptors.interceptForward(channel, Lists.newArrayList(interceptorSet));
  }

  Object determineInstance(String name, Field field, Channel channel) {
    Class<?> type = field.getType();
    if (Channel.class.isAssignableFrom(type)) {
      return channel;
    }
    if (AbstractStub.class.isAssignableFrom(type)) {
      AbstractStub<?> stub = createStub(type.asSubclass(AbstractStub.class), channel);
      return stub.withWaitForReady()
          .withOption(
              CallOptionKeys.TIMEOUT_KEY,
              Duration.ofMillis(this.channelFactory.channelProperties(name).getCallTimeout())
          );
    } else {
      throw new BeanInstantiationException(type, "cannot determine %s to `@RPCClient` annotated field".formatted(type.getName()));
    }
  }

  private <T extends AbstractStub<T>> T createStub(final Class<T> stubType, final Channel channel) {
    try {
      // First try the public static factory method
      final String methodName = deriveStubFactoryMethodName(stubType);
      final Class<?> enclosingClass = stubType.getEnclosingClass();
      final Method factoryMethod = enclosingClass.getMethod(methodName, Channel.class);
      return stubType.cast(factoryMethod.invoke(null, channel));
    } catch (final Exception e) {
      try {
        // Use the private constructor as backup
        final Constructor<T> constructor = stubType.getDeclaredConstructor(Channel.class);
        constructor.setAccessible(true);
        return constructor.newInstance(channel);
      } catch (final Exception e1) {
        e.addSuppressed(e1);
      }
      throw new BeanInstantiationException(stubType, "failed to create gRPC client", e);
    }
  }

  private String deriveStubFactoryMethodName(final Class<? extends AbstractStub<?>> stubType) {
    if (AbstractAsyncStub.class.isAssignableFrom(stubType)) {
      return "newStub";
    } else if (AbstractBlockingStub.class.isAssignableFrom(stubType)) {
      return "newBlockingStub";
    } else if (AbstractFutureStub.class.isAssignableFrom(stubType)) {
      return "newFutureStub";
    } else {
      throw new IllegalArgumentException("unsupported stub type: %s -> please report this issue".formatted(stubType.getName()));
    }
  }

  @Override
  public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
    return bean;
  }

  private Object getTargetBean(Object bean) {
    Object target = bean;
    while (AopUtils.isAopProxy(target)) {
      try {
        target = ((Advised) target).getTargetSource().getTarget();
      } catch (Exception ignored) {
      }
    }
    return target;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  public void destroy() {
    for (Channel channel : this.channelMap.values()) {
      if (channel instanceof ManagedChannel managedChannel) {
        managedChannel.shutdown();
      }
    }
  }

}
