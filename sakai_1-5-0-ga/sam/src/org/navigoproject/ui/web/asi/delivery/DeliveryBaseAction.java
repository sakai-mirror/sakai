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
 * Created on Feb 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.util.SessionHash;
import org.navigoproject.util.XmlUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.DocumentImpl;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.jaxen.dom.DOMXPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import osid.authentication.AuthenticationException;

import osid.dr.DigitalRepositoryException;

import osid.shared.SharedException;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class DeliveryBaseAction
  extends Action
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DeliveryBaseAction.class);

  //  public static final String ITEMRESULTPATH =
  //    "navigo_taken_assessment/qti_result_report/result/assessment_result/item_result";
  //  public static final String ITEMRESULTPATH =
  //    "qti_result_report/result/assessment_result/item_result";
  public static final String ITEMRESULTPATH = "//item_result";
  public static final String SECTIONPATH = "questestinterop/assessment/section";
  public final String CANCEL = "Cancel";
  public final String SUCCESS = "Success";
  public final String BEGINTESTFORAUTOSAVE= "BeginTestForAutoSave";
  public final String AUTOSAVETEST= "AutoSaveTest";
  public final String FAILURE = "Failure";
  public final String SUMMARY = "ContentTree";
  public final String CONFIRM = "Confirm";
  public final String RECEIPT = "Receipt";
  public final String PREVIEWASSESSMENT = "previewAssessment";

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
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "execute(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest request, " +
        "HttpServletResponse response)");
    }

    XmlDeliveryForm deliveryForm = (XmlDeliveryForm) actionForm;
    String action = deliveryForm.getAction();

    // when loading the audio applet "CapturePalyback.jar", the orginal session
    // associated with the user was invalidated. To get round this problem, we stored
    // the attributes & values of the session in SessionHash with the userName just before
    // the applet is loading. Then we re-insert the attributes & values into the new session
    if(
      request.getSession().getAttribute(XmlSelectAction.ASSESSMENT_SESSION_KEY) == null)
    {
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
        LOG.info("***sessionMap = " + sessionMap);
        Set keys = sessionMap.keySet();
        Iterator i = keys.iterator();
        while(i.hasNext())
        {
          String key = (String) i.next();
          session.setAttribute(key, sessionMap.get(key));
          LOG.info("***get " + key + "=" + sessionMap.get(key));
        }

        SessionHash.getInstance().remove(userName);
      }
    }

    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    String method = request.getMethod();
    String markedItemIndex = request.getParameter("markedItemIndex");
    String blankItemIndex = request.getParameter("blankItemIndex");
    String itemIndex = request.getParameter("itemIndex");

    if(
      (markedItemIndex != null) | (blankItemIndex != null) |
        (itemIndex != null))
    {
      return nextQuestionAction(mapping, deliveryBean, deliveryForm, request);
    }

    if(request.getParameter("saveAndContinue") != null)
    {
      String totalItemNumber = request.getParameter("totalItem");
      if(totalItemNumber != null)
        request.setAttribute("totalItem", totalItemNumber);
      if (request.getSession().getAttribute("feedback_holder") != null){
        String fb = (String) request.getSession().getAttribute("feedback_holder");
        deliveryBean.setFeedback(fb);
      }
      
      return nextQuestionAction(mapping, deliveryBean, deliveryForm, request);
    }

    if(request.getParameter("showFeedback") != null)
    {
      
      request.getSession().setAttribute("feedback_holder", deliveryBean.getFeedback());
      deliveryBean.setFeedback("nada");            
                  
      return nextQuestionAction(mapping, deliveryBean, deliveryForm, request);      
    }

    if(request.getParameter("saveAndExit") != null)
    {
      return saveAndExitAction(mapping, deliveryBean, deliveryForm, request);
    }

    if(request.getParameter("jumpToReview") != null)
    {
      return cancelReviewAction(mapping, request, deliveryForm, deliveryBean);
    }

    // for autosave
    if(request.getParameter("frame") != null)
    {
     
       if( request.getParameter("frame").equals("beginTest"))
       {
           return beginTestAction(mapping, deliveryForm, deliveryBean, request);
       }
    }

    if(request.getParameter("beginTest") != null)
    {
      return beginTestAction(mapping, deliveryForm, deliveryBean, request);
    }

    if(request.getParameter("tableOfContents") != null)
    {
      return tableOfContentsAction(
        mapping, deliveryForm, deliveryBean, request);
    }

    if(request.getParameter("previewAssessment") != null)
    {
      XmlSelectAction xsa = new XmlSelectAction();
      xsa.retrieveTest2(mapping, deliveryForm, deliveryBean, request);

      return previewAssessmentAction(
        mapping, deliveryForm, deliveryBean, request);
    }

    if(request.getParameter("reviewAll") != null)
    {
      return reviewAllAction(mapping, deliveryForm, deliveryBean, request);
    }

    if(request.getParameter("reviewQuestions") != null)
    {
      return this.findForwardSummary(
        mapping, request, deliveryForm, deliveryBean);
    }

    if(request.getParameter("submit") != null)
    {
      return submitAction(mapping, deliveryForm, deliveryBean, request);
    }

    if(request.getParameter("cancelReview") != null)
    {
      return cancelReviewAction(mapping, request, deliveryForm, deliveryBean);
    }

    if(request.getParameter("previous") != null)
    {
      String totalItemNumber = request.getParameter("totalItem");
      if(totalItemNumber != null)
        request.setAttribute("totalItem", totalItemNumber);       
      if (request.getSession().getAttribute("feedback_holder") != null){
        String fb = (String) request.getSession().getAttribute("feedback_holder");
        deliveryBean.setFeedback(fb);
      }
      
      return this.previousQuestionAction(
        mapping, deliveryBean, deliveryForm, request);
    }

    if(request.getParameter("returnToTestsAndSurveys") != null)
    {
      return cancelAction(mapping, request, deliveryForm, deliveryBean);
    }
    if(request.getParameter("autoSave") != null)
    {
      return autoSaveInputAction(mapping, deliveryBean, deliveryForm, request);
    }

    if(method.equals("POST"))
    {
      return autoSaveAction(mapping, deliveryBean, deliveryForm, request);
    }
    else
    {
      return cancelAction(mapping, request, deliveryForm, deliveryBean);
    }
  }

  /*************************************/
  /********  Abstract methods  *********/
  /*************************************/
  protected abstract ActionForward beginTestAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request);


  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected abstract ActionForward previewAssessmentAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request);

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected abstract ActionForward cancelReviewAction(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean);

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
  protected abstract ActionForward submitAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request);

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
  protected abstract ActionForward reviewAllAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request);

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
  protected abstract ActionForward autoSaveAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request);

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
  protected abstract ActionForward autoSaveInputAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request);

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param deliveryForm DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected abstract ActionForward cancelAction(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean);

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  protected abstract void saveInputs(HttpServletRequest request)
    throws LateHandlingException;

  /************************************/
  /****** Protected Methods *******/
  /************************************/
  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param document DOCUMENTATION PENDING
   */
  protected void saveDocument(HttpServletRequest request, Document document)
  {
    super.saveDocument(request, document);
    Document result = this.getResultDocument(request);
    if(result != null)
    {
      super.saveDocument(request, result);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Element getSaveElement(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    String questionLayout = deliveryBean.getQuestionLayout();
    Element element = null;
    if("A".equals(questionLayout))
    {
      if(
        (request.getParameter("saveAndContinue") == null) ||
          (request.getParameter("showFeedback") != null))
      {
        element = getAssessmentElement(request);
      }
    }

    if("S".equals(questionLayout))
    {
      element = getSectionElement(request);
    }

    if("I".equals(questionLayout))
    {
      element = getItemElement(request);
    }

    if(questionLayout == null)
    {
      element = getItemElement(request);
    }

    return element;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Element getAssessmentElement(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    Element assessment = deliveryBean.getAssessment();

    return assessment;
  }

  /**
   * gets section Element object from assessment Element object
   * stored in XmlDeliveryBean object based on section xpath
   * constructed using parameters from request.
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Element getSectionElement(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    //    Document assessmentTaken = deliveryBean.getAssessmentTaken();
    Document questtestInterop = deliveryBean.getQuesttestInterop();

    Element section = null;
    int sectionIndex = this.getSectionIndex(request);

    //    String sectionPath =
    //      "navigo_taken_assessment/questestinterop/assessment/section";
    try
    {
      XPath xpath = new DOMXPath(SECTIONPATH);
      List nodes = xpath.selectNodes(questtestInterop);
      if((nodes.size() > 0) && (sectionIndex < nodes.size()))
      {
        section = (Element) nodes.get(sectionIndex);
      }
    }
    catch(JaxenException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return section;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Element getItemElement(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    int itemIndex = getItemIndex(request);
    deliveryBean.setItemIndex(itemIndex);

    Element item = null;
    try
    {
      if((itemIndex >= 0) && (itemIndex < deliveryBean.getItems().size()))
      {
        XmlStringBuffer xsb =
          new XmlStringBuffer(deliveryBean.getQuesttestInterop());
        String path = (String) deliveryBean.getItems().get(itemIndex);
        List items = xsb.selectNodes(path);
        item = (Element) items.iterator().next();
        this.saveSectionPM(path, xsb, deliveryBean);
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    if(item != null)
    {
      return (Element) item.cloneNode(true);
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
   *
   * @return DOCUMENTATION PENDING
   */
  protected ActionForward findForwardSummary(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "findForwardSummary(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    Element assessment = deliveryBean.getAssessment();

    Document document = XmlUtil.createDocument();
    Element newAssessment = (Element) document.importNode(assessment, true);
    document.appendChild(newAssessment);
    this.saveDocument(request, document);

    return this.findForward(
      mapping, request, deliveryForm, deliveryBean, this.SUMMARY);
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
  protected ActionForward findForward(
    ActionMapping mapping, HttpServletRequest request,
    XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
    String forwardName)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "findForward(ActionMapping " + mapping + ", " + "XmlDeliveryForm " +
        deliveryForm + ", " + "XmlDeliveryBean " + deliveryBean + ", " +
        "HttpServletRequest request");
    }

    deliveryForm.setAssessmentId(deliveryBean.getAssessmentId());
    deliveryForm.setAssessmentTitle(deliveryBean.getAssessmentTitle());
    deliveryForm.setBeginTime(deliveryBean.getBeginTime());
    deliveryForm.setEndTime(deliveryBean.getEndTime());
    deliveryForm.setCurrentTime(this.getCurrentTime());
    deliveryForm.setItemIndex(deliveryBean.getItemIndex());
    deliveryForm.setSectionIndex(deliveryBean.getSectionIndex());
    deliveryForm.setMarkedForReview(deliveryBean.getMarkedForReview());
    deliveryForm.setBlankItems(deliveryBean.getBlankItems());
    deliveryForm.setReviewBlank(deliveryBean.isReviewBlank());
    deliveryForm.setMultipleAttemps(deliveryBean.getMultipleAttemps());
    deliveryForm.setReviewAll(deliveryBean.getReviewAll());
    deliveryForm.setReviewMarked(deliveryBean.getReviewMarked());
    deliveryForm.setSubmissionTicket(deliveryBean.getSubmissionTicket());
    deliveryForm.setSubmissionTime(deliveryBean.getSubmissionTime());
    deliveryForm.setTimeElapse(deliveryBean.getTimeElapse());
    deliveryForm.setTimeOutSubmission(deliveryBean.getTimeOutSubmission());
    deliveryForm.setUsername(deliveryBean.getUsername());
    deliveryForm.setSize(deliveryBean.getSize());
    deliveryForm.setDuration(deliveryBean.getDuration());
    deliveryForm.setCreatorName(deliveryBean.getCreatorName());
    if((deliveryBean.getItemIndex() > 0) |
        (deliveryBean.getSectionIndex() > 0))
    {
      deliveryForm.setPrevious(true);
    }
    else
    {
      deliveryForm.setPrevious(false);
    }

    /**
       private String questionLayout;
       private String navigation;
       private String numbering;
       private String feedback;
       private String feedbackDate;
     **/
    //Settings
    deliveryForm.setQuestionLayout(deliveryBean.getQuestionLayout());
    deliveryForm.setNavigation(deliveryBean.getNavigation());
    deliveryForm.setNumbering(deliveryBean.getNumbering());
    deliveryForm.setFeedback(deliveryBean.getFeedback());
    if(
      (request.getParameter("showFeedback") != null) ||
        (request.getParameter("tableOfContents") != null))
    {
      deliveryForm.setFeedbackComponent(deliveryBean.getFeedbackComponent());
    }

if (deliveryBean.getBlankItems()!=null){
    ArrayList blankItemIdents =
      getItemIdents(deliveryBean.getBlankItems(), deliveryBean.getItems());
    deliveryForm.setBlankItemIdents(blankItemIdents);

}
if (deliveryBean.getMarkedForReview()!=null){
    ArrayList markedItemIdents =
      getItemIdents(deliveryBean.getMarkedForReview(), deliveryBean.getItems());
    deliveryForm.setMarkedForReviewIdents(markedItemIdents);

}
    deliveryForm.setQuestionLayout(deliveryBean.getQuestionLayout());
    deliveryForm.setSectionPM(deliveryBean.getSectionPM());

    request.getSession().setAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY, deliveryBean);
    request.getSession().setAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY, deliveryBean);
    LOG.debug("deliveryBean set in DeliveryBaseAction" + deliveryBean);

    return mapping.findForward(forwardName);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xsb DOCUMENTATION PENDING
   * @param itemResultPathWithIdent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Element selectFirstElement(
    XmlStringBuffer xsb, String itemResultPathWithIdent)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "selectFirstElement(XmlStringBuffer " + xsb + ", String" +
        itemResultPathWithIdent + ")");
    }

    List itemResults = xsb.selectNodes(itemResultPathWithIdent);
    if(itemResults.size() > 0)
    {
      Element result = (Element) itemResults.get(0);

      return (Element) (result.cloneNode(true));
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Document getSaveDocument(HttpServletRequest request)
  {
    //    String action = request.getParameter("action");
    Element element = (Element) getSaveElement(request);

    Document document = XmlUtil.createDocument();
    if(element != null)
    {
      Element newItem = (Element) document.importNode(element, true);
      document.appendChild(newItem);
    }

    return document;
  }

  /**
   * @param mapping
   * @param deliveryBean
   * @param deliveryForm
   * @param request
   * @return
   */
  protected ActionForward previousQuestionAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "nextQuestionAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    //    this.saveAnswers(request);
    Document document = this.getSaveDocument(request);
    this.saveDocument(request, document);

    if((document == null) || (document.getDocumentElement() == null))
    {
      return this.findForwardSummary(
        mapping, request, deliveryForm, deliveryBean);
    }

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

  /*************************************/
  /********  Private methods  *********/
  /*************************************/

  //  /**
  //   * @param request
  //   * @return
  //   */
  //  private Document getResultDocument(HttpServletRequest request)
  //  {
  //    Document document = XmlUtil.createDocument();
  //    Element result = this.getResult(request);
  //    Element newResult = (Element) document.importNode(result, true);
  //    document.appendChild(newResult);
  //
  //    return document;
  //  }

  /**
   * @param mapping
   * @param deliveryForm
   * @param deliveryBean
   * @param request
   * @return
   */
  protected ActionForward tableOfContentsAction(
    ActionMapping mapping, XmlDeliveryForm deliveryForm,
    XmlDeliveryBean deliveryBean, HttpServletRequest request)
  {
    try
    {
      Element assessment = null;
      Document assessmentDoc = null;
      assessment = deliveryBean.getAssessment();

      //TODO rewrite the code above. ignore for now.
      assessmentDoc = XmlUtil.createDocument();
      Element newAssessment =
        (Element) assessmentDoc.importNode(assessment, true);
      assessmentDoc.appendChild(newAssessment);

      this.saveDocument(request, assessmentDoc);

      return this.findForward(
        mapping, request, deliveryForm, deliveryBean, "ContentTree");
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);

      return mapping.findForward("INDEX");
    }
  }

  /**
   * @param mapping
   * @param deliveryBean
   * @param deliveryForm
   * @param request
   * @return
   */
  protected ActionForward saveAndExitAction(
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

    try
    {
      this.saveInputs(request);
    }
    catch(LateHandlingException e)
    {
      this.lateHandlingSubmit(mapping, deliveryForm, deliveryBean, request);
    }

    ActionForward forwardPage =
      this.findForward(
        mapping, request, deliveryForm, deliveryBean, "SELECT_ASSESSMENT");
    request.getSession().removeAttribute(
      XmlSelectAction.ASSESSMENT_SESSION_KEY);
    deliveryBean.release();

    return forwardPage;
  }

  /**
   * @param mapping
   * @param deliveryBean
   * @param deliveryForm
   * @param request
   * @return
   */
  protected ActionForward nextQuestionAction(
    ActionMapping mapping, XmlDeliveryBean deliveryBean,
    XmlDeliveryForm deliveryForm, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "nextQuestionAction(ActionMapping " + mapping + ", " +
        "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
        deliveryBean + ", " + "HttpServletRequest request");
    }

    //prepare parameters needed for submitting audio question
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

    if(
      (request.getParameter("saveAndContinue") != null) ||
        (request.getParameter("showFeedback") != null))
    {
      try
      {
        this.saveInputs(request);
      }
      catch(LateHandlingException e)
      {
        return this.lateHandlingSubmit(
          mapping, deliveryForm, deliveryBean, request);
      }
    }

    Document document = this.getSaveDocument(request);

    if(
      (document.getDocumentElement() == null) &&
        ! "RANDOM".equals(deliveryBean.getNavigation()))
    {
      return this.submitAction(mapping, deliveryForm, deliveryBean, request);
    }

    if(document.getDocumentElement() == null)
    {
      return this.findForwardSummary(
        mapping, request, deliveryForm, deliveryBean);
    }

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
   * DOCUMENTATION PENDING
   *
   * @param path DOCUMENTATION PENDING
   * @param xsb DOCUMENTATION PENDING
   * @param deliveryBean DOCUMENTATION PENDING
   */
  private void saveSectionPM(
    String path, XmlStringBuffer xsb, XmlDeliveryBean deliveryBean)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "saveSectionPM(String " + path + ", XmlStringBuffer " + xsb +
        ", XmlDeliveryBean " + deliveryBean + ")");
    }

    int endIndex = path.indexOf(QTIConstantStrings.ITEM);
    String sectionPath = path.substring(0, endIndex);
    String sectionPMPath =
      sectionPath + QTIConstantStrings.PRESENTATION_MATERIAL;
    List sections = xsb.selectNodes(sectionPath.substring(0, endIndex - 1));
    Element section = (Element) sections.get(0);
    List sectionPMs = xsb.selectNodes(sectionPMPath);
    if(sectionPMs.size() > 0)
    {
      Node clonedSection = section.cloneNode(false);
      Element sectionPM = (Element) sectionPMs.get(0);
      Node clonedSectionPM = sectionPM.cloneNode(true);
      clonedSection.appendChild(clonedSectionPM);
      deliveryBean.setSectionPM(clonedSection);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private int getItemIndex(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    String markedItemIndex = request.getParameter("markedItemIndex");
    String blankItemIndex = request.getParameter("blankItemIndex");
    String itemIndexString = request.getParameter("itemIndex");

    int itemIndex = -1;

    if(markedItemIndex != null)
    {
      deliveryBean.setReviewMarked(true);
      deliveryBean.setReviewBlank(false);
      deliveryBean.setReviewAll(false);
      itemIndex = Integer.parseInt(markedItemIndex);

      return itemIndex;
    }

    if(blankItemIndex != null)
    {
      deliveryBean.setReviewBlank(true);
      deliveryBean.setReviewAll(false);
      deliveryBean.setReviewMarked(false);
      itemIndex = Integer.parseInt(blankItemIndex);

      return itemIndex;
    }

    if(itemIndexString != null)
    {
      deliveryBean.setReviewBlank(false);
      deliveryBean.setReviewAll(false);
      deliveryBean.setReviewMarked(false);
      itemIndex = Integer.parseInt(itemIndexString) - 1;

      return itemIndex;
    }

    if(deliveryBean.getReviewMarked())
    {
      //review marked items
      LOG.debug("Review marked items.");
      itemIndex =
        this.getReviewItemIndex(
          deliveryBean, deliveryBean.getMarkedForReview());
    }

    if(deliveryBean.isReviewBlank())
    {
      LOG.debug("Review blank items.");
      itemIndex =
        this.getReviewItemIndex(deliveryBean, deliveryBean.getBlankItems());
    }
    else
    {
      itemIndex = deliveryBean.getItemIndex();
      if(request.getParameter("previous") != null)
      {
        itemIndex--;
      }
      else if(
        (request.getParameter("beginTest") != null) ||
          (request.getParameter("reviewAll") != null))
      {
        itemIndex = 0;
      }

      else if(request.getParameter("showFeedback") != null)
      {
        return itemIndex;
      }

      //display items in order
      else if(itemIndex < (deliveryBean.getSize() - 1))
      {
        LOG.debug("Display items in order.");
        itemIndex++;
      }

      // reach the end of the item list, dislpaly summary page.
      else if(itemIndex >= (deliveryBean.getSize() - 1))
      {
        LOG.debug("Reach the end of item list, display summary page");
        itemIndex = -1;
      }
    }

    return itemIndex;
  }

  /**
   * @param deliveryBean
   * @param list
   * @return
   */
  private int getReviewItemIndex(XmlDeliveryBean deliveryBean, ArrayList list)
  {
    int itemIndex = -1;
    itemIndex = deliveryBean.getItemIndex();
    int index = list.indexOf(String.valueOf(itemIndex));
    if(index >= (list.size() - 1))
    {
      LOG.debug(
        "Reach the end of marked for review item list, display summary page.");

      return -1;
    }

    String itemIndexString = (String) list.get(index + 1);
    itemIndex = Integer.parseInt(itemIndexString);

    return itemIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private int getSectionIndex(HttpServletRequest request)
  {
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);
    int sectionIndex = deliveryBean.getSectionIndex();
    if(request.getParameter("previous") != null)
    {
      sectionIndex--;
    }
    else if(
      (request.getParameter("beginTest") != null) |
        (request.getParameter("reviewAll") != null))
    {
      sectionIndex = 0;
    }
    else if(request.getParameter("showFeedback") != null)
    {
      return sectionIndex;
    }
    else
    {
      sectionIndex++;
    }

    int itemIndex = getSectionItemIndex(request);
    if(itemIndex >= 0)
    {
      //itemIndex >=0 select the section that include the specified item.
      String itemXPath = (String) deliveryBean.getItems().get(itemIndex);
      int indexOfSection = itemXPath.indexOf("section");
      int sectionSlash = itemXPath.indexOf("/", indexOfSection);
      String sectionPath = itemXPath.substring(0, sectionSlash);

      //Need to renew the sectionIndex in the deliveryBean.
      ArrayList sectionList = deliveryBean.getSections();
      sectionIndex = sectionList.indexOf(sectionPath);
    }

    deliveryBean.setSectionIndex(sectionIndex);

    return sectionIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private int getSectionItemIndex(HttpServletRequest request)
  {
    int itemIndex = -1;
    String markedItemIndex = request.getParameter("markedItemIndex");
    String blankItemIndex = request.getParameter("blankItemIndex");
    String itemIndexString = request.getParameter("itemIndex");

    if(markedItemIndex != null)
    {
      itemIndex = Integer.parseInt(markedItemIndex);
    }

    if(blankItemIndex != null)
    {
      itemIndex = Integer.parseInt(blankItemIndex);
    }

    if(itemIndexString != null)
    {
      itemIndex = Integer.parseInt(itemIndexString) - 1;
    }

    return itemIndex;
  }

  /**
   * @return
   */
  protected Document getResultDocument(HttpServletRequest request)
  {
    LOG.info("session in getResultDocument=" + request.getSession());
    XmlDeliveryBean deliveryBean =
      (XmlDeliveryBean) request.getSession().getAttribute(
        XmlSelectAction.ASSESSMENT_SESSION_KEY);

    return deliveryBean.getQtiResultReport();
  }

  /**
   * @param list
   * @param i
   * @return
   */
  private ArrayList getItemIdents(ArrayList itemIndexs, ArrayList itemXpaths)
  {
    ArrayList list = new ArrayList();
    for(int i = 0; i < itemIndexs.size(); i++)
    {
      String index = (String) itemIndexs.get(i);
      String itemXpath = (String) itemXpaths.get(Integer.parseInt(index));
      int identIndex = itemXpath.lastIndexOf("ident");
      String itemIdent =
        itemXpath.substring(identIndex + 7, itemXpath.length() - 2);
      list.add(itemIdent);
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String getCurrentTime()
  {
    LOG.debug("getCurrentTime()");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    Date now = new Date();

    return sdf.format(now);
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
    return null;
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
    super.saveDocument(request, document);
  }
}
