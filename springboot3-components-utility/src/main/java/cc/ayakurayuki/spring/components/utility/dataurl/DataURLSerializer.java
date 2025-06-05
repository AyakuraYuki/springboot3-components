package cc.ayakurayuki.spring.components.utility.dataurl;

import cc.ayakurayuki.spring.components.utility.dataurl.encoder.Base64Encoder;
import cc.ayakurayuki.spring.components.utility.dataurl.encoder.IEncoder;
import cc.ayakurayuki.spring.components.utility.dataurl.encoder.URLEncodedEncoder;
import cc.ayakurayuki.spring.components.utility.dataurl.encoder.URLEncoding;
import java.io.Serial;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ayakura Yuki
 */
public class DataURLSerializer implements IDataURLSerializer {

  @Serial
  private static final long serialVersionUID = 69130932460901238L;

  /**
   * Pattern used to split header fields;
   */
  private final Pattern PATTERN_META_SPLIT = Pattern.compile(";");

  /**
   * Pattern used to check MIME-Types
   */
  private static final Pattern PATTERN_MIMETYPE = Pattern.compile("^[a-z\\-0-9]+\\/[a-z\\-\\.\\+0-9]+$");

  /**
   * IEncoder for {@link DataURLEncoding#Base64} encoded {@link DataURL}s
   */
  private final Base64Encoder base64Encoder = new Base64Encoder();

  /**
   * IEncoder for {@link DataURLEncoding#URL} encoded {@link DataURL}s
   */
  private final URLEncodedEncoder urlEncodedEncoder = new URLEncodedEncoder();

  @Override
  public String serialize(DataURL dataURL) throws MalformedURLException {
    DataURLEncoding encoding = dataURL.getEncoding();
    IEncoder encoder = getAppliedEncoder(encoding);
    Map<String, String> headers = dataURL.getHeaders();
    int headerSize = headers != null ? headers.size() : 0;
    StringBuilder sb = new StringBuilder("data:");
    String mimeType = dataURL.getMimeType();

    if (mimeType != null) {
      sb.append(mimeType);
      if (headerSize > 0 || encoding != DataURLEncoding.URL) {
        sb.append(';');
      }
    }

    if (headers != null && headerSize > 0) {
      int i = 0;
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        String value;
        try {
          value = URLEncoding.encode(entry.getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
          throw new AssertionError();
        }
        sb.append(entry.getKey()).append('=').append(value);
        i++;
        if (i < headerSize || encoding != DataURLEncoding.URL) {
          sb.append(';');
        }
      }
    }

    String encodingName = encoding.encoding;

    if (!encodingName.isEmpty()) {
      sb.append(encodingName);
    }

    sb.append(',');

    String appliedCharset = getAppliedCharset(headers);

    try {
      sb.append(encoder.encode(appliedCharset, dataURL.getData()));
    } catch (Exception e) {
      throw new MalformedURLException("Error encoding the data");
    }

    return sb.toString();
  }

  @Override
  public DataURL deserialize(String urlString) throws MalformedURLException {
    if (urlString == null) {
      throw new NullPointerException();
    }

    byte[] data;
    String mimeType = null;
    HashMap<String, String> headers = new HashMap<>();

    if (!urlString.startsWith("data:")) {
      throw new MalformedURLException("Wrong protocol");
    }

    int colon = urlString.indexOf(':');
    int comma = urlString.indexOf(',');

    String metaString = urlString.substring(colon + 1, comma);
    String dataString = urlString.substring(comma + 1);
    String encodingName = "";

    String[] metaArray = PATTERN_META_SPLIT.split(metaString);
    for (int i = 0; i < metaArray.length; i++) {
      String meta = metaArray[i];
      if (i == 0) {
        Matcher m = PATTERN_MIMETYPE.matcher(meta);
        if (m.matches()) {
          mimeType = meta;
          continue;
        }
      }

      if (i + 1 == metaArray.length) {
        if (meta.indexOf('=') == -1) {
          encodingName = meta;
          continue;
        }
      }

      int equals = meta.indexOf('=');
      if (equals < 1) {
        throw new MalformedURLException();
      }

      String name = meta.substring(0, equals);
      String value = meta.substring(equals + 1);

      try {
        headers.put(name, URLEncoding.decode(value, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        throw new AssertionError(e);
      }
    }

    DataURLEncoding encoding;
    try {
      encoding = DataURLEncoding.of(encodingName);
    } catch (IllegalArgumentException e) {
      throw new MalformedURLException("Unknown encoding \"" + encodingName + "\"");
    }
    IEncoder encoder = getAppliedEncoder(encoding);
    String appliedCharset = getAppliedCharset(headers);

    try {
      data = encoder.decode(appliedCharset, dataString);
    } catch (Exception e) {
      throw new MalformedURLException("");
    }

    return new DataURL(data, encoding, mimeType, headers);
  }

  /**
   * Gets the charset that should be used to encode the {@link DataURL}
   *
   * @param headers Headers map
   *
   * @return Applied charset, never {@code null}
   */
  protected String getAppliedCharset(Map<String, String> headers) {
    String encoding;
    if (headers != null && (encoding = headers.get("charset")) != null) {
      return encoding;
    }
    return "US-ASCII";
  }

  /**
   * Get the matching encoder for the given encoding
   *
   * @param encoding Encoding
   *
   * @return Matching encoder
   */
  protected IEncoder getAppliedEncoder(DataURLEncoding encoding) {
    if (encoding == null) {
      throw new IllegalArgumentException();
    }
    return switch (encoding) {
      case Base64 -> base64Encoder;
      case URL -> urlEncodedEncoder;
    };
  }

}
