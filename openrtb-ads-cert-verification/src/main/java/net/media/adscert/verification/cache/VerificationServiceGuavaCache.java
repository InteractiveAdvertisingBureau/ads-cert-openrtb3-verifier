/*
 * Copyright © 2019 - present. MEDIA NET SOFTWARE SERVICES PVT. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
