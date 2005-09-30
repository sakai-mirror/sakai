/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentContent.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.assignment;

// import
import java.util.List;

import org.sakaiproject.service.legacy.resource.AttachmentContainer;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>AssignmentContent is the an interface for the CHEF assignments module.
* It represents the part of the assignment content that is "unchanging" for
* different versions of the assignment.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface AssignmentContent
	extends Resource, AttachmentContainer
{
	/**
	 * Access the creator of this object.
	 * @return String - the user id of the creator.
	 */
	public String getCreator();
	
	/**
	 * Access the title.
	 * @return The AssignmentContent's title.
	 */
	public String getTitle();

	/**
	 * Access the context at the time of creation.
	 * @return String - the context string.
	 */
	public String getContext();

	/**
	 * Access the instructions for the assignment
	 * @return The Assignment's instructions.
	 */
	public String getInstructions();

	/**
	 * Access the time that this object was created.
	 * @return The Time object representing the time of creation.
	 */
	public Time getTimeCreated();

	/**
	 * Access the time of last modificaiton.
	 * @return The Time of last modification.
	 */
	public Time getTimeLastModified();

	/**
	 * Access the author of last modificaiton
	 * @return the User
	 */
	public String getAuthorLastModified();

	/**
	 * Access the type of submission.
	 * @return An integer representing the type of submission.
	 */
	public int getTypeOfSubmission();
	
	/**
	 * Access the grade type
	 * @return The integer representing the type of grade.
	 */
	public int getTypeOfGrade();

	/**
	 * Access a string describing the type of grade.
	 * @param gradeType - The integer representing the type of grade.
	 * @return Description of the type of grade.
	 */
	public String getTypeOfGradeString(int gradeType);
	
	/**
	 * Gets the maximum grade if grade type is SCORE_GRADE_TYPE(3)
	 * @return int The maximum grade score, or zero if the
	 * grade type is not SCORE_GRADE_TYPE(3).
	 */
	public int getMaxGradePoint();

	/**
	* Get the maximum grade for grade type = SCORE_GRADE_TYPE(3)
	* Formated to show one decimal place
	* @return The maximum grade score.
	*/
	public String getMaxGradePointDisplay();
			
	/**
	 * Get whether this project can be a group project.
	 * @return True if this can be a group project, false otherwise.
	 */
	public boolean getGroupProject();

	/**
	 * Access whether group projects should be individually graded.
	 * @return true if projects are individually graded, false
	 * if grades are given to the group.
	 */
	public boolean individuallyGraded();

	/**
	 * Access whether grades can be released once submissions are graded.
	 * @return True if grades can be released once submission are graded, false if
	 * they must be released manually.
	 */
	public boolean releaseGrades();

	/**
	 * Access the Honor Pledge type;
	 * values are NONE and ENGINEERING_HONOR_PLEDGE.
	 * @return  The type of pledge.
	 */
	public int getHonorPledge();

	/** 
	 * Access whether this AssignmentContent allows attachments.
	 * @return true if the AssignmentContent allows attachments, false otherwise.
	 */
	public boolean getAllowAttachments();

	/**
	 * Access the list of authors.
	 * @return List of the author's user-ids.
	 */
	public List getAuthors();

	/**
	 * Access whether this AssignmentContent is in use by an Assignment.
	 * @return boolean - Is this AssignmentContent used by an Assignment.
	 */
	public boolean inUse();

}	// AssignmentContent

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentContent.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
