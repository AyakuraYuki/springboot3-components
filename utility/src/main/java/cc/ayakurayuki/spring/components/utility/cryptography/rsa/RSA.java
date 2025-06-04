package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;
import javax.crypto.Cipher;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public final class RSA {

  // RSA 算法
  private static final String ALGORITHM = "RSA";

  // 签名算法
  private static final String SHA1withRSA   = "SHA1withRSA";
  private static final String SHA256withRSA = "SHA256withRSA";
  private static final String SHA512withRSA = "SHA512withRSA";

  // Base64
  private static final Encoder ENCODER     = Base64.getEncoder();
  private static final Decoder DECODER     = Base64.getDecoder();
  private static final Encoder URL_ENCODER = Base64.getUrlEncoder();
  private static final Decoder URL_DECODER = Base64.getUrlDecoder();

  // region Encrypt / Decrypt

  public static String encrypt(String data, RSAPublicKey publicKey) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] raw = data.getBytes(StandardCharsets.UTF_8);
    byte[] encrypted = cipher.doFinal(raw);
    return ENCODER.encodeToString(encrypted);
  }

  public static String decrypt(String source, RSAPrivateKey privateKey) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] raw = DECODER.decode(source);
    byte[] decrypted = cipher.doFinal(raw);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  // endregion

  // region Sign / Verify

  public static String sign(byte[] data, RSAPrivateKey privateKey, String signAlgorith) throws GeneralSecurityException {
    Signature signature = Signature.getInstance(signAlgorith);
    signature.initSign(privateKey);
    signature.update(data);
    return ENCODER.encodeToString(signature.sign());
  }

  public static boolean verify(byte[] data, RSAPublicKey publicKey, String signAlgorith, byte[] sign) throws GeneralSecurityException {
    Signature signature = Signature.getInstance(signAlgorith);
    signature.initVerify(publicKey);
    signature.update(data);
    return signature.verify(sign);
  }

  // endregion

  // region Generating

  public static RSAKeyPair generateKey() throws NoSuchAlgorithmException {
    return generateKey(2048);
  }

  public static RSAKeyPair generateKey(int keySize) throws NoSuchAlgorithmException {
    if (keySize != 1024 && keySize != 2048 && keySize != 4096) {
      throw new IllegalArgumentException("illegal key size: %d".formatted(keySize));
    }
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
    keyPairGenerator.initialize(keySize);
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

  // endregion

  // region Parser

  /**
   * Parse private key content into {@link RSAPrivateKey}
   */
  public static RSAPrivateKey toPrivateKey(String content) throws GeneralSecurityException, IOException {
    Objects.requireNonNull(content);

    PemReader reader = new PemReader(new StringReader(content));
    PemObject pemObject = reader.readPemObject();
    Objects.requireNonNull(pemObject);

    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemObject.getContent());
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }

  /**
   * Parse public key content into {@link RSAPublicKey}
   */
  public static RSAPublicKey toPublicKey(String content) throws GeneralSecurityException, IOException {
    Objects.requireNonNull(content);

    PemReader reader = new PemReader(new StringReader(content));
    PemObject pemObject = reader.readPemObject();
    Objects.requireNonNull(pemObject);

    X509EncodedKeySpec spec = new X509EncodedKeySpec(pemObject.getContent());
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  /**
   * Get RSA public key by modulus and public exponent
   * <p>
   * RSA/None/PKCS1Padding in standard JDK, RSA/None/NoPadding in Android.
   *
   * @param n modulus bytes
   * @param e public exponent bytes
   */
  public static RSAPublicKey toPublicKey(byte[] n, byte[] e) throws GeneralSecurityException {
    BigInteger modulus = new BigInteger(1, n);
    BigInteger exponent = new BigInteger(1, e);
    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  // endregion

}
