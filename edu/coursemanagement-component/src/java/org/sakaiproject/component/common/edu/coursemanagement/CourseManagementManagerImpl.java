package org.sakaiproject.component.common.edu.coursemanagement;

import java.util.List;
import java.util.Date;
import java.util.Collection;
import net.sf.hibernate.Hibernate;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
import org.sakaiproject.api.edu.coursemanagement.CanonicalCourseStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseManagementManager;
import org.sakaiproject.api.edu.coursemanagement.CourseOffering;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingType;
import org.sakaiproject.api.edu.coursemanagement.CourseSection;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionType;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentStatusType;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentType;
import org.sakaiproject.api.edu.coursemanagement.ParticipationStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseSet;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;
import org.sakaiproject.api.edu.coursemanagement.SessionType;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class CourseManagementManagerImpl
    extends HibernateDaoSupport
    implements CourseManagementManager {

  public CanonicalCourse createCanonicalCourse(String title,
                                               String description,
                                               String courseNumber,
                                               String uuid,
                                               CanonicalCourseStatusType status) {
    CanonicalCourseImpl c = new CanonicalCourseImpl(
        title, description, courseNumber,
        uuid, status);
    Date currentDate = new Date();
    c.setTitle(title);
    c.setDescription(description);
    c.setCourseNumber(courseNumber);
    c.setCanonicalCourseStatus(status);
    c.setUuid("*uuid_canonical_" + currentDate.getTime());
    c.setCreatedBy("admin");
    c.setCreatedDate(currentDate);
    c.setLastModifiedBy("admin");
    c.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(c);
    return c;
  }

  // added by daisyf
  public CanonicalCourse saveCanonicalCourse(CanonicalCourse c) {
    getHibernateTemplate().saveOrUpdate(c);
    return c;
  }

  public CourseOffering createCourseOffering(String title,
                                             String description,
                                             String offeringNumber,
                                             String canonicalCourseUuid,
                                             String sessionUuid,
                                             CourseOfferingType type) {
    SessionImpl session = (SessionImpl) getSession(sessionUuid);
  	CourseOfferingImpl offering = new CourseOfferingImpl(
        title, description, offeringNumber,
        canonicalCourseUuid, session, type);
    Date currentDate = new Date();
  	offering.setUuid("*uuid_offering_" + currentDate.getTime());
    offering.setCreatedBy("admin");
    offering.setCreatedDate(currentDate);
    offering.setLastModifiedBy("admin");
    offering.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(offering);
    return offering;
  }

  // added by daisyf
  public CourseOffering saveCourseOffering(CourseOffering c) {
    getHibernateTemplate().saveOrUpdate(c);
    return c;
  }

  public CourseSection createCourseSection(String title, String description,
                                           String sectionNumber,
                                           String courseOfferingUuid,
                                           String sessionUuid,
                                           CourseSectionType type) {
    SessionImpl session = (SessionImpl) getSession(sessionUuid);
   	CourseSectionImpl section = new CourseSectionImpl(
        title, description, sectionNumber,
        courseOfferingUuid, session, type);
    Date currentDate = new Date();
    section.setUuid("*uuid_section_" + currentDate.getTime());
    section.setCreatedBy("admin");
    section.setCreatedDate(currentDate);
    section.setLastModifiedBy("admin");
    section.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(section);
    return section;
  }

  // added by daisyf  
  public CourseSection saveCourseSection(CourseSection c) {
    getHibernateTemplate().saveOrUpdate(c);
    return c;
  }
  
  public CourseSection createSubSection(String title, String description,
                                        String sectionNumber,
                                        String courseSectionUuid,
                                        String sessionUuid,
                                        CourseSectionType type) {
    // TODO Auto-generated method stub
    return null;
  }

  public CourseSet createCourseSet(String title) {
    Date currentDate = new Date();
    CourseSetImpl cs = new CourseSetImpl(title);
    cs.setUuid("*uuid_courseset_" + currentDate.getTime());
    cs.setCreatedBy("admin");
    cs.setCreatedDate(currentDate);
    cs.setLastModifiedBy("admin");
    cs.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(cs);
    return cs;
  }

  // added by daisyf
  public CourseSet saveCourseSet(CourseSet cs) {
    getHibernateTemplate().saveOrUpdate(cs);
    return cs;
  }

  public Session createSession(String title, String abbreviation,
                               String year, SessionType type, String uuid,
                               Boolean isCurrent) {
    SessionImpl s = new SessionImpl(title, abbreviation,
                                    year, type, uuid, isCurrent);
    Date currentDate = new Date();
  	s.setUuid("*uuid_session_" + currentDate.getTime());
    s.setCreatedBy("admin");
    s.setCreatedDate(currentDate);
    s.setLastModifiedBy("admin");
    s.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(s);
    return s;
  }

  public EnrollmentRecord createEnrollmentRecord(String agentUuid,
                                                 String role, String statusUuid,
                                                 String courseSectionUuid) {
    EnrollmentStatusType status = getEnrollmentStatusByUuid(statusUuid);
  	EnrollmentRecordImpl e = new EnrollmentRecordImpl(
    		agentUuid, role, status, courseSectionUuid);
    Date currentDate = new Date();
  	e.setUuid("*uuid_enrollment_" + currentDate.getTime());
    e.setCreatedBy("admin");
    e.setCreatedDate(currentDate);
    e.setLastModifiedBy("admin");
    e.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(e);
    return e;
  }

  public ParticipationRecord createParticipationRecord(String agentUuid,
      String role, String statusUuid, String courseSectionUuid) {
    ParticipationStatusType status = getParticipationStatusByUuid(statusUuid);
    ParticipationRecordImpl p = new ParticipationRecordImpl(
    		agentUuid, role, status, courseSectionUuid);
    Date currentDate = new Date();
  	p.setUuid("*uuid_enrollment_" + currentDate.getTime());
    p.setCreatedBy("admin");
    p.setCreatedDate(currentDate);
    p.setLastModifiedBy("admin");
    p.setLastModifiedDate(currentDate);
    getHibernateTemplate().save(p);
    return p;
  }

  public CanonicalCourse getCanonicalCourse(String uuid) {
    String query = "select c from CanonicalCourseImpl c where c.uuid=?";
    System.out.println("****query=" + query);
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    System.out.println("****list" + list);
    if (list != null && list.size() > 0) {
      return (CanonicalCourse) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseOffering getCourseOffering(String uuid) {
    String query = "select c from CourseOfferingImpl c where c.uuid=?";
    System.out.println("****query=" + query);
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    System.out.println("****list" + list);
    if (list != null && list.size() > 0) {
      return (CourseOffering) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseSection getCourseSection(String uuid) {
    String query = "select c from CourseSectionImpl c where c.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    System.out.println("****list" + list);
    if (list != null && list.size() > 0) {
      return (CourseSection) list.get(0);
    }
    else {
      return null;
    }
  }

  public List getCanonicalCourses() {
    String query = "select c from CanonicalCourseImpl c";
    List list = getHibernateTemplate().find(query);
    return list;
  }

  // this method does not make sense to me - daisyf
  public List getCanonicalCourses(String canonicalCourseUuid) {
    // TODO Auto-generated method stub
    return null;
  }

  public List getCourseOfferings(String canonicalCourseUuid) {
    String query = "select c from CourseOfferingImpl c and c.canonicalCourseUuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {canonicalCourseUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

  public List getCourseSections(String courseOfferingUuid) {
    String query = "select s from CourseSectionImpl s where s.courseOfferingUuid=? and s.parentId=null";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {courseOfferingUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

  public List getSubSections(String courseSectionUuid) {
    String query = "select s from CourseSectionImpl s where s.parentSection.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {courseSectionUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

  public List getSessions() {
    String query = "select s from SessionImpl s";
    List list = getHibernateTemplate().find(query);
    return list;
  }

  public List getCanonicalCoursesByType(CanonicalCourseStatusType status) {
    String query = "select c from CanonicalCourseImpl c where c.canonicalCourseStatus.canonicalCourseStatusTypeId=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {status.getCanonicalCourseStatusTypeId()}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

  // hmmmm... i don't understand this one neither
  public List getCanonicalCoursesByType(String canonicalCourseUuid,
                                        CanonicalCourseStatusType status) {
  	return null;
  }

  public List getCourseOfferingsByType(String canonicalCourseUuid,
                                       CourseOfferingType type) {
    String query = "select c from CourseOfferingImpl c"
    	+ " where c.offeringType.courseOfferingTypeId=?" 
    	+ " and c.canonicalCourseUuid=?"	;
    List list = getHibernateTemplate().find(query,
                                            new Object[] {((CourseOfferingTypeImpl)type).getCourseOfferingTypeId(), canonicalCourseUuid }
                                            ,
                                            new net.sf.hibernate.type.Type[] {
    		                                    Hibernate.LONG,
                                            Hibernate.STRING});
    return list;
  }

  public List getCourseSectionsByType(String courseOfferingUuid,
                                      CourseSectionType type) {
    String query = "select s from CourseSectionImpl s"
    	+ " where s.sectionType.courseSectionTypeId=?" 
    	+ " and c.courseOfferingUuid=?"	;
    List list = getHibernateTemplate().find(query,
                                            new Object[] {((CourseSectionTypeImpl)type).getCourseSectionTypeId(), courseOfferingUuid }
                                            ,
                                            new net.sf.hibernate.type.Type[] {
    		                                    Hibernate.LONG,
                                            Hibernate.STRING});
    return list;
  }

  public List getSubSectionsByType(String courseSectionUuid,
                                   CourseSectionType type) {
    String query = "select s from CourseSectionImpl s"
    	+ " where s.sectionType.courseSectionTypeId=?" 
    	+ " and s.parentSection.uuid=?"	;
    List list = getHibernateTemplate().find(query,
                                            new Object[] {((CourseSectionTypeImpl)type).getCourseSectionTypeId(), courseSectionUuid }
                                            ,
                                            new net.sf.hibernate.type.Type[] {
    		                                    Hibernate.LONG,
                                            Hibernate.STRING});
    return list;
  }

  public List getCourseSet(String canonicalCourseUuid) {
    // TODO Auto-generated method stub
    return null;
  }

  // all the methods below are added by daisyf
  public SessionType getSessionTypeByKeyword(String keyword) {
    String query = "select t from SessionTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (SessionType) list.get(0);
    }
    else {
      return null;
    }
  }

  public Session getSession(String uuid) {
    String query = "select t from SessionImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (Session) list.get(0);
    }
    else {
      return null;
    }
  }

  public CanonicalCourseStatusType getCanonicalCourseStatusByKeyword(String
      keyword) {
    String query =
        "select t from CanonicalCourseStatusTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CanonicalCourseStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CanonicalCourseStatusType getCanonicalCourseStatusByUuid(String uuid) {
    String query =
        "select t from CanonicalCourseStatusTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CanonicalCourseStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseOfferingStatusType getCourseOfferingStatusByUuid(String uuid) {
    String query =
        "select t from CourseOfferingStatusTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseOfferingStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseOfferingStatusType getCourseOfferingStatusByKeyword(String keyword) {
    String query =
        "select t from CourseOfferingStatusTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseOfferingStatusType) list.get(0);
    }
    else {
      return null;
    }
  }


  public CourseOfferingType getCourseOfferingTypeByKeyword(String keyword) {
    String query = "select t from CourseOfferingTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseOfferingType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseSectionStatusType getCourseSectionStatusByUuid(String uuid) {
    String query = "select t from CourseSectionStatusTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseSectionStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseSectionType getCourseSectionTypeByUuid(String uuid) {
    String query = "select t from CourseSectionTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseSectionType) list.get(0);
    }
    else {
      return null;
    }
  }

  public CourseSectionType getCourseSectionTypeByKeyword(String keyword) {
    String query = "select t from CourseSectionTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseSectionType) list.get(0);
    }
    else {
      return null;
    }
  }

  public EnrollmentStatusType getEnrollmentStatusByUuid(String uuid) {
    String query = "select t from EnrollmentStatusTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (EnrollmentStatusType) list.get(0);
    }
    else {
      return null;
    }
  }
  
  public EnrollmentStatusType getEnrollmentStatusByKeyword(String keyword) {
    String query = "select t from EnrollmentStatusTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (EnrollmentStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

  public EnrollmentType getEnrollmentTypeByUuid(String uuid) {
    String query = "select t from EnrollmentTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (EnrollmentType) list.get(0);
    }
    else {
      return null;
    }
  }

  public EnrollmentType getEnrollmentTypeByKeyword(String keyword) {
    String query = "select t from EnrollmentTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (EnrollmentType) list.get(0);
    }
    else {
      return null;
    }
  }

  public ParticipationStatusType getParticipationStatusByUuid(String uuid) {
    String query = "select t from ParticipationStatusTypeImpl t where t.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {uuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (ParticipationStatusType) list.get(0);
    }
    else {
      return null;
    }
  }
  
  public ParticipationStatusType getParticipationStatusByKeyword(String keyword) {
    String query = "select t from ParticipationStatusTypeImpl t where t.keyword=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {keyword}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (ParticipationStatusType) list.get(0);
    }
    else {
      return null;
    }
  }

	public void removeCourseSet(String setUuid) {
		// TODO Auto-generated method stub
		
	}

	public void removeCanonicalCourse(String canonicalUuid) {
		CanonicalCourse c = getCanonicalCourse(canonicalUuid);
    if (c != null)
		  getHibernateTemplate().delete(c);		
	}

	public void removeCourseOffering(String offeringUuid) {
		CourseOffering c = getCourseOffering(offeringUuid);
	  if (c != null)
  		getHibernateTemplate().delete(c);		
	}

	public void removeCourseSection(String sectionUuid) {
		CourseSection c = getCourseSection(sectionUuid);
	  if (c != null)
  		getHibernateTemplate().delete(c);		
	}

	public void removeSession(String sessionUuid) {
		Session c = getSession(sessionUuid);
	  if (c != null)
  		getHibernateTemplate().delete(c);		
	}

	public void saveOrUpdateAll(Collection c) {
	  if (c != null)
  		getHibernateTemplate().saveOrUpdateAll(c);		
	}

}
