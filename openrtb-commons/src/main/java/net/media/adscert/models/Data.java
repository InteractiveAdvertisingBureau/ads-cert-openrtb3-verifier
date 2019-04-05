package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Data {

  private String id;
  private String name;
  private Segment[] segment;
  private Ext ext;

}
