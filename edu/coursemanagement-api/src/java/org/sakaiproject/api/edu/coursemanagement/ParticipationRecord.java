/**********************************************************************************
*
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
/*
 * Created on Jul 8, 2005
 *
 */
package org.sakaiproject.api.edu.coursemanagement;

import org.sakaiproject.api.common.manager.Persistable;

/**
 * 	A ParticipationRecord describes the participation of a person in a course, 
 * 	their role, and status in the course.
 *	<ul>
 *	<li>A reference to a person [Agent ID Object]
 *	<li>A role.  This role might not correspond to an authorization role.  [String]
 *	<li>A participation status.  [ParticipationStatusType]
 *	<li>A course reference. [CourseSection Id]
 *	</ul>
 * @author Mark Norton
 *
 */
public interface ParticipationRecord extends Persistable{

	/**
	 * Get the agent uuid of the participant.
	 * 
	 * @return entrolled agent uuid of a student.
	 */
	public String getAgent();
	
	/**
	 * Set the agent uuid of a participant.
	 * 
	 * @param agentUuid
	 */
	public void setAgent(String agentUuid);
	
	/**
	 * Get the role of the participant.
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
	 * Get the participation status.
	 * 
	 * @return enrollment status type.
	 */
	public ParticipationStatusType getParticipationStatus();
	
	/**
	 * Set the participation status for the participant.
	 * 
	 * @param type
	 */
	public void setParticipationStatus(ParticipationStatusType status);
	
	/**
	 * Get a course section uuid reference.  This provides
	 * reference to the section.
	 * 
	 * @return course section uuid.
	 */
	public String getCourseReference();
	
	/**
	 * Set the course section uuid.
	 * 
	 * @param courseSectionUuid
	 */
	public void setCourseReference (String courseSectionUuid);

	/**
	 * Return true if the agent is a leader in the course section
	 * 
	 * @return true if agent is leader
	 */
	public Boolean getIsLeader();
	  
	/**
	 * Set is leader flag.
	 * 
	 * @param isLeader
	 */
	public void setIsLeader(Boolean isLeader);

	/**
	 * Return true if the agent is the other in the course section
	 * 
	 * @return true if agent is other
	 */
	public Boolean getIsOtherPeople();
	    
	/**
	 * Set is other flag.
	 * 
	 * @param isOther
	 */
	public void setIsOtherPeople(Boolean isOtherPeople);

	/**
	 * Get the course offering uuid that this record belongs to.
	 * 
	 * @return course offering uuid.
	 */
	public String getCourseOffering();

	/**
	 * Set the course offering uuid id that this record belongs to.
	 * 
	 * @param courseOfferingUuid
	 */
	public void setCourseOffering(String courseOfferingUuid);

}
