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

import java.util.Collection;

/**
* <p>UserDirectoryProvider is the Interface for CHEF user directory information providers.
* These are used by a user directory service to access external user information.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface UserDirectoryProvider
{
	/**
	 * Authenticate a user / password.
	 * If the user edit exists it may be modified, and will be stored if...
	 * @param id The user id.
	 * @param edit The UserEdit matching the id to be authenticated (and updated) if we have one.
	 * @param password The password.
	 * @return true if authenticated, false if not.
	 */
	boolean authenticateUser(String id, UserEdit edit, String password);

	/**
	 * Will this provider update user records on successfull authentication?
	 * If so, the UserDirectoryService will cause these updates to be stored.
	 * @return true if the user record may be updated after successfull authentication, false if not.
	 */
	boolean updateUserAfterAuthentication();

	/**
	 * Remove any authentication traces for the current user / request
	 */
	void destroyAuthentication();

	/**
	 * See if a user by this id is known to the provider.
	 * @param id The user id string.
	 * @return true if a user by this id exists, false if not.
	 */
	boolean userExists(String id);

	/**
	 * Access a user object.  Update the object with the information found.
	 * @param edit The user object (id is set) to fill in.
	 * @return true if the user object was found and information updated, false if not.
	 */
	boolean getUser(UserEdit edit);

	/**
	 * Access a collection of UserEdit objects; if the user is found, update the information, otherwise remove the UserEdit object from the collection.
	 * @param users The UserEdit objects (with id set) to fill in or remove.
	 */
	void getUsers(Collection users);

	/**
	 * Find a user object who has this email address. Update the object with the information found.
	 * @param email The email address string.
	 * @return true if the user object was found and information updated, false if not.
	 */
	boolean findUserByEmail(UserEdit edit, String email);
}



