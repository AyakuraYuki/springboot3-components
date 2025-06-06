package cc.ayakurayuki.spring.components.utility.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Date;

/**
 * @author Ayakura Yuki
 */
public class DateTimestampMillisJacksonSerializer extends JsonSerializer<Date> {

  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider serializer) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeNumber(value.getTime());
    }
  }

}
