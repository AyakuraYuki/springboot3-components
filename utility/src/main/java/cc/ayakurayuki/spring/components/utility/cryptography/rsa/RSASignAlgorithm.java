package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

public enum RSASignAlgorithm {

  @Deprecated SHA1withRSA("SHA1withRSA"), // unsafe

  SHA256withRSA("SHA256withRSA"),

  SHA512withRSA("SHA512withRSA"),
  ;

  public final String algorithm;

  RSASignAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

}
