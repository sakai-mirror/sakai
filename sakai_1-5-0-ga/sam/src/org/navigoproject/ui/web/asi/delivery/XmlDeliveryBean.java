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
 * Created on Aug 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import org.apache.xerces.dom.TextImpl;
import org.navigoproject.CompositeObjectUtil;
import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Iso8601DateFormat;
import org.navigoproject.business.entity.XmlStringBuffer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class XmlDeliveryBean
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlDeliveryBean.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    return CompositeObjectUtil.printToString(this);
  }

  /**
   * Creates a new XmlDeliveryBean object.
   */
  public XmlDeliveryBean()
  {
  }
  
  /**
   * 
   */
  public Document getQuesttestInterop()
  {
    return this.questtestInterop;
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTaken DOCUMENTATION PENDING
   */
  public void setQuesttestInterop(Document questtestinterop)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setAssessmentTaken(Document " + questtestinterop);
    }

    XmlDeliveryService xmlDeliveryService = new XmlDeliveryService();
    DeliveryBeanUtil util = new DeliveryBeanUtil(questtestinterop);
    
    this.questionLayout = util.getFieldentry("QUESTION_LAYOUT");
    this.navigation = util.getFieldentry("NAVIGATION");
    this.numbering = util.getFieldentry("QUESTION_NUMBERING");
    this.feedback = util.getFieldentry("FEEDBACK_DELIVERY");
    if("DATED".equals(feedback))
    {
      this.feedbackDate = util.getFieldentry("FEEDBACK_DELIVERY_DATE");
    }
    this.feedbackComponent = util.getFeedbackComponent();
    ItemSectionPathExtractor ispe = new ItemSectionPathExtractor(numbering);
    
    this.questtestInterop = questtestinterop;
    this.assessmentId = util.getAssessmentId(questtestInterop);
    this.assessmentTitle =
      util.getAssessmentTitle(questtestInterop);
    this.items = ispe.extractDocumentItemXpath(questtestInterop);
    this.sections = ispe.extractDocumentSectionXpath(questtestInterop);
    if(items != null){
      this.size = this.items.size();
    }
    this.markedForReview = new ArrayList();
    this.blankItems = new ArrayList();
    this.reviewMarked = false;
    this.reviewAll = false;
    
    if (util.getFieldentry("LATE_HANDLING").equals("True"))
    {
      this.lateHandling = true;
    }
    if (util.getFieldentry("CONSIDER_END_DATE").equals("True"))
    {
      this.setDueDate(util.getFieldentry("END_DATE"));
    }
    
    if(util.getFieldentry("CONSIDER_DURATION").equals("True"))
    {
      this.setDuration();
    }
    this.creatorName = util.getFieldentry("CREATOR");
    if(size > 0)
    {
      this.markedMap = new ReviewBitMap(size);
      this.blankMap = new ReviewBitMap(size);
      this.blankMap.setAll();
    }
  }

  public Document getQtiResultReport()
  {
    return this.qtiResultReport;
  }
  /**
   * @param document
   */
  public void setQtiResultReport(Document qtiResultReport)
  {
    this.qtiResultReport = qtiResultReport; 
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getMarkedForReview()
  {
    LOG.debug("getMarkedForReview()");

    if(this.markedMap == null)
    {
      return null;
    }

    return this.markedMap.getReviewList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getItems()
  {
    LOG.debug("getItems()");

    return this.items;
  }
  
  public ArrayList getSections()
  {
    LOG.debug("getSections()");
    return this.sections;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getItemIndex()
  {
    LOG.debug("getItemIndex()");

    return this.itemIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemIndex DOCUMENTATION PENDING
   */
  public void setItemIndex(int itemIndex)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setItemIndex(int " + itemIndex + ")");
    }

    this.itemIndex = itemIndex;
  }
  
  public int getSectionIndex()
  {
    return this.sectionIndex;
  }
  
  public void setSectionIndex(int sectionIndex)
  {
    this.sectionIndex = sectionIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getSize()
  {
    LOG.debug("getSize()");

    return this.size;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentId()
  {
    LOG.debug("getAssessmentId");

    return assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReviewMarked()
  {
    LOG.debug("getReviewMarked()");

    return this.reviewMarked;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewMarked DOCUMENTATION PENDING
   */
  public void setReviewMarked(boolean reviewMarked)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setReviewMarked(boolean " + reviewMarked + ")");
    }

    this.reviewMarked = reviewMarked;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReviewAll()
  {
    LOG.debug("getReviewAll()");

    return this.reviewAll;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewAll DOCUMENTATION PENDING
   */
  public void setReviewAll(boolean reviewAll)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setReviewAll(boolean " + reviewAll + ")");
    }

    this.reviewAll = reviewAll;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isReviewBlank()
  {
    LOG.debug("isReviewBlank");

    return this.reviewBlank;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewBlank DOCUMENTATION PENDING
   */
  public void setReviewBlank(boolean reviewBlank)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setReviewBlank(boolean " + reviewBlank + ")");
    }

    this.reviewBlank = reviewBlank;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAction()
  {
    LOG.debug("getAction()");

    return this.action;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param action DOCUMENTATION PENDING
   */
  public void setAction(String action)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setAction(String " + action + ")");
    }

    this.action = action;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getBeginTime()
  {
    LOG.debug("getBeginTime");

    return beginTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param beginTime DOCUMENTATION PENDING
   */
  public void setBeginTime(String beginTime)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setBeginTime(String " + beginTime + ")");
    }

    this.beginTime = beginTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEndTime()
  {
    LOG.debug("getEndTime()");

    return endTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param endTime DOCUMENTATION PENDING
   */
  public void setEndTime(String endTime)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setEndTime(String " + endTime + ")");
    }

    this.endTime = endTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getCurrentTime()
  {
    return this.currentTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param currentTime DOCUMENTATION PENDING
   */
  public void setCurrentTime(String currentTime)
  {
    this.currentTime = currentTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMultipleAttemps()
  {
    return this.multipleAttemps;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param multipleAttemps DOCUMENTATION PENDING
   */
  public void setMultipleAttemps(String multipleAttemps)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setMultipleAttemps(String " + multipleAttemps + ")");
    }

    this.multipleAttemps = multipleAttemps;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTimeOutSubmission()
  {
    return this.timeOutSubmission;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param timeOutSubmission DOCUMENTATION PENDING
   */
  public void setTimeOutSubmission(String timeOutSubmission)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setTimeOutSubmission(String " + timeOutSubmission + ")");
    }

    this.timeOutSubmission = timeOutSubmission;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionTime()
  {
    return submissionTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param submissionTime DOCUMENTATION PENDING
   */
  public void setSubmissionTime(String submissionTime)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setSubmissionTime(String " + submissionTime + ")");
    }

    this.submissionTime = submissionTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTimeElapse()
  {
    return timeElapse;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param timeElapse DOCUMENTATION PENDING
   */
  public void setTimeElapse(String timeElapse)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setTimeElaps(String " + timeElapse + ")");
    }

    this.timeElapse = timeElapse;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionTicket()
  {
    return submissionTicket;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param submissionTicket DOCUMENTATION PENDING
   */
  public void setSubmissionTicket(String submissionTicket)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setEndTime(String " + endTime + ")");
    }

    this.submissionTicket = submissionTicket;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getDisplayIndex()
  {
    return this.itemIndex + 1;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void release()
  {
    //    this.assessment = null;
    this.markedForReview = null;
    this.items = null;
    this.itemIndex = -1;
    this.reviewMarked = false;
    this.reviewAll = false;
    this.size = 0;
    this.action = null;
    this.endTime = null;
    this.multipleAttemps = null;
    this.timeOutSubmission = null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param username DOCUMENTATION PENDING
   */
  public void setUsername(String username)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setUsername(String " + username + ")");
    }

    this.username = username;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Element getAssessment()
  {
    LOG.debug("getAssessment()");
    Element assessment = null;
    try
    {
//      XmlStringBuffer xsb = new XmlStringBuffer(assessmentTaken);
//      Iterator assessments =
//        xsb.selectNodes(XmlDeliveryService.ASSESSMENT_PATH).iterator();
      XmlStringBuffer xsb = new XmlStringBuffer(questtestInterop);
      Iterator assessments =
        xsb.selectNodes(XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT).iterator();      
      if(assessments.hasNext())
      {
        assessment = (Element) assessments.next();
      }
    }
    catch(Exception ex)
    {
      LOG.debug(ex.getMessage(), ex);
    }

    //    return (Element) assessment.clone();
    return (Element) assessment.cloneNode(true);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentTitle()
  {
    return assessmentTitle;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTitle DOCUMENTATION PENDING
   */
  public void setAssessmentTitle(String assessmentTitle)
  {
    this.assessmentTitle = assessmentTitle;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getBlankItems()
  {
    if(this.blankMap == null)
    {
      return null;
    }

    return this.blankMap.getReviewList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param blankItems DOCUMENTATION PENDING
   */

  //  public void setBlankItems(ArrayList blankItems)
  //  {
  //    this.blankItems = blankItems;
  //  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Node getSectionPM()
  {
    return this.sectionPM;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionPM DOCUMENTATION PENDING
   */
  public void setSectionPM(Node sectionPM)
  {
    this.sectionPM = sectionPM;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ReviewBitMap getBlankMap()
  {
    return blankMap;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param blankMap DOCUMENTATION PENDING
   */
  public void setBlankMap(ReviewBitMap blankMap)
  {
    this.blankMap = blankMap;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ReviewBitMap getMarkedMap()
  {
    return markedMap;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param markedMap DOCUMENTATION PENDING
   */
  public void setMarkedMap(ReviewBitMap markedMap)
  {
    this.markedMap = markedMap;
  }
  

//  public void setLateSubmission(int lateSubmission)
//  {
//    this.lateSubmission = lateSubmission;
//  }
//  
//  public int getLateSubmission()
//  {
//    return this.lateSubmission;
//  }
  
//  public Element getResultResport()
//  {
//    LOG.debug("getAssessment()");
//    Element assessment = null;
//    try
//    {
//      XmlStringBuffer xsb = new XmlStringBuffer(assessmentTaken);
//      Iterator resultReports =
//        xsb.selectNodes(XmlDeliveryService.ASSESSMENT_PATH).iterator();
//      if(resultReports.hasNext())
//      {
//        assessment = (Element) resultReports.next();
//      }
//    }
//    catch(Exception ex)
//    {
//      LOG.debug(ex.getMessage(), ex);
//    }
//
//    //    return (Element) assessment.clone();
//    return (Element) assessment.cloneNode(true);
//  }


  //Settings
  public String getQuestionLayout(){
    return questionLayout;
  }
  
  public void setQuestionLayout(String questionLayout)
  {
    this.questionLayout = questionLayout;
  }
  
  public String getNavigation(){
    return navigation;
  }
  
  public String getNumbering(){
    return numbering;
  }
  
  public String getFeedback(){
    return feedback;
  }
  
  public void setFeedback(String feedback){
    this.feedback = feedback;
  }
  
  public String getFeedbackDate(){
    return feedbackDate;
  }
  
  public FeedbackComponent getFeedbackComponent()
  {
    return feedbackComponent;
  }
  
  
  public Date getDueDate(){
    return dueDate;
  }
  
  private void setDueDate(String dueDateString)
  {
    Iso8601DateFormat dateFormat = new Iso8601DateFormat();
    if(dueDateString != null && !dueDateString.trim().equals(""))
    {
      this.dueDate = dateFormat.parse(dueDateString.trim()).getTime();
    } 
  }
  
  public boolean isLateHandling(){
    return lateHandling;  
  }
  
  public void setLateHandling(boolean isLateHandling)
  {
    this.lateHandling = isLateHandling;
  }
  
  public String getDuration()
  {
    return duration; 
  }
  
  private void setDuration()
  {
    NodeList durationNodes =
      this.getAssessment().getElementsByTagName(
        QTIConstantStrings.DURATION);
    if(durationNodes.getLength() > 0)
    {
      TextImpl cdi = (TextImpl) durationNodes.item(0).getFirstChild();
      if(cdi != null)
      {
        duration = cdi.getNodeValue();
      }
    }
  }

  //Don't think we need this anymore, add qtiquestInterop and qtiResultReport instead.
//  private Document assessmentTaken; 
  private Document questtestInterop;
  private Document qtiResultReport;
  
  private String assessmentId;
  private String assessmentTitle;
  private ArrayList markedForReview;
  private ArrayList blankItems;
  private ArrayList items;
  private ArrayList sections;
  private boolean reviewMarked;
  private boolean reviewAll;
  private boolean reviewBlank;
  private int itemIndex;
  private int sectionIndex;
  private int size;
  private String action;
  private String beginTime;
  private String endTime;
  private String currentTime;
  private String multipleAttemps;
  private String timeOutSubmission;
  private String submissionTime;
  private String submissionTicket;
  private String timeElapse;
  private String username;
  private Node sectionPM;
  private ReviewBitMap blankMap;
  private ReviewBitMap markedMap;
//  private int lateSubmission;

  //Settings
  private String questionLayout;
  private String navigation;
  private String numbering;
  private String feedback;
  private String feedbackDate;
  private String creatorName;
  
  /**
   * FEEDBACK_SHOW_CORRECT_SCORE
     FEEDBACK_SHOW_STUDENT_SCORE
     FEEDBACK_SHOW_ITEM_LEVEL
     FEEDBACK_SHOW_SECTION_LEVEL
     FEEDBACK_SHOW_GRADER_COMMENT
     FEEDBACK_SHOW_STATS
     FEEDBACK_SHOW_QUESTION
     FEEDBACK_SHOW_RESPONSE
   **/
  private FeedbackComponent feedbackComponent;
  
  private Date dueDate;
  private boolean lateHandling;
  private String duration;
  
  private QtiSettingsDeliveryBean qtiSettingBean;
  
  /**
   * @return
   */
  public QtiSettingsDeliveryBean getQtiSettingBean()
  {
    return qtiSettingBean;
  }

  /**
   * @param bean
   */
  public void setQtiSettingBean(QtiSettingsDeliveryBean bean)
  {
    qtiSettingBean = bean;
  }

  /**
   * @param publishedAssessmentId
   */
  public void setAssessmentId(String publishedAssessmentId)
  {
    this.assessmentId = publishedAssessmentId;
  }

  /**
   * @param date
   */
  public void setDueDate(Date date)
  {
    dueDate = date;
  }

  /**
   * @param string
   */
  public void setFeedbackDate(String string)
  {
    feedbackDate = string;
  }

  /**
   * @return
   */
  public String getCreatorName()
  {
    return creatorName;
  }

}
