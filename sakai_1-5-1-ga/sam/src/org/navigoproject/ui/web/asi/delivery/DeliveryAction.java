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
 * Created on Feb 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Iso8601TimeInterval;
import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.ResultProcessor;
import org.navigoproject.business.entity.ResultReporter;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.business.exception.FormatException;
import org.navigoproject.data.RealizationBean;
import org.navigoproject.data.RealizationManager;
import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.service.evaluation.EvaluationServiceDelegate;
import org.navigoproject.ui.web.asi.result.ItemResult;
import org.navigoproject.util.SessionHash;
import org.navigoproject.util.XmlUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.helpers.ISO8601DateFormat;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.DocumentImpl;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.jaxen.dom.DOMXPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import osid.shared.Agent;
import osid.shared.SharedException;

import org.navigoproject.util.XmlUtil;
/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeliveryAction
  extends DeliveryBaseAction
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DeliveryAction.class);


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
  protected ActionForward beginTestAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "beginTestAction(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }

//Now check to see if autosave is on/off.
    if (request.getParameter("frame") == null){

      	if(deliveryBean.getQtiSettingBean().getAutoSave().equals(new Character('T')))
        {
		// Autosave is on, show frameset page
        	deliveryForm.setAutoSave("true");

	 	// show test in frames.
    		return this.findForward(
  		mapping, request, deliveryForm, deliveryBean, this.BEGINTESTFORAUTOSAVE);  	

	}
    	else {
		// Autosave is off, continue taking test 
    		deliveryForm.setAutoSave("false");
    	}
    }


    // set applet parameter in audio question - daisyf
    String userName = "guest";
    try
    {
      if(OsidManagerFactory.getAgent() != null)
      {
        userName = OsidManagerFactory.getAgent().getDisplayName();
      }
    }
    catch(Exception e)
    {
      LOG.info("will use guest as username");
    }

    RecordingData recordingData =
      new RecordingData(userName, userName, "course settings", null, null);
    setRecordingInfoInDocument(recordingData, request);
    String publishedAssessmentId = deliveryBean.getAssessmentId();

    AuthorizationSupport authzSupport = new AuthorizationSupport(request);
    if(! authzSupport.isAuthorizedToTakeAssessment(publishedAssessmentId))
    {
      //user is not authorized to take the test.
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return mapping.findForward(this.FAILURE);
    }

    RealizationManager realizationManager = new RealizationManager();

    //    RealizationBean resultBean =
    //      realizationManager.getRealizationBean(assessmentId);
    //TODO replace the above code with code below.
    String agentIdString = this.getAgentIdString(request);
    
    if(! verifyUsernamePassword(request, deliveryBean))
    {
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return mapping.findForward(this.FAILURE);
    }

    String ip = request.getRemoteAddr();
    if(! verifyIpAddress(request, deliveryBean))
    {
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return mapping.findForward(this.FAILURE);
    }
    
    RealizationBean resultBean =
      realizationManager.getNonSubmittedRealizationBean(
        publishedAssessmentId, agentIdString);
    Document realizedAssessment = null;
    try
    {
      realizedAssessment =
        realizationManager.getRealizedAssessment(
          publishedAssessmentId, agentIdString);
      deliveryBean.setQuesttestInterop(realizedAssessment);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      //Unable to realize the assessment.
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return mapping.findForward(this.FAILURE);
    }
    
    if(resultBean != null)
    {
      Date beginTime = resultBean.getBeginTime();
      Date endTime = resultBean.getEndTime();
      if(endTime != null)
      {
        LOG.debug("Duration is not null, format begin time and end time. ");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        deliveryBean.setBeginTime(sdf.format(beginTime));
        deliveryBean.setEndTime(sdf.format(endTime));
      }
    }
    else
    {
      createRealizationBean(
        deliveryBean, publishedAssessmentId, deliveryBean.getAssessmentId(),
        agentIdString);
    }

    int totalItemNumber = deliveryBean.getItems().size();
    String totalItem = new Integer(totalItemNumber).toString();
    request.setAttribute("totalItem", totalItem);
    
    this.updateResultReport(request);
    
    Document document = this.getSaveDocument(request);
    this.saveDocument(request, document);

    if(deliveryBean.getQtiSettingBean().getAutoSave().equals(new Character('T')))
    {
// go to deliveryAutoSave.dox  
    return this.findForward(
      mapping, request, deliveryForm, deliveryBean, this.AUTOSAVETEST);
    }
    else {
    return this.findForward(
      mapping, request, deliveryForm, deliveryBean, this.SUCCESS);
    }
  }

  /**
   * @param request
   * @param deliveryBean
   * @return
   */
  private boolean verifyIpAddress(
    HttpServletRequest request, XmlDeliveryBean deliveryBean)
  {
    String beanIpAddresses = deliveryBean.getQtiSettingBean().getIpAddresses();
    if(beanIpAddresses == null)
    {
      return true;
    }
    else
    {
      String remoteAddress = request.getRemoteAddr();
      StringTokenizer token = new StringTokenizer(beanIpAddresses, ",");
      while(token.hasMoreTokens())
      {
        String ip = token.nextToken();
        if(remoteAddress.equals(ip))
        {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * @param request
   * @return
   */
  private boolean verifyUsernamePassword(
    HttpServletRequest request, XmlDeliveryBean deliveryBean)
  {
    String beanUsername = deliveryBean.getQtiSettingBean().getUsername();
    String beanPassword = deliveryBean.getQtiSettingBean().getPassword();
    String requestUsername = request.getParameter("username");
    String requestPassword = request.getParameter("password");
    if((beanUsername == null) || (beanPassword == null))
    {
      return true;
    }

    if(
      beanUsername.equals(requestUsername) &&
        beanPassword.equals(requestPassword))
    {
      return true;
    }

    return false;
  }

  /**
   * @param deliveryBean
   * @param assessmentId
   * @param agentIdString
   */
  private void createRealizationBean(
    XmlDeliveryBean deliveryBean, String assessmentPublishedId,
    String assessmentTakenId, String agentIdString)
  {
    String duration = deliveryBean.getDuration();
    Date now = new Date();
    RealizationBean storeBean = new RealizationBean();
    storeBean.setAssessmentTitle(deliveryBean.getAssessmentTitle());
    storeBean.setAssessmentPublishedId(assessmentPublishedId);
    storeBean.setAssessmentTakenId(assessmentTakenId);
    storeBean.setBeginTime(new java.sql.Timestamp(now.getTime()));
    storeBean.setSubmitted(Integer.valueOf("0"));
    storeBean.setAgentId(agentIdString);
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    deliveryBean.setBeginTime(sdf.format(now));

    if((duration != null) && ! ("PT0H0M").equals(duration))
    {
      LOG.debug("Duration is not null, format begin time and end time. ");

      long millSeconds = new Iso8601TimeInterval(duration).getDuration();
      Date end = new Date(now.getTime() + millSeconds);
      if(deliveryBean.getDueDate() != null)
      {
        if(deliveryBean.getDueDate().before(end))
        {
          end = deliveryBean.getDueDate();
        }
      }

      deliveryBean.setEndTime(sdf.format(end));
      storeBean.setEndTime(new java.sql.Timestamp(end.getTime()));
    }
    else if(
      (deliveryBean.getDueDate() != null) && ! deliveryBean.isLateHandling())
    {
      Date end = deliveryBean.getDueDate();
      deliveryBean.setEndTime(sdf.format(end));
      storeBean.setEndTime(new java.sql.Timestamp(end.getTime()));
    }

    PersistenceService.getInstance().getQtiQueries().persistRealizationBean(
      storeBean);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param deliveryBean DOCUMENTATION PENDING
   * @param assessmentId DOCUMENTATION PENDING
   */
  private void createRealizationBean(
    XmlDeliveryBean deliveryBean, String assessmentId)
  {
    String duration = deliveryBean.getDuration();
    Date now = new Date();
    RealizationBean storeBean = new RealizationBean();    
    storeBean.setAssessmentPublishedId(assessmentId);
    storeBean.setAssessmentTitle(deliveryBean.getAssessmentTitle());
    storeBean.setAssessmentTakenId(assessmentId);
    storeBean.setBeginTime(new java.sql.Timestamp(now.getTime()));
    storeBean.setSubmitted(Integer.valueOf("0"));

    if((duration != null) && ! ("PT0H0M").equals(duration))
    {
      LOG.debug("Duration is not null, format begin time and end time. ");
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
      deliveryBean.setBeginTime(sdf.format(now));
      long millSeconds = new Iso8601TimeInterval(duration).getDuration();
      Date end = new Date(now.getTime() + millSeconds);
      if(deliveryBean.getDueDate() != null)
      {
        if(deliveryBean.getDueDate().before(end))
        {
          end = deliveryBean.getDueDate();
        }

        deliveryBean.setEndTime(sdf.format(end));
      }

      storeBean.setEndTime(new java.sql.Timestamp(end.getTime()));
    }

    PersistenceService.getInstance().getQtiQueries().persistRealizationBean(
      storeBean);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param iso8601DateFormatString DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Date getDate(String iso8601DateFormatString)
  {
    ISO8601DateFormat dateFormat = new ISO8601DateFormat();
    Date date = null;
    try
    {
      date = dateFormat.parse(iso8601DateFormatString);
    }
    catch(ParseException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return date;
  }

  /**
   * @param mapping
   * @param request
   * @param deliveryForm
   * @param deliveryBean
   * @return
   */
  protected ActionForward cancelReviewAction(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "cancelReviewAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    deliveryBean.setReviewBlank(false);
    deliveryBean.setReviewMarked(false);
    deliveryBean.setReviewAll(false);

    return this.findForwardSummary(
      mapping, request, deliveryForm, deliveryBean);
  }

  /**
   * @param mapping
   * @param deliveryForm
   * @param deliveryBean
   * @param request
   * @return
   */
  protected ActionForward submitAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "submitAction(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }

    try
    {
      Date now = new Date();
      Date dueDate = deliveryBean.getDueDate();
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
      deliveryBean.setSubmissionTime(sdf.format(now));
      if(deliveryBean.getBeginTime() != null)
      {
        Date beginTime = sdf.parse(deliveryBean.getBeginTime());
        long remaining = now.getTime() - beginTime.getTime();
        long days = (remaining - (remaining % 86400000)) / 86400000;
        remaining = remaining - (days * 86400000);
        long hours = (remaining - (remaining % 3600000)) / 3600000;
        remaining = remaining - (hours * 3600000);
        long minutes = (remaining - (remaining % 60000)) / 60000;
        remaining = remaining - (minutes * 60000);
        long seconds = (remaining - (remaining % 1000)) / 1000;

        deliveryBean.setTimeElapse(
          ("" + days + "days " + hours + "hrs " + minutes + "mins " + seconds +
          "secs "));
      }

      RealizationBean bean =
        (new RealizationManager()).getNonSubmittedRealizationBean(
          deliveryBean.getAssessmentId());
      bean.setSubmitted(Integer.valueOf("1"));
      bean.setSubmissionTime(new java.sql.Timestamp(now.getTime()));
      if((dueDate != null) && now.after(dueDate))
      {
        bean.setLateSubmission(new Integer(1));
      }

      //bean.store();
      PersistenceService.getInstance().getQtiQueries().persistRealizationBean(
        bean);

      this.callEvaluationService(request);

      deliveryBean.setSubmissionTicket(deliveryBean.getAssessmentId());
      super.saveDocument(request, deliveryBean.getQuesttestInterop());
      ActionForward receiptPage =
        this.findForward(
          mapping, request, deliveryForm, deliveryBean, this.RECEIPT);
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return receiptPage;
    }
    catch(ParseException parseEx)
    {
      return mapping.findForward(this.FAILURE);
    }
  }

  /**
   * @param mapping
   * @param deliveryForm
   * @param deliveryBean
   * @param request
   * @return
   */
  protected ActionForward reviewAllAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "reviewAllAction(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }

    deliveryBean.setReviewMarked(false);
    deliveryBean.setReviewBlank(false);
    deliveryBean.setReviewAll(true);

    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "nextQuestionAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    Document document = this.getSaveDocument(request);
    this.saveDocument(request, document);

    if((document == null) || (document.getDocumentElement() == null))
    {
      return this.findForwardSummary(
        mapping, request, deliveryForm, deliveryBean);
    }

    return this.findForward(
      mapping, request, deliveryForm, deliveryBean, this.SUCCESS);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected ActionForward autoSaveInputAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "autoSaveInputAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }
    // auto save user input 
    this.saveInputs2(request);
    return this.findForward(mapping, request, deliveryForm, deliveryBean, this.RECEIPT);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected ActionForward autoSaveAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "saveAndExitAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    //When auto save is called make sure that input got saved so call saveInputs2 instead. 
    this.saveInputs2(request);

    return this.submitAction(mapping, deliveryForm, deliveryBean, request);
  }

  /*
   * Cancel assessment delivering, clean XmlDeliveryBean from session.
   */
  protected ActionForward cancelAction(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
  {
    request.getSession().removeAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY);

    deliveryBean.release();

    return mapping.findForward("Cancel");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  protected void saveInputs(HttpServletRequest request)
    throws LateHandlingException
  {
    if(
      request.getSession().getAttribute(XmlSelectAction.ASSESSMENT_SESSION_KEY) == null)
    {
      // a work around when loading of an audio applet causes invalidation of a user session
      String userName = "guest";
      try
      {
        if(OsidManagerFactory.getAgent() != null)
        {
          userName = OsidManagerFactory.getAgent().getDisplayName();
        }
      }
      catch(Exception e)
      {
        LOG.info("will use guest as username");
      }

      HttpSession session = request.getSession();
      HashMap sessionMap = (HashMap) SessionHash.getInstance().get(userName);
      if(sessionMap != null)
      {
        Set keys = sessionMap.keySet();
        Iterator i = keys.iterator();
        while(i.hasNext())
        {
          String key = (String) i.next();
          session.setAttribute(key, sessionMap.get(key));
        }

        SessionHash.getInstance().remove(userName);
      }
    }

    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    LOG.debug("**deliverybean = " + deliveryBean);

    Date dueDate = deliveryBean.getDueDate();
    boolean lateHandling = deliveryBean.isLateHandling();
    Date now = new Date();
    if((dueDate == null) || now.before(dueDate) || lateHandling)
    {
      this.saveInputs2(request);
    }
    else
    {
      //TODO remind user that no answer will be saved after due date. 
      throw new LateHandlingException(
        "Due date has passed. Late Handling is not availabe.");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  protected void saveInputs2(HttpServletRequest request)
  {
    ItemResponseMap irmap = new ItemResponseMap();

    //first get all qualified parameters.
    String oldItemIdent = "";
    ArrayList itemMaredForReview = new ArrayList();
    Enumeration enumer = request.getParameterNames();
    while(enumer.hasMoreElements())
    {
      String name = (String) enumer.nextElement();
      String value = request.getParameter(name);
      try
      {
        ItemResponseIdentMap irimap = new ItemResponseIdentMap(name);
        String itemIdentRef = irimap.getItemResultIdentRef();
        irmap.put(itemIdentRef, name);
      }
      catch(FormatException fe)
      {
        /***If this exception is caught means that parameter is not a
         * qualified item response value parameter.
         ***/
        try
        {
          //Try to see if it is a markForReview identifier
          ItemIdentReviewMap iirmap = new ItemIdentReviewMap(name);
          String itemIdentReviewRef = iirmap.getItemIdentRef();
          itemMaredForReview.add(itemIdentReviewRef);
        }
        catch(FormatException fe2)
        {
          /*** do nothing here***/
        }
      }
    }
    
    this.saveItemResults(request, irmap);
    this.saveMarkedForReview(request, itemMaredForReview);
    this.updateResultReport(request);
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
  protected ActionForward previewAssessmentAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    try
    {
      String assessmentId = null;
      if(request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY) == null)
      {
        assessmentId =
          (String) request.getAttribute(XmlSelectAction.ASSESSMENT_ID_KEY);
      }
      else
      {
        assessmentId =
          (String) request.getParameter(XmlSelectAction.ASSESSMENT_ID_KEY);
      }

      Document questtestInterop =
        (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentId);

      deliveryBean = new XmlDeliveryBean();
      deliveryBean.setQuesttestInterop(questtestInterop);
      deliveryBean.setQtiResultReport(
        (new XmlDeliveryService()).getQtiResultReport(assessmentId));

      return this.findForward(
        mapping, request, deliveryForm, deliveryBean, this.PREVIEWASSESSMENT);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);

      return mapping.findForward("INDEX");
    }
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

//    if("IMMEDIATE".equalsIgnoreCase(deliveryBean.getFeedback()))
//    {
      //get current resultReport
      ServletContext context = this.getServlet().getServletContext();
      Document questtestInterop = deliveryBean.getQuesttestInterop();
      ResultProcessor processor =
        new ResultProcessor(context, questtestInterop);
      Document unprocessedReport = ResultReporter.getAssessmentResult(deliveryBean.getAssessmentId());
      Document resultReport = processor.processResults(unprocessedReport);
      deliveryBean.setQtiResultReport(resultReport);
//    }
  }

  /**
   * @param request
   * @param itemMaredForReview
   */
  private void saveMarkedForReview(
    HttpServletRequest request, ArrayList itemMarkedForReview)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
    for(int i = 0; i < itemMarkedForReview.size(); i++)
    {
      String itemIdent = (String) itemMarkedForReview.get(i);
      int index = this.identToIndex(deliveryBean.getItems(), itemIdent);
      deliveryBean.getMarkedMap().setBit(index);
    }
  }

  /**
   * @param oldItemIdent
   * @return
   */
  private Element getItemElement(
    HttpServletRequest request, String oldItemIdent)
  {
    Element assessment = this.getAssessmentElement(request);
    Element item = null;
    try
    {
      XPath xpath = new DOMXPath("*/item[@ident=\"" + oldItemIdent + "\"]");
      List items = xpath.selectNodes(assessment);
      item = (Element) items.get(0);
    }
    catch(JaxenException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return item;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param map DOCUMENTATION PENDING
   */
  private void saveItemResults(HttpServletRequest request, ItemResponseMap map)
  {
    //Update QTI_ASSESSMENT_TAKEN table.
    //    updateForLateSubmission(request);
    for(int i = 0; i < map.size(); i++)
    {
      ArrayList itemResponseValues = map.get(i);
      String itemIdent = map.getKey(i);
      this.saveItemResult(request, itemResponseValues, itemIdent);
    }
  }

  /**
   * @param request
   */

  //  private void updateForLateSubmission(HttpServletRequest request)
  //  {
  //    XmlDeliveryBean deliveryBean =
  //      (XmlDeliveryBean) request.getSession().getAttribute(
  //        XmlSelectAction.ASSESSMENT_SESSION_KEY);
  //    if(deliveryBean.getLateSubmission() == 1)
  //    {
  //      RealizationManager realizationManager = new RealizationManager();
  //      RealizationBean realizationBean = realizationManager.getRealizationBean(deliveryBean.getAssessmentId());
  //      if(realizationBean != null)
  //      {
  //        if(realizationBean.getSubmitted().intValue() == 0)
  //        {
  //          realizationBean.setLateSubmission(Integer.valueOf("1"));
  //          realizationBean.store();
  //        }
  //      }
  //    }
  //  }

  /**
   * @param itemResponseValues
   * @param item
   * @param oldItemIdent
   */
  private void saveItemResult(
    HttpServletRequest request, ArrayList itemResponseValues, String itemIdent)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
    Element itemElement = getItemElement(request, itemIdent);
    Element itemResultElement = getCachedItemResult(deliveryBean, itemIdent);

    ItemResult itemResult =
      new ItemResult(
        deliveryBean.getAssessmentId(), itemIdent, itemElement,
        itemResultElement, itemResponseValues);

    itemResult.updateItemResult(request);

    //update local cache , comment this out because update resultReport will accomplish the same thing.
    // Also the current updateLocalCache method append item_result directly under assessment_result didn't
    // reflect the A/S/I structure.
    //    updateLocalCache(deliveryBean, itemResult.getItemResult());
    //record empty response if applicable.
    if(itemResult.isEmptyResponseValue())
    {
      recordEmptyResponse(deliveryBean, itemIdent);
    }
    else
    {
      recordNonEmptyResponse(deliveryBean, itemIdent);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param deliveryBean DOCUMENTATION PENDING
   */
  private void recordEmptyResponse(
    XmlDeliveryBean deliveryBean, String itemIdent)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("recordEmptyResponse(XmlDeliveryBean " + deliveryBean + ")");
    }

    //     ArrayList blankItems = deliveryBean.getBlankItems();
    int blankItemIndex = this.identToIndex(deliveryBean.getItems(), itemIdent);
    deliveryBean.getBlankMap().setBit(blankItemIndex);
    deliveryBean.getMarkedMap().clearBit(blankItemIndex);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param deliveryBean DOCUMENTATION PENDING
   */
  private void recordNonEmptyResponse(
    XmlDeliveryBean deliveryBean, String itemIdent)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("recordNonEmptyResponse(XmlDeliveryBean " + deliveryBean + ")");
    }

    //     ArrayList blankItems = deliveryBean.getBlankItems();
    int NonblankItemIndex =
      this.identToIndex(deliveryBean.getItems(), itemIdent);
    deliveryBean.getBlankMap().clearBit(NonblankItemIndex);
    deliveryBean.getMarkedMap().clearBit(NonblankItemIndex);
  }

  /**
   * @param request
   * @param deliveryBean
   * @return
   */
  private Element getCachedItemResult(
    XmlDeliveryBean deliveryBean, String itemIdentRef)
  {
    Element itemResult = null;
    try
    {
      itemResult = getItemResult(deliveryBean, itemIdentRef);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return itemResult;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param deliveryBean DOCUMENTATION PENDING
   * @param itemResult DOCUMENTATION PENDING
   */
  private void updateLocalCache(
    XmlDeliveryBean deliveryBean, Element itemResult)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateLocalCache(XmlDeliveryBean " + deliveryBean + ", Element " +
        itemResult + ")");
    }

    String itemIdentRef = itemResult.getAttribute(QTIConstantStrings.IDENT_REF);
    XmlStringBuffer xsb =
      new XmlStringBuffer(deliveryBean.getQtiResultReport());
    String xpath = ITEMRESULTPATH + "[@ident_ref=\"" + itemIdentRef + "\"]";
    xsb.update(xpath, itemResult);
    System.out.print(xsb.stringValue());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param deliveryBean DOCUMENTATION PENDING
   * @param itemIdentRef DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Element getItemResult(
    XmlDeliveryBean deliveryBean, String itemIdentRef)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getItemResult(XmlDeliveryBean " + deliveryBean + ", String " +
        itemIdentRef + ")");
    }

    try
    {
      XmlStringBuffer xsb =
        new XmlStringBuffer(deliveryBean.getQtiResultReport());
      LOG.debug(xsb.stringValue());

      String itemResultPathWithIdent =
        ITEMRESULTPATH + "[@ident_ref=\"" + itemIdentRef + "\"]";
      LOG.debug("itemResultPath: " + itemResultPathWithIdent);

      return selectFirstElement(xsb, itemResultPathWithIdent);
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
   * @param itemPaths DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private int identToIndex(ArrayList itemPaths, String itemIdent)
  {
    int size = itemPaths.size();
    for(int i = 0; i < size; i++)
    {
      String path = (String) itemPaths.get(i);
      int indexOfIdent = path.indexOf(itemIdent);
      if(indexOfIdent > 0)
      {
        return i;
      }
    }

    return -1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param recordingData DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  private void setRecordingInfoInDocument(
    RecordingData recordingData, HttpServletRequest request)
  {
    Document document = recordingData.getXMLDataModel();
    saveDocument(request, document);
  }

  /**
   * @param request
   */
  private void callEvaluationService(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
    Element assessment = deliveryBean.getAssessment();
    Document qtiResultDoc = this.getResultDocument(request);

    Document combinedDoc = XmlUtil.createDocument();
    Element root = combinedDoc.createElement("assessment_taken");

    Node newAssessment = combinedDoc.importNode(assessment, true);
    Node newQtiResultDoc =
      combinedDoc.importNode(qtiResultDoc.getDocumentElement(), true);

    root.appendChild(newAssessment);
    root.appendChild(newQtiResultDoc);
    combinedDoc.appendChild(root);

    EvaluationServiceDelegate evalDelegate;
    ServletContext context = context = request.getSession().getServletContext();

    try
    {
      evalDelegate = new EvaluationServiceDelegate();
      evalDelegate.setGradeFromDocument(combinedDoc, context);
    }
    catch(Exception ex)
    {
      LOG.error(
        "Exception found in calling EvaluationServiceDelegate.setGradeFromDocument ()");
      LOG.error(ex);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Agent getAgent(HttpServletRequest request)
  {
    Agent agent = null;
    try
    {
      agent = org.navigoproject.osid.OsidManagerFactory.getAgent();
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return agent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String getAgentIdString(HttpServletRequest request)
  {
    String idString = "";
    try
    {
      idString = this.getAgent(request).getId().getIdString();
    }
    catch(SharedException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return idString;
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
  protected ActionForward lateHandlingSubmit(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "submitAction(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }

    try
    {
      Date now = new Date();
      Date dueDate = deliveryBean.getDueDate();
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
      deliveryBean.setSubmissionTime(sdf.format(now));
      if(deliveryBean.getBeginTime() != null)
      {
        Date beginTime = sdf.parse(deliveryBean.getBeginTime());
        long remaining = now.getTime() - beginTime.getTime();
        long days = (remaining - (remaining % 86400000)) / 86400000;
        remaining = remaining - (days * 86400000);
        long hours = (remaining - (remaining % 3600000)) / 3600000;
        remaining = remaining - (hours * 3600000);
        long minutes = (remaining - (remaining % 60000)) / 60000;
        remaining = remaining - (minutes * 60000);
        long seconds = (remaining - (remaining % 1000)) / 1000;

        deliveryBean.setTimeElapse(
          ("" + days + "days " + hours + "hrs " + minutes + "mins " + seconds +
          "secs "));
      }

      RealizationBean bean =
        (new RealizationManager()).getNonSubmittedRealizationBean(
          deliveryBean.getAssessmentId());
      bean.setSubmitted(Integer.valueOf("1"));
      bean.setSubmissionTime(new java.sql.Timestamp(now.getTime()));
      bean.setLateSubmission(new Integer(0));

      PersistenceService.getInstance().getQtiQueries().persistRealizationBean(
        bean);

      this.callEvaluationService(request);

      deliveryBean.setSubmissionTicket(deliveryBean.getAssessmentId());
      ActionForward receiptPage =
        this.findForward(
          mapping, request, deliveryForm, deliveryBean, "LateHandling");
      request.getSession().removeAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
      deliveryBean.release();

      return receiptPage;
    }
    catch(ParseException parseEx)
    {
      return mapping.findForward(this.FAILURE);
    }
  }
}
