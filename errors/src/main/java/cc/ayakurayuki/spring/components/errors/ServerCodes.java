package cc.ayakurayuki.spring.components.errors;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-15:37
 */
public class ServerCodes {

  public static boolean is5xx(int code) {
    int absCode = Math.abs(code);
    return absCode >= 500 && absCode <= 599;
  }

  public static boolean isOK(int code) {
    return code == 0 || (code >= 100 && code <= 299);
  }

}
