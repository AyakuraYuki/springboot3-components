package cc.ayakurayuki.spring.components.stats.metrics.prometheus;

import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;
import io.prometheus.client.Counter;

/**
 * @author Ayakura Yuki
 */
public class PrometheusCounter implements ICounter {

  private final Counter counter;

  PrometheusCounter(Counter counter) {
    this.counter = counter;
  }

  @Override
  public void inc(String... labelValues) {
    counter.labels(labelValues).inc();
  }

  @Override
  public void inc(double amount, String... labelValues) {
    counter.labels(labelValues).inc(amount);
  }

  @Override
  public double get(String... labelValues) {
    return counter.labels(labelValues).get();
  }

  @Override
  public void register() {
    counter.register();
  }

}
