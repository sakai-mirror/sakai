/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/coursemanagement/CourseManagementProvider.java,v 1.2 2004/12/22 19:52:33 zqian.umich.edu Exp $
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

// package
package org.sakaiproject.service.legacy.coursemanagement;

import java.util.List;
import org.sakaiproject.exception.IdUnusedException;

/**
* <p>CourseManagementProvider is the Interface for course management information providers.
* These are used by a course management service to access external course information.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.2 $
*/
public interface CourseManagementProvider
{
	/**
	 * Access a course object.  Update the object with the information found.
	 * @param String the course id
	 * @return The course object found
	 */
	Course getCourse(String courseId) throws IdUnusedException;
	
	/**
	 * Access the course members.
	 * @param String the course id
	 * @return The list of CourseMember objects
	 */
	List getCourseMembers(String courseId) throws IdUnusedException;
	
	/**
	* Get the course name by id
	* @param courseId The course Id
	* @return The course name
	*/
	String getCourseName(String courseId) throws IdUnusedException;
	
	/**
	* Get all the course objects in specific term and with the user as the instructor
	* @param instructorId The id for the instructor
	* @param termYear The term year
	* @param termTerm The term term
	* @return The list of courses
	*/
	public List getInstructorCourses(String instructorId, String termYear, String termTerm);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/coursemanagement/CourseManagementProvider.java,v 1.2 2004/12/22 19:52:33 zqian.umich.edu Exp $
*
**********************************************************************************/
