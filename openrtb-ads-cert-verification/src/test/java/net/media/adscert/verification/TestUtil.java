package net.media.adscert.verification;

import net.media.adscert.models.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

	public static OpenRTB getOpenRTBObject() {
		OpenRTB openRTB = new OpenRTB();
		final Request request = new Request();
		openRTB.setRequest(request);
		final Source source = new Source();
		request.setSource(source);
		source.setTid("ABC7E92FBD6A");
		Item item = new Item();
		final Spec spec = new Spec();
		spec.setPlacement(new Placement());
		item.setSpec(spec);
		request.setItem(Arrays.asList(item));
		source.setDsmap("domain=&ft=&tid=");
		source.setDigest("domain=newsite.com&ft=d&tid=ABC7E92FBD6A");
		source.setTs(System.currentTimeMillis());
		request.setContext(new Context());
		request.getContext().setSite(new Site());
		request.getContext().getSite().setDomain("newsite.com");
		return openRTB;
	}

	public static Map<String, Object> getMapOfDigestFields() {
		Map<String, Object> digestFields = new HashMap<>();
		digestFields.put("domain", "newsite.com");
		digestFields.put("ft", "d");
		digestFields.put("tid", "ABC7E92FBD6A");
		return digestFields;
	}

}
