package net.media.adscert.verification;

import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultJCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceJCache;
import net.media.adscert.verification.service.VerificationService;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class VerificationServiceTests {


	public OpenRTB getOpenRTBObject() {
		OpenRTB openRTB = new OpenRTB();
		openRTB.setRequest(new Request());
		openRTB.getRequest().setSource(new Source());
		openRTB.getRequest().getSource().setDsmap("domain=&ft=&tid=");
		openRTB.getRequest().getSource().setDigest("domain=newsite.com&ft=d&tid=ABC7E92FBD6A");
		return openRTB;
	}

	public Map<String, String> getMapOfDigestFields() {
		Map<String, String> digestFields = new HashMap<>();
		digestFields.put("domain", "newsite.com");
		digestFields.put("ft", "d");
		digestFields.put("tid", "ABC7E92FBD6A");
		return digestFields;
	}

	@Test
	public void verifySignatureFromOpenRTBObject() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, SignatureException {
		VerificationService verificationService = new VerificationService();
		OpenRTB openRTB = getOpenRTBObject();
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));

		assertEquals(true, verificationService.verifyRequest(openRTB, true, publicKey));
	}

	@Test
	public void verifySignatureFromSpecificFields() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		VerificationService verificationService = new VerificationService();
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String dsMap = "domain=&ft=&tid=";
		String digest = "domain=newsite.com&ft=d&tid=ABC7E92FBD6A";
		String ds = SignatureUtil.signMessage(privateKey, digest);

		assertEquals(true, verificationService.verifyRequest(publicKey, dsMap, ds, getMapOfDigestFields()));
		assertEquals(true, verificationService.verifyRequest(publicKey, dsMap, ds, digest, getMapOfDigestFields()));
	}

	@Test
	public void verifySignatureFromOpenRTBJCache() {
		VerificationServiceJCache verificationServiceJCache = new VerificationServiceJCache(DefaultJCacheBuilder.newBuilder().build());

	}
}
