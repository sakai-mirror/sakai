/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.service.legacy.authzGroup;

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
import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.service.legacy.resource.EntityProducer;

/**
 * <p>
 * AuthzGroupService manages authorization grops.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface AuthzGroupService extends EntityProducer
{
	/** This string can be used to find the service in the service manager. */
	static final String SERVICE_NAME = AuthzGroupService.class.getName();

	/** This string starts the references to resources in this service. */
	static final String REFERENCE_ROOT = Entity.SEPARATOR + "realm";

	/** Name for the event of adding an AuthzGroup. */
	static final String SECURE_ADD_AUTHZ_GROUP = "realm.add";

	/** Name for the event of removing an AuthzGroup. */
	static final String SECURE_REMOVE_AUTHZ_GROUP = "realm.del";

	/** Name for the event of updating an AuthzGroup. */
	static final String SECURE_UPDATE_AUTHZ_GROUP = "realm.upd";

	/** Name for the event of updating ones own relationship in an AuthzGroup. */
	static final String SECURE_UPDATE_OWN_AUTHZ_GROUP = "realm.upd.own";

	/** Standard role name for the anon. role. */
	static final String ANON_ROLE = ".anon";

	/** Standard role name for the auth. role. */
	static final String AUTH_ROLE = ".auth";

	/**
	 * Access a list of AuthzGroups that meet specified criteria, naturally sorted.
	 * 
	 * @param criteria
	 *        Selection criteria: AuthzGroups returned will match this string somewhere in their id, or provider group id.
	 * @param page
	 *        The PagePosition subset of items to return.
	 * @return The List (AuthzGroup) that meet specified criteria.
	 */
	List getAuthzGroups(String criteria, PagingPosition page);

	/**
	 * Count the AuthzGroups that meet specified criteria.
	 * 
	 * @param criteria
	 *        Selection criteria: AuthzGroups returned will match this string somewhere in their id, or provider group id.
	 * @return The count of AuthzGroups that meet specified criteria.
	 */
	int countAuthzGroups(String criteria);

	/**
	 * Access an AuthzGroup.
	 * 
	 * @param id
	 *        The id string.
	 * @return The AuthzGroup.
	 * @exception IdUnusedException
	 *            if not found.
	 */
	AuthzGroup getAuthzGroup(String id) throws IdUnusedException;

	/**
	 * Check permissions for updating an AuthzGroup.
	 * 
	 * @param id
	 *        The id.
	 * @return true if the user is allowed to update the AuthzGroup, false if not.
	 */
	boolean allowUpdate(String id);

	/**
	 * Save the changes made to the AuthzGroup. The AuthzGroup must already exist, and the user must have permission to update.
	 * 
	 * @param azGroup
	 *        The AuthzGroup to save.
	 * @exception IdUnusedException
	 *            if the AuthzGroup id is not defined.
	 * @exception PermissionException
	 *            if the current user does not have permission to update the AuthzGroup.
	 */
	void save(AuthzGroup azGroup) throws IdUnusedException, PermissionException;

	/**
	 * Check permissions for adding an AuthzGroup.
	 * 
	 * @param id
	 *        The authzGroup id.
	 * @return true if the current user is allowed add the AuthzGroup, false if not.
	 */
	boolean allowAdd(String id);

	/**
	 * Add a new AuthzGroup
	 * 
	 * @param id
	 *        The AuthzGroup id.
	 * @return The new AuthzGroup.
	 * @exception IdInvalidException
	 *            if the id is invalid.
	 * @exception IdUsedException
	 *            if the id is already used.
	 * @exception PermissionException
	 *            if the current user does not have permission to add the AuthzGroup.
	 */
	AuthzGroup addAuthzGroup(String id) throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * Add a new AuthzGroup, as a copy of another AuthzGroup (except for id), and give a user "maintain" access based on the other's definition of "maintain".
	 * 
	 * @param id
	 *        The id.
	 * @param other
	 *        The AuthzGroup to copy into this new AuthzGroup.
	 * @param maintainUserId
	 *        Optional user id to get "maintain" access, or null if none.
	 * @return The new AuthzGroup object.
	 * @exception IdInvalidException
	 *            if the id is invalid.
	 * @exception IdUsedException
	 *            if the id is already used.
	 * @exception PermissionException
	 *            if the current user does not have permission to add the AuthzGroup.
	 */
	AuthzGroup addAuthzGroup(String id, AuthzGroup other, String maintainUserId) throws IdInvalidException, IdUsedException,
			PermissionException;

	/**
	 * Check permissions for removing an AuthzGroup.
	 * 
	 * @param id
	 *        The AuthzGroup id.
	 * @return true if the user is allowed to remove the AuthzGroup, false if not.
	 */
	boolean allowRemove(String id);

	/**
	 * Remove this AuthzGroup.
	 * 
	 * @param azGroup
	 *        The AuthzGroup to remove.
	 * @exception PermissionException
	 *            if the current user does not have permission to remove this AuthzGroup.
	 */
	void removeAuthzGroup(AuthzGroup azGroup) throws PermissionException;

	/**
	 * Remove the AuthzGroup with this id, if it exists (fails quietly if not).
	 * 
	 * @param id
	 *        The AuthzGroup id.
	 * @exception PermissionException
	 *            if the current user does not have permission to remove this AthzGroup.
	 */
	void removeAuthzGroup(String id) throws PermissionException;

	/**
	 * Access the internal reference which can be used to access the AuthzGroup from within the system.
	 * 
	 * @param id
	 *        The AuthzGroup id.
	 * @return The the internal reference which can be used to access the AuthzGroup from within the system.
	 */
	String authzGroupReference(String id);

	/**
	 * Cause the current user to join the given site. Fails if site's not defined, not joinable, or does not have the proper joiner role defined. Security is just authzGroup.upd.own.
	 * 
	 * @param siteId
	 *        the id of the site.
	 * @throws IdUnusedException
	 *         if the id is not a valid site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to join this site.
	 * @exception InUseException
	 *            if the site's authzGroup is otherwise being edited.
	 */
	void joinSite(String siteId) throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Cause the current user to unjoin the given site. Fails if site's not defined. Security is just SECURE_UPDATE_OWN_AUTHZ_GROUP.
	 * 
	 * @param siteId
	 *        the id of the site.
	 * @throws IdUnusedException
	 *         if the id is not a valid site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to unjoin this site.
	 */
	void unjoinSite(String siteId) throws IdUnusedException, PermissionException;

	/**
	 * Check permissions for unjoining a site.
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to unjoin the site, false if not.
	 */
	public boolean allowUnjoinSite(String id);

	/**
	 * Test if this user is allowed to perform the function in the named AuthzGroup.
	 * 
	 * @param userId
	 *        The user id.
	 * @param function
	 *        The function to open.
	 * @param azGroupId
	 *        The AuthzGroup id to consult, if it exists.
	 * @return true if this user is allowed to perform the function in the named AuthzGroup, false if not.
	 */
	boolean isAllowed(String userId, String function, String azGroupId);

	/**
	 * Test if this user is allowed to perform the function in the named AuthzGroups.
	 * 
	 * @param userId
	 *        The user id.
	 * @param function
	 *        The function to open.
	 * @param azGroups
	 *        A collection of AuthzGroup ids to consult.
	 * @return true if this user is allowed to perform the function in the named AuthzGroups, false if not.
	 */
	boolean isAllowed(String userId, String function, Collection azGroups);

	/**
	 * Get the set of user ids of users who are allowed to perform the function in the named AuthzGroups.
	 * 
	 * @param function
	 *        The function to check.
	 * @param azGroups
	 *        A collection of the ids of AuthzGroups to consult.
	 * @return the Set (String) of user ids of users who are allowed to perform the function in the named AuthzGroups.
	 */
	Set getUsersIsAllowed(String function, Collection azGroups);

	/**
	 * Get the set of AuthzGroup ids in which this user is allowed to perform this function.
	 * 
	 * @param userId
	 *        The user id.
	 * @param function
	 *        The function to check.
	 * @return the Set (String) of AuthzGroup ids in which this user is allowed to perform this function.
	 */
	Set getAuthzGroupsIsAllowed(String userId, String function);

	/**
	 * Get the set of functions that users with this role in these AuthzGroups are allowed to perform.
	 * 
	 * @param role
	 *        The role name.
	 * @param azGroups
	 *        A collection of AuthzGroup ids to consult.
	 * @return the Set (String) of functions that users with this role in these AuthzGroups are allowed to perform
	 */
	Set getAllowedFunctions(String role, Collection azGroups);

	/**
	 * Refresh this user's AuthzGroup external definitions.
	 * 
	 * @param userId
	 *        The user id.
	 */
	void refreshUser(String userId);
}
