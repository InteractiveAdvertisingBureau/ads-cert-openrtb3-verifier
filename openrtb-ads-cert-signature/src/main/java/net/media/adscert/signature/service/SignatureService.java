package net.media.adscert.signature.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.DigestUtil;
import net.media.adscert.utils.SignatureUtil;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Map;

public class SignatureService {

	private PrivateKey privateKey;
	private String publicKeyUrl;

	public SignatureService(PrivateKey privateKey, String publicKeyUrl) {
		this.privateKey = privateKey;
		this.publicKeyUrl = publicKeyUrl;
	}

	public SignatureService(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public String generateSignature(String dsMap, Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (dsMap == null) {
			throw new InvalidDataException("dsMap is empty");
		}
		if (digestFields == null) {
			throw new InvalidDataException("Values for digest fields not provided");
		}

		try {
			String digest = DigestUtil.getDigestFromDsMap(dsMap, digestFields);
			return SignatureUtil.signMessage(privateKey, digest);
		} catch (Exception e) {
			throw new ProcessException("Error in signing", e);
		}
	}

	public String generateSignature(OpenRTB openRTB) throws InvalidDataException, ProcessException {
		return generateSignature(openRTB, false);
	}

	public String generateSignature(OpenRTB openRTB, Boolean debug) throws InvalidDataException, ProcessException {
		if(openRTB == null || openRTB.getRequest() == null) {
			throw new InvalidDataException("OpenRtb.request: may not be null");
		}
		if(openRTB.getRequest().getSource() == null) {
			openRTB.getRequest().setSource(new Source());
		}
		if(openRTB.getRequest().getSource().getCert() == null) {
			openRTB.getRequest().getSource().setCert(publicKeyUrl);
		}
		if(openRTB.getRequest().getSource().getTid() == null) {
			openRTB.getRequest().getSource().setTid(Instant.now().toString());
		}

		try {
			String digest = debug
					? DigestUtil.getDigest(openRTB)
					: DigestUtil.getDigestFromOpenRtb(openRTB);

			return SignatureUtil.signMessage(privateKey, digest);
		} catch (Exception e) {
			throw new ProcessException("Error in signing", e);
		}
	}

}
