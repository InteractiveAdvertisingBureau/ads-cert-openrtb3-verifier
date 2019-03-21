package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class App extends DistributionChannel {

  private String domain;
  private String[] cat;
  private String[] sectcat;
  private String[] pagecat;
  private Integer cattax;
  private Integer privpolicy;
  private String keywords;
  private String bundle;
  private String storeid;
  private String storeurl;
  private String ver;
  private Integer paid;
  private Ext ext;
}
