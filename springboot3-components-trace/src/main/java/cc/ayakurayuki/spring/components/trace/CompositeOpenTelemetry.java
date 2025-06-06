package cc.ayakurayuki.spring.components.trace;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ayakura Yuki
 */
public class CompositeOpenTelemetry implements OpenTelemetry {

  @Override
  public TracerProvider getTracerProvider() {
    return this.activeOpenTelemetryRef.get().getTracerProvider();
  }

  @Override
  public ContextPropagators getPropagators() {
    return this.activeOpenTelemetryRef.get().getPropagators();
  }

  private static final CompositeOpenTelemetry INSTANCE = new CompositeOpenTelemetry();

  private final OpenTelemetry                  defaultOpenTelemetry;
  private final AtomicReference<OpenTelemetry> activeOpenTelemetryRef;

  private CompositeOpenTelemetry() {
    this.defaultOpenTelemetry = OpenTelemetry.noop();
    this.activeOpenTelemetryRef = new AtomicReference<>(this.defaultOpenTelemetry);
  }

  public static CompositeOpenTelemetry get() {
    return INSTANCE;
  }

  public boolean registerIfAbsent(final OpenTelemetry openTelemetry) {
    Objects.requireNonNull(openTelemetry, "register OpenTelemetry must be not null");
    return activeOpenTelemetryRef.compareAndSet(this.defaultOpenTelemetry, openTelemetry);
  }

  public boolean isNoop() {
    return Objects.equals(this.defaultOpenTelemetry, activeOpenTelemetryRef.get());
  }

}
