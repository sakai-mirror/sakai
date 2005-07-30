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
 * Created on Mar 4, 2005
 *
 * (c) 2005 The Sakai Project
 *     All Rights Reserved.
 */
package org.sakaiproject.api.edu.coursemanagement;

import java.util.Date;
import java.util.List;

/**
 *	A CourseSet is a set of canonical courses that can be used to describe majors, departments 
 *	and other high level collections of canonical courses.  This information may be part of the 
 *	organizational hierarchy (SakaiSuperStructure), or it may cut across the hierarchy.  This 
 *	distinction is left as an implementation detail to be defined.
 *	<ul>
 *	<li>A title  [String]
 *	<li>A unique identifier  [Id Object]
 *	<li>A list of CanonicalCourse objects associated with this group.  [List of Canonical Course Id Objects]
 *      <li>Uuid of Agent who created this record [String]
 *      <li>Date & Time whne this record is created [Date]
 *      <li>Uuid of Agent who last modified this record [String]
 *      <li>Date & Time whne this record is last modified [Date]
 *	</ul>
 * @author Mark Norton
 *
 */
public interface CourseSet {
	
	public Long getCourseSetId();

	/**
	 * Get the title of this course set.
	 * 
	 * @return course title.
	 */
	public String getTitle();
	
	/**
	 * Set the title of this course set.
	 * 
	 * @param title
	 */
	public void setTitle (String title);
	
	/**
	 * Get the uuid of this course set.
	 * 
	 * @return course group uuid.
	 */
	public String getUuid();
	
	/**
	 * Get a list of canonical course uuids that are 
	 * contained in this set.
	 * 
	 * @return List of canonical course uuids.
	 */
	public List getCanonicalCourses();
	
	/**
	 * Add a canonical course uuid to this set.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void addCanonicalCourse (String canonicalCourseUuid);
	
	/**
	 * Remove a canonical course uuid from this set.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void removeCanonicalCourse (String canonicalCourseUuid);
	
        /**
         * Get the uuid of the Agent who has created this record.
         * @return uuid of the Agent
         */
	public String getCreatedBy();

	/**
	 * Set the uuid of the Agent who has created this record.
	 *
	 *	@param uuid of the Agent
	 */
	public void setCreatedBy(String createdBy);

       /**
        * Get the date when this record is created.
        * @return creation date
        */
	public Date getCreatedDate();

	/**
	 * Set the creation date.
	 *
	 *	@param creation date
	 */
	public void setCreatedDate(Date createdDate);

        /**
         * Get the uuid of the Agent who has last modified this record.
         * @return uuid of the Agent
        */
	public String getLastModifiedBy();

	/**
	 * Set the uuid of the Agent who has last modified this record.
	 *
	 *	@param uuid of the Agent
	 */
	public void setLastModifiedBy(String lastModifiedBy);

       /**
        * Get the date when this record is last modified.
        * @return last modified date
        */
	public Date getLastModifiedDate();

	/**
	 * Set the last modified date.
	 *
	 *	@param last modified date
	 */
	public void setLastModifiedDate(Date lastModifiedDate);

	
}
