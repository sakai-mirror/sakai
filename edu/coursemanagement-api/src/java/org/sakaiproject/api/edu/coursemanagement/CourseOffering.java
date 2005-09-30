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
 * A Course Offering can occur in a specific term and have a grade type, status type, etc. A course offering is a canonical course associated with a specific term. It may be concrete where the canonical is abstract. The roster associated with a course
 * offering allows sections to be aggregated.
 * <ul>
 * <li>A title [String]
 * <li>A description (subject) [String]
 * <li>An offering number [String]
 * <li>A session reference [Session Id Object]
 * <li>Maximum number of students allowed [Integer]
 * <li>An offering type [Type Object]
 * <li>An offering status type [Enumerated Property]
 * <li>A set of people designated as the default leaders. [Set of ParticipationObjects]
 * <li>A set of other people associated with this course. [Set of ParticipationRecords]
 * <li>A set of all enrolled students [Set of EnrollmentRecords]
 * <li>An aggregated set of enrolled students by course section type. [Set of EnrollmentRecords]
 * <li>An enrollment type [Enumerated Property]
 * <li>The canonical course that this offering represents. [CanonicalCourse Id]
 * <li>A set of course sections derived from this offering. [Set of CourseSection Ids]
 * <li>A default Location [String]
 * <li>A default Meeting Time [String] 
 * <li>A default Schedule [String] 
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseOffering extends Persistable
{
	/**
	 * Get the title of a course offering as it might 
	 * appear in a course catalog.
	 * 
	 * @return the title of the course offering.
	 */
	public String getTitle();

	/**
	 * Set the title of a course offering as it might 
	 * appear in a course catalog.
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
	 * Get the course number. There are no restrictions on 
	 * how this number is formatted.
	 * 
	 * @return offeringNnumber
	 */
	public String getOfferingNumber();

	/**
	 * Set the offering number. There are no restirctions on 
	 * how this number is formatted.
	 * 
	 * @param offeringNumber
	 */
	public void setOfferingNumber(String offeringNumber);

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
	 * Return true if this course offering is cross listed. 
	 * A course is cross listed if the set of equivalent 
	 * courses is non-empty.
	 * 
	 * @return true if cross listed.
	 */
	public Boolean isCrossListed();
	
    
	/**
	 * Set cross listing flag.
	 * 
	 * @param isCrossListed
	 */
	public void setIsCrossListed(Boolean isCrossListed);

	/**
	 * Get a set of equivalent course offering uuids. This is 
	 * one way to represent cross seting in a set of courses.
	 * 
	 * @return Set of canonical course uuids.
	 */
	public Set getEquivalents();

	/**
	 * Add the course offering given by its uuid to the set of 
	 * equivalent courses.
	 * 
	 * @param courseOfferingUuid
	 */
	public void addEquivalent(String courseOfferingUuid);

	/**
	 * Remove the course offering given by its uuid from the set 
	 * of equivalent courses.
	 * 
	 * @param courseOfferingUuid
	 */
	public void removeEquivalent(String courseOfferingUuid);

	/**
	 * Get the maxiumum number of students permitted to enroll 
	 * in this course offering.
	 * 
	 * @return maxiumum number of students allowed
	 */
	public Integer getMaximumStudents();

	/**
	 * Set the maximum number of students permitted to entroll 
	 * in this course offering.
	 * 
	 * @param maxStudents
	 */
	public void setMaximumStudents(Integer maxStudents);

	/**
	 * Get the course offering type. This type indicates what 
	 * kind of offering is being made.
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
	 * Get the course ofering status. The course offering status 
	 * might be open, closed, wait-seted, etc.
	 * 
	 * @return status
	 */
	public CourseOfferingStatusType getOfferingStatus();

	/**
	 * Set the course offering status type.
	 * 
	 * @param status
	 */
	public void setOfferingStatus(CourseOfferingStatusType status);

	/**
	 * Get the set of course sections that represent this offering.
	 * 
	 * @return Set of course sections
	 */
	public Set getCourseSections();

	/*
	 * Add a course section that represents this offering.
	 * 
	 * @param sectionUuid
	 */
	//public void addCourseSection(String sectionUuid);

	/**
	 * Remove a course section from this offering set.
	 * 
	 * @param sectionUuid
	 */
	public void removeCourseSection(String sectionUuid);

	/**
	 * Get the set of participation for the instructors of 
	 * a given course section type (e.g. lecture, lab, 
	 * seminar) in this course. While most sections will 
	 * have only one instructor, provisions are made for 
	 * multiple instructors.
	 * 
	 * @return defaultLeaderSet
	 */
	public Set getDefaultLeaders();
	
	/**
	 * Add a participation record as an instructor of this section.
	 * 
	 * @param participationRecord
	 */	
	public void addDefaultLeader(ParticipationRecord participationRecord);

	/**
	 * Remove a leader given an agent uuid. The participation 
	 * record containing this uuid will be removed.
	 * 
	 * @param agentUuid
	 */
	public void removeDefaultLeader(String agentUuid);

	/**
	 * Get a set of other people associated with this course offering. 
	 * This set might include teaching assistance, lab supervisors, 
	 * translators, etc. The set elements are participation uuids.
	 * 
	 * @return otherPeopleSet
	 */
	public Set getOtherPeople();

	/**
	 * Add a person to the other set.
	 * 
	 * @param participantRecord
	 */
	public void addOtherPerson(ParticipationRecord participantRecord);

	/**
	 * Remove a person from the other set given an agent uuid. 
	 * The participation record associated with this uuid will 
	 * be removed from the set of other people.
	 * 
	 * @param participantUuid
	 */
	public void removeOtherPerson(String participantUuid);

	/**
	 * Get an aggregated set of all students enrolled of a 
	 * given course section type (e.g. lecture, lab, seminar) 
	 * in this course. This is a set of Enrollment records 
	 * drawn from sections under this offering. Aggregated 
	 * enrollment is a simple way to find all people enrolled 
	 * in any section based on this course
	 * offering.
	 * 
	 * @return Set of enrollment records.
	 */	
	public Set getAllEnrollments();

	/**
	 * Get an aggregated set of all students enrolled in this 
	 * course. This is a set of Enrollment records drawn from 
	 * sections under this offering. Aggregated enrollment is 
	 * a simple way to find all people enrolled in any section 
	 * based on this course offering.
	 * 
	 * @return aggregated set of enrollment records.
	 */
	public Set getAggregatedEnrollments(CourseSectionType type);


	/**
	 * Get the entrollment type for this course. Since a course 
	 * offering enrollment is always an aggregate, this will 
	 * always return an EnrollmentType of aggregate.
	 * 
	 * @return entrollment type.
	 */
	public EnrollmentType getEnrollmentType();

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
	 * Get a set of uuids of sections of a given course section type, 
	 * derived from this course offering. Each of these sections is 
	 * a real class with a location, teacher, students, etc.
	 * 
	 * @return Set of course section uuids.
	 */
	public Set getSectionsByType(CourseSectionType type);

	/**
	 * Get the location of a course section.
	 *
	 * @return the default location of the course section.
	 */
	public String getDefaultLocation();

	/**
	 * Set the default location of this course section.
	 *
	 * @param location
	 */
	public void setDefaultLocation(String location);

	/**
	 * Get the default meeting time of a course section.
	 *
	 * @return the meeting time of the course section.
	 */
	public String getDefaultMeetingTime();

	/**
	 * Set the default meeting time of this course section.
	 *
	 * @param meeting time
	 */
	public void setDefaultMeetingTime(String meetingTime);
   	
	/**
	 * Get the default schedule of a course section.
	 *
	 * @return the schedule of the course section.
	 */
	public String getDefaultSchedule();

	/**
	 * Set the default schedule of this course section.
	 *
	 * @param schedule
	 */
	public void setDefaultSchedule(String schedule);

}
