package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class Context {
  private Site site;
  private App app;
  @Valid
  private User user;
  private Device device;
  private Regs regs;
  private Restrictions restrictions;
}
