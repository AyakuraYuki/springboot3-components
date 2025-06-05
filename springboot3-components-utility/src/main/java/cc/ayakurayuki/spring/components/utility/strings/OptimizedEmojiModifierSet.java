package cc.ayakurayuki.spring.components.utility.strings;

import java.util.BitSet;

/**
 * An optimized set implementation for Unicode code points using BitSet.
 * This implementation is specifically designed for fast contains-checking
 * operations on a predefined range of integer values.
 *
 * @author Ayakura Yuki
 */
public class OptimizedEmojiModifierSet {

  public static final int MAX_CODE_POINT = 0x10FFFF;

  private final BitSet bitSet;

  /**
   * Creates a new OptimizedEmojiModifierSet with the maximum Unicode code
   * point capacity.
   * The Current Unicode standard defines code points up to 0x10FFFF.
   */
  public OptimizedEmojiModifierSet() {
    // Initialize with capacity for all possible Unicode code points
    // slightly larger than 0x10FFFF for safe margin
    this.bitSet = new BitSet(MAX_CODE_POINT + 1);
  }

  /**
   * Adds a code point to the set.
   *
   * @param codePoint the Unicode code point to add
   *
   * @throws IllegalArgumentException if the code point is negative or exceeds Unicode range
   */
  public void add(int codePoint) {
    if (codePoint < 0 || codePoint > MAX_CODE_POINT) {
      throw new IllegalArgumentException("invalid unicode code point: " + codePoint);
    }
    bitSet.set(codePoint);
  }

  /**
   * Adds all code points in the specified range (inclusive).
   *
   * @param start the starting code point
   * @param end   the ending code point (inclusive)
   *
   * @throws IllegalArgumentException if the range is invalid
   */
  public void addRange(int start, int end) {
    if (start < 0 || end > MAX_CODE_POINT || start > end) {
      throw new IllegalArgumentException("Invalid Unicode code point range: " + start + " to " + end);
    }
    bitSet.set(start, end + 1);
  }

  /**
   * Checks if the specified code point is in the set.
   * This operation is O(1) as it only needs to check a single bit.
   *
   * @param codePoint the Unicode code point to check
   *
   * @return true if the code point is in the set, false otherwise
   */
  public boolean contains(int codePoint) {
    if (codePoint < 0 || codePoint > MAX_CODE_POINT) {
      return false;
    }
    return bitSet.get(codePoint);
  }

  /**
   * Removes a code point from the set.
   *
   * @param codePoint the Unicode code point to remove
   */
  public void remove(int codePoint) {
    if (codePoint >= 0 && codePoint <= MAX_CODE_POINT) {
      bitSet.clear(codePoint);
    }
  }

  /**
   * Clears all code points from the set.
   */
  public void clear() {
    bitSet.clear();
  }

  /**
   * Returns the number of code points in the set.
   *
   * @return the number of set bits in the BitSet
   */
  public int size() {
    return bitSet.cardinality();
  }

}
