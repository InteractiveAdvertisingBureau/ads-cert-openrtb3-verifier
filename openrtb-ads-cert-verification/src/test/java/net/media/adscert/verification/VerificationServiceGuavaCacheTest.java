package net.media.adscert.verification;

import net.media.adscert.models.OpenRTB;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultGuavaCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceGuavaCache;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.TimeUnit;

public class VerificationServiceGuavaCacheTest {

	@Test
	public void test() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		final KeyPair keyPair1 = SignatureUtil.generateKeyPair();
		final KeyPair keyPair2 = SignatureUtil.generateKeyPair();

		VerificationServiceGuavaCache service = new VerificationServiceGuavaCache(DefaultGuavaCacheBuilder.newBuilder()
				.setRefreshTime(100, TimeUnit.MILLISECONDS)
				.build());

		OpenRTB openRTB = TestUtil.getOpenRTBObject();
		openRTB.getRequest().getSource().setCert("http://www.blahblahblah.com");
		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair1.getPrivate(), digest));



	}
}
