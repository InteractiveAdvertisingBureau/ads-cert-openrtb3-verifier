package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.ECDSAUtil;
import net.media.adscert.verification.manager.PublicKeyManager;
import net.media.adscert.verification.utils.AdCertVerification;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.security.PublicKey;
import java.util.Map;

public class VerificationService {

	private PublicKeyManager publicKeyManager;

	private Validator validator;

	public VerificationService(PublicKeyManager publicKeyManager) {
		this.publicKeyManager = publicKeyManager;
		init();
	}

	private void init() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		this.validator = validatorFactory.getValidator();
	}

	public Boolean verifyRequest(OpenRTB openRTB) throws InvalidDataException {
		return verifyRequest(openRTB, false);
	}

	public Boolean verifyRequest(String cert, String dsMap, String ds, Map<String, String> digestFields) throws InvalidDataException {
		return verifyRequest(cert, dsMap, ds, null, digestFields);
	}

	public Boolean verifyRequest(String cert, String dsMap, String ds, String digest, Map<String, String> digestFields) throws InvalidDataException {
		if (digest == null && digestFields == null) {
			throw new InvalidDataException("Digest Field Map is null");
		}

		if (cert == null || cert.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (dsMap == null || dsMap.length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			digest = digest == null
					? AdCertVerification.getDigestFromDsMap(dsMap, digestFields)
					: digest;

			PublicKey pub = publicKeyManager.getKey(cert);

			return ECDSAUtil.verifySign(pub, digest, ds);
		} catch (Exception e) {
			throw new InvalidDataException("Error verifying the OpenRTB object", e);
		}
	}

	public Boolean verifyRequest(OpenRTB openRTB, Boolean debug) throws InvalidDataException {
		if (openRTB == null) {
			throw new InvalidDataException("OpenRTB object is null");
		}

		Source source = openRTB.getRequest().getSource();

		if (source.getCert() == null || source.getCert().length() == 0) {
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
					? AdCertVerification.getDigest(openRTB)
					: AdCertVerification.getDigestFromDsMap(openRTB);


			PublicKey pub = publicKeyManager.getKey(source.getCert());

			return ECDSAUtil.verifySign(pub, digest, source.getDs());
		} catch (Exception e) {
			throw new InvalidDataException("Error verifying the OpenRTB object", e);
		}
	}

}
