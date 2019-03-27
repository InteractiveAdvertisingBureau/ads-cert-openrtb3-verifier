package net.media.adscert.verification.service;

import net.media.adscert.exceptions.ProcessException;

import javax.cache.Cache;
import java.security.PublicKey;

public class VerificationServiceJCache extends VerificationServiceWithCache {

	private Cache<String, PublicKey> publicKeyCache;

	public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache) {
		super();
		this.publicKeyCache = publicKeyCache;
	}

	@Override
	protected PublicKey getKeyFromCache(String url) throws ProcessException {
		try {
			return this.publicKeyCache.get(url);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}
}
