##Verification Service for Signed Bid Requests

Read about Ads.Cert - Signed Bid Requests here: [IAB Ads.Cert](https://github.com/InteractiveAdvertisingBureau/openrtb/blob/master/ads.cert:%20Signed%20Bid%20Requests%201.0%20BETA.md)

This service is intended to be used for verification of the digital signature in ORTB requests by checking whether the signature provided is forged or not.

###Usage

Instantiate an object of ``` VerificationService ``` to access the methods for verifying the request. The class VerificationService is thread-safe so it is recommended to be used as a singleton. Furthermore, optionally, a sampling percentage can be provided while instantiation to control the percentage of requests for which such a verification is desired. The default value of sampling rate is 100, which means that all requests will be verified. 

Two more implementations are provided for VerificationService which supports the use of caches such as JCache and Guava. The caches store the PublicKey present at a given url. The corresponding classes are ``` VerificationServiceJCache ``` and ``` VerificationServiceGuavaCache ```. 

Additionally, default implementation for both caches are also provided. You can either use them or pass your own cache object to the constructor.

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

Both the default cache builders have default values set for fields. So it is not necessary to set the values. For example, you can write ``` DefaultGuavaCacheBuilder.newBuilder().build() ``` 
and it will return a cache created with parameters set to default values.

Finally, a support has been provided to check message expiry. The timestamp in ORTB is assumed to be the time elapsed since UTC epoch. If the difference between timestamp in the ORTB request and current system timestamp exceeds by a pre-defined margin, the service can fail the verification.
```
new VerificationService(100, 2000l).verifyRequest(OpenRTB openRTB, Boolean debug, PublicKey publicKey,  sboolean checkMessageExpiry
```

**Bulk verification**

Bulk verification can be performed by passing path to the input file containing JSONs of OpenRTB requests (each line has complete json of one request), along with the path to the file to which output should be written

```
FileVerificationService.verify("input.txt", "output.txt");
```

##Requirements
Java 8


