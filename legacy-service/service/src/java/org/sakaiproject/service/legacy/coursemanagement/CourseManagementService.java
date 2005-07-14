/**********************************************************************************
* $URL$
* $Id$
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

// package
package org.sakaiproject.service.legacy.coursemanagement;

import java.util.List;

import org.sakaiproject.exception.IdUnusedException;

/**
* <p>CourseManagemeentService provides ways to access and manupilate Term and Course objects. </p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface CourseManagementService
{	
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = CourseManagementService.class.getName();
	
	/** Security lock / event for updating any course. */
	public static final String SECURE_UPDATE_COURSE_ANY = "revise.course.any";
	
	/** Security lock / event for adding any term. */
	public static final String SECURE_ADD_TERM = "add.term";
	
	/** Security lock / event for removing any term. */
	public static final String SECURE_REMOVE_TERM = "remove.term";
	
	/**
	* Get the course object with the course provider id specified
	* @param the course provider id, the meaning of which is defined in the implementation
	* @return The Course object
	*/
	public Course getCourse(String courseId) throws IdUnusedException;
	
	/**
	* Get the list of CourseMember objects with the course provider id specified
	* @param the course provider id, the meaning of which is defined in the implementation
	* @return a list of CourseMember objects
	*/
	public List getCourseMembers(String courseId) throws IdUnusedException;
	
	/**
	* Get the course name by id
	* @param courseId The course Id
	* @return The course name
	*/
	public String getCourseName(String courseId) throws IdUnusedException;
	
	/**
	* Get all the course objects in specific term and with the user as the instructor
	* @param instructorId The id for the instructor
	* @param termYear The term year
	* @param termTerm The term term
	* @return The list of courses
	*/
	public List getInstructorCourses(String instructorId, String termYear, String termTerm);
	
	/**
	* Get the list of all know terms
	* @return The List of Term objects
	*/
	public List getTerms();
	
	/**
	* Get the term with the id
	* @return The Term object
	*/
	public Term getTerm(String termId);
	
}	// CourseManagementService



