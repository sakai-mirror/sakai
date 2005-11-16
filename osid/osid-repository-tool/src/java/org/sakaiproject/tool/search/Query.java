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
package org.sakaiproject.tool.search;

import org.sakaiproject.tool.net.*;
import org.sakaiproject.tool.search.*;
import org.sakaiproject.tool.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
/**
 * Send a simple query to the NYPL Digital Gallery
 */
public class Query extends HttpTransactionQueryBase {
	/**
	 * Base URL for query
	 */
	public static final String BASE_URL	=
															 "http://localhost:8080/sakai-osid-repo-test/OSIDRepository/OSIDsearch";
	/**
   * Query parameters (tack on the URLEncoded search string)
   */
   public static final String QUERY_URL	= BASE_URL
															+ "?criteria=";
	/**
	 * Image count parameter (request a specific number of images)
	 */
	public static final String IMAGECOUNT_PARAMETER	= "&imgs=";
  /**
   * Constructor
   */
	public Query() 
        {
            super();
        }

	/**
	 * Search for images
	 */
        public void doQuery() 
        {
    /*
     * URLConnection handles redirects, query with GET, expect UTF-8 response
     */
    setRedirectBehavior(REDIRECT_AUTOMATIC);
    setQueryMethod(METHOD_GET);
    setDefaultCharacterSet("UTF-8");
    /*
     * Set up the URL (for GET), submit query
     */
		setUrl(QUERY_URL
				+  URLEncoder.encode(getRequestParameter("searchString"))
				+  IMAGECOUNT_PARAMETER
				+  getImageCount());

    submit();
	}
}
