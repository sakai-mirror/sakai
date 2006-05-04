/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/coursemanagement/Course.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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

// import
import java.util.List;
import java.util.Vector;

/**
* <p>Course contains information about term, 
 * people and roles.  The section includes information about the location of
 * the class as well as the roster of students. </p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class Course
{
	/** The course id. */
	protected String m_id = null;

	/** The course title. */
	protected String m_title = null;
	
	/** The course subject. */
	protected String m_subject = null;
	
	/** The course term id. */
	protected String m_termId = null;
	
	/** The course member list. */
	protected List m_members = new Vector();
	
	/** Is cross listed? */
	protected String m_crossListed = null;
	
	/**
	* The course id 
	* @return a String which is defined inside implementation for uniquely identify a course
	*/
	public String getId()
	{
		return m_id;
	}

	/**
	* Get the title of the course
	* @return a String which is the title of a course
	*/
	public String getTitle()
	{
		return m_title;
	}
	
	/**
	* Get the subject of the course
	* @return a String which is the subject of a course
	*/
	public String getSubject()
	{
		return m_subject;
	}

	/**
	* Get the course cross listed attribute
	* @return a String for cross listing
	*/
	public String getCrossListed()
	{
		return m_crossListed;
	}
	
	/**
	* Get the list of members is class
	* @return The list of CourseMember
	*/
	public List getMembers()
	{
		return m_members;
	}

	/** 
	 * Get the term id associated with the course
	 * @return the term id
	 */
	public String getTermId()
	{
		return m_termId;
	}
	
	/**
	* Set the course's id.
	* Note: this is a special purpose routine that is used only to establish the id field,
	* when the id is null, and cannot be used to change a course's id.
	* @param name The course id.
	*/
	public void setId(String id)
	{
		m_id = id;
		
	}	// setId
	
	/**
	* Set the course title
	* @param title The title String
	*/
	public void setTitle(String title)
	{
		m_title = title;
		
	}	// setTitle
	
	/**
	* Set the course subject
	* @param title The subject String
	*/
	public void setSubject(String subject)
	{
		m_subject = subject;
		
	}	// setSubject
	
	/**
	* Set the course cross listed attribute
	* @param crossListed a String for cross listing
	*/
	public void setCrossListed(String crossListed)
	{
		m_crossListed = crossListed;
		
	}	// setCrossListed

	/**
	* Set the term id
	* @param termId The course term id string.
	*/
	public void setTermId(String termId)
	{
		m_termId = termId;
		
	}	// setTermId

	/**
	 * Set members
	 * @oaram members The member list
	 */
	public void setMembers(List members)
	{
		m_members.addAll(members);
		
	}	// setMembers
	
}	// Course

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/coursemanagement/Course.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
