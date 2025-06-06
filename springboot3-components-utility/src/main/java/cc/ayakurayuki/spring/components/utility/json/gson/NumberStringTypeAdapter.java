package cc.ayakurayuki.spring.components.utility.json.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author Ayakura Yuki
 */
public class NumberStringTypeAdapter extends TypeAdapter<Number> {

  @Override
  public void write(JsonWriter out, Number value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public Number read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }

    String str = in.nextString();
    try {
      if (str.contains(".")) {
        return Double.valueOf(str);
      } else {
        return Long.valueOf(str);
      }
    } catch (NumberFormatException e) {
      throw new JsonParseException("invalid number format: %s".formatted(str), e);
    }
  }

}
