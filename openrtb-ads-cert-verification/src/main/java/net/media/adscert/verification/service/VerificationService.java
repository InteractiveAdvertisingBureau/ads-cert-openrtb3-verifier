/*
 * Copyright © 2019 - present. MEDIA.NET ADVERTISING FZ-LLC.
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

package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.exceptions.VerificationServiceException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.enums.Result;
import net.media.adscert.verification.metrics.BlackholeMetricsManager;
import net.media.adscert.verification.metrics.MetricsManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.media.adscert.verification.enums.Result.Status.*;

/**
 * A {@link VerificationService} provides means to verify digital signature.
 *
 * <p>In addition, a debug flag can be used to decide whether the provided digest should be used or
 * not.
 *
 * @author anupam.v
 * @author pranav.a
 * @since 1.0
 */
public class VerificationService {

  private static final Supplier<Result> FAILURE_RESULT =
      () -> new Result(FAILURE, "Processing Failure");
  private static final Function<Exception, Result> FAILURE_WITH_EXCEPTION_RESULT =
      e -> new Result(FAILURE, "Processing Failure", e);
  private static final Supplier<Result> SUCCESS_RESULT = () -> new Result(SUCCESS, null);
  private static final Supplier<Result> SAMPLED_RESULT = () -> new Result(SAMPLED, null);
  protected int samplingPercentage = 100;
  protected long messageExpiryTimeInMillis = 1000l;
  protected MetricsManager metricsManager = new BlackholeMetricsManager();

  public VerificationService() {}

  public VerificationService(MetricsManager metricsManager) {
    this.metricsManager = metricsManager;
  }

  public VerificationService(int samplingPercentage) {
    this(samplingPercentage, 1000l);
  }

  public VerificationService(
      int samplingPercentage, long messageExpiryTimeInMillis, MetricsManager metricsManager) {
    this(samplingPercentage, messageExpiryTimeInMillis);
    this.metricsManager = metricsManager;
  }

  public VerificationService(int samplingPercentage, long messageExpiryTimeInMillis) {
    if (samplingPercentage > 100 || samplingPercentage < 1) {
      throw new VerificationServiceException(
          "Sampling rate should be between 1 (inclusive) and 100 (inclusive)");
    }
    this.samplingPercentage = samplingPercentage;

    if (messageExpiryTimeInMillis < 0) {
      throw new VerificationServiceException(
          "Message Expiry Time (In Millis) should be greater than 0");
    }

    this.messageExpiryTimeInMillis = messageExpiryTimeInMillis;
  }

  public boolean toConsider() {
    return ThreadLocalRandom.current().nextInt(1, 101) <= samplingPercentage;
  }

  public PublicKey getPublicKey(String url) throws IOException, GeneralSecurityException {
    return SignatureUtil.getPublicKeyFromUrl(url);
  }

  /**
   * Verifies the digital signature using public key url and digest fields.
   *
   * @param publicKeyURL url of the public key of the signing authority
   * @param ds digital signature in the request
   * @param digest a string with concatenated field and value pairs (f1=v1&f2=v2)
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  private Result verifyRequest(String publicKeyURL, String ds, String digest)
      throws InvalidDataException, ProcessException {
    if (ds == null || ds.length() == 0) {
      return new Result(FAILURE, "Digital Signature is empty");
    }
    if (digest == null || digest.length() == 0) {
      return new Result(FAILURE, "Digest is empty");
    }
    try {
      if (publicKeyURL == null || publicKeyURL.isEmpty()) {
        return new Result(FAILURE, "Filename of certificate is empty");
      }
      PublicKey publicKey = getPublicKey(publicKeyURL);
      final boolean status = SignatureUtil.verifySign(publicKey, digest, ds);
      if (status) {
        return SUCCESS_RESULT.get();
      }
      return FAILURE_RESULT.get();
    } catch (Exception e) {
      return FAILURE_WITH_EXCEPTION_RESULT.apply(e);
    }
  }

  /**
   * Verifies the digital signature using public key url and digest fields.
   *
   * @param publicKeyURL full url of the public key of the signing authority
   * @param dsMap the fields that were used for signing the request
   * @param ds digital signature in the request
   * @param digestFieldMap map of fields that were used for generating the signature and their
   *     values
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(
      String publicKeyURL, String dsMap, String ds, Map<String, Object> digestFieldMap)
      throws InvalidDataException, ProcessException {
    Result result = FAILURE_RESULT.get();
    if (digestFieldMap == null || digestFieldMap.size() == 0) {
      return new Result(FAILURE, "digestFieldMap is empty");
    }
    try {
      if (publicKeyURL == null || publicKeyURL.isEmpty()) {
        return new Result(FAILURE, "Filename of certificate is empty");
      }
      if (!toConsider()) {
        result = SAMPLED_RESULT.get();
        return result;
      }
      String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFieldMap);
      result = verifyRequest(publicKeyURL, ds, digest);
      return result;
    } catch (Exception e) {
      result = FAILURE_WITH_EXCEPTION_RESULT.apply(e);
      return result;
    } finally {
      if (digestFieldMap != null && result != null) {
        metricsManager.pushMetrics(digestFieldMap, result);
      }
    }
  }

  /**
   * Verifies the digital signature using {@link PublicKey} and digest fields.
   *
   * @param publicKey {@link PublicKey} of the signing authority
   * @param dsMap the fields that were used for signing the request
   * @param ds digital signature in the request
   * @param digestFieldMap map of fields that were used for generating the signature and their
   *     values
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(
      PublicKey publicKey, String dsMap, String ds, Map<String, Object> digestFieldMap)
      throws InvalidDataException, ProcessException {
    Result result = FAILURE_RESULT.get();

    if (dsMap == null || dsMap.isEmpty()) {
      throw new InvalidDataException("DsMap cannot be empty");
    }
    try {
      if (ds == null || ds.length() == 0) {
        result.setMessage("Digital Signature is empty");
        return result;
      }

      if (publicKey == null) {
        result.setMessage("Filename of certificate is empty");
        return result;
      }

      if (!toConsider()) {
        result = SAMPLED_RESULT.get();
        return result;
      }
      String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFieldMap);
      result = verifyRequest(publicKey, ds, digest);
      return result;
    } catch (Exception e) {
      result = FAILURE_WITH_EXCEPTION_RESULT.apply(e);
      return result;
    } finally {
      if (digestFieldMap != null && result != null) {
        metricsManager.pushMetrics(digestFieldMap, result);
      }
    }
  }

  /**
   * Verifies the digital signature using public key url and digest fields.
   *
   * @param publicKey {@link PublicKey} of the signing authority
   * @param ds digital signature in the request
   * @param digest a string with concatenated field and value pairs (f1=v1&f2=v2)
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  private Result verifyRequest(PublicKey publicKey, String ds, String digest)
      throws InvalidDataException, ProcessException {
    if (digest == null || digest.length() == 0) {
      throw new InvalidDataException("Digest is empty");
    }
    try {
      boolean status = SignatureUtil.verifySign(publicKey, digest, ds);
      if (status) {
        return SUCCESS_RESULT.get();
      }
      return FAILURE_RESULT.get();
    } catch (Exception e) {
      return FAILURE_WITH_EXCEPTION_RESULT.apply(e);
    }
  }

  /**
   * Verifies an {@link OpenRTB} request.
   *
   * @param openRTB {@link OpenRTB} request
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(OpenRTB openRTB) throws InvalidDataException, ProcessException {
    return verifyRequest(openRTB, false);
  }

  /**
   * @param openRTB {@link OpenRTB} request
   * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or
   *     not
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(OpenRTB openRTB, Boolean debug)
      throws InvalidDataException, ProcessException {
    return verifyRequest(openRTB, debug, null, false);
  }

  /**
   * @param openRTB {@link OpenRTB} request
   * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or
   *     not
   * @param checkMessageExpiry flag to decide whether message expiry checks be performed or not
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(OpenRTB openRTB, Boolean debug, Boolean checkMessageExpiry)
      throws InvalidDataException, ProcessException {
    return verifyRequest(openRTB, debug, null, checkMessageExpiry);
  }

  /**
   * @param openRTB {@link OpenRTB} request
   * @param publicKey {@link PublicKey} of the signing authority
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(OpenRTB openRTB, PublicKey publicKey)
      throws InvalidDataException, ProcessException {
    return verifyRequest(openRTB, false, publicKey, false);
  }

  /**
   * @param openRTB {@link OpenRTB} request
   * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or
   *     not
   * @param publicKey {@link PublicKey} of the signing authority
   * @param checkMessageExpiry flag to decide whether message expiry checks be performed or not
   * @return a boolean stating whether the verification of the signature succeeded or not
   * @throws InvalidDataException if the parameters are null or empty
   * @throws ProcessException if an exception is thrown during the verification process
   */
  public Result verifyRequest(
      OpenRTB openRTB, Boolean debug, PublicKey publicKey, boolean checkMessageExpiry)
      throws InvalidDataException, ProcessException {
    Result result = new Result(FAILURE, "Processing Failure");

    if (openRTB == null) {
      throw new InvalidDataException("OpenRTB object is null");
    }

    if (openRTB.getRequest() == null) {
      throw new InvalidDataException("OpenRTB.Request object is null");
    }

    Source source = openRTB.getRequest().getSource();

    if (source == null) {
      throw new InvalidDataException("OpenRTB.Request.Source is null");
    }

    Map<String, Object> map = null;
    try {
      if (!toConsider()) {
        result.setMessage(null);
        result.setStatus(SAMPLED);
        return result;
      }
      if (checkMessageExpiry) {
        long diff = System.currentTimeMillis() - openRTB.getRequest().getSource().getTs();
        if (diff > messageExpiryTimeInMillis) {
          result =
              new Result(
                  FAILURE,
                  "Message has expired",
                  new ProcessException("Message has expired. Time Difference (in millis):" + diff));
          return result;
        }
      }

      String cert = source.getCert();
      String domain = openRTB.getRequest().getContext().getSite().getDomain();
      String ds = source.getDs();
      String dsMap = source.getDsmap();

      String digest;
      if (debug) {
        digest = DigestUtil.getDigest(openRTB);
      } else {
        map = DigestUtil.getDigestFromDsMap(openRTB);
        digest = DigestUtil.getDigestFromDsMap(dsMap, map);
      }

      if (publicKey != null) {
        result = verifyRequest(publicKey, ds, digest);
      } else {
        String publicKeyUrlToUse = "https://www." + domain + "/" + cert;
        result = verifyRequest(publicKeyUrlToUse, ds, digest);
      }
      return result;
    } finally {
      if (map != null && result != null) {
        metricsManager.pushMetrics(map, result);
      }
    }
  }
}
