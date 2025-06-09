package cc.ayakurayuki.spring.components.http.server.response;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

/**
 * Biz API response data result
 */
@Getter
@ToString(callSuper = true)
public class JsonResult<T> extends JsonRsp {

  private final T data;

  protected JsonResult(T data) {
    this(0, "", data);
  }

  protected JsonResult(int status, T data) {
    this(status, "", data);
  }

  protected JsonResult(@Nonnull ServerCode serverCode) {
    this(serverCode.code(), serverCode.message(), null);
  }

  protected JsonResult(@Nonnull ServerCode serverCode, T data) {
    this(serverCode.code(), serverCode.message(), data);
  }

  protected JsonResult(int status, String message, T data) {
    super(status, message);
    this.data = data;
  }

  public static JsonResult<Map<String, Object>> success() {
    return new JsonResult<>(new HashMap<>());
  }

  public static <T> JsonResult<T> success(T data) {
    return new JsonResult<>(data);
  }

  public static <T> JsonResult<T> create(@Nonnull ServerCode serverCode) {
    return new JsonResult<>(serverCode);
  }

  public static <T> JsonResult<T> create(@Nonnull ServerCode serverCode, T data) {
    return new JsonResult<>(serverCode, data);
  }

  public static <T> JsonResult<T> create(int status, String message) {
    return new JsonResult<>(status, message, null);
  }

  public static <T> JsonResult<T> create(int status, String message, T data) {
    return new JsonResult<>(status, message, data);
  }

}
