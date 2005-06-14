/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentEdit.java,v 1.5.4.1 2005/03/21 15:19:10 lance.indiana.edu Exp $
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
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>Assignment is an interface for the CHEF assignments module.
* It represents a specific assignment (as for a specific section or class).</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface AssignmentEdit
	extends Assignment, Edit
{
	/**
	 * Set the reference of the AssignmentContent of this Assignment.
	 * @param String - the reference of the AssignmentContent.
	 */
	public void setContentReference(String contentReference);
	
	/**
	 * Set the AssignmentContent of this Assignment.
	 * @param content - the Assignment's AssignmentContent.
	 */
	public void setContent(AssignmentContent content);

	/**
	 * Set the first time at which the assignment can be viewed;
	 * may be null.
	 * @param openTime - The Time at which the Assignment opens.
	 */
	public void setOpenTime(Time openTime);

	/**
	 * Set the time at which the assignment is due;
	 * may be null.
	 * @param dueTime - The Time at which the Assignment is due.
	 */
	public void setDueTime(Time dueTime);

	/**
	 * Set the drop dead time after which responses to this assignment are considered late;
	 * may be null.
	 * @param dropDeadTime - The Time object representing the drop dead time.
	 */
	public void setDropDeadTime(Time dropDeadTime);

	/**
	 * Set the time after which this assignment can no longer be viewed,
	 * and after which submissions will not be accepted. May be null.
	 * @param closeTime - The Time after which the Assignment is closed, or null if unspecified.
	 */
	public void setCloseTime(Time closeTime);

	/**
	 * Set the section info
	 * @param sectionId - The section id.
	 */
	public void setSection(String sectionId);

	/**
	 * Set the Assignment's context at the time of creation.
	 * @param context - The context string.
	 */
	public void setContext(String context);
	
	/**
	 * Set whether this is a draft or final copy.
	 * @param draft - true if this is a draft, false if it is a final copy.
	 */
	public void setDraft(boolean draft);

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
	 * Set the title.
	 * @param title - The Assignment's title.
	 */
	public void setTitle(String title);

}	// AssignmentEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/assignment/AssignmentEdit.java,v 1.5.4.1 2005/03/21 15:19:10 lance.indiana.edu Exp $
*
**********************************************************************************/
