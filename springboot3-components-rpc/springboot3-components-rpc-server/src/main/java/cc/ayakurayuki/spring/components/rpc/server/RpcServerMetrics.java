package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;

class RpcServerMetrics {

  private static final String NAMESPACE = "grpc_server";
  private static final String SUBSYSTEM = "requests";

  // rpc server total requests
  public static final AYCounter RPC_SERVER_TOTAL = AYCounter.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("total")
      .help("rpc server total")
      .labelNames("method", "caller")
      .create()
      .register();

  // rpc server request duration
  public static final AYHistogram RPC_SERVER_DURATION = AYHistogram.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("duration_ms")
      .help("rpc server duration")
      .labelNames("method", "caller")
      .buckets(5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000)
      .create()
      .register();

  // rpc server code
  public static final AYCounter RPC_SERVER_CODE = AYCounter.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("code_total")
      .help("rpc server code")
      .labelNames("method", "caller", "code")
      .create()
      .register();

}
