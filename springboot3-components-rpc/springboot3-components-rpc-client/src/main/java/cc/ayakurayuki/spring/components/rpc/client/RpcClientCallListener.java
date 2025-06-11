package cc.ayakurayuki.spring.components.rpc.client;

import cc.ayakurayuki.spring.components.rpc.core.StatusCode;
import cc.ayakurayuki.spring.components.stats.metrics.AYSimpleCollector;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RpcClientCallListener<S> extends ForwardingClientCallListener.SimpleForwardingClientCallListener<S> {

  private final String name;
  private final String method;
  private final long   startNano;
  private final Span   span;

  protected RpcClientCallListener(String name, ClientCall.Listener<S> delegate, String methos, Span span) {
    super(delegate);
    this.name = name;
    this.method = methos;
    this.span = span;
    this.startNano = System.nanoTime();
  }

  @Override
  public void onClose(Status status, Metadata trailers) {
    super.onClose(status, trailers);
    int code = StatusCode.toCode(status, trailers);
    RpcUtils.observeCallMetrics(
        this.name,
        this.method,
        String.valueOf(code),
        AYSimpleCollector.escapeMillisFromNanos(this.startNano, System.nanoTime())
    );
    if (this.span != null) {
      try (Scope ignored = this.span.makeCurrent()) {
        if (!status.isOk()) {
          this.span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, String.valueOf(code));
        }
      } finally {
        this.span.end();
      }
    }

    // post-handle meltdown breaker (optional)
  }

}
