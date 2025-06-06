package cc.ayakurayuki.spring.components.context.util;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.IP;
import cc.ayakurayuki.spring.components.context.User;
import cc.ayakurayuki.spring.components.context.http.HttpContext;
import com.google.common.primitives.Longs;
import java.util.Optional;

/**
 * Context utility
 *
 * @author kuaiyue
 */
public class ContextUtils {

  public static String getPath() {
    return Context.current().getPath();
  }

  public static long getUid() {
    return Optional.ofNullable(Context.current().getUser())
        .map(User::getId)
        .orElse(0L);
  }

  public static String getUserIp() {
    return Optional.ofNullable(Context.current().getIp())
        .map(IP::userIP)
        .orElse("");
  }

  public static boolean isMirror() {
    return Context.current().isMirror();
  }

  public static String getCookie() {
    return getFromHttpMetadata("cookie");
  }

  public static String getRefer() {
    return getFromHttpMetadata("refer");
  }

  public static long getUserIDFromParameter() {
    Long userID = Longs.tryParse(getFromParameter("userID"));
    return Optional.ofNullable(userID).orElse(0L);
  }

  private static String getFromParameter(String key) {
    Context context = Context.current();
    if (context instanceof HttpContext httpContext) {
      return Optional.ofNullable(httpContext.getParameter())
          .map(parameter -> parameter.get(key))
          .map(value -> value[0])
          .orElse("");
    }
    return "";
  }

  private static String getFromHttpMetadata(String key) {
    Context context = Context.current();
    if (context instanceof HttpContext httpContext) {
      return Optional.ofNullable(httpContext.getMetadata())
          .map(metadata -> metadata.get(key))
          .orElse("");
    }
    return "";
  }

}
