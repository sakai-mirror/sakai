/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.service.legacy.site;

// imports
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>SiteService manages Sites.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface SiteService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = SiteService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "site";

	/** Name for the event of visiting a site. */
	public static final String SITE_VISIT = "site.visit";

	/** Name for the event of visiting an unpublished site. */
	public static final String SITE_VISIT_UNPUBLISHED = "site.visit.unp";

	/** Name for the event of accessing a site. */
	// public static final String SECURE_ACCESS_SITE = "site.access";

	/** Name for the event of adding a site. */
	public static final String SECURE_ADD_SITE = "site.add";

	/** Name for the event of adding a user's My Workspace site. */
	public static final String SECURE_ADD_USER_SITE = "site.add.usersite";

	/** Name for the event of removing a site. */
	public static final String SECURE_REMOVE_SITE = "site.del";

	/** Name for the event of updating a site. */
	public static final String SECURE_UPDATE_SITE = "site.upd";

	/** Name for the event of viewing project site participants. */
	public static final String SECURE_VIEW_ROSTER = "site.viewRoster";

	/** The name of the main container for a resource in a site (channel, calendar, etc.). */
	public static final String MAIN_CONTAINER = "main";

	/** The name of a special site that is a template for non-user sites. */
	public static final String SITE_TEMPLATE = "!worksite";

	/** The name of a special site that is a template for user sites. */
	public static final String USER_SITE_TEMPLATE = "!user";
	
	/** The name of a special site that is used if the requested site is not available. */
	public static final String SITE_ERROR = "!error";
	
	/** The name of a special site that is used if the requested url is not understood. */
	public static final String URL_ERROR = "!urlError";

	/**
	 * <p>SelectionType enumerates different supported types of selection criteria for getting / counting sites.</p>
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

		/** Get sites that are marked for public view (non-myWorkspace, non-special). */
		public static final SelectionType PUBVIEW = new SelectionType("pubView", true, true, true);

		/** Get any sites. */
		public static final SelectionType ANY = new SelectionType("any", false, false, false);

		/** Get any non-user sites. */
		public static final SelectionType NON_USER = new SelectionType("nonUser", false, true, false);
	}

	/**
	 * <p>SortType enumerates different supported types of site sorting.</p>
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
	* @param id The site id.
	* @return true if the site is allowed to access the site, false if not.
	*/
	public boolean allowAccessSite(String id);

	/**
	* Access a site object.
	* @param id The site id string.
	* @return A site object containing the site information.
	* @exception IdUnusedException if not found.
	*/
	public Site getSite(String id) throws IdUnusedException;

	/**
	* Access a site object for purposes of having the user visit the site - visitation permissions are in effect.
	* @param id The site id string.
	* @return A site object containing the site information.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to visit this site.
	*/
	public Site getSiteVisit(String id) throws IdUnusedException, PermissionException;

	/**
	* check permissions for updating a site
	* @param id The site id.
	* @return true if the site is allowed to update the site, false if not.
	*/
	public boolean allowUpdateSite(String id);

	/**
	* Get a locked site object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The site id string.
	* @return A SiteEdit object for editing.
	* @exception IdUnusedException if not found, or if not an SiteEdit object
	* @exception PermissionException if the current user does not have permission to modify with this site.
	* @exception InUseException if the site is otherwise being edited.
	*/
	public SiteEdit editSite(String id) throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a SiteEdit object, and release the lock.
	* The SiteEdit is disabled, and not to be used after this call.
	* @param site The SiteEdit object to commit.
	*/
	public void commitEdit(SiteEdit site);

	/**
	* Cancel the changes made to a SiteEdit object, and release the lock.
	* The SiteEdit is disabled, and not to be used after this call.
	* @param site The SiteEdit object to commit.
	*/
	public void cancelEdit(SiteEdit site);

	/**
	* check permissions for addSite().
	* @param id The site id.
	* @return true if the site is allowed to addSite(id), false if not.
	*/
	public boolean allowAddSite(String id);

	/**
	* Add a new site.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The site id.
	* @return The new locked site object.
	* @exception IdInvalidException if the site id is invalid.
	* @exception IdUsedException if the site id is already used.
	* @exception PermissionException if the current user does not have permission to add a site.
	*/
	public SiteEdit addSite(String id) throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* Add a new site.  Will be structured just like <other>.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The site id.
	* @param other The site to make this site a structural copy of.
	* @return The new locked site object.
	* @exception IdInvalidException if the site id is invalid.
	* @exception IdUsedException if the site id is already used.
	* @exception PermissionException if the current user does not have permission to add a site.
	*/
	public SiteEdit addSite(String id, Site other) throws IdInvalidException, IdUsedException, PermissionException;
	
	/**
	* check permissions for removeSite().
	* @param id The site id.
	* @return true if the site is allowed to removeSite(id), false if not.
	*/
	public boolean allowRemoveSite(String id);

	/**
	* Remove this site's information
	* - it must be a site with a lock from editSite().
	* The SiteEdit is disabled, and not to be used after this call.
	* @param id The site id.
	* @exception PermissionException if the current user does not have permission to remove this site.
	*/
	public void removeSite(SiteEdit site) throws PermissionException;

	/**
	* Access the internal reference which can be used to access the site from within the system.
	* @param id The site id.
	* @return The the internal reference which can be used to access the site from within the system.
	*/
	public String siteReference(String id);

	/**
	* Access the internal reference which can be used to access the site page from within the system.
	* @param siteId The site id.
	* @param pageId The page id.
	* @return The the internal reference which can be used to access the site page from within the system.
	*/
	public String sitePageReference(String siteId, String pageId);

	/**
	* Access the internal reference which can be used to access the site tool from within the system.
	* @param siteId The site id.
	* @param toolId The tool id.
	* @return The the internal reference which can be used to access the site tool from within the system.
	*/
	public String siteToolReference(String siteId, String toolId);

	/**
	* Is this site (id or reference) a user site?
	* @param site The site id or reference.
	* @return true if this is a user site, false if not.
	*/
	public boolean isUserSite(String site);

	/**
	* Extract the user id for this user site from the site id or reference.
	* @param site The site id or reference.
	* @return The user id associated with this site.
	*/
	public String getSiteUserId(String site);

	/**
	* Form the site id for this user's site.
	* @param userId The user id.
	* @return The site id for this user's site.
	*/
	public String getUserSiteId(String userId);

	/**
	* Is this site (id or reference) a special site?
	* @param site The site id or reference.
	* @return true if this is a special site, false if not.
	*/
	public boolean isSpecialSite(String site);

	/**
	* Extract the special id for this special site from the site id or reference.
	* @param site The site id or reference.
	* @return The special id associated with this site.
	*/
	public String getSiteSpecialId(String site);

	/**
	* Form the site id for this special site.
	* @param special The special id.
	* @return The site id for this user's site.
	*/
	public String getSpecialSiteId(String special);

	/**
	* Form a display of the site title and id for this site.
	* @param id The site id.
	* @return A display of the site title and id for this site.
	*/
	public String getSiteDisplay(String id);

	/**
	* Access the ToolConfiguration that has this id, if one is defined, else return null.
	* The tool may be on any Site and on any SitePage.
	* @param id The id of the tool.
	* @return The ToolConfiguration that has this id, if one is defined, else return null.
	*/
	public ToolConfiguration findTool(String id);

	/**
	* Access the Page that has this id, if one is defined, else return null.
	* The page may be on any Site.
	* @param id The id of the page.
	* @return The SitePage that has this id, if one is defined, else return null.
	*/
	public SitePage findPage(String id);

	/**
	* check permissions for viewing project site participants
	* @param id The site id.
	* @return true if the site is allowed to addSite(id), false if not.
	*/
	public boolean allowViewRoster(String id);

	/**
	 * Cause the current user to join the site as defined by the site's joinable flag and joiner role.
	 * @param id The site id.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to join this site.
	 * @exception InUseException if the site is otherwise being edited.
	 */
	public void join(String id) throws IdUnusedException, PermissionException, InUseException;
	
	/**
	 * check permissions for unjoin() - unjoining the site and removing all role relationships.
	 * @param id The site id.
	 * @return true if the user is allowed to unjoin(id), false if not.
	 */
	public boolean allowUnjoinSite(String id);

	/**
	 * Cause the current user to unjoin the site, removing all role relationships.
	 * @param id The site id.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to unjoin this site.
	 * @exception InUseException if the site is otherwise being edited.
	 */
	public void unjoin(String id) throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Compute the skin to use for the (optional) site specified in the id parameter.
	 * If no site specified, or if the site has no skin defined, use the configured default skin.
	 * @param id The (optional) site id.
	 * @return A skin to use for this site.
	 */
	public String getSiteSkin(String id);

	/**
	 * Access a unique list of String site types for any site type defined for any site,
	 * sorted by type.
	 * @return A list (String) of all used site types.
	 */
	public List getSiteTypes();

	/**
	 * Access a list of Site objets that meet specified criteria.
	 * @param type The SelectionType specifying what sort of selection is intended.
	 * @param ofType Site type criteria: null for any type; a String to match a single type; A String[],
	 * 			List or Set to match any type in the collection.
	 * @param criteria Additional selection criteria: sites returned will match this string somewhere in their
	 * 			id, title, description, or skin.
	 * @param propertyCriteria Additional selection criteria: sites returned will have a property named to match
	 * 			each key in the map, whose values match (somewhere in their value) the value in the map (may be null
	 * 			or empty).
	 * @param sort A SortType indicating the desired sort.  For no sort, set to SortType.NONE.
	 * @param page The PagePosition subset of items to return.
	 * @return The List (Site) of Site objets that meet specified criteria.
	 */
	public List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort, PagingPosition page);

	/**
	 * Count the Site objets that meet specified criteria.
	 * @param type The SelectionType specifying what sort of selection is intended.
	 * @param ofType Site type criteria: null for any type; a String to match a single type; A String[],
	 * 			List or Set to match any type in the collection.
	 * @param criteria Additional selection criteria: sits returned will match this string somewhere in their
	 * 			id, title, description, or skin.
	 * @param propertyCriteria Additional selection criteria: sites returned will have a property named to match
	 * 			each key in the map, whose values match (somewhere in their value) the value in the map (may be null
	 * 			or empty).
	 * @return The count of Site objets that meet specified criteria.
	 */
	public int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria);

	/**
	 * Establish the internal security for this site.  Previous security settings are replaced for this site.
	 * Assigning a user with update implies the two reads; assigning a user with unp read implies the other read.
	 * @param siteId The id of the site.
	 * @param updateUsers The set of String User Ids who have update access.
	 * @param visitUnpUsers The set of String User Ids who have visit unpublished access.
	 * @param visitUsers The set of String User Ids who have visit access.
	 */
	public void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers);

	/**
	 * Establish the internal security for user for all sites.  Previous security settings are replaced for this user.
	 * Assigning a user with update implies the two reads; assigning a user with unp read implies the other read.
	 * @param userId The id of the user.
	 * @param updateSites The set of String site ids where the user has update access.
	 * @param visitUnpSites The set of String site ids where the user has visit unpublished access.
	 * @param visitSites The set of String site ids where the user has visit access.
	 */
	public void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites);
	

	/**
	 * Merge the site information from the archive into the given site.
	 * @param toSiteId The target site id.
	 * @param fromSite The source site id
	 * @param e The XML DOM tree of content to merge.
	 * @param creatorId The site creator id for target site.
	 * @return A log of status messages from the archive.
	 */
	public String merge(String toSiteId, Element e, String creatorId);

}



