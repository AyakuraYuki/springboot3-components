package cc.ayakurayuki.spring.components.utility.strings;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Ayakura Yuki
 */
public abstract class Strings {

  private static final String[] EMPTY_STRING_ARRAY = {};

  private static final String FOLDER_SEPARATOR = "/";

  private static final char FOLDER_SEPARATOR_CHAR = '/';

  private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

  private static final String TOP_PATH = "..";

  private static final String CURRENT_PATH = ".";

  private static final char EXTENSION_SEPARATOR = '.';

  /**
   * Check that the given {@code CharSequence} is neither {@code null} nor of length 0.
   * <p>Note: this method returns {@code true} for a {@code CharSequence}
   * that purely consists of whitespace.
   * <p><pre class="code">
   * StringUtils.hasLength(null) = false
   * StringUtils.hasLength("") = false
   * StringUtils.hasLength(" ") = true
   * StringUtils.hasLength("Hello") = true
   * </pre>
   *
   * @param str the {@code CharSequence} to check (may be {@code null})
   *
   * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
   *
   * @see #hasLength(String)
   * @see #hasText(CharSequence)
   */
  public static boolean hasLength(CharSequence str) {
    return (str != null && !str.isEmpty());
  }

  /**
   * Check that the given {@code String} is neither {@code null} nor of length 0.
   * <p>Note: this method returns {@code true} for a {@code String} that
   * purely consists of whitespace.
   *
   * @param str the {@code String} to check (may be {@code null})
   *
   * @return {@code true} if the {@code String} is not {@code null} and has length
   *
   * @see #hasLength(CharSequence)
   * @see #hasText(String)
   */
  public static boolean hasLength(String str) {
    return (str != null && !str.isEmpty());
  }

  /**
   * Check whether the given {@code CharSequence} contains actual <em>text</em>.
   * <p>More specifically, this method returns {@code true} if the
   * {@code CharSequence} is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
   * <p><pre class="code">
   * StringUtils.hasText(null) = false
   * StringUtils.hasText("") = false
   * StringUtils.hasText(" ") = false
   * StringUtils.hasText("12345") = true
   * StringUtils.hasText(" 12345 ") = true
   * </pre>
   *
   * @param str the {@code CharSequence} to check (may be {@code null})
   *
   * @return {@code true} if the {@code CharSequence} is not {@code null}, its length is greater than 0, and it does not contain whitespace only
   *
   * @see #hasText(String)
   * @see #hasLength(CharSequence)
   * @see Character#isWhitespace
   */
  public static boolean hasText(CharSequence str) {
    return (str != null && !str.isEmpty() && containsText(str));
  }

  /**
   * Check whether the given {@code String} contains actual <em>text</em>.
   * <p>More specifically, this method returns {@code true} if the
   * {@code String} is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
   *
   * @param str the {@code String} to check (may be {@code null})
   *
   * @return {@code true} if the {@code String} is not {@code null}, its length is greater than 0, and it does not contain whitespace only
   *
   * @see #hasText(CharSequence)
   * @see #hasLength(String)
   * @see Character#isWhitespace
   */
  public static boolean hasText(String str) {
    return (str != null && !str.isEmpty() && containsText(str));
  }

  private static boolean containsText(CharSequence str) {
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Cut string s around the first instance of sep,
   * returning the text before and after sep.
   * The found result reports whether sep appears in s.
   * If sep does not appear in s, cut returns s, "", false.
   */
  public static Cut cut(String s, String sep) {
    if (s == null) {
      return new Cut(null, "", false);
    }
    int i = s.indexOf(sep);
    if (i >= 0) {
      return new Cut(s.substring(0, i), s.substring(i + sep.length()), true);
    }
    return new Cut(s, "", false);
  }

  /**
   * count counts the number of non-overlapping instances of substr in s.
   * <p>
   * If substr is an empty string, count returns 1 + the number of Unicode code points in s.
   */
  public static int count(String s, String substr) {
    if (s == null) {
      return 0;
    }
    if (substr == null || substr.isEmpty()) {
      return (int) (1 + s.codePoints().count());
    }

    String str = s;
    int n = 0;
    for (; ; ) {
      int i = str.indexOf(substr);
      if (i == -1) {
        return n;
      }
      n++;
      str = str.substring(i + substr.length());
    }
  }

  /**
   * containsCTLByte reports whether that s contains any ASCII control character.
   */
  public static boolean containsCTLByte(String s) {
    for (int i = 0; i < s.length(); i++) {
      char b = s.charAt(i);
      if (b < ' ' || b == 0x7f) {
        return true;
      }
    }
    return false;
  }

  /**
   * contains reports whether substr is within s.
   */
  public static boolean contains(String s, String substr) {
    if (StringUtils.isEmpty(s)) {
      return false;
    }
    return s.contains(substr);
  }

  public static boolean startsWith(String s, String prefix) {
    return startsWith(s, prefix, 0);
  }

  public static boolean startsWith(String s, String prefix, int offset) {
    return StringUtils.length(s) >= StringUtils.length(prefix) && s.startsWith(prefix, offset);
  }

  public static boolean endsWith(String s, String suffix) {
    return StringUtils.length(s) >= StringUtils.length(suffix) && s.endsWith(suffix);
  }

}
