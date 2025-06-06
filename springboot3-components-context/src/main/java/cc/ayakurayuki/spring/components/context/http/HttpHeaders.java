package cc.ayakurayuki.spring.components.context.http;

public class HttpHeaders {

  public static final String X_REAL_IP = "X-Real-IP";

  public static final String X_FORWARDED_FOR = "X-Forwarded-For";

  public static final String X_FORWARDED_PORT = "X-Forwarded-Port";

  // source caller
  public static final String CALLER = "x1-ayspy-caller";

  // 全链路压测 mirror
  public static final String MIRROR = "x1-ayspy-mirror";

}
