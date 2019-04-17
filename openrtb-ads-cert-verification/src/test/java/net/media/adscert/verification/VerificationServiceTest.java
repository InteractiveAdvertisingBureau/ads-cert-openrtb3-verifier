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

import net.media.adscert.models.OpenRTB;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.JacksonObjectMapper;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.enums.Result;
import net.media.adscert.verification.metrics.MetricsManager;
import net.media.adscert.verification.service.FileVerificationService;
import net.media.adscert.verification.service.VerificationService;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class VerificationServiceTest {
  private ClassLoader classLoader = getClass().getClassLoader();

  @Test
  public void verifySignatureFromOpenRTBObject()
      throws GeneralSecurityException, InterruptedException {
    VerificationService verificationService = new VerificationService(100, 500l);
    OpenRTB openRTB = TestUtil.getOpenRTBObject();
    KeyPair keyPair = TestUtil.generateKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();

    String digest = DigestUtil.getDigest(openRTB);
    openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));

    assertTrue(
        verificationService.verifyRequest(openRTB, true, publicKey, false).getStatus()
            == Result.Status.SUCCESS);

    Thread.sleep(600l);

    assertTrue(
        verificationService.verifyRequest(openRTB, true, publicKey, true).getStatus()
            == Result.Status.FAILURE);
    assertTrue(
        verificationService.verifyRequest(openRTB, false, publicKey, false).getStatus()
            == Result.Status.SUCCESS);
  }

  @Test
  public void verifySignatureFromFile()
      throws GeneralSecurityException {
    FileVerificationService verificationService = new FileVerificationService();
    OpenRTB openRTB = TestUtil.getOpenRTBObject();
    OpenRTB openRTB1 = TestUtil.getOpenRTBObject();
    KeyPair keyPair = TestUtil.generateKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();

    String digest = "domain=newsite.com&ft=d&tid=ABC7E92FBD6A";
    openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(privateKey, digest));
    openRTB1.getRequest().getSource().setDs("abcdef");
    String inputFilePath =
        classLoader.getResource("request").getPath() + "/request30ForFileTesting.json";
    String outputFilePath =
        classLoader.getResource("request").getPath() + "/resultFileTesting.json";
    File inputFile = new File(inputFilePath);
    try {
      FileOutputStream outputStream = new FileOutputStream(inputFile);
      outputStream.write(
          (JacksonObjectMapper.getMapper().writeValueAsString(openRTB)
                  + System.getProperty("line.separator")
                  + JacksonObjectMapper.getMapper().writeValueAsString(openRTB1))
              .getBytes());

      verificationService.verify(inputFilePath, outputFilePath, publicKey);
      assertTrue(true);
      List<String> list = new ArrayList<>();

      try (BufferedReader br = Files.newBufferedReader(Paths.get(outputFilePath))) {

        // br returns as stream and convert it into a List
        list = br.lines().collect(Collectors.toList());
        Assert.assertEquals("SUCCESS", list.get(0));
        Assert.assertNotEquals("SUCCESS", list.get(1));
      } catch (IOException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void verifySignatureFromSpecificFields()
      throws GeneralSecurityException {
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
    VerificationService verificationService = new VerificationService(100, 1000l, metricsManager);
    KeyPair keyPair = TestUtil.generateKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();

    String dsMap = "domain=&ft=&tid=";
    String digest = "domain=newsite.com&ft=d&tid=ABC7E92FBD6A";
    String ds = SignatureUtil.signMessage(privateKey, digest);

    assertTrue(
        verificationService
                .verifyRequest(publicKey, dsMap, ds, TestUtil.getMapOfDigestFields())
                .getStatus()
            == Result.Status.SUCCESS);
    // assertEquals(true, verificationService.verifyRequest(publicKey, ds, digest));
  }
}
