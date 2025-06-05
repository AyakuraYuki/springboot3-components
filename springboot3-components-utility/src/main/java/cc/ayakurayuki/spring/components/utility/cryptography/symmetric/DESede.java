package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * DESede, a.k.a. 3DES, Triple DES
 *
 * @author Ayakura Yuki
 */
public final class DESede {

  public static byte[] ECBEncrypt(byte[] data, byte[] key) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.ECB, CipherPadding.PKCS7Padding, data, key, null);
  }

  public static byte[] ECBEncrypt(byte[] data, byte[] key, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.ECB, padding, data, key, null);
  }

  public static byte[] ECBDecrypt(byte[] data, byte[] key) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.ECB, CipherPadding.PKCS7Padding, data, key, null);
  }

  public static byte[] ECBDecrypt(byte[] data, byte[] key, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.ECB, padding, data, key, null);
  }

  public static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CBC, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CBC, padding, data, key, iv);
  }

  public static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CBC, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CBC, padding, data, key, iv);
  }

  public static byte[] CFBEncrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CFB, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CFBEncrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CFB, padding, data, key, iv);
  }

  public static byte[] CFBDecrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CFB, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CFBDecrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CFB, padding, data, key, iv);
  }

  public static byte[] OFBEncrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.OFB, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] OFBEncrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.OFB, padding, data, key, iv);
  }

  public static byte[] OFBDecrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.OFB, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] OFBDecrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.OFB, padding, data, key, iv);
  }

  public static byte[] CTREncrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CTR, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CTREncrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.CTR, padding, data, key, iv);
  }

  public static byte[] CTRDecrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CTR, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] CTRDecrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.CTR, padding, data, key, iv);
  }

  public static byte[] GCMEncrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.encrypt(CipherName.DESede, CipherMode.GCM, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] GCMEncrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    Objects.requireNonNull(padding);
    return Symmetric.encrypt(CipherName.DESede, CipherMode.GCM, padding, data, key, iv);
  }

  public static byte[] GCMDecrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.GCM, CipherPadding.PKCS7Padding, data, key, iv);
  }

  public static byte[] GCMDecrypt(byte[] data, byte[] key, byte[] iv, CipherPadding padding) throws GeneralSecurityException {
    return Symmetric.decrypt(CipherName.DESede, CipherMode.GCM, padding, data, key, iv);
  }

}
