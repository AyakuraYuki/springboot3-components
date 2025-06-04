package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.lang3.StringUtils;

public final class RSA {

  // RSA 算法
  private static final String ALGORITHM = "RSA";
  private static final int    KEY_SIZE  = 2048;

  // 签名算法
  private static final String SIGN_ALGORITHM = "SHA1withRSA";

  // Base64
  private static final Encoder ENCODER     = Base64.getEncoder();
  private static final Decoder DECODER     = Base64.getDecoder();
  private static final Encoder URL_ENCODER = Base64.getUrlEncoder();
  private static final Decoder URL_DECODER = Base64.getUrlDecoder();

  public static RSAKeyPair generateKey() throws NoSuchAlgorithmException {
    return generateKey(KEY_SIZE);
  }

  public static RSAKeyPair generateKey(int keySize) throws NoSuchAlgorithmException {
    if (keySize != 1024 && keySize != 2048 && keySize != 4096) {
      throw new IllegalArgumentException("illegal key size: %d".formatted(keySize));
    }
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
    keyPairGenerator.initialize(KEY_SIZE);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    return new RSAKeyPair((RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
  }

  /**
   * Generate a new public key by given private key, which must be a {@link RSAPrivateCrtKey}.
   */
  public static RSAPublicKey generatePublicKey(RSAPrivateKey privateKey) throws GeneralSecurityException {
    if (privateKey == null) {
      throw new IllegalArgumentException("private key is required");
    }
    if (!privateKey.getAlgorithm().equals(ALGORITHM)) {
      throw new IllegalArgumentException("private key algorithm not supported: %s".formatted(privateKey.getAlgorithm()));
    }

    if (privateKey instanceof RSAPrivateCrtKey crtKey) {
      RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(crtKey.getModulus(), crtKey.getPublicExponent());
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
      return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
    } else {
      throw new IllegalArgumentException("private key must be a RSAPrivateCrtKey");
    }
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Parse private key content into {@link RSAPrivateKey}
   */
  public static RSAPrivateKey parsePrivateKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (StringUtils.isEmpty(content)) {
      throw new IllegalArgumentException("Missing key content.");
    }

    if (content.contains("-----BEGIN PRIVATE KEY-----")) {
      content = content
          .replace("-----BEGIN PRIVATE KEY-----", "")
          .replace("-----END PRIVATE KEY-----", "")
          .replace("\n", "")
          .replace(" ", "")
          .replace("\r", "")
          .replace("\t", "")
          .trim();
    } else if (content.contains("-----BEGIN RSA PRIVATE KEY-----")) {
      content = content
          .replace("-----BEGIN RSA PRIVATE KEY-----", "")
          .replace("-----END RSA PRIVATE KEY-----", "")
          .replace("\n", "")
          .replace(" ", "")
          .replace("\r", "")
          .replace("\t", "")
          .trim();
    }

    byte[] decoded = DECODER.decode(content);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }

  /**
   * Parse public key content into {@link RSAPublicKey}
   */
  public static RSAPublicKey parsePublicKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CertificateException {
    if (StringUtils.isEmpty(content)) {
      throw new IllegalArgumentException("Missing key content.");
    }

    if (content.contains("-----BEGIN PUBLIC KEY-----")) {
      content = content
          .replace("-----BEGIN PUBLIC KEY-----", "")
          .replace("-----END PUBLIC KEY-----", "")
          .replace("\n", "")
          .replace(" ", "")
          .replace("\r", "")
          .replace("\t", "")
          .trim();
    } else if (content.contains("-----BEGIN RSA PUBLIC KEY-----")) {
      content = content
          .replace("-----BEGIN RSA PUBLIC KEY-----", "")
          .replace("-----END RSA PUBLIC KEY-----", "")
          .replace("\n", "")
          .replace(" ", "")
          .replace("\r", "")
          .replace("\t", "")
          .trim();
    } else if (content.contains("-----BEGIN CERTIFICATE-----")) {
      CertificateFactory factory = CertificateFactory.getInstance("X.509");
      try (InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(inputStream);
        return (RSAPublicKey) certificate.getPublicKey();
      }
    }

    byte[] decoded = DECODER.decode(content);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  /**
   * 从密钥对获取公钥的模数指数元组
   *
   * @param keyPair 密钥对
   *
   * @return {@link RSARawKey}
   */
  public static RSARawKey toRSARawKey(RSAKeyPair keyPair) {
    return toRSARawKey(keyPair.publicKey());
  }

  /**
   * 从公钥获取模数指数元组
   *
   * @param publicKey 密钥对
   *
   * @return {@link RSARawKey}
   */
  public static RSARawKey toRSARawKey(RSAPublicKey publicKey) {
    byte[] modulus = publicKey.getModulus().toByteArray();
    byte[] exponent = publicKey.getPublicExponent().toByteArray();
    return new RSARawKey(ENCODER.encodeToString(modulus), ENCODER.encodeToString(exponent));
  }

  /**
   * 使用模和指数生成RSA公钥
   *
   * <p>
   * 注意：此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding
   * </p>
   *
   * @param n 模
   * @param e 指数
   */
  public static RSAPublicKey getPublicKey(String n, String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
    BigInteger modulus = new BigInteger(1, DECODER.decode(n));
    BigInteger exponent = new BigInteger(1, DECODER.decode(e));
    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  /**
   * 使用模和指数生成RSA公钥
   *
   * <p>
   * 注意：此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding
   * </p>
   *
   * @param n 模（使用了Base64UrlEncoder处理过的内容）
   * @param e 指数（使用了Base64UrlEncoder处理过的内容）
   */
  public static RSAPublicKey getPublicKeyWithUrlEncoded(String n, String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
    BigInteger modulus = new BigInteger(1, URL_DECODER.decode(n));
    BigInteger exponent = new BigInteger(1, URL_DECODER.decode(e));
    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  // /**
  //  * 使用模和指数生成RSA私钥
  //  *
  //  * <p>
  //  * 注意：此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding
  //  * </p>
  //  *
  //  * @param n 模
  //  * @param e 指数
  //  */
  // public static RSAPrivateKey getPrivateKey(String n, String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
  //   BigInteger modulus = new BigInteger(1, DECODER.decode(n));
  //   BigInteger exponent = new BigInteger(1, DECODER.decode(e));
  //   RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
  //   KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
  //   return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
  // }

  /**
   * RSA 公钥加密
   */
  public static String encrypt(String data, RSAPublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] raw = data.getBytes(StandardCharsets.UTF_8);
    byte[] encrypted = cipher.doFinal(raw);
    return ENCODER.encodeToString(encrypted);
  }

  /**
   * RSA 私钥解密
   */
  public static String decrypt(String source, RSAPrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] raw = DECODER.decode(source);
    byte[] decrypted = cipher.doFinal(raw);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  /**
   * RSA 私钥签名
   */
  public static String sign(String data, RSAPrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature signature = Signature.getInstance(SIGN_ALGORITHM);
    signature.initSign(privateKey);
    signature.update(data.getBytes(StandardCharsets.UTF_8));
    return ENCODER.encodeToString(signature.sign());
  }

  /**
   * RSA 公钥验证
   *
   * @param data       原始数据
   * @param signString 签名
   */
  public static boolean verify(String data, String signString, RSAPublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    byte[] sign = DECODER.decode(signString);
    Signature signature = Signature.getInstance(SIGN_ALGORITHM);
    signature.initVerify(publicKey);
    signature.update(data.getBytes(StandardCharsets.UTF_8));
    return signature.verify(sign);
  }

}
