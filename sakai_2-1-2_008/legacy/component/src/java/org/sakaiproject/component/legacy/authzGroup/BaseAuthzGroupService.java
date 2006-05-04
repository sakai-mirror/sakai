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
package org.sakaiproject.component.legacy.authzGroup;

// imports
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.GroupProvider;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * BaseAuthzGroupService is a Sakai azGroup service implementation.
 * </p>
 * <p>
 * To support the public view feature, an AuthzGroup named TEMPLATE_PUBVIEW must exist, with a role named ROLE_PUBVIEW - all the abilities in this role become the public view abilities for any resource.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public abstract class BaseAuthzGroupService implements AuthzGroupService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A provider of additional Abilities for a userId. */
	protected GroupProvider m_provider = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Abstractions, etc.
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Construct storage for this service.
	 */
	protected abstract Storage newStorage();

	/**
	 * Access the partial URL that forms the root of resource URLs.
	 * 
	 * @param relative
	 *        if true, form within the access path only (i.e. starting with /content)
	 * @return the partial URL that forms the root of resource URLs.
	 */
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : m_serverConfigurationService.getAccessUrl()) + m_relativeAccessPoint;
	}

	/**
	 * Access the azGroup id extracted from an AuthzGroup reference.
	 * 
	 * @param ref
	 *        The azGroup reference string.
	 * @return The the azGroup id extracted from an AuthzGroup reference.
	 */
	protected String authzGroupId(String ref)
	{
		String start = getAccessPoint(true) + Entity.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1) return ref;
		String id = ref.substring(i + start.length());
		return id;
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param resource
	 *        The resource reference string, or null if no resource is involved.
	 * @return true if allowd, false if not
	 */
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param resource
	 *        The resource reference string, or null if no resource is involved.
	 * @exception PermissionException
	 *            Thrown if the azGroup does not have access
	 */
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(lock, resource);
		}
	}

	/**
	 * Create the live properties for the azGroup.
	 */
	protected void addLiveProperties(BaseAuthzGroup azGroup)
	{
		String current = SessionManager.getCurrentSessionUserId();

		azGroup.m_createdUserId = current;
		azGroup.m_lastModifiedUserId = current;

		Time now = TimeService.newTime();
		azGroup.m_createdTime = now;
		azGroup.m_lastModifiedTime = (Time) now.clone();
	}

	/**
	 * Update the live properties for an AuthzGroup for when modified.
	 */
	protected void addLiveUpdateProperties(BaseAuthzGroup azGroup)
	{
		String current = SessionManager.getCurrentSessionUserId();

		azGroup.m_lastModifiedUserId = current;
		azGroup.m_lastModifiedTime = TimeService.newTime();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
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

	/**
	 * Configuration: set the azGroup provider helper service.
	 * 
	 * @param provider
	 *        the azGroup provider helper service.
	 */
	public void setProvider(GroupProvider provider)
	{
		m_provider = provider;
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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

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

			// register as an entity producer
			m_entityManager.registerEntityProducer(this, REFERENCE_ROOT);

			// register functions
			FunctionManager.registerFunction(SECURE_ADD_AUTHZ_GROUP);
			FunctionManager.registerFunction(SECURE_REMOVE_AUTHZ_GROUP);
			FunctionManager.registerFunction(SECURE_UPDATE_AUTHZ_GROUP);
			FunctionManager.registerFunction(SECURE_UPDATE_OWN_AUTHZ_GROUP);

			// if no provider was set, see if we can find one
			if (m_provider == null)
			{
				m_provider = (GroupProvider) ComponentManager.get(GroupProvider.class.getName());
			}

			m_logger.info(this + ".init(): provider: " + ((m_provider == null) ? "none" : m_provider.getClass().getName()));
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(); ", t);
		}
	}

	/**
	 * Returns to uninitialized state.
	 */
	public void destroy()
	{
		m_storage.close();
		m_storage = null;

		m_logger.info(this + ".destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * AuthzGroupService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public List getAuthzGroups(String criteria, PagingPosition page)
	{
		return m_storage.getAuthzGroups(criteria, page);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countAuthzGroups(String criteria)
	{
		return m_storage.countAuthzGroups(criteria);
	}

	/**
	 * {@inheritDoc}
	 */
	public AuthzGroup getAuthzGroup(String id) throws IdUnusedException
	{
		// Note: since this is a "read" operations, we do NOT refresh (i.e. write) the provider info.
		if (id == null) throw new IdUnusedException("<null>");

		AuthzGroup azGroup = m_storage.get(id);

		// if not found
		if (azGroup == null)
		{
			throw new IdUnusedException(id);
		}

		return azGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public void joinGroup(String authzGroupId, String roleId) throws IdUnusedException, PermissionException
	{
		String user = SessionManager.getCurrentSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);

		// get the AuthzGroup
		AuthzGroup azGroup = m_storage.get(authzGroupId);
		if (azGroup == null)
		{
			throw new IdUnusedException(authzGroupId);
		}

		// check the role
		Role role = azGroup.getRole(roleId);
		if (role == null)
		{
			throw new IdUnusedException(roleId);
		}

		((BaseAuthzGroup) azGroup).setEvent(SECURE_UPDATE_OWN_AUTHZ_GROUP);

		// see if already joined
		BaseMember grant = (BaseMember) azGroup.getMember(user);
		if (grant != null)
		{
			// if inactive, make it active
			if (!grant.active) grant.active = true;
		}

		// give the user this role
		else
		{
			azGroup.addMember(user, roleId, true, false);
		}

		// and save
		completeSave(azGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unjoinGroup(String authzGroupId) throws IdUnusedException, PermissionException
	{
		String user = SessionManager.getCurrentSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);

		// get the AuthzGroup
		AuthzGroup azGroup = m_storage.get(authzGroupId);
		if (azGroup == null)
		{
			throw new IdUnusedException(authzGroupId);
		}

		// if not joined (no grant), we are done
		BaseMember grant = (BaseMember) azGroup.getMember(user);
		if (grant == null)
		{
			return;
		}

		// if the user currently is the only maintain role user, disallow the unjoin
		if (grant.getRole().getId().equals(azGroup.getMaintainRole()))
		{
			Set maintainers = azGroup.getUsersHasRole(azGroup.getMaintainRole());
			if (maintainers.size() <= 1)
			{
				throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);
			}
		}

		((BaseAuthzGroup) azGroup).setEvent(SECURE_UPDATE_OWN_AUTHZ_GROUP);

		// if the grant is provider, make it inactive so it doesn't revert to provider status
		if (grant.isProvided())
		{
			grant.active = false;
		}
		else
		{
			// remove the user completely
			((BaseAuthzGroup) azGroup).removeMember(user);
		}

		// and save
		completeSave(azGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowJoinGroup(String authzGroupId)
	{
		String user = SessionManager.getCurrentSessionUserId();
		if (user == null) return false;

		// check security (throws if not permitted)
		return unlockCheck(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowUnjoinGroup(String authzGroupId)
	{
		String user = SessionManager.getCurrentSessionUserId();
		if (user == null)
		{
			return false;
		}

		// check security (throws if not permitted)
		if (!unlockCheck(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupId)) return false;

		// get the azGroup
		AuthzGroup azGroup = m_storage.get(authzGroupId);
		if (azGroup == null)
		{
			return false;
		}

		// if not joined (no grant), unable to unjoin
		BaseMember grant = (BaseMember) azGroup.getMember(user);
		if (grant == null)
		{
			return false;
		}

		// if the grant is provider, unable to unjoin
		else if (grant.isProvided())
		{
			return false;
		}

		// if the user currently is the only maintain role user, disallow the unjoin
		if (grant.getRole().getId().equals(azGroup.getMaintainRole()))
		{
			Set maintainers = azGroup.getUsersHasRole(azGroup.getMaintainRole());
			if (maintainers.size() <= 1)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowUpdate(String id)
	{
		return unlockCheck(SECURE_UPDATE_AUTHZ_GROUP, authzGroupReference(id));
	}

	/**
	 * {@inheritDoc}
	 */
	public void save(AuthzGroup azGroup) throws IdUnusedException, PermissionException
	{
		if (azGroup.getId() == null) throw new IdUnusedException("<null>");
		
		Reference ref = m_entityManager.newReference(azGroup.getId());
		if (!SiteService.allowUpdateSiteMembership(ref.getId()))
		{
			// check security (throws if not permitted)
			unlock(SECURE_UPDATE_AUTHZ_GROUP, authzGroupReference(azGroup.getId()));
		}

		// make sure it's in storage
		if (!m_storage.check(azGroup.getId()))
		{
			// if this was new, create it in storage
			if (((BaseAuthzGroup) azGroup).m_isNew)
			{
				// reserve an AuthzGroup with this id from the info store - if it's in use, this will return null
				AuthzGroup newAzg = m_storage.put(azGroup.getId());
				if (newAzg == null)
				{
					m_logger.warn(this + ".saveUsingSecurity, storage.put for a new returns null");
				}
			}
			else
			{
				throw new IdUnusedException(azGroup.getId());
			}
		}

		// complete the save
		completeSave(azGroup);
	}

	/**
	 * Complete the saving of the group, once id and security checks have been cleared.
	 * 
	 * @param azGroup
	 */
	protected void completeSave(AuthzGroup azGroup)
	{
		// update the properties
		addLiveUpdateProperties((BaseAuthzGroup) azGroup);

		// complete the azGroup
		m_storage.save(azGroup);

		// track it
		String event = ((BaseAuthzGroup) azGroup).getEvent();
		if (event == null) event = SECURE_UPDATE_AUTHZ_GROUP;
		EventTrackingService.post(EventTrackingService.newEvent(event, azGroup.getReference(), true));

		// close the azGroup object
		((BaseAuthzGroup) azGroup).closeEdit();

		// update the db with latest provider, and site security with the latest changes, using the updated azGroup
		BaseAuthzGroup updatedRealm = (BaseAuthzGroup) m_storage.get(azGroup.getId());
		updateSiteSecurity(updatedRealm);

		// clear the event for next time
		((BaseAuthzGroup) azGroup).setEvent(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowAdd(String id)
	{
		return unlockCheck(SECURE_ADD_AUTHZ_GROUP, authzGroupReference(id));
	}

	/**
	 * {@inheritDoc}
	 */
	public AuthzGroup addAuthzGroup(String id) throws IdInvalidException, IdUsedException, PermissionException
	{
		// check security (throws if not permitted)
		unlock(SECURE_ADD_AUTHZ_GROUP, authzGroupReference(id));

		// reserve an AuthzGroup with this id from the info store - if it's in use, this will return null
		AuthzGroup azGroup = m_storage.put(id);
		if (azGroup == null)
		{
			throw new IdUsedException(id);
		}

		((BaseAuthzGroup) azGroup).setEvent(SECURE_ADD_AUTHZ_GROUP);

		// update the properties
		addLiveProperties((BaseAuthzGroup) azGroup);

		// save
		completeSave(azGroup);

		return azGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public AuthzGroup addAuthzGroup(String id, AuthzGroup other, String userId) throws IdInvalidException, IdUsedException,
			PermissionException
	{
		// make the new AuthzGroup
		AuthzGroup azGroup = addAuthzGroup(id);

		// move in the values from the old AuthzGroup (this includes the id, which we restore
		((BaseAuthzGroup) azGroup).set(other);
		((BaseAuthzGroup) azGroup).m_id = id;

		// give the user the "maintain" role
		String roleName = azGroup.getMaintainRole();
		if ((roleName != null) && (userId != null))
		{
			azGroup.addMember(userId, roleName, true, false);
		}

		// update the properties
		addLiveProperties((BaseAuthzGroup) azGroup);

		// save
		completeSave(azGroup);

		return azGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public AuthzGroup newAuthzGroup(String id, AuthzGroup other, String userId) throws IdUsedException
	{
		// make the new AuthzGroup
		BaseAuthzGroup azGroup = new BaseAuthzGroup(id);
		azGroup.m_isNew = true;

		// move in the values from the old AuthzGroup (this includes the id, which we restore)
		if (other != null)
		{
			azGroup.set(other);
			azGroup.m_id = id;
		}

		// give the user the "maintain" role
		String roleName = azGroup.getMaintainRole();
		if ((roleName != null) && (userId != null))
		{
			azGroup.addMember(userId, roleName, true, false);
		}

		return azGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowRemove(String id)
	{
		return unlockCheck(SECURE_REMOVE_AUTHZ_GROUP, authzGroupReference(id));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAuthzGroup(AuthzGroup azGroup) throws PermissionException
	{
		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_AUTHZ_GROUP, azGroup.getReference());

		// complete the azGroup
		m_storage.remove(azGroup);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_AUTHZ_GROUP, azGroup.getReference(), true));

		// close the azGroup object
		((BaseAuthzGroup) azGroup).closeEdit();

		// clear any site security based on this (if a site) azGroup
		removeSiteSecurity(azGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAuthzGroup(String azGroupId) throws PermissionException
	{
		if (azGroupId == null) return;

		// check for existance
		AuthzGroup azGroup = m_storage.get(azGroupId);
		if (azGroup == null)
		{
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_AUTHZ_GROUP, authzGroupReference(azGroupId));

		// complete the azGroup
		m_storage.remove(azGroup);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_AUTHZ_GROUP, azGroup.getReference(), true));

		// close the azGroup object
		((BaseAuthzGroup) azGroup).closeEdit();

		// clear any site security based on this (if a site) azGroup
		removeSiteSecurity(azGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	public String authzGroupReference(String id)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAllowed(String user, String function, String azGroupId)
	{
		return m_storage.isAllowed(user, function, azGroupId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAllowed(String user, String function, Collection azGroups)
	{
		return m_storage.isAllowed(user, function, azGroups);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getUsersIsAllowed(String function, Collection azGroups)
	{
		return m_storage.getUsersIsAllowed(function, azGroups);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getAllowedFunctions(String role, Collection azGroups)
	{
		return m_storage.getAllowedFunctions(role, azGroups);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getAuthzGroupsIsAllowed(String userId, String function, Collection azGroups)
	{
		return m_storage.getAuthzGroupsIsAllowed(userId, function, azGroups);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserRole(String userId, String azGroupId)
	{
		return m_storage.getUserRole(userId, azGroupId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getUsersRole(Collection userIds, String azGroupId)
	{
		return m_storage.getUsersRole(userIds, azGroupId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void refreshUser(String userId)
	{
		if ((m_provider == null) || (userId == null)) return;

		Map providerGrants = m_provider.getGroupRolesForUser(userId);
		m_storage.refreshUser(userId, providerGrants);

		// update site security for this user - get the user's realms for the three site locks
		Set updAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SECURE_UPDATE_SITE, null);
		Set unpAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SITE_VISIT_UNPUBLISHED, null);
		Set visitAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SITE_VISIT, null);

		// convert from azGroup ids (potential site references) to site ids for those that are site,
		// skipping special and user sites other than our user's
		Set updSites = new HashSet();
		for (Iterator i = updAuthzGroups.iterator(); i.hasNext();)
		{
			String azGroupId = (String) i.next();
			Reference ref = m_entityManager.newReference(azGroupId);
			if ((SiteService.SERVICE_NAME.equals(ref.getType())) && !SiteService.isSpecialSite(ref.getId())
					&& (!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				updSites.add(ref.getId());
			}
		}

		Set unpSites = new HashSet();
		for (Iterator i = unpAuthzGroups.iterator(); i.hasNext();)
		{
			String azGroupId = (String) i.next();
			Reference ref = m_entityManager.newReference(azGroupId);
			if ((SiteService.SERVICE_NAME.equals(ref.getType())) && !SiteService.isSpecialSite(ref.getId())
					&& (!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				unpSites.add(ref.getId());
			}
		}

		Set visitSites = new HashSet();
		for (Iterator i = visitAuthzGroups.iterator(); i.hasNext();)
		{
			String azGroupId = (String) i.next();
			Reference ref = m_entityManager.newReference(azGroupId);
			if ((SiteService.SERVICE_NAME.equals(ref.getType())) && !SiteService.isSpecialSite(ref.getId())
					&& (!SiteService.isUserSite(ref.getId()) || userId.equals(SiteService.getSiteUserId(ref.getId()))))
			{
				visitSites.add(ref.getId());
			}
		}

		SiteService.setUserSecurity(userId, updSites, unpSites, visitSites);
	}

	/**
	 * Update the site security based on the values in the AuthzGroup, if it is a site AuthzGroup.
	 * 
	 * @param azGroup
	 *        The AuthzGroup.
	 */
	protected void updateSiteSecurity(AuthzGroup azGroup)
	{
		// Special code for the site service
		Reference ref = m_entityManager.newReference(azGroup.getId());
		if (SiteService.SERVICE_NAME.equals(ref.getType()) && SiteService.SITE_SUBTYPE.equals(ref.getSubType()))
		{
			// collect the users
			Set updUsers = azGroup.getUsersIsAllowed(SiteService.SECURE_UPDATE_SITE);
			Set unpUsers = azGroup.getUsersIsAllowed(SiteService.SITE_VISIT_UNPUBLISHED);
			Set visitUsers = azGroup.getUsersIsAllowed(SiteService.SITE_VISIT);

			SiteService.setSiteSecurity(ref.getId(), updUsers, unpUsers, visitUsers);
		}
	}

	/**
	 * Update the site security when an AuthzGroup is deleted, if it is a site AuthzGroup.
	 * 
	 * @param azGroup
	 *        The AuthzGroup.
	 */
	protected void removeSiteSecurity(AuthzGroup azGroup)
	{
		// Special code for the site service
		Reference ref = m_entityManager.newReference(azGroup.getId());
		if (ref.getType().equals(SiteService.SERVICE_NAME))
		{
			// no azGroup, no users
			Set empty = new HashSet();

			SiteService.setSiteSecurity(ref.getId(), empty, empty, empty);
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * EntityProducer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "authzGroup";
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
		// for azGroup access
		if (reference.startsWith(REFERENCE_ROOT))
		{
			// the azGroup id may have separators - we use everything after "/realm/"
			String id = reference.substring(REFERENCE_ROOT.length() + 1, reference.length());

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
		return null;
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

		// if the reference is an AuthzGroup, and not a special one
		// get the list of realms for the azGroup-referenced resource
		if ((ref.getId() != null) && (ref.getId().length() > 0) && (!ref.getId().startsWith("!")))
		{
			// add the current user's azGroup (for what azGroup stuff everyone can do, i.e. add)
			ref.addUserAuthzGroup(rv, SessionManager.getCurrentSessionUserId());

			// make a new reference on the azGroup's id
			Reference refnew = m_entityManager.newReference(ref.getId());
			rv.addAll(refnew.getRealms());
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
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport)
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
		// TODO: move the site azg code here?
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Storage
	 *********************************************************************************************************************************************************************************************************************************************************/

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
		 * Check if an AuthzGroup by this id exists.
		 * 
		 * @param id
		 *        The AuthzGroup id.
		 * @return true if an AuthzGroup by this id exists, false if not.
		 */
		boolean check(String id);

		/**
		 * Get the AuthzGroup with this id, or null if not found.
		 * 
		 * @param id
		 *        The AuthzGroup id.
		 * @return The AuthzGroup with this id, or null if not found.
		 */
		AuthzGroup get(String id);

		/**
		 * Add a new AuthzGroup with this id.
		 * 
		 * @param id
		 *        The AuthzGroup id.
		 * @return The new AuthzGroup, or null if the id is in use.
		 */
		AuthzGroup put(String id);

		/**
		 * Save the changes to the AuthzGroup
		 * 
		 * @param azGroup
		 *        The AuthzGroup to save.
		 */
		void save(AuthzGroup azGroup);

		/**
		 * Remove this AuthzGroup.
		 * 
		 * @param azGroup
		 *        The azGroup to remove.
		 */
		void remove(AuthzGroup azGroup);

		/**
		 * Access a list of AuthzGroups that meet specified criteria, naturally sorted.
		 * 
		 * @param criteria
		 *        Selection criteria: AuthzGroups returned will match this string somewhere in their id, or provider group id.
		 * @param page
		 *        The PagePosition subset of items to return.
		 * @return The List (AuthzGroup) of AuthzGroups that meet specified criteria.
		 */
		List getAuthzGroups(String criteria, PagingPosition page);

		/**
		 * Count the AuthzGroup objets that meet specified criteria.
		 * 
		 * @param criteria
		 *        Selection criteria: realms returned will match this string somewhere in their id, or provider group id.
		 * @return The count of AuthzGroups that meet specified criteria.
		 */
		int countAuthzGroups(String criteria);

		/**
		 * Complete the read process once the basic AuthzGroup info has been read.
		 * 
		 * @param azGroup
		 *        The AuthzGroup to complete.
		 */
		void completeGet(BaseAuthzGroup azGroup);

		/**
		 * Test if this user is allowed to perform the function in the named AuthzGroup.
		 * 
		 * @param userId
		 *        The user id.
		 * @param function
		 *        The function to open.
		 * @param azGroupId
		 *        The AuthzGroup id to consult, if it exists.
		 * @return true if this user is allowed to perform the function in the named AuthzGroup, false if not.
		 */
		boolean isAllowed(String userId, String function, String azGroupId);

		/**
		 * Test if this user is allowed to perform the function in the named AuthzGroups.
		 * 
		 * @param userId
		 *        The user id.
		 * @param function
		 *        The function to open.
		 * @param azGroups
		 *        A collection of AuthzGroup ids to consult.
		 * @return true if this user is allowed to perform the function in the named AuthzGroups, false if not.
		 */
		boolean isAllowed(String userId, String function, Collection realms);

		/**
		 * Get the set of user ids of users who are allowed to perform the function in the named AuthzGroups.
		 * 
		 * @param function
		 *        The function to check.
		 * @param azGroups
		 *        A collection of the ids of AuthzGroups to consult.
		 * @return the Set (String) of user ids of users who are allowed to perform the function in the named AuthzGroups.
		 */
		Set getUsersIsAllowed(String function, Collection azGroups);

		/**
		 * Get the set of functions that users with this role in these AuthzGroups are allowed to perform.
		 * 
		 * @param role
		 *        The role name.
		 * @param azGroups
		 *        A collection of AuthzGroup ids to consult.
		 * @return the Set (String) of functions that users with this role in these AuthzGroups are allowed to perform
		 */
		Set getAllowedFunctions(String role, Collection azGroups);

		/**
		 * Get the set of AuthzGroup ids in which this user is allowed to perform this function.
		 * 
		 * @param userId
		 *        The user id.
		 * @param function
		 *        The function to check.
		 * @param azGroups
		 *        The Collection of AuthzGroup ids to search; if null, search them all.
		 * @return the Set (String) of AuthzGroup ids in which this user is allowed to perform this function.
		 */
		Set getAuthzGroupsIsAllowed(String userId, String function, Collection azGroups);

		/**
		 * Get the role name for this user in this AuthzGroup.
		 * 
		 * @param userId
		 *        The user id.
		 * @param function
		 *        The function to open.
		 * @param azGroupId
		 *        The AuthzGroup id to consult, if it exists.
		 * @return the role name for this user in this AuthzGroup, if the user has active membership, or null if not.
		 */
		String getUserRole(String userId, String azGroupId);

		/**
		 * Get the role name for each user in the userIds Collection in this AuthzGroup.
		 * 
		 * @param userId
		 *        The user id.
		 * @param function
		 *        The function to open.
		 * @param azGroupId
		 *        The AuthzGroup id to consult, if it exists.
		 * @return A Map (userId -> role name) of role names for each user who have active membership; if the user does not, it will not be in the Map.
		 */
		Map getUsersRole(Collection userIds, String azGroupId);

		/**
		 * Refresh this user's roles in any AuthzGroup that has an entry in the map; the user's new role is in the map.
		 * 
		 * @param userId
		 *        The user id
		 * @param providerMembership
		 *        The Map of external group id -> role id.
		 */
		void refreshUser(String userId, Map providerMembership);

		/**
		 * Refresh the external user - role membership for this AuthzGroup
		 * 
		 * @param azGroup
		 *        The azGroup to refresh.
		 */
		void refreshAuthzGroup(BaseAuthzGroup azGroup);
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * StorageUser implementation (no container)
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Construct a new continer given just an id.
	 * 
	 * @param id
	 *        The id for the new object.
	 * @return The new containe Resource.
	 */
	public Entity newContainer(String ref)
	{
		return null;
	}

	/**
	 * Construct a new container resource, from an XML element.
	 * 
	 * @param element
	 *        The XML.
	 * @return The new container resource.
	 */
	public Entity newContainer(Element element)
	{
		return null;
	}

	/**
	 * Construct a new container resource, as a copy of another
	 * 
	 * @param other
	 *        The other contianer to copy.
	 * @return The new container resource.
	 */
	public Entity newContainer(Entity other)
	{
		return null;
	}

	/**
	 * Construct a new rsource given just an id.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param id
	 *        The id for the new object.
	 * @param others
	 *        (options) array of objects to load into the Resource's fields.
	 * @return The new resource.
	 */
	public Entity newResource(Entity container, String id, Object[] others)
	{
		return new BaseAuthzGroup(id);
	}

	/**
	 * Construct a new resource, from an XML element.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param element
	 *        The XML.
	 * @return The new resource from the XML.
	 */
	public Entity newResource(Entity container, Element element)
	{
		return new BaseAuthzGroup(element);
	}

	/**
	 * Construct a new resource from another resource of the same type.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param other
	 *        The other resource.
	 * @return The new resource as a copy of the other.
	 */
	public Entity newResource(Entity container, Entity other)
	{
		return new BaseAuthzGroup((AuthzGroup) other);
	}

	/**
	 * Construct a new continer given just an id.
	 * 
	 * @param id
	 *        The id for the new object.
	 * @return The new containe Resource.
	 */
	public Edit newContainerEdit(String ref)
	{
		return null;
	}

	/**
	 * Construct a new container resource, from an XML element.
	 * 
	 * @param element
	 *        The XML.
	 * @return The new container resource.
	 */
	public Edit newContainerEdit(Element element)
	{
		return null;
	}

	/**
	 * Construct a new container resource, as a copy of another
	 * 
	 * @param other
	 *        The other contianer to copy.
	 * @return The new container resource.
	 */
	public Edit newContainerEdit(Entity other)
	{
		return null;
	}

	/**
	 * Construct a new rsource given just an id.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param id
	 *        The id for the new object.
	 * @param others
	 *        (options) array of objects to load into the Resource's fields.
	 * @return The new resource.
	 */
	public Edit newResourceEdit(Entity container, String id, Object[] others)
	{
		BaseAuthzGroup e = new BaseAuthzGroup(id);
		e.activate();
		return e;
	}

	/**
	 * Construct a new resource, from an XML element.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param element
	 *        The XML.
	 * @return The new resource from the XML.
	 */
	public Edit newResourceEdit(Entity container, Element element)
	{
		BaseAuthzGroup e = new BaseAuthzGroup(element);
		e.activate();
		return e;
	}

	/**
	 * Construct a new resource from another resource of the same type.
	 * 
	 * @param container
	 *        The Resource that is the container for the new resource (may be null).
	 * @param other
	 *        The other resource.
	 * @return The new resource as a copy of the other.
	 */
	public Edit newResourceEdit(Entity container, Entity other)
	{
		BaseAuthzGroup e = new BaseAuthzGroup((AuthzGroup) other);
		e.activate();
		return e;
	}

	/**
	 * Collect the fields that need to be stored outside the XML (for the resource).
	 * 
	 * @return An array of field values to store in the record outside the XML (for the resource).
	 */
	public Object[] storageFields(Entity r)
	{
		return null;
	}

	/**
	 * Check if this resource is in draft mode.
	 * 
	 * @param r
	 *        The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Entity r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * 
	 * @param r
	 *        The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Entity r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * 
	 * @param r
	 *        The resource.
	 * @return The resource date.
	 */
	public Time getDate(Entity r)
	{
		return null;
	}
}