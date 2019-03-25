package net.media.adscert.models;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class OpenRTB {
  private String ver;
  private String domainspec;
  @NotEmpty
  private String domainver;
  @NotNull
  @Valid
  private Request request;
  private Response response;

  public String getVer() {
    return this.ver;
  }

  public String getDomainspec() {
    return this.domainspec;
  }

  public @NotEmpty String getDomainver() {
    return this.domainver;
  }

  public @NotNull @Valid Request getRequest() {
    return this.request;
  }

  public Response getResponse() {
    return this.response;
  }

  public void setVer(String ver) {
    this.ver = ver;
  }

  public void setDomainspec(String domainspec) {
    this.domainspec = domainspec;
  }

  public void setDomainver(@NotEmpty String domainver) {
    this.domainver = domainver;
  }

  public void setRequest(@NotNull @Valid Request request) {
    this.request = request;
  }

  public void setResponse(Response response) {
    this.response = response;
  }
}
