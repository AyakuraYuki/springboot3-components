package cc.ayakurayuki.spring.components.stats.metrics.vector;

/**
 * @author Ayakura Yuki
 */
public interface IGauge extends IVector {

  void inc(String... labelValues);

  void inc(double amount, String... labelValues);

  void dec(String... labelValues);

  void dec(double amount, String... labelValues);

  void set(double value, String... labelValues);

  double get(String... labelValues);

}
