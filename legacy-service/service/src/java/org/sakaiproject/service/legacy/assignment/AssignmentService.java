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
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceService;
import org.sakaiproject.service.legacy.user.User;
import org.w3c.dom.Element;

/**
* <p>AssignmentService is the service that handles assignments.</p>
* <p>Each Assignment has an associated AssignmentContent (an AssignmentContent can
* belong to more the one Assignment) and a list of AssignmentSubmission
* (the student responses to the Assignment).</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.Assignment
* @see org.chefproject.core.AssignmentContent
* @see org.chefproject.core.AssignmentSubmission
* @see org.chefproject.core.AssignmentEdit
* @see org.chefproject.core.AssignmentContentEdit
* @see org.chefproject.core.AssignmentSubmissionEdit
*/
public interface AssignmentService
	extends ResourceService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = AssignmentService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "assignment";

	/** Security lock for adding an assignment. */
	public static final String SECURE_ADD_ASSIGNMENT = "asn.new";

	/** Security lock for adding an assignment. */
	public static final String SECURE_ADD_ASSIGNMENT_CONTENT = "asn.new";

	/** Security lock for adding an assignment submission. */
	public static final String SECURE_ADD_ASSIGNMENT_SUBMISSION = "asn.submit";

	/** Security lock for removing an assignment. */
	public static final String SECURE_REMOVE_ASSIGNMENT = "asn.delete";

	/** Security lock for removing an assignment content. */
	public static final String SECURE_REMOVE_ASSIGNMENT_CONTENT = "asn.delete";

	/** Security lock for removing an assignment submission. */
	public static final String SECURE_REMOVE_ASSIGNMENT_SUBMISSION = "asn.delete";

	/** Security lock for accessing an assignment. */
	public static final String SECURE_ACCESS_ASSIGNMENT = "asn.read";

	/** Security lock for accessing an assignment content. */
	public static final String SECURE_ACCESS_ASSIGNMENT_CONTENT = "asn.read";
	
	/** Security lock for accessing an assignment submission. */
	public static final String SECURE_ACCESS_ASSIGNMENT_SUBMISSION = "asn.submit";

	/** Security lock for updating an assignment. */
	public static final String SECURE_UPDATE_ASSIGNMENT = "asn.revise";

	/** Security lock for updating an assignment content. */
	public static final String SECURE_UPDATE_ASSIGNMENT_CONTENT = "asn.revise";

	/** Security lock for updating an assignment submission. */
	public static final String SECURE_UPDATE_ASSIGNMENT_SUBMISSION = "asn.submit";
	
	/** Security lock for grading submission */
	public static final String SECURE_GRADE_ASSIGNMENT_SUBMISSION = "asn.grade";

	/** The Reference type for an assignment. */
	public static final String REF_TYPE_ASSIGNMENT = "a";

	/** The Reference type for a submission. */
	public static final String REF_TYPE_SUBMISSION = "s";

	/** The Reference type for a content. */
	public static final String REF_TYPE_CONTENT = "c";

	/** The Reference type for a grade spreadsheet. */
	public static final String REF_TYPE_GRADES = "grades";

	/** The Reference type for a submissions zip. */
	public static final String REF_TYPE_SUBMISSIONS = "submissions";	

	/**
	 * Check permissions for adding an Assignment.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an Assignment, false if not.
	 */
	public boolean allowAddAssignment(String context);

	/**
	 * Check permissions for reading an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to get the Assignment, false if not.
	 */
	public boolean allowGetAssignment(String assignmentReference);

	/**
	 * Check permissions for updating an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to update the Assignment, false if not.
	 */
	public boolean allowUpdateAssignment(String assignmentReference);

	/**
	 * Check permissions for removing an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to remove the Assignment, false if not.
	 */
	public boolean allowRemoveAssignment(String assignmentReference);

	/**
	 * Check permissions for adding an AssignmentContent.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an AssignmentContent, false if not.
	 */
	public boolean allowAddAssignmentContent(String context);

	/**
	 * Check permissions for get AssignmentContent
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to get the AssignmentContent, false if not.
	 */
	public boolean allowGetAssignmentContent(String contentReference);

	/**
	 * Check permissions for updating AssignmentContent
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to update the AssignmentContent, false if not.
	 */
	public boolean allowUpdateAssignmentContent(String contentReference);

	/**
	 * Check permissions for remove Assignment content
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to remove the AssignmentContent, false if not.
	 */
	public boolean allowRemoveAssignmentContent(String contentReference);

	/**
	 * Check permissions for add AssignmentSubmission
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an AssignmentSubmission, false if not.
	 */
	public boolean allowAddSubmission(String context);

	/**
	 * Get the List of Users who can addSubmission() for this assignment.
	 * @param assignmentReference - a reference to an assignment
	 * @return the List (User) of users who can addSubmission() for this assignment.
	 */
	public List allowAddSubmissionUsers(String assignmentReference);

	/**
	 * Check permissions for reading a Submission.
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to get the AssignmentSubmission, false if not.
	 */
	public boolean allowGetSubmission(String submissionReference);

	/**
	 * Check permissions for updating Submission.
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to update the AssignmentSubmission, false if not.
	 */
	public boolean allowUpdateSubmission(String submissionReference);

	/**
	 * Check permissions for remove Submission
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to remove the AssignmentSubmission, false if not.
	 */
	public boolean allowRemoveSubmission(String submissionReference);
	
	/**
	 * Check permissions for grading Submission
	 * 
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to grade the AssignmentSubmission, false if not.
	 */
	public boolean allowGradeSubmission(String submissionReference);
	
	/** 
	 * Creates and adds a new Assignment to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return AssignmentEdit The new Assignment object.
	 * @throws IdInvalidException if the id contains prohibited characers.
	 * @throws IdUsedException if the id is already used in the service.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentEdit addAssignment(String context)
		throws PermissionException;

	/**
	* Add a new assignment to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the assignment.
	* @return A locked AssignmentEdit object (reserving the id).
	* @exception IdInvalidException if the assignment id is invalid.
	* @exception IdUsedException if the assignment id is already used.
	* @exception PermissionException if the current user does not have permission to add an assignnment.
	*/
	public AssignmentEdit mergeAssignment(Element el)
		throws IdInvalidException, IdUsedException, PermissionException;

	/** 
	 * Creates and adds a new Assignment to the service
	 * which is a copy of an existing Assignment.
	 * @param context - From DefaultId.getChannel(RunData)
	 * @param assignmentReference - The reference of the Assignment to be duplicated.
	 * @return The new AssignmentEdit object, or null if the original Assignment does not exist.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentEdit addDuplicateAssignment(String context, String assignmentReference)
		throws IdInvalidException, PermissionException, IdUsedException, IdUnusedException;

	/** 
	 * Removes this Assignment and all references to it.
	 * @param assignment - The Assignment to remove.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeAssignment(AssignmentEdit assignment)
		throws PermissionException;

	/**
	* Get a locked assignment object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The assignment id string.
	* @return A AssignmentEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentEdit object
	* @exception PermissionException if the current user does not have permission to edit this assignment.
	* @exception InUseException if the Assignment object is locked by someone else.
	*/
	public AssignmentEdit editAssignment(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a AssignmentEdit object, and release the lock.
	* The AssignmentEdit is disabled, and not to be used after this call.
	* @param assignment The AssignmentEdit object to commit.
	*/
	public void commitEdit(AssignmentEdit assignment);

	/**
	* Cancel the changes made to a AssignmentEdit object, and release the lock.
	* The AssignmentEdit is disabled, and not to be used after this call.
	* @param assignment The AssignmentEdit object to commit.
	*/
	public void cancelEdit(AssignmentEdit assignment);

	/** 
	 * Creates and adds a new AssignmentContent to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return AssignmentContentEdit The new AssignmentContent object.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentContentEdit addAssignmentContent(String context)
		throws PermissionException;

	/**
	* Add a new AssignmentContent to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the AssignmentContent.
	* @return A locked AssignmentContentEdit object (reserving the id).
	* @exception IdInvalidException if the AssignmentContent id is invalid.
	* @exception IdUsedException if the AssignmentContent id is already used.
	* @exception PermissionException if the current user does not have permission to add an AssignnmentContent.
	*/
	public AssignmentContentEdit mergeAssignmentContent(Element el)
		throws IdInvalidException, IdUsedException, PermissionException;

	/** 
	 * Creates and adds a new AssignmentContent to the service
	 * which is a copy of an existing AssignmentContent.
	 * @param context - From DefaultId.getChannel(RunData)
	 * @param contentId - The id of the AssignmentContent to be duplicated.
	 * @return AssignmentContentEdit The new AssignmentContentEdit object, or null if the original does not exist.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentContentEdit addDuplicateAssignmentContent(String context, String contentReference)
		throws IdInvalidException, PermissionException, IdUnusedException;

	/** 
	 * Removes an AssignmentContent
	 * @param content - the AssignmentContent to remove.
	 * @throws an AssignmentContentNotEmptyException if this content still has related Assignments.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeAssignmentContent(AssignmentContentEdit content)
		throws AssignmentContentNotEmptyException, PermissionException;

	/**
	* Get a locked AssignmentContent object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The content id string.
	* @return An AssignmentContentEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentContentEdit object
	* @exception PermissionException if the current user does not have permission to edit this content.
	* @exception InUseException if the AssignmentContent object is locked by someone else.
	*/
	public AssignmentContentEdit editAssignmentContent(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a AssignmentContentEdit object, and release the lock.
	* The AssignmentContentEdit is disabled, and not to be used after this call.
	* @param content The AssignmentContentEdit object to commit.
	*/
	public void commitEdit(AssignmentContentEdit content);

	/**
	* Cancel the changes made to a AssignmentContentEdit object, and release the lock.
	* The AssignmentContentEdit is disabled, and not to be used after this call.
	* @param content The AssignmentContentEdit object to commit.
	*/
	public void cancelEdit(AssignmentContentEdit content);

	/** 
	 * Adds an AssignmentSubmission to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return The new AssignmentSubmissionEdit.
	 * @exception IdInvalidException if the submission id is invalid.
	 * @exception IdUsedException if the submission id is already used.
	 * @throws PermissionException if the current User does not have permission to do this.
	 */
	public AssignmentSubmissionEdit addSubmission(String context, String assignmentId)
		throws PermissionException;

	/**
	* Add a new AssignmentSubmission to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the submission.
	* @return A locked AssignmentSubmissionEdit object (reserving the id).
	* @exception IdInvalidException if the submission id is invalid.
	* @exception IdUsedException if the submission id is already used.
	* @exception PermissionException if the current user does not have permission to add a submission.
	*/
	public AssignmentSubmissionEdit mergeSubmission(Element el)
		throws IdInvalidException, IdUsedException, PermissionException;

	/** 
	 * Removes an AssignmentSubmission and all references to it
	 * @param submission - the AssignmentSubmission to remove.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeSubmission(AssignmentSubmissionEdit submission)
		throws PermissionException;

	/**
	* Get a locked AssignmentSubmission object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The submission id string.
	* @return An AssignmentSubmissionEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentSubmissionEdit object
	* @exception PermissionException if the current user does not have permission to edit this submission.
	* @exception InUseException if the AssignmentSubmission object is locked by someone else.
	*/
	public AssignmentSubmissionEdit editSubmission(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a AssignmentSubmissionEdit object, and release the lock.
	* The AssignmentSubmissionEdit is disabled, and not to be used after this call.
	* @param submission The AssignmentSubmissionEdit object to commit.
	*/
	public void commitEdit(AssignmentSubmissionEdit submission);

	/**
	* Cancel the changes made to a AssignmentSubmissionEdit object, and release the lock.
	* The AssignmentSubmissionEdit is disabled, and not to be used after this call.
	* @param submission The AssignmentSubmissionEdit object to commit.
	*/
	public void cancelEdit(AssignmentSubmissionEdit submission);

	/**
	 * Access list of all AssignmentContents created by the User.
	 * @param owner - The User who's AssignmentContents are requested.
	 * @return Iterator over all AssignmentContents owned by this User.
	 */
	public Iterator getAssignmentContents(User owner);

	/**
	 * Access the Assignment with the specified id.
	 * @param assignmentId - The id of the Assignment.
	 * @return The Assignment corresponding to the id, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this id.
	 * @throws PermissionException if the current user is not allowed to read this.
	 */
	public Assignment getAssignment(String assignmentId)
		throws IdUnusedException, PermissionException;

	/**
	 * Access the AssignmentContent with the specified id.
	 * @param contentId - The id of the AssignmentContent.
	 * @return The AssignmentContent corresponding to the id, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this id.
	 * @throws PermissionException if the current user does not have permission to see this.
	 */
	public AssignmentContent getAssignmentContent(String contentId)
		throws IdUnusedException, PermissionException;

	/**
	 * Access the AssignmentSubmission with the specified id.
	 * @param submissionId - The id of the AssignmentSubmission.
	 * @return The AssignmentSubmission corresponding to the id, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this id.
	 * @throws PermissionException if the current user is not allowed to read this.
	 */
	public AssignmentSubmission getSubmission(String submissionId)
		throws IdUnusedException, PermissionException;

	/**
	 * Access all the Assignments which have the specified AssignmentContent.
	 * @param content - The particular AssignmentContent.
	 * @return Iterator over all the Assignments with the specified AssignmentContent.
	 */
	public Iterator getAssignments(AssignmentContent content);

	/**
	 * Access all the Assignemnts associated with a group.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return Iterator over all the Assignments associated with a group.
	 */
	public Iterator getAssignmentsForContext(String context);

	/**
	 * Access a User's AssignmentSubmission to a particular Assignment.
	 * @param assignmentId - The id of the assignment.
	 * @param person - The User who's Submission you would like.
	 * @return AssignmentSubmission The user's submission for that Assignment, or null if one does not exist.
	 * @throws IdUnusedException if the assignmentId does not correspond to an existing Assignment.
	 * @throws PermissionException if the current user is not allowed to read this.
	 */
	public AssignmentSubmission getSubmission(String assignmentId, User person)
		throws IdUnusedException, PermissionException;

	/**
	 * Get the submissions for an assignment.
	 * @param assignment - the Assignment who's submissions you would like.
	 * @return Iterator over all the submissions for an Assignment.
	 */
	public Iterator getSubmissions(Assignment assignment);
	
	/**
	* Access the grades spreadsheet for the reference, either for an assignment or all assignments in a context.
	* @param ref The reference, either to a specific assignment, or just to an assignment context.
	* @return The grades spreadsheet bytes.
	* @throws IdUnusedException if there is no object with this id.
	* @throws PermissionException if the current user is not allowed to access this.
	*/
	public byte[] getGradesSpreadsheet(String ref)
		throws IdUnusedException, PermissionException;
	
	/**
	* Access the submissions zip for the assignment reference.
	* @param ref The assignment reference.
	* @return The submissions zip bytes.
	* @throws IdUnusedException if there is no object with this id.
	* @throws PermissionException if the current user is not allowed to access this.
	*/
	public byte[] getSubmissionsZip(String ref)
		throws IdUnusedException, PermissionException;
	
	/**
	* Access the internal reference which can be used to assess security clearance.
	* @param id The assignment id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String assignmentReference(String context, String id);

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The content id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String contentReference(String context, String id);

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The submission id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String submissionReference(String context, String id, String assignmentId);

	/**
	* Get the String to form an assignment grade spreadsheet 
	* @param context The assignment context String
	* @param assignmentId The id for the assignment object; when null, indicates all assignment in that context
	*/
	public String gradesSpreadsheetReference(String context, String assignmentId);
	
	/**
	* Get the string to form an assignment submissions zip file
	* @param context The assignment context String
	* @param assignmentId The id for the assignment object; 
	*/
	public String submissionsZipReference(String context, String assignmentId);

} // GenericAssignmentService



