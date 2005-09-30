/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/RealmService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>RealmService manages Realms.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface RealmService
{
	/** This string can be used to find the service in the service manager. */
	static final String SERVICE_NAME = RealmService.class.getName();

	/** This string starts the references to resources in this service. */
	static final String REFERENCE_ROOT = Resource.SEPARATOR + "realm";

	/** Name for the event of accessing a realm. */
	//static final String SECURE_ACCESS_REALM = "realm.access";

	/** Name for the event of adding a realm. */
	static final String SECURE_ADD_REALM = "realm.add";

	/** Name for the event of removing a realm. */
	static final String SECURE_REMOVE_REALM = "realm.del";

	/** Name for the event of updating a realm. */
	static final String SECURE_UPDATE_REALM = "realm.upd";

	/** Name for the event of updating ones own relationship in a realm. */
	static final String SECURE_UPDATE_OWN_REALM = "realm.upd.own";

	/** Standard role name for the anon. role. */
	static final String ANON_ROLE = ".anon";

	/** Standard role name for the auth. role. */
	static final String AUTH_ROLE = ".auth";

	/**
	 * Access a list of Realm objets that meet specified criteria, naturally sorted.
	 * @param criteria Selection criteria: realms returned will match this string somewhere in their
	 * 			id, or provider realm id.
	 * @param page The PagePosition subset of items to return.
	 * @return The List (Realm) of Rrealm objets that meet specified criteria.
	 */
	List getRealms(String criteria, PagingPosition page);

	/**
	 * Count the Realm objets that meet specified criteria.
	 * @param criteria Selection criteria: realms returned will match this string somewhere in their
	 * 			id, or provider realm id.
	 * @return The count of Realm objets that meet specified criteria.
	 */
	int countRealms(String criteria);

	/**
	 * Access a realm object.
	 * @param id The realm id string.
	 * @return A realm object containing the realm information.
	 * @exception IdUnusedException if not found.
	 */
	Realm getRealm(String id)
		throws IdUnusedException;

	/**
	 * Check permissions for updating a realm.
	 * @param id The realm id.
	 * @return true if the user is allowed to update the realm, false if not.
	 */
	boolean allowUpdateRealm(String id);

	/**
	 * Get a locked realm object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	 * @param id The realm id string.
	 * @return A RealmEdit object for editing.
	 * @exception IdUnusedException if not found, or if not an RealmEdit object
	 * @exception PermissionException if the current user does not have permission to edit with this realm.
	 * @exception InUseException if the realm is being edited by another user.
	 */
	RealmEdit editRealm(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Commit the changes made to a RealmEdit object, and release the lock.
	 * The RealmEdit is disabled, and not to be used after this call.
	 * @param realm The RealmEdit object to commit.
	 */
	void commitEdit(RealmEdit realm);

	/**
	 * Cancel the changes made to a RealmEdit object, and release the lock.
	 * The RealmEdit is disabled, and not to be used after this call.
	 * @param realm The RealmEdit object to commit.
	 */
	void cancelEdit(RealmEdit realm);

	/**
	 * Check permissions for addRealm().
	 * @param id The realm id.
	 * @return true if the current user is allowed to addRealm(id), false if not.
	 */
	boolean allowAddRealm(String id);	

	/**
	 * Add a new realm.  Must commitEdit() to make official, or cancelEdit() when done!
	 * @param id The realm id.
	 * @return The new locked realm object.
	 * @exception IdInvalidException if the realm id is invalid.
	 * @exception IdUsedException if the realm id is already used.
	 * @exception PermissionException if the current user does not have permission to add a realm.
	 */
	RealmEdit addRealm(String id)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * Add a new realm, as a copy of another realm (except for id).
	 * Must commitEdit() to make official, or cancelEdit() when done!
	 * @param id The realm id.
	 * @param other The Realm to copy into this new Realm.
	 * @return The new locked realm object.
	 * @exception IdInvalidException if the realm id is invalid.
	 * @exception IdUsedException if the realm id is already used.
	 * @exception PermissionException if the current user does not have permission to add a realm.
	 */
	RealmEdit addRealm(String id, Realm other)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * check permissions for removeRealm().
	 * @param id The realm id.
	 * @return true if the realm is allowed to removeRealm(id), false if not.
	 */
	boolean allowRemoveRealm(String id);

	/**
	 * Remove this realm's information from the directory
	 * - it must be a realm with a lock from editRealm().
	 * The RealmEdit is disabled, and not to be used after this call.
	 * @param realm The realm already locked for edit.
	 * @exception PermissionException if the current user does not have permission to remove this realm.
	 */
	void removeRealm(RealmEdit realm)
		throws PermissionException;

	/**
	 * Remove this realm's information from the directory, if it exists (fails quietly if not).
	 * @param id The realm id.
	 * @exception PermissionException if the current user does not have permission to remove this realm.
	 */
	void removeRealm(String id)
		throws PermissionException;

	/**
	 * Access the internal reference which can be used to access the resource from within the system.
	 * @param id The realm id.
	 * @return The the internal reference which can be used to access the resource from within the system.
	 */
	String realmReference(String id);

	/**
	 * Cause the current user to join the given site.
	 * Fails if site's not defined, not joinable, or does not have the proper joiner role defined.
	 * Security is just realm.upd.own.
	 * @param siteId the id of the site.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to join this site.
	 * @exception InUseException if the site's realm is otherwise being edited.
	 */
	void joinSite(String siteId)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Cause the current user to unjoin the given site.
	 * Fails if site's not defined.
	 * Security is just realm.upd.own.
	 * @param siteId the id of the site.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to unjoin this site.
	 * @exception InUseException if the site's realm is otherwise being edited.
	 */
	void unjoinSite(String siteId)
		throws IdUnusedException, PermissionException, InUseException;
		
	/**
	 * check permissions for unjoin() - unjoining the site and removing all role relationships.
	 * @param id The site id.
	 * @return true if the user is allowed to unjoin(id), false if not.
	 */
	public boolean allowUnjoinSite(String id);

	/**
	 * Test if this user can unlock the lock in the named realm.
	 * @param userId The user id.
	 * @param lock The lock to open.
	 * @param realmId The realm id to consult, if it exists.
	 * @return true if this user can unlock the lock in this realm, false if not.
	 */
	boolean unlock(String userId, String lock, String realmId);

	/**
	 * Test if this user can unlock the lock in the named realms.
	 * @param userId The user id.
	 * @param lock The lock to open.
	 * @param realms A collection of realm ids to consult.
	 * @return true if this user can unlock the lock in these realms, false if not.
	 */
	boolean unlock(String userId, String lock, Collection realms);

	/**
	 * Get the set of user ids which are granted roles which can unlock this lock in these realms.
	 * @param lock The lock.
	 * @param realms A collection of realm ids to consult.
	 * @return the Set (String) of user ids which are granted roles which can unlock this lock in these realms.
	 */
	Set getUsers(String lock, Collection realms);

	/**
	 * Get the set of realm ids in which this user is granted roles which can unlock this lock.
	 * @param userId  The user id.
	 * @param lock The lock.
	 * @return the Set (String) of realm ids in which this user is granted roles which can unlock this lock.
	 */
	Set unlockRealms(String userId, String lock);

	/**
	 * Get the set of locks that this role contains in these realms.
	 * @param role The role.
	 * @param realms A collection of realm ids to consult.
	 * @return the Set (String) of locks that this role contains in these realms.
	 */
	Set getLocks(String role, Collection realms);

	/**
	 * Refresh this user's realm external definitions
	 * @param userId The user id.
	 */
	void refreshUser(String userId);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/RealmService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
