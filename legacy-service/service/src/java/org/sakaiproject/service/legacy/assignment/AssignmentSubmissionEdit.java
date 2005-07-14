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
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>AssignmentSubmissionEdit is an interface for the CHEF assignments module.
* It represents editable student submissions for assignments.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface AssignmentSubmissionEdit
	extends AssignmentSubmission, Edit
{
	/**
	 * Set the AssignmentSubmissions's context at the time of creation.
	 * @param context - The context string.
	 */
	public void setContext(String context);

	/**
	 * Set the Assignment for this Submission
	 * @param assignment - the Assignment
	 */
	public void setAssignment(Assignment assignment);
	
	/**
	 * Add a User to the submitters list.
	 * @param submitter - the User to add.
	 */
	public void addSubmitter(User submitter);
	
	/**
	 * Remove an User from the submitter list
	 * @param submitter - the User to remove.
	 */
	public void removeSubmitter(User submitter);
	
	/**
	 * Remove all user from the submitter list
	 */
	public void clearSubmitters();

	/**
	 * Set whether this is a final submission.
	 * @param submitted - True if a final submission, false if still a draft.
	 */
	public void setSubmitted(boolean submitted);

	/**
	 * Set the time at which this response was submitted;
	 * setting it to null signifies the response is unsubmitted.
	 * @param timeSubmitted - Time of submission.
	 */
	public void setTimeSubmitted(Time timeSubmitted);

	/**
	 * Text submitted in response to the Assignment.
	 * @param submissionText - The text of the submission.
	 */
	public void setSubmittedText(String submissionText);

	/**
	 * Add an attachment to the list of submitted attachments.
	 * @param attachment - The Reference object pointing to the attachment.
	 */
	public void addSubmittedAttachment(Reference attachment);

	/**
	 * Remove an attachment from the list of submitted attachments
	 * @param attachment - The Reference object pointing to the attachment.
	 */
	public void removeSubmittedAttachment(Reference attachment);

	/**
	 * Remove all submitted attachments.
	 */
	public void clearSubmittedAttachments();

	/**
	 * Set the general comments by the grader.
	 * @param comment - the text of the grader's comments; may be null.
	 */
	public void setFeedbackComment(String comment);

	/**
	 * Set the text part of the instructors feedback; usually an
	 * annotated copy of the submittedText
	 * @param feedback - The text of the grader's feedback.
	 */
	public void setFeedbackText(String feedback);

	/**
	 * Add an attachment to the list of feedback attachments.
	 * @param attachment - The Resource object pointing to the attachment.
	 */
	public void addFeedbackAttachment(Reference attachment);

	/**
	 * Remove an attachment from the list of feedback attachments.
	 * @param attachment - The Resource pointing to the attachment to remove.
	 */
	public void removeFeedbackAttachment(Reference attachment);

	/**
	 * Remove all feedback attachments.
	 */
	public void clearFeedbackAttachments();

	/**
	 * Set whether this Submission was rejected by the grader.
	 * @param returned - true if this response was rejected by the grader, false otherwise.
	 */
	public void setReturned(boolean returned);

	/**
	 * Set whether this Submission has been graded.
	 * @param graded - true if the submission has been graded, false otherwise.
	 */
	public void setGraded(boolean graded);
	
	/**
	 * Set whether the grade has been released.
	 * @param released - True if the Submissions's grade has been released, false otherwise.
	 */
	public void setGradeReleased(boolean released);

	/**
	 * Sets the grade for the Submisssion.
	 * @param grade - The Submission's grade.
	 */
	public void setGrade(String grade);

	/**
	 * Set the time at which the graded Submission was returned;
	 * setting it to null means it is not yet graded.
	 * @param timeReturned - The time at which the graded Submission was returned.
	 */
	public void setTimeReturned(Time timeReturned);

	/**
	 * Set the checked status of the honor pledge flag.
	 * @param honorPledgeFlag - True if the honor pledge is checked, false otherwise.
	 */
	public void setHonorPledgeFlag(boolean honorPledgeFlag);
	
	/**
	 * Set the time last modified.
	 * @param lastmod - The Time at which the Submission was last modified.
	 */
	public void setTimeLastModified(Time lastmod);

}	// AssignmentSubmissionEdit



