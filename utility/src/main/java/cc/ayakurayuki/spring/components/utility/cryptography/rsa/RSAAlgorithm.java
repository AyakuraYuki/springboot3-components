package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

/**
 * @author Ayakura Yuki
 * @date 2025/06/05-01:11
 */
public enum RSAAlgorithm {

  RSA("RSA"),
  PKCS1v15("RSA/ECB/PKCS1Padding"),
  ;

  public final String algorithm;

  RSAAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

}
