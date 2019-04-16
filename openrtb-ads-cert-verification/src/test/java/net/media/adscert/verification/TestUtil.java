/*
 * Copyright © 2019 - present. MEDIA.NET ADVERTISING FZ-LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  public static Map<String, Object> getMapOfDigestFields() {
    Map<String, Object> digestFields = new HashMap<>();
    digestFields.put("domain", "newsite.com");
    digestFields.put("ft", "d");
    digestFields.put("tid", "ABC7E92FBD6A");
    return digestFields;
  }

  public OpenRTB getOpenRTBObject() {

    File inputFile = new File(classLoader.getResource("request").getPath() + "/request30.json");
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
    source.setTs((int) System.currentTimeMillis());
    openRTB.getRequest().setContext(new Context());
    openRTB.getRequest().getContext().setSite(new Site());
    openRTB.getRequest().getContext().getSite().setDomain("newsite.com");
    return openRTB;
  }
}
