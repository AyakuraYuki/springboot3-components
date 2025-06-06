package cc.ayakurayuki.spring.components.stats.metrics.vector;

/**
 * @author Ayakura Yuki
 */
public interface IHistogram extends IVector {

  void observe(double duration, String... labelValues);

  double[] get(String... labelValues);

}
