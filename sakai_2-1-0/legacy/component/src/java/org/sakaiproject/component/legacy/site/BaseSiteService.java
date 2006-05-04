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

package org.sakaiproject.component.legacy.site;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.SiteCache;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
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
import org.sakaiproject.service.legacy.security.SecurityAdvisor;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * BaseSiteService is a base implementation of the SiteService.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public abstract class BaseSiteService implements SiteService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A site cache. */
	protected SiteCache m_siteCache = null;

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
	 * Access the site id extracted from a site reference.
	 * 
	 * @param ref
	 *        The site reference string.
	 * @return The the site id extracted from a site reference.
	 */
	protected String siteId(String ref)
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
	 *            Thrown if the user does not have access
	 */
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(lock, resource);
		}
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock1
	 *        The lock id string.
	 * @param lock2
	 *        The lock id string.
	 * @param resource
	 *        The resource reference string, or null if no resource is involved.
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
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock1
	 *        The lock id string.
	 * @param lock2
	 *        The lock id string.
	 * @param resource
	 *        The resource reference string, or null if no resource is involved.
	 * @exception PermissionException
	 *            Thrown if the user does not have access to either.
	 */
	protected void unlock2(String lock1, String lock2, String resource) throws PermissionException
	{
		if (!unlockCheck2(lock1, lock2, resource))
		{
			throw new PermissionException(lock1 + "/" + lock2, resource);
		}
	}

	/**
	 * Update the live properties for a site for when modified.
	 */
	protected void addLiveUpdateProperties(BaseSite site)
	{
		String current = SessionManager.getCurrentSessionUserId();

		site.m_lastModifiedUserId = current;
		site.m_lastModifiedTime = TimeService.newTime();
	}

	/**
	 * Create the live properties for the site.
	 */
	protected void addLiveProperties(BaseSite site)
	{
		String current = SessionManager.getCurrentSessionUserId();

		site.m_createdUserId = current;
		site.m_lastModifiedUserId = current;

		Time now = TimeService.newTime();
		site.m_createdTime = now;
		site.m_lastModifiedTime = (Time) now.clone();
	}

	/**
	 * Return the url unchanged, unless it's a reference, then return the reference url
	 */
	protected String convertReferenceUrl(String url)
	{
		// make a reference
		Reference ref = m_entityManager.newReference(url);

		// if it didn't recognize this, return it unchanged
		if (!ref.isKnownType()) return url;

		// return the reference's url
		return ref.getUrl();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Constructors, Dependencies and their setter methods
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

	/** If true, run the regenerate ids pass on all sites at startup. */
	protected boolean m_regenerateIds = false;

	/**
	 * Configuration: regenerate all site;'s page and tool ids to assure uniqueness.
	 * 
	 * @param value
	 *        The regenerate ids value
	 */
	public void setRegenerateIds(String value)
	{
		m_regenerateIds = new Boolean(value).booleanValue();
	}

	/** The # seconds to cache the site queries. 0 disables the cache. */
	protected int m_cacheSeconds = 3 * 60;

	/**
	 * Set the # minutes to cache the site queries.
	 * 
	 * @param time
	 *        The # minutes to cache the site queries (as an integer string).
	 */
	public void setCacheMinutes(String time)
	{
		m_cacheSeconds = Integer.parseInt(time) * 60;
	}

	/** The # seconds to cache gets. 0 disables the cache. */
	protected int m_cacheCleanerSeconds = 15 * 60;

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

	/**
	 * Regenerate the page and tool ids for all sites.
	 */
	protected void regenerateAllSiteIds()
	{
		List sites = m_storage.getAll();
		for (Iterator iSites = sites.iterator(); iSites.hasNext();)
		{
			Site site = (Site) iSites.next();
			Site edit = m_storage.get(site.getId());
			if (site != null)
			{
				edit.regenerateIds();
				m_storage.save(edit);

				m_logger.info(this + ".regenerateAllSiteIds: site: " + site.getId());
			}
			else
			{
				m_logger.warn(this + "regenerateAllSiteIds: site: " + site.getId() + " could not be edited.");
			}
		}
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

			if (m_regenerateIds)
			{
				regenerateAllSiteIds();
				m_regenerateIds = false;
			}

			// <= 0 minutes indicates no caching desired
			if (m_cacheSeconds > 0)
			{
				// build a synchronized map for the call cache, automatiaclly checking for expiration every 15 mins.
				m_siteCache = MemoryService.newSiteCache(m_cacheCleanerSeconds, siteReference(""));
			}

			// register as an entity producer
			m_entityManager.registerEntityProducer(this);

			// register functions
			FunctionManager.registerFunction(SITE_VISIT);
			FunctionManager.registerFunction(SITE_VISIT_UNPUBLISHED);
			FunctionManager.registerFunction(SECURE_ADD_SITE);
			FunctionManager.registerFunction(SECURE_ADD_USER_SITE);
			FunctionManager.registerFunction(SECURE_REMOVE_SITE);
			FunctionManager.registerFunction(SECURE_UPDATE_SITE);
			FunctionManager.registerFunction(SECURE_VIEW_ROSTER);
			FunctionManager.registerFunction(SECURE_UPDATE_SITE_MEMBERSHIP);
			FunctionManager.registerFunction(SECURE_UPDATE_GROUP_MEMBERSHIP);

			m_logger.info(this + ".init() - caching minutes: " + m_cacheSeconds / 60);
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
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
	 * SiteService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public boolean allowAccessSite(String id)
	{
		boolean rv = false;

		try
		{
			Site site = getSite(id);
			if (site.isPublished())
			{
				rv = unlockCheck(SITE_VISIT, site.getReference());
			}

			else
			{
				rv = unlockCheck(SITE_VISIT_UNPUBLISHED, site.getReference());
			}
		}
		catch (Exception ignore)
		{
		}

		return rv;
	}

	/**
	 * Access an already defined site object.
	 * 
	 * @param id
	 *        The site id string.
	 * @return A site object containing the site information
	 * @exception IdUnusedException
	 *            if not found
	 */
	protected Site getDefinedSite(String id) throws IdUnusedException
	{
		if (id == null) throw new IdUnusedException("<null>");

		Site rv = null;

		// check the cache
		String ref = siteReference(id);
		if ((m_siteCache != null) && (m_siteCache.containsKey(ref)))
		{
			rv = (Site) m_siteCache.get(ref);
			
			// return a copy of the site from the cache
			rv = new BaseSite(rv, true);

			return rv;
		}

		rv = m_storage.get(id);

		// if not found
		if (rv == null) throw new IdUnusedException(id);

		// get all of the site loaded
		rv.loadAll();

		// track it - we don't track site access -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(SECURE_ACCESS_SITE, site.getReference()));

		// cache a copy
		if (m_siteCache != null)
		{
			Site copy = new BaseSite(rv, true);
			m_siteCache.put(ref, copy, m_cacheSeconds);
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public Site getSite(String id) throws IdUnusedException
	{
		if (id == null)
		{
			throw new IdUnusedException("null");
		}

		try
		{
			return getDefinedSite(id);
		}
		catch (IdUnusedException e)
		{
			// if this is the current user's site, we can create it
			if (isUserSite(id) && id.substring(1).equals(SessionManager.getCurrentSessionUserId()))
			{
				// use lowercase user id inside user's MyWorkspace id
				id = id.toLowerCase();

				// pick a template, type based, to clone it exactly but set this as the id
				BaseSite template = null;
				try
				{
					User user = UserDirectoryService.getUser((SessionManager.getCurrentSessionUserId()).toLowerCase());
					template = (BaseSite) getDefinedSite(USER_SITE_TEMPLATE + "." + user.getType());
				}
				catch (Throwable t)
				{
				}

				// if a type based template was not found, use the generic one
				// will throw IdUnusedException all the way out of this method if that's not defined
				if (template == null)
				{
					template = (BaseSite) getDefinedSite(USER_SITE_TEMPLATE);
				}

				// reserve a site with this id from the info store - if it's in use, this will return null
				try
				{
					// check security (throws if not permitted)
					unlock(SECURE_ADD_USER_SITE, siteReference(id));

					// reserve a site with this id from the info store - if it's in use, this will return null
					BaseSite site = (BaseSite) m_storage.put(id);
					if (site == null)
					{
						throw new IdUsedException(id);
					}

					site.setEvent(SECURE_ADD_SITE);

					// copy in the template
					site.set(template, false);

					doSave(site, true, EntityProducer.ChangeType.ADD);

					return site;
				}
				catch (IdUsedException ee)
				{
					throw e;
				}
				catch (PermissionException ee)
				{
					throw e;
				}
			}
			else
			{
				throw e;
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public Site getSiteVisit(String id) throws IdUnusedException, PermissionException
	{
		// get the site
		Site rv = getSite(id);

		// check for visit permission
		if (rv.isPublished())
		{
			unlock(SITE_VISIT, rv.getReference());
		}
		else
		{
			unlock(SITE_VISIT_UNPUBLISHED, rv.getReference());
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowUpdateSite(String id)
	{
		return unlockCheck(SECURE_UPDATE_SITE, siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowUpdateSiteMembership(String id)
	{
		return unlockCheck2(SECURE_UPDATE_SITE, SECURE_UPDATE_SITE_MEMBERSHIP, siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowUpdateGroupMembership(String id)
	{
		return unlockCheck2(SECURE_UPDATE_SITE, SECURE_UPDATE_GROUP_MEMBERSHIP, siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public void save(Site site) throws IdUnusedException, PermissionException
	{
		if (site.getId() == null) throw new IdUnusedException("<null>");

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_SITE, site.getReference());

		// check for existance
		if (!m_storage.check(site.getId()))
		{
			throw new IdUnusedException(site.getId());
		}

		doSave((BaseSite) site, false, EntityProducer.ChangeType.UPDATE);
	}

	/**
	 * @inheritDoc
	 */
	public void saveSiteMembership(Site site) throws IdUnusedException, PermissionException
	{
		if (site.getId() == null) throw new IdUnusedException("<null>");

		// check security (throws if not permitted)
		unlock2(SECURE_UPDATE_SITE_MEMBERSHIP, SECURE_UPDATE_SITE, site.getReference());

		// check for existance
		if (!m_storage.check(site.getId()))
		{
			throw new IdUnusedException(site.getId());
		}

		enableAzgSecurityAdvisor();
		saveSiteAzg(site);
		SecurityService.clearAdvisors();

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_UPDATE_SITE_MEMBERSHIP, site.getReference(), true));
	}

	/**
	 * @inheritDoc
	 */
	public void saveGroupMembership(Site site) throws IdUnusedException, PermissionException
	{
		if (site.getId() == null) throw new IdUnusedException("<null>");

		// check security (throws if not permitted)
		unlock2(SECURE_UPDATE_GROUP_MEMBERSHIP, SECURE_UPDATE_SITE, site.getReference());

		// check for existance
		if (!m_storage.check(site.getId()))
		{
			throw new IdUnusedException(site.getId());
		}

		enableAzgSecurityAdvisor();
		saveGroupAzgs(site);
		SecurityService.clearAdvisors();

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_UPDATE_GROUP_MEMBERSHIP, site.getReference(), true));
	}

	/**
	 * Comlete the save process.
	 * @param site The site to save.
	 */
	protected void doSave(BaseSite site, boolean isNew, EntityProducer.ChangeType change)
	{
		if (isNew)
		{
			addLiveProperties(site);
		}

		// update the properties
		addLiveUpdateProperties(site);

		// complete the edit
		m_storage.save(site);

		// save any modified azgs
		enableAzgSecurityAdvisor();
		saveSiteAzg(site);
		saveGroupAzgs(site);
		SecurityService.clearAdvisors();

		// sync up with all other services
		// TODO: do this under the security advisor, too, so we don't need all the various service security on site creation? -ggolden
		enableRelated(site, change);

		// track it
		String event = site.getEvent();
		if (event == null) event = SECURE_UPDATE_SITE;
		EventTrackingService.post(EventTrackingService.newEvent(event, site.getReference(), true));

		// clear the event for next time
		site.setEvent(null);
	}

	/**
	 * Establish a security advisor to allow the "embedded" azg work to occur with no need for additional security permissions.
	 */
	protected void enableAzgSecurityAdvisor()
	{
		// put in a security advisor so we can do our azg work without need of further permissions
		// TODO: could make this more specific to the AuthzGroupService.SECURE_UPDATE_AUTHZ_GROUP permission -ggolden
		SecurityService.pushAdvisor(new SecurityAdvisor()
		{
			public SecurityAdvice isAllowed(String userId, String function, String reference)
			{
				return SecurityAdvice.ALLOWED;
			}
		});
	}

	/**
	 * Save the site's azg if modified.
	 * 
	 * @param site
	 *        The site to save.
	 */
	protected void saveSiteAzg(Site site)
	{
		if (((BaseSite) site).m_azgChanged)
		{
			try
			{
				AuthzGroupService.save(((BaseSite) site).m_azg);
			}
			catch (Throwable t)
			{
				m_logger.warn(this + ".saveAzgs - site: " + t);
			}
			((BaseSite) site).m_azgChanged = false;
		}
	}

	/**
	 * Save group azgs that are modified.
	 * @param site The site to save.
	 */
	protected void saveGroupAzgs(Site site)
	{
		for (Iterator i = site.getGroups().iterator(); i.hasNext();)
		{
			BaseGroup group = (BaseGroup) i.next();
			if (group.m_azgChanged)
			{
				try
				{
					AuthzGroupService.save(group.m_azg);
				}
				catch (Throwable t)
				{
					m_logger.warn(this + ".saveAzgs - group: " + group.getTitle() + " : " + t);
				}
				group.m_azgChanged = false;
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public void saveSiteInfo(String id, String description, String infoUrl) throws IdUnusedException, PermissionException
	{
		String ref = siteReference(id);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_SITE, ref);

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		m_storage.saveInfo(id, description, infoUrl);
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowAddSite(String id)
	{
		// check security (throws if not permitted)
		if (id != null && isUserSite(id))
		{
			return unlockCheck(SECURE_ADD_USER_SITE, siteReference(id));
		}
		else
		{
			return unlockCheck(SECURE_ADD_SITE, siteReference(id));
		}
	}

	/**
	 * @inheritDoc
	 */
	public Site addSite(String id, String type) throws IdInvalidException, IdUsedException, PermissionException
	{
		// check for a valid site name
		Validator.checkResourceId(id);

		id = Validator.escapeResourceName(id);

		// check security (throws if not permitted)
		unlock(SECURE_ADD_SITE, siteReference(id));

		// reserve a site with this id from the info store - if it's in use, this will return null
		Site site = m_storage.put(id);
		if (site == null)
		{
			throw new IdUsedException(id);
		}

		// set the type before we enable related, since the azg template for the site depends on type
		if (type != null)
		{
			site.setType(type);
		}
		
		((BaseSite) site).setEvent(SECURE_ADD_SITE);

		doSave((BaseSite) site, true, EntityProducer.ChangeType.ADD);

		return site;
	}

	/**
	 * @inheritDoc
	 */
	public Site addSite(String id, Site other) throws IdInvalidException, IdUsedException, PermissionException
	{
		// check for a valid site name
		Validator.checkResourceId(id);

		id = Validator.escapeResourceName(id);

		// check security (throws if not permitted)
		if (isUserSite(id))
		{
			unlock(SECURE_ADD_USER_SITE, siteReference(id));
		}
		else
		{
			unlock(SECURE_ADD_SITE, siteReference(id));
		}

		// reserve a site with this id from the info store - if it's in use, this will return null
		Site site = m_storage.put(id);
		if (site == null)
		{
			throw new IdUsedException(id);
		}

		// make this site a copy of other, but with new ids (not an exact copy)
		((BaseSite) site).set((BaseSite) other, false);

		// copy the realm (to get permissions settings)
		try
		{
			AuthzGroup realm = AuthzGroupService.getAuthzGroup(other.getReference());
			AuthzGroup re = AuthzGroupService.addAuthzGroup(site.getReference(), realm, UserDirectoryService.getCurrentUser().getId());

			// clear the users from the copied realm, adding in the current user as a maintainer
			re.removeMembers();
			re.addMember(UserDirectoryService.getCurrentUser().getId(), re.getMaintainRole(), true, false);

			AuthzGroupService.save(re);
		}
		catch (Exception e)
		{
			m_logger.warn(this + ".addSite(): error copying realm", e);
		}

		// clear the site's notification id in properties
		site.getPropertiesEdit().removeProperty(ResourceProperties.PROP_SITE_EMAIL_NOTIFICATION_ID);

		((BaseSite) site).setEvent(SECURE_ADD_SITE);

		doSave((BaseSite) site, true, EntityProducer.ChangeType.ADD);

		return site;
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowRemoveSite(String id)
	{
		return unlockCheck(SECURE_REMOVE_SITE, siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public void removeSite(Site site) throws PermissionException
	{
		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_SITE, site.getReference());

		// complete the edit
		m_storage.remove(site);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_SITE, site.getReference(), true));

		// get the services related to this site setup for the site's removal
		disableRelated(site);
	}

	/**
	 * @inheritDoc
	 */
	public String siteReference(String id)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + id;
	}

	/**
	 * @inheritDoc
	 */
	public String sitePageReference(String siteId, String pageId)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + siteId + Entity.SEPARATOR + PAGE_SUBTYPE+ Entity.SEPARATOR + pageId;
	}

	/**
	 * @inheritDoc
	 */
	public String siteToolReference(String siteId, String toolId)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + siteId + Entity.SEPARATOR + TOOL_SUBTYPE + Entity.SEPARATOR + toolId;
	}

	/**
	 * @inheritDoc
	 */
	public String siteGroupReference(String siteId, String groupId)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + siteId + Entity.SEPARATOR + GROUP_SUBTYPE + Entity.SEPARATOR + groupId;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isUserSite(String site)
	{
		if (site == null) return false;

		// deal with a reference
		if (site.startsWith(siteReference("~")) && (!site.equals(siteReference("~")))) return true;

		// deal with an id
		return (site.startsWith("~") && (!site.equals("~")));
	}

	/**
	 * @inheritDoc
	 */
	public String getSiteUserId(String site)
	{
		// deal with a reference
		String ref = siteReference("~");
		if (site.startsWith(ref))
		{
			return site.substring(ref.length());
		}

		else if (site.startsWith("~"))
		{
			return site.substring(1);
		}

		return null;
	}

	/**
	 * @inheritDoc
	 */
	public String getUserSiteId(String userId)
	{
		return "~" + userId;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isSpecialSite(String site)
	{
		if (site == null) return false;

		// Note: ! is special except if it's !admin, not considered special

		// deal with a reference
		if (site.startsWith(siteReference("!")) && !site.equals(siteReference("!admin"))) return true;

		// TODO: legacy code - we don't use the "~" site anymore (!user.template*) -ggolden
		if (site.equals(siteReference("~"))) return true;

		// deal with an id
		if (site.startsWith("!") && !site.equals("!admin")) return true;

		// TODO: legacy code - we don't use the "~" site anymore (!user.template*) -ggolden
		if (site.equals("~")) return true;

		return false;
	}

	/**
	 * @inheritDoc
	 */
	public String getSiteSpecialId(String site)
	{
		// deal with a reference
		String ref = siteReference("!");
		if (site.startsWith(ref))
		{
			return site.substring(ref.length());
		}

		else if (site.startsWith("!"))
		{
			return site.substring(1);
		}

		return null;
	}

	/**
	 * @inheritDoc
	 */
	public String getSpecialSiteId(String special)
	{
		return "!" + special;
	}

	/**
	 * @inheritDoc
	 */
	public String getSiteDisplay(String id)
	{
		String rv = "(" + id + ")";

		if (isUserSite(id))
		{
			String userName = id;
			try
			{
				User user = UserDirectoryService.getUser(getSiteUserId(id));
				userName = user.getDisplayName();
			}
			catch (IdUnusedException ignore)
			{
			}

			rv = "\"" + userName + "'s site\" " + rv;
		}

		else
		{
			Site site = null;
			try
			{
				site = getSite(id);
				rv = "\"" + site.getTitle() + "\" " + rv;
			}
			catch (IdUnusedException ignore)
			{
			}
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public ToolConfiguration findTool(String id)
	{
		ToolConfiguration rv = null;

		// check the site cache
		if (m_siteCache != null)
		{
			rv = m_siteCache.getTool(id);
			if (rv != null)
			{
				// return a copy from the cache
				rv = new BaseToolConfiguration(rv, rv.getContainingPage(), true);
				return rv;
			}

			// if not, get the tool's site id, cache the site, and try again
			String siteId = m_storage.findToolSiteId(id);
			if (siteId != null)
			{
				// read and cache the site, pages, tools, etc.
				try
				{
					Site site = getDefinedSite(siteId);

					// return what we find from the copy we got from the cache
					rv = site.getTool(id);

					return rv;
				}
				catch (IdUnusedException e)
				{
				}
			}

			return null;
		}

		rv = m_storage.findTool(id);

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public SitePage findPage(String id)
	{
		SitePage rv = null;

		// check the site cache
		if (m_siteCache != null)
		{
			rv = m_siteCache.getPage(id);
			if (rv != null)
			{
				rv = new BaseSitePage(rv, rv.getContainingSite(), true);
				return rv;
			}

			// if not, get the page's site id, cache the site, and try again
			String siteId = m_storage.findPageSiteId(id);
			if (siteId != null)
			{
				// read and cache the site, pages, tools
				try
				{
					Site site = getDefinedSite(siteId);

					// return what we find from the site copy from the cache
					rv = site.getPage(id);
					return rv;
				}
				catch (IdUnusedException e)
				{
				}
			}

			return null;
		}

		rv = m_storage.findPage(id);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowViewRoster(String id)
	{
		return unlockCheck(SECURE_VIEW_ROSTER, siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public void join(String id) throws IdUnusedException, PermissionException
	{
		String user = SessionManager.getCurrentSessionUserId();
		if (user == null) throw new PermissionException(user, AuthzGroupService.SECURE_UPDATE_OWN_AUTHZ_GROUP, siteReference(id));

		// get the site
		Site site = getDefinedSite(id);

		// must be joinable
		if (!site.isJoinable())
		{
			throw new PermissionException(user, AuthzGroupService.SECURE_UPDATE_OWN_AUTHZ_GROUP, siteReference(id));
		}

		// the role to assign
		String roleId = site.getJoinerRole();
		if (roleId == null)
		{
			m_logger.warn(this + ".join(): null site joiner role for site: " + id);
			throw new PermissionException(user, AuthzGroupService.SECURE_UPDATE_OWN_AUTHZ_GROUP, siteReference(id));
		}

		// do the join
		AuthzGroupService.joinGroup(siteReference(id), roleId);
	}

	/**
	 * @inheritDoc
	 */
	public void unjoin(String id) throws IdUnusedException, PermissionException
	{
		AuthzGroupService.unjoinGroup(siteReference(id));
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowUnjoinSite(String id)
	{
		// basic unjoin AuthzGroup test
		if (!AuthzGroupService.allowUnjoinGroup(siteReference(id))) return false;
		
		// one more check - don't let a maintain role user unjoin a non-joinable site, or
		// a joinable site that does not have the maintain role as the joiner role.
		try
		{
			// get the site
			Site site = getDefinedSite(id);
			
			// get the AuthGroup
			AuthzGroup azg = AuthzGroupService.getAuthzGroup(siteReference(id));

			String user = SessionManager.getCurrentSessionUserId();
			if (user == null) return false;

			if (		(StringUtil.different(site.getJoinerRole(), azg.getMaintainRole()))
				||	(!site.isJoinable()))
			{
				Role role = azg.getUserRole(user);
				if (role == null)
				{
					return false;
				}
				if (role.getId().equals(azg.getMaintainRole()))
				{
					return false;
				}
			}
		}
		catch (IdUnusedException e)
		{
			return false;
		}
		
		return true;
	}

	/**
	 * @inheritDoc
	 */
	public String getSiteSkin(String id)
	{
		String rv = null;

		// check the site cache
		if (m_siteCache != null)
		{
			try
			{
				// this gets the site from the cache, or reads the site / pages / tools and caches it
				Site s = getDefinedSite(id);
				String skin = adjustSkin(s.getSkin(), s.isPublished());

				return skin;
			}
			catch (IdUnusedException e)
			{
			}

			// if the site's not around, use the default
			return adjustSkin(null, true);
		}

		rv = m_storage.getSiteSkin(id);

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public List getSiteTypes()
	{
		return m_storage.getSiteTypes();
	}

	/**
	 * @inheritDoc
	 */
	public List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort,
			PagingPosition page)
	{
		return m_storage.getSites(type, ofType, criteria, propertyCriteria, sort, page);
	}

	/**
	 * @inheritDoc
	 */
	public int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria)
	{
		return m_storage.countSites(type, ofType, criteria, propertyCriteria);
	}

	/**
	 * @inheritDoc
	 */
	public void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers)
	{
		m_storage.setSiteSecurity(siteId, updateUsers, visitUnpUsers, visitUsers);
		
		// the site's azg may have just been updated, so enforce site group subset membership
		enforceGroupSubMembership(siteId);
	}

	/**
	 * @inheritDoc
	 */
	public void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites)
	{
		m_storage.setUserSecurity(userId, updateSites, visitUnpSites, visitSites);
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * EntityProducer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "site";
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
		return new HttpAccess()
		{
			public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws PermissionException, IdUnusedException, ServerOverloadException
			{
				try
				{
					Site site = (Site) ref.getEntity();
	
					res.setContentType("text/html; charset=UTF-8");
					PrintWriter out = res.getWriter();
	
					out.println("<html><head><style type=" + "\"" + "text/css" + "\"" + ">body{margin:	0px;padding:1em;font-family:Verdana,Arial,Helvetica,sans-serif;font-size:80%;}</style></head><body>");
	
					// get the description - if missing, use the site title
					String description = site.getDescription();
					if (description == null)
					{
						description = site.getTitle();
					}
	
					// make it safe for html
					description = Validator.escapeHtml(description);
	
					out.println(description);
	
					out.println("</body></html>");
				}
				catch (Throwable t)
				{
					throw new IdUnusedException(ref.getReference());
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean parseEntityReference(String reference, Reference ref)
	{
		// for site access
		if (reference.startsWith(REFERENCE_ROOT))
		{
			String id = null;
			String container = null;
			String subType = SITE_SUBTYPE;

			// we will get null, service, siteId, page | group | tool, page/group/tool id
			String[] parts = StringUtil.split(reference, Entity.SEPARATOR);

			if (parts.length > 2)
			{
				id = parts[2];
				container = id;

				if (parts.length > 4)
				{
					subType = parts[3];
					id = parts[4];
				}
			}

			ref.set(SERVICE_NAME, subType, id, container, null);

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

		String rv = "Site: " + ref.getReference();

		try
		{
			Site site = getSite(ref.getId());
			rv = "Site: " + site.getTitle() + " (" + site.getId() + ")\n" + " Created: "
					+ site.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
					+ site.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
					+ site.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
					+ StringUtil.limit((site.getDescription() == null ? "" : site.getDescription()), 30);
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
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Entity rv = null;

		try
		{
			rv = getSite(ref.getId());
		}
		catch (IdUnusedException e)
		{
			m_logger.warn("getEntity(): " + e);
		}
		catch (NullPointerException e)
		{
			m_logger.warn("getEntity(): " + e);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getEntityAuthzGroups(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Collection rv = new Vector();
		
		try
		{
			// first, use the reference as an authzGroup (site, group, page or tool)
			rv.add(ref.getReference());
			
// do NOT use the site if the reference is a group or other part
//			// if this is a sub-type, add the site's reference - container is site id
//			if (!SITE_SUBTYPE.equals(ref.getSubType()))
//			{
//				rv.add(siteReference(ref.getContainer()));
//			}

			// add the current user's realm
			ref.addUserAuthzGroup(rv, SessionManager.getCurrentSessionUserId());

			// site helper
			rv.add("!site.helper");
		}
		catch (Throwable e)
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
		// we are the site, nothing to do!
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Sync up with all other services for a site that exists.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableRelated(BaseSite site, EntityProducer.ChangeType change)
	{
		// skip if special
		if (isSpecialSite(site.getId()))
		{
			return;
		}

		// take care of our AuthzGroups
		enableAzgSecurityAdvisor();
		enableAzg(site);
		SecurityService.clearAdvisors();

		// offer to all EntityProducers
		for (Iterator i = m_entityManager.getEntityProducers().iterator(); i.hasNext();)
		{
			EntityProducer ep = (EntityProducer) i.next();
			ep.syncWithSiteChange(site, change);
		}
	}

	/**
	 * Sync up with all other services for a site that is going away.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableRelated(Site site)
	{
		// skip if special
		if (isSpecialSite(site.getId()))
		{
			return;
		}

		// offer to all EntityProducers
		for (Iterator i = m_entityManager.getEntityProducers().iterator(); i.hasNext();)
		{
			EntityProducer ep = (EntityProducer) i.next();
			ep.syncWithSiteChange(site, EntityProducer.ChangeType.REMOVE);
		}

		// disable the azgs last, so permissions were in place for the above
		enableAzgSecurityAdvisor();
		disableAzg(site);
		SecurityService.clearAdvisors();
	}

	/**
	 * Enable the site and site group AuthzGroups.
	 * @param site The site.
	 */
	protected void enableAzg(BaseSite site)
	{
		// figure the site authorization group template
		String siteAzgTemplate = siteAzgTemplate(site);

		// try the site created-by user for the maintain role in the site
		String userId = site.getCreatedBy().getId();
		if (userId != null)
		{
			// make sure it's valid
			try
			{
				UserDirectoryService.getUser(userId);
			}
			catch (IdUnusedException e1)
			{
				userId = null;
			}
		}
		
		// use the current user if needed
		if (userId == null)
		{
			User user = UserDirectoryService.getCurrentUser();
			userId = user.getId();
		}

		enableAuthorizationGroup(site.getReference(), siteAzgTemplate, userId, "!site.template");

		// figure the group authorization group template
		String groupAzgTemplate = groupAzgTemplate(site);

		// enable a realm for each group: use the same template as for the site, but don't assign a user maintain in the group's azg
		for (Iterator iGroups = site.getGroups().iterator(); iGroups.hasNext();)
		{
			Group group = (Group) iGroups.next();
			enableAuthorizationGroup(group.getReference(), groupAzgTemplate, null, "!group.template");
		}

		// disable the authorization groups for any groups deleted in this edit
		for (Iterator iGroups = site.m_deletedGroups.iterator(); iGroups.hasNext();)
		{
			Group group = (Group) iGroups.next();
			disableAuthorizationGroup(group.getReference());
		}
	}

	/**
	 * Disable the site and site group azgs for a site that's being deleted.
	 * @param site The site.
	 */
	protected void disableAzg(Site site)
	{
		// disable a realm for each group
		for (Iterator iGroups = site.getGroups().iterator(); iGroups.hasNext();)
		{
			Group group = (Group) iGroups.next();
			disableAuthorizationGroup(group.getReference());
		}

		// disable realm last, to keep those permissions around
		disableAuthorizationGroup(site.getReference());
	}

	/**
	 * Figure the site's authorization group template, based on type and if it's a user site.
	 * 
	 * @param site
	 *        The site to figure the realm for.
	 * @return the site's authorization group template, based on type and if it's a user site.
	 */
	protected String siteAzgTemplate(Site site)
	{
		String azgTemplate = null;
		if (isUserSite(site.getId()))
		{
			azgTemplate = "!site.user";
		}
		else
		{
			// use the type's template, if defined
			azgTemplate = "!site.template";
			String type = site.getType();
			if (type != null)
			{
				azgTemplate = azgTemplate + "." + type;
			}
		}

		return azgTemplate;
	}

	/**
	 * Figure the authorization group template for a group of this site, based on type and if it's a user site.
	 * 
	 * @param site
	 *        The site to figure the authorization group templates for.
	 * @return the authorization group template for a group of this site, based on type and if it's a user site.
	 */
	protected String groupAzgTemplate(Site site)
	{
		String azgTemplate = null;
		if (isUserSite(site.getId()))
		{
			azgTemplate = "!group.user";
		}
		else
		{
			// use the type's template, if defined
			azgTemplate = "!group.template";
			String type = site.getType();
			if (type != null)
			{
				azgTemplate = azgTemplate + "." + type;
			}
		}

		return azgTemplate;
	}

	/**
	 * Setup the realm for an active site.
	 * 
	 * @param ref
	 *        The reference for which the realm will be created (site, user).
	 * @param templateId
	 *        The realm id of a template to use for the new realm.
	 * @param userId
	 *        The user to get maintain in this realm.
	 */
	protected void enableAuthorizationGroup(String ref, String templateId, String userId, String fallbackTemplate)
	{
		// see if it exists already
		try
		{
			AuthzGroup realm = AuthzGroupService.getAuthzGroup(ref);
		}
		catch (IdUnusedException un)
		{
			// see if there's a new site AuthzGroup template
			AuthzGroup template = null;
			try
			{
				template = AuthzGroupService.getAuthzGroup(templateId);
			}
			catch (Exception e)
			{
				try
				{
					// if the template is not defined, try the fall back template
					template = AuthzGroupService.getAuthzGroup(fallbackTemplate);
				}
				catch (Exception ee)
				{
				}
			}

			// add the realm
			try
			{
				AuthzGroup realm = null;

				if (template == null)
				{
					realm = AuthzGroupService.addAuthzGroup(ref);
				}
				else
				{
					realm = AuthzGroupService.addAuthzGroup(ref, template, userId);
				}
			}
			catch (Exception e)
			{
				m_logger.warn(this + ".enableRealm: AuthzGroup exception: " + e);
			}
		}
	}

	/**
	 * Remove a site's realm.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableAuthorizationGroup(String ref)
	{
		try
		{
			AuthzGroupService.removeAuthzGroup(ref);
		}
		catch (Exception e)
		{
			m_logger.warn(this + ".removeSite: AuthzGroup exception: " + e);
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Storage
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected interface Storage
	{
		/**
		 * Open and be ready to read / write.
		 */
		public void open();

		/**
		 * Close.
		 */
		public void close();

		/**
		 * Does the site with this id exist?
		 * 
		 * @param id
		 *        The site id.
		 * @return true if the site with this id exists, false if not.
		 */
		public boolean check(String id);

		/**
		 * Get the site with this id, or null if not found.
		 * 
		 * @param id
		 *        The site id.
		 * @return The site with this id, or null if not found.
		 */
		public Site get(String id);

		/**
		 * Get all sites.
		 * 
		 * @return The list of all sites.
		 */
		public List getAll();

		/**
		 * Add a new site with this id.
		 * 
		 * @param id
		 *        The site id.
		 * @return The site with this id, or null if in use.
		 */
		public Site put(String id);

		/**
		 * Save the changes.
		 * 
		 * @param site
		 *        The site to commit.
		 */
		public void save(Site site);

		/**
		 * Save the changes to the two info fields (description and infoUrl) only.
		 * 
		 * @param siteId
		 *        The site to commit.
		 * @param description
		 *        The new site description.
		 * @param infoUrl
		 *        The new site infoUrl.
		 */
		public void saveInfo(String siteId, String description, String infoUrl);

		/**
		 * Remove this site.
		 * 
		 * @param user
		 *        The site to remove.
		 */
		public void remove(Site site);

		/**
		 * Count all the sites.
		 * 
		 * @return The count of all sites.
		 */
		public int count();

		/**
		 * Access a unique list of String site types for any site type defined for any site, sorted by type.
		 * 
		 * @return A list (String) of all used site types.
		 */
		public List getSiteTypes();

		/**
		 * Access a list of Site objets that meet specified criteria.
		 * 
		 * @param type
		 *        The SelectionType specifying what sort of selection is intended.
		 * @param ofType
		 *        Site type criteria: null for any type; a String to match a single type; A String[], List or Set to match any type in the collection.
		 * @param criteria
		 *        Additional selection criteria: sits returned will match this string somewhere in their id, title, description, or skin.
		 * @param propertyCriteria
		 *        Additional selection criteria: sites returned will have a property named to match each key in the map, whose values match (somewhere in their value) the value in the map (may be null or empty).
		 * @param sort
		 *        A SortType indicating the desired sort. For no sort, set to SortType.NONE.
		 * @param page
		 *        The PagePosition subset of items to return.
		 * @return The List (Site) of Site objets that meet specified criteria.
		 */
		public List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort,
				PagingPosition page);

		/**
		 * Count the Site objets that meet specified criteria.
		 * 
		 * @param type
		 *        The SelectionType specifying what sort of selection is intended.
		 * @param ofType
		 *        Site type criteria: null for any type; a String to match a single type; A String[], List or Set to match any type in the collection.
		 * @param criteria
		 *        Additional selection criteria: sits returned will match this string somewhere in their id, title, description, or skin.
		 * @param propertyCriteria
		 *        Additional selection criteria: sites returned will have a property named to match each key in the map, whose values match (somewhere in their value) the value in the map (may be null or empty).
		 * @return The count of Site objets that meet specified criteria.
		 */
		public int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria);

		/**
		 * Access the ToolConfiguration that has this id, if one is defined, else return null. The tool may be on any SitePage in any site.
		 * 
		 * @param id
		 *        The id of the tool.
		 * @return The ToolConfiguration that has this id, if one is defined, else return null.
		 */
		public ToolConfiguration findTool(String id);

		/**
		 * Access the Site id for the tool with this id.
		 * 
		 * @param id
		 *        The id of the tool.
		 * @return The Site id for the tool with this id, if the tool is found, else null.
		 */
		public String findToolSiteId(String id);

		/**
		 * Access the Page that has this id, if one is defined, else return null. The page may be on any Site.
		 * 
		 * @param id
		 *        The id of the page.
		 * @return The SitePage that has this id, if one is defined, else return null.
		 */
		public SitePage findPage(String id);

		/**
		 * Access the Site id for the page with this id.
		 * 
		 * @param id
		 *        The id of the page.
		 * @return The Site id for the page with this id, if the page is found, else null.
		 */
		public String findPageSiteId(String id);

		/**
		 * Read site properties from storage into the site's properties.
		 * 
		 * @param site
		 *        The site for which properties are desired.
		 */
		public void readSiteProperties(Site site, ResourcePropertiesEdit props);

		/**
		 * Read site properties and all page and tool properties for the site from storage.
		 * 
		 * @param site
		 *        The site for which properties are desired.
		 */
		public void readAllSiteProperties(Site site);

		/**
		 * Read page properties from storage into the page's properties.
		 * 
		 * @param page
		 *        The page for which properties are desired.
		 */
		public void readPageProperties(SitePage page, ResourcePropertiesEdit props);

		/**
		 * Read tool configuration from storage into the tool's configuration properties.
		 * 
		 * @param tool
		 *        The tool for which properties are desired.
		 */
		public void readToolProperties(ToolConfiguration tool, Properties props);

		/**
		 * Read group properties from storage into the group's properties.
		 * 
		 * @param groupId
		 *        The groupId for which properties are desired.
		 */
		public void readGroupProperties(Group groupId, Properties props);

		/**
		 * Read site pages from storage into the site's pages.
		 * 
		 * @param site
		 *        The site for which pages are desired.
		 */
		public void readSitePages(Site site, ResourceVector pages);

		/**
		 * Read site page tools from storage into the page's tools.
		 * 
		 * @param page
		 *        The page for which tools are desired.
		 */
		public void readPageTools(SitePage page, ResourceVector tools);

		/**
		 * Read tools for all pages from storage into the site's page's tools.
		 * 
		 * @param site
		 *        The site for which tools are desired.
		 */
		public void readSiteTools(Site site);

		/**
		 * Return the skin for this site
		 * 
		 * @param siteId
		 *        The site id.
		 * @return the skin for this site.
		 */
		public String getSiteSkin(String siteId);

		/**
		 * Establish the internal security for this site. Previous security settings are replaced for this site. Assigning a user with update implies the two reads; assigning a user with unp read implies the other read.
		 * 
		 * @param siteId
		 *        The id of the site.
		 * @param updateUsers
		 *        The set of String User Ids who have update access.
		 * @param visitUnpUsers
		 *        The set of String User Ids who have visit unpublished access.
		 * @param visitUsers
		 *        The set of String User Ids who have visit access.
		 */
		public void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers);

		/**
		 * Establish the internal security for user for all sites. Previous security settings are replaced for this user. Assigning a user with update implies the two reads; assigning a user with unp read implies the other read.
		 * 
		 * @param userId
		 *        The id of the user.
		 * @param updateSites
		 *        The set of String site ids where the user has update access.
		 * @param visitUnpSites
		 *        The set of String site ids where the user has visit unpublished access.
		 * @param visitSites
		 *        The set of String site ids where the user has visit access.
		 */
		public void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites);

		/**
		 * Write an updated tool configuration to the database.
		 * 
		 * @param conn
		 *        Optional connection to use.
		 * @param tool
		 *        TooConfiguration to commit.
		 */
		public void saveToolConfig(Connection conn, ToolConfiguration tool);

		/**
		 * Access the Site id for the group with this id.
		 * 
		 * @param id
		 *        The id of the group.
		 * @return The Site id for the group with this id, if the group is found, else null.
		 */
		public String findGroupSiteId(String id);

		/**
		 * Read site pages from storage into the site's pages.
		 * 
		 * @param site
		 *        The site for which groups are desired.
		 * @param groups
		 *        The Collection to fill in.
		 */
		public void readSiteGroups(Site site, Collection groups);
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * StorageUser implementation
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
		return new BaseSite(id);
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
		return null;
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
		return new BaseSite((Site) other, true);
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
		BaseSite e = new BaseSite(id);
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
		return null;
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
		BaseSite e = new BaseSite((Site) other);
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

	/**
	 * Adjust a skin value to be just a (folder) name, with no extension, and if missing, be null.
	 * 
	 * @param skin
	 *        The skin value to adjust.
	 * @return A defaulted and adjusted skin value.
	 */
	protected String adjustSkin(String skin, boolean published)
	{
		// return the skin as just a name, no ".css", and not dependent on the published status, or a null if not defined
		if (skin == null) return null;

		if (!skin.endsWith(".css")) return skin;

		return skin.substring(0, skin.lastIndexOf(".css"));
	}

	/**
	 * @inheritDoc
	 */
	public String merge(String siteId, Element el, String creatorId)
	{
		StringBuffer msg = new StringBuffer();

		try
		{
			// if the target site already exists, don't change the site attributes
			Site s = getSite(siteId);
		}
		catch (IdUnusedException e)
		{
			try
			{
				// reserve a site with this id from the info store - if it's in use, this will return null
				// check security (throws if not permitted)
				// TODO: why security on add_user_site? -ggolden
				unlock(SECURE_ADD_USER_SITE, siteReference(siteId));

				// reserve a site with this id from the info store - if it's in use, this will return null
				BaseSite site = (BaseSite) m_storage.put(siteId);
				if (site == null)
				{
					msg.append(this + "cannot find site: " + siteId);
				}

				site.setEvent(SECURE_ADD_SITE);

				if (creatorId != null)
				{
					el.setAttribute("created-id", creatorId);
				}

				// assign source site's attributes to the target site
				((BaseSite) site).set(new BaseSite(el), false);

				try
				{
					save(site);					
				}
				catch (Throwable t)
				{
					m_logger.warn(this + ".merge: " + t);
				}
			}
			catch (PermissionException ignore)
			{
			}
		}

		return msg.toString();
	}

	/**
	 * @inheritDoc
	 */
	public Group findGroup(String refOrId)
	{
		Group rv = null;

		// parse the reference or id
		Reference ref = m_entityManager.newReference(refOrId);

		// for ref, get the site from the cache, or cache it and get the group from the site
		if (SERVICE_NAME.equals(ref.getType()))
		{
			try
			{
				// here we return the group from the site, so the group's containing site is really the site that contains it.
				Site site = getDefinedSite(ref.getContainer());	
				rv = site.getGroup(ref.getId());
			}
			// we can ignore a site not found exception, just returning a null Group
			catch (IdUnusedException e) {}
		}

		// for id, check the cache or get the site from storage, then get the group from the site
		else
		{
			// check the site cache
			if (m_siteCache != null)
			{
				// this lets us find the group from an alredy cached site directly, by group id
				Group group = m_siteCache.getGroup(refOrId);
				if (group != null)
				{
					// Here we need to make a copy of the site, and pull the group from there,
					// so that the group's containing site really contains the group we return.
					// The group we get from the siteCache is a group from the actual cached site, so it's containing site is the actual cached site.

					// get a copy of the site from the cache
					Site site = new BaseSite(group.getContainingSite(), true);
					
					// get the group from there
					rv = site.getGroup(refOrId);
				}
			}

			// if we don't have it yet, get the group's site, and the group from there
			if (rv == null)
			{
				String siteId = m_storage.findGroupSiteId(refOrId);
				if (siteId != null)
				{
					try
					{
						// read (and cache if enabled) the full site
						Site site = getDefinedSite(siteId);

						// here we return the group from the site, so the group's containing site is really the site that contains it.
						rv = site.getGroup(refOrId);
					}
					// we can ignore a site not found exception, just returning a null Group
					catch (IdUnusedException e) {}
				}
			}
		}

		return rv;
	}
	
	/**
	 * Adjust any site groups for this site so that the group membership is a subset of the site's membership.
	 * @param siteId The site to adjust.
	 */
	protected void enforceGroupSubMembership(String siteId)
	{
		// just being paranoid, but lets make sure we don't get stuck in a loop here -ggolden
		if (ThreadLocalManager.get("enforceGroupSubMembership") != null)
		{
			m_logger.warn(this + ".enforceGroupSubMembership: recursion avoided!: " + siteId);
			return;
		}
		ThreadLocalManager.set("enforceGroupSubMembership", siteId);

		try
		{
			Site site = getDefinedSite(siteId);
			for (Iterator i = site.getGroups().iterator(); i.hasNext();)
			{
				Group group = (Group) i.next();
				group.keepIntersection(site);
			}

			// save any changed group azg
			enableAzgSecurityAdvisor();
			saveGroupAzgs(site);
			SecurityService.clearAdvisors();
		}
		catch (IdUnusedException e)
		{
			// site not found?
			m_logger.warn(this + ".enforceGroupSubMembership: site not found: " + siteId);
		}
		
		ThreadLocalManager.set("enforceGroupSubMembership", null);
	}
}