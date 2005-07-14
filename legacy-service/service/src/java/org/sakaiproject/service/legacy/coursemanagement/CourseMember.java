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

/**
* <p>CourseMember contains member's registar information.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class CourseMember
{
	/** The member name */
	protected String m_name = null;
	
	/** The member uniqname */
	protected String m_uniqname = null;
	
	/** The member course registration id */
	protected String m_id = null;
	
	/** The member level */
	protected String m_level = null;
	
	/** The member credits */
	protected String m_credits = null;
	
	/** The member role */
	public String m_role = null;
	
	/** The member role from provider */
	public String m_providerRole = null;
	
	/** The course */
	public String m_course = null;
	
	/** The section */
	public String m_section = null;
	
	// constructor
	public CourseMember()
	{
		m_name = null;
		m_uniqname = null;
		m_id = null;
		m_level = null;
		m_credits = null;
		m_role = null;
		m_course = null;
		m_section = null;
	}
		
	/**
	 * get member name
	 * @return Member's name
	 */
	public String getName() 
	{
		return m_name; 
	
	}	// getName
	
	/**
	 * set member name
	 * @param name Member's name
	 */
	public void setName(String name) 
	{
		m_name = name; 
	
	}	// setName
	
	/** 
	 * get member's uniqname
	 * @return member's uniqname
	 */
	public String getUniqname() 
	{
		return m_uniqname; 
		
	}	// getUniqname
	
	/** 
	 * set member's uniqname
	 * @param uniqname member's uniqname
	 */
	public void setUniqname(String uniqname) 
	{
		m_uniqname = uniqname;
		
	}	// setUniqname
	
	/** 
	 * get member's course registration Id
	 * @return member's registration id
	 */
	public String getId() 
	{ 
		return m_id; 
		
	}	// getId
	
	/** 
	 * set member's course registration Id
	 * @param id member's registration id
	 */
	public void setId(String id) 
	{ 
		m_id = id; 
		
	}	// setId
	
	/**
	 * get member's level in course
	 * @return member's level in course
	 */
	public String getLevel() 
	{ 
		return m_level; 
		
	}	// getLevel
	
	/**
	 * set member's level in course
	 * @param level Member's level in course
	 */
	public void setLevel(String level) 
	{ 
		m_level = level; 
		
	}	// setLevel
	
	/**
	 * get member's credits in course
	 * @return member's credits in course
	 */
	public String getCredits() 
	{ 
		return m_credits; 
		
	}	// getCredits
	
	/**
	 * set member's credits in course
	 * @param credits Member's credits in course
	 */
	public void setCredits(String credits) 
	{ 
		m_credits = credits; 
		
	}	// setCredits
	
	
	/**
	 * get member's role in course
	 * @return member's role in course
	 */
	public String getRole() 
	{ 
		return m_role; 
	
	}	// getRole
	
	/**
	 * set member's role in course
	 * @param member's role in course
	 */
	public void setRole(String role) 
	{ 
		m_role = role; 
	
	}	// setRole
	
	/**
	 * get member's role in course from provider
	 * @return member's role in course from provider
	 */
	public String getProviderRole() 
	{ 
		return m_providerRole; 
	
	}	// getProviderRole
	
	/**
	 * set member's role in course from provider
	 * @param member's role in course from provider
	 */
	public void setProviderRole(String role) 
	{ 
		m_providerRole = role; 
	
	}	// setProviderRole
	
	/**
	 * get course that the member enrolled in
	 * @return Course name that the member enrolled in
	 */
	public String getCourse() 
	{ 
		return m_course; 
		
	}	// getCourse
	
	/**
	 * set course that the member enrolled in
	 * @param course Course name that the member enrolled in
	 */
	public void setCourse(String course) 
	{ 
		m_course = course; 
		
	}	// setCourse
	
	/**
	 * get section that the member enrolled in
	 * @return Section that the member enrolled in
	 */
	public String getSection() 
	{ 
		return m_section; 
		
	}	// getSection
	
	/**
	 * set section that the member enrolled in
	 * @param Section Section that the member enrolled in
	 */
	public void setSection(String section) 
	{ 
		m_section = section; 
		
	}	// getSection
	
}	// CourseMember



