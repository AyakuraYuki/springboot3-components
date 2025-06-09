package cc.ayakurayuki.spring.components.http.server.response;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.trace.TraceUtils;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JsonRsp {

  /**
   * response status code
   */
  private final int status;

  /**
   * response message
   */
  private final String message;

  /**
   * trace_id
   */
  private final String traceId;

  public JsonRsp(int status, String message) {
    this.status = status;
    this.message = message;
    this.traceId = TraceUtils.getTraceId();
  }

  public JsonRsp(@Nonnull ServerCode serverCode) {
    this.status = serverCode.code();
    this.message = serverCode.message();
    this.traceId = TraceUtils.getTraceId();
  }

}
