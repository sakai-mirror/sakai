/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Realm.java,v 1.5 2004/07/13 22:44:22 ggolden.umich.edu Exp $
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
import java.util.List;
import java.util.Set;

import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>Realm is a named security context, in which users are granted roles and granted the ability to unlock locks.</p>
* <p>Grants in a Realm for Roles and locks are given to Users, or to "any authenticated user",
* or to the anon. user (and perhaps later to a group of users).  Grants may be accessed for these special cases,
* but all testing routines test for a specific user.</p>
* <p>A separate Realm can be used for each worksite, to store the permissions related to resources
* associated with the site.  A separate Realm can be used for any resource, to define security at the
* resource level.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface Realm
	extends Resource, Comparable
{
	/**
	* @return a description of the item this realm applies to.
	*/
	public String getDescription();

	/**
	* Test if this user can unlock the lock in this Realm.
	* @param User The user.
	* @param lock The lock to open.
	* @param helperRealms Other realms that might refine role abilities.
	* @return true if this user can unlock the lock in this Realm, false if not.
	*/
	public boolean unlock(User user, String lock, List helperRealms);

	/**
	* Test if this user has this role in this Realm.
	* @param User The user.
	* @param Role The Role.
	* @return true if the User has this role in this Realm, false if not.
	*/
	public boolean hasRole(User user, Role role);

	/**
	* Access all the locks the user may open in this Realm.
	* @param User The user.
	* @param helperRealms Other realms that might refine role abilities.
	* @return The Set of locks (String) the user is may open in this Realm.
	*/
	public Set collectUserLocks(User user, List helperRealms);

	/**
	* Access all the Roles this user has in this Realm.
	* @param User The user.
	* @return The Set of roles (Role) for this user in this Realm.
	*/
	public Set collectUserRoles(User user);

	/**
	* Access all users who can unlock this lock in this Realm.
	* @param lock The lock.
	* @param helperRealms Other realms that might refine role abilities.
	* @return The Set of users (User) who can unlock this lock in this Realm.
	*/
	public Set collectLockUsers(String lock, List helperRealms);

	/**
	* Access all users who have this role in this Realm.
	* @param role The Role.
	* @return The Set of users (User) who have this role in this Realm.
	*/
	public Set collectRoleUsers(Role role);

	/**
	* Access all users who have any role or are allowed through any lock in this Realm.
	* @return The Set of users (User) who have any role or are allowed through any lock in this Realm.
	*/
	public Set collectUsers();

	/**
	* Access all users who have direct role ability grants in the Realm.
	* @return The Set of users (User) who have direct role ability grants in the Realm.
	*/
	public Set getUsers();

	/**
	* Access all the locks directly added for the user in this Realm.
	* @param User The user.
	* @return The Set of locks (String) directly added for the user in this Realm.
	*/
	public Set getUserLocks(User user);

	/**
	* Access all the locks directly added for all authenticated users in the Realm.
	* @return The Set of locks (String) directly added for all authenticated users in the Realm.
	*/
	public Set getAuthLocks();

	/**
	* Access all the locks directly added for the non-authenticated user (anon) in the Realm.
	* @return The Set of locks (String) directly added for the non-authenticated user (anon) in the Realm.
	*/
	public Set getAnonLocks();

	/**
	* Access all roles directly added for the user in this Realm.
	* @param User The user.
	* @return The Set of roles (Role) directly added for the user in this Realm.
	*/
	public Set getUserRoles(User user);

	/**
	* Access all roles directly added for all authenticated users in the Realm.
	* @return The Set of roles (Role) directly added for all authenticated users in the Realm.
	*/
	public Set getAuthRoles();

	/**
	* Access all roles directly added for the non-authenticated user (anon) in the Realm.
	* @return The Set of roles (Role) directly added for the non-authenticated user (anon) in the Realm.
	*/
	public Set getAnonRoles();

	/**
	* Access all Roles defined for this Realm.
	* @return The set of roles (Role) defined for this Realm.
	*/
	public Set getRoles();

	/**
	* Access a Role defined in this Realm.
	* @param id The role id.
	* @return The Role, if found, or null, if not.
	*/
	public Role getRole(String id);

	/**
	* Access the realm id for the RealmProvider for this Realm.
	* @return The the realm id for the RealmProvider for this Realm, or null if none defined.
	*/
	public String getProviderRealmId();

	/**
	* Is this realm empty of any roles or grants?
	* @return true if the realm is empty, false if not.
	*/
	public boolean isEmpty();

	/**
	 * Access the name of the role to use for granting maintain access.
	 * @return The name of the "maintain" role.
	 */
	public String getMaintainRole();

}	// Realm

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Realm.java,v 1.5 2004/07/13 22:44:22 ggolden.umich.edu Exp $
*
**********************************************************************************/
