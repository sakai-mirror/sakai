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
package org.sakaiproject.service.legacy.user;

// imports
import java.util.Collection;
import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.w3c.dom.Element;

/**
* <p>UserDirectory is the Interface for CHEF user directory services. Everything known about a user
* in CHEF is stored in the user directory (with the exception of the user's authentication password).</p>
* <p>Each service component that provides this interface handles location of user information (objects
* following the User interface) based on a user id string (getUser()).  They also handle adding new user
* objects into the directory and updating existing user objects.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.User
*/
public interface UserDirectoryService
	extends EntityProducer
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = UserDirectoryService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Entity.SEPARATOR + "user";

	/** Name for the event of accessing a user. */
	//public static final String SECURE_ACCESS_USER = "user.access";

	/** Name for the event of adding a group. */
	public static final String SECURE_ADD_USER = "user.add";

	/** Name for the event of removing a user. */
	public static final String SECURE_REMOVE_USER = "user.del";

	/** Name for the event of updating one's own user info. */
	public static final String SECURE_UPDATE_USER_OWN = "user.upd.own";

	/** Name for the event of updating any user info. */
	public static final String SECURE_UPDATE_USER_ANY = "user.upd.any";

	/**
	* Access a user object.
	* @param id The user id string.
	* @return A user object containing the user information
	* @exception IdUnusedException if not found
	*/
	public User getUser(String id)
		throws IdUnusedException;

	/**
	* Access a bunch of user object.
	* @param ids The Collection (String) of user ids.
	* @return A List (User) of user objects for valid ids.
	*/
	public List getUsers(Collection ids);

	/**
	* Access the user object associated with the "current" request.
	* @return The current user (may be anon).
	*/
	public User getCurrentUser();

	/**
	* Find the user objects which have this email address.
	* @param email The email address string.
	* @return A Collection (User) of user objects which have this email address (may be empty).
	*/
	public Collection findUsersByEmail(String email);

	/**
	* check permissions for editUser()
	* @param id The user id.
	* @return true if the user is allowed to update the user, false if not.
	*/
	public boolean allowUpdateUser(String id);

	/**
	* Get a locked user object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The user id string.
	* @return A UserEdit object for editing.
	* @exception IdUnusedException if not found, or if not an UserEdit object
	* @exception PermissionException if the current user does not have permission to mess with this user.
	* @exception InUseException if the User object is locked by someone else.
	*/
	public UserEdit editUser(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a UserEdit object, and release the lock.
	* The UserEdit is disabled, and not to be used after this call.
	* @param user The UserEdit object to commit.
	*/
	public void commitEdit(UserEdit user);

	/**
	* Cancel the changes made to a UserEdit object, and release the lock.
	* The UserEdit is disabled, and not to be used after this call.
	* @param user The UserEdit object to commit.
	*/
	public void cancelEdit(UserEdit user);

	/**
	* Access the anonymous user object.
	* @return the anonymous user object.
	*/
	public User getAnonymousUser();

	/**
	* Access all user objects - known to us (not from external providers).
	* @return A list of user objects containing each user's information.
	* @exception IdUnusedException if not found.
	*/
	public List getUsers();

	/**
	* Find all the users within the record range given (sorted by sort name).
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (User) of all the users within the record range given (sorted by sort name).
	*/
	public List getUsers(int first, int last);

	/**
	* Count all the users.
	* @return The count of all users.
	*/
	public int countUsers();

	/**
	* Search all the users that match this criteria in id or email, first or last name, returning a subset of records
	* within the record range given (sorted by sort name).
	* @param criteria The search criteria.
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (User) of all the aliases matching the criteria, within the record range given (sorted by sort name).
	*/
	public List searchUsers(String criteria, int first, int last);

	/**
	* Count all the users that match this criteria in id or target, first or last name.
	* @return The count of all users matching the criteria.
	*/
	public int countSearchUsers(String criteria);

	/**
	* check permissions for addUser().
	* @param id The group id.
	* @return true if the user is allowed to addUser(id), false if not.
	*/
	public boolean allowAddUser(String id);	

	/**
	* Add a new user to the directory.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The user id.
	* @return A locked UserEdit object (reserving the id).
	* @exception IdInvalidException if the user id is invalid.
	* @exception IdUsedException if the user id is already used.
	* @exception PermissionException if the current user does not have permission to add a user.
	*/
	public UserEdit addUser(String id)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* Add a new user to the directory, complete in one operation.
	* @param id The user id.
	* @param firstName The user first name.
	* @param lastName The user last name.
	* @param email The user email.
	* @param pw The user password.
	* @param type The user type.
	* @param properties Other user properties.
	* @return The User object created.
	* @exception IdInvalidException if the user id is invalid.
	* @exception IdUsedException if the user id is already used.
	* @exception PermissionException if the current user does not have permission to add a user.
	*/
	public User addUser(String id, String firstName, String lastName, String email, String pw, String type, ResourceProperties properties)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* Add a new user to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the user.
	* @return A locked UserEdit object (reserving the id).
	* @exception IdInvalidException if the user id is invalid.
	* @exception IdUsedException if the user id is already used.
	* @exception PermissionException if the current user does not have permission to add a user.
	*/
	public UserEdit mergeUser(Element el)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* check permissions for removeUser().
	* @param id The group id.
	* @return true if the user is allowed to removeUser(id), false if not.
	*/
	public boolean allowRemoveUser(String id);

	/**
	* Remove this user's information from the directory - it must be a user with a lock from editUser().
	* The UserEdit is disabled, and not to be used after this call.
	* @param user The locked user object to remove.
	* @exception PermissionException if the current user does not have permission to remove this user.
	*/
	public void removeUser(UserEdit user)
		throws PermissionException;

	/**
	* Authenticate a user / password.
	* @param id The user id.
	* @param password The password.
	* @return The User object of the authenticated user if successfull, null if not.
	*/
	public User authenticate(String id, String password);

	/**
	 * Remove authentication for the current user.
	 */
	public void destroyAuthentication();

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The user id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String userReference(String id);

	/**
	 * Adjust an email so that the user case is preserved, but the domain case is lowered
	 * @param email The email address to adjust.
	 * @return The adjusted email address.
	 */
	public String normalizeEmailAddress(String email);

}	// UserDirectoryService



