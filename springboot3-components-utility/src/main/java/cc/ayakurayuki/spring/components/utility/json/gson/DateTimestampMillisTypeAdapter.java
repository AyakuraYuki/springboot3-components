package cc.ayakurayuki.spring.components.utility.json.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Type adapter: {@link Date} / timestamp millis
 *
 * @author Ayakura Yuki
 */
public class DateTimestampMillisTypeAdapter extends TypeAdapter<Date> {

  @Override
  public void write(JsonWriter out, Date value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.getTime());
    }
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }

    long timestamp;

    if (in.peek() == JsonToken.STRING) {

      String str = in.nextString();
      try {
        timestamp = Long.parseLong(str);
      } catch (NumberFormatException e) {
        throw new JsonParseException("invalid timestamp format: %s".formatted(str), e);
      }

    } else {

      timestamp = in.nextLong();

    }

    return new Date(timestamp);
  }

}
