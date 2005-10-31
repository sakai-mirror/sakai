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
package org.sakaiproject.component.legacy.user;

// imports
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryProvider;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseUserDirectoryService is a Sakai user directory services implementation.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
* @see org.chefproject.core.User
*/
public abstract class BaseUserDirectoryService implements UserDirectoryService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** An anon. user. */
	protected User m_anon = null;

	/** A user directory provider. */
	protected UserDirectoryProvider m_provider = null;

	/** Key for current service caching of current user */
	protected final String M_curUserKey = getClass().getName()+".currentUser";

	/** A cache of calls to the service and the results. */
	protected Cache m_callCache = null;

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
	*/
	protected abstract Storage newStorage();

	/**
	* Access the partial URL that forms the root of resource URLs.
	* @param relative if true, form within the access path only (i.e. starting with /content)
	* @return the partial URL that forms the root of resource URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : m_serverConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The user id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String userReference(String id)
	{
		// clean up the id
		id = StringUtil.trimToZeroLower(id);

		return getAccessPoint(true) + Entity.SEPARATOR + id;

	} // userReference

	/**
	* Access the user id extracted from a user reference.
	* @param ref The user reference string.
	* @return The the user id extracted from a user reference.
	*/
	protected String userId(String ref)
	{
		String start = getAccessPoint(true) + Entity.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1)
			return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // userId

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if allowd, false if not
	*/
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;

	} // unlockCheck

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if either allowed, false if not
	*/
	protected boolean unlockCheck2(String lock1, String lock2, String resource)
	{
		if (!SecurityService.unlock(lock1, resource))
		{
			if (!SecurityService.unlock(lock2, resource))
			{
				return false;
			}
		}

		return true;

	} // unlockCheck2

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(lock, resource);
		}

	} // unlock

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access to either.
	*/
	protected void unlock2(String lock1, String lock2, String resource) throws PermissionException
	{
		if (!unlockCheck2(lock1, lock2, resource))
		{
			throw new PermissionException(lock1 + "/" + lock2, resource);
		}

	} // unlock2

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

	/**
	 * Configuration: set the user directory provider helper service.
	 * @param provider the user directory provider helper service.
	 */
	public void setProvider(UserDirectoryProvider provider)
	{
		m_provider = provider;
	}

	/** The # seconds to cache gets. 0 disables the cache. */
	protected int m_cacheSeconds = 0;

	/**
	 * Set the # minutes to cache a get.
	 * 
	 * @param time
	 *        The # minutes to cache a get (as an integer string).
	 */
	public void setCacheMinutes(String time)
	{
		m_cacheSeconds = Integer.parseInt(time) * 60;
	}

	/** The # seconds to cache gets. 0 disables the cache. */
	protected int m_cacheCleanerSeconds = 0;

	/**
	 * Set the # minutes between cache cleanings.
	 * 
	 * @param time
	 *        The # minutes between cache cleanings. (as an integer string).
	 */
	public void setCacheCleanerMinutes(String time)
	{
		m_cacheCleanerSeconds = Integer.parseInt(time) * 60;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * 
	 * @param service
	 *        The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/** Dependency: EntityManager. */
	protected EntityManager m_entityManager = null;

	/**
	 * Dependency: EntityManager.
	 * 
	 * @param service
	 *        The EntityManager.
	 */
	public void setEntityManager(EntityManager service)
	{
		m_entityManager = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_relativeAccessPoint = REFERENCE_ROOT;

			// construct storage and read
			m_storage = newStorage();
			m_storage.open();

			// make an anon. user
			m_anon = new BaseUserEdit("");

			// <= 0 indicates no caching desired
			if ((m_cacheSeconds > 0) && (m_cacheCleanerSeconds > 0))
			{
				// build a synchronized map for the call cache, automatiaclly checking for expiration every 15 mins, expire on user events, too.
				m_callCache = MemoryService.newHardCache(m_cacheCleanerSeconds, userReference(""));
			}

			// register as an entity producer
			m_entityManager.registerEntityProducer(this);

			// register functions
			FunctionManager.registerFunction(SECURE_ADD_USER);
			FunctionManager.registerFunction(SECURE_REMOVE_USER);
			FunctionManager.registerFunction(SECURE_UPDATE_USER_OWN);
			FunctionManager.registerFunction(SECURE_UPDATE_USER_ANY);

			m_logger.info(this +".init(): provider: " + ((m_provider == null) ? "none" : m_provider.getClass().getName()) + " - caching minutes: " + m_cacheSeconds / 60 + " - cache cleaner minutes: " + m_cacheCleanerSeconds / 60);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*
	* You can use this method to release resources thet your Service
	* allocated when Turbine shuts down.
	*/
	public void destroy()
	{
		m_storage.close();
		m_storage = null;
		m_provider = null;
		m_anon = null;

		m_logger.info(this +".destroy()");

	} // destroy

	/*******************************************************************************
	* UserDirectoryService implementation
	*******************************************************************************/

	/**
	* Access a user object.
	* @param id The user id string.
	* @return A user object containing the user information
	* @exception IdUnusedException if not found
	*/
	public User getUser(String id) throws IdUnusedException
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);

		if (id == null)
			throw new IdUnusedException("null");

		// see if we've done this already in this thread
		String ref = userReference(id);
		UserEdit user = (UserEdit) CurrentService.getInThread(ref);
		
		// if not
		if (user == null)
		{
			// check the cache
			if ((m_callCache != null) && (m_callCache.containsKey(ref)))
			{
				user = (UserEdit) m_callCache.get(ref);
			}

			else
			{
				// find our user record, and use it if we have it
				user = findUser(id);
	
				if ((user == null) && (m_provider != null))
				{
					// make a new edit to hold the provider's info, hoping it will be filled in
					user = new BaseUserEdit((String) null);
					((BaseUserEdit) user).m_id = id;
				
					if (!m_provider.getUser(user))
					{
						// it was not provided for, so clear back to null
						user = null;
					}
				}

				// if found, save it for later use in this thread
				if (user != null)
				{
					CurrentService.setInThread(ref, user);
					
					// cache
					if (m_callCache != null) m_callCache.put(ref, user, m_cacheSeconds);
				}
			}			
		}

		// if not found
		if (user == null)
		{
			throw new IdUnusedException(id);
		}

		// track it - Note: we are not tracking user access -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(SECURE_ACCESS_USER, user.getReference()));

		return user;

	} // getUser

	/**
	 * {@inheritDoc}
	 */
	public List getUsers(Collection ids)
	{
		// TODO: make this more efficient
		
		// User objects to return
		List rv = new Vector();

		// a list of User (edits) setup to check with the provider
		Collection fromProvider = new Vector();

		// for each requested id
		for (Iterator i = ids.iterator(); i.hasNext();)
		{
			String id = (String) i.next();

			// clean up the id
			id = StringUtil.trimToNullLower(id);

			// skip nulls
			if (id == null) continue;

			// see if we've done this already in this thread
			String ref = userReference(id);
			UserEdit user = (UserEdit) CurrentService.getInThread(ref);
			
			// if not
			if (user == null)
			{
				// check the cache
				if ((m_callCache != null) && (m_callCache.containsKey(ref)))
				{
					user = (UserEdit) m_callCache.get(ref);
				}

				else
				{
					// find our user record, and use it if we have it
					user = findUser(id);

					// if we didn't find it locally, collect a list of externals to get
					if ((user == null) && (m_provider != null))
					{
						// make a new edit to hold the provider's info; the provider will either fill this in, if known, or remove it from the collection
						BaseUserEdit providerUser = new BaseUserEdit((String) null);
						providerUser.m_id = id;
						fromProvider.add(providerUser);
					}

					// if found, save it for later use in this thread
					if (user != null)
					{
						CurrentService.setInThread(ref, user);
						
						// cache
						if (m_callCache != null) m_callCache.put(ref, user, m_cacheSeconds);
					}
				}			
			}
			
			// if we found a user for this id, add it to the return
			if (user != null)
			{
				rv.add(user);
			}
		}

		// check the provider
		if ((m_provider != null) && (!fromProvider.isEmpty()))
		{
			m_provider.getUsers(fromProvider);
			
			// for each User in the collection that was filled in (and not removed) by the provider, cache and return it
			for (Iterator i = fromProvider.iterator(); i.hasNext();)
			{
				User user = (User) i.next();				
				
				// cache, thread and call cache
				String ref = user.getReference();
				CurrentService.setInThread(ref, user);
				if (m_callCache != null) m_callCache.put(ref, user, m_cacheSeconds);
				
				// add to return
				rv.add(user);
			}
		}
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public User getCurrentUser()
	{
		String id = SessionManager.getCurrentSessionUserId();

		// check current service caching - discard if the session user is different
		User rv = (User) CurrentService.getInThread(M_curUserKey);
		if ((rv != null) && (rv.getId().equals(id))) return rv;

		try
		{
			rv = getUser(id);
		}
		catch (IdUnusedException e)
		{
			rv = getAnonymousUser();
		}

		// cache in the current service
		CurrentService.setInThread(M_curUserKey, rv);

		return rv;
	}

	/**
	* check permissions for updating a user
	* (i.s. using getUserEditClone() and calling commitEditClone())
	* @param id The user id.
	* @return true if the user is allowed to update the user, false if not.
	*/
	public boolean allowUpdateUser(String id)
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);

		// is this the user's own?
		if (id.equals(SessionManager.getCurrentSessionUserId()))
		{
			// own or any
			return unlockCheck2(SECURE_UPDATE_USER_OWN, SECURE_UPDATE_USER_ANY, userReference(id));
		}

		else
		{
			// just any
			return unlockCheck(SECURE_UPDATE_USER_ANY, userReference(id));
		}

	} // allowUpdateUser

	/**
	* Get a locked user object for editing.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The user id string.
	* @return A UserEdit object for editing.
	* @exception IdUnusedException if not found, or if not an UserEdit object
	* @exception PermissionException if the current user does not have permission to mess with this user.
	* @exception InUseException if the current user does not have permission to mess with this user.
	*/
	public UserEdit editUser(String id) throws IdUnusedException, PermissionException, InUseException
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);

		if (id == null)
			throw new IdUnusedException("null");

		// is this the user's own?
		String function = null;
		if (id.equals(SessionManager.getCurrentSessionUserId()))
		{
			// own or any
			unlock2(SECURE_UPDATE_USER_OWN, SECURE_UPDATE_USER_ANY, userReference(id));
			function = SECURE_UPDATE_USER_OWN;
		}
		else
		{
			// just any
			unlock(SECURE_UPDATE_USER_ANY, userReference(id));
			function = SECURE_UPDATE_USER_ANY;
		}

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the user with a lock from the info store
		UserEdit user = m_storage.edit(id);
		if (user == null)
			throw new InUseException(id);

		((BaseUserEdit) user).setEvent(function);

		return user;

	} // editUser

	/**
	* Commit the changes made to a UserEdit object, and release the lock.
	* @param user The UserEdit object to commit.
	*/
	public void commitEdit(UserEdit user)
	{
		// check for closed edit
		if (!user.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitEdit(): closed UserEdit", e);
			}
			return;
		}

		// update the properties
		addLiveUpdateProperties((BaseUserEdit) user);

		// complete the edit
		m_storage.commit(user);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseUserEdit) user).getEvent(), user.getReference(), true));

		// close the edit object
		 ((BaseUserEdit) user).closeEdit();

		// %%% sync with portal service, i.e. user's portal page, any others? -ggolden

	} // commitEdit

	/**
	* Cancel the changes made to a UserEdit object, and release the lock.
	* @param user The UserEdit object to commit.
	*/
	public void cancelEdit(UserEdit user)
	{
		// check for closed edit
		if (!user.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelEdit(): closed UserEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancel(user);

		// close the edit object
		 ((BaseUserEdit) user).closeEdit();

	} // cancelEdit

	/**
	* Access all user objects - known to us (not from external providers).
	* @return A list of user objects containing each user's information.
	*/
	public List getUsers()
	{
		List users = m_storage.getAll();
		return users;

	} // getUsers


	/**
	 * {@inheritDoc}
	 */
	public List getUsers(int first, int last)
	{
		List all = m_storage.getAll(first, last);

		return all;
	}

	/**
	 * {@inheritDoc}
	 */
	public int countUsers()
	{
		return m_storage.count();
	}

	/**
	 * {@inheritDoc}
	 */
	public List searchUsers(String criteria, int first, int last)
	{
		return m_storage.search(criteria, first, last);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countSearchUsers(String criteria)
	{
		return m_storage.countSearch(criteria);
	}

	/**
	* Find a user object who has this email address.
	* @param email The email address string.
	* @return A user object containing the user information
	* @exception IdUnusedException if not found
	*/
	public User findUserByEmail(String email) throws IdUnusedException
	{
		// check internal users
		User user = m_storage.findUserByEmail(email);
		if (user != null)
		{
			return user;
		}
		
		// if not there, check our provider
		if (m_provider != null)
		{
			// make a new edit to hold the provider's info
			UserEdit edit = new BaseUserEdit((String) null);
			if (m_provider.findUserByEmail(edit, email))
			{
				return edit;
			}
		}

		// not found
		throw new IdUnusedException(email);

	} // findUserByEmail

	/**
	* Access the anonymous user object.
	* @return the anonymous user object.
	*/
	public User getAnonymousUser()
	{
		return m_anon;

	} // getAnonymousUser

	/**
	* check permissions for addUser().
	* @param id The user id.
	* @return true if the user is allowed to addUser(id), false if not.
	*/
	public boolean allowAddUser(String id)
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);

		return unlockCheck(SECURE_ADD_USER, userReference(id));

	} // allowAddUser

	/**
	* Add a new user to the directory.  Must commit() to make official, or cancel() when done!
	* @param id The user id.
	* @return An edit clone to the new user object.
	* @exception IdInvalidException if the user id is invalid.
	* @exception IdUsedException if the user id is already used.
	* @exception PermissionException if the current user does not have permission to add a user.
	*/
	public UserEdit addUser(String id) throws IdInvalidException, IdUsedException, PermissionException
	{
		// clean up the id
		id = StringUtil.trimToZeroLower(id);

		// check for a valid user name
		Validator.checkResourceId(id);

		// check security (throws if not permitted)
		unlock(SECURE_ADD_USER, userReference(id));

		// reserve a user with this id from the info store - if it's in use, this will return null
		UserEdit user = m_storage.put(id);
		if (user == null)
		{
			throw new IdUsedException(id);
		}

		((BaseUserEdit) user).setEvent(SECURE_ADD_USER);

		return user;

	} // addUser

	/**
	 * {@inheritDoc}
	 */
	public User addUser(String id, String firstName, String lastName, String email, String pw, String type, ResourceProperties properties)
		throws IdInvalidException, IdUsedException, PermissionException
	{
		// get it added
		UserEdit edit = addUser(id);

		// fill in the fields
		edit.setLastName(lastName);
		edit.setFirstName(firstName);
		edit.setEmail(email);
		edit.setPassword(pw);
		edit.setType(type);

		ResourcePropertiesEdit props = edit.getPropertiesEdit();
		if (properties != null)
		{
			props.addAll(properties);
		}

		// no live props!

		// get it committed - no further security check
		m_storage.commit(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseUserEdit) edit).getEvent(), edit.getReference(), true));

		// close the edit object
		 ((BaseUserEdit) edit).closeEdit();

		return edit;
	}

	/**
	* Add a new user to the directory, from a definition in XML
	* @param el The XML DOM Element defining the user.
	* @return An edit clone to the new user object.
	* @exception IdInvalidException if the user id is invalid.
	* @exception IdUsedException if the user id is already used.
	* @exception PermissionException if the current user does not have permission to add a user.
	*/
	public UserEdit mergeUser(Element el) throws IdInvalidException, IdUsedException, PermissionException
	{
		// construct from the XML
		User userFromXml = new BaseUserEdit(el);

		// check for a valid user name
		Validator.checkResourceId(userFromXml.getId());

		// check security (throws if not permitted)
		unlock(SECURE_ADD_USER, userFromXml.getReference());

		// reserve a user with this id from the info store - if it's in use, this will return null
		UserEdit user = m_storage.put(userFromXml.getId());
		if (user == null)
		{
			throw new IdUsedException(userFromXml.getId());
		}

		// transfer from the XML read user object to the UserEdit
		 ((BaseUserEdit) user).set(userFromXml);

		((BaseUserEdit) user).setEvent(SECURE_ADD_USER);

		return user;

	} // mergeUser

	/**
	* check permissions for removeUser().
	* @param id The user id.
	* @return true if the user is allowed to removeUser(id), false if not.
	*/
	public boolean allowRemoveUser(String id)
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);

		return unlockCheck(SECURE_REMOVE_USER, userReference(id));

	} // allowRemoveUser

	/**
	* Remove this user's information from the directory - it must be a user with a lock from editUser()
	* @param user The locked user object to remove.
	* @exception PermissionException if the current user does not have permission to remove this user.
	*/
	public void removeUser(UserEdit user) throws PermissionException
	{
		// check for closed edit
		if (!user.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeUser(): closed UserEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_USER, user.getReference());

		// complete the edit
		m_storage.remove(user);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_USER, user.getReference(), true));

		// %%% sync with portal service, i.e. user's portal page, any others? -ggolden

		// close the edit object
		 ((BaseUserEdit) user).closeEdit();

		// remove any realm defined for this resource
		try
		{
			AuthzGroupService.removeAuthzGroup(AuthzGroupService.getAuthzGroup(user.getReference()));
		}
		catch (PermissionException e)
		{
			m_logger.warn(this +".removeUser: removing realm for : " + user.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeUser

	/**
	 * {@inheritDoc}
	 */
	public User authenticate(String id, String password)
	{
		// clean up the id
		id = StringUtil.trimToNullLower(id);
		if (id == null) return null;

		boolean authenticated = false;

		// do we have a record for this user?
		UserEdit user = findUser(id);
		
		if (user == null)
		{
			if (m_provider != null && m_provider.createUserRecord(id))
			{
				try 
				{
					user = addUser(id);
				} 
				catch (IdInvalidException e) 
				{
					m_logger.debug(this +".authenticate(): Id invalid: " + id);
				} 
				catch (IdUsedException e) 
				{
					m_logger.debug(this +".authenticate(): Id used: " + id);
				} 
				catch (PermissionException e) 
				{
					m_logger.debug(this +".authenticate(): PermissionException for adding user " + id);
				}
			}
		}

		if (m_provider != null && m_provider.authenticateWithProviderFirst(id))
		{
			// 1. check provider
			authenticated = authenticateViaProvider(id, user, password);
			if (!authenticated && user != null)
			{
				// 2. check our user record, if any, if not yet authenticated
				authenticated = user.checkPassword(password);
			}
		}
		else
		{
			// 1. check our user record, if any, if not yet authenticated
			if (user != null)
			{
				authenticated = user.checkPassword(password);
			}

			// 2. check our provider, if any, if not yet authenticated
			if (!authenticated && m_provider != null)
			{
				authenticated = authenticateViaProvider(id, user, password);
			}
		}

		// if authenticated, get the user record to return - we might already have it
		User rv = null;
		if (authenticated)
		{
			rv = user;
			if (rv == null)
			{
				try
				{
					rv = getUser(id);
				}
				catch (IdUnusedException e)
				{
					// we might have authenticated by provider, but don't have proper
					// user "existance" (i.e. provider existance or internal user records) to let the user in -ggolden
					m_logger.info(this +".authenticate(): attempt by unknown user id: " + id);
				}
				catch (Throwable e)
				{
					// we might have authenticated by provider, but don't have proper
					// user "existance" (i.e. provider existance or internal user records) to let the user in -ggolden
					m_logger.warn(this +".authenticate(): could not getUser() after auth: " + id + " : " + e);
				}
			}
			
			// cache the user (if we didn't go through the getUser() above, which would have cached it
			else
			{
				if (m_callCache != null) m_callCache.put(userReference(id), rv, m_cacheSeconds);
			}
		}

		return rv;

	} // authenticate
	
	/**
	 * Authenticate user by provider information
	 * @param id	The id string
	 * @param user The UserEdit object
	 * @param password The password string
	 * @return
	 */
	protected boolean authenticateViaProvider(String id, UserEdit user, String password)
	{
		boolean authenticated = m_provider.authenticateUser(id, user, password);
		// m_logger.info(" *** UserDirectory.authenticate: id: " + id + " result of PUDP.authenticateUser: " + authenticated);

		// some providers want to update the user record on authentication - if so, we need to save it
		if ((authenticated) && (m_provider.updateUserAfterAuthentication()) && user != null)
		{
			// save user
			BaseUserEdit edit = (BaseUserEdit) m_storage.edit(id);
			if (edit != null)
			{
				edit.setAll(user);
				edit.setEvent(SECURE_UPDATE_USER_ANY);

				m_storage.commit(edit);

				EventTrackingService.post(EventTrackingService.newEvent(edit.getEvent(), edit.getReference(), true));
			}
			else
			{
				m_logger.warn(this +".authenticate(): could not save user after auth: " + id);
			}
		}
		return authenticated;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroyAuthentication()
	{
		// let the provider know
		if (m_provider != null)
		{
			m_provider.destroyAuthentication();
		}
	}

	/**
	* Find the user object, in cache or storage (only - no provider check).
	* @param id The user id.
	* @return The user object found in cache or storage, or null if not found.
	*/
	protected BaseUserEdit findUser(String id)
	{
		BaseUserEdit user = (BaseUserEdit) m_storage.get(id);

		return user;

	} // findUser

	/**
	* Create the live properties for the user.
	*/
	protected void addLiveProperties(BaseUserEdit edit)
	{
		String current = SessionManager.getCurrentSessionUserId();

		edit.m_createdUserId = current;
		edit.m_lastModifiedUserId = current;

		Time now = TimeService.newTime();
		edit.m_createdTime = now;
		edit.m_lastModifiedTime = (Time) now.clone();

	} //  addLiveProperties

	/**
	* Update the live properties for a user for when modified.
	*/
	protected void addLiveUpdateProperties(BaseUserEdit edit)
	{
		String current = SessionManager.getCurrentSessionUserId();

		edit.m_lastModifiedUserId = current;
		edit.m_lastModifiedTime = TimeService.newTime();

	} //  addLiveUpdateProperties

	/**********************************************************************************************************************************************************************************************************************************************************
	 * EntityProducer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
 	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "user";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willArchiveMerge()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willImport()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpAccess getHttpAccess()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean parseEntityReference(String reference, Reference ref)
	{
		// for user access
		if (reference.startsWith(REFERENCE_ROOT))
		{
			String id = null;

			// we will get null, service, userId
			String[] parts = StringUtil.split(reference, Entity.SEPARATOR);

			if (parts.length > 2)
			{
				id = parts[2];
			}

			ref.set(SERVICE_NAME, null, id, null, null);

			return true;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityDescription(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		String rv = "User: " + ref.getReference();

		try
		{
			User user = getUser(ref.getId());
			rv = "User: " + user.getDisplayName();
		}
		catch (IdUnusedException e)
		{
		}
		catch (NullPointerException e)
		{
		}
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getEntityResourceProperties(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Entity getEntity(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getEntityAuthzGroups(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Collection rv = new Vector();

		// for user access: user and template realms
		try
		{
			rv.add(userReference(ref.getId()));

			ref.addUserTemplateAuthzGroup(rv, SessionManager.getCurrentSessionUserId());
		}
		catch (NullPointerException e)
		{
			m_logger.warn("getEntityRealms(): " + e);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityUrl(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments)
	{
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames,
			Map userIdTrans, Set userListAllowImport)
	{
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public void importEntities(String fromContext, String toContext, List ids)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChange(Site site, EntityProducer.ChangeType change)
	{
	}

	/*******************************************************************************
	* UserEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseUserEdit is an implementation of the CHEF UserEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseUserEdit implements UserEdit, SessionBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/** The user id. */
		protected String m_id = null;

		/** The user first name. */
		protected String m_firstName = null;

		/** The user last name. */
		protected String m_lastName = null;

		/** The user email address. */
		protected String m_email = null;

		/** The user password. */
		protected String m_pw = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The user type. */
		protected String m_type = null;

		/** The created user id. */
		protected String m_createdUserId = null;

		/** The last modified user id. */
		protected String m_lastModifiedUserId = null;

		/** The time created. */
		protected Time m_createdTime = null;

		/** The time last modified. */
		protected Time m_lastModifiedTime = null;

		/**
		* Construct.
		* @param id The user id.
		*/
		public BaseUserEdit(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			// if the id is not null (a new user, rather than a reconstruction)
			// and not the anon (id == "") user,
			// add the automatic (live) properties
			if ((m_id != null) && (m_id.length() > 0))
				addLiveProperties(this);

		} // BaseUserEdit

		/**
		* Construct from another User object.
		* @param user The user object to use for values.
		*/
		public BaseUserEdit(User user)
		{
			setAll(user);

		} // BaseUserEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the user.
		*/
		public BaseUserEdit(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = StringUtil.trimToNullLower(el.getAttribute("id"));
			m_firstName = StringUtil.trimToNull(el.getAttribute("first-name"));
			m_lastName = StringUtil.trimToNull(el.getAttribute("last-name"));
			m_email = StringUtil.trimToNull(el.getAttribute("email"));
			m_pw = el.getAttribute("pw");
			m_type = StringUtil.trimToNull(el.getAttribute("type"));
			m_createdUserId = StringUtil.trimToNull(el.getAttribute("created-id"));
			m_lastModifiedUserId = StringUtil.trimToNull(el.getAttribute("modified-id"));
			
			String time = StringUtil.trimToNull(el.getAttribute("created-time"));
			if (time != null)
			{
				m_createdTime = TimeService.newTimeGmt(time);
			}

			time = StringUtil.trimToNull(el.getAttribute("modified-time"));
			if (time != null)
			{
				m_lastModifiedTime = TimeService.newTimeGmt(time);
			}

			// the children (roles, properties)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
					
					// pull out some properties into fields to convert old (pre 1.38) versions
					if (m_createdUserId == null)
					{
						m_createdUserId = m_properties.getProperty("CHEF:creator");
					}
					if (m_lastModifiedUserId == null)
					{
						m_lastModifiedUserId = m_properties.getProperty("CHEF:modifiedby");
					}
					if (m_createdTime == null)
					{
						try
						{
							m_createdTime = m_properties.getTimeProperty("DAV:creationdate");
						}
						catch (Exception ignore) {}
					}
					if (m_lastModifiedTime == null)
					{
						try
						{
							m_lastModifiedTime = m_properties.getTimeProperty("DAV:getlastmodified");
						}
						catch (Exception ignore) {}
					}
					m_properties.removeProperty("CHEF:creator");
					m_properties.removeProperty("CHEF:modifiedby");
					m_properties.removeProperty("DAV:creationdate");
					m_properties.removeProperty("DAV:getlastmodified");
				}
			}

		} // BaseUserEdit

		/**
		* ReConstruct.
		* @param id The id.
		* @param email The email.
		* @param firstName The first name.
		* @param lastName The last name.
		* @param type The type.
		* @param pw The password.
		* @param createdBy The createdBy property.
		* @param createdOn The createdOn property.
		* @param modifiedBy The modified by property.
		* @param modifiedOn The modified on property.
		*/
		public BaseUserEdit(String id, String email, String firstName, String lastName, String type, String pw,
							String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
		{
			m_id = id;
			m_firstName = firstName;
			m_lastName = lastName;
			m_type = type;
			m_email = email;
			m_pw = pw;
			m_createdUserId = createdBy;
			m_lastModifiedUserId = modifiedBy;
			m_createdTime = createdOn;
			m_lastModifiedTime = modifiedOn;

			// setup for properties, but mark them lazy since we have not yet established them from data
			BaseResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			props.setLazy(true);
			m_properties = props;
		}

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void setAll(User user)
		{
			m_id = user.getId();
			m_firstName = user.getFirstName();
			m_lastName = user.getLastName();
			m_type = user.getType();
			m_email = user.getEmail();
			m_pw = ((BaseUserEdit) user).m_pw;
			m_createdUserId = ((BaseUserEdit) user).m_createdUserId;
			m_lastModifiedUserId = ((BaseUserEdit) user).m_lastModifiedUserId;
			if (((BaseUserEdit) user).m_createdTime != null) m_createdTime = (Time) ((BaseUserEdit) user).m_createdTime.clone();
			if (((BaseUserEdit) user).m_lastModifiedTime != null) m_lastModifiedTime = (Time) ((BaseUserEdit) user).m_lastModifiedTime.clone();

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(user.getProperties());
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) user.getProperties()).isLazy());

		} // setAll

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element user = doc.createElement("user");

			if (stack.isEmpty())
			{
				doc.appendChild(user);
			}
			else
			{
				((Element) stack.peek()).appendChild(user);
			}

			stack.push(user);

			user.setAttribute("id", getId());
			if (m_firstName != null)
				user.setAttribute("first-name", m_firstName);
			if (m_lastName != null)
				user.setAttribute("last-name", m_lastName);
			if (m_type != null)
				user.setAttribute("type", m_type);
			user.setAttribute("email", getEmail());
			user.setAttribute("pw", m_pw);
			user.setAttribute("created-id", m_createdUserId);
			user.setAttribute("modified-id", m_lastModifiedUserId);
			user.setAttribute("created-time", m_createdTime.toString());
			user.setAttribute("modified-time", m_lastModifiedTime.toString());

			// properties
			getProperties().toXml(doc, stack);

			stack.pop();

			return user;

		} // toXml

		/**
		* Access the user id.
		* @return The user id string.
		*/
		public String getId()
		{
			if (m_id == null)
				return "";
			return m_id;

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return userReference(m_id);

		} // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readProperties(this, m_properties);
			}

			return m_properties;

		} // getProperties

		/**
		 * {@inheritDoc}
		 */
		public User getCreatedBy()
		{
			try
			{
				return getUser(m_createdUserId);
			}
			catch (Exception e)
			{
				return getAnonymousUser();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public User getModifiedBy()
		{
			try
			{
				return getUser(m_lastModifiedUserId);
			}
			catch (Exception e)
			{
				return getAnonymousUser();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Time getCreatedTime()
		{
			return m_createdTime;
		}

		/**
		 * {@inheritDoc}
		 */
		public Time getModifiedTime()
		{
			return m_lastModifiedTime;
		}

		/**
		* Access the user's name for display purposes.
		* @return The user's name for display purposes.
		*/
		public String getDisplayName()
		{
			StringBuffer buf = new StringBuffer(128);
			if (m_firstName != null)
				buf.append(m_firstName);
			if (m_lastName != null)
			{
				buf.append(" ");
				buf.append(m_lastName);
			}

			if (buf.length() == 0)
				return getId();

			return buf.toString();

		} // getDisplayName

		/**
		* Access the user's first name.
		* @return The user's first name.
		*/
		public String getFirstName()
		{
			if (m_firstName == null)
				return "";
			return m_firstName;

		} // getFirstName

		/**
		* Access the user's last name.
		* @return The user's last name.
		*/
		public String getLastName()
		{
			if (m_lastName == null)
				return "";
			return m_lastName;

		} // getLastName

		/**
		* Access the user's name for sorting purposes.
		* @return The user's name for sorting purposes.
		*/
		public String getSortName()
		{
			StringBuffer buf = new StringBuffer(128);
			if (m_lastName != null)
				buf.append(m_lastName);
			if (m_firstName != null)
			{
				buf.append(", ");
				buf.append(m_firstName);
			}

			if (buf.length() == 0)
				return getId();

			return buf.toString();

		} // getSortName

		/**
		* Access the email address.
		* @return The email address string.
		*/
		public String getEmail()
		{
			if (m_email == null)
				return "";
			return m_email;

		} // getEmail

		/**
		 * {@inheritDoc}
		 */
		public String getType()
		{
			return m_type;
		}

		/**
		* Check if this is the user's password.
		* @param pw The clear text password to check.
		* @return true if the password matches, false if not.
		*/
		public boolean checkPassword(String pw)
		{
			// if we have no password, or none is given, we fail
			if ((m_pw == null) || (pw == null)) return false;

			// encode this password
			String encoded = OneWayHash.encode(pw);

			if (m_pw.equals(encoded)) return true;

			return false;

		} // checkPassword

		/**
		* Are these objects equal?  If they are both User objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof User))
				return false;
			return ((User) obj).getId().equals(getId());

		} // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		} // hashCode

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof User))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// start the compare by comparing their sort names
			int compare = getSortName().compareTo(((User) obj).getSortName());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((User) obj).getId());
			}

			return compare;

		} // compareTo

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // finalize

		/**
		* Set the user's id.
		* Note: this is a special purpose routine that is used only to establish the id field,
		* when the id is null, and cannot be used to change a user's id.
		* @param name The user id.
		*/
		public void setId(String id)
		{
			if (m_id == null)
			{
				m_id = id;
			}

		} // setId

		/**
		* Set the user's first name.
		* @param name The user's first name.
		*/
		public void setFirstName(String name)
		{
			m_firstName = name;

		} // setFirstName

		/**
		* Set the user's last name.
		* @param name The user's last name.
		*/
		public void setLastName(String name)
		{
			m_lastName = name;

		} // setLastName

		/**
		* Set the email address.
		* @param email The email address string.
		*/
		public void setEmail(String email)
		{
			m_email = email;

		} // setEmail

		/**
		* Set the user's password
		* @param pw The user's new password.
		*/
		public void setPassword(String pw)
		{
			// encode this password
			String encoded = OneWayHash.encode(pw);

			m_pw = encoded;

		} // setPassword

		/**
		 * {@inheritDoc}
		 */
		public void setType(String type)
		{
			m_type = type;
		}

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void set(User user)
		{
			setAll(user);

		} // set

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readProperties(this, m_properties);
			}

			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/**
		 * Check this User object to see if it is selected by the criteria.
		 * @param criteria The critera.
		 * @return True if the User object is selected by the criteria, false if not.
		 */
		protected boolean selectedBy(String criteria)
		{
			if (	StringUtil.containsIgnoreCase(getSortName(), criteria)
				||	StringUtil.containsIgnoreCase(getDisplayName(), criteria)
				||	StringUtil.containsIgnoreCase(getId(), criteria)
				||	StringUtil.containsIgnoreCase(getEmail(), criteria))
			{
				return true;
			}
			
			return false;
		}

		/*******************************************************************************
		* SessionBindingListener implementation
		*******************************************************************************/

		public void valueBound(SessionBindingEvent event)
		{
		}

		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // valueUnbound

	} // BaseUserEdit

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open.
		*/
		public void open();

		/**
		* Close.
		*/
		public void close();

		/**
		* Check if a user by this id exists.
		* @param id The user id.
		* @return true if a user by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the user with this id, or null if not found.
		* @param id The user id.
		* @return The user with this id, or null if not found.
		*/
		public UserEdit get(String id);

		/**
		* Get the user with this email, or null if not found.
		* @param id The user email.
		* @return The user with this email, or null if not found.
		*/
		public UserEdit findUserByEmail(String email);

		/**
		* Get all users.
		* @return The List (UserEdit) of all users.
		*/
		public List getAll();

		/**
		* Get all the users in record range.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseUserEdit) of all users.
		*/
		public List getAll(int first, int last);

		/**
		* Count all the users.
		* @return The count of all users.
		*/
		public int count();

		/**
		* Search for users with id or email, first or last name matching criteria, in range.
		* @param criteria The search criteria.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseUserEdit) of all alias.
		*/
		public List search(String criteria, int first, int last);

		/**
		* Count all the users with id or email, first or last name matching criteria.
		* @param criteria The search criteria.
		* @return The count of all aliases with id or target matching criteria.
		*/
		public int countSearch(String criteria);

		/**
		* Add a new user with this id.
		* @param id The user id.
		* @return The locked User object with this id, or null if the id is in use.
		*/
		public UserEdit put(String id);

		/**
		* Get a lock on the user with this id, or null if a lock cannot be gotten.
		* @param id The user id.
		* @return The locked User with this id, or null if this records cannot be locked.
		*/
		public UserEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param user The user to commit.
		*/
		public void commit(UserEdit user);

		/**
		* Cancel the changes and release the lock.
		* @param user The user to commit.
		*/
		public void cancel(UserEdit user);

		/**
		* Remove this user.
		* @param user The user to remove.
		*/
		public void remove(UserEdit user);

		/**
		 * Read properties from storage into the edit's properties.
		 * @param edit The user to read properties for.
		 */
		public void readProperties(UserEdit edit, ResourcePropertiesEdit props);

	} // Storage

	/*******************************************************************************
	* StorageUser implementation (no container)
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Entity newContainer(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Entity newContainer(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Entity newContainer(Entity other)
	{
		return null;
	}

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Entity newResource(Entity container, String id, Object[] others)
	{
		return new BaseUserEdit(id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Entity newResource(Entity container, Element element)
	{
		return new BaseUserEdit(element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Entity newResource(Entity container, Entity other)
	{
		return new BaseUserEdit((User) other);
	}

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Entity other)
	{
		return null;
	}

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Entity container, String id, Object[] others)
	{
		BaseUserEdit e = new BaseUserEdit(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Entity container, Element element)
	{
		BaseUserEdit e = new BaseUserEdit(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Entity container, Entity other)
	{
		BaseUserEdit e = new BaseUserEdit((User) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Entity r)
	{
		return null;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Entity r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Entity r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Entity r)
	{
		return null;
	}

} // BaseUserDirectoryService



