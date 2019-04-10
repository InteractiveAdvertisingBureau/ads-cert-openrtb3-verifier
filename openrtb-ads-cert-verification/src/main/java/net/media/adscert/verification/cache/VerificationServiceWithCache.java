package net.media.adscert.verification.cache;

import net.media.adscert.verification.service.VerificationService;

public abstract class VerificationServiceWithCache extends VerificationService {

	public VerificationServiceWithCache() {
		super();
	}

	public VerificationServiceWithCache(int samplingRate, long messageExpiryTimeInMillis) {
		super(samplingRate, messageExpiryTimeInMillis);
	}
}
