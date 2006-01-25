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

package org.sakaiproject.service.legacy.coursemanagement.cover;

import java.util.List;
import org.sakaiproject.service.legacy.coursemanagement.Term;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>CourseManagementService is a static Cover for the {@link org.sakaiproject.service.legacy.coursemanagement CourseManageService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class CourseManagementService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.coursemanagement.CourseManagementService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.coursemanagement.CourseManagementService) ComponentManager.get(org.sakaiproject.service.legacy.coursemanagement.CourseManagementService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.coursemanagement.CourseManagementService) ComponentManager.get(org.sakaiproject.service.legacy.coursemanagement.CourseManagementService.class);
		}
	}
	private static org.sakaiproject.service.legacy.coursemanagement.CourseManagementService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.coursemanagement.CourseManagementService.SERVICE_NAME;

	public static java.util.List getCourseIdRequiredFields()
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourseIdRequiredFields();
	}
     
	public static java.util.List getCourseIdRequiredFieldsSizes()
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourseIdRequiredFieldsSizes();
	}
      
	public static String getCourseId(Term term, List requiredFields)
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourseId(term, requiredFields);
	}
     
	public static org.sakaiproject.service.legacy.coursemanagement.Course getCourse(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourse(param0);
	}
	
	public static java.util.List getCourseMembers(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourseMembers(param0);
	}
	
	public static java.lang.String getCourseName(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getCourseName(param0);
	}
	
	public static java.util.List getInstructorCourses(java.lang.String param0, java.lang.String param1, java.lang.String param2) 
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getInstructorCourses(param0, param1, param2);
	}
	
	public static java.util.List getTerms() 
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getTerms();
	}
	
	public static org.sakaiproject.service.legacy.coursemanagement.Term getTerm(java.lang.String param0) 
	{
		org.sakaiproject.service.legacy.coursemanagement.CourseManagementService service = getInstance();
		if (service == null)
			return null;

		return service.getTerm(param0);
	}

}



