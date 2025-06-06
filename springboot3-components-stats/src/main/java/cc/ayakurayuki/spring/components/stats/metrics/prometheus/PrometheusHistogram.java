package cc.ayakurayuki.spring.components.stats.metrics.prometheus;

import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;
import io.prometheus.client.Histogram;

/**
 * @author Ayakura Yuki
 */
public class PrometheusHistogram implements IHistogram {

  private final Histogram histogram;

  PrometheusHistogram(Histogram histogram) {
    this.histogram = histogram;
  }

  @Override
  public void observe(double duration, String... labelValues) {
    histogram.labels(labelValues).observe(duration);
  }

  @Override
  public double[] get(String... labelValues) {
    return histogram.labels(labelValues).get().buckets;
  }

  @Override
  public void register() {
    histogram.register();
  }

}
