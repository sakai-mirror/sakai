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
package org.sakaiproject.service.legacy.assignment;

// import
import java.util.List;

import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>AssignmentSubmission is the an interface for the CHEF assignments module.
* It represents student submissions for assignments.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface AssignmentSubmission
	extends Entity
{
	/**
	 * Access the context at the time of creation.
	 * @return String - the context string.
	 */
	public String getContext();

	/**
	 * Access the Assignment for this Submission
	 * @return the Assignment
	 */
	public Assignment getAssignment();

	/**
	 * Access the ID for the Assignment for this Submission
	 * @return String - the Assignment id
	 */
	public String getAssignmentId();

	/**
	 * Access the list of Users who submitted this response to the Assignment.
	 * @return Array of user objects.
	 */
	public User[] getSubmitters();

	/**
	 * Access the list of Users who submitted this response to the Assignment.
	 * @return List of user ids
	 */
	public List getSubmitterIds();

	/**
	 * Get whether this is a final submission.
	 * @return True if a final submission, false if still a draft.
	 */
	public boolean getSubmitted();

	/**
	 * Set the time at which this response was submitted;
	 * null signifies the response is unsubmitted.
	 * @return Time of submission.
	 */
	public Time getTimeSubmitted();

	/**
	 * Text submitted in response to the Assignment.
	 * @return The text of the submission.
	*/
	public String getSubmittedText();

	/**
	 * Access the list of attachments to this response to the Assignment.
	 * @return List of the list of attachments as Reference objects;
	 */
	public List getSubmittedAttachments();

	/**
	 * Get the general comments by the grader
	 * @return The text of the grader's comments; may be null.
	 */
	public String getFeedbackComment();

	/**
	 * Access the text part of the instructors feedback; usually an
	 * annotated copy of the submittedText
	 * @return The text of the grader's feedback.
	 */
	public String getFeedbackText();

	/**
	 * Access the list of attachments returned to the students
	 * in the process of grading this assignment; usually a modified
	 * or annotated version of the attachment submitted.
	 * @return List of the Resource objects pointing to the attachments.
	 */
	public List getFeedbackAttachments();

	/**
	 * Get whether this Submission was rejected by the grader.
	 * @return True if this response was rejected by the grader, false otherwise.
	*/
	public boolean getReturned();

	/**
	 * Get whether this Submission has been graded.
	 * @return True if the submission has been graded, false otherwise.
	 */
	public boolean getGraded();

	/**
	 * Get whether the grade has been released.
	 * @return True if the Submissions's grade has been released, false otherwise.
	 */
	public boolean getGradeReleased();

	/**
	 * Access the grade recieved.
	 * @return The Submission's grade..
	 */
	public String getGrade();
	
	/**
	 * Access the grade recieved.
	 * When points-type, format it to one decimal place
	 * @return The Submission's grade..
	 */
	public String getGradeDisplay();

	/**
	 * Get the time of last modification;
	 * @return The time of last modification.
	 */
	public Time getTimeLastModified();

	/**
	 * Get the time at which the graded submission was returned;
	 * null means the response is not yet graded.
	 * @return the time (may be null)
	 */
	public Time getTimeReturned();
	
	/**
	 * Access the checked status of the honor pledge flag.
	 * @return True if the honor pledge is checked, false otherwise.
	 */
	public boolean getHonorPledgeFlag();
	
	/**
	 * Returns the status of the submission : 
	 * Not Started, submitted, returned or graded.
	 * @return The Submission's status.
	 */
	public String getStatus();

}	// AssignmentSubmission



