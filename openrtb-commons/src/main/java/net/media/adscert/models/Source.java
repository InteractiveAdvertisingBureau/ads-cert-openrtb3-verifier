package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class Source {
  @NotEmpty
  private String tid;
  private Long ts;
  @NotEmpty
  private String ds;
  private String dsmap;
  @NotEmpty
  private String cert;
  private String digest;
  private String pchain;
  private Ext ext;
}
