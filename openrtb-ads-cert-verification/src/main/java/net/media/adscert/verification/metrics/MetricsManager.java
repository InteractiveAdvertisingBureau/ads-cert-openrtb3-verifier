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

package net.media.adscert.verification.metrics;

import java.util.Map;

/**
 * This class can be used to handle metrics in the form of key-value pairs.
 *
 * @author anupam verma
 * @since 1.0
 */
public abstract class MetricsManager {

  /**
   * Handles metric names and their values.
   *
   * @param metricsMap map of metrics and their corresponding values
   * @param status status of the operation against which the metric was generated
   * @param failureMessage message highlighting the failure cause
   */
  public abstract void pushMetrics(
      Map<String, Object> metricsMap, String status, String failureMessage);
}
