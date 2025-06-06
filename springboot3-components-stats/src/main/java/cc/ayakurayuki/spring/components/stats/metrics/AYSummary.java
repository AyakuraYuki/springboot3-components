package cc.ayakurayuki.spring.components.stats.metrics;

import cc.ayakurayuki.spring.components.stats.metrics.factory.MetricLoader;
import cc.ayakurayuki.spring.components.stats.metrics.vector.ISummary;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;

/**
 * @author Ayakura Yuki
 */
@Getter
public class AYSummary extends AYSimpleCollector<ISummary, AYSummary> {

  @Override
  public AYSummary register() {
    iVector = MetricLoader.metricFactory.summary(this);
    iVector.register();
    return this;
  }

  @Override
  public String getVectorName() {
    return "summary";
  }

  private final Long             maxAgeSeconds;
  private final Integer          ageBuckets;
  private final List<AYQuantile> quantiles;

  public AYSummary(Builder b) {
    super(b);
    maxAgeSeconds = b.maxAgeSeconds;
    ageBuckets = b.ageBuckets;
    quantiles = b.quantiles;
  }

  public void observe(double duration, String... labelValues) {
    checkState();
    iVector.observe(duration, labelValues);
  }

  public static Builder build() {
    return new Builder();
  }

  public static class Builder extends AYSimpleCollector.Builder<Builder, AYSummary> {

    private       Long             maxAgeSeconds = null;
    private       Integer          ageBuckets    = null;
    private final List<AYQuantile> quantiles     = Lists.newArrayList();

    public Builder quantile(double quantile, double error) {
      quantiles.add(new AYQuantile(quantile, error));
      return this;
    }

    public Builder maxAgeSeconds(long maxAgeSeconds) {
      this.maxAgeSeconds = maxAgeSeconds;
      return this;
    }

    public Builder ageBuckets(Integer ageBuckets) {
      this.ageBuckets = ageBuckets;
      return this;
    }

    @Override
    public AYSummary create() {
      return new AYSummary(this);
    }

  }

  public record AYQuantile(
      double quantile,
      double error
  ) {}

}
