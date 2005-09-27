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

package org.sakaiproject.service.legacy.assignment.cover;

import java.util.Set;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>AssignmentService is a static Cover for the {@link org.sakaiproject.service.legacy.assignment.AssignmentService AssignmentService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class AssignmentService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.assignment.AssignmentService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.assignment.AssignmentService) ComponentManager.get(org.sakaiproject.service.legacy.assignment.AssignmentService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.assignment.AssignmentService) ComponentManager.get(org.sakaiproject.service.legacy.assignment.AssignmentService.class);
		}
	}
	private static org.sakaiproject.service.legacy.assignment.AssignmentService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.assignment.AssignmentService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.assignment.AssignmentService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_ASSIGNMENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ADD_ASSIGNMENT;
	public static java.lang.String SECURE_ADD_ASSIGNMENT_CONTENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ADD_ASSIGNMENT_CONTENT;
	public static java.lang.String SECURE_ADD_ASSIGNMENT_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ADD_ASSIGNMENT_SUBMISSION;
	public static java.lang.String SECURE_REMOVE_ASSIGNMENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_REMOVE_ASSIGNMENT;
	public static java.lang.String SECURE_REMOVE_ASSIGNMENT_CONTENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_REMOVE_ASSIGNMENT_CONTENT;
	public static java.lang.String SECURE_REMOVE_ASSIGNMENT_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_REMOVE_ASSIGNMENT_SUBMISSION;
	public static java.lang.String SECURE_ACCESS_ASSIGNMENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ACCESS_ASSIGNMENT;
	public static java.lang.String SECURE_ACCESS_ASSIGNMENT_CONTENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ACCESS_ASSIGNMENT_CONTENT;
	public static java.lang.String SECURE_ACCESS_ASSIGNMENT_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_ACCESS_ASSIGNMENT_SUBMISSION;
	public static java.lang.String SECURE_UPDATE_ASSIGNMENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_UPDATE_ASSIGNMENT;
	public static java.lang.String SECURE_UPDATE_ASSIGNMENT_CONTENT = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_UPDATE_ASSIGNMENT_CONTENT;
	public static java.lang.String SECURE_UPDATE_ASSIGNMENT_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_UPDATE_ASSIGNMENT_SUBMISSION;
	public static java.lang.String SECURE_GRADE_ASSIGNMENT_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.SECURE_GRADE_ASSIGNMENT_SUBMISSION;
	public static java.lang.String REF_TYPE_ASSIGNMENT = org.sakaiproject.service.legacy.assignment.AssignmentService.REF_TYPE_ASSIGNMENT;
	public static java.lang.String REF_TYPE_SUBMISSION = org.sakaiproject.service.legacy.assignment.AssignmentService.REF_TYPE_SUBMISSION;
	public static java.lang.String REF_TYPE_CONTENT = org.sakaiproject.service.legacy.assignment.AssignmentService.REF_TYPE_CONTENT;
	public static java.lang.String REF_TYPE_GRADES = org.sakaiproject.service.legacy.assignment.AssignmentService.REF_TYPE_GRADES;
	public static java.lang.String REF_TYPE_SUBMISSIONS = org.sakaiproject.service.legacy.assignment.AssignmentService.REF_TYPE_SUBMISSIONS;

	public static boolean allowAddAssignment(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddAssignment(param0);
	}

	public static boolean allowGetAssignment(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetAssignment(param0);
	}

	public static boolean allowUpdateAssignment(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateAssignment(param0);
	}

	public static boolean allowRemoveAssignment(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveAssignment(param0);
	}

	public static boolean allowAddAssignmentContent(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddAssignmentContent(param0);
	}

	public static boolean allowGetAssignmentContent(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetAssignmentContent(param0);
	}

	public static boolean allowUpdateAssignmentContent(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateAssignmentContent(param0);
	}

	public static boolean allowRemoveAssignmentContent(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveAssignmentContent(param0);
	}

	public static boolean allowAddSubmission(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddSubmission(param0);
	}

	public static java.util.List allowAddSubmissionUsers(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.allowAddSubmissionUsers(param0);
	}

	public static boolean allowGetSubmission(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetSubmission(param0);
	}

	public static boolean allowUpdateSubmission(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateSubmission(param0);
	}

	public static boolean allowRemoveSubmission(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveSubmission(param0);
	}
	
	public static boolean allowGradeSubmission(String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return false;
		
		return service.allowGradeSubmission(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentEdit addAssignment(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.addAssignment(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentEdit mergeAssignment(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.mergeAssignment(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentEdit addDuplicateAssignment(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.addDuplicateAssignment(param0, param1);
	}

	public static void removeAssignment(org.sakaiproject.service.legacy.assignment.AssignmentEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.removeAssignment(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentEdit editAssignment(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.editAssignment(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.assignment.AssignmentContentEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.assignment.AssignmentEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.assignment.AssignmentContentEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.assignment.AssignmentEdit param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentContentEdit addAssignmentContent(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.addAssignmentContent(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentContentEdit mergeAssignmentContent(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.mergeAssignmentContent(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentContentEdit addDuplicateAssignmentContent(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.addDuplicateAssignmentContent(param0, param1);
	}

	public static void removeAssignmentContent(org.sakaiproject.service.legacy.assignment.AssignmentContentEdit param0) throws org.sakaiproject.service.legacy.assignment.AssignmentContentNotEmptyException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.removeAssignmentContent(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentContentEdit editAssignmentContent(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.editAssignmentContent(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit addSubmission(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.addSubmission(param0, param1);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit mergeSubmission(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.mergeSubmission(param0);
	}

	public static void removeSubmission(org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.removeSubmission(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit editSubmission(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.editSubmission(param0);
	}

	public static java.util.Iterator getAssignmentContents(org.sakaiproject.service.legacy.user.User param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getAssignmentContents(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.Assignment getAssignment(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getAssignment(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentContent getAssignmentContent(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getAssignmentContent(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentSubmission getSubmission(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getSubmission(param0);
	}

	public static org.sakaiproject.service.legacy.assignment.AssignmentSubmission getSubmission(java.lang.String param0, org.sakaiproject.service.legacy.user.User param1) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getSubmission(param0, param1);
	}

	public static java.util.Iterator getAssignments(org.sakaiproject.service.legacy.assignment.AssignmentContent param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getAssignments(param0);
	}

	public static java.util.Iterator getAssignmentsForContext(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getAssignmentsForContext(param0);
	}

	public static java.util.Iterator getSubmissions(org.sakaiproject.service.legacy.assignment.Assignment param0)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getSubmissions(param0);
	}

	public static byte[] getGradesSpreadsheet(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getGradesSpreadsheet(param0);
	}

	public static byte[] getSubmissionsZip(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getSubmissionsZip(param0);
	}

	public static java.lang.String assignmentReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.assignmentReference(param0, param1);
	}

	public static java.lang.String contentReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.contentReference(param0, param1);
	}

	public static java.lang.String submissionReference(java.lang.String param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.submissionReference(param0, param1, param2);
	}

	public static java.lang.String gradesSpreadsheetReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.gradesSpreadsheetReference(param0, param1);
	}

	public static java.lang.String submissionsZipReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.submissionsZipReference(param0, param1);
	}

	public static java.lang.String merge(java.lang.String param0, org.w3c.dom.Element param1, java.lang.String param2, java.lang.String param3, java.util.Map param4, java.util.HashMap param5, Set param6)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.merge(param0, param1, param2, param3, param4, param5, param6);
	}

	public static java.lang.String getLabel()
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.getLabel();
	}

	public static java.lang.String archive(java.lang.String param0, org.w3c.dom.Document param1, java.util.Stack param2, java.lang.String param3, java.util.List param4)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return null;

		return service.archive(param0, param1, param2, param3, param4);
	}
	
	public static void importResources(java.lang.String param0, java.lang.String param1, java.util.List param2)
	{
		org.sakaiproject.service.legacy.assignment.AssignmentService service = getInstance();
		if (service == null)
			return;

		service.importEntities(param0, param1, param2);
	}
}



