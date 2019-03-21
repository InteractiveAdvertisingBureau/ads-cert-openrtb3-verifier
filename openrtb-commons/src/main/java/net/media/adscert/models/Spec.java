package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by rajat.go on 14/12/18.
 */

@Getter
@Setter
public class Spec {
  @NotNull
  @Valid
  private Placement placement;
}
