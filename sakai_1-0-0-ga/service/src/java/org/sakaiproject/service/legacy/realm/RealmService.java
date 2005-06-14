/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmService.java,v 1.6 2004/07/07 17:13:28 ggolden.umich.edu Exp $
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

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>RealmService manages CHEF Realms.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public interface RealmService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = RealmService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "realm";

	/** Name for the event of accessing a realm. */
	//public static final String SECURE_ACCESS_REALM = "realm.access";

	/** Name for the event of adding a realm. */
	public static final String SECURE_ADD_REALM = "realm.add";

	/** Name for the event of removing a realm. */
	public static final String SECURE_REMOVE_REALM = "realm.del";

	/** Name for the event of updating a realm. */
	public static final String SECURE_UPDATE_REALM = "realm.upd";

	/** Name for the event of updating ones own relationship in a realm. */
	public static final String SECURE_UPDATE_OWN_REALM = "realm.upd.own";

	/**
	* Access all realm objects.
	* @return A List (Realm) of all defined realms.
	*/
	public List getRealms();

	/**
	* Access a realm object.
	* @param id The realm id string.
	* @return A realm object containing the realm information.
	* @exception IdUnusedException if not found.
	*/
	public Realm getRealm(String id)
		throws IdUnusedException;

	/**
	* Access a realm object.
	* This one is a strange one.  It's given two chances (two ids), and will find one, or the other.
	* or return null.  It doesn't throw exceptions.  Good for use in exception-phobic Velocity.
	* @param id1 The realm id we really want.
	* @param id2 Another realm id to get if the first is not defined.
	* @return A realm object containing the realm information for id1, or id2, or a null if neither are found.
	*/
	public Realm getRealm(String id1, String id2);

	/**
	* Check permissions for updating a realm.
	* @param id The realm id.
	* @return true if the user is allowed to update the realm, false if not.
	*/
	public boolean allowUpdateRealm(String id);

	/**
	* Get a locked realm object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The realm id string.
	* @return A RealmEdit object for editing.
	* @exception IdUnusedException if not found, or if not an RealmEdit object
	* @exception PermissionException if the current user does not have permission to edit with this realm.
	* @exception InUseException if the realm is being edited by another user.
	*/
	public RealmEdit editRealm(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a RealmEdit object, and release the lock.
	* The RealmEdit is disabled, and not to be used after this call.
	* @param realm The RealmEdit object to commit.
	*/
	public void commitEdit(RealmEdit realm);

	/**
	* Cancel the changes made to a RealmEdit object, and release the lock.
	* The RealmEdit is disabled, and not to be used after this call.
	* @param realm The RealmEdit object to commit.
	*/
	public void cancelEdit(RealmEdit realm);

	/**
	* check permissions for addRealm().
	* @param id The realm id.
	* @return true if the current user is allowed to addRealm(id), false if not.
	*/
	public boolean allowAddRealm(String id);	

	/**
	* Add a new realm.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The realm id.
	* @return The new locked realm object.
	* @exception IdInvalidException if the realm id is invalid.
	* @exception IdUsedException if the realm id is already used.
	* @exception PermissionException if the current user does not have permission to add a realm.
	*/
	public RealmEdit addRealm(String id)
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
	public RealmEdit addRealm(String id, Realm other)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* check permissions for removeRealm().
	* @param id The realm id.
	* @return true if the realm is allowed to removeRealm(id), false if not.
	*/
	public boolean allowRemoveRealm(String id);

	/**
	* Remove this realm's information from the directory
	* - it must be a realm with a lock from editRealm().
	* The RealmEdit is disabled, and not to be used after this call.
	* @param id The realm id.
	* @exception PermissionException if the current user does not have permission to remove this realm.
	*/
	public void removeRealm(RealmEdit realm)
		throws PermissionException;

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The realm id.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String realmReference(String id);

	/**
	* Update this resource's realm so that it matches the setting for public view.
	* @param ref The resource reference
	* @param pubview The public view setting.
	* @throws InUseException if the ream is not availabe for modification.
	*/
	public void setPubView(String ref, boolean pubview)
		throws InUseException;

	/**
	* Does this resource support public view?
	* @param ref The resource reference
	* @return true if this resource supports public view, false if not.
	*/
	public boolean getPubView(String ref);

	/**
	* Does this resource inherit a public view setting from it's relevant set of realms, other than its own?
	* @param ref The resource reference
	* @return true if this resource inherits public view, false if not.
	*/
	public boolean getPubViewInheritance(String ref);

	/**
	 * Cause the current user to join the given site.
	 * Fails if site's not defined, not joinable, or does not have the proper joiner role defined.
	 * Security is just realm.upd.own.
	 * @param siteId the id of the site.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to join this site.
	 * @exception InUseException if the site's realm is otherwise being edited.
	 */
	public void joinSite(String siteId)
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
	public void unjoinSite(String siteId)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Create a new abilities object.
	 */
	public Abilities newAbilities();

}	// RealmService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmService.java,v 1.6 2004/07/07 17:13:28 ggolden.umich.edu Exp $
*
**********************************************************************************/
