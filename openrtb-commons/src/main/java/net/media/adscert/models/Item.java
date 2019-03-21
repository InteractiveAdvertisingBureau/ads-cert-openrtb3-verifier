package net.media.adscert.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class Item {
  @NotEmpty
  private String id;
  private Integer qty;
  private Integer seq;
  private double flr;
  private String flrcur;
  private Integer exp;
  private Integer dt;
  private Integer dlvy;
  private List<Metric> metric;
  private List<Deal> deal;
  @JsonProperty("private")
  private Integer priv;
  @NotNull
  @Valid
  private Spec spec;
  private Object ext;
}
