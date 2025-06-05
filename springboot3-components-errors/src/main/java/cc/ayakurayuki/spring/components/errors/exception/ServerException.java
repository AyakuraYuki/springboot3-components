package cc.ayakurayuki.spring.components.errors.exception;

import cc.ayakurayuki.spring.components.errors.ServerCode;

/**
 * @author Ayakura Yuki
 */
public class ServerException extends RuntimeException {

  private final Integer code;

  public ServerException(ServerCode serverCode) {
    super(serverCode.message());
    this.code = serverCode.code();
  }

  public ServerException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public ServerException(Integer code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  @Override
  public String toString() {
    String s = getClass().getName();
    String message = getLocalizedMessage();
    return (message != null) ? (s + ": " + code + " " + message) : s;
  }

  public static ServerException code(int code, String message, Object... args) {
    if (args.length == 0) {
      return new ServerException(code, message);
    }
    return new ServerException(code, String.format(message, args));
  }

  public static ServerException code(ServerCode code, Object... args) {
    return code(code.code(), code.message(), args);
  }

  public static ServerException code(ServerCode code, Throwable cause, Object... args) {
    return code(code.code(), code.message(), cause, args);
  }

  public static ServerException code(int code, String message, Throwable cause, Object... args) {
    if (args.length == 0) {
      return new ServerException(code, message, cause);
    }
    return new ServerException(code, String.format(message, args), cause);
  }

}
