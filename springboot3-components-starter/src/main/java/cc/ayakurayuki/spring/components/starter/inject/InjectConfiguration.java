package cc.ayakurayuki.spring.components.starter.inject;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * Inject configuration
 *
 * @author kuaiyue
 */
@ConditionalOnClass(InjectListener.class)
public class InjectConfiguration {

  @Bean
  public InjectListener injectListener() {
    return new InjectListener();
  }

}
