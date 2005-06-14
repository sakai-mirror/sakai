/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/Dropbox.java,v 1.8 2004/10/07 01:57:37 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.legacy.site;

// imports
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.StringUtil;

/**
* <p>Dropbox handles details of the Dropbox feature, used with the Resources tool and the ContentHosting service.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/

public class Dropbox
{
	/** The content root collection for dropboxes. */
	public static final String COLLECTION_DROPBOX = "/group-user/";

	protected static final String PROP_MEMBER_DROPBOX_DESCRIPTION = "Use this folder to store private files you wish to share only with organizers of this worksite.";
	protected static final String PROP_SITE_DROPBOX_DESCRIPTION = "Use this folder to store private files you wish to share only with individual members of this worksite.";
	protected static final String DROPBOX_ID = " Drop Box";

	/** Security ability (permission, lock, event function...) for those who may OWN a dropbox. */
	public static final String EVENT_DROPBOX_OWN = "dropbox.own";
	
	/**
	* Access the default dropbox collection id for the current request.
	* If the current user has permission to modify the site's dropbox collection, this is returned.
	* Otherwise, the current user's collection within the site's dropbox is returned.
	* @return The default dropbox collection id for the current request.
	*/
	public static String getCollection()
	{
		return getCollection(PortalService.getCurrentSiteId());

	}	// getCollection

	/**
	* Access the default dropbox collection id for the site.
	* If the current user has permission to modify the site's dropbox collection, this is returned.
	* Otherwise, the current user's collection within the site's dropbox is returned.
	* @param siteId The site id.
	* @return The default dropbox collection id for the site.
	*/
	public static String getCollection(String siteId)
	{
		String rv = null;

		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return rv;
		}

		// form the site's dropbox collection
		rv = COLLECTION_DROPBOX + siteId + "/";

		// test if the current user has modify ability in the site's dropbox collection
		if (ContentHostingService.allowUpdateCollection(rv))
		{
			// return the site's dropbox collection
			return rv;
		}

		// if the dropbox collection exists, form the current user's dropbox collection within this site's
		try
		{
			ContentHostingService.checkCollection(rv);
			rv += StringUtil.trimToZero(UsageSessionService.getSessionUserId()) + "/";
			return rv;
		}
		catch (Exception ignore) {}
		
		return null;

	}	// getCollection

	/**
	* Access the default dropbox collection display name for the current request.
	* If the current user has permission to modify the site's dropbox collection, this is returned.
	* Otherwise, the current user's collection within the site's dropbox is returned.
	* @return The default dropbox collection display name for the current request.
	*/
	public static String getDisplayName()
	{
		return getDisplayName(PortalService.getCurrentSiteId());

	}	// getDisplayName

	/**
	* Access the default dropbox collection display name for the site.
	* If the current user has permission to modify the site's dropbox collection, this is returned.
	* Otherwise, the current user's collection within the site's dropbox is returned.
	* @param siteId the Site id.
	* @return The default dropbox collection display name for the site.
	*/
	public static String getDisplayName(String siteId)
	{
		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return null;
		}

		// form the site's dropbox collection
		String id = COLLECTION_DROPBOX + siteId + "/";

		// test if the current user has modify ability in the site's dropbox collection
		if (ContentHostingService.allowUpdateCollection(id))
		{
			// return the site's dropbox collection
			return siteId + DROPBOX_ID;
		}

		// return the current user's sort name
		return UserDirectoryService.getCurrentUser().getSortName();

	}	// getDisplayName

	/**
	* Create the site's dropbox collection and one for each qualified user that the current user can make.
	*/
	public static void createCollection()
	{
		createCollection(PortalService.getCurrentSiteId());

	}	// createCollection

	/**
	* Create the site's dropbox collection and one for each qualified user that the current user can make.
	* @param siteId the Site id.
	*/
	public static void createCollection(String siteId)
	{		
		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return;
		}

		// form the site's dropbox collection
		String dropbox = COLLECTION_DROPBOX + siteId + "/";

		// try to create if it doesn't exist
		try
		{
			ContentHostingService.checkCollection(dropbox);
		}
		catch (IdUnusedException unused)
		{
			try
			{
				ContentCollectionEdit edit = ContentHostingService.addCollection(dropbox);
				ResourcePropertiesEdit props = edit.getPropertiesEdit();
				props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, siteId + DROPBOX_ID);
				props.addProperty(ResourceProperties.PROP_DESCRIPTION, PROP_SITE_DROPBOX_DESCRIPTION);
				ContentHostingService.commitCollection(edit);
			}
			catch (PermissionException permissionException)
			{
				return;
			}
			catch (IdUsedException e)
			{
				Logger.warn("Dropbox.createCollection(): IdUsedException: " + dropbox);
				return;
			}
			catch (IdInvalidException e)
			{
				Logger.warn("Dropbox.createCollection(): IdInvalidException: " + dropbox);
				return;
			}
			catch (InconsistentException e)
			{
				Logger.warn("Dropbox.createCollection(): InconsistentException: " + dropbox);
				return;
			}
		}
		catch (PermissionException e)
		{
			return;
		}
		catch (TypeException typeException)
		{
			Logger.warn("Dropbox.createCollection(): typeException: "+ dropbox);
			return;
		}

		// The EVENT_DROPBOX_OWN is granted within the site, so we can ask for all the users who have this ability
		// using just the dropbox collection
		List users = SecurityService.unlockUsers(EVENT_DROPBOX_OWN, ContentHostingService.getReference(dropbox));
		for (Iterator it = users.iterator(); it.hasNext(); )
		{
			User user = (User) it.next();

			// the folder id for this user's dropbox in this group
			String userFolder = dropbox + user.getId() + "/";

			// see if it exists
			try
			{
				ContentHostingService.checkCollection(userFolder);
			}
			catch (IdUnusedException unused)
			{
				// doesn't exist, try to create it
				try
				{
					ContentCollectionEdit edit = ContentHostingService.addCollection(userFolder);
					ResourcePropertiesEdit props = edit.getPropertiesEdit();
					props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, user.getSortName());
					props.addProperty(ResourceProperties.PROP_DESCRIPTION, PROP_MEMBER_DROPBOX_DESCRIPTION);
					ContentHostingService.commitCollection(edit);
				}
				catch (PermissionException ignore) {}
				catch (IdUsedException e)
				{
					Logger.warn("Dropbox.createCollection(): idUsedException: " + userFolder);
				}
				catch (IdInvalidException e)
				{
					Logger.warn("Dropbox.createCollection(): IdInvalidException: " + userFolder);
				}
				catch (InconsistentException e)
				{
					Logger.warn("Dropbox.createCollection(): InconsistentException: " + userFolder);
				}
			}
			catch (PermissionException ignore) {}
			catch (TypeException typeException)
			{
				Logger.warn("Dropbox.createCollection(): TypeException: " + userFolder);
			}
		}

	}	// createCollection

}	// Dropbox

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/Dropbox.java,v 1.8 2004/10/07 01:57:37 ggolden.umich.edu Exp $
*
***********************************************************************************/
