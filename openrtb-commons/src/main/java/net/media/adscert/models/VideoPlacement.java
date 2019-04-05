package net.media.adscert.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by shiva.b on 14/12/18.
 */
@Data
public class VideoPlacement {
  private Integer ptype;
  private Integer pos;
  private Integer delay;
  private Integer skip;
  private Integer skipmi;
  private Integer skipafter;
  private Integer playmethod;
  private Integer playend;
  private Integer clktype;
  @NotNull
  private String[] mime;
  private Integer[] api;
  private Integer[] ctype;
  private Integer w;
  private Integer h;
  private Integer unit;
  private Integer mindur;
  private Integer maxdur;
  private Integer maxext;
  private Integer minbitr;
  private Integer maxbitr;
  private Integer[] delivery;
  private Integer maxseq;
  private Integer linear;
  private Integer boxing;
  private Companion[] comp;
  private Integer[] comptype;
  private Object ext;

}
