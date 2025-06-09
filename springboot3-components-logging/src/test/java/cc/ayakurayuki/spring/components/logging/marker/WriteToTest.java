package cc.ayakurayuki.spring.components.logging.marker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Ayakura Yuki
 */
@SuppressWarnings({"unchecked"})
class WriteToTest {

  private final Map<?, ?> map = Map.ofEntries(
      Map.entry("name1", "value1"),
      Map.entry("name2", 5),
      Map.entry("name3", new int[]{1, 2, 3}),
      Map.entry("name4", Map.entry("name5", 6))
  );

  @Test
  void testMapWriteToMap() throws JsonProcessingException {
    Map<String, Object> generator = new HashMap<>();
    generator.put("message", "hello, world");

    for (Entry<?, ?> entry : this.map.entrySet()) {
      generator.put(String.valueOf(entry.getKey()), entry.getValue());
    }

    assert generator.size() == this.map.size() + 1;
    assert generator.containsKey("name1");
    assert generator.containsKey("name2");
    assert generator.containsKey("name3");
    assert generator.containsKey("name4");
    assert generator.containsKey("message");
  }

  @Test
  void testMapWriteToJackson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode generator = mapper.createObjectNode();
    generator.put("message", "hello, world");

    JsonNode mapNode = mapper.valueToTree(this.map);
    if (mapNode.isObject()) {
      generator.setAll((ObjectNode) mapNode);
    }

    assert generator.size() == this.map.size() + 1;
    assert generator.has("name1");
    assert generator.has("name2");
    assert generator.has("name3");
    assert generator.has("name4");
    assert generator.has("message");
  }

  // ----------------------------------------------------------------------------------------------------

  @Data
  static class Foo {

    private String name;

  }

  private final String objectField = "foo";
  private final Object object      = new Foo() {{
    setName("2233");
  }};

  @Test
  void testObjectWriteToMap() throws JsonProcessingException {
    Map<String, Object> generator = new HashMap<>();
    generator.put("message", "hello, world");

    generator.put(objectField, object);

    assert generator.size() == 2;
    assert generator.containsKey("message");
    assert generator.containsKey(objectField);
  }

  @Test
  void testObjectWriteToJackson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode generator = mapper.createObjectNode();
    generator.put("message", "hello, world");

    JsonNode node = mapper.valueToTree(this.object);
    generator.set(objectField, node);

    assert generator.size() == 2;
    assert generator.has("message");
    assert generator.has(objectField);
  }

  @Test
  void testObjectFieldWriteToMap() throws JsonProcessingException {
    Map<String, Object> generator = new HashMap<>();
    generator.put("message", "hello, world");

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> elements;
    try {
      String raw = mapper.writeValueAsString(this.object);
      elements = mapper.readValue(raw, Map.class);
    } catch (Exception ignored) {
      elements = Collections.emptyMap();
    }

    generator.putAll(elements);

    assert generator.size() == 2;
    assert generator.containsKey("message");
    assert generator.containsKey("name");
  }

  @Test
  void testObjectFieldWriteToJackson() {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode generator = mapper.createObjectNode();
    generator.put("message", "hello, world");

    Map<String, Object> elements;
    try {
      String raw = mapper.writeValueAsString(this.object);
      elements = mapper.readValue(raw, Map.class);
    } catch (Exception ignored) {
      elements = Collections.emptyMap();
    }

    JsonNode mapNode = mapper.valueToTree(elements);
    if (mapNode.isObject()) {
      generator.setAll((ObjectNode) mapNode);
    }

    assert generator.size() == 2;
    assert generator.has("message");
    assert generator.has("name");
  }

  // ----------------------------------------------------------------------------------------------------

  private final String rawJson      = "{\"name\":\"foobar\"}";
  private final String rawJsonField = "raw";

  @Test
  void testRawJsonWriteToMap() {
    Map<String, Object> generator = new HashMap<>();
    generator.put("message", "hello, world");
    generator.put(rawJsonField, rawJson);
    assert generator.size() == 2;
    assert generator.containsKey("message");
    assert generator.containsKey(rawJsonField);
  }

  @Test
  void testRawJsonWriteToGson() {
    JsonObject generator = new JsonObject();
    generator.addProperty("message", "hello, world");
    generator.addProperty(rawJsonField, rawJson);
    assert generator.size() == 2;
    assert generator.has("message");
    assert generator.has(rawJsonField);
  }

  @Test
  void testRawJsonWriteToJackson() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode generator = mapper.createObjectNode();
    generator.put("message", "hello, world");
    generator.put(rawJsonField, rawJson);
    assert generator.size() == 2;
    assert generator.has("message");
    assert generator.has(rawJsonField);
  }

}
