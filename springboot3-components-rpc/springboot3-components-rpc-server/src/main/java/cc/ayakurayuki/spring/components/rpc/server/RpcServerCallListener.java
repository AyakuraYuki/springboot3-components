package cc.ayakurayuki.spring.components.rpc.server;

import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;
import jakarta.annotation.Nonnull;

class RpcServerCallListener<ReqT, RspT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

  private final RpcServerCall<ReqT, RspT> call;

  protected RpcServerCallListener(ServerCall.Listener<ReqT> delegate, @Nonnull RpcServerCall<ReqT, RspT> call) {
    super(delegate);
    this.call = call;
  }

  @Override
  public void onReady() {
    this.call.runInContext(super::onReady);
  }

  @Override
  public void onMessage(ReqT message) {
    this.call.runInContext(() -> {
      this.call.onReceiveMessage(message);
      super.onMessage(message);
    });
  }

  @Override
  public void onComplete() {
    this.call.runInContext(super::onComplete);
  }

  @Override
  public void onHalfClose() {
    this.call.runInContext(super::onHalfClose);
  }

  @Override
  public void onCancel() {
    this.call.runInContext(super::onCancel);
  }

}
