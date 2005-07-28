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

import java.util.Date;
import java.util.List;

/**
 * A Course Offering can occur in a specific term and have a grade type, status type, etc. A course offering is a canonical course associated with a specific term. It may be concrete where the canonical is abstract. The roster associated with a course
 * offering allows sections to be aggregated.
 * <ul>
 * <li>A title [String]
 * <li>A description (subject) [String]
 * <li>An offering number [String]
 * <li>A unique identifier [Id Object]
 * <li>A session reference [Session Id Object]
 * <li>A Sakai SuperStructure node reference [Node]
 * <li>Maximum number of students allowed [Integer]
 * <li>An offering type [Type Object]
 * <li>An offering status type [Enumerated Property]
 * <li>A list of people designated as the default leaders. [List of ParticipationObjects]
 * <li>An aggregated list of enrolled students. [List of EnrollmentRecords]
 * <li>An enrollment type [Enumerated Property]
 * <li>A default list of other people associated with this course. [List of ParticipationRecords]
 * <li>The canonical course that this offering represents. [CanonicalCourse Id]
 * <li>A list of course sections derived from this offering. [List of CourseSection Ids]
 * <li>Uuid of Agent who created this record [String]
 * <li>Date & Time whne this record is created [Date]   
 * <li>Uuid of Agent who last modified this record [String]
 * <li>Date & Time whne this record is last modified [Date]   
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseOffering
{

	/**
	 * Get the title of a course offering as it might appear in a course catalog.
	 * 
	 * @return the title of the course offering.
	 */
	public String getTitle();

	/**
	 * Set the title of a course offering as it might appear in a course catalog.
	 * 
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * Get the description of this course offering.
	 * 
	 * @return the desription of the course offering.
	 */
	public String getDescription();

	/**
	 * Set the description of this course offering.
	 * 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Get the course number. There are no restrictions on how this number is formatted.
	 * 
	 * @return Course number
	 */
	public String getOfferingNumber();

	/**
	 * Set the course number. There are no restirctions on how this number is foramtted.
	 * 
	 * @param courseNumber
	 */
	public String setOfferingNumber(String offeringNumber);

	/**
	 * Get the unique id of this course offering.
	 * 
	 * @return canonical course uuid
	 */
	public String getUuid();

	/**
	 * Get the term of this course offering.
	 * 
	 * @return the term
	 */
	public Session getSession();

	/**
	 * Set the term of this course offering.
	 * 
	 * @param term
	 */
	public void setSession(Session term);

	/**
	 * Return true if this course offering is cross listed. A course is cross listed if the list of equivalent courses is non-empty.
	 * 
	 * @return true if cross listed.
	 */
	public Boolean isCrossListed();

	/**
	 * Get a list of equivalent course offering uuids. This is one way to represent cross listing in a set of courses.
	 * 
	 * @return List of canonical course uuids.
	 */
	public List getEquivalents();

	/**
	 * Add the course offering given by its uuid to the list of equivalent courses.
	 * 
	 * @param courseOfferingUuid
	 */
	public void addEquivalent(String courseOfferingUuid);

	/**
	 * Remove the course offering given by its uuid from the list of equivalent courses.
	 * 
	 * @param courseOfferingUuid
	 */
	public void removeEquivalent(String courseOfferingUuid);

	/**
	 * Get the maxiumum number of students permitted to enroll in this course offering.
	 * 
	 * @return maxiumum number of students allowed
	 */
	public int getMaximumStudents();

	/**
	 * Set the maximum number of students permitted to entroll in this course offering.
	 * 
	 * @param maxStudents
	 */
	public void setMaximumStudents(int maxStudents);

	/**
	 * Get the course offering type. This type indicates what kind of offering is being made.
	 * 
	 * @return course offering type.
	 */
	public CourseOfferingType getOfferingType();

	/**
	 * Set the course offering type.
	 * 
	 * @param type
	 */
	public void setOfferingType(CourseOfferingType type);

	/**
	 * Get the course ofering status. The course offering status might be open, closed, wait-listed, etc.
	 * 
	 * @return
	 */
	public CourseOfferingStatusType getOfferingStatus();

	/**
	 * Set the course offering status type.
	 * 
	 * @param type
	 */
	public void setOfferingStatusType(CourseOfferingStatusType type);

	/**
	 * Get the list of course sections that represent this offering.
	 * 
	 * @return List of course sections
	 */
	public List getCourseSections();

	/**
	 * Add a course section that represents this offering.
	 * 
	 * @param sectionUuid
	 */
	public void addCourseSection(String sectionUuid);

	/**
	 * Remove a course section from this offering list.
	 * 
	 * @param sectionUuid
	 */
	public void removeCourseSection(String sectionUuid);

	/**
	 * Get the list of participation for the instructors of this course. While most sections will have only one instructor, provisions are made for multiple instructors.
	 * 
	 * @return List of agent uuids for instructors.
	 */
	public List getDefaultLeaders();

	/**
	 * Add a participation record as an instructor of this section.
	 * 
	 * @param agentUuid
	 */
	public void addDefaultLeader(ParticipationRecord participationRecord);

	/**
	 * Remove a leader given an agent uuid. The participation record containing this uuid will be removed.
	 * 
	 * @param agentUuid
	 */
	public void removeDefaultLeader(String agentUuid);

	/**
	 * Get an aggregated list of all students enrolled in this course. This is a list of Enrollment records drawn from sections under this offering. Aggregated enrollment is a simple way to find all people enrolled in any section based on this course
	 * offering.
	 * 
	 * @return aggregated list of enrollment records.
	 */
	public List getAggregatedEnrollment();

	/**
	 * Get the entrollment type for this course. Since a course offering enrollment is always an aggregate, this will always return an EnrollmentType of aggregate.
	 * 
	 * @return entrollment type.
	 */
	public EnrollmentType getEnrollmentType();

	/**
	 * Get a list of other people associated with this course offering. This list might include teaching assistance, lab supervisors, translators, etc. The list elements are participation uuids.
	 * 
	 * @return
	 */
	public List getOtherPeople();

	/**
	 * Add a person to the other list.
	 * 
	 * @param agentUuid
	 */
	public void addOtherPerson(ParticipationRecord participant);

	/**
	 * Remove a person from the other list given an agent uuid. The participation record associated with this uuid will be removed from the list of other people.
	 * 
	 * @param agentUuid
	 */
	public void removeOtherPerson(String agentUuid);

	/**
	 * Get the uuid of the canonical course that this offering represents.
	 * 
	 * @return uuid of canonical course
	 */
	public String getCanonicalCourse();

	/**
	 * Set the uuid of the canonical course that this offering represents.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void setCanonicalCourse(String canonicalCourseUuid);

	/**
	 * Get a list of uuids of sections derived from this course offering. Each of these sections is a real class with a location, teacher, students, etc.
	 * 
	 * @return List of course section uuids.
	 */
	public List getSections();

	/**
	 * Add a course section uuid to represent this offering.
	 * 
	 * @param sectionUuid
	 */
	public void addSection(String sectionUuid);

	/**
	 * Remove a course section uuid that reprsents this course offering.
	 * 
	 * @param sectionUuid
	 */
	public void removeSection(String sectionUuid);
	
    /**
     * Get the uuid of the Agent who has created this course offering.
     * @return uuid of the Agent
     */
	public String getCreatedBy();

	/**
	 * Set the uuid of the Agent who has created this course offering.
	 *
	 *	@param uuid of the Agent
	 */
	public void setCreatedBy(String createdBy);

    /**
     * Get the date when this course offering is created.
     * @return creation date
     */
	public Date getCreatedDate();

	/**
	 * Set the creation date.
	 *
	 *	@param creation date
	 */
	public void setCreatedDate(Date createdDate);

    /**
     * Get the uuid of the Agent who has last modified this course offering.
     * @return uuid of the Agent
     */
	public String getLastModifiedBy();

	/**
	 * Set the uuid of the Agent who has last modified this course offering.
	 *
	 *	@param uuid of the Agent
	 */
	public void setLastModifiedBy(String lastModifiedBy);

    /**
     * Get the date when this course offering is last modified.
     * @return last modified date
     */
	public Date getLastModifiedDate();

	/**
	 * Set the last modified date.
	 *
	 *	@param last modified date
	 */
	public void setLastModifiedDate(Date lastModifiedDate);
	
	
}
