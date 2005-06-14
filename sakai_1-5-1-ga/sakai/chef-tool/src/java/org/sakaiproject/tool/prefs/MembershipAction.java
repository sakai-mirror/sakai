/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/MembershipAction.java,v 1.15 2005/01/03 20:20:17 ggolden.umich.edu Exp $
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
import java.util.List;

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
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.delivery.RefreshSiteNavDelivery;

/**
* <p>MembershipAction is a tool which displays Sites and lets the user join and un-join joinable Sites.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.15 $
*/
public class MembershipAction extends VelocityPortletPaneledAction
{
	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, Context context, RunData rundata, SessionState state)
	{
		// process all the sites the user has access to so can unjoin
		List unjoinableSites = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
						null, null, null, org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC, null);
		context.put("unjoinableSites", unjoinableSites);

		// get the sites the user does not have access to and are open so can join
		List openSites = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.JOINABLE,
						null, null, null, org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC, null);
		context.put("openSites", openSites);

		return (String) getContext(rundata).get("template");

	} // buildMainPanelContext

	/**
	* Handle the eventSubmit_doJoin command to have the user join one or more sites.
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
					// This could occur if the user's role is the maintain role for the site, and we don't let the user
					// unjoin sites they are maintainers of
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

} // MembershipAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/MembershipAction.java,v 1.15 2005/01/03 20:20:17 ggolden.umich.edu Exp $
*
**********************************************************************************/
