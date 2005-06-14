/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmEdit.java,v 1.6 2004/11/19 20:48:41 ggolden.umich.edu Exp $
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

/**
* <p>RealmEdit is an editable Reaml.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public interface RealmEdit
	extends Realm, Edit
{
	/**
	 * Add this role to this user in this Realm.
	 * @param userId The user.
	 * @param role The role name.
	 * @param active The active flag for the role grant.
	 * @param provided If true, from an external provider.
	 */
	void addUserRole(String userId, String roleId, boolean active, boolean provided);

	/**
	 * Remove all Roles for this user from this Realm.
	 * @param userId The user.
	 */
	void removeUser(String userId);

	/**
	 * Remove all Roles for all users from this Realm.
	 */
	void removeUsers();

	/**
	 * Create a new Role within this Realm.
	 * @param id The role id.
	 * @return the new Role.
	 * @exception IdUsedException if the id is already a Role in this Realm.
	 */
	RoleEdit addRole(String id)
		throws IdUsedException;

	/**
	 * Create a new Role within this Realm, as a copy of this other role
	 * @param id The role id.
	 * @param other The role to copy.
	 * @return the new Role.
	 * @exception IdUsedException if the id is already a Role in this Realm.
	 */
	RoleEdit addRole(String id, Role other)
		throws IdUsedException;

	/**
	 * Remove this Role from this Realm.  Any grants of this Role in the Realm are also removed.
	 * @param role The role name.
	 */
	void removeRole(String role);

	/**
	 * Remove all Roles from this Realm.
	 */
	void clearRoles();

	/**
	 * Access a Role defined in this Realm for editing.
	 * @param id The role id.
	 * @return The Role, if found, or null, if not.
	 */
	RoleEdit getRoleEdit(String id);

	/**
	 * Set the realm id for the RealmProvider for this Realm (set to null to have none).
	 * @param id The realm id for the RealmProvider, or null if there is to be none.
	 */
	void setProviderRealmId(String id);

	/**
	 * Set the role name to use for "maintain" access.
	 * @param role The name of the "maintain" role.
	 */
	void setMaintainRole(String role);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmEdit.java,v 1.6 2004/11/19 20:48:41 ggolden.umich.edu Exp $
*
**********************************************************************************/
