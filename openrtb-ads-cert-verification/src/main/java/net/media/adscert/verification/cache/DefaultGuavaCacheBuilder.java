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

package net.media.adscert.verification.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.security.PublicKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Builder to create a ready-to-use Guava cache.
 *
 * @author pranav.a
 * @author anupam.v
 * @since 1.0
 */
public class DefaultGuavaCacheBuilder {
  private Long maximumSize;
  private Duration expireAfterAccess = Duration.of(30, ChronoUnit.DAYS);
  private Duration expireAfterWrite = Duration.of(30, ChronoUnit.DAYS);

  private DefaultGuavaCacheBuilder() {
    this.maximumSize = 1000L;
  }

  /**
   * Constructs a {@link DefaultGuavaCacheBuilder} for creating a {@link Cache} with default
   * settings.
   *
   * @return {@link DefaultGuavaCacheBuilder}
   */
  public static DefaultGuavaCacheBuilder newBuilder() {
    return new DefaultGuavaCacheBuilder();
  }

  /**
   * See {@link CacheBuilder#maximumSize(long)}.
   *
   * @param maximumSize maximum entries the cache can hold
   * @return {@link DefaultGuavaCacheBuilder}
   */
  public DefaultGuavaCacheBuilder setMaximumSize(Long maximumSize) {
    this.maximumSize = maximumSize;
    return this;
  }

  /**
   * See {@link CacheBuilder#expireAfterAccess(Duration)}.
   *
   * @param expireAfterAccess represents duration of time after which an entry should be removed
   * @return {@link DefaultGuavaCacheBuilder}
   */
  public DefaultGuavaCacheBuilder setExpireAfterAccess(Duration expireAfterAccess) {
    this.expireAfterAccess = expireAfterAccess;
    return this;
  }

  /**
   * See {@link CacheBuilder#expireAfterWrite(Duration)}.
   *
   * @param expireAfterWrite represents duration of time that should elapse since an entry's
   *     creation or latest replacement of its value before the entry is removed
   * @return {@link DefaultGuavaCacheBuilder}
   */
  public DefaultGuavaCacheBuilder setExpireAfterWrite(Duration expireAfterWrite) {
    this.expireAfterWrite = expireAfterWrite;
    return this;
  }

  /**
   * Calls {@link CacheBuilder#build()} with the values configured via {@link
   * DefaultGuavaCacheBuilder}.
   *
   * @return {@link Cache}
   */
  public Cache<String, PublicKey> build() {
    return CacheBuilder.newBuilder()
        .maximumSize(this.maximumSize)
        .expireAfterAccess(this.expireAfterAccess)
        .expireAfterWrite(this.expireAfterWrite)
        .build();
  }
}
