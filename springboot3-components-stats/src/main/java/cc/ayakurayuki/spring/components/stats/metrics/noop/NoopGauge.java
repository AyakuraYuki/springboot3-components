package cc.ayakurayuki.spring.components.stats.metrics.noop;

import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;

/**
 * @author Ayakura Yuki
 */
public class NoopGauge implements IGauge {

  @Override
  public void inc(String... labelValues) {}

  @Override
  public void inc(double amount, String... labelValues) {}

  @Override
  public void dec(String... labelValues) {}

  @Override
  public void dec(double amount, String... labelValues) {}

  @Override
  public void set(double value, String... labelValues) {}

  @Override
  public double get(String... labelValues) {
    return 0;
  }

  @Override
  public void register() {}

}
