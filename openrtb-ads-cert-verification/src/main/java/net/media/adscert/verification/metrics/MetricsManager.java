package net.media.adscert.verification.metrics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

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
  public abstract void pushMetrics(Map<String, Object> metricsMap, String status, String failureMessage);

}
