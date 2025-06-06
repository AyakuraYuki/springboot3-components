package cc.ayakurayuki.spring.components.stats.metrics.noop;

import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;

/**
 * @author Ayakura Yuki
 */
public class NoopCounter implements ICounter {

  @Override
  public void inc(String... labelValues) {}

  @Override
  public void inc(double amount, String... labelValues) {}

  @Override
  public double get(String... labelValues) {
    return 0;
  }

  @Override
  public void register() {}

}
