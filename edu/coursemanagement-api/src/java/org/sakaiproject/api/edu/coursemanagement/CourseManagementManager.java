/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
 *
 **********************************************************************************/

package org.sakaiproject.api.edu.coursemanagement;

import java.util.List;
import java.util.Date;

/**
 * The Course Management Manager provides the following capabilities:
 * <ul>
 * <li>Create a CanonicalCourse, CourseOffering, CourseSection (if allowed).
 * <li>Create a set of CourseSections of given type.
 * <li>Bulk section set operations based on course section types: schedule, location, etc.
 * <li>Bulk enroll students by allowing a List of Agents to be added to a specified course section.
 * <li>Get collections of Canonical Courses, Course Offerintgs, and Course Sections wholly or filtered. Get individual objects by Identifier.
 * <li>Update a given object (canonical, offering, or section) back to the persistent store.
 * <li>Post a grade of a particular type for a person in a specified course section or offering.
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseManagementManager
{

	/**
	 * Create a new canonical course given the parameters. If the canonicalCourseUuid is given as null, it is assumed to be a root canonical course.
	 * 
	 * @param title
	 * @param description
	 * @param courseNumber
	 * @param canonicalCourseUuid
	 * @param status
	 * @return a new canonical course.
	 * @author Mark Norton
	 */
	public CanonicalCourse createCanonicalCourse(String title, String description, String courseNumber, String canonicalCourseUuid,
			CanonicalCourseStatusType status);

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
	 * @author Mark Norton
	 */
	public CourseOffering createCourseOffering(String title, String description, String offeringNumber, String canonicalCourseUuid,
			String sessionUuid, CourseOfferingType type);

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
	 * @author Mark Norton
	 */
	public CourseSection createCourseSection(String title, String description, String sectionNumber, String courseOfferingUuid,
			String sessionUuid, CourseSectionType type);

	/**
	 * Create a sub-section. Sub sections must always be derived from a Course Section (or another sub-section).
	 * 
	 * @param title
	 * @param description
	 * @param sectionNumber
	 * @param courseSectionUuid
	 * @param sessionUuid
	 * @param type
	 * @return a new course section.
	 * @author Mark Norton
	 */
	public CourseSection createSubSection(String title, String description, String sectionNumber, String courseSectionUuid,
			String sessionUuid, CourseSectionType type);

	/**
	 * Create a course set.
	 * 
	 * @param title
	 * @return a new course set.
	 */
	public CourseSet createCourseSet(String title);

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
	 * @author Mark Norton
	 */
	public Session createSession(String title, String abbreviation, String year, SessionType type, String uuid, Boolean isCurrent);

	/**
	 * Create a new enrollment record.
	 * 
	 * @param agentUuid
	 * @param role
	 * @param status
	 * @param courseSectionUuid
	 * @return a new enrollment record.
	 * @author Mark Norton
	 */
	public EnrollmentRecord createEnrollmentRecord(String agentUuid, String role, String status, String courseSectionUuid);

	/**
	 * Create a new participation record.
	 * 
	 * @param agentUuid
	 * @param role
	 * @param status
	 * @param courseSectionUuid
	 * @return a new participation record.
	 * @author Mark Norton
	 */
	public ParticipationRecord createParticipationRecord(String agentUuid, String role, String status, String courseSectionUuid);

	/**
	 * Find the canonical course associated with the uuid passed.
	 * 
	 * @param uuid
	 * @return a canonical course
	 * @author Mark Norton
	 */
	public CanonicalCourse getCanonicalCourse(String uuid);

	/**
	 * Fine the course offering associated with the uuid passed.
	 * 
	 * @param uuid
	 * @return a course offering.
	 * @author Mark Norton
	 */
	public CourseOffering getCourseOffering(String uuid);

	/**
	 * Find the course section associated with the uuid passed.
	 * 
	 * @param uuid
	 * @return a course section.
	 * @author Mark Norton
	 */
	public CourseSection getCourseSection(String uuid);

	/**
	 * Get the session associated with the uuid given.
	 * 
	 * @param uuid
	 * @return a session
	 * @author Mark Norton
	 */
	public Session getSession(String uuid);

	/**
	 * Return a list of all canonical courses.
	 * 
	 * @return a list of canonical courses
	 * @author Mark Norton
	 */
	public List getCanonicalCourses();

	/**
	 * Return a list of canonical courses derrived from the one given by the uuid passed.
	 * 
	 * @param canonicalCourseUuid
	 * @return a list of canonical courses
	 * @author Mark Norton
	 */
	public List getCanonicalCourses(String canonicalCourseUuid);

	/**
	 * Return a list of course offerings derived from the
	 * 
	 * @param canonicalCourseUuid
	 * @return a list of course offerings
	 * @author Mark Norton
	 */
	public List getCourseOfferings(String canonicalCourseUuid);

	/**
	 * Return a list of course sections derived from the course offering uuid passed.
	 * 
	 * @param courseOfferingUuid
	 * @return a list of course sections
	 * @author Mark Norton
	 */
	public List getCourseSections(String courseOfferingUuid);

	/**
	 * Return a list of course sections derived from the course section uuid passed (sub-sections).
	 * 
	 * @param courseSectionUuid
	 * @return a list of course sub-sections
	 * @author Mark Norton
	 */
	public List getSubSections(String courseSectionUuid);

	/**
	 * Return a list of all known sessions.
	 * 
	 * @return a list of sessions
	 * @author Mark Norton
	 */
	public List getSessions();

	/**
	 * Return a list of all canonical courses filtered by type.
	 * 
	 * @param status
	 * @return a list of canoncial courses.
	 * @author Mark Norton
	 */
	public List getCanonicalCoursesByType(CanonicalCourseStatusType status);

	/**
	 * Return a list of canonical courses derived from the one given filtered by type.
	 * 
	 * @param canonicalCourseUuid
	 * @param status
	 * @return a list of canonical courses
	 * @author Mark Norton
	 */
	public List getCanonicalCoursesByType(String canonicalCourseUuid, CanonicalCourseStatusType status);

	/**
	 * Return a list of course offerings derived from the canonical course given filtered by type.
	 * 
	 * @param canonicalCourseUuid
	 * @param type
	 * @return a list of course offerings
	 * @author Mark Norton
	 */
	public List getCourseOfferingsByType(String canonicalCourseUuid, CourseOfferingType type);

	/**
	 * Return a list course sections derived from the course offering given filtered by type.
	 * 
	 * @param courseOfferingUuid
	 * @param type
	 * @return a list of sections
	 * @author Mark Norton
	 */
	public List getCourseSectionsByType(String courseOfferingUuid, CourseSectionType type);

	/**
	 * Return a list of sub-sections derived from the course section given filtered by type.
	 * 
	 * @param courseSectionUuid
	 * @param type
	 * @return a list of sub-sections
	 * @author Mark Norton
	 */
	public List getSubSectionsByType(String courseSectionUuid, CourseSectionType type);

	/**
	 * Return a list of CourseSet object in which a given canonical course is associated with
	 * 
	 * @param canonicalCourseUuid
	 * @return a list of CourseSet
	 * @author Mark Norton
	 */
  public List getCourseSet(String canonicalCourseUuid);
  public CanonicalCourseStatusType getCanonicalCourseStatusType(String canonicalCourseUuid);
  public SessionType getSessionType(String canonicalCourseUuid);
        
        
}
