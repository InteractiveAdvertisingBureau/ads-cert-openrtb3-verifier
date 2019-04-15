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

package net.media.adscert.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.models.OpenRTB;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DigestUtil {
  private static final LinkedHashMap<String, Function<OpenRTB, Object>> digestMap =
      new LinkedHashMap<>();
  private static final Splitter queryParamSplitter =
      Splitter.on(CommonConstants.QUERY_PARAM_SEPERATOR).trimResults().omitEmptyStrings();
  private static final Splitter keyValueSplitter =
      Splitter.on(CommonConstants.KEY_VALUE_SEPERATOR).trimResults().omitEmptyStrings();
  private static final Joiner queryParamJoiner =
      Joiner.on(CommonConstants.QUERY_PARAM_SEPERATOR).skipNulls();

  static {
    digestMap.put("bundle", t -> t.getRequest().getContext().getApp().getBundle());
    digestMap.put("cert", t -> t.getRequest().getSource().getCert());
    digestMap.put("domain", t -> t.getRequest().getContext().getSite().getDomain());

    digestMap.put(
        "ft",
        t -> {
          Set<String> fts =
              t.getRequest()
                  .getItem()
                  .stream()
                  .map(i -> i.getSpec().getPlacement())
                  .map(
                      p -> {
                        if (p.getVideo() != null) {
                          return "v";
                        }
                        if (p.getAudio() != null) {
                          return "a";
                        }
                        return "d";
                      })
                  .collect(Collectors.toSet());
          String res = "";
          if (fts.contains("v")) res += "v";
          if (fts.contains("d")) res += "d";
          if (fts.contains("a")) res += "a";
          return res;
        });

    digestMap.put(
        "h",
        t ->
            t.getRequest()
                .getItem()
                .stream()
                .map(i -> i.getSpec().getPlacement().getVideo())
                .filter(Objects::nonNull)
                .findFirst()
                .map(videoPlacement -> videoPlacement.getW().toString())
                .orElse(""));

    digestMap.put("ifa", t -> t.getRequest().getContext().getDevice().getIfa());
    digestMap.put("ip", t -> t.getRequest().getContext().getDevice().getIp());
    digestMap.put("ipv6", t -> t.getRequest().getContext().getDevice().getIpv6());

    digestMap.put("tid", t -> t.getRequest().getSource().getTid());
    digestMap.put("ts", t -> t.getRequest().getSource().getTs().toString());
    digestMap.put("ua", t -> t.getRequest().getContext().getDevice().getUa());
    digestMap.put(
        "w",
        t ->
            t.getRequest()
                .getItem()
                .stream()
                .map(i -> i.getSpec().getPlacement().getVideo())
                .filter(Objects::nonNull)
                .findFirst()
                .map(videoPlacement -> videoPlacement.getW().toString())
                .orElse(""));
  }

  public static String getDigest(OpenRTB openRtb) throws InvalidDataException {
    if (openRtb.getRequest().getSource().getDigest() == null) {
      throw new InvalidDataException("OpenRtb.source.digest: may not be null");
    }
    return openRtb.getRequest().getSource().getDigest();
  }

  public static Map<String, Object> getDigestFromDsMap(OpenRTB openRtb)
      throws InvalidDataException {
    return getDigestFromDsMap(openRtb, digestMap);
  }

  public static Map<String, Object> getDigestFromDsMap(
      OpenRTB openRtb, LinkedHashMap<String, Function<OpenRTB, Object>> digestMap)
      throws InvalidDataException {
    try {
      return queryParamSplitter
          .splitToList(openRtb.getRequest().getSource().getDsmap())
          .stream()
          .collect(
              Collectors.toMap(
                  key -> key.substring(0, key.length() - 1),
                  key -> digestMap.get(key.substring(0, key.length() - 1)).apply(openRtb)));
      /*return queryParamJoiner.join(queryParamSplitter.splitToList(openRtb.getRequest().getSource().getDsmap()).stream()
      .map(key -> key + digestMap.get(key.substring(0, key.length()-1)).apply(openRtb))
      .collect(Collectors.toList()));*/
    } catch (Exception e) {
      throw new InvalidDataException("OpenRtb.source.dsmap: bad dsmap provided", e);
    }
  }

  public static String getDigestFromDsMap(String dsMap, Map<String, Object> digestFields)
      throws InvalidDataException {
    try {
      return queryParamJoiner.join(
          queryParamSplitter
              .splitToList(dsMap)
              .stream()
              .filter(
                  key -> {
                    if (!digestFields.containsKey(key.substring(0, key.length() - 1))) {
                      throw new InvalidDataException("Bad dsmap provided");
                    }
                    return true;
                  })
              .map(key -> key + digestFields.get(key.substring(0, key.length() - 1)))
              .collect(Collectors.toList()));
    } catch (Exception e) {
      throw new InvalidDataException("Bad dsmap provided", e);
    }
  }
}
