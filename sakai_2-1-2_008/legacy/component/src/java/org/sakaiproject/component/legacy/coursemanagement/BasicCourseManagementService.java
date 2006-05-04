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
package org.sakaiproject.component.legacy.coursemanagement;

// imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.coursemanagement.Course;
import org.sakaiproject.service.legacy.coursemanagement.CourseManagementProvider;
import org.sakaiproject.service.legacy.coursemanagement.CourseManagementService;
import org.sakaiproject.service.legacy.coursemanagement.Term;
import org.sakaiproject.service.legacy.time.cover.TimeService;

/**
* <p>BasicCourseManagementService is a course management services implemented as a Turbine Service.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class BasicCourseManagementService implements CourseManagementService
{	
	/** A course management provider. */
	protected CourseManagementProvider m_provider = null;
	
	/** The term information */
	protected List m_terms = null;

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

	/**
	 * Configuration: set the course management provider helper service.
	 * @param provider the user directory provider helper service.
	 */
	public void setProvider(CourseManagementProvider provider)
	{
		m_provider = provider;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// init
		m_terms = new Vector();
		List termTerms = new Vector();
		List termYears = new Vector();
		List termListAbbrs = new Vector();
		List termIsCurrent = new Vector();
		List termStartTimes = new Vector();
		List termEndTimes = new Vector();
		
		// get term information
		if (ServerConfigurationService.getStrings("termterm") != null)
		{
			termTerms = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termterm")));
		}
		if (ServerConfigurationService.getStrings("termyear") != null)
		{
			termYears = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termyear")));
		}
		if (ServerConfigurationService.getStrings("termlistabbr") != null)
		{
			termListAbbrs = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termlistabbr")));
		}
		if (ServerConfigurationService.getStrings("termiscurrent") != null)
		{
			termIsCurrent = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termiscurrent")));
		}
		if (ServerConfigurationService.getStrings("termstarttime") != null)
		{
			termStartTimes = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termstarttime")));
		}
		if (ServerConfigurationService.getStrings("termendtime") != null)
		{
			termEndTimes = new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("termendtime")));
		}
		
		for (int i=0; i<termTerms.size();i++)
		{
			String termTerm = (String) termTerms.get(i);
			String termYear = (String) termYears.get(i);
			Term term = new Term ();
			term.setId(termTerm + " " + termYear);
			term.setTerm(termTerm);
			term.setYear(termYear);
			if (i<termListAbbrs.size())
			{
				term.setListAbbreviation((String) termListAbbrs.get(i));
			}
			if (i<termIsCurrent.size())
			{
				String current = (String) termIsCurrent.get(i);
				if (current!=null && current.equalsIgnoreCase(Boolean.TRUE.toString()))
				{
					term.setIsCurrentTerm(true);
				}
				else
				{
					term.setIsCurrentTerm(false);
				}
			}
			if (i<termStartTimes.size())
			{
				term.setStartTime(TimeService.newTimeGmt((String) termStartTimes.get(i)));
			}
			if (i<termEndTimes.size())
			{
				term.setEndTime(TimeService.newTimeGmt((String) termEndTimes.get(i)));
			}
			
			m_terms.add(term);
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
		m_provider = null;

		m_logger.info(this +".destroy()");

	} // destroy

	/*******************************************************************************
	* CourseManagementService implementation
	*******************************************************************************/
	
	/**
	* @inheritDoc
	*/
	public List getCourseIdRequiredFields()
	{
		return m_provider.getCourseIdRequiredFields();
    	 	
	}	// getCourseIdRequriedFields
     
 	/**
	* @inheritDoc
	*/
	public List getCourseIdRequiredFieldsSizes()
	{
		return m_provider.getCourseIdRequiredFieldsSizes();
     	 	
	}	// getCourseIdRequiredFieldsSizes
     
	/**
	* @inheritDoc
	*/
	public String getCourseId(Term term, List requiredFields)
	{
		return m_provider.getCourseId(term, requiredFields);
    	 	
	}	// getCourseId
     
     
	/**
	* Access all terms
	* @return A course object containing the coure information
	* @exception IdUnusedException if not found
	*/
	public List getTerms()
	{
		return m_terms;
		
	}	// getTerms 
	
	/**
	* Access a term
	* @param termId The term id
	* @exception IdUnusedException if not found
	*/
	public Term getTerm(String termId)
	{
		Term rv = null;
		for (int i=0; i<m_terms.size() && rv == null; i++)
		{
			Term m = (Term) m_terms.get(i);
			if (m.getId().equals(termId))
			{
				rv = m;
			}
		}
		return rv;
		
	}	// getTerms 
	
	/**
	* Find the course object  (only - no provider check).
	* @param id The course id.
	* @return The course object found, or null if not found.
	*/
	protected Course findCourse(String courseId)
	{
		// use the cache
		Course m = null;
		
		return null;
		
	} // findCourse
	
	/**
	* @inheritDoc
	*/
	public Course getCourse(String id) throws IdUnusedException
	{
		if (id != null)
		{
			return m_provider.getCourse(id);
		}
		else
		{
			return new Course();
		}

	} // getCourse
	
	/**
	* @inheritDoc
	*/
	public List getCourseMembers(String id) throws IdUnusedException
	{
		if (id != null)
		{
			return m_provider.getCourseMembers(id);
		}
		else
		{
			return new Vector();
		}

	} // getCourseMembers
	
	/**
	* @inheritDoc
	*/
	public String getCourseName(String id) throws IdUnusedException
	{
		return m_provider.getCourseName(id);
		
	}	// getCourseName

	/**
	* @inheritDoc
	*/
	public List getInstructorCourses(String instructorId, String termYear, String termTerm)
	{
		// TODO: make this more efficient
		List rv = new Vector();
		
		rv = m_provider.getInstructorCourses(instructorId, termYear, termTerm);
		
		return rv;
	}
	
} // BasicCourseManagementService



