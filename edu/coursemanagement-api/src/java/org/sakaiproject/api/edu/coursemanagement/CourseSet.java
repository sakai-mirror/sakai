/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.edu.coursemanagement;

import java.util.List;

/**
 * A CourseSet is a set of canonical courses that can be used to describe majors, departments and other high level collections of canonical courses. This information may be part of the organizational hierarchy (SakaiSuperStructure), or it may cut across
 * the hierarchy. This distinction is left as an implementation detail to be defined.
 * <ul>
 * <li>A title [String]
 * <li>A unique identifier [Id Object]
 * <li>A list of CanonicalCourse objects associated with this group. [List of Canonical Course Id Objects]
 * </ul>
 * 
 * @author Mark Norton
 */
public interface CourseSet
{

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
	public void setTile(String title);

	/**
	 * Get the uuid of this course set.
	 * 
	 * @return course group uuid.
	 */
	public String getUuid();

	/**
	 * Get a list of canonical course uuids that are contained in this set.
	 * 
	 * @return List of canonical course uuids.
	 */
	public List getCanonicalCourses();

	/**
	 * Add a canonical course uuid to this set.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void addCanonicalCourse(String canonicalCourseUuid);

	/**
	 * Remove a canonical course uuid from this set.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void removeCanonicalCourse(String canonicalCourseUuid);
}
