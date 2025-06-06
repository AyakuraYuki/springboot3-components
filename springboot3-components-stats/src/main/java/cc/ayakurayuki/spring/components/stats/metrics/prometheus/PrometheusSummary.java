package cc.ayakurayuki.spring.components.stats.metrics.prometheus;

import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;
import io.prometheus.client.Summary;

/**
 * @author Ayakura Yuki
 */
public class PrometheusSummary implements ISummary {

  private final Summary summary;

  PrometheusSummary(Summary summary) {
    this.summary = summary;
  }

  @Override
  public void observe(double duration, String... labelValues) {
    summary.labels(labelValues).observe(duration);
  }

  @Override
  public void register() {
    summary.register();
  }

}
