package cc.ayakurayuki.spring.components.utility.paging;

import cc.ayakurayuki.spring.components.utility.json.gson.NumberStringTypeAdapter;
import cc.ayakurayuki.spring.components.utility.json.jackson.NumberStringJacksonDeserializer;
import cc.ayakurayuki.spring.components.utility.json.jackson.NumberStringJacksonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import lombok.Data;

/**
 * Pagination context
 *
 * @author Ayakura Yuki
 */
@Data
public class PageContext {

  @JsonInclude(Include.NON_NULL) // jackson: ignore null field
  @JsonSerialize(using = NumberStringJacksonSerializer.class) // jackson: number -> string
  @JsonDeserialize(using = NumberStringJacksonDeserializer.class) // jackson: string -> number
  @JsonAdapter(NumberStringTypeAdapter.class) // gson: number <-> string
  private Number nextId;

  @JsonInclude(Include.NON_NULL)
  private String token;

}
