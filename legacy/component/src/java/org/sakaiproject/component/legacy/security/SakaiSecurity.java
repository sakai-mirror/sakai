/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/security/SakaiSecurity.java,v 1.2 2005/05/14 23:54:49 ggolden.umich.edu Exp $
 *
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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>
 * SakaiSecurity is a Sakai security service.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SakaiSecurity implements SecurityService, CacheRefresher
{
	/** Current service key for the super user status of the current user. */
	protected final String M_curUserSuperKey = getClass().getName() + ".super";

	/** A cache of calls to the service and the results. */
	protected Cache m_callCache = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies, configuration, and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * 
	 * @param service
	 *        The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** The # minutes to cache the security answers. 0 disables the cache. */
	protected int m_cacheMinutes = 3;

	/**
	 * Set the # minutes to cache a security answer.
	 * 
	 * @param time
	 *        The # minutes to cache a security answer (as an integer string).
	 */
	public void setCacheMinutes(String time)
	{
		m_cacheMinutes = Integer.parseInt(time);
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// <= 0 minutes indicates no caching desired
		if (m_cacheMinutes > 0)
		{
			// build a synchronized map for the call cache, automatiaclly checking for expiration every 15 mins.
			m_callCache = MemoryService.newHardCache(this, 15 * 60);
		}

		m_logger.info(this + ".init() - caching minutes: " + m_cacheMinutes);
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * SecurityService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public boolean isSuperUser()
	{
		return isSuperUser(UserDirectoryService.getCurrentUser());
	}

	/**
	 * Is this a super special super (admin or postmaster) user?
	 * 
	 * @return true, if the user is a cut above the rest, false if a mere mortal.
	 */
	protected boolean isSuperUser(User user)
	{
		if (user == null) return false;

		// check the cache
		String command = "super@" + user.getId();
		if ((m_callCache != null) && (m_callCache.containsKey(command)))
		{
			boolean rv = ((Boolean) m_callCache.get(command)).booleanValue();
			// m_logger.info("security super hit: " + user.getId() + " " + rv);
			return rv;
		}

		boolean rv = false;

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
			if (RealmService.unlock(user.getId(), SiteService.SECURE_UPDATE_SITE, "/site/!admin"))
			{
				rv = true;
			}
		}

		// cache
		// m_logger.info("security super miss: " + user.getId() + " " + rv);
		if (m_callCache != null) m_callCache.put(command, Boolean.valueOf(rv), m_cacheMinutes * 60);

		return rv;

	} // isSuperUser

	/**
	 * {@inheritDoc}
	 */
	public boolean unlock(String lock, String resource)
	{
		return unlock(null, lock, resource);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean unlock(User u, String lock, String resource)
	{
		// pick up the current user if needed
		User user = u;
		if (user == null)
		{
			user = UserDirectoryService.getCurrentUser();
		}

		if (user == null || lock == null || resource == null)
		{
			m_logger.warn(this + ".unlock(): null: " + user + " " + lock + " " + resource);
			return false;
		}

		// if super, grant
		if (isSuperUser(user))
		{
			return true;
		}

		// check the cache
		String command = "unlock@" + user.getId() + "@" + lock + "@" + resource;
		if ((m_callCache != null) && (m_callCache.containsKey(command)))
		{
			boolean rv = ((Boolean) m_callCache.get(command)).booleanValue();
			// m_logger.info("security unlock hit: " + user.getId() + " " + lock + " " + resource + " " + rv);
			return rv;
		}

		//	make a reference for the resource
		Reference ref = new Reference(resource);

		// get this resource's Realms
		List realms = ref.getRealms();
		boolean rv = RealmService.unlock(user.getId(), lock, realms);

		if (m_logger.isDebugEnabled())
		{
			m_logger.debug(this + ".unlock(): " + user.getId() + " @ " + lock + " @ " + resource + " = " + rv);
		}

		// cache
		// m_logger.info("security unlock miss: " + user.getId() + " " + lock + " " + resource + " " + rv);
		if (m_callCache != null) m_callCache.put(command, Boolean.valueOf(rv), m_cacheMinutes * 60);

		return rv;

	} // unlock

	/**
	 * Access the List the Users who can unlock the lock for use with this resource.
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param reference
	 *        The resource reference string.
	 * @return A List (User) of the users can unlock the lock (may be empty).
	 */
	public List unlockUsers(String lock, String reference)
	{
		if (reference == null)
		{
			m_logger.warn(this + ".unlockUsers(): null resource: " + lock);
			return new Vector();
		}

		//	make a reference for the resource
		Reference ref = new Reference(reference);

		// get this resource's Realms
		List realms = ref.getRealms();

		// get the users who can unlock in these realms
		List ids = new Vector();
		ids.addAll(RealmService.getUsers(lock, realms));

		// convert the set of Users into a sorted list of users
		List users = UserDirectoryService.getUsers(ids);
		Collections.sort(users);

		return users;

	} // unlockUsers

	/**
	 * Add a new key.
	 * 
	 * @param userOrGroup
	 *        The id of the user or user group which is given the key.
	 * @param lockOrRole
	 *        The id of the lock or role (lock group) which the key opens.
	 * @param resourceOrGroup
	 *        the id of the resource or resource group which restricts the key (the key will work only for these resources. null if no resource is involved).
	 * @param allow
	 *        true if the key allows access, false if it denys access.
	 */
	public void addKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow)
	{
		m_logger.warn(this + ".addKey() [NOT SUPPORTED]: user: " + userOrGroup + " lock: " + lockOrRole + " resource: "
				+ resourceOrGroup + " allow: " + allow);

	} // addKey

	/**
	 * Remove any keys that exactly match this key specification.
	 * 
	 * @param userOrGroup
	 *        The id of the user or user group which is given the key.
	 * @param lockOrRole
	 *        The id of the lock or role (lock group) which the key opens.
	 * @param resourceOrGroup
	 *        the id of the resource or resource group which restricts the key (the key will work only for these resources. null if no resource is involved).
	 * @param allow
	 *        true if the key allows access, false if it denys access.
	 */
	public void removeKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow)
	{
		m_logger.warn(this + ".removeKey()[NOT SUPPORTED]: user: " + userOrGroup + " lock: " + lockOrRole + " resource: "
				+ resourceOrGroup + " allow: " + allow);

	} // removeKey

	/**********************************************************************************************************************************************************************************************************************************************************
	 * CacheRefresher implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Get a new value for this key whose value has already expired in the cache.
	 * 
	 * @param key
	 *        The key whose value has expired and needs to be refreshed.
	 * @param oldValue
	 *        The old exipred value of the key.
	 * @param event
	 *        The event which triggered this refresh.
	 * @return a new value for use in the cache for this key; if null, the entry will be removed.
	 */
	public Object refresh(Object key, Object oldValue, Event event)
	{
		// instead of refreshing when an entry expires, let it go and we'll get it again if needed -ggolden
		return null;

	} // refresh
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/security/SakaiSecurity.java,v 1.2 2005/05/14 23:54:49 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
