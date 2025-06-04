package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public record RSAKeyPair(
    RSAPrivateKey privateKey,
    RSAPublicKey publicKey
) {

  public String getPrivateKeyString() {
    byte[] payload = privateKey.getEncoded();
    return Base64.getEncoder().encodeToString(payload);
  }

  public String getPublicKeyString() {
    byte[] payload = publicKey.getEncoded();
    return Base64.getEncoder().encodeToString(payload);
  }

}
