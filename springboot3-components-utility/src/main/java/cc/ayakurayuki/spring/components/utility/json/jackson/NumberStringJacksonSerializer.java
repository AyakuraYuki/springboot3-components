package cc.ayakurayuki.spring.components.utility.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * @author Ayakura Yuki
 */
public class NumberStringJacksonSerializer extends JsonSerializer<Number> {

  @Override
  public void serialize(Number value, JsonGenerator gen, SerializerProvider serializer) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeString(value.toString());
    }
  }

}
