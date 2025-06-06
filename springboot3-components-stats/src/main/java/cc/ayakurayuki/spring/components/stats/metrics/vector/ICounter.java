package cc.ayakurayuki.spring.components.stats.metrics.vector;

/**
 * @author Ayakura Yuki
 */
public interface ICounter extends IVector {

  void inc(String... labelValues);

  void inc(double amount, String... labelValues);

  double get(String... labelValues);

}
