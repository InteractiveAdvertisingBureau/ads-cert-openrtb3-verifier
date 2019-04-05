package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restrictions {

  private String[] bcat;
  private Integer cattax;
  private String[] badv;
  private String[] bapp;
  private Integer[] battr;
  private Ext ext;

}
