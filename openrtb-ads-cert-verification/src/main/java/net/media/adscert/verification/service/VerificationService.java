package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.utils.DigestUtil;

import java.security.PublicKey;
import java.util.Map;


/**
 *
 */
public class VerificationService {

	/**
	 * Constructor to create an instance of the service
	 */
	public VerificationService() {

	}

	/**
	 *
	 * @param openRTB OpenRTB object
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false);
	}

	/**
	 *
	 * @param publicKeyURL  Public Key of the signing authority
	 * @param dsMap The fields that were used for signing the request
	 * @param ds  Digital Signature in the request
	 * @param digestFields  A map of fields that were used for generating the signature and their values
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(publicKeyURL, dsMap, ds, null, digestFields);
	}

	/**
	 *
	 * @param publicKey Public Key of the signing authority
	 * @param dsMap The fields that were used for signing the request
	 * @param ds  Digital Signature in the request
	 * @param digestFields  A map of fields that were used for generating the signature and their values
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(publicKey, dsMap, ds, null, digestFields);
	}

	/**
	 *
	 * @param publicKeyURL
	 * @param dsMap
	 * @param ds
	 * @param digest
	 * @param digestFields
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (publicKeyURL == null || publicKeyURL.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}

		PublicKey publicKey;

		try {
			publicKey = SignatureUtil.getPublicKeyFromUrl(publicKeyURL);
		} catch (Exception e) {
			throw new ProcessException(e);
		}

		return verifyRequest(publicKey, dsMap, ds, digest, digestFields);
	}

	/**
	 *
	 * @param publicKey
	 * @param dsMap
	 * @param ds
	 * @param digest
	 * @param digestFields
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (digest == null && digestFields == null) {
			throw new InvalidDataException("Digest Field Map is null");
		}
		if (publicKey == null) {
			throw new InvalidDataException("Public Key cannot be null");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (dsMap == null || dsMap.length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			digest = digest == null
					? DigestUtil.getDigestFromDsMap(dsMap, digestFields)
					: digest;

			return SignatureUtil.verifySign(publicKey, digest, ds);
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

	/**
	 *
	 * @param openRTB
	 * @param debug
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, debug, null);
	}

	/**
	 *
	 * @param openRTB
	 * @param publicKey
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false, publicKey);
	}

	/**
	 *
	 * @param openRTB
	 * @param debug
	 * @param publicKey
	 * @return  a boolean stating whether the signature in the request is correct or forged
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		if (openRTB == null) {
			throw new InvalidDataException("OpenRTB object is null");
		}

		Source source = openRTB.getRequest().getSource();

		if (publicKey == null && (source.getCert() == null || source.getCert().length() == 0)) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		if (source.getDs() == null || source.getDs().length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (source.getDsmap() == null || source.getDsmap().length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			String digest = debug
					? DigestUtil.getDigest(openRTB)
					: DigestUtil.getDigestFromDsMap(openRTB);


			publicKey = publicKey == null
					? SignatureUtil.getPublicKeyFromUrl(source.getCert())
					: publicKey;

			return SignatureUtil.verifySign(publicKey, digest, source.getDs());
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

}
