package cc.ayakurayuki.spring.components.utility.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-17:25
 */
@RunWith(JUnit4.class)
public class BCTest {

  @Test
  public void testPadding() throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());

    byte[] src = "apple".getBytes(StandardCharsets.UTF_8);
    byte[] key = "1234567812345678".getBytes(StandardCharsets.UTF_8);
    byte[] iv = "1234567812345678".getBytes(StandardCharsets.UTF_8);

    SecretKey secretKey = new SecretKeySpec(key, "AES");
    Cipher pkcs5 = Cipher.getInstance("AES/CBC/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
    pkcs5.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
    byte[] dst = pkcs5.doFinal(src);
    System.out.println("AES/CBC/PKCS5Padding -> (b64) " + Base64.getEncoder().encodeToString(dst));

    secretKey = new SecretKeySpec(key, "AES");
    Cipher pkcs7 = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME);
    pkcs7.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
    dst = pkcs7.doFinal(src);
    System.out.println("AES/CBC/PKCS7Padding -> (b64) " + Base64.getEncoder().encodeToString(dst));
  }

}
