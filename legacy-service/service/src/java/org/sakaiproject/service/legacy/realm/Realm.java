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
import java.util.Set;

import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>Realm is a named security context, in which users are granted roles which are collection of function locks.</p>
* <p>A user can be granted a single Role in realm.  The special anon. role is "granted" to any anon. user.  The special
* auth. role is granted to any authorized user.</p>
* <p>The id of a realm is the "reference" string of the Sakai resource for which the realm applies.
* Special realms not related to a resource have ids that begin with a "!"</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface Realm
	extends Entity, Comparable
{
	// TODO: move these to Resource
	
	/**
	 * @return the user who created this.
	 */
	User getCreatedBy();

	/**
	 * @return the user who last modified this.
	 */
	User getModifiedBy();

	/**
	 * @return the time created.
	 */
	Time getCreatedTime();

	/**
	 * @return the time last modified.
	 */
	Time getModifiedTime();
	
	// TODO:

	/**
	 * @return a description of the item this realm applies to.
	 */
	String getDescription();

	/**
	 * Test if this user can unlock the lock in this Realm.
	 * @param userId The user id.
	 * @param lock The lock to open.
	 * @return true if this user can unlock the lock in this Realm, false if not.
	 */
	boolean unlock(String userId, String lock);

	/**
	 * Test if this user has been actively granted this role in this Realm.
	 * @param userId The user id.
	 * @param role The role name.
	 * @return true if the User has been granted this role in this Realm, false if not.
	 */
	boolean hasRole(String userId, String role);

	/**
	 * Access all users who have active Role grants in the Realm.
	 * @return The Set of users ids (String) who have Role grants in the Realm.
	 */
	public Set getUsers();

	/**
	 * Access all users who have an active Role grant that includes this lock.
	 * @return The Set of user ids (String) who have a Role grant that includes this lock.
	 */
	public Set getUsersWithLock(String lock);

	/**
	 * Access all users who have an active Role grant to this role.
	 * @return The Set of user ids (String) who have a Role grant to this role.
	 */
	public Set getUsersWithRole(String role);

	/**
	 * Access the active Role granted to the user.
	 * @param userId The user id.
	 * @return The Role granted for the user, or null if none granted.
	 */
	public Role getUserRole(String userId);

	/**
	 * Access the Role granted to the user, along with the information about the grant (active, provided).
	 * @param userId The user id.
	 * @return The Grant granted for the user, or null if none granted.
	 */
	public Grant getUserGrant(String userId);

	/**
	 * Access all Grants defined for this Realm.
	 * @return The set of grants (Grant) defined for this Realm.
	 */
	public Set getGrants();

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
}



