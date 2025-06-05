package cc.ayakurayuki.spring.components.utility.strings;

import java.util.Random;

/**
 * @author Ayakura Yuki
 */
public abstract class RandomStrings {

  private static final String lower  = "abcdefghijklmnopqrstuvwxyz";
  private static final String upper  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String digit  = "0123456789";
  private static final String symbol = "!@#$%^&*()-_=+,./:;<>?[]{|}~\\\"'";

  public static final int Lower           = 1; // 1 << 0
  public static final int Upper           = 1 << 1; // 1 << 0
  public static final int Digit           = 1 << 2; // 1 << 0
  public static final int LowerUpper      = Lower | Upper;
  public static final int LowerDigit      = Lower | Digit;
  public static final int UpperDigit      = Upper | Digit;
  public static final int LowerUpperDigit = Lower | Upper | Digit;

  public static String random(int size, int flags) {
    StringBuilder chars = new StringBuilder();
    if ((flags & Lower) == Lower) {
      chars.append(lower);
    }
    if ((flags & Upper) == Upper) {
      chars.append(upper);
    }
    if ((flags & Digit) == Digit) {
      chars.append(digit);
    }

    char[] token = chars.toString().toCharArray();
    StringBuilder builder = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < size; i++) {
      builder.append(token[random.nextInt(token.length)]);
    }

    return builder.toString();
  }

  public static String password(int size) {
    if (size < 6) {
      size = 6; // the minimum size is 6
    }

    String chars = lower + upper + digit + symbol;
    char[] token = chars.toCharArray();
    StringBuilder builder = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < size; i++) {
      builder.append(switch (i) {
        case 0 -> upper.charAt(random.nextInt(upper.length()));
        case 1 -> lower.charAt(random.nextInt(lower.length()));
        case 2 -> symbol.charAt(random.nextInt(symbol.length()));
        case 3 -> digit.charAt(random.nextInt(digit.length()));
        default -> token[random.nextInt(token.length)];
      });
    }

    return builder.toString();
  }

}
