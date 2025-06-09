package cc.ayakurayuki.spring.components.logging.support.marker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serial;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link org.slf4j.Marker} that "unwraps" the given object into the log event.
 * <p>
 * <p>
 * For example, if the message is "mymessage {}", and the object argument is:
 *
 * <pre>{@code
 * {
 *     name1 : "value1",
 *     name2 : 5,
 *     name3 : [1, 2, 3],
 *     name4 : {
 *         name5 : 6
 *     }
 * }
 * }</pre>
 * <p>
 * Then the message, name1, name2, name3, name4 fields will be added to the json for the log event.
 * <p>
 * For example:
 *
 * <pre>{@code
 * {
 *     "message" : "mymessage objectsToStringValue",
 *     "name1" : "value1",
 *     "name2" : 5,
 *     "name3" : [1, 2, 3],
 *     "name4" : { "name5" : 6 }
 * }
 * }</pre>
 * <p>
 * Note that if the object cannot be unwrapped, then nothing will be written.
 */
public class ObjectFieldsAppendingMarker extends LogMarker {

  public static final String MARKER_NAME = MARKER_NAME_PREFIX + "OBJECT_FIELDS";

  @Serial
  private static final long serialVersionUID = -4442640633201824050L;

  private final Object object;

  public ObjectFieldsAppendingMarker(Object object) {
    super(MARKER_NAME);
    this.object = object;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void writeTo(Object generator) {
    if (this.object == null) {
      return;
    }

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> elements;
    try {
      String raw = mapper.writeValueAsString(this.object);
      elements = mapper.readValue(raw, Map.class);
    } catch (Exception ignored) {
      elements = Collections.emptyMap();
    }

    switch (generator) {
      case Map m -> m.putAll(elements);
      case ObjectNode json -> {
        JsonNode mapNode = mapper.valueToTree(elements);
        if (mapNode.isObject()) {
          json.setAll((ObjectNode) mapNode);
        }
      }
      case null, default -> {
      }
    }
  }

  @Override
  public String toStringSelf() {
    return MarkerUtils.toString(object);
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ObjectFieldsAppendingMarker other)) {
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
