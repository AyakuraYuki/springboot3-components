package cc.ayakurayuki.spring.components.logging.support.marker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serial;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link org.slf4j.Marker} that appends entries from a {@link Map} into the logging event output.
 * <p>
 * <p>
 * When writing to a String, {@link String#valueOf(Object)} is used to convert the map to a string.
 * <p>
 * <p>
 * For example, if the message is "mymessage {}", and map argument contains is
 *
 * <pre>{@code
 *     name1= a String "value1",
 *     name2= an Integer 5,
 *     name3= an array containing [1, 2, 3],
 *     name4= a map containing  name5=6
 * }</pre>
 * <p>
 * Then the message, name1, name2, name3, name4 fields will be added to the json for the log event.
 * <p>
 * For example:
 * <pre>{@code
 * {
 *     "message" : "mymessage [name1=value1,name2=5,name3=[b...,name4=[name5=6]]",
 *     "name1"   : "value1",
 *     "name2"   : 5,
 *     "name3"   : [1, 2, 3],
 *     "name4"   : { "name5" : 6 }
 * }
 * }</pre>
 */
public class MapEntriesAppendingMarker extends LogMarker {

  public static final String MARKER_NAME = LogMarker.MARKER_NAME_PREFIX + "MAP_FIELDS";

  @Serial
  private static final long serialVersionUID = 3179342657540529525L;

  /**
   * The map from which entries will be appended to the log JSON event.
   */
  private final Map<?, ?> map;

  public MapEntriesAppendingMarker(Map<?, ?> map) {
    super(MARKER_NAME);
    this.map = map;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void writeTo(Object generator) {
    if (this.map == null) {
      return;
    }

    switch (generator) {
      case Map m -> {
        for (Map.Entry<?, ?> entry : this.map.entrySet()) {
          m.put(String.valueOf(entry.getKey()), entry.getValue());
        }
      }
      case ObjectNode json -> {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mapNode = mapper.valueToTree(this.map);
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
    return String.valueOf(map);
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof MapEntriesAppendingMarker other)) {
      return false;
    }
    return Objects.equals(this.map, other.map);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + super.hashCode();
    result = prime * result + (this.map == null ? 0 : this.map.hashCode());
    return result;
  }

}
