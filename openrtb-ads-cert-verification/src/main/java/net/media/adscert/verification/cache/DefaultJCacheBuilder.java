package net.media.adscert.verification.cache;

import net.media.adscert.utils.SignatureUtil;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.spi.CachingProvider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Builder to create a ready-to-use JSR 107 compliant cache.
 *
 * @author pranav.a
 * @author anupam.v
 *
 * @since 1.0
 */
public class DefaultJCacheBuilder {
	private Duration expiryForAccess = new Duration(TimeUnit.DAYS, 30);
	private Duration expiryForCreation = new Duration(TimeUnit.DAYS, 30);
	private Duration expiryForUpdate = new Duration(TimeUnit.DAYS, 30);
	private CacheLoader<String, PublicKey> cacheLoader;

	private DefaultJCacheBuilder() {
		this.cacheLoader = new CacheLoader<String, PublicKey>() {

			@Override
			public PublicKey load(String url) throws CacheLoaderException {
				try {
					return SignatureUtil.getPublicKeyFromUrl(url);
				} catch (Exception e) {
					throw new CacheLoaderException(e);
				}
			}

			@Override
			public Map<String, PublicKey> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
				Map<String, PublicKey> data = new HashMap<>();
				for (String key : keys) {
					try {
						data.put(key, SignatureUtil.getPublicKeyFromUrl(key));
					} catch (Exception ignored) {

					}
				}
				return data;
			}
		};

	}

	/**
	 * Constructs a {@link DefaultJCacheBuilder} for creating a JSR-107 compliant {@link Cache}.
	 *
	 * @return {@link DefaultJCacheBuilder}
	 */
	public static DefaultJCacheBuilder newBuilder() {
		return new DefaultJCacheBuilder();
	}

	public DefaultJCacheBuilder setExpiryForAccess(Duration expiryForAccess) {
		this.expiryForAccess = expiryForAccess;
		return this;
	}

	public DefaultJCacheBuilder setExpiryForCreation(Duration expiryForCreation) {
		this.expiryForCreation = expiryForCreation;
		return this;
	}

	public DefaultJCacheBuilder setExpiryForUpdate(Duration expiryForUpdate) {
		this.expiryForUpdate = expiryForUpdate;
		return this;
	}

	public DefaultJCacheBuilder setCacheLoader(CacheLoader<String, PublicKey> loader) {
		this.cacheLoader = loader;
		return this;
	}


	public Cache<String, PublicKey> build() {
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();

		ExpiryPolicy expiryPolicy = new ExpiryPolicy() {
			@Override
			public Duration getExpiryForCreation() {
				return expiryForCreation;
			}

			@Override
			public Duration getExpiryForAccess() {
				return expiryForAccess;
			}

			@Override
			public Duration getExpiryForUpdate() {
				return expiryForUpdate;
			}
		};

		return cacheManager.createCache("publicKeyCache", new MutableConfiguration<String, PublicKey>()
				.setReadThrough(true)
				.setExpiryPolicyFactory(new FactoryBuilder.SingletonFactory<>(expiryPolicy))
				.setCacheLoaderFactory(new FactoryBuilder.SingletonFactory<>(this.cacheLoader)));
	}
}
