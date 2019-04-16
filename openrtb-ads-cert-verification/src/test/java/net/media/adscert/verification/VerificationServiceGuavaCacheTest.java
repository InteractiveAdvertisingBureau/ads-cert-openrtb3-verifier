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

package net.media.adscert.verification;

import com.google.common.cache.Cache;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultGuavaCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceGuavaCache;
import net.media.adscert.verification.enums.Result;
import net.media.adscert.verification.metrics.MetricsManager;
import org.junit.Assert;
import org.junit.Test;

import java.security.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class VerificationServiceGuavaCacheTest {

  @Test
  public void test()
		  throws GeneralSecurityException, InterruptedException {
    final KeyPair keyPair1 = TestUtil.generateKeyPair();
    final KeyPair keyPair2 = TestUtil.generateKeyPair();
    final KeyPair keyPair3 = TestUtil.generateKeyPair();
    MetricsManager metricsManager =
        new MetricsManager() {
          @Override
          public void pushMetrics(Map<String, Object> metricsMap, Result result) {
            assertTrue(metricsMap.size() == 3);
            assertTrue(metricsMap.get("domain").toString().equals("newsite.com"));
            assertTrue(metricsMap.get("ft").toString().equals("d"));
            assertTrue(metricsMap.get("tid").toString().equals("ABC7E92FBD6A"));
            assertTrue(result.getStatus() == Result.Status.SUCCESS);
          }
        };

    Cache<String, PublicKey> cache =
        DefaultGuavaCacheBuilder.newBuilder()
            .setExpireAfterAccess(Duration.of(100, ChronoUnit.MILLIS))
            .setExpireAfterWrite(Duration.of(100, ChronoUnit.MILLIS))
            .build();

    AtomicInteger count = new AtomicInteger(0);

    VerificationServiceGuavaCache service =
        new VerificationServiceGuavaCache(
            cache,
            url ->
                () -> {
                  int currentCount = count.addAndGet(1);
                  if (currentCount == 1) {
                    return keyPair1.getPublic();
                  } else if (currentCount == 2) {
                    return keyPair2.getPublic();
                  } else {
                    return keyPair3.getPublic();
                  }
                },
            metricsManager);

    OpenRTB openRTB = TestUtil.getOpenRTBObject();
    openRTB.getRequest().getSource().setCert("ads1.cert");
    String digest = DigestUtil.getDigest(openRTB);
    openRTB
        .getRequest()
        .getSource()
        .setDs(SignatureUtil.signMessage(keyPair1.getPrivate(), digest));

    Assert.assertTrue(service.verifyRequest(openRTB, true).getStatus() == Result.Status.SUCCESS);
    Assert.assertTrue(service.verifyRequest(openRTB, false).getStatus() == Result.Status.SUCCESS);

    Thread.sleep(560);

    // Testing refresh
    openRTB
        .getRequest()
        .getSource()
        .setDs(SignatureUtil.signMessage(keyPair2.getPrivate(), digest));
    Assert.assertTrue(service.verifyRequest(openRTB, true).getStatus() == Result.Status.SUCCESS);
    Assert.assertTrue(service.verifyRequest(openRTB, false).getStatus() == Result.Status.SUCCESS);

    cache.invalidateAll();

    // Testing cache clear operation
    openRTB
        .getRequest()
        .getSource()
        .setDs(SignatureUtil.signMessage(keyPair3.getPrivate(), digest));
    Assert.assertTrue(service.verifyRequest(openRTB, true).getStatus() == Result.Status.SUCCESS);
    Assert.assertTrue(service.verifyRequest(openRTB, false).getStatus() == Result.Status.SUCCESS);
  }
}
