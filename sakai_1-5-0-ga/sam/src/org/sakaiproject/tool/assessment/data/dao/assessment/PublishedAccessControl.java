package org.sakaiproject.tool.assessment.data.dao.assessment;

import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentTemplateIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import java.util.Date;

/**
 * This keeps track of the submission scheme, and the number allowed.
 *
 * @author Rachel Gollub
 */
public class PublishedAccessControl
    implements java.io.Serializable, AssessmentAccessControlIfc
{
  // keep in mind that this id can be an assesmentId or assessmentTemplateId.
  // This depends on the AssessmentBase object (superclass of AssessmentData and
  // AssessmentTemplateData) that is associated with it.
  private Long id;

  private AssessmentIfc assessment;
  private Integer submissionsAllowed;
  private Integer submissionsSaved;// bad name, this is autoSaved
  private Integer assessmentFormat;
  private Integer bookMarkingItem;
  private Integer timeLimit;
  private Integer timedAssessment;
  private Integer retryAllowed;
  private Integer lateHandling;
  private Date startDate;
  private Date dueDate;
  private Date scoreDate;
  private Date feedbackDate;
  private Date retractDate;
  private Integer autoSubmit;  // auto submit when time expires
  private Integer itemNavigation; // linear (1)or random (0)
  private Integer itemNumbering;  // continuous between parts(1), restart between parts(0)
  private String submissionMessage;
  private String finalPageUrl;
  private String releaseTo;
  private String username;
  private String password;
  private Boolean unlimitedSubmissions;

  /**
   * Creates a new SubmissionModel object.
   */
  public PublishedAccessControl()
  {
    this.submissionsAllowed = new Integer(9999); // =  no limit
    this.submissionsSaved = new Integer(1); // no. of copy
  }

  public PublishedAccessControl(Integer submissionsAllowed, Integer submissionsSaved,
                                 Integer assessmentFormat, Integer bookMarkingItem,
                                 Integer timeLimit, Integer timedAssessment,
                                 Integer retryAllowed, Integer lateHandling,
                                 Date startDate, Date dueDate,
                                 Date scoreDate, Date feedbackDate,
                                 String releaseTo)
  {
    this.submissionsAllowed = submissionsAllowed; // =  no limit
    this.submissionsSaved = submissionsSaved;
    this.assessmentFormat = assessmentFormat;
    this.bookMarkingItem =  bookMarkingItem;
    this.timeLimit = timeLimit;
    this.timedAssessment = timedAssessment;
    this.retryAllowed = retryAllowed;
    this.lateHandling = lateHandling;
    this.startDate = startDate;
    this.dueDate = dueDate;
    this.scoreDate = scoreDate;
    this.feedbackDate = feedbackDate;
    this.releaseTo = releaseTo;
  }

  public PublishedAccessControl(Integer submissionsAllowed, Integer submissionsSaved,
                                 Integer assessmentFormat, Integer bookMarkingItem,
                                 Integer timeLimit, Integer timedAssessment,
                                 Integer retryAllowed, Integer lateHandling,
                                 Date startDate, Date dueDate,
                                 Date scoreDate, Date feedbackDate)
  {
    this.submissionsAllowed = submissionsAllowed; // =  no limit
    this.submissionsSaved = submissionsSaved;
    this.assessmentFormat = assessmentFormat;
    this.bookMarkingItem =  bookMarkingItem;
    this.timeLimit = timeLimit;
    this.timedAssessment = timedAssessment;
    this.retryAllowed = retryAllowed;
    this.lateHandling = lateHandling;
    this.startDate = startDate;
    this.dueDate = dueDate;
    this.scoreDate = scoreDate;
    this.feedbackDate = feedbackDate;
  }

  public Object clone() throws CloneNotSupportedException{
    Object cloned = new PublishedAccessControl(
        this.getSubmissionsAllowed(), this.getSubmissionsSaved(),
        this.getAssessmentFormat(), this.getBookMarkingItem(),
        this.getTimeLimit(), this.getTimedAssessment(),
        this.getRetryAllowed(), this.getLateHandling(),
        this.getStartDate(), this.getDueDate(),
        this.getScoreDate(), this.getFeedbackDate(),
        this.getReleaseTo());
    ((PublishedAccessControl)cloned).setRetractDate(this.retractDate);
    ((PublishedAccessControl)cloned).setAutoSubmit(this.autoSubmit);
    ((PublishedAccessControl)cloned).setItemNavigation(this.itemNavigation);
    ((PublishedAccessControl)cloned).setItemNumbering(this.itemNumbering);
    ((PublishedAccessControl)cloned).setSubmissionMessage(this.submissionMessage);
    ((PublishedAccessControl)cloned).setUsername(this.username);
    ((PublishedAccessControl)cloned).setPassword(this.password);
    ((PublishedAccessControl)cloned).setFinalPageUrl(this.finalPageUrl);
    ((PublishedAccessControl)cloned).setUnlimitedSubmissions(this.unlimitedSubmissions);
    return cloned;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public void setAssessment(AssessmentIfc assessment)
  {
    this.assessment = assessment;
  }

  public AssessmentIfc getAssessment()
  {
     return (AssessmentIfc)assessment;
  }

  public void setAssessmentBase(AssessmentBaseIfc assessment)
  {
    setAssessment((AssessmentIfc)assessment);
  }

  public AssessmentBaseIfc getAssessmentBase()
  {
    return getAssessment();
  }

  public Integer getSubmissionsAllowed()
  {
    return submissionsAllowed;
  }

  public void setSubmissionsAllowed(Integer submissionsAllowed)
  {
    this.submissionsAllowed = submissionsAllowed;
  }

  public Integer getSubmissionsSaved()
  {
    return submissionsSaved;
  }

  public void setSubmissionsSaved(Integer submissionsSaved)
  {
    this.submissionsSaved = submissionsSaved;
  }

  public Integer getAssessmentFormat()
  {
    return assessmentFormat;
  }

  public void setAssessmentFormat(Integer assessmentFormat)
  {
    this.assessmentFormat = assessmentFormat;
  }

  public Integer getBookMarkingItem()
  {
    return bookMarkingItem;
  }

  public void setBookMarkingItem(Integer bookMarkingItem)
  {
    this.bookMarkingItem = bookMarkingItem;
  }

  public Integer getTimeLimit()
  {
    return timeLimit;
  }

  public void setTimeLimit(Integer timeLimit)
  {
    this.timeLimit = timeLimit;
  }

  public Integer getTimedAssessment()
  {
    return timedAssessment;
  }

  public void setRetryAllowed(Integer retryAllowed)
  {
    this.retryAllowed = retryAllowed;
  }

  public Integer getRetryAllowed()
  {
    return retryAllowed;
  }

  public void setLateHandling(Integer lateHandling)
  {
    this.lateHandling = lateHandling;
  }

  public Integer getLateHandling()
  {
    return lateHandling;
  }

  public void setTimedAssessment(Integer timedAssessment)
  {
    this.timedAssessment = timedAssessment;
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getDueDate() {
    return this.dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Date getScoreDate() {
    return this.scoreDate;
  }

  public void setScoreDate(Date scoreDate) {
    this.scoreDate = scoreDate;
  }

  public Date getFeedbackDate() {
    return this.feedbackDate;
  }

  public void setFeedbackDate(Date feedbackDate) {
    this.feedbackDate = feedbackDate;
  }

  public Date getRetractDate() {
    return this.retractDate;
  }

  public void setRetractDate(Date retractDate) {
    this.retractDate = retractDate;
  }

  public void setAutoSubmit(Integer autoSubmit)
  {
    this.autoSubmit = autoSubmit;
  }

  public Integer getAutoSubmit()
  {
    return autoSubmit;
  }

  public void setItemNavigation(Integer itemNavigation)
  {
    this.itemNavigation = itemNavigation;
  }

  public Integer getItemNavigation()
  {
    return itemNavigation;
  }

  public void setItemNumbering(Integer itemNumbering)
  {
    this.itemNumbering = itemNumbering;
  }

  public Integer getItemNumbering()
  {
    return itemNumbering;
  }

  public void setSubmissionMessage(String submissionMessage)
  {
    this.submissionMessage = submissionMessage;
  }

  public String getSubmissionMessage()
  {
    return submissionMessage;
  }

  public void setFinalPageUrl(String finalPageUrl) {
    this.finalPageUrl = finalPageUrl;
  }

  public String getFinalPageUrl() {
    return finalPageUrl;
  }

  public String getReleaseTo() {
    return this.releaseTo;
  }

  public void setReleaseTo(String releaseTo) {
    this.releaseTo = releaseTo;
  }
  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getUnlimitedSubmissions() {
    return this.unlimitedSubmissions;
  }

  public void setUnlimitedSubmissions(Boolean unlimitedSubmissions) {
    this.unlimitedSubmissions = unlimitedSubmissions;
  }

}
