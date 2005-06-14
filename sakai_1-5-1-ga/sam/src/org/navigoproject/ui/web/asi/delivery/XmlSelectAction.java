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
 * Created on Aug 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Iso8601DateFormat;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.PublishedAssessmentBean;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.osid.FunctionLib;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.util.XmlUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.DocumentImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import osid.OsidException;
import osid.authentication.AuthenticationException;
import osid.authorization.AuthorizationManager;

import osid.dr.DigitalRepositoryException;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedException;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class XmlSelectAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlSelectAction.class);
  public final static String ASSESSMENT_SESSION_KEY =
    "org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean";
  public final static String ASSESSMENT_ID_KEY = "assessmentId";

  //  public final static String ASSESSMENT_ID =
  //    "20030819120758701-898481440418557591-iu.edu";

  /* (non-Javadoc)
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */

  // Create a new XmlSelectAction object
  public XmlSelectAction()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
    throws Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "execute(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest request, " +
        "HttpServletResponse response)");
    }

    XmlDeliveryForm deliveryForm = (XmlDeliveryForm) actionForm;
    String action = deliveryForm.getAction();
    XmlDeliveryBean deliveryBean = new XmlDeliveryBean();
    String assessmentId = null;
    
    if(request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY) == null) {
      
      assessmentId = (String) request.getAttribute(XmlSelectAction.ASSESSMENT_ID_KEY);
      
    } else {
    
      assessmentId = (String) request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY);
    }
    
      

    // TODO passing assessmentId dynamically. 
    if(assessmentId == null)
    {
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

      return mapping.getInputForward();
    }
    else
    {
      return retrieveTest(mapping, deliveryForm, deliveryBean, request);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ActionForward retrieveTest(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    String publishedAssessmentId = null;
    if(request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY) == null) {
      
     publishedAssessmentId = (String) request.getAttribute(XmlSelectAction.ASSESSMENT_ID_KEY);
      
   } else {
    
     publishedAssessmentId = (String) request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY);
   }
    
    try
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "retrieveTest(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
          deliveryForm + ", " + "XmlDeliveryBean , " +
          "HttpServletRequest request)");
      }
      
      PublishedAssessmentBean pab = 
        PersistenceService.getInstance().getPublishedAssessmentQueries().findCoreIdByPublishedId(publishedAssessmentId);
     
      String coreAssessmentId = pab.getCoreId();
      QtiSettingsBean qsb =
        PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedAssessmentId);
      Timestamp createdTime = qsb.getCreatedDate();
      Calendar createdDate = Calendar.getInstance();
      createdDate.setTimeInMillis(createdTime.getTime());
      
      Document questtestInterop =
        (new XmlDeliveryService()).getAssessmentQTIInterop(coreAssessmentId, createdDate);

      deliveryBean = new XmlDeliveryBean();

      deliveryBean.setQuesttestInterop(questtestInterop);
      deliveryBean.setAssessmentId(publishedAssessmentId);
      Element assessment = null;
      Document assessmentDoc = null;
      assessment = deliveryBean.getAssessment();

      //TODO rewrite the code above. ignore for now.
      assessmentDoc = XmlUtil.createDocument();
      Element newAssessment =
        (Element) assessmentDoc.importNode(assessment, true);
      assessmentDoc.appendChild(newAssessment);
      deliveryBean.setUsername(
        OsidManagerFactory.getAgent().getDisplayName());
      this.saveDocument(request, assessmentDoc);
      
      QtiSettingsBean qtiSettingBean = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedAssessmentId);
      
      deliveryBean.setQtiSettingBean(this.setQtiSettingsDeliveryBean(qtiSettingBean));
      
      return this.findForward(
        mapping, request, deliveryForm, deliveryBean, "Entry");
    }
    catch(SharedException ex)
    {
      return mapping.findForward("INDEX");
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return mapping.findForward("INDEX");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected ActionForward retrieveTest2(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    String assessmentId = null;
    if(request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY) == null) {
      
      assessmentId = (String) request.getAttribute(XmlSelectAction.ASSESSMENT_ID_KEY);
      
    } else {
    
      assessmentId = (String) request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY);
    }
    try
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "retrieveTest(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
          deliveryForm + ", " + "XmlDeliveryBean , " +
          "HttpServletRequest request)");
      }

      Document questtestInterop = 
        (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentId);

      deliveryBean = new XmlDeliveryBean();
      deliveryBean.setQuesttestInterop(questtestInterop);
      deliveryBean.setQtiResultReport(
        (new XmlDeliveryService()).getQtiResultReport(assessmentId));
      Element assessment = null;
      Document assessmentDoc = null;
      assessment = deliveryBean.getAssessment();

      assessmentDoc = XmlUtil.createDocument();
      Element newAssessment =
        (Element) assessmentDoc.importNode(assessment, true);
      assessmentDoc.appendChild(newAssessment);
      deliveryBean.setUsername(
        OsidManagerFactory.getAgent().getDisplayName());
      this.saveDocument(request, assessmentDoc);

      return this.findForward(
        mapping, request, deliveryForm, deliveryBean, "Entry");
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);

      return mapping.findForward("INDEX");
    }
    catch(SharedException ex)
    {
      return mapping.findForward("INDEX");
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return mapping.findForward("INDEX");
    }
  }
  
  public QtiSettingsDeliveryBean setQtiSettingsDeliveryBean(QtiSettingsBean qtiSettingsBean)
  {
    QtiSettingsDeliveryBean qtiSettingsDeliveryBean = new QtiSettingsDeliveryBean();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    String autoSubmit = "Disabled";
    if(qtiSettingsBean.getAutoSubmit().endsWith("True"))
    {
      autoSubmit = "Enabled";
    }

    Character autoSave= new  Character('F');
    if ((qtiSettingsBean.getAutoSave() != null) && (qtiSettingsBean.getAutoSave().equals(new Character('T'))))
    {
      autoSave = new Character('T');
    }

    String dueDate = "None";
    Timestamp dueTime = qtiSettingsBean.getEndDate();
    if(dueTime != null)
    {
      dueDate = sdf.format(dueTime);
    }

    Timestamp feedbackTime = qtiSettingsBean.getFeedbackDate();
    String feedback = "Not available";
    if(feedbackTime != null)
    {
      feedback = "Available on " + sdf.format(feedbackTime);
    }
    
    int max = qtiSettingsBean.getMaxAttempts().intValue();
    String maxAttempts = "Unlimited";
    if(max >0)
    {
      maxAttempts = String.valueOf(max); 
    }
    qtiSettingsDeliveryBean.setAutoSubmit(autoSubmit);
    qtiSettingsDeliveryBean.setAutoSave(autoSave);
    qtiSettingsDeliveryBean.setDueDate(dueDate);
    qtiSettingsDeliveryBean.setFeedback(feedback);
    qtiSettingsDeliveryBean.setMaxAttempts(maxAttempts);
    qtiSettingsDeliveryBean.setUsername(qtiSettingsBean.getUsernameRestriction());
    qtiSettingsDeliveryBean.setPassword(qtiSettingsBean.getPasswordRestriction());
    qtiSettingsDeliveryBean.setIpAddresses(qtiSettingsBean.getIpRestrictions());
    return qtiSettingsDeliveryBean;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTaken DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Element getAssessment(Document assessmentTaken)
  {
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(assessmentTaken);
      Iterator iterator =
        xsb.selectNodes("*/" + QTIConstantStrings.ASSESSMENT).iterator();
      if(iterator.hasNext())
      {
        return (Element) iterator.next();
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   * @param forwardName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ActionForward findForward(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
    String forwardName)
  {
    deliveryForm.setAssessmentId(deliveryBean.getAssessmentId());
    deliveryForm.setAssessmentTitle(deliveryBean.getAssessmentTitle());
    deliveryForm.setBeginTime(deliveryBean.getBeginTime());
    deliveryForm.setEndTime(deliveryBean.getEndTime());
    deliveryForm.setItemIndex(deliveryBean.getItemIndex());
    deliveryForm.setMarkedForReview(deliveryBean.getMarkedForReview());
    deliveryForm.setMultipleAttemps(deliveryBean.getMultipleAttemps());
    deliveryForm.setReviewAll(deliveryBean.getReviewAll());
    deliveryForm.setReviewMarked(deliveryBean.getReviewMarked());
    deliveryForm.setSubmissionTicket(deliveryBean.getSubmissionTicket());
    deliveryForm.setSubmissionTime(deliveryBean.getSubmissionTime());
    deliveryForm.setTimeElapse(deliveryBean.getTimeElapse());
    deliveryForm.setTimeOutSubmission(deliveryBean.getTimeOutSubmission());
    deliveryForm.setUsername(deliveryBean.getUsername());
    deliveryForm.setSize(deliveryBean.getSize());
    deliveryForm.setQtiSettingBean(deliveryBean.getQtiSettingBean());
    request.getSession().setAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY, deliveryBean);

    return mapping.findForward(forwardName);
  }
}
