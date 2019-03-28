package net.media.adscert.verification.cache;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.verification.service.VerificationService;

import java.security.PublicKey;
import java.util.Map;

public abstract class VerificationServiceWithCache extends VerificationService {

	public VerificationServiceWithCache() {
		super();
	}

	protected abstract PublicKey getKeyFromCache(String url) throws ProcessException;

	@Override
	public Boolean verifyRequest(String cert,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (cert == null || cert.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}

		try {
			return verifyRequest(getKeyFromCache(cert), dsMap, ds, digest, digestFields);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
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
