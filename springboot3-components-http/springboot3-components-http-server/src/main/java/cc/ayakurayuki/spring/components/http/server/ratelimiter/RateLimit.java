package cc.ayakurayuki.spring.components.http.server.ratelimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Ayakura Yuki
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

  /**
   * times allowed in each window, default to 100
   */
  int count() default 100;

  /**
   * duration seconds of each window, default to 1 second
   */
  int time() default 1;

  /**
   * time unit of the duration to each window, default as second
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

}
