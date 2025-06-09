package cc.ayakurayuki.spring.components.http.server.interceptor;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.http.HttpContext;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@SuppressWarnings("rawtypes")
public class HttpContextInterceptor implements HandlerInterceptor, Ordered {

  @Override
  public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
    if (isNotHandlerMethod(handler)) {
      return true;
    }
    String patternUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    if (patternUrl == null) {
      patternUrl = request.getRequestURI();
    }
    HttpContext context = HttpContext.create(patternUrl, request);
    context.setRealPath(request.getRequestURI());
    context.attach();
    HttpServerStats.startStats(patternUrl, request, context);
    return true;
  }

  @Override
  public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) throws Exception {
    if (isNotHandlerMethod(handler)) {
      return;
    }
    Context context = Context.current();
    HttpServerStats.finishStats(request, response, context);
    context.detach(null);
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }

  private boolean isNotHandlerMethod(Object handler) {
    return !(handler instanceof HandlerMethod);
  }

}
