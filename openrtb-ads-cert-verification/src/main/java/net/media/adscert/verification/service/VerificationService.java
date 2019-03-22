package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.utils.DigestUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;

public class VerificationService {

	public VerificationService() {

	}

	public Boolean verifyRequest(OpenRTB openRTB) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false);
	}

	public Boolean verifyRequest(String cert,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(cert, dsMap, ds, null, digestFields);
	}

	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(publicKey, dsMap, ds, null, digestFields);
	}

	public Boolean verifyRequest(String cert,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (cert == null || cert.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		try {
			PublicKey pub = SignatureUtil.getPublicKeyFromUrl(cert);
			return verifyRequest(pub, dsMap, ds, digest, digestFields);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}

	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (digest == null && digestFields == null) {
			throw new InvalidDataException("Digest Field Map is null");
		}
		if (publicKey == null) {
			throw new InvalidDataException("Public Key cannot be null");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (dsMap == null || dsMap.length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			digest = digest == null
					? DigestUtil.getDigestFromDsMap(dsMap, digestFields)
					: digest;

			return SignatureUtil.verifySign(publicKey, digest, ds);
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, debug, null);
	}

	public Boolean verifyRequest(OpenRTB openRTB,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false, publicKey);
	}

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
					? SignatureUtil.getPublicKeyFromUrl(source.getCert())
					: publicKey;

			return SignatureUtil.verifySign(publicKey, digest, source.getDs());
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		PublicKey publicKey = SignatureUtil.getPublicKey("/home/pranava/Desktop/public.txt");
		VerificationService verificationService = new VerificationService();
		OpenRTB openRTB = new OpenRTB();
		openRTB.setRequest(new Request());
		openRTB.getRequest().setSource(new Source());
		openRTB.getRequest().getSource().setDsmap("domain=&ft=&tid=");
		openRTB.getRequest().getSource().setDigest("domain=newsite.com&ft=d&tid=ABC7E92FBD6A");
		openRTB.getRequest().getSource().setDs("MEQCIEkGv182K5Sq7xgiDalvpyPMh9WUhd3e9nWcide4rfqoAiARjz0TgSmIZGcK4gBTVNl3lcU1DKr1p7A4Vwj01G2BKA==");
		System.out.println(verificationService.verifyRequest(openRTB, true, publicKey));
	}

}
