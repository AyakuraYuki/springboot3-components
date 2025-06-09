package cc.ayakurayuki.spring.components.http.server.utility;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpRequestAttribute<V> {

  private final String key;

  private HttpRequestAttribute(@Nonnull String key) {
    this.key = key;
  }

  public static <T> HttpRequestAttribute<T> of(String key) {
    return new HttpRequestAttribute<T>(key);
  }

  public String getKey() {
    return key;
  }

  public void setAttribute(HttpServletRequest request, V value) {
    if (request != null) {
      request.setAttribute(key, value);
    }
  }

  @SuppressWarnings("unchecked")
  public V getAttribute(HttpServletRequest request) {
    if (request != null) {
      return (V) request.getAttribute(key);
    }
    return null;
  }

  public V getAttribute() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr != null) {
      return getAttribute(attr.getRequest());
    }
    return null;
  }

  public void setAttribute(V value) {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr != null) {
      setAttribute(attr.getRequest(), value);
    }
  }

  public void resetAttribute(HttpServletRequest request) {
    if (request != null) {
      request.removeAttribute(key);
    }
  }

}
