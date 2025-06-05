package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Ayakura Yuki
 */
public final class Symmetric {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static byte[] encrypt(CipherName algorithm, CipherMode mode, CipherPadding padding, byte[] src, byte[] key, byte[] iv) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);

    SecretKey secretKey = new SecretKeySpec(key, algorithm.algorithm);

    String transformation = Assembler.transformation(algorithm, mode, padding);
    Cipher cipher = Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);

    if (mode == CipherMode.ECB) {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    } else {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
    }

    return cipher.doFinal(src);
  }

  public static byte[] decrypt(CipherName algorithm, CipherMode mode, CipherPadding padding, byte[] src, byte[] key, byte[] iv) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);

    SecretKey secretKey = new SecretKeySpec(key, algorithm.algorithm);

    String transformation = Assembler.transformation(algorithm, mode, padding);
    Cipher cipher = Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);

    if (mode == CipherMode.ECB) {
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
    } else {
      cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
    }

    return cipher.doFinal(src);
  }

}
