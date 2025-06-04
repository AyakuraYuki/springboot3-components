package cc.ayakurayuki.spring.components.utility.cryptography.rc4;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-18:11
 */
public final class RC4 {

  private static final String ALGORITHM = "RC4";

  public static byte[] encrypt(byte[] src, byte[] key) {
    try {
      Key keySpec = new SecretKeySpec(key, ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      return cipher.doFinal(src);
    } catch (Exception e) {
      return null;
    }
  }

  public static byte[] encrypt(String src, String key) {
    return encrypt(src.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] decrypt(byte[] src, byte[] key) {
    try {
      Key keySpec = new SecretKeySpec(key, ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
      return cipher.doFinal(src);
    } catch (Exception e) {
      return null;
    }
  }

  public static String decrypt(byte[] src, String key) {
    byte[] dst = decrypt(src, key.getBytes(StandardCharsets.UTF_8));
    if (dst == null) {
      return "";
    }
    return new String(dst, StandardCharsets.UTF_8);
  }

}
