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
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.GroupProvider;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.service.legacy.resource.EntityManager;
import org.sakaiproject.service.legacy.resource.Reference;
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
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BaseRealmService is a Sakai azGroup service implementation.
 * </p>
 * <p>
 * To support the public view feature, an AuthzGroup named TEMPLATE_PUBVIEW must exist, with a role named ROLE_PUBVIEW - all the abilities in this role become the public view abilities for any resource.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public abstract class BaseRealmService implements AuthzGroupService, StorageUser
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
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, resource);
		}
	}

	/**
	 * Create the live properties for the azGroup.
	 */
	protected void addLiveProperties(BaseAuthzGroup azGroup)
	{
		String current = UsageSessionService.getSessionUserId();

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
		String current = UsageSessionService.getSessionUserId();

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
			m_entityManager.registerEntityProducer(this);

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
	public void joinSite(String siteId) throws IdUnusedException, PermissionException
	{
		String user = UsageSessionService.getSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));

		// get the site
		Site site = SiteService.getSite(siteId);

		// must be joinable
		if (!site.isJoinable())
		{
			throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));
		}

		// the role to assign
		String roleId = site.getJoinerRole();
		if (roleId == null)
		{
			m_logger.warn(this + ".joinSite(): null site joiner role for site: " + siteId);
			throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));
		}

		// get the site's azGroup (the azGroup id is the site reference)
		String azGroupId = SiteService.siteReference(siteId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupReference(azGroupId));

		// get the AuthzGroup
		AuthzGroup azGroup = m_storage.get(azGroupId);
		if (azGroup == null)
		{
			throw new IdUnusedException(azGroupId);
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
	public void unjoinSite(String siteId) throws IdUnusedException, PermissionException
	{
		String user = UsageSessionService.getSessionUserId();
		if (user == null) throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));

		// get the site
		Site site = SiteService.getSite(siteId);

		// get the site's azGroup (the azGroup id is the site reference)
		String azGroupId = SiteService.siteReference(siteId);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupReference(azGroupId));

		// get the AuthzGroup
		AuthzGroup azGroup = m_storage.get(azGroupId);
		if (azGroup == null)
		{
			throw new IdUnusedException(azGroupId);
		}

		// if not joined (no grant), we are done
		BaseMember grant = (BaseMember) azGroup.getMember(user);
		if (grant == null)
		{
			return;
		}

		// if joined with the maintain role, and the joiner role is different or the site is not joinable
		// (so the user could not re-join as maintainer), don't allow the unjoin

		// update: if joined with the maintain role, but not the only maintainer of the azGroup
		// allow the unjoin

		String maintainRole = azGroup.getMaintainRole();
		String joinerRole = site.getJoinerRole();

		if (!StringUtil.different(maintainRole, grant.getRole().getId()))
		{
			// if maintainter, check if the only maintainer
			Set maintainers = azGroup.getUsersHasRole(maintainRole);
			if (maintainers.size() <= 1)
			{
				if (StringUtil.different(maintainRole, joinerRole) || !site.isJoinable())
				{
					throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));
				}
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
	public boolean allowUnjoinSite(String siteId)
	{
		String user = UsageSessionService.getSessionUserId();
		try
		{
			if (user == null)
			{
				throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));
			}
			// get the site
			Site site = SiteService.getSite(siteId);

			// get the site's azGroup (the azGroup id is the site reference)
			String azGroupId = SiteService.siteReference(siteId);

			// check security (throws if not permitted)
			unlock(SECURE_UPDATE_OWN_AUTHZ_GROUP, authzGroupReference(azGroupId));

			// get the azGroup
			AuthzGroup azGroup = m_storage.get(azGroupId);
			if (azGroup == null)
			{
				throw new IdUnusedException(azGroupId);
			}

			// if not joined (no grant), we are done
			// if the grant is provider, unable to unjoin the site
			BaseMember grant = (BaseMember) azGroup.getMember(user);
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

			// update: if joined with the maintain role, but not the only maintainer of the azGroup
			// allow the unjoin

			String maintainRole = azGroup.getMaintainRole();
			String joinerRole = site.getJoinerRole();

			if (!StringUtil.different(maintainRole, grant.getRole().getId()))
			{
				// if maintainter, check if the only maintainer
				Set maintainers = azGroup.getUsersHasRole(maintainRole);
				if (maintainers.size() <= 1)
				{
					if (StringUtil.different(maintainRole, joinerRole) || !site.isJoinable())
					{
						throw new PermissionException(user, SECURE_UPDATE_OWN_AUTHZ_GROUP, SiteService.siteReference(siteId));
					}
				}
			}
		}
		catch (IdUnusedException e)
		{
			return false;
		}
		catch (PermissionException e)
		{
			return false;
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

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_AUTHZ_GROUP, authzGroupReference(azGroup.getId()));

		// check for existance
		if (!m_storage.check(azGroup.getId()))
		{
			throw new IdUnusedException(azGroup.getId());
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
		EventTrackingService.post(EventTrackingService
				.newEvent(((BaseAuthzGroup) azGroup).getEvent(), azGroup.getReference(), true));

		// close the azGroup object
		((BaseAuthzGroup) azGroup).closeEdit();

		// update the db with latest provider, and site security with the latest changes, using the updated azGroup
		BaseAuthzGroup updatedRealm = (BaseAuthzGroup) m_storage.get(azGroup.getId());
		updateSiteSecurity(updatedRealm);
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

		// save the changes
		m_storage.save(azGroup);

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
	public Set getAuthzGroupsIsAllowed(String userId, String function)
	{
		return m_storage.getAuthzGroupsIsAllowed(userId, function);
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
		Set updAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SECURE_UPDATE_SITE);
		Set unpAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SITE_VISIT_UNPUBLISHED);
		Set visitAuthzGroups = getAuthzGroupsIsAllowed(userId, SiteService.SITE_VISIT);

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
		if (SiteService.SERVICE_NAME.equals(ref.getType()))
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
	public Collection getEntityRealms(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Collection rv = new Vector();

		// if the reference is an AuthzGroup, and not a special one
		// get the list of realms for the azGroup-referenced resource
		if ((ref.getId() != null) && (ref.getId().length() > 0) && (!ref.getId().startsWith("!")))
		{
			// add the current user's azGroup (for what azGroup stuff everyone can do, i.e. add)
			ref.addUserRealm(rv, UsageSessionService.getSessionUserId());

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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * AuthzGroup implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * <p>
	 * BaseAuthzGroup is an implementation of the AuthzGroup object.
	 * </p>
	 */
	public class BaseAuthzGroup implements AuthzGroup
	{
		/** The internal 'db' key. */
		protected Integer m_key = null;

		/** The azGroup id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** Map of userId to Member */
		protected Map m_userGrants = null;

		/** Map of Role id to a Role defined in this AuthzGroup. */
		protected Map m_roles = null;

		/** The external azGroup id, or null if not defined. */
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

		/** Set while the azGroup is not fully loaded from the storage. */
		protected boolean m_lazy = false;

		/** The event code for this azGroup. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		 * Construct.
		 * 
		 * @param id
		 *        The azGroup id.
		 */
		public BaseAuthzGroup(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			m_userGrants = new HashMap();
			m_roles = new HashMap();

			// if the id is not null (a new azGroup, rather than a reconstruction)
			// add the automatic (live) properties
			if (m_id != null) addLiveProperties(this);
		}

		/**
		 * Construct from another AuthzGroup object.
		 * 
		 * @param azGroup
		 *        The azGroup object to use for values.
		 */
		public BaseAuthzGroup(AuthzGroup azGroup)
		{
			setAll(azGroup);
		}

		/**
		 * (Re)Construct from parts.
		 * 
		 * @param dbid
		 *        The database id.
		 * @param id
		 *        The azGroup id.
		 * @param providerId
		 *        The provider id.
		 * @param maintainRole
		 *        The maintain role id.
		 * @param createdBy
		 *        The user created by id.
		 * @param createdOn
		 *        The time created.
		 * @param modifiedBy
		 *        The user modified by id.
		 * @param modifiedOn
		 *        The time modified.
		 */
		public BaseAuthzGroup(Integer dbid, String id, String providerId, String maintainRole, String createdBy, Time createdOn,
				String modifiedBy, Time modifiedOn)
		{
			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			m_userGrants = new HashMap();
			m_roles = new HashMap();

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
		 * Construct from information in XML.
		 * 
		 * @param el
		 *        The XML DOM Element definining the azGroup.
		 */
		public BaseAuthzGroup(Element el)
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
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
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
					BaseRole role = new BaseRole(element, this);
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
					BaseRole role = (BaseRole) m_roles.get(roleId);
					if (role != null)
					{
						// if already granted, update to point to the role with the most permissions
						BaseMember grant = (BaseMember) m_userGrants.get(userId);
						if (grant != null)
						{
							if (role.m_locks.size() > ((BaseRole) grant.role).m_locks.size())
							{
								m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
										+ grant.role.getId() + " keeping: " + roleId);
								grant.role = role;
							}
							else
							{
								m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
										+ roleId + " keeping: " + grant.role.getId());
							}
						}
						else
						{
							grant = new BaseMember(role, Boolean.valueOf(active).booleanValue(), Boolean.valueOf(provided)
									.booleanValue(), userId);
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
							BaseRole role = (BaseRole) m_roles.get(ANON_ROLE);
							if (role == null)
							{
								role = new BaseRole(ANON_ROLE);
								m_roles.put(ANON_ROLE, role);
							}
							role.allowFunction(lock);
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
							BaseRole role = (BaseRole) m_roles.get(AUTH_ROLE);
							if (role == null)
							{
								role = new BaseRole(AUTH_ROLE);
								m_roles.put(AUTH_ROLE, role);
							}
							role.allowFunction(lock);
						}
					}

					else if (userId != null)
					{
						BaseRole role = (BaseRole) m_roles.get(roleId);
						if (role != null)
						{
							// if already granted, update to point to the role with the most permissions
							BaseMember grant = (BaseMember) m_userGrants.get(userId);
							if (grant != null)
							{
								if (role.m_locks.size() > ((BaseRole) grant.role).m_locks.size())
								{
									m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
											+ grant.role.getId() + " keeping: " + roleId);
									grant.role = role;
								}
								else
								{
									m_logger.warn(this + "(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
											+ roleId + " keeping: " + grant.role.getId());
								}
							}
							else
							{
								grant = new BaseMember(role, true, false, userId);
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
				catch (Exception ignore)
				{
				}
			}
			if (m_lastModifiedTime == null)
			{
				try
				{
					m_lastModifiedTime = m_properties.getTimeProperty("DAV:getlastmodified");
				}
				catch (Exception ignore)
				{
				}
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

				BaseRole role = (BaseRole) m_roles.get(ANON_ROLE);
				if (role == null)
				{
					role = new BaseRole(ANON_ROLE);
					m_roles.put(ANON_ROLE, role);
				}
				role.allowFunction("content.read");
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
				return "Site AuthzGroup Template";
			}

			else if (getId().equals("!site.user"))
			{
				return "My Workspace AuthzGroup Template";
			}

			else if (getId().startsWith("!user.template"))
			{
				return "User AuthzGroup Template";
			}

			else if (getId().equals("!site.helper"))
			{
				return "Site Helper Patch AuthzGroup";
			}

			else if (getId().startsWith("!"))
			{
				return "Special AuthzGroup";
			}

			// the rest are references to some resource
			try
			{
				Reference ref = m_entityManager.newReference(getId());
				return ref.getDescription();
			}
			catch (Throwable ignore)
			{
			}

			return "unknown";
		}

		/**
		 * Take all values from this object.
		 * 
		 * @param azGroup
		 *        The AuthzGroup to take values from.
		 */
		protected void setAll(AuthzGroup azGroup)
		{
			if (((BaseAuthzGroup) azGroup).m_lazy) m_storage.completeGet(((BaseAuthzGroup) azGroup));

			m_key = ((BaseAuthzGroup) azGroup).m_key;
			m_id = ((BaseAuthzGroup) azGroup).m_id;
			m_providerRealmId = ((BaseAuthzGroup) azGroup).m_providerRealmId;
			m_maintainRole = ((BaseAuthzGroup) azGroup).m_maintainRole;

			m_createdUserId = ((BaseAuthzGroup) azGroup).m_createdUserId;
			m_lastModifiedUserId = ((BaseAuthzGroup) azGroup).m_lastModifiedUserId;
			if (((BaseAuthzGroup) azGroup).m_createdTime != null)
				m_createdTime = (Time) ((BaseAuthzGroup) azGroup).m_createdTime.clone();
			if (((BaseAuthzGroup) azGroup).m_lastModifiedTime != null)
				m_lastModifiedTime = (Time) ((BaseAuthzGroup) azGroup).m_lastModifiedTime.clone();

			// make a deep copy of the roles as new Role objects
			m_roles = new HashMap();
			for (Iterator it = ((BaseAuthzGroup) azGroup).m_roles.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseRole role = (BaseRole) entry.getValue();
				String id = (String) entry.getKey();

				m_roles.put(id, new BaseRole(id, role));
			}

			// make a deep copy (w/ new Member objects pointing to my own roles) of the user - role grants
			m_userGrants = new HashMap();
			for (Iterator it = ((BaseAuthzGroup) azGroup).m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseMember grant = (BaseMember) entry.getValue();
				String id = (String) entry.getKey();

				m_userGrants.put(id, new BaseMember((Role) m_roles.get(grant.role.getId()), grant.active, grant.provided,
						grant.userId));
			}

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(azGroup.getProperties());
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) azGroup.getProperties()).isLazy());

			m_lazy = false;
		}

		/**
		 * {@inheritDoc}
		 */
		public Element toXml(Document doc, Stack stack)
		{
			if (m_lazy) m_storage.completeGet(this);

			Element azGroup = doc.createElement("azGroup");

			if (stack.isEmpty())
			{
				doc.appendChild(azGroup);
			}
			else
			{
				((Element) stack.peek()).appendChild(azGroup);
			}

			stack.push(azGroup);

			azGroup.setAttribute("id", getId());
			if (m_providerRealmId != null)
			{
				azGroup.setAttribute("provider-id", m_providerRealmId);
			}
			if (m_maintainRole != null)
			{
				azGroup.setAttribute("maintain-role", m_maintainRole);
			}

			azGroup.setAttribute("created-id", m_createdUserId);
			azGroup.setAttribute("modified-id", m_lastModifiedUserId);
			azGroup.setAttribute("created-time", m_createdTime.toString());
			azGroup.setAttribute("modified-time", m_lastModifiedTime.toString());

			// properties
			getProperties().toXml(doc, stack);

			// roles (write before grants!)
			for (Iterator i = m_roles.values().iterator(); i.hasNext();)
			{
				BaseRole role = (BaseRole) i.next();
				role.toXml(doc, stack);
			}

			// user - role grants
			for (Iterator i = m_userGrants.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				BaseMember grant = (BaseMember) entry.getValue();
				String user = (String) entry.getKey();

				Element element = doc.createElement("grant");
				azGroup.appendChild(element);
				element.setAttribute("user", user);
				element.setAttribute("role", grant.role.getId());
				element.setAttribute("active", Boolean.valueOf(grant.active).toString());
				element.setAttribute("provided", Boolean.valueOf(grant.provided).toString());
			}

			stack.pop();

			return azGroup;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getId()
		{
			if (m_id == null) return "";
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
			return authzGroupReference(m_id);
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
		public boolean isAllowed(String user, String lock)
		{
			if (m_lazy) m_storage.completeGet(this);

			// consider a role granted
			BaseMember grant = (BaseMember) m_userGrants.get(user);
			if ((grant != null) && (grant.active))
			{
				if (grant.role.isAllowed(lock)) return true;
			}

			// consider auth role
			if (!UserDirectoryService.getAnonymousUser().getId().equals(user))
			{
				Role auth = (Role) m_roles.get(AUTH_ROLE);
				if (auth != null)
				{
					if (auth.isAllowed(lock)) return true;
				}
			}

			// consider anon role
			Role anon = (Role) m_roles.get(ANON_ROLE);
			if (anon != null)
			{
				if (anon.isAllowed(lock)) return true;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasRole(String user, String role)
		{
			if (m_lazy) m_storage.completeGet(this);

			BaseMember grant = (BaseMember) m_userGrants.get(user);
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
				Member grant = (Member) entry.getValue();
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
		public Set getMembers()
		{
			// Note: this is the only way to see non-active grants

			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Member grant = (Member) entry.getValue();
				rv.add(grant);
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsersIsAllowed(String lock)
		{
			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				BaseMember grant = (BaseMember) entry.getValue();
				if (grant.active && grant.role.isAllowed(lock))
				{
					rv.add(user);
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsersHasRole(String role)
		{
			if (m_lazy) m_storage.completeGet(this);

			Set rv = new HashSet();
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String user = (String) entry.getKey();
				BaseMember grant = (BaseMember) entry.getValue();
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

			BaseMember grant = (BaseMember) m_userGrants.get(user);
			if ((grant != null) && (grant.active)) return grant.role;

			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Member getMember(String user)
		{
			if (m_lazy) m_storage.completeGet(this);

			BaseMember grant = (BaseMember) m_userGrants.get(user);
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
		public String getProviderGroupId()
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
		 * {@inheritDoc}
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof AuthzGroup)) return false;
			return ((AuthzGroup) obj).getId().equals(getId());
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof AuthzGroup)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// sort based on id
			int compare = getId().compareTo(((AuthzGroup) obj).getId());

			return compare;
		}

		/**
		 * {@inheritDoc}
		 */
		public void addMember(String user, String roleId, boolean active, boolean provided)
		{
			Role role = (Role) m_roles.get(roleId);
			if (role == null)
			{
				m_logger.warn(this + ".addUserRole: role undefined: " + roleId);
				return;
			}

			BaseMember grant = (BaseMember) m_userGrants.get(user);
			if (grant == null)
			{
				grant = new BaseMember(role, active, provided, user);
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
		 * {@inheritDoc}
		 */
		public void removeMember(String user)
		{
			if (m_lazy) m_storage.completeGet(this);

			m_userGrants.remove(user);
		}

		/**
		 * Take all values from this object.
		 * 
		 * @param azGroup
		 *        The AuthzGroup object to take values from.
		 */
		protected void set(AuthzGroup azGroup)
		{
			setAll(azGroup);
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeMembers()
		{
			if (m_lazy) m_storage.completeGet(this);

			m_userGrants.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public Role addRole(String id) throws IdUsedException
		{
			if (m_lazy) m_storage.completeGet(this);

			Role role = (Role) m_roles.get(id);
			if (role != null) throw new IdUsedException(id);

			role = new BaseRole(id);
			m_roles.put(role.getId(), role);

			return role;
		}

		/**
		 * {@inheritDoc}
		 */
		public Role addRole(String id, Role other) throws IdUsedException
		{
			if (m_lazy) m_storage.completeGet(this);

			Role role = (Role) m_roles.get(id);
			if (role != null) throw new IdUsedException(id);

			role = new BaseRole(id, other);
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
					BaseMember grant = (BaseMember) entry.getValue();
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
		public void removeRoles()
		{
			if (m_lazy) m_storage.completeGet(this);

			// clear roles and grants (since grants grant roles)
			m_roles.clear();
			m_userGrants.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public Role getRoleEdit(String id)
		{
			if (m_lazy) m_storage.completeGet(this);

			BaseRole azGroup = (BaseRole) m_roles.get(id);
			if (azGroup != null) azGroup.activate();

			return azGroup;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setProviderGroupId(String id)
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
		 * Access the event code for this azGroup.
		 * 
		 * @return The event code for this azGroup.
		 */
		protected String getEvent()
		{
			return m_event;
		}

		/**
		 * Set the event code for this azGroup.
		 * 
		 * @param event
		 *        The event code for this azGroup.
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
		 * Check to see if the azGroup is still active, or has already been closed.
		 * 
		 * @return true if the azGroup is active, false if it's been closed.
		 */
		public boolean isActiveEdit()
		{
			return m_active;
		}

		/**
		 * Close the azGroup object - it cannot be used after this.
		 */
		protected void closeEdit()
		{
			m_active = false;
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Role implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BaseRole implements Role
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
		 * 
		 * @param id
		 *        The role id.
		 */
		public BaseRole(String id)
		{
			m_id = id;
			m_locks = new HashSet();
		}

		/**
		 * Construct as a copy
		 * 
		 * @param id
		 *        The role id.
		 * @param other
		 *        The role to copy.
		 */
		public BaseRole(String id, Role other)
		{
			m_id = id;
			m_description = ((BaseRole) other).m_description;
			m_locks = new HashSet();
			m_locks.addAll(((BaseRole) other).m_locks);
		}

		/**
		 * Construct from information in XML.
		 * 
		 * @param el
		 *        The XML DOM Element definining the role.
		 */
		public BaseRole(Element el, BaseAuthzGroup azGroup)
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
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
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
		 * {@inheritDoc}
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
			if (m_description != null) Xml.encodeAttribute(role, "description-enc", m_description);

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
		 * Check to see if the azGroup is still active, or has already been closed.
		 * 
		 * @return true if the azGroup is active, false if it's been closed.
		 */
		public boolean isActiveEdit()
		{
			return m_active;
		}

		/**
		 * Close the azGroup object - it cannot be used after this.
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
		public boolean isAllowed(String lock)
		{
			return m_locks.contains(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getAllowedFunctions()
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
		public void allowFunction(String lock)
		{
			m_locks.add(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public void allowFunctions(Collection locks)
		{
			m_locks.addAll(locks);
		}

		/**
		 * {@inheritDoc}
		 */
		public void disallowFunction(String lock)
		{
			m_locks.remove(lock);
		}

		/**
		 * {@inheritDoc}
		 */
		public void disallowFunctions(Collection locks)
		{
			m_locks.removeAll(locks);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean allowsNoFunctions()
		{
			return m_locks.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public void disallowAll()
		{
			m_locks.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Role)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((Role) obj).getId());

			return compare;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Role)) return false;

			return ((Role) obj).getId().equals(getId());
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}
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
		 * @return the Set (String) of AuthzGroup ids in which this user is allowed to perform this function.
		 */
		Set getAuthzGroupsIsAllowed(String userId, String function);

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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Member Implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BaseMember implements Member
	{
		public Role role = null;

		public boolean provided = false;

		public boolean active = true;

		public String userId = null;

		public BaseMember(Role role, boolean active, boolean provided, String userId)
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
			if (!(obj instanceof Member)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// compare by comparing the user id
			int compare = getUserId().compareTo(((Member) obj).getUserId());

			return compare;
		}
	}
}
