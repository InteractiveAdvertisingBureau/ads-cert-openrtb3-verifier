package net.media.adscert.utils;

import net.media.adscert.exceptions.InvalidDataException;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ECDSAUtil {

  public static void main(String[] args) throws Exception {
//    KeyPair kp = generateKeyPair();
//    saveKeyPair("/Users/aditya.ja/Desktop", kp);
//    PrivateKey priv = getPrivateKey("/Users/aditya.ja/Desktop/private.txt");
//    PublicKey pub = getPublicKey("/Users/aditya.ja/Desktop/public.txt");
  }

  /**
   * Generate new KeyPair for ECDSA( prime256v1 )
   */
  private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    keyGen.initialize(256, random);
    return keyGen.generateKeyPair();
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
  public static String signMessage(PrivateKey priv, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
    Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
    ecdsaSign.initSign(priv);
    ecdsaSign.update(message.getBytes("UTF-8"));
    byte[] sign = ecdsaSign.sign();
    return new String(Base64.encodeBase64(sign), "UTF-8");
  }

  /**
   * Now that all the data to be signed has been read in, generate a
   * signature for it
   */
  public static boolean verifySign(PublicKey pub, String digest, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
    try {
      Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
      ecdsaVerify.initVerify(pub);
      ecdsaVerify.update(digest.getBytes("UTF-8"));
      return ecdsaVerify.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
    } catch (SignatureException e) {
      throw new InvalidDataException("Invalid signature provided");
    } catch (UnsupportedEncodingException e) {
      throw new InvalidDataException("Invalid signature provided");
    }
  }

  public static PublicKey getPublicKeyFromUrl(String urlName) throws IOException, GeneralSecurityException {
    String publicKeyPEM = Util.getKeyFromUrl(urlName);
    return getPublicKeyFromString(publicKeyPEM);
  }

  public static PrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
    String privateKeyPEM = Util.getKeyFromFile(filename);
    return getPrivateKeyFromString(privateKeyPEM);
  }

  private static PrivateKey getPrivateKeyFromString(String privateKeyPEM) throws IOException, GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(privateKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec privKeySpec = new PKCS8EncodedKeySpec(encoded);
    return kf.generatePrivate(privKeySpec);
  }

  private static PublicKey getPublicKeyFromString(String publicKeyPEM) throws IOException, GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(publicKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
    return kf.generatePublic(pubKeySpec);
  }
//
//  public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
//    Cipher cipher = Cipher.getInstance("RSA");
//    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//    return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes("UTF-8")));
//  }
//
//  public static String decrypt(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
//    Cipher cipher = Cipher.getInstance("RSA");
//    cipher.init(Cipher.DECRYPT_MODE, privateKey);
//    return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), "UTF-8");
//  }
}
