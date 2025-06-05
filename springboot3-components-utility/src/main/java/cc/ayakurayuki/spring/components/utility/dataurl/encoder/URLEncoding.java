package cc.ayakurayuki.spring.components.utility.dataurl.encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Encodes/Decodes a {@link String} RFC 3986 compatible
 *
 * @author Max Schuster
 */
public class URLEncoding {

  /**
   * Encodes a {@link String}
   *
   * @param s   String to encode
   * @param enc Encoding to use
   *
   * @return Encoded {@link String}
   *
   * @throws UnsupportedEncodingException If the named encoding is not supported
   */
  public static String encode(String s, String enc) throws UnsupportedEncodingException {
    return URLEncoder.encode(s, enc).replace("+", "%20");
  }

  /**
   * Decodes a {@link String}
   *
   * @param s   String to decode
   * @param enc Encoding to use
   *
   * @return Decoded {@link String}
   *
   * @throws UnsupportedEncodingException If the named encoding is not supported
   */
  public static String decode(String s, String enc) throws UnsupportedEncodingException {
    return URLDecoder.decode(s.replace("%20", "+"), enc);
  }

}
