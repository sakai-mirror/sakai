/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RoleEdit.java,v 1.4 2004/06/22 03:14:50 ggolden Exp $
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
package org.sakaiproject.service.legacy.realm;

// imports
import java.util.Set;

/**
* <p>RoleEdit is an editable Role.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface RoleEdit
	extends Role
{
	/**
	* Add the ability to open this lock to this Role.
	* @param lock The lock.
	*/
	public void addLock(String lock);

	/**
	* Add the ability to open these locks to this Role.
	* @param lock The lock.
	*/
	public void addLocks(Set locks);

	/**
	* Remove the ability to open this lock from this Role.
	* @param lock The lock.
	*/
	public void removeLock(String lock);

	/**
	* Add this other role to be part of this Role, so that this Role has all the abilities of the another Role.
	* @param role The other Role.
	*/
	public void addRole(Role role);

	/**
	* Remove this other Role from this Role.
	* @param role The other Role.
	*/
	public void removeRole(Role role);

	/**
	* Remove all locks and Roles from the Role.
	*/
	public void clear();

}	// RoleEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RoleEdit.java,v 1.4 2004/06/22 03:14:50 ggolden Exp $
*
**********************************************************************************/
