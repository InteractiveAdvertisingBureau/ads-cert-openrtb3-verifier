package net.media.adscert.verification;

import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Request;
import net.media.adscert.models.Source;

import java.util.HashMap;
import java.util.Map;

public class TestUtil {

	public static OpenRTB getOpenRTBObject() {
		OpenRTB openRTB = new OpenRTB();
		openRTB.setRequest(new Request());
		openRTB.getRequest().setSource(new Source());
		openRTB.getRequest().getSource().setDsmap("domain=&ft=&tid=");
		openRTB.getRequest().getSource().setDigest("domain=newsite.com&ft=d&tid=ABC7E92FBD6A");
		return openRTB;
	}

	public static Map<String, String> getMapOfDigestFields() {
		Map<String, String> digestFields = new HashMap<>();
		digestFields.put("domain", "newsite.com");
		digestFields.put("ft", "d");
		digestFields.put("tid", "ABC7E92FBD6A");
		return digestFields;
	}

}
