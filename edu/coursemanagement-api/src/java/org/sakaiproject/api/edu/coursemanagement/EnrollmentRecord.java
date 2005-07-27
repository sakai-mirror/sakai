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

/**
 * An EnrollmentRecord describes the participation of a person in a course, their role, and their status in the course.
 * <ul>
 * <li>A reference to a person [Agent Id Object]
 * <li>A role. This role might not correspond to an authorization role. [String]
 * <li>A student enrollment status. [Enumerated Property]
 * <li>Credits [String]
 * <li>A course reference [CourseSection Id]
 * </ul>
 * 
 * @author Mark Norton
 */
public interface EnrollmentRecord
{

	/**
	 * Get the agent uuid of the student enrolled.
	 * 
	 * @return entrolled agent uuid of a student.
	 */
	public String getAgent();

	/**
	 * Set the agent uuid of an enrolled student.
	 * 
	 * @param agentUuid
	 */
	public void setAgent(String agentUuid);

	/**
	 * Get the role string.
	 * 
	 * @return
	 */
	public String getRole();

	/**
	 * Set the role string.
	 * 
	 * @param role
	 */
	public void setRole(String role);

	/**
	 * Get the enrollment status type. This might be full credit, partial credit, no credit, auditing, dropped, etc.
	 * 
	 * @return enrollment status type.
	 */
	public EnrollmentStatusType getStatusType();

	/**
	 * Set the enrollment status type for this student.
	 * 
	 * @param type
	 */
	public void setStatusType(EnrollmentStatusType status);

	/**
	 * Get the credits that can be earned by this student. The actual number of credits may be a funtion of the entrollment type.
	 * 
	 * @return credits
	 */
	public String getCredits();

	/**
	 * Set the number of credits this student can earn.
	 * 
	 * @param credits
	 */
	public void setCredits(String credits);

	/**
	 * Get a course section uuid reference. This provides reference to the section that the student is enrolled in.
	 * 
	 * @return course section uuid.
	 */
	public String getCourseReference();

	/**
	 * Set the course section uuid.
	 * 
	 * @param courseSectionUuid
	 */
	public void setCourseReference(String courseSectionUuid);

	/**
	 * Get a course offering uuid reference. This provides reference to the section that the student is enrolled in.
	 * 
	 * @return course offering uuid.
	 */
	public String getCourseOffering();

	/**
	 * Set the course offering uuid.
	 * 
	 * @param courseOfferingUuid
	 */
	public void setCourseOffering(String courseOfferingUuid);
}
