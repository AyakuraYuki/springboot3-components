package cc.ayakurayuki.spring.components.utility.dataurl;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ayakura Yuki
 */
public class DataURLBuilder implements Serializable {

  @Serial
  private static final long serialVersionUID = 3240243201358665936L;

  /**
   * data payload
   */
  private byte[] data;

  /**
   * encoding method
   */
  private DataURLEncoding encoding;

  /**
   * MIME-Type of this data url payload
   */
  private String mimeType;

  /**
   * headers/parameters of this data url
   */
  private Map<String, String> headers;

  /**
   * Creates a new {@link DataURL} instance
   *
   * @return New {@link DataURL} instance
   *
   * @throws NullPointerException if data or encoding is {@code null}
   */
  public DataURL build() {
    Objects.requireNonNull(data, "data is null");
    Objects.requireNonNull(encoding, "encoding is null");
    return new DataURL(data, encoding, mimeType, headers);
  }

  public byte[] getData() {
    return data;
  }

  public DataURLBuilder setData(byte[] data) {
    this.data = data;
    return this;
  }

  public DataURLEncoding getEncoding() {
    return encoding;
  }

  public DataURLBuilder setEncoding(DataURLEncoding encoding) {
    this.encoding = encoding;
    return this;
  }

  public String getMimeType() {
    return mimeType;
  }

  public DataURLBuilder setMimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * Overwrite the existed headers/parameters
   *
   * @param headers headers/parameters to overwrite
   */
  public DataURLBuilder setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  /**
   * Shorthand to set a header/parameter.
   *
   * @param name  name of the header/parameter, if the name exists, it will be overwritten
   * @param value value of the header/parameter
   */
  public DataURLBuilder setHeader(String name, String value) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, value);
    return this;
  }

  /**
   * Append headers/parameters
   *
   * @param headers headers/parameters to append
   */
  public DataURLBuilder addHeaders(Map<String, String> headers) {
    if (this.headers == null) {
      this.headers = headers;
      return this;
    }
    this.headers.putAll(headers);
    return this;
  }

  /**
   * Shorthand to set the charset header
   *
   * @param charset Charset name
   */
  public DataURLBuilder setCharset(String charset) {
    setHeader("charset", charset);
    return this;
  }

  /**
   * Fills this {@link DataURLBuilder} with the contents of the given {@link DataURL} template
   *
   * @param template {@link DataURL} template
   */
  public DataURLBuilder fromDataURL(DataURL template) {
    setData(template.getData());
    setEncoding(template.getEncoding());
    setHeaders(template.getHeaders());
    setMimeType(template.getMimeType());
    return this;
  }

}
