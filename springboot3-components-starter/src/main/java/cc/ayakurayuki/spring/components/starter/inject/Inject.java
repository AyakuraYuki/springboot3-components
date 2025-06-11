package cc.ayakurayuki.spring.components.starter.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * inject spring bean to a static field by given bean name or bean type
 *
 * @author kuaiyue
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

  /**
   * inject instance by name
   */
  String name() default "";

}
