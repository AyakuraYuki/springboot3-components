package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.ServerInterceptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCService {

  Class<? extends ServerInterceptor>[] interceptors() default {};

}
