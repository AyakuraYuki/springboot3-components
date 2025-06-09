package cc.ayakurayuki.spring.components.http.server.interceptor;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.concurrent.Contexts;
import cc.ayakurayuki.spring.components.context.http.HttpContext;
import cc.ayakurayuki.spring.components.context.rpc.RpcHeaders;
import cc.ayakurayuki.spring.components.errors.ServerCodes;
import cc.ayakurayuki.spring.components.http.server.utility.HttpRequestAttributes;
import cc.ayakurayuki.spring.components.stats.metrics.AYSimpleCollector;
import cc.ayakurayuki.spring.components.trace.CompositeOpenTelemetry;
import cc.ayakurayuki.spring.components.trace.SpanTags;
import cc.ayakurayuki.spring.components.utility.logging.LogUtils;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.api.baggage.BaggageEntry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

public class HttpServerStats {

  private static final Logger log = LoggerFactory.getLogger("http-access-log");

  public static void startStats(String patternUrl, HttpServletRequest request, HttpContext context) {
    HttpRequestAttributes.REQUEST_START.setAttribute(request, System.nanoTime());
    HttpRequestAttributes.REQUEST_CONTEXT.setAttribute(request, context);
    io.opentelemetry.context.Context parentContext = CompositeOpenTelemetry.get()
        .getPropagators()
        .getTextMapPropagator()
        .extract(io.opentelemetry.context.Context.current(), request, getGetter());
    io.opentelemetry.context.Context otelContext = withHttpContext(context, parentContext);
    Span span = starterTrace(patternUrl, request, otelContext);
    Scope scope = otelContext.with(span).makeCurrent();
    HttpRequestAttributes.REQUEST_SPAN.setAttribute(request, span);
    HttpRequestAttributes.REQUEST_SCOPE.setAttribute(request, scope);
  }

  private static Span starterTrace(String patternUrl, HttpServletRequest request, io.opentelemetry.context.Context parentContext) {
    if (isLegalRequest(patternUrl) && CompositeOpenTelemetry.get().isNoop()) {
      log.warn("Found noop opentelemetry sdk implementation, It seems forget to add '@EnableTracing' annotation in your application ?");
    }
    return CompositeOpenTelemetry.get()
        .getTracer(HttpServerStats.class.getPackage().getName())
        .spanBuilder(patternUrl)
        .setParent(parentContext)
        .setSpanKind(SpanKind.SERVER)
        .setAttribute(SpanTags.COMPONENT, "http")
        .setAttribute(SpanTags.HTTP_METHOD, request.getMethod())
        .setAttribute(SpanTags.HTTP_URL, Strings.isNullOrEmpty(request.getQueryString())
                                         ? request.getRequestURL().toString()
                                         : String.format("%s?%s", request.getRequestURL(), request.getQueryString()))
        .setAttribute(SpanTags.SERVER_THROUGH_IN, request.getContentLength())
        .startSpan();
  }

  private static io.opentelemetry.context.Context withHttpContext(HttpContext httpContext, io.opentelemetry.context.Context otelContext) {
    Baggage baggage = Baggage.fromContext(otelContext);
    Map<String, BaggageEntry> baggageEntryMap = baggage.asMap();
    BaggageBuilder newBaggageBuilder = baggage.toBuilder();
    if (!baggageEntryMap.containsKey(RpcHeaders.REMOTE_IP)
        && Objects.nonNull(httpContext.getIp())
        && Objects.nonNull(httpContext.getIp().userIP())) {
      newBaggageBuilder.put(RpcHeaders.REMOTE_IP, httpContext.getIp().userIP());
    }
    if (!baggageEntryMap.containsKey(RpcHeaders.PATH)
        && Objects.nonNull(httpContext.getPath())) {
      newBaggageBuilder.put(RpcHeaders.PATH, httpContext.getPath());
    }
    return otelContext.with(newBaggageBuilder.build());
  }

  @SuppressWarnings("rawtypes")
  public static void finishStats(HttpServletRequest request, HttpServletResponse response, Context context) {
    finishMetrics(request, response, context);
    finishTrace(request, response);
    HttpRequestAttributes.resetAttributes(request);
  }

  @SuppressWarnings("rawtypes")
  private static void finishMetrics(HttpServletRequest request, HttpServletResponse response, Context context) {
    long start = Optional.ofNullable(HttpRequestAttributes.REQUEST_START.getAttribute(request)).orElse(System.nanoTime());

    double elapsed = AYSimpleCollector.escapeMillisFromNanos(start, System.nanoTime());
    String path = context.getPath();

    boolean isLegalRequest = isLegalRequest(path);
    if (!isLegalRequest) {
      return;
    }

    final int error = context.getError();
    final int status = response.getStatus();
    final int code = Optional.ofNullable(HttpRequestAttributes.RESPONSE_CODE.getAttribute(request)).orElse(response.getStatus());
    final String message = Optional.ofNullable(HttpRequestAttributes.RESPONSE_MESSAGE.getAttribute(request)).orElse("");

    // HttpServerMetrics.HTTP_SERVER_TOTAL.inc(path); // 已使用 http_server_requests_seconds_count 指标
    HttpServerMetrics.HTTP_SERVER_DURATION.observe(elapsed, path);
    HttpServerMetrics.HTTP_SERVER_CODE.inc(path, String.valueOf(code));
    MDC.put("http_status", String.valueOf(status));
    MDC.put("http_method", request.getMethod());
    MDC.put("http_referer", LogUtils.separatorProcess(request.getHeader("Referer")));
    MDC.put("http_user_agent", LogUtils.separatorProcess(request.getHeader("User-Agent")));
    MDC.put("error", String.valueOf(error));
    MDC.put("biz_msg", LogUtils.separatorProcess(message));
    MDC.put("biz_code", String.valueOf(code));
    MDC.put("ts", String.valueOf(BigDecimal.valueOf(elapsed / 1000)
                                     .setScale(3, RoundingMode.HALF_UP)
                                     .floatValue()));
    // access log
    if ((ServerCodes.is5xx(code) && Contexts.is5xxWithLog()) || Contexts.isError()) {
      // (5xx && error != -1) || error = 1
      log.error(Strings.nullToEmpty(message), context.getException());
    } else if (ServerCodes.isOK(code)) {
      // success
      log.info(Strings.nullToEmpty(message), context.getException());
    } else {
      // biz error
      log.warn(Strings.nullToEmpty(message), context.getException());
    }
  }

  private static void finishTrace(HttpServletRequest request, HttpServletResponse response) {
    Optional.ofNullable(HttpRequestAttributes.REQUEST_SPAN.getAttribute(request)).ifPresent(span -> {
      String contentLength = Strings.nullToEmpty(response.getHeader(HttpHeaders.CONTENT_LENGTH));
      span.setAttribute(SpanTags.HTTP_STATUS_CODE, response.getStatus());
      span.setAttribute(SpanTags.SERVER_THROUGH_OUT, Optional.ofNullable(Ints.tryParse(contentLength)).orElse(0));
      span.end();
    });
    Optional.ofNullable(HttpRequestAttributes.REQUEST_SCOPE.getAttribute(request)).ifPresent(Scope::close);
  }

  private static boolean isLegalRequest(String requestURI) {
    if (requestURI.equals("/health/heartbeat")) {
      return false;
    }
    return !requestURI.startsWith("/monitor/ping")
        && !requestURI.startsWith("/metrics");
  }

  private static TextMapGetter<HttpServletRequest> getGetter() {
    return JakartaHttpServletRequestGetter.GETTER;
  }

}
