package cc.ayakurayuki.spring.components.utility.cryptography.hmac;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Ayakura Yuki
 */
class HmacTest {

  @BeforeAll
  static void beforeAll() {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Test
  void testBCHmac() throws Exception {
    byte[] key = "00000000000000000000000000000000".getBytes(StandardCharsets.UTF_8);
    SecretKeySpec spec = new SecretKeySpec(key, "HmacSHA256");
    Mac mac = Mac.getInstance(spec.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
    mac.init(spec);
    byte[] dst = mac.doFinal("apple".getBytes(StandardCharsets.UTF_8));
    System.out.println(Base64.getEncoder().encodeToString(dst));
  }

}
