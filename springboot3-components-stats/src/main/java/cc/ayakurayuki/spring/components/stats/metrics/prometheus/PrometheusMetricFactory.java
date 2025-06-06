package cc.ayakurayuki.spring.components.stats.metrics.prometheus;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYGauge;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.AYSummary;
import cc.ayakurayuki.spring.components.stats.metrics.AYSummary.AYQuantile;
import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricFactory;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;
import cc.ayakurayuki.spring.components.utility.collection.CollectionUtils;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

/**
 * @author Ayakura Yuki
 */
public class PrometheusMetricFactory implements MetricFactory {

  @Override
  public ICounter counter(AYCounter counter) {
    Counter.Builder builder = Counter.build()
        .namespace(counter.getNamespace())
        .subsystem(counter.getSubsystem())
        .name(counter.getName())
        .help(counter.getHelp());
    if (counter.getLabelNames() != null && counter.getLabelNames().length > 0) {
      builder.labelNames(counter.getLabelNames());
    }
    return new PrometheusCounter(builder.create());
  }

  @Override
  public IGauge gauge(AYGauge gauge) {
    Gauge.Builder builder = Gauge.build()
        .namespace(gauge.getNamespace())
        .subsystem(gauge.getSubsystem())
        .name(gauge.getName())
        .help(gauge.getHelp());
    if (gauge.getLabelNames() != null && gauge.getLabelNames().length > 0) {
      builder.labelNames(gauge.getLabelNames());
    }
    return new PrometheusGauge(builder.create());
  }

  @Override
  public IHistogram histogram(AYHistogram histogram) {
    Histogram.Builder builder = Histogram.build()
        .namespace(histogram.getNamespace())
        .subsystem(histogram.getSubsystem())
        .name(histogram.getName())
        .help(histogram.getHelp());
    if (histogram.getLabelNames() != null && histogram.getLabelNames().length > 0) {
      builder.labelNames(histogram.getLabelNames());
    }
    if (histogram.getBuckets() != null && histogram.getBuckets().length > 0) {
      builder.buckets(histogram.getBuckets());
    }
    return new PrometheusHistogram(builder.create());
  }

  @Override
  public ISummary summary(AYSummary summary) {
    Summary.Builder builder = Summary.build()
        .namespace(summary.getNamespace())
        .subsystem(summary.getSubsystem())
        .name(summary.getName())
        .help(summary.getHelp());

    if (summary.getLabelNames() != null && summary.getLabelNames().length > 0) {
      builder.labelNames(summary.getLabelNames());
    }

    if (CollectionUtils.isNotEmpty(summary.getQuantiles())) {
      for (AYQuantile q : summary.getQuantiles()) {
        builder.quantile(q.quantile(), q.error());
      }
    }

    if (summary.getAgeBuckets() != null) {
      builder.ageBuckets(summary.getAgeBuckets());
    }

    if (summary.getMaxAgeSeconds() != null) {
      builder.maxAgeSeconds(summary.getMaxAgeSeconds());
    }

    return new PrometheusSummary(builder.create());
  }

}
