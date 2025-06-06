package cc.ayakurayuki.spring.components.stats.metrics.noop;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYGauge;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.AYSummary;
import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricFactory;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;

/**
 * @author Ayakura Yuki
 */
public class NoopMetricFactory implements MetricFactory {

  @Override
  public ICounter counter(AYCounter counter) {
    return new NoopCounter();
  }

  @Override
  public IGauge gauge(AYGauge gauge) {
    return new NoopGauge();
  }

  @Override
  public IHistogram histogram(AYHistogram histogram) {
    return new NoopHistogram();
  }

  @Override
  public ISummary summary(AYSummary summary) {
    return new NoopSummary();
  }

}
