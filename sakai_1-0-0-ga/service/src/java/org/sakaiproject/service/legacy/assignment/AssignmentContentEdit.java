/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentContentEdit.java,v 1.5 2004/06/22 03:14:41 ggolden Exp $
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

import org.sakaiproject.service.legacy.resource.AttachmentContainerEdit;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>AssignmentContentEdit is the an interface for the CHEF assignments module.
* It represents the editable part of the assignment content that is "unchanging" for
* different versions of the assignment.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface AssignmentContentEdit
	extends AssignmentContent, AttachmentContainerEdit, Edit
{
	/**
	 * Set the title.
	 * @param title - The Assignment's title.
	 */
	public void setTitle(String title);

	/**
	 * Set the instructions for the Assignment.
	 * @param instructions - The Assignment's instructions.
	 */
	public void setInstructions(String instructions);
	
	/**
	 * Set the AssignmentContent's context at the time of creation.
	 * @param context - The context string.
	 */
	public void setContext(String context);

	/**
	 * Set the type of submission.
	 * @param subType - The type of submission.
	 */
	public void setTypeOfSubmission(int subType);
	
	/**
	 * Set the grade type.
	 * @param gradeType - The type of grade.
	 */
	public void setTypeOfGrade(int gradeType);

	/**
	 * Set the maximum grade for grade type = SCORE_GRADE_TYPE(3)
	 * @param maxPoints - The maximum grade score.
	 */
	public void setMaxGradePoint(int maxPoints);

	/**
	 * Set whether this project can be a group project.
	 * @param groupProject - True if this can be a group project, false otherwise.
	 */
	public void setGroupProject(boolean groupProject);

	/**
	 * Set whether group projects should be individually graded.
	 * @param individGraded - true if projects are individually graded, false
	 * if grades are given to the group.
	 */
	public void setIndividuallyGraded(boolean individGraded);

	/**
	 * Sets whether grades can be released once submissions are graded.
	 * @param release - true if grades can be released once submission are graded, false if
	 * they must be released manually.
	 */
	public void setReleaseGrades(boolean release);

	/**
	 * Set the Honor Pledge type;
	 * values are NONE and ENGINEERING_HONOR_PLEDGE.
	 * @param pledgeType - the Honor Pledge value.
	 */
	public void setHonorPledge(int pledgeType);

	/** 
	 * Does this Assignment allow attachments?
	 * @param allow - true if the Assignment allows attachments, false otherwise?
	 */
	public void setAllowAttachments(boolean allow);

	/**
	 * Add an author to the author list.
	 * @param author - The User to add to the author list.
	 */
	public void addAuthor(User author);
	
	/**
	 * Remove an author from the author list.
	 * @param author - the User to remove from the author list.
	 */
	public void removeAuthor(User author);
	
	/**
	 * Set the time last modified.
	 * @param lastmod - The Time at which the Content was last modified.
	 */
	public void setTimeLastModified(Time lastmod);
	
}	// AssignmentContentEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentContentEdit.java,v 1.5 2004/06/22 03:14:41 ggolden Exp $
*
**********************************************************************************/
