package cc.ayakurayuki.spring.components.utility.cryptography.symmetric;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Ayakura Yuki
 */
abstract class Assembler {

  static String transformation(CipherName algorithm, CipherMode mode, CipherPadding padding) {
    if (algorithm == null) {
      return "";
    }
    if (mode == null || padding == null) {
      return algorithm.algorithm;
    }
    return "%s/%s/%s".formatted(algorithm.algorithm, mode.mode, padding.padding);
  }

  static String transformation(String algorithm, String mode, String padding) {
    if (algorithm == null || algorithm.isBlank()) {
      return "";
    }
    if (isBlank(mode) || isBlank(padding)) {
      return algorithm;
    }
    return "%s/%s/%s".formatted(algorithm, mode, padding);
  }

}
