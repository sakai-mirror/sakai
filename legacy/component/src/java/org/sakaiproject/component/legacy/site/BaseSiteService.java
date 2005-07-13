/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/site/BaseSiteService.java,v 1.18 2005/05/25 03:52:26 zqian.umich.edu Exp $
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

import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;

import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.calendar.CalendarEdit;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.RoleEdit;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SitePageEdit;
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
 * BaseSiteService is a base implementation of the SiteService .
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public abstract class BaseSiteService implements SiteService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

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
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	 * Access the site id extracted from a site reference.
	 * 
	 * @param ref
	 *        The site reference string.
	 * @return The the site id extracted from a site reference.
	 */
	protected String siteId(String ref)
	{
		String start = getAccessPoint(true) + Resource.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1) return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // siteId

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

	} // unlockCheck

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

	} // unlock

	/**
	 * Update the live properties for a site for when modified.
	 */
	protected void addLiveUpdateProperties(BaseSite site)
	{
		String current = UsageSessionService.getSessionUserId();

		site.m_lastModifiedUserId = current;
		site.m_lastModifiedTime = TimeService.newTime();

	} //  addLiveUpdateProperties

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

	} //  addLiveProperties

	/**
	 * Return the url unchanged, unless it's a reference, then return the reference url
	 */
	protected String convertReferenceUrl(String url)
	{
		// make a reference
		Reference ref = new Reference(url);

		// if it didn't recognize this, return it unchanged
		if (ref.getType() == null) return url;

		// return the reference's url
		return ref.getUrl();

	} // convertReferenceUrl

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

	/**
	 * Regenerate the page and tool ids for all sites.
	 */
	protected void regenerateAllSiteIds()
	{
		List sites = m_storage.getAll();
		for (Iterator iSites = sites.iterator(); iSites.hasNext();)
		{
			Site site = (Site) iSites.next();
			SiteEdit edit = m_storage.edit(site.getId());
			if (site != null)
			{
				edit.regenerateIds();
				m_storage.commit(edit);

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

			m_logger.info(this + ".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
		}

	} // init

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
	 * check permissions for accessing (i.e. visiting) a site
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to access the site, false if not.
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

	} // allowAccessSite

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

		Site site = m_storage.get(id);

		// if not found
		if (site == null) throw new IdUnusedException(id);

		// track it - we don't track site access -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(SECURE_ACCESS_SITE, site.getReference()));

		return site;

	} // getDefinedSite

	/**
	 * Access a site object. If thie site is a user site, and it's not defined yet, create it if possible.
	 * 
	 * @param id
	 *        The site id string.
	 * @return A site object containing the site information
	 * @exception IdUnusedException
	 *            if not found
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
				// pick a template, type based, to clone it exactly but set this as the id
				BaseSite template = null;
				try
				{
					User user = UserDirectoryService.getUser(UsageSessionService.getSessionUserId());
					template = (BaseSite) getDefinedSite(USER_SITE_TEMPLATE + "." + user.getType());
				}
				catch (Throwable t) {}
				
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
					BaseSiteEdit site = (BaseSiteEdit) m_storage.put(id);
					if (site == null)
					{
						throw new IdUsedException(id);
					}

					site.setEvent(SECURE_ADD_SITE);

					// copy in the template
					site.set(template, false);

					commitEdit(site);

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
	 * Access a site object for purposes of having the user visit the site - visitation permissions are in effect.
	 * 
	 * @param id
	 *        The site id string.
	 * @return A site object containing the site information.
	 * @exception IdUnusedException
	 *            if not found.
	 * @exception PermissionException
	 *            if the current user does not have permission to visit this site.
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
	 * check permissions for updating a site (i.s. using getSiteEditClone() and calling commitEditClone())
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to update the site, false if not.
	 */
	public boolean allowUpdateSite(String id)
	{
		return unlockCheck(SECURE_UPDATE_SITE, siteReference(id));

	} // allowUpdateSite

	/**
	 * Get a locked site object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	 * 
	 * @param id
	 *        The site id string.
	 * @return A SiteEdit object for editing.
	 * @exception IdUnusedException
	 *            if not found, or if not an SiteEdit object
	 * @exception PermissionException
	 *            if the current user does not have permission to mess with this site.
	 * @exception InUseException
	 *            if the site is being edited by another user.
	 */
	public SiteEdit editSite(String id) throws IdUnusedException, PermissionException, InUseException
	{
		String ref = siteReference(id);

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_SITE, ref);

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the site with a lock from the info store
		SiteEdit site = m_storage.edit(id);
		if (site == null) throw new InUseException(id);

		((BaseSiteEdit) site).setEvent(SECURE_UPDATE_SITE);

		// force the site to be fully read - no more lazy!
		((BaseSiteEdit) site).readFully();

		return site;

	} // editSite

	/**
	 * Commit the changes made to a SiteEdit object, and release the lock.
	 * 
	 * @param user
	 *        The SiteEdit object to commit.
	 */
	public void commitEdit(SiteEdit site)
	{
		// check for closed edit
		if (!site.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this + ".commitEdit(): closed SiteEdit", e);
			}
			return;
		}

		// update the properties
		addLiveUpdateProperties(((BaseSite) site));

		// sync up with all other services
		enableRelated(site);

		// complete the edit
		m_storage.commit(site);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(((BaseSiteEdit) site).getEvent(), site.getReference(), true));

		// close the edit object
		((BaseSiteEdit) site).closeEdit();

	} // commitEdit

	/**
	 * Cancel the changes made to a SiteEdit object, and release the lock.
	 * 
	 * @param user
	 *        The SiteEdit object to cancel.
	 */
	public void cancelEdit(SiteEdit site)
	{
		// check for closed edit
		if (!site.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this + ".cancelEdit(): closed SiteEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancel(site);

		// close the edit object
		((BaseSiteEdit) site).closeEdit();

	} // cancelEdit

	/**
	 * check permissions for addSite().
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to addSite(id), false if not.
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

	} // allowAddSite

	/**
	 * Add a new site to the directory. Must commitEdit() to make official, or cancelEdit() when done!
	 * 
	 * @param id
	 *        The site id.
	 * @return The new locked site object.
	 * @exception IdInvalidException
	 *            if the site id is invalid.
	 * @exception IdUsedException
	 *            if the site id is already used.
	 * @exception PermissionException
	 *            if the current user does not have permission to add a site.
	 */
	public SiteEdit addSite(String id) throws IdInvalidException, IdUsedException, PermissionException
	{
		// check for a valid site name
		Validator.checkResourceId(id);

		id = Validator.escapeResourceName(id);

		// check security (throws if not permitted)
		unlock(SECURE_ADD_SITE, siteReference(id));

		// reserve a site with this id from the info store - if it's in use, this will return null
		SiteEdit site = m_storage.put(id);
		if (site == null)
		{
			throw new IdUsedException(id);
		}

		((BaseSiteEdit) site).setEvent(SECURE_ADD_SITE);

		return site;

	} // addSite

	/**
	 * Add a new site. Will be structured just like <other>. Must commitEdit() to make official, or cancelEdit() when done!
	 * 
	 * @param id
	 *        The site id.
	 * @param other
	 *        The site to make this site a structural copy of.
	 * @return The new locked site object.
	 * @exception IdInvalidException
	 *            if the site id is invalid.
	 * @exception IdUsedException
	 *            if the site id is already used.
	 * @exception PermissionException
	 *            if the current site does not have permission to add a site.
	 */
	public SiteEdit addSite(String id, Site other) throws IdInvalidException, IdUsedException, PermissionException
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
		SiteEdit site = m_storage.put(id);
		if (site == null)
		{
			throw new IdUsedException(id);
		}

		((BaseSiteEdit) site).setEvent(SECURE_ADD_SITE);

		// make this site a copy of other, but with new ids (not an exact copy)
		((BaseSite) site).set((BaseSite) other, false);

		// copy the realm (to get permissions settings)
		try
		{
			Realm realm = RealmService.getRealm(other.getReference());
			RealmEdit re = RealmService.addRealm(site.getReference(), realm);

			// clear the users from the copied realm, adding in the current user as a maintainer
			re.removeUsers();
			re.addUserRole(UserDirectoryService.getCurrentUser().getId(), re.getMaintainRole(), true, false);

			RealmService.commitEdit(re);
		}
		catch(Exception e)
		{
			m_logger.warn(this + ".addSite(): error copying realm", e);
		}

		// clear the site's notification id in properties
		site.getPropertiesEdit().removeProperty(ResourceProperties.PROP_SITE_EMAIL_NOTIFICATION_ID);

		// give it new properties
		addLiveProperties(((BaseSite) site));

		return site;

	} // addSite

	/**
	 * check permissions for removeSite().
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to removeSite(id), false if not.
	 */
	public boolean allowRemoveSite(String id)
	{
		return unlockCheck(SECURE_REMOVE_SITE, siteReference(id));

	} // allowRemoveSite

	/**
	 * Remove this site's information from the directory - it must be a site with a lock from editSite(). The SiteEdit is disabled, and not to be used after this call.
	 * 
	 * @param id
	 *        The site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to remove this site.
	 */
	public void removeSite(SiteEdit site) throws PermissionException
	{
		// check for closed edit
		if (!site.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this + ".removeSite(): closed SiteEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_SITE, site.getReference());

		// complete the edit
		m_storage.remove(site);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_SITE, site.getReference(), true));

		// get the services related to this site setup for the site's removal
		disableRelated(site);

		// close the edit object
		((BaseSiteEdit) site).closeEdit();

	} // removeSite

	/**
	 * Access the internal reference which can be used to access the site from within the system.
	 * 
	 * @param id
	 *        The site id.
	 * @return The the internal reference which can be used to access the site from within the system.
	 */
	public String siteReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;

	} // siteReference

	/**
	 * Access the internal reference which can be used to access the site page from within the system.
	 * 
	 * @param siteId
	 *        The site id.
	 * @param pageId
	 *        The page id.
	 * @return The the internal reference which can be used to access the site page from within the system.
	 */
	public String sitePageReference(String siteId, String pageId)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + siteId + Resource.SEPARATOR + "page" + Resource.SEPARATOR + pageId;

	} // sitePageReference

	/**
	 * Access the internal reference which can be used to access the site tool from within the system.
	 * 
	 * @param siteId
	 *        The site id.
	 * @param toolId
	 *        The tool id.
	 * @return The the internal reference which can be used to access the site tool from within the system.
	 */
	public String siteToolReference(String siteId, String toolId)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + siteId + Resource.SEPARATOR + "tool" + Resource.SEPARATOR + toolId;

	} // siteToolReference

	/**
	 * Is this site (id or reference) a user site? Note, /site/~ and ~ are NOT considered user sites.
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return true if this is a user site, false if not.
	 */
	public boolean isUserSite(String site)
	{
		if (site == null) return false;

		// deal with a reference
		if (site.startsWith(siteReference("~")) && (!site.equals(siteReference("~")))) return true;

		// deal with an id
		return (site.startsWith("~") && (!site.equals("~")));

	} // isUserSite

	/**
	 * Extract the user id for this user site from the site id or reference.
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return The user id associated with this site.
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

	} // getSiteUserId

	/*
	 * Form the site id for this user's site. @param userId The user id. @return The site id for this user's site.
	 */
	public String getUserSiteId(String userId)
	{
		return "~" + userId;

	} // getUserSiteId

	/**
	 * Is this site (id or reference) a special site?
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return true if this is a special site, false if not.
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

	} // isSpecialSite

	/**
	 * Extract the special id for this special site from the site id or reference.
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return The special id associated with this site.
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

	} // getSiteSpecialId

	/**
	 * Form the site id for this special site.
	 * 
	 * @param special
	 *        The special id.
	 * @return The site id for this user's site.
	 */
	public String getSpecialSiteId(String special)
	{
		return "!" + special;

	} // getUserSiteId

	/**
	 * Form a display of the site title and id for this site.
	 * 
	 * @param id
	 *        The site id.
	 * @return A display of the site title and id for this site.
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

	} // getSiteDisplay

	/**
	 * Access the ToolConfiguration that has this id, if one is defined, else return null. The tool may be on any SitePage in the site.
	 * 
	 * @param id
	 *        The id of the tool.
	 * @return The ToolConfiguration that has this id, if one is defined, else return null.
	 */
	public ToolConfiguration findTool(String id)
	{
		return m_storage.findTool(id);

	} // findTool

	/**
	 * {@inheritDoc}
	 */
	public SitePage findPage(String id)
	{
		return m_storage.findPage(id);
	}

	/**
	 * check permissions for viewing project site participants
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to addSite(id), false if not.
	 */
	public boolean allowViewRoster(String id)
	{
		return unlockCheck(SECURE_VIEW_ROSTER, siteReference(id));

	} // allowViewRoster

	/**
	 * Cause the current user to join the site as defined by the site's joinable flag and joiner role.
	 * 
	 * @param id
	 *        The site id.
	 * @throws IdUnusedException
	 *         if the id is not a valid site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to join this site.
	 * @exception InUseException
	 *            if the site is otherwise being edited.
	 */
	public void join(String id) throws IdUnusedException, PermissionException, InUseException
	{
		RealmService.joinSite(id);
	}

	/**
	 * Cause the current user to unjoin the site, removing all role relationships.
	 * 
	 * @param id
	 *        The site id.
	 * @throws IdUnusedException
	 *         if the id is not a valid site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to unjoin this site.
	 * @exception InUseException
	 *            if the site is otherwise being edited.
	 */
	public void unjoin(String id) throws IdUnusedException, PermissionException, InUseException
	{
		RealmService.unjoinSite(id);
	}

	/**
	 * check permissions for unjoin() - unjoining the site and removing all role relationships.
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to unjoin(id), false if not.
	 */
	public boolean allowUnjoinSite(String id)
	{
		return RealmService.allowUnjoinSite(id);
	}

	/**
	 * Compute the skin to use for the (optional) site specified in the id parameter. If no site specified, or if the site has no skin defined, use the configured default skin.
	 * 
	 * @param id
	 *        The (optional) site id.
	 * @return A skin to use for this site.
	 */
	public String getSiteSkin(String id)
	{
		String skin = m_storage.getSiteSkin(id);

		return skin;
	}

	/**
	 * {@inheritDoc}
	 */
	public List getSiteTypes()
	{
		return m_storage.getSiteTypes();
	}

	public List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort,
			PagingPosition page)
	{
		return m_storage.getSites(type, ofType, criteria, propertyCriteria, sort, page);
	}

	public int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria)
	{
		return m_storage.countSites(type, ofType, criteria, propertyCriteria);
	}

	/**
	 * Establish the internal security for this site. Previous security settings are replaced for this site. Assigning a user with update implies the two reads; assigning a user with unp read implies the other read.
	 * 
	 * @param siteId
	 *        The id of the site.
	 * @param updateUsers
	 *        The set of User objects who have update access.
	 * @param visitUnpUsers
	 *        The set of User objects who have visit unpublished access.
	 * @param visitUsers
	 *        The set of User objects who have visit access.
	 */
	public void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers)
	{
		m_storage.setSiteSecurity(siteId, updateUsers, visitUnpUsers, visitUsers);
	}

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
	public void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites)
	{
		m_storage.setUserSecurity(userId, updateSites, visitUnpSites, visitSites);
	}

	// TODO: the following enable/disable routines are UGLY here - oh gods of the separation of concerns
	// and modularity forgive me - I will clean this up soon -ggolden

	/**
	 * Sync up with all other services for a site that exists.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableRelated(SiteEdit site)
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

		// the site's realm
		String realmTemplate = null;
		String userId = null;
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

			try
			{
				Realm realm = RealmService.getRealm(site.getReference());
			}
			catch (IdUnusedException un)
			{
				if (site.getCreatedBy() != null)
				{
					userId = site.getCreatedBy().getId();
				}
			}
		}
		enableRealm(site.getReference(), realmTemplate, userId, "!site.template");

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

		// %%% others ? -ggolden

	} // enableRelated

	/**
	 * Sync up with all other services for a site that is going away.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableRelated(SiteEdit site)
	{
		// skip if special
		if (isSpecialSite(site.getId()))
		{
			return;
		}

		disableMailbox(site);
		// others %%%

		// disable realm last, to keep those permissions around
		disableRealm(site);

	} // disableRelated

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
			Realm realm = RealmService.getRealm(ref);
		}
		catch (IdUnusedException un)
		{
			// see if there's a new site Realm template
			Realm template = null;
			try
			{
				template = RealmService.getRealm(templateId);
			}
			catch (Exception e)
			{
				try
				{
					// if the template is not defined, try the fall back template
					template = RealmService.getRealm(fallbackTemplate);
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
				RealmEdit realm = null;

				if (template == null)
				{
					realm = RealmService.addRealm(ref);
				}
				else
				{
					realm = RealmService.addRealm(ref, template);
				}

				// make sure there's a maintain role, creating it if needed
				RoleEdit role = realm.getRoleEdit(realm.getMaintainRole());
				if (role == null)
				{
					role = realm.addRole(realm.getMaintainRole());
					role.add(SECURE_UPDATE_SITE);
					role.add(SITE_VISIT);
					role.add(SITE_VISIT_UNPUBLISHED);
				}

				if (!realm.hasRole(user.getId(), role.getId()))
				{
					realm.addUserRole(user.getId(), role.getId(), true, false);
				}

				RealmService.commitEdit(realm);
			}
			catch (Exception e)
			{
				m_logger.warn(this + ".enableRealm: Realm exception: " + e);
			}
		}

	} // enableRealm

	/**
	 * Remove a site's realm.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableRealm(Site site)
	{
		try
		{
			// delete all at once, so it's not tempted to try to update the realm when it makes the edit -ggolden
			RealmService.removeRealm(site.getReference());
		}
		catch (Exception e)
		{
			m_logger.warn(this + ".removeSite: Realm exception: " + e);
		}

		// %%% do we want to remove all the realms associated with the site's resources? -ggolden

	} // disableRealm

	/**
	 * Setup the mailbox for an active site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableMailbox(SiteEdit site)
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

	} // enableMailbox

	/**
	 * Set a site's mailbox to inactive - it remains in existance, just disabled
	 * 
	 * @param site
	 *        The site.
	 */
	protected void disableMailbox(SiteEdit site)
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

	} // disableMailbox

	/**
	 * Setup a message channel.
	 * 
	 * @param site
	 *        The site.
	 * @param serviceId
	 *        The name of the message service.
	 */
	protected void enableMessageChannel(SiteEdit site, String serviceId)
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

	} // enableMessageChannel

	/**
	 * Setup a calendar for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableSchedule(SiteEdit site)
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

	} // enableSchedule

	/**
	 * Make sure a home in resources exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableResources(SiteEdit site)
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

	} // enableResources

	/**
	 * Make sure a home in resources for dropbox exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableDropbox(SiteEdit site)
	{
		// create it and the user folders within
		Dropbox.createCollection(site.getId());

	} // enableDropbox

	/**
	 * Setup the gradebook for an active site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableGradebook(SiteEdit site)
	{
		// find the gradebook service
//		org.sakaiproject.service.gradebook.shared.GradebookService service = (org.sakaiproject.service.gradebook.shared.GradebookService) ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService");
//		if (service == null) return;
//
//		// just to keep us from having a dependency on this optional tool, do this the hard way
//		if (!service.gradebookExists(site.getId())) service.addGradebook(site.getId(), site.getId());

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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Site implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * <p>
	 * BaseSite is an implementation of the CHEF Site object.
	 * </p>
	 */
	public class BaseSite implements Site
	{
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

			// if the id is not null (a new site, rather than a reconstruction)
			// add the automatic (live) properties
			if (m_id != null) addLiveProperties(this);

		} // BaseSite

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

		} // BaseSite

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

						BaseSitePageEdit page = new BaseSitePageEdit(pageEl, this);
						m_pages.add(page);
					}
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

		} // BaseSite

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
			m_createdUserId = other.m_createdUserId;
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
				BaseSitePageEdit page = (BaseSitePageEdit) iPages.next();
				m_pages.add(new BaseSitePageEdit(page, this, exact));
			}
			m_pagesLazy = other.m_pagesLazy;

		} // set

		/**
		 * Access the site id.
		 * 
		 * @return The site id string.
		 */
		public String getId()
		{
			if (m_id == null) return "";
			return m_id;

		} // getId

		/**
		 * Access the URL which can be used to access the resource.
		 * 
		 * @return The URL which can be used to access the resource.
		 */
		public String getUrl()
		{
			return ServerConfigurationService.getPortalUrl() + "/site/" + m_id;
			//			return ServerConfigurationService.getPortalUrl() + "?site=" + m_id;

		} // getUrl

		/**
		 * Access the internal reference which can be used to access the resource from within the system.
		 * 
		 * @return The the internal reference which can be used to access the resource from within the system.
		 */
		public String getReference()
		{
			return siteReference(m_id);

		} // getReference

		/**
		 * Access the resources's properties.
		 * 
		 * @return The resources's properties.
		 */
		public ResourceProperties getProperties()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readSiteProperties(this, m_properties);
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
		 * Access the site's full name (for display purposes).
		 * 
		 * @return The site's full name (for display purposes).
		 */
		public String getTitle()
		{
			// if set here, use the setting
			if (m_title != null) return m_title;

			// if not otherwise set, use the id
			return getId();

		} // getTitle

		/**
		 * Access the site's short description.
		 * 
		 * @return The site's short description.
		 */
		public String getShortDescription()
		{
			return m_shortDescription;
		}

		/**
		 * Access the site's description.
		 * 
		 * @return The site's description.
		 */
		public String getDescription()
		{
			return m_description;

		} // getDescription

		/**
		 * Is this site open for users to join?
		 * 
		 * @return true if the site is joinable, false if closed.
		 */
		public boolean isJoinable()
		{
			return m_joinable;

		} // isJoinable

		/** @return the role name given to users who join a joinable site. */
		public String getJoinerRole()
		{
			return m_joinerRole;

		} // getJoinerRole

		/**
		 * {@inheritDoc}
		 */
		public boolean isPublished()
		{
			return m_published;
		}

		/** @return the skin to use for this site. */
		public String getSkin()
		{
			return m_skin;

		} // getSkin

		/**
		 * Access the site's display icon image URL.
		 * 
		 * @return the site's display icon image URL.
		 */
		public String getIconUrl()
		{
			return m_icon;

		} // getIconUrl

		/**
		 * {@inheritDoc}
		 */
		public String getIconUrlFull()
		{
			return convertReferenceUrl(m_icon);
		}

		/**
		 * Access the site's site info URL.
		 * 
		 * @return the site's site info URL.
		 */
		public String getInfoUrl()
		{
			return m_info;

		} // getSiteInfoUrl

		/**
		 * {@inheritDoc}
		 */
		public String getInfoUrlFull()
		{
			return convertReferenceUrl(m_info);
		}

		/**
		 * Return the page list for this site.
		 * 
		 * @return List (SitePage) the page list for this site
		 */
		public List getPages()
		{
			if (m_pagesLazy)
			{
				m_pagesLazy = false;
				m_storage.readSitePages(this, m_pages);
			}

			return m_pages;

		} // getPages

		/**
		 * {@inheritDoc}
		 */
		public List getOrderedPages()
		{
			List order = ServerConfigurationService.getToolOrder(getType());
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
		 * Access the SitePage that has this id, if one is defined, or null if not defined.
		 * 
		 * @param id
		 *        The id of the SitePage.
		 * @return The SitePage that has this id, if one is defined, or null if not defined.
		 */
		public SitePage getPage(String id)
		{
			return (SitePage) ((ResourceVector) getPages()).getById(id);

		} // getPage

		/**
		 * Access the ToolConfiguration that has this id, if one is defined, else return null. The tool may be on any SitePage in the site.
		 * 
		 * @param id
		 *        The id of the tool.
		 * @return The ToolConfiguration that has this id, if one is defined, else return null.
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

		} // getTool

		/**
		 * {@inheritDoc}
		 */
		public String getType()
		{
			return m_type;
		}

		/**
		 * Test if the site is of this type. It is if the param is null.
		 * 
		 * @param type
		 *        A String type to match, or a String[], List or Set of Strings, any of which can match.
		 * @return true if the site is of the type(s) specified, false if not.
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
		 * Access the site's properties, pages, each page's properties and tools, each tool's properties, to assure they are fully read in and not lazy.
		 */
		protected void readFully()
		{
			this.getProperties();
			for (Iterator pages = this.getPages().iterator(); pages.hasNext();)
			{
				BaseSitePageEdit page = (BaseSitePageEdit) pages.next();
				page.getProperties();
				for (Iterator tools = page.getTools().iterator(); tools.hasNext();)
				{
					BaseToolConfiguration tool = (BaseToolConfiguration) tools.next();
					tool.getPlacementConfig();
				}
			}
		}

		/**
		 * Are these objects equal? If they are both Site objects, and they have matching id's, they are.
		 * 
		 * @return true if they are equal, false if not.
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

		} // equals

		/**
		 * Make a hash code that reflects the equals() logic as well. We want two objects, even if different instances, if they have the same id to hash the same.
		 */
		public int hashCode()
		{
			return getId().hashCode();

		} // hashCode

		/**
		 * Compare this object with the specified object for order.
		 * 
		 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
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

		} // compareTo

		/**
		 * {@inheritDoc}
		 */
		public boolean isPubView()
		{
			return m_pubView;
		}

		/**
		 * Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		 * 
		 * @param doc
		 *        The DOM doc to contain the XML (or null for a string return).
		 * @param stack
		 *        The DOM elements, the top of which is the containing element of the new "resource" element.
		 * @return The newly added element.
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
				BaseSitePageEdit page = (BaseSitePageEdit) iPages.next();
				page.toXml(doc, stack);
			}
			stack.pop();

			return site;

		} // toXml

	} // BaseSite

	/**********************************************************************************************************************************************************************************************************************************************************
	 * SiteEdit implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BaseSiteEdit extends BaseSite implements SiteEdit, SessionBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		 * Construct.
		 * 
		 * @param id
		 *        The site id.
		 */
		public BaseSiteEdit(String id)
		{
			super(id);

		} // BaseSiteEdit

		/**
		 * Construct from another Site.
		 * 
		 * @param otherSite
		 *        The other site to copy values from.
		 */
		public BaseSiteEdit(Site otherSite)
		{
			super(otherSite, true);

		} // BaseSiteEdit

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
		public BaseSiteEdit(String id, String title, String type, String shortDesc, String description, String iconUrl,
				String infoUrl, String skin, boolean published, boolean joinable, boolean pubView, String joinRole,
				boolean isSpecial, boolean isUser, String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
		{
			super((String) null);

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

			// set up the page list
			m_pagesLazy = true;
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

		} // finalize

		/**
		 * Set the site's title (for display purposes).
		 * 
		 * @param name
		 *        The site's titlesite's (for display purposes).
		 */
		public void setTitle(String title)
		{
			m_title = StringUtil.trimToNull(title);

		} // setTitle

		/**
		 * Set the site's short description.
		 * 
		 * @param name
		 *        The site's short description.
		 */
		public void setShortDescription(String shortDescripion)
		{
			m_shortDescription = StringUtil.trimToNull(shortDescripion);
		}

		/**
		 * Set the site's description.
		 * 
		 * @param name
		 *        The site's description.
		 */
		public void setDescription(String description)
		{
			m_description = StringUtil.trimToNull(description);

		} // setDescription

		/**
		 * Set the joinable status for a site.
		 * 
		 * @param joinable
		 *        true if the site is joinable, false if closed.
		 */
		public void setJoinable(boolean joinable)
		{
			m_joinable = joinable;

		} // setJoinable

		/**
		 * Set the joiner role for a site.
		 * 
		 * @param role
		 *        the joiner role for a site.
		 */
		public void setJoinerRole(String role)
		{
			m_joinerRole = role;

		} // setJoinerRole

		/**
		 * {@inheritDoc}
		 */
		public void setPublished(boolean published)
		{
			m_published = published;

		}

		/**
		 * Set the skin to use for this site.
		 * 
		 * @param skin
		 *        The skin to use for this site.
		 */
		public void setSkin(String skin)
		{
			m_skin = skin;

		} // setSkin

		/**
		 * Set the site's display icon image URL.
		 * 
		 * @param url
		 *        The site's display icon image URL.
		 */
		public void setIconUrl(String url)
		{
			m_icon = StringUtil.trimToNull(url);

		} // setIconUrl

		/**
		 * Set the site's site info URL.
		 * 
		 * @param url
		 *        The site's site info URL.
		 */
		public void setInfoUrl(String url)
		{
			m_info = StringUtil.trimToNull(url);

		} // setInfoUrl

		/**
		 * Create a new site page and add it to this site.
		 * 
		 * @return The SitePageEdit object for the new site page.
		 */
		public SitePageEdit addPage()
		{
			BaseSitePageEdit page = new BaseSitePageEdit(this);
			getPages().add(page);

			return page;

		} // addPage

		/** @return the List (SitePageEdit) of editable Site Pages. */
		public List getPageEdits()
		{
			return m_pages;

		} // getPageEdits

		/**
		 * Access the SitePage that has this id, if one is defined, editable, or null if not defined.
		 * 
		 * @param id
		 *        The id of the SitePage.
		 * @return The SitePage that has this id, if one is defined, or null if not defined.
		 */
		public SitePageEdit getPageEdit(String id)
		{
			return (SitePageEdit) ((ResourceVector) getPages()).getById(id);

		} // getPageEdit

		/**
		 * Remove a site page from this site.
		 * 
		 * @param page
		 *        The SitePage to remove.
		 */
		public void removePage(SitePageEdit page)
		{
			getPages().remove(page);

		} // removePage

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
		 * Access the resource's properties for modification
		 * 
		 * @return The resource's properties.
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readSiteProperties(this, m_properties);
			}

			return m_properties;

		} // getPropertiesEdit

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

		} // activate

		/**
		 * Check to see if the edit is still active, or has already been closed.
		 * 
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
		 * Generate a new set of pages and tools that have new, unique ids. Good if the site had non-unique-system-wide ids for pages and tools. The Site Id does not change.
		 */
		public void regenerateIds()
		{
			// deep copy the pages
			ResourceVector newPages = new ResourceVector();
			for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
			{
				BaseSitePageEdit page = (BaseSitePageEdit) iPages.next();
				newPages.add(new BaseSitePageEdit(page, this, false));
			}

			m_pages = newPages;
		}

		/******************************************************************************************************************************************************************************************************************************************************
		 * SessionBindingListener implementation
		 *****************************************************************************************************************************************************************************************************************************************************/

		public void valueBound(SessionBindingEvent event)
		{
		}

		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // valueUnbound

	} // BaseSiteEdit

	/**********************************************************************************************************************************************************************************************************************************************************
	 * SitePage/Edit implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseSitePageEdit implements SitePageEdit, Identifiable
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
		protected BaseSitePageEdit(Site site)
		{
			m_site = (BaseSite) site;
			m_id = IdService.getUniqueId();
			m_properties = new BaseResourcePropertiesEdit();
			m_tools = new ResourceVector();

		} // BaseSitePageEdit

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
		protected BaseSitePageEdit(Site site, String id, String title, String layout)
		{
			m_site = (BaseSite) site;
			m_id = id;

			m_properties = new BaseResourcePropertiesEdit();
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_tools = new ResourceVector();
			m_toolsLazy = true;

			m_title = title;
			m_layout = "0".equals(layout) ? LAYOUT_SINGLE_COL : LAYOUT_DOUBLE_COL;
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
		protected BaseSitePageEdit(String pageId, String title, String layout, String siteId, String skin)
		{
			m_site = null;
			m_id = pageId;

			m_properties = new BaseResourcePropertiesEdit();
			((BaseResourcePropertiesEdit) m_properties).setLazy(true);

			m_tools = new ResourceVector();
			m_toolsLazy = true;

			m_title = title;
			m_layout = "0".equals(layout) ? LAYOUT_SINGLE_COL : LAYOUT_DOUBLE_COL;

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
		protected BaseSitePageEdit(SitePageEdit other, Site site, boolean exact)
		{
			BaseSitePageEdit bOther = (BaseSitePageEdit) other;

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
			m_toolsLazy = ((BaseSitePageEdit) other).m_toolsLazy;

		} // BaseSitePageEdit

		/**
		 * Construct from XML element.
		 * 
		 * @param el
		 *        The XML element.
		 * @param site
		 *        The site in which this page lives.
		 */
		protected BaseSitePageEdit(Element el, Site site)
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

		} // BaseSitePageEdit

		/** @return The human readable Title of this SitePage. */
		public String getTitle()
		{
			return m_title;

		} // getTitle

		/** @return the layout for this page. */
		public int getLayout()
		{
			return m_layout;

		} // getLayout

		/** @return the skin to use for this page. */
		public String getSkin()
		{
			if (m_site != null)
			{
				return adjustSkin(m_site.getSkin(), m_site.isPublished());
			}

			return m_skin;
		}

		/** @return the site id for this page. */
		public String getSiteId()
		{
			if (m_site != null)
			{
				return m_site.getId();
			}

			return m_siteId;
		}

		/** @return the layout title for this page. */
		public String getLayoutTitle()
		{
			return LAYOUT_NAMES[m_layout];

		} // getLayoutTitle

		/** @return The List (ToolConfiguration) of tools on this page. */
		public List getTools()
		{
			if (m_toolsLazy)
			{
				m_toolsLazy = false;
				m_storage.readPageTools(this, m_tools);
			}

			// TODO: need to sort by layout hint
			return m_tools;

		} // getTools

		/** @return The List (ToolConfiguration) of tools on this column (0 based) of this page. */
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
		 * Access a tool on this page by id.
		 * 
		 * @param id
		 *        The tool id.
		 * @return The tool on this page with this id, or null if not found.
		 */
		public ToolConfiguration getTool(String id)
		{
			return (ToolConfiguration) ((ResourceVector) getTools()).getById(id);

		} // getTool

		/**
		 * Set the display title of this page.
		 * 
		 * @param title
		 *        The new title.
		 */
		public void setTitle(String title)
		{
			m_title = StringUtil.trimToNull(title);

		} // setTitle

		/**
		 * Set the layout for this page.
		 * 
		 * @param layout
		 *        The new layout.
		 */
		public void setLayout(int layout)
		{
			if ((layout == LAYOUT_SINGLE_COL) || (layout == LAYOUT_DOUBLE_COL))
			{
				m_layout = layout;
			}
			else
				m_logger.warn(this + ".setLayout(): set to invalid value: " + layout);

		} // setLayout

		/**
		 * Add a new tool to the page.
		 * 
		 * @return the ToolConfigurationEdit object for the new tool.
		 */
		public ToolConfiguration addTool()
		{
			BaseToolConfiguration tool = new BaseToolConfiguration(this);
			((ResourceVector) getTools()).add(tool);

			return tool;

		} // addTool

		/**
		 * Add a new tool to the page, initialized to the tool registration information provided.
		 * 
		 * @param ref
		 *        The tool registration information used to initialize the tool.
		 * @return the ToolConfigurationEdit object for the new tool.
		 */
		public ToolConfiguration addTool(Tool reg)
		{
			BaseToolConfiguration tool = new BaseToolConfiguration(reg, this);
			((ResourceVector) getTools()).add(tool);

			return tool;

		} // addTool

		/**
		 * Remove a tool from this page, if found.
		 * 
		 * @param tool
		 *        The tool to remove.
		 */
		public void removeTool(ToolConfiguration tool)
		{
			((ResourceVector) getTools()).remove(tool);

		} // removeTool

		/**
		 * Move this page one step towards the start of the order of pages in this site.
		 */
		public void moveUp()
		{
			if (m_site == null) return;
			((ResourceVector) m_site.getPages()).moveUp(this);

		} // moveUp

		/**
		 * Move this page one step towards the end of the order of pages in this site.
		 */
		public void moveDown()
		{
			if (m_site == null) return;
			((ResourceVector) m_site.getPages()).moveDown(this);

		} // moveDown

		/**
		 * Access the resource's properties for modification
		 * 
		 * @return The resource's properties.
		 */
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readPageProperties(this, m_properties);
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
		 * 
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
		 * Access the URL which can be used to access the resource.
		 * 
		 * @return The URL which can be used to access the resource.
		 */
		public String getUrl()
		{
			String rv = null;
			if (m_site == null)
			{
				rv = ServerConfigurationService.getPortalUrl() + sitePageReference(m_siteId, m_id);
			}

			rv = ServerConfigurationService.getPortalUrl() + sitePageReference(m_site.getId(), m_id);

			//			if (m_site == null)
			//			{
			//				return ServerConfigurationService.getPortalUrl() + sitePageReference(m_siteId, m_id);
			//			}
			//
			//			return ServerConfigurationService.getPortalUrl() + sitePageReference(m_site.getId(), m_id);
			return rv;
		} // getUrl

		/**
		 * Access the internal reference which can be used to access the resource from within the system.
		 * 
		 * @return The the internal reference which can be used to access the resource from within the system.
		 */
		public String getReference()
		{
			if (m_site == null)
			{
				return sitePageReference(m_siteId, m_id);
			}

			return sitePageReference(m_site.getId(), m_id);

		} // getReference

		/**
		 * Access the id of the resource.
		 * 
		 * @return The id.
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * Access the site in which this page belongs.
		 * 
		 * @return the site in which this page belongs.
		 */
		public Site getContainingSite()
		{
			return m_site;

		} // getContainingSite

		/**
		 * Access the resource's properties.
		 * 
		 * @return The resource's properties.
		 */
		public ResourceProperties getProperties()
		{
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readPageProperties(this, m_properties);
			}

			return m_properties;
		}

		/**
		 * Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		 * 
		 * @param doc
		 *        The DOM doc to contain the XML (or null for a string return).
		 * @param stack
		 *        The DOM elements, the top of which is the containing element of the new "resource" element.
		 * @return The newly added element.
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

		} // toXml

	} // class BaseSitePageEdit

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ToolConfiguration implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class BaseToolConfiguration extends org.sakaiproject.util.Placement implements ToolConfiguration, Identifiable
	{
		/** The layout hints. */
		protected String m_layoutHints = null;

		/** The SitePage I belong to. */
		protected BaseSitePageEdit m_page = null;

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

			m_page = (BaseSitePageEdit) page;
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
			m_page = (BaseSitePageEdit) page;

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

			// setup for properties
			m_config.putAll(other.getPlacementConfig());

			// TODO: - change the other's property values to replace the other's site id with mine? why? exact? -ggolden
			//			m_properties = new BaseResourcePropertiesEdit();
			//			ResourceProperties pOther = other.getProperties();
			//			Iterator l = pOther.getPropertyNames();
			//			while (l.hasNext())
			//			{
			//				String pOtherName = (String) l.next();
			//				m_properties.addProperty(pOtherName, pOther.getProperty(pOtherName).replaceAll(other.getSiteId(), getSiteId()));
			//			}
			//			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());
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

			m_page = (BaseSitePageEdit) page;
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

			m_page = (BaseSitePageEdit) page;
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

			m_page = (BaseSitePageEdit) page;

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

		} // BaseToolConfigurationEdit

		/**
		 * {@inheritDoc}
		 */
		public Properties getPlacementConfig()
		{
			// if the config has not yet been read, read it
			if (m_configLazy)
			{
				m_configLazy = false;
				m_storage.readToolProperties(this, m_config);
			}

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
		 * Set the layout hints for this tool.
		 * 
		 * @param hints
		 *        The new layout hints.
		 */
		public void setLayoutHints(String hints)
		{
			m_layoutHints = hints;
		}

		/**
		 * Move this tool one step towards the start of the order of pages in this page.
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
		 * Move this tpp; one step towards the end of the order of pages in this page.
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
		 * Access the SitePage in which this tool configuration lives.
		 * 
		 * @return the SitePage on which this tool configuration lives.
		 */
		public SitePage getContainingPage()
		{
			return m_page;
		}

		/**
		 * Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		 * 
		 * @param doc
		 *        The DOM doc to contain the XML (or null for a string return).
		 * @param stack
		 *        The DOM elements, the top of which is the containing element of the new "resource" element.
		 * @return The newly added element.
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

		} // toXml

		/**
		 * {@inheritDoc}
		 */
		public void save()
		{
			// jam this changed tool configuration into the db, no matter what else is going on
			// TODO: security? version?
			m_storage.commitToolConfig(null, this);
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
		 * @return The locked site with this id, or null if in use.
		 */
		public SiteEdit put(String id);

		/**
		 * Get a lock on the site with this id, or null if a lock cannot be gotten.
		 * 
		 * @param id
		 *        The site reference.
		 * @return The locked Site with this id, or null if this records cannot be locked.
		 */
		public SiteEdit edit(String id);

		/**
		 * Commit the changes and release the lock.
		 * 
		 * @param user
		 *        The site to commit.
		 */
		public void commit(SiteEdit site);

		/**
		 * Cancel the changes and release the lock.
		 * 
		 * @param user
		 *        The site to commit.
		 */
		public void cancel(SiteEdit site);

		/**
		 * Remove this site.
		 * 
		 * @param user
		 *        The site to remove.
		 */
		public void remove(SiteEdit site);

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
		 * Access the Page that has this id, if one is defined, else return null. The page may be on any Site.
		 * 
		 * @param id
		 *        The id of the page.
		 * @return The SitePage that has this id, if one is defined, else return null.
		 */
		public SitePage findPage(String id);

		/**
		 * Read site properties from storage into the site's properties.
		 * 
		 * @param site
		 *        The site for which properties are desired.
		 */
		public void readSiteProperties(Site site, ResourcePropertiesEdit props);

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
		public void commitToolConfig(Connection conn, ToolConfiguration tool);

	} // Storage

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
	public Resource newContainer(String ref)
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
	public Resource newContainer(Element element)
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
	public Resource newContainer(Resource other)
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
	public Resource newResource(Resource container, String id, Object[] others)
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
	public Resource newResource(Resource container, Element element)
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
	public Resource newResource(Resource container, Resource other)
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
	public Edit newContainerEdit(Resource other)
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
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseSiteEdit e = new BaseSiteEdit(id);
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
	public Edit newResourceEdit(Resource container, Element element)
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
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseSiteEdit e = new BaseSiteEdit((Site) other);
		e.activate();
		return e;
	}

	/**
	 * Collect the fields that need to be stored outside the XML (for the resource).
	 * 
	 * @return An array of field values to store in the record outside the XML (for the resource).
	 */
	public Object[] storageFields(Resource r)
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
	public boolean isDraft(Resource r)
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
	public String getOwnerId(Resource r)
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
	public Time getDate(Resource r)
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
				BaseSiteEdit site = (BaseSiteEdit) m_storage.put(siteId);
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
				((BaseSiteEdit) site).set(new BaseSite(el), false);
				
				commitEdit(site);
			}
			catch (PermissionException ignore)
			{
			}
		}
		
		return msg.toString();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/site/BaseSiteService.java,v 1.18 2005/05/25 03:52:26 zqian.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
