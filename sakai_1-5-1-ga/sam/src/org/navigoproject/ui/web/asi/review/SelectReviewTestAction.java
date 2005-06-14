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
package org.navigoproject.ui.web.asi.review;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.Iso8601DateFormat;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.data.RealizationBean;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;
import org.navigoproject.ui.web.asi.delivery.XmlSelectAction;
import org.navigoproject.util.XmlUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.DocumentImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import osid.authentication.AuthenticationException;

import osid.dr.DigitalRepositoryException;

import osid.shared.SharedException;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class SelectReviewTestAction
  extends Action
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SelectReviewTestAction.class);
  public final static String ASSESSMENT_SESSION_KEY =
    "org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean";
  public final static String ASSESSMEND_ID_KEY = "assessmentId";

  //  public final static String ASSESSMENT_ID =
  //    "20030819120758701-898481440418557591-iu.edu";

  /* (non-Javadoc)
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */

  // Create a new XmlSelectAction object
  public SelectReviewTestAction()
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
   
   //TODO make sure that user is authorized to access this assessment.

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

      //      deliveryBean.setAssessmentTaken(assessmentTaken);
      deliveryBean.setQuesttestInterop(questtestInterop);
      deliveryBean.setQtiResultReport(
        (new XmlDeliveryService()).getQtiResultReport(assessmentId));
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
        
      //TODO make sure that feedback is available at this time. 

      Assessment assessmentXml = new Assessment(questtestInterop);
     
      String feedbackType = assessmentXml.getFieldentry("FEEDBACK_DELIVERY");
      if("DATED".equalsIgnoreCase(feedbackType)){
        List list =
          PersistenceService.getInstance().getQtiQueries().returnGetAllRealizations(assessmentId);
        
        if(list.size()>0)
        {
          RealizationBean rbean = (RealizationBean)list.get(0);
          String publishedId = rbean.getAssessmentPublishedId();
          QtiSettingsBean qsBean = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedId);
          Timestamp feedbackDate= qsBean.getFeedbackDate();
          Calendar now = Calendar.getInstance();
          if(now.getTimeInMillis() < feedbackDate.getTime()){
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            String feedbackDateString = sdf.format(feedbackDate);
            deliveryForm.setErrorMessage("Feedback will be available on" + feedbackDateString +".");
            return mapping.findForward("Feedback");
          }
        } 
      }
      else if("NONE".equalsIgnoreCase(feedbackType))
      {
        deliveryForm.setErrorMessage("There is no feedback available for this assessment.  Please contact your instructor if you have any questions.");
        return mapping.findForward("Feedback");
      }        
      this.saveDocument(request, assessmentDoc);

      //This entry page will allow user to set A/S/I level of display. 
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
    request.getSession().setAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY, deliveryBean);

    return mapping.findForward(forwardName);
  }
}
