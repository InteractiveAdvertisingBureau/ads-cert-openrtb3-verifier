# Verification Service for Signed Bid Requests

Ad Fraud has always been a big problem in the ad industry. Inventory spoofing is a problem that still exists where a request can be modified by any entity in the supply chain to pose it as premium inventory. This is the problem which ads.cert tries to solve.

## How ads.cert works?

 - The publisher or the signing authority maintains the private key
 - A small set of essential fields (DsMap) and values in the request is used to generate the digest
 - The Signing Service generates the Digital Signature (ds) using the digest and the private key
 - The request is sent to the Exchanges/DSPs including the Digital Signature, DsMap and other fields
 - The Signature Verification Service creates a new digest from the request it receives using the fields present in the DsMap and the respective values
 - The digest and the public key (hosted on publisher domain) are then used to verify the Digital Signature present in the OpenRTB request using ECDSA SHA 256 algorithm

![N|Solid](openrtb-ads-cert-verification/flow.png)

Read about Ads.Cert - Signed Bid Requests here: [IAB Ads.Cert](https://github.com/InteractiveAdvertisingBureau/openrtb/blob/master/ads.cert:%20Signed%20Bid%20Requests%201.0%20BETA.md)

## Goal of this library:

To allow for fast-track on-boarding for ads.cert, media.net is offering the verification service as an open-source solution. Following are the features supported:

 - Digital Signature Verification (via OpenRTB 3.0 object or Digest or Map of key-values)
 - Sampling
 - Message Expiry checks
 - Offline Verification
 - Reporting hooks
 - In-memory caching to minimize latencies

## Usage

Refer [README.md](openrtb-ads-cert-verification/README.md).