package net.media.adscert.verification.service;

import com.google.common.cache.Cache;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class VerificationServiceGuavaCache extends VerificationService {

	private Cache<String, PublicKey> publicKeyCache;

	public VerificationServiceGuavaCache(Cache<String, PublicKey> publicKeyCache) {
		super();
		this.publicKeyCache = publicKeyCache;
	}

	private PublicKey getKeyFromCache(String url) throws ExecutionException {
		return this.publicKeyCache.get(url, new Callable<PublicKey>() {
			@Override
			public PublicKey call() {
				try {
					return SignatureUtil.getPublicKeyFromUrl(url);
				} catch (Exception e) {
					throw new InvalidDataException("Unable to fetch key data from url provided.", e);
				}
			}
		});
	}

	@Override
	public Boolean verifyRequest(String cert,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (cert == null || cert.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}

		PublicKey pub;

		try {
			pub = getKeyFromCache(cert);
		} catch (Exception e) {
			throw new ProcessException(e);
		}

		return verifyRequest(pub, dsMap, ds, digest, digestFields);
	}

	@Override
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		if (openRTB == null) {
			throw new InvalidDataException("OpenRTB object is null");
		}

		Source source = openRTB.getRequest().getSource();

		if (publicKey == null && (source.getCert() == null || source.getCert().length() == 0)) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		if (source.getDs() == null || source.getDs().length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (source.getDsmap() == null || source.getDsmap().length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			String digest = debug
					? DigestUtil.getDigest(openRTB)
					: DigestUtil.getDigestFromDsMap(openRTB);


			publicKey = publicKey == null
					? getKeyFromCache(source.getCert())
					: publicKey;

			return SignatureUtil.verifySign(publicKey, digest, source.getDs());
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}
}
