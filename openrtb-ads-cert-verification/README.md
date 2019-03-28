##Verification Service for Signed Bid Requests

Read about Ads.Cert - Signed Bid Requests here: [IAB Ads.Cert](https://github.com/InteractiveAdvertisingBureau/openrtb/blob/master/ads.cert:%20Signed%20Bid%20Requests%201.0%20BETA.md)

This service is intended to be used in verification of the said requests by checking whether the signature provided in the request is not forged or modified.

###Usage

Instantiate an object of VerificationService to access the methods used to verify the request. The class VerificationService is thread-safe so it is recommended to use it as a singleton. 

Two more implementations are provided for VerificationService which supports the use of caches such as JCache and Guava. The caches store the PublicKey present at a given url. The corresponding classes for these caches are VerificationServiceJCache and VerificationServiceGuavaCache. 

Additionally, default implementations for both caches are also provided. You can either use them or pass your own cache object to the constructor.

**JCache:**

```
Cache<String, PublicKey> cache = DefaultJCacheBuilder.newBuilder()
                                       .setExpiryForAccess(...)
                                       .setExpiryForCreation(...)
                                       .setExpiryForUpdate(...)
                                       .setCacheLoader(...)
                                       .build();
                                       
VerificationServiceJCache service = new VerificationServiceJCache(cache);
```

**Guava:**

```
Cache<String, PublicKey> cache = DefaultGuavaCacheBuilder.newBuilder()
                                        .setMaximumSize(...)
                                        .setExpireAfterAccess(...)
                                        .setExpireAfterWrite(...)
                                        .build();

VerificationServiceGuavaCache service = new VerificationServiceGuavaCache(cache)
```

Both the default cache builders have default values for fields so it is not necessary to set the values. For example, you can write ``DefaultGuavaCacheBuilder.newBuilder().build()`` and it will return a cache with default parameters.

The default values are listed below:


##Requirements
1. Java 8


