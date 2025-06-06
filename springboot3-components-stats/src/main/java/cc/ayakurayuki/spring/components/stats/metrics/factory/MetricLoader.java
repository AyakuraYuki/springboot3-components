package cc.ayakurayuki.spring.components.stats.metrics.factory;

import cc.ayakurayuki.spring.components.stats.metrics.noop.NoopMetricFactory;
import java.util.Iterator;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ayakura Yuki
 */
@Slf4j
public class MetricLoader {

  public static final MetricFactory metricFactory;

  static {
    ServiceLoader<MetricFactory> metricFactories = ServiceLoader.load(MetricFactory.class);
    Iterator<MetricFactory> metricFactoryIterator = metricFactories.iterator();
    if (metricFactoryIterator.hasNext()) {
      metricFactory = metricFactoryIterator.next();
    } else {
      metricFactory = new NoopMetricFactory();
      log.warn("found noop metric implementation, please be warned to check the service registration of interface MetricFactory");
    }
  }

}
