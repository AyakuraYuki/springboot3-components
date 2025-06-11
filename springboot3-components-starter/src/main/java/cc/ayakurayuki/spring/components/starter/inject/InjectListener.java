package cc.ayakurayuki.spring.components.starter.inject;

import jakarta.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * Injectable listener
 *
 * @author kuaiyue
 */
@Slf4j
public class InjectListener implements ApplicationListener<ApplicationStartedEvent>, BeanFactoryAware, Ordered {

  private ListableBeanFactory beanFactory;

  @Override
  public void onApplicationEvent(@Nonnull ApplicationStartedEvent event) {
    log.info("start to inject static fields");
    int count = 0;
    for (Object value : this.beanFactory.getBeansWithAnnotation(Injectable.class).values()) {
      this.inject(value);
      count++;
    }
    log.info("end of inject static fields, %d %s has been injected".formatted(count, count <= 1 ? "class" : "classes"));
  }

  private void inject(Object bean) {
    for (Field declaredField : bean.getClass().getDeclaredFields()) {
      Inject annotation = declaredField.getAnnotation(Inject.class);
      if (annotation == null) {
        continue;
      }

      if (!Modifier.isStatic(declaredField.getModifiers())) {
        throw new RuntimeException("non-static field injection is not supported. class: [%s], field: [%s]".formatted(bean.getClass().getName(), declaredField.getName()));
      }

      String name = annotation.name();
      declaredField.setAccessible(true);

      try {
        Object injectBean;
        if (declaredField.getType() == List.class) {
          throw new RuntimeException("collections are not supported for injection");
        } else if (name == null || name.isEmpty()) {
          injectBean = this.beanFactory.getBean(declaredField.getType());
        } else {
          injectBean = this.beanFactory.getBean(name);
        }
        declaredField.set(bean, injectBean);
      } catch (NoSuchBeanDefinitionException e) {
        throw new BeanInitializationException("failed to inject bean to static field cause bean not found. class: %s, name: %s".formatted(declaredField.getType().getSimpleName(), name), e);
      } catch (IllegalAccessException | IllegalAccessError e) {
        throw new BeanInitializationException("failed to inject bean to static field cause bean not found. class: %s, name: %s".formatted(declaredField.getDeclaringClass().getSimpleName(), name), e);
      }
    }
  }

  @Override
  public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ListableBeanFactory listableBeanFactory) {
      this.beanFactory = listableBeanFactory;
      return;
    }
    throw new RuntimeException("beanFactory is not of type ListableBeanFactory");
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE - 1;
  }

}
