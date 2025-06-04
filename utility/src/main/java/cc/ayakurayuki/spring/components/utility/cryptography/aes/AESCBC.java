package cc.ayakurayuki.spring.components.utility.cryptography.aes;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * AES/CBC/PKCS7Padding
 */
public final class AESCBC {

  public static byte[] encrypt(String data, String key, String iv) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
    return encrypt(dataBytes, keyBytes, ivBytes);
  }

  public static byte[] encrypt(String data, byte[] key, byte[] iv) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    return encrypt(dataBytes, key, iv);
  }

  public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return AES.encrypt(AESMode.CBC, data, key, iv);
  }

  public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return AES.decrypt(AESMode.CBC, data, key, iv);
  }

  public static byte[] decrypt(byte[] data, String key, String iv) throws GeneralSecurityException {
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
    return AES.decrypt(AESMode.CBC, data, keyBytes, ivBytes);
  }

}
