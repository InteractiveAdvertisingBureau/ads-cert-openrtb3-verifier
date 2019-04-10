package net.media.adscert.utils;

import net.media.adscert.exceptions.ProcessException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SignatureUtil {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static void main(String[] args) throws Exception {
    KeyPair kp = generateKeyPair();
    saveKeyPair("/home/pranava/Desktop", kp);
//    PrivateKey priv = getPrivateKey("/Users/aditya.ja/Desktop/private.txt");
//    PublicKey pub = getPublicKey("/Users/aditya.ja/Desktop/public.txt");
  }

  /**
   * Generate new KeyPair for ECDSA( prime256v1 )
   */
  public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
    ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("prime256v1");
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
    keyPairGenerator.initialize(ecGenSpec, new SecureRandom());
    return keyPairGenerator.generateKeyPair();
  }

  /**
   * Store key
   */
  public static void saveKeyPair(String path, KeyPair keyPair) throws IOException {
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    // Store Public Key.
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
    byte[] encoded = Base64.encodeBase64(x509EncodedKeySpec.getEncoded());
    FileOutputStream fos = new FileOutputStream(path + "/public.txt");
    fos.write(encoded);
    fos.close();

    // Store Private Key.
    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
    fos = new FileOutputStream(path + "/private.txt");
    fos.write(Base64.encodeBase64(pkcs8EncodedKeySpec.getEncoded()));
    fos.close();
  }

  /**
   * Create a digital signature using private key
   */
  public static String signMessage(PrivateKey priv, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
    Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
    ecdsaSign.initSign(priv);
    ecdsaSign.update(message.getBytes(UTF_8));
    byte[] sign = ecdsaSign.sign();
    return new String(Base64.encodeBase64(sign), UTF_8);
  }

  /**
   * Now that all the data to be signed has been read in, generate a
   * signature for it
   */
  public static boolean verifySign(PublicKey pub, String digest, String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
    Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
    ecdsaVerify.initVerify(pub);
    ecdsaVerify.update(digest.getBytes(UTF_8));
    return ecdsaVerify.verify(Base64.decodeBase64(signature.getBytes(UTF_8)));
  }

  public static PublicKey getPublicKeyFromUrl(String urlName) throws IOException, GeneralSecurityException {
    String publicKeyPEM = Util.getKeyFromUrl(urlName);
    return getPublicKeyFromString(publicKeyPEM);
  }

  public static PrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
    String privateKeyPEM = Util.getKeyFromFile(filename);
    return getPrivateKeyFromString(privateKeyPEM);
  }

  public static PublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
    String publicKeyPEM = Util.getKeyFromFile(filename);
    return getPublicKeyFromString(publicKeyPEM);
  }

  private static PrivateKey getPrivateKeyFromString(String privateKeyPEM) throws GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(privateKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
    KeySpec privKeySpec = new PKCS8EncodedKeySpec(encoded);
    return kf.generatePrivate(privKeySpec);
  }

  private static PublicKey getPublicKeyFromString(String publicKeyPEM) throws GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(publicKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
    KeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
    return kf.generatePublic(pubKeySpec);
  }

}
