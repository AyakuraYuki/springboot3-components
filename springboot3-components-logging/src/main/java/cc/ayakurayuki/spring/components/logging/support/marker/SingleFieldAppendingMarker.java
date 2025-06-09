package cc.ayakurayuki.spring.components.logging.support.marker;

import java.io.Serial;
import java.util.Objects;

/**
 * A {@link org.slf4j.Marker} that appends a single field into the JSON event.
 */
public abstract class SingleFieldAppendingMarker extends LogMarker {

  public static final String MARKER_NAME_PREFIX = LogMarker.MARKER_NAME_PREFIX + "APPEND_";

  @Serial
  private static final long serialVersionUID = 6062776367927474957L;

  private final String fieldName;

  public SingleFieldAppendingMarker(String markerName, String fieldName) {
    super(markerName);
    this.fieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
  }

  public String getFieldName() {
    return fieldName;
  }

  @Override
  protected String toStringSelf() {
    return toStringValue();
  }

  protected abstract String toStringValue();

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof SingleFieldAppendingMarker other)) {
      return false;
    }
    return this.fieldName.equals(other.fieldName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + super.hashCode();
    result = prime * result + this.fieldName.hashCode();
    return result;
  }

}
