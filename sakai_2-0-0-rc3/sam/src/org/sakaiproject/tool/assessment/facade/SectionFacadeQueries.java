package org.sakaiproject.tool.assessment.facade;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.Hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionMetaData;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class SectionFacadeQueries  extends HibernateDaoSupport implements SectionFacadeQueriesAPI {
  private static Log log = LogFactory.getLog(SectionFacadeQueries.class);

  public SectionFacadeQueries () {
  }

  public static void main(String[] args) throws DataFacadeException {
    SectionFacadeQueriesAPI instance = new SectionFacadeQueries ();
    // add an assessmentTemplate
    if (args[0].equals("add")) {
      Long assessmentId = new Long(args[1]);
      Long sectionId = instance.addSection(assessmentId);
      SectionFacade section = instance.get(sectionId);
      //System.out.println("**print Sectoion");
      print(section);
    }
    if (args[0].equals("remove")) {
      instance.remove(new Long(args[1]));
      //System.out.println("**deleted SectionId #");
    }
    if (args[0].equals("load")) {
      SectionFacade s = (SectionFacade)instance.get(new Long(args[1]));
      //System.out.println("**print Section");
      print(s);
    }
    System.exit(0);
  }

  public static void print(SectionFacade section) {
    //System.out.println("**sectionId #" + section.getId());
    //System.out.println("**Section Title = " + section.getTitle());
    //System.out.println("**Item = " + section.getItemSet());
  }

  public Long addSection(Long assessmentId) {
    // take default submission model
    SectionData section = new SectionData();
      AssessmentBaseData assessment = (AssessmentBaseData) getHibernateTemplate().load(AssessmentBaseData.class, assessmentId);
      //section.setAssessmentId(assessmentId);
      section.setAssessment((AssessmentData)assessment);
      section.setDuration(new Integer(30));
      section.setSequence(new Integer(1));
      section.setTitle("section title");
      section.setDescription("section description");
      section.setTypeId(TypeFacade.DEFAULT_SECTION);
      section.setStatus(new Integer(1));
      section.setCreatedBy("1");
      section.setCreatedDate(new Date());
      section.setLastModifiedBy("1");
      section.setLastModifiedDate(new Date());
      ItemManager itemManager = new ItemManager();
      ItemData item = itemManager.prepareItem();
      item.setSection(section);
      section.addItem(item);

      getHibernateTemplate().save(section);
    return section.getSectionId();
  }

  public void remove(Long sectionId) {
      SectionFacade section = (SectionFacade) getHibernateTemplate().load(SectionData.class, sectionId);
      getHibernateTemplate().delete(section);
  }

  public SectionFacade get(Long sectionId) {
      SectionData section = (SectionData) getHibernateTemplate().load(SectionData.class, sectionId);
      return new SectionFacade(section);
  }

  public SectionData load(Long sectionId) {
      return (SectionData) getHibernateTemplate().load(SectionData.class, sectionId);
  }

  public void addSectionMetaData(Long sectionId, String label, String value) {
    System.out.println("lydiatest adding metadata " + sectionId + " " + label + " " + value );
    SectionData section = (SectionData)getHibernateTemplate().load(SectionData.class, sectionId);
    if (section != null) {

      SectionMetaData sectionmetadata = new SectionMetaData(section, label, value);
      getHibernateTemplate().save(sectionmetadata);
    }
  }

  public void deleteSectionMetaData(Long sectionId, String label) {
    String query = "from SectionMetaData imd where imd.section.sectionId=? and imd.label= ? ";
    List sectionmetadatalist = getHibernateTemplate().find(query,
        new Object[] { sectionId, label },
        new net.sf.hibernate.type.Type[] { Hibernate.LONG , Hibernate.STRING });
    getHibernateTemplate().deleteAll(sectionmetadatalist);
  }



}