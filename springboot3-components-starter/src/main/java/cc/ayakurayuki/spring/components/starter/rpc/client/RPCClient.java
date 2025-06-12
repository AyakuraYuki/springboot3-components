package cc.ayakurayuki.spring.components.starter.rpc.client;

import io.grpc.ClientInterceptor;
import jakarta.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC Client injection annotation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCClient {

  /**
   * A unique application id, also auto-inject configuration by this name
   *
   * @see RPCChannelProperties
   */
  @Nonnull String value();

  Class<? extends ClientInterceptor>[] interceptors() default {};

}
