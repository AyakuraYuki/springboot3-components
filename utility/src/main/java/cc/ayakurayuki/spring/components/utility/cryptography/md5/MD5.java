package cc.ayakurayuki.spring.components.utility.cryptography.md5;

import cc.ayakurayuki.spring.components.utility.cryptography.DigestAlgorithm;
import cc.ayakurayuki.spring.components.utility.cryptography.digest.Digests;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

public final class MD5 {

  /**
   * 获取输入流的 MD5 散列信息
   */
  public static byte[] digest(InputStream inputStream) {
    try {
      return Digests.digest(inputStream, DigestAlgorithm.MD5);
    } catch (NoSuchAlgorithmException | IOException e) {
      return null;
    }
  }

  /**
   * 获取输入流的 MD5 散列文本结果
   */
  public static String digestToString(InputStream inputStream) {
    byte[] digest;
    try {
      digest = Digests.digest(inputStream, DigestAlgorithm.MD5);
    } catch (NoSuchAlgorithmException | IOException e) {
      return null;
    }
    if (digest == null) {
      return null;
    }
    return BaseEncoding.base16().encode(digest);
  }

  /**
   * 获取文本内容的 MD5 散列信息
   */
  public static byte[] digest(String plainText) {
    try {
      return Digests.digest(plainText, DigestAlgorithm.MD5);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  /**
   * 获取文本内容的 MD5 散列文本结果
   */
  public static String digestToString(String plainText) {
    try {
      return Digests.digestToHexString(plainText, DigestAlgorithm.MD5);
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

  /**
   * 获取文本内容的 MD5 散列文本结果，可以指定对文本内容使用的编码集
   */
  public static String digestToString(String plainText, Charset charset) {
    try {
      return Digests.digestToHexString(plainText, charset, DigestAlgorithm.MD5);
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

}
