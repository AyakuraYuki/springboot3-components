package cc.ayakurayuki.spring.components.logging.support.marker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serial;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Marker;

/**
 * A {@link Marker}that
 * writes an object under a given fieldName in the log event output.
 * <p>
 * <p>
 * When writing to the JSON data,
 * the object will be converted into an appropriate JSON type
 * (number, string, object, array) and written to the JSON event under a given fieldName.
 * <p>
 * <p>
 * Example:
 * <p>
 * <pre>{@code
 * logger.info("My Message {}", StructuredArguments.keyValue("key", "value"));
 * }</pre>
 * <p>
 * results in the following log event output:
 * <pre>{@code
 * {
 *     "message" : "My Message key=value",
 *     "key"     : "value"
 * }
 * }</pre>
 */
public class ObjectAppendingMarker extends SingleFieldAppendingMarker {

  public static final String MARKER_NAME = MARKER_NAME_PREFIX + "OBJECT";

  @Serial
  private static final long serialVersionUID = 6094393843141751219L;

  /**
   * The object to write as the field's value.
   * Can be a {@link String}, {@link Number}, array, or some other object that can be processed
   */
  private final Object object;

  public ObjectAppendingMarker(String fieldName, Object object) {
    super(MARKER_NAME, fieldName);
    this.object = object;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void writeTo(Object generator) {
    switch (generator) {
      case Map m -> m.put(getFieldName(), this.object);
      case ObjectNode json -> {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(this.object);
        json.set(getFieldName(), node);
      }
      case null, default -> {
      }
    }
  }

  @Override
  public String toStringValue() {
    return MarkerUtils.toString(object);
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ObjectAppendingMarker other)) {
      return false;
    }
    return Objects.equals(this.object, other.object);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + super.hashCode();
    result = prime * result + (this.object == null ? 0 : this.object.hashCode());
    return result;
  }

}
