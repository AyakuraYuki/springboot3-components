package cc.ayakurayuki.spring.components.rpc.client;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import com.google.common.collect.Sets;
import io.grpc.Attributes;
import jakarta.annotation.Nonnull;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcUtils {

  public static final int DEFAULT_WEIGHT = 10;

  public static final Attributes.Key<Integer> WEIGHT_KEY = Attributes.Key.create("pleiades.discovery.weight");

  /**
   * meltdown error code set
   */
  public static final Set<Integer> BREAKER_CODE = Sets.newHashSet(
      ServerCode.TOO_MANY_REQUESTS.code(),
      ServerCode.NOT_IMPLEMENTED.code(),
      ServerCode.SERVICE_UNAVAILABLE.code(),
      ServerCode.DEADLINE_EXCEEDED.code(),
      ServerCode.LIMIT_EXCEEDED.code()
  );

  static void observeCallMetrics(@Nonnull String name, @Nonnull String method, @Nonnull String code, double duration) {
    try {
      RpcClientMetrics.RPC_CLIENT_DURATION.observe(duration, method);
      RpcClientMetrics.RPC_CLIENT_CODE.inc(method, code, name);
    } catch (Exception e) {
      log.error("call metrics error", e);
    }
  }

}
