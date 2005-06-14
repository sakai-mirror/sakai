/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmEdit.java,v 1.5 2004/07/13 22:44:22 ggolden.umich.edu Exp $
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
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>RealmEdit is an editable Reaml.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface RealmEdit
	extends Realm, Edit
{
	/**
	* Add this role to this user in this Realm.
	* @param user The user.
	* @param role The Role.
	*/
	public void addUserRole(User user, Role role);

	/**
	* Add this role to the non-authenticated (anon) user in this Realm.
	* @param role The Role.
	*/
	public void addAnonRole(Role role);

	/**
	* Add this role to all authenticated users in this Realm.
	* @param role The Role.
	*/
	public void addAuthRole(Role role);

	/**
	* Add the ablilty to open this lock to this user in this Realm.
	* @param user The user.
	* @param lock The lock.
	*/
	public void addUserLock(User user, String lock);

	/**
	* Add the ablilty to open this lock to the non-authenticated (anon) user in this Realm.
	* @param lock The lock.
	*/
	public void addAnonLock(String lock);

	/**
	* Add the ablilty to open this lock to all authenticated users in this Realm.
	* @param lock The lock.
	*/
	public void addAuthLock(String lock);

	/**
	* Remove this Role from this User in this Realm.
	* @param user The user.
	* @param role The Role.
	*/
	public void removeUserRole(User user, Role role);

	/**
	* Remove this Role from the non-authenticated (anon) user in this Realm.
	* @param role The Role.
	*/
	public void removeAnonRole(Role role);

	/**
	* Remove this Role from all authenticated users in this Realm.
	* @param role The Role.
	*/
	public void removeAuthRole(Role role);

	/**
	* Remove the ability to open this lock from this User in this Realm.
	* @param user The user.
	* @param lock The lock.
	*/
	public void removeUserLock(User user, String lock);

	/**
	* Remove the ability to open this lock from the non-authenticated (anon) user in this Realm.
	* @param lock The lock.
	*/
	public void removeAnonLock(String lock);

	/**
	* Remove the ability to open this lock from all authenticated users in this Realm.
	* @param lock The lock.
	*/
	public void removeAuthLock(String lock);

	/**
	* Remove all Roles and locks for this user from this Realm.
	* @param user The user.
	*/
	public void removeUser(User user);

	/**
	* Remove all Roles and locks for the non-authenticated (anon) user in this Realm.
	*/
	public void removeAnon();

	/**
	* Remove all Roles and locks for all authenticated users in this Realm.
	*/
	public void removeAuth();

	/**
	* Remove all Roles and locks for all users from this Realm.
	*/
	public void removeUsers();

	/**
	* Create a new Role within this Realm.
	* @param id The role id.
	* @return the new Role.
	* @exception IdUsedException if the id is already a Role in this Realm.
	*/
	public RoleEdit addRole(String id)
		throws IdUsedException;

	/**
	* Create a new Role within this Realm, as a copy of this other role
	* @param id The role id.
	* @param other The role to copy.
	* @return the new Role.
	* @exception IdUsedException if the id is already a Role in this Realm.
	*/
	public RoleEdit addRole(String id, Role other)
		throws IdUsedException;

	/**
	* Remove this Role from this Realm.  Any grants of this Role in the Realm are also removed.
	* @param role The Role.
	*/
	public void removeRole(Role role);

	/**
	* Remove all Roles from this Realm.
	*/
	public void clearRoles();

	/**
	* Access a Role defined in this Realm for editing.
	* @param id The role id.
	* @return The Role, if found, or null, if not.
	*/
	public RoleEdit getRoleEdit(String id);

	/**
	* Set the realm id for the RealmProvider for this Realm (set to null to have none).
	* @param id The realm id for the RealmProvider, or null if there is to be none.
	*/
	public void setProviderRealmId(String id);

	/**
	 * Set the role name to use for "maintain" access.
	 * @param role The name of the "maintain" role.
	 */
	public void setMaintainRole(String role);

}	// RealmEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmEdit.java,v 1.5 2004/07/13 22:44:22 ggolden.umich.edu Exp $
*
**********************************************************************************/
