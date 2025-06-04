package cc.ayakurayuki.spring.components.utility.cryptography.hex;

/**
 * {@link Hex} extends to {@link org.bouncycastle.util.encoders.Hex}, and presents some
 * useful methods.
 */
public final class Hex extends org.bouncycastle.util.encoders.Hex {

  public static String toHexStringLowerCase(byte[] src) {
    return toHexString(src).toLowerCase();
  }

  public static String toHexStringUpperCase(byte[] src) {
    return toHexString(src).toUpperCase();
  }

}
