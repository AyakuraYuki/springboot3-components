package cc.ayakurayuki.spring.components.stats.metrics;

import cc.ayakurayuki.spring.components.stats.metrics.vector.IVector;
import com.google.common.base.Preconditions;
import lombok.Getter;

/**
 * @author Ayakura Yuki
 */
@Getter
public abstract class AYSimpleCollector<V extends IVector, C extends AYSimpleCollector<V, C>> {

  private final String   namespace;
  private final String   subsystem;
  private final String   name;
  private final String   metricName;
  private final String   help;
  private final String[] labelNames;

  protected V iVector;

  @SuppressWarnings({"rawtypes"})
  AYSimpleCollector(Builder b) {
    namespace = b.namespace;
    subsystem = b.subsystem;
    name = b.name;
    metricName = String.format("%s_%s_%s", b.namespace, b.subsystem, b.name);
    help = b.help;
    labelNames = b.labelNames;
  }

  public static double escapeMillisFromNanos(long start, long end) {
    return escapeMillisFromNanos(end - start);
  }

  public static double escapeMillisFromNanos(long nanos) {
    return nanos / 1000000D;
  }

  public abstract C register();

  public abstract String getVectorName();

  void checkState() {
    Preconditions.checkState(iVector != null, String.format("%s must call register() method", getVectorName()));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public abstract static class Builder<B extends Builder<B, C>, C extends AYSimpleCollector> {

    String   namespace  = "";
    String   subsystem  = "";
    String   name       = "";
    String   help       = "";
    String[] labelNames = new String[]{};

    public B namespace(String namespace) {
      this.namespace = namespace;
      return (B) this;
    }

    public B subsystem(String subsystem) {
      this.subsystem = subsystem;
      return (B) this;
    }

    public B name(String name) {
      this.name = name;
      return (B) this;
    }

    public B help(String help) {
      this.help = help;
      return (B) this;
    }

    public B labelNames(String... labelNames) {
      this.labelNames = labelNames;
      return (B) this;
    }

    public abstract C create();

  }

}
