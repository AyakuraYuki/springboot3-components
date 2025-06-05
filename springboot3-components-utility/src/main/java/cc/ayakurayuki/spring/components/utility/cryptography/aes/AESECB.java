package cc.ayakurayuki.spring.components.utility.cryptography.aes;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * AES/ECB/PKCS7Padding
 *
 * @author Ayakura Yuki
 */
public final class AESECB {

  public static byte[] encrypt(String data, String key) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return encrypt(dataBytes, keyBytes);
  }

  public static byte[] encrypt(String data, byte[] key) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    return encrypt(dataBytes, key);
  }

  public static byte[] encrypt(byte[] data, String key) throws GeneralSecurityException {
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return encrypt(data, keyBytes);
  }

  public static byte[] encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
    return AES.encrypt(AESMode.ECB, data, key, null);
  }

  public static byte[] decrypt(String data, String key) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return decrypt(dataBytes, keyBytes);
  }

  public static byte[] decrypt(String data, byte[] key) throws GeneralSecurityException {
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    return decrypt(dataBytes, key);
  }

  public static byte[] decrypt(byte[] data, String key) throws GeneralSecurityException {
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return decrypt(data, keyBytes);
  }

  public static byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException {
    return AES.decrypt(AESMode.ECB, data, key, null);
  }

}
