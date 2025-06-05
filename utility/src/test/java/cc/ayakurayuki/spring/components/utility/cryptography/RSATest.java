package cc.ayakurayuki.spring.components.utility.cryptography;

import cc.ayakurayuki.spring.components.utility.cryptography.rsa.RSA;
import cc.ayakurayuki.spring.components.utility.cryptography.rsa.RSAAlgorithm;
import cc.ayakurayuki.spring.components.utility.cryptography.rsa.RSAKeyPair;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-20:59
 */
@RunWith(JUnit4.class)
public class RSATest {

  @Test
  public void testGenerateKeyPair() throws Exception {
    RSAKeyPair keyPair = RSA.generateKeyPair(2048);
    System.out.println(keyPair.privateKey() instanceof RSAPrivateCrtKey);
    System.out.println(keyPair.privateKey().getAlgorithm());

    keyPair = RSA.generateKeyPairWithBC(2048);
    System.out.println(keyPair.privateKey() instanceof RSAPrivateCrtKey);
    System.out.println(keyPair.privateKey().getAlgorithm());

    RSAPublicKey publicKey = RSA.generatePublicKey(keyPair.privateKey());
    System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
  }

  @Test
  public void test_crossLang() throws Exception {
    String privateKeyContent = """
        -----BEGIN RSA PRIVATE KEY-----
        MIIEowIBAAKCAQEAshmbJBp0pu6wZ3M5QgihNtg6YUtca0Niq0XCbb97Wo6lxeac
        IX6e8m6T4TVDAuRGcN/lUbg8s0iHvXX4WIUUaHI/lhoWwfDPcd8p0WfyxmkWkalB
        1JSc5sEWrZEO9R+s3XS2k5dTknTx/5uuYmw0gbCQIf6FzgtyAL6EaD8e9Mqv5cIt
        0roiZMSzV1RO67n7RcIONIqav4GcvgBYjY8lzbnftU53Ao52xBI+gcW/VktEcPzg
        LxSGRVz0NC5UgvRbLVm/DwJ1F+9tPX0nowPExlNKMmZuuFp8wW8aBcmup/FWdoNV
        JdtXFuurK8UA6K443AJJr97YJt/vUH5BmcvkSwIDAQABAoIBAAZXPnx9hGoGn86h
        vdYvEwa8WQu/14HjPXRX602s3mTPszg3WrPKW450Y/I3oIH4yvHl8lNRxn8GclHZ
        cKavfAeTtbmBhpxzXlPN0bBFFOAou4qxEUH9joWcb0TfPIV7Eg1t7vLNeSoRFe0+
        Y/guF0Ncxrsf/mj/R25JBNM7wr2NUmFk1xSjVvXV23Vm8tNTrf6Qe0cfEznXQyr+
        3b/hj4i4JdbcNt4EHzsAsIMEkZ30MKNTxRKcFkvOEp1mT+32+R+qjTGDCK3P9f2d
        WSad8vCcY8NnIH9DR0j9osGCAo4tJCFYn3s1vA6nIQ4i9BDVvm6cBjMwDlUzW0im
        mINqnBUCgYEA20q/n1bZ66HE8VN3UbTrlqRM5FxAXpxtofR41C7tyKmF0gIfOyUD
        fxEsZ2V1Cb9/ayOBKM55Sxk6Xi88LoYdoJJeCduSjjhX5sdBN/Pn3MYk8iVIKQUx
        A9sODoVFKzL0Khx2ArZCllnNVOarFcJjT9ro0dkY/fjnQFR1lyt4T3UCgYEAz+mr
        zDVIISDW1nkkezS3BlOdvUyYBczAGy+1aB1jTDn7ABmqmQUuqynoIWvKTCR8geF7
        smLjBU0SFqjJHV5uMhJif1p7zCGsxV6/fPkESudnWX6F0DYUMbHQqTEuciNKDRiV
        j4FwRk0KYExUVTImlsZ/Rp0gB/0bu9Og6qBOrL8CgYAiCHDCztzVN+7zsQt0j8p8
        P5V7X2HSDR7qoqFZ9MqMeNq+4/tq12y6fLTSeyl10NV6kDEMXzbt08V+NoB1i/Tv
        JagYiY4uKkpiE4xonZ7fYZT8S5LuYtLfkVCWKlK4yJiiLHaYJycgZGF8iqwEnX2C
        OCwn3TSGvelNz43MXz+1vQKBgQC0k2a6vawf2b2+GQkuWOJ9hDIEL3n5z5MqCY2t
        F5keo1z1AVXCAVY/Iu6R2GSjLS1WQNiGXox+FioXtBC4iUN8CauyoIvVB4pfAaOe
        j0jDJagFenYOGGoBn0ZzLFwCldPC/A8pzGoPJOpWKPU16ZDKcywn0F/5kEN+Jpgk
        L4clUQKBgEpp/PC2NwpNLHhQmBlxe9YrqqM2UW5lRpLMkW6cm1wmx8/HgDwadlLn
        XGLo+4cTIygsnEWp+vepY02v01ghP8oFA7iaXbCNNAUA29o5mOn1SLvGUWDRuhat
        Q2+R3rI7lOIq5+dT/EOQ1zArKzTY5zIomSEbD10h/ZEa6OWA5/vZ
        -----END RSA PRIVATE KEY-----
        """;
    String publicKeyContent = """
        -----BEGIN RSA PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAshmbJBp0pu6wZ3M5Qgih
        Ntg6YUtca0Niq0XCbb97Wo6lxeacIX6e8m6T4TVDAuRGcN/lUbg8s0iHvXX4WIUU
        aHI/lhoWwfDPcd8p0WfyxmkWkalB1JSc5sEWrZEO9R+s3XS2k5dTknTx/5uuYmw0
        gbCQIf6FzgtyAL6EaD8e9Mqv5cIt0roiZMSzV1RO67n7RcIONIqav4GcvgBYjY8l
        zbnftU53Ao52xBI+gcW/VktEcPzgLxSGRVz0NC5UgvRbLVm/DwJ1F+9tPX0nowPE
        xlNKMmZuuFp8wW8aBcmup/FWdoNVJdtXFuurK8UA6K443AJJr97YJt/vUH5Bmcvk
        SwIDAQAB
        -----END RSA PUBLIC KEY-----
        """;

    String data = "apple";
    String encrypted = "FoczMJnzUc2x9G7bUEDTxEwvj1ze4h7TLBp8r3EJAqwdJUCdXKoxQJHqUt7f7K/Slx2uOIc7oTgNwB56MyPOOI5PHo5ThkDBMkDMd7Y+6CmsO8gsjCBtZoMWn+PynDpyJCk88JmL5kQCWweNsr1S0CzQ8bidP4w2cwEeyAtYMFdARsPpY1rcWgyhMSPr+nV7cSpW/Q48eLWyzHTpFQZkbZiNvHEoMwjwGQa2/WysTl7sD841dv7UI9GLd/kydwEovVnyPYSed0POxbNSSPjLEdnXgcQLCMr7oG+RORx8a/csHbQ1RZOIwWoBn4piTg5oatRGBaMMVIh6yU+J45SPDA==";

    RSAPublicKey publicKey = RSA.toPublicKey(publicKeyContent);
    byte[] dst = RSA.encryptChunks(data.getBytes(), publicKey, RSAAlgorithm.RSA);
    System.out.println(Base64.getEncoder().encodeToString(dst));

    RSAPrivateKey privateKey = RSA.fromPKCS8ToPrivateKey(privateKeyContent);
    dst = RSA.decryptChunks(dst, privateKey, RSAAlgorithm.RSA);
    System.out.println(new String(dst, StandardCharsets.UTF_8));
    dst = RSA.decryptChunksWithBC(Base64.getDecoder().decode(encrypted), privateKey, RSAAlgorithm.PKCS1v15);
    System.out.println(new String(dst, StandardCharsets.UTF_8));
  }

}
