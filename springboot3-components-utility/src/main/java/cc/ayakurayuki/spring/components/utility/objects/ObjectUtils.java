package cc.ayakurayuki.spring.components.utility.objects;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectUtils {

  private static final int INITIAL_HASH = 7;
  private static final int MULTIPLIER   = 31;

  private static final String   EMPTY_STRING            = "";
  private static final String   NULL_STRING             = "null";
  private static final String   ARRAY_START             = "{";
  private static final String   ARRAY_END               = "}";
  private static final String   EMPTY_ARRAY             = ARRAY_START + ARRAY_END;
  private static final String   ARRAY_ELEMENT_SEPARATOR = ", ";
  private static final Object[] EMPTY_OBJECT_ARRAY      = new Object[0];

  /**
   * Determine whether the given object is an array: either an Object array or a primitive array.
   *
   * @param obj the object to check
   */
  public static boolean isArray(Object obj) {
    return (obj != null && obj.getClass().isArray());
  }

  /**
   * Determine whether the given array is empty: i.e. {@code null} or of zero length.
   *
   * @param array the array to check
   *
   * @see #isEmpty(Object)
   */
  public static boolean isEmpty(Object[] array) {
    return (array == null || array.length == 0);
  }

  /**
   * Determine whether the given object is empty.
   * <p>This method supports the following object types.
   * <ul>
   * <li>{@code Optional}: considered empty if not {@link Optional#isPresent()}</li>
   * <li>{@code Array}: considered empty if its length is zero</li>
   * <li>{@link CharSequence}: considered empty if its length is zero</li>
   * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
   * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
   * </ul>
   * <p>If the given object is non-null and not one of the aforementioned
   * supported types, this method returns {@code false}.
   *
   * @param obj the object to check
   *
   * @return {@code true} if the object is {@code null} or <em>empty</em>
   *
   * @see Optional#isPresent()
   * @see ObjectUtils#isEmpty(Object[])
   */
  public static boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    }

    if (obj instanceof Optional) {
      return ((Optional<?>) obj).isEmpty();
    }
    if (obj instanceof CharSequence) {
      return ((CharSequence) obj).isEmpty();
    }
    if (obj.getClass().isArray()) {
      return Array.getLength(obj) == 0;
    }
    if (obj instanceof Collection) {
      return ((Collection<?>) obj).isEmpty();
    }
    if (obj instanceof Map) {
      return ((Map<?, ?>) obj).isEmpty();
    }

    // else
    return false;
  }

  /**
   * Determine if the given objects are equal, returning {@code true} if both are {@code null} or {@code false} if only one is {@code null}.
   * <p>Compares arrays with {@code Arrays.equals}, performing an equality
   * check based on the array elements rather than the array reference.
   *
   * @param o1 first Object to compare
   * @param o2 second Object to compare
   *
   * @return whether the given objects are equal
   *
   * @see Object#equals(Object)
   * @see Arrays#equals
   */
  public static boolean nullSafeEquals(Object o1, Object o2) {
    if (o1 == o2) {
      return true;
    }
    if (o1 == null || o2 == null) {
      return false;
    }
    if (o1.equals(o2)) {
      return true;
    }
    if (o1.getClass().isArray() && o2.getClass().isArray()) {
      return arrayEquals(o1, o2);
    }
    return false;
  }

  /**
   * Compare the given arrays with {@code Arrays.equals}, performing an equality check based on the array elements rather than the array reference.
   *
   * @param o1 first array to compare
   * @param o2 second array to compare
   *
   * @return whether the given objects are equal
   *
   * @see #nullSafeEquals(Object, Object)
   * @see Arrays#equals
   */
  private static boolean arrayEquals(Object o1, Object o2) {
    if (o1 instanceof Object[] && o2 instanceof Object[]) {
      return Arrays.equals((Object[]) o1, (Object[]) o2);
    }
    if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
      return Arrays.equals((boolean[]) o1, (boolean[]) o2);
    }
    if (o1 instanceof byte[] && o2 instanceof byte[]) {
      return Arrays.equals((byte[]) o1, (byte[]) o2);
    }
    if (o1 instanceof char[] && o2 instanceof char[]) {
      return Arrays.equals((char[]) o1, (char[]) o2);
    }
    if (o1 instanceof double[] && o2 instanceof double[]) {
      return Arrays.equals((double[]) o1, (double[]) o2);
    }
    if (o1 instanceof float[] && o2 instanceof float[]) {
      return Arrays.equals((float[]) o1, (float[]) o2);
    }
    if (o1 instanceof int[] && o2 instanceof int[]) {
      return Arrays.equals((int[]) o1, (int[]) o2);
    }
    if (o1 instanceof long[] && o2 instanceof long[]) {
      return Arrays.equals((long[]) o1, (long[]) o2);
    }
    if (o1 instanceof short[] && o2 instanceof short[]) {
      return Arrays.equals((short[]) o1, (short[]) o2);
    }
    return false;
  }

}
