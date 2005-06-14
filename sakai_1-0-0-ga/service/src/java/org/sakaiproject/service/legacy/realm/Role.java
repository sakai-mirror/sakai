/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Role.java,v 1.4 2004/06/22 03:14:50 ggolden Exp $
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
* <p>Role is a named set of security locks (Strings) which may be opened by any users granted the Role,
* and a set of other Roles whose definitions act as if they are defined in this Role.</p>
* <p>Users are granted roles within a security Realm, such as the Realm for the resources of a worksite.
* Once so granted, the user has permission to all the functions, to open all the locks, in the Role's set,
* within the context of the Realm.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface Role
	extends Comparable
{
	/**
	* Access the Role id.
	* @return The role id.
	*/
	public String getId();

	/**
	* Test if a holder of this Role can unlock the lock.
	* @param lock The lock to open.
	* @return true if a holder of this Role can unlock the lock, false if not.
	*/
	public boolean unlock(String lock);

	/**
	* Test if a holder of this Role thereby holds this other Role.
	* @param role The other role.
	* @return true if a holder of this Role thereby holds this other Role.
	*/
	public boolean hasRole(Role role);

	/**
	* Access all the locks which may be opened by holders of this Role.
	* @return The Set of locks (String) which may be opened by users with this Role.
	*/
	public Set collectLocks();

	/**
	* Access all the roles that holders of this Role thereby also have.
	* @return The Set of roles (Role) that holders of this Role thereby also have.
	*/
	public Set collectRoles();

	/**
	* Access all the locks added to this Role.
	* @return The Set of locks (String) locks added to this Role.
	*/
	public Set getLocks();

	/**
	* Access all roles added to this Role.
	* @return The Set of roles (Role) added to this Role.
	*/
	public Set getRoles();

}	// Role

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Role.java,v 1.4 2004/06/22 03:14:50 ggolden Exp $
*
**********************************************************************************/
