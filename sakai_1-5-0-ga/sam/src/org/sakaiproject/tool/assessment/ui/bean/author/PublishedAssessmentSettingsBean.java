package org.sakaiproject.tool.assessment.ui.bean.author;

import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedMetaData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SecuredIPAddressIfc;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.Iterator;
import java.util.Calendar;
import java.util.HashMap;
import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishingTarget;

public class PublishedAssessmentSettingsBean
    implements Serializable {
  private static final org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(PublishedAssessmentSettingsBean.class);
    /**
     *  we use the calendar widget which uses 'MM-dd-yyyy hh:mm:ss a'
     *  used to take the internal format from calendar picker and move it
     *  transparently in and out of the date properties
     *
     */
    private static final String DISPLAY_DATEFORMAT = "MM-dd-yyyy hh:mm:ss a";
    private static final SimpleDateFormat displayFormat = new SimpleDateFormat(
        DISPLAY_DATEFORMAT);

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -630950053380808339L;
  private PublishedAssessmentFacade assessment;
  private Long assessmentId;
  private String title;
  private String creator;
  private String description;
  private boolean hasQuestions;

  // meta data
  private String objectives;
  private String keywords;
  private String rubrics;
  private String authors;

  // these are properties in PublishedAccessControl
  private Date startDate;
  private Date dueDate;
  private Date retractDate;
  private Date feedbackDate;
  private Integer timeLimit; // in seconds, calculated from timedHours & timedMinutes
  private Integer timedHours;
  private Integer timedMinutes;
  private Integer timedSeconds;
  private boolean timedAssessment = false;
  private boolean autoSubmit = false;
  private String assessmentFormat; // question (1)/part(2)/assessment(3) on separate page
  private String itemNavigation; // linear (1)or random (2)
  private String itemNumbering; // continuous between parts(1), restart between parts(2)
  private String unlimitedSubmissions;
  private String submissionsAllowed;
  private String submissionsSaved; // bad name, this is autoSaved
  private String lateHandling;
  private String submissionMessage;
  private SelectItem[] publishingTargets;
  private String[] targetSelected;
  private String releaseTo;
  private String username;
  private String password;
  private String finalPageUrl;
  private String ipAddresses;

  // properties of PublishedFeedback
  private String feedbackDelivery; // immediate, on specific date , no feedback
  private String editComponents; // 0 = cannot
  private boolean showQuestionText = false;
  private boolean showStudentResponse = false;
  private boolean showCorrectResponse = false;
  private boolean showStudentScore = false;
  private boolean showQuestionLevelFeedback = false;
  private boolean showSelectionLevelFeedback = false; // must be MC
  private boolean showGraderComments = false;
  private boolean showStatistics = false;

  // properties of PublishedEvaluationModel
  private String anonymousGrading;
  private String toDefaultGradebook;
  private String scoringType;
  private String bgColor;
  private String bgImage;
  private HashMap values = new HashMap();

  // extra properties
  private String publishedUrl;
  private String alias;

  /*
   * Creates a new AssessmentBean object.
   */
  public PublishedAssessmentSettingsBean() {
  }

  public PublishedAssessmentFacade getAssessment() {
    return assessment;
  }

  public void setAssessment(PublishedAssessmentFacade assessment) {
    try {
      this.assessment = assessment;
      System.out.println("** beginning of assessment"+assessment);
      // set the valueMap
      setValueMap(assessment.getAssessmentMetaDataMap());
      this.assessmentId = assessment.getPublishedAssessmentId();
      this.title = assessment.getTitle();
      this.creator = AgentFacade.getDisplayName(assessment.getCreatedBy());
      this.description = assessment.getDescription();
      // assessment meta data
      this.authors = assessment.getAssessmentMetaDataByLabel(AssessmentMetaDataIfc.
          AUTHORS);
      this.objectives = assessment.getAssessmentMetaDataByLabel(
          AssessmentMetaDataIfc.OBJECTIVES);
      this.keywords = assessment.getAssessmentMetaDataByLabel(AssessmentMetaDataIfc.
          KEYWORDS);
      this.rubrics = assessment.getAssessmentMetaDataByLabel(AssessmentMetaDataIfc.
          RUBRICS);
      this.bgColor = assessment.getAssessmentMetaDataByLabel(AssessmentMetaDataIfc.
          BGCOLOR);
      this.bgImage = assessment.getAssessmentMetaDataByLabel(AssessmentMetaDataIfc.
          BGIMAGE);

      // these are properties in AssessmentAccessControl
      AssessmentAccessControlIfc accessControl = null;
      accessControl = assessment.getAssessmentAccessControl();
      System.out.println("**** beginning of access control="+accessControl);
      if (accessControl != null) {
        this.startDate = accessControl.getStartDate();
        this.dueDate = accessControl.getDueDate();
        this.retractDate = accessControl.getRetractDate();
        this.feedbackDate = accessControl.getFeedbackDate();

        // deal with releaseTo
        this.releaseTo = accessControl.getReleaseTo(); // list of String
        this.publishingTargets = getPublishingTargets();
        this.targetSelected = getTargetSelected(releaseTo);

        this.timeLimit = accessControl.getTimeLimit(); // in seconds
        if (timeLimit !=null && timeLimit.intValue()>0)
          setTimeLimitDisplay(timeLimit.intValue());
        if ((new Integer(1)).equals(assessment.getAssessmentAccessControl().getTimedAssessment()))
          this.timedAssessment = true;
        if ((new Integer(1)).equals(assessment.getAssessmentAccessControl().getAutoSubmit()))
          this.autoSubmit = true;
        if (accessControl.getAssessmentFormat()!=null)
          this.assessmentFormat = accessControl.getAssessmentFormat().toString(); // question/part/assessment on separate page
        if (accessControl.getItemNavigation()!=null)
          this.itemNavigation = accessControl.getItemNavigation().toString(); // linear or random
        if (accessControl.getItemNumbering()!=null)
          this.itemNumbering = accessControl.getItemNumbering().toString();
        if (accessControl.getSubmissionsSaved()!=null)
          this.submissionsSaved = accessControl.getSubmissionsSaved().toString();

        // default to unlimited if control value is null
        if (accessControl.getUnlimitedSubmissions()!=null && !accessControl.getUnlimitedSubmissions().booleanValue()){
          this.unlimitedSubmissions=AssessmentAccessControlIfc.LIMITED_SUBMISSIONS.toString();
          this.submissionsAllowed = accessControl.getSubmissionsAllowed().toString();
        }
        else{
          this.unlimitedSubmissions=AssessmentAccessControlIfc.UNLIMITED_SUBMISSIONS.toString();
          this.submissionsAllowed="";
        }
        System.out.println("**assessmentSettingBean, control unlimited submissin="+accessControl.getUnlimitedSubmissions());
        System.out.println("**assessmentSettingBean, unlimited submissions="+this.unlimitedSubmissions);
        System.out.println("**assessmentSettingBean, control submissionsAllowed="+accessControl.getSubmissionsAllowed());
        System.out.println("**assessmentSettingBean, submissionsAllowed="+this.submissionsAllowed);

        if (accessControl.getLateHandling() !=null)
          this.lateHandling = accessControl.getLateHandling().toString();
        if (accessControl.getSubmissionsSaved()!=null)
          this.submissionsSaved = accessControl.getSubmissionsSaved().toString();
        this.submissionMessage = accessControl.getSubmissionMessage();
        this.username = accessControl.getUsername();
        this.password = accessControl.getPassword();
        this.finalPageUrl = accessControl.getFinalPageUrl();
        System.out.println("**** end of access control");
      }

      // properties of AssesmentFeedback
      AssessmentFeedbackIfc feedback = assessment.getAssessmentFeedback();
      System.out.println("** feeback ="+feedback);
      if (feedback != null) {
        if (feedback.getFeedbackDelivery()!=null);
          this.feedbackDelivery = feedback.getFeedbackDelivery().toString();
        if ((Boolean.TRUE).equals(feedback.getShowQuestionText()))
          this.showQuestionText = true;
        if ((Boolean.TRUE).equals(feedback.getShowStudentResponse()))
          this.showStudentResponse = true;
        if ((Boolean.TRUE).equals(feedback.getShowCorrectResponse()))
          this.showCorrectResponse = true;
        if ((Boolean.TRUE).equals(feedback.getShowStudentScore()))
          this.showStudentScore = true;
        if ((Boolean.TRUE).equals(feedback.getShowQuestionLevelFeedback()))
          this.showQuestionLevelFeedback = true;
        if ((Boolean.TRUE).equals(feedback.getShowSelectionLevelFeedback()))
          this.showSelectionLevelFeedback = true;// must be MC
        if ((Boolean.TRUE).equals(feedback.getShowGraderComments()))
          this.showGraderComments = true;
        if ((Boolean.TRUE).equals(feedback.getShowStatistics()))
          this.showStatistics = true;
      }

      // properties of EvaluationModel
      EvaluationModelIfc evaluation = assessment.getEvaluationModel();
      System.out.println("** Evaluation ="+evaluation);
      if (evaluation != null) {
        if (evaluation.getAnonymousGrading()!=null)
          this.anonymousGrading = evaluation.getAnonymousGrading().toString();
        if (evaluation.getToGradeBook()!=null )
          this.toDefaultGradebook = evaluation.getToGradeBook().toString();
        if (evaluation.getScoringType()!=null)
          this.scoringType = evaluation.getScoringType().toString();
      }

      //set IPAddresses
      setIpAddresses(assessment);

      // publishedUrl
      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext extContext = context.getExternalContext();
      // get the alias to the pub assessment
      this.alias = assessment.getAssessmentMetaDataByLabel(
          AssessmentMetaDataIfc.ALIAS);
      String server = ( (javax.servlet.http.HttpServletRequest) extContext.
                       getRequest()).getRequestURL().toString();
      int index = server.indexOf(extContext.getRequestContextPath() + "/"); // "/samigo/"
      server = server.substring(0, index);
      System.out.println("servletPath=" + server);
      String url = server + extContext.getRequestContextPath();
      this.publishedUrl = url + "/servlet/Login?id=" + this.alias;
      System.out.println("**publishedUrl =" + publishedUrl);
    }
    catch (Exception ex) {
    }
    System.out.println("** end of setting assessment"+assessment);
  }

  // properties from Assessment
  public Long getAssessmentId() {
    return this.assessmentId;
  }

  public void setAssessmentId(Long assessmentId) {
    this.assessmentId = assessmentId;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  // properties form AssessmentMetaData
  public String getObjectives() {
    return this.objectives;
  }

  public void setObjectives(String objectives) {
    this.objectives = objectives;
  }

  public String getKeywords() {
    return this.keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getRubrics() {
    return this.rubrics;
  }

  public void setRubrics(String rubrics) {
    this.rubrics = rubrics;
  }

  public String getAuthors() {
    return this.authors;
  }

  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public String getBgColor() {
    return this.bgColor;
  }

  public void setBgColor(String bgColor) {
    this.bgColor = bgColor;
  }

  public String getBgImage() {
    return this.bgImage;
  }

  public void setBgImage(String bgImage) {
    this.bgImage = bgImage;
  }

  public boolean getHasQuestions() {
    return this.hasQuestions;
  }

  public void setHasQuestions(boolean hasQuestions) {
    this.hasQuestions = hasQuestions;
  }

  // copied from AssessmentAccessControl ;-)
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

  public String getReleaseTo() {
    String anonymousString="";
    String authenticatedString="";
    if (values.get("hasAnonymousRelease")!=null)
      anonymousString = values.get("hasAnonymousRelease").toString();
    if ( values.get("hasAuthenticatedRelease")!=null)
      authenticatedString = values.get("hasAuthenticatedRelease").toString();
    if (("true").equals(anonymousString) && ("true").equals(authenticatedString))
      this.releaseTo="anonymous; authenticated users";
    else if (("true").equals(anonymousString))
      this.releaseTo="anonymous";
    else if (("true").equals(authenticatedString))
      this.releaseTo="authenticated users";
    return this.releaseTo;
  }

  public void setReleaseTo(String releaseTo) {
    this.releaseTo = releaseTo;
  }

  public Integer getTimeLimit() {
    return new Integer(timedHours.intValue()*3600
        + timedMinutes.intValue()*60
        + timedSeconds.intValue());
  }

  public void setTimeLimit(Integer timeLimit) {
    this.timeLimit = timeLimit;
  }

  public void setTimedHours(Integer timedHours) {
    this.timedHours = timedHours;
  }

  public Integer getTimedHours() {
    return timedHours;
  }

  public void setTimedMinutes(Integer timedMinutes) {
    this.timedMinutes =  timedMinutes;
  }

  public Integer getTimedMinutes() {
    return timedMinutes;
  }

  public void setTimedSeconds(Integer timedSeconds) {
    this.timedSeconds =  timedSeconds;
  }

  public Integer getTimedSeconds() {
    return timedSeconds;
  }

  public boolean getTimedAssessment() {
    return timedAssessment;
  }

  public void setTimedAssessment(boolean timedAssessment) {
    this.timedAssessment = timedAssessment;
  }

  public boolean getAutoSubmit() {
    return autoSubmit;
  }

  public void setAutoSubmit(boolean autoSubmit) {
    this.autoSubmit = autoSubmit;
  }

  public String getAssessmentFormat() {
    return assessmentFormat;
  }

  public void setAssessmentFormat(String assessmentFormat) {
    this.assessmentFormat = assessmentFormat;
  }

  public String getItemNavigation() {
    return itemNavigation;
  }

  public void setItemNavigation(String itemNavigation) {
    this.itemNavigation = itemNavigation;
  }

  public String getItemNumbering() {
    return itemNumbering;
  }

  public void setItemNumbering(String itemNumbering) {
    this.itemNumbering = itemNumbering;
  }

  public String getUnlimitedSubmissions() {
    return unlimitedSubmissions;
  }

  public void setUnlimitedSubmissions(String unlimitedSubmissions) {
    this.unlimitedSubmissions = unlimitedSubmissions;
  }

  public String getSubmissionsAllowed() {
    return submissionsAllowed;
  }

  public void setSubmissionsAllowed(String submissionsAllowed) {
      this.submissionsAllowed = submissionsAllowed;
  }

  public void setLateHandling(String lateHandling) {
    this.lateHandling = lateHandling;
  }

  public String getLateHandling() {
    return lateHandling;
  }

  // bad name - this is autoSaved
  public void setSubmissionsSaved(String submissionSaved) {
    this.submissionsSaved = submissionSaved;
  }

  public String getSubmissionsSaved() {
    return submissionsSaved;
  }

  public void setSubmissionMessage(String submissionMessage) {
    this.submissionMessage = submissionMessage;
  }

  public String getSubmissionMessage() {
    return submissionMessage;
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

  public void setFinalPageUrl(String finalPageUrl) {
    this.finalPageUrl = finalPageUrl;
  }

  public String getFinalPageUrl() {
    return finalPageUrl;
  }

  public String getFeedbackDelivery() {
    return feedbackDelivery;
  }

  public void setFeedbackDelivery(String feedbackDelivery) {
    this.feedbackDelivery = feedbackDelivery;
  }

  public boolean getShowQuestionText() {
    return showQuestionText;
  }

  public void setShowQuestionText(boolean showQuestionText) {
    this.showQuestionText = showQuestionText;
  }

  public boolean getShowStudentResponse() {
    return showStudentResponse;
  }

  public void setShowStudentResponse(boolean showStudentResponse) {
    this.showStudentResponse = showStudentResponse;
  }

  public boolean getShowCorrectResponse() {
    return showCorrectResponse;
  }

  public void setShowCorrectResponse(boolean showCorrectResponse) {
    this.showCorrectResponse = showCorrectResponse;
  }

  public boolean getShowStudentScore() {
    return showStudentScore;
  }

  public void setShowStudentScore(boolean showStudentScore) {
    this.showStudentScore = showStudentScore;
  }

  public boolean getShowQuestionLevelFeedback() {
    return showQuestionLevelFeedback;
  }

  public void setShowQuestionLevelFeedback(boolean showQuestionLevelFeedback) {
    this.showQuestionLevelFeedback = showQuestionLevelFeedback;
  }

  public boolean getShowSelectionLevelFeedback() {
    return showSelectionLevelFeedback;
  }

  public void setShowSelectionLevelFeedback(boolean showSelectionLevelFeedback) {
    this.showSelectionLevelFeedback = showSelectionLevelFeedback;
  }

  public boolean getShowGraderComments() {
    return showGraderComments;
  }

  public void setShowGraderComments(boolean showGraderComments) {
    this.showGraderComments = showGraderComments;
  }

  public boolean getShowStatistics() {
    return showStatistics;
  }

  public void setShowStatistics(boolean showStatistics) {
    this.showStatistics = showStatistics;
  }

  public String getAnonymousGrading() {
    return this.anonymousGrading;
  }

  public void setAnonymousGrading(String anonymousGrading) {
    this.anonymousGrading = anonymousGrading;
  }

  public String getToDefaultGradebook() {
    return this.toDefaultGradebook;
  }

  public void setToDefaultGradebook(String toDefaultGradebook) {
    this.toDefaultGradebook = toDefaultGradebook;
  }

  public String getScoringType() {
    return this.scoringType;
  }

  public void setScoringType(String scoringType) {
    this.scoringType = scoringType;
  }


  public void setValue(String key, Object value){
    this.values.put(key, value);
  }

  public Object getValue(String key)
  {
    if (this.values.get(key) == null)
    {
      return Boolean.FALSE;
    }

    return values.get(key);
  }

  public void setValueMap(HashMap newMap){
    this.values = newMap;
  }

  public HashMap getValueMap(){
    return values;
  }

  public String getDateString(Date date) {
    if (date!=null){
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      int mon = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);
      int year = c.get(Calendar.YEAR);
      String dateString = mon + "/" + day + "/" + year;
      System.out.println("** date is = " + dateString);
      return dateString;
    }
    else
      return "";
  }

  public void setTimeLimitDisplay(int time){
    System.out.println("****the time is ="+time);
    this.timedHours=new Integer(time/60/60);
    System.out.println("****the timedHours is ="+time/3600);
    this.timedMinutes = new Integer((time/60)%60);
    System.out.println("****the timedMin is ="+(time/60)%60);
    this.timedSeconds = new Integer(time % 60);
    System.out.println("****the timedSec is ="+time %60);
  }

  // followings are set of SelectItem[] used in authorSettings.jsp
  public SelectItem[] getHours() {
    return hours;
  }

  public void setHours(SelectItem[] hours) {
    this.hours =  hours;
  }

  public SelectItem[] getMins() {
    return mins;
  }

  public void setMins(SelectItem[] mins) {
    this.mins =  mins;
  }

  private static List months;
  private static List days;
  private static SelectItem[] mins;
  private static SelectItem[] hours;
  static{
    months = new ArrayList();
    for (int i=1; i<=12; i++){
      months.add(new SelectItem(new Integer(i)));
    }
    days = new ArrayList();
    for (int i=1; i<32; i++){
      days.add(new SelectItem(new Integer(i)));
    }
    hours = new SelectItem[24];
    for (int i=0; i<24; i++){
      if (i < 10)
        hours[i] = new SelectItem(new Integer(i),"0"+i);
      else
        hours[i] = new SelectItem(new Integer(i),i+"");
    }
    mins = new SelectItem[60];
    for (int i=0; i<60; i++){
      if (i < 10)
        mins[i] = new SelectItem(new Integer(i),"0"+i);
      else
        mins[i] = new SelectItem(new Integer(i),i+"");
    }
  }

  public String getIpAddresses() {
    return ipAddresses;
  }

  public void setIpAddresses(PublishedAssessmentFacade assessment) {
      // ip addresses
      this.ipAddresses = "";
      Set ipAddressSet = assessment.getSecuredIPAddressSet();
      if (ipAddressSet != null){
        Iterator iter = ipAddressSet.iterator();
        while (iter.hasNext()) {
          SecuredIPAddressIfc ip = (SecuredIPAddressIfc) iter.next();
          this.ipAddresses = ip.getIpAddress()+"\n"+this.ipAddresses;
        }
      }
      System.out.println("**IPAddress ="+this.ipAddresses);
  }

  // the following methods are used to take the internal format from
  // calendar picker and move it transparently in and out of the date
  // properties

  /**
   * date from internal string of calendar widget
   * @param date Date object
   * @return date String "MM-dd-yyyy hh:mm:ss a"
   */
  private static String getDisplayFormatFromDate(Date date) {
    String dateString = "";
    if (date == null) {
      return dateString;
    }

    try {
      LOG.warn("DATETIME=:" + date + ":");
      dateString = displayFormat.format(date);
    }
    catch (Exception ex) {
      // we will leave it as an empty string
      LOG.warn("Unable to format date.");
      ex.printStackTrace(System.out);
    }
    return dateString;
  }

  /**
   * format according to internal requirements of calendar widget
   * @param dateString "MM-dd-yyyy hh:mm:ss a"
   * @return Date object
   */
  private static Date getDateFromDisplayFormat(String dateString) {
    Date date = null;
    if (dateString == null || dateString.trim().equals("")) {
      return date;
    }

    try {
      LOG.warn("DATETIME=:" + dateString + ":");
      date = (Date) displayFormat.parse(dateString);
    }
    catch (Exception ex) {
      // we will leave it as a null date
      LOG.warn("Unable to format date.");
      ex.printStackTrace(System.out);
    }

    return date;
  }

  public String getStartDateString()
  {
    return getDisplayFormatFromDate(startDate);
  }
  public void setStartDateString(String startDateString)
  {
    this.startDate = getDateFromDisplayFormat(startDateString);
  }
  public String getDueDateString()
  {
    return getDisplayFormatFromDate(dueDate);
  }
  public void setDueDateString(String dueDateString)
  {
    this.dueDate  = getDateFromDisplayFormat(dueDateString);
  }
  public String getRetractDateString()
  {
    return getDisplayFormatFromDate(retractDate);
  }
  public void setRetractDateString(String retractDateString)
  {
    this.retractDate  = getDateFromDisplayFormat(retractDateString);
  }
  public String getFeedbackDateString()
  {
    return getDisplayFormatFromDate(feedbackDate);
  }
  public void setFeedbackDateString(String feedbackDateString)
  {
    this.feedbackDate  = getDateFromDisplayFormat(feedbackDateString);
  }
  public String getPublishedUrl() {
    return this.publishedUrl;
  }

  public void setPublishedUrl(String publishedUrl) {
    this.publishedUrl = publishedUrl;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public SelectItem[] getPublishingTargets() {
    PublishingTarget publishingTarget = new PublishingTarget();
    HashMap targets = publishingTarget.getTargets();
    Set e = targets.keySet();
    Iterator iter = e.iterator();
    SelectItem[] target = new SelectItem[targets.size()];
    while (iter.hasNext()) {
      for (int i = 0; i < e.size(); i++) {
        target[i] = new SelectItem( (String) iter.next());
      }
    }
    return target;
  }

  public void setTargetSelected(String[] targetSelected) {
    this.targetSelected = targetSelected;
  }

  public String[] getTargetSelected() {
    return targetSelected;
  }

  public String[] getTargetSelected(String releaseTo) {
    if (releaseTo != null) {
      this.targetSelected = releaseTo.split(",");
      for (int i = 0; i < targetSelected.length; i++) {
        targetSelected[i] = targetSelected[i].trim();
      }
    }
    return this.targetSelected;
  }


}
