package net.media.adscert.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by shiva.b on 14/12/18.
 */
public class NativeFormat {
  @NotNull
  @Valid
  public AssetFormat[] asset;
  public Object ext;
}
