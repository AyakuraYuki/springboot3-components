package cc.ayakurayuki.spring.components.stats.metrics.factory;

import cc.ayakurayuki.spring.components.stats.metrics.AYCounter;
import cc.ayakurayuki.spring.components.stats.metrics.AYGauge;
import cc.ayakurayuki.spring.components.stats.metrics.AYHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.AYSummary;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ICounter;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IGauge;
import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;

/**
 * @author Ayakura Yuki
 */
public interface MetricFactory {

  ICounter counter(AYCounter counter);

  IGauge gauge(AYGauge gauge);

  IHistogram histogram(AYHistogram histogram);

  ISummary summary(AYSummary summary);

}
