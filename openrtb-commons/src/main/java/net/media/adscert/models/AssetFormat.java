package net.media.adscert.models;

import net.media.adscert.models.validator.CheckExactlyOneNotNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by shiva.b on 14/12/18.
 */
@CheckExactlyOneNotNull(fieldNames = {"title", "img", "video", "data"})
public class AssetFormat {
  @NotNull
  private Integer id;
  private Integer req;
  @Valid
  private TitleAssetFormat title;
  private ImageAssetFormat img;
  @Valid
  private VideoPlacement video;
  @Valid
  private DataAssetFormat data;
  private Object ext;
}
