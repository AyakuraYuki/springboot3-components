package cc.ayakurayuki.spring.components.utility.cryptography;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-17:07
 */
public enum DigestAlgorithm {

  SHA1("SHA-1"),
  SHA256("SHA-256"),
  SHA384("SHA-384"),
  SHA512("SHA-512"),
  MD5("MD5"),
  ;

  public final String algorithm;

  DigestAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

}
