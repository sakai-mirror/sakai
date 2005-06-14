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

package org.navigoproject.struts;

import org.navigoproject.osid.OsidManagerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.plugin.StxxTilesRequestProcessor;

import org.apache.struts.config.ForwardConfig;

import osid.authentication.AuthenticationException;

import osid.shared.Agent;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: RequestProcessor.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class RequestProcessor
  extends StxxTilesRequestProcessor
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RequestProcessor.class);
  private static final String PATH_EXEMPT_PROCESS_REQUEST =
    "path is exempt; process request";
  private static final String INDEX_FORWARD = "INDEX";
  private static final String NOT_AUTHENTICATED_IS_EXEMPT =
    "InteractiveUser does NOT exist in session; is path exempt?";
  private static final String AUTHENTICATED_REQUEST =
    "InteractiveUser DOES exist in session; process request";
  private static final String UNIQUE_ID = "UNIQUE_ID";
  private static final ExemptPath EXEMPT_PATH = ExemptPath.getInstance();


  /**
   * DOCUMENT ME!
   *
   * @param request DOCUMENT ME!
   * @param response DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected boolean processPreprocess(
    HttpServletRequest request, HttpServletResponse response)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "processPreprocess(HttpServletRequest request" +
        "HttpServletResponse response)");
    }

    /* the entire logic of this method could be much better 
     * optimized - LDS */
    
    if(EXEMPT_PATH.isPathExempt(request.getServletPath()))
    {
      LOG.debug(PATH_EXEMPT_PROCESS_REQUEST);

      return true;
    }

    /* check AuthN */
    Agent agent = null;
    try
    {
      agent = OsidManagerFactory.getAgent();
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }

    if(agent != null) // optimize for most common case
    {
      LOG.debug(AUTHENTICATED_REQUEST);
      try
      {
        final String uniqueId = agent.getDisplayName();
        org.apache.log4j.MDC.put(UNIQUE_ID, uniqueId);
      }
      catch(Exception ex)
      {
        LOG.error(ex.getMessage(), ex);
      }

      return true;
    }
    else
    {
      LOG.debug(NOT_AUTHENTICATED_IS_EXEMPT);
      if(EXEMPT_PATH.isPathExempt(request.getServletPath()))
      {
        LOG.debug(PATH_EXEMPT_PROCESS_REQUEST);

        return true;
      }
      else
      {
        if(LOG.isInfoEnabled())
        {
          LOG.info(
            "path [" + request.getServletPath() +
            "] is NOT exempt; forward to ???");
        }

        final ForwardConfig config =
          moduleConfig.findForwardConfig(INDEX_FORWARD);
        try
        {
          /* invalidating Session seems like the right thing to do, but I think
           * it breaks stuff - LDS
           */

          //          request.getSession().invalidate();
          response.sendRedirect(config.getPath());
        }
        catch(Exception ex)
        {
          LOG.error(ex.getMessage(), ex);
        }

        return false;
      }
    }
  }
}
