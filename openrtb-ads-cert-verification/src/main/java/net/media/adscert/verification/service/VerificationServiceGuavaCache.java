package net.media.adscert.verification.service;

import com.google.common.cache.Cache;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.utils.SignatureUtil;

import java.security.PublicKey;

public class VerificationServiceGuavaCache extends VerificationServiceWithCache {

	private Cache<String, PublicKey> publicKeyCache;

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache) {
		super();
		this.publicKeyCache = publicKeyCache;
	}

	@Override
	protected PublicKey getKeyFromCache(String url) throws ProcessException {
		try {
			return this.publicKeyCache.get(url, () -> {
						try {
							return SignatureUtil.getPublicKeyFromUrl(url);
						} catch (Exception e) {
							throw new InvalidDataException("Unable to fetch key data from url : " + url, e);
						}
					}
			);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}
}
