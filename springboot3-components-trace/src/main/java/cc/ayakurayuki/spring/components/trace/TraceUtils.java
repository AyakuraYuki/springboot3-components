package cc.ayakurayuki.spring.components.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ayakura Yuki
 */
@Slf4j
public abstract class TraceUtils {

  public static final String EMPTY_STRING = "";

  /**
   * try to get trace id from {@link io.opentelemetry.api.trace.SpanContext}
   */
  public static String getTraceId() {
    try {
      Span currentSpan = Span.current();
      if (currentSpan.getSpanContext().isValid()) {
        SpanContext spanContext = currentSpan.getSpanContext();
        return spanContext.getTraceId();
      } else {
        return EMPTY_STRING;
      }
    } catch (Exception e) {
      log.warn(e.getLocalizedMessage(), e);
      return EMPTY_STRING;
    }
  }

}
