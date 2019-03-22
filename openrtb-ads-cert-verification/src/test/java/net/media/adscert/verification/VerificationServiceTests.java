package net.media.adscert.verification;

import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.service.VerificationService;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;

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

	@Test
	public void verifySignature() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, SignatureException {
		VerificationService verificationService = new VerificationService();
		OpenRTB openRTB = getOpenRTBObject();
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String digest = DigestUtil.getDigest(openRTB);
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));

		assertEquals(true, verificationService.verifyRequest(openRTB, true, publicKey));
	}
}
