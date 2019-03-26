package net.media.adscert.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.utils.SignatureUtil;

import java.security.PublicKey;

public class PublicKeyManager {
	private LoadingCache<String, PublicKey> pubKeyCache;

	public PublicKeyManager(Long cacheSize) {
		CacheLoader<String, PublicKey> publicKeyCacheLoader = new CacheLoader<String, PublicKey>() {
			@Override
			public PublicKey load(String url) {
				try {
					return SignatureUtil.getPublicKeyFromUrl(url);
				} catch (Exception e) {
					throw new InvalidDataException("Unable to fetch key data from url provided.");
				}
			}
		};
		this.pubKeyCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(publicKeyCacheLoader);
	}

	public PublicKey get(final String url) {
		return this.pubKeyCache.getUnchecked(url);
	}

	public void clear() {
		this.pubKeyCache.invalidateAll();
	}

	public void refresh() {
		for (String k : this.pubKeyCache.asMap().keySet()) {
			try {
				PublicKey newV = SignatureUtil.getPublicKeyFromUrl(k);
				pubKeyCache.put(k, newV);
			} catch (Exception e) {
				pubKeyCache.invalidate(k);
			}
		}
		this.pubKeyCache.cleanUp();
	}
}
