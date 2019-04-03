package net.media.adscert.verification.cache;

import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.verification.MetricsManager;

import javax.cache.Cache;
import java.security.PublicKey;

public class VerificationServiceJCache extends VerificationServiceWithCache {

	private Cache<String, PublicKey> publicKeyCache;

	public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache) {
		super();
		this.publicKeyCache = publicKeyCache;
	}

	public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache, MetricsManager metricsManager) {
		super();
		this.publicKeyCache = publicKeyCache;
		this.metricsManager = metricsManager;
	}

	public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache,
																	 int samplingRate, long messageExpiryTimeInMillis, MetricsManager metricsManager) {
		super(samplingRate, messageExpiryTimeInMillis);
		this.publicKeyCache = publicKeyCache;
		this.metricsManager = metricsManager;
	}

	@Override
	public PublicKey getPublicKey(String url) throws ProcessException {
		try {
			return this.publicKeyCache.get(url);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}
}
