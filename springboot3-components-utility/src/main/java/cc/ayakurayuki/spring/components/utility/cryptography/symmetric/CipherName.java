package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

/**
 * @author Ayakura Yuki
 */
public enum CipherName {

  AES("AES"),
  AES_128("AES_128"),
  AES_192("AES_192"),
  AES_256("AES_256"),

  AESWrap("AESWrap"),
  AESWrap_128("AESWrap_128"),
  AESWrap_192("AESWrap_192"),
  AESWrap_256("AESWrap_256"),

  DES("DES"),
  DESede("DESede"), // Triple DES (a.k.a. 3DES, DES-EDE or Triple-DES)
  DESedeWrap("DESedeWrap"),
  ;

  public final String algorithm;

  CipherName(String algorithm) {
    this.algorithm = algorithm;
  }

}
