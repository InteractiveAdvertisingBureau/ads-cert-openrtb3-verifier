/*
 * Copyright © 2019 - present. MEDIA NET SOFTWARE SERVICES PVT. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.media.adscert.utils;

import net.media.adscert.exceptions.ProcessException;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SignatureUtil {

  public static void main(String[] args) throws Exception {
//    KeyPair kp = generateKeyPair();
//    saveKeyPair("/Users/aditya.ja/Desktop", kp);
//    PrivateKey priv = getPrivateKey("/Users/aditya.ja/Desktop/private.txt");
//    PublicKey pub = getPublicKey("/Users/aditya.ja/Desktop/public.txt");
  }

  /**
   * Generate new KeyPair for ECDSA( prime256v1 )
   */
  public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
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
  public static String signMessage(PrivateKey priv, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
    ecdsaSign.initSign(priv);
    ecdsaSign.update(message.getBytes(UTF_8));
    byte[] sign = ecdsaSign.sign();
    return new String(Base64.encodeBase64(sign), UTF_8);
  }

  /**
   * Now that all the data to be signed has been read in, generate a
   * signature for it
   */
  public static boolean verifySign(PublicKey pub, String digest, String signature)
    throws ProcessException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
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

  private static Key getKey(String keyPEM, Function<byte[], KeySpec> f1, Function<KeySpec, Key> f2) throws NoSuchAlgorithmException {
    byte[] encoded = Base64.decodeBase64(keyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec keySpec = f1.apply(encoded);
    return f2.apply(keySpec);
  }

  private static PrivateKey getPrivateKeyFromString(String privateKeyPEM) throws GeneralSecurityException {
//    KeyFactory kf = KeyFactory.getInstance("EC");
//    return (PrivateKey) getKey(privateKeyPEM, PKCS8EncodedKeySpec::new, KeyFactory::generatePrivate);
    byte[] encoded = Base64.decodeBase64(privateKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec privKeySpec = new PKCS8EncodedKeySpec(encoded);
    return kf.generatePrivate(privKeySpec);
//    return null;
  }

  private static PublicKey getPublicKeyFromString(String publicKeyPEM) throws GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(publicKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
    return kf.generatePublic(pubKeySpec);
  }

}
