/*
 * Copyright © 2019 - present. MEDIA.NET ADVERTISING FZ-LLC.
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

import javax.validation.constraints.NotNull;

public class Macro {

  @NotNull private String key;
  private String value;

  public Macro() {}

  public @NotNull String getKey() {
    return this.key;
  }

  public void setKey(@NotNull String key) {
    this.key = key;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Macro)) return false;
    final Macro other = (Macro) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$key = this.getKey();
    final Object other$key = other.getKey();
    if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
    final Object this$value = this.getValue();
    final Object other$value = other.getValue();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $key = this.getKey();
    result = result * PRIME + ($key == null ? 43 : $key.hashCode());
    final Object $value = this.getValue();
    result = result * PRIME + ($value == null ? 43 : $value.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Macro;
  }

  public String toString() {
    return "net.media.openrtb3.Macro(key=" + this.getKey() + ", value=" + this.getValue() + ")";
  }
}
