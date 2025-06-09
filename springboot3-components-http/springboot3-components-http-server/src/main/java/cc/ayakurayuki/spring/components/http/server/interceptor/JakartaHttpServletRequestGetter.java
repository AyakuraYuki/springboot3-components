package cc.ayakurayuki.spring.components.http.server.interceptor;

import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

final class JakartaHttpServletRequestGetter implements TextMapGetter<HttpServletRequest> {

  public static final JakartaHttpServletRequestGetter GETTER = new JakartaHttpServletRequestGetter();

  @Override
  public Iterable<String> keys(HttpServletRequest carrier) {
    return Collections.list(carrier.getHeaderNames());
  }

  @Override
  public String get(HttpServletRequest carrier, @Nonnull String key) {
    if (carrier == null) {
      return "";
    }
    return carrier.getHeader(key);
  }

}
