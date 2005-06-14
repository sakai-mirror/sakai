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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.framework.Constants;
import org.sakaiproject.framework.ThreadLocalMapProvider;

/**
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: SentryFilter.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class SentryFilter
  implements Filter
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SentryFilter.class);
  private static final String CONTEXT_PATH = "CONTEXT_PATH";
  private static final String COURSE_ID = Constants.COURSE_ID;
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

    if(request instanceof HttpServletRequest)
    {
      final HttpServletRequest hrequest = (HttpServletRequest) request;

      /* add ContextPath to the requests's attributes for XSL and relative paths */
      hrequest.setAttribute(CONTEXT_PATH, hrequest.getContextPath());

      /* get and maintain COURSE_ID as appropriate */
      String courseId = hrequest.getParameter(COURSE_ID);

      if(courseId == null)
      { // may already exist in session
        courseId = (String) hrequest.getSession().getAttribute(COURSE_ID);
      }

      if(LOG.isDebugEnabled())
      {
        LOG.debug("courseId=" + courseId);
      }

      if(courseId != null)
      {
        /* add COURSE_ID to the requests's attributes for XSL */
        hrequest.setAttribute(COURSE_ID, courseId);
        
        /* add COURSE_ID to the session's attributes for state */
        hrequest.getSession().setAttribute(COURSE_ID, courseId);

        /* add COURSE_ID to the ThreadLocal for use elsewhere */
        ThreadLocalMapProvider.getMap().put(Constants.COURSE_ID, courseId);
      }
    }

    //    if(response instanceof HttpServletResponse)
    //    {
    //      HttpServletResponse hresponse = (HttpServletResponse) response;
    //
    //      //      hresponse.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    //      //      hresponse.setHeader("Pragma", "no-cache"); //HTTP 1.0
    //      //
    //      //      //prevents caching at the proxy server
    //      //      hresponse.setDateHeader("Expires", -1);
    //      ;
    //
    //      // Set to expire far in the past.
    //      hresponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    //
    //      // Set standard HTTP/1.1 no-cache headers.
    //      hresponse.setHeader(
    //        "Cache-Control", "no-store, no-cache, must-revalidate");
    //
    //      // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
    //      hresponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
    //
    //      // Set standard HTTP/1.0 no-cache header.
    //      hresponse.setHeader("Pragma", "no-cache");
    //    }
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
