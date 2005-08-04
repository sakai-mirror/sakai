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

import java.util.Set;

import org.sakaiproject.api.common.manager.Persistable;

/**
 * A CourseSection is a physical instance of a course offering that has a schedule, location, roster, etc. A CourseSection represents an actual class with an assigned instructor(s) and enrolled students.
 * <ul>
 * <li>A title [String]
 * <li>A description (subject) [String]
 * <li>A section number [String]
 * <li>A unique identifier [Id Object]
 * <li>Maximum number of students allowed [Integer]
 * <li>A course section kind [Enumerated Property]
 * <li>A course section status [Enumerated Property]
 * <li>A session reference [Session Id Object]
 * <li>A Sakai SuperStructure node reference [Node]
 * <li>A set of SakaiSuperStructure nodes to define a cross-seting set. [Set of Nodes]
 * <li>Schedule of meeting dates/times and locations. [Schedule Id Object]
 * <li>A set of people designated as leaders. [Set of ParticipationRecords]
 * <li>A set of enrolled students, each with their own enrollment record. [Set of Enrollment Record Objects]
 * <li>A student enrollment status [Enumerated Property]
 * <li>A set of other people associated with this section. [Set of ParticipationRecords]
 * <li>The course offering that this section is derived from. [CourseOffering Id]
 * <li>A set of section events [Set of Event Ids]
 * <li>A flag to indicate created locally [Boolean, true if created locally]
 * <li>A flag to indicate this as a holding section [Boolean, true if a holding section]
 * <li>A flag to indicate students may self-register for this section [Boolean, true if self-registration is allowed]
 * <li>A Location [String]
 * <li>Meeting Time [String] 
 * <li>A parent Course Section object.  [CourseSection Uuid]
 * <li>A set of child course sections derived from this course section. [Set of CourseSection Ids]
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseSection extends Persistable
{
	/**
	 * Get the title of a course section.
	 * 
	 * @return the title of the course section
	 */
	public String getTitle();

	/**
	 * Set the title of this course section.
	 * 
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * Get the description of this course section.
	 * 
	 * @return description of course section.
	 */
	public String getDescription();

	/**
	 * Set the descriptionof this course section.
	 * 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Get the course section number.
	 * 
	 * @return course section number.
	 */
	public String getSectionNumber();

	/**
	 * Set the course section number.
	 * 
	 * @param sectionNumber
	 */
	public void setSectionNumber(String sectionNumber);

	/**
	 * Get the maxiumum number of students permitted to enroll in this course section.
	 * 
	 * @return maxiumum number of students allowed
	 */
	public int getMaximumStudents();

	/**
	 * Set the maximum number of students permitted to entroll in this course section.
	 * 
	 * @param maxStudents
	 */
	public void setMaximumStudents(int maxStudents);

	/**
	 * Get the session for this course section.
	 * 
	 * @return term for this course section.
	 */
	public Session getSession();

	/**
	 * Set the session for this course section.
	 * 
	 * @param term
	 */
	public void setSession(Session term);

	/**
	 * Get the course section type. This type could be a lecture, seminar, lab, recitation, studio, etc.
	 * 
	 * @return course section type.
	 */
	public CourseSectionType getSectionType();

	/**
	 * Set the course section type.
	 * 
	 * @param type
	 */
	public void setSectionType(CourseSectionType type);

	/**
	 * Get the course section status type. This type could be open, closed, or wait-seted.
	 * 
	 * @return course ection status type.
	 */
	public CourseSectionStatusType getSectionStatus();

	/**
	 * Set the course section status type.
	 * 
	 * @param type
	 */
	public void setSectionStatus(CourseSectionStatusType type);

	/**
	 * Get the schedule for this course section. The schedule specified when the course meets, how often, what time, etc.
	 * 
	 * @return course section schedule
	 */
	//public Schedule getSchedule ();

	/**
	 * Get the set of participation for the instructors of this course. While most sections will have only one instructor, provisions are made for multiple instructors.
	 * 
	 * @return Set of agent uuids for instructors.
	 */
	public Set getLeaders();

	/**
	 * Add a participation record as an instructor of this section.
	 * 
	 * @param agentUuid
	 */
	public void addLeader(ParticipationRecord participationRecord);

	/**
	 * Remove a leader given an agent uuid. The participation record containing this uuid will be removed.
	 * 
	 * @param agentUuid
	 */
	public void removeLeader(String agentUuid);

	/**
	 * Get the set of enrollment records for students enrolled in this section. Note that this set contains actual record, not uuids as is typical elsewhere.
	 * 
	 * @return Set of student enrollment records.
	 */
	public Set getEnrollmentRecords();

	/**
	 * Add an enrollment record for a student enrolled in this section.
	 * 
	 * @param student
	 *        enrollment record
	 */
	public void addEnrollmentRecord(EnrollmentRecord record);

	/**
	 * Remove the enrollment record of a student for this ection.
	 * 
	 * @param record
	 */
	public void removeEnrollmentRecord(EnrollmentRecord record);

	/**
	 * Get the entrollment status type.
	 * 
	 * @return
	 */
	public EnrollmentStatusType getEnrollmentStatus();

	/**
	 * Set the enrollment status type.
	 * 
	 * @param status
	 */
	public void setEnrollmentStatus(EnrollmentStatusType status);

	/**
	 * Get a set of other people associated with this course offering. This set might include teaching assistance, lab supervisors, translators, etc. The set elements are participation uuids.
	 * 
	 * @return
	 */
	public Set getOtherPeople();

	/**
	 * Add a person to the other set.
	 * 
	 * @param agentUuid
	 */
	public void addOtherPerson(ParticipationRecord participant);

	/**
	 * Remove a person from the other set given an agent uuid. The participation record associated with this uuid will be removed from the set of other people.
	 * 
	 * @param agentUuid
	 */
	public void removeOtherPerson(String agentUuid);

	/**
	 * Get the course offering uuid that this course section represents.
	 * 
	 * @return course offering uuid.
	 */
	public String getCourseOffering();

	/**
	 * Set the course offering uuid id that this course section represents.
	 * 
	 * @param courseOfferingUuid
	 */
	public void setCourseOffering(String courseOfferingUuid);

	/**
	 * Get the uuid of the schedule associated with this course section. See schedule service for more details.
	 * 
	 * @return schedule uuid.
	 */
	public String getSchedule();

	/**
	 * Set the uuid of the schedule associated wtih this course section See schedule service for more details.
	 * 
	 * @param scheduleUuid
	 */
	public void setSchedule(String scheduleUuid);

	/**
	 * Get the set of event associated with this course section. Event objects are TBD at this time.
	 * 
	 * @return
	 */
	public Set getSectionEvents();

	/**
	 * Add an event given it's uuid.
	 * 
	 * @param eventUuid
	 */
	public void addSectionEvent(String eventUuid);

	/**
	 * Remove an event given it's uuid.
	 * 
	 * @param eventUuid
	 */
	public void removeSectionEvent(String eventUuid);

	/**
	 * Check to see if this section was created locally. True if local, false if this section represents one in a registrar system.
	 * 
	 * @return
	 */
	public Boolean getCreatedLocally();

	/**
	 * Set the created locally flag.
	 * 
	 * @param createdLocally
	 */
	public void setCreatedLocally(Boolean createdLocally);

	/**
	 * Check to see if this is a holding section. True if this section is being used to hold students that will be re-assigned later.
	 * 
	 * @return
	 */
	public Boolean getHoldingSection();

	/**
	 * Set the holding section flag.
	 * 
	 * @param holdingSection
	 */
	public void setHoldingSection(Boolean holdingSection);

	/**
	 * Check to see if self-registration is allowed for this section. If true, then students may enroll or register themselves.
	 * 
	 * @return
	 */
	public Boolean getAllowSelfRegistration();

	/**
	 * Set the self registration flag.
	 * 
	 * @param allowSelfRegistration
	 */
	public void setAllowSelfRegistration(Boolean allowSelfRegistration);


	/**
	 * Get the location of a course section.
	 *
	 * @return the location of the course section.
	 */
	public String getLocation();

	/**
	 * Set the location of this course section.
	 *
	 * @param location
	 */
	public void setLocation(String location);

	/**
	 * Get the meeting time of a course section.
	 *
	 * @return the meeting time of the course section.
	 */
	public String getMeetingTime();

	/**
	 * Set the meeting time of this course section.
	 *
	 * @param meeting time
	 */
	public void setMeetingTime(String meetingTime);
   	
	/**
	 * Get the parent uuid of a course section.
	 *
	 * @return the parent uuid of the course section.
	 */
	public String getParentId();
	/**
	 * Set the parentId of this course section.
	 *
	 * @param parent uuid
	 */
	public void setParentId(String parentUuid);
	
	/**
	 * Get a set of uuids of child sections derived from this course section. Each of these sections is a real class with a location, teacher, students, etc.
	 * 
	 * @return Set of course section uuids.
	 */
	public Set getAllChildSections();

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
	 * Get a set of uuids of child sections of a given type, derived from this course section. Each of these sections is a real class with a location, teacher, students, etc.
	 * 
	 * @return Set of course section uuids.
	 */
	public Set getChildSectionsByType(CourseSectionType type);	
}
