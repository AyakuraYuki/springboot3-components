package cc.ayakurayuki.spring.components.utility.dataurl;

import java.util.Arrays;

/**
 * Supported encodings to serialize/unserialize {@link DataURL}
 *
 * @author Ayakura Yuki
 */
public enum DataURLEncoding {

  /**
   * Base64 encoded
   */
  Base64("base64"),

  /**
   * URL encoded
   */
  URL("");

  public final String encoding;

  DataURLEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Gets the matching enum constant of the given enconding
   *
   * @param encoding Name of the encoding in a data url
   *
   * @return Matching enum constant
   *
   * @throws IllegalArgumentException if this enum type has no constant with the specified encodingName
   * @throws NullPointerException     if encodingName is null
   */
  public static DataURLEncoding of(String encoding) {
    if (encoding == null) {
      throw new IllegalArgumentException("encoding must not be null");
    }
    return Arrays.stream(values())
        .filter(e -> e.encoding.equals(encoding))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("unsupported encoding: %s".formatted(encoding)));
  }

}
