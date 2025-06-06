package cc.ayakurayuki.spring.components.stats;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

/**
 * @author Ayakura Yuki
 */
public class StatsBootstrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "io.prometheus.client"
    };
  }

}
