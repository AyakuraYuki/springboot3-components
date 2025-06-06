package cc.ayakurayuki.spring.components.trace;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

/**
 * @author Ayakura Yuki
 */
public class TraceBootstrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "io.opentelemetry",
        "io.grpc"
    };
  }

}
