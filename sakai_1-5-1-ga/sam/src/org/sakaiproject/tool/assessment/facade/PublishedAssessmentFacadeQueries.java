package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentTemplateData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.EvaluationModel;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedItemFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedItemMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAnswer;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedAnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedEvaluationModel;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedSectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedSecuredIPAddress;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SecuredIPAddress;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentTemplateIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    PublishedAssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
//import org.sakaiproject.tool.assessment.facade.authz.AuthorizationFacadeQueries;
//import org.sakaiproject.tool.assessment.facade.authz.AuthorizationFacade;
//import org.sakaiproject.tool.assessment.facade.authz.QualifierFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentGradingFacade;
import org.sakaiproject.tool.assessment.util.PagingUtilQueries;
import org.osid.assessment.AssessmentException;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishingTarget;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.lang.Integer;
import java.io.FileOutputStream;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.type.Type;
import org.apache.log4j.Logger;
import org.navigoproject.osid.impl.PersistenceService;

// import org.apache.log4j.*;
import org.sakaiproject.tool.assessment.util.HibernateUtil;
import org.osid.assessment.AssessmentException;
import java.io.*;
import java.util.Vector;

public class PublishedAssessmentFacadeQueries
    extends HibernateDaoSupport {

  //static Category errorLogger = Category.getInstance("errorLogger");
  public static String STARTDATE = "assessmentAccessControl.startDate";
  public static String DUEDATE = "assessmentAccessControl.dueDate";
  public static String RETRACTDATE = "assessmentAccessControl.retractDate";
  public static String RELEASETO = "assessmentAccessControl.releaseTo";
  public static String TITLE = "title";
  public static String GRADE = "finalScore";
  public static String DUE = "dueDate";
  public static String RAW = "totalAutoScore";
  public static String TIME = "timeElapsed";
  public static String SUBMITTED = "submittedDate";
  private static Logger LOG =
      Logger.getLogger(AssessmentFacadeQueries.class.getName());

  public PublishedAssessmentFacadeQueries() {
  }

  public IdImpl getId(String id) {
    return new IdImpl(id);
  }

  public IdImpl getId(Long id) {
    return new IdImpl(id);
  }

  public IdImpl getId(long id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentId(String id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentId(Long id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentId(long id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentTemplateId(String id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentTemplateId(Long id) {
    return new IdImpl(id);
  }

  public IdImpl getAssessmentTemplateId(long id) {
    return new IdImpl(id);
  }


  public PublishedAssessmentData preparePublishedAssessment(AssessmentData a) {
    PublishedAssessmentData publishedAssessment = new PublishedAssessmentData(a.
        getTitle(), a.getDescription(),
        a.getComments(), TypeFacade.HOMEWORK, a.getInstructorNotification(),
        a.getTesteeNotification(), a.getMultipartAllowed(),
        a.getStatus(), AgentFacade.getAgentString(), new Date(),
        AgentFacade.getAgentString(), new Date());
    publishedAssessment.setAssessment(a);

    // section set
    Set publishedSectionSet = preparePublishedSectionSet(publishedAssessment,
        a.getSectionSet());
    publishedAssessment.setSectionSet(publishedSectionSet);

    // access control
    PublishedAccessControl publishedAccessControl =
        preparePublishedAccessControl(publishedAssessment,
                                      (AssessmentAccessControl) a.
                                      getAssessmentAccessControl());
    publishedAssessment.setAssessmentAccessControl(publishedAccessControl);

    // evaluation model
    PublishedEvaluationModel publishedEvaluationModel =
        preparePublishedEvaluationModel(publishedAssessment,
                                        (EvaluationModel) a.
                                        getEvaluationModel());
    publishedAssessment.setEvaluationModel(publishedEvaluationModel);

    // feedback
    PublishedFeedback publishedFeedback = preparePublishedFeedback(
        publishedAssessment,
        (AssessmentFeedback) a.getAssessmentFeedback());
    publishedAssessment.setAssessmentFeedback(publishedFeedback);

    // metadata
    Set publishedMetaDataSet = preparePublishedMetaDataSet(publishedAssessment,
        a.getAssessmentMetaDataSet());
    //System.out.println("******* metadata set" + a.getAssessmentMetaDataSet());
    //System.out.println("******* published metadata set" + publishedMetaDataSet);
    publishedAssessment.setAssessmentMetaDataSet(publishedMetaDataSet);

    // let's check if we need a publishedUrl
    String releaseTo = publishedAccessControl.getReleaseTo();
    if (releaseTo != null) {
      boolean anonymousAllowed = ( (releaseTo).indexOf(
          "Anonymous Users") > -1);
      if (anonymousAllowed) {
        // generate an alias to the pub assessment
        String alias = AgentFacade.getAgentString() + (new Date()).getTime();
        PublishedMetaData meta = new PublishedMetaData(publishedAssessment,
            "ALIAS", alias);
        publishedMetaDataSet.add(meta);
        publishedAssessment.setAssessmentMetaDataSet(publishedMetaDataSet);
      }
    }

    // IPAddress
    Set publishedIPSet = preparePublishedSecuredIPSet(publishedAssessment,
        a.getSecuredIPAddressSet());
    publishedAssessment.setSecuredIPAddressSet(publishedIPSet);

    return publishedAssessment;
  }

  public PublishedFeedback preparePublishedFeedback(PublishedAssessmentData p,
      AssessmentFeedback a) {
    if (a == null) {
      return null;
    }
    PublishedFeedback publishedFeedback = new PublishedFeedback(
        a.getFeedbackDelivery(), a.getEditComponents(), a.getShowQuestionText(),
        a.getShowStudentResponse(), a.getShowCorrectResponse(),
        a.getShowStudentScore(),
        a.getShowQuestionLevelFeedback(), a.getShowSelectionLevelFeedback(),
        a.getShowGraderComments(), a.getShowStatistics());
    publishedFeedback.setAssessmentBase(p);
    return publishedFeedback;
  }

  public PublishedAccessControl preparePublishedAccessControl(
      PublishedAssessmentData p, AssessmentAccessControl a) {
    if (a == null) {
      return new PublishedAccessControl();
    }
    PublishedAccessControl publishedAccessControl = new PublishedAccessControl(
        a.getSubmissionsAllowed(), a.getSubmissionsSaved(),
        a.getAssessmentFormat(),
        a.getBookMarkingItem(), a.getTimeLimit(), a.getTimedAssessment(),
        a.getRetryAllowed(), a.getLateHandling(), a.getStartDate(),
        a.getDueDate(), a.getScoreDate(), a.getFeedbackDate());
    publishedAccessControl.setRetractDate(a.getRetractDate());
    publishedAccessControl.setAutoSubmit(a.getAutoSubmit());
    publishedAccessControl.setItemNavigation(a.getItemNavigation());
    publishedAccessControl.setItemNumbering(a.getItemNumbering());
    publishedAccessControl.setSubmissionMessage(a.getSubmissionMessage());
    publishedAccessControl.setReleaseTo(a.getReleaseTo());
    publishedAccessControl.setUsername(a.getUsername());
    publishedAccessControl.setPassword(a.getPassword());
    publishedAccessControl.setFinalPageUrl(a.getFinalPageUrl());
    publishedAccessControl.setUnlimitedSubmissions(a.getUnlimitedSubmissions());
    publishedAccessControl.setAssessmentBase(p);
    return publishedAccessControl;
  }

  public PublishedEvaluationModel preparePublishedEvaluationModel(
      PublishedAssessmentData p, EvaluationModel e) {
    if (e == null) {
      return null;
    }
    PublishedEvaluationModel publishedEvaluationModel = new
        PublishedEvaluationModel(
        e.getEvaluationComponents(), e.getScoringType(),
        e.getNumericModelId(), e.getFixedTotalScore(),
        e.getGradeAvailable(), e.getIsStudentIdPublic(),
        e.getAnonymousGrading(), e.getAutoScoring(),
        e.getToGradeBook());
    publishedEvaluationModel.setAssessmentBase(p);
    return publishedEvaluationModel;
  }

  public Set preparePublishedMetaDataSet(PublishedAssessmentData p,
                                         Set metaDataSet) {
    HashSet h = new HashSet();
    Iterator i = metaDataSet.iterator();
    while (i.hasNext()) {
      AssessmentMetaData metaData = (AssessmentMetaData) i.next();
      PublishedMetaData publishedMetaData = new PublishedMetaData(
          p, metaData.getLabel(), metaData.getEntry());
      h.add(publishedMetaData);
    }
    return h;
  }

  public Set preparePublishedSecuredIPSet(PublishedAssessmentData p, Set ipSet) {
    HashSet h = new HashSet();
    Iterator i = ipSet.iterator();
    if (ipSet != null) {
      while (i.hasNext()) {
        SecuredIPAddress ip = (SecuredIPAddress) i.next();
        PublishedSecuredIPAddress publishedIP = new PublishedSecuredIPAddress(
            p, ip.getHostname(), ip.getIpAddress());
        h.add(publishedIP);
      }
    }
    return h;
  }

  public Set preparePublishedSectionSet(PublishedAssessmentData
                                        publishedAssessment, Set sectionSet) {
    //System.out.println("**published section size = " + sectionSet.size());
    HashSet h = new HashSet();
    Iterator i = sectionSet.iterator();
    while (i.hasNext()) {
      SectionData section = (SectionData) i.next();
      PublishedSectionData publishedSection = new PublishedSectionData(
          section.getDuration(), section.getSequence(), section.getTitle(),
          section.getDescription(), section.getTypeId(), section.getStatus(),
          section.getCreatedBy(), section.getCreatedDate(),
          section.getLastModifiedBy(),
          section.getLastModifiedDate());
      Set publishedItemSet = preparePublishedItemSet(publishedSection,
          section.getItemSet());
      publishedSection.setItemSet(publishedItemSet);
      publishedSection.setAssessment(publishedAssessment);
      h.add(publishedSection);
    }
    return h;
  }

  public Set preparePublishedItemSet(PublishedSectionData publishedSection,
                                     Set itemSet) {
    //System.out.println("**published item size = " + itemSet.size());
    HashSet h = new HashSet();
    Iterator j = itemSet.iterator();
    while (j.hasNext()) {
      ItemData item = (ItemData) j.next();
      PublishedItemData publishedItem = new PublishedItemData(
          publishedSection, item.getSequence(), item.getDuration(),
          item.getInstruction(),
          item.getDescription(), item.getTypeId(), item.getGrade(),
          item.getScore(),
          item.getHint(), item.getHasRationale(), item.getStatus(),
          item.getCreatedBy(),
          item.getCreatedDate(), item.getLastModifiedBy(),
          item.getLastModifiedDate(),
          null, null, null, // set ItemTextSet, itemMetaDataSet and itemFeedbackSet later
          item.getTriesAllowed());
      Set publishedItemTextSet = preparePublishedItemTextSet(publishedItem,
          item.getItemTextSet());
      Set publishedItemMetaDataSet = preparePublishedItemMetaDataSet(
          publishedItem, item.getItemMetaDataSet());
      Set publishedItemFeedbackSet = preparePublishedItemFeedbackSet(
          publishedItem, item.getItemFeedbackSet());
      publishedItem.setItemTextSet(publishedItemTextSet);
      publishedItem.setItemMetaDataSet(publishedItemMetaDataSet);
      publishedItem.setItemFeedbackSet(publishedItemFeedbackSet);
      h.add(publishedItem);
    }
    return h;
  }

  public Set preparePublishedItemTextSet(PublishedItemData publishedItem,
                                         Set itemTextSet) {
    //System.out.println("**published item text size = " + itemTextSet.size());
    HashSet h = new HashSet();
    Iterator k = itemTextSet.iterator();
    while (k.hasNext()) {
      ItemText itemText = (ItemText) k.next();
      //System.out.println("**item text id =" + itemText.getId());
      PublishedItemText publishedItemText = new PublishedItemText(
          publishedItem, itemText.getSequence(), itemText.getText(), null);
      Set publishedAnswerSet = preparePublishedAnswerSet(publishedItemText,
          itemText.getAnswerSet());
      publishedItemText.setAnswerSet(publishedAnswerSet);
      h.add(publishedItemText);
    }
    return h;
  }

  public Set preparePublishedItemMetaDataSet(PublishedItemData publishedItem,
                                             Set itemMetaDataSet) {
    HashSet h = new HashSet();
    Iterator n = itemMetaDataSet.iterator();
    while (n.hasNext()) {
      ItemMetaData itemMetaData = (ItemMetaData) n.next();
      PublishedItemMetaData publishedItemMetaData = new PublishedItemMetaData(
          publishedItem, itemMetaData.getLabel(), itemMetaData.getEntry());
      h.add(publishedItemMetaData);
    }
    return h;
  }

  public Set preparePublishedItemFeedbackSet(PublishedItemData publishedItem,
                                             Set itemFeedbackSet) {
    HashSet h = new HashSet();
    Iterator o = itemFeedbackSet.iterator();
    while (o.hasNext()) {
      ItemFeedback itemFeedback = (ItemFeedback) o.next();
      PublishedItemFeedback publishedItemFeedback = new PublishedItemFeedback(
          publishedItem, itemFeedback.getTypeId(), itemFeedback.getText());
      h.add(publishedItemFeedback);
    }
    return h;
  }

  public Set preparePublishedAnswerSet(PublishedItemText publishedItemText,
                                       Set answerSet) {
    //System.out.println("**published answer size = " + answerSet.size());
    HashSet h = new HashSet();
    Iterator l = answerSet.iterator();
    while (l.hasNext()) {
      Answer answer = (Answer) l.next();
      PublishedAnswer publishedAnswer = new PublishedAnswer(
          publishedItemText, answer.getText(), answer.getSequence(),
          answer.getLabel(),
          answer.getIsCorrect(), answer.getGrade(), answer.getScore(), null);
      Set publishedAnswerFeedbackSet = preparePublishedAnswerFeedbackSet(
          publishedAnswer, answer.getAnswerFeedbackSet());
      publishedAnswer.setAnswerFeedbackSet(publishedAnswerFeedbackSet);
      h.add(publishedAnswer);
    }
    return h;
  }

  public Set preparePublishedAnswerFeedbackSet(PublishedAnswer publishedAnswer,
                                               Set answerFeedbackSet) {
    HashSet h = new HashSet();
    Iterator m = answerFeedbackSet.iterator();
    while (m.hasNext()) {
      AnswerFeedback answerFeedback = (AnswerFeedback) m.next();
      PublishedAnswerFeedback publishedAnswerFeedback = new
          PublishedAnswerFeedback(
          publishedAnswer, answerFeedback.getTypeId(), answerFeedback.getText());
      h.add(publishedAnswerFeedback);
    }
    return h;
  }

  public PublishedAssessmentFacade getPublishedAssessment(Long assessmentId) {
    PublishedAssessmentData a = loadPublishedAssessment(assessmentId);
    a.setSectionSet(getSectionSetForAssessment(a));
    PublishedAssessmentFacade f = new PublishedAssessmentFacade(a);
    return f;
  }

  public static void print(AssessmentBaseIfc a) {
    //System.out.println("**assessment base Id #" + a.getAssessmentBaseId());
    //System.out.println("**assessment title #" + a.getTitle());
    //System.out.println("**assessment is template? " + a.getIsTemplate());
    if (a.getIsTemplate().equals(Boolean.FALSE)) {
	//System.out.println("**assessmentTemplateId #" +
        //                 ( (AssessmentData) a).getAssessmentTemplateId());
	//System.out.println("**section: " +
        //                 ( (AssessmentData) a).getSectionSet());
    }
    if (a.getAssessmentAccessControl() != null) {
	//System.out.println("**assessment due date: " +
        //                 a.getAssessmentAccessControl().getDueDate());
    }
    if (a.getAssessmentMetaDataSet() != null) {
	//System.out.println("**assessment metadata" +
        //                 a.getAssessmentMetaDataSet());
	//System.out.println("**Objective not lazy = " +
        //                 a.getAssessmentMetaDataByLabel("ASSESSMENT_OBJECTIVE"));
    }

  }

  public static void printPublished(PublishedAssessmentData a) {
      //System.out.println("**assessment published Id #" +
      //                 a.getPublishedAssessmentId());
      //System.out.println("**assessment title #" + a.getTitle());
    if (a.getAssessmentAccessControl() != null) {
      //System.out.println("**assessment due date: " +
      //                 a.getAssessmentAccessControl().getDueDate());
    }
    if (a.getAssessmentMetaDataSet() != null) {
	//System.out.println("**assessment metadata" +
	//                   a.getAssessmentMetaDataSet());
      //System.out.println("**Objective not lazy = " +
      //                   a.getAssessmentMetaDataByLabel("ASSESSMENT_OBJECTIVE"));
    }

  }

  public Long publishAssessment(AssessmentData assessment) {
    PublishedAssessmentData publishedAssessment = preparePublishedAssessment(
        assessment);
    getHibernateTemplate().save(publishedAssessment);
    // write authorization
    createAuthorization(publishedAssessment);
    return publishedAssessment.getPublishedAssessmentId();
  }

  public PublishedAssessmentFacade publishAssessment(AssessmentFacade
      assessment) {
    PublishedAssessmentData publishedAssessment = preparePublishedAssessment( (
        AssessmentData) assessment.getData());
    getHibernateTemplate().save(publishedAssessment);
    // write authorization
    createAuthorization(publishedAssessment);
    return new PublishedAssessmentFacade(publishedAssessment);
  }

  public void createAuthorization(PublishedAssessmentData p) {
    String qualifierIdString = p.getPublishedAssessmentId().toString();
    Vector v = new Vector();

    //1. get all possible publishing targets (agentKey, agentId)
    PublishingTarget publishingTarget = new PublishingTarget();
    HashMap targets = publishingTarget.getTargets();

    // 2. get the key of the target selected, it is stored in accessControl.releaseTo
    AssessmentAccessControlIfc control = p.getAssessmentAccessControl();
    String releaseTo = control.getReleaseTo();
    if (releaseTo != null) {
      String[] targetSelected = releaseTo.split(",");
      for (int i = 0; i < targetSelected.length; i++) {
        String agentKey = targetSelected[i].trim();
        // add agentId into v
        if (targets.get(agentKey) != null) {
          v.add( (String) targets.get(agentKey));
        }
      }
    }
    // 3. give selected site right to view Published Assessment
    PersistenceService.getInstance().getAuthzQueriesFacade().
          createAuthorization(AgentFacade.getCurrentSiteId(),
                              "VIEW_ASSESSMENT", qualifierIdString);
    // 4. create authorization for all the agentId in v
    for (int i = 0; i < v.size(); i++) {
      String agentId = (String) v.get(i);
      //System.out.println("** agentId=" + agentId);
      PersistenceService.getInstance().getAuthzQueriesFacade().
          createAuthorization(agentId,
                              "TAKE_ASSESSMENT", qualifierIdString);
      PersistenceService.getInstance().getAuthzQueriesFacade().
          createAuthorization(agentId,
                              "VIEW_ASSESSMENT_FEEDBACK", qualifierIdString);
      PersistenceService.getInstance().getAuthzQueriesFacade().
          createAuthorization(agentId,
                              "GRADE_ASSESSMENT", qualifierIdString);
      if (!AgentFacade.getCurrentSiteId().equals(agentId)){
        PersistenceService.getInstance().getAuthzQueriesFacade().
          createAuthorization(agentId,
                              "VIEW_ASSESSMENT", qualifierIdString);
      }
    }
  }

  public AssessmentData loadAssessment(Long assessmentId) {
    return (AssessmentData) getHibernateTemplate().
        load(AssessmentData.class, assessmentId);
  }

  public PublishedAssessmentData loadPublishedAssessment(Long assessmentId) {
    return (PublishedAssessmentData) getHibernateTemplate().
        load(PublishedAssessmentData.class, assessmentId);
  }

  public ArrayList getAllTakeableAssessments(String orderBy, boolean ascending,
                                             Integer status) {

    String query =
        "from PublishedAssessmentData as p where p.status=? order by p." +
        orderBy + " asc";
    if (ascending == false) {
      query = "from PublishedAssessmentData as p where p.status=? order by p." +
          orderBy + " desc";
    }
    //System.out.println("Order by " + orderBy);
    List list = getHibernateTemplate().find(query, new Object[] {status}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.
                                            INTEGER});
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData a = (PublishedAssessmentData) list.get(i);
      //System.out.println("Title: " + a.getTitle());
      // Don't need sections for list of assessments
      //a.setSectionSet(getSectionSetForAssessment(a));
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }

/**
  public ArrayList getAllPublishedAssessmentId() {

    ArrayList list = getBasicInfoOfAllActivePublishedAssessments("title");
    ArrayList publishedIds = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentFacade f = (PublishedAssessmentFacade) list.get(i);
      Long publishedId = f.getPublishedAssessmentId();
      publishedIds.add(publishedId);
    }
    return publishedIds;

  }
*/

  public Integer getNumberOfSubmissions(String publishedAssessmentId,
                                        String agentId) {
    String query = "select count(a) from AssessmentGradingData a where a.publishedAssessment.publishedAssessmentId=? and a.agentId=? and a.forGrade=?";
    Object[] objects = new Object[3];
    objects[0] = new Long(publishedAssessmentId);
    objects[1] = agentId;
    objects[2] = new Boolean(true);
    Type[] types = new Type[3];
    types[0] = Hibernate.LONG;
    types[1] = Hibernate.STRING;
    types[2] = Hibernate.BOOLEAN;
    List list = getHibernateTemplate().find(query, objects, types);
    return (Integer) list.get(0);
  }

  public List getNumberOfSubmissionsOfAllAssessmentsByAgent(String agentId) {
    String query = "select new AssessmentGradingData(" +
        " a.publishedAssessment.publishedAssessmentId, count(a)) " +
        " from AssessmentGradingData as a where a.agentId=? and a.forGrade=?" +
        " group by a.publishedAssessment.publishedAssessmentId";
    Object[] objects = new Object[2];
    objects[0] = agentId;
    objects[1] = new Boolean(true);
    Type[] types = new Type[2];
    types[0] = Hibernate.STRING;
    types[1] = Hibernate.BOOLEAN;
    return getHibernateTemplate().find(query, objects, types);
  }

/**
  public ArrayList getAllReviewableAssessments(String orderBy,
                                               boolean ascending) {

    ArrayList publishedIds = getAllPublishedAssessmentId();
    ArrayList newlist = new ArrayList();
    for (int i = 0; i < publishedIds.size(); i++) {
      String publishedId = ( (Long) publishedIds.get(i)).toString();
      String query = "from AssessmentGradingData a where a.publishedAssessment.publishedAssessmentId=? order by agentId ASC," +
          orderBy;
      if (ascending == false) {
        query = query + " desc,";
      }
      else {
        query = query + " asc,";
      }
      query = query + "submittedDate DESC";
      List list = getHibernateTemplate().find(query, new Long(publishedId),
                                              Hibernate.LONG);
      if (!list.isEmpty()) {
        Iterator items = list.iterator();
        String agentid = null;
        AssessmentGradingData data = (AssessmentGradingData) items.next();
        agentid = data.getAgentId();
        newlist.add(data);
        while (items.hasNext()) {
          while (items.hasNext()) {
            data = (AssessmentGradingData) items.next();
            if (!data.getAgentId().equals(agentid)) {
              agentid = data.getAgentId();
              newlist.add(data);
              //System.out.println("Added new submission " +
              //                   data.getAssessmentGradingId());
              break;
            }
          }
        }
      }
    }
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < newlist.size(); i++) {
      AssessmentGradingData a = (AssessmentGradingData) newlist.get(i);
      AssessmentGradingFacade f = new AssessmentGradingFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }
*/
  public ArrayList getAllPublishedAssessments(String sortString) {
    String orderBy = getOrderBy(sortString);
    List list = getHibernateTemplate().find(
        "from PublishedAssessmentData p order by p." + orderBy);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData a = (PublishedAssessmentData) list.get(i);
      a.setSectionSet(getSectionSetForAssessment(a));
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public ArrayList getAllPublishedAssessments(String sortString, Integer status) {
    String orderBy = getOrderBy(sortString);
    List list = getHibernateTemplate().find(
        "from PublishedAssessmentData as p where p.status=? order by p." +
        orderBy,
        new Object[] {status}
        , new net.sf.hibernate.type.Type[] {Hibernate.INTEGER});
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData a = (PublishedAssessmentData) list.get(i);
      a.setSectionSet(getSectionSetForAssessment(a));
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public ArrayList getAllPublishedAssessments(
      int pageSize, int pageNumber, String sortString, Integer status) {
    String orderBy = getOrderBy(sortString);
    String queryString = "from PublishedAssessmentData p order by p." + orderBy;
    if (!status.equals(PublishedAssessmentFacade.ANY_STATUS)) {
      queryString = "from PublishedAssessmentData p where p.status =" +
          status.intValue() + " order by p." + orderBy;
    }
    PagingUtilQueries pagingUtilQueries = PersistenceService.getInstance().
        getPagingUtilQueries();
    List pageList = pagingUtilQueries.getAll(pageSize, pageNumber, queryString);
    //System.out.println("**** pageList=" + pageList);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < pageList.size(); i++) {
      PublishedAssessmentData a = (PublishedAssessmentData) pageList.get(i);
      a.setSectionSet(getSectionSetForAssessment(a));
      //System.out.println("****  published assessment=" + a.getTitle());
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(a);
      //System.out.println("**** published assessment title=" + f.getTitle());
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public void removeAssessment(Long assessmentId) {
    PublishedAssessmentData assessment = (PublishedAssessmentData)
        getHibernateTemplate().load(PublishedAssessmentData.class, assessmentId);
    // if AssessmentGradingData exist, simply set pub assessment to inactive
    // else delete assessment
    List count = getHibernateTemplate().find(
        "select count(g) from AssessmentGradingData g where g.publishedAssessment=?",
        assessment);
    //System.out.println("no. of Assessment Grading =" + count.size());
    Iterator iter = count.iterator();
    int i = ( (Integer) iter.next()).intValue();
    if (i > 0) {
      assessment.setStatus(PublishedAssessmentIfc.DEAD_STATUS);
      getHibernateTemplate().update(assessment);
    }
    else {
      getHibernateTemplate().delete(assessment);
    }
  }

  private String getOrderBy(String sortString) {
    String startDate = (PublishedAssessmentFacadeQueries.STARTDATE).substring(
        (PublishedAssessmentFacadeQueries.STARTDATE).lastIndexOf(".") + 1);
    String dueDate = (PublishedAssessmentFacadeQueries.DUEDATE).substring(
        (PublishedAssessmentFacadeQueries.DUEDATE).lastIndexOf(".") + 1);
    String releaseTo = (PublishedAssessmentFacadeQueries.RELEASETO).substring(
        (PublishedAssessmentFacadeQueries.RELEASETO).lastIndexOf(".") + 1);

    if ( (sortString).equals(startDate)) {
      return PublishedAssessmentFacadeQueries.STARTDATE;
    }
    else if ( (sortString).equals(dueDate)) {
      return PublishedAssessmentFacadeQueries.DUEDATE;
    }
    else if ( (sortString).equals(releaseTo)) {
      return PublishedAssessmentFacadeQueries.RELEASETO;
    }
    else {
      return PublishedAssessmentFacadeQueries.TITLE;
    }
  }

  public void saveOrUpdate(PublishedAssessmentFacade assessment) {
    PublishedAssessmentData data = (PublishedAssessmentData) assessment.getData();
    getHibernateTemplate().saveOrUpdate(data);
  }

  public ArrayList getBasicInfoOfAllActivePublishedAssessments(String
      sortString, String siteAgentId) {
    Date currentDate = new Date();
    String orderBy = getOrderBy(sortString);
    String query =
        "select new PublishedAssessmentData(p.publishedAssessmentId, p.title, "+
        " c.releaseTo, c.startDate, c.dueDate, c.retractDate) " +
        " from PublishedAssessmentData p, PublishedAccessControl c, AuthorizationData z  " +
        " where c.assessment = p and p.status=1 and " +
        " p.publishedAssessmentId=z.qualifierId and z.functionId='VIEW_ASSESSMENT' " +
        " and z.agentIdString= ? order by p." + orderBy;
    List l = getHibernateTemplate().find(query,
        new Object[] {siteAgentId},
        new net.sf.hibernate.type.Type[] {Hibernate.STRING});

    // we will filter the one that is past duedate & retract date
    ArrayList list = new ArrayList();
    for (int j = 0; j < l.size(); j++) {
      PublishedAssessmentData p = (PublishedAssessmentData) l.get(j);
      if ( (p.getDueDate() == null || (p.getDueDate()).after(currentDate))
          &&
          (p.getRetractDate() == null || (p.getRetractDate()).after(currentDate))) {
        list.add(p);
      }
    }

    ArrayList pubList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData p = (PublishedAssessmentData) list.get(i);
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(
          p.getPublishedAssessmentId(), p.getTitle(),
          p.getReleaseTo(), p.getStartDate(), p.getDueDate());
      pubList.add(f);
    }
    return pubList;
  }

  /**
   * According to Marc inactive means either the dueDate or the retractDate has
   * passed for 1.5 release (IM on 12/17/04)
   * @param sortString
   * @return
   */
  public ArrayList getBasicInfoOfAllInActivePublishedAssessments(String
      sortString, String siteAgentId) {
    String orderBy = getOrderBy(sortString);
    Date currentDate = new Date();
    long currentTime = currentDate.getTime();
    String query = "select new PublishedAssessmentData(p.publishedAssessmentId, p.title,"+
                   " c.releaseTo, c.startDate, c.dueDate, c.retractDate) from PublishedAssessmentData p,"+
                   " PublishedAccessControl c, AuthorizationData z  " +
                   " where c.assessment=p and (p.status=0 or c.dueDate< ? or  c.retractDate< ?)" +
                   " and p.publishedAssessmentId=z.qualifierId and z.functionId='VIEW_ASSESSMENT' " +
                   " and z.agentIdString= ? order by p." + orderBy;
    List list = getHibernateTemplate().find(query,
        new Object[] {new Date(), new Date(),siteAgentId} ,
        new net.sf.hibernate.type.Type[] {Hibernate.DATE, Hibernate.DATE, Hibernate.STRING});

    ArrayList pubList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData p = (PublishedAssessmentData) list.get(i);
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(
          p.getPublishedAssessmentId(), p.getTitle(),
          p.getReleaseTo(), p.getStartDate(), p.getDueDate());
      pubList.add(f);
    }
    return pubList;
  }

  /** return a set of PublishedSectionData
   * IMPORTANT:
   * 1. we have declared SectionData as lazy loading, so we need to
   * initialize it using getHibernateTemplate().initialize(java.lang.Object).
   * Unfortunately,  we are using Spring 1.0.2 which does not support this
   * Hibernate feature. I tried upgrading Spring to 1.1.3. Then it failed
   * to load all the OR maps correctly. So for now, I am just going to
   * initialize it myself. I will take a look at it again next year.
   * - daisyf (12/13/04)
   */
  public HashSet getSectionSetForAssessment(PublishedAssessmentData assessment) {
    List sectionList = getHibernateTemplate().find(
        "from PublishedSectionData s where s.assessment.publishedAssessmentId=" +
        assessment.getPublishedAssessmentId());
    HashSet set = new HashSet();
    for (int j = 0; j < sectionList.size(); j++) {
      set.add( (PublishedSectionData) sectionList.get(j));
    }
    return set;
  }

  // IMPORTANT:
  // 1. we do not want any Section info, so set loadSection to false
  // 2. We have also declared SectionData as lazy loading. If loadSection is set
  // to true, we will see null pointer
  public PublishedAssessmentFacade getSettingsOfPublishedAssessment(Long
      assessmentId) {
    PublishedAssessmentData a = loadPublishedAssessment(assessmentId);
    Boolean loadSection = Boolean.FALSE;
    PublishedAssessmentFacade f = new PublishedAssessmentFacade(a, loadSection);
    return f;
  }

  public PublishedItemData loadPublishedItem(Long itemId) {
    return (PublishedItemData) getHibernateTemplate().
        load(PublishedItemData.class, itemId);
  }

  public PublishedItemText loadPublishedItemText(Long itemTextId) {
    return (PublishedItemText) getHibernateTemplate().
        load(PublishedItemText.class, itemTextId);
  }

  // added by daisy - please check the logic - I based this on the getBasicInfoOfAllActiveAssessment
  public ArrayList getBasicInfoOfAllPublishedAssessments(String orderBy,
      boolean ascending, Integer status) {

    String query =
        "select new PublishedAssessmentData(p.publishedAssessmentId, p.title, " +
        " c.releaseTo, c.startDate, c.dueDate, c.retractDate, " +
        " c.feedbackDate, f.feedbackDelivery, c.lateHandling, " +
        " c.unlimitedSubmissions, c.submissionsAllowed) " +
        " from PublishedAssessmentData as p, PublishedAccessControl as c," +
        " PublishedFeedback as f" +
        " where c.assessment.publishedAssessmentId=p.publishedAssessmentId " +
        " and p.publishedAssessmentId = f.assessment.publishedAssessmentId " +
        " and p.status=? order by ";

    if (ascending == false) {

      if (orderBy.equals(DUE)) {
        query = query + " c." + orderBy + " desc";
      }
      else {
        query = query + " p." + orderBy + " desc";
      }
    }
    else {
      if (orderBy.equals(DUE)) {
        query = query + " c." + orderBy + " asc";
      }
      else {
        query = query + " p." + orderBy + " asc";
      }
    }

    List list = getHibernateTemplate().find(query, new Object[] {status}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.INTEGER});

    ArrayList pubList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      PublishedAssessmentData p = (PublishedAssessmentData) list.get(i);
      PublishedAssessmentFacade f = new PublishedAssessmentFacade(
          p.getPublishedAssessmentId(), p.getTitle(),
          p.getReleaseTo(), p.getStartDate(), p.getDueDate(),
          p.getRetractDate(), p.getFeedbackDate(), p.getFeedbackDelivery(),
          p.getLateHandling(),
          p.getUnlimitedSubmissions(), p.getSubmissionsAllowed());
      pubList.add(f);
    }
    return pubList;
  }

  /**
       * return an array list of the last AssessmentGradingFacade per assessment that
   * a user has submitted for grade.
   * @param agentId
   * @param orderBy
   * @param ascending
   * @return
   */
  public ArrayList getBasicInfoOfLastSubmittedAssessments(String agentId,
      String orderBy, boolean ascending) {
    // 1. get total no. of submission per assessment by the given agent
    HashMap h = getTotalSubmissionPerAssessment(agentId);
    String query = "select new AssessmentGradingData(" +
        " a.assessmentGradingId, p.publishedAssessmentId, p.title, a.agentId," +
        " a.submittedDate, a.isLate," +
        " a.forGrade, a.totalAutoScore, a.totalOverrideScore,a.finalScore," +
        " a.comments, a.status, a.gradedBy, a.gradedDate, a.attemptDate," +
        " a.timeElapsed) " +
        " from AssessmentGradingData a, PublishedAssessmentData p" +
        " where a.publishedAssessment = p  and a.forGrade=1 and a.agentId=?" +
        " order by p.publishedAssessmentId DESC, a.submittedDate DESC";

    /* The sorting for each type will be done in the action listener.
         if (orderBy.equals(TITLE))
         {
      query = query + ", p." + orderBy;
         }
         else if (!orderBy.equals(SUBMITTED))
         {
     query = query + ", a." + orderBy;
         }
         if (!orderBy.equals(SUBMITTED))
         {
        if (ascending == false)
      {
       query = query + " desc";
      }
         else
      {
         query = query + " asc";
      }
         }
     */

    ArrayList list = (ArrayList) getHibernateTemplate().find(query,
        new Object[] {agentId}
        ,
        new net.sf.hibernate.type.Type[] {Hibernate.STRING});

    ArrayList assessmentList = new ArrayList();
    Long current = new Long("0");
    Date currentDate = new Date();
    for (int i = 0; i < list.size(); i++) {
      AssessmentGradingData a = (AssessmentGradingData) list.get(i);
      // criteria: only want the most recently submitted assessment from a given user.
      if (!a.getPublishedAssessmentId().equals(current)) {
        current = a.getPublishedAssessmentId();
        AssessmentGradingFacade f = new AssessmentGradingFacade(a);
        assessmentList.add(f);
      }
    }
    return assessmentList;
  }

  /** total submitted for grade
       * returns HashMap (Long publishedAssessmentId, Integer totalSubmittedForGrade);
   */
  public HashMap getTotalSubmissionPerAssessment(String agentId) {
    List l = getNumberOfSubmissionsOfAllAssessmentsByAgent(agentId);
    HashMap h = new HashMap();
    for (int i = 0; i < l.size(); i++) {
      AssessmentGradingData d = (AssessmentGradingData) l.get(i);
      h.put(d.getPublishedAssessmentId(), new Integer(d.getTotalSubmitted()));
      //System.out.println("pId=" + d.getPublishedAssessmentId() + " submitted=" +
      //                   d.getTotalSubmitted());
    }
    return h;
  }

  public Integer getTotalSubmission(String agentId, Long publishedAssessmentId) {
    String query =
        "select count(a) from AssessmentGradingData a where a.forGrade=1 " +
        " and a.agentId=? and a.publishedAssessment.publishedAssessmentId=?";
    List l = getHibernateTemplate().find(query,
                                         new Object[] {agentId,
                                         publishedAssessmentId}
                                         ,
                                         new net.sf.hibernate.type.Type[] {
                                         Hibernate.STRING, Hibernate.LONG});
    return (Integer) l.get(0);
  }

  public PublishedAssessmentFacade getPublishedAssessmentIdByAlias(String alias) {
    return getPublishedAssessmentIdByMetaLabel("ALIAS", alias);
  }

  public PublishedAssessmentFacade getPublishedAssessmentIdByMetaLabel(
      String label, String entry) {
    /**
         String query = "select new PublishedAssessmentData("+
      " p.publishedAssessmentId, p.title, "+
      " c.releaseTo, c.startDate, c.dueDate, c.retractDate) " +
      " from PublishedAssessmentData p, PublishedAccessControl c, " +
      " PublishedMetaData m where p=c.assessment and p=m.assessment "+
      " and m.label=? and m.entry=?";
     */
    String query = "select p " +
        " from PublishedAssessmentData p, " +
        " PublishedMetaData m where p=m.assessment " +
        " and m.label=? and m.entry=?";
    List l = getHibernateTemplate().find(query,
                                         new Object[] {label, entry}
                                         ,
                                         new net.sf.hibernate.type.Type[] {
                                         Hibernate.STRING, Hibernate.STRING});
    if (l.size() > 0) {
      return new PublishedAssessmentFacade( (PublishedAssessmentData) l.get(0));
    }
    else {
      return null;
    }
  }

  public void saveOrUpdateMetaData(PublishedMetaData meta) {
    getHibernateTemplate().saveOrUpdate(meta);
  }

  public HashMap getFeedbackHash() {
    HashMap h = new HashMap();
    String query = "select new PublishedFeedback(" +
        " p.assessment.publishedAssessmentId," +
        " p.feedbackDelivery, p.editComponents, p.showQuestionText," +
        " p.showStudentResponse, p.showCorrectResponse," +
        " p.showStudentScore," +
        " p.showQuestionLevelFeedback, p.showSelectionLevelFeedback," +
        " p.showGraderComments, p.showStatistics)" +
        " from PublishedFeedback p";
    List l = getHibernateTemplate().find(query);
    for (int i = 0; i < l.size(); i++) {
      PublishedFeedback f = (PublishedFeedback) l.get(i);
      h.put(f.getAssessmentId(), f);
    }
    return h;
  }


  /** this return a HashMap containing 
   *  (Long publishedAssessmentId, PublishedAssessmentFacade publishedAssessment)
   *  Note that the publishedAssessment is a partial object used for display only.
   *  do not use it for persisting. It only contains title, releaseTo, startDate, dueDate 
   *  & retractDate
   */
  public HashMap getAllAssessmentsReleasedToAuthenticatedUsers(){
    HashMap h = new HashMap();
    String query =
        "select new PublishedAssessmentData(p.publishedAssessmentId, p.title, "+
        " c.releaseTo, c.startDate, c.dueDate, c.retractDate) " +
        " from PublishedAssessmentData p, PublishedAccessControl c  " +
        " where c.assessment = p and c.releaseTo like '%Authenticated Users%'";
    List l = getHibernateTemplate().find(query);
    for (int i = 0; i < l.size(); i++) {
      PublishedAssessmentData p = (PublishedAssessmentData) l.get(i);
      h.put(p.getPublishedAssessmentId(), new PublishedAssessmentFacade(p));
    }
    return h;
  }

}