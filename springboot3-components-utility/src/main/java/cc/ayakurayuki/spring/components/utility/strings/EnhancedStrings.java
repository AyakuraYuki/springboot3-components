package cc.ayakurayuki.spring.components.utility.strings;

import cc.ayakurayuki.spring.components.utility.collection.CollectionUtils;
import cc.ayakurayuki.spring.components.utility.strings.UnicodeGraphemeAnalyzer.InnerNode;
import cc.ayakurayuki.spring.components.utility.strings.UnicodeGraphemeAnalyzer.Node;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enhanced string utility class based on Unicode 12.0
 * <p>
 * Provides advanced string handling capabilities with proper Unicode support,
 * especially for handling emoji and complex grapheme clusters.
 *
 * @author Ayakura Yuki
 */
public final class EnhancedStrings {

  /**
   * Gets the visual length of a text sequence.
   * <p>
   * Uses a state machine to evaluate Unicode character values and determines
   * the visual length of the text.
   * Composite emoji characters are counted as a single character based on their
   * visual representation.
   *
   * @param charSequence the text content to measure
   *
   * @return the visual length of the text in grapheme clusters
   */
  public static int length(CharSequence charSequence) {
    if (charSequence == null || charSequence.isEmpty()) {
      return 0;
    }
    UnicodeGraphemeAnalyzer sm = new UnicodeGraphemeAnalyzer();
    sm.read(charSequence);
    return sm.getCurrentCharSize();
  }

  /**
   * Analyzes characters in a string and returns their properties as sequential
   * nodes.
   * <p>
   * Each node contains information about character properties including
   * position, length, and whether it forms a grapheme cluster.
   *
   * @param charSequence the text content to analyze
   *
   * @return a list of Node objects containing character properties
   */
  public static List<Node> analyseText(CharSequence charSequence) {
    if (charSequence == null || charSequence.isEmpty()) {
      return Collections.emptyList();
    }

    UnicodeGraphemeAnalyzer sm = new UnicodeGraphemeAnalyzer();
    sm.read(charSequence);

    List<Node> nodes = new ArrayList<>();

    for (InnerNode innerNode : sm.getCharList()) {
      int length = 0;
      for (Integer codePoint : innerNode.getCodePoints()) {
        length += Character.charCount(codePoint);
      }
      Node node = Node.builder()
          .startIndex(innerNode.getStartIndex())
          .length(length)
          .isGraphemeCluster(innerNode.getIsGraphemeCluster())
          .codePoints(innerNode.getCodePoints())
          .build();
      nodes.add(node);
    }

    return nodes;
  }

  /**
   * Determines if a character at the specified visual position is an emoji.
   * <p>
   * Note: The index parameter refers to the visual index (where each emoji counts as one),
   * not the character index in the string.
   * <p>
   * An emoji may consist of multiple Unicode code points, appearing as a single visual
   * character but occupying multiple character positions in the string.
   *
   * @param charSequence the string that may contain emoji
   * @param index        the visual index position (counting each emoji as one unit)
   *
   * @return true if the character at the specified position is an emoji, false otherwise
   */
  public static boolean isEmoji(CharSequence charSequence, int index) {
    List<Node> nodes = analyseText(charSequence);
    return isEmoji(nodes, index);
  }

  /**
   * Determines if a character at the specified visual position is an emoji
   * using pre-analyzed nodes.
   * <p>
   * Note: The index parameter refers to the visual index (where each emoji counts as one),
   * not the character index in the string.
   * <p>
   * An emoji may consist of multiple Unicode code points, appearing as a single visual
   * character but occupying multiple character positions in the string.
   *
   * @param nodes the list of nodes obtained from {@link #analyseText(CharSequence)}
   * @param index the visual index position (counting each emoji as one unit)
   *
   * @return true if the character at the specified position is an emoji, false otherwise
   */
  public static boolean isEmoji(List<Node> nodes, int index) {
    if (CollectionUtils.isEmpty(nodes)) {
      return false;
    }
    return MoreObjects.firstNonNull(nodes.get(index).getIsGraphemeCluster(), false);
  }

  /**
   * Determines if a character at the specified character position is an emoji.
   * <p>
   * Note: The index parameter refers to the actual character index in the string,
   * not the visual position.
   * <p>
   * An emoji may consist of multiple Unicode code points, appearing as a single visual
   * character but occupying multiple character positions in the string.
   *
   * @param charSequence the string that may contain emoji
   * @param index        the character index in range [0, String::length]
   *
   * @return true if the character at the specified position is an emoji, false otherwise
   */
  public static boolean isEmojiInChars(CharSequence charSequence, int index) {
    List<Node> nodes = analyseText(charSequence);
    return isEmojiInChars(nodes, index);
  }

  /**
   * Determines if a character at the specified character position is an emoji
   * using pre-analyzed nodes.
   * <p>
   * Note: The index parameter refers to the actual character index in the string,
   * not the visual position.
   * <p>
   * An emoji may consist of multiple Unicode code points, appearing as a single visual
   * character but occupying multiple character positions in the string.
   *
   * @param nodes the list of nodes obtained from {@link #analyseText(CharSequence)}
   * @param index the character index in range [0, String::length]
   *
   * @return true if the character at the specified position is an emoji, false otherwise
   */
  public static boolean isEmojiInChars(List<Node> nodes, int index) {
    if (CollectionUtils.isEmpty(nodes)) {
      return false;
    }
    int visionIndex = Collections.binarySearch(nodes, null, (lhs, rhs) -> {
      if (index < lhs.getStartIndex()) {
        return 1;
      } else if (index >= lhs.getStartIndex() + lhs.getLength()) {
        return -1;
      } else {
        return 0;
      }
    });
    if (visionIndex < 0) {
      return false;
    }
    return isEmoji(nodes, index);
  }

  /**
   * Creates a subsequence of a string containing emoji characters, ensuring that no emoji
   * is split in the process.
   * <p>
   * This method preserves the integrity of emoji characters in the resulting subsequence.
   *
   * @param charSequence the text content to substring
   * @param end          the ending index (exclusive)
   *
   * @return the subsequence from the start (0) to the specified end position
   */
  public static CharSequence subSequence(CharSequence charSequence, int end) {
    return subSequence(charSequence, 0, end);
  }

  /**
   * Creates a subsequence of a string containing emoji characters, ensuring that no emoji
   * is split in the process.
   * <p>
   * This method preserves the integrity of emoji characters in the resulting subsequence.
   * The end index has some tolerance and may exceed the visual range but must not exceed
   * the character index range.
   *
   * @param charSequence the text content to substring
   * @param start        the starting index (inclusive)
   * @param end          the ending index (exclusive)
   *
   * @return the subsequence from the specified start to end position
   *
   * @throws IndexOutOfBoundsException if start is negative, end is greater than string length,
   *                                   or start is greater than end
   */
  public static CharSequence subSequence(CharSequence charSequence, int start, int end) {
    if (charSequence == null || charSequence.isEmpty()) {
      return charSequence;
    }
    if (start < 0 || end > charSequence.length()) {
      throw new IndexOutOfBoundsException("The index should be in range [0, " + charSequence.length() + "], but actually start = " + start + " and end = " + end + ".");
    }
    if (start > end) {
      throw new IndexOutOfBoundsException("The start index must smaller than the end index, but actually start = " + start + " and end = " + end + ".");
    }
    if (start == end) {
      return "";
    }

    UnicodeGraphemeAnalyzer sm = new UnicodeGraphemeAnalyzer();
    sm.read(charSequence);
    List<InnerNode> charList = sm.getCharList();

    InnerNode startNode = charList.get(start);
    if (startNode == null) {
      return "";
    }
    int startIndex = startNode.getStartIndex();

    if (end > charList.size()) {
      end = charList.size();
    }

    InnerNode endNode = charList.get(end - 1);
    if (endNode == null) {
      return charSequence.subSequence(startIndex, charSequence.length());
    }

    int endStartIndex = endNode.getStartIndex();
    int sum = 0;
    for (int v : endNode.getCodePoints()) {
      sum += Character.charCount(v);
    }
    int endIndex = endStartIndex + sum;
    return charSequence.subSequence(startIndex, endIndex);
  }

  /**
   * Converts text content to a list of Unicode code point representations.
   * <p>
   * Each character in the input string is converted to its Unicode code point
   * in the format "U+XXXX" where XXXX is the hexadecimal value.
   *
   * @param charSequence the text content to convert
   *
   * @return a list of Unicode code point strings for each visual character
   */
  public static List<String> transferToUnicodeCodes(CharSequence charSequence) {
    if (charSequence == null || charSequence.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<>();
    int codePoint;
    for (int i = 0; i < charSequence.length(); i += Character.charCount(codePoint)) {
      codePoint = Character.codePointAt(charSequence, i);
      result.add("U+" + Integer.toHexString(codePoint));
    }
    return result;
  }

}
