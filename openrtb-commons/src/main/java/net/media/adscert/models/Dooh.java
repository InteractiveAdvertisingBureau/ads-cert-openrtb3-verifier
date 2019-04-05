package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dooh extends DistributionChannel {

  private Integer venue;
  private Integer fixed;
  private Integer etime;
  private Integer dpi;
  private Ext ext;
}
