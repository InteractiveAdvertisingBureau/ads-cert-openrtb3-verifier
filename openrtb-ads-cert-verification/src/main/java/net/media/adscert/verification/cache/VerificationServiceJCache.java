/*
 * Copyright © 2019 - present. MEDIA.NET ADVERTISING FZ-LLC.
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

import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.verification.metrics.MetricsManager;

import javax.cache.Cache;
import java.security.PublicKey;

public class VerificationServiceJCache extends VerificationServiceWithCache {

  private Cache<String, PublicKey> publicKeyCache;

  public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache) {
    super();
    this.publicKeyCache = publicKeyCache;
  }

  public VerificationServiceJCache(Cache<String, PublicKey> publicKeyCache, int samplingRate) {
    super(samplingRate, 1000l);
    this.publicKeyCache = publicKeyCache;
  }

  public VerificationServiceJCache(
      Cache<String, PublicKey> publicKeyCache, int samplingRate, long messageExpiryTimeInMillis) {
    super(samplingRate, messageExpiryTimeInMillis);
    this.publicKeyCache = publicKeyCache;
  }

  public VerificationServiceJCache(
      Cache<String, PublicKey> publicKeyCache, MetricsManager metricsManager) {
    super();
    this.publicKeyCache = publicKeyCache;
    this.metricsManager = metricsManager;
  }

  public VerificationServiceJCache(
      Cache<String, PublicKey> publicKeyCache,
      int samplingRate,
      long messageExpiryTimeInMillis,
      MetricsManager metricsManager) {
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
