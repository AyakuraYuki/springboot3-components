package cc.ayakurayuki.spring.components.logging.support.marker;

import java.util.Arrays;

class MarkerUtils {

  @SuppressWarnings("rawtypes")
  public static String toString(Object arg) {
    if (arg == null) {
      return "null";
    }

    Class argClass = arg.getClass();
    try {
      if (!argClass.isArray()) {
        return String.valueOf(arg);
      } else {
        if (argClass == byte[].class) {
          return Arrays.toString((byte[]) arg);
        } else if (argClass == short[].class) {
          return Arrays.toString((short[]) arg);
        } else if (argClass == int[].class) {
          return Arrays.toString((int[]) arg);
        } else if (argClass == long[].class) {
          return Arrays.toString((long[]) arg);
        } else if (argClass == char[].class) {
          return Arrays.toString((char[]) arg);
        } else if (argClass == float[].class) {
          return Arrays.toString((float[]) arg);
        } else if (argClass == double[].class) {
          return Arrays.toString((double[]) arg);
        } else if (argClass == boolean[].class) {
          return Arrays.toString((boolean[]) arg);
        } else {
          return Arrays.deepToString((Object[]) arg);
        }
      }
    } catch (Exception e) {
      System.err.println("Failed toString() invocation on an object of type [" + argClass.getName() + "]");
      e.printStackTrace();
      return "[FAILED toString()]";
    }
  }

}
