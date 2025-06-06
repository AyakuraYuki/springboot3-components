package cc.ayakurayuki.spring.components.stats.metrics.vector;

/**
 * @author Ayakura Yuki
 */
public interface ISummary extends IVector {

  void observe(double duration, String... labelValues);

}
