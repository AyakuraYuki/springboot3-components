package cc.ayakurayuki.spring.components.stats.metrics.noop;

import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;

/**
 * @author Ayakura Yuki
 */
public class NoopSummary implements ISummary {

  @Override
  public void observe(double duration, String... labelValues) {}

  @Override
  public void register() {}

}
