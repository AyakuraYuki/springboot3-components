package cc.ayakurayuki.spring.components.http.server.interceptor;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;

class HttpServerMetrics {

  private static final String NAME_SPACE = "http_server";
  private static final String SUBSYSTEM  = "requests";

  /**
   * Total requests to HTTP server
   */
  public static final AYCounter HTTP_SERVER_TOTAL = AYCounter.build()
      .namespace(NAME_SPACE)
      .subsystem(SUBSYSTEM)
      .name("total")
      .help("http server counter")
      .labelNames("path")
      .create()
      .register();

  /**
   * Request duration handled by HTTP server
   */
  public static final AYHistogram HTTP_SERVER_DURATION = AYHistogram.build()
      .namespace(NAME_SPACE)
      .subsystem(SUBSYSTEM)
      .name("duration_ms")
      .help("http server histogram")
      .labelNames("path")
      .buckets(5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000)
      .create()
      .register();

  public static final AYCounter HTTP_SERVER_CODE = AYCounter.build()
      .namespace(NAME_SPACE)
      .subsystem(SUBSYSTEM)
      .name("code_total")
      .help("http server code")
      .labelNames("path", "code")
      .create()
      .register();

  /**
   * This method is deprecated, collector now will self register.
   * <p>
   * Call this method nothing will happen
   */
  @Deprecated
  public static void register() {}

}
