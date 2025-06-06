package cc.ayakurayuki.spring.components.logging.support.annotation;

import cc.ayakurayuki.spring.components.logging.support.Level;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.METHOD
})
public @interface LogLevel {

  Level value();

}
