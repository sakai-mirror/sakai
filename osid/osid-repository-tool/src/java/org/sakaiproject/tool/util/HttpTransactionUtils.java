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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP utilites
 */
public class HttpTransactionUtils
{
  private HttpTransactionUtils() {
  }
  /**
   * Default HTTP character set
   */
  public static final String DEFAULTCS	= "ISO-8859-1";

	/*
	 * Parameter handling
	 */

	/**
	 * Format one HTTP parameter
	 * @param name Parameter name
	 * @param value Parameter value (URLEncoded using default chracter set)
	 * @return Parameter text (ampersand+name=url-encoded-value)
	 */
  public static String formatParameter(String name, String value) {
  	return formatParameter(name, value, "&", DEFAULTCS);
  }

	/**
	 * Format one HTTP parameter
	 * @param name Parameter name
	 * @param value Parameter value (will be URLEncoded)
	 * @param separator Character to separate parameters
	 * @param cs Character set specification (utf-8, etc)
	 * @return Parameter text (separator+name=url-encoded-value)
	 */
  public static String formatParameter(String name, String value,
  																		 String separator, String cs) {
  	StringBuffer parameter = new StringBuffer();

    if (!StringUtils.isNull(value)) {

	   	parameter.append(separator);
 		 	parameter.append(name);
 			parameter.append('=');

			try {
  	 		parameter.append(URLEncoder.encode(value, cs));
  	 	} catch (UnsupportedEncodingException exception) {
  	 		throw new IllegalArgumentException("Invalid character set: \""
  	 																			 + cs
  	 																			 + "\"");
  		}
  	}
  	return parameter.toString();
  }

	/*
   * HTTP status values
   */

 	/**
 	 * Informational status?
 	 * @return true if so
 	 */
	public static boolean isHttpInfo(int status) {
		return ((status / 100) == 1);
	}

 	/**
 	 * HTTP redirect?
 	 * @return true if so
 	 */
	public static boolean isHttpRedirect(int status) {
		return ((status / 100) == 3);
	}

 	/**
 	 * Success status?
 	 * @return true if so
 	 */
	public static boolean isHttpSuccess(int status) {
		return ((status / 100) == 2);
	}

 	/**
 	 * Error in request?
 	 * @return true if so
 	 */
	public static boolean isHttpRequestError(int status) {
		return ((status / 100) == 4);
	}

 	/**
 	 * Server error?
 	 * @return true if so
 	 */
	public static boolean isHttpServerError(int status) {
		return ((status / 100) == 5);
	}

 	/**
 	 * General "did an error occur"?
 	 * @return true if so
 	 */
	public static boolean isHttpError(int status) {
		return isHttpRequestError(status) || isHttpServerError(status);
	}

	/**
	 * Set up a simple Map of HTTP request parameters (assumes no duplicate names)
	 * @param request HttpServletRequest object
	 * @return Map of name=value pairs
	 */
	public static Map getAttributesAsMap(HttpServletRequest request) {
		Enumeration enumeration		= request.getParameterNames();
		HashMap			map						= new HashMap();

		while (enumeration.hasMoreElements()) {
			String name	= (String) enumeration.nextElement();

			map.put(name, request.getParameter(name));
		}
		return map;
	}
}
