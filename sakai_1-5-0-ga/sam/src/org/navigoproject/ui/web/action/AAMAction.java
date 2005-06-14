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

package org.navigoproject.ui.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

// import org.navigoproject.ui.web.form.evaluation.*;
//import edu.stanford.aam.results.model.*;

/**
 * Parent action class for AAM.
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @version $Id: AAMAction.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class AAMAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AAMAction.class);

  /**
   * Creates a new AAMAction object.
   */
  public AAMAction()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param httpServletResponse DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "execute(" + actionMapping + ", " + actionForm +
        ", httpServletRequest, httpServletResponse)");
    }

    return null;

    // This may do something useful later.  -rmg
  }

  /**
   * This is a utility method inherited by all AAM action classes. Usage (in
   * execute method): forward = forwardToFailure("This is an error message",
   * actionMapping, httpServletRequest); //... return forward;
   *
   * @param message Error message to be displayed on error page
   * @param actionMapping current action mapping
   * @param request request to hold the message
   *
   * @return the forward
   */
  public ActionForward forwardToFailure(
    String message, ActionMapping actionMapping, HttpServletRequest request)
  {
    request.setAttribute("failMessage", message);
    ActionForward forward = actionMapping.findForward("Failure");

    return forward;
  }

  /**
   * This invalidates a session without erasing the metadata from coursework.
   *
   * @param session DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected HttpSession invalidateSession(
    HttpSession session, HttpServletRequest request)
  {
    String agent_id = (String) session.getAttribute("agent_id");
    String agent_name = (String) session.getAttribute("agent_name");
    String course_id = (String) session.getAttribute("course_id");
    String course_name = (String) session.getAttribute("course_name");
    session.invalidate();
    session = request.getSession(true);
    session.setAttribute("agent_id", agent_id);
    session.setAttribute("agent_name", agent_name);
    session.setAttribute("course_id", course_id);
    session.setAttribute("course_name", course_name);

    return session;
  }
}
