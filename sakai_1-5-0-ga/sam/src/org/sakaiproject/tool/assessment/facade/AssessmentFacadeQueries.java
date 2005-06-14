/**
 * @author
 * @version
 */

package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.EvaluationModel;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentTemplateData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.SecuredIPAddress;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentTemplateIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.osid.assessment.AssessmentException;

import org.sakaiproject.tool.assessment.util.HibernateUtil;
import org.sakaiproject.tool.assessment.util.PagingUtilQueries;
import org.navigoproject.osid.impl.PersistenceService;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;

import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import java.io.*;

public class AssessmentFacadeQueries
    extends HibernateDaoSupport {
  private static Logger LOG =
      Logger.getLogger(AssessmentFacadeQueries.class.getName());
  public static String LASTMODIFIEDDATE = "lastModifiedDate";
  public static String TITLE = "title";

  public AssessmentFacadeQueries() {
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

  public static void main(String[] args) throws DataFacadeException {
    AssessmentFacadeQueries instance = new AssessmentFacadeQueries();
    // add an assessmentTemplate
    if (args[0].equals("addTemplate")) {
      Long assessmentTemplateId = instance.addTemplate();
      AssessmentTemplateData a = instance.loadTemplate(assessmentTemplateId);
      System.out.println("**print AssessmentTemplate");
      print(a);
      System.out.println("**print AssessmentTemplateFacade");
      AssessmentTemplateFacade af = new AssessmentTemplateFacade(a);
      printFacade(af);
    }
    if (args[0].equals("removeT")) {
      instance.removeTemplate(new Long(args[1]));
      System.out.println("**deletedId #");
    }
    if (args[0].equals("addA")) {
      Long assessmentId = instance.addAssessment(new Long(args[1]));
      AssessmentData a = instance.loadAssessment(assessmentId);
      System.out.println("**print Assessment");
      print(a);
    }
    if (args[0].equals("loadT")) {
      AssessmentTemplateData a = (AssessmentTemplateData) instance.load(new
          Long(args[1]));
      System.out.println("**print Assessment Template");
      print(a);
    }
    if (args[0].equals("loadA")) {
      AssessmentData a = (AssessmentData) instance.load(new Long(args[1]));
      System.out.println("**print Assessment");
      print(a);
    }
    System.exit(0);
  }

  public static void print(AssessmentBaseData a) {
    System.out.println("**assessmentId #" + a.getAssessmentBaseId());
    System.out.println("**assessment is template? " + a.getIsTemplate());
    if (a.getIsTemplate().equals(Boolean.FALSE)) {
      System.out.println("**assessmentTemplateId #" +
                         ( (AssessmentData) a).getAssessmentTemplateId());
    }
    System.out.println("**assessment due date: " +
                       a.getAssessmentAccessControl().getDueDate());
    System.out.println("**assessment control #" +
                       a.getAssessmentAccessControl());
    System.out.println("**assessment metadata" +
                       a.getAssessmentMetaDataSet());
    System.out.println("**Objective not lazy = " +
                       a.getAssessmentMetaDataByLabel("ASSESSMENT_OBJECTIVES"));

  }

  public static void printFacade(AssessmentTemplateFacade a) {
    System.out.println("**assessmentId #" + a.getAssessmentTemplateId());
    System.out.println("**assessment due date: " +
                       a.getAssessmentAccessControl().getDueDate());
    System.out.println("**assessment control #" +
                       a.getAssessmentAccessControl());
    System.out.println("**assessment metadata" +
                       a.getAssessmentMetaDataSet());
    System.out.println("**Objective not lazy = " +
                       a.getAssessmentMetaDataByLabel("ASSESSMENT_OBJECTIVE"));

  }

  public Long addTemplate() {
    AssessmentTemplateData assessmentTemplate = new AssessmentTemplateData(
        new Long(0),
        "title", "description", "comments",
        TypeD.HOMEWORK,
        new Integer(1), new Integer(1),
        new Integer(1), new Integer(1), "1",
        new Date(), "1",
        new Date()
        );
    AssessmentAccessControl s = new AssessmentAccessControl(
        new Integer(0), new Integer(0),
        new Integer(0), new Integer(0),
        new Integer(0), new Integer(0),
        new Integer(0), new Integer(0),
        new Date(), new Date(),
        new Date(), new Date(), new Date(),
        new Integer(1), new Integer(1),
        new Integer(1), "Thanks for submitting",
        "anonymous");
    s.setAssessmentBase(assessmentTemplate);
    assessmentTemplate.setAssessmentAccessControl( (AssessmentAccessControlIfc)
                                                  s);
    assessmentTemplate.addAssessmentMetaData("ASSESSMENTTEMPLATE_OBJECTIVES",
        " assesmentT: the objective is to ...");
    // take default submission model
    getHibernateTemplate().save(assessmentTemplate);
    return assessmentTemplate.getAssessmentTemplateId();
  }

  public void removeTemplate(Long assessmentId) {
    AssessmentTemplateData assessment = (AssessmentTemplateData)
        getHibernateTemplate().load(AssessmentTemplateData.class, assessmentId);
    getHibernateTemplate().delete(assessment);
  }

  public Long addAssessment(Long assessmentTemplateId) {
    AssessmentData assessment = new AssessmentData(
        new Long(0),
        "assessment title", "assessment description", "assessment acomments",
        assessmentTemplateId,
        TypeD.HOMEWORK,
        new Integer(1), new Integer(1),
        new Integer(1), new Integer(1), "1",
        new Date(), "1",
        new Date()
        );
    AssessmentAccessControl s = new AssessmentAccessControl(
        new Integer(1), new Integer(1),
        new Integer(1), new Integer(1),
        new Integer(1), new Integer(1),
        new Integer(1), new Integer(1),
        new Date(), new Date(),
        new Date(), new Date(), new Date(),
        new Integer(1), new Integer(1),
        new Integer(1), "Thanks for submitting",
        "anonymous");

    s.setAssessmentBase(assessment);
    assessment.setAssessmentAccessControl( (AssessmentAccessControlIfc) s);
    assessment.addAssessmentMetaData("ASSESSMENT_OBJECTIVES",
                                     " assesment: the objective is to ...");
    getHibernateTemplate().save(assessment);
    return assessment.getAssessmentId();
  }

  public AssessmentBaseData load(Long id) {
    AssessmentBaseData a = (AssessmentBaseData) getHibernateTemplate().load(
        AssessmentBaseData.class, id);
    if (a.getIsTemplate().equals(Boolean.TRUE)) {
      return (AssessmentTemplateData) a;
    }
    else {
      return (AssessmentData) a;
    }
  }

  public AssessmentTemplateData loadTemplate(Long assessmentTemplateId) {
    return (AssessmentTemplateData) getHibernateTemplate().load(
        AssessmentTemplateData.class, assessmentTemplateId);
  }

  public AssessmentData loadAssessment(Long assessmentId) {
    return (AssessmentData) getHibernateTemplate().load(AssessmentData.class,
        assessmentId);
  }

  /* The following methods are real
   *
   */
  public AssessmentTemplateFacade getAssessmentTemplate(Long
      assessmentTemplateId) {
    AssessmentTemplateData template = (AssessmentTemplateData)
        getHibernateTemplate().load(
        AssessmentTemplateData.class, assessmentTemplateId);
    return new AssessmentTemplateFacade(template);
  }

  public ArrayList getAllAssessmentTemplates() {
    List list = getHibernateTemplate().find(
        "from AssessmentTemplateData a order by a.title");
    ArrayList templateList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentTemplateData a = (AssessmentTemplateData) list.get(i);
      AssessmentTemplateFacade f = new AssessmentTemplateFacade(a);
      templateList.add(f);
    }
    return templateList;
  }

  public ArrayList getAllActiveAssessmentTemplates() {
    List list = getHibernateTemplate().find(
        "from AssessmentTemplateData a where a.status=1 order by a.title");
    ArrayList templateList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentTemplateData a = (AssessmentTemplateData) list.get(i);
      AssessmentTemplateFacade f = new AssessmentTemplateFacade(a);
      templateList.add(f);
    }
    return templateList;
  }

  /**
   *
       * @return a list of AssessmentTemplateFacade. However, it is IMPORTANT to note
       * that it is not a full object, it contains merely assessmentBaseId (which is
       * the templateId) & title. This methods is used when a list of template titles
   * is required for displaying purposes.
   */
  public ArrayList getTitleOfAllActiveAssessmentTemplates() {
    List list = getHibernateTemplate().find(
        "select new AssessmentTemplateData(a.assessmentBaseId, a.title) from AssessmentTemplateData a where a.status=1 order by a.title");
    ArrayList templateList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentTemplateData a = (AssessmentTemplateData) list.get(i);
      a.setAssessmentTemplateId(a.getAssessmentBaseId());
      AssessmentTemplateFacade f = new AssessmentTemplateFacade(a.
          getAssessmentBaseId(), a.getTitle());
      templateList.add(f);
    }
    return templateList;
  }

  public AssessmentFacade getAssessment(Long assessmentId) {
    AssessmentData assessment = (AssessmentData) getHibernateTemplate().load(
        AssessmentData.class, assessmentId);
    assessment.setSectionSet(getSectionSetForAssessment(assessment));
    return new AssessmentFacade(assessment);
  }

  /**
   * IMPORTANT:
   * 1. we have declared SectionData as lazy loading, so we need to
   * initialize it using getHibernateTemplate().initialize(java.lang.Object).
   * Unfortunately,  we are using Spring 1.0.2 which does not support this
   * Hibernate feature. I tried upgrading Spring to 1.1.3. Then it failed
   * to load all the OR maps correctly. So for now, I am just going to
   * initialize it myself. I will take a look at it again next year.
   * - daisyf (12/13/04)
   */
  private HashSet getSectionSetForAssessment(AssessmentData assessment) {
    List sectionList = getHibernateTemplate().find(
        "from SectionData s where s.assessment.assessmentBaseId=" +
        assessment.getAssessmentBaseId());
    HashSet set = new HashSet();
    for (int j = 0; j < sectionList.size(); j++) {
      set.add( (SectionData) sectionList.get(j));
    }
    return set;
  }

  public void removeAssessment(Long assessmentId) {
    AssessmentData assessment = (AssessmentData) getHibernateTemplate().load(
        AssessmentData.class, assessmentId);
    // if pubAssessment exist, simply set assessment to inactive
    // else delete assessment
    List count = getHibernateTemplate().find(
        "select count(p) from PublishedAssessmentData p where p.assessment=?",
        assessment);
    System.out.println("no. of pub Assessment =" + count.size());
    Iterator iter = count.iterator();
    int i = ( (Integer) iter.next()).intValue();
    if (i > 0) {
      assessment.setStatus(AssessmentIfc.DEAD_STATUS);
      getHibernateTemplate().update(assessment);
    }
    else {
      // need to check if item in sections belongs to any QuestionPool
      QuestionPoolService qpService = new QuestionPoolService();
      HashMap h = qpService.getQuestionPoolItemMap();
      checkForQuestionPoolItem(assessment, h);
      getHibernateTemplate().delete(assessment);
    }
  }

  public AssessmentData cloneAssessmentFromTemplate(AssessmentTemplateData t) {
    System.out.println("**** DEFAULT templateId inside clone" +
                       t.getAssessmentTemplateId());
    AssessmentData assessment = new AssessmentData(
        t.getParentId(),
        "Assessment created with" + t.getTitle(),
        t.getDescription(),
        t.getComments(),
        t.getAssessmentTemplateId(),
        TypeD.HOMEWORK, // by default for now
        t.getInstructorNotification(),
        t.getTesteeNotification(),
        t.getMultipartAllowed(),
        t.getStatus(),
        AgentFacade.getAgentString(), new Date(),
        AgentFacade.getAgentString(), new Date());
    try {
      // deal with Access Control
      AssessmentAccessControl controlOrig =
          (AssessmentAccessControl) t.getAssessmentAccessControl();
      if (controlOrig != null) {
        AssessmentAccessControl control = (AssessmentAccessControl) controlOrig.
            clone();
        control.setAssessmentBase(assessment);
        assessment.setAssessmentAccessControl(control);
      }
      //deal with feedback
      AssessmentFeedback feedbackOrig = (AssessmentFeedback) t.
          getAssessmentFeedback();
      if (feedbackOrig != null) {
        AssessmentFeedback feedback = (AssessmentFeedback) feedbackOrig.clone();
        feedback.setAssessmentBase(assessment);
        assessment.setAssessmentFeedback(feedback);
      }
      //deal with evaluation
      EvaluationModel evalOrig = (EvaluationModel) t.getEvaluationModel();
      if (evalOrig != null) {
        EvaluationModel eval = (EvaluationModel) evalOrig.clone();
        eval.setAssessmentBase(assessment);
        assessment.setEvaluationModel(eval);
      }
      // deal with MetaData
      HashSet h = new HashSet();
      Set s = t.getAssessmentMetaDataSet();
      Iterator iter = s.iterator();
      while (iter.hasNext()) {
        AssessmentMetaData mOrig = (AssessmentMetaData) iter.next();
        if (mOrig.getLabel() != null) {
          AssessmentMetaData m = new AssessmentMetaData(
              assessment, mOrig.getLabel(), mOrig.getEntry());
          h.add(m);
        }
      }
      assessment.setAssessmentMetaDataSet(h);
      // we need to add the FIRST section to an assessment
      // it is a requirement that each assesment must have at least one section
      HashSet sh = new HashSet();
      SectionData section = new SectionData(
          null, new Integer("1"), // FIRST section
          "Section 1", "this section is added when a new assessment is created",
          TypeD.DEFAULT_SECTION, SectionData.ACTIVE_STATUS,
          AgentFacade.getAgentString(), new Date(),
          AgentFacade.getAgentString(), new Date());
      section.setAssessment(assessment);
      sh.add(section);
      assessment.setSectionSet(sh);
    }
    catch (CloneNotSupportedException ex) {
      ex.printStackTrace();
    }
    return assessment;
  }

  /** This method is the same as createAssessment() except that no default
   *  section will be created with the assessment.
   */
  public AssessmentFacade createAssessmentWithoutDefaultSection(
      String title, String description, Long typeId, Long templateId) {
    // #1 - get the template (a facade) and create Assessment based on it
    AssessmentTemplateFacade template = getAssessmentTemplate(templateId);
    AssessmentData assessment = cloneAssessmentFromTemplate( (
        AssessmentTemplateData) template.getData());

    // remove the default section added by cloneAssessmentFromTemplate
    assessment.setSectionSet(new HashSet());

    assessment.setTitle(title);
    assessment.setDescription(description);
    assessment.setTypeId(typeId);
    AssessmentAccessControl control = (AssessmentAccessControl) assessment.
        getAssessmentAccessControl();
    if (control == null) {
      control = new AssessmentAccessControl();
    }
    control.setReleaseTo("Authenticated Users");
    getHibernateTemplate().save(assessment);
    System.out.println("** DEFUALT createAssessment templateId=" +
                       assessment.getAssessmentTemplateId());
    System.out.println("** DEFUALT createAssessment templateId=" +
                       assessment.getAssessmentAccessControl().
                       getSubmissionsAllowed());
    System.out.println("** Assessment Section size =" +
                       assessment.getSectionSet().size());
    return new AssessmentFacade(assessment);
  }

  public AssessmentFacade createAssessment(
      String title, String description, Long typeId, Long templateId) {
    // #1 - get the template (a facade) and create Assessment based on it
    AssessmentTemplateFacade template = getAssessmentTemplate(templateId);
    AssessmentData assessment = cloneAssessmentFromTemplate( (
        AssessmentTemplateData) template.getData());
    assessment.setTitle(title);
    assessment.setDescription(description);
    assessment.setTypeId(typeId);
    AssessmentAccessControl control = (AssessmentAccessControl) assessment.
        getAssessmentAccessControl();
    if (control == null) {
      control = new AssessmentAccessControl();
    }
    control.setReleaseTo("Authenticated Users");
    getHibernateTemplate().save(assessment);
    System.out.println("** DEFUALT createAssessment templateId=" +
                       assessment.getAssessmentTemplateId());
    System.out.println("** DEFUALT createAssessment templateId=" +
                       assessment.getAssessmentAccessControl().
                       getSubmissionsAllowed());
    System.out.println("** Assessment Section size =" +
                       assessment.getSectionSet().size());
    return new AssessmentFacade(assessment);
  }

  public ArrayList getAllAssessments(String orderBy) {
    List list = getHibernateTemplate().find(
        "from AssessmentData a order by a." + orderBy);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentData a = (AssessmentData) list.get(i);
      AssessmentFacade f = new AssessmentFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public ArrayList getAllActiveAssessments(String orderBy) {
    List list = getHibernateTemplate().find(
        "from AssessmentData a where a.status=1 order by a." + orderBy);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentData a = (AssessmentData) list.get(i);
      a.setSectionSet(getSectionSetForAssessment(a));
      AssessmentFacade f = new AssessmentFacade(a);
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public ArrayList getBasicInfoOfAllActiveAssessments(String orderBy) {
    List list = getHibernateTemplate().find(
        "select new AssessmentData(a.assessmentBaseId, a.title, a.lastModifiedDate)from AssessmentData a where a.status=1 order by a." +
        orderBy);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentData a = (AssessmentData) list.get(i);
      AssessmentFacade f = new AssessmentFacade(a.getAssessmentBaseId(),
                                                a.getTitle(),
                                                a.getLastModifiedDate());
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public AssessmentFacade getBasicInfoOfAnAssessment(Long assessmentId) {
    AssessmentData a = (AssessmentData) getHibernateTemplate().load(
        AssessmentData.class, assessmentId);
    return new AssessmentFacade(a.getAssessmentBaseId(), a.getTitle(),
                                a.getLastModifiedDate());
  }

  public ArrayList getSettingsOfAllActiveAssessments(String orderBy) {
    List list = getHibernateTemplate().find(
        "from AssessmentData a where a.status=1 order by a." + orderBy);
    ArrayList assessmentList = new ArrayList();
    // IMPORTANT:
    // 1. we do not want any Section info, so set loadSection to false
    // 2. We have also declared SectionData as lazy loading. If loadSection is set
    // to true, we will see null pointer
    Boolean loadSection = Boolean.FALSE;
    for (int i = 0; i < list.size(); i++) {
      AssessmentData a = (AssessmentData) list.get(i);
      AssessmentFacade f = new AssessmentFacade(a, loadSection);
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public ArrayList getAllAssessments(
      int pageSize, int pageNumber, String orderBy) {
    String queryString = "from AssessmentData a order by a." + orderBy;
    PagingUtilQueries pagingUtilQueries = PersistenceService.getInstance().
        getPagingUtilQueries();
    List pageList = pagingUtilQueries.getAll(pageSize, pageNumber, queryString);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < pageList.size(); i++) {
      AssessmentData a = (AssessmentData) pageList.get(i);
      AssessmentFacade f = new AssessmentFacade(a);
      System.out.println("**** assessment facade Id=" + f.getAssessmentId());
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public int getQuestionSize(final Long assessmentId) {
    List size = getHibernateTemplate().find(
        "select count(i) from ItemData i, SectionData s,  AssessmentData a where a = s.assessment and s = i.section and a.assessmentBaseId=?",
        new Object[] {assessmentId}
        , new net.sf.hibernate.type.Type[] {Hibernate.LONG});
    Iterator iter = size.iterator();
    if (iter.hasNext()) {
      int i = ( (Integer) iter.next()).intValue();
      System.out.println("** Question size = " + i);
      return i;
    }
    else {
      System.out.println("** no questions");
      return 0;
    }
  }

  public void saveOrUpdate(AssessmentFacade assessment) {
    // delete old IP before save
    List ip = getHibernateTemplate().find(
        "from SecuredIPAddress s where s.assessment.assessmentBaseId = ?",
        new Object[] {assessment.getAssessmentId()}
        , new net.sf.hibernate.type.Type[] {Hibernate.LONG});
    if (ip != null) {
      Iterator iter = ip.iterator();
      while (iter.hasNext()) {
        System.out.println("Deleting ip");
        getHibernateTemplate().delete( (SecuredIPAddress) iter.next());
      }
    }
    AssessmentData data = (AssessmentData) assessment.getData();
    getHibernateTemplate().saveOrUpdate(data);
  }

  public void saveOrUpdate(AssessmentTemplateData template) {
//    if (template.getAssessmentTemplateId().equals(new Long(0)))
//    {
    System.out.println("Rachel: Saving template " +
                       template.getAssessmentTemplateId().longValue());
    List metadatas = getHibernateTemplate().find(
        "from AssessmentMetaData a where a.assessment.assessmentBaseId = ?",
        new Object[] {template.getAssessmentTemplateId()}
        , new net.sf.hibernate.type.Type[] {Hibernate.LONG});
    System.out.println("Rachel: metadata size = " + metadatas.size());
    Iterator iter = metadatas.iterator();
    while (iter.hasNext()) {
      System.out.println("Deleting metadata");
      getHibernateTemplate().delete( (AssessmentMetaData) iter.next());
    }
    getHibernateTemplate().saveOrUpdate(template);
//    }
//    else
//    { // This is temporary until Daisy figures this out.
//      System.out.println("Rachel: Updating template");
//      getHibernateTemplate().delete(template);
//      getHibernateTemplate().save(template);
//    }
  }

  public void deleteTemplate(Long templateId) {
    getHibernateTemplate().delete(getAssessmentTemplate(templateId).getData());
  }

  public SectionFacade addSection(Long assessmentId) {
    // #1 - get the assessment and attach teh new section to it
    // we are working with Data instead of Facade in this method but should return
    // SectionFacade at the end
    AssessmentData assessment = loadAssessment(assessmentId);
    // lazy loading on sectionSet, so need to initialize it
    Set sectionSet = getSectionSetForAssessment(assessment);
    assessment.setSectionSet(sectionSet);

    // #2 - will called the section "Section d" here d is the total no. of section in
    // this assessment
    SectionData section = new SectionData(
        null, new Integer(sectionSet.size() + 1), // NEXT section
        "Section " + (sectionSet.size() + 1), "this section is added by user",
        TypeD.DEFAULT_SECTION, SectionData.ACTIVE_STATUS,
        AgentFacade.getAgentString(), new Date(),
        AgentFacade.getAgentString(), new Date());
    section.setAssessment(assessment);
    sectionSet.add(section);
    getHibernateTemplate().saveOrUpdate(assessment);
    return new SectionFacade(section);
  }

  public SectionFacade getSection(Long sectionId) {
    SectionData section = (SectionData) getHibernateTemplate().load(SectionData.class,
        sectionId);
    return new SectionFacade(section);
  }

  public void removeSection(Long sectionId) {
    SectionData section = loadSection(sectionId);
    if (section != null) {
      // need to check that items in the selected section is not associated
      // with any pool
      QuestionPoolService qpService = new QuestionPoolService();
      HashMap h = qpService.getQuestionPoolItemMap();
      checkForQuestionPoolItem(section, h);

      AssessmentData assessment = (AssessmentData) section.getAssessment();
      // lazy loading on sectionSet, so need to initialize it
      Set sectionSet = getSectionSetForAssessment(assessment);
      assessment.setSectionSet(sectionSet);
      ArrayList sections = assessment.getSectionArraySorted();
      // need to reorder the remaining section
      HashSet set = new HashSet();
      int count = 1;
      for (int i = 0; i < sections.size(); i++) {
        SectionData s = (SectionData) sections.get(i);
        System.out.println("** s Section no: " + s.getSequence() + ":" +
                           s.getSectionId());
        System.out.println("** section Section no: " + section.getSequence() +
                           ":" + section.getSectionId());
        if (! (s.getSectionId()).equals(section.getSectionId())) {
          System.out.println("** bingo !!");
          s.setSequence(new Integer(count++));
          set.add(s);
        }
      }
      assessment.setSectionSet(set);
      getHibernateTemplate().update(assessment); // sections reordered
      getHibernateTemplate().delete(section);
    }
  }

  public SectionData loadSection(Long sectionId) {
    return (SectionData) getHibernateTemplate().load(SectionData.class,
        sectionId);
  }

  public void saveOrUpdateSection(SectionFacade section) {
    getHibernateTemplate().saveOrUpdate(section.getData());
  }

  /**
       * This method return a list of ItemData belings to the section with the given
   * sectionId
   * @param sectionId
   * @return
   */
  private List loadAllItems(Long sectionId) {
    return getHibernateTemplate().find(
        "from ItemData i where i.section.sectionId=" + sectionId);
  }

  /**
   * This method move a set of questions form one section to another
   * @param sourceSectionId
   * @param destSectionId
   */
  public void moveAllItems(Long sourceSectionId, Long destSectionId) {
    SectionData destSection = loadSection(destSectionId);
    List list = loadAllItems(sourceSectionId);
    Set set = destSection.getItemSet();
    if (set == null) {
      set = new HashSet();
    }
    int itemNum = set.size();
    for (int i = 0; i < list.size(); i++) {
      ItemDataIfc a = (ItemDataIfc) list.get(i);
      a.setSection(destSection);
      a.setSequence(new Integer(++itemNum));
      set.add(a);
    }
    destSection.setItemSet(set);
    getHibernateTemplate().update(destSection);
  }

  public ArrayList getBasicInfoOfAllActiveAssessmentTemplates(String orderBy) {
    List list = getHibernateTemplate().find(
        "select new AssessmentTemplateData(a.assessmentBaseId, a.title, a.lastModifiedDate)from AssessmentTemplateData a where a.status=1 order by a." +
        orderBy);
    ArrayList assessmentList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      AssessmentTemplateData a = (AssessmentTemplateData) list.get(i);
      AssessmentTemplateFacade f = new AssessmentTemplateFacade(a.
          getAssessmentBaseId(), a.getTitle(), a.getLastModifiedDate());
      assessmentList.add(f);
    }
    return assessmentList;
  }

  public void checkForQuestionPoolItem(AssessmentData assessment,
                                       HashMap qpItemHash) {
    Set sectionSet = getSectionSetForAssessment(assessment);
    Iterator iter = sectionSet.iterator();
    while (iter.hasNext()) {
      SectionData s = (SectionData) iter.next();
      checkForQuestionPoolItem(s, qpItemHash);
    }
  }

  public void checkForQuestionPoolItem(SectionData section,
                                       HashMap qpItemHash) {
    Set itemSet = section.getItemSet();
    HashSet newItemSet = new HashSet();
    Iterator iter = itemSet.iterator();
    System.out.println("***itemSet before=" + itemSet.size());
    while (iter.hasNext()) {
      ItemData item = (ItemData) iter.next();
      if (qpItemHash.get(item.getItemId().toString()) != null) {
        // item belongs to a pool, in this case, set section=null so
        // item won't get deleted during section deletion
        item.setSection(null);
        getHibernateTemplate().update(item);
      }
      else {
        newItemSet.add(item);
      }
    }
    System.out.println("***itemSet after=" + newItemSet.size());
    section.setItemSet(newItemSet);
    getHibernateTemplate().update(section);
  }
}
