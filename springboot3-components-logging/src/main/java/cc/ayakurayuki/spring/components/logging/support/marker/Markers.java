package cc.ayakurayuki.spring.components.logging.support.marker;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Marker;

/**
 * Convenience class for constructing various {@link LogMarker}s used to add fields into the log event.
 * <p>
 * This creates a somewhat fluent interface that can be used to create markers.
 * <p>
 * For example:
 *
 * <pre>{@code
 * import static cc.ayakurayuki.spring.components.logging.support.marker.Markers.*
 *
 * logger.info(append("name1", "value1"), "log message");
 * logger.info(append("name1", "value1").and(append("name2", "value2")), "log message");
 * logger.info(appendEntries(myMap), "log message");
 * }</pre>
 */
public class Markers {

  private Markers() {}

  /**
   * @see MapEntriesAppendingMarker
   */
  public static LogMarker appendEntries(Map<?, ?> map) {
    return new MapEntriesAppendingMarker(map);
  }

  /**
   * @see ObjectFieldsAppendingMarker
   */
  public static LogMarker appendFields(Object object) {
    return new ObjectFieldsAppendingMarker(object);
  }

  /**
   * @see ObjectAppendingMarker
   */
  public static LogMarker append(String fieldName, Object object) {
    return new ObjectAppendingMarker(fieldName, object);
  }

  /**
   * @see ObjectAppendingMarker
   */
  public static LogMarker appendArray(String fieldName, Object... objects) {
    return new ObjectAppendingMarker(fieldName, objects);
  }

  /**
   * @see RawJsonAppendingMarker
   */
  public static LogMarker appendRaw(String fieldName, String rawJsonValue) {
    return new RawJsonAppendingMarker(fieldName, rawJsonValue);
  }

  /**
   * Aggregates the given markers into a single marker.
   *
   * @param markers the markers to aggregate
   *
   * @return the aggregated marker.
   */
  public static LogMarker aggregate(Marker... markers) {
    LogMarker m = empty();
    if (markers != null) {
      for (Marker marker : markers) {
        m.add(marker);
      }
    }
    return m;
  }

  /**
   * Aggregates the given markers into a single marker.
   *
   * @param markers the markers to aggregate
   *
   * @return the aggregated marker.
   */
  public static LogMarker aggregate(Collection<? extends Marker> markers) {
    LogMarker m = empty();
    if (markers != null) {
      for (Marker marker : markers) {
        m.add(marker);
      }
    }
    return m;
  }

  /**
   * @see DeferredMarker
   */
  public static LogMarker defer(Supplier<? extends LogMarker> markerSupplier) {
    return new DeferredMarker(markerSupplier);
  }

  /**
   * @see EmptyMarker
   */
  public static LogMarker empty() {
    return new EmptyMarker();
  }

}
