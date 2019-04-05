package net.media.adscert.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultJCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceJCache;
import net.media.adscert.verification.service.VerificationService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VerificationServiceTest {

	@Test
	public void verifySignatureFromOpenRTBObject() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InterruptedException {
		VerificationService verificationService = new VerificationService(100, 500l);
		OpenRTB openRTB = TestUtil.getOpenRTBObject();
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));

		assertEquals(true, verificationService.verifyRequest(openRTB, true, publicKey, false));

		Thread.sleep(600l);

		try {
			verificationService.verifyRequest(openRTB, true, publicKey, true);
			assertTrue("Timestamp check did not fail", false);
		} catch (Exception e) {
			assertTrue(true);
		}
		assertTrue(verificationService.verifyRequest(openRTB, false, publicKey, false));
	}

	@Test
	public void verifySignatureFromSpecificFields() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		MetricsManager metricsManager = new MetricsManager();
    metricsManager.setJsonHandler(
        json -> {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						final Map map = objectMapper.readValue(json, Map.class);
						assertTrue(map.size() == 4);
						assertTrue(map.get("domain").toString().equals("newsite.com"));
						assertTrue(map.get("ft").toString().equals("d"));
						assertTrue(map.get("tid").toString().equals("ABC7E92FBD6A"));
						assertTrue(map.get("status").toString().equals("success"));
					} catch (IOException e) {
						Assert.fail(e.getMessage());
					}
				});
		VerificationService verificationService = new VerificationService(100, 1000l, metricsManager);
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String dsMap = "domain=&ft=&tid=";
		String digest = "domain=newsite.com&ft=d&tid=ABC7E92FBD6A";
		String ds = SignatureUtil.signMessage(privateKey, digest);

		assertEquals(true, verificationService.verifyRequest(publicKey, dsMap, ds, TestUtil.getMapOfDigestFields()));
		// assertEquals(true, verificationService.verifyRequest(publicKey, ds, digest));
	}
}
