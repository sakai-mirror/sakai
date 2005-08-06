package org.sakaiproject.component.common.edu.coursemanagement;
import java.util.List;
import java.util.Date;

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

	public CanonicalCourse createCanonicalCourse(String title,
			String description, String courseNumber,
			String canonicalCourseUuid, CanonicalCourseStatusType status) {
	    CanonicalCourseImpl c = new CanonicalCourseImpl();
	    c.setTitle(title);
	    c.setDescription(description);
	    c.setCourseNumber(courseNumber);
	    c.setUuid(canonicalCourseUuid);
            try{
		//getHibernateTemplate().save(c);
            }
	    catch (Exception e){
                System.out.println(e.getMessage());
	    }
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
			String year, SessionType type, String uuid,
                        Boolean isCurrent, String createdBy, Date createdDate, 
                        String lastModifiedBy, Date lastModifiedDate) {
            SessionImpl s = new SessionImpl(title, abbreviation, 
                        year, type, uuid,
                        isCurrent, createdBy, createdDate,
                        lastModifiedBy, lastModifiedDate);
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

}
