package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.exceptions.VerificationServiceException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.utils.DigestUtil;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link VerificationService} provides means to verify digital signature. Following are <b>some</b> of the ways to verify:
 * <ol>
 *   <li>By passing {@link OpenRTB} request object</li>
 *   <li>By passing Public Key URL, Fields used to generate Digital Signature, Digital Signature and Map of fields used to sign the request</li>
 *   <li>By passing {@link PublicKey}, Fields used to generate Digital Signature, Digital Signature and Map of fields used to sign the request</li>
 *   <li>By passing Public Key URL, Fields used to generate Digital Signature, Digital Signature, Digest and Map of fields used to sign the request</li>
 *   <li>By passing {@link PublicKey}, Fields used to generate Digital Signature, Digital Signature, Digest and Map of fields used to sign the request</li>
 * </ol>
 *
 * In addition, a debug flag can be used to decide whether the provided digest should be used or not.
 *
 * @author pranav.a
 * @author anupam.v
 *
 * @since 1.0
 *
 */
public class VerificationService {

	private int samplingRate = 100;

	public VerificationService() {

	}

	public VerificationService(int samplingRate) {
		if(samplingRate > 100 || samplingRate < 1) {
			throw new VerificationServiceException("Sampling rate should be between 1 (inclusive) and 100 (inclusive)");
		}
		this.samplingRate = samplingRate;
	}

	public boolean toConsider() {
		return ThreadLocalRandom.current().nextInt(1, samplingRate + 1) < samplingRate;
	}

	/**
	 *	Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL url of the public key of the signing authority
	 * @param ds  digital signature in the request
	 * @param digest
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String ds,
	                             String digest) throws InvalidDataException, ProcessException {
		if(!toConsider()) {
			return true;
		}

		if (publicKeyURL == null || publicKeyURL.isEmpty()) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		try {
			PublicKey publicKey = SignatureUtil.getPublicKeyFromUrl(publicKeyURL);
			return verifyRequest(publicKey, ds, digest);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}

	/**
	 * Verifies the digital signature using {@link PublicKey} and digest fields.
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFields  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if(!toConsider()) {
			return true;
		}
		if (dsMap == null || dsMap.isEmpty()) {
			throw new InvalidDataException("DsMap is null");
		}
		String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFields);
		return verifyRequest(publicKey, ds, digest);
	}

	/**
	 * Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL url of the public key of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFields  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if(!toConsider()) {
			return true;
		}
		if (publicKeyURL == null || publicKeyURL.isEmpty()) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFields);
		return verifyRequest(publicKeyURL, ds, digest);
	}

	/**
	 * Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param ds  digital signature in the request
	 * @param digest
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
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
		return verifyRequest(openRTB, debug, null);
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
		return verifyRequest(openRTB, false, publicKey);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug a boolean used to decide whether the digest from {@link OpenRTB} should be used or not
	 * @param publicKey {@link PublicKey} of the signing authority
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		if(!toConsider()) {
			return true;
		}
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
		String cert  = source.getCert();
		String ds = source.getDs();
		String dsMap = source.getDsmap();

		String digest = debug
				? DigestUtil.getDigest(openRTB)
				: DigestUtil.getDigestFromDsMap(openRTB);

		if (publicKey == null) {
			return verifyRequest(cert, ds, digest);
		}

		return verifyRequest(publicKey, ds, digest);
	}

}
