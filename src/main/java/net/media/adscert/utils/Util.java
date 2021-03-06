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

package net.media.adscert.utils;

import com.google.common.net.InternetDomainName;
import net.media.adscert.exceptions.ProcessException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.media.adscert.utils.CommonConstants.MAX_REDIRECTS;

public class Util {

  public static String getExceptionStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }

  private static final Set<Integer> redirectionCodes = new HashSet<>(Arrays.asList(301, 302, 303, 307, 308));

  private static String getKeyFromUrl(URL url, String rootDomain, int remainingRedirects) throws IOException {

    String newRootDomain = InternetDomainName.from(url.getHost()).topPrivateDomain().toString();

    if (!newRootDomain.equals(rootDomain)) {
      throw new ProcessException("Redirection to a new domain");
    }

    if (remainingRedirects < 0) {
      throw new ProcessException("Redirection limit is exceeded");
    }

    if (!url.getProtocol().equals("https")) {
      throw new ProcessException("Protocol is not https");
    }

    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    conn.setInstanceFollowRedirects(false);
    conn.setRequestMethod("GET");

    int responseCode = conn.getResponseCode();

    if (redirectionCodes.contains(responseCode)) {
      String newUrlToRead = conn.getHeaderField("Location");
      conn.disconnect();

      URL newUrl;
	    try {
		    newUrl = new URL(newUrlToRead);
		    if (!url.getProtocol().equalsIgnoreCase(newUrl.getProtocol())) {
			    throw new ProcessException("Redirection to a different protocol");
		    }
	    } catch (MalformedURLException mue) {
		    newUrl = new URL(url, newUrlToRead);
	    }
      return getKeyFromUrl(newUrl, rootDomain, remainingRedirects - 1);
    }

    if (responseCode != HttpURLConnection.HTTP_OK) {
      throw new ProcessException("Invalid Response");
    }

    StringBuilder result = new StringBuilder();

    try(BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String line;
      while ((line = rd.readLine()) != null) {
        if (!line.isEmpty() && line.charAt(0) != '#' && line.charAt(0) != '-') {
          result.append(line);
        }
      }
    } finally {
      conn.disconnect();
    }
    return result.toString();
  }

  public static String getKeyFromUrl(String urlToRead) throws IOException {
    // Read key from url
    URL url = new URL(urlToRead);
    String rootDomain = InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
    return getKeyFromUrl(url, rootDomain, MAX_REDIRECTS);
  }

  public static String getKeyFromFile(String filename) throws IOException {
    // Read key from file
    StringBuilder strKeyPEM = new StringBuilder();
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty() && line.charAt(0) != '#' && line.charAt(0) != '-') {
        strKeyPEM.append(line);
      }
    }
    br.close();
    return strKeyPEM.toString();
  }
}
