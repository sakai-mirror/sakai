/*
 * Created on Mar 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.review;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.navigoproject.business.entity.ResultProcessor;
import org.navigoproject.business.entity.ResultReporter;
import org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm;
import org.navigoproject.ui.web.asi.delivery.XmlSelectAction;
import org.w3c.dom.Document;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReviewAction extends DeliveryBaseAction
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ReviewAction.class);
  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#beginTestAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward beginTestAction(ActionMapping mapping, XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "nextQuestion(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }
    
    //process result. 
    this.updateResultReport(request);
    String questionLayout = request.getParameter("questionLayout");
    deliveryBean.setQuestionLayout(questionLayout);
        
    Document document = this.getSaveDocument(request);
    this.saveDocument(request, document);

    return this.findForward(
      mapping, request, deliveryForm, deliveryBean, this.SUCCESS);
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#cancelReviewAction(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean)
   */
  protected ActionForward cancelReviewAction(ActionMapping mapping, HttpServletRequest request, XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#submitAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward submitAction(ActionMapping mapping, XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#reviewAllAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward reviewAllAction(ActionMapping mapping, XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }

 /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#autoSaveInputAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward autoSaveInputAction(ActionMapping mapping, XmlDeliveryBean deliveryBean, XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }


  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#autoSaveAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward autoSaveAction(ActionMapping mapping, XmlDeliveryBean deliveryBean, XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#cancelAction(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean)
   */
  protected ActionForward cancelAction(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
  {
    request.getSession().removeAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY);
    return mapping.findForward("Cancel");
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#saveInputs(javax.servlet.http.HttpServletRequest)
   */
  protected void saveInputs(HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#previewAssessmentAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward previewAssessmentAction(ActionMapping mapping, XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#nextQuestionAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward nextQuestionAction(
    ActionMapping mapping,
    XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm,
    HttpServletRequest request)
  {
    // enable all feedback display at all time based on settings in feedbackComponent. 
    deliveryForm.setFeedbackComponent(deliveryBean.getFeedbackComponent());
    return super.nextQuestionAction(
      mapping,
      deliveryBean,
      deliveryForm,
      request);
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#previousQuestionAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward previousQuestionAction(
    ActionMapping mapping,
    XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm,
    HttpServletRequest request)
  {
    // enable all feedback display at all time based on settings in feedbackComponent. 
    deliveryForm.setFeedbackComponent(deliveryBean.getFeedbackComponent());
    return super.previousQuestionAction(
      mapping,
      deliveryBean,
      deliveryForm,
      request);
  }

  /* (non-Javadoc)
   * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#saveAndExitAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, javax.servlet.http.HttpServletRequest)
   */
  protected ActionForward saveAndExitAction(
    ActionMapping mapping,
    XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm,
    HttpServletRequest request)
  {
    // will just exit the review  without saving
    ActionForward forwardPage =
      this.findForward(
        mapping, request, deliveryForm, deliveryBean, "SELECT_ASSESSMENT");
    request.getSession().removeAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY);
    deliveryBean.release();

    return forwardPage;
  }
  
  /**
   * @param request
   */
  private void updateResultReport(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    // TODO only if the feedback type is immediate. 
    if("IMMEDIATE".equalsIgnoreCase(deliveryBean.getFeedback()))
    {
      //get current resultReport
      ServletContext context = this.getServlet().getServletContext();
      Document questtestInterop = deliveryBean.getQuesttestInterop();
      ResultProcessor processor =
        new ResultProcessor(context, questtestInterop);
      Document unprocessedReport =
        ResultReporter.getAssessmentResult(deliveryBean.getAssessmentId());
      Document resultReport = processor.processResults(unprocessedReport);
      deliveryBean.setQtiResultReport(resultReport);
    }
  }

}
