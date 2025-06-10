package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.context.rpc.RpcContext;
import cc.ayakurayuki.spring.components.context.rpc.RpcHeaders;
import cc.ayakurayuki.spring.components.trace.CompositeOpenTelemetry;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.api.baggage.BaggageEntry;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServerInterceptor implements ServerInterceptor {

  private final String                     compressor;
  private final RpcServerExceptionResolver exceptionResolver;

  public RpcServerInterceptor(@Nullable String compressor, @Nullable RpcServerExceptionResolver exceptionResolver) {
    this.compressor = compressor;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  public <ReqT, RspT> Listener<ReqT> interceptCall(ServerCall<ReqT, RspT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RspT> next) {
    final RpcContext context = RpcContext.create(metadata, serverCall);
    io.opentelemetry.context.Context otelContext = CompositeOpenTelemetry.get()
        .getPropagators()
        .getTextMapPropagator()
        .extract(io.opentelemetry.context.Context.current(), metadata, GrpcExtractAdapter.GETTER);
    otelContext = withRpcContext(context, otelContext);

    final RpcServerCall<ReqT, RspT> rpcServerCall = new RpcServerCall<>(serverCall, context, otelContext, this.exceptionResolver);
    final Supplier<ServerCall.Listener<ReqT>> supplier = () -> {
      if (this.compressor != null) {
        rpcServerCall.setCompression(this.compressor);
      }
      ServerCall.Listener<ReqT> listener = next.startCall(rpcServerCall, metadata);
      return new RpcServerCallListener<>(listener, rpcServerCall);
    };

    try {
      return context.supplier(supplier);
    } catch (Exception e) {
      if (this.exceptionResolver != null && this.exceptionResolver.resolveException(rpcServerCall, context, e)) {
        io.grpc.Context ctx = io.grpc.Context.current();
        if (ctx instanceof Context.CancellableContext cancel) {
          cancel.cancel(e);
        }
        return new NoopListener<>(); // to avoid NPE check
      }
      throw e;
    }
  }

  /**
   * setup baggage to opentelemetry context
   */
  private io.opentelemetry.context.Context withRpcContext(RpcContext rpcContext, io.opentelemetry.context.Context otelContext) {
    Baggage baggage = Baggage.fromContext(otelContext);
    Map<String, BaggageEntry> baggageEntryMap = baggage.asMap();
    BaggageBuilder baggageBuilder = baggage.toBuilder();

    if (!baggageEntryMap.containsKey(RpcHeaders.REMOTE_IP) && Objects.nonNull(rpcContext.getIp()) && Objects.nonNull(rpcContext.getIp().userIP())) {
      baggageBuilder.put(RpcHeaders.REMOTE_IP, rpcContext.getIp().userIP());
    }

    if (!baggageEntryMap.containsKey(RpcHeaders.CALLER) && Objects.nonNull(rpcContext.getCaller())) {
      baggageBuilder.put(RpcHeaders.CALLER, rpcContext.getCaller());
    }

    if (!baggageEntryMap.containsKey(RpcHeaders.PATH) && Objects.nonNull(rpcContext.getPath())) {
      baggageBuilder.put(RpcHeaders.PATH, rpcContext.getPath());
    }

    return otelContext.with(baggageBuilder.build());
  }

}
