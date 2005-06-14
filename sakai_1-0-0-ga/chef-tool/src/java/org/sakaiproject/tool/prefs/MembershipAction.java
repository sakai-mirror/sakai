/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/MembershipAction.java,v 1.10 2004/09/30 20:20:29 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.prefs;

// imports
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.delivery.RefreshSiteNavDelivery;

/**
* <p>MembershipAction is a tool which displays Sites and lets the user join and un-join joinable Sites.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
*/
public class MembershipAction extends VelocityPortletPaneledAction
{
	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, Context context, RunData rundata, SessionState state)
	{
		// collect the sites that are open and published and that the user is not a member and may join
		List openSites = new Vector();

		// collect the sites that the user has access to already, and may not unjoin
		List fixedSites = new Vector();

		// collect the sites that the user has access to already, and may unjoin
		List unjoinableSites = new Vector();

		// process all the sites
		List sites = SiteService.getSites();
		for (Iterator iSites = sites.iterator(); iSites.hasNext();)
		{
			Site site = (Site) iSites.next();

			// skip user sites (myWorkspaces)
			if (SiteService.isUserSite(site.getId()))
				continue;

			// skip special sites (!gateway...)
			if (SiteService.isSpecialSite(site.getId()))
				continue;

			// is the current user allowed to access the site?
			if (SiteService.allowAccessSite(site.getId()))
			{
				// can the user unjoin?
				boolean unjoin = true;

				if (unjoin)
				{
					unjoinableSites.add(site);
				}
				else
				{
					fixedSites.add(site);
				}
			}
			else
			{
				// skip unpublished sites
				if (site.getStatus() != Site.SITE_STATUS_PUBLISHED)
					continue;

				// skip non-joinable sites
				if (!site.isJoinable())
					continue;

				// we'll take it
				openSites.add(site);
			}
		}

		if (openSites.size() > 0)
		{
			Collections.sort(openSites);
			context.put("openSites", openSites);
		}

		if (fixedSites.size() > 0)
		{
			Collections.sort(fixedSites);
			context.put("fixedSites", fixedSites);
		}

		if (unjoinableSites.size() > 0)
		{
			Collections.sort(unjoinableSites);
			context.put("unjoinableSites", unjoinableSites);
		}

		return (String) getContext(rundata).get("template");

	} // buildMainPanelContext

	/**
	* Handle the eventSubmit_doJoin command to have the user join one or more groups.
	*/
	public void doJoin(RunData data)
	{
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(((JetspeedRunData) data).getJs_peid());

		// read the group ids to join
		String[] ids = data.getParameters().getStrings("join");
		if (ids != null)
		{
			for (int i = 0; i < ids.length; i++)
			{
				try
				{
					// join the site
					SiteService.join(ids[i]);
				}
				catch (IdUnusedException e)
				{
					Log.warn("chef", this +".doJoin(): " + e);
				}
				catch (PermissionException e)
				{
					Log.warn("chef", this +".doJoin(): " + e);
				}
				catch (InUseException e)
				{
					Log.warn("chef", this +".doJoin(): " + e);
				}
			}
		}

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));

	} // doJoin

	/**
	* Handle the eventSubmit_doUnjoin command to have the user un-join one or more groups.
	*/
	public void doUnjoin(RunData data)
	{
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(((JetspeedRunData) data).getJs_peid());

		// read the form / state to figure out which attachment(s) to add.
		String[] ids = data.getParameters().getStrings("unjoin");
		if (ids != null)
		{
			for (int i = 0; i < ids.length; i++)
			{
				try
				{
					SiteService.unjoin(ids[i]);
				}
				catch (IdUnusedException ignore)
				{
				}
				catch (PermissionException e)
				{
					Log.warn("chef", this +".doUnjoin(): " + e);
				}
				catch (InUseException e)
				{
					Log.warn("chef", this +".doJoin(): " + e);
				}
			}
		}

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));

	} // doUnjoin

	/**
	* Say if the tool will require the "reset tool" button on the title bar.
	* @return true to have the "reset tool" button, false if not.
	* The default is to have it.
	*/
	protected boolean requireResetToolLink()
	{
		return false;

	} // requireResetToolLink

	protected boolean isUnjoinable(Site site)
	{
		boolean isUnjoinable = true;
		User user = UserDirectoryService.getCurrentUser();
		try
		{
			// Note: this is bypassing the security API, since realms are private to the security impl.,
			// not the API. %%% Oh Well. -ggolden

			// unjoinable if the user has SITE_VISIT~ in the realm by internal (not provider) definition
			Realm realm = RealmService.getRealm(site.getReference());
			Set userLocks = realm.getUserLocks(user);
			isUnjoinable =
				(userLocks.contains(SiteService.SITE_VISIT)
					|| userLocks.contains(SiteService.SITE_VISIT_UNPUBLISHED));

			// if not a direct grant, try via a role grant
			if (!isUnjoinable)
			{
				Set userRoles = realm.getUserRoles(user);
				for (Iterator iRoles = userRoles.iterator(); iRoles.hasNext();)
				{
					Role role = (Role) iRoles.next();

					isUnjoinable =
						(role.unlock(SiteService.SITE_VISIT)
							|| role.unlock(SiteService.SITE_VISIT_UNPUBLISHED));
					if (isUnjoinable)
						break;
				}
			}
		}
		catch (Exception ignore)
		{
		}

		return isUnjoinable;
	}

} // MembershipAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/MembershipAction.java,v 1.10 2004/09/30 20:20:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
