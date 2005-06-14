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

package org.navigoproject.ui.web.navigation;

import org.navigoproject.business.exception.ValidationException;
import org.navigoproject.settings.ApplicationSettings;
import org.navigoproject.ui.web.debug.BuildInfoBean;
import org.navigoproject.ui.web.messaging.DisplayMessage;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: NavigationAction.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class NavigationAction
  extends org.apache.struts.action.Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(NavigationAction.class);
  private static final ResourceBundle RB =
    ResourceBundle.getBundle("org.navigoproject.build");

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
  {
    NavigationForm form = (NavigationForm) actionForm;
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "perform(ActionMapping " + mapping +
        ", ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)");
      LOG.debug("form=" + form);
    }

    ActionErrors errors = new ActionErrors();
    if(form.getNavigationSubmit() != null)
    {
      LOG.debug("Form data found; Which action was requested?");
      if(form.getNavigationSubmit().equals("Select Assessments"))
      {
        LOG.debug("forwarding to SELECT_ASSESSMENT");

        this.saveErrors(request, errors);

        return mapping.findForward("SELECT_ASSESSMENT");
      }

      if(form.getNavigationSubmit().equals("View Results"))
      {
        LOG.debug("forwarding to SELECT_ASSESSMENT_RESULT");
        this.saveErrors(request, errors);

        return mapping.findForward("SELECT_ASSESSMENT_RESULT");
      }

      if(form.getNavigationSubmit().equals("Debug Session"))
      {
        LOG.debug("forwarding to DEBUG_SESSION");

        this.saveErrors(request, errors);

        return mapping.findForward("DEBUG_SESSION");
      }

      if(form.getNavigationSubmit().equals("Exit"))
      {
        LOG.debug("forwarding to EXIT");

        this.saveErrors(request, errors);

        return mapping.findForward("EXIT");
      }

      if(form.getNavigationSubmit().equals("Author"))
      {
        LOG.debug("forwarding to AUTHOR");

        this.saveErrors(request, errors);

        return mapping.findForward("AUTHOR");
      }

      if(form.getNavigationSubmit().equals("Upload Assessment"))
      {
        LOG.debug("forwarding to Upload Assessment");

        this.saveErrors(request, errors);

        return mapping.findForward("Upload");
      }

      if(form.getNavigationSubmit().equals("Template Editor"))
      {
        LOG.debug("forwarding to Template Editor");

        this.saveErrors(request, errors);

        return mapping.findForward("TEMPLATEEDITOR");
      }

      if(form.getNavigationSubmit().equals("Deliver Test as PDF"))
      {
        LOG.debug("forwarding to Deliver Test as PDF");

        this.saveErrors(request, errors);

        return mapping.findForward("PDFServlet");
      }

      if(form.getNavigationSubmit().equals("Test DisplayMessage"))
      {
        LOG.debug("Test DisplayMessage");
        try
        {
          DisplayMessage message =
            DisplayMessage.getInstance(
              "Title", "This is the body of the message",
              mapping.findForward("NAVIGATION"), request);
        }
        catch(ValidationException ex)
        {
          LOG.warn(ex.getMessage(), ex);
          errors.add(
            ActionErrors.GLOBAL_ERROR,
            new ActionError("error.generic", ex.getMessage()));
        }

        this.saveErrors(request, errors);

        return mapping.findForward("DISPLAY_MESSAGE");
      }

      LOG.error("This code should not be reached under normal circumstances!");
      String errorMessage =
        "No forward found for requested action: " + form.getNavigationSubmit();
      LOG.error(errorMessage);
      errors.add(
        ActionErrors.GLOBAL_ERROR,
        new ActionError("error.generic", errorMessage));
    }

    BuildInfoBean bib = new BuildInfoBean();
    bib.setBuildVersion(ApplicationSettings.getBuildVersion());
    bib.setBuildTime(ApplicationSettings.getBuildTime());
    bib.setBuildTag(ApplicationSettings.getBuildTag());
    request.setAttribute("BuildInfoBean", bib);
    LOG.debug("forwarding to input");

    this.saveErrors(request, errors);

    return mapping.getInputForward();
  }
}
