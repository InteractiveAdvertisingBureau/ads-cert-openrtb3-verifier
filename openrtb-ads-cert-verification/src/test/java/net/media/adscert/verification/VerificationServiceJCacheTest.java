package net.media.adscert.verification;

import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.cache.DefaultJCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceJCache;
import org.junit.Assert;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.expiry.Duration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerificationServiceJCacheTest {

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
  public void test() throws NoSuchAlgorithmException, InterruptedException, SignatureException, InvalidKeyException {
    final KeyPair keyPair1 = SignatureUtil.generateKeyPair();
    final KeyPair keyPair2 = SignatureUtil.generateKeyPair();

    VerificationServiceJCache service = new VerificationServiceJCache(DefaultJCacheBuilder.newBuilder()
      .setExpiryForAccess(new Duration(TimeUnit.MILLISECONDS, 100))
      .setExpiryForCreation(new Duration(TimeUnit.MILLISECONDS, 100))
      .setExpiryForUpdate(new Duration(TimeUnit.MILLISECONDS, 100))
      .setCacheLoader(new CacheLoader<String, PublicKey>() {
        int count = 0;
        @Override
        public PublicKey load(String key) throws CacheLoaderException {
          if(count == 0) {
            ++count;
            return keyPair1.getPublic();
          } else {
            ++count;
            return keyPair2.getPublic();
          }
        }

        @Override
        public Map<String, PublicKey> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
          return null;
        }
      }).build());
    OpenRTB openRTB = getOpenRTBObject();
    openRTB.getRequest().getSource().setCert("http://www.blahblahblah.com");
    String digest = DigestUtil.getDigest(openRTB);
    openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair1.getPrivate(), digest));

    Assert.assertTrue(service.verifyRequest(openRTB, true));
    Thread.sleep(560);

    openRTB.getRequest().getSource().setDs(SignatureUtil.signMessage(keyPair2.getPrivate(), digest));
    Assert.assertTrue(service.verifyRequest(openRTB, true));

  }

}
