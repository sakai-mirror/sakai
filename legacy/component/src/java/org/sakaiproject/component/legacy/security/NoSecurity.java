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
package org.sakaiproject.component.legacy.security;

// imports
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
* <p>NoSecurity is an example implementation of the Sakai SecurityService.</p>
* <p>Work Interfaces:<ul><li>SecurityService</li></ul></p>
* <p>Implementation Design:<ul><li>
* This sample implementation lets anything go!
* </li></ul></p>
* <p>External Dependencies:<ul><li>none</li></ul></p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class NoSecurity
	implements SecurityService
{
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this + ".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* SecurityService implementation
	*******************************************************************************/

	/**
	* Get the authenticated session user
	* @param user If not null, use this user, else use the session one.
	* @return the User object authenticated to the current request's session.
	*/
	protected User getUser(User user)
	{
		if (user != null) return user;

		return UserDirectoryService.getCurrentUser();

	}	// getUser

	/**
	* Is this a super special super (admin) user?
	* @return true, if the user is a cut above the rest, false if a mere mortal.
	*/
	public boolean isSuperUser()
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".isSuperUser() true user: " + getUserId(null));
		return true;

	}	// isSuperUser

	/**
	* Can the user in the security context unlock the lock for use with this resource?
	* @param lock The lock id string.
	* @param resource The resource id string, or null if no resource is involved.
	* @return true, if the user can unlock the lock, false otherwise.
	*/
	public boolean unlock(String lock, String resource)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".unlock() true user: " + getUserId(null)
						+ " lock: " + lock + " resource: " + resource);
		return true;

	}	// unlock

	/**
	* Can the user in the security context unlock the lock for use with this resource?
	* @param lock The lock id string.
	* @param resource The resource id string, or null if no resource is involved.
	* @return true, if the user can unlock the lock, false otherwise.
	*/
	public boolean unlock(User user, String lock, String resource)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".unlock() true user: " + getUserId(user)
						+ " lock: " + lock + " resource: " + resource);
		return true;

	}	// unlock

	/**
	* Access the List of Users who can unlock the lock for use with this resource.
	* @param lock The lock id string.
	* @param reference The resource reference string.
	* @return A List (User) of the users can unlock the lock (may be empty).
	*/
	public List unlockUsers(String lock, String reference)
	{
		return new Vector();

	}	// unlockUsers

	/**
	* Add a new key.
	* @param userOrGroup The id of the user or user group which is given the key.
	* @param lockOrRole The id of the lock or role (lock group) which the key opens.
	* @param resourceOrGroup the id of the resource or resource group which restricts the key
	* (the key will work only for these resources.  null if no resource is involved).
	* @param allow true if the key allows access, false if it denys access.
	*/
	public void addKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".addKey(): user: " + userOrGroup
						+ " lock: " + lockOrRole + " resource: " + resourceOrGroup
						+ " allow: " + allow);

	}	// addKey

	/**
	* Remove any keys that exactly match this key specification.
	* @param userOrGroup The id of the user or user group which is given the key.
	* @param lockOrRole The id of the lock or role (lock group) which the key opens.
	* @param resourceOrGroup the id of the resource or resource group which restricts the key
	* (the key will work only for these resources.  null if no resource is involved).
	* @param allow true if the key allows access, false if it denys access.
	*/
	public void removeKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".removeKey(): user: " + userOrGroup + " lock: "
						+ lockOrRole + " resource: " + resourceOrGroup
						+ " allow: " + allow);

	}	// removeKey

	protected String getUserId(User u)
	{
		User user = getUser(u);
		if (user == null) return "";
		String id = user.getId();
		if (id == null) return "";
		return id;

	}	// getUserId
		
}	// NoSecurity



