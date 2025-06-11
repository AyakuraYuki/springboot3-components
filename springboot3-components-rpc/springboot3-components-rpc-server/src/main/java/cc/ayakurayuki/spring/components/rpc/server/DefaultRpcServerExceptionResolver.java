package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.rpc.server.exception.ServerRpcExceptions;
import cc.ayakurayuki.spring.components.rpc.server.exception.WrappedStatus;
import io.grpc.ServerCall;
import jakarta.annotation.Nonnull;

public class DefaultRpcServerExceptionResolver implements RpcServerExceptionResolver {

  @SuppressWarnings("rawtypes")
  @Override
  public <ReqT, RespT> boolean resolveException(@Nonnull ServerCall<ReqT, RespT> serverCall, @Nonnull Context context, Throwable e) {
    if (serverCall instanceof RpcServerCall rpcServerCall && rpcServerCall.isClosed()) {
      return false;
    }
    context.setException(e);
    WrappedStatus ws = ServerRpcExceptions.convertStatus(e);
    serverCall.close(ws.status(), ws.trailers());
    return true;
  }

}
