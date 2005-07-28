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
 * <li>A list of SakaiSuperStructure nodes to define a cross-listing set. [List of Nodes]
 * <li>Schedule of meeting dates/times and locations. [Schedule Id Object]
 * <li>A list of people designated as leaders. [List of ParticipationRecords]
 * <li>A list of enrolled students, each with their own enrollment record. [List of Enrollment Record Objects]
 * <li>A student enrollment status [Enumerated Property]
 * <li>A list of other people associated with this section. [List of ParticipationRecords]
 * <li>The course offering that this section is derived from. [CourseOffering Id]
 * <li>A list of section events [List of Event Ids]
 * <li>A flag to indicate created locally [Boolean, true if created locally]
 * <li>A flag to indicate this as a holding section [Boolean, true if a holding section]
 * <li>A flag to indicate students may self-register for this section [Boolean, true if self-registration is allowed]
 * <li>A Location [String]
 * <li>Meeting Time [String] 
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseSection
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
	 * Get the course section uuid.
	 * 
	 * @return course section uuid.
	 */
	public String getUuid();

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
	 * Get the course section status type. This type could be open, closed, or wait-listed.
	 * 
	 * @return course ection status type.
	 */
	public CourseSectionStatusType getSectionStatus();

	/**
	 * Set the course section status type.
	 * 
	 * @param type
	 */
	public void setSectionStatusType(CourseSectionStatusType type);

	/**
	 * Get the schedule for this course section. The schedule specified when the course meets, how often, what time, etc.
	 * 
	 * @return course section schedule
	 */
	public Schedule getSchedule ();

	/**
	 * Get the list of participation for the instructors of this course. While most sections will have only one instructor, provisions are made for multiple instructors.
	 * 
	 * @return List of agent uuids for instructors.
	 */
	public List getLeaders();

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
	 * Get the list of enrollment records for students enrolled in this section. Note that this list contains actual record, not uuids as is typical elsewhere.
	 * 
	 * @return List of student enrollment records.
	 */
	public List getEnrollmentRecords();

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
	 * Get the list of event associated with this course section. Event objects are TBD at this time.
	 * 
	 * @return
	 */
	public List getSectionEvents();

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
	public boolean getCreatedLocally();

	/**
	 * Set the created locally flag.
	 * 
	 * @param createdLocally
	 */
	public void setCreatedLocally(boolean createdLocally);

	/**
	 * Check to see if this is a holding section. True if this section is being used to hold students that will be re-assigned later.
	 * 
	 * @return
	 */
	public boolean getHoldingSection();

	/**
	 * Set the holding section flag.
	 * 
	 * @param holdingSection
	 */
	public void setHoldingSection(boolean holdingSection);

	/**
	 * Check to see if self-registration is allowed for this section. If true, then students may enroll or register themselves.
	 * 
	 * @return
	 */
	public boolean getAllowSelfRegistration();

	/**
	 * Set the self registration flag.
	 * 
	 * @param allowSelfRegistration
	 */
	public void setAllowSelfRegistration(boolean allowSelfRegistration);


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
         * Get the uuid of the Agent who has created this course section.
         * @return uuid of the Agent
         */
    	public String getCreatedBy();

    	/**
    	 * Set the uuid of the Agent who has created this course section.
    	 *
    	 *	@param uuid of the Agent
    	 */
    	public void setCreatedBy(String createdBy);

        /**
         * Get the date when this course section is created.
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
         * Get the uuid of the Agent who has last modified this course section.
         * @return uuid of the Agent
         */
    	public String getLastModifiedBy();

    	/**
    	 * Set the uuid of the Agent who has last modified this course section.
    	 *
    	 *	@param uuid of the Agent
    	 */
    	public void setLastModifiedBy(String lastModifiedBy);

        /**
         * Get the date when this course section is last modified.
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
