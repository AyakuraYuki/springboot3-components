package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

/**
 * @author Ayakura Yuki
 */
public enum CipherPadding {

  NoPadding("NoPadding"),

  PKCS1Padding("PKCS1Padding"),
  PKCS5Padding("PKCS5Padding"),
  PKCS7Padding("PKCS7Padding"),

  SSL3Padding("SSL3Padding"),

  ISO10126Padding("ISO10126Padding"),

  OAEPPadding("OAEPPadding"),
  OAEPWithMD5AndMGF1Padding("OAEPWithMD5AndMGF1Padding"),
  OAEPWithSHA1AndMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),
  OAEPWithSHA256AndMGF1Padding("OAEPWithSHA-256AndMGF1Padding"),
  OAEPWithSHA512AndMGF1Padding("OAEPWithSHA-512AndMGF1Padding"),
  ;

  public final String padding;

  CipherPadding(String padding) {
    this.padding = padding;
  }

}
