package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Metric {
  @NotEmpty
  private String type;
  @NotNull
  private Double value;
  private String vendor;
  private Object ext;
}
