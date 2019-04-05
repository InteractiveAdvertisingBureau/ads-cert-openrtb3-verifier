package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class Deal {
  @NotEmpty
  private String id;
  private Double flr;
  private String flrcur;
  private Integer at;
  private List<String> wseat;
  private String[] wadomain;
  private Object ext;
}
