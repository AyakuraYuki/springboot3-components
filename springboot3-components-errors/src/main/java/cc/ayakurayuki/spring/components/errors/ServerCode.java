package cc.ayakurayuki.spring.components.errors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ayakura Yuki
 */
public record ServerCode(
    int code,
    String message
) {

  public static final ServerCode SUCCESS              = create(0); // success
  public static final ServerCode APP_NOT_EXIST        = create(1); // 应用程序不存在或已被封禁
  public static final ServerCode SIGN_ERROR           = create(3); // API校验密匙错误
  public static final ServerCode NOT_LOGIN            = create(101); // 未登录
  public static final ServerCode ACCOUNT_BANED        = create(102); // 账号被封停
  public static final ServerCode ILLEGAL_CSRF         = create(111); // csrf 校验失败
  public static final ServerCode SERVICE_UPDATE       = create(112); // 系统升级中，敬请谅解
  public static final ServerCode NOT_MODIFIED         = create(304); // 木有改动
  public static final ServerCode BAD_REQUEST          = create(400); // 请求错误
  public static final ServerCode UNAUTHORIZED         = create(401); // 未授权
  public static final ServerCode FORBIDDEN            = create(403); // 访问权限不足
  public static final ServerCode NOT_FOUND            = create(404); // 啥都木有
  public static final ServerCode METHOD_NOT_SUPPORT   = create(405); // 不支持该方法
  public static final ServerCode CONFLICT             = create(409); // 请求冲突
  public static final ServerCode TOO_MANY_REQUESTS    = create(429); // 请求过快
  public static final ServerCode CLIENT_CLOSE_REQUEST = create(499); // 请求被关闭
  public static final ServerCode SERVER_ERROR         = create(500); // 服务器错误
  public static final ServerCode NOT_IMPLEMENTED      = create(501); // 服务未实现
  public static final ServerCode BAD_CONNECTION       = create(502); // 连接错误
  public static final ServerCode SERVICE_UNAVAILABLE  = create(503); // 服务暂不可用
  public static final ServerCode DEADLINE_EXCEEDED    = create(504); // 处理超时
  public static final ServerCode LIMIT_EXCEEDED       = create(509); // 请求过快
  public static final ServerCode FAIL_FILE_NOT_EXIST  = create(616); // 上传文件不存在
  public static final ServerCode FAIL_FILE_TOO_LARGE  = create(617); // 上传文件太大

  private static final Map<Integer, String> CODES = new ConcurrentHashMap<>();

  static {
    CODES.putAll(defaults());
  }

  public static ServerCode create(int code) {
    return new ServerCode(code, "");
  }

  public static ServerCode create(int code, String message) {
    return new ServerCode(code, message);
  }

  public static void addCodes(Map<Integer, String> codes) {
    if (codes != null) {
      CODES.putAll(codes);
    }
  }

  private static Map<Integer, String> defaults() {
    return Map.ofEntries(
        Map.entry(SUCCESS.code(), ""),
        Map.entry(APP_NOT_EXIST.code(), "应用程序不存在或已被封禁"),
        Map.entry(SIGN_ERROR.code(), "API校验密匙错误"),
        Map.entry(NOT_LOGIN.code(), "未登录"),
        Map.entry(ACCOUNT_BANED.code(), "账号被封停"),
        Map.entry(ILLEGAL_CSRF.code(), "csrf 校验失败"),
        Map.entry(SERVICE_UPDATE.code(), "系统升级中，敬请谅解"),
        Map.entry(NOT_MODIFIED.code(), "木有改动"),

        Map.entry(BAD_REQUEST.code(), "请求错误"),
        Map.entry(UNAUTHORIZED.code(), "未授权"),
        Map.entry(FORBIDDEN.code(), "访问权限不足"),
        Map.entry(NOT_FOUND.code(), "啥都木有"),
        Map.entry(METHOD_NOT_SUPPORT.code(), "不支持该方法"),
        Map.entry(CONFLICT.code(), "请求冲突"),
        Map.entry(TOO_MANY_REQUESTS.code(), "请求过快"),
        Map.entry(CLIENT_CLOSE_REQUEST.code(), "请求被关闭"),

        Map.entry(SERVER_ERROR.code(), "服务器错误"),
        Map.entry(NOT_IMPLEMENTED.code(), "服务未实现"),
        Map.entry(BAD_CONNECTION.code(), "连接错误"),
        Map.entry(SERVICE_UNAVAILABLE.code(), "服务暂不可用"),
        Map.entry(DEADLINE_EXCEEDED.code(), "处理超时"),
        Map.entry(LIMIT_EXCEEDED.code(), "请求过快"),

        Map.entry(FAIL_FILE_NOT_EXIST.code(), "上传文件不存在"),
        Map.entry(FAIL_FILE_TOO_LARGE.code(), "上传文件太大")
    );
  }

  @Override
  public String message() {
    if (message == null || message.isEmpty()) {
      return CODES.getOrDefault(code, String.valueOf(code));
    }
    return message;
  }

}
