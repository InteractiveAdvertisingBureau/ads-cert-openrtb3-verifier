package net.media.adscert.models;

import javax.validation.constraints.NotNull;

/**
 * Created by shiva.b on 14/12/18.
 */
public class DataAssetFormat {
  @NotNull
  private Integer type;
  private Integer len;
  private Object ext;
}
