package net.media.adscert.verification;

import com.google.common.cache.Cache;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultGuavaCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceGuavaCache;
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
	public void test() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InterruptedException {
		final KeyPair keyPair1 = SignatureUtil.generateKeyPair();
		final KeyPair keyPair2 = SignatureUtil.generateKeyPair();
		final KeyPair keyPair3 = SignatureUtil.generateKeyPair();
		MetricsManager metricsManager = new MetricsManager() {
			@Override
			public void pushMetrics(Map<String, Object> metricsMap, String status, String failureMessage) {
				assertTrue(metricsMap.size() == 3);
				assertTrue(metricsMap.get("domain").toString().equals("newsite.com"));
				assertTrue(metricsMap.get("ft").toString().equals("d"));
				assertTrue(metricsMap.get("tid").toString().equals("ABC7E92FBD6A"));
				assertTrue(status.equals("success"));
			}
		};

		Cache<String, PublicKey> cache = DefaultGuavaCacheBuilder.newBuilder()
				.setExpireAfterAccess(Duration.of(100, ChronoUnit.MILLIS))
				.setExpireAfterWrite(Duration.of(100, ChronoUnit.MILLIS))
				.build();

		AtomicInteger count = new AtomicInteger(0);

		VerificationServiceGuavaCache service = new VerificationServiceGuavaCache(cache, url -> () -> {
			int currentCount = count.addAndGet(1);
			if (currentCount == 1) {
				return keyPair1.getPublic();
			}
			else if (currentCount == 2) {
				return keyPair2.getPublic();
			}
			else {
				return keyPair3.getPublic();
			}
		}, metricsManager);

		TestUtil testUtil = new TestUtil();
		OpenRTB openRTB = testUtil.getOpenRTBObject();
		openRTB.getRequest().getSource().setCert("ads1.cert");
		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair1.getPrivate(), digest));

		Assert.assertTrue(service.verifyRequest(openRTB, true));
		Assert.assertTrue(service.verifyRequest(openRTB, false));

		Thread.sleep(560);

		// Testing refresh
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair2.getPrivate(), digest));
		Assert.assertTrue(service.verifyRequest(openRTB, true));
		Assert.assertTrue(service.verifyRequest(openRTB, false));

		cache.invalidateAll();

		// Testing cache clear operation
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair3.getPrivate(), digest));
		Assert.assertTrue(service.verifyRequest(openRTB, true));
		Assert.assertTrue(service.verifyRequest(openRTB, false));
	}
}
