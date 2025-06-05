package cc.ayakurayuki.spring.components.utility.dataurl;

import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * @author Ayakura Yuki
 */
public interface IDataURLSerializer extends Serializable {

  /**
   * Serialize the given {@link DataURL} to an RFC 2397 data url {@link String}.
   *
   * @param dataURL {@link DataURL} to serialize.
   *
   * @return The serialized RFC 2397 data url {@link String}.
   *
   * @throws MalformedURLException If the given {@link DataURL} can't be serialized.
   */
  String serialize(DataURL dataURL) throws MalformedURLException;

  /**
   * Unserialize the given RFC 2397 data url {@link String} to a {@link DataURL}.
   *
   * @param urlString RFC 2397 data url {@link String} to unserialize.
   *
   * @return The unserialized {@link DataURL}
   *
   * @throws MalformedURLException If the given RFC 2397 data url {@link String} can't be unserialized.
   */
  DataURL deserialize(String urlString) throws MalformedURLException;

}
