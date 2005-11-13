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

import org.sakaiproject.tool.util.*;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;


/**
 * This module provides a single, core implementation of SearchQueryInterface.
 * It's referenced in the search servlet, Search.java, to obtain "lowest
 * common denominator" functionality.
 *
 * In reality, each search application should extend QueryBase
 * and implement appropriate methods.  See HttpTransactionQueryBase.java
 * for an example.
 */
public abstract class QueryBase implements QueryInterface {

	private	Map			_parameterMap;

	/**
	 * Do a query - there is no base implementation.
	 */
  public abstract void doQuery();

	/**
	 * Populate user request parameters
	 * @param parameterMap Request details as a map (name=value pairs)
	 */
	protected void populateRequestParameters(Map parameterMap) {
	 	_parameterMap = parameterMap;
	}

	/**
	 * Parse and save user request parameters.  This base method supports only
	 * the standard, simple query format.  Override if necessary.
	 * @param parameterMap Request details (name=value pairs)
	 */
  public void parseRequest(Map parameterMap) {

		populateRequestParameters(parameterMap);

  	if (getRequestParameter("database") == null) {
      throw new IllegalArgumentException("Missing database name");
    }

  	if (getRequestParameter("searchEngine") == null) {
      throw new IllegalArgumentException("Missing search engine");
    }

  	if (getRequestParameter("searchString") == null) {
      throw new IllegalArgumentException("Missing search text");
    }

    setSearchString(getRequestParameter("searchString"));
  }

	/**
	 * Fetch a request parameter by name
	 * @param name Parameter name
	 * @return Parameter value
	 */
  public String	getRequestParameter(String name) {
  	return (String) _parameterMap.get(name);
	}

	/**
	 * Fetch the entire request parameter Map
	 * @return Parameter Map
	 */
  public Map getRequestParameterMap() {
  	return _parameterMap;
  }

	/**
	 * Fetch the desired image count
	 * @return The desired number of images
	 */
  public String	getImageCount() {
  	return ResultPageBase.IMAGES_PER_PAGE;

  }

	/*
	 * The following methods are required by the interface.  Any that you wish
	 * to use should be overridden in your application.
	 */
  public void setImageCount(String count) {
  	throw new UnsupportedOperationException();
  }

  public void setUrl(String url) {
  	throw new UnsupportedOperationException();
  }

  public String getUrl() {
  	throw new UnsupportedOperationException();
 }

  public void setSearchString(String searchString) {
  	throw new UnsupportedOperationException();
  }

  public String getSearchString() {
  	throw new UnsupportedOperationException();
  }

  public void setParameter(String name, String value) {
  	throw new UnsupportedOperationException();
  }

  public String getParameter(String name, String value) {
  	throw new UnsupportedOperationException();
  }

  public int submit() {
  	throw new UnsupportedOperationException();
  }

	public String getResponseString() {
  	throw new UnsupportedOperationException();
  }
}