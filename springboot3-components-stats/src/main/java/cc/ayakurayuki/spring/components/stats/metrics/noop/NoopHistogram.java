package cc.ayakurayuki.spring.components.stats.metrics.noop;

import cc.ayakurayuki.spring.components.stats.metrics.vector.IHistogram;

/**
 * @author Ayakura Yuki
 */
public class NoopHistogram implements IHistogram {

  @Override
  public void observe(double duration, String... labelValues) {}

  @Override
  public double[] get(String... labelValues) {
    return new double[labelValues.length];
  }

  @Override
  public void register() {}

}
