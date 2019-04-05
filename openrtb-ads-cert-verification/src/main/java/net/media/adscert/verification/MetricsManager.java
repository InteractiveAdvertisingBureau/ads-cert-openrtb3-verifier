package net.media.adscert.verification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class MetricsManager {

  private Set<String> fieldsAsMetrics;
  private int samplingRate = 100;
  private Consumer<String> jsonHandler;
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public Set<String> getFieldsAsMetrics() {
    return fieldsAsMetrics;
  }

  public void setFieldsAsMetrics(Set<String> fieldsAsMetrics) {
    this.fieldsAsMetrics = fieldsAsMetrics;
  }

  public int getSamplingRate() {
    return samplingRate;
  }

  public void setSamplingRate(int samplingRate) {
    this.samplingRate = samplingRate;
  }

  public Consumer<String> getJsonHandler() {
    return jsonHandler;
  }

  public void setJsonHandler(Consumer<String> jsonHandler) {
    this.jsonHandler = jsonHandler;
  }

  public boolean toConsider() {
    return ThreadLocalRandom.current().nextInt(1, samplingRate + 1) < samplingRate;
  }

  public void pushMetrics(Map<String, Object> metricsMap, String status) {
    pushMetrics(metricsMap, status, jsonHandler);
  }

  public void pushMetrics(Map<String, Object> metricsMap, String status, Consumer<String> jsonHandler) {
    if(!toConsider()) {
      return;
    }
    if(jsonHandler == null) {
      return;
    }
    try {
      LinkedHashMap<String, Object> map = new LinkedHashMap<>(metricsMap);
      if (fieldsAsMetrics != null && fieldsAsMetrics.size() > 0) {
        final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
          final Map.Entry<String, Object> entry = iterator.next();
          if(!fieldsAsMetrics.contains(entry.getKey())) {
            iterator.remove();
          }
        }
      }
      map.put("status", status);
      jsonHandler.accept(objectMapper.writeValueAsString(map));
    } catch (JsonProcessingException e) {
      // e.printStackTrace();
    }
  }
}
