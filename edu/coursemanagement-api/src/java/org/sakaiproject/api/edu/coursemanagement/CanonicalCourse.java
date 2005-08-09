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
 */
package org.sakaiproject.api.edu.coursemanagement;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.common.manager.Persistable;

/**
 * A Canonical Course is a general course that exists across terms.  It is an abstract 
 * course.  One way of thinking about canonical courses is that they are the set of all 
 * possible courses that could be offered at a university.  Not that all courses are not 
 * offered every term, which is why there is a course offering (see below).
 * 
 * This class provides access to a title, catalog description, course number, default
 * credits, a list of topics, a list of equivalent courses, and a list of prerequisites.
 * 
 * From a structural viewpoint, the class includes a canonical course type, a unique id, 
 * a parent canonical course, child canonical courses, and course offerings based on this 
 * course.
 * 
 * <ul>
 *	<li>A title  [String]
 *	<li>A catalog description [String]
 *	<li>A course number [String]
 *	<li>A canonical course status [Enumerated Property]
 *	<li>Default credits [String]
 *	<li>A Sakai SuperStructure node reference [Node]
 *	<li>A list of topics [List of Strings]
 *	<li>A set of equivalent canonical courses. [Set of Canonical Course Id Objects]
 *	<li>A set of prerequisites [Set of Strings]
 *	<li>A parent CanonicalCourse object.  [CanonicalCourse Id]
 *	<li>A set of CanonicalCourse children.  [Set of CanonicalCourse Ids]
 *	<li>A set of course offerings based on this canonical course.  [Set of Course Offering Ids]
 * </ul>
 *  * @author Mark Norton
 *
 */
public interface CanonicalCourse extends Persistable{
		
	/**
	 * Get the title of a canonical course as it might appear in a course catalog.
	 * 
	 * @return the tile of the canonical course.
	 */
	public String getTitle();
	
	/**
	 * Set the title of a canonical course as it might appear in a course catalog.
	 * @param title
	 */
	public void setTitle (String title);

	/**
	 * Get the description of this canonical course.
	 * 
	 * @return the desription of the canonical course.
	 */
	public String getDescription ();
	
	/**
	 * Set the description of this canonical course.
	 * 
	 * @param description
	 */
	public void setDescription (String description);
	
	/**
	 * Get the course number.  There are no restrictions on how
	 * this number is formatted.
	 * 
	 * @return Course number
	 */
	public String getCourseNumber ();
	
	/**
	 * Set the course number.  There are no restirctions on how
	 * this number is foramtted.
	 * 
	 * @param courseNumber
	 */
	public void setCourseNumber (String courseNumber);
	
	/**
	 * Get the course type of this canonical course.  Typically,
	 * this type will indicate if ths canonical course is available,
	 * discontinued, etc.
	 * @return
	 */
	public CanonicalCourseStatusType getCanonicalCourseStatus();
	
	/**
	 * Set the course type of this canonical course.
	 * @param type
	 */
	public void setCanonicalCourseStatus(CanonicalCourseStatusType type);
	
	/**
	 * Get the default credits for this course.  While there are no
	 * requirements on what this field contains, a numerical quantity 
	 * is likely expected.
	 * 
	 * @return the default credits.
	 */
	public String getDefaultCredits();
	
	/**
	 * Set the defalt credits.
	 *
	 *	@param credits
	 */
	public void setDefaultCredits(String defaultCredits);
	
	/**
	 * Get a list of topics associated with this course.  Each topic
	 * is a text string.  Order of topic strings is retained.
	 * 
	 * @return List of topic strings.
	 */
	public List getTopics();
	
	/**
	 * Add a topic string to the list of course topics.
	 * 
	 * @param topic
	 */
	public void addTopic(String topic);
	
	/**
	 * Remove a topic string from the list of course topics.  String must
	 * be identical to the one being removed.
	 * 
	 * @param topic
	 */
	public void removeTopic (String topic);
	
	/**
	 * Get a list of equivalent canonical courses uuids.  This is one way to
	 * represent cross listing in a set of courses.
	 * 
	 * @return List of canonical course uuids.
	 */
	public List getEquivalents ();
	
	/**
	 * Add the canonical course given by its uuid to the list of equivalent
	 * courses.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void addEquivalent (String canonicalCourseUuid);
	
	/**
	 * Remove the canonical course given by its uuid to the list of equivalent
	 * courses.
	 * 
	 * @param canonicalCourseUuid
	 */
	public void removeEquivalent (String canonicalCourseUuid);
	
	/**
	 * Get the set of prerequisites.
	 * 
	 * @return Set of prerequiste strings.
	 */
	public Set getPrerequisites ();
	
	/**
	 * Add a prerequisite string to the list of prerequisites.
	 * 
	 * @param prerequisite
	 */
	public void addPrerequisite (String prerequisite);
	
	/**
	 * Remove a prerequisite string to the list of prerequisites.  The string
	 * must be identical to the one being removed.
	 * 
	 * @param prerequisite
	 */
	public void removePrerequisite (String prerequisite);
	
	/**
	 * Get the uuid of the parent of this canonical course.  Null is returned if
	 * there is no partent of this course.
	 * 
	 * @return  Uuid of parent.
	 */
	public String getParentId ();
	
	/**
	 * Set the uuid parent of this course.
	 * 
	 * @param parentUuid
	 */
	public void setParentId (String parentUuid);
	
	/**
	 * Get a set of course offering uuids derived from this canonical course.
	 * 
	 * @return Set of course offering uuids.
	 */
	public Set getOfferings();
	
	/**
	 * Add a course offering uuid as derived from this canonical course.
	 * 
	 * @param offeringUuid
	 */
	public void addOffering (String offeringUuid);
	
	/**
	 * Remove a course offering from the list in this canonical course.  Uuid must
	 * be identical to the one to be removed.
	 * 
	 * @param offeringUuid
	 */
	public void removeOffering (String offeringUuid);
	
}
