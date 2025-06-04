package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public record RSAKeyPair(
    KeyPair keyPair,
    RSAPrivateKey privateKey,
    RSAPublicKey publicKey
) {

  public static RSAKeyPair fromKeyPair(KeyPair keyPair) {
    return new RSAKeyPair(
        keyPair,
        (RSAPrivateKey) keyPair.getPrivate(),
        (RSAPublicKey) keyPair.getPublic()
    );
  }

  public String privateKeyContent() {
    if (privateKey == null) {
      return "";
    }
    PemObject pem = new PemObject("RSA PRIVATE KEY", privateKey.getEncoded());
    StringWriter writer = new StringWriter();
    try (PemWriter pemWriter = new PemWriter(writer)) {
      pemWriter.writeObject(pem);
      writer.flush();
    } catch (IOException ignored) {
      return "";
    }
    return writer.toString();
  }

  public String publicKeyContent() {
    if (publicKey == null) {
      return "";
    }
    PemObject pem = new PemObject("RSA PUBLIC KEY", publicKey.getEncoded());
    StringWriter writer = new StringWriter();
    try (PemWriter pemWriter = new PemWriter(writer)) {
      pemWriter.writeObject(pem);
      writer.flush();
    } catch (IOException e) {
      return "";
    }
    return writer.toString();
  }

}
