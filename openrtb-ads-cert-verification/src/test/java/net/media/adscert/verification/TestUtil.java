package net.media.adscert.verification;

import net.media.adscert.models.*;
import net.media.adscert.utils.JacksonObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {
	private ClassLoader classLoader = getClass().getClassLoader();
	public OpenRTB getOpenRTBObject() {

		File inputFile = new File(classLoader.getResource("request").getPath()+"/request30.json");
		byte[] jsonData = new byte[0];
		try {
			jsonData = Files.readAllBytes(inputFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OpenRTB openRTB = null;
		try {
			openRTB = JacksonObjectMapper.getMapper().readValue(jsonData, OpenRTB.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Source source = new Source();
		openRTB.getRequest().setSource(source);
		source.setTid("ABC7E92FBD6A");
		Item item = new Item();
		final Spec spec = new Spec();
		spec.setPlacement(new Placement());
		item.setSpec(spec);
		openRTB.getRequest().setItem(Arrays.asList(item));
		source.setDsmap("domain=&ft=&tid=");
		source.setDigest("domain=newsite.com&ft=d&tid=ABC7E92FBD6A");
		source.setTs((int)System.currentTimeMillis());
		openRTB.getRequest().setContext(new Context());
		openRTB.getRequest().getContext().setSite(new Site());
		openRTB.getRequest().getContext().getSite().setDomain("newsite.com");
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
