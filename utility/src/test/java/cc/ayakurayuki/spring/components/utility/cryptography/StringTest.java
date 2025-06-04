package cc.ayakurayuki.spring.components.utility.cryptography;

import cc.ayakurayuki.spring.components.utility.cryptography.rsa.RSA;
import cc.ayakurayuki.spring.components.utility.cryptography.rsa.RSAKeyPair;
import java.security.GeneralSecurityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-20:59
 */
@RunWith(JUnit4.class)
public class StringTest {

  @Test
  public void testTrimRSAContent() throws GeneralSecurityException {
    RSAKeyPair keyPair = RSA.generateKey();
    System.out.println(keyPair.getPrivateKeyPemContent());
    System.out.println(keyPair.getPublicKeyPemContent());
  }

}
