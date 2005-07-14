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
package org.sakaiproject.component.legacy.realm;

// imports
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;

import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.realm.Grant;
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
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseRealmService is a Sakai realm service implementation.</p>
* <p>To support the public view feature, a realm named TEMPLATE_PUBVIEW must exist, with a role
* named ROLE_PUBVIEW - all the abilities in this role become the public view abilities for any resource.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public abstract class BaseRealmService implements RealmService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

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
	protected void addLiveProperties(BaseRealm realm)
	{
		String current = UsageSessionService.getSessionUserId();

		realm.m_createdUserId = current;
		realm.m_lastModifiedUserId = current;

		Time now = TimeService.newTime();
		realm.m_createdTime = now;
		realm.m_lastModifiedTime = (Time) now.clone();

	} //  addLiveProperties

	/**
	 * Update the live properties for a realm for when modified.
	 */
	protected void addLiveUpdateProperties(BaseRealm realm)
	{
		String current = UsageSessionService.getSessionUserId();

		realm.m_lastModifiedUserId = current;
		realm.m_lastModifiedTime = TimeService.newTime();

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
		m_storage.close();
		m_storage = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	 * RealmService implementation
	*******************************************************************************/

	/**
	 * Access a list of Realm objets that meet specified criteria, naturally sorted.
	 * @param criteria Selection criteria: realms returned will match this string somewhere in their
	 * 			id, or provider realm id.
	 * @param page The PagePosition subset of items to return.
	 * @return The List (Realm) of Rrealm objets that meet specified criteria.
	 */
	public List getRealms(String criteria, PagingPosition page)
	{
		return m_storage.getRealms(criteria, page);
	}

	/**
	 * Count the Realm objets that meet specified criteria.
	 * @param criteria Selection criteria: realms returned will match this string somewhere in their
	 * 			id, or provider realm id.
	 * @return The count of Realm objets that meet specified criteria.
	 */
	public int countRealms(String criteria)
	{
		return m_storage.countRealms(criteria);
	}

	/**
	 * Access a realm object.
	 * @param id The realm id string.
	 * @return A realm object containing the realm information.
	 * @exception IdUnusedException if not found.
	 */
	public Realm getRealm(String id) throws IdUnusedException
	{
		// Note: since this is a "read" operations, we do NOT refresh (i.e. write) the provider info.
		if (id == null)
			throw new IdUnusedException("<null>");

		Realm realm = realm = m_storage.get(id);

		// if not found
		if (realm == null)
		{
			throw new IdUnusedException(id);
		}

		// track it - Note: we are not tracking realm access -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(SECURE_ACCESS_REALM, realm.getReference()));

		return realm;
	}

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
		String roleId = site.getJoinerRole();
		if (roleId == null)
		{
			m_logger.warn(this +".joinSite(): null site joiner role for site: " + siteId);
			throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
		}

		// get the site's realm (the realm id is the site reference)
		String realmId = SiteService.siteReference(siteId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_REALM, realmReference(realmId));

		// check for existance
		if (!m_storage.check(realmId))
		{
			throw new IdUnusedException(realmId);
		}

		// get the realm with a lock from the info store
		RealmEdit edit = m_storage.edit(realmId);
		if (edit == null)
			throw new InUseException(realmId);

		((BaseRealmEdit) edit).setEvent(SECURE_UPDATE_OWN_REALM);

		// see if already joined
		MyGrant grant = (MyGrant) edit.getUserGrant(user);
		if (grant != null)
		{
			// if inactive, make it active
			if (!grant.active) grant.active = true;
		}

		// give the user this role
		else
		{
			edit.addUserRole(user, roleId, true, false);
		}

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
		if (!m_storage.check(realmId))
		{
			throw new IdUnusedException(realmId);
		}

		// get the realm with a lock from the info store
		RealmEdit edit = m_storage.edit(realmId);
		if (edit == null)
		{
			throw new InUseException(realmId);
		}

		// if not joined (no grant), we are done
		MyGrant grant = (MyGrant) edit.getUserGrant(user);
		if (grant == null)
		{
			cancelEdit(edit);
			return;
		}

		// if joined with the maintain role, and the joiner role is different or the site is not joinable
		// (so the user could not re-join as maintainer), don't allow the unjoin
		
		// update: if joined with the maintain role, but not the only maintainer of the realm
		// allow the unjoin
		
		String maintainRole = edit.getMaintainRole();
		String joinerRole = site.getJoinerRole();
			
		if (!StringUtil.different(maintainRole, grant.getRole().getId()))
		{
			// if maintainter, check if the only maintainer
			Set maintainers = edit.getUsersWithRole(maintainRole);
			if (maintainers.size() <= 1)
			{
				if (StringUtil.different(maintainRole, joinerRole) || !site.isJoinable())
				{
					cancelEdit(edit);
					throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
				}
			}
		}
		
		((BaseRealmEdit) edit).setEvent(SECURE_UPDATE_OWN_REALM);

		// if the grant is provider, make it inactive so it doesn't revert to provider status
		if (grant.isProvided())
		{
			grant.active = false;
		}
		else
		{
			// remove the user completely
			((BaseRealmEdit) edit).removeUser(user);
		}

		// and commit
		commitEdit(edit);
	}

	/**
	 * check permissions for unjoin() - unjoining the site and removing all role relationships.
	 * @param id The site id.
	 * @return true if the user is allowed to unjoin(id), false if not.
	 */
	public boolean allowUnjoinSite(String siteId)
	{
		String user = UsageSessionService.getSessionUserId();
		try
		{
			if (user == null) 
			{
				throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
			}
			// get the site
			Site site = SiteService.getSite(siteId);

			// get the site's realm (the realm id is the site reference)
			String realmId = SiteService.siteReference(siteId);

			// check security (throws if not permitted)
			unlock(SECURE_UPDATE_OWN_REALM, realmReference(realmId));

			// check for existance
			if (!m_storage.check(realmId))
			{
				throw new IdUnusedException(realmId);
			}

			// get the realm with a lock from the info store
			Realm realm = m_storage.get(realmId);
			// if not joined (no grant), we are done
			// if the grant is provider, unable to unjoin the site
			MyGrant grant = (MyGrant) realm.getUserGrant(user);
			if (grant == null)
			{
				return false;
			}
			else if (grant.isProvided())
			{
				return false;
			}

			// if joined with the maintain role, and the joiner role is different or the site is not joinable
			// (so the user could not re-join as maintainer), don't allow the unjoin

			// update: if joined with the maintain role, but not the only maintainer of the realm
			// allow the unjoin

			String maintainRole = realm.getMaintainRole();
			String joinerRole = site.getJoinerRole();
	
			if (!StringUtil.different(maintainRole, grant.getRole().getId()))
			{
				// if maintainter, check if the only maintainer
				Set maintainers = realm.getUsersWithRole(maintainRole);
				if (maintainers.size() <= 1)
				{
					if (StringUtil.different(maintainRole, joinerRole) || !site.isJoinable())
					{
						throw new PermissionException(user, SECURE_UPDATE_OWN_REALM, SiteService.siteReference(siteId));
					}
				}
			}
		}
		catch(IdUnusedException e)
		{
			return false;
		}
		catch(PermissionException e)
		{
			return false;
		}
		
		return true;
		
	} // allowUnjoinSite

	/**
	 * Check permissions for updating a realm.
	 * @param id The realm id.
	 * @return true if the user is allowed to update the realm, false if not.
	 */
	public boolean allowUpdateRealm(String id)
	{
		return unlockCheck(SECURE_UPDATE_REALM, realmReference(id));
	}

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
		// Note: this method is responsible for refreshing this realm's external provider information.
		// This is currently more easily done in the storage edit() implementations.
		
		if (id == null)
			throw new IdUnusedException("<null>");

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_REALM, realmReference(id));

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the realm with a lock from the info store
		RealmEdit realm = m_storage.edit(id);
		if (realm == null)
			throw new InUseException(id);

		((BaseRealmEdit) realm).setEvent(SECURE_UPDATE_REALM);

		// since we updated the realm with the latest provider info, update site security to match
		updateSiteSecurity(realm);

		return realm;
	}

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
		addLiveUpdateProperties((BaseRealm) realm);

		// complete the edit
		m_storage.commit(realm);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseRealmEdit) realm).getEvent(), realm.getReference(), true));

		// close the edit object
		((BaseRealmEdit) realm).closeEdit();

		// update the db with latest provider, and site security with the latest changes, using the updated realm
		BaseRealm updatedRealm = (BaseRealm) m_storage.get(realm.getId());
		updateSiteSecurity(updatedRealm);
	}

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
	}

	/**
	 * check permissions for addRealm().
	 * @param id The realm id.
	 * @return true if the current user is allowed to addRealm(id), false if not.
	 */
	public boolean allowAddRealm(String id)
	{
		return unlockCheck(SECURE_ADD_REALM, realmReference(id));
	}

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
	}

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
	}

	/**
	 * check permissions for removeRealm().
	 * @param id The realm id.
	 * @return true if the realm is allowed to removeRealm(id), false if not.
	 */
	public boolean allowRemoveRealm(String id)
	{
		return unlockCheck(SECURE_REMOVE_REALM, realmReference(id));
	}

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

		// clear any site security based on this (if a site) realm
		removeSiteSecurity(realm);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRealm(String realmId) throws PermissionException
	{
		if (realmId == null) return;

		// check for existance
		if (!m_storage.check(realmId))
		{
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_REALM, realmReference(realmId));

		// ignore the cache - get the realm with a lock from the info store
		RealmEdit realm = m_storage.edit(realmId);
		if (realm == null) return;

		// complete the edit
		m_storage.remove(realm);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_REALM, realm.getReference(), true));

		// close the edit object
		((BaseRealmEdit) realm).closeEdit();

		// clear any site security based on this (if a site) realm
		removeSiteSecurity(realm);
	}

	/**
	 * Access the internal reference which can be used to access the resource from within the system.
	 * @param id The realm id string.
	 * @return The the internal reference which can be used to access the resource from within the system.
	 */
	public String realmReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean unlock(String user, String lock, String realmId)
	{
		return m_storage.unlock(user, lock, realmId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean unlock(String user, String lock, Collection realms)
	{
		return m_storage.unlock(user, lock, realms);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getUsers(String lock, Collection realms)
	{
		return m_storage.getUsers(lock, realms);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getLocks(String role, Collection realms)
	{
		return m_storage.getLocks(role, realms);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set unlockRealms(String userId, String lock)
	{
		return m_storage.unlockRealms(userId, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public void refreshUser(String userId)
	{
		if ((m_provider == null) || (userId == null)) return;

		Map providerGrants = m_provider.getRealmRolesForUser(userId);
		m_storage.refreshUser(userId, providerGrants);
		
		// update site security for this user - get the user's realms for the three site locks
		Set updRealms = unlockRealms(userId, SiteService.SECURE_UPDATE_SITE);
		Set unpRealms = unlockRealms(userId, SiteService.SITE_VISIT_UNPUBLISHED);
		Set visitRealms = unlockRealms(userId, SiteService.SITE_VISIT);

		// convert from realm ids (potential site references) to site ids for those that are site,
		// skipping special and user sites other than our user's
		Set updSites = new HashSet();
		for (Iterator i = updRealms.iterator(); i.hasNext();)
		{
			String realmId = (String) i.next();
			Reference ref = new Reference(realmId);
			if (	(SiteService.SERVICE_NAME.equals(ref.getType()))
				&&	!SiteService.isSpecialSite(ref.getId())
				&&	(!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				updSites.add(ref.getId());
			}
		}

		Set unpSites = new HashSet();
		for (Iterator i = unpRealms.iterator(); i.hasNext();)
		{
			String realmId = (String) i.next();
			Reference ref = new Reference(realmId);
			if (	(SiteService.SERVICE_NAME.equals(ref.getType()))
				&&	!SiteService.isSpecialSite(ref.getId())
				&&	(!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				unpSites.add(ref.getId());
			}
		}

		Set visitSites = new HashSet();
		for (Iterator i = visitRealms.iterator(); i.hasNext();)
		{
			String realmId = (String) i.next();
			Reference ref = new Reference(realmId);
			if (	(SiteService.SERVICE_NAME.equals(ref.getType()))
				&&	!SiteService.isSpecialSite(ref.getId())
				&&	(!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				visitSites.add(ref.getId());
			}
		}
		
		SiteService.setUserSecurity(userId, updSites, unpSites, visitSites);
	}

	/**
	 * Update the site security based on the values in the realm, if it is a site realm.
	 * @param realm The realm.
	 */
	protected void updateSiteSecurity(Realm realm)
	{
		// Special code for the site service
		Reference ref = new Reference(realm.getId());
		if (SiteService.SERVICE_NAME.equals(ref.getType()))
		{
			// collect the users
			Set updUsers = realm.getUsersWithLock(SiteService.SECURE_UPDATE_SITE);
			Set unpUsers = realm.getUsersWithLock(SiteService.SITE_VISIT_UNPUBLISHED);
			Set visitUsers = realm.getUsersWithLock(SiteService.SITE_VISIT);
			
			SiteService.setSiteSecurity(ref.getId(), updUsers, unpUsers, visitUsers);
		}
	}

	/**
	 * Update the site security when a realm is deleted, if it is a site realm.
	 * @param realm The realm.
	 */
	protected void removeSiteSecurity(Realm realm)
	{
		// Special code for the site service
		Reference ref = new Reference(realm.getId());
		if (ref.getType().equals(SiteService.SERVICE_NAME))
		{
			// no realm, no users
			Set empty = new HashSet();
			
			SiteService.setSiteSecurity(ref.getId(), empty, empty, empty);
		}
	}

	/*******************************************************************************
	 * Realm implementation
	*******************************************************************************/

	/**
	 * <p>BaseRealm is an implementation of the Realm object.</p>
	 */
	public class BaseRealm implements Realm
	{
		/** The internal 'db' key. */
		protected Integer m_key = null;

		/** The realm id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** Map of userId to Grant */
		protected Map m_userGrants = null;

		/** Map of Role id to a Role defined in this Realm. */
		protected Map m_roles = null;

		/** The external realm id, or null if not defined. */
		protected String m_providerRealmId = null;

		/** The role to use for maintain users. */
		protected String m_maintainRole = null;

		/** The created user id. */
		protected String m_createdUserId = null;

		/** The last modified user id. */
		protected String m_lastModifiedUserId = null;

		/** The time created. */
		protected Time m_createdTime = null;

		/** The time last modified. */
		protected Time m_lastModifiedTime = null;

		/** Set while the realm is not fully loaded from the storage. */
		protected boolean m_lazy = false;

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

			m_userGrants = new HashMap();
			m_roles = new HashMap();

			// if the id is not null (a new realm, rather than a reconstruction)
			// add the automatic (live) properties
			if (m_id != null)
				addLiveProperties(this);
		}

		/**
		 * Construct from another Realm object.
		 * @param realm The realm object to use for values.
		 */
		public BaseRealm(Realm realm)
		{
			setAll(realm);
		}

		/**
		 * Construct from information in XML.
		 * @param el The XML DOM Element definining the realm.
		 */
		public BaseRealm(Element el)
		{
			m_userGrants = new HashMap();
			m_roles = new HashMap();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = StringUtil.trimToNull(el.getAttribute("id"));
			m_providerRealmId = StringUtil.trimToNull(el.getAttribute("provider-id"));
			m_maintainRole = StringUtil.trimToNull(el.getAttribute("maintain-role"));

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

			// process the children (properties, grants, abilities, roles)
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
				}

				// look for a role
				else if (element.getTagName().equals("role"))
				{
					BaseRoleEdit role = new BaseRoleEdit(element, this);
					m_roles.put(role.getId(), role);
				}

				// process a grant
				else if (element.getTagName().equals("grant"))
				{
					String userId = StringUtil.trimToNullLower(element.getAttribute("user"));
					String roleId = StringUtil.trimToNull(element.getAttribute("role"));
					String active = StringUtil.trimToNull(element.getAttribute("active"));
					String provided = StringUtil.trimToNull(element.getAttribute("provided"));

					// record this user - role grant - just use the first one
					BaseRoleEdit role = (BaseRoleEdit) m_roles.get(roleId);
					if (role != null)
					{
						// if already granted, update to point to the role with the most permissions
						MyGrant grant = (MyGrant) m_userGrants.get(userId);
						if (grant != null)
						{
							if (role.m_locks.size() > ((BaseRoleEdit) grant.role).m_locks.size())
							{
								m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + grant.role.getId() + " keeping: " + roleId);
								grant.role = role;
							}
							else
							{
								m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + roleId + " keeping: " + grant.role.getId());
							}
						}
						else
						{
							grant = new MyGrant(role, Boolean.valueOf(active).booleanValue(), Boolean.valueOf(provided).booleanValue(), userId);
							m_userGrants.put(userId, grant);
						}
					}
					else
					{
						m_logger.warn(this + "(el): role null: " + roleId);
					}
				}

				// look for user - [ Role | lock ] ability (the old way, pre 1.23)
				else if (element.getTagName().equals("ability"))
				{
					String userId = StringUtil.trimToNullLower(element.getAttribute("user"));
					String roleId = StringUtil.trimToNull(element.getAttribute("role"));
					String lock = StringUtil.trimToNull(element.getAttribute("lock"));
					String anon = StringUtil.trimToNull(element.getAttribute("anon"));
					String auth = StringUtil.trimToNull(element.getAttribute("auth"));

					// old way anon was stored
					// add the lock to the anon role definition
					if (anon != null)
					{
						if (roleId != null)
						{
							// the old pubview was done this way, we handle it so no need for warning
							if (!("pubview".equals(roleId)))
							{
								m_logger.warn(this + "(el) role for anon: " + m_id + " " + roleId);
							}
						}

						if (lock != null)
						{
							BaseRoleEdit role = (BaseRoleEdit) m_roles.get(ANON_ROLE);
							if (role == null)
							{
								role = new BaseRoleEdit(ANON_ROLE);
								m_roles.put(ANON_ROLE, role);
							}
							role.add(lock);
						}
					}

					// old way auth was stored
					// add the lock to the auth role definition
					else if (auth != null)
					{
						if (roleId != null)
						{
							// the old pubview was done this way, we handle it so no need for warning
							if (!("pubview".equals(roleId)))
							{
								m_logger.warn(this + "(el) role for auth: " + m_id + " " + roleId);
							}
						}

						if (lock != null)
						{
							BaseRoleEdit role = (BaseRoleEdit) m_roles.get(AUTH_ROLE);
							if (role == null)
							{
								role = new BaseRoleEdit(AUTH_ROLE);
								m_roles.put(AUTH_ROLE, role);
							}
							role.add(lock);
						}
					}

					else if (userId != null)
					{
						BaseRoleEdit role = (BaseRoleEdit) m_roles.get(roleId);
						if (role != null)
						{
							// if already granted, update to point to the role with the most permissions
							MyGrant grant = (MyGrant) m_userGrants.get(userId);
							if (grant != null)
							{
								if (role.m_locks.size() > ((BaseRoleEdit) grant.role).m_locks.size())
								{
									m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + grant.role.getId() + " keeping: " + roleId);
									grant.role = role;
								}
								else
								{
									m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + roleId + " keeping: " + grant.role.getId());
								}
							}
							else
							{
								grant = new MyGrant(role, true, false, userId);
								m_userGrants.put(userId, grant);
							}
						}
						else
						{
							m_logger.warn(this + "(el): role null: " + roleId);
						}
					}
				}
			}

			// pull out some properties into fields to convert old (pre 1.23) versions
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
			
			// make sure we have our times
			if ((m_createdTime == null) && (m_lastModifiedTime != null))
			{
				m_createdTime = (Time) m_lastModifiedTime.clone();
			}

			if (m_createdTime == null)
			{
				m_createdTime = TimeService.newTime();
			}

			if (m_lastModifiedTime == null)
			{
				m_lastModifiedTime = (Time) m_createdTime.clone();
			}
			
			// and our users
			if ((m_createdUserId == null) && (m_lastModifiedUserId != null))
			{
				m_createdUserId = m_lastModifiedUserId;
			}

			if (m_createdUserId == null)
			{
				m_createdUserId = "admin";
			}

			if (m_lastModifiedUserId == null)
			{
				m_lastModifiedUserId = m_createdUserId;
			}

			// recognize old (ContentHosting) pubview realms where anon/auth were granted "pubview" role
			// roles can not be nested anymore - remove the pubview role and put the one "content.read" lock into .anon
			if (m_roles.get("pubview") != null)
			{
				m_roles.remove("pubview");

				BaseRoleEdit role = (BaseRoleEdit) m_roles.get(ANON_ROLE);
				if (role == null)
				{
					role = new BaseRoleEdit(ANON_ROLE);
					m_roles.put(ANON_ROLE, role);
				}
				role.add("content.read");
			}
		}

		/**
		 * {@inheritDoc}
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
				return "My Workspace Realm Template";
			}

			else if (getId().startsWith("!user.template"))
			{
				return "User Realm Template";
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
		}

		/**
		 * Take all values from this object.
		 * @param realm The realm object to take values from.
		 */
		protected void setAll(Realm realm)
		{
			if (((BaseRealm) realm).m_lazy) m_storage.completeGet(((BaseRealm) realm));

			m_key = ((BaseRealm) realm).m_key;
			m_id = ((BaseRealm) realm).m_id;
			m_providerRealmId = ((BaseRealm) realm).m_providerRealmId;
			m_maintainRole = ((BaseRealm) realm).m_maintainRole;

			m_createdUserId = ((BaseRealm) realm).m_createdUserId;
			m_lastModifiedUserId = ((BaseRealm) realm).m_lastModifiedUserId;
			if (((BaseRealm) realm).m_createdTime != null) m_createdTime = (Time) ((BaseRealm) realm).m_createdTime.clone();
			if (((BaseRealm) realm).m_lastModifiedTime != null) m_lastModifiedTime = (Time) ((BaseRealm) realm).m_lastModifiedTime.clone();

			// make a deep copy of the roles as new Role objects
			m_roles = new HashMap();
			for (Iterator it = ((BaseRealm) realm).m_roles.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseRoleEdit role = (BaseRoleEdit) entry.getValue();
				String id = (String) entry.getKey();

				m_roles.put(id, new BaseRoleEdit(id, role));
			}

			// make a deep copy (w/ new Grant objects pointing to my own roles) of the user - role grants
			m_userGrants = new HashMap();
			for (Iterator it = ((BaseRealm) realm).m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				MyGrant grant = (MyGrant) entry.getValue();
				String id = (String) entry.getKey();

				m_userGrants.put(id, new MyGrant((Role) m_roles.get(grant.role.getId()), grant.active, grant.provided, grant.userId));
			}

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(realm.getProperties());
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) realm.getProperties()).isLazy());

			m_lazy = false;
		}

		/**
		 * {@inheritDoc}
		 */
		public Element toXml(Document doc, Stack stack)
		{
			if (m_lazy) m_storage.completeGet(this);

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

			realm.setAttribute("created-id", m_createdUserId);
			realm.setAttribute("modified-id", m_lastModifiedUserId);
			realm.setAttribute("created-time", m_createdTime.toString());
			realm.setAttribute("modified-time", m_lastModifiedTime.toString());

			// properties
			getProperties().toXml(doc, stack);

			// roles (write before grants!)
			for (Iterator i = m_roles.values().iterator(); i.hasNext();)
			{
				BaseRoleEdit role = (BaseRoleEdit) i.next();
				role.toXml(doc, stack);
			}

			// user - role grants
			for (Iterator i = m_userGrants.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				MyGrant grant = (MyGrant) entry.getValue();
				String user = (String) entry.getKey();
				
				Element element = doc.createElement("grant");
				realm.appendChild(element);
				element.setAttribute("user", user);
				element.setAttribute("role", grant.role.getId());
				element.setAttribute("active", Boolean.valueOf(grant.active).toString());
				element.setAttribute("provided", Boolean.valueOf(grant.provided).toString());
			}

			stack.pop();

			return realm;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getId()
		{
			if (m_id == null)
				return "";
			return m_id;
		}

		/**
		 * {@inheritDoc}
		 */
		public Integer getKey()
		{
			return m_key;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getReference()
		{
			return realmReference(m_id);
		}

		/**
		 * {@inheritDoc}
		 */
		public ResourceProperties getProperties()
		{
			if (m_lazy) m_storage.completeGet(this);

			return m_properties;
		}

		/**
		 * {@inheritDoc}
		 */
		public User getCreatedBy()
		{
			try
			{
				return UserDirectoryService.getUser(m_createdUserId);
			}
			catch (Exception e)
			{
				return UserDirectoryService.getAnonymousUser();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public User getModifiedBy()
		{
			try
			{
				return UserDirectoryService.getUser(m_lastModifiedUserId);
			}
			catch (Exception e)
			{
				return UserDirectoryService.getAnonymousUser();
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
		 * {@inheritDoc}
		 */
		public boolean unlock(String user, String lock)
		{
			if (m_lazy) m_storage.completeGet(this);

			// consider a role granted
			MyGrant grant = (MyGrant) m_userGrants.get(user);
			if ((grant != null) && (grant.active))
			{
				if (grant.role.contains(lock))
					return true;
			}

			// consider auth role			
			if (!UserDirectoryService.getAnonymousUser().getId().equals(user))
			{
				Role auth = (Role) m_roles.get(AUTH_ROLE);
				if (auth != null)
				{
					if (auth.contains(lock))
						return true;
				}
			}

			// consider anon role
			Role anon = (Role) m_roles.get(ANON_ROLE);
			if (anon != null)
			{
				if (anon.contains(lock))
					return true;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasRole(String user, String role)
		{
			if (m_lazy) m_storage.completeGet(this);

			MyGrant grant = (MyGrant) m_userGrants.get(user);
			if ((grant != null) && (grant.active) && (grant.role.getId().equals(role))) return true;

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsers()
		{
			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				Grant grant = (Grant) entry.getValue();
				if (grant.isActive())
				{
					rv.add(user);
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getGrants()
		{
			// Note: this is the only way to see non-active grants

			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Grant grant = (Grant) entry.getValue();
				rv.add(grant);
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsersWithLock(String lock)
		{
			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				MyGrant grant = (MyGrant) entry.getValue();
				if (grant.active && grant.role.contains(lock))
				{
					rv.add(user);
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsersWithRole(String role)
		{
			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				MyGrant grant = (MyGrant) entry.getValue();
				if (grant.active && grant.role.getId().equals(role))
				{
					rv.add(user);
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Role getUserRole(String user)
		{
			if (m_lazy) m_storage.completeGet(this);

			MyGrant grant = (MyGrant) m_userGrants.get(user);
			if ((grant != null) && (grant.active)) return grant.role;
			
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Grant getUserGrant(String user)
		{
			if (m_lazy) m_storage.completeGet(this);

			MyGrant grant = (MyGrant) m_userGrants.get(user);
			return grant;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getRoles()
		{
			if (m_lazy) m_storage.completeGet(this);

			return new HashSet(m_roles.values());
		}

		/**
		 * {@inheritDoc}
		 */
		public Role getRole(String id)
		{
			if (m_lazy) m_storage.completeGet(this);

			return (Role) m_roles.get(id);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getProviderRealmId()
		{
			return m_providerRealmId;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isEmpty()
		{
			if (m_lazy) m_storage.completeGet(this);

			// no roles, no grants to users, nothing in anon or auth
			if (m_roles.isEmpty() && m_userGrants.isEmpty())
			{
				return true;
			}

			return false;
		}

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
		}

		/**
		 * Make a hash code that reflects the equals() logic as well.
		 * We want two objects, even if different instances, if they have the same id to hash the same.
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}

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

			// sort based on id
			int compare = getId().compareTo(((Realm) obj).getId());

			return compare;
		}

		// Note: these next two edit methods are implemented here to support the XmlFileRealmService

		/**
		 * Add this role to this user in this Realm.
		 * @param userId The user.
		 * @param role The role name.
		 * @param active The active flag for the role grant.
		 * @param provided If true, from an external provider.
		 */
		public void addUserRole(String user, String roleId, boolean active, boolean provided)
		{
			Role role = (Role) m_roles.get(roleId);
			if (role == null)
			{
				m_logger.warn(this + ".addUserRole: role undefined: " + roleId);
				return;
			}

			MyGrant grant = (MyGrant) m_userGrants.get(user);
			if (grant == null)
			{
				grant = new MyGrant(role, active, provided, user);
				m_userGrants.put(user, grant);
			}
			else
			{
				grant.role = role;
				grant.active = active;
				grant.provided = provided;
			}
		}

		/**
		 * Remove all Roles for this user from this Realm.
		 * @param userId The user.
		 */
		public void removeUser(String user)
		{
			if (m_lazy) m_storage.completeGet(this);

			m_userGrants.remove(user);
		}
	}

	/*******************************************************************************
	 * RealmEdit implementation
	*******************************************************************************/

	/**
	 * <p>BaseRealmEdit is an implementation of the RealmEdit object.</p>
	 */
	public class BaseRealmEdit extends BaseRealm implements RealmEdit, SessionBindingListener
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
		}

		/**
		 * Construct from information in XML.
		 * @param el The XML DOM Element definining the realm.
		 */
		public BaseRealmEdit(Element el)
		{
			super(el);
		}

		/**
		 * Construct from another Realm object.
		 * @param realm The realm object to use for values.
		 */
		public BaseRealmEdit(Realm realm)
		{
			super(realm);
		}

		/**
		 * (Re)Construct from parts.
		 * @param dbid The database id.
		 * @param id The realm id.
		 * @param providerId The provider id.
		 * @param maintainRole The maintain role id.
		 * @param createdBy The user created by id.
		 * @param createdOn The time created.
		 * @param modifiedBy The user modified by id.
		 * @param modifiedOn The time modified.
		 */
		public BaseRealmEdit(Integer dbid, String id, String providerId, String maintainRole,
							String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
		{
			super((String) null);

			m_key = dbid;
			m_id = id;
			m_providerRealmId = StringUtil.trimToNull(providerId);
			m_maintainRole = StringUtil.trimToNull(maintainRole);

			m_createdUserId = createdBy;
			m_lastModifiedUserId = modifiedBy;
			m_createdTime = createdOn;
			m_lastModifiedTime = modifiedOn;

			// setup for properties, but mark them lazy since we have not yet established them from data
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_lazy = true;
		}

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
		}

		/**
		 * Take all values from this object.
		 * @param realm The realm object to take values from.
		 */
		protected void set(Realm realm)
		{
			setAll(realm);
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeUsers()
		{
			if (m_lazy) m_storage.completeGet(this);

			m_userGrants.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public RoleEdit addRole(String id) throws IdUsedException
		{
			if (m_lazy) m_storage.completeGet(this);

			RoleEdit role = (RoleEdit) m_roles.get(id);
			if (role != null)
				throw new IdUsedException(id);

			role = new BaseRoleEdit(id);
			m_roles.put(role.getId(), role);

			return role;
		}

		/**
		 * {@inheritDoc}
		 */
		public RoleEdit addRole(String id, Role other) throws IdUsedException
		{
			if (m_lazy) m_storage.completeGet(this);

			RoleEdit role = (RoleEdit) m_roles.get(id);
			if (role != null)
				throw new IdUsedException(id);

			role = new BaseRoleEdit(id, other);
			m_roles.put(role.getId(), role);

			return role;
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeRole(String roleId)
		{
			if (m_lazy) m_storage.completeGet(this);

			Role r = (Role) m_roles.get(roleId);
			if (r != null)
			{
				m_roles.remove(roleId);

				// remove the role from any appearance in m_userGrants
				for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
				{
					Map.Entry entry = (Map.Entry) it.next();
					MyGrant grant = (MyGrant) entry.getValue();
					String id = (String) entry.getKey();

					if (grant.role.equals(r))
					{
						it.remove();
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void clearRoles()
		{
			if (m_lazy) m_storage.completeGet(this);

			// clear roles and grants (since grants grant roles)
			m_roles.clear();
			m_userGrants.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public RoleEdit getRoleEdit(String id)
		{
			if (m_lazy) m_storage.completeGet(this);

			BaseRoleEdit edit = (BaseRoleEdit) m_roles.get(id);
			if (edit != null)
				edit.activate();

			return edit;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setProviderRealmId(String id)
		{
			m_providerRealmId = StringUtil.trimToNull(id);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMaintainRole(String role)
		{
			m_maintainRole = StringUtil.trimToNull(role);
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
		 * {@inheritDoc}
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			if (m_lazy) m_storage.completeGet(this);

			return m_properties;
		}

		/**
		 * Enable editing.
		 */
		protected void activate()
		{
			m_active = true;
		}

		/**
		 * Check to see if the edit is still active, or has already been closed.
		 * @return true if the edit is active, false if it's been closed.
		 */
		public boolean isActiveEdit()
		{
			return m_active;
		}

		/**
		 * Close the edit object - it cannot be used after this.
		 */
		protected void closeEdit()
		{
			m_active = false;
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
		}
	}

	/*******************************************************************************
	 * RoleEdit implementation
	*******************************************************************************/

	/**
	 * <p>BaseRoleEdit is an implementation of the CHEF RoleEdit object.</p>
	 */
	public class BaseRoleEdit implements RoleEdit
	{
		/** The role id. */
		protected String m_id = null;

		/** The locks that make up this. */
		protected Set m_locks = null;

		/** The role description. */
		protected String m_description = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		 * Construct.
		 * @param id The role id.
		 */
		public BaseRoleEdit(String id)
		{
			m_id = id;
			m_locks = new HashSet();
		}

		/**
		 * Construct as a copy
		 * @param id The role id.
		 * @param other The role to copy.
		 */
		public BaseRoleEdit(String id, Role other)
		{
			m_id = id;
			m_description = ((BaseRoleEdit) other).m_description;
			m_locks = new HashSet();
			m_locks.addAll(((BaseRoleEdit) other).m_locks);
		}

		/**
		 * Construct from information in XML.
		 * @param el The XML DOM Element definining the role.
		 */
		public BaseRoleEdit(Element el, BaseRealm realm)
		{
			m_locks = new HashSet();
			m_id = StringUtil.trimToNull(el.getAttribute("id"));

			m_description = StringUtil.trimToNull(el.getAttribute("description"));
			if (m_description == null)
			{
				m_description = StringUtil.trimToNull(Xml.decodeAttribute(el, "description-enc"));
			}

			// the children (abilities)
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
					String roleId = StringUtil.trimToNull(element.getAttribute("role"));
					String lock = StringUtil.trimToNull(element.getAttribute("lock"));

					if (roleId != null)
					{
						m_logger.warn(this + "(el): nested role: " + m_id + " " + roleId);
					}

					m_locks.add(lock);
				}
			}
		}

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

			// encode the description
			if (m_description != null)
				Xml.encodeAttribute(role, "description-enc", m_description);

			// locks
			for (Iterator a = m_locks.iterator(); a.hasNext();)
			{
				String lock = (String) a.next();

				Element element = doc.createElement("ability");
				role.appendChild(element);
				element.setAttribute("lock", lock);
			}

			stack.pop();

			return role;
		}

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
		}

		/**
		 * Close the edit object - it cannot be used after this.
		 */
		protected void closeEdit()
		{
			m_active = false;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getDescription()
		{
			return m_description;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean contains(String lock)
		{
			return m_locks.contains(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getLocks()
		{
			Set rv = new HashSet();
			rv.addAll(m_locks);
			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDescription(String description)
		{
			m_description = StringUtil.trimToNull(description);
		}

		/**
		 * {@inheritDoc}
		 */
		public void add(String lock)
		{
			m_locks.add(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public void add(Collection locks)
		{
			m_locks.addAll(locks);
		}

		/**
		 * {@inheritDoc}
		 */
		public void remove(String lock)
		{
			m_locks.remove(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public void remove(Collection locks)
		{
			m_locks.removeAll(locks);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isEmpty()
		{
			return m_locks.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public void clear()
		{
			m_locks.clear();
		}

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
		}

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
		}

		/**
		 * Make a hash code that reflects the equals() logic as well.
		 * We want two objects, even if different instances, if they have the same id to hash the same.
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}
	}

	/*******************************************************************************
	 * Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		 * Open.
		 */
		void open();

		/**
		 * Close.
		 */
		void close();

		/**
		 * Check if a realm by this id exists.
		 * @param id The realm id.
		 * @return true if a realm by this id exists, false if not.
		 */
		boolean check(String id);

		/**
		 * Get the realm with this id, or null if not found.
		 * @param id The realm id.
		 * @return The realm with this id, or null if not found.
		 */
		Realm get(String id);

		/**
		 * Add a new realm with this id.
		 * @param id The realm id.
		 * @return The locked Realm object with this id, or null if the id is in use.
		 */
		RealmEdit put(String id);

		/**
		 * Get a lock on the realm with this id, or null if a lock cannot be gotten.
		 * @param id The realm id.
		 * @return The locked Realm with this id, or null if this records cannot be locked.
		 */
		RealmEdit edit(String id);

		/**
		 * Commit the changes and release the lock.
		 * @param realm The realm to commit.
		 */
		void commit(RealmEdit realm);

		/**
		 * Cancel the changes and release the lock.
		 * @param realm The realm to commit.
		 */
		void cancel(RealmEdit realm);

		/**
		 * Remove this realm.
		 * @param realm The realm to remove.
		 */
		void remove(RealmEdit realm);

		/**
		 * Access a list of Realm objets that meet specified criteria, naturally sorted.
		 * @param criteria Selection criteria: realms returned will match this string somewhere in their
		 * 			id, or provider realm id.
		 * @param page The PagePosition subset of items to return.
		 * @return The List (Realm) of Rrealm objets that meet specified criteria.
		 */
		List getRealms(String criteria, PagingPosition page);

		/**
		 * Count the Realm objets that meet specified criteria.
		 * @param criteria Selection criteria: realms returned will match this string somewhere in their
		 * 			id, or provider realm id.
		 * @return The count of Realm objets that meet specified criteria.
		 */
		int countRealms(String criteria);

		/**
		 * Complete the read process once the basic realm info has been read
		 * @param realm The real to complete
		 */
		void completeGet(BaseRealm realm);

		/**
		 * Test if this user can unlock the lock in the named realm.
		 * @param userId The user id.
		 * @param lock The lock to open.
		 * @param realmId The realm id to consult, if it exists.
		 * @return true if this user can unlock the lock in this realm, false if not.
		 */
		boolean unlock(String userId, String lock, String realmId);

		/**
		 * Test if this user can unlock the lock in the named realms.
		 * @param userId The user id.
		 * @param lock The lock to open.
		 * @param roleRealms A collection of realm ids to consult.
		 * @return true if this user can unlock the lock in these realms, false if not.
		 */
		boolean unlock(String userId, String lock, Collection realms);

		/**
		 * Get the set of user ids which are granted roles which can unlock this lock in this realm.
		 * @param lock The lock.
		 * @param roleRealms A collection of realm ids to consult.
		 * @return the Set (String) of user ids which are granted roles which can unlock this lock in this realm.
		 */
		Set getUsers(String lock, Collection realms);

		/**
		 * Get the set of locks that this role contains in these realms.
		 * @param role The role.
		 * @param realms A collection of realm ids to consult.
		 * @return the Set (String) of locks that this role contains in these realms.
		 */
		Set getLocks(String role, Collection realms);

		/**
		 * Get the set of realm ids in which this user is granted roles which can unlock this lock.
		 * @param userId  The user id.
		 * @param lock The lock.
		 * @return the Set (String) of realm ids in which this user is granted roles which can unlock this lock.
		 */
		Set unlockRealms(String userId, String lock);

		/**
		 * Refresh this user's roles in any realm with an external provider in the map to the role in the map.
		 * @param userId The user id
		 * @param providerGrants The Map of external realm id -> role id.
		 */
		void refreshUser(String userId, Map providerGrants);

		/**
		 * Refresh the external user - role grants for this realm
		 * @param realm The realm to refresh.
		 */		
		void refreshRealm(BaseRealm realm);
	}

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
	 * Grant - modeling a role grant to a user in a realm
	*******************************************************************************/

	/**
	 * <p>Grant models a role grant to a user in a realm, with the role, and flags for active and provided.</p>
	 */
	public class MyGrant implements Grant
	{
		public Role role = null;
		public boolean provided = false;
		public boolean active = true;
		public String userId = null;
		
		public MyGrant(Role role, boolean active, boolean provided, String userId)
		{
			this.role = role;
			this.active = active;
			this.provided = provided;
			this.userId = userId;
		}

		/**
		 * {@inheritDoc}
		 */
		public Role getRole()
		{
			return role;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getUserId()
		{
			return userId;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isProvided()
		{
			return provided;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isActive()
		{
			return active;
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Grant))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// compare by comparing the user id
			int compare = getUserId().compareTo(((Grant) obj).getUserId());

			return compare;
		}
	}
}



