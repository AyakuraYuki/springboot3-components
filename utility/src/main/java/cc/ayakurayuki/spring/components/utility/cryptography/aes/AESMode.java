package cc.ayakurayuki.spring.components.utility.cryptography.aes;

import java.util.Arrays;

public enum AESMode {

  ECB("ECB", "Electronic CodeBook"),
  CBC("CBC", "Cipher Block Chaining"),
  CFB("CFB", "Cipher FeedBack"),
  OFB("OFB", "Output FeedBack"),
  CTR("CTR", "Counter"),
  GCM("GCM", "Galois/Counter Mode");

  public final String mode;
  public final String description;

  AESMode(String mode, String description) {
    this.mode = mode;
    this.description = description;
  }

  public static AESMode of(String mode, AESMode defaultMode) {
    return Arrays.stream(values())
        .filter(e -> e.mode.equalsIgnoreCase(mode))
        .findAny()
        .orElse(defaultMode);
  }

  public static AESMode of(String mode) {
    return of(mode, null);
  }

}
