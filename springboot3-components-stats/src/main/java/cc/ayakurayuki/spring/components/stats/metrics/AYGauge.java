package cc.ayakurayuki.spring.components.stats.metrics;

import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricLoader;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;
import lombok.Getter;

/**
 * @author Ayakura Yuki
 */
@Getter
public class AYGauge extends AYSimpleCollector<IGauge, AYGauge> {

  @Override
  public AYGauge register() {
    iVector = MetricLoader.metricFactory.gauge(this);
    iVector.register();
    return this;
  }

  @Override
  public String getVectorName() {
    return "gauge";
  }

  public AYGauge(Builder b) {
    super(b);
  }

  public void inc(String... labelValues) {
    checkState();
    iVector.inc(labelValues);
  }

  public void inc(double amount, String... labelValues) {
    checkState();
    iVector.inc(amount, labelValues);
  }

  public void dec(String... labelValues) {
    checkState();
    iVector.dec(labelValues);
  }

  public void dec(double amount, String... labelValues) {
    checkState();
    iVector.dec(amount, labelValues);
  }

  public void set(double value, String... labelValues) {
    checkState();
    iVector.set(value, labelValues);
  }

  public double get(String... labelValues) {
    return iVector.get(labelValues);
  }

  public static Builder build() {
    return new Builder();
  }

  public static class Builder extends AYSimpleCollector.Builder<Builder, AYGauge> {

    @Override
    public AYGauge create() {
      return new AYGauge(this);
    }

  }

}
