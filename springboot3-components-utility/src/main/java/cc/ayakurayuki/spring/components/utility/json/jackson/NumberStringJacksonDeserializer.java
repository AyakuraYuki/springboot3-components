package cc.ayakurayuki.spring.components.utility.json.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * @author Ayakura Yuki
 */
public class NumberStringJacksonDeserializer extends JsonDeserializer<Number> {

  @Override
  public Number deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
    String str = parser.getValueAsString();
    if (str == null || str.isEmpty()) {
      return null;
    }
    try {
      if (str.contains(".")) {
        return Double.valueOf(str);
      } else {
        return Long.valueOf(str);
      }
    } catch (NumberFormatException e) {
      throw new JsonParseException(parser, "invalid number format: %s".formatted(str), e);
    }
  }

}
