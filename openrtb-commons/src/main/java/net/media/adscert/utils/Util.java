package net.media.adscert.utils;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

  public static String getExceptionStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }

  public static String getKeyFromUrl(String urlToRead) throws IOException {
    // Read key from url
    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      if(!line.isEmpty() && line.charAt(0)!='#' && line.charAt(0)!='-') {
        result.append(line);
      }
    }
    rd.close();
    return result.toString();
  }

  public static String getKeyFromFile(String filename) throws IOException {
    // Read key from file
    StringBuilder strKeyPEM = new StringBuilder();
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = br.readLine()) != null) {
      if(!line.isEmpty() && line.charAt(0)!='#' && line.charAt(0)!='-') {
        strKeyPEM.append(line);
      }
    }
    br.close();
    return strKeyPEM.toString();
  }

  public static <T extends Number> T getNumber(Class<T> classObj, String value, T defaultValue) {
    if (value == null) return defaultValue;
    try {
      if (classObj == Integer.class) {
        return (T) Ints.tryParse(value);
      }
      if (classObj == Float.class) {
        return (T) Floats.tryParse(value);
      }
      if (classObj == Double.class) {
        return (T) Doubles.tryParse(value);
      }
      if (classObj == Long.class) {
        return (T) Longs.tryParse(value);
      }
      if (classObj == BigDecimal.class) {
        return (T) new BigDecimal(value);
      }
//      log.warn("getNumber : Unsupported class passed : '" + classObj.getName() + "' returning null");
    } catch (NumberFormatException e) {
//      log.warn("Exception occurred while converting, returning null", e);
    }
    return defaultValue;
  }
}
