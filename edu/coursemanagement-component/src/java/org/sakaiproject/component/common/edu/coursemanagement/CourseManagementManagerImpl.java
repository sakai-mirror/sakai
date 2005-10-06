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



  /**
   * Create a new canonical course given the parameters. If the canonicalCourseUuid is 
   * given as null, it is assumed to be a root canonical course.
   *
   * @param title
   * @param description
   * @param courseNumber
   * @param canonicalCourseUuid
   * @param status
   * @return a new canonical course.
   */
  public CanonicalCourse createCanonicalCourse(String title,
                                               String description,
                                               String courseNumber,
                                               String canonicalCourseUuid,
                                               CanonicalCourseStatusType status) {
    CanonicalCourseImpl c = new CanonicalCourseImpl(
        title, description, courseNumber,
        canonicalCourseUuid, status);
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

    /**
     * Course offerings must always be derived from a Canonical Course.
     *
     * @param title
     * @param description
     * @param offeringNumber
     * @param canonicalCourseUuid
     * @param sessionUuid
     * @param type
     * @return a new course offering.
     */
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

    /**
     * Course sections must always be derived from a Course Offering.
     *
     * @param title
     * @param description
     * @param sectionNumber
     * @param courseOfferingUuid
     * @param sessionUuid
     * @param type
     * @return a new course section.
     */
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
  
    /**
     * Create a sub-section. Sub sections must always be derived from a Course Section 
     * (or another sub-section).
     *
     * @param title
     * @param description
     * @param sectionNumber
     * @param courseSectionUuid
     * @param sessionUuid
     * @param type
     * @return a new course section.
     */
  public CourseSection createSubSection(String title, String description,
                                        String sectionNumber,
                                        String courseSectionUuid,
                                        String sessionUuid,
                                        CourseSectionType type) {

      SessionImpl session = (SessionImpl) getSession(sessionUuid);
      CourseSection parentSection = getCourseSection(courseSectionUuid);
      CourseSectionImpl section = new CourseSectionImpl(
				   title, description, sectionNumber,
                                   parentSection.getCourseOffering(), session, type);
      section.setParentId(parentSection.getUuid());
      Date currentDate = new Date();
      section.setUuid("*uuid_section_" + currentDate.getTime());
      section.setCreatedBy("admin");
      section.setCreatedDate(currentDate);
      section.setLastModifiedBy("admin");
      section.setLastModifiedDate(currentDate);
      getHibernateTemplate().save(section);
      return section;

  }

    /**
     * Create a course set.
     *
     * @param title
     * @return a new course set.
     */
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

    /**
     * Create a new session record.
     *
     * @param title
     * @param abbreviation
     * @param year
     * @param type
     * @param uuid
     * @param isCurrent
     * @return a new session.
     */
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

    /**
     * Create a new enrollment record.
     *
     * @param agentUuid
     * @param role
     * @param status
     * @param courseSectionUuid
     * @return a new enrollment record.
     */
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

    /**
     * Create a new participation record.
     *
     * @param agentUuid
     * @param role
     * @param statusUuid
     * @param courseSectionUuid
     * @return a new participation record.
     */
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

    /**
     * Find the canonical course associated with the uuid passed.
     *
     * @param uuid
     * @return a canonical course
     */
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

    /**
     * Fine the course offering associated with the uuid passed.
     *
     * @param uuid
     * @return a course offering.
     */
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

    /**
     * Find the course section associated with the uuid passed.
     *
     * @param uuid
     * @return a course section.
     */
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

    /**
     * Return a list of all canonical courses.
     *
     * @return a list of canonical courses
     */
  public List getCanonicalCourses() {
    String query = "select c from CanonicalCourseImpl c";
    List list = getHibernateTemplate().find(query);
    return list;
  }

    /**
     * Return a list of canonical courses derrived from the one given by the uuid passed.
     *
     * @param canonicalCourseUuid
     * @return a list of canonical courses
     */
  public List getCanonicalCourses(String canonicalCourseUuid) {
    String query = "select c from CanonicalCourseImpl c where c.parentId="+canonicalCourseUuid;
    List list = getHibernateTemplate().find(query);
    return list;
  }

    /**
     * Return a list of course offerings derived from the
     *
     * @param canonicalCourseUuid
     * @return a list of course offerings
     */
  public List getCourseOfferings(String canonicalCourseUuid) {
    String query = "select c from CourseOfferingImpl c and c.canonicalCourseUuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {canonicalCourseUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Return a list of course sections derived from the course offering uuid passed.
     *
     * @param courseOfferingUuid
     * @return a list of course sections
     */
  public List getCourseSections(String courseOfferingUuid) {
    String query = "select s from CourseSectionImpl s where s.courseOfferingUuid=? and s.parentId=null";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {courseOfferingUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Return a list of course sections derived from the course section uuid passed (sub-sections).
     *
     * @param courseSectionUuid
     * @return a list of course sub-sections
     */
  public List getSubSections(String courseSectionUuid) {
    String query = "select s from CourseSectionImpl s where s.parentSection.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {courseSectionUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Get the session associated with the uuid given.
     *
     * @param uuid
     * @return a session
     */
  public List getSessions() {
    String query = "select s from SessionImpl s";
    List list = getHibernateTemplate().find(query);
    return list;
  }

    /**
     * Return a list of all canonical courses filtered by status.
     *
     * @param status
     * @return a list of canoncial courses.
     */
  public List getCanonicalCoursesByType(CanonicalCourseStatusType status) {
    String query = "select c from CanonicalCourseImpl c where c.canonicalCourseStatus.canonicalCourseStatusTypeId=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {status.getCanonicalCourseStatusTypeId()}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Return a list of canonical courses derived from the one given filtered by type.
     *
     * @param canonicalCourseUuid - this is the parentId
     * @param status
     * @return a list of canonical courses
     */
  public List getCanonicalCoursesByType(String canonicalCourseUuid,
                                        CanonicalCourseStatusType status) {
    String query = "select c from CanonicalCourseImpl c where c.canonicalCourseStatus.canonicalCourseStatusTypeId=? and c.parentId="+canonicalCourseUuid;
    List list = getHibernateTemplate().find(query,
                                            new Object[] {status.getCanonicalCourseStatusTypeId()}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Return a list of course offerings derived from the canonical course given filtered by type.
     *
     * @param canonicalCourseUuid
     * @param type
     * @return a list of course offerings
     */
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

    /**
     * Return a list course sections derived from the course offering given filtered by type.
     *
     * @param courseOfferingUuid
     * @param type
     * @return a list of sections
     */
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

    /**
     * Return a list of sub-sections derived from the course section given filtered by type.
     *
     * @param courseSectionUuid
     * @param type
     * @return a list of sub-sections
     */
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
    String query = "select c from CanonicalCourseSet ccs1, CanonicalCourseSet ccs2, CanonicalCourseImpl c where ccs1.canonicalCourseUuid=? and ccs1.courseSetId=ccs2.courseSetId and ccs2.canonicalCourseId=c.canonicalCourseId";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {canonicalCourseUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

  public CourseSet getCourseSetByUuid(String setUuid) {
    String query = "select c from CourseSetImpl c where c.uuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {setUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    if (list.size() > 0) {
      return (CourseSet) list.get(0);
    }
    else {
      return null;
    }
  }

  public List getCanonicalCourseSetBySetUuid(String setUuid) {
    String query = "select ccs from CanonicalCourseSet ccs where ccs.courseSetUuid=?";
    List list = getHibernateTemplate().find(query,
                                            new Object[] {setUuid}
                                            ,
                                            new net.sf.hibernate.type.Type[] {
                                            Hibernate.STRING});
    return list;
  }

    /**
     * Get the session associated with the uuid given.
     *
     * @param uuid
     * @return a session
     */
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
    CourseSet cs = getCourseSetByUuid(setUuid);
    if (cs != null){
      List l = getCanonicalCourseSetBySetUuid(setUuid);
      getHibernateTemplate().deleteAll(l);		
      getHibernateTemplate().delete(cs);		
    }
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
