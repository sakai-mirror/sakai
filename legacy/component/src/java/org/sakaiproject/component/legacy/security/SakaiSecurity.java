/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.api.kernel.thread_local.ThreadLocalManager;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.MultiRefCache;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.security.SecurityAdvisor;
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
public class SakaiSecurity implements SecurityService
{
	/** Current service key for the super user status of the current user. */
	//protected final String M_curUserSuperKey = getClass().getName() + ".super";

	/** A cache of calls to the service and the results. */
	protected MultiRefCache m_callCache = null;

	/** ThreadLocalManager key for our SecurityAdvisor Stack. */
	protected final static String ADVISOR_STACK = "SakaiSecurity.advisor.stack";
	
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

	/** Dependency: the current manager. */
	protected ThreadLocalManager m_threadLocalManager = null;

	/**
	 * Dependency - set the current manager.
	 * 
	 * @param value
	 *        The current manager.
	 */
	public void setThreadLocalManager(ThreadLocalManager manager)
	{
		m_threadLocalManager = manager;
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
			m_callCache = MemoryService.newMultiRefCache(15 * 60);
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
		// if no user or the no-id user (i.e. the anon user)
		if ((user == null) || (user.getId().length() == 0)) return false;

		// check the cache
		String command = "super@" + user.getId();
		if ((m_callCache != null) && (m_callCache.containsKey(command)))
		{
			boolean rv = ((Boolean) m_callCache.get(command)).booleanValue();
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
			if (AuthzGroupService.isAllowed(user.getId(), SiteService.SECURE_UPDATE_SITE, "/site/!admin"))
			{
				rv = true;
			}
		}

		// cache
		if (m_callCache != null)
		{
			Collection azgIds = new Vector();
			azgIds.add("/site/!admin");
			m_callCache.put(command, Boolean.valueOf(rv), m_cacheMinutes * 60, null, azgIds);
		}

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
	public boolean unlock(User u, String function, String entityRef)
	{
		// pick up the current user if needed
		User user = u;
		if (user == null)
		{
			user = UserDirectoryService.getCurrentUser();
		}
		
		// make sure we have complete parameters
		if (user == null || function == null || entityRef == null)
		{
			m_logger.warn(this + ".unlock(): null: " + user + " " + function + " " + entityRef);
			return false;
		}

		// if super, grant
		if (isSuperUser(user))
		{
			return true;
		}

		// let the advisors have a crack at it, if we have any
		// Note: this cannot be cached without taking into consideration the exact advisor configuration -ggolden
		if (hasAdvisors())
		{
			SecurityAdvisor.SecurityAdvice advice = adviseIsAllowed(user.getId(), function, entityRef);
			if (advice != SecurityAdvisor.SecurityAdvice.PASS)
			{
				return advice == SecurityAdvisor.SecurityAdvice.ALLOWED;
			}
		}

		// check with the AuthzGroups appropriate for this entity
		return checkAuthzGroups(user.getId(), function, entityRef);
	}

	/**
	 * Check the appropriate AuthzGroups for the answer - this may be cached
	 * @param userId The user id.
	 * @param function The security function.
	 * @param entityRef The entity reference string.
	 * @return true if allowed, false if not.
	 */
	protected boolean checkAuthzGroups(String userId, String function, String entityRef)
	{
		// check the cache
		String command = "unlock@" + userId + "@" + function + "@" + entityRef;
		if ((m_callCache != null) && (m_callCache.containsKey(command)))
		{
			boolean rv = ((Boolean) m_callCache.get(command)).booleanValue();
			return rv;
		}

		//	make a reference for the entity
		Reference ref = EntityManager.newReference(entityRef);

		// get this entity's AuthzGroups
		Collection azgs = ref.getRealms();
		boolean rv = AuthzGroupService.isAllowed(userId, function, azgs);

		// cache
		if (m_callCache != null) m_callCache.put(command, Boolean.valueOf(rv), m_cacheMinutes * 60, entityRef, azgs);

		return rv;
	}

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
		Reference ref = EntityManager.newReference(reference);

		// get this resource's Realms
		Collection realms = ref.getRealms();

		// get the users who can unlock in these realms
		List ids = new Vector();
		ids.addAll(AuthzGroupService.getUsersIsAllowed(lock, realms));

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
	 * SecurityAdvisor Support
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Get the thread-local security advisor stack, possibly creating it
	 * @param force if true, create if missing
	 */
	protected Stack getAdvisorStack(boolean force)
	{
		Stack advisors = (Stack) m_threadLocalManager.get(ADVISOR_STACK);
		if ((advisors == null) && force)
		{
			advisors = new Stack();
			m_threadLocalManager.set(ADVISOR_STACK, advisors);
		}

		return advisors;
	}

	/**
	 * Remove the thread-local security advisor stack
	 */
	protected void dropAdvisorStack()
	{
		m_threadLocalManager.set(ADVISOR_STACK, null);
	}

	/**
	 * Check the advisor stack - if anyone declares ALLOWED or NOT_ALLOWED, stop and return that, else, while they PASS, keep checking.
	 * @param userId The user id.
	 * @param function The security function.
	 * @param reference The Entity reference.
	 * @return ALLOWED or NOT_ALLOWED if an advisor makes a decision, or PASS if there are no advisors or they cannot make a decision.
	 */
	protected SecurityAdvisor.SecurityAdvice adviseIsAllowed(String userId, String function, String reference)
	{
		Stack advisors = getAdvisorStack(false);
		if ((advisors == null) || (advisors.isEmpty())) return SecurityAdvisor.SecurityAdvice.PASS;

		// a Stack grows to the right - process from top to bottom
		for (int i = advisors.size()-1; i >= 0; i--)
		{
			SecurityAdvisor advisor = (SecurityAdvisor) advisors.elementAt(i);

			SecurityAdvisor.SecurityAdvice advice = advisor.isAllowed(userId, function, reference);
			if (advice != SecurityAdvisor.SecurityAdvice.PASS)
			{
				return advice;
			}
		}

		return SecurityAdvisor.SecurityAdvice.PASS;
	}

	/**
	 * @inheritDoc
	 */
	public void pushAdvisor(SecurityAdvisor advisor)
	{
		Stack advisors = getAdvisorStack(true);
		advisors.push(advisor);
	}

	/**
	 * @inheritDoc
	 */
	public SecurityAdvisor popAdvisor()
	{
		Stack advisors = getAdvisorStack(false);
		if (advisors == null) return null;
		
		SecurityAdvisor rv = null;

		if (advisors.size() > 0)
		{
			rv = (SecurityAdvisor) advisors.pop();
		}
		
		if (advisors.isEmpty())
		{
			dropAdvisorStack();
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasAdvisors()
	{
		Stack advisors = getAdvisorStack(false);
		if (advisors == null) return false;

		return !advisors.isEmpty();
	}

	/**
	 * @inheritDoc
	 */
	public void clearAdvisors()
	{
		dropAdvisorStack();
	}
}
