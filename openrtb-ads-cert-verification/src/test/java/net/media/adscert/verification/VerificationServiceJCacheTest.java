package net.media.adscert.verification;

import net.media.adscert.verification.cache.DefaultJCacheBuilder;
import net.media.adscert.verification.cache.VerificationServiceJCache;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.expiry.Duration;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

public class VerificationServiceJCacheTest {

  @Test
  public void test() {
    VerificationServiceJCache service = new VerificationServiceJCache(DefaultJCacheBuilder.newBuilder()
      .setExpiryForAccess(new Duration(TimeUnit.MILLISECONDS, 100))
      .setExpiryForCreation(new Duration(TimeUnit.MILLISECONDS, 100))
      .setExpiryForUpdate(new Duration(TimeUnit.MILLISECONDS, 100)).build());
  }

}
