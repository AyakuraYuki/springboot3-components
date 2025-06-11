package cc.ayakurayuki.spring.components.starter.boot;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * Application Boot configuration
 * <p>
 * Enabled as default
 * <p>
 * <ul>
 *   <li>properties: {@code application.boot.enabled}</li>
 *   <li>value: a single boolean value in {@code true / false}</li>
 * </ul>
 */
@Configuration
@ConditionalOnProperty(prefix = "application.boot", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(BootstrapSource.class)
public class ApplicationBootConfiguration {

  @EventListener(ContextRefreshedEvent.class)
  public void onApplicationEvent(ContextRefreshedEvent event) {
    ApplicationBootstrap bootstrap = new ApplicationBootstrap();
    bootstrap.boot();
  }

}
