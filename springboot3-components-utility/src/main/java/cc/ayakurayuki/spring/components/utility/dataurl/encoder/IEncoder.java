package cc.ayakurayuki.spring.components.utility.dataurl.encoder;

import cc.ayakurayuki.spring.components.utility.dataurl.DataURL;
import java.io.Serializable;

/**
 * An encoder for {@link DataURL}s
 *
 * @author Max Schuster
 */
public interface IEncoder extends Serializable {

  /**
   * Decodes the given {@link String}
   *
   * @param charset Charset
   * @param string  String to decode
   *
   * @return Decoded data
   *
   * @throws Exception If something goes wrong
   */
  byte[] decode(String charset, String string) throws Exception;

  /**
   * Encodes the given byte[] of data
   *
   * @param charset Charset
   * @param data    String to encode
   *
   * @return Encoded String
   *
   * @throws Exception If something goes wrong
   */
  String encode(String charset, byte[] data) throws Exception;

}
