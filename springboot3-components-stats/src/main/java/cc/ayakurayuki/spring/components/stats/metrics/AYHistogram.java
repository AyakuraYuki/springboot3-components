package cc.ayakurayuki.spring.components.stats.metrics;

import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricLoader;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;
import lombok.Getter;

/**
 * @author Ayakura Yuki
 */
@Getter
public class AYHistogram extends AYSimpleCollector<IHistogram, AYHistogram> {

  private final double[] buckets;

  public AYHistogram(Builder b) {
    super(b);
    buckets = b.buckets;
  }

  public static Builder build() {
    return new Builder();
  }

  @Override
  public AYHistogram register() {
    iVector = MetricLoader.metricFactory.histogram(this);
    iVector.register();
    return this;
  }

  @Override
  public String getVectorName() {
    return "histogram";
  }

  public AYHistogram observe(double duration, String... labelValues) {
    checkState();
    iVector.observe(duration, labelValues);
    return this;
  }

  public double[] get(String... labelValues) {
    checkState();
    return iVector.get(labelValues);
  }

  public static class Builder extends AYSimpleCollector.Builder<Builder, AYHistogram> {

    private double[] buckets = null;

    public Builder buckets(double... buckets) {
      this.buckets = buckets;
      return this;
    }

    public Builder linearBuckets(double start, double width, int count) {
      buckets = new double[count];
      for (int i = 0; i < count; i++) {
        buckets[i] = start + i * width;
      }
      return this;
    }

    public Builder exponentialBuckets(double start, double factor, int count) {
      buckets = new double[count];
      for (int i = 0; i < count; i++) {
        buckets[i] = start * Math.pow(factor, i);
      }
      return this;
    }

    @Override
    public AYHistogram create() {
      return new AYHistogram(this);
    }

  }

}
