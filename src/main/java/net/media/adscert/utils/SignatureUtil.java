/*
 * Copyright  2019 - present. IAB Tech Lab
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

import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SignatureUtil {
  /** Store key */
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

  /** Create a digital signature using private key */
  public static String signMessage(PrivateKey priv, String message)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
    Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
    ecdsaSign.initSign(priv);
    ecdsaSign.update(message.getBytes(UTF_8));
    byte[] sign = ecdsaSign.sign();
    return new String(Base64.encodeBase64(sign), UTF_8);
  }

  /** Now that all the data to be signed has been read in, generate a signature for it
   */
  public static boolean verifySign(PublicKey pub, String digest, String signature)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
    Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
    ecdsaVerify.initVerify(pub);
    ecdsaVerify.update(digest.getBytes(UTF_8));
    return ecdsaVerify.verify(Base64.decodeBase64(signature.getBytes(UTF_8)));
  }

  public static PublicKey getPublicKeyFromUrl(String urlName)
      throws IOException, GeneralSecurityException {
    String publicKeyPEM = Util.getKeyFromUrl(urlName);
    return getPublicKeyFromString(publicKeyPEM);
  }

  public static PrivateKey getPrivateKey(String filename)
      throws IOException, GeneralSecurityException {
    String privateKeyPEM = Util.getKeyFromFile(filename);
    return getPrivateKeyFromString(privateKeyPEM);
  }

  public static PublicKey getPublicKey(String filename)
      throws IOException, GeneralSecurityException {
    String publicKeyPEM = Util.getKeyFromFile(filename);
    return getPublicKeyFromString(publicKeyPEM);
  }

  private static PrivateKey getPrivateKeyFromString(String privateKeyPEM)
      throws GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(privateKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec privKeySpec = new PKCS8EncodedKeySpec(encoded);
    return kf.generatePrivate(privKeySpec);
  }

  private static PublicKey getPublicKeyFromString(String publicKeyPEM)
      throws GeneralSecurityException {
    byte[] encoded = Base64.decodeBase64(publicKeyPEM);

    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
    return kf.generatePublic(pubKeySpec);
  }
}
