/*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.grading;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.w3c.dom.Document;

import org.navigoproject.business.entity.AssessmentReport;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm;
import org.navigoproject.ui.web.asi.delivery.XmlSelectAction;

import com.oroad.stxx.action.Action;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SelectAssessmentReport extends Action
{  
  public ActionForward execute(ActionMapping mapping, ActionForm actionForm, 
  HttpServletRequest request, HttpServletResponse response)
  {
    XmlDeliveryForm deliveryForm = (XmlDeliveryForm)actionForm;
    
    //get the taken assessment
    String assessmentId = request.getParameter("assessmentId");    
    Document quesTestInterop = (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentId);
    this.saveDocument(request, quesTestInterop);
    
    //get the assessment report
    AssessmentReport assessmentReport = AssessmentReport.getAssessmentReport(assessmentId);
    this.saveDocument(request, assessmentReport.getDocument());
    request.getSession().setAttribute("ASSESSMENT_REPORT", assessmentReport);
    
    //create the deliveryBean
    XmlDeliveryBean deliveryBean = new XmlDeliveryBean();
    deliveryBean.setQuesttestInterop(quesTestInterop);
    deliveryBean.setQtiResultReport(assessmentReport.getDocument());
    request.getSession().setAttribute(XmlSelectAction.ASSESSMENT_SESSION_KEY, deliveryBean);
    
    //update deliveryBean and forward to table of contents
    this.updateDeliveryForm(deliveryForm, deliveryBean);
    return mapping.findForward("ContentTree");
  }
  
  private void updateDeliveryForm(XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
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
  }
}
