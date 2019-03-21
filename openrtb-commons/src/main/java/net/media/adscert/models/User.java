package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;
import net.media.adscert.models.validator.CheckAtLeastOneNotNull;

@Getter
@Setter
@CheckAtLeastOneNotNull(fieldNames = {"id", "buyeruid"})
public class User {
  private String id;
  private String buyeruid;
  private Integer yob;
  private String gender;
  private String keywords;
  private String consent;
  private Geo geo;
  private Data[] data;
  private Ext ext;
}
