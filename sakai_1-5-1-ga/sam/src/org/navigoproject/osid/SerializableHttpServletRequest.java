/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

/*
 * Created on Aug 22, 2003
 *
 */
package org.navigoproject.osid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.security.Principal;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @deprecated Should use ThreadLocal pattern instead.
 * @version $Id: SerializableHttpServletRequest.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class SerializableHttpServletRequest
  implements Serializable, HttpServletRequest
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SerializableHttpServletRequest.class);
  static int counter = 1;
  private int myId;

  /**
   * Explicitly setting serialVersionUID insures future versions can be
   * successfully restored. It is essential this variable name not be changed
   * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;
  private HttpServletRequest request;

  /**
   * Creates a new SerializableHttpServletRequest object.
   *
   * @param request DOCUMENTATION PENDING
   */
  public SerializableHttpServletRequest(HttpServletRequest request)
  {
    LOG.debug("new SerializableHttpServletRequest(HttpServletRequest request)");
    if(request == null)
    {
      LOG.warn(
        "SerializableHttpServletRequest being constructed from a null request");

      //throw new Error("SerializableHttpServletRequest can't be constructed from a null request");
    }
    else
    {
      try {
        if(request.getAttribute("edu.iu.uis.cas.filter.ApplicationCode") 
          == null)
        {
          LOG.debug("CAS ApplicationCode not found in passed Request");
        }
        else
        {
          LOG.debug("ApplicationCode present; good.");
        }
      } catch (Exception e) {
        LOG.debug("ApplicationCode not found in passed Request");
      }
    }

    this.request = request;
    myId = counter++;
    LOG.debug("Creating request: " + myId);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    return request.equals(obj);
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   *
   * @throws Error DOCUMENTATION PENDING
   */
  public Object getAttribute(String arg0)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAttribute(" + arg0 + ")");
      LOG.debug("myId: " + myId);
    }

    if(request == null)
    {
      LOG.error("Can't proxy to null request");
      throw new Error("Can't proxy to null request");
    }

    Object result = request.getAttribute(arg0);

    return result;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Enumeration getAttributeNames()
  {
    return request.getAttributeNames();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getAuthType()
  {
    return request.getAuthType();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getCharacterEncoding()
  {
    return request.getCharacterEncoding();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public int getContentLength()
  {
    return request.getContentLength();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getContentType()
  {
    return request.getContentType();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getContextPath()
  {
    return request.getContextPath();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Cookie[] getCookies()
  {
    return request.getCookies();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public long getDateHeader(String arg0)
  {
    return request.getDateHeader(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public String getHeader(String arg0)
  {
    return request.getHeader(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Enumeration getHeaderNames()
  {
    return request.getHeaderNames();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public Enumeration getHeaders(String arg0)
  {
    return request.getHeaders(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   *
   * @throws IOException
   */
  public ServletInputStream getInputStream()
    throws IOException
  {
    return request.getInputStream();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public int getIntHeader(String arg0)
  {
    return request.getIntHeader(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Locale getLocale()
  {
    return request.getLocale();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Enumeration getLocales()
  {
    return request.getLocales();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getMethod()
  {
    return request.getMethod();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public String getParameter(String arg0)
  {
    return request.getParameter(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Map getParameterMap()
  {
    return request.getParameterMap();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Enumeration getParameterNames()
  {
    return request.getParameterNames();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public String[] getParameterValues(String arg0)
  {
    return request.getParameterValues(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getPathInfo()
  {
    return request.getPathInfo();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getPathTranslated()
  {
    return request.getPathTranslated();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getProtocol()
  {
    return request.getProtocol();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getQueryString()
  {
    return request.getQueryString();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   *
   * @throws IOException
   */
  public BufferedReader getReader()
    throws IOException
  {
    return request.getReader();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public String getRealPath(String arg0)
  {
    return request.getRealPath(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getRemoteAddr()
  {
    return request.getRemoteAddr();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getRemoteHost()
  {
    return request.getRemoteHost();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getRemoteUser()
  {
    return request.getRemoteUser();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public RequestDispatcher getRequestDispatcher(String arg0)
  {
    return request.getRequestDispatcher(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getRequestedSessionId()
  {
    return request.getRequestedSessionId();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getRequestURI()
  {
    return request.getRequestURI();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public StringBuffer getRequestURL()
  {
    return request.getRequestURL();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getScheme()
  {
    return request.getScheme();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getServerName()
  {
    return request.getServerName();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public int getServerPort()
  {
    return request.getServerPort();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getServletPath()
  {
    return request.getServletPath();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public HttpSession getSession()
  {
    LOG.debug("myId: " + myId);
    if(myId == 2)
    {
      LOG.debug("The possibly stale request was just accessed.");
    }

    return request.getSession();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public HttpSession getSession(boolean arg0)
  {
    LOG.debug("myId: " + myId);
    if(myId == 2)
    {
      LOG.debug("The possibly stale request was just accessed.");
    }

    return request.getSession(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Principal getUserPrincipal()
  {
    return request.getUserPrincipal();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return request.hashCode();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isRequestedSessionIdFromCookie()
  {
    return request.isRequestedSessionIdFromCookie();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isRequestedSessionIdFromUrl()
  {
    return request.isRequestedSessionIdFromUrl();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isRequestedSessionIdFromURL()
  {
    return request.isRequestedSessionIdFromURL();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isRequestedSessionIdValid()
  {
    return request.isRequestedSessionIdValid();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public boolean isSecure()
  {
    return request.isSecure();
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @return
   */
  public boolean isUserInRole(String arg0)
  {
    return request.isUserInRole(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   */
  public void removeAttribute(String arg0)
  {
    request.removeAttribute(arg0);
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   * @param arg1
   */
  public void setAttribute(String arg0, Object arg1)
  {
    request.setAttribute(arg0, arg1);
  }

  /**
   * DOCUMENT ME!
   *
   * @param arg0
   *
   * @throws UnsupportedEncodingException
   */
  public void setCharacterEncoding(String arg0)
    throws UnsupportedEncodingException
  {
    request.setCharacterEncoding(arg0);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return request.toString();
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public HttpServletRequest getRequest()
  {
    return request;
  }
}
