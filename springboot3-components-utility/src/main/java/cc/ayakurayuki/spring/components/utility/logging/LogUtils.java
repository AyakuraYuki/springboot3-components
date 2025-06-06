package cc.ayakurayuki.spring.components.utility.logging;

/**
 * LogUtil
 *
 * @author kuaiyue
 */
public class LogUtils {

  public static String separatorProcess(String msg) {
    if (msg == null) {
      return null;
    }
    return msg.replaceAll("\\|", "%7C");
  }

}
