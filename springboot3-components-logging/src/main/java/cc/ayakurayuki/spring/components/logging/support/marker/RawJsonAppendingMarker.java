package cc.ayakurayuki.spring.components.logging.support.marker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import java.io.Serial;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Marker;

/**
 * A {@link Marker} that writes a raw JSON value to the log JSON event under a given field name.
 */
public class RawJsonAppendingMarker extends SingleFieldAppendingMarker {

  public static final String MARKER_NAME = MARKER_NAME_PREFIX + "RAW";

  @Serial
  private static final long serialVersionUID = 5489982347232346768L;

  private final String rawJson;

  public RawJsonAppendingMarker(String fieldName, String rawJson) {
    super(MARKER_NAME, fieldName);
    this.rawJson = Objects.requireNonNull(rawJson, "rawJson must not be null");
  }

  @Override
  protected String toStringValue() {
    return MarkerUtils.toString(rawJson);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void writeTo(Object generator) {
    switch (generator) {
      case Map m -> m.put(getFieldName(), rawJson);
      case JsonObject json -> json.addProperty(getFieldName(), rawJson);
      case ObjectNode json -> json.put(getFieldName(), rawJson);
      case null, default -> {
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof RawJsonAppendingMarker other)) {
      return false;
    }
    return Objects.equals(this.rawJson, other.rawJson);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + super.hashCode();
    result = prime * result + this.rawJson.hashCode();
    return result;
  }

}
