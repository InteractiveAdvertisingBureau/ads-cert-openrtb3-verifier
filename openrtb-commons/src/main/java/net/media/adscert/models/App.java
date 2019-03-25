package net.media.adscert.models;

public class App extends DistributionChannel {

  private String domain;
  private String[] cat;
  private String[] sectcat;
  private String[] pagecat;
  private Integer cattax;
  private Integer privpolicy;
  private String keywords;
  private String bundle;
  private String storeid;
  private String storeurl;
  private String ver;
  private Integer paid;
  private Ext ext;

  public String getDomain() {
    return this.domain;
  }

  public String[] getCat() {
    return this.cat;
  }

  public String[] getSectcat() {
    return this.sectcat;
  }

  public String[] getPagecat() {
    return this.pagecat;
  }

  public Integer getCattax() {
    return this.cattax;
  }

  public Integer getPrivpolicy() {
    return this.privpolicy;
  }

  public String getKeywords() {
    return this.keywords;
  }

  public String getBundle() {
    return this.bundle;
  }

  public String getStoreid() {
    return this.storeid;
  }

  public String getStoreurl() {
    return this.storeurl;
  }

  public String getVer() {
    return this.ver;
  }

  public Integer getPaid() {
    return this.paid;
  }

  public Ext getExt() {
    return this.ext;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public void setCat(String[] cat) {
    this.cat = cat;
  }

  public void setSectcat(String[] sectcat) {
    this.sectcat = sectcat;
  }

  public void setPagecat(String[] pagecat) {
    this.pagecat = pagecat;
  }

  public void setCattax(Integer cattax) {
    this.cattax = cattax;
  }

  public void setPrivpolicy(Integer privpolicy) {
    this.privpolicy = privpolicy;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  public void setStoreid(String storeid) {
    this.storeid = storeid;
  }

  public void setStoreurl(String storeurl) {
    this.storeurl = storeurl;
  }

  public void setVer(String ver) {
    this.ver = ver;
  }

  public void setPaid(Integer paid) {
    this.paid = paid;
  }

  public void setExt(Ext ext) {
    this.ext = ext;
  }
}
