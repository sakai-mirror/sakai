
/*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/*
 * Created on Aug 6, 2003
 *
 */
package org.sakaiproject.tool.assessment.ui.bean.delivery;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.activation.FileDataSource;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.MediaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedSecuredIPAddress;
import org.sakaiproject.tool.assessment.ui.bean.util.Validator;

import org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.delivery.SubmitToGradingActionListener;
import org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener;
import java.io.FileInputStream;
import java.io.File;
import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;


/**
 *
 * @author casong To change the template for this generated type comment go to
 * $Id: DeliveryBean.java,v 1.44.2.7 2005/03/28 16:45:44 daisyf.stanford.edu Exp $
 *
 * Used to be org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm.java
 */
public class DeliveryBean
  implements Serializable
{
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
  private Date beginTime;
  private String endTime;
  private String currentTime;
  private String multipleAttempts;
  private String timeOutSubmission;
  private String submissionTicket;
  private String timeElapse;
  private String username;
  private int sectionIndex;
  private boolean previous;
  private String duration;
  private String url;
  private String confirmation;
  private String outcome;

  //Settings
  private String questionLayout;
  private String navigation;
  private String numbering;
  private String feedback;
  private String creatorName;

  private FeedbackComponent feedbackComponent;
  private String errorMessage;
  private SettingsDeliveryBean settings;
  private java.util.Date dueDate;
  private boolean statsAvailable;
  private boolean submitted;
  private boolean graded;
  private String graderComment;
  private String rawScore;
  private String grade;
  private java.util.Date submissionDate;
  private java.util.Date submissionTime;
  private String image;
  private boolean hasImage;
  private String instructorMessage;
  private String courseName;
  private String timeLimit;
  private int timeLimit_hour;
  private int timeLimit_minute;
  private ContentsDeliveryBean tableOfContents;
  private String previewMode;
  private String submissionId;
  private String submissionMessage;
  private String instructorName;
  private ContentsDeliveryBean pageContents;
  private int submissionsRemaining;

  private boolean forGrade;
  private String password;

  // For paging
  private int partIndex;
  private int questionIndex;
  private boolean next_page;
  private boolean reload = true;

  // daisy added these for SelectActionListener
  private boolean notTakeable = true;
  private boolean pastDue;
  private long subTime;
  private long raw;
  private String takenHours;
  private String takenMinutes;
  private AssessmentGradingData adata;
  private PublishedAssessmentFacade publishedAssessment;
  private java.util.Date feedbackDate;
  private String showScore;
  private boolean hasTimeLimit;

  // daisyf added for servlet Login.java, to support anonymous login with
  // publishedUrl
  private boolean anonymousLogin = false;
  private boolean accessViaUrl = false;
  private String contextPath;
 
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -1090852048737428722L;

  /**
   * Creates a new DeliveryBean object.
   */
  public DeliveryBean()
  {
  }

  /**
   *
   *
   * @return
   */
  public int getItemIndex()
  {
    return this.itemIndex;
  }

  /**
   *
   *
   * @param itemIndex
   */
  public void setItemIndex(int itemIndex)
  {
    this.itemIndex = itemIndex;
  }

  /**
   *
   *
   * @return
   */
  public int getSize()
  {
    return this.size;
  }

  /**
   *
   *
   * @param size
   */
  public void setSize(int size)
  {
    this.size = size;
  }

  /**
   *
   *
   * @return
   */
  public String getAssessmentId()
  {
    return assessmentId;
  }

  /**
   *
   *
   * @param assessmentId
   */
  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

  /**
   *
   *
   * @return
   */
  public ArrayList getMarkedForReview()
  {
    return markedForReview;
  }

  /**
   *
   *
   * @param markedForReview
   */
  public void setMarkedForReview(ArrayList markedForReview)
  {
    this.markedForReview = markedForReview;
  }

  /**
   *
   *
   * @return
   */
  public boolean getReviewMarked()
  {
    return this.reviewMarked;
  }

  /**
   *
   *
   * @param reviewMarked
   */
  public void setReviewMarked(boolean reviewMarked)
  {
    this.reviewMarked = reviewMarked;
  }

  /**
   *
   *
   * @return
   */
  public boolean getReviewAll()
  {
    return this.reviewAll;
  }

  /**
   *
   *
   * @param reviewAll
   */
  public void setReviewAll(boolean reviewAll)
  {
    this.reviewAll = reviewAll;
  }

  /**
   *
   *
   * @return
   */
  public String getAction()
  {
    return this.action;
  }

  /**
   *
   *
   * @param action
   */
  public void setAction(String action)
  {
    this.action = action;
  }

  /**
   *
   *
   * @return
   */
  public Date getBeginTime()
  {
    return beginTime;
  }

  /**
   *
   *
   * @param beginTime
   */
  public void setBeginTime(Date beginTime)
  {
    this.beginTime = beginTime;
  }

  /**
   *
   *
   * @return
   */
  public String getEndTime()
  {
    return endTime;
  }

  /**
   *
   *
   * @param endTime
   */
  public void setEndTime(String endTime)
  {
    this.endTime = endTime;
  }

  /**
   *
   *
   * @return
   */
  public String getCurrentTime()
  {
    return this.currentTime;
  }

  /**
   *
   *
   * @param currentTime
   */
  public void setCurrentTime(String currentTime)
  {
    this.currentTime = currentTime;
  }

  /**
   *
   *
   * @return
   */
  public String getMultipleAttempts()
  {
    return this.multipleAttempts;
  }

  /**
   *
   *
   * @param multipleAttempts
   */
  public void setMultipleAttempts(String multipleAttempts)
  {
    this.multipleAttempts = multipleAttempts;
  }

  /**
   *
   *
   * @return
   */
  public String getTimeOutSubmission()
  {
    return this.timeOutSubmission;
  }

  /**
   *
   *
   * @param timeOutSubmission
   */
  public void setTimeOutSubmission(String timeOutSubmission)
  {
    this.timeOutSubmission = timeOutSubmission;
  }

  /**
   *
   *
   * @return
   */
  public java.util.Date getSubmissionTime()
  {
    return submissionTime;
  }

  /**
   *
   *
   * @param submissionTime
   */
  public void setSubmissionTime(java.util.Date submissionTime)
  {
    this.submissionTime = submissionTime;
  }

  /**
   *
   *
   * @return
   */
  public String getTimeElapse()
  {
    return timeElapse;
  }

  /**
   *
   *
   * @param timeElapse
   */
  public void setTimeElapse(String timeElapse)
  {
    this.timeElapse = timeElapse;
  }

  /**
   *
   *
   * @return
   */
  public String getSubmissionTicket()
  {
    return submissionTicket;
  }

  /**
   *
   *
   * @param submissionTicket
   */
  public void setSubmissionTicket(String submissionTicket)
  {
    this.submissionTicket = submissionTicket;
  }

  /**
   *
   *
   * @return
   */
  public int getDisplayIndex()
  {
    return this.itemIndex + 1;
  }

  /**
   *
   *
   * @return
   */
  public String getUsername()
  {
    return username;
  }

  /**
   *
   *
   * @param username
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  /**
   *
   *
   * @return
   */
  public String getAssessmentTitle()
  {
    return assessmentTitle;
  }

  /**
   *
   *
   * @param assessmentTitle
   */
  public void setAssessmentTitle(String assessmentTitle)
  {
    this.assessmentTitle = assessmentTitle;
  }

  /**
   *
   *
   * @return
   */
  public ArrayList getBlankItems()
  {
    return this.blankItems;
  }

  /**
   *
   *
   * @param blankItems
   */
  public void setBlankItems(ArrayList blankItems)
  {
    this.blankItems = blankItems;
  }

  /**
   *
   *
   * @return
   */
  public boolean getReviewBlank()
  {
    return reviewBlank;
  }

  /**
   *
   *
   * @param reviewBlank
   */
  public void setReviewBlank(boolean reviewBlank)
  {
    this.reviewBlank = reviewBlank;
  }


  /**
   *
   *
   * @return
   */
  public ArrayList getMarkedForReviewIdents()
  {
    return markedForReviewIdents;
  }

  /**
   *
   *
   * @param markedForReviewIdents
   */
  public void setMarkedForReviewIdents(ArrayList markedForReviewIdents)
  {
    this.markedForReviewIdents = markedForReviewIdents;
  }

  /**
   *
   *
   * @return
   */
  public ArrayList getBlankItemIdents()
  {
    return blankItemIdents;
  }

  /**
   *
   *
   * @param blankItemIdents
   */
  public void setBlankItemIdents(ArrayList blankItemIdents)
  {
    this.blankItemIdents = blankItemIdents;
  }

  /**
   *
   *
   * @return
   */
  public int getSectionIndex()
  {
    return this.sectionIndex;
  }

  /**
   *
   *
   * @param sectionIndex
   */
  public void setSectionIndex(int sectionIndex)
  {
    this.sectionIndex = sectionIndex;
  }

  /**
   *
   *
   * @return
   */
  public boolean getPrevious()
  {
    return previous;
  }

  /**
   *
   *
   * @param previous
   */
  public void setPrevious(boolean previous)
  {
    this.previous = previous;
  }

  //Settings
  public String getQuestionLayout()
  {
    return questionLayout;
  }

  /**
   *
   *
   * @param questionLayout
   */
  public void setQuestionLayout(String questionLayout)
  {
    this.questionLayout = questionLayout;
  }

  /**
   *
   *
   * @return
   */
  public String getNavigation()
  {
    return navigation;
  }

  /**
   *
   *
   * @param navigation
   */
  public void setNavigation(String navigation)
  {
    this.navigation = navigation;
  }

  /**
   *
   *
   * @return
   */
  public String getNumbering()
  {
    return numbering;
  }

  /**
   *
   *
   * @param numbering
   */
  public void setNumbering(String numbering)
  {
    this.numbering = numbering;
  }

  /**
   *
   *
   * @return
   */
  public String getFeedback()
  {
    return feedback;
  }

  /**
   *
   *
   * @param feedback
   */
  public void setFeedback(String feedback)
  {
    this.feedback = feedback;
  }

  /**
   *
   *
   * @return
   */
  public FeedbackComponent getFeedbackComponent()
  {
    return feedbackComponent;
  }

  /**
   *
   *
   * @param feedbackComponent
   */
  public void setFeedbackComponent(FeedbackComponent feedbackComponent)
  {
    this.feedbackComponent = feedbackComponent;
  }

  /**
   * Types of feedback in FeedbackComponent:
   *
   * SHOW CORRECT SCORE
   * SHOW STUDENT SCORE
   * SHOW ITEM LEVEL
   * SHOW SECTION LEVEL
   * SHOW GRADER COMMENT
   * SHOW STATS
   * SHOW QUESTION
   * SHOW RESPONSE
   **/

  /**
   * @return
   */
  public SettingsDeliveryBean getSettings()
  {
    return settings;
  }

  /**
   * @param bean
   */
  public void setSettings(SettingsDeliveryBean settings)
  {
    this.settings = settings;
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
  public java.util.Date getDueDate()
  {
    return dueDate;
  }
  public void setDueDate(java.util.Date dueDate)
  {
    this.dueDate = dueDate;
  }
  public boolean isStatsAvailable() {
    return statsAvailable;
  }
  public void setStatsAvailable(boolean statsAvailable) {
    this.statsAvailable = statsAvailable;
  }
  public boolean isSubmitted() {
    return submitted;
  }
  public void setSubmitted(boolean submitted) {
    this.submitted = submitted;
  }
  public boolean isGraded() {
    return graded;
  }
  public void setGraded(boolean graded) {
    this.graded = graded;
  }

  public String getGraderComment() {
    if (graderComment == null)
      return "";
    return graderComment;
  }

  public void setGraderComment(String newComment)
  {
    graderComment = newComment;
  }

  public String getRawScore() {
    return rawScore;
  }
  public void setRawScore(String rawScore) {
    this.rawScore = rawScore;
  }
  public long getRaw() {
    return raw;
  }
  public void setRaw(long raw) {
    this.raw = raw;
  }
  public String getGrade() {
    return grade;
  }
  public void setGrade(String grade) {
    this.grade = grade;
  }
  public java.util.Date getSubmissionDate() {
    return submissionDate;
  }
  public void setSubmissionDate(java.util.Date submissionDate) {
    this.submissionDate = submissionDate;
  }
  public String getImage() {
    return image;
  }
  public void setImage(String image) {
    this.image = image;
  }
  public boolean isHasImage() {
    return hasImage;
  }
  public void setHasImage(boolean hasImage) {
    this.hasImage = hasImage;
  }
  public String getInstructorMessage() {
    return instructorMessage;
  }
  public void setInstructorMessage(String instructorMessage) {
    this.instructorMessage = instructorMessage;
  }
  public String getCourseName() {
    return courseName;
  }
  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }
  public String getTimeLimit() {
    return timeLimit;
  }
  public void setTimeLimit(String timeLimit) {
    this.timeLimit = timeLimit;
  }
  public int getTimeLimit_hour() {
    return timeLimit_hour;
  }
  public void setTimeLimit_hour(int timeLimit_hour) {
    this.timeLimit_hour = timeLimit_hour;
  }
  public int getTimeLimit_minute() {
    return timeLimit_minute;
  }
  public void setTimeLimit_minute(int timeLimit_minute) {
    this.timeLimit_minute = timeLimit_minute;
  }

  /**
   * Bean with table of contents information and
   * a list of all the sections in the assessment
   * which in  turn has a list of all the item contents.
   * @return table of contents
   */
  public ContentsDeliveryBean getTableOfContents() {
    return tableOfContents;
  }

  /**
   * Bean with table of contents information and
   * a list of all the sections in the assessment
   * which in  turn has a list of all the item contents.
   * @param tableOfContents table of contents
   */
  public void setTableOfContents(ContentsDeliveryBean tableOfContents) {
    this.tableOfContents = tableOfContents;
  }
  /**
   * Bean with a list of all the sections in the current page
   * which in turn has a list of all the item contents for the page.
   *
   * This is like the table of contents, but the selections are restricted to
   * that on one page.
   *
   *  Since these properties are on a page delivery basis--if:
   *  1. there is only one item per page the list of items will
   *  contain one item and the list of parts will return one part, or if--
   *
   *  2. there is one part per page the list of items will be that
   *  for that part only and there will only be one part, however--
   *
   *  3. if it is all parts and items on a single page there
   *  will be a list of all parts and items.
   *
   * @return ContentsDeliveryBean
   */
  public ContentsDeliveryBean getPageContents() {
    return pageContents;
  }

  /**
   * Bean with a list of all the sections in the current page
   * which in turn has a list of all the item contents for the page.
   *
   *  Since these properties are on a page delivery basis--if:
   *  1. there is only one item per page the list of items will
   *  contain one item and the list of parts will return one part, or if--
   *
   *  2. there is one part per page the list of items will be that
   *  for that part only and there will only be one part, however--
   *
   *  3. if it is all parts and items on a single page there
   *  will be a list of all parts and items.
   *
   * @param pageContents ContentsDeliveryBean
   */
  public void setPageContents(ContentsDeliveryBean pageContents) {
    this.pageContents = pageContents;
  }


  /**
   * track whether delivery is "live" with update of database allowed and
   * whether the Ui components are disabled.
   * @return true if preview only
   */
  public String getPreviewMode() {
    return Validator.check(previewMode, "false");
  }

  /**
   * track whether delivery is "live" with update of database allowed and
   * whether the UI components are disabled.
   * @param previewMode true if preview only
   */
  public void setPreviewMode(boolean previewMode) {
    this.previewMode = new Boolean(previewMode).toString();
  }
  public String getSubmissionId() {
    return submissionId;
  }
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }
  public String getSubmissionMessage() {
    return submissionMessage;
  }
  public void setSubmissionMessage(String submissionMessage) {
    this.submissionMessage = submissionMessage;
  }
  public int getSubmissionsRemaining() {
    return submissionsRemaining;
  }
  public void setSubmissionsRemaining(int submissionsRemaining) {
    this.submissionsRemaining = submissionsRemaining;
  }
  public String getInstructorName() {
    return instructorName;
  }
  public void setInstructorName(String instructorName) {
    this.instructorName = instructorName;
  }
  public boolean getForGrade() {
    return forGrade;
  }
  public void setForGrade(boolean newfor) {
    forGrade = newfor;
  }

  public String submitForGrade()
  {
    forGrade = true;
    SubmitToGradingActionListener listener =
      new SubmitToGradingActionListener();
    listener.processAction(null);

    SelectActionListener l2 = new SelectActionListener();
    l2.processAction(null);
    reload = true;

    return "submitAssessment";
  }

  public String saveAndExit()
  {

      FacesContext context = FacesContext.getCurrentInstance();
      System.out.println("***DeliverBean.saveAndEXit face context ="+context);

    // If this was automatically triggered by running out of time,
    // check for autosubmit, and do it if so.
    if (timeOutSubmission != null && timeOutSubmission.equals("true"))
    {
      if (getSettings().getAutoSubmit())
      {
        submitForGrade();
        return "timeout";
      }
    }

    forGrade = false;
    SubmitToGradingActionListener listener =
      new SubmitToGradingActionListener();
    listener.processAction(null);

    SelectActionListener l2 = new SelectActionListener();
    l2.processAction(null);
    reload = true;

    if (timeOutSubmission != null && timeOutSubmission.equals("true"))
    {
      return "timeout";
    }

    if (getAccessViaUrl()){ // if this is access via url, display quit message
      System.out.println("**anonymous login, go to quit");
      return "anonymousQuit";
    }
    else{
      System.out.println("**NOT anonymous login, go to select");
      return "select";
    }
  }

  public String next_page()
  {
    if (getSettings().isFormatByPart())
      partIndex++;
    if (getSettings().isFormatByQuestion())
      questionIndex++;

    forGrade = false;
    SubmitToGradingActionListener listener =
      new SubmitToGradingActionListener();
    listener.processAction(null);

    DeliveryActionListener l2 = new DeliveryActionListener();
    l2.processAction(null);

    reload = false;
    return "takeAssessment";
  }

  public String previous()
  {
    if (getSettings().isFormatByPart())
      partIndex--;
    if (getSettings().isFormatByQuestion())
      questionIndex--;

    forGrade = false;
    SubmitToGradingActionListener listener =
      new SubmitToGradingActionListener();
    listener.processAction(null);

    DeliveryActionListener l2 = new DeliveryActionListener();
    l2.processAction(null);

    reload = false;
    return "takeAssessment";
  }

  // this is the PublishedAccessControl.finalPageUrl
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url= url;
  }

  public String getConfirmation() {
    return confirmation;
  }
  public void setConfirmation(String confirmation) {
    this.confirmation= confirmation;
  }

  /**
   * if required, assessment password
   * @return password
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * if required, assessment password
   * @param string assessment password
   */
  public void setPassword(String string)
  {
    password = string;
  }

  public String validatePassword() {
    if (password == null || username == null)
      return "accessError";
    if (password.equals(getSettings().getPassword()) &&
        username.equals(getSettings().getUsername()))
    {
      if (getNavigation().equals
        (AssessmentAccessControl.RANDOM_ACCESS.toString()))
        return "tableOfContents";
      else
        return "takeAssessment";
    }
    else
      return "accessError";
  }

  public String validateIP() {
    String thisIp = ((javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr();
    Iterator addresses = getSettings().getIpAddresses().iterator();
    while (addresses.hasNext())
    {
      String next = ((PublishedSecuredIPAddress) addresses.next()).
        getIpAddress();
      if (next != null && next.indexOf("*") > -1)
        next = next.substring(0, next.indexOf("*"));
      if (next == null || next.trim().equals("") ||
        thisIp.trim().startsWith(next.trim()))
      {
        if (getNavigation().equals
          (AssessmentAccessControl.RANDOM_ACCESS.toString()))
          return "tableOfContents";
        else
          return "takeAssessment";
      }
    }
    return "accessError";
  }

  public String validate() {
   try {
    String results = "";
    if (!getSettings().getUsername().equals(""))
      results = validatePassword();
    if (!results.equals("accessError") &&
      getSettings().getIpAddresses() != null &&
      !getSettings().getIpAddresses().isEmpty())
      results = validateIP();
    if (results.equals(""))
    {
      if (getNavigation().equals
        (AssessmentAccessControl.RANDOM_ACCESS.toString()))
        return "tableOfContents";
      else
        return "takeAssessment";
    }
    return results;
   } catch (Exception e) {
     e.printStackTrace();
     return "accessError";
   }
  }

  // Skipped paging methods
  public int getPartIndex()
  {
    return partIndex;
  }

  public void setPartIndex(int newindex)
  {
    partIndex = newindex;
  }

  public int getQuestionIndex()
  {
    return questionIndex;
  }

  public void setQuestionIndex(int newindex)
  {
    questionIndex = newindex;
  }

  public boolean getContinue()
  {
    return next_page;
  }

  public void setContinue(boolean docontinue)
  {
    next_page = docontinue;
  }

  public boolean getReload()
  {
    return reload;
  }

  public void setReload(boolean doreload)
  {
    reload = doreload;
  }

  // Store for paging
  public AssessmentGradingData getAssessmentGrading()
  {
    return adata;
  }

  public void setAssessmentGrading(AssessmentGradingData newdata)
  {
    adata = newdata;
  }


  private byte[] getMediaStream(String mediaLocation){
    File media=null;
    FileInputStream mediaStream=null;
    BufferedInputStream buf_inputStream=null;
    byte[] mediaByte = new byte[0];
    try {
      media = new File(mediaLocation);
      mediaStream = new FileInputStream(media);
      int i = 0;
      int size = 0;
      buf_inputStream = new BufferedInputStream(mediaStream);
      if(buf_inputStream != null)
      {
        while((i = buf_inputStream.read()) != -1)
          size++;
      }
      mediaByte = new byte[size];
      mediaStream.read(mediaByte);
   }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
    finally{
      try {
        mediaStream.close();
        buf_inputStream.close();
      }
      catch (IOException ex1) {
      }
    }
    return mediaByte;
  }

  /**
   * This method is used by jsf/delivery/deliveryFileUpload.jsp
   *   <corejsf:upload
   *     target="/jsf/upload_tmp/assessment#{delivery.assessmentId}/
   *             question#{question.itemData.itemId}/admin"
   *     valueChangeListener="#{delivery.addMediaToItemGrading}" />
   */
  public String addMediaToItemGrading(javax.faces.event.ValueChangeEvent e)
  {
    // 1. format of the media location is: assessmentXXX/questionXXX/agentId/myfile
    String mediaLocation = (String) e.getNewValue();
    //System.out.println("***1. addMediaToItemGrading, new value ="+mediaLocation);
    File media = new File(mediaLocation);
    byte[] mediaByte = getMediaStream(mediaLocation);

    // 2. get the questionId (which is the PublishedItemData.itemId)
    int assessmentIndex = mediaLocation.lastIndexOf("assessment");
    int questionIndex = mediaLocation.lastIndexOf("question");
    int agentIndex = mediaLocation.indexOf("/", questionIndex+8);
    String pubAssessmentId = mediaLocation.substring(assessmentIndex+10,questionIndex-1);
    String questionId = mediaLocation.substring(questionIndex+8,agentIndex);
    //System.out.println("***2a. addMediaToItemGrading, questionId ="+questionId);
    //System.out.println("***2b. addMediaToItemGrading, assessmentId ="+assessmentId);

    // 3. prepare itemGradingData
    PublishedAssessmentService publishedService = new PublishedAssessmentService();
    PublishedItemData item = publishedService.loadPublishedItem(questionId);
    //System.out.println("***3. addMediaToItemGrading, item ="+item);
    //System.out.println("***4. addMediaToItemGrading, itemTextArray ="+item.getItemTextArray());
    //System.out.println("***5. addMediaToItemGrading, itemText(0) ="+item.getItemTextArray().get(0));
    // there is only one text in audio question
    PublishedItemText itemText = (PublishedItemText) item.getItemTextArraySorted().get(0);
    ItemGradingData itemGradingData = new ItemGradingData();
    itemGradingData.setAssessmentGrading(adata);
    itemGradingData.setPublishedItem(item);
    itemGradingData.setPublishedItemText(itemText);

    //System.out.println("***6. addMediaToItemGrading, adata before="+this.adata);

    // 5. attach itemGradingData to assessmentGrading
    if (this.adata == null)
    {
      adata = new AssessmentGradingData();
      adata.setAgentId(AgentFacade.getAgentString());
      adata.setPublishedAssessment(publishedService.getPublishedAssessment(assessmentId).getData());
    }
    // store AssessmentGradingData - I don't really understand this method
    // but the stmt at ** failed if assessmentGrdaingData does not exist yet
    adata.setForGrade(new Boolean(getForGrade()));

    Set itemDataSet = adata.getItemGradingSet();
    //System.out.println("***8a. addMediaToItemGrading, itemDataSet="+itemDataSet);
    if (itemDataSet == null)
      itemDataSet = new HashSet();
    else
      itemDataSet.add(itemGradingData);
    //System.out.println("***8b. addMediaToItemGrading, adata="+adata);
    adata.setItemGradingSet(itemDataSet);
    //System.out.println("***8c. addMediaToItemGrading, getForGrade()="+getForGrade());
    // **
    adata.setForGrade(new Boolean(getForGrade()));

    //System.out.println("***9. addMediaToItemGrading, adata="+adata);
    if (adata.getItemGradingSet()==null)
        adata.setItemGradingSet(new HashSet());
    adata.getItemGradingSet().add(itemGradingData);
    // 6. save AssessmentGardingData with ItemGardingData
    GradingService gradingService = new GradingService();
    gradingService.storeGrades(adata);
    //System.out.println("***10. addMediaToItemGrading, saved="+adata);

    // 7. create a media record and itemGradingRecord
    FileDataSource source = new FileDataSource(media);
    String mimeType = source.getContentType();
    boolean SAVETODB = true;
    MediaData mediaData=null;
    if (SAVETODB){ // put the byte[] in
      mediaData = new MediaData(itemGradingData, mediaByte,
                                new Long(mediaByte.length + ""),
                                mimeType, "description", null,
                                media.getName(), false, false, new Integer(1),
                                AgentFacade.getAgentString(), new Date(),
                                AgentFacade.getAgentString(), new Date());
    }
    else{ // put the location in
      mediaData = new MediaData(itemGradingData, null,
                                new Long(mediaByte.length + ""),
                                mimeType, "description", mediaLocation,
                                media.getName(), false, false, new Integer(1),
                                AgentFacade.getAgentString(), new Date(),
                                AgentFacade.getAgentString(), new Date());

    }
    //System.out.println("***11. addMediaToItemGrading, adata="+adata);

    Long mediaId = gradingService.saveMedia(mediaData);
    //System.out.println("mediaId"+mediaId);
    //System.out.println("***12. addMediaToItemGrading, adata="+adata);

/**
    forGrade = false;
    SubmitToGradingActionListener listener =
      new SubmitToGradingActionListener();
    listener.processAction(null);
*/
    DeliveryActionListener l2 = new DeliveryActionListener();
    l2.processAction(null);
    reload = true;
    return "takeAssessment";

  }

  public boolean getNotTakeable() {
    return notTakeable;
  }
  public void setNotTakeable(boolean notTakeable) {
    this.notTakeable = notTakeable;
  }

  public boolean getPastDue() {
    return pastDue;
  }
  public void setPastDue(boolean pastDue) {
    this.pastDue = pastDue;
  }

  public long getSubTime() {
    return subTime;
  }

  public void setSubTime(long newSubTime)
  {
    subTime = newSubTime;
  }

  public String getSubmissionHours() {
    return takenHours;
  }

  public void setSubmissionHours(String newHours)
  {
    takenHours = newHours;
  }

  public String getSubmissionMinutes() {
    return takenMinutes;
  }

  public void setSubmissionMinutes(String newMinutes)
  {
    takenMinutes = newMinutes;
  }

  public PublishedAssessmentFacade getPublishedAssessment() {
    return publishedAssessment;
  }

  public void setPublishedAssessment(PublishedAssessmentFacade publishedAssessment)
  {
    this.publishedAssessment = publishedAssessment;
  }

  public java.util.Date getFeedbackDate() {
    return feedbackDate;
  }
  public void setFeedbackDate(java.util.Date feedbackDate) {
    this.feedbackDate = feedbackDate;
  }

  public String getShowScore()
  {
    return showScore;
  }

  public void setShowScore(String showScore)
  {
    this.showScore = showScore;
  }

  public boolean getHasTimeLimit() {
    return hasTimeLimit;
  }
  public void setHasTimeLimit(boolean hasTimeLimit) {
    this.hasTimeLimit = hasTimeLimit;
  }

  public String getOutcome()
  {
    return outcome;
  }

  public void setOutcome(String outcome)
  {
    this.outcome = outcome;
  }

  public String doit(){
    return outcome;
  }

  public boolean getAnonymousLogin() {
    return anonymousLogin;
  }
  public void setAnonymousLogin(boolean anonymousLogin) {
    this.anonymousLogin = anonymousLogin;
  }

  public boolean getAccessViaUrl() {
    return accessViaUrl;
  }
  public void setAccessViaUrl(boolean accessViaUrl) {
    this.accessViaUrl = accessViaUrl;
  }

  public String getContextPath() {
    return contextPath;
  }
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

}
