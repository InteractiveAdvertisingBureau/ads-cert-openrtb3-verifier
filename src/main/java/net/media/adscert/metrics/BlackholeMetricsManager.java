/*
 * Copyright  2019 - present. IAB Tech Lab
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

package net.media.adscert.metrics;

import net.media.adscert.enums.Result;

import java.util.Map;

/**
 * Acts as a sink for metrics by performing no operation.
 *
 * @author anupam verma
 * @since 1.0
 */
public class BlackholeMetricsManager extends MetricsManager {

  @Override
  public void pushMetrics(Map<String, Object> metricsMap, Result result) {}
}
