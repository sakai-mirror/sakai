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

import org.navigoproject.CompositeObjectUtil;
import org.navigoproject.data.QtiSettingsBean;

import java.util.ArrayList;

import org.apache.struts.action.ActionForm;

import org.w3c.dom.Node;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class XmlDeliveryForm
  extends ActionForm
{
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
   * Creates a new XmlDeliveryForm object.
   */
  public XmlDeliveryForm()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getItemIndex()
  {
    return this.itemIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemIndex DOCUMENTATION PENDING
   */
  public void setItemIndex(int itemIndex)
  {
    this.itemIndex = itemIndex;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getSize()
  {
    return this.size;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param size DOCUMENTATION PENDING
   */
  public void setSize(int size)
  {
    this.size = size;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentId()
  {
    return assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   */
  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getMarkedForReview()
  {
    return markedForReview;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param markedForReview DOCUMENTATION PENDING
   */
  public void setMarkedForReview(ArrayList markedForReview)
  {
    this.markedForReview = markedForReview;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReviewMarked()
  {
    return this.reviewMarked;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewMarked DOCUMENTATION PENDING
   */
  public void setReviewMarked(boolean reviewMarked)
  {
    this.reviewMarked = reviewMarked;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReviewAll()
  {
    return this.reviewAll;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewAll DOCUMENTATION PENDING
   */
  public void setReviewAll(boolean reviewAll)
  {
    this.reviewAll = reviewAll;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAction()
  {
    return this.action;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param action DOCUMENTATION PENDING
   */
  public void setAction(String action)
  {
    this.action = action;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getBeginTime()
  {
    return beginTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param beginTime DOCUMENTATION PENDING
   */
  public void setBeginTime(String beginTime)
  {
    this.beginTime = beginTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEndTime()
  {
    return endTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param endTime DOCUMENTATION PENDING
   */
  public void setEndTime(String endTime)
  {
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
    this.username = username;
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
    return this.blankItems;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param blankItems DOCUMENTATION PENDING
   */
  public void setBlankItems(ArrayList blankItems)
  {
    this.blankItems = blankItems;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReviewBlank()
  {
    return reviewBlank;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param reviewBlank DOCUMENTATION PENDING
   */
  public void setReviewBlank(boolean reviewBlank)
  {
    this.reviewBlank = reviewBlank;
  }

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
  
  public ArrayList getMarkedForReviewIdents()
  {
    return markedForReviewIdents;
  }
  
  public void setMarkedForReviewIdents(ArrayList markedForReviewIdents){
    this.markedForReviewIdents = markedForReviewIdents;
  }
  
  public ArrayList getBlankItemIdents()
  {
    return blankItemIdents;
  }
  
  public void setBlankItemIdents(ArrayList blankItemIdents){
    this.blankItemIdents = blankItemIdents;
  }
  
  public int getSectionIndex()
  {
    return this.sectionIndex;
  }
  
  public void setSectionIndex(int sectionIndex)
  {
    this.sectionIndex = sectionIndex;
  }
  
  public boolean getPrevious()
  {
    return previous;
  }
  
  public void setPrevious(boolean previous)
  {
    this.previous = previous;
  }
  
  //Settings
  public String getQuestionLayout(){
    return questionLayout;
  }
  
  public void setQuestionLayout(String questionLayout){
    this.questionLayout = questionLayout;
  }
  
  public String getNavigation(){
    return navigation;
  }
  
  public void setNavigation(String navigation){
    this.navigation = navigation;
  }
  
  public String getNumbering(){
    return numbering;
  }
  
  public void setNumbering(String numbering){
    this.numbering = numbering;
  }
  
  public String getFeedback(){
    return feedback;
  }
  
  public void setFeedback(String feedback){
    this.feedback = feedback;
  }
  
  public FeedbackComponent getFeedbackComponent()
  {
    return feedbackComponent;
  }
  
  public void setFeedbackComponent(FeedbackComponent feedbackComponent)
  {
    this.feedbackComponent = feedbackComponent;
  }

  private String assessmentId;
  private String assessmentTitle;
  private ArrayList markedForReview;
  private ArrayList blankItems;
  private ArrayList markedForReviewIdents;
  private ArrayList blankItemIdents;
  private boolean reviewMarked;
  private boolean reviewAll;
  private boolean reviewBlank;
  private int itemIndex;
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
  private int sectionIndex;
  private boolean previous; 
  private String duration;
  
  //Settings
  private String questionLayout;
  private String navigation;
  private String numbering;
  private String feedback;
  private String creatorName;
  private String autoSave;
  
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
  
  private String errorMessage;
  
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
  public void setQtiSettingBean(QtiSettingsDeliveryBean qtiSettingBean)
  {
    this.qtiSettingBean = qtiSettingBean;
  }

  /**
   * @return
   */
  public String getErrorMessage()
  {
    return errorMessage;
  }

  /**
   * @param string
   */
  public void setErrorMessage(String string)
  {
    errorMessage = string;
  }

  /**
   * @return
   */
  public String getDuration()
  {
    return duration;
  }

  /**
   * @param string
   */
  public void setDuration(String string)
  {
    duration = string;
  }

  /**
   * @return
   */
  public String getCreatorName()
  {
    return creatorName;
  }

  /**
   * @param string
   */
  public void setCreatorName(String string)
  {
    creatorName = string;
  }
  /**
   * @return
   */
  public String getAutoSave()
  {
    return autoSave;
  }

  /**
   * @param string
   */
  public void setAutoSave(String string)
  {
    autoSave = string;
  }


}
