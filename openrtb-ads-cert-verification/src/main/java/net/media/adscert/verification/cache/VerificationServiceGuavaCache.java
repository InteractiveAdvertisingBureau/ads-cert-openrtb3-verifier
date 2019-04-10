package net.media.adscert.verification.cache;

import com.google.common.cache.Cache;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.metrics.MetricsManager;

import java.security.PublicKey;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class VerificationServiceGuavaCache extends VerificationServiceWithCache {

	private Cache<String, PublicKey> publicKeyCache;
	private Function<String, Callable<PublicKey>> keyLoader = url -> () -> {
		try {
			return SignatureUtil.getPublicKeyFromUrl(url);
		} catch (Exception e) {
			throw new InvalidDataException("Unable to fetch key data from url : " + url, e);
		}
	};

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache) {
		super();
		this.publicKeyCache = publicKeyCache;
	}

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache, int samplingRate) {
		super(samplingRate, 1000l);
		this.publicKeyCache = publicKeyCache;
	}

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache, Function<String, Callable<PublicKey>> keyLoader) {
		super();
		this.publicKeyCache = publicKeyCache;
		this.keyLoader = keyLoader;
	}

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache, MetricsManager metricsManager) {
		super();
		this.publicKeyCache = publicKeyCache;
		this.metricsManager = metricsManager;
	}

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache, Function<String, Callable<PublicKey>> keyLoader, MetricsManager metricsManager) {
		super();
		this.publicKeyCache = publicKeyCache;
		this.keyLoader = keyLoader;
		this.metricsManager = metricsManager;
	}

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache, Function<String, Callable<PublicKey>> keyLoader,
																			 int samplingRate, long messageExpiryTimeInMillis, MetricsManager metricsManager) {
		super(samplingRate, messageExpiryTimeInMillis);
		this.publicKeyCache = publicKeyCache;
		this.keyLoader = keyLoader;
		this.metricsManager = metricsManager;
	}

	@Override
	public PublicKey getPublicKey(String url) throws ProcessException {
		try {
			return this.publicKeyCache.get(url, keyLoader.apply(url));
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}
}
