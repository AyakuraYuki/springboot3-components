package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.ServerInterceptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
@RPCService
public @interface RPCController {

  @AliasFor(annotation = RPCService.class, attribute = "interceptors")
  Class<? extends ServerInterceptor>[] interceptors() default {};

}
