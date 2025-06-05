package cc.ayakurayuki.spring.components.utility.cryptography.hmac;

/**
 * @author Ayakura Yuki
 */
public enum HmacAlgorithm {

  HmacMD5("HmacMD5"),

  HmacSHA1("HmacSHA1"),
  HmacSHA224("HmacSHA224"),
  HmacSHA256("HmacSHA256"),
  HmacSHA384("HmacSHA384"),
  HmacSHA512("HmacSHA512"),
  ;

  public final String algorithm;

  HmacAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

}
