package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

/**
 * @author Ayakura Yuki
 */
public enum CipherMode {

  NONE("NONE"), // No mode
  ECB("ECB"), // Electronic Codebook Mode, as defined in FIPS PUB 81 (generally this mode should not be used for multiple blocks of data).
  CBC("CBC"), // Cipher Block Chaining Mode, as defined in FIPS PUB 81.
  CFB("CFB"), // Cipher Feedback Mode, as defined in FIPS PUB 81.
  OFB("OFB"), // Output Feedback Mode, as defined in FIPS PUB 81.
  CTR("CTR"), // A simplification of OFB, Counter mode updates the input block as a counter.
  GCM("GCM"), // Galois/Counter Mode, as defined in NIST Special Publication SP 800-38D Recommendation for Block Cipher Modes of Operation: Galois/Counter Mode (GCM) and GMAC.
  ;

  public final String mode;

  CipherMode(String mode) {
    this.mode = mode;
  }

}
