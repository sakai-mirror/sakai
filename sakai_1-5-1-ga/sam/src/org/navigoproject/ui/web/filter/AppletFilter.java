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

package org.navigoproject.ui.web.filter;

import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.util.SessionHash;

import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: AppletFilter.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class AppletFilter
  implements Filter
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AppletFilter.class);
  private FilterConfig filterConfig;

  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig config)
    throws ServletException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("init(FilterConfig " + filterConfig + ")");
    }

    this.filterConfig = config;
  }

  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(
    ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "doFilter(ServletRequest " + request + ", ServletResponse " + response +
        ", FilterChain " + chain + ")");
    }

    // filter request
    LOG.debug("Filter request");
    HttpSession session = ((HttpServletRequest) request).getSession();
    String url = ((HttpServletRequest) request).getRequestURL().toString();

    String userName = "guest";
    try
    {
      if(OsidManagerFactory.getAgent() != null)
      {
        userName = OsidManagerFactory.getAgent().getDisplayName();
      }
    }
    catch(Exception e)
    {
      LOG.info("will use guest as username");
    }

    LOG.debug("***userName= " + userName);

    // The identity of the user is lost when loading the applet, hence the session associated
    // with the user is also lost. This problem seems to occurs only with JBoss security
    // We can round this problem by checking if the url is the applet "CapturePlaybar.jar".
    // If so, we store all the attributes & values in session in SessionHash with the userName
    // before it get lost loading the applet. 
    if(url.lastIndexOf("CapturePlayback") != -1)
    {
      HashMap sessionMap = new HashMap();
      for(Enumeration e = session.getAttributeNames(); e.hasMoreElements();)
      {
        String name = (String) e.nextElement();
        sessionMap.put(name, session.getAttribute(name));
        LOG.debug("**put " + name + "=" + session.getAttribute(name));
      }

      SessionHash sh = SessionHash.getInstance();
      sh.put(userName, sessionMap);
      LOG.debug(
        "session is old, put session in hash for " + userName + "=" +
        sh.get(userName));
    }

    // next filter in chain
    chain.doFilter(request, response);

    // filter response
  }

  /**
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy()
  {
    ;
  }
}
