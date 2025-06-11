package cc.ayakurayuki.spring.components.rpc.client;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;

class RpcClientMetrics {

  private static final String NAMESPACE = "grpc_client";
  private static final String SUBSYSTEM = "requests";

  // rpc client total requests
  public static final AYCounter RPC_CLIENT_TOTAL = AYCounter.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("total")
      .help("rpc client total")
      .labelNames("method")
      .create()
      .register();

  // rpc client request duration
  public static final AYHistogram RPC_CLIENT_DURATION = AYHistogram.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("duration_ms")
      .help("rpc client histogram")
      .labelNames("method")
      .buckets(5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000)
      .create()
      .register();

  //  rpc client code
  public static final AYCounter RPC_CLIENT_CODE = AYCounter.build()
      .namespace(NAMESPACE)
      .subsystem(SUBSYSTEM)
      .name("code_total")
      .help("rpc client code")
      .labelNames("method", "code", "appid")
      .create()
      .register();

}
