package cc.ayakurayuki.spring.components.http.server.utility;

import cc.ayakurayuki.spring.components.context.Context;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;

public class HttpRequestAttributes {

  // http request start
  public static final HttpRequestAttribute<Long> REQUEST_START = HttpRequestAttribute.of(HttpRequestAttributes.class.getName() + "request.start");

  // http request context
  public static final HttpRequestAttribute<Context> REQUEST_CONTEXT = HttpRequestAttribute.of(HttpRequestAttributes.class.getName() + "request.context");

  // http request opentelemetry span
  public static final HttpRequestAttribute<Span> REQUEST_SPAN = HttpRequestAttribute.of(HttpRequestAttribute.class.getName() + "request.span");

  // http request opentelemetry scope
  public static final HttpRequestAttribute<Scope> REQUEST_SCOPE = HttpRequestAttribute.of(HttpRequestAttribute.class.getName() + "request.scope");

  // http json response code
  public static final HttpRequestAttribute<Integer> RESPONSE_CODE = HttpRequestAttribute.of(HttpRequestAttributes.class.getName() + ".response.code");

  // http json response message
  public static final HttpRequestAttribute<String> RESPONSE_MESSAGE = HttpRequestAttribute.of(HttpRequestAttributes.class.getName() + ".response.message");

  public static void resetAttributes(HttpServletRequest request) {
    HttpRequestAttributes.REQUEST_START.resetAttribute(request);
    HttpRequestAttributes.REQUEST_CONTEXT.resetAttribute(request);
    HttpRequestAttributes.RESPONSE_CODE.resetAttribute(request);
    HttpRequestAttributes.RESPONSE_MESSAGE.resetAttribute(request);
  }

}
