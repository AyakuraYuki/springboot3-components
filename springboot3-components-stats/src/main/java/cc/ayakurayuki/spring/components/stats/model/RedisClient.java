package cc.ayakurayuki.spring.components.stats.model;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYGauge;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;

/**
 * @author Ayakura Yuki
 */
public class RedisClient {

  private static final String NAMESPACE = "redis_client";

  /**
   * redis 请求总数与耗时
   */
  public static final AYHistogram REDIS_CLIENT_DURATION = AYHistogram.build()
      .namespace(NAMESPACE)
      .subsystem("requests")
      .name("duration_ms")
      .help("redis client histogram")
      .labelNames("name", "addr", "command")
      .buckets(5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000)
      .create()
      .register();

  /**
   * redis code 监控
   */
  public static final AYCounter REDIS_CLIENT_CODE = AYCounter.build()
      .namespace(NAMESPACE)
      .subsystem("requests")
      .name("error_total")
      .help("redis client code")
      .labelNames("name", "addr", "command", "error")
      .create()
      .register();

  /**
   * redis 缓存命中总数
   */
  public static final AYGauge REDIS_HITS = AYGauge.build()
      .namespace(NAMESPACE)
      .name("hits_total")
      .help("redis client hits")
      .labelNames("name", "addr")
      .create()
      .register();

  /**
   * redis 缓存丢失总数
   */
  public static final AYGauge REDIS_MISS = AYGauge.build()
      .namespace(NAMESPACE)
      .name("misses_total")
      .help("redis client miss")
      .labelNames("name", "addr")
      .create()
      .register();

  /**
   * redis 连接池配置的总连接数 (gauge, counter)
   */
  public static final AYGauge REDIS_CONNECTIONS_TOTAL = AYGauge.build()
      .namespace(NAMESPACE)
      .subsystem("connections")
      .name("total")
      .help("redis client pool total")
      .labelNames("name", "addr", "state")
      .create()
      .register();

  /**
   * redis 连接池活跃中、闲置中的连接的个数
   */
  public static final AYGauge REDIS_CONNECTIONS_CURRENT = AYGauge.build()
      .namespace(NAMESPACE)
      .subsystem("connections")
      .name("current")
      .help("redis client pool current")
      .labelNames("name", "addr", "state")
      .create()
      .register();

}
