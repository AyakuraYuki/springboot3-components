package cc.ayakurayuki.spring.components.rpc.client;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.rpc.RpcHeaders;
import cc.ayakurayuki.spring.components.env.Environment;
import cc.ayakurayuki.spring.components.env.EnvironmentKeys;
import cc.ayakurayuki.spring.components.trace.CompositeOpenTelemetry;
import cc.ayakurayuki.spring.components.trace.SpanTags;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@SuppressWarnings("rawtypes")
public class RpcClientCall<R, S> extends ClientInterceptors.CheckedForwardingClientCall<R, S> {

  private static final String APPID;

  static {
    APPID = Environment.ofNullable(EnvironmentKeys.APP_ID).orElse("");
  }

  private final String  name;
  private final String  method;
  private final Context context;
  private final Tracer  tracer;

  protected RpcClientCall(String name, ClientCall<R, S> delegate, String method) {
    super(delegate);
    this.name = name;
    this.method = method;
    this.context = Context.current();
    this.tracer = CompositeOpenTelemetry.get().getTracer(getClass().getPackage().getName());
  }

  @Override
  protected void checkedStart(Listener<S> responseListener, Metadata headers) {
    Span span = startSpan(this.method);
    try (Scope ignored = span.makeCurrent()) {
      // meltdown-breaker-check around the following codes in this block (optional)
      attachHeaders(headers, span);
      this.delegate().start(new RpcClientCallListener<>(this.name, responseListener, this.method, span), headers);
    }
  }

  private Span startSpan(String method) {
    return this.tracer.spanBuilder(method)
        .setSpanKind(SpanKind.CLIENT)
        .setAttribute(SpanTags.COMPONENT, "gRPC")
        .setAttribute(SpanTags.PEER_SERVICE, method)
        .startSpan();
  }

  private void attachHeaders(Metadata headers, Span span) {
    io.opentelemetry.context.Context otelContext = io.opentelemetry.context.Context.current();

    // attach baggage
    Baggage baggage = Baggage.fromContext(otelContext);
    BaggageBuilder baggageBuilder = baggage.toBuilder();
    baggageBuilder.put(RpcHeaders.CALLER, APPID);
    GrpcInjectAdapter.SETTER.set(headers, RpcHeaders.CALLER, APPID);
    if (this.context != null && this.context.getIp() != null && this.context.getIp().userIP() != null) {
      baggageBuilder.put(RpcHeaders.REMOTE_IP, this.context.getIp().userIP());
      GrpcInjectAdapter.SETTER.set(headers, RpcHeaders.REMOTE_IP, this.context.getIp().userIP());
    }
    baggageBuilder.remove(RpcHeaders.PATH);
    otelContext = otelContext.with(baggageBuilder.build());

    // attach span
    otelContext = otelContext.with(span);
    GrpcInjectAdapter.SETTER.set(headers, RpcHeaders.AY_MICRO_TRACE_HEADER_TRACE_ID, span.getSpanContext().getTraceId());
    GrpcInjectAdapter.SETTER.set(headers, RpcHeaders.AY_MICRO_TRACE_HEADER_SPAN_ID, span.getSpanContext().getSpanId());

    // attach traceId to Grpc header
    CompositeOpenTelemetry.get().getPropagators().getTextMapPropagator().inject(otelContext, headers, GrpcInjectAdapter.SETTER);
  }

}
