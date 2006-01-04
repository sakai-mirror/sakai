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
package org.sakaiproject.tool.OSIDRepository;

import org.sakaiproject.tool.net.*;
import org.sakaiproject.tool.search.*;
import org.sakaiproject.tool.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import javax.net.ssl.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Set up server side authentication handlers.  These handle server-side
 * authorization chores (security issues between the Twin peaks server and
 * the search engine:<br>
 * <ul>
 * <li>HTTP realm authentication
 * <li>Simple SSL management (tr
 * </ul>
 */
public class SecuritySetup {
	/**
	 * Only initialize handlers once
	 */
	private static boolean _initialized = false;

  /**
   * Constructor
   */
  public SecuritySetup() {
  }

  /**
   * Establish configured authentication handlers
   * @param xmlStream Configuration file as an InputStream
   */
  public static synchronized void initialize(InputStream xmlStream)
  																											 throws Exception {
		Document			document;
		int						length;

		/*
		 * Only populate the list once
		 */
		if (_initialized) {
			return;
		}
		_initialized = true;
		/*
		 * Parse the configuration file
		 */
		if (xmlStream == null) {
			System.out.println("No authenticator configuration is available");
			return;
		}
		document = DomUtils.parseXmlStream(xmlStream);

		/*
		 * Set up any configured HTTP realm-based authentication
		 */
    establishRealmAuthentication(document.getDocumentElement());
    /*
     * Set up our own SSL TrustManager instance (for HTTPS connections)
     */
    establishSslTrustManager(document.getDocumentElement());
  }

	/**
	 * Set up an SSL "trust manager"
	 * @param root SSL configuration root
	 */
	private static void establishSslTrustManager(Element root)
															throws 	java.lang.ClassNotFoundException,
																			java.lang.IllegalAccessException,
																			java.lang.InstantiationException,
																			java.security.NoSuchAlgorithmException,
																			java.security.KeyManagementException {
		TrustManager[]	trustManager;
   	SSLContext 			sslContext;
		Element					sslElement;

		/*
		 * Make sure SSL is configured for use
		 */
		sslElement = DomUtils.getElement(root, "ssl");
		if (sslElement == null) {
			System.out.println("No SSL configuration provided");
			return;
		}

		if (!isEnabled(sslElement)) {
			System.out.println("SSL disabled in configuration");
			return;
		}
		/*
		 * Set up the requested TrsutManager
		 */
		trustManager		= new TrustManager[1];
		trustManager[0] = (TrustManager)
													Class.forName(getHandlerName(sslElement)).newInstance();

   	sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustManager, new java.security.SecureRandom());

    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

    System.out.println("SSL manager configured: " + getHandlerName(sslElement));
  }

	/**
	 * Set up a realm-based authenticator
	 * @param root HTTP configuration root
	 */
	private static void establishRealmAuthentication(Element root)
																			throws 	java.lang.ClassNotFoundException,
																							java.lang.IllegalAccessException,
																							java.lang.InstantiationException {
		HttpAuthenticator	authenticator;
		Element 					httpElement;
		NodeList					realmList;
		int								length, count;

		/*
		 * Make sure HTTP realm-based authentication is configured
		 */
		httpElement = DomUtils.getElement(root, "http");
		if (httpElement == null) {
			System.out.println("No HTTP authentication configuration provided");
			return;
		}

		if (!isEnabled(httpElement)) {
			System.out.println("HTTP authentication disabled in configuration");
			return;
		}
		/*
		 * Instantiate the handler (once) and establish credentials for each
		 * configured realm
		 */
		authenticator = null;
		realmList 		= DomUtils.getElementList(httpElement, "credential");
		length 				= realmList.getLength();
		count					= 0;

		for (int i = 0; i < length; i++) {
			Element	realmElement;
			String 	realm, username, password;

			realmElement = (Element) realmList.item(i);
			try {
				realm 		= getCredential(realmElement, "realm");
				username 	= getCredential(realmElement, "username");
				password 	= getCredential(realmElement, "password");

				if (authenticator == null) {
					authenticator =	(HttpAuthenticator)
														Class.forName(getHandlerName(httpElement)).newInstance();
				}
				authenticator.setCredentials(realm, username, password);
				count++;

			} catch (NoSuchElementException ignore) {
				System.err.println("HTTP credential format error");
				continue;
			}
		}
		/*
		 * Success, use this handler
		 */
		if (count > 0) {
		   Authenticator.setDefault(authenticator);
	     System.out.println("HTTP handler configured: " +	getHandlerName(httpElement));
		}
	}

	/*
	 * Helpers
	 */

	/**
	 * Is this handler enabled?
	 * @param root Handler root element
	 * @return true Only if the option enabled attribute = "true"
	 */
	private static boolean isEnabled(Element root) {
		Element element = DomUtils.getElement(root, "options");

		if (element != null) {
			return "true".equalsIgnoreCase(element.getAttribute("enabled"));
		}
		return false;
	}

	/*
	 * Get handler name
	 * @param root Handler root element
	 * @return The name of the configured handler (null if none)
	 */
	private static String getHandlerName(Element root) {
		Element element = DomUtils.getElement(root, "handler");

		if (element != null) {
			String name = element.getAttribute("name");
			return StringUtils.isNull(name) ? null : name;
		}
		return null;
	}

	/*
	 * Get handler name
	 * @param root Credentials root
	 * @param Credential name
	 * @return The credential value
	 */
	private static String getCredential(Element root, String name)
																			throws NoSuchElementException {
		Element element = DomUtils.getElement(root, name);

		if (element != null) {
			return DomUtils.getText(element);
		}
		throw new NoSuchElementException(name);
	}
}
