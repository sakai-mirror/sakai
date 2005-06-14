/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitebrowser/SiteBrowserAction.java,v 1.12 2004/09/30 20:20:58 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.sitebrowser;

// imports
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PagedResourceAction;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.tool.sitesetup.SiteAction;
import org.sakaiproject.util.StringUtil;

/**
* <p>SiteBrowserAction is the CHEF site browser, showing a searchable list of the defined sites, and details including
* public resources of each when selected.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.12 $
*/
public class SiteBrowserAction
	extends PagedResourceAction
{
	private final static String PROP_SITE_SHORT_DESCRIPTION = "CTNG:short-description";
	
	/**
	* Implement this to return alist of all the resources that there are to page.
	* Sort them as appropriate, and apply search criteria.
	*/
	protected List readAllResources(SessionState state)
	{
		List sites = SiteService.getSites();
		String search = (String) state.getAttribute(STATE_SEARCH);

		// filter to only published, non-user, non-special sites
		List published = new Vector();
		for (Iterator iSites = sites.iterator(); iSites.hasNext();)
		{
			Site site = (Site) iSites.next();
			String include = site.getProperties().getProperty(SiteAction.PROP_SITE_INCLUDE);
			if (	(site.getStatus() == Site.SITE_STATUS_PUBLISHED)
				&&	(!SiteService.isUserSite(site.getId()))
				&&	(!SiteService.isSpecialSite(site.getId())))
			{
				// show the project site if it was created with "Include the site in the Sites index" 
				if (include == null || (include !=null && (new Boolean(include)).booleanValue()))
				{
					published.add(site);
				}
			}
		}
		sites = published;

		// return them all if no search
		if (search == null)
		{
			Collections.sort(sites);	
			return sites;
		}

		// filter
		else
		{
			Vector rv = new Vector();
			for (Iterator iSites = sites.iterator(); iSites.hasNext();)
			{
				Site site = (Site) iSites.next();

				String descStr = site.getProperties().getPropertyFormatted(PROP_SITE_SHORT_DESCRIPTION);
				if ((descStr == null) || (descStr.equals("")))
					descStr = site.getDescription();

				if (	StringUtil.containsIgnoreCase(site.getTitle(),search)
					||	StringUtil.containsIgnoreCase(descStr,search)
					||	StringUtil.containsIgnoreCase(site.getSkin(),search))
				{
					rv.add(site);
				}
			}
			
			Collections.sort(rv);
			return rv;
		}

	}	// readAllResources

	/**
	* Populate the state object, if needed.
	* @param state The state object.
	* @param portlet The portlet.
	* @param rundata The current request's rundata.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		// setup the observer to notify our main panel
/*		if (state.getAttribute(STATE_OBSERVER) == null)
		{
			// the delivery location for this tool
			String deliveryId = clientWindowId(state, portlet.getID());
			
			// the html element to update on delivery
			String elementId = mainPanelUpdateId(portlet.getID());
			
			// the event resource reference pattern to watch for
			String pattern = SiteService.siteReference("");

			state.setAttribute(STATE_OBSERVER, new EventObservingCourier(deliveryId, elementId, pattern));
		}

		// make sure the observer is in sync with state
		updateObservationOfChannel(state, portlet.getID());
*/
	}   // initState

	/**
	* Setup our observer to be watching for change events for our channel.
	* @param state The state object.
	* @param peid The portlet id.
	*/
	private void updateObservationOfChannel(SessionState state, String peid)
	{
/*		EventObservingCourier observer = (EventObservingCourier) state.getAttribute(STATE_OBSERVER);

		// the delivery location for this tool
		String deliveryId = clientWindowId(state, peid);
		observer.setDeliveryId(deliveryId);
*/		
	}   // updateObservationOfChannel

	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		String template = null;

		// check mode and dispatch
		String mode = (String) state.getAttribute("mode");
		if (mode == null)
		{
			template = buildListContext(state, context);
		}
		else if (mode.equals("visit"))
		{
			template = buildVisitContext(state, context);
		}
		else
		{
			Log.warn("chef", "SiteBrowserAction: mode: " + mode);
			template = buildListContext(state, context);
		}
		
		return (String)getContext(rundata).get("template") + template;

	}	// buildMainPanelContext

	/**
	* Build the context for the main list mode.
	*/
	private String buildListContext(SessionState state, Context context)
	{
		// put the service in the context (used for allow update calls on each site)
		context.put("service", SiteService.getInstance());

		// prepare the paging of realms
		List sites = prepPage(state);
		context.put("sites", sites);

		// we need the Realms, too!
		context.put("realms", RealmService.getInstance());

		// build the menu
		Menu bar = new Menu();

		// add the paging commands
		addListPagingMenus(bar, state);

		// add the search commands
		addSearchMenus(bar, state);

		// add the refresh commands
		// %%% we want manual only
		addRefreshMenus(bar, state);

		if (bar.size() > 0)
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
//		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).justDelivered();

		return "_list";

	}	// buildListContext

	/**
	* Build the context for the visit site mode.
	*/
	private String buildVisitContext(SessionState state, Context context)
	{
		// get the site to visit
		Site site = (Site) state.getAttribute("site");
		context.put("site", site);

		// get the public announcements
		String anncRef = AnnouncementService.channelReference(site.getId(), SiteService.MAIN_CONTAINER);
		List announcements = null;
		try
		{
			announcements = AnnouncementService.getMessages(anncRef);
		}
		catch (IdUnusedException e)
		{
			announcements = new Vector();
		}
		context.put("announcements", announcements);

		// get the public resources
		List resources = ContentHostingService.getAllResources(ContentHostingService.getSiteCollection(site.getId()));
		context.put("resources", resources);

		// the height for the info frame
		context.put("height", "300px");

		// the url for info
		String url = site.getInfoUrl();
		if (url != null)
		{
			url = url.trim();
			url = convertReferenceUrl(url);
			context.put("infoUrl", url);
		}

		context.put("contentTypeImageService", ContentTypeImageService.getInstance());

		return "_visit";

	}	// buildEditContext

	/**
	* Handle a request to visit a site.
	*/
	public void doVisit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");

		// get the site
		try
		{
			Site site = SiteService.getSite(id);
			state.setAttribute("site", site);

			state.setAttribute("mode", "visit");

			// disable auto-updates while in view mode
//			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteBrowserAction.doEdit: site not found: " + id);

			addAlert(state,  "Site " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
//			enableObserver(state);
		}

	}	// doEdit

	/**
	* Handle a request to return to the list.
	*/
	public void doBack(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		state.removeAttribute("mode");
		state.removeAttribute("site");

	}	// doBack

	/**
	* Say if the tool will require the "reset tool" button on the title bar.
	* @return true to have the "reset tool" button, false if not.
	* The default is to have it.
	*/
	protected boolean requireResetToolLink()
	{
		return false;

	}	// requireResetToolLink

	/**
	 * Return the url unchanged, unless it's a reference, then return the reference url
	 */
	private String convertReferenceUrl(String url)
	{
		// make a reference
		Reference ref = new Reference(url);

		// if it didn't recognize this, return it unchanged
		if (ref.getType() == null) return url;

		// return the reference's url
		return ref.getUrl();

	}	// convertReferenceUrl

}	// SiteBrowserAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitebrowser/SiteBrowserAction.java,v 1.12 2004/09/30 20:20:58 ggolden.umich.edu Exp $
*
**********************************************************************************/
