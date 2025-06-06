package cc.ayakurayuki.spring.components.logging;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

public class LoggingBootstrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "ch.qos.logback",
        "jnr.unixsocket"
    };
  }

}
