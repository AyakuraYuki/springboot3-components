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
 * @date 2025/06/05-21:14
 */
class HmacTest {

  @BeforeAll
  static void beforeAll() {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Test
  void testBCHmac() throws Exception {
    var key = "00000000000000000000000000000000".getBytes(StandardCharsets.UTF_8);
    var spec = new SecretKeySpec(key, "HmacSHA256");
    var mac = Mac.getInstance(spec.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
    mac.init(spec);
    var dst = mac.doFinal("apple".getBytes(StandardCharsets.UTF_8));
    System.out.println(Base64.getEncoder().encodeToString(dst));
  }

}
