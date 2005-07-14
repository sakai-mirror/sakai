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
package org.sakaiproject.service.legacy.realm;

// imports
import java.util.Collection;

/**
* <p>RoleEdit is an editable Role.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface RoleEdit
	extends Role
{
	/**
	 * Set the role description.
	 * @param description The role description.
	 */
	void setDescription(String description);

	/**
	 * Add this lock to the Role.
	 * @param lock The lock.
	 */
	void add(String lock);

	/**
	 * Add these locks to the Role.
	 * @param lock The locks.
	 */
	void add(Collection locks);

	/**
	 * Remove this lock from the Role.
	 * @param lock The lock.
	 */
	void remove(String lock);

	/**
	 * Remove these locks from the Role.
	 * @param lock The locks.
	 */
	void remove(Collection lock);

	/**
	 * Remove all locks from the Role.
	 */
	void clear();
	
	/**
	 * Check if the Role has no locks.
	 * @return true if the role has no locks, false if it does.
	 */
	boolean isEmpty();
}



