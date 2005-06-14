/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/coursemanagement/UnivOfMichCourseManagementProvider.java,v 1.6 2005/01/21 19:45:11 zqian.umich.edu Exp $
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

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.coursemanagement.Course;
import org.sakaiproject.service.legacy.coursemanagement.CourseManagementProvider;
import org.sakaiproject.service.legacy.coursemanagement.CourseMember;
import org.sakaiproject.util.UmiacClient;

/**
* <p>UnivOfMichCourseManagementProvider is CourseManagementProvider that provides a course for any
* course known to the U of M UMIAC system.</p>
* <p>Note: The server running this code must be known the the UMIAC system to be able to make requests.</p>
* <p>Todo: %%% cache users?</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public class UnivOfMichCourseManagementProvider
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
		
		termIndex.put("SUMMER", "1");
		termIndex.put("FALL","2");
		termIndex.put("WINTER", "3");
		termIndex.put("SPRING", "4");
		termIndex.put("SPRING_SUMMER","5");

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

	/** My UMIAC client interface. */
	protected UmiacClient m_umiac = UmiacClient.getInstance();

	/**
	* @inheritDoc
	*/
	public String getCourseName(String courseId)
	{	
		String rv = "";
		// get the course name
		try
		{
			rv = m_umiac.getGroupName(courseId);
			if (rv!=null)
			{
				return rv;
			}
		}
		catch (IdUnusedException e)
		{
			
		}
		
		return "";
	}
	
	/**
	* @inheritDoc
	*/
	public Course getCourse(String courseId)
	{
		String[] fields = courseId.split(",");
		
		Course course = new Course();
		course.setId(courseId);
		course.setTitle(getCourseName(courseId));
		course.setSubject(fields[3]);
		
		//get course participant list
		//output as a Vector of String[] objects (one String[] per output line:
		//sort_name|uniqname|umid|level (always "-")|credits|role|enrl_status
		Vector plist = m_umiac.getClassList (fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
		Vector members = new Vector();
		for (int j= 0; j<plist.size(); j++)
		{
			String[] res = (String[]) plist.get(j);
			CourseMember m = new CourseMember();
			m.setName(res[0]);
			m.setUniqname((res[1]).toLowerCase());
			m.setId(res[2]);
			m.setLevel(res[3]);
			m.setCredits(res[4]);
			m.setRole(res[5]);
			members.add(m);
		}
		course.setMembers(members);
		
		return course;

	}	// getCourse
	
	/**
	* @inheritDoc
	*/
	public List getCourseMembers(String courseId)
	{	
		String[] fields = courseId.split(",");
		
		//get course participant list
		//output as a Vector of String[] objects (one String[] per output line:
		//sort_name|uniqname|umid|level (always "-")|credits|role|enrl_status
		Vector plist = m_umiac.getClassList (fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
		Vector members = new Vector();
		for (int j= 0; j<plist.size(); j++)
		{
			String[] res = (String[]) plist.get(j);
			CourseMember m = new CourseMember();
			m.setName(res[0]);
			m.setUniqname(res[1].toLowerCase());
			m.setId(res[2]);
			m.setLevel(res[3]);
			m.setCredits(res[4]);
			m.setRole(res[5]);
			m.setProviderRole(res[5]);
			m.setCourse(fields[3] + " " + fields[4]);
			m.setSection(fields[5]);
			members.add(m);
		}
		
		return members;

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
		Vector rv = new Vector();
		int i = 0;
		
		try
		{
			//getInstructorSections returns 12 strings: year, term_id, campus_code, 
			//subject, catalog_nbr, class_section, title, url, component, role, 
			//subrole, "CL" if cross-listed, blank if not
			Vector courses = m_umiac.getInstructorSections (instructorId, termYear, (String) termIndex.get(termTerm));
			
			int count = courses.size();
			for (i=0; i<courses.size(); i++)
			{
				String[] res = (String[]) courses.get(i);
				Course c = new Course();
				c.setId(res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "," + res[5]);
				c.setSubject(res[3]);
				c.setTitle(res[6]);
				c.setTermId(termTerm+"_"+termYear);
				//if course is crosslisted, a "CL" is added at the end
				if (res.length == 12)
				{
					c.setCrossListed("CL");
				}
				else
				{
					c.setCrossListed("");
				}
				
				//output as a Vector of String[] objects (one String[] per output line:
				//sort_name|uniqname|umid|level (always "-")|credits|role|enrl_status
				Vector plist = m_umiac.getClassList (res[0], res[1], res[2], res[3], res[4], res[5]);
				Vector members = new Vector();
				for (int j= 0; j<plist.size(); j++)
				{
					String[] res1 = (String[]) plist.get(j);
					
					CourseMember m = new CourseMember();
					m.setName(res1[0]);
					m.setUniqname(res1[1]);
					m.setId(res1[2]);
					m.setLevel(res1[3]);
					m.setCredits(res1[4]);
					m.setRole(res1[5]);
					members.add(m);
				}
				c.setMembers(members);
				rv.add(c);
			}
		}
		catch (Exception e)
		{
			//m_logger.info(this + " Cannot find any course in record for the instructor with id " + instructorId + ". ");
		}
		return rv;
		
	}	// getInstructorCourses

}	// UnivOfMichCourseManagementProvider

/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/coursemanagement/UnivOfMichCourseManagementProvider.java,v 1.6 2005/01/21 19:45:11 zqian.umich.edu Exp $
*
**********************************************************************************/
