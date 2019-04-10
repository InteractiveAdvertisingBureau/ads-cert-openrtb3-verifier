package net.media.adscert.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.media.adscert.exceptions.VerificationServiceException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.JacksonObjectMapper;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultJCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceJCache;
import net.media.adscert.verification.service.FileVerificationService;
import net.media.adscert.verification.service.VerificationService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VerificationServiceTest {
	private ClassLoader classLoader = getClass().getClassLoader();

	@Test
	public void verifySignatureFromOpenRTBObject() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InterruptedException, NoSuchProviderException, InvalidAlgorithmParameterException {
		VerificationService verificationService = new VerificationService(100, 500l);
		TestUtil testUtil = new TestUtil();
		OpenRTB openRTB = testUtil.getOpenRTBObject();
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
	public void verifySignatureFromFile() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InterruptedException, NoSuchProviderException, InvalidAlgorithmParameterException {
		FileVerificationService verificationService = new FileVerificationService();
		TestUtil testUtil = new TestUtil();
		OpenRTB openRTB = testUtil.getOpenRTBObject();
		OpenRTB openRTB1 = testUtil.getOpenRTBObject();
		KeyPair keyPair = SignatureUtil.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String digest = "domain=newsite.com&ft=d&tid=ABC7E92FBD6A";
		openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));
		openRTB1.getRequest().getSource().setDs("abcdef");
		String inputFilePath = classLoader.getResource("request").getPath()+"/request30ForFileTesting.json";
		String outputFilePath = classLoader.getResource("request").getPath()+"/resultFileTesting.json";
		File inputFile = new File(inputFilePath);
		try {
			FileOutputStream outputStream = new FileOutputStream(inputFile);
			outputStream.write((JacksonObjectMapper.getMapper().writeValueAsString(openRTB) + System.getProperty("line.separator")
					+ JacksonObjectMapper.getMapper().writeValueAsString(openRTB1)).getBytes());

			verificationService.verify(inputFilePath, outputFilePath, publicKey);
			assertTrue(true);
			List<String> list = new ArrayList<>();

			try (BufferedReader br = Files.newBufferedReader(Paths.get(outputFilePath))) {

				//br returns as stream and convert it into a List
				list = br.lines().collect(Collectors.toList());
				Assert.assertEquals( "Success", list.get(0));
				Assert.assertNotEquals("Success", list.get(1));
			} catch (IOException e) {
				e.printStackTrace();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void verifySignatureFromSpecificFields() throws GeneralSecurityException, IOException {
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
