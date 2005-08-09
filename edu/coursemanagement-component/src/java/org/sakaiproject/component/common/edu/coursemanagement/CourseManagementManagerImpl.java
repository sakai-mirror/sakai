package org.sakaiproject.component.common.edu.coursemanagement;
import java.util.List;
import java.util.Date;
import net.sf.hibernate.Hibernate;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
import org.sakaiproject.api.edu.coursemanagement.CanonicalCourseStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseManagementManager;
import org.sakaiproject.api.edu.coursemanagement.CourseOffering;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingType;
import org.sakaiproject.api.edu.coursemanagement.CourseSection;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionType;
import org.sakaiproject.api.edu.coursemanagement.CourseSet;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;
import org.sakaiproject.api.edu.coursemanagement.SessionType;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class CourseManagementManagerImpl extends HibernateDaoSupport implements CourseManagementManager {
	
	private static CourseManagementManagerImpl instance;
	
	public static CourseManagementManagerImpl getInstance(){
		if (instance == null);
		  instance = new CourseManagementManagerImpl();
    return instance;
	}
	public CanonicalCourse createCanonicalCourse(String title,
			String description, String courseNumber,
			String uuid, CanonicalCourseStatusType status) {
		CanonicalCourseImpl c = new CanonicalCourseImpl(
				title, description, courseNumber,
				uuid, status);
		Date currentDate = new Date();
		c.setTitle(title);
		c.setDescription(description);
		c.setCourseNumber(courseNumber);
		c.setCanonicalCourseStatus(status);
		c.setUuid("*uuid_canonical_"+currentDate.getTime());
		c.setCreatedBy("admin");
		c.setCreatedDate(currentDate);
		c.setLastModifiedBy("admin");
		c.setLastModifiedDate(currentDate);
		getHibernateTemplate().save(c);
		return c;
	}

	public CanonicalCourse saveCanonicalCourse(CanonicalCourse c){
		getHibernateTemplate().saveOrUpdate(c);
		return c;		
	}
	
	public CourseOffering createCourseOffering(String title,
			String description, String offeringNumber,
			String canonicalCourseUuid, String sessionUuid,
			CourseOfferingType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CourseSection createCourseSection(String title, String description,
			String sectionNumber, String courseOfferingUuid,
			String sessionUuid, CourseSectionType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CourseSection createSubSection(String title, String description,
			String sectionNumber, String courseSectionUuid, String sessionUuid,
			CourseSectionType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CourseSet createCourseSet(String title) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Session createSession(String title, String abbreviation,
			String year, SessionType type, String uuid, Boolean isCurrent) {
		SessionImpl s = new SessionImpl(title, abbreviation, 
				year, type, uuid, isCurrent);
		Date currentDate = new Date();
		s.setCreatedBy("admin");
		s.setCreatedDate(currentDate);
		s.setLastModifiedBy("admin");
		s.setLastModifiedDate(currentDate);
		getHibernateTemplate().save(s);
		return s;
	}

	public EnrollmentRecord createEnrollmentRecord(String agentUuid,
			String role, String status, String courseSectionUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ParticipationRecord createParticipationRecord(String agentUuid,
			String role, String status, String courseSectionUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CanonicalCourse getCanonicalCourse(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CourseOffering getCourseOffering(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CourseSection getCourseSection(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Session getSession(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCanonicalCourses() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCanonicalCourses(String canonicalCourseUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCourseOfferings(String canonicalCourseUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCourseSections(String courseOfferingUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getSubSections(String courseSectionUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getSessions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCanonicalCoursesByType(CanonicalCourseStatusType status) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCanonicalCoursesByType(String canonicalCourseUuid,
			CanonicalCourseStatusType status) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCourseOfferingsByType(String canonicalCourseUuid,
			CourseOfferingType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCourseSectionsByType(String courseOfferingUuid,
			CourseSectionType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getSubSectionsByType(String courseSectionUuid,
			CourseSectionType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List getCourseSet(String canonicalCourseUuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SessionType getSessionTypeByKeyword(String keyword) {
		String query = "select t from SessionTypeImpl t where t.keyword=?";
		List list = getHibernateTemplate().find(query,
				new Object[] {keyword},
				new net.sf.hibernate.type.Type[] {Hibernate.STRING});
		if (list.size()>0)
			return (SessionType)list.get(0);
		else
			return null;
	}
	
	public CanonicalCourseStatusType getCanonicalCourseStatusTypeByKeyword(String keyword) {
		String query = "select t from CanonicalCourseStatusTypeImpl t where t.keyword=?";
		List list = getHibernateTemplate().find(query,
				new Object[] {keyword},
				new net.sf.hibernate.type.Type[] {Hibernate.STRING});
		if (list.size()>0)
			return (CanonicalCourseStatusType)list.get(0);
		else
			return null;
	}
	
	public CanonicalCourseStatusType getCanonicalCourseStatusTypeByUuid(String statusUuid) {
		String query = "select t from CanonicalCourseStatusTypeImpl t where t.uuid=?";
		List list = getHibernateTemplate().find(query,
				new Object[] {statusUuid},
				new net.sf.hibernate.type.Type[] {Hibernate.STRING});
		if (list.size()>0)
			return (CanonicalCourseStatusType)list.get(0);
		else
			return null;
	}

	public EquivalentCoursesImpl saveEquivalentCourses(EquivalentCoursesImpl e){
		getHibernateTemplate().saveOrUpdate(e);
		return e;		
	}
	public CanonicalCourseImpl getCanonicalCourseByUuid(String uuid) {
		String query = "select c from CanonicalCourseImpl c where c.uuid=?";
		System.out.println("****query="+query);
		List list = getHibernateTemplate().find(query,
				new Object[] {uuid},
				new net.sf.hibernate.type.Type[] {Hibernate.STRING});
		System.out.println("****list"+list);		
		if (list!=null && list.size()>0)
			return (CanonicalCourseImpl)list.get(0);
		else
			return null;
	}
	
}
