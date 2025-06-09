package cc.ayakurayuki.spring.components.logging.support.marker;

import java.io.IOException;
import java.io.Serial;
import org.slf4j.Marker;

/**
 * A {@link Marker} that is known and understood by the encoder.
 * <p>
 * In particular these markers are used to write data into the JSON event via {@link #writeTo(Object)}.
 */
public abstract class LogMarker extends BasicMarker implements Iterable<Marker> {

  public static final String MARKER_NAME_PREFIX = "PL_";

  @Serial
  private static final long serialVersionUID = 6056624202262626002L;

  public LogMarker(String name) {
    super(name);
  }

  /**
   * Adds the given marker as a reference, and returns this marker.
   * <p>
   * This can be used to chain markers together fluently on a log line. For example:
   *
   * <pre>{@code
   * import static cc.ayakurayuki.spring.components.logging.support.marker.Markers.*
   *
   * logger.info(append("name1", "value1).and(append("name2", "value2")), "log message");
   * }</pre>
   *
   * @param <T>       subtype of LogMarker
   * @param reference The marker to add
   *
   * @return A marker with this marker and the given marker
   */
  @SuppressWarnings("unchecked")
  public <T extends LogMarker> T and(Marker reference) {
    add(reference);
    return (T) this;
  }

  /**
   * Writes the data associated with this marker to the given {@link Object}.
   *
   * @param generator the generator to which to write the output of this marker.
   *
   * @throws IOException if there was an error writing to the generator.
   */
  public abstract void writeTo(Object generator);

  /**
   * Returns a String in the form of
   * <pre>
   *     self, reference1, reference2, ...
   * </pre>
   *
   * <p>Where <code>self</code> is the value returned by {@link #toStringSelf()},
   * and <code>reference*</code> are the <code>toString()</code> values of any references.</p>
   *
   * <p>It is recommended that subclasses only override {@link #toStringSelf()},
   * so that references are automatically included in the value returned from {@code toString()}.</p>
   *
   * @return a string representation of the object, which includes references
   */
  @Override
  public String toString() {
    String self = toStringSelf();
    if (!hasReferences()) {
      return self;
    }

    StringBuilder sb = new StringBuilder(self);
    boolean appendSeparator = !self.isEmpty();
    for (Marker marker : this) {
      if (appendSeparator) {
        sb.append(", ");
      }
      String referenceToString = marker.toString();
      sb.append(referenceToString);
      appendSeparator = !referenceToString.isEmpty();
    }

    return sb.toString();
  }

  /**
   * Returns a string representation of this object, without including any references.
   *
   * <p>Subclasses should override {@code toStringSelf()} instead of {@link #toString()},
   * since {@link #toString()} will automatically include the {@code toStringSelf()} and references.</p>
   *
   * @return a string representation of this object, without including any references.
   */
  protected String toStringSelf() {
    return getName();
  }

}
