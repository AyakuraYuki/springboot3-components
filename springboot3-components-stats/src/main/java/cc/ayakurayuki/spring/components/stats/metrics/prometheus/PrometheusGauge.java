package cc.ayakurayuki.spring.components.stats.metrics.prometheus;

import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;
import io.prometheus.client.Gauge;

/**
 * @author Ayakura Yuki
 */
public class PrometheusGauge implements IGauge {

  private final Gauge gauge;

  PrometheusGauge(Gauge gauge) {
    this.gauge = gauge;
  }

  @Override
  public void inc(String... labelValues) {
    gauge.labels(labelValues).inc();
  }

  @Override
  public void inc(double amount, String... labelValues) {
    gauge.labels(labelValues).inc(amount);
  }

  @Override
  public void dec(String... labelValues) {
    gauge.labels(labelValues).dec();
  }

  @Override
  public void dec(double amount, String... labelValues) {
    gauge.labels(labelValues).dec(amount);
  }

  @Override
  public void set(double value, String... labelValues) {
    gauge.labels(labelValues).set(value);
  }

  @Override
  public double get(String... labelValues) {
    return gauge.labels(labelValues).get();
  }

  @Override
  public void register() {
    gauge.register();
  }

}
