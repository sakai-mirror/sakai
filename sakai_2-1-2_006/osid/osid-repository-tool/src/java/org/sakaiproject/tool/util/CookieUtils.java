/**********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.sakaiproject.tool.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CookieUtils {

  private CookieUtils() {
  }

  /**
   * Get a new (empty) CookieData list
   * return An empty ArrayList
   */
  public static List newCookieList() {
    return new ArrayList();
  }

  /**
   * Parse an HTTP cookie
   * @param value Cookie value
   * @return A CookieData object representing this cookie
   */
  public static CookieData parseCookie(URL url, String value) {
    String[]      cookieFields;
    CookieData    cookie;

    cookieFields  = value.split(";\\s*");
    cookie        = makeCookieData(url, cookieFields[0]);

    for (int j = 1; j < cookieFields.length; j++) {
      if ("secure".equalsIgnoreCase(cookieFields[j])) {
        cookie.setSecure(true);
        continue;
      }

      if (cookieFields[j].indexOf('=') > 0) {
        String[] field = cookieFields[j].split("=");

        if ("expires".equalsIgnoreCase(field[0])) {
          cookie.setExpires(field[1]);
        } else if ("domain".equalsIgnoreCase(field[0])) {
          cookie.setDomain(field[1]);
        } else if ("path".equalsIgnoreCase(field[0])) {
          cookie.setPath(field[1]);
        } else if ("version".equalsIgnoreCase(field[0])) {
          cookie.setVersion(field[1]);
        } else if ("max-age".equalsIgnoreCase(field[0])) {
          cookie.setMaxAge(field[1]);
        }
      }
    }
    return cookie;
  }

	private static CookieData makeCookieData(URL url, String cookieText) {

		for (int i = 0; i < cookieText.length(); i++) {

			if (cookieText.charAt(i) == '=') {
				String name 	= cookieText.substring(0, i);
				String value	= "";

				if (i + 1 <= cookieText.length()) {
					value = cookieText.substring(i + 1);
				}
				return new CookieData(url, name, value);
			}
		}
		return new CookieData(url, cookieText, "");
	}

  /**
   * Maintain a list of CookieData objects (add, replace, or delete a cookie)
   * @param cookieList CookieData list
   * @param cookie A CookieData object
   */
  public static void storeCookie(List cookieList, CookieData cookie) {
    int size = cookieList.size();

    for (int i = 0; i < size; i++) {
      CookieData cd = (CookieData) cookieList.get(i);

      if (cookie.equals(cd)) {
        if (cookie.getMaxAge() == 0) {
          cookieList.remove(i);
          return;
        }
        cookieList.set(i, cookie);
        return;
      }
    }
    if (cookie.getMaxAge() != 0) {
      cookieList.add(cookie);
    }
  }

  /**
   * Does the cookie domain match the URL?
   * @param urlString URL String to match
   * @param cookie CookieData object (the cookie)
   * @return true if the cookie domain matches the URL
   */
  public static boolean inDomain(String urlString, CookieData cookie) {
    URL url;

    try {
      url = new URL(urlString);
    } catch (MalformedURLException exception) {
      return false;
    }
    return inDomain(url, cookie);
  }

  /**
   * Does the cookie domain match the URL?
   * @param url URL to match
   * @param cookie CookieData object (the cookie)
   * @return true if the cookie domain matches the URL
   */
  public static boolean inDomain(URL url, CookieData cookie) {
    String  domain = cookie.getDomain();

    return url.getHost().toLowerCase().endsWith(domain.toLowerCase());
  }

  /**
   * Does the cookie path match the URL "file"?
   * @param urlString String URL to match
   * @param cookie CookieData object (the cookie)
   * @return true if the cookie domain matches the URL
   */
  public static boolean inPath(String urlString, CookieData cookie) {
    URL url;

    try {
      url = new URL(urlString);
    } catch (MalformedURLException exception) {
      return false;
    }
    return inPath(url, cookie);
  }

  /**
   * Does the cookie path match the URL "file"?
   * @param url URL to match
   * @param cookie CookieData object (the cookie)
   * @return true if the cookie domain matches the URL
   */
  public static boolean inPath(URL url, CookieData cookie) {
    return url.getFile().startsWith(cookie.getPath());
  }

  /**
   * Find all stored cookies which associated with this server
   * @param cookieList List of stored cookies (CookieData objects)
   * @param url URL representing the request to lookup (server)
   * @return A List of associated cookies
   */
  public static List findCookiesForServer(List cookieList, URL url) {
    Iterator    iterator  = cookieList.iterator();
    ArrayList   list      = new ArrayList();

    while (iterator.hasNext()) {
      CookieData cookie = (CookieData) iterator.next();

      if ((inDomain(url, cookie)) && (inPath(url, cookie))) {
        list.add(cookie);
      }
    }
    return list;
  }

  /**
   * Find cookies associated with this server (by name)
   * @param cookieList List of stored cookies (CookieData objects)
   * @param url URL representing the request to lookup (server)
   * @param name Cookie name
   * @param exact true for exact name match, false to match on name prefix
   * @return A List of associated cookies
   */
  public static List getCookies(List cookieList, URL url,
                                String name, boolean exact) {

    List        serverCookies = findCookiesForServer(cookieList, url);
    Iterator    iterator      = serverCookies.iterator();
    ArrayList   list          = new ArrayList();

    while (iterator.hasNext()) {
      CookieData cookie = (CookieData) iterator.next();

      if (exact) {
        if (cookie.getName().equals(name)) {
          list.add(cookie);
        }
        continue;
      }

      if (cookie.getName().startsWith(name)) {
          list.add(cookie);
      }
    }
    return list;
  }

  /**
   * Find cookies associated with this server (exact name match)
   * @param cookieList List of stored cookies (CookieData objects)
   * @param url URL representing the request to lookup (server)
   * @param name Cookie name
   * @return A List of associated cookies
   */
  public static List getCookiesByName(List cookieList, URL url, String name) {
    return getCookies(cookieList, url, name, true);
  }

  /**
   * Find cookies associated with this server (match on name "prefix")
   * @param cookieList List of stored cookies (CookieData objects)
   * @param url URL representing the request to lookup (server)
   * @param name Cookie name
   * @return A List of associated cookies
   */
  public static List getCookiesByPrefix(List cookieList, URL url, String name) {
    return getCookies(cookieList, url, name, false);
  }
}