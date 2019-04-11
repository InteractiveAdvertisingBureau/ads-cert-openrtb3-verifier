/*
 * Copyright © 2019 - present. MEDIA NET SOFTWARE SERVICES PVT. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.media.adscert.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

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

  public void setId(@NotEmpty String id) {
    this.id = id;
  }

  public void setTest(Integer test) {
    this.test = test;
  }

  public void setTmax(Integer tmax) {
    this.tmax = tmax;
  }

  public void setAt(Integer at) {
    this.at = at;
  }

  public void setCur(String[] cur) {
    this.cur = cur;
  }

  public void setSeat(Set<String> seat) {
    this.seat = seat;
  }

  public void setWseat(Integer wseat) {
    this.wseat = wseat;
  }

  public void setCdata(String cdata) {
    this.cdata = cdata;
  }

  public void setSource(@NotNull @Valid Source source) {
    this.source = source;
  }

  public void setItem(@NotEmpty @Valid List<Item> item) {
    this.item = item;
  }

  public void setPack(Integer pack) {
    this.pack = pack;
  }

  public void setContext(@NotNull @Valid Context context) {
    this.context = context;
  }

  public void setExt(Object ext) {
    this.ext = ext;
  }

  public @NotEmpty String getId() {
    return this.id;
  }

  public Integer getTest() {
    return this.test;
  }

  public Integer getTmax() {
    return this.tmax;
  }

  public Integer getAt() {
    return this.at;
  }

  public String[] getCur() {
    return this.cur;
  }

  public Set<String> getSeat() {
    return this.seat;
  }

  public Integer getWseat() {
    return this.wseat;
  }

  public String getCdata() {
    return this.cdata;
  }

  public @NotNull @Valid Source getSource() {
    return this.source;
  }

  public @NotEmpty @Valid List<Item> getItem() {
    return this.item;
  }

  public Integer getPack() {
    return this.pack;
  }

  public @NotNull @Valid Context getContext() {
    return this.context;
  }

  public Object getExt() {
    return this.ext;
  }
}
