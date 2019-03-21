package net.media.adscert.verification.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.utils.ECDSAUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;

public class PublicKeyManager {
  private Cache<String, PublicKey> pubKeyCache;

  public PublicKeyManager(Long cacheMaxSize) {
    this.pubKeyCache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();
  }

  public PublicKey getKey(String url) throws ExecutionException {
    return pubKeyCache.get(url, () -> {
      try {
        return ECDSAUtil.getPublicKeyFromUrl(url);
      } catch (IOException e) {
        throw new InvalidDataException("Unable to fetch key data from url provided.");
      } catch (GeneralSecurityException gse) {
        throw new InvalidDataException("Unable to parse key data from url provided.");
      }
    });
  }

  public void refresh() {
    pubKeyCache.asMap().forEach((k,v) -> {
      try {
        PublicKey newV = ECDSAUtil.getPublicKeyFromUrl(k);
        pubKeyCache.put(k, newV);
      } catch (Exception e) {
        pubKeyCache.invalidate(k);
      }
    });
    pubKeyCache.cleanUp();
  }
}
