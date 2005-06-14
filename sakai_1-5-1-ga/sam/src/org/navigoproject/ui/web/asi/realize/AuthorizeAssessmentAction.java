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
 * Created on Aug 26, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.realize;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import osid.dr.Asset;
import osid.dr.DigitalRepository;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class AuthorizeAssessmentAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthorizeAssessmentAction.class);

  /* (non-Javadoc)
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm form, HttpServletRequest request,
    HttpServletResponse response)
    throws Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "execute(ActionMapping " + mapping + ", ActionForm " + form +
        ", HttpServletRequest " + request + ", HttpServletResponse " +
        response + ")");
    }

    String assessmentId = request.getParameter("assessmentId");
    Document publishedAssessment =
      getPublishedAssessment(assessmentId, request);
    Asset realizedAssessment =
      createRealizedAssessment(publishedAssessment, request);
    AuthorizationSupport authorization = new AuthorizationSupport(request);
    request.getSession();

    Agent agent = OsidManagerFactory.getAgent();
    request.getSession();

    Calendar effectiveDate = new GregorianCalendar();
    Date now = new Date();
    effectiveDate.setTime(now);
    Calendar expirationDate = new GregorianCalendar();
    long sevenDay = (long) 7 * (long) 24 * (long) 3600 * (long) 1000;
    expirationDate.setTimeInMillis(now.getTime() + sevenDay);
    authorization.authorizeAssessment(
      agent, realizedAssessment, effectiveDate, expirationDate);

    // Forward to the selectAssessmentAction.
    return mapping.findForward("SELECT_ASSESSMENT");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Document getPublishedAssessment(
    String assessmentId, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getPublishedAssessment(String " + assessmentId +
        ", HttpServletRequest " + request + ")");
    }

    Document qtiAssessment =
      (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentId);

    return qtiAssessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessment DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Asset createRealizedAssessment(
    Document publishedAssessment, HttpServletRequest request)
    throws Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createRealizedAssessment(Document " + publishedAssessment +
        ", HttpServletRequest " + request + ")");
    }

    Asset asset = null;
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(publishedAssessment);

      // TODO Auto-generated method stub
      // Copy the given Assessment and create a new asset authorize the user to use it. 
      RepositoryManager rm = new RepositoryManager();
      DigitalRepository dr = rm.getDigitalRepository(Assessment.class);

      NodeList list =
        publishedAssessment.getDocumentElement().getElementsByTagName(
          QTIConstantStrings.ASSESSMENT);
      String assessmentTitle =
        ((Element) list.item(0)).getAttribute(QTIConstantStrings.TITLE);
      String assessmentDescription = assessmentTitle;
      asset =
        dr.createAsset(
          assessmentTitle, assessmentDescription,
          org.navigoproject.osid.TypeLib.DR_QTI_ASSESSMENT_TAKEN);

      Id myAssetId = asset.getId();
      Id myDrId = dr.getId();

      LOG.debug("myAssetId:" + myAssetId);

      LOG.debug("myDrId:" + myDrId);

      Document doc = xsb.getDocument();
      Element root = doc.getDocumentElement();
      NodeList nodes = root.getElementsByTagName(QTIConstantStrings.ASSESSMENT);
      if(nodes.getLength() > 0)
      {
        Element assessment = (Element) nodes.item(0);
        assessment.setAttribute(QTIConstantStrings.IDENT, myAssetId.toString());
        xsb = new XmlStringBuffer(doc);
        asset.updateContent(xsb);
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
      throw ex;
    }

    return asset;
  }
}
