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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.component.section.cover.CourseManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.SiteCache;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.calendar.CalendarEdit;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Section;
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
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, resource);
		}
	}

	/**
	 * Update the live properties for a site for when modified.
	 */
	protected void addLiveUpdateProperties(BaseSite site)
	{
		String current = UsageSessionService.getSessionUserId();

		site.m_lastModifiedUserId = current;
		site.m_lastModifiedTime = TimeService.newTime();
	}

	/**
	 * Create the live properties for the site.
	 */
	protected void addLiveProperties(BaseSite site)
	{
		String current = UsageSessionService.getSessionUserId();

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
			if (isUserSite(id) && id.substring(1).equals(UsageSessionService.getSessionUserId()))
			{
				// use lowercase user id inside user's MyWorkspace id
				id = id.toLowerCase();

				// pick a template, type based, to clone it exactly but set this as the id
				BaseSite template = null;
				try
				{
					User user = UserDirectoryService.getUser((UsageSessionService.getSessionUserId()).toLowerCase());
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

					save(site);

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
	public void save(Site site)
	{
		// update the properties
		addLiveUpdateProperties(((BaseSite) site));

		// sync up with all other services
		enableRelated((BaseSite) site);

		// complete the edit
		m_storage.save(site);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(((BaseSite) site).getEvent(), site.getReference(), true));
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
	public Site addSite(String id) throws IdInvalidException, IdUsedException, PermissionException
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

		((BaseSite) site).setEvent(SECURE_ADD_SITE);

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

		((BaseSite) site).setEvent(SECURE_ADD_SITE);

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

		// give it new properties
		addLiveProperties(((BaseSite) site));

		// save the new info
		m_storage.save(site);

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
	public String siteSectionReference(String siteId, String sectionId)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + siteId + Entity.SEPARATOR + SECTION_SUBTYPE + Entity.SEPARATOR + sectionId;
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
		// TODO: fix realm to avoid this try block
		try
		{
			AuthzGroupService.joinSite(id);
		}
		catch (Throwable e)
		{
		}
	}

	/**
	 * @inheritDoc
	 */
	public void unjoin(String id) throws IdUnusedException, PermissionException
	{
		// TODO: fix realm to avoid this try block
		try
		{
			AuthzGroupService.unjoinSite(id);
		}
		catch (Throwable e)
		{
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean allowUnjoinSite(String id)
	{
		return AuthzGroupService.allowUnjoinSite(id);
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
	public boolean parseEntityReference(String reference, Reference ref)
	{
		// for site access
		if (reference.startsWith(REFERENCE_ROOT))
		{
			String id = null;
			String container = null;
			String subType = SITE_SUBTYPE;

			// we will get null, service, siteId, page | section | tool, page/section/tool id
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

		// for site access: user realm
		try
		{
			rv.add(siteReference(ref.getId()));

			// add the current user's realm
			ref.addUserAuthzGroup(rv, UsageSessionService.getSessionUserId());

			// site helper
			rv.add("!site.helper");
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
	 *********************************************************************************************************************************************************************************************************************************************************/

	// TODO: the following enable/disable routines are UGLY here - oh gods of the separation of concerns
	// and modularity forgive me - I will clean this up soon -ggolden
	/**
	 * Sync up with all other services for a site that exists.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableRelated(BaseSite site)
	{
		// skip if special
		if (isSpecialSite(site.getId()))
		{
			return;
		}

		// what features are enabled for the site?
		boolean hasChat = false;
		boolean hasMailbox = false;
		boolean hasResources = false;
		boolean hasDropbox = false;
		boolean hasSchedule = false;
		boolean hasAnnouncements = false;
		boolean hasDiscussion = false;
		boolean hasAssignment = false;
		boolean hasDissertation = false;
		boolean hasGradebook = false;
		for (Iterator iPages = site.getPages().iterator(); iPages.hasNext();)
		{
			SitePage page = (SitePage) iPages.next();

			// for each tool
			for (Iterator iTools = page.getTools().iterator(); iTools.hasNext();)
			{
				ToolConfiguration tool = (ToolConfiguration) iTools.next();

				// check known cases
				if ("sakai.chat".equals(tool.getTool().getId()))
				{
					hasChat = true;
				}
				else if ("sakai.mailbox".equals(tool.getTool().getId()))
				{
					hasMailbox = true;
				}
				else if ("sakai.resources".equals(tool.getTool().getId()))
				{
					hasResources = true;
				}
				else if ("sakai.dropbox".equals(tool.getTool().getId()))
				{
					hasDropbox = true;
				}
				else if ("sakai.schedule".equals(tool.getTool().getId()))
				{
					hasSchedule = true;
				}
				else if ("sakai.announcements".equals(tool.getTool().getId()))
				{
					hasAnnouncements = true;
				}
				else if ("sakai.discussion".equals(tool.getTool().getId()))
				{
					hasDiscussion = true;
				}
				else if ("sakai.assignment".equals(tool.getTool().getId()))
				{
					hasAssignment = true;
				}
				else if ("sakai.threadeddiscussion".equals(tool.getTool().getId()))
				{
					hasDiscussion = true;
				}
				else if ("sakai.dissertation".equals(tool.getTool().getId()))
				{
					hasDissertation = true;
				}
				else if ("sakai.gradebook.tool".equals(tool.getTool().getId()))
				{
					hasGradebook = true;
				}
			}
		}

		// enable features used, disable those not

		// figure the site's realm template
		String realmTemplate = siteRealmTemplate(site);

		// try the site created-by user for the maintain role in the site
		String userId = null;
		if (site.getCreatedBy() != null)
		{
			userId = site.getCreatedBy().getId();
		}

		enableRealm(site.getReference(), realmTemplate, userId, "!site.template");

		// enable a realm for each section: use the same template as for the site
		for (Iterator iSections = site.getSections().iterator(); iSections.hasNext();)
		{
			Section section = (Section) iSections.next();
			enableRealm(section.getReference(), realmTemplate, userId, "!site.template");
		}

		// disable the reams for any sections deleted in this edit
		for (Iterator iSections = site.m_deletedSections.iterator(); iSections.hasNext();)
		{
			Section section = (Section) iSections.next();
			disableRealm(section.getReference());
		}

		if (hasMailbox)
		{
			enableMailbox(site);
		}
		else
		{
			disableMailbox(site);
		}

		if (hasChat)
		{
			enableMessageChannel(site, ChatService.SERVICE_NAME);
		}

		if (hasAnnouncements)
		{
			enableMessageChannel(site, AnnouncementService.SERVICE_NAME);
		}

		if (hasDiscussion)
		{
			enableMessageChannel(site, DiscussionService.SERVICE_NAME);
		}

		if (hasSchedule)
		{
			enableSchedule(site);
		}

		if (hasResources)
		{
			enableResources(site);
		}

		if (hasDropbox)
		{
			enableDropbox(site);
		}

		if (hasGradebook)
		{
			enableGradebook(site);
		}

		// Regardless of whether the section info tool is deployed, the tools
		// relying on SectionAwareness will need this --jholtzman@berkeley.edu
		enableSections(site);

		// %%% others ? -ggolden
	}

	/**
	 * Figure the site's realm template, based on type and if it's a user site.
	 * 
	 * @param site
	 *        The site to figure the realm for.
	 * @return the site's realm template, based on type and if it's a user site.
	 */
	protected String siteRealmTemplate(Site site)
	{
		// figure the site's realm template
		String realmTemplate = null;
		if (isUserSite(site.getId()))
		{
			realmTemplate = "!site.user";
		}
		else
		{
			// use the type's template, if defined
			realmTemplate = "!site.template";
			String type = site.getType();
			if (type != null)
			{
				realmTemplate = realmTemplate + "." + type;
			}
		}

		return realmTemplate;
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

		disableMailbox(site);
		// others %%%

		// disable realm last, to keep those permissions around
		disableRealm(site.getReference());

		// disable a realm for each section
		for (Iterator iSections = site.getSections().iterator(); iSections.hasNext();)
		{
			Section section = (Section) iSections.next();
			disableRealm(section.getReference());
		}
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
	protected void enableRealm(String ref, String templateId, String userId, String fallbackTemplate)
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

			// who is "the user" to get maintain in the realm
			User user = null;
			if (userId != null)
			{
				try
				{
					user = UserDirectoryService.getUser(userId);
				}
				catch (IdUnusedException e1)
				{
					m_logger.warn(this + ".enableRealm: cannot find user for new user site: " + userId);

					// try some user, at least!
					user = UserDirectoryService.getCurrentUser();
				}
			}
			else
			{
				user = UserDirectoryService.getCurrentUser();
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
					realm = AuthzGroupService.addAuthzGroup(ref, template, user.getId());
				}

				// if there's not a maintain role, then the user will not have any realm access to the new realm, so will not be able to proceed...
//				// make sure there's a maintain role, creating it if needed
//				Role role = realm.getRole(realm.getMaintainRole());
//				if (role == null)
//				{
//					role = realm.addRole(realm.getMaintainRole());
//					role.allowFunction(SECURE_UPDATE_SITE);
//					role.allowFunction(SITE_VISIT);
//					role.allowFunction(SITE_VISIT_UNPUBLISHED);
//				}
//
//				if (!realm.hasRole(user.getId(), role.getId()))
//				{
//					realm.addMember(user.getId(), role.getId(), true, false);
//				}
//
//				AuthzGroupService.save(realm);
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
	protected void disableRealm(String ref)
	{
		try
		{
			// delete all at once, so it's not tempted to try to update the realm when it makes the edit -ggolden
			AuthzGroupService.removeAuthzGroup(ref);
		}
		catch (Exception e)
		{
			m_logger.warn(this + ".removeSite: AuthzGroup exception: " + e);
		}

		// %%% do we want to remove all the realms associated with the site's resources? -ggolden
	}

	/**
	 * Setup the mailbox for an active site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableMailbox(Site site)
	{
		// form the email channel name
		String channelRef = MailArchiveService.channelReference(site.getId(), MAIN_CONTAINER);

		// see if there's a channel
		MessageChannel channel = null;
		try
		{
			channel = MailArchiveService.getChannel(channelRef);
		}
		catch (IdUnusedException e)
		{
		}
		catch (PermissionException e)
		{
		}

		// if it exists, make sure it's enabled
		if (channel != null)
		{
			if (channel.getProperties().getProperty(ResourceProperties.PROP_CHANNEL_ENABLED) == null)
			{
				try
				{
					MessageChannelEdit edit = (MessageChannelEdit) MailArchiveService.editChannel(channelRef);
					edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_CHANNEL_ENABLED, "true");
					MailArchiveService.commitChannel(edit);
					channel = edit;
				}
				catch (IdUnusedException ignore)
				{
				}
				catch (PermissionException ignore)
				{
				}
				catch (InUseException ignore)
				{
				}
			}
		}

		// otherwise create it
		else
		{
			try
			{
				// create a channel and mark it as enabled
				MessageChannelEdit edit = MailArchiveService.addMailArchiveChannel(channelRef);
				edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_CHANNEL_ENABLED, "true");
				MailArchiveService.commitChannel(edit);
				channel = edit;
			}
			catch (IdUsedException e)
			{
			}
			catch (IdInvalidException e)
			{
			}
			catch (PermissionException e)
			{
			}
		}
	}

	/**
	 * Set a site's mailbox to inactive - it remains in existance, just disabled
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableMailbox(Site site)
	{
		// form the email channel name
		String channelRef = MailArchiveService.channelReference(site.getId(), MAIN_CONTAINER);

		// see if there's a channel
		MessageChannel channel = null;
		try
		{
			channel = MailArchiveService.getChannel(channelRef);
		}
		catch (IdUnusedException e)
		{
		}
		catch (PermissionException e)
		{
		}

		// if it exists, make sure it's disabled
		if (channel != null)
		{
			if (channel.getProperties().getProperty(ResourceProperties.PROP_CHANNEL_ENABLED) != null)
			{
				try
				{
					MessageChannelEdit edit = (MessageChannelEdit) MailArchiveService.editChannel(channelRef);
					edit.getPropertiesEdit().removeProperty(ResourceProperties.PROP_CHANNEL_ENABLED);
					MailArchiveService.commitChannel(edit);
					channel = edit;
				}
				catch (IdUnusedException ignore)
				{
				}
				catch (PermissionException ignore)
				{
				}
				catch (InUseException ignore)
				{
				}
			}
		}

		// remove any alias
		try
		{
			AliasService.removeTargetAliases(channelRef);
		}
		catch (PermissionException e)
		{
		}
	}

	/**
	 * Setup a message channel.
	 * 
	 * @param site
	 *        The site.
	 * @param serviceId
	 *        The name of the message service.
	 */
	protected void enableMessageChannel(Site site, String serviceId)
	{
		MessageService service = getMessageService(serviceId);

		// form the channel name
		String channelRef = service.channelReference(site.getId(), MAIN_CONTAINER);

		// see if there's a channel
		try
		{
			service.getChannel(channelRef);
		}
		catch (IdUnusedException un)
		{
			try
			{
				// create a channel
				MessageChannelEdit edit = service.addChannel(channelRef);
				service.commitChannel(edit);
			}
			catch (IdUsedException e)
			{
			}
			catch (IdInvalidException e)
			{
			}
			catch (PermissionException e)
			{
			}
		}
		catch (PermissionException e)
		{
		}
	}

	/**
	 * Setup a calendar for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableSchedule(Site site)
	{
		// form the calendar name
		String calRef = CalendarService.calendarReference(site.getId(), MAIN_CONTAINER);

		// see if there's a calendar
		try
		{
			CalendarService.getCalendar(calRef);
		}
		catch (IdUnusedException un)
		{
			try
			{
				// create a calendar
				CalendarEdit edit = CalendarService.addCalendar(calRef);
				CalendarService.commitCalendar(edit);
			}
			catch (IdUsedException e)
			{
			}
			catch (IdInvalidException e)
			{
			}
			catch (PermissionException e)
			{
			}
		}
		catch (PermissionException e)
		{
		}
	}

	/**
	 * Make sure a home in resources exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableResources(Site site)
	{
		// it would be called
		String id = ContentHostingService.getSiteCollection(site.getId());

		// does it exist?
		try
		{
			ContentCollection collection = ContentHostingService.getCollection(id);

			// do we need to update the title?
			if (!site.getTitle().equals(collection.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME)))
			{
				try
				{
					ContentCollectionEdit edit = ContentHostingService.editCollection(id);
					edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, site.getTitle());
					ContentHostingService.commitCollection(edit);
				}
				catch (IdUnusedException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
				catch (PermissionException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
				catch (InUseException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
			}
		}
		catch (IdUnusedException un)
		{
			// make it
			try
			{
				ContentCollectionEdit collection = ContentHostingService.addCollection(id);
				collection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, site.getTitle());
				ContentHostingService.commitCollection(collection);
			}
			catch (IdUsedException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (IdInvalidException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (PermissionException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (InconsistentException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
		}
		catch (TypeException e)
		{
			m_logger.warn(this + ".enableResources: " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this + ".enableResources: " + e);
		}
	}

	/**
	 * Make sure a home in resources for dropbox exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableDropbox(Site site)
	{
		// create it and the user folders within
		Dropbox.createCollection(site.getId());
	}

	/**
	 * Setup the gradebook for an active site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableGradebook(Site site)
	{
		// find the gradebook service
		// org.sakaiproject.service.gradebook.shared.GradebookService service = (org.sakaiproject.service.gradebook.shared.GradebookService) ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService");
		// if (service == null) return;
		//
		// // just to keep us from having a dependency on this optional tool, do this the hard way
		// if (!service.gradebookExists(site.getId())) service.addGradebook(site.getId(), site.getId());

		Object service = ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService");
		if (service == null) return;

		// the method signature
		Class[] signature = new Class[2];
		signature[0] = String.class;
		signature[1] = String.class;

		// the method name
		String methodName = "addGradebook";

		// find a method of this class with this name and signature
		try
		{
			Method method = service.getClass().getMethod(methodName, signature);

			// the parameters
			Object[] args = new Object[2];
			args[0] = site.getId();
			args[1] = site.getId();

			// make the call
			method.invoke(service, args);
		}
		catch (Throwable t)
		{
			m_logger.debug(t.toString());
		}
	}

	protected void enableSections(Site site)
	{
		String siteId = site.getId();
		if (!CourseManager.courseExists(siteId))
		{
			String title = site.getTitle();
			if (m_logger.isInfoEnabled()) m_logger.info("Creating a new section container for site " + siteId);
			CourseManager.createCourse(siteId, title, false, false, false);
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Site implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * <p>
	 * BaseSite is an implementation of the Sakai Site object.
	 * </p>
	 */
	public class BaseSite implements Site
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/** List of sections deleted in this edit pass. */
		protected Collection m_deletedSections = new Vector();

		/** The site id. */
		protected String m_id = null;

		/** The site title. */
		protected String m_title = null;

		/** The site short description. */
		protected String m_shortDescription = null;

		/** The site description. */
		protected String m_description = null;

		/** The name of the role given to users who join a joinable site. */
		protected String m_joinerRole = null;

		/** Is this site joinable. */
		protected boolean m_joinable = false;

		/** Published or not. */
		protected boolean m_published = false;

		/** The icon url. */
		protected String m_icon = null;

		/** The site info url. */
		protected String m_info = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The list of site pages for this site. */
		protected ResourceVector m_pages = null;

		/** Set true while the pages have not yet been read in for a site. */
		protected boolean m_pagesLazy = false;

		/** The skin to use for this site. */
		protected String m_skin = null;

		/** The pubView flag. */
		protected boolean m_pubView = false;

		/** The site type. */
		protected String m_type = null;

		/** The created user id. */
		protected String m_createdUserId = null;

		/** The last modified user id. */
		protected String m_lastModifiedUserId = null;

		/** The time created. */
		protected Time m_createdTime = null;

		/** The time last modified. */
		protected Time m_lastModifiedTime = null;

		/** The list of site sections for this site. */
		protected ResourceVector m_sections = null;

		/** Set true while the sections have not yet been read in for a site. */
		protected boolean m_sectionsLazy = false;

		/**
		 * Construct.
		 * 
		 * @param id
		 *        The site id.
		 */
		public BaseSite(String id)
		{
			m_id = id;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// set up the page list
			m_pages = new ResourceVector();

			// set up the sections collection
			m_sections = new ResourceVector();

			// if the id is not null (a new site, rather than a reconstruction)
			// add the automatic (live) properties
			if (m_id != null) addLiveProperties(this);
		}

		/**
		 * Construct from another Site, exact.
		 * 
		 * @param site
		 *        The other site to copy values from.
		 * @param exact
		 *        If true, we copy ids - else we generate new ones for site, page and tools.
		 */
		public BaseSite(Site other)
		{
			BaseSite bOther = (BaseSite) other;
			set(bOther, true);
		}

		/**
		 * Construct from another Site.
		 * 
		 * @param site
		 *        The other site to copy values from.
		 * @param exact
		 *        If true, we copy ids - else we generate new ones for site, page and tools.
		 */
		public BaseSite(Site other, boolean exact)
		{
			BaseSite bOther = (BaseSite) other;
			set(bOther, exact);
		}

		/**
		 * Construct from an existing definition, in xml.
		 * 
		 * @param el
		 *        The message in XML in a DOM element.
		 */
		public BaseSite(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// setup for page list
			m_pages = new ResourceVector();

			// setup for the sections list
			m_sections = new ResourceVector();

			m_id = el.getAttribute("id");
			m_title = StringUtil.trimToNull(el.getAttribute("title"));

			// description might be encripted
			m_description = StringUtil.trimToNull(el.getAttribute("description"));
			if (m_description == null)
			{
				m_description = StringUtil.trimToNull(Xml.decodeAttribute(el, "description-enc"));
			}

			// short description might be encripted
			m_shortDescription = StringUtil.trimToNull(el.getAttribute("short-description"));
			if (m_shortDescription == null)
			{
				m_shortDescription = StringUtil.trimToNull(Xml.decodeAttribute(el, "short-description-enc"));
			}

			m_joinable = Boolean.valueOf(el.getAttribute("joinable")).booleanValue();
			m_joinerRole = StringUtil.trimToNull(el.getAttribute("joiner-role"));

			String published = StringUtil.trimToNull(el.getAttribute("published"));
			if (published == null)
			{
				// read the old "status" (this file 1.42 and before) 1-un 2-pub
				published = StringUtil.trimToNull(el.getAttribute("status"));
				if (published != null)
				{
					published = Boolean.valueOf("2".equals(published)).toString();
				}
			}

			m_published = Boolean.valueOf(published).booleanValue();

			m_icon = StringUtil.trimToNull(el.getAttribute("icon"));
			m_info = StringUtil.trimToNull(el.getAttribute("info"));
			m_skin = StringUtil.trimToNull(el.getAttribute("skin"));

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

			// get pubView setting - but old versions (pre 1.42 of this file) won't have it and will have a property instead
			String pubViewValue = StringUtil.trimToNull(el.getAttribute("pubView"));

			// get the type - but old versions (pre 1.42 of this file) won't have it and will have a property instead
			String typeValue = StringUtil.trimToNull(el.getAttribute("type"));

			// the children (properties and page list)
			NodeList children = el.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
				Element element = (Element) child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);

					// look for pubview (pre 1.42 of this file) in properties
					if (pubViewValue == null)
					{
						pubViewValue = m_properties.getProperty("CTNG:site-include");
						if (pubViewValue == null)
						{
							pubViewValue = m_properties.getProperty("site-include");
						}
					}
					m_properties.removeProperty("CTNG:site-include");
					m_properties.removeProperty("site-include");

					// look for type (pre 1.42 of this file) in properties (two possibilities)
					if (typeValue == null)
					{
						typeValue = m_properties.getProperty("SAKAI:site-type");
						if (typeValue == null)
						{
							typeValue = m_properties.getProperty("CTNG:site-type");
						}
					}
					m_properties.removeProperty("SAKAI:site-type");
					m_properties.removeProperty("CTNG:site-type");

					// look for short description (pre 1.42 of this file) in properties
					if (m_shortDescription == null)
					{
						m_shortDescription = m_properties.getProperty("CTNG:short-description");

						if (m_shortDescription == null)
						{
							m_shortDescription = m_properties.getProperty("short-description");
						}
					}
					m_properties.removeProperty("CTNG:short-description");
					m_properties.removeProperty("short-description");

					// pull out some properties into fields to convert old (pre 1.42) versions
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
				}

				// look for the page list
				else if (element.getTagName().equals("pages"))
				{
					NodeList pagesNodes = element.getChildNodes();
					for (int p = 0; p < pagesNodes.getLength(); p++)
					{
						Node pageNode = pagesNodes.item(p);
						if (pageNode.getNodeType() != Node.ELEMENT_NODE) continue;
						Element pageEl = (Element) pageNode;
						if (!pageEl.getTagName().equals("page")) continue;

						BaseSitePage page = new BaseSitePage(pageEl, this);
						m_pages.add(page);
					}

					// TODO: else if ( "sections")
				}
			}

			// set the pubview, now it's found in either the attribute or the properties
			if (pubViewValue != null)
			{
				m_pubView = Boolean.valueOf(pubViewValue).booleanValue();
			}
			else
			{
				m_pubView = false;
			}

			// set the type, now it's found in either the attribute or the properties
			m_type = typeValue;
		}

		/**
		 * ReConstruct.
		 * 
		 * @param id
		 * @param title
		 * @param type
		 * @param shortDesc
		 * @param description
		 * @param iconUrl
		 * @param infoUrl
		 * @param skin
		 * @param published
		 * @param joinable
		 * @param pubView
		 * @param joinRole
		 * @param isSpecial
		 * @param isUser
		 * @param createdBy
		 * @param createdOn
		 * @param modifiedBy
		 * @param modifiedOn
		 */
		public BaseSite(String id, String title, String type, String shortDesc, String description, String iconUrl, String infoUrl,
				String skin, boolean published, boolean joinable, boolean pubView, String joinRole, boolean isSpecial,
				boolean isUser, String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// set up the page list
			m_pages = new ResourceVector();

			// set up the sections collection
			m_sections = new ResourceVector();

			m_id = id;
			m_title = title;
			m_type = type;
			m_shortDescription = shortDesc;
			m_description = description;
			m_icon = iconUrl;
			m_info = infoUrl;
			m_skin = skin;
			m_published = published;
			m_joinable = joinable;
			m_pubView = pubView;
			m_joinerRole = joinRole;
			// TODO: isSpecial
			// TODO: isUser
			m_createdUserId = createdBy;
			m_lastModifiedUserId = modifiedBy;
			m_createdTime = createdOn;
			m_lastModifiedTime = modifiedOn;

			// setup for properties, but mark them lazy since we have not yet established them from data
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_pagesLazy = true;
			m_sectionsLazy = true;
		}

		/**
		 * Set me to be a deep copy of other (all but my id.)
		 * 
		 * @param bOther
		 *        the other to copy.
		 * @param exact
		 *        If true, we copy ids - else we generate new ones for site, page and tools.
		 */
		protected void set(BaseSite other, boolean exact)
		{
			// if exact, set the id, else assume the id was already set
			if (exact)
			{
				m_id = other.m_id;
			}

			m_title = other.m_title;
			m_shortDescription = other.m_shortDescription;
			m_description = other.m_description;
			m_joinable = other.m_joinable;
			m_joinerRole = other.m_joinerRole;
			m_published = other.m_published;
			m_icon = other.m_icon;
			m_info = other.m_info;
			m_skin = other.m_skin;
			m_type = other.m_type;
			m_pubView = other.m_pubView;
			if (exact)
			{
				m_createdUserId = other.m_createdUserId;
			}
			else
			{
				m_createdUserId = UserDirectoryService.getCurrentUser().getId();
			}
			m_lastModifiedUserId = other.m_lastModifiedUserId;
			if (other.m_createdTime != null) m_createdTime = (Time) other.m_createdTime.clone();
			if (other.m_lastModifiedTime != null) m_lastModifiedTime = (Time) other.m_lastModifiedTime.clone();

			m_properties = new BaseResourcePropertiesEdit();
			ResourceProperties pOther = other.getProperties();
			Iterator l = pOther.getPropertyNames();
			while (l.hasNext())
			{
				String pOtherName = (String) l.next();
				m_properties.addProperty(pOtherName, pOther.getProperty(pOtherName).replaceAll(other.getId(), getId()));
			}
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());

			// deep copy the pages
			m_pages = new ResourceVector();
			for (Iterator iPages = other.getPages().iterator(); iPages.hasNext();)
			{
				BaseSitePage page = (BaseSitePage) iPages.next();
				m_pages.add(new BaseSitePage(page, this, exact));
			}
			m_pagesLazy = other.m_pagesLazy;

			// deep copy the sections
			m_sections = new ResourceVector();
			for (Iterator iSections = other.getSections().iterator(); iSections.hasNext();)
			{
				Section section = (Section) iSections.next();
				m_sections.add(new BaseSection(section, this, exact));
			}
			m_sectionsLazy = other.m_sectionsLazy;
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			if (m_id == null) return "";
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public String getUrl()
		{
			return m_serverConfigurationService.getPortalUrl() + "/site/" + m_id;
		}

		/**
		 * @inheritDoc
		 */
		public String getReference()
		{
			return siteReference(m_id);
		}

		/**
		 * @inheritDoc
		 */
		public ResourceProperties getProperties()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				m_storage.readSiteProperties(this, m_properties);
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
			}

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
		 * @inheritDoc
		 */
		public String getTitle()
		{
			// if set here, use the setting
			if (m_title != null) return m_title;

			// if not otherwise set, use the id
			return getId();
		}

		/**
		 * @inheritDoc
		 */
		public String getShortDescription()
		{
			return m_shortDescription;
		}

		/**
		 * @inheritDoc
		 */
		public String getDescription()
		{
			return m_description;
		}

		/**
		 * @inheritDoc
		 */
		public boolean isJoinable()
		{
			return m_joinable;
		}

		/**
		 * @inheritDoc
		 */
		public String getJoinerRole()
		{
			return m_joinerRole;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isPublished()
		{
			return m_published;
		}

		/**
		 * @inheritDoc
		 */
		public String getSkin()
		{
			return m_skin;
		}

		/**
		 * @inheritDoc
		 */
		public String getIconUrl()
		{
			return m_icon;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getIconUrlFull()
		{
			return convertReferenceUrl(m_icon);
		}

		/**
		 * @inheritDoc
		 */
		public String getInfoUrl()
		{
			return m_info;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getInfoUrlFull()
		{
			if (m_info == null) return null;

			return convertReferenceUrl(m_info);
		}

		/**
		 * {@inheritDoc}
		 */
		public List getPages()
		{
			if (m_pagesLazy)
			{
				m_storage.readSitePages(this, m_pages);
				m_pagesLazy = false;
			}

			return m_pages;
		}

		/**
		 * {@inheritDoc}
		 */
		public Collection getSections()
		{
			if (m_sectionsLazy)
			{
				m_storage.readSiteSections(this, m_sections);
				m_sectionsLazy = false;
			}

			return m_sections;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSections()
		{
			Collection sections = getSections();
			return !sections.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public void loadAll()
		{
			// first, pages
			getPages();

			// next, tools from all pages, all at once
			m_storage.readSiteTools(this);

			// get sections, all at once
			getSections();

			// now all properties
			m_storage.readAllSiteProperties(this);
		}

		/**
		 * {@inheritDoc}
		 */
		public List getOrderedPages()
		{
			List order = m_serverConfigurationService.getToolOrder(getType());
			if (order.isEmpty()) return getPages();

			// get a copy we can modify without changing the site!
			List pages = new Vector(getPages());

			// find any pages that include the tool type for each tool in the ordering, move them into the newOrder and remove from the old
			List newOrder = new Vector();

			// for each entry in the order
			for (Iterator i = order.iterator(); i.hasNext();)
			{
				String toolId = (String) i.next();

				// find any pages that have this tool
				for (Iterator p = pages.iterator(); p.hasNext();)
				{
					SitePage page = (SitePage) p.next();
					List tools = page.getTools();
					for (Iterator t = tools.iterator(); t.hasNext();)
					{
						ToolConfiguration tool = (ToolConfiguration) t.next();
						if (tool.getTool().getId().equals(toolId))
						{
							// this page has this tool, so move it from the pages to the newOrder
							newOrder.add(page);
							p.remove();
							break;
						}
					}
				}
			}

			// add any remaining
			newOrder.addAll(pages);

			return newOrder;
		}

		/**
		 * {@inheritDoc}
		 */
		public SitePage getPage(String id)
		{
			return (SitePage) ((ResourceVector) getPages()).getById(id);
		}

		/**
		 * {@inheritDoc}
		 */
		public ToolConfiguration getTool(String id)
		{
			// search the pages
			for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
			{
				SitePage page = (SitePage) iPages.next();
				ToolConfiguration tool = page.getTool(id);

				if (tool != null) return tool;
			}

			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Section getSection(String id)
		{
			return (Section) ((ResourceVector) getSections()).getById(id);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getType()
		{
			return m_type;
		}

		/**
		 * @inheritDoc
		 */
		public boolean isType(Object type)
		{
			if (type == null) return true;

			String myType = getType();

			if (type instanceof String[])
			{
				for (int i = 0; i < ((String[]) type).length; i++)
				{
					String test = ((String[]) type)[i];
					if ((test != null) && (test.equals(myType)))
					{
						return true;
					}
				}
			}

			else if (type instanceof Collection)
			{
				return ((Collection) type).contains(myType);
			}

			else if (type instanceof String)
			{
				return type.equals(myType);
			}

			return false;
		}

		/**
		 * @inheritDoc
		 */
		public boolean equals(Object obj)
		{
			if (obj instanceof Site)
			{
				return ((Site) obj).getId().equals(getId());
			}

			// compare to strings as id
			if (obj instanceof String)
			{
				return ((String) obj).equals(getId());
			}

			return false;
		}

		/**
		 * @inheritDoc
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}

		/**
		 * @inheritDoc
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Site)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// start the compare by comparing their sort names
			int compare = getTitle().compareTo(((Site) obj).getTitle());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((Site) obj).getId());
			}

			return compare;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isPubView()
		{
			return m_pubView;
		}

		/**
		 * {@inheritDoc}
		 */
		public Element toXml(Document doc, Stack stack)
		{
			Element site = doc.createElement("site");
			if (stack.isEmpty())
			{
				doc.appendChild(site);
			}
			else
			{
				((Element) stack.peek()).appendChild(site);
			}

			site.setAttribute("id", getId());
			if (m_title != null) site.setAttribute("title", m_title);

			// encode the short description
			if (m_shortDescription != null) Xml.encodeAttribute(site, "short-description-enc", m_shortDescription);

			// encode the description
			if (m_description != null) Xml.encodeAttribute(site, "description-enc", m_description);

			site.setAttribute("joinable", new Boolean(m_joinable).toString());
			if (m_joinerRole != null) site.setAttribute("joiner-role", m_joinerRole);
			site.setAttribute("published", Boolean.valueOf(m_published).toString());
			if (m_icon != null) site.setAttribute("icon", m_icon);
			if (m_info != null) site.setAttribute("info", m_info);
			if (m_skin != null) site.setAttribute("skin", m_skin);
			site.setAttribute("pubView", Boolean.valueOf(m_pubView).toString());
			site.setAttribute("type", m_type);

			site.setAttribute("created-id", m_createdUserId);
			site.setAttribute("modified-id", m_lastModifiedUserId);
			site.setAttribute("created-time", m_createdTime.toString());
			site.setAttribute("modified-time", m_lastModifiedTime.toString());

			// properties
			stack.push(site);
			getProperties().toXml(doc, stack);
			stack.pop();

			// site pages
			Element list = doc.createElement("pages");
			site.appendChild(list);
			stack.push(list);
			for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
			{
				BaseSitePage page = (BaseSitePage) iPages.next();
				page.toXml(doc, stack);
			}
			stack.pop();

			// TODO: site sections

			return site;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setTitle(String title)
		{
			m_title = StringUtil.trimToNull(title);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setShortDescription(String shortDescripion)
		{
			m_shortDescription = StringUtil.trimToNull(shortDescripion);
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
		public void setJoinable(boolean joinable)
		{
			m_joinable = joinable;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setJoinerRole(String role)
		{
			m_joinerRole = role;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setPublished(boolean published)
		{
			m_published = published;

		}

		/**
		 * {@inheritDoc}
		 */
		public void setSkin(String skin)
		{
			m_skin = skin;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setIconUrl(String url)
		{
			m_icon = StringUtil.trimToNull(url);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setInfoUrl(String url)
		{
			m_info = StringUtil.trimToNull(url);
		}

		/**
		 * {@inheritDoc}
		 */
		public SitePage addPage()
		{
			BaseSitePage page = new BaseSitePage(this);
			getPages().add(page);

			return page;
		}

		/**
		 * @inheritDoc
		 */
		public void removePage(SitePage page)
		{
			getPages().remove(page);
		}

		/**
		 * Access the event code for this edit.
		 * 
		 * @return The event code for this edit.
		 */
		protected String getEvent()
		{
			return m_event;
		}

		/**
		 * Set the event code for this edit.
		 * 
		 * @param event
		 *        The event code for this edit.
		 */
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		 * @inheritDoc
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				m_storage.readSiteProperties(this, m_properties);
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
			}

			return m_properties;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setType(String type)
		{
			m_type = type;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setPubView(boolean pubView)
		{
			m_pubView = pubView;

		}

		/**
		 * Enable editing.
		 */
		protected void activate()
		{
			m_active = true;
		}

		/**
		 * @inheritDoc
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
		 * @inheritDoc
		 */
		public void regenerateIds()
		{
			// deep copy the pages
			ResourceVector newPages = new ResourceVector();
			for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
			{
				BaseSitePage page = (BaseSitePage) iPages.next();
				newPages.add(new BaseSitePage(page, this, false));
			}

			m_pages = newPages;
		}

		/**
		 * {@inheritDoc}
		 */
		public Section addSection()
		{
			Section rv = new BaseSection(this);
			m_sections.add(rv);

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeSection(Section section)
		{
			// remove it
			m_sections.remove(section);

			// track so we can clean up related on commit
			m_deletedSections.add(section);
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * SitePage implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseSitePage implements SitePage, Identifiable
	{
		/** The title. */
		protected String m_title = null;

		/** The layout. */
		protected int m_layout = LAYOUT_SINGLE_COL;

		/** The site id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** the list of tool configurations for this SitePage */
		protected ResourceVector m_tools = null;

		/** false while the page's tools have not yet been read in. */
		protected boolean m_toolsLazy = false;

		/** Active flag. */
		protected boolean m_active = false;

		/** The site I belong to. */
		protected BaseSite m_site = null;

		/** The site id I belong to, in case I have no m_site. */
		protected transient String m_siteId = null;

		/** The site skin, in case I have no m_site. */
		protected transient String m_skin = null;

		/**
		 * Construct. Auto-generate the id.
		 * 
		 * @param site
		 *        The site in which this page lives.
		 */
		protected BaseSitePage(Site site)
		{
			m_site = (BaseSite) site;
			m_id = IdService.getUniqueId();
			m_properties = new BaseResourcePropertiesEdit();
			m_tools = new ResourceVector();
		}

		/**
		 * ReConstruct
		 * 
		 * @param site
		 *        The site in which this page lives.
		 * @param id
		 *        The page id.
		 * @param title
		 *        The page title.
		 * @param layout
		 *        The layout as a string ("0" or not currently supported).
		 */
		protected BaseSitePage(Site site, String id, String title, String layout)
		{
			m_site = (BaseSite) site;
			m_id = id;

			m_properties = new BaseResourcePropertiesEdit();
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_tools = new ResourceVector();
			m_toolsLazy = true;

			m_title = title;

			if (layout.equals(String.valueOf(LAYOUT_SINGLE_COL)))
				m_layout = LAYOUT_SINGLE_COL;
			else if (layout.equals(String.valueOf(LAYOUT_DOUBLE_COL))) m_layout = LAYOUT_DOUBLE_COL;
		}

		/**
		 * ReConstruct - if we don't have a site to follow up to get to certain site info.
		 * 
		 * @param site
		 *        The site in which this page lives.
		 * @param id
		 *        The page id.
		 * @param title
		 *        The page title.
		 * @param layout
		 *        The layout as a string ("0" or not currently supported).
		 */
		protected BaseSitePage(String pageId, String title, String layout, String siteId, String skin)
		{
			m_site = null;
			m_id = pageId;

			m_properties = new BaseResourcePropertiesEdit();
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_tools = new ResourceVector();
			m_toolsLazy = true;

			m_title = title;

			if (layout.equals(String.valueOf(LAYOUT_SINGLE_COL)))
				m_layout = LAYOUT_SINGLE_COL;
			else if (layout.equals(String.valueOf(LAYOUT_DOUBLE_COL))) m_layout = LAYOUT_DOUBLE_COL;

			m_siteId = siteId;
			m_skin = skin;
		}

		/**
		 * Construct as a copy of another.
		 * 
		 * @param other
		 *        The other to copy.
		 * @param site
		 *        The site in which this page lives.
		 * @param exact
		 *        If true, we copy ids - else we generate new ones for page and tools.
		 */
		protected BaseSitePage(SitePage other, Site site, boolean exact)
		{
			BaseSitePage bOther = (BaseSitePage) other;

			m_site = (BaseSite) site;

			if (exact)
			{
				m_id = bOther.m_id;
			}
			else
			{
				m_id = IdService.getUniqueId();
			}
			m_title = bOther.m_title;
			m_layout = bOther.m_layout;

			m_properties = new BaseResourcePropertiesEdit();
			ResourceProperties pOther = other.getProperties();
			Iterator l = pOther.getPropertyNames();
			while (l.hasNext())
			{
				String pOtherName = (String) l.next();
				// TODO: why this replaceAll? When is the site id in a page property? if exact, it's a big waste... - ggolden
				m_properties.addProperty(pOtherName, pOther.getProperty(pOtherName).replaceAll(bOther.getSiteId(), getSiteId()));
			}
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());

			// deep copy the tools
			m_tools = new ResourceVector();
			for (Iterator iTools = bOther.getTools().iterator(); iTools.hasNext();)
			{
				BaseToolConfiguration tool = (BaseToolConfiguration) iTools.next();
				m_tools.add(new BaseToolConfiguration(tool, this, exact));
			}
			m_toolsLazy = ((BaseSitePage) other).m_toolsLazy;

			m_siteId = bOther.m_siteId;
			m_skin = bOther.m_skin;
		}

		/**
		 * Construct from XML element.
		 * 
		 * @param el
		 *        The XML element.
		 * @param site
		 *        The site in which this page lives.
		 */
		protected BaseSitePage(Element el, Site site)
		{
			m_site = (BaseSite) site;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// setup for page list
			m_tools = new ResourceVector();

			m_id = el.getAttribute("id");
			m_title = StringUtil.trimToNull(el.getAttribute("title"));
			try
			{
				m_layout = Integer.parseInt(StringUtil.trimToNull(el.getAttribute("layout")));
			}
			catch (Exception e)
			{
			}

			// the children (properties and page list)
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

				// look for the tool list
				else if (element.getTagName().equals("tools"))
				{
					NodeList toolsNodes = element.getChildNodes();
					for (int t = 0; t < toolsNodes.getLength(); t++)
					{
						Node toolNode = toolsNodes.item(t);
						if (toolNode.getNodeType() != Node.ELEMENT_NODE) continue;
						Element toolEl = (Element) toolNode;
						if (!toolEl.getTagName().equals("tool")) continue;

						BaseToolConfiguration tool = new BaseToolConfiguration(toolEl, this);
						m_tools.add(tool);
					}
				}
			}
		}

		/**
		 * @inheritDoc
		 */
		public String getTitle()
		{
			return m_title;
		}

		/**
		 * @inheritDoc
		 */
		public int getLayout()
		{
			return m_layout;
		}

		/**
		 * @inheritDoc
		 */
		public String getSkin()
		{
			if (m_site != null)
			{
				return adjustSkin(m_site.getSkin(), m_site.isPublished());
			}

			return m_skin;
		}

		/**
		 * @inheritDoc
		 */
		public String getSiteId()
		{
			if (m_site != null)
			{
				return m_site.getId();
			}

			return m_siteId;
		}

		/**
		 * @inheritDoc
		 */
		public boolean isPopUp()
		{
			// TODO:
			return false;
		}

		/**
		 * @inheritDoc
		 */
		public String getLayoutTitle()
		{
			return LAYOUT_NAMES[m_layout];
		}

		/**
		 * @inheritDoc
		 */
		public List getTools()
		{
			if (m_toolsLazy)
			{
				m_storage.readPageTools(this, m_tools);
				m_toolsLazy = false;
			}

			// TODO: need to sort by layout hint
			return m_tools;
		}

		/**
		 * @inheritDoc
		 */
		public List getTools(int col)
		{
			// TODO: need to sort by layout hint
			List rv = new Vector();
			for (Iterator iTools = getTools().iterator(); iTools.hasNext();)
			{
				ToolConfiguration tc = (ToolConfiguration) iTools.next();
				// row, col
				int[] layout = tc.parseLayoutHints();
				if (layout != null)
				{
					if (layout[1] == col)
					{
						rv.add(tc);
					}
				}
				// else consider it part of the 0 column
				else if (col == 0)
				{
					rv.add(tc);
				}
			}
			return rv;
		}

		/**
		 * @inheritDoc
		 */
		public ToolConfiguration getTool(String id)
		{
			return (ToolConfiguration) ((ResourceVector) getTools()).getById(id);
		}

		/**
		 * @inheritDoc
		 */
		public void setTitle(String title)
		{
			m_title = StringUtil.trimToNull(title);
		}

		/**
		 * @inheritDoc
		 */
		public void setLayout(int layout)
		{
			if ((layout == LAYOUT_SINGLE_COL) || (layout == LAYOUT_DOUBLE_COL))
			{
				m_layout = layout;
			}
			else
				m_logger.warn(this + ".setLayout(): set to invalid value: " + layout);
		}

		/**
		 * @inheritDoc
		 */
		public ToolConfiguration addTool()
		{
			BaseToolConfiguration tool = new BaseToolConfiguration(this);
			((ResourceVector) getTools()).add(tool);

			return tool;
		}

		/**
		 * @inheritDoc
		 */
		public ToolConfiguration addTool(Tool reg)
		{
			BaseToolConfiguration tool = new BaseToolConfiguration(reg, this);
			((ResourceVector) getTools()).add(tool);

			return tool;
		}

		/**
		 * @inheritDoc
		 */
		public void removeTool(ToolConfiguration tool)
		{
			((ResourceVector) getTools()).remove(tool);
		}

		/**
		 * @inheritDoc
		 */
		public void moveUp()
		{
			if (m_site == null) return;
			((ResourceVector) m_site.getPages()).moveUp(this);
		}

		/**
		 * @inheritDoc
		 */
		public void moveDown()
		{
			if (m_site == null) return;
			((ResourceVector) m_site.getPages()).moveDown(this);
		}

		/**
		 * @inheritDoc
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				m_storage.readPageProperties(this, m_properties);
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
			}

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
		 * @inheritDoc
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
		 * @inheritDoc
		 */
		public String getUrl()
		{
			String rv = null;
			if (m_site == null)
			{
				rv = m_serverConfigurationService.getPortalUrl() + sitePageReference(m_siteId, m_id);
			}

			rv = m_serverConfigurationService.getPortalUrl() + sitePageReference(m_site.getId(), m_id);

			return rv;
		}

		/**
		 * @inheritDoc
		 */
		public String getReference()
		{
			if (m_site == null)
			{
				return sitePageReference(m_siteId, m_id);
			}

			return sitePageReference(m_site.getId(), m_id);
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public Site getContainingSite()
		{
			return m_site;
		}

		/**
		 * @inheritDoc
		 */
		public ResourceProperties getProperties()
		{
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				m_storage.readPageProperties(this, m_properties);
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
			}

			return m_properties;
		}

		/**
		 * @inheritDoc
		 */
		public Element toXml(Document doc, Stack stack)
		{
			Element page = doc.createElement("page");
			((Element) stack.peek()).appendChild(page);

			page.setAttribute("id", getId());
			if (m_title != null) page.setAttribute("title", m_title);
			page.setAttribute("layout", Integer.toString(m_layout));

			// properties
			stack.push(page);
			getProperties().toXml(doc, stack);
			stack.pop();

			// tools
			Element list = doc.createElement("tools");
			page.appendChild(list);
			stack.push(list);
			for (Iterator iTools = getTools().iterator(); iTools.hasNext();)
			{
				BaseToolConfiguration tool = (BaseToolConfiguration) iTools.next();
				tool.toXml(doc, stack);
			}
			stack.pop();

			return page;
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ToolConfiguration implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseToolConfiguration extends org.sakaiproject.util.Placement implements ToolConfiguration, Identifiable
	{
		/** The layout hints. */
		protected String m_layoutHints = null;

		/** The SitePage I belong to. */
		protected BaseSitePage m_page = null;

		/** The site id I belong to, in case I have no m_page. */
		protected transient String m_siteId = null;

		/** The page id I belong to, in case I have no m_page. */
		protected transient String m_pageId = null;

		/** The site skin, in case I have no m_page. */
		protected transient String m_skin = null;

		/** True if the placement conf has not been read yet. */
		protected transient boolean m_configLazy = false;

		/** The order within the page. */
		protected transient int m_pageOrder = -1;

		/**
		 * ReConstruct
		 * 
		 * @param page
		 *        The page in which this tool lives.
		 * @param id
		 *        The tool (placement) id.
		 * @param toolId
		 *        The id (registration code) of the tool to place here.
		 * @param title
		 *        The tool title.
		 * @param layoutHints
		 *        The layout hints.
		 * @param pageOrder
		 *        The order within the page.
		 */
		protected BaseToolConfiguration(SitePage page, String id, String toolId, String title, String layoutHints, int pageOrder)
		{
			super(id, ToolManager.getTool(toolId), null, null, title);

			m_page = (BaseSitePage) page;
			m_layoutHints = layoutHints;
			m_pageOrder = pageOrder;

			m_configLazy = true;
		}

		/**
		 * ReConstruct - if we don't have a page to follow up to get to certain page and site info.
		 * 
		 * @param id
		 *        The tool (placement) id.
		 * @param toolId
		 *        The id (registration code) of the tool to place here.
		 * @param title
		 *        The tool title.
		 * @param layoutHints
		 *        The layout hints.
		 * @param pageId
		 *        The page id in which this tool lives.
		 * @param siteId
		 *        The site id in which this tool lives.
		 * @param skin
		 *        The site's skin.
		 * @param pageOrder
		 *        The order within the page.
		 */
		protected BaseToolConfiguration(String id, String toolId, String title, String layoutHints, String pageId, String siteId,
				String skin, int pageOrder)
		{
			super(id, ToolManager.getTool(toolId), null, null, title);

			m_page = null;

			m_layoutHints = layoutHints;
			m_pageId = pageId;
			m_siteId = siteId;
			m_skin = skin;
			m_pageOrder = pageOrder;

			m_configLazy = true;
		}

		/**
		 * Construct as a copy of another.
		 * 
		 * @param other
		 *        The other to copy.
		 * @param page
		 *        The page in which this tool lives.
		 * @param exact
		 *        If true, we copy ids - else we generate a new one.
		 */
		protected BaseToolConfiguration(ToolConfiguration other, SitePage page, boolean exact)
		{
			m_page = (BaseSitePage) page;
			BaseToolConfiguration bOther = (BaseToolConfiguration) other;

			if (exact)
			{
				m_id = other.getId();
			}
			else
			{
				m_id = IdService.getUniqueId();
			}
			m_tool = other.getTool();
			m_title = other.getTitle();
			m_layoutHints = other.getLayoutHints();
			m_pageId = bOther.m_pageId;
			m_pageOrder = bOther.m_pageOrder;
			m_siteId = bOther.m_siteId;
			m_skin = bOther.m_skin;

			m_config.putAll(other.getPlacementConfig());
			m_configLazy = bOther.m_configLazy;
		}

		/**
		 * Construct using a tool registration for default information.
		 * 
		 * @param reg
		 *        The tool registration.
		 * @param page
		 *        The page in which this tool lives.
		 */
		protected BaseToolConfiguration(SitePage page)
		{
			super(IdService.getUniqueId(), null, null, null, null);

			m_page = (BaseSitePage) page;
		}

		/**
		 * Construct using a tool registration for default information.
		 * 
		 * @param reg
		 *        The tool registration.
		 * @param page
		 *        The page in which this tool lives.
		 */
		protected BaseToolConfiguration(Tool reg, SitePage page)
		{
			super(IdService.getUniqueId(), reg, null, null, null);

			m_page = (BaseSitePage) page;
		}

		/**
		 * Construct from XML element.
		 * 
		 * @param el
		 *        The XML element.
		 * @param page
		 *        The page in which this tool lives.
		 */
		protected BaseToolConfiguration(Element el, SitePage page)
		{
			super();

			m_page = (BaseSitePage) page;

			m_id = el.getAttribute("id");
			String toolId = StringUtil.trimToNull(el.getAttribute("toolId"));
			if (toolId != null)
			{
				m_tool = ToolManager.getTool(toolId);
			}
			m_title = StringUtil.trimToNull(el.getAttribute("title"));
			m_layoutHints = StringUtil.trimToNull(el.getAttribute("layoutHints"));

			// the children (properties)
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
					Xml.xmlToProperties(m_config, element);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Properties getPlacementConfig()
		{
			// if the config has not yet been read, read it
			if (m_configLazy)
			{
				m_storage.readToolProperties(this, m_config);
				m_configLazy = false;
			}

			return m_config;
		}

		/**
		 * Acces the m_config, which is inherited and not visible to this package outside this class -ggolden
		 */
		protected Properties getMyConfig()
		{
			return m_config;
		}

		/**
		 * @inheritDoc
		 */
		public String getLayoutHints()
		{
			return m_layoutHints;
		}

		/**
		 * @inheritDoc
		 */
		public int[] parseLayoutHints()
		{
			try
			{
				if (m_layoutHints == null) return null;
				String[] parts = StringUtil.split(m_layoutHints, ",");
				if (parts.length < 2) return null;
				int[] rv = new int[2];
				rv[0] = Integer.parseInt(parts[0]);
				rv[1] = Integer.parseInt(parts[1]);
				return rv;
			}
			catch (Throwable t)
			{
				return null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public int getPageOrder()
		{
			return m_pageOrder;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getSkin()
		{
			// use local copy if no page is set
			if (m_page == null)
			{
				return m_skin;
			}

			return m_page.getSkin();
		}

		/**
		 * {@inheritDoc}
		 */
		public String getPageId()
		{
			// use local copy if no page is set
			if (m_page == null)
			{
				return m_pageId;
			}

			return getContainingPage().getId();
		}

		/**
		 * {@inheritDoc}
		 */
		public String getSiteId()
		{
			// use local copy if no page is set
			if (m_page == null)
			{
				return m_siteId;
			}

			return getContainingPage().getContainingSite().getId();
		}

		/**
		 * {@inheritDoc}
		 */
		public String getContext()
		{
			// the context of a site based placement is the site id
			return getSiteId();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setLayoutHints(String hints)
		{
			m_layoutHints = hints;
		}

		/**
		 * {@inheritDoc}
		 */
		public void moveUp()
		{
			if (m_page == null)
			{
				m_logger.warn(this + ".moveUp: null page: " + m_id);
				return;
			}

			((ResourceVector) m_page.getTools()).moveUp(this);
		}

		/**
		 * {@inheritDoc}
		 */
		public void moveDown()
		{
			if (m_page == null)
			{
				m_logger.warn(this + ".moveDown: null page: " + m_id);
				return;
			}

			((ResourceVector) m_page.getTools()).moveDown(this);
		}

		/**
		 * {@inheritDoc}
		 */
		public SitePage getContainingPage()
		{
			return m_page;
		}

		/**
		 * {@inheritDoc}
		 */
		public Element toXml(Document doc, Stack stack)
		{
			Element element = doc.createElement("tool");
			((Element) stack.peek()).appendChild(element);
			stack.push(element);

			element.setAttribute("id", getId());
			if (m_tool != null) element.setAttribute("toolId", m_tool.getId());
			if (m_title != null) element.setAttribute("title", m_title);
			if (m_layoutHints != null) element.setAttribute("layoutHints", m_layoutHints);

			// properties
			Xml.propertiesToXml(getPlacementConfig(), doc, stack);

			stack.pop();

			return (Element) element;
		}

		/**
		 * {@inheritDoc}
		 */
		public void save()
		{
			// TODO: security? version?
			m_storage.saveToolConfig(null, this);

			// track the site change
			EventTrackingService.post(EventTrackingService.newEvent(SECURE_UPDATE_SITE, siteReference(getSiteId()), true));
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Section implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseSection implements Section, Identifiable
	{
		/** The title. */
		protected String m_title = null;

		/** The description. */
		protected String m_description = null;

		/** The site id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The site I belong to. */
		protected BaseSite m_site = null;

		/** The site id I belong to, in case I have no m_site. */
		protected transient String m_siteId = null;

		/**
		 * Construct. Auto-generate the id.
		 * 
		 * @param site
		 *        The site in which this page lives.
		 */
		protected BaseSection(Site site)
		{
			m_site = (BaseSite) site;
			m_id = IdService.getUniqueId();
			m_properties = new BaseResourcePropertiesEdit();
		}

		protected BaseSection(String id, String title, String description, Site site)
		{
			m_id = id;
			m_title = title;
			m_description = description;
			m_site = (BaseSite) site;
			m_properties = new BaseResourcePropertiesEdit();
		}

		protected BaseSection(String id, String title, String description, String siteId)
		{
			m_id = id;
			m_title = title;
			m_description = description;
			m_siteId = siteId;
			m_properties = new BaseResourcePropertiesEdit();
		}

		/**
		 * Construct as a copy of another.
		 * 
		 * @param other
		 *        The other to copy.
		 * @param site
		 *        The site in which this section lives.
		 * @param exact
		 *        If true, we copy id - else we generate a new one.
		 */
		protected BaseSection(Section other, Site site, boolean exact)
		{
			BaseSection bOther = (BaseSection) other;

			m_site = (BaseSite) site;
			m_siteId = bOther.m_siteId;

			if (exact)
			{
				m_id = bOther.m_id;
			}
			else
			{
				m_id = IdService.getUniqueId();
			}

			m_title = bOther.m_title;
			m_description = bOther.m_description;

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());
		}

		/**
		 * @inheritDoc
		 */
		public String getTitle()
		{
			return m_title;
		}

		/**
		 * @inheritDoc
		 */
		public String getDescription()
		{
			return m_description;
		}

		/**
		 * @inheritDoc
		 */
		public Site getContainingSite()
		{
			return m_site;
		}

		/**
		 * @inheritDoc
		 */
		public String getSiteId()
		{
			if (m_site != null)
			{
				return m_site.getId();
			}
			
			return m_siteId;
		}

		/**
		 * @inheritDoc
		 */
		public void setTitle(String title)
		{
			m_title = title;
		}

		/**
		 * @inheritDoc
		 */
		public void setDescription(String description)
		{
			m_description = description;
		}

		/**
		 * @inheritDoc
		 */
		public String getUrl()
		{
			return null;
		}

		/**
		 * @inheritDoc
		 */
		public String getReference()
		{
			if (m_site != null)
			{
				return siteSectionReference(m_site.getId(), getId());
			}
			
			return siteSectionReference(m_siteId, getId());
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public ResourceProperties getProperties()
		{
			return m_properties;
		}

		/**
		 * @inheritDoc
		 */
		public Element toXml(Document doc, Stack stack)
		{
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * @inheritDoc
		 */
		public boolean isActiveEdit()
		{
			return true;
		}

		/**
		 * @inheritDoc
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;
		}
		
		/**
		 * @inheritDoc
		 */
		public String toString()
		{
			return m_title + " (" + m_id + ")";
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
		 * Read section properties from storage into the section's properties.
		 * 
		 * @param section
		 *        The section for which properties are desired.
		 */
		public void readSectionProperties(Section section, Properties props);

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
		 * Access the Section that has this id, if one is defined, else return null. The section may be in any Site.
		 * 
		 * @param id
		 *        The id of the section.
		 * @return The Section that has this id, if one is defined, else return null.
		 */
		public Section findSection(String id);

		/**
		 * Access the Site id for the section with this id.
		 * 
		 * @param id
		 *        The id of the section.
		 * @return The Site id for the section with this id, if the section is found, else null.
		 */
		public String findSectionSiteId(String id);

		/**
		 * Read site pages from storage into the site's pages.
		 * 
		 * @param site
		 *        The site for which pages are desired.
		 * @param sections
		 *        The Collection to fill in.
		 */
		public void readSiteSections(Site site, Collection sections);
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
	 * Improves performance by returning the appropriate MessageService through the service Cover classes instead of through the ComponentManager (for certain well-known services)
	 * 
	 * @param ifaceName
	 */
	private static final MessageService getMessageService(String ifaceName)
	{
		if (!ComponentManager.CACHE_MESSAGE_SERVICES) return (MessageService) ComponentManager.get(ifaceName);
		if (ifaceName.equals(ChatService.SERVICE_NAME))
			return ChatService.getInstance();
		else if (ifaceName.equals(AnnouncementService.SERVICE_NAME))
			return AnnouncementService.getInstance();
		else if (ifaceName.equals(DiscussionService.SERVICE_NAME))
			return DiscussionService.getInstance();
		else if (ifaceName.equals(MailArchiveService.SERVICE_NAME))
			return MailArchiveService.getInstance();
		else
			return (MessageService) ComponentManager.get(ifaceName);
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

				save(site);
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
	public Section findSection(String sectionRefOrId)
	{
		Section rv = null;

		// parse the reference or id
		Reference ref = m_entityManager.newReference(sectionRefOrId);

		// for ref, get the site from the cache, or cache it and get the section from the site
		if (SERVICE_NAME.equals(ref.getType()))
		{
			try
			{
				Site site = getDefinedSite(ref.getContainer());	
				rv = site.getSection(ref.getId());
			}
			catch (IdUnusedException e) {}
		}

		// for id, check the cache or get the section from storage
		else
		{
			// check the site cache
			if (m_siteCache != null)
			{
				rv = m_siteCache.getSection(sectionRefOrId);
				if (rv != null)
				{
					// make a copy from the cache
					rv = new BaseSection(rv, rv.getContainingSite(), true);
				}

				else
				{
					// if not, get the section's site id, cache the site, and try again
					String siteId = m_storage.findSectionSiteId(sectionRefOrId);
					if (siteId != null)
					{
						try
						{
							// read and cache the site, pages, tools
							Site site = getDefinedSite(siteId);
	
							// find in the copy we get from the cache
							rv = site.getSection(sectionRefOrId);
						}
						catch (IdUnusedException e) {}
					}
				}
			}

			else
			{
				rv = m_storage.findSection(sectionRefOrId);
			}
		}

		return rv;
	}
}
