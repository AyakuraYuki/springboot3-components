package cc.ayakurayuki.spring.components.stats.metrics;

import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricLoader;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;
import lombok.Getter;

/**
 * @author Ayakura Yuki
 */
@Getter
public class AYCounter extends AYSimpleCollector<ICounter, AYCounter> {

  public AYCounter(Builder b) {
    super(b);
  }

  public static Builder build() {
    return new Builder();
  }

  @Override
  public AYCounter register() {
    iVector = MetricLoader.metricFactory.counter(this);
    iVector.register();
    return this;
  }

  @Override
  public String getVectorName() {
    return "counter";
  }

  public void inc(String... labelValues) {
    checkState();
    iVector.inc(labelValues);
  }

  public void inc(double amount, String... labelValues) {
    checkState();
    iVector.inc(amount, labelValues);
  }

  public double get(String... labelValues) {
    checkState();
    return iVector.get(labelValues);
  }

  public static class Builder extends AYSimpleCollector.Builder<Builder, AYCounter> {

    @Override
    public AYCounter create() {
      return new AYCounter(this);
    }

  }

}
