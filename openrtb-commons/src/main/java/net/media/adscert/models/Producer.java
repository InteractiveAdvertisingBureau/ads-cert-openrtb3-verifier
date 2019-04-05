package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Producer {
  @NotNull
  private String id;
  private String name;
  private String domain;
  private String[] cat;
  private Integer cattax;
  private Ext	ext;

}
