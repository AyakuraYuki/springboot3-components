package cc.ayakurayuki.spring.components.utility.dataurl;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ayakura Yuki
 */
public final class DataURL implements Serializable {

  @Serial
  private static final long serialVersionUID = -9110037528251741033L;

  /**
   * data payload
   */
  private final byte[] data;

  /**
   * encoding method
   */
  private final DataURLEncoding encoding;

  /**
   * MIME-Type of this data url payload
   */
  private final String mimeType;

  /**
   * headers/parameters of this data url
   */
  private final Map<String, String> headers;

  /**
   * Constructs a new DataUrl with URL encoding and without headers/parameters and MIME-Type
   *
   * @param data Payload of this data url. Must not be {@code null}
   *
   * @throws NullPointerException if data is {@code null}
   */
  public DataURL(byte[] data) {
    this(data, DataURLEncoding.URL);
  }

  /**
   * Constructs a new DataUrl without headers/parameters and MIME-Type
   *
   * @param data     Payload of this data url. Must not be {@code null}
   * @param encoding Encoding method. Must not be {@code null}
   *
   * @throws NullPointerException if data or encoding is {@code null}
   */
  public DataURL(byte[] data, DataURLEncoding encoding) {
    this(data, encoding, null);
  }

  /**
   * Constructs a new DataUrl without headers/parameters
   *
   * @param data     Payload of this data url. Must not be {@code null}
   * @param encoding Encoding method. Must not be {@code null}
   * @param mimeType MIME-Type of this data urls content
   *
   * @throws NullPointerException if data or encoding is {@code null}
   */
  public DataURL(byte[] data, DataURLEncoding encoding, String mimeType) {
    this(data, encoding, mimeType, null);
  }

  /**
   * Constructs a new DataUrl
   *
   * @param data     Payload of this data url. Must not be {@code null}
   * @param encoding Encoding method. Must not be {@code null}
   * @param mimeType MIME-Type of this data urls content
   * @param headers  Headers/parameters of this data url
   *
   * @throws NullPointerException if data or encoding is {@code null}
   */
  public DataURL(byte[] data, DataURLEncoding encoding, String mimeType, Map<String, String> headers) {
    Objects.requireNonNull(data, "data must not be null");
    Objects.requireNonNull(encoding, "encoding must not be null");

    this.data = data;
    this.encoding = encoding;
    this.mimeType = mimeType;
    if (headers != null) {
      this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    } else {
      this.headers = Collections.emptyMap();
    }
  }

  /**
   * Gets the payload of this data url
   *
   * @return Payload of this data url
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Gets the MIME-Type of this data urls content.
   *
   * @return MIME-Type of this data urls content or {@code null}.
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Gets the encoding method
   *
   * @return Encoding method
   */
  public DataURLEncoding getEncoding() {
    return encoding;
  }

  /**
   * Gets the headers/parameters of this data url
   *
   * @return Headers/parameters of this data url or {@code null}
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 97 * hash + Arrays.hashCode(this.data);
    hash = 97 * hash + (this.mimeType != null ? this.mimeType.hashCode() : 0);
    hash = 97 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
    hash = 97 * hash + (this.headers != null ? this.headers.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    if (this == o) {
      return true;
    }
    DataURL dataURL = (DataURL) o;
    return Arrays.equals(data, dataURL.data)
        && encoding == dataURL.encoding
        && Objects.equals(mimeType, dataURL.mimeType)
        && Objects.equals(headers, dataURL.headers);
  }

  @Override
  public String toString() {
    return "DataUrl{ " + "mimeType = \"" + mimeType + "\", " + "encoding = \"" + encoding + "\", " + "headers = \"" + headers + "\", " + "data.length = \"" + data.length + " bytes\" }";
  }

}
