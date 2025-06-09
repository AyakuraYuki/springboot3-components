package cc.ayakurayuki.spring.components.logging.support.marker;

import java.io.Serial;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Marker;

/**
 * A {@link LogMarker} that defers the creation of another {@link LogMarker} until the first time it is encoded.
 *
 * <p>
 * The deferred value will only be calculated once.
 * The single value supplied by the supplier will be reused every time the marker is written.
 * The supplier will be invoked when the first appender encodes the marker.
 * That same supplied value will be used when the next appender encodes the marker.
 */
public class DeferredMarker extends LogMarker {

  public static final String DEFERRED_MARKER_NAME = "DEFERRED";

  @Serial
  private static final long serialVersionUID = 2169344152826099309L;

  private final Supplier<? extends LogMarker> logMarkerSupplier;

  private volatile LogMarker suppliedValue;

  public DeferredMarker(Supplier<? extends LogMarker> logMarkerSupplier) {
    super(DEFERRED_MARKER_NAME);
    this.logMarkerSupplier = Objects.requireNonNull(logMarkerSupplier, "logMarkerSupplier must not be null");
  }

  @Override
  public void writeTo(Object generator) {
    if (generator == null) {
      return;
    }
    writeMarker(generator, getSuppliedValue());
  }

  private LogMarker getSuppliedValue() {
    if (suppliedValue == null) {
      synchronized (this) {
        if (suppliedValue == null) {
          LogMarker logMarker = logMarkerSupplier.get();
          if (logMarker == null) {
            logMarker = Markers.empty();
          }
          suppliedValue = logMarker;
        }
      }
    }
    return suppliedValue;
  }

  private void writeMarker(Object generator, Marker marker) {
    if (marker == null) {
      return;
    }

    if (marker instanceof LogMarker logMarker) {
      logMarker.writeTo(generator);
    }

    if (marker.hasReferences()) {
      for (Iterator<Marker> i = marker.iterator(); i.hasNext(); ) {
        Marker next = i.next();
        writeMarker(generator, next);
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

}
