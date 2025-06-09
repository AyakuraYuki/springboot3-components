package cc.ayakurayuki.spring.components.logging.support.marker;

import java.io.Serial;

/**
 * An empty marker that does nothing itself, but can be used as a base marker when you want to conditionally chain other markers with {@link #and(org.slf4j.Marker)}.
 * For example:</p>
 *
 * <pre>{@code
 *     Marker marker = Markers.empty();
 *     if (condition1) {
 *         marker = marker.and(Markers.append("fieldName1", value1);
 *     }
 *     if (condition2) {
 *         marker = marker.and(Markers.append("fieldName2", value2);
 *     }
 * }</pre>
 */
public class EmptyMarker extends LogMarker {

  public static final String EMPTY_MARKER_NAME = "EMPTY";

  @Serial
  private static final long serialVersionUID = -998139462585490926L;

  public EmptyMarker() {
    super(EMPTY_MARKER_NAME);
  }

  @Override
  public void writeTo(Object generator) {
    // no-op
  }

  @Override
  protected String toStringSelf() {
    return "";
  }

}
