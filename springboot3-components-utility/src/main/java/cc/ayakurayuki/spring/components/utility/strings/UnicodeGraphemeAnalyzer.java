package cc.ayakurayuki.spring.components.utility.strings;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * A state machine implementation for handling Unicode grapheme clusters with
 * focus on emoji processing.
 * This implementation follows the Unicode 12.0 specification for grapheme
 * cluster recognition, particularly for handling complex emoji sequences
 * including ZWJ sequences, flag sequences, and modifier sequences.
 *
 * @author Ayakura Yuki
 */
public class UnicodeGraphemeAnalyzer {

  // possible_emoji := zwj_element (\x{200D} zwj_element)+
  // by using 200D, combine two emoji into a new emoji
  static final int JOINER = 0x200D;

  // emoji_modification :=
  //     \p{EMD}
  //     | (\x{FE0F} | \p{Me}) \p{Me}*
  //     | tag_modifier
  // E.g. 0x23 0xFE0F 0x20E3
  static final int ModifierBlack    = 0xFE0E;
  static final int ModifierColorFul = 0xFE0F;
  static final int ModifierKeyCap   = 0x20E3;

  // tag_modifier := \x{E0020}-\x{E007E}+ \x{E007F}
  static final int            ModifierTagRangeStart = 0xE0020;
  static final int            ModifierTagRangeEnd   = 0xE007F;
  static final Range<Integer> ModifierTagRange      = Range.closed(ModifierTagRangeStart, ModifierTagRangeEnd);

  // diversity
  static final Set<Integer> ModifierSkinTone = Set.of(
      0x1F3FB, //light skin tone
      0x1F3FC, //medium-light skin tone
      0x1F3FD, //medium skin tone
      0x1F3FE, //medium-dark skin tone
      0x1F3FF //dark skin tone
  );

  // status of this State Machine
  static final int STATE_DEFAULT        = 0x0;
  static final int STATE_EMOJI          = 0x1;
  static final int STATE_PRE_EMOJI      = 0x10;
  static final int STATE_NATIONAL_FLAG  = STATE_EMOJI | 0x100;
  static final int STATE_EMOJI_MODIFIER = STATE_EMOJI | 0x1000;
  static final int STATE_EMOJI_JOIN     = 0x10000;


  private final OptimizedEmojiModifierSet emojiModifier;
  private final List<InnerNode>           charUnitList;

  private int       currentIndex     = 0;
  private int       currentCodePoint = 0x0;
  private InnerNode currentChar      = new InnerNode(0);
  private int       currentState     = STATE_DEFAULT;


  public UnicodeGraphemeAnalyzer() {
    this(30);
  }

  public UnicodeGraphemeAnalyzer(int estimateCapacity) {
    // init char unit list
    this.charUnitList = new ArrayList<>(estimateCapacity);

    // init emoji modifier
    this.emojiModifier = new OptimizedEmojiModifierSet();
    this.emojiModifier.add(ModifierBlack);
    this.emojiModifier.add(ModifierColorFul);
    this.emojiModifier.add(ModifierKeyCap);
    for (Integer codePoint : ModifierSkinTone) {
      this.emojiModifier.add(codePoint);
    }
    this.emojiModifier.addRange(ModifierTagRangeStart, ModifierTagRangeEnd);
  }

  private void endChar() {
    this.currentState = STATE_DEFAULT;
    if (!this.currentChar.codePoints.isEmpty()) {
      this.charUnitList.add(this.currentChar);
      this.currentChar = new InnerNode(this.currentIndex);
    }
  }

  private void assertEmoji() {
    this.currentChar.isGraphemeCluster = true;
  }

  private void moveToNext() {
    this.currentChar.codePoints.add(this.currentCodePoint);
    this.currentIndex += Character.charCount(this.currentCodePoint);
  }

  private void moveToPrev() {
    int lastCodePoint = this.currentChar.codePoints.removeLast();
    this.currentIndex += Character.charCount(lastCodePoint);
  }

  /**
   * Processes a character sequence to identify grapheme clusters and emoji sequences.
   * <p>
   * This method analyzes the input string character by character, identifying:
   * <ul>
   *   <li>Simple emoji characters</li>
   *   <li>Complex emoji sequences (ZWJ sequences)</li>
   *   <li>National flag sequences</li>
   *   <li>Emoji with modifiers (skin tone, keycap, etc.)</li>
   * </ul>
   * <p>
   * The analysis results are stored internally and can be retrieved using {@link #getCharList()}.
   *
   * @param str the character sequence to process
   *
   * @throws NullPointerException if the input sequence is null
   */
  public void read(CharSequence str) {
    Objects.requireNonNull(str);
    int end = str.length();
    while (this.currentIndex < str.length()) {
      this.currentCodePoint = Character.codePointAt(str, this.currentIndex);

      if (this.currentState == STATE_EMOJI_JOIN) {
        if (this.isEmojiCodePoint(this.currentCodePoint)) {
          // emoji + emoji
          // +号后面是emoji，符合期望
          this.currentState = STATE_EMOJI;
          this.moveToNext();

        } else {
          // emoji + !emoji
          // 因为 + 后面没有跟另一个emoji，所以回塑到 + 这个字符
          // + 不再代表 emoji 的连=连接符
          this.moveToPrev();
          this.endChar();

        }

      } else if (this.currentState == STATE_NATIONAL_FLAG) {
        if (this.isRegionalIndicator(this.currentCodePoint)) {
          // flag_sequence := \p{RI} \p{RI}
          // 两个国家区域，完成一个国旗emoji，符合期望
          this.moveToNext();
        }
        // 上面如果是 else 的情况：
        // 没达到两个国家区域，但前面的也是emoji
        // 结束前面一个国家区域字符，并且在下一次遍历处理当前字符
        this.assertEmoji();
        this.endChar();

      } else if (this.currentState == STATE_PRE_EMOJI) {
        if (this.emojiModifier.contains(this.currentCodePoint)) {
          // maybeEmoji Modifier*
          // emoji 后面可以跟多个 Modifier
          this.currentState = STATE_EMOJI_MODIFIER;
          this.moveToNext();

        } else {
          // 结束前面一个字符，并且在下一次遍历处理当前字符
          this.endChar();
        }

      } else if ((this.currentState & STATE_EMOJI) != 0) {
        if (JOINER == this.currentCodePoint) {
          // emoji + emoji
          // 准备连接下一个emoji
          this.currentState = STATE_EMOJI_JOIN;
          this.moveToNext();

        } else if (this.emojiModifier.contains(this.currentCodePoint)) {
          // emoji Modifier*
          // emoji 或 Modifier 后面可以跟多个 Modifier
          this.currentState = STATE_EMOJI_MODIFIER;
          this.moveToNext();

        } else {
          // 结束前面一个Emoji，并且在下一次遍历处理当前字符
          this.assertEmoji();
          this.endChar();

        }

      } else if (isRegionalIndicator(this.currentCodePoint)) {
        // flag_sequence := \p{RI} \p{RI}
        // 遇到第一个国家区域，等待下一个国家区域可以合并成一个国旗emoji
        this.currentState = STATE_NATIONAL_FLAG;
        this.moveToNext();

      } else if (maybeEmojiCodePoint(this.currentCodePoint)) {
        // 有可能是emoji码点，由下一个码点是否是修饰符来决定
        this.currentState = STATE_PRE_EMOJI;
        this.moveToNext();

      } else if (isEmojiCodePoint(this.currentCodePoint)) {
        // emoji码点，等待下一个 Join 或者 Modifier
        this.currentState = STATE_EMOJI;
        this.moveToNext();

      } else {
        // 普通字符
        this.moveToNext();
        this.endChar();

      }

      if (getCurrentCharSize() >= end) {
        break;
      }
    }

    if (this.currentState != STATE_DEFAULT) {
      if ((this.currentState & STATE_EMOJI) != 0) {
        assertEmoji();
      }
      endChar();
    }
  }

  /**
   * Returns the current processing position within the input sequence.
   * <p>
   * This represents the actual character index (not grapheme cluster index)
   * in the input string being processed.
   *
   * @return the current character index position
   */
  public int getCurrentIndex() {
    return this.currentIndex;
  }

  /**
   * Returns the number of grapheme clusters processed so far.
   * <p>
   * This count represents visual characters, where complex emoji sequences
   * (including ZWJ sequences and flag sequences) are counted as single units.
   *
   * @return the number of processed grapheme clusters
   */
  public int getCurrentCharSize() {
    return this.charUnitList.size();
  }

  /**
   * Returns the list of processed character nodes.
   * <p>
   * Each node in the list represents a character or grapheme cluster,
   * containing information about its position, length, and whether it
   * forms a complete grapheme cluster (such as an emoji sequence).
   *
   * @return a list of InnerNode objects representing the processed characters
   */
  public List<InnerNode> getCharList() {
    return this.charUnitList;
  }

  /**
   * \p{Emoji}
   * <p>
   * zwj_element := \p{Emoji} emoji_modification?
   * <p>
   *
   * Emoji 的码点。这个方法不一定靠谱，通过 unicode v12 规范 emoji 表观察归纳所得。随着 unicode 版本更新，这个 codePoint 的范围可能会增大。
   * <p>
   * see <a href="https://unicode.org/emoji/charts-12.0/full-emoji-list.html">full-emoji-list</a>
   */
  private boolean isEmojiCodePoint(int codePoint) {
    return (Range.closed(0x1F200, 0x1FFFF).contains(codePoint))
        || (Range.closed(0x231A, 0x23FF).contains(codePoint))
        || (Range.closed(0x2460, 0x24FF).contains(codePoint)) // 带圈或括号的字母数字
        || (Range.closed(0x2500, 0x2FFF).contains(codePoint)) // 制表符/方块元素/几何图形/杂项/印刷/追加箭头/表意文字
        || (Range.closed(0x3200, 0x32FF).contains(codePoint)) // 带圈中日韩字母月份
        || isSpecialSymbol(codePoint);
  }

  /**
   * very special case
   */
  private boolean isSpecialSymbol(int codePoint) {
    return codePoint == 0x3030 // wavy dash
        || codePoint == 0x00A9 // copyright
        || codePoint == 0x00AE // registered
        || codePoint == 0x2122; // trade mark
  }

  /**
   * 不是独立 emoji 要和修饰符一起用。主要是方框数字，CodePoint = \p{Number} ModifierKeyCap
   * <p>
   * E.g 0x39 0x20E3
   */
  private boolean maybeEmojiCodePoint(int codePoint) {
    return Range.closed(0x0, 0x39).contains(codePoint)
        || Range.closed(0x2190, 0x21FF).contains(codePoint);
  }

  /**
   * Regional_Indicator
   * <p>
   * emoji_flag_sequence := regional_indicator regional_indicator
   * <p>
   * 国家区域标识，两个标识会变成一个国旗emoji。
   */
  private boolean isRegionalIndicator(int codePoint) {
    return Range.closed(0x1F000, 0x1F1FF).contains(codePoint);
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Represents a processed character node with its properties and metadata.
   * This is the public interface for accessing character information after processing.
   */
  @Data
  @Builder
  public static class Node {

    /**
     * The starting index of this node in the original character sequence
     */
    private Integer startIndex;

    /**
     * The length of this node in the original character sequence
     */
    private Integer length;

    /**
     * Whether this node represents a complete grapheme cluster (e.g., an emoji sequence)
     */
    private Boolean isGraphemeCluster;

    /**
     * The list of Unicode code points that make up this node
     */
    private List<Integer> codePoints;

    @Override
    public String toString() {
      List<String> hexes = codePoints.stream().map(Integer::toHexString).toList();
      return "Node{startIndex=%d, length=%d, isGraphemeCluster=%b, codePoints=%s}".formatted(
          startIndex,
          length,
          isGraphemeCluster,
          Joiner.on(",").join(hexes)
      );
    }

  }

  /**
   * Internal representation of a character node during processing.
   * Used by the state machine to track character properties while reading the input sequence.
   */
  @Data
  public static class InnerNode {

    private Integer       startIndex;
    private Boolean       isGraphemeCluster = false;
    private List<Integer> codePoints        = new ArrayList<>();

    /**
     * Creates a new InnerNode with the specified starting index.
     *
     * @param startIndex the starting position of this node in the original sequence
     */
    public InnerNode(Integer startIndex) {
      this.startIndex = startIndex;
    }

  }

}
