package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OpenRTB {
  private String ver;
  private String domainspec;
  @NotEmpty
  private String domainver;
  @NotNull
  @Valid
  private Request request;
  private Response response;

}
