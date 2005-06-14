/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SiteService.java,v 1.13 2004/08/11 20:21:16 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.site;

// imports
import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>SiteService is the Interface for CHEF Site Management.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.13 $
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

	/** Name for the event of adding a user's myWorkspace site. */
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

	/** The name of a special site that is used if the requestd site is not available. */
	public static final String SITE_ERROR = "!error";

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
	public Site getSite(String id)
		throws IdUnusedException;

	/**
	* Access a site object for purposes of having the user visit the site - visitation permissions are in effect.
	* @param id The site id string.
	* @return A site object containing the site information.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to visit this site.
	*/
	public Site getSiteVisit(String id)
		throws IdUnusedException, PermissionException;

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
	public SiteEdit editSite(String id)
		throws IdUnusedException, PermissionException, InUseException;

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
	* Access all site objects.
	* @return A list of site objects containing each site's information.
	*/
	public List getSites();

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
	public SiteEdit addSite(String id)
		throws IdInvalidException, IdUsedException, PermissionException;

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
	public SiteEdit addSite(String id, Site other)
		throws IdInvalidException, IdUsedException, PermissionException;

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
	public void removeSite(SiteEdit site)
		throws PermissionException;

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
	public void join(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Cause the current user to unjoin the site, removing all role relationships.
	 * @param id The site id.
	 * @throws IdUnusedException if the id is not a valid site id.
	 * @exception PermissionException if the current user does not have permission to unjoin this site.
	 * @exception InUseException if the site is otherwise being edited.
	 */
	public void unjoin(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	 * Compute the skin to use for the (optional) site specified in the id parameter.
	 * If no site specified, or if the site has no skin defined, use the configured default skin.
	 * @param id The (optional) site id.
	 * @return A skin to use for this site.
	 */
	public String getSiteSkin(String id);

	/**
	 * Access a list of Site objects that the current user has access to.
	 * Sorting and inclusing of the user's own site (myWorkspace) is optional.
	 * Sites the user has read access to are returned, unless withUpdate is specified.
	 * @param sort If true, the sites will be sorted.
	 * @param withUpdate If true, only sites the user has update abilities for are included, else all sites
	 * the user has read abilities are included.
	 * @param ofType Type criteria.  null for any type.  A String to match a single type.  A String[],
	 * List or Set to match any type in the collection.
	 * @return The list (Site) of sites the current user has access to.
	 */
	public List getAllowedSites(boolean sort, boolean withUpdate, Object ofType);

}	// SiteService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SiteService.java,v 1.13 2004/08/11 20:21:16 ggolden.umich.edu Exp $
*
**********************************************************************************/
