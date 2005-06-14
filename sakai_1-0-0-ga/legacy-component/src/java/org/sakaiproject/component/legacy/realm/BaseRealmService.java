/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/BaseRealmService.java,v 1.22 2004/10/07 01:57:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.realm;

// imports
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.realm.Abilities;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.RealmProvider;
import org.sakaiproject.service.legacy.realm.RealmService;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.RoleEdit;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseRealmService is a CHEF realm directory services implemented as a Turbine Service.</p>
* <p>To support the public view feature, a realm named TEMPLATE_PUBVIEW must exist, with a role
* named ROLE_PUBVIEW - all the abilities in this role become the public view abilities for any resource.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.22 $
*/
public abstract class BaseRealmService implements RealmService, StorageUser, CacheRefresher
{
	/** The special role name for public view. */
	protected final static String ROLE_PUBVIEW = "pubview";

	/** The special realm used as a template for all pubview realm ROLE_PUBVIEW roles. */
	protected final static String TEMPLATE_PUBVIEW = "!pubview";

	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** A Cache for this service - Realms keyed by reference. */
	protected Cache m_cache = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A provider of additional Abilities for a userId. */
	protected RealmProvider m_provider = null;

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
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	* Access the realm id extracted from a realm reference.
	* @param ref The realm reference string.
	* @return The the realm id extracted from a realm reference.
	*/
	protected String realmId(String ref)
	{
		String start = getAccessPoint(true) + Resource.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1)
			return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // realmId

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
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the realm does not have access
	*/
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, resource);
		}

	} // unlock

	/**
	* Create the live properties for the realm.
	*/
	protected void addLiveProperties(ResourcePropertiesEdit props)
	{
		String current = UsageSessionService.getSessionUserId();

		props.addProperty(ResourceProperties.PROP_CREATOR, current);
		props.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		props.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

	} //  addLiveProperties

	/**
	* Update the live properties for a realm for when modified.
	*/
	protected void addLiveUpdateProperties(ResourcePropertiesEdit props)
	{
		String current = UsageSessionService.getSessionUserId();

		props.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);
		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE, TimeService.newTime().toString());

	} //  addLiveUpdateProperties

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

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * @param service The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/**
	 * Configuration: set the realm provider helper service.
	 * @param provider the realm provider helper service.
	 */
	public void setProvider(RealmProvider provider)
	{
		m_provider = provider;
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

			// make the cache - make a hard one that will not go away on low memory
			// TODO:
			m_cache = m_memoryService.newHardCache(this, realmReference(""));

			m_logger.info(this +".init(): provider: " + ((m_provider == null) ? "none" : m_provider.getClass().getName()));
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(); ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_cache.destroy();
		m_cache = null;
		m_storage.close();
		m_storage = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* RealmService implementation
	*******************************************************************************/

	/**
	* Access all realm objects.
	* @return A List (Realm) of all defined realms.
	*/
	public List getRealms()
	{
		List realms = new Vector();

		// if we have disabled the cache, don't use it
		if (m_cache.disabled())
		{
			realms = m_storage.getAll();
		}

		else
		{
			// complete the cache if needed
			if (!m_cache.isComplete())
			{
				completeCache();
			}

			// get the realms from the complete cache
			realms = m_cache.getAll();
		}

		return realms;

	} // getRealms

	/**
	* Complete the cache by reading all realm objects from storage.
	*/
	protected void completeCache()
	{
		// Note: while we are getting from storage, storage might change.  These can be processed
		// after we get the storage entries, and put them in the cache, and mark the cache complete.
		// -ggolden
		synchronized (m_cache)
		{
			// if we were waiting and it's now complete...
			if (m_cache.isComplete())
				return;

			// save up any events to the cache until we get past this load
			m_cache.holdEvents();

			List realms = m_storage.getAll();

			// update the cache, and mark it complete
			for (int i = 0; i < realms.size(); i++)
			{
				Realm realm = (Realm) realms.get(i);
				m_cache.put(realm.getReference(), realm);
			}

			m_cache.setComplete();

			// now we are complete, process any cached events
			m_cache.processEvents();
		}

	} // completeCache

	/**
	* Access a realm object.
	* @param id The realm id string.
	* @return A realm object containing the realm information.
	* @exception IdUnusedException if not found.
	*/
	public Realm getRealm(String id) throws IdUnusedException
	{
		if (id == null)
			throw new IdUnusedException("<null>");

		Realm realm = null;

		// if we are not caching, get it from storage
		if (m_cache.disabled())
		{
			realm = m_storage.get(id);
		}

		// otherwise use the cache
		else
		{
			// if the cache is not complete, complete it
			if (!m_cache.isComplete())
			{
				completeCache();
			}

			// get the realm from the complete cache
			String key = realmReference(id);
			if (m_cache.containsKey(key))
			{
				realm = (Realm) m_cache.get(key);
			}

			// if not in the cache, see if we have it in our info store
			else
			{
				realm = m_storage.get(id);

				// cache it (hit or miss)
				m_cache.put(key, realm);
			}
		}

		// if not found
		if (realm == null)
		{
			throw new IdUnusedException(id);
		}

		// track it - Note: we are not tracking realm access -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(SECURE_ACCESS_REALM, realm.getReference()));

		return realm;

	} // getRealm

	/**
	* Access a realm object.
	* This one is a strange one.  It's given two chances (two ids), and will find one, or the other.
	* or return null.  It doesn't throw exceptions.  Good for use in exception-phobic Velocity.
	* @param id1 The realm id we really want.
	* @param id2 Another realm id to get if the first is not defined.
	* @return A realm object containing the realm information for id1, or id2, or a null if neither are found.
	*/
	public Realm getRealm(String id1, String id2)
	{
		try
		{
			return getRealm(id1);
		}
		catch (IdUnusedException e)
		{
			try
			{
				return getRealm(id2);
			}
			catch (IdUnusedException e2)
			{
				return null;
			}
		}

	} // getRealm

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
		throws IdUnusedException, PermissionException, InUseException
	{
		String user = UsageSessionService.getSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));

		// get the site
		Site site = SiteService.getSite(siteId);

		// must be joinable
		if (!site.isJoinable())
		{
			throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
		}

		// the role to assign
		String role = site.getJoinerRole();
		if (role == null)
		{
			m_logger.warn(this +".joinSite(): null site joiner role for site: " + siteId);
			throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
		}

		// get the site's realm (the realm id is the site reference)
		String realmId = SiteService.siteReference(siteId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_REALM, realmReference(realmId));

		// check for existance
		if ((m_cache.get(realmReference(realmId)) == null) && (!m_storage.check(realmId)))
		{
			throw new IdUnusedException(realmId);
		}

		// ignore the cache - get the realm with a lock from the info store
		RealmEdit edit = m_storage.edit(realmId);
		if (edit == null)
			throw new InUseException(realmId);

		((BaseRealmEdit) edit).setEvent(SECURE_UPDATE_OWN_REALM);

		// give the user this role
		((BaseRealmEdit) edit).addUserRole(user, role);

		// and commit
		commitEdit(edit);
	}

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
		throws IdUnusedException, PermissionException, InUseException
	{
		String user = UsageSessionService.getSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));

		// get the site
		Site site = SiteService.getSite(siteId);

		// get the site's realm (the realm id is the site reference)
		String realmId = SiteService.siteReference(siteId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_REALM, realmReference(realmId));

		// check for existance
		if ((m_cache.get(realmReference(realmId)) == null) && (!m_storage.check(realmId)))
		{
			throw new IdUnusedException(realmId);
		}

		// ignore the cache - get the realm with a lock from the info store
		RealmEdit edit = m_storage.edit(realmId);
		if (edit == null)
			throw new InUseException(realmId);

		((BaseRealmEdit) edit).setEvent(SECURE_UPDATE_OWN_REALM);

		// remove the user completely
		((BaseRealmEdit) edit).removeUser(user);

		// and commit
		commitEdit(edit);
	}

	/**
	* Check permissions for updating a realm.
	* @param id The realm id.
	* @return true if the user is allowed to update the realm, false if not.
	*/
	public boolean allowUpdateRealm(String id)
	{
		return unlockCheck(SECURE_UPDATE_REALM, realmReference(id));

	} // allowUpdateRealm

	/**
	* Get a locked realm object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The realm id string.
	* @return A RealmEdit object for editing.
	* @exception IdUnusedException if not found, or if not an RealmEdit object
	* @exception PermissionException if the current user does not have permission to edit with this realm.
	* @exception InUseException if the realm is being edited by another user.
	*/
	public RealmEdit editRealm(String id) throws IdUnusedException, PermissionException, InUseException
	{
		if (id == null)
			throw new IdUnusedException("<null>");

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_REALM, realmReference(id));

		// check for existance
		if ((m_cache.get(realmReference(id)) == null) && (!m_storage.check(id)))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the realm with a lock from the info store
		RealmEdit realm = m_storage.edit(id);
		if (realm == null)
			throw new InUseException(id);

		((BaseRealmEdit) realm).setEvent(SECURE_UPDATE_REALM);

		return realm;

	} // editRealm

	/**
	* Commit the changes made to a RealmEdit object, and release the lock.
	* The RealmEdit is disabled, and not to be used after this call.
	* @param realm The RealmEdit object to commit.
	*/
	public void commitEdit(RealmEdit realm)
	{
		// check for closed edit
		if (!realm.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitEdit(): closed RealmEdit", e);
			}
			return;
		}

		// update the properties
		addLiveUpdateProperties(realm.getPropertiesEdit());

		// complete the edit
		m_storage.commit(realm);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseRealmEdit) realm).getEvent(), realm.getReference(), true));

		// close the edit object
		 ((BaseRealmEdit) realm).closeEdit();

	} // commitEdit

	/**
	* Cancel the changes made to a RealmEdit object, and release the lock.
	* The RealmEdit is disabled, and not to be used after this call.
	* @param realm The RealmEdit object to commit.
	*/
	public void cancelEdit(RealmEdit realm)
	{
		// check for closed edit
		if (!realm.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelEdit(): closed RealmEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancel(realm);

		// close the edit object
		 ((BaseRealmEdit) realm).closeEdit();

	} // cancelEdit

	/**
	* check permissions for addRealm().
	* @param id The realm id.
	* @return true if the current user is allowed to addRealm(id), false if not.
	*/
	public boolean allowAddRealm(String id)
	{
		return unlockCheck(SECURE_ADD_REALM, realmReference(id));

	} // allowAddRealm

	/**
	* Add a new realm.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The realm id.
	* @return The new locked realm object.
	* @exception IdInvalidException if the realm id is invalid.
	* @exception IdUsedException if the realm id is already used.
	* @exception PermissionException if the current user does not have permission to add a realm.
	*/
	public RealmEdit addRealm(String id) throws IdInvalidException, IdUsedException, PermissionException
	{
		// check for a valid realm name
		// %%% not this: we need the /: Validator.checkResourceId(id);

		// check security (throws if not permitted)
		unlock(SECURE_ADD_REALM, realmReference(id));

		// reserve a realm with this id from the info store - if it's in use, this will return null
		RealmEdit realm = m_storage.put(id);
		if (realm == null)
		{
			throw new IdUsedException(id);
		}

		((BaseRealmEdit) realm).setEvent(SECURE_ADD_REALM);

		return realm;

	} // addRealm

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
	public RealmEdit addRealm(String id, Realm other) throws IdInvalidException, IdUsedException, PermissionException
	{
		// make the new Realm
		RealmEdit edit = addRealm(id);

		// move in the values from the old Realm
		 ((BaseRealmEdit) edit).set(other);

		// restore the proper id!
		 ((BaseRealmEdit) edit).m_id = id;

		return edit;

	} // addRealm

	/**
	* check permissions for removeRealm().
	* @param id The realm id.
	* @return true if the realm is allowed to removeRealm(id), false if not.
	*/
	public boolean allowRemoveRealm(String id)
	{
		return unlockCheck(SECURE_REMOVE_REALM, realmReference(id));

	} // allowRemoveRealm

	/**
	* Remove this realm's information from the directory
	* - it must be a realm with a lock from editRealm().
	* The RealmEdit is disabled, and not to be used after this call.
	* @param realm The locked realm to remove.
	* @exception PermissionException if the current user does not have permission to remove this realm.
	*/
	public void removeRealm(RealmEdit realm) throws PermissionException
	{
		// check for closed edit
		if (!realm.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeRealm(): closed RealmEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_REALM, realm.getReference());

		// complete the edit
		m_storage.remove(realm);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_REALM, realm.getReference(), true));

		// close the edit object
		 ((BaseRealmEdit) realm).closeEdit();

	} // removeRealm

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The realm id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String realmReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;

	} // realmReference

	/**
	* Update this resource's realm so that it matches the setting for public view.
	* @param ref The resource reference
	* @param pubview The public view setting.
	* @throws InUseException if the ream is not availabe for modification.
	*/
	public void setPubView(String ref, boolean pubview) throws InUseException
	{
		// edit the realm
		RealmEdit edit = null;

		try
		{
			edit = editRealm(ref);
		}
		catch (IdUnusedException e)
		{
			// if no realm yet, and we need one, make one
			if (pubview)
			{
				try
				{
					edit = addRealm(ref);
				}
				catch (IdInvalidException ee)
				{
				}
				catch (IdUsedException ee)
				{
				}
				catch (PermissionException ee)
				{
				}
			}
		}
		catch (PermissionException e)
		{
		}
		catch (InUseException e)
		{
		}

		// if we have no realm and don't need one, we are done
		if ((edit == null) && (!pubview))
			return;

		// if we need a realm and didn't get an edit, exception
		if ((edit == null) && pubview)
			throw new InUseException(ref);

		boolean changed = false;
		boolean delete = false;

		// align the realm with our positive setting
		if (pubview)
		{
			// check for the pubview role
			RoleEdit role = edit.getRoleEdit(ROLE_PUBVIEW);
			if (role == null)
			{
				// create it
				try
				{
					role = edit.addRole(ROLE_PUBVIEW);
				}
				catch (IdUsedException ignore)
				{
				}

				// add all the abilities in the TEMPLATE_PUBVIEW realm's ROLE_PUBVIEW
				try
				{
					Realm template = getRealm(TEMPLATE_PUBVIEW);
					Role templatePubview = template.getRole(ROLE_PUBVIEW);
					if (templatePubview != null)
					{
						Set templateLocks = templatePubview.getLocks();
						role.addLocks(templateLocks);
						changed = true;
					}
					else
						m_logger.warn(
							this +".setPubView: missing role: " + ROLE_PUBVIEW + " in realm: " + TEMPLATE_PUBVIEW);
				}
				catch (IdUnusedException e)
				{
					m_logger.warn(this +".setPubView: missing realm: " + TEMPLATE_PUBVIEW);
				}
			}

			// make sure the anon user has this role
			Set anon = edit.getAnonRoles();
			if (!anon.contains(role))
			{
				edit.addAnonRole(role);
				changed = true;
			}

			// make sure the auth user has this role
			Set auth = edit.getAuthRoles();
			if (!auth.contains(role))
			{
				edit.addAuthRole(role);
				changed = true;
			}
		}

		// align the realm with our negative setting
		else
		{
			// get the role
			RoleEdit role = edit.getRoleEdit(ROLE_PUBVIEW);
			if (role != null)
			{
				// remove it from anon
				Set anon = edit.getAnonRoles();
				if (anon.contains(role))
				{
					edit.removeAnonRole(role);
					changed = true;
				}

				// remove it from auth
				Set auth = edit.getAnonRoles();
				if (auth.contains(role))
				{
					edit.removeAuthRole(role);
					changed = true;
				}

				// remove the role
				edit.removeRole(role);
				changed = true;
			}

			// if "empty", we can delete the realm
			if (edit.isEmpty())
				delete = true;
		}

		// if we want the realm deleted
		if (delete)
		{
			try
			{
				removeRealm(edit);
			}
			catch (PermissionException e)
			{
				cancelEdit(edit);
			}
		}

		// if we made a change
		else if (changed)
		{
			commitEdit(edit);
		}

		else
		{
			cancelEdit(edit);
		}

	} // setPubView

	/**
	* Does this resource support public view?
	* @param ref The resource reference
	* @return true if this resource supports public view, false if not.
	*/
	public boolean getPubView(String ref)
	{
		// get the realm
		try
		{
			Realm realm = getRealm(ref);

			// if the realm has no "pubview" role, no pub view
			Role pubview = realm.getRole(ROLE_PUBVIEW);
			if (pubview == null)
				return false;

			// if the anon and auth users don't have the "pubview" role, no pub view
			Set anon = realm.getAnonRoles();
			if (!anon.contains(pubview))
				return false;

			Set auth = realm.getAuthRoles();
			if (!auth.contains(pubview))
				return false;
		}
		catch (IdUnusedException e)
		{
			// if no realm, no pub view
			return false;
		}

		return true;

	} // getPubView

	/**
	* Does this resource inherit a public view setting from it's relevant set of realms, other than its own?
	* @param ref The resource reference
	* @return true if this resource inherits public view, false if not.
	*/
	public boolean getPubViewInheritance(String ref)
	{
		// the direct realm
		Realm directRealm = null;
		try
		{
			directRealm = getRealm(ref);
		}
		catch (Exception ignore)
		{
		}

		// make a reference, and get the relevant realms
		Reference r = new Reference(ref);
		List realms = r.getRealms();
		for (Iterator iRealms = realms.iterator(); iRealms.hasNext();)
		{
			Realm realm = (Realm) iRealms.next();
			if (realm != directRealm)
			{
				// check that there's the role, and that anon is set to it
				Role pubview = realm.getRole(ROLE_PUBVIEW);
				if (realm != null)
				{
					Set anon = realm.getAnonRoles();
					if (anon.contains(pubview))
						return true;
				}
			}
		}

		return false;

	} // getPubViewInheritance

	/**
	 * Create a new abilities object.
	 */
	public Abilities newAbilities()
	{
		return new MyAbilities();
	}

	/*******************************************************************************
	* Realm implementation
	*******************************************************************************/

	/**
	* <p>BaseRealm is an implementation of the CHEF Realm object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseRealm implements Realm
	{
		/** The realm id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** Map of userId to an Abilities */
		protected Map m_users = null;

		/** Abilities for "any authenticated" user. */
		protected Abilities m_authAbilities = null;

		/** Abilities for a non-authenticated (anon) user. */
		protected Abilities m_anonAbilities = null;

		/** Map of Role id to a Role defined in this Realm. */
		protected Map m_roles = null;

		/** The external realm id, or null if not defined. */
		protected String m_providerRealmId = null;

		/** The role to use for maintain users. */
		protected String m_maintainRole = null;

		/**
		* Construct.
		* @param id The realm id.
		*/
		public BaseRealm(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			m_users = new HashMap();
			m_anonAbilities = new MyAbilities();
			m_authAbilities = new MyAbilities();

			m_roles = new HashMap();

			// if the id is not null (a new realm, rather than a reconstruction)
			// add the automatic (live) properties
			if (m_id != null)
				addLiveProperties(props);

		} // BaseRealm

		/**
		* Construct from another Realm object.
		* @param realm The realm object to use for values.
		*/
		public BaseRealm(Realm realm)
		{
			setAll(realm);

		} // BaseRealm

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the realm.
		*/
		public BaseRealm(Element el)
		{
			m_users = new HashMap();
			m_anonAbilities = new MyAbilities();
			m_authAbilities = new MyAbilities();

			m_roles = new HashMap();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = StringUtil.trimToNull(el.getAttribute("id"));
			m_providerRealmId = StringUtil.trimToNull(el.getAttribute("provider-id"));
			m_maintainRole = StringUtil.trimToNull(el.getAttribute("maintain-role"));

			// pre-pass the role children to get roles avaialble for later parsing
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for a role
				if (element.getTagName().equals("role"))
				{
					Role role = new BaseRoleEdit(StringUtil.trimToNull(element.getAttribute("id")));
					m_roles.put(role.getId(), role);
				}
			}

			// fully process the children (properties, grants, roles)
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
				}

				// look for a role and update it
				else if (element.getTagName().equals("role"))
				{
					BaseRoleEdit role = new BaseRoleEdit(element, this);
					BaseRoleEdit already = (BaseRoleEdit) m_roles.get(role.getId());
					already.m_abilities = role.m_abilities;
				}

				// look for user - [ Role | lock ] ability
				else if (element.getTagName().equals("ability"))
				{
					String user = StringUtil.trimToNullLower(element.getAttribute("user"));
					String role = StringUtil.trimToNull(element.getAttribute("role"));
					String lock = StringUtil.trimToNull(element.getAttribute("lock"));
					String anon = StringUtil.trimToNull(element.getAttribute("anon"));
					String auth = StringUtil.trimToNull(element.getAttribute("auth"));

					// add to the users and roles
					grant(user, role, lock, anon, auth);
				}
			}

		} // BaseRealm

		/**
		* @return a description of the Realm.
		*/
		public String getDescription()
		{
			// the special ones
			if (getId().startsWith("!site.template"))
			{
				return "Site Realm Template";
			}

			else if (getId().equals("!site.user"))
			{
				return "MyWorkspace Realm Template";
			}

			else if (getId().startsWith("!user.template"))
			{
				return "User Realm Template";
			}

			else if (getId().equals(TEMPLATE_PUBVIEW))
			{
				return "Public View Realm Template";
			}

			else if (getId().equals("!site.helper"))
			{
				return "Site Helper Patch Realm";
			}

			else if (getId().startsWith("!"))
			{
				return "Special Realm";
			}
			
			// the rest are references to some resource
			try
			{
				Reference ref = new Reference(getId());
				return ref.getDescription();
			}
			catch (Throwable ignore){}

			return "unknown";

		} // getDescription

		/**
		* Add a user-role or user-lock.
		* @param user The user id (this or anon, or auth).
		* @param roleId The role id (this or lock).
		* @param lock The lock id (this or roleId).
		* @param anon Anon abilities grant indicator (this or user, or auth).
		* @param auth Authorized User abilities grant indicator (this or anon, or user).
		*/
		protected void grant(String user, String roleId, String lock, String anon, String auth)
		{
			Object grant = null;

			// if it's a role
			if (roleId != null)
			{
				// find the role
				grant = m_roles.get(roleId);
			}

			// for a lock
			else
			{
				grant = lock;
			}

			if (grant == null)
			{
				m_logger.warn(this +".grant: grant null: " + roleId + ", " + lock);
				return;
			}

			// for a user grant
			if (user != null)
			{
				synchronized (m_users)
				{
					// make sure we have an entry for this user
					Abilities abilities = (Abilities) m_users.get(user);
					if (abilities == null)
					{
						abilities = new MyAbilities();
						m_users.put(user, abilities);
					}
					abilities.add(grant);
				}
			}

			// for the anon abilities grant
			else if (anon != null)
			{
				// sync all changes to abilities on m_users
				synchronized (m_users)
				{
					m_anonAbilities.add(grant);
				}
			}

			// for the auth user grant
			else if (auth != null)
			{
				// sync all changes to abilities on m_users
				synchronized (m_users)
				{
					m_authAbilities.add(grant);
				}
			}

			else
			{
				m_logger.warn(this +".grant: user/auth/anon all null");
			}

		} // grant

		/**
		* Remove a user-role or user-lock.
		* @param user The user id (this or anon, or auth).
		* @param roleId The role id (this or lock).
		* @param lock The lock id (this or roleId).
		* @param anon Anon abilities grant indicator (this or user, or auth).
		* @param auth Authorized User abilities grant indicator (this or anon, or user).
		*/
		protected void revoke(String user, String roleId, String lock, String anon, String auth)
		{
			Object grant = null;

			// if it's a role
			if (roleId != null)
			{
				// find the role
				grant = m_roles.get(roleId);
			}

			// for a lock
			else
			{
				grant = lock;
			}

			if (grant == null)
			{
				m_logger.warn(this +".revoke: grant null: " + roleId + ", " + lock);
				return;
			}

			// for a user revoke
			if (user != null)
			{
				synchronized (m_users)
				{
					// make sure we have an entry for this user
					Abilities abilities = (Abilities) m_users.get(user);
					if (abilities != null)
					{
						abilities.remove(grant);

						// if now empty, remove the user
						if (abilities.isEmpty())
						{
							m_users.remove(user);
						}
					}
				}
			}

			// for the anon abilities grant
			else if (anon != null)
			{
				// sync all changes to abilities on m_users
				synchronized (m_users)
				{
					m_anonAbilities.remove(grant);
				}
			}

			// for the auth user grant
			else if (auth != null)
			{
				// sync all changes to abilities on m_users
				synchronized (m_users)
				{
					m_authAbilities.remove(grant);
				}
			}

			else
			{
				m_logger.warn(this +".revoke: user/auth/anon all null");
			}

		} // revoke

		/**
		* Take all values from this object.
		* @param realm The realm object to take values from.
		*/
		protected void setAll(Realm realm)
		{
			m_id = ((BaseRealm) realm).m_id;
			m_providerRealmId = ((BaseRealm) realm).m_providerRealmId;
			m_maintainRole = ((BaseRealm) realm).m_maintainRole;

			// make a copy of the roles as new Role objects - empty for now
			m_roles = new HashMap();
			for (Iterator it = ((BaseRealm) realm).m_roles.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseRoleEdit role = (BaseRoleEdit) entry.getValue();
				String id = (String) entry.getKey();

				m_roles.put(id, new BaseRoleEdit(id));
			}

			// now that we have roles, we can fill them, updating any Role references with our new Roles
			for (Iterator it = ((BaseRealm) realm).m_roles.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseRoleEdit role = (BaseRoleEdit) entry.getValue();
				String id = (String) entry.getKey();

				BaseRoleEdit myRole = (BaseRoleEdit) m_roles.get(id);

				// update the abilities to the new Roles
				for (Iterator ita = role.m_abilities.iterator(); ita.hasNext();)
				{
					Object obj = ita.next();
					if (obj instanceof Role)
					{
						obj = m_roles.get(((Role) obj).getId());
					}
					myRole.m_abilities.add(obj);
				}
			}

			// rebuild the anon abilities with the new roles
			m_anonAbilities = new MyAbilities();
			for (Iterator ita = ((BaseRealm) realm).m_anonAbilities.iterator(); ita.hasNext();)
			{
				Object obj = ita.next();
				if (obj instanceof Role)
				{
					obj = m_roles.get(((Role) obj).getId());
				}
				m_anonAbilities.add(obj);
			}

			// rebuild the auth abilities with the new roles
			m_authAbilities = new MyAbilities();
			for (Iterator ita = ((BaseRealm) realm).m_authAbilities.iterator(); ita.hasNext();)
			{
				Object obj = ita.next();
				if (obj instanceof Role)
				{
					obj = m_roles.get(((Role) obj).getId());
				}
				m_authAbilities.add(obj);
			}

			// make a deep copy (new Abilities, rebuilt for new roles) of the user set
			m_users = new HashMap();
			for (Iterator it = ((BaseRealm) realm).m_users.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Abilities abilities = (Abilities) entry.getValue();
				String id = (String) entry.getKey();

				Abilities a = new MyAbilities();
				m_users.put(id, a);

				for (Iterator ita = abilities.iterator(); ita.hasNext();)
				{
					Object obj = ita.next();
					if (obj instanceof Role)
					{
						obj = m_roles.get(((Role) obj).getId());
					}
					a.add(obj);
				}
			}

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(((BaseRealm) realm).m_properties);

		} // setAll

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element realm = doc.createElement("realm");

			if (stack.isEmpty())
			{
				doc.appendChild(realm);
			}
			else
			{
				((Element) stack.peek()).appendChild(realm);
			}

			stack.push(realm);

			realm.setAttribute("id", getId());
			if (m_providerRealmId != null)
			{
				realm.setAttribute("provider-id", m_providerRealmId);
			}
			if (m_maintainRole != null)
			{
				realm.setAttribute("maintain-role", m_maintainRole);
			}

			// properties
			m_properties.toXml(doc, stack);

			// roles (write before grants!)
			for (Iterator i = m_roles.values().iterator(); i.hasNext();)
			{
				BaseRoleEdit role = (BaseRoleEdit) i.next();
				role.toXml(doc, stack);
			}

			// user abilities
			for (Iterator i = m_users.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				Abilities abilities = (Abilities) entry.getValue();
				String user = (String) entry.getKey();
				for (Iterator a = abilities.iterator(); a.hasNext();)
				{
					Element element = doc.createElement("ability");
					realm.appendChild(element);
					element.setAttribute("user", user);

					Object next = a.next();

					if (next instanceof String)
					{
						element.setAttribute("lock", (String) next);
					}
					else
					{
						element.setAttribute("role", ((Role) next).getId());
					}
				}
			}

			// anon abilities
			for (Iterator a = m_anonAbilities.iterator(); a.hasNext();)
			{
				Element element = doc.createElement("ability");
				realm.appendChild(element);
				element.setAttribute("anon", "anon");

				Object next = a.next();

				if (next instanceof String)
				{
					element.setAttribute("lock", (String) next);
				}
				else
				{
					element.setAttribute("role", ((Role) next).getId());
				}
			}

			// auth abilities
			for (Iterator a = m_authAbilities.iterator(); a.hasNext();)
			{
				Element element = doc.createElement("ability");
				realm.appendChild(element);
				element.setAttribute("auth", "auth");

				Object next = a.next();

				if (next instanceof String)
				{
					element.setAttribute("lock", (String) next);
				}
				else
				{
					element.setAttribute("role", ((Role) next).getId());
				}
			}

			stack.pop();

			return realm;

		} // toXml

		/**
		* Access the realm id.
		* @return The realm id string.
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
			return realmReference(m_id);

		} // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Find the set of abilities for the user - the user's own, or the anon, else and the auth.
		* Add any abilities for this user's id from the provider.
		*/
		protected List findUserAbilities(User user)
		{
			List rv = new Vector();

			// see if we have this user defined
			Abilities abilities = (Abilities) m_users.get(user.getId());
			if (abilities != null)
			{
				rv.add(abilities);

				// if this is a known user, this is also an authenticated user
				rv.add(m_authAbilities);
			}

			// not known to the realm ...
			else
			{
				// anon?
				if (user.equals(UserDirectoryService.getAnonymousUser()))
				{
					rv.add(m_anonAbilities);
				}

				// not anon, but not otherwise known to the realm, give the auth abilities & provider
				else
				{
					rv.add(m_authAbilities);

					// Note: if the user is specifically know to the realm, we will not be checking the provider.
					// if there's a provider, and we are using it, and it has Abilities for this auth user, get them
					if ((m_provider != null) && (m_providerRealmId != null))
					{
						Abilities providerAbilities = m_provider.getAbilities(this, m_providerRealmId, user.getId());
						if (providerAbilities != null)
						{
							rv.add(providerAbilities);
						}
					}

				}
			}

			return rv;

		} // findUserAbilities

		/**
		* Iterate over all users abilities map entries, those defined here, or if none here, those provided.
		*/
		protected Iterator findUserAbilityMapEntries()
		{
			// no provider?  Use just the m_users
			if ((m_provider == null) || (m_providerRealmId == null))
			{
				return m_users.entrySet().iterator();
			}

			// get the Map (user id to Abilities) of grants from the provider
			Map fromProvider = m_provider.getAbilities(this, m_providerRealmId);

			// remove any user entry from this map that is in our m_users
			for (Iterator i = m_users.keySet().iterator(); i.hasNext();)
			{
				String userId = (String) i.next();
				fromProvider.remove(userId);
			}

			// return the combination of our user grants, and those from the provider for user not in our user grants
			return new SeriesIterator(
				m_users.entrySet().iterator(),
				fromProvider.entrySet().iterator());

		} // findUserAbilityMapEntries

		/**
		* Test if this user can unlock the lock in this Realm.
		* @param User The user.
		* @param lock The lock to open.
		* @param helperRealms Other realms that might refine role abilities.
		* @return true if this user can unlock the lock in this Realm, false if not.
		*/
		public boolean unlock(User user, String lock, List helperRealms)
		{
			// find the abilities for the user
			List abilities = findUserAbilities(user);
			for (Iterator it = abilities.iterator(); it.hasNext();)
			{
				Abilities a = (Abilities) it.next();
				if (a.unlock(lock, helperRealms))
					return true;
			}

			return false;

		} // unlock

		/**
		* Test if this user has this role in this Realm.
		* @param User The user.
		* @param Role The Role.
		* @return true if the User has this role in this Realm, false if not.
		*/
		public boolean hasRole(User user, Role role)
		{
			// find the abilities for the user
			List abilities = findUserAbilities(user);
			for (Iterator it = abilities.iterator(); it.hasNext();)
			{
				Abilities a = (Abilities) it.next();
				if (a.hasRole(role))
					return true;
			}

			return false;

		} // hasRole

		/**
		* Access all the locks the user may open in this Realm.
		* @param User The user.
		* @param helperRealms Other realms that might refine role abilities.
		* @return The Set of locks (String) the user is may open in this Realm.
		*/
		public Set collectUserLocks(User user, List helperRealms)
		{
			Set rv = new HashSet();

			// find the abilities for the user
			List abilities = findUserAbilities(user);
			for (Iterator it = abilities.iterator(); it.hasNext();)
			{
				Abilities a = (Abilities) it.next();
				rv.addAll(a.collectLocks(helperRealms));
			}

			return rv;

		} // collectUserLocks

		/**
		* Access all the Roles this user has in this Realm.
		* @param User The user.
		* @return The Set of roles (Role) for this user in this Realm.
		*/
		public Set collectUserRoles(User user)
		{
			Set rv = new HashSet();

			// find the abilities for the user
			List abilities = findUserAbilities(user);
			for (Iterator it = abilities.iterator(); it.hasNext();)
			{
				Abilities a = (Abilities) it.next();
				rv.addAll(a.collectRoles());
			}

			return rv;

		} // collectUserRoles

		/**
		* Access all users who can unlock this lock in this Realm.
		* @param lock The lock.
		* @param helperRealms Other realms that might refine role abilities.
		* @return The Set of users (User) who can unlock this lock in this Realm.
		*/
		public Set collectLockUsers(String lock, List helperRealms)
		{
			Set rv = new HashSet();

			// check all users
			for (Iterator it = findUserAbilityMapEntries(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Abilities abilities = (Abilities) entry.getValue();
				String user = (String) entry.getKey();

				// does this user have access to this lock?
				if (abilities.unlock(lock, helperRealms))
				{
					// add this user to the set
					try
					{
						rv.add(UserDirectoryService.getUser(user));
					}
					catch (IdUnusedException e)
					{
						if (m_logger.isDebugEnabled())
							m_logger.debug(this +".collectLockUsers: unknown user: " + user);
					}
				}
			}

			return rv;

		} // collectLockUsers

		/**
		* Access all users who have this role in this Realm.
		* @param role The Role.
		* @return The Set of users (User) who have this role in this Realm.
		*/
		public Set collectRoleUsers(Role role)
		{
			Set rv = new HashSet();

			// check all users
			for (Iterator it = findUserAbilityMapEntries(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Abilities abilities = (Abilities) entry.getValue();
				String user = (String) entry.getKey();

				// does this user have access to this lock?
				if (abilities.hasRole(role))
				{
					// add this user to the set
					try
					{
						rv.add(UserDirectoryService.getUser(user));
					}
					catch (IdUnusedException e)
					{
						if (m_logger.isDebugEnabled())
							m_logger.debug(this +".collectRoleUsers: unknown user: " + user);
					}
				}
			}

			return rv;

		} // collectRoleUsers

		/**
		* Access all users who have any role or are allowed through any lock in this Realm.
		* @return The Set of users (User) who have any role or are allowed through any lock in this Realm.
		*/
		public Set collectUsers()
		{
			// return a copy as Users
			Set rv = new HashSet();
			for (Iterator it = findUserAbilityMapEntries(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				try
				{
					rv.add(UserDirectoryService.getUser(user));
				}
				catch (IdUnusedException e)
				{
					if (m_logger.isDebugEnabled())
						m_logger.debug(this +".collectUsers: unknown user: " + user);
				}
			}

			return rv;

		} // collectUsers

		/**
		* Access all users who have direct role ability grants in the Realm.
		* @return The Set of users (User) who have direct role ability grants in the Realm.
		*/
		public Set getUsers()
		{
			// return a copy as Users
			Set rv = new HashSet();
			for (Iterator it = m_users.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				try
				{
					rv.add(UserDirectoryService.getUser(user));
				}
				catch (IdUnusedException e)
				{
					if (m_logger.isDebugEnabled())
						m_logger.debug(this +".getUsers: unknown user: " + user);
				}
			}

			return rv;

		} // getUsers

		/**
		* Access all the locks directly added for the user in this Realm.
		* @param User The user.
		* @return The Set of locks (String) directly added for the user in this Realm.
		*/
		public Set getUserLocks(User user)
		{
			// find the abilities for the user
			Abilities abilities = (Abilities) m_users.get(user.getId());
			if (abilities == null)
				return new HashSet();

			return abilities.getLocks();

		} // getUserLocks

		/**
		* Access all the locks directly added for all authenticated users in the Realm.
		* @return The Set of locks (String) directly added for all authenticated users in the Realm.
		*/
		public Set getAuthLocks()
		{
			return m_authAbilities.getLocks();

		} // getAuthLocks

		/**
		* Access all the locks directly added for the non-authenticated user (anon) in the Realm.
		* @return The Set of locks (String) directly added for the non-authenticated user (anon) in the Realm.
		*/
		public Set getAnonLocks()
		{
			return m_anonAbilities.getLocks();

		} // getAnonLocks

		/**
		* Access all roles directly added for the user in this Realm.
		* @param User The user.
		* @return The Set of roles (Role) directly added for the user in this Realm.
		*/
		public Set getUserRoles(User user)
		{
			// find the abilities for the user
			Abilities abilities = (Abilities) m_users.get(user.getId());
			if (abilities == null)
				return new HashSet();

			return abilities.getRoles();

		} // getUserRoles

		/**
		* Access all roles directly added for all authenticated users in the Realm.
		* @return The Set of roles (Role) directly added for all authenticated users in the Realm.
		*/
		public Set getAuthRoles()
		{
			return m_authAbilities.getRoles();

		} // getAuthRoles

		/**
		* Access all roles directly added for the non-authenticated user (anon) in the Realm.
		* @return The Set of roles (Role) directly added for the non-authenticated user (anon) in the Realm.
		*/
		public Set getAnonRoles()
		{
			return m_anonAbilities.getRoles();

		} // getAnonRoles

		/**
		* Access all Roles defined for this Realm.
		* @return The set of roles (Role) defined for this Realm.
		*/
		public Set getRoles()
		{
			return new HashSet(m_roles.values());

		} // getRoles

		/**
		* Access a Role defined in this Realm.
		* @param id The role id.
		* @return The Role, if found, of null, if not.
		*/
		public Role getRole(String id)
		{
			return (Role) m_roles.get(id);

		} // getRole

		/**
		* Access the realm id for the RealmProvider for this Realm.
		* @return The the realm id for the RealmProvider for this Realm, or null if none defined.
		*/
		public String getProviderRealmId()
		{
			return m_providerRealmId;

		} // getProviderRealmId

		/**
		* Is this realm empty of any roles or grants?
		* @return true if the realm is empty, false if not.
		*/
		public boolean isEmpty()
		{
			// no roles, no grants to users, nothing in anon or auth
			if (getRoles().isEmpty() && getUsers().isEmpty() && getAuthLocks().isEmpty() && getAnonLocks().isEmpty())
			{
				return true;
			}

			return false;

		} // isEmpty

		/**
		 * {@inheritDoc}
		 */
		public String getMaintainRole()
		{
			if (m_maintainRole == null)
			{
				return "maintain";
			}

			return m_maintainRole;
		}

		/**
		* Are these objects equal?  If they are both Realm objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Realm))
				return false;
			return ((Realm) obj).getId().equals(getId());

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
			if (!(obj instanceof Realm))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((Realm) obj).getId());

			return compare;

		} // compareTo

	} // BaseRealm

	/*******************************************************************************
	* RealmEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseRealmEdit is an implementation of the CHEF RealmEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseRealmEdit extends BaseRealm implements RealmEdit, SessionStateBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct.
		* @param id The realm id.
		*/
		public BaseRealmEdit(String id)
		{
			super(id);

		} // BaseRealmEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the realm.
		*/
		public BaseRealmEdit(Element el)
		{
			super(el);

		} // BaseRealmEdit

		/**
		* Construct from another Realm object.
		* @param realm The realm object to use for values.
		*/
		public BaseRealmEdit(Realm realm)
		{
			super(realm);

		} // BaseRealmEdit

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
		* Take all values from this object.
		* @param realm The realm object to take values from.
		*/
		protected void set(Realm realm)
		{
			setAll(realm);

		} // set

		/**
		* Add this role to this user in this Realm.
		* @param user The user.
		* @param role The Role.
		*/
		public void addUserRole(User user, Role role)
		{
			grant(user.getId(), role.getId(), null, null, null);

		} // addUserRole

		/**
		* Add this role to this user in this Realm.
		* @param user The User id.
		* @param role The Role id.
		*/
		protected void addUserRole(String user, String role)
		{
			grant(user, role, null, null, null);

		} // addUserRole

		/**
		* Add this role to the non-authenticated (anon) user in this Realm.
		* @param role The Role.
		*/
		public void addAnonRole(Role role)
		{
			grant(null, role.getId(), null, "anon", null);

		} // addAnonRole

		/**
		* Add this role to all authenticated users in this Realm.
		* @param role The Role.
		*/
		public void addAuthRole(Role role)
		{
			grant(null, role.getId(), null, null, "auth");

		} // addAuthRole

		/**
		* Add the ablilty to open this lock to this user in this Realm.
		* @param user The user.
		* @param lock The lock.
		*/
		public void addUserLock(User user, String lock)
		{
			grant(user.getId(), null, lock, null, null);

		} // addUserLock

		/**
		* Add the ablilty to open this lock to the non-authenticated (anon) user in this Realm.
		* @param lock The lock.
		*/
		public void addAnonLock(String lock)
		{
			grant(null, null, lock, "anon", null);

		} // addAnonLock

		/**
		* Add the ablilty to open this lock to all authenticated users in this Realm.
		* @param lock The lock.
		*/
		public void addAuthLock(String lock)
		{
			grant(null, null, lock, null, "auth");

		} // addAuthLock

		/**
		* Remove this Role from this User in this Realm.
		* @param user The user.
		* @param role The Role.
		*/
		public void removeUserRole(User user, Role role)
		{
			revoke(user.getId(), role.getId(), null, null, null);

		} // removeUserRole

		/**
		* Remove this Role from the non-authenticated (anon) user in this Realm.
		* @param role The Role.
		*/
		public void removeAnonRole(Role role)
		{
			revoke(null, role.getId(), null, "anon", null);

		} // removeAnonRole

		/**
		* Remove this Role from all authenticated users in this Realm.
		* @param role The Role.
		*/
		public void removeAuthRole(Role role)
		{
			revoke(null, role.getId(), null, null, "auth");

		} // removeAuthRole

		/**
		* Remove the ability to open this lock from this User in this Realm.
		* @param user The user.
		* @param lock The lock.
		*/
		public void removeUserLock(User user, String lock)
		{
			revoke(user.getId(), null, lock, null, null);

		} // removeUserLock

		/**
		* Remove the ability to open this lock from the non-authenticated (anon) user in this Realm.
		* @param lock The lock.
		*/
		public void removeAnonLock(String lock)
		{
			revoke(null, null, lock, "anon", null);

		} // removeAnonLock

		/**
		* Remove the ability to open this lock from all authenticated users in this Realm.
		* @param lock The lock.
		*/
		public void removeAuthLock(String lock)
		{
			revoke(null, null, lock, null, "auth");

		} // removeAuthLock

		/**
		* Remove all Roles and locks for this user from this Realm.
		* @param user The user.
		*/
		public void removeUser(User user)
		{
			synchronized (m_users)
			{
				m_users.remove(user.getId());
			}

		} // removeUserLock

		/**
		* Remove all Roles and locks for this user from this Realm.
		* @param user The user.
		*/
		protected void removeUser(String user)
		{
			synchronized (m_users)
			{
				m_users.remove(user);
			}

		} // removeUserLock

		/**
		* Remove all Roles and locks for the non-authenticated (anon) user in this Realm.
		*/
		public void removeAnon()
		{
			synchronized (m_users)
			{
				m_anonAbilities.clear();
			}

		} // removeAnon

		/**
		* Remove all Roles and locks for all authenticated users in this Realm.
		*/
		public void removeAuth()
		{
			synchronized (m_users)
			{
				m_authAbilities.clear();
			}

		} // removeAuth

		/**
		* Remove all Roles and locks for all users from this Realm.
		*/
		public void removeUsers()
		{
			synchronized (m_users)
			{
				m_users.clear();
				m_anonAbilities.clear();
				m_authAbilities.clear();
			}

		} // removeUsers

		/**
		* Create a new Role within this Realm.
		* @param id The role id.
		* @return the new Role.
		* @exception IdUsedException if the id is already a Role in this Realm.
		*/
		public RoleEdit addRole(String id) throws IdUsedException
		{
			synchronized (m_users)
			{
				RoleEdit role = (RoleEdit) m_roles.get(id);
				if (role != null)
					throw new IdUsedException(id);

				role = new BaseRoleEdit(id);
				m_roles.put(role.getId(), role);

				return role;
			}

		} // addRole

		/**
		* Create a new Role within this Realm, as a copy of this other role
		* @param id The role id.
		* @param other The role to copy.
		* @return the new Role.
		* @exception IdUsedException if the id is already a Role in this Realm.
		*/
		public RoleEdit addRole(String id, Role other) throws IdUsedException
		{
			synchronized (m_users)
			{
				RoleEdit role = (RoleEdit) m_roles.get(id);
				if (role != null)
					throw new IdUsedException(id);

				role = new BaseRoleEdit(id, other);
				m_roles.put(role.getId(), role);

				return role;
			}

		} // addRole

		/**
		* Remove this Role from this Realm.  Any grants of this Role in the Realm are also removed.
		* @param role The Role.
		*/
		public void removeRole(Role role)
		{
			synchronized (m_users)
			{
				Role r = (Role) m_roles.get(role.getId());
				if (r != null)
				{
					m_roles.remove(role.getId());

					// remove the role from any appearance in m_users Abilities,
					// or in m_authAbilities, m_anonAbilities,
					// or in any of the remaining Roles
					for (Iterator it = m_users.entrySet().iterator(); it.hasNext();)
					{
						Map.Entry entry = (Map.Entry) it.next();
						Abilities abilities = (Abilities) entry.getValue();
						String id = (String) entry.getKey();

						if (abilities.contains(r))
						{
							abilities.remove(r);

							// if there's nothing left in the abilities, remove it from the m_users set
							if (abilities.isEmpty())
							{
								it.remove();
							}
						}
					}

					m_authAbilities.remove(r);
					m_anonAbilities.remove(r);

					for (Iterator it = m_roles.entrySet().iterator(); it.hasNext();)
					{
						Map.Entry entry = (Map.Entry) it.next();
						RoleEdit edit = (RoleEdit) entry.getValue();
						String id = (String) entry.getKey();

						if (edit.getRoles().contains(r))
						{
							edit.removeRole(r);
						}
					}
				}
			}

		} // removeRole		

		/**
		* Remove all Roles from this Realm.
		*/
		public void clearRoles()
		{
			// clear all Role type entries from any Sets in m_users  %%%
			synchronized (m_users)
			{
				m_roles.clear();

				// remove all roles from any appearance in m_users Abilities,
				// or in m_authAbilities, m_anonAbilities,
				for (Iterator it = m_users.entrySet().iterator(); it.hasNext();)
				{
					Map.Entry entry = (Map.Entry) it.next();
					Abilities abilities = (Abilities) entry.getValue();
					String id = (String) entry.getKey();

					for (Iterator ita = abilities.iterator(); ita.hasNext();)
					{
						Object obj = ita.next();
						if (obj instanceof Role)
						{
							ita.remove();
						}

					}
					// if there's nothing left in the abilities, remove it from the m_users set
					if (abilities.isEmpty())
					{
						it.remove();
					}
				}

				for (Iterator ita = m_authAbilities.iterator(); ita.hasNext();)
				{
					Object obj = ita.next();
					if (obj instanceof Role)
					{
						ita.remove();
					}
				}

				for (Iterator ita = m_anonAbilities.iterator(); ita.hasNext();)
				{
					Object obj = ita.next();
					if (obj instanceof Role)
					{
						ita.remove();
					}
				}
			}

		} // clearRoles

		/**
		* Access a Role defined in this Realm for editing.
		* @param id The role id.
		* @return The Role, if found, or null, if not.
		*/
		public RoleEdit getRoleEdit(String id)
		{
			BaseRoleEdit edit = (BaseRoleEdit) m_roles.get(id);
			if (edit != null)
				edit.activate();

			return edit;

		} // getRoleEdit

		/**
		* Set the realm id for the RealmProvider for this Realm (set to null to have none).
		* @param id The realm id for the RealmProvider, or null if there is to be none.
		*/
		public void setProviderRealmId(String id)
		{
			m_providerRealmId = id;

		} // setProviderRealmId

		/**
		 * {@inheritDoc}
		 */
		public void setMaintainRole(String role)
		{
			m_maintainRole = role;
		}

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

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // valueUnbound

	} // BaseRealmEdit

	/*******************************************************************************
	* RoleEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseRoleEdit is an implementation of the CHEF RoleEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseRoleEdit implements RoleEdit
	{
		/** The role id. */
		protected String m_id = null;

		/** The abilities of this role. */
		protected Abilities m_abilities = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct.
		* @param id The role id.
		*/
		public BaseRoleEdit(String id)
		{
			m_id = id;
			m_abilities = new MyAbilities();

		} // BaseRoleEdit

		/**
		* Construct as a copy
		* @param id The role id.
		* @param other The role to copy.
		*/
		public BaseRoleEdit(String id, Role other)
		{
			m_id = id;
			m_abilities = new MyAbilities((MyAbilities)(((BaseRoleEdit) other).m_abilities));

		} // BaseRoleEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the role.
		*/
		public BaseRoleEdit(Element el, BaseRealm realm)
		{
			m_abilities = new MyAbilities();

			m_id = StringUtil.trimToNull(el.getAttribute("id"));

			// the children (grants)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for role | lock ability
				if (element.getTagName().equals("ability"))
				{
					String role = StringUtil.trimToNull(element.getAttribute("role"));
					String lock = StringUtil.trimToNull(element.getAttribute("lock"));

					// add to the users and role
					Object grant = null;
					if (role != null)
					{
						grant = realm.getRole(role);
					}
					else
					{
						grant = lock;
					}
					if (grant != null)
					{
						m_abilities.add(grant);
					}
					else
					{
						m_logger.warn(this +"el constructor: grant null: " + role + ", " + lock);
					}
				}
			}

		} // BaseRoleEdit

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element role = doc.createElement("role");

			if (stack.isEmpty())
			{
				doc.appendChild(role);
			}
			else
			{
				((Element) stack.peek()).appendChild(role);
			}

			stack.push(role);

			role.setAttribute("id", getId());

			// abilities
			for (Iterator a = m_abilities.iterator(); a.hasNext();)
			{
				Element element = doc.createElement("ability");
				role.appendChild(element);

				Object next = a.next();

				if (next instanceof String)
				{
					element.setAttribute("lock", (String) next);
				}
				else
				{
					element.setAttribute("role", ((Role) next).getId());
				}
			}

			stack.pop();

			return role;

		} // toXml

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
		* Access the Role id.
		* @return The role id.
		*/
		public String getId()
		{
			return m_id;
		}

		/**
		* Test if a holder of this Role can unlock the lock.
		* @param lock The lock to open.
		* @return true if a holder of this Role can unlock the lock, false if not.
		*/
		public boolean unlock(String lock)
		{
			// Note: this will be recursive if the lock is not defined directly and there are Roles
			// in m_abilities
			return m_abilities.unlock(lock, null);

		} // unlock

		/**
		* Test if a holder of this Role thereby holds this other Role.
		* @param role The other role.
		* @return true if a holder of this Role thereby holds this other Role.
		*/
		public boolean hasRole(Role role)
		{
			// Note: this will be recursive if the role is not defined directly and there are Roles
			// in m_abilities
			return m_abilities.hasRole(role);

		} // unlock

		/**
		* Access all the locks which may be opened by holders of this Role.
		* @return The Set of locks (String) which may be opened by users with this Role.
		*/
		public Set collectLocks()
		{
			// Note: this may be recursive if there are Roles in m_abilities
			return m_abilities.collectLocks(null);

		} // collectLocks

		/**
		* Access all the roles that holders of this Role thereby also have.
		* Not including me!
		* @return The Set of roles (Role) that holders of this Role thereby also have.
		*/
		public Set collectRoles()
		{
			// Note: this may be recursive if there are Roles in m_abilities
			return m_abilities.collectRoles();

		} // collectLocks

		/**
		* Access all the locks added to this Role.
		* @return The Set of locks (String) locks added to this Role.
		*/
		public Set getLocks()
		{
			return m_abilities.getLocks();

		} // getLocks

		/**
		* Access all roles added to this Role.
		* @return The Set of roles (Role) added to this Role.
		*/
		public Set getRoles()
		{
			return m_abilities.getRoles();

		} // getRoles

		/**
		* Add the ability to open this lock to this Role.
		* @param lock The lock.
		*/
		public void addLock(String lock)
		{
			m_abilities.add(lock);

		} // addLock

		/**
		* Add the ability to open these locks to this Role.
		* @param lock The lock.
		*/
		public void addLocks(Set locks)
		{
			m_abilities.addAll(locks);

		} // addLock

		/**
		* Remove the ability to open this lock from this Role.
		* @param lock The lock.
		*/
		public void removeLock(String lock)
		{
			m_abilities.remove(lock);

		} // removeLock

		/**
		* Add this other role to be part of this Role, so that this Role has all the abilities of the another Role.
		* @param role The other Role.
		*/
		public void addRole(Role role)
		{
			m_abilities.add(role);

		} // addRole

		/**
		* Remove this other Role from this Role.
		* @param role The other Role.
		*/
		public void removeRole(Role role)
		{
			m_abilities.remove(role);

		} // removeRole

		/**
		* Remove all locks and Roles from the Role.
		*/
		public void clear()
		{
			m_abilities.clear();

		} // clear

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Role))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((Role) obj).getId());

			return compare;

		} // compareTo

		/**
		* Are these objects equal?  If they are both Realm objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Role))
				return false;
			return ((Role) obj).getId().equals(getId());

		} // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		} // hashCode

	} // BaseRoleEdit

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
		* Check if a realm by this id exists.
		* @param id The realm id.
		* @return true if a realm by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the realm with this id, or null if not found.
		* @param id The realm id.
		* @return The realm with this id, or null if not found.
		*/
		public Realm get(String id);

		/**
		* Get all realms.
		* @return The list of all realms.
		*/
		public List getAll();

		/**
		* Add a new realm with this id.
		* @param id The realm id.
		* @return The locked Realm object with this id, or null if the id is in use.
		*/
		public RealmEdit put(String id);

		/**
		* Get a lock on the realm with this id, or null if a lock cannot be gotten.
		* @param id The realm id.
		* @return The locked Realm with this id, or null if this records cannot be locked.
		*/
		public RealmEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param realm The realm to commit.
		*/
		public void commit(RealmEdit realm);

		/**
		* Cancel the changes and release the lock.
		* @param realm The realm to commit.
		*/
		public void cancel(RealmEdit realm);

		/**
		* Remove this realm.
		* @param realm The realm to remove.
		*/
		public void remove(RealmEdit realm);

	} // Storage

	/*******************************************************************************
	* StorageUser implementation (no container)
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other)
	{
		return null;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{
		return new BaseRealm(id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{
		return new BaseRealm(element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{
		return new BaseRealm((Realm) other);
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
	public Edit newContainerEdit(Resource other)
	{
		return null;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseRealmEdit e = new BaseRealmEdit(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseRealmEdit e = new BaseRealmEdit(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseRealmEdit e = new BaseRealmEdit((Realm) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r)
	{
		return null;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Resource r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Resource r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Resource r)
	{
		return null;
	}

	/*******************************************************************************
	* CacheRefresher implementation (no container)
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		// key is a reference, but our storage wants an id
		String id = realmId((String) key);

		// get whatever we have from storage for the cache for this vale
		Realm realm = m_storage.get(id);

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".refresh(): " + key + " : " + id);

		return realm;

	} // refresh

	public class MyAbilities
		extends HashSet
		implements Abilities
	{
		/**
		* Construct.
		*/
		public MyAbilities()
		{
			super();

		}	// Abilities

		/**
		* Construct as a copy
		*/
		public MyAbilities(MyAbilities other)
		{
			super(other);

		}	// Abilities

		/**
		* Check this ability set for a lock.
		* @param lock The lock.
		* @param helperRealms Other realms that might refine role abilities.
		* @return true if this lock is within this abilities, false if not.
		*/
		public boolean unlock(String lock, List helperRealms)
		{
			// direct?
			if (contains(lock)) return true;

			// check each role
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();
				if (next instanceof Role)
				{
					if (((Role) next).unlock(lock)) return true;
				
					// see if this role is refined in the helper realms
					if ((helperRealms != null) && (helperRealms.size() > 0))
					{
						for (Iterator iRealms = helperRealms.iterator(); iRealms.hasNext(); )
						{
							Realm realm = (Realm) iRealms.next();

							// find this role by id
							Role role = realm.getRole(((Role) next).getId());
							if (role != null)
							{
								if (role.unlock(lock)) return true;
							}
						}
					}
				}
			}

			return false;

		}	// unlock

		/**
		* Check for this role in this abilities.
		* @param role The Role.
		*/
		public boolean hasRole(Role role)
		{
			// direct?
			if (contains(role)) return true;

			// check each role
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();
				if (next instanceof Role)
				{
					if (((Role) next).hasRole(role)) return true;
				}
			}

			return false;

		}	// hasRole

		/**
		* Access all the locks this ability set can open.
		* @param helperRealms Other realms that might refine role abilities.
		* @return The Set of locks (String) this ability set can open.
		*/
		public Set collectLocks(List helperRealms)
		{
			Set rv = new HashSet();
		
			// check all abilities
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();

				// String - a lock
				if (next instanceof String)
				{
					rv.add(next);
				}

				// else, a Role
				else
				{
					rv.addAll(((Role) next).collectLocks());

					// see if this role is refined in the helper realms
					if ((helperRealms != null) && (helperRealms.size() > 0))
					{
						for (Iterator iRealms = helperRealms.iterator(); iRealms.hasNext(); )
						{
							Realm realm = (Realm) iRealms.next();

							// find this role by id
							Role role = realm.getRole(((Role) next).getId());
							if (role != null)
							{
								rv.addAll(role.collectLocks());
							}
						}
					}
				}
			}

			return rv;

		}	// collectLocks

		/**
		* Access all the Roles this ability set can open.
		* @return The Set of roles (Role) this ability set can open.
		*/
		public Set collectRoles()
		{
			Set rv = new HashSet();

			// check all abilities
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();

				// for the Roles
				if (next instanceof Role)
				{
					// add this Role
					rv.add(next);
				
					// and any Roles contained herein
					rv.addAll(((Role) next).collectRoles());
				}
			}

			return rv;

		}	// collectRoles

		/**
		* Access all the locks directly added to this ability set.
		* @return The Set of locks (String) directly added to this ability set.
		*/
		public Set getLocks()
		{
			Set rv = new HashSet();
		
			// check all abilities
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();

				// String - a lock
				if (next instanceof String)
				{
					rv.add(next);
				}
			}

			return rv;

		}	// getLocks

		/**
		* Access all roles directly added to this ability set.
		* @return The Set of roles (Role) directly added to this ability set.
		*/
		public Set getRoles()
		{
			Set rv = new HashSet();
		
			// check all abilities
			for (Iterator it = iterator(); it.hasNext();)
			{
				Object next = it.next();

				// for the Roles
				if (next instanceof Role)
				{
					rv.add(next);
				}
			}

			return rv;

		}	// getRoles

	}	// MyAbilities

} // BaseRealmService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/BaseRealmService.java,v 1.22 2004/10/07 01:57:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
