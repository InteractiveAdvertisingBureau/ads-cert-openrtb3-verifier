package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class DisplayPlacement {
  private Integer pos;
  private Integer instl;
  private Integer topframe;
  private List<String> ifrbust;
  private Integer clktype;
  private Integer ampren;
  private Integer ptype;
  private String mime;
  private Integer[] api;
  private Integer[] ctype;
  private Integer w;
  private Integer h;
  private Integer unit;
  private Integer priv;
  private DisplayFormat displayfmt;
  @Valid
  private NativeFormat nativefmt;
  private EventSpec event;
  private Object ext;
}