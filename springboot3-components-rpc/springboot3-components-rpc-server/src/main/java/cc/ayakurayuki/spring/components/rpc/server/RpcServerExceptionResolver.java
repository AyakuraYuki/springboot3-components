package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.context.Context;
import io.grpc.ServerCall;
import jakarta.annotation.Nonnull;

public interface RpcServerExceptionResolver {

  @SuppressWarnings("rawtypes")
  <ReqT, RespT> boolean resolveException(@Nonnull ServerCall<ReqT, RespT> serverCall, @Nonnull Context context, Throwable e);

}
