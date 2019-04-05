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

	public VerificationServiceWithCache(int samplingRate, long messageExpiryTimeInMillis) {
		super(samplingRate, messageExpiryTimeInMillis);
	}
}
