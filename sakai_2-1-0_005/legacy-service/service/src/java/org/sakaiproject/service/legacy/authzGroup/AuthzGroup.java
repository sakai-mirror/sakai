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
import java.io.Serializable;
import java.util.Set;

import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
 * <p>
 * AuthzGroup is a authorization group; a group of users, each with a role, and a set of permissions of functions made to each role.
 * </p>
 * <p>
 * AuthzGroups can related to Entities in Sakai; The entity reference forms the AuthzGroup id.
 * </p>
 * <p>
 * Special AuthzGroups not related to an entity have ids that begin with a "!".
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface AuthzGroup extends Edit, Comparable, Serializable
{
	/**
	 * Add a member to the AuthzGroup.
	 * 
	 * @param userId
	 *        The user.
	 * @param role
	 *        The role name.
	 * @param active
	 *        The active flag.
	 * @param provided
	 *        If true, from an external provider.
	 */
	void addMember(String userId, String roleId, boolean active, boolean provided);

	/**
	 * Create a new Role within this AuthzGroup.
	 * 
	 * @param id
	 *        The role id.
	 * @return the new Role.
	 * @exception IdUsedException
	 *            if the id is already a Role in this AuthzGroup.
	 */
	Role addRole(String id) throws IdUsedException;

	/**
	 * Create a new Role within this AuthzGroup, as a copy of this other role
	 * 
	 * @param id
	 *        The role id.
	 * @param other
	 *        The role to copy.
	 * @return the new Role.
	 * @exception IdUsedException
	 *            if the id is already a Role in this AuthzGroup.
	 */
	Role addRole(String id, Role other) throws IdUsedException;

	/**
	 * @return the user who created this.
	 */
	User getCreatedBy();

	/**
	 * @return the time created.
	 */
	Time getCreatedTime();

	/**
	 * @return a description of the item this realm applies to.
	 */
	String getDescription();

	/**
	 * Access the name of the role to use for giving a user membership with "maintain" access.
	 * 
	 * @return The name of the "maintain" role.
	 */
	public String getMaintainRole();

	/**
	 * Access the user's membership record for this AuthzGroup; the role, and status flags.
	 * 
	 * @param userId
	 *        The user id.
	 * @return The Membership record for the user in this AuthzGroup, or null if the use is not a member.
	 */
	public Member getMember(String userId);

	/**
	 * Access all Membership records defined for this AuthzGroup.
	 * 
	 * @return The set of Membership records (Membership) defined for this AuthzGroup.
	 */
	public Set getMembers();

	/**
	 * @return the user who last modified this.
	 */
	User getModifiedBy();

	/**
	 * @return the time last modified.
	 */
	Time getModifiedTime();

	/**
	 * Access the group id for the GroupProvider for this AuthzGroup.
	 * 
	 * @return The the group id for the GroupProvider for this AuthzGroup, or null if none defined.
	 */
	public String getProviderGroupId();

	/**
	 * Access a Role defined in this AuthzGroup.
	 * 
	 * @param id
	 *        The role id.
	 * @return The Role, if found, or null, if not.
	 */
	public Role getRole(String id);

	/**
	 * Access all Roles defined for this AuthzGroup.
	 * 
	 * @return The set of roles (Role) defined for this AuthzGroup.
	 */
	public Set getRoles();

	/**
	 * Access all roles that have been granted permission to this function.
	 * 
	 * @param function
	 *        The function to check.
	 * @return The Set of role names (String) that have been granted permission to this function.
	 */
	public Set getRolesIsAllowed(String function);

	/**
	 * Access the active role for this user's membership.
	 * 
	 * @param userId
	 *        The user id.
	 * @return The Role for this user's membership, or null if the user has no active membership.
	 */
	public Role getUserRole(String userId);

	/**
	 * Access all users who have active role membership in the AuthzGroup.
	 * 
	 * @return The Set of users ids (String) who have active role membership in the AuthzGroup.
	 */
	public Set getUsers();

	/**
	 * Access all users who have an active role membership with this role.
	 * 
	 * @return The Set of user ids (String) who have an active role membership with this role.
	 */
	public Set getUsersHasRole(String role);

	/**
	 * Access all users who have an active role membership to a role that is allowed this function.
	 * 
	 * @param function
	 *        The function to check.
	 * @return The Set of user ids (String) who have an active role membership to a role that is allowed this function.
	 */
	public Set getUsersIsAllowed(String function);

	/**
	 * Test if this user has a membership in this AuthzGroup that has this role and is active.
	 * 
	 * @param userId
	 *        The user id.
	 * @param role
	 *        The role name.
	 * @return true if the User has has a membership in this AuthzGroup that has this role and is active.
	 */
	boolean hasRole(String userId, String role);

	/**
	 * Test if this user is allowed to perform the function in this AuthzGroup.
	 * 
	 * @param userId
	 *        The user id.
	 * @param function
	 *        The function to open.
	 * @return true if this user is allowed to perform the function in this AuthzGroup, false if not.
	 */
	boolean isAllowed(String userId, String function);

	/**
	 * Is this AuthzGroup empty of any roles or membership?
	 * 
	 * @return true if the AuthzGroup is empty, false if not.
	 */
	public boolean isEmpty();

	/**
	 * Remove membership for for this user from the AuthzGroup.
	 * 
	 * @param userId
	 *        The user.
	 */
	void removeMember(String userId);

	/**
	 * Remove all membership from this AuthzGroup.
	 */
	void removeMembers();

	/**
	 * Remove this Role from this AuthzGroup. Any grants of this Role in the AuthzGroup are also removed.
	 * 
	 * @param role
	 *        The role name.
	 */
	void removeRole(String role);

	/**
	 * Remove all Roles from this AuthzGroup.
	 */
	void removeRoles();

	/**
	 * Set the role name to use for "maintain" access.
	 * 
	 * @param role
	 *        The name of the "maintain" role.
	 */
	void setMaintainRole(String role);

	/**
	 * Set the external group id for the GroupProvider for this AuthzGroup (set to null to have none).
	 * 
	 * @param id
	 *        The external group id for the GroupProvider, or null if there is to be none.
	 */
	void setProviderGroupId(String id);
	
	/**
	 * Adjust membership so that active members are all active in other, and inactive members are all defined in other
	 * @param other The other azg to adjust to.
	 * @return true if any changes were made, false if not.
	 */
	boolean keepIntersection(AuthzGroup other);
}