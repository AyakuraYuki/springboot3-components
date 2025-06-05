package cc.ayakurayuki.spring.components.utility.cryptography.hmac;

import cc.ayakurayuki.spring.components.utility.cryptography.hex.Hex;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Ayakura Yuki
 */
public final class Hmac {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static byte[] hmac(HmacAlgorithm algorithm, byte[] data, byte[] key) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    SecretKeySpec spec = new SecretKeySpec(key, algorithm.algorithm);
    Mac mac = Mac.getInstance(spec.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
    mac.init(spec);
    return mac.doFinal(data);
  }

  public static String hmacToBase64(HmacAlgorithm algorithm, byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(algorithm, data, key, Base64.getEncoder());
  }

  public static String hmacToURLBase64(HmacAlgorithm algorithm, byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(algorithm, data, key, Base64.getUrlEncoder());
  }

  public static String hmacToMimeBase64(HmacAlgorithm algorithm, byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(algorithm, data, key, Base64.getMimeEncoder());
  }

  public static String hmacToBase64(HmacAlgorithm algorithm, byte[] data, byte[] key, Encoder encoder) throws GeneralSecurityException {
    Objects.requireNonNull(encoder);
    byte[] dst = hmac(algorithm, data, key);
    if (dst == null) {
      return "";
    }
    return encoder.encodeToString(dst);
  }

  public static String hmacToHex(HmacAlgorithm algorithm, byte[] data, byte[] key) throws GeneralSecurityException {
    byte[] dst = hmac(algorithm, data, key);
    if (dst == null) {
      return "";
    }
    return Hex.toHexString(dst);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] hmacMD5(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmac(HmacAlgorithm.HmacMD5, data, key);
  }

  public static String hmacMD5ToBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(HmacAlgorithm.HmacMD5, data, key);
  }

  public static String hmacMD5ToURLBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToURLBase64(HmacAlgorithm.HmacMD5, data, key);
  }

  public static String hmacMD5ToMimeBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToMimeBase64(HmacAlgorithm.HmacMD5, data, key);
  }

  public static String hmacMD5ToHex(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToHex(HmacAlgorithm.HmacMD5, data, key);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] hmacSHA1(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmac(HmacAlgorithm.HmacSHA1, data, key);
  }

  public static String hmacSHA1ToBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(HmacAlgorithm.HmacSHA1, data, key);
  }

  public static String hmacSHA1ToURLBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToURLBase64(HmacAlgorithm.HmacSHA1, data, key);
  }

  public static String hmacSHA1ToMimeBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToMimeBase64(HmacAlgorithm.HmacSHA1, data, key);
  }

  public static String hmacSHA1ToHex(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToHex(HmacAlgorithm.HmacSHA1, data, key);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] hmacSHA256(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmac(HmacAlgorithm.HmacSHA256, data, key);
  }

  public static String hmacSHA256ToBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(HmacAlgorithm.HmacSHA256, data, key);
  }

  public static String hmacSHA256ToURLBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToURLBase64(HmacAlgorithm.HmacSHA256, data, key);
  }

  public static String hmacSHA256ToMimeBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToMimeBase64(HmacAlgorithm.HmacSHA256, data, key);
  }

  public static String hmacSHA256ToHex(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToHex(HmacAlgorithm.HmacSHA256, data, key);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] hmacSHA512(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmac(HmacAlgorithm.HmacSHA512, data, key);
  }

  public static String hmacSHA512ToBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToBase64(HmacAlgorithm.HmacSHA512, data, key);
  }

  public static String hmacSHA512ToURLBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToURLBase64(HmacAlgorithm.HmacSHA512, data, key);
  }

  public static String hmacSHA512ToMimeBase64(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToMimeBase64(HmacAlgorithm.HmacSHA512, data, key);
  }

  public static String hmacSHA512ToHex(byte[] data, byte[] key) throws GeneralSecurityException {
    return hmacToHex(HmacAlgorithm.HmacSHA512, data, key);
  }

}
