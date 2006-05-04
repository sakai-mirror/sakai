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
package org.sakaiproject.service.legacy.site;

// imports
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.w3c.dom.Element;

/**
 * <p>
 * SiteService manages Sites.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface SiteService extends EntityProducer
{
	/** This string can be used to find the service in the service manager. */
	static final String SERVICE_NAME = SiteService.class.getName();

	/** The Entity Reference sub-type for Site references. */
	static final String SITE_SUBTYPE = "site";

	/** The Entity Reference sub-type for Group references. */
	static final String GROUP_SUBTYPE = "group";

	/** The Entity Reference sub-type for Page references. */
	static final String PAGE_SUBTYPE = "page";

	/** The Entity Reference sub-type for Tool references. */
	static final String TOOL_SUBTYPE = "tool";

	/** This string starts the references to resources in this service. */
	static final String REFERENCE_ROOT = Entity.SEPARATOR + SITE_SUBTYPE;

	/** Name for the event of visiting a site. */
	static final String SITE_VISIT = "site.visit";

	/** Name for the event of visiting an unpublished site. */
	static final String SITE_VISIT_UNPUBLISHED = "site.visit.unp";

	/** Name for the event of accessing a site. */
	// static final String SECURE_ACCESS_SITE = "site.access";
	/** Name for the event of adding a site. */
	static final String SECURE_ADD_SITE = "site.add";

	/** Name for the event of adding a user's My Workspace site. */
	static final String SECURE_ADD_USER_SITE = "site.add.usersite";

	/** Name for the event of removing a site. */
	static final String SECURE_REMOVE_SITE = "site.del";

	/** Name for the event of updating a site. */
	static final String SECURE_UPDATE_SITE = "site.upd";

	/** Name for the event of updating a site's membership. */
	static final String SECURE_UPDATE_SITE_MEMBERSHIP = "site.upd.site.mbrshp";

	/** Name for the event of updating a site's groups' memberships. */
	static final String SECURE_UPDATE_GROUP_MEMBERSHIP = "site.upd.grp.mbrshp";

	/** Name for the event of viewing project site participants. */
	static final String SECURE_VIEW_ROSTER = "site.viewRoster";

	/** The name of the main container for a resource in a site (channel, calendar, etc.). */
	static final String MAIN_CONTAINER = "main";

	/** The name of a special site that is a template for non-user sites. */
	static final String SITE_TEMPLATE = "!worksite";

	/** The name of a special site that is a template for user sites. */
	static final String USER_SITE_TEMPLATE = "!user";

	/** The name of a special site that is used if the requested site is not available. */
	static final String SITE_ERROR = "!error";

	/** The name of a special site that is used if the requested url is not understood. */
	static final String URL_ERROR = "!urlError";

	/**
	 * <p>
	 * SelectionType enumerates different supported types of selection criteria for getting / counting sites.
	 * </p>
	 */
	public class SelectionType
	{
		private final String m_id;

		private final boolean m_ignoreSpecial;

		private final boolean m_ignoreUser;

		private final boolean m_ignoreUnpublished;

		private SelectionType(String id, boolean ignoreSpecial, boolean ignoreUser, boolean ignoreUnpublished)
		{
			m_id = id;
			m_ignoreSpecial = ignoreSpecial;
			m_ignoreUser = ignoreUser;
			m_ignoreUnpublished = ignoreUnpublished;
		}

		public String toString()
		{
			return m_id;
		}

		public boolean isIgnoreSpecial()
		{
			return m_ignoreSpecial;
		}

		public boolean isIgnoreUser()
		{
			return m_ignoreUser;
		}

		public boolean isIgnoreUnpublished()
		{
			return m_ignoreUnpublished;
		}

		/** Get sites that the current user has read access to (non-myWorkspace, non-special). */
		public static final SelectionType ACCESS = new SelectionType("access", true, true, false);

		/** Get sites that the current user has write access to (non-myWorkspace, non-special). */
		public static final SelectionType UPDATE = new SelectionType("update", true, true, false);

		/** Get sites that the current user does not have read access to but are joinable (non-myWorkspace, non-special). */
		public static final SelectionType JOINABLE = new SelectionType("joinable", true, true, true);

		/** Get sites that are marked for view (non-myWorkspace, non-special). */
		public static final SelectionType PUBVIEW = new SelectionType("pubView", true, true, true);

		/** Get any sites. */
		public static final SelectionType ANY = new SelectionType("any", false, false, false);

		/** Get any non-user sites. */
		public static final SelectionType NON_USER = new SelectionType("nonUser", false, true, false);
	}

	/**
	 * <p>
	 * SortType enumerates different supported types of site sorting.
	 * </p>
	 */
	public class SortType
	{
		private final String m_id;

		private final boolean m_asc;

		private SortType(String id, boolean asc)
		{
			m_id = id;
			m_asc = asc;
		}

		public String toString()
		{
			return m_id;
		}

		public boolean isAsc()
		{
			return m_asc;
		}

		/** Sort on title ASC */
		public static final SortType NONE = new SortType("none", true);

		/** Sort on id ASC */
		public static final SortType ID_ASC = new SortType("id", true);

		/** Sort on id DESC */
		public static final SortType ID_DESC = new SortType("id", false);

		/** Sort on title ASC */
		public static final SortType TITLE_ASC = new SortType("title", true);

		/** Sort on title DESC */
		public static final SortType TITLE_DESC = new SortType("title", false);

		/** Sort on type ASC */
		public static final SortType TYPE_ASC = new SortType("type", true);

		/** Sort on type DESC */
		public static final SortType TYPE_DESC = new SortType("type", false);

		/** Sort on published ASC */
		public static final SortType PUBLISHED_ASC = new SortType("published", true);

		/** Sort on published DESC */
		public static final SortType PUBLISHED_DESC = new SortType("published", false);

		/** Sort on created by ASC */
		public static final SortType CREATED_BY_ASC = new SortType("created by", true);

		/** Sort on created by DESC */
		public static final SortType CREATED_BY_DESC = new SortType("created by", false);

		/** Sort on modified by ASC */
		public static final SortType MODIFIED_BY_ASC = new SortType("modified by", true);

		/** Sort on modified by DESC */
		public static final SortType MODIFIED_BY_DESC = new SortType("modified by", false);

		/** Sort on created time ASC */
		public static final SortType CREATED_ON_ASC = new SortType("created on", true);

		/** Sort on created time DESC */
		public static final SortType CREATED_ON_DESC = new SortType("created on", false);

		/** Sort on modified time ASC */
		public static final SortType MODIFIED_ON_ASC = new SortType("modified on", true);

		/** Sort on modified time DESC */
		public static final SortType MODIFIED_ON_DESC = new SortType("modified on", false);
	}

	/**
	 * check permissions for accessing (i.e. visiting) a site
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to access the site, false if not.
	 */
	boolean allowAccessSite(String id);

	/**
	 * Is this a valid site id?
	 * 
	 * @param id
	 *        The site id string.
	 * @return True if a site with this id is defined, false if not.
	 */
	boolean siteExists(String id);

	/**
	 * Access a site object.
	 * 
	 * @param id
	 *        The site id string.
	 * @return A site object containing the site information.
	 * @exception IdUnusedException
	 *            if not found.
	 */
	Site getSite(String id) throws IdUnusedException;

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
	Site getSiteVisit(String id) throws IdUnusedException, PermissionException;

	/**
	 * check permissions for updating a site
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to update the site, false if not.
	 */
	boolean allowUpdateSite(String id);

	/**
	 * check permissions for updating a site's membership
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to update the site's membership, false if not.
	 */
	boolean allowUpdateSiteMembership(String id);

	/**
	 * check permissions for updating a site's groups' memberships
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to update the site's groups' memberships, false if not.
	 */
	boolean allowUpdateGroupMembership(String id);

	/**
	 * Save any updates to this site - it must be a defined site (the id must exist) and the user must have update permissions.
	 * 
	 * @param site
	 *        The site, modified, to save.
	 * @throws IdUnusedException
	 *         If the site's id is not a defined site id.
	 * @throws PermissionException
	 *         If the end user does not have permission to update the site.
	 */
	void save(Site site) throws IdUnusedException, PermissionException;

	/**
	 * Save only site membership updates to this site - it must be a defined site (the id must exist) and the user must have site membership update permissions.
	 * 
	 * @param site
	 *        The site, modified, to save.
	 * @throws IdUnusedException
	 *         If the site's id is not a defined site id.
	 * @throws PermissionException
	 *         If the end user does not have permission to update the membership of the site.
	 */
	void saveSiteMembership(Site site) throws IdUnusedException, PermissionException;

	/**
	 * Save only site group membership updates to this site - it must be a defined site (the id must exist) and the user must have site group membership update permissions.
	 * 
	 * @param site
	 *        The site, modified, to save.
	 * @throws IdUnusedException
	 *         If the site's id is not a defined site id.
	 * @throws PermissionException
	 *         If the end user does not have permission to update the membership of the site.
	 */
	void saveGroupMembership(Site site) throws IdUnusedException, PermissionException;

	/**
	 * Save a site's information display fields: description and info url
	 * 
	 * @param id
	 *        The site id to update.
	 * @param description
	 *        The updated description.
	 * @param infoUrl
	 *        The updated infoUrl
	 * @throws IdUnusedException
	 *         If the site's id is not a defined site id.
	 * @throws PermissionException
	 *         If the end user does not have permission to update the site.
	 */
	void saveSiteInfo(String id, String description, String infoUrl) throws IdUnusedException, PermissionException;

	/**
	 * check permissions for addSite().
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to addSite(id), false if not.
	 */
	boolean allowAddSite(String id);

	/**
	 * Add a new site. The site will exist with just an id once done, so remove() it if you don't want to keep it.
	 * 
	 * @param id
	 *        The site id.
	 * @param type
	 *        The site type.
	 * @return The new site object.
	 * @exception IdInvalidException
	 *            if the site id is invalid.
	 * @exception IdUsedException
	 *            if the site id is already used.
	 * @exception PermissionException
	 *            if the current user does not have permission to add a site.
	 */
	Site addSite(String id, String type) throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * Add a new site. Will be structured just like <other>.
	 * 
	 * @param id
	 *        The site id.
	 * @param other
	 *        The site to make this site a structural copy of.
	 * @return The new site object.
	 * @exception IdInvalidException
	 *            if the site id is invalid.
	 * @exception IdUsedException
	 *            if the site id is already used.
	 * @exception PermissionException
	 *            if the current user does not have permission to add a site.
	 */
	Site addSite(String id, Site other) throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * check permissions for removeSite().
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to removeSite(id), false if not.
	 */
	boolean allowRemoveSite(String id);

	/**
	 * Remove this site's information.
	 * 
	 * @param id
	 *        The site id.
	 * @exception PermissionException
	 *            if the current user does not have permission to remove this site.
	 */
	void removeSite(Site site) throws PermissionException;

	/**
	 * Access the internal reference which can be used to access the site from within the system.
	 * 
	 * @param id
	 *        The site id.
	 * @return The the internal reference which can be used to access the site from within the system.
	 */
	String siteReference(String id);

	/**
	 * Access the internal reference which can be used to access the site page from within the system.
	 * 
	 * @param siteId
	 *        The site id.
	 * @param pageId
	 *        The page id.
	 * @return The the internal reference which can be used to access the site page from within the system.
	 */
	String sitePageReference(String siteId, String pageId);

	/**
	 * Access the internal reference which can be used to access the site tool from within the system.
	 * 
	 * @param siteId
	 *        The site id.
	 * @param toolId
	 *        The tool id.
	 * @return The the internal reference which can be used to access the site tool from within the system.
	 */
	String siteToolReference(String siteId, String toolId);

	/**
	 * Access the internal reference which can be used to access the site group from within the system.
	 * 
	 * @param siteId
	 *        The site id.
	 * @param groupId
	 *        The group id.
	 * @return The the internal reference which can be used to access the site group from within the system.
	 */
	String siteGroupReference(String siteId, String groupId);

	/**
	 * Is this site (id or reference) a user site?
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return true if this is a user site, false if not.
	 */
	boolean isUserSite(String site);

	/**
	 * Extract the user id for this user site from the site id or reference.
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return The user id associated with this site.
	 */
	String getSiteUserId(String site);

	/**
	 * Form the site id for this user's site.
	 * 
	 * @param userId
	 *        The user id.
	 * @return The site id for this user's site.
	 */
	String getUserSiteId(String userId);

	/**
	 * Is this site (id or reference) a special site?
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return true if this is a special site, false if not.
	 */
	boolean isSpecialSite(String site);

	/**
	 * Extract the special id for this special site from the site id or reference.
	 * 
	 * @param site
	 *        The site id or reference.
	 * @return The special id associated with this site.
	 */
	String getSiteSpecialId(String site);

	/**
	 * Form the site id for this special site.
	 * 
	 * @param special
	 *        The special id.
	 * @return The site id for this user's site.
	 */
	String getSpecialSiteId(String special);

	/**
	 * Form a display of the site title and id for this site.
	 * 
	 * @param id
	 *        The site id.
	 * @return A display of the site title and id for this site.
	 */
	String getSiteDisplay(String id);

	/**
	 * Access the ToolConfiguration that has this id, if one is defined, else return null. The tool may be on any Site and on any SitePage.
	 * 
	 * @param id
	 *        The id of the tool.
	 * @return The ToolConfiguration that has this id, if one is defined, else return null.
	 */
	ToolConfiguration findTool(String id);

	/**
	 * Access the Page that has this id, if one is defined, else return null. The page may be on any Site.
	 * 
	 * @param id
	 *        The id of the page.
	 * @return The SitePage that has this id, if one is defined, else return null.
	 */
	SitePage findPage(String id);

	/**
	 * check permissions for viewing project site participants
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the site is allowed to addSite(id), false if not.
	 */
	boolean allowViewRoster(String id);

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
	void join(String id) throws IdUnusedException, PermissionException;

	/**
	 * check permissions for unjoin() - unjoining the site and removing all role relationships.
	 * 
	 * @param id
	 *        The site id.
	 * @return true if the user is allowed to unjoin(id), false if not.
	 */
	boolean allowUnjoinSite(String id);

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
	void unjoin(String id) throws IdUnusedException, PermissionException;

	/**
	 * Compute the skin to use for the (optional) site specified in the id parameter. If no site specified, or if the site has no skin defined, use the configured default skin.
	 * 
	 * @param id
	 *        The (optional) site id.
	 * @return A skin to use for this site.
	 */
	String getSiteSkin(String id);

	/**
	 * Access a unique list of String site types for any site type defined for any site, sorted by type.
	 * 
	 * @return A list (String) of all used site types.
	 */
	List getSiteTypes();

	/**
	 * Access a list of Site objets that meet specified criteria.
	 * 
	 * @param type
	 *        The SelectionType specifying what sort of selection is intended.
	 * @param ofType
	 *        Site type criteria: null for any type; a String to match a single type; A String[], List or Set to match any type in the collection.
	 * @param criteria
	 *        Additional selection criteria: sites returned will match this string somewhere in their id, title, description, or skin.
	 * @param propertyCriteria
	 *        Additional selection criteria: sites returned will have a property named to match each key in the map, whose values match (somewhere in their value) the value in the map (may be null or empty).
	 * @param sort
	 *        A SortType indicating the desired sort. For no sort, set to SortType.NONE.
	 * @param page
	 *        The PagePosition subset of items to return.
	 * @return The List (Site) of Site objets that meet specified criteria.
	 */
	List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort, PagingPosition page);

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
	int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria);

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
	void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers);

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
	void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites);

	/**
	 * Merge the site information from the archive into the given site.
	 * 
	 * @param toSiteId
	 *        The target site id.
	 * @param fromSite
	 *        The source site id
	 * @param e
	 *        The XML DOM tree of content to merge.
	 * @param creatorId
	 *        The site creator id for target site.
	 * @return A log of status messages from the archive.
	 */
	String merge(String toSiteId, Element e, String creatorId);

	/**
	 * Access a Group object, given a reference string or id.
	 * 
	 * @param refOrId
	 *        The group reference or id string.
	 * @return The Group object if found, or null if not.
	 */
	Group findGroup(String refOrId);
}
