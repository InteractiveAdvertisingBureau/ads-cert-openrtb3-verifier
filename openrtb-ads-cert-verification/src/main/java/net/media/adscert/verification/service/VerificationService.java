package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.exceptions.VerificationServiceException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.verification.MetricsManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link VerificationService} provides means to verify digital signature.
 *
 * In addition, a debug flag can be used to decide whether the provided digest should be used or not.
 *
 * @author anupam.v
 * @author pranav.a
 *
 * @since 1.0
 *
 */
public class VerificationService {

	protected int samplingPercentage = 100;
	protected long messageExpiryTimeInMillis = 1000l;
	protected MetricsManager metricsManager = new MetricsManager();

	public VerificationService() {
	}

	public VerificationService(MetricsManager metricsManager) {
		this.metricsManager = metricsManager;
	}

	public VerificationService(int samplingPercentage) {
		this(samplingPercentage, 1000l);
	}

	public VerificationService(int samplingPercentage, long messageExpiryTimeInMillis, MetricsManager metricsManager) {
		this(samplingPercentage, messageExpiryTimeInMillis);
		this.metricsManager = metricsManager;
	}

	public VerificationService(int samplingPercentage, long messageExpiryTimeInMillis) {
		if(samplingPercentage > 100 || samplingPercentage < 1) {
			throw new VerificationServiceException("Sampling rate should be between 1 (inclusive) and 100 (inclusive)");
		}
		this.samplingPercentage = samplingPercentage;

		if(messageExpiryTimeInMillis < 0) {
			throw new VerificationServiceException("Message Expiry Time (In Millis) should be greater than 0");
		}

		this.messageExpiryTimeInMillis = messageExpiryTimeInMillis;
	}

	public boolean toConsider() {
		return ThreadLocalRandom.current().nextInt(1, 101) < samplingPercentage;
	}

	public PublicKey getPublicKey(String url) throws IOException, GeneralSecurityException {
		return SignatureUtil.getPublicKeyFromUrl(url);
	}

	/**
	 *	Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL url of the public key of the signing authority
	 * @param ds  digital signature in the request
	 * @param digest a string with concatenated field and value pairs (f1=v1&f2=v2)
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	private Boolean verifyRequest(String publicKeyURL,
															 String ds,
															 String digest) throws InvalidDataException, ProcessException {
		if (publicKeyURL == null || publicKeyURL.isEmpty()) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital Signature is empty");
		}
		if (digest == null || digest.length() == 0) {
			throw new InvalidDataException("Digest is empty");
		}
		try {
			PublicKey publicKey = getPublicKey(publicKeyURL);
			return SignatureUtil.verifySign(publicKey, digest, ds);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}

	/**
	 * Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL full url of the public key of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFieldMap  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
															 String dsMap,
															 String ds,
															 Map<String, Object> digestFieldMap) throws InvalidDataException, ProcessException {
		boolean status = false;
		try {
			if(!toConsider()) {
				return true;
			}

			if (publicKeyURL == null || publicKeyURL.isEmpty()) {
				throw new InvalidDataException("Filename of certificate is empty");
			}
			String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFieldMap);
			status = verifyRequest(publicKeyURL, ds, digest);
			return status;
		} catch (Exception e) {
			status = false;
			throw new ProcessException(e);
		} finally{
			if(digestFieldMap != null) {
				metricsManager.pushMetrics(digestFieldMap, status ? "success" : "failed");
			}
		}
	}

	/**
	 * Verifies the digital signature using {@link PublicKey} and digest fields.
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFieldMap  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
															 String dsMap,
															 String ds,
															 Map<String, Object> digestFieldMap) throws InvalidDataException, ProcessException {
		if (dsMap == null || dsMap.isEmpty()) {
			throw new InvalidDataException("DsMap is null");
		}
		String status = "success";
		try {
			String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFieldMap);
			boolean flag = verifyRequest(publicKey, ds, digest);
			status = flag ? "success" : "failure";
			return flag;
		} catch (Exception e) {
			status = e.getMessage();
			throw e;
		} finally{
			metricsManager.pushMetrics(digestFieldMap, status);
		}
	}

	/**
	 * Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param ds  digital signature in the request
	 * @param digest a string with concatenated field and value pairs (f1=v1&f2=v2)
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	private Boolean verifyRequest(PublicKey publicKey,
															 String ds,
															 String digest) throws InvalidDataException, ProcessException {
		if(!toConsider()) {
			return true;
		}
		if (publicKey == null) {
			throw new InvalidDataException("Public Key is null");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital Signature is empty");
		}
		if (digest == null || digest.length() == 0) {
			throw new InvalidDataException("Digest is empty");
		}

		try {
			return SignatureUtil.verifySign(publicKey, digest, ds);
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

	/**
	 * Verifies an {@link OpenRTB} request.
	 *
	 * @param openRTB {@link OpenRTB} request
	 *
	 * @return  a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or not
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
															 Boolean debug) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, debug, null, false);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or not
	 * @param checkMessageExpiry flag to decide whether message expiry checks be performed or not
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
															 Boolean debug,
															 Boolean checkMessageExpiry) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, debug, null, checkMessageExpiry);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param publicKey {@link PublicKey} of the signing authority
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
															 PublicKey publicKey) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false, publicKey, false);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or not
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param checkMessageExpiry flag to decide whether message expiry checks be performed or not
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
															 Boolean debug,
															 PublicKey publicKey,
															 boolean checkMessageExpiry) throws InvalidDataException, ProcessException {
		String status = "success";
		boolean flag = false;
		Map<String, Object> map = null;
		try{
			if(!toConsider()) {
				return true;
			}
			if(checkMessageExpiry) {
				long diff = System.currentTimeMillis() - openRTB.getRequest().getSource().getTs();
				if(diff > messageExpiryTimeInMillis) {
					status = "Message has expired";
					throw new ProcessException("Message has expired. Time Difference (in millis):" + diff);
				}
			}
			if (openRTB == null) {
				status = "OpenRTB object is null";
				throw new InvalidDataException(status);
			}

			if (openRTB.getRequest() == null) {
				status = "OpenRTB.Request object is null";
				throw new InvalidDataException(status);
			}

			Source source = openRTB.getRequest().getSource();

			if (source == null) {
				status = "OpenRTB.Request.Source is null";
				throw new InvalidDataException(status);
			}
			String cert  = source.getCert();
			String domain = openRTB.getRequest().getContext().getSite().getDomain();
			String ds = source.getDs();
			String dsMap = source.getDsmap();

			String digest;
			if(debug) {
				digest = DigestUtil.getDigest(openRTB);
			} else {
				map = DigestUtil.getDigestFromDsMap(openRTB);
				digest = DigestUtil.getDigestFromDsMap(dsMap, map);
			}

			if (publicKey == null) {
				String publicKeyUrlToUse = cert;
				if(!cert.startsWith("http")) {
					if(!cert.startsWith("www")) {
						publicKeyUrlToUse = "http://www." + domain + "/" + cert;
					} else {
						publicKeyUrlToUse = "http://" + cert;
					}
				}

				flag = verifyRequest(publicKeyUrlToUse, ds, digest);
				status = flag ? "success" : "failed";
				return flag;
			}

			flag = verifyRequest(publicKey, ds, digest);
			status = flag ? "success" : "failed";
			return flag;
		} finally{
			if(map != null) {
				metricsManager.pushMetrics(map, status);
			}
		}
	}

}
