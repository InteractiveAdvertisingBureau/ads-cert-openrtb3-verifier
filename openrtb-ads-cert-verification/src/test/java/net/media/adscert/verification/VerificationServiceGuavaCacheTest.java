package net.media.adscert.verification;

import com.google.common.cache.Cache;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultGuavaCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceGuavaCache;
import org.junit.Assert;
import org.junit.Test;

import java.security.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VerificationServiceGuavaCacheTest {

	@Test
	public void test() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InterruptedException {
		final KeyPair keyPair1 = SignatureUtil.generateKeyPair();
		final KeyPair keyPair2 = SignatureUtil.generateKeyPair();
		final KeyPair keyPair3 = SignatureUtil.generateKeyPair();

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
		});

		OpenRTB openRTB = TestUtil.getOpenRTBObject();
		openRTB.getRequest().getSource().setCert("http://www.blahblahblah.com");
		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair1.getPrivate(), digest));

		Assert.assertTrue(service.verifyRequest(openRTB, true));

		Thread.sleep(560);

		// Testing refresh
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair2.getPrivate(), digest));
		Assert.assertTrue(service.verifyRequest(openRTB, true));

		cache.invalidateAll();

		// Testing cache clear operation
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair3.getPrivate(), digest));
		Assert.assertTrue(service.verifyRequest(openRTB, true));
	}
}
