package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public final class RSA {

  public static final String ALGORITHM = "RSA";

  public static final String RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  // region encrypt

  /**
   * encrypt data with given algorithm and standard provider
   *
   * @param src       data to encrypt
   * @param publicKey rsa public key
   * @param algorithm rsa algorithm
   *
   * @return encrypted data
   */
  public static byte[] encrypt(byte[] src, RSAPublicKey publicKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return cipher.doFinal(src);
  }

  /**
   * encrypt data with given algorithm and {@link BouncyCastleProvider}
   *
   * @param src       data to encrypt
   * @param publicKey rsa public key
   * @param algorithm rsa algorithm
   *
   * @return encrypted data
   */
  public static byte[] encryptBC(byte[] src, RSAPublicKey publicKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return cipher.doFinal(src);
  }

  /**
   * encrypt large data in chunks with given algorithm and standard provider
   *
   * @param src       data to encrypt
   * @param publicKey rsa public key
   * @param algorithm rsa algorithm
   *
   * @return encrypted data
   */
  public static byte[] encryptChunks(byte[] src, RSAPublicKey publicKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm);
    return encryptChunks(cipher, src, publicKey);
  }

  /**
   * encrypt large data in chunks with given algorithm and {@link BouncyCastleProvider}
   *
   * @param src       data to encrypt
   * @param publicKey rsa public key
   * @param algorithm rsa algorithm
   *
   * @return encrypted data
   */
  public static byte[] encryptChunksBC(byte[] src, RSAPublicKey publicKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    return encryptChunks(cipher, src, publicKey);
  }

  private static byte[] encryptChunks(Cipher cipher, byte[] src, RSAPublicKey publicKey) throws GeneralSecurityException {
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    final int blockSize = publicKey.getModulus().bitLength() / 8 - 11; // PKCS#1 padding overhead is 11
    final int length = src.length;
    byte[] dst = new byte[0];
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      for (int offset = 0; offset < length; offset += blockSize) {
        int size = Math.min(blockSize, length - offset);
        byte[] chunk = new byte[size];
        System.arraycopy(src, offset, chunk, 0, size);
        byte[] encrypted = cipher.doFinal(chunk);
        output.write(encrypted);
      }
      output.flush();
      dst = output.toByteArray();
    } catch (IOException ignored) {
    }
    return dst;
  }

  // endregion

  // region decrypt

  /**
   * decrypt data with given algorithm and standard provider
   *
   * @param src        data to decrypt
   * @param privateKey rsa private key
   * @param algorithm  rsa algorithm
   *
   * @return decrypted data
   */
  public static byte[] decrypt(byte[] src, RSAPrivateKey privateKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(src);
  }

  /**
   * decrypt data with given algorithm and {@link BouncyCastleProvider}
   *
   * @param src        data to decrypt
   * @param privateKey rsa private key
   * @param algorithm  rsa algorithm
   *
   * @return decrypted data
   */
  public static byte[] decryptBC(byte[] src, RSAPrivateKey privateKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(src);
  }

  /**
   * decrypt large data in chunks with given algorithm and standard provider
   *
   * @param src        data to decrypt
   * @param privateKey rsa private key
   * @param algorithm  rsa algorithm
   *
   * @return decrypted data
   */
  public static byte[] decryptChunks(byte[] src, RSAPrivateKey privateKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm);
    return decryptChunks(cipher, src, privateKey);
  }

  /**
   * decrypt large data in chunks with given algorithm and {@link BouncyCastleProvider}
   *
   * @param src        data to decrypt
   * @param privateKey rsa private key
   * @param algorithm  rsa algorithm
   *
   * @return decrypted data
   */
  public static byte[] decryptChunksBC(byte[] src, RSAPrivateKey privateKey, RSAAlgorithm algorithm) throws GeneralSecurityException {
    Objects.requireNonNull(algorithm);
    Cipher cipher = Cipher.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    return decryptChunks(cipher, src, privateKey);
  }

  private static byte[] decryptChunks(Cipher cipher, byte[] src, RSAPrivateKey privateKey) throws GeneralSecurityException {
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    final int blockSize = privateKey.getModulus().bitLength() / 8;
    final int length = src.length;
    byte[] dst = new byte[0];
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      for (int offset = 0; offset < length; offset += blockSize) {
        int size = Math.min(blockSize, length - offset);
        byte[] chunk = new byte[size];
        System.arraycopy(src, offset, chunk, 0, size);
        byte[] decrypted = cipher.doFinal(chunk);
        output.write(decrypted);
      }
      output.flush();
      dst = output.toByteArray();
    } catch (IOException ignored) {
    }
    return dst;
  }

  // endregion

  // region sign / verify

  public static byte[] sign(byte[] src, RSAPrivateKey privateKey, RSASignAlgorithm algorithm) throws GeneralSecurityException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    Signature signature = Signature.getInstance(algorithm.algorithm);
    signature.initSign(privateKey);
    signature.update(src);
    return signature.sign();
  }

  public static byte[] signBC(byte[] src, RSAPrivateKey privateKey, RSASignAlgorithm algorithm) throws GeneralSecurityException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    Signature signature = Signature.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    signature.initSign(privateKey);
    signature.update(src);
    return signature.sign();
  }

  public static boolean verify(byte[] src, RSAPublicKey publicKey, RSASignAlgorithm algorithm, byte[] sign) throws GeneralSecurityException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    Signature signature = Signature.getInstance(algorithm.algorithm);
    signature.initVerify(publicKey);
    signature.update(src);
    return signature.verify(sign);
  }

  public static boolean verifyBC(byte[] src, RSAPublicKey publicKey, RSASignAlgorithm algorithm, byte[] sign) throws GeneralSecurityException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    Signature signature = Signature.getInstance(algorithm.algorithm, BouncyCastleProvider.PROVIDER_NAME);
    signature.initVerify(publicKey);
    signature.update(src);
    return signature.verify(sign);
  }

  // endregion

  // region generate

  public static RSAKeyPair generateKeyPair(int keySize) throws GeneralSecurityException {
    if (keySize < 1024) {
      throw new IllegalArgumentException("illegal key size: %d".formatted(keySize));
    }
    KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
    generator.initialize(keySize);
    KeyPair keyPair = generator.generateKeyPair();
    return RSAKeyPair.fromKeyPair(keyPair);
  }

  public static RSAPublicKey generatePublicKey(PrivateKey privateKey) throws GeneralSecurityException {
    if (privateKey == null) {
      throw new IllegalArgumentException("privateKey cannot be null");
    }
    if (!privateKey.getAlgorithm().equalsIgnoreCase(ALGORITHM)) {
      throw new IllegalArgumentException("private key algorithm not supported: %s".formatted(privateKey.getAlgorithm()));
    }
    if (privateKey instanceof RSAPrivateCrtKey crtKey) {
      RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(crtKey.getModulus(), crtKey.getPublicExponent());
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
      return (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec);
    } else {
      throw new IllegalArgumentException("private key must be a RSAPrivateCrtKey");
    }
  }

  // endregion

  // region parser

  public static RSAPrivateKey fromPKCS1ToPrivateKey(String content) throws GeneralSecurityException, IOException {
    if (content == null) {
      throw new IllegalArgumentException("content cannot be null");
    }
    PemObject pemObject = readPemObject(content);
    assurePemObject(pemObject);
    // PKCS1 -> PKCS8
    AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag);
    ASN1Object asn1Object = ASN1ObjectIdentifier.fromByteArray(pemObject.getContent());
    PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, asn1Object);
    byte[] pkcs8Bytes = privateKeyInfo.getEncoded();
    // recover rsa private key
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8Bytes);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }

  public static RSAPrivateKey fromPKCS1ToPrivateKeyBC(String content) throws IOException {
    if (content == null) {
      throw new IllegalArgumentException("content cannot be null");
    }
    PEMParser parser = new PEMParser(new StringReader(content));
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
    Object object = parser.readObject();
    if (object instanceof PEMKeyPair pemKeyPair) {
      KeyPair keyPair = converter.getKeyPair(pemKeyPair);
      return (RSAPrivateKey) keyPair.getPrivate();
    } else {
      return null;
    }
  }

  public static RSAPrivateKey fromPKCS8ToPrivateKey(String content) throws GeneralSecurityException {
    if (content == null) {
      throw new IllegalArgumentException("content cannot be null");
    }
    PemObject pemObject = readPemObject(content);
    assurePemObject(pemObject);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemObject.getContent());
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
    return (RSAPrivateKey) keyFactory.generatePrivate(spec);
  }

  public static RSAPublicKey toPublicKey(String content) throws GeneralSecurityException {
    if (content == null) {
      throw new IllegalArgumentException("content cannot be null");
    }

    PemObject pemObject = readPemObject(content);
    assurePemObject(pemObject);

    X509EncodedKeySpec spec = new X509EncodedKeySpec(pemObject.getContent());
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  public static RSAPublicKey toPublicKey(byte[] n, byte[] e) throws GeneralSecurityException {
    BigInteger modulus = new BigInteger(1, n);
    BigInteger exponent = new BigInteger(1, e);
    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
    return (RSAPublicKey) keyFactory.generatePublic(spec);
  }

  private static PemObject readPemObject(String content) {
    PemObject pemObject;
    try (PemReader pemReader = new PemReader(new StringReader(content))) {
      pemObject = pemReader.readPemObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return pemObject;
  }

  private static void assurePemObject(PemObject pemObject) {
    if (pemObject == null) {
      throw new RuntimeException("failed to read pem object");
    }
  }

  // endregion

}
