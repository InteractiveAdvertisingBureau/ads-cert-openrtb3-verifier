package net.media.adscert.verification.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.models.OpenRTB;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.media.adscert.verification.utils.CommonConstants.KEY_VALUE_SEPERATOR;
import static net.media.adscert.verification.utils.CommonConstants.QUERY_PARAM_SEPERATOR;

public class AdCertVerification {
  private static final Map<String, Function<OpenRTB, String>> digestMap = new LinkedHashMap<>();
  private static final Splitter queryParamSplitter = Splitter.on(QUERY_PARAM_SEPERATOR).trimResults().omitEmptyStrings();
  private static final Splitter keyValueSplitter = Splitter.on(KEY_VALUE_SEPERATOR).trimResults().omitEmptyStrings();
  private static final Joiner queryParamJoiner = Joiner.on(QUERY_PARAM_SEPERATOR).skipNulls();

  static {
    digestMap.put("bundle", t -> t.getRequest().getContext().getApp().getBundle());
    digestMap.put("cert", t -> t.getRequest().getSource().getCert());
    digestMap.put("domain", t -> t.getRequest().getContext().getSite().getDomain());

    digestMap.put("ft", t -> {
      Set<String> fts = t.getRequest().getItem().stream()
        .map(i -> i.getSpec().getPlacement())
        .map(p -> {
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
      if(fts.contains("v")) res += "v";
      if(fts.contains("d")) res += "d";
      if(fts.contains("a")) res += "a";
      return res;
    });

    digestMap.put("h", t -> t.getRequest().getItem().stream()
      .map(i -> i.getSpec().getPlacement().getVideo())
      .filter(Objects::nonNull)
      .findFirst().map(videoPlacement -> videoPlacement.getW().toString()).orElse(""));

    digestMap.put("ifa", t -> t.getRequest().getContext().getDevice().getIfa());
    digestMap.put("ip", t -> t.getRequest().getContext().getDevice().getIp());
    digestMap.put("ipv6", t -> t.getRequest().getContext().getDevice().getIpv6());

    digestMap.put("tid", t -> t.getRequest().getSource().getTid());
    digestMap.put("ts", t -> t.getRequest().getSource().getTs().toString());
    digestMap.put("ua", t -> t.getRequest().getContext().getDevice().getUa());
    digestMap.put("w", t -> t.getRequest().getItem().stream()
      .map(i -> i.getSpec().getPlacement().getVideo())
      .filter(Objects::nonNull)
      .findFirst().map(videoPlacement -> videoPlacement.getW().toString()).orElse(""));
  }

  public static String getDigest(OpenRTB openRtb) throws InvalidDataException {
    if(openRtb.getRequest().getSource().getDigest() == null) {
      throw new InvalidDataException("OpenRtb.source.digest: may not be null");
    }
    return openRtb.getRequest().getSource().getDigest();
  }

  public static String getDigestFromDsMap(OpenRTB openRtb) throws InvalidDataException {
    try {
      return queryParamJoiner.join(queryParamSplitter.splitToList(openRtb.getRequest().getSource().getDsmap()).stream()
        .map(key -> key + digestMap.get(key.substring(0, key.length()-1)).apply(openRtb))
        .collect(Collectors.toList()));
    } catch (Exception e) {
      throw new InvalidDataException("OpenRtb.source.dsmap: bad dsmap provided", e);
    }
  }

  public static String getDigestFromDsMap(String dsMap, Map<String, String> digestFields) throws InvalidDataException {
    try {
      return queryParamJoiner.join(queryParamSplitter.splitToList(dsMap).stream()
          .filter(key -> {
            if (!digestFields.containsKey(key.substring(0, key.length() - 1)))
              throw new InvalidDataException("Bad dsmap provided");
            return true;
          })
          .map(key -> key + digestFields.get(key.substring(0, key.length() - 1)))
          .collect(Collectors.toList()));
    } catch (Exception e) {
      throw new InvalidDataException("Bad dsmap provided", e);
    }
  }

  public static String getDigestFromOpenRtb(OpenRTB openRtb) throws InvalidDataException {
    if(openRtb.getRequest().getSource().getDsmap() != null) {
      return getDigestFromDsMap(openRtb);
    }
    StringBuilder dsMapBuilder = new StringBuilder();
    StringBuilder digestBuilder = new StringBuilder();
    digestMap.forEach((k,v) -> {
      try {
        String val = v.apply(openRtb);
        if (!val.isEmpty()) {
          dsMapBuilder.append(k).append("=&");
          digestBuilder.append(k).append("=").append(val).append("&");
        }
      } catch (NullPointerException e) {

      }
    });
    String dsMap = dsMapBuilder.substring(0, dsMapBuilder.length() - 1);
    openRtb.getRequest().getSource().setDsmap(dsMap);
    return digestBuilder.substring(0, digestBuilder.length() - 1);
  }

//  public static void signSource(OpenRTB openRtb, String fileName) throws IOException, GeneralSecurityException {
//    openRtb.getRequest().getSource().setTid(Instant.now().toString());
//    StringBuilder dsMapBuilder = new StringBuilder();
//    StringBuilder digestBuilder = new StringBuilder();
//    digestMap.forEach((k,v) -> {
//      try {
//        String val = v.apply(openRtb);
//        if (!val.isEmpty()) {
//          dsMapBuilder.append(k).append("=&");
//          digestBuilder.append(k).append("=").append(val).append("&");
//        }
//      } catch (NullPointerException e) {
//
//      }
//    });
//
//    PrivateKey priv = ECDSAUtil.getPrivateKey(fileName);
//    String dsMap = dsMapBuilder.substring(0, dsMapBuilder.length() - 1);
//    String digest = digestBuilder.substring(0, digestBuilder.length() - 1);
//    openRtb.getRequest().getSource().setDsmap(dsMap);
//    openRtb.getRequest().getSource().setDs(ECDSAUtil.signMessage(priv, digest));
//  }

}
