package cc.ayakurayuki.spring.components.utility.cryptography.aes;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class AES {

  public static final String ALGORITHM = "AES";
  public static final String PADDING   = "PKCS7Padding";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static byte[] encrypt(AESMode mode, byte[] src, byte[] key, byte[] iv) throws GeneralSecurityException {
    SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
    Cipher cipher = Cipher.getInstance(selectCipher(mode), BouncyCastleProvider.PROVIDER_NAME);
    if (mode == AESMode.ECB) {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    } else {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
    }
    return cipher.doFinal(src);
  }

  public static byte[] decrypt(AESMode mode, byte[] src, byte[] key, byte[] iv) throws GeneralSecurityException {
    SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
    Cipher cipher = Cipher.getInstance(selectCipher(mode), BouncyCastleProvider.PROVIDER_NAME);
    if (mode == AESMode.ECB) {
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
    } else {
      IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
    }
    return cipher.doFinal(src);
  }

  public static SecretKey generateKey(int size) throws NoSuchAlgorithmException {
    if (size < 128) {
      size = 128; // minimum key size
    }
    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
    keyGenerator.init(size);
    return keyGenerator.generateKey();
  }

  public static byte[] generateKeyBytes(int keySize) throws NoSuchAlgorithmException {
    return generateKey(keySize).getEncoded();
  }

  private static String selectCipher(AESMode mode) {
    if (mode == null) {
      throw new IllegalArgumentException("mode id required");
    }
    return "%s/%s/%s".formatted(ALGORITHM, mode.mode, PADDING);
  }

}
