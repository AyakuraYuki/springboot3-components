package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.BindableService;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;

@Slf4j
public class RPCServiceBeanDiscoverer implements RPCServiceRegistry, ApplicationContextAware {

  private ApplicationContext context;

  @Override
  public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  @Override
  public Collection<RPCServiceDefinition> definedServices() {
    String[] beanNames = this.context.getBeanNamesForAnnotation(RPCService.class);
    List<RPCServiceDefinition> definitions = new ArrayList<>(beanNames.length);
    for (String beanName : beanNames) {
      BindableService bindableService = this.context.getBean(beanName, BindableService.class);
      ServerServiceDefinition serviceDefinition = bindableService.bindService();
      RPCService rpcService = AnnotatedElementUtils.findMergedAnnotation(bindableService.getClass(), RPCService.class);
      if (rpcService == null) {
        continue;
      }
      serviceDefinition = bindInterceptors(serviceDefinition, rpcService);
      definitions.add(new RPCServiceDefinition(bindableService.getClass(), bindableService, serviceDefinition));
      log.debug("found RPC service %s (bean: %s, class: %s)".formatted(serviceDefinition.getServiceDescriptor().getName(), beanName, bindableService.getClass().getName()));
    }
    return definitions;
  }

  private ServerServiceDefinition bindInterceptors(ServerServiceDefinition definition, RPCService rpcService) {
    Set<ServerInterceptor> interceptorSet = new LinkedHashSet<>();
    for (Class<? extends ServerInterceptor> serverInterceptorClass : rpcService.interceptors()) {
      ServerInterceptor serverInterceptor;
      if (this.context.getBeanNamesForType(serverInterceptorClass).length > 0) {
        serverInterceptor = this.context.getBean(serverInterceptorClass);
      } else {
        try {
          serverInterceptor = serverInterceptorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
          throw new BeanCreationException("failed to create interceptor instance", e);
        }
      }
      interceptorSet.add(serverInterceptor);
    }
    return ServerInterceptors.interceptForward(definition, new ArrayList<>(interceptorSet));
  }

}
