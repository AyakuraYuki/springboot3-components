package cc.ayakurayuki.spring.components.utility.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;

/**
 * @author Ayakura Yuki
 */
public class DateTimestampMillisJacksonDeserializer extends JsonDeserializer<Date> {

  @Override
  public Date deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
    JsonToken token = parser.getCurrentToken();
    if (token == JsonToken.VALUE_NULL) {
      return null;
    }

    long timestamp = switch (token) {
      case VALUE_NUMBER_INT -> parser.getValueAsLong();
      case VALUE_STRING -> {
        String str = parser.getValueAsString();
        try {
          yield Long.parseLong(str);
        } catch (NumberFormatException e) {
          throw new JsonParseException(parser, "invalid timestamp format: %s".formatted(str), e);
        }
      }
      default -> throw new JsonParseException(parser, "unexpected timestamp value: %s".formatted(token));
    };

    return new Date(timestamp);
  }

}
