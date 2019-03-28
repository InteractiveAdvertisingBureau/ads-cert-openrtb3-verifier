package net.media.adscert.verification.cache;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.service.VerificationService;

import java.security.PublicKey;
import java.util.Map;

public abstract class VerificationServiceWithCache extends VerificationService {

	public VerificationServiceWithCache() {
		super();
	}

	protected abstract PublicKey getKeyFromCache(String url) throws ProcessException;

	@Override
	public Boolean verifyRequest(String publicKeyURL,
	                             String ds,
	                             String digest) throws InvalidDataException, ProcessException {
		if (publicKeyURL == null || publicKeyURL.isEmpty()) {
			throw new InvalidDataException("Filename of certificate is empty");
		}

		try {
			return verifyRequest(getKeyFromCache(publicKeyURL), ds, digest);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}
}
