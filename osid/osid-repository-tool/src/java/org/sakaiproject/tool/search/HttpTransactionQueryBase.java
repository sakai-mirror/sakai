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
import org.sakaiproject.tool.OSIDRepository.*;
import org.sakaiproject.tool.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Base class for HTTP search activities
 */
public abstract class HttpTransactionQueryBase
											extends QueryBase
											implements HttpTransactionQueryInterface {
	/**
	 * Name of the cookie List (stored with session context)
	 */
	private static final String	COOKIELIST = "CookieList";
	/**
	 * The stored query URL (stored with session context)
	 */
	private static final String	QUERYURL   = "QueryUrl";
	/**
	 * Stored query form (stored with session context)
	 */
	private static final String	QUERYFORM  = "QueryForm";

  private HttpTransaction	_transaction;
  private boolean     		_followRedirects;
  private int		 	    		_redirectBehavior;
  private String					_method;
  private String      		_searchString;
  private String      		_url;
  private String      		_searchResult;
	private SessionContext	_session;

	/**
	 * Constructor
	 */
  public HttpTransactionQueryBase() {
    _transaction 			= null;
		_session					= null;
		_method 					= "POST";
    _followRedirects  = false;
    _searchString     = null;
    _url              = null;
    _searchResult     = null;
  }

	/**
	 * One time initialization
	 * @param session SessionContext object
	 */
	public void initialize(SessionContext session) {

		_session = session;

		if ((_session.get(COOKIELIST)) == null) {
			_session.put(COOKIELIST, CookieUtils.newCookieList());
		}
		_transaction = new HttpTransaction();
		_transaction.initialize((List) _session.get(COOKIELIST));
	}

	/**
	 * Set search URL
	 * @param url URL string
	 */
  public void setUrl(String url) {
    _url = url;
  }

	/**
	 * Set search URL
	 * @param url URL object
	 */
  public void setUrl(URL url) {
    _url = url.toString();
  }

	/**
	 * Fetch the current search URL
	 * @return The URL (as a String)
	 */
  public String getUrl() {
    return _url;
  }

	/**
	 * Specify the search text
	 * @param searchString Text to look for
	 */
  public void setSearchString(String searchString) {
    _searchString = searchString;
  }

	/**
	 * Fetch the current search text
	 * @return The search string
	 */
  public String getSearchString() {
    return _searchString;
  }

	/**
	 * Set the HTTP query method (post or get)
	 * @param method <code>METHOD_POST</code> or <code>METHOD_GET</code>
	 */
	public void setQueryMethod(String method) {
		_method = method;
	}

	/**
	 * Fetch the current HTTP query method
	 * @return The method (as text)
	 */
	public String getQueryMethod() {
		return _method;
	}

	/**
	 * Fetch a named HTTP response parameter
	 * @param name Parameter name
	 * @return Parameter value
	 */
	public String getResponseHeader(String name) {
		return _transaction.getResponseHeader(name);
	}

	/**
	 * Set the default character set for this transaction
	 * @param cs Character set (UTF-8, ISO-8859-1, etc)
	 */
	public void setDefaultCharacterSet(String cs) {
		_transaction.setDefaultCharacterSet(cs);
	}

	/**
	 * Fetch the response character set
	 * @return Character set designation (as a String)
	 */
	public String getResponseCharacterSet() {
		return _transaction.getResponseCharacterSet();
	}

	/*
	 * The following "query" methods are used only for EBSCO - should
	 * they be moved to EbscoQueryBase?
	 */

	/**
	 * Create the session context name for a specified consumer
	 * @param base The base name for session context object
	 * @param consumer A unique name for the "user"
	 * @return Full session context name
	 */
	private String scn(String base, String consumer) {
		StringBuffer name = new StringBuffer(base);

		name.append('.');
		name.append(consumer);

		return name.toString();
	}

	/**
	 * Save the URL for the query page
	 * @param consumer A unique name for the "user" of this object
	 * @param queryUrl Address of the final query page
	 */
	public void setQueryUrl(String consumer, String queryUrl) {
		_session.put(scn(QUERYURL, consumer), queryUrl);
	}

	/**
	 * Fetch the URL for the query
	 * @param consumer A unique name for the "user" of this object
	 * @return Address of the final query page
	 */
	public String getQueryUrl(String consumer) {
		return (String) _session.get(scn(QUERYURL, consumer));
	}

	/**
	 * Save the final query form as a DOM document
	 * @param consumer A unique name for the "user" of this object
	 * @param queryForm Query page as a DOM document
	 */
	public void setQueryDocument(String consumer, Document queryForm) {
		_session.put(scn(QUERYFORM, consumer), queryForm);
	}

	/**
	 * Fetch the final query form as a DOM document
	 * @param consumer A unique name for the "user" of this object
	 * @return Query form (as a DOM document)
	 */
	public Document getQueryDocument(String consumer) {
		return (Document) _session.get(scn(QUERYFORM, consumer));
	}

	/**
	 * Get the SessionContext object for this user
	 * @return The current SessionContext
	 */
	public SessionContext getSessionContext() {
		return _session;
	}

	/**
	 * Establish a mechanism for handling redirects
	 * @param behavior Specifies the desired behavior.  Use one of:
	 *<ul>
	 *<li> REDIRECT_AUTOMATIC						- <code>URLConnection</code> handles
	 																			all redirects
	 *<li> REDIRECT_MANAGED   					- The <code>QueryBase submit()</code> code
	 *																	  handles any redirects
	 *<li> REDIRECT_MANAGED_SINGLESTEP	- The caller will handle each redirect
	 *</ul>
	 */
  public void setRedirectBehavior(int behavior) throws SearchException {

    switch (behavior) {
    	case REDIRECT_AUTOMATIC:
		    _followRedirects = true;
		    break;

		  case REDIRECT_MANAGED:
		  case REDIRECT_MANAGED_SINGLESTEP:
		   	_followRedirects = false;
		  	break;

		  default:
		  	throw new SearchException("Invalid redirect behavior: " + behavior);
		}
    _redirectBehavior	= behavior;
  }

	/**
	 * Should URLConnection follow redirects?
	 * @return true if URLConnection should handle redirects
	 */
  public boolean getFollowRedirects() {
    return _followRedirects;
  }

  /**
   * Set up a name=value pair
   * @param name Parameter name
   * @param value Parameter value
   */
  public void setParameter(String name, String value) {
    _transaction.setParameter(name, value);
  }

	/**
	 * Get a named parameter
   * @param name Parameter name
   * @return Parameter value
	 */
  public String getParameter(String name) {
    return _transaction.getParameter(name);
  }

	/**
	 * Clear the parameter list
	 */
	public void clearParameters() {
		_transaction.clearParameters();
	}

	/**
	 * Submit a request (POST or GET) and read the response.  Various aspects
	 * of the response can be inspected using the "getXXX()" methods.
	 * @return Submission status code (200 = success)
	 */
  public int submit() throws SearchException {
  	int status;

    /*
     * Send the request
     */
    try {
      _transaction.setFollowRedirects(_followRedirects);
      _transaction.setTransactionType(_method);

  		status = _transaction.doTransaction(_url);

      switch (_redirectBehavior) {
      	case REDIRECT_AUTOMATIC:
      	case REDIRECT_MANAGED_SINGLESTEP:
      		return status;

      	default:
      		break;
      }
			/*
			 * Were we redirected to another page?  If so, try to fetch
			 */
    	while (HttpTransactionUtils.isHttpRedirect(status)) {
    		String 	location 	= _transaction.getResponseHeader("Location");
    		String 	baseUrl		= _transaction.getBaseUrlSpecification();
    		URL			fullUrl		= newFullUrl(baseUrl, location);

    		setUrl(fullUrl);

    		_transaction.setTransactionType("GET");
	  		status = _transaction.doTransaction(fullUrl);
  		}
  		/*
  		 * Done, return final status
  		 */
		  return status;

    } catch (Exception exception) {
    	System.out.println("Exception seen, the current URL is \"" + getUrl() + "\"");
    	exception.printStackTrace(System.out);
      throw new SearchException(exception.toString());
    }
  }

	/**
   * Get the server response text
   * @return The response (as a String)
   */
  public String getResponseString() {
    return _transaction.getResponseString();
  }

	/**
	 * Parse the server response (override if required)
	 * @return Response Document
	 */
  public Document getResponseDocument() throws SearchException {
  	try {
    	return getHtmlResponseDocument();

    } catch (Exception exception) {
      throw new SearchException(exception.toString());
    }
  }

	/**
	 * Parse the server response as HTML
	 * @return Response Document
	 */
  public Document getHtmlResponseDocument() throws DomException {
    return DomUtils.parseHtmlString(_transaction.getResponseString());
  }

	/**
	 * Parse the server response as XML
	 * @return Response Document
	 */
  public Document getXmlResponseDocument() throws DomException {
    return DomUtils.parseXmlString(_transaction.getResponseString());
  }

  /*
   * Helpers
   */

 	/**
 	 * Locate the HTML BODY element in the page document
 	 * @param pageDocument An HTML page (as a DOM)
 	 * @return The body Element
 	 */
	public Element getBody(Document pageDocument) {
    Element		root = pageDocument.getDocumentElement();

    return DomUtils.getElement(root, "BODY");
	}

	/**
	 * Construct a new URL from base and relative components
	 * @param baseComponent Base URL - the relative URL is added to this
	 * @param relativeComponent A partial (or full) URL that represents our target
	 * @return A full URL composed of the relative URL combined with "missing"
	 * 				 portions taken from the base
	 */
	public URL newFullUrl(String baseComponent, String relativeComponent) {
		try {
 			URL	baseUrl	= new URL(baseComponent);
 			return new URL(baseUrl, relativeComponent);

 		} catch (MalformedURLException exception) {
 			throw new SearchException(exception.toString());
 		}
	}

	/**
	 * Set query parameters based on page-wide INPUTs
	 * @param pageDocument The search engine query page (as a DOM Document)
	 * @param nameList A list of the parameters we're looking for
	 */
  public void setParametersFromInputs(Document pageDocument, List nameList) {
    setParametersFromList(DomUtils.getElementList(getBody(pageDocument), "INPUT"),
    											nameList);
  }

	/**
	 * Produce a target URL for this query by combining the form "action" value
	 * with the base URL of the query page
	 * @param pageDocument The search engine query page (as a DOM Document)
	 * @param formName The name of the FORM to lookup
	 *									(eg <code>FORM name="formName"</code>)
	 * @param nameList A list of the parameters we're looking for
	 */
  public void setParametersFromFormInputs(Document 	pageDocument,
  																				String 		formName,
  																				List   		nameList)
  																				throws SearchException {
    Element	formElement;

    if ((formElement = getFormElement(pageDocument, formName)) == null) {
	    throw new SearchException("No such form: " + formName);
		}
		setParametersFromElementInputs(formElement, nameList);
	}

	/**
	 * Set query parameters based on INPUTs within an Element
	 * @param element The base element (often a FORM)
	 * @param nameList A list of the parameters we're looking for
	 */
  public void setParametersFromElementInputs(Element element, List nameList) {
    setParametersFromList(DomUtils.getElementList(element, "INPUT"), nameList);
  }

	/**
	 * Set query parameters based on INPUTs
	 * @param nodeList List of Elements to evaluate
	 * @param nameList A list of the parameters we're looking for
	 */
  public void setParametersFromList(NodeList nodeList, List nameList) {
    int	nodeSize	= nodeList.getLength();

    for (int i = 0; i < nodeSize; i++) {
      Element element			= (Element) nodeList.item(i);
      String	attribute		= element.getAttribute("name");

      if (nameList.contains(attribute)) {
      	setParameter(attribute, element.getAttribute("value"));
      }
    }
  }

	/**
	 * Produce a target URL for this query by combining the form "action" value
	 * with the base URL of the query page
	 * @param pageDocument The search engine query page (as a DOM Document)
	 * @param formName The name of the FORM to lookup
	 *									(eg <code>FORM name="formName"</code>)
	 */
  public void setUrlFromForm(Document pageDocument, String formName) throws SearchException {
    Element	form;

    if ((form = getFormElement(pageDocument, formName)) == null) {
	    throw new SearchException("No such form: " + formName);
		}

		try {
			setUrl(newFullUrl(_transaction.getBaseUrlSpecification(),
	     								  form.getAttribute("action")));
	  } catch (MalformedURLException exception) {
	    throw new SearchException(exception.toString());
	  }
  }

	/**
	 * Find a named FORM element
	 * @param pageDocument The search engine query page (as a DOM Document)
	 * @param formName The name of the FORM to lookup
	 *									(eg <code>FORM name="formName"</code>)
	 */
  public Element getFormElement(Document pageDocument, String formName) {
 		return DomUtils.selectFirstElementByAttributeValue(getBody(pageDocument),
 																											 "FORM",
 																											 "name", formName);
  }
 }