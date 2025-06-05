package cc.ayakurayuki.spring.components.utility.bitwise;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Crop
 */
public class Attributes {

  private Attributes() {
  }

  /**
   * 位运算设置 attr 的某一位为 0 或者 1
   *
   * @param attr     当前值，十进制实值
   * @param position 第 n 位，位从 1 开始，1 是第 1 位，2 是第 2 位
   * @param flag     offset 位值
   *                 <ul>
   *                   <li>true：offset 位为 1</li>
   *                   <li>false：offset 位为 0</li>
   *                 </ul>
   *
   * @return 运算后的实值
   */
  public static int setBit(int attr, int position, boolean flag) {
    return flag ? attr | 1 << (position - 1) : attr & ~(1 << (position - 1));
  }

  /**
   * 位运算设置 attr 的某一位为 0 或者 1
   *
   * @param attr     当前值，十进制实值
   * @param position 第 n 位，位从 1 开始，1 是第 1 位，2 是第 2 位
   * @param flag     offset 位值
   *                 <ul>
   *                   <li>true：offset 位为 1</li>
   *                   <li>false：offset 位为 0</li>
   *                 </ul>
   *
   * @return 运算后的实值
   */
  public static long setBit(long attr, int position, boolean flag) {
    return flag ? attr | 1L << (position - 1) : attr & ~(1L << (position - 1));
  }

  /**
   * 获取 attr 二进制数值的第 offset - 1 值是 0 还是 1
   *
   * @param attr     当前值，十进制实值
   * @param position 第 n 位，位从 1 开始，1 是第 1 位，2 是第 2 位
   *
   * @return 第 offset - 1 位的值
   */
  public static int getBit(long attr, int position) {
    return (int) ((attr >> (position - 1)) & 1);
  }

  /**
   * 获取 attr 二进制数值的第 offset - 1 值是 0 还是 1
   *
   * @param attr     当前值，十进制实值
   * @param position 第 n 位，位从 1 开始，1 是第 1 位，2 是第 2 位
   *
   * @return 第 offset - 1 位的值
   */
  public static int getBit(int attr, int position) {
    return (attr >> (position - 1)) & 1;
  }

  /**
   * 获取数字和
   *
   * <p>
   * 举例说明，传入一个列表，包含 [ 0, 1, 0, 1 ]，表示：
   *   <ul>
   *     <li>第 1 位 position 是 0</li>
   *     <li>第 2 位 position 是 1</li>
   *     <li>第 3 位 position 是 0</li>
   *     <li>第 4 位 position 是 1</li>
   *   </ul>
   *   按照上述描述，转换成运算式，得到 {@code 0 & ~(1<<0) | (1<<1) & ~(1<<2) | (1<<3)}，其二进制表达为 {@code 1010}，十进制的运算结果为：10
   * </p>
   *
   * <p>
   *   建议使用 {@link #getTotalBits(List)} 以获取 Long 类型的结果。这个方法得到的结果类型是 Integer，存在类型范围限制。
   * </p>
   *
   * @param list 一个只包含 0 或者 1 的列表，0/1 在列表的位置 +1 代表了 position 的值
   *
   * @return 运算后的 attr 十进制实值
   *
   * @see #getTotalBits(List)
   */
  public static int getTotalBit(List<Integer> list) {
    int attr = 0;
    if (list != null && list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        attr = setBit(attr, i + 1, list.get(i) != 0);
      }
    }
    return attr;
  }

  /**
   * 获取数字和
   *
   * <p>
   * 举例说明，传入一个列表，包含 [ 0, 1, 0, 1 ]，表示：
   *   <ul>
   *     <li>第 1 位 position 是 0</li>
   *     <li>第 2 位 position 是 1</li>
   *     <li>第 3 位 position 是 0</li>
   *     <li>第 4 位 position 是 1</li>
   *   </ul>
   *   按照上述描述，转换成运算式，得到 {@code 0 & ~(1<<0) | (1<<1) & ~(1<<2) | (1<<3)}，其二进制表达为 {@code 1010}，十进制的运算结果为：10
   * </p>
   *
   * @param list 一个只包含 0 或者 1 的列表，0/1 在列表的位置 +1 代表了 position 的值
   *
   * @return 运算后的 attr 十进制实值
   */
  public static long getTotalBits(List<Integer> list) {
    long attr = 0;
    if (list != null && list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        attr = setBit(attr, i + 1, list.get(i) != 0);
      }
    }
    return attr;
  }

  /**
   * 从 attr 位实值获取指定 len 长度的位值
   *
   * <p>
   * 例如，传入 attr 是 1109，传入 len 是 15，<br/>得到返回结果列表是：{@code [1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0]}
   * </p>
   *
   * @param attr 当前值，十进制实值
   * @param len  要读取位的长度
   *
   * @return 给定长度的位值结果列表
   */
  public static List<Integer> getBitList(Integer attr, int len) {
    List<Integer> list = new ArrayList<>();
    for (int i = 1; i <= len; i++) {
      int b = getBit(attr, i);
      list.add(b);
    }
    return list;
  }

  /**
   * 从 attr 位实值获取指定 len 长度的位值
   *
   * <p>
   * 例如，传入 attr 是 1109，传入 len 是 15，<br/>得到返回结果列表是：{@code [1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0]}
   * </p>
   *
   * @param attr 当前值，十进制实值
   * @param len  要读取位的长度
   *
   * @return 给定长度的位值结果列表
   */
  public static List<Integer> getBitList(Long attr, int len) {
    List<Integer> list = new ArrayList<>();
    for (int i = 1; i <= len; i++) {
      int b = getBit(attr, i);
      list.add(b);
    }
    return list;
  }

}
