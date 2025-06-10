package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.rpc.RpcContext;
import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.errors.ServerCodes;
import cc.ayakurayuki.spring.components.rpc.core.StatusCode;
import cc.ayakurayuki.spring.components.stats.metrics.AYSimpleCollector;
import cc.ayakurayuki.spring.components.trace.CompositeOpenTelemetry;
import cc.ayakurayuki.spring.components.trace.SpanTags;
import com.google.common.base.Strings;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

class RpcServerCall<R, S> extends ForwardingServerCall.SimpleForwardingServerCall<R, S> {

  private static final TextFormat.Printer PRINTER = TextFormat.printer().escapingNonAscii(false);

  private static final Logger log = LoggerFactory.getLogger("grpc-access-log");

  private final    LongAdder                         receivedBytes = new LongAdder();
  private final    LongAdder                         sentBytes     = new LongAdder();
  private final    RpcServerExceptionResolver        exceptionResolver;
  private final    String                            method;
  private final    RpcContext                        context;
  private final    io.opentelemetry.api.trace.Tracer tracer;
  private final    io.opentelemetry.context.Context  spanContext;
  private final    long                              startNano;
  private volatile boolean                           closed        = false;

  RpcServerCall(@Nonnull ServerCall<R, S> delegate,
                @Nonnull RpcContext ctx,
                @Nonnull io.opentelemetry.context.Context otelContext,
                @Nullable RpcServerExceptionResolver exceptionResolver) {
    super(delegate);
    this.method = delegate.getMethodDescriptor().getFullMethodName();
    this.context = ctx;
    this.tracer = CompositeOpenTelemetry.get().getTracer(getClass().getPackage().getName());
    this.exceptionResolver = exceptionResolver;
    this.startNano = System.nanoTime();
    this.spanContext = startContext(this.method, otelContext);
  }

  private io.opentelemetry.context.Context startContext(String method, io.opentelemetry.context.Context otelContext) {
    Span span = this.tracer.spanBuilder(method)
        .setParent(otelContext)
        .setSpanKind(SpanKind.SERVER)
        .setAttribute(SpanTags.COMPONENT, "gRPC")
        .startSpan();
    return otelContext.with(span);
  }

  public final void runInContext(@Nonnull Runnable runnable) {
    try {
      this.context.run(this.spanContext.wrap(runnable));
    } catch (Exception e) {
      if (this.exceptionResolver != null && this.exceptionResolver.resolveException(this, this.context, e)) {
        io.grpc.Context ctx = io.grpc.Context.current();
        if (ctx instanceof io.grpc.Context.CancellableContext cancel) {
          cancel.cancel(e);
        }
        return;
      }
      throw e;
    }
  }

  public void onReceiveMessage(R received) {
    if (received instanceof Message message) {
      this.context.setParameter(message.getAllFields());
      this.context.setDebugParameter(PRINTER.emittingSingleLine(true).printToString(message));
      this.receivedBytes.add(message.getSerializedSize());
    }
  }

  @Override
  public void sendMessage(S sent) {
    super.sendMessage(sent);
    if (sent instanceof Message message) {
      this.sentBytes.add(message.getSerializedSize());
    }
  }

  @Override
  public final void close(Status status, Metadata trailers) {
    this.closed = true;
    this.context.run(() -> {
      try (Scope ignored = this.spanContext.makeCurrent()) {
        super.close(status, trailers);
        accessLog(status, trailers, AYSimpleCollector.escapeMillisFromNanos(this.startNano, System.nanoTime()));
      } finally {
        finishContext(status);
      }
    });
  }

  public boolean isClosed() {
    return this.closed;
  }

  @SuppressWarnings("rawtypes")
  private void accessLog(Status status, Metadata trailers, double escapeMillis) {
    final Context ctx = this.context;
    if (ctx == null) {
      return;
    }

    RpcServerMetrics.RPC_SERVER_DURATION.observe(escapeMillis, this.method, ctx.getCaller());
    RpcServerMetrics.RPC_SERVER_TOTAL.inc(this.method, ctx.getCaller());

    final ServerCode sc = StatusCode.toServerCode(status, trailers);
    final int code = sc.code();
    final String msg = sc.message();
    final int error = ctx.getError();
    RpcServerMetrics.RPC_SERVER_CODE.inc(this.method, ctx.getCaller(), String.valueOf(code));

    MDC.put("error", String.valueOf(error));
    MDC.put("biz_msg", msg);
    MDC.put("biz_code", String.valueOf(code));
    final float ts = BigDecimal.valueOf(escapeMillis / 1000)
        .setScale(3, RoundingMode.HALF_UP)
        .floatValue();
    MDC.put("ts", String.valueOf(ts));

    if (ServerCodes.isOK(code)) {
      log.info(Strings.nullToEmpty(msg), ctx.getException()); // success
    } else if (ServerCodes.is5xx(code) || ctx.getError() == 1) {
      log.error(Strings.nullToEmpty(msg), ctx.getException()); // 5xx error
    } else {
      log.warn(Strings.nullToEmpty(msg), ctx.getException()); // warning the biz error
    }
  }

  private void finishContext(Status status) {
    Span span = Span.fromContext(this.spanContext);
    span.setAttribute(SpanTags.SERVER_THROUGH_IN, this.receivedBytes.sum());
    span.setAttribute(SpanTags.SERVER_THROUGH_OUT, this.sentBytes.sum());
    if (!status.isOk()) {
      span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR);
    }
    span.end();
  }

  @SneakyThrows
  private String printStackTrace(Throwable e) {
    if (e == null) {
      return "";
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    e.printStackTrace(new PrintStream(baos, false, StandardCharsets.UTF_8));
    return baos.toString(StandardCharsets.UTF_8);
  }

}
