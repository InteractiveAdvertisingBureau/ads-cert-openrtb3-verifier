package net.media.adscert.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Request {
  @NotEmpty
  private String id;
  private Integer test;
  private Integer tmax;
  private Integer at;
  private String[] cur;
  private Set<String> seat;
  private Integer wseat;
  private String cdata;
  @NotNull
  @Valid
  private Source source;
  @NotEmpty
  @Valid
  private List<Item> item;
  @JsonProperty("package")
  private Integer pack;
  @NotNull
  @Valid
  private Context context;
  private Object ext;
}
