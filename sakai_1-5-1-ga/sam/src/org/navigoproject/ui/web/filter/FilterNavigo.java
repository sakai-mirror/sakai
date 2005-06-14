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
 * Created on Jan 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.filter;

import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.struts.ExemptPath;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import osid.shared.Agent;

/**
 * @author ajpoland
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: FilterNavigo.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class FilterNavigo
  implements Filter
{
  private String beginPostPage;
  private String endPostPage;
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(FilterNavigo.class);
  private static final String CONTEXT_PATH = "CONTEXT_PATH";

  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig arg0)
    throws ServletException
  {
    beginPostPage =
      "<html><head><script>\n" + "function pf(){\n" +
      "\tdocument.f.submit();\n}" + "\n</script>\n</head>" +
      "<title>Auth Redirect</title>" + "<body onload=\"pf()\">\n";
    endPostPage = "<input type='submit'></form></body></html>";
  }

  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(
    ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest hrequest = (HttpServletRequest) request;
    HttpServletResponse hresponse = (HttpServletResponse) response;
    HttpSession session = hrequest.getSession();

    org.apache.log4j.MDC.put("REMOTE_ADDR", request.getRemoteAddr());
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "doFilter(ServletRequest request, ServletResponse response, " +
        "FilterChain chain)");
    }

    /* add ContextPath to the requests's attributes for XSL and relative paths*/
    request.setAttribute(CONTEXT_PATH, hrequest.getContextPath());
    if(LOG.isDebugEnabled())
    {
      LOG.debug(CONTEXT_PATH + "=" + hrequest.getAttribute(CONTEXT_PATH));
    }

    /* check AuthN */
    Agent agent = null;
    try
    {
      LOG.debug("Trying to getAgent");
      agent = OsidManagerFactory.getAgent();
      LOG.debug("Got agent " + agent.getDisplayName());
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      //      throw new Error(e);
    }

    if(agent != null) // optimize for most common case
    {
      LOG.debug("InteractiveUser DOES exist in session; process request");
      try
      {
        String uniqueId = agent.getId().getIdString();
        org.apache.log4j.MDC.put("UNIQUE_ID", uniqueId);
      }
      catch(Exception ex)
      {
        LOG.error(ex.getMessage(), ex);
        throw new Error(ex);
      }

      LOG.debug("Calling doFilter #1");
      chain.doFilter(hrequest, hresponse);
      LOG.debug("Finished doFilter #1");
    }
    else
    {
      LOG.debug("InteractiveUser does NOT exist in session; is path exempt?");
      ExemptPath exemptPath = ExemptPath.getInstance();
      if(exemptPath.isPathExempt(hrequest.getServletPath()))
      {
        LOG.debug("path is exempt; process request");

        LOG.debug("Calling doFilter #2");
        chain.doFilter(hrequest, hresponse);
        LOG.debug("Finished doFilter #2");
      }
      else
      {
        if(LOG.isInfoEnabled())
        {
          LOG.info(
            "path [" + hrequest.getServletPath() +
            "] is NOT exempt; forward to ???");
        }

        //ForwardConfig config = this.moduleConfig.findForwardConfig("LOGIN");
        try
        {
          //          request.getSession().invalidate();
          //LOG.debug("Sending redirect");
          //hresponse.sendRedirect(config.getPath());
          hresponse.sendRedirect("/Navigo/Login.do");
        }
        catch(Exception ex)
        {
          LOG.error(ex.getMessage(), ex);
          throw new Error(ex);
        }
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param hrequest DOCUMENTATION PENDING
   * @param hresponse DOCUMENTATION PENDING
   * @param redirectURL DOCUMENTATION PENDING
   *
   * @throws java.io.IOException DOCUMENTATION PENDING
   */

  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy()
  {
    ;
  }
}
