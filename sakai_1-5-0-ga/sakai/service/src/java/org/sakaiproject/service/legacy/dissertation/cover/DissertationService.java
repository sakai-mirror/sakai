/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/dissertation/cover/DissertationService.java,v 1.13 2004/12/13 00:52:29 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.dissertation.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>DissertationService is a static Cover for the {@link org.sakaiproject.service.legacy.dissertation.DissertationService DissertationService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.13 $
*/
public class DissertationService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.dissertation.DissertationService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.dissertation.DissertationService) ComponentManager.get(org.sakaiproject.service.legacy.dissertation.DissertationService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.dissertation.DissertationService) ComponentManager.get(org.sakaiproject.service.legacy.dissertation.DissertationService.class);
		}
	}
	private static org.sakaiproject.service.legacy.dissertation.DissertationService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.dissertation.DissertationService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.dissertation.DissertationService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_DISSERTATION = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ADD_DISSERTATION;
	public static java.lang.String SECURE_ACCESS_DISSERTATION = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ACCESS_DISSERTATION;
	public static java.lang.String SECURE_UPDATE_DISSERTATION = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION;
	public static java.lang.String SECURE_REMOVE_DISSERTATION = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_REMOVE_DISSERTATION;
	public static java.lang.String SECURE_ADD_DISSERTATION_CANDIDATEPATH = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ADD_DISSERTATION_CANDIDATEPATH;
	public static java.lang.String SECURE_ACCESS_DISSERTATION_CANDIDATEPATH = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ACCESS_DISSERTATION_CANDIDATEPATH;
	public static java.lang.String SECURE_UPDATE_DISSERTATION_CANDIDATEPATH = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION_CANDIDATEPATH;
	public static java.lang.String SECURE_UPDATE_DISSERTATION_CANDIDATEPATH_COMM = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION_CANDIDATEPATH_COMM;
	public static java.lang.String SECURE_REMOVE_DISSERTATION_CANDIDATEPATH = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_REMOVE_DISSERTATION_CANDIDATEPATH;
	public static java.lang.String SECURE_ADD_DISSERTATION_STEP = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ADD_DISSERTATION_STEP;
	public static java.lang.String SECURE_ACCESS_DISSERTATION_STEP = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ACCESS_DISSERTATION_STEP;
	public static java.lang.String SECURE_UPDATE_DISSERTATION_STEP = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION_STEP;
	public static java.lang.String SECURE_REMOVE_DISSERTATION_STEP = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_REMOVE_DISSERTATION_STEP;
	public static java.lang.String SECURE_ADD_DISSERTATION_STEPSTATUS = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ADD_DISSERTATION_STEPSTATUS;
	public static java.lang.String SECURE_ACCESS_DISSERTATION_STEPSTATUS = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ACCESS_DISSERTATION_STEPSTATUS;
	public static java.lang.String SECURE_UPDATE_DISSERTATION_STEPSTATUS = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION_STEPSTATUS;
	public static java.lang.String SECURE_REMOVE_DISSERTATION_STEPSTATUS = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_REMOVE_DISSERTATION_STEPSTATUS;
	public static java.lang.String SECURE_ADD_DISSERTATION_CANDIDATEINFO = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ADD_DISSERTATION_CANDIDATEINFO;
	public static java.lang.String SECURE_ACCESS_DISSERTATION_CANDIDATEINFO = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_ACCESS_DISSERTATION_CANDIDATEINFO;
	public static java.lang.String SECURE_UPDATE_DISSERTATION_CANDIDATEINFO = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_UPDATE_DISSERTATION_CANDIDATEINFO;
	public static java.lang.String SECURE_REMOVE_DISSERTATION_CANDIDATEINFO = org.sakaiproject.service.legacy.dissertation.DissertationService.SECURE_REMOVE_DISSERTATION_CANDIDATEINFO;
	public static java.lang.String STEP_STATUS_PREREQS_NOT_COMPLETED = org.sakaiproject.service.legacy.dissertation.DissertationService.STEP_STATUS_PREREQS_NOT_COMPLETED;
	public static java.lang.String STEP_STATUS_PREREQS_COMPLETED = org.sakaiproject.service.legacy.dissertation.DissertationService.STEP_STATUS_PREREQS_COMPLETED;
	public static java.lang.String STEP_STATUS_COMPLETED = org.sakaiproject.service.legacy.dissertation.DissertationService.STEP_STATUS_COMPLETED;
	public static java.lang.String CHECKLIST_SECTION_HEADING1 = org.sakaiproject.service.legacy.dissertation.DissertationService.CHECKLIST_SECTION_HEADING1;
	public static java.lang.String CHECKLIST_SECTION_HEADING2 = org.sakaiproject.service.legacy.dissertation.DissertationService.CHECKLIST_SECTION_HEADING2;
	public static java.lang.String CHECKLIST_SECTION_HEADING3 = org.sakaiproject.service.legacy.dissertation.DissertationService.CHECKLIST_SECTION_HEADING3;
	public static java.lang.String CHECKLIST_SECTION_HEADING4 = org.sakaiproject.service.legacy.dissertation.DissertationService.CHECKLIST_SECTION_HEADING4;
	public static java.lang.String DATAFILE_NAME = org.sakaiproject.service.legacy.dissertation.DissertationService.DATAFILE_NAME;
	public static java.lang.String DATAFILE_TYPE = org.sakaiproject.service.legacy.dissertation.DissertationService.DATAFILE_TYPE;
	public static java.lang.String DATAFILE_LINES = org.sakaiproject.service.legacy.dissertation.DissertationService.DATAFILE_LINES;
	public static java.lang.String DISSERTATION_TYPE_MUSIC_PERFORMANCE = org.sakaiproject.service.legacy.dissertation.DissertationService.DISSERTATION_TYPE_MUSIC_PERFORMANCE;
	public static java.lang.String DISSERTATION_TYPE_DISSERTATION_STEPS = org.sakaiproject.service.legacy.dissertation.DissertationService.DISSERTATION_TYPE_DISSERTATION_STEPS;
	
	
	public static boolean allowAddBlockGrantGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddBlockGrantGroup(param0);
	}

	public static boolean allowGetBlockGrantGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetBlockGrantGroup(param0);
	}
	
	public static boolean allowRemoveBlockGrantGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveBlockGrantGroup(param0);
	}
	
	public static boolean allowUpdateBlockGrantGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateBlockGrantGroup(param0);
	}
	
	public static java.lang.String getMusicPerformanceSite()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getMusicPerformanceSite();
	}
	
	public static org.sakaiproject.service.legacy.dissertation.BlockGrantGroupEdit addBlockGrantGroup(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addBlockGrantGroup(param0);
	}
	
	public static void removeBlockGrantGroup(org.sakaiproject.service.legacy.dissertation.BlockGrantGroupEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeBlockGrantGroup(param0);
	}
	
	public static org.sakaiproject.service.legacy.dissertation.BlockGrantGroupEdit editBlockGrantGroup(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editBlockGrantGroup(param0);
	}
	
	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.BlockGrantGroupEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}
	
	public static org.sakaiproject.service.legacy.dissertation.DissertationStepEdit addDissertationStep(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addDissertationStep(param0, param1);
	}
	
	public static org.sakaiproject.service.legacy.dissertation.CandidateInfo getInfoForCandidate(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;
		return service.getInfoForCandidate(param0);
	}
	
	public static org.sakaiproject.service.legacy.dissertation.Dissertation getDissertationForSite(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertationForSite(param0, param1);
	}
	
	public static java.util.List getBlockGrantGroups()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getBlockGrantGroups();
	}
	
	public static org.sakaiproject.service.legacy.dissertation.BlockGrantGroup getBlockGrantGroupForFieldOfStudy(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getBlockGrantGroupForFieldOfStudy(param0);
	}

	public static boolean isMusicPerformanceCandidate(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isMusicPerformanceCandidate(param0);
	}
	
	public static boolean allowAddDissertation(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddDissertation(param0);
	}

	public static boolean allowRemoveDissertation(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveDissertation(param0);
	}

	public static boolean allowGetDissertation(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetDissertation(param0);
	}

	public static boolean allowUpdateDissertation(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateDissertation(param0);
	}

	public static boolean allowAddCandidatePath(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddCandidatePath(param0);
	}

	public static boolean allowRemoveCandidatePath(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveCandidatePath(param0);
	}

	public static boolean allowGetCandidatePath(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetCandidatePath(param0);
	}

	public static boolean allowUpdateCandidatePath(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateCandidatePath(param0);
	}

	public static boolean allowUpdateCandidatePathComm(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateCandidatePathComm(param0);
	}

	public static boolean allowAddDissertationStep(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddDissertationStep(param0);
	}

	public static boolean allowRemoveDissertationStep(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveDissertationStep(param0);
	}

	public static boolean allowGetDissertationStep(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetDissertationStep(param0);
	}

	public static boolean allowUpdateDissertationStep(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateDissertationStep(param0);
	}

	public static boolean allowAddStepStatus(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddStepStatus(param0);
	}

	public static boolean allowRemoveStepStatus(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveStepStatus(param0);
	}

	public static boolean allowGetStepStatus(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetStepStatus(param0);
	}

	public static boolean allowUpdateStepStatus(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateStepStatus(param0);
	}

	public static boolean allowAddCandidateInfo(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddCandidateInfo(param0);
	}

	public static boolean allowRemoveCandidateInfo(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveCandidateInfo(param0);
	}

	public static boolean allowGetCandidateInfo(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowGetCandidateInfo(param0);
	}

	public static boolean allowUpdateCandidateInfo(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateCandidateInfo(param0);
	}

	public static java.lang.String getParentSiteForUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getParentSiteForUser(param0);
	}

	public static java.lang.String getEmplidForUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getEmplidForUser(param0);
	}

	public static java.lang.String getSchoolSite()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getSchoolSite();
	}

	public static boolean hasCandidateInfos()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.hasCandidateInfos();
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationEdit addDissertation(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addDissertation(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationEdit mergeDissertation(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.mergeDissertation(param0);
	}

	public static void removeDissertation(org.sakaiproject.service.legacy.dissertation.DissertationEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeDissertation(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.Dissertation getDissertation(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertation(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationEdit editDissertation(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editDissertation(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.CandidatePathEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.DissertationEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.DissertationStepEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.dissertation.StepStatusEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.dissertation.DissertationEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.dissertation.DissertationStepEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.dissertation.StepStatusEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.dissertation.CandidatePathEdit param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationStepEdit addDissertationStep(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addDissertationStep(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationStepEdit mergeDissertationStep(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.mergeDissertationStep(param0);
	}

	public static void removeDissertationStep(org.sakaiproject.service.legacy.dissertation.DissertationStepEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeDissertationStep(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationStep getDissertationStep(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertationStep(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.DissertationStepEdit editDissertationStep(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editDissertationStep(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePathEdit addCandidatePath(org.sakaiproject.service.legacy.dissertation.Dissertation param0, java.lang.String param1) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addCandidatePath(param0, param1);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePathEdit mergeCandidatePath(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.mergeCandidatePath(param0);
	}

	public static void removeCandidatePath(org.sakaiproject.service.legacy.dissertation.CandidatePathEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeCandidatePath(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePath getCandidatePath(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePath(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePathEdit editCandidatePath(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editCandidatePath(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.StepStatusEdit addStepStatus(java.lang.String param0, org.sakaiproject.service.legacy.dissertation.DissertationStep param1, boolean param2) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addStepStatus(param0, param1, param2);
	}

	public static org.sakaiproject.service.legacy.dissertation.StepStatusEdit addStepStatus(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addStepStatus(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.StepStatusEdit mergeStepStatus(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.mergeStepStatus(param0);
	}

	public static void removeStepStatus(org.sakaiproject.service.legacy.dissertation.StepStatusEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeStepStatus(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.StepStatus getStepStatus(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getStepStatus(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.StepStatusEdit editStepStatus(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editStepStatus(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit addCandidateInfo(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addCandidateInfo(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit addCandidateInfoFromListener(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.addCandidateInfoFromListener(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit mergeCandidateInfo(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.mergeCandidateInfo(param0);
	}

	public static void removeCandidateInfo(org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.removeCandidateInfo(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfo getCandidateInfo(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidateInfo(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit getCandidateInfoEditForEmplid(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidateInfoEditForEmplid(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidateInfoEdit editCandidateInfo(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.editCandidateInfo(param0);
	}

	public static java.util.List getCandidatePathsForParentSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePathsForParentSite(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePath getCandidatePathForCandidate(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePathForCandidate(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.CandidatePath getCandidatePathForSite(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePathForSite(param0);
	}

	public static org.sakaiproject.service.legacy.dissertation.Dissertation getDissertationForSite(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertationForSite(param0);
	}

	public static java.util.List getDissertations()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertations();
	}
	
	public static java.util.List getDissertationsOfType(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertationsOfType(param0);
	}

	public static java.util.List getDissertationSteps()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDissertationSteps();
	}

	public static java.util.List getCandidatePaths()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePaths();
	}
	
	public static java.util.List getCandidatePathsOfType(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getCandidatePathsOfType(param0);
	}

	public static boolean getActivePaths()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.getActivePaths();
	}

	public static boolean getActivePathsForSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.getActivePathsForSite(param0);
	}
	
	public static boolean isUserOfTypeForLetter(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isUserOfTypeForLetter(param0, param1);
	}
	
	public static boolean isUserOfParentForLetter(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isUserOfParentForLetter(param0, param1);
	}
	
	public static org.sakaiproject.service.legacy.user.User[] getAllUsersForSite(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getAllUsersForSite(param0, param1);
	}
	
	public static java.util.List getSortedUsersOfTypeForLetter(java.lang.String param0, java.lang.String  param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getSortedUsersOfTypeForLetter(param0, param1);
	}
	
	public static java.util.List getSortedUsersOfParentForLetter(java.lang.String param0, java.lang.String  param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getSortedUsersOfParentForLetter(param0, param1);
	}

	public static boolean isCandidate(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isCandidate(param0);
	}

	public static void dumpData(java.util.Vector param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return;

		service.dumpData(param0);
	}

	public static boolean isInitialized()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isInitialized();
	}

	public static java.lang.String getProgram(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getProgram(param0);
	}

	public static java.util.Hashtable getDataFileProperties(byte[] param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.getDataFileProperties(param0);
	}

	public static java.util.Vector loadData(byte[] param0, byte[] param1)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return null;

		return service.loadData(param0, param1);
	}

	public static boolean isLoading()
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isLoading();
	}
	
	public static boolean isCandidatePathOfType(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.dissertation.DissertationService service = getInstance();
		if (service == null)
			return false;

		return service.isCandidatePathOfType(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/dissertation/cover/DissertationService.java,v 1.13 2004/12/13 00:52:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
