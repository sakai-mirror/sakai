/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/security/ChefSecurity.java,v 1.10 2004/10/14 19:27:44 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.security;

// imports
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
* <p>ChefSecurity is the Course Tools Next Generation (CTNG) security service.
* It is a role based service, with roles derived (not granted) from user's
* class relationship (student, instructor, none) as reported by UMIAC.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
* @see org.chefproject.service..SecurityService
*/
public class ChefSecurity
	implements SecurityService
{
	/** Current service key for the super user status of the current user. */
	protected final String M_curUserSuperKey = getClass().getName()+".super";

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
	* @param user if null, get the session, else use this one
	* @return the User object authenticated to the current request's session.
	*/
	protected User getUser(User user)
	{
		if (user != null) return user;

		return UserDirectoryService.getCurrentUser();

	}	// getUser

	/**
	 * {@inheritdoc}
	 */
	public boolean isSuperUser()
	{
		return isSuperUser(null);
	}

	/**
	* Is this a super special super (admin or postmaster) user?
	* @return true, if the user is a cut above the rest, false if a mere mortal.
	*/
	protected boolean isSuperUser(User u)
	{
		// check current service caching, i we are doing current checking
		if (u == null)
		{
			Boolean cached = (Boolean) CurrentService.getInThread(M_curUserSuperKey);
			if (cached != null) return cached.booleanValue();
		}
		
		boolean rv = false;

		User user = getUser(u);
		if (user != null)
		{
			// these known ids are super
			if ("admin".equalsIgnoreCase(user.getId()))
			{
				rv = true;
			}

			else if ("postmaster".equalsIgnoreCase(user.getId()))
			{
				rv = true;
			}

			// if the user has site modification rights in the "!admin" site, welcome aboard!
			else
			{
				try
				{
					Realm adminRealm = RealmService.getRealm("/site/!admin");
					if (adminRealm.unlock(user, SiteService.SECURE_UPDATE_SITE, null))
					{
						rv = true;
					}
				}
				catch (Exception ignore) {}
			}
		}

		// cache in the current service, if we had checked for the current user
		if (u == null)
		{
			CurrentService.setInThread(M_curUserSuperKey, Boolean.valueOf(rv));
		}

		return rv;

	}	// isSuperUser

	/**
	 * {@inheritdoc}
	 */
	public boolean unlock(String lock, String resource)
	{
		return unlock(null, lock, resource);
	}

	/**
	 * {@inheritdoc}
	 */
	public boolean unlock(User user, String lock, String resource)
	{
		boolean rv = doUnlock(user, lock, resource);

		if (m_logger.isDebugEnabled())
		{
			User u = getUser(user);
			String uid = "";
			if (u != null) uid = u.getId();
			m_logger.debug(this + ".unlock(): " + uid + " @ " + lock + " @ " + resource + " = " + rv);
		}

		return rv;

	}	// unlock

	/**
	* Access the List the Users who can unlock the lock for use with this resource.
	* @param lock The lock id string.
	* @param reference The resource reference string.
	* @return A List (User) of the users can unlock the lock (may be empty).
	*/
	public List unlockUsers(String lock, String reference)
	{
		Set userSet = new HashSet();
		List rv = new Vector();

		if (reference == null)
		{
			m_logger.warn(this + ".unlockUsers(): null resource: " + lock);
			return rv;
		}

		//	make a reference for the resource
		Reference ref = new Reference(reference);

		// get this resource's Realms
		List realms = ref.getRealms();

		// build up a list of helper realms as we go
		List helperRealms = new Vector();

		// check in all realms
		for (Iterator it = realms.iterator(); it.hasNext(); )
		{
			Realm realm = (Realm) it.next();
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".unlockUsers(): checking realm: " + realm.getId() + " for lock: " + lock);

			userSet.addAll(realm.collectLockUsers(lock, helperRealms));

			// if the  user Realm's in the list, add the current user
			// TODO: this should be a realm service constant -ggolden
			if (realm.getId().startsWith("!user.template"))
			{
				User user = getUser(null);
				if (realm.unlock(user, lock, helperRealms))
				{
					userSet.add(user);
				}
			}
			
			// add this to the helper list for next time
			helperRealms.add(0, realm);
		}

		// convert the set of Users into a sorted List
		rv.addAll(userSet);
		Collections.sort(rv);

		return rv;

	}	// unlockUsers

	/**
	* Can the user in the security context unlock the lock for use with this resource?
	* @param user if null, use the current user, else use this one.
	* @param lock The lock id string.
	* @param resource The resource id string, or null if no resource is involved.
	* @return true, if the user can unlock the lock, false otherwise.
	*/
	protected boolean doUnlock(User user, String lock, String resource)
	{
		// if super, grant
		if (isSuperUser(user))
		{
			return true;
		}

		if (resource == null)
		{
			m_logger.warn(this + ".unlock(): null resource: " + lock);
			return false;
		}

		//	make a reference for the resource
		Reference ref = new Reference(resource);

		// get this resource's Realms
		List realms = ref.getRealms();

		// build up a list of helper realms as we go
		List helperRealms = new Vector();

		// check in all realms
		for (Iterator it = realms.iterator(); it.hasNext(); )
		{
			Realm realm = (Realm) it.next();
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".doUnlock(): checking realm: " + realm.getId() + " for lock: " + lock);

			if (realm.unlock(getUser(user), lock, helperRealms)) return true;

			// add this to the helper list for next time
			helperRealms.add(0, realm);
		}

		return false;

	}   // doUnlock

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
		m_logger.warn(this + ".addKey() [NOT SUPPORTED]: user: " + userOrGroup
						+ " lock: " + lockOrRole + " resource: " + resourceOrGroup
						+ " allow: " + allow);

	}   // addKey

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
		m_logger.warn(this + ".removeKey()[NOT SUPPORTED]: user: " + userOrGroup + " lock: "
						+ lockOrRole + " resource: " + resourceOrGroup
						+ " allow: " + allow);

	}   // removeKey

}   // ChefSecurity

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/security/ChefSecurity.java,v 1.10 2004/10/14 19:27:44 ggolden.umich.edu Exp $
*
**********************************************************************************/
