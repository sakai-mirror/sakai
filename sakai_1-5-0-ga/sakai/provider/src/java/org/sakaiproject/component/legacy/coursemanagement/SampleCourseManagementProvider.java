/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/coursemanagement/SampleCourseManagementProvider.java,v 1.3 2004/12/22 19:54:14 zqian.umich.edu Exp $
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
package org.sakaiproject.component.legacy.coursemanagement;

// imports
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.coursemanagement.Course;
import org.sakaiproject.service.legacy.coursemanagement.CourseManagementProvider;

/**
* <p>Sample of course management provider. </p>
* <p>Todo: %%% to be implemented; read course info from some config file. </p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.3 $
*/
public class SampleCourseManagementProvider
	implements CourseManagementProvider
{
	private Hashtable termIndex = new Hashtable();
	
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: CurrentService */
	protected CurrentService m_currentService = null;

	/**
	 * Dependency: CurrentService.
	 * @param service The CurrentService.
	 */
	public void setCurrentService(CurrentService service)
	{
		m_currentService = service;
	}

	/** Dependency: ServerConfigurationService*/
	protected ServerConfigurationService m_configService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * @param service The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_configService = service;
	}

	/** Configuration: kerberos or cosign. */
	protected boolean m_useKerberos = true;

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*
	* You can use this method to release resources thet your Service
	* allocated when Turbine shuts down.
	*/
	public void destroy()
	{
		m_logger.info(this +".destroy()");

	} // destroy

	/*******************************************************************************
	* CourseManagementProvider implementation
	*******************************************************************************/

	/**
	* @inheritDoc
	*/
	public String getCourseName(String courseId)
	{	
		return "";
	}
	
	/**
	* Access a course object.  Update the object with the information found.
	* @param edit The user object (id is set) to fill in.
	* @return true if the user object was found and information updated, false if not.
	*/
	public Course getCourse(String courseId)
	{
		return null;

	}	// getCourse
	
	/**
	* Access a course object.  Update the object with the information found.
	* @param edit The user object (id is set) to fill in.
	* @return true if the user object was found and information updated, false if not.
	*/
	public List getCourseMembers(String courseId)
	{
		return new Vector();

	}	// getCourseMembers
	
	/**
	* Get all the course objects in specific term and with the user as the instructor
	* @param instructorId The id for the instructor
	* @param termYear The term year
	* @param termTerm The term term
	* @return The list of courses
	*/
	public List getInstructorCourses(String instructorId, String termYear, String termTerm)
	{
		return new Vector();
		
	}	// getInstructorCourses

}	// SampleCourseManagementProvider

/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/coursemanagement/SampleCourseManagementProvider.java,v 1.3 2004/12/22 19:54:14 zqian.umich.edu Exp $
*
**********************************************************************************/
