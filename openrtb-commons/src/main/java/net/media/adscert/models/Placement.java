package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;
import net.media.adscert.models.validator.CheckAtLeastOneNotNull;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@CheckAtLeastOneNotNull(fieldNames={"display", "video", "audio"})
public class Placement {
  private String tagid;
  private Integer ssai;
  private String sdk;
  private String sdkver;
  private Integer reward;
  private List<String> wlang;
  private Integer secure;
  private Integer admx;
  private Integer curlx;
  @Valid
  private DisplayPlacement display;
  @Valid
  private VideoPlacement video;
  @Valid
  private AudioPlacement audio;
  private Object ext;
}
