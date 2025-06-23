package cc.ayakurayuki.spring.components.starter.http.server;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.errors.exception.ServerException;
import cc.ayakurayuki.spring.components.http.server.ratelimiter.RateLimit;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author Ayakura Yuki
 */
@Aspect
public class RateLimitAspect {

  private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

  @Before("@annotation(rateLimit)")
  public void rateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
    String key = joinPoint.getSignature().toLongString();
    double windowTime = (double) rateLimit.timeUnit().toSeconds(rateLimit.time());
    RateLimiter limiter = limiters.computeIfAbsent(key, k -> RateLimiter.create(rateLimit.count() / windowTime));
    if (!limiter.tryAcquire()) {
      throw ServerException.code(ServerCode.LIMIT_EXCEEDED);
    }
  }

}
