/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/dissertation/CandidatePathEdit.java,v 1.5 2004/06/22 03:14:42 ggolden Exp $
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

//package
package org.sakaiproject.service.legacy.dissertation;

//imports
import java.util.Hashtable;
import java.util.List;
import org.sakaiproject.service.legacy.resource.Edit;

/**
* <p>CandidatePathEdit is the interface for BaseCandidatePathEdit, defined in BaseDissertationService.</p>
* <p>It contains the steps in the path to candidacy and methods to handle orders from the parent Dissertation object. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface CandidatePathEdit
	extends Edit, CandidatePath
{
	/**
	* Set the candidate that owns this.
	* @param candidateId - the user id of the candidate
	*/
	public void setCandidate(String candidateId);
	
	/**
	* Set the advisor of the candidate.
	* @param userId - the user id of the candidate's advisor
	*/
	public void setAdvisor(String userId);

	/**
	* Set the site at the time of creation.
	* @return siteId - the site id
	*/
	public void setSite(String siteId);
	
	/**
	* Set the type of dissertation at the time of creation.
	*/
	public void setType(String type);

	/**
	* Add a step to a CandidatePath in use.
	* @param step - The DissertationStep to be added.
	* @param previousStepRefs - The references of the steps preceeding the step to be added in the parent Dissertation.
	* @param siteId - the site id at the time of creation.
	*/
	public void liveAddStep(DissertationStep step, String[] previousStepRefs, String siteId);

	/**
	* Add a step to a CandidatePath in use.
	* @param step - The DissertationStep to be added.
	* @param previousStepRefs - The references of the steps preceeding the step to be added in the parent Dissertation.
	* @param siteId - the site id at the time of creation.
	*/
	//public void liveAddStep(DissertationStep step, String[] previousStepRefs, String siteId);
	public void liveAddStep(DissertationStep step, List previousStepRefs, String siteId);

	/**
	* Remove a step from a CandidatePath in use.
	* @param step - The DissertationStep to be removed.
	*/
	public void liveRemoveStep(DissertationStep step);

	/**
	* Update a step in a CandidatePath in use.
	* @param before - The DissertationStep to be updated, in its original form.
	* @param after - The new version of the step.
	*/
	public void liveUpdateStep(DissertationStep before, DissertationStepEdit after);
	
	/**
	* Move a step in a CandidatePath in use.
	* @param step - the step to be moved.
	* @param location - 
	* @param previousStepPosition - the order number as a String.
	*/
	public void liveMoveStep(DissertationStep step, String location, String previousStepPosition);

	/**
	* Add a prerequisite to a StepStatus in a CandidatePath in use.
	* @param stepRef - the reference of the step to which the prerequisite is to be added.
	* @param prereqRef - the reference of the prerequisite to be added.
	*/
	public void liveAddSchoolPrereq(String stepRef, String prereqRef);
	
	/**
	* Remove a prerequisite from a StepStatus in a CandidatePath in use.
	* @param stepRef - the reference of the step from which the prerequisite is to be removed.
	* @param prereqRef - the reference of the prerequisite to be removed.
	*/
	public void liveRemoveSchoolPrereq(String stepRef, String prereqRef);
	
	/**
	* Set the ordered StepStatus object for this CandidatePath.
	* @param hash - the HashTable of StepStatus references, keyed by order number.
	*/
	public void setOrderedStatus(Hashtable hash);
	
	
	/**
	* Move a StepStatus object within the ordered status HashTable.
	* @param statusRef - the reference of the StepStatus to be moved.
	* @param location - the new order number.
	*/
	public void moveStatus(String statusRef, String location);

	
	/**
	* Add a StepStatus object to the ordered status HashTable.
	* @param newStatus - the StepStatus to be added.
	* @param location - the order number.
	*/
	public void addToOrderedStatus(StepStatus newStatus, int location);
	
	
	/**
	* Remove a StepStatus object from the ordered status HashTable.
	* @param statusRef - the reference of the StepStatus to be removed.
	*/
	public void removeFromOrderedStatus(String statusRef);
	
	/**
	* Set the prequisites added for Rackham School Steps within a department's Dissertation.
	* @param schoolPrereqs - the HashTable of step references of the prerequisites, keyed by their step reference
	*/
	public void setSchoolPrereqs(Hashtable schoolPrereqs);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/dissertation/CandidatePathEdit.java,v 1.5 2004/06/22 03:14:42 ggolden Exp $
*
**********************************************************************************/
