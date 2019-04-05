package net.media.adscert.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Geo {

  private Integer type;
  private Float lat;
  private Float lon;
  private Integer accur;
  private Integer lastfix;
  private Integer ipserv;
  private String country;
  private String region;
  private String metro;
  private String city;
  private String zip;
  private Integer utcoffset;
  private Ext ext;

}