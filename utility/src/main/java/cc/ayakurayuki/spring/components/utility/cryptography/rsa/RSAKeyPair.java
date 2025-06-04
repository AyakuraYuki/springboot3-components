package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.io.StringWriter;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public record RSAKeyPair(
    RSAPrivateKey privateKey,
    RSAPublicKey publicKey
) {

  public String getPrivateKeyPemContent() {
    PemObject pem = new PemObject("RSA PRIVATE KEY", privateKey.getEncoded());
    StringWriter writer = new StringWriter();

    try (PemWriter pemWriter = new PemWriter(writer)) {

      pemWriter.writeObject(pem);
      writer.flush();

    } catch (Exception ignored) {

      byte[] payload = privateKey.getEncoded();
      String content = Base64.getEncoder().encodeToString(payload);
      StringBuilder builder = new StringBuilder();
      builder.append("-----BEGIN RSA PRIVATE KEY-----").append('\n');
      int length = content.length();
      for (int i = 0; i < length; i += 64) {
        int end = Math.min(i + 64, length);
        builder.append(content, i, end).append('\n');
      }
      builder.append("-----END RSA PRIVATE KEY-----");
      return builder.toString();

    }

    return writer.toString();
  }

  public String getPublicKeyPemContent() {
    PemObject pem = new PemObject("RSA PUBLIC KEY", publicKey.getEncoded());
    StringWriter writer = new StringWriter();
    try (PemWriter pemWriter = new PemWriter(writer)) {

      pemWriter.writeObject(pem);
      writer.flush();

    } catch (Exception e) {

      byte[] payload = publicKey.getEncoded();
      String content = Base64.getEncoder().encodeToString(payload);
      StringBuilder builder = new StringBuilder();
      builder.append("-----BEGIN RSA PUBLIC KEY-----").append('\n');
      int length = content.length();
      for (int i = 0; i < length; i += 64) {
        int end = Math.min(i + 64, length);
        builder.append(content, i, end).append('\n');
      }
      builder.append("-----END RSA PUBLIC KEY-----");
      return builder.toString();

    }

    return writer.toString();
  }

}
