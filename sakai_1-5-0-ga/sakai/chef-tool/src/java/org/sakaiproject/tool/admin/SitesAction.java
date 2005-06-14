/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/SitesAction.java,v 1.20 2005/02/11 21:21:59 janderse.umich.edu Exp $
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
package org.sakaiproject.tool.admin;

// imports
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PagedResourceActionII;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SitePageEdit;
import org.sakaiproject.service.legacy.site.ToolConfigurationEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.delivery.RefreshSiteNavDelivery;
import org.sakaiproject.util.observer.EventObservingCourier;

/**
* <p>SitesAction is the CHEF sites editor.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.20 $
*/
public class SitesAction
	extends PagedResourceActionII
{
	/** A Boolean indicating if we are to search only user sites (True) or non-user sites (False). */
	protected static String STATE_SEARCH_USER = "search.user";

	/**
	 * {@inheritDoc}
	 */
	protected List readResourcesPage(SessionState state, int first, int last)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		Boolean userOnly = (Boolean) state.getAttribute(STATE_SEARCH_USER);

		// search for non-user sites, using the criteria
		if ((userOnly == null) || (userOnly.booleanValue() == false) || (search == null))
		{
			return SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.NON_USER,
					null, search, null,
					org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC,
					new PagingPosition(first, last));
		}

		// search for a specific user site for the particular user id in the criteria - exact match only
		else
		{
			List rv = new Vector();
			try
			{
				Site userSite = SiteService.getSite(SiteService.getUserSiteId(search));
				rv.add(userSite);
			}
			catch (IdUnusedException e) {}

			return rv;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected int sizeResources(SessionState state)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		Boolean userOnly = (Boolean) state.getAttribute(STATE_SEARCH_USER);

		// search for non-user sites, using the criteria
		if ((userOnly == null) || (userOnly.booleanValue() == false) || (search == null))
		{
			return SiteService.countSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.NON_USER,
					null, search, null);
		}

		// search for a specific user site for the particular user id in the criteria - exact match only
		else
		{
			try
			{
				Site userSite = SiteService.getSite(SiteService.getUserSiteId(search));
				return 1;
			}
			catch (IdUnusedException e) {}

			return 0;
		}
	}

	/**
	* Populate the state object, if needed.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		// setup the observer to notify our main panel
		if (state.getAttribute(STATE_OBSERVER) == null)
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

	}   // initState

	/**
	* Setup our observer to be watching for change events for our channel.
	* @param peid The portlet id.
	*/
	private void updateObservationOfChannel(SessionState state, String peid)
	{
		EventObservingCourier observer = (EventObservingCourier) state.getAttribute(STATE_OBSERVER);

		// the delivery location for this tool
		String deliveryId = clientWindowId(state, peid);
		observer.setDeliveryId(deliveryId);
		
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
		else if (mode.equals("new"))
		{
			template = buildNewContext(state, context);
		}
		else if (mode.equals("edit"))
		{
			template = buildEditContext(state, context);
		}
		else if (mode.equals("confirm"))
		{
			template = buildConfirmRemoveContext(state, context);
		}
		else if (mode.equals("saveas"))
		{
			template = buildSaveasContext(state, context);
		}

		else if (mode.equals("pages"))
		{
			template = buildPagesContext(state, context);
		}
		else if (mode.equals("newPage"))
		{
			template = buildNewPageContext(state, context);
		}
		else if (mode.equals("editPage"))
		{
			template = buildEditPageContext(state, context);
		}

		else if (mode.equals("tools"))
		{
			template = buildToolsContext(state, context);
		}
		else if (mode.equals("newTool"))
		{
			template = buildNewToolContext(state, context);
		}
		else if (mode.equals("editTool"))
		{
			template = buildEditToolContext(state, context);
		}

		//		else if (mode.equals("newMember"))
//		{
//			template = buildNewMemberContext(state, context);
//		}
		else
		{
			Log.warn("chef", "SitesAction: mode: " + mode);
			template = buildListContext(state, context);
		}

		String prefix = (String)getContext(rundata).get("template");		
		return prefix + template;

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
		if (SiteService.allowAddSite(""))
		{
			bar.add( new MenuEntry("New Site", "doNew") );
		}

		// add the paging commands
		addListPagingMenus(bar, state);

		// add the search commands
		addSearchMenus(bar, state);
		Boolean userSearchB = ((Boolean)state.getAttribute(STATE_SEARCH_USER));
		boolean userSearch = false;
		if (userSearchB != null)
		{
			userSearch = userSearchB.booleanValue();
		}
		bar.add( new MenuEntry((userSearch ? "User MyWorkspaces" : "Non-User Sites"), null, true,
				(userSearch ? MenuItem.CHECKED_TRUE : MenuItem.CHECKED_FALSE),
				"doToggle_user_search") );

		// add the refresh commands
		addRefreshMenus(bar, state);

		if (bar.size() > 0)
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).justDelivered();

		return "_list";

	}	// buildListContext

	/**
	* Build the context for the new site mode.
	*/
	private String buildNewContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "site-form");

		return "_edit";

	}	// buildNewContext

	/**
	* Build the context for the edit site mode.
	*/
	private String buildEditContext(SessionState state, Context context)
	{
		// get the site to edit
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		context.put("site", site);

		// get the site's realm
		//RealmEdit realm = (RealmEdit) state.getAttribute("realm");
		//context.put("realm", realm);

		// get the list of members - those with roles in the realm
		//Set members = realm.collectUsers();
		//List mbrsSorted = new Vector();
		//mbrsSorted.addAll(members);
		//Collections.sort(mbrsSorted);
		//context.put("members", mbrsSorted);

		// name the html form for user edit fields
		context.put("form-name", "site-form");

		// build the menu
		// we need the form fields for the remove...
		Menu bar = new Menu();
		if (SiteService.allowRemoveSite(site.getId()))
		{
			bar.add( new MenuEntry("Remove Site", null, true, MenuItem.CHECKED_NA, "doRemove", "site-form") );
		}

		bar.add( new MenuEntry("Save As", null, true, MenuItem.CHECKED_NA, "doSaveas_request", "site-form") );

		context.put(Menu.CONTEXT_MENU, bar);

		return "_edit";

	}	// buildEditContext

	/**
	* Build the context for the new site mode.
	*/
	private String buildConfirmRemoveContext(SessionState state, Context context)
	{
		// get the site to edit
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		context.put("site", site);

		// get the site's realm
//		RealmEdit realm = (RealmEdit) state.getAttribute("realm");
//		context.put("realm", realm);

		return "_confirm_remove";

	}	// buildConfirmRemoveContext

	/**
	* Build the context for the new site mode.
	*/
	private String buildSaveasContext(SessionState state, Context context)
	{
		// get the site to edit
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		context.put("site", site);

		return "_saveas";

	}	// buildSaveasContext

	/**
	* Build the context for the new member mode
	*/
/*	private String buildNewMemberContext(SessionState state, Context context)
	{
		return "_add_member";

	}	// buildNewMemberContext
*/

	/**
	* Build the context for the pages display in edit mode.
	*/
	private String buildPagesContext(SessionState state, Context context)
	{
		// get the site to edit
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		context.put("site", site);

		// put all site's pages into the context
		List pages = site.getPages();
		context.put("pages", pages);

		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry("New Page", "doNew_page") );
		context.put(Menu.CONTEXT_MENU, bar);

		return "_pages";

	}	// buildPagesContext

	/**
	* Build the context for the new page mode.
	*/
	private String buildNewPageContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "page-form");

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");

		context.put("site", site);
		context.put("page", page);

		context.put("layouts", layoutsList());

		return "_edit_page";

	}	// buildNewPageContext

	/**
	* Build the context for the edit page mode.
	*/
	private String buildEditPageContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "page-form");

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");

		context.put("site", site);
		context.put("page", page);

		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry("Remove Page", null, true, MenuItem.CHECKED_NA, "doRemove_page") );
		context.put(Menu.CONTEXT_MENU, bar);

		context.put("layouts", layoutsList());

		return "_edit_page";

	}	// buildEditPageContext

	/**
	* Build the context for the tools display in edit mode.
	*/
	private String buildToolsContext(SessionState state, Context context)
	{
		// get the site to edit
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		context.put("site", site);

		// get the page being edited
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		context.put("page", page);

		// put all page's tools into the context
		List tools = page.getTools();
		context.put("tools", tools);

		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry("New Tool", "doNew_tool") );
		context.put(Menu.CONTEXT_MENU, bar);

		return "_tools";

	}	// buildToolsContext

	/**
	* Build the context for the new tool mode.
	*/
	private String buildNewToolContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "tool-form");

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		context.put("site", site);
		context.put("page", page);
		context.put("tool", tool);

		List features = ServerConfigurationService.getToolRegistrations();
		Collections.sort(features);
		context.put("features", features);

		return "_edit_tool";

	}	// buildNewToolContext

	/**
	* Build the context for the edit tool mode.
	*/
	private String buildEditToolContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "tool-form");

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		context.put("site", site);
		context.put("page", page);
		context.put("tool", tool);

		List features = ServerConfigurationService.getToolRegistrations();
		Collections.sort(features);
		context.put("features", features);

		context.put("toolReg", ServerConfigurationService.getToolRegistration(tool.getToolId()));

		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry("Remove Tool", null, true, MenuItem.CHECKED_NA, "doRemove_tool") );
		context.put(Menu.CONTEXT_MENU, bar);

		return "_edit_tool";

	}	// buildEditToolContext

	/**
	* Handle a request for a new site.
	*/
	public void doNew(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "new");

		// mark the site as new, so on cancel it can be deleted
		state.setAttribute("new", "true");

		// disable auto-updates while in view mode
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();

	}	// doNew

	/**
	* Handle a request to edit a site.
	*/
	public void doEdit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");

		// get the site
		try
		{
			SiteEdit site = SiteService.editSite(id);
			state.setAttribute("site", site);

//			RealmEdit realm = RealmService.editRealm("/site/" + id);	// %%% use a site service call -ggolden
//			state.setAttribute("realm", realm);

			state.setAttribute("mode", "edit");

			// disable auto-updates while in view mode
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SitesAction.doEdit: site not found: " + id);

			addAlert(state,  "Site " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (PermissionException e)
		{
			addAlert(state,  "You do not have permission to edit Site " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (InUseException e)
		{
			addAlert(state,  "Someone else is currently editing this Site: " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}

	}	// doEdit

	/**
	* Handle a request to save the site edit (from the site edit form).
	*/
	public void doSave(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readSiteForm(data, state)) return;

		doSave_edit(data, context);

	}	// doSave

	/**
	* Handle a request to save the edit from either page or tools list mode - no form to read in.
	*/
	public void doSave_edit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// commit the change
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		if (site != null)
		{
			// bring the mail archive service's channel for this site in sync with the site's setting
//			syncWithMailArchive(site);

			SiteService.commitEdit(site);

			// save the realm, too
//			RealmEdit realm = (RealmEdit) state.getAttribute("realm");
//			RealmService.commitEdit(realm);
		}

		// cleanup
		cleanState(state);

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));

	}	// doSave_edit

	/**
	* Go into saveas mode
	*/
	public void doSaveas_request(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readSiteForm(data, state)) return;

		// go to saveas mode
		state.setAttribute("mode", "saveas");

	}	// doSaveas_request

	/**
	* Handle a request to save-as the site as a new site.
	*/
	public void doSaveas(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form
		String id = data.getParameters().getString("id");

		// get the site to copy from
		SiteEdit site = (SiteEdit) state.getAttribute("site");

		try
		{
			// make a new site with this id and as a structural copy of site
			SiteEdit newSite = SiteService.addSite(id, site);

			// commit
			SiteService.commitEdit(newSite);
		}
		catch (IdUsedException e)
		{
			addAlert(state,  "The site id is already in use");
			return;
		}
		catch (IdInvalidException e)
		{
			addAlert(state,  "The site id is invalid");
			return;
		}
		catch (PermissionException e)
		{
			addAlert(state,  "You do not have permission to create this Site");
			return;
		}

		// cancel and close
		SiteService.cancelEdit(site);

		cleanState(state);

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));

	}	// doSaveas

	/**
	* cancel the saveas request, return to edit
	*/
	public void doCancel_saveas(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to main mode
		state.setAttribute("mode", "edit");

	}	// doCancel_saveas

	/**
	* doCancel called when "eventSubmit_doCancel" is in the request parameters to cancel site edits
	*/
	public void doCancel(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// cancel the realm edit - it will be removed if the site is removed
//		RealmEdit realm = (RealmEdit) state.getAttribute("realm");
//		if (realm != null)
//		{
//			RealmService.cancelEdit(realm);
//		}

		// get the site
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		if (site != null)
		{
			// if this was a new, delete the site
			if ("true".equals(state.getAttribute("new")))
			{
				// remove the site
				try
				{
					SiteService.removeSite(site);
				}
				catch (PermissionException e)
				{
					addAlert(state,  "You do not have permission to remove Site " + site.getId());
				}
			}
			// otherwise, just cancel the edits
			else
			{
				SiteService.cancelEdit(site);
			}
		}

		// cleanup
		cleanState(state);

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

	}	// doCancel

	/**
	* doRemove called when "eventSubmit_doRemove" is in the request parameters to confirm removal of the site
	*/
	public void doRemove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readSiteForm(data, state)) return;

		// go to remove confirm mode
		state.setAttribute("mode", "confirm");

	}	// doRemove

	/**
	* doRemove_confirmed called when "eventSubmit_doRemove_confirmed" is in the request parameters to remove the site
	*/
	public void doRemove_confirmed(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the site
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		
		// cancel the realm edit - the site remove will remove the realm
//		RealmEdit realm = (RealmEdit) state.getAttribute("realm");
//		RealmService.cancelEdit(realm);

		// remove the site
		try
		{
			SiteService.removeSite(site);
		}
		catch (PermissionException e)
		{
			addAlert(state,  "You do not have permission to remove Site " + site.getId());
		}

		// cleanup
		cleanState(state);

		// go to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));

	}	// doRemove_confirmed

	/**
	* doCancel_remove called when "eventSubmit_doCancel_remove" is in the request parameters to cancel site removal
	*/
	public void doCancel_remove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_remove

	/**
	* Read the site form and update the site in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readSiteForm(RunData data, SessionState state)
	{
		// read the form
		String id = StringUtil.trimToNull(data.getParameters().getString("id"));
		String title = StringUtil.trimToNull(data.getParameters().getString("title"));
		String type = StringUtil.trimToNull(data.getParameters().getString("type"));
		String shortDescription = StringUtil.trimToNull(data.getParameters().getString("shortDescription"));
		String description = StringUtil.trimToNull(data.getParameters().getString("description"));
		boolean joinable = data.getParameters().getBoolean("joinable");
		String joinerRole = StringUtil.trimToNull(data.getParameters().getString("joinerRole"));
		String icon = StringUtil.trimToNull(data.getParameters().getString("icon"));
		String info = StringUtil.trimToNull(data.getParameters().getString("info"));
		boolean published = data.getParameters().getBoolean("published");
		String skin = StringUtil.trimToNull(data.getParameters().getString("skin"));
		boolean pubView = data.getParameters().getBoolean("pubView");

		// get the site
		SiteEdit site = (SiteEdit) state.getAttribute("site");

		// add if needed
		if (site == null)
		{
			try
			{
				site = SiteService.addSite(id);
				
				// put the site in the state
				state.setAttribute("site", site);
			}
			catch (IdUsedException e)
			{
				addAlert(state,  "The site id is already in use");
				return false;
			}
			catch (IdInvalidException e)
			{
				addAlert(state,  "The site id is invalid");
				return false;
			}
			catch (PermissionException e)
			{
				addAlert(state,  "You do not have permission to edit this Site");
				return false;
			}
		}

		// update
		if (site != null)
		{
			site.setTitle(title);
			site.setShortDescription(shortDescription);
			site.setDescription(description);
			site.setJoinable(joinable);
			site.setJoinerRole(joinerRole);
			site.setIconUrl(icon);
			site.setInfoUrl(info);
			site.setSkin(skin);
			site.setType(type);
			site.setPubView(pubView);
			site.setPublished(published);
		}

		return true;

	}	// readSiteForm

	/**
	* Switch to page display mode within a site edit.
	*/
	public void doPages(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readSiteForm(data, state)) return;

		state.setAttribute("mode", "pages");

	}	// doPages

	/**
	* Handle a request to create a new page in the site edit.
	*/
	public void doNew_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "newPage");

		// make the page so we have the id
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = site.addPage();
		state.setAttribute("page", page);

		// mark the site as new, so on cancel it can be deleted
		state.setAttribute("newPage", "true");

	}	// doNew_page

	/**
	* Edit an existing page.
	*/
	public void doEdit_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "editPage");

		String id = data.getParameters().getString("id");

		// get the page
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = site.getPageEdit(id);
		state.setAttribute("page", page);

	}	// doEdit_page

	/**
	* Move the page up in the order.
	*/
	public void doEdit_page_up(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String id = data.getParameters().getString("id");

		// get the page
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = site.getPageEdit(id);
		state.setAttribute("page", page);
		
		// move it
		page.moveUp();

	}	// doEdit_page_up

	/**
	* Move the page down in the order.
	*/
	public void doEdit_page_down(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String id = data.getParameters().getString("id");

		// get the page
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = site.getPageEdit(id);
		state.setAttribute("page", page);
		
		// move it
		page.moveDown();

	}	// doEdit_page_down

	/**
	* save the page edited, and save the site edit
	*/
	public void doSave_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readPageForm(data, state)) return;

		// done with the page
		state.removeAttribute("page");

		// commit the entire site edit
		doSave_edit(data, context);

	}	// doSave_page

	/**
	* save the page edited, and return to the pages mode
	*/
	public void doDone_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readPageForm(data, state)) return;

		// done with the page
		state.removeAttribute("page");

		// return to main mode
		state.setAttribute("mode", "pages");

	}	// doDone_page

	/**
	* cancel a page edit, return to the pages list
	*/
	public void doCancel_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");

		// if the page was new, remove it
		if ("true".equals(state.getAttribute("newPage")))
		{
			site.removePage(page);
		}

		// %%% do we need the old page around for a restore; did we already modify it? - ggolden

		// done with the page
		state.removeAttribute("page");

		// return to main mode
		state.setAttribute("mode", "pages");

	}	// doCancel_page

	/**
	* Handle a request to remove the page being edited.
	*/
	public void doRemove_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");

		// remove the page (no confirm)
		site.removePage(page);

		// done with the page
		state.removeAttribute("page");

		// return to pages mode
		state.setAttribute("mode", "pages");

	}	// doRemove_page

	/**
	* Switch back to edit main info mode from another edit mode (like pages).
	*/
	public void doEdit_to_main(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		state.setAttribute("mode", "edit");

	}	// doEdit_to_main

	/**
	* Read the site form and update the site in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readPageForm(RunData data, SessionState state)
	{
		// get the page - it's there
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");

		// read the form
		String title = StringUtil.trimToNull(data.getParameters().getString("title"));
		page.setTitle(title);

		try
		{
			// this comes in 1 based, convert to 0 based
			int layout = Integer.parseInt(data.getParameters().getString("layout")) - 1;
			page.setLayout(layout);
		}
		catch (Exception e) { Log.warn("chef", this + ".readPageForm(): reading layout: " + e); }

		if (title == null)
		{
			addAlert(state, "Please specify the page title. ");
			return false;
		}
		else
		{
			return true;
		}

	}	// readPageForm

	/**
	* Switch to tools display mode within a site edit.
	*/
	public void doTools(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readPageForm(data, state)) return;

		state.setAttribute("mode", "tools");

	}	// doTools

	/**
	* create a new tool in the page edit.
	*/
	public void doNew_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "newTool");

		// make the tool so we have the id
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = page.addTool();
		state.setAttribute("tool", tool);

		// mark the site as new, so on cancel it can be deleted
		state.setAttribute("newTool", "true");

	}	// doNew_tool

	/**
	* Edit an existing tool.
	*/
	public void doEdit_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "editTool");

		String id = data.getParameters().getString("id");

		// get the tool
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = page.getToolEdit(id);
		state.setAttribute("tool", tool);

	}	// doEdit_tool

	/**
	* Move the tool up in the order.
	*/
	public void doEdit_tool_up(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String id = data.getParameters().getString("id");

		// get the tool
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = page.getToolEdit(id);
		
		// move it
		tool.moveUp();

	}	// doEdit_tool_up

	/**
	* Move the tool down in the order.
	*/
	public void doEdit_tool_down(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String id = data.getParameters().getString("id");

		// get the tool
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = page.getToolEdit(id);
		
		// move it
		tool.moveDown();

	}	// doEdit_tool_down

	/**
	* save the tool edited, and save the site edit.
	*/
	public void doSave_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readToolForm(data, state)) return;

		// done with the tool
		state.removeAttribute("tool");

		// commit the entire site edit
		doSave_edit(data, context);

	}	// doSave_tool

	/**
	* save the tool edited, and return to the tools mode
	*/
	public void doDone_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readToolForm(data, state)) return;

		// done with the tool
		state.removeAttribute("tool");

		// return to main mode
		state.setAttribute("mode", "tools");

	}	// doDone_tool

	/**
	* save the tool's selected feature, continue editing the tool
	*/
	public void doDone_feature(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readToolFeatureForm(data, state)) return;

		// go into edit mode
		state.setAttribute("mode", "editTool");

	}	// doDone_feature

	/**
	* cancel a tool edit, return to the tools list
	*/
	public void doCancel_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		SiteEdit site = (SiteEdit) state.getAttribute("site");
		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		// if the tool was new, remove it
		if ("true".equals(state.getAttribute("newTool")))
		{
			page.removeTool(tool);
		}

		// %%% do we need the old tool around for a restore; did we already modify it? - ggolden

		// done with the tool
		state.removeAttribute("tool");

		// return to tools mode
		state.setAttribute("mode", "tools");

	}	// doCancel_tool

	/**
	* Handle a request to remove the tool being edited.
	*/
	public void doRemove_tool(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		SitePageEdit page = (SitePageEdit) state.getAttribute("page");
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		// remove the tool (no confirm)
		page.removeTool(tool);

		// done with the tool
		state.removeAttribute("tool");

		// return to tools mode
		state.setAttribute("mode", "tools");

	}	// doRemove_tool

	/**
	* Switch back to edit page info mode from another edit mode (like tools).
	*/
	public void doEdit_to_page(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		state.setAttribute("mode", "editPage");

	}	// doEdit_to_page

	/**
	* Handle a request to regenerate the ids in the site under edit.
	*/
	public void doIds(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the site
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		
		site.regenerateIds();
		addAlert(state, "The Site's Page and Tool IDs have been re-generated.");
	}

	/** 
	* Handle a user clicking on the view-user menu.
	**/	
	public void doToggle_user_search(RunData runData, Context context)
	{
		toggleState(runData, STATE_SEARCH_USER);

	}	// doToggle_user_display

	/** 
	* Toggle the state attribute
	* @param stateName The name of the state attribute to toggle
	**/	
	private void toggleState(RunData runData, String stateName)
	{
		// access the portlet element id to find our state
		// %%% use CHEF api instead of Jetspeed to get state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// toggle the state setting
		boolean newValue = false;
		Boolean oldValue = (Boolean) state.getAttribute(stateName);
		if (oldValue == null)
		{
			newValue = true;
		}
		else
		{
			newValue = ! oldValue.booleanValue();
		}

		state.setAttribute(stateName, new Boolean(newValue));

	}	// toggleState

	/**
	* Read the tool form and update the site in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readToolForm(RunData data, SessionState state)
	{
		// get the tool - it's there
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		// read the form
		String title = data.getParameters().getString("title");
		tool.setTitle(title);

		// read the layout hints
		String hints = data.getParameters().getString("layoutHints");
		tool.setLayoutHints(hints);

		String feature = tool.getToolId();
		if (feature != null)
		{
			// get this feature
			ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
	
			// read in any params
			for (Iterator iParams = reg.getDefaultConfiguration().getPropertyNames(); iParams.hasNext();)
			{
				String paramName = (String) iParams.next();
				String formName = "param_" + paramName;
				String value = data.getParameters().getString(formName);
				if (value != null)
				{
					value = StringUtil.trimToNull(value);
					if (value != null)
					{
						tool.getPropertiesEdit().addProperty(paramName, value);
					}
					else
					{
						tool.getPropertiesEdit().removeProperty(paramName);
					}
				}
			}
		}
		else
		{
			addAlert(state, "Please select a feature for this tool.");
			return false;
		}

		return true;

	}	// readToolForm

	/**
	* Read the tool feature form and update the site in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readToolFeatureForm(RunData data, SessionState state)
	{
		// get the tool - it's there
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute("tool");

		// read the form
		String feature = data.getParameters().getString("feature");

		// get this feature
		ToolRegistration reg = ServerConfigurationService.getToolRegistration(feature);

		// if the feature has changed, update the default configuration
		if (!feature.equals(tool.getToolId()))
		{
			tool.setToolId(feature);
			tool.setTitle(reg.getTitle());

			tool.getPropertiesEdit().clear();
			tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());
		}

		return true;

	}	// readToolFeatureForm

	/**
	* doNew_member called when "eventSubmit_doNew_member" is in the request parameters to get a new member for the site
	*/
/*	public void doNew_member(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readSiteForm(data, state)) return;

		state.setAttribute("mode", "newMember");

	}	// doNew_member
*/

	/**
	* doAdd_member called when "eventSubmit_doAdd_member" is in the request parameters to complete adding a new member for the site
	*/
/*	public void doAdd_member(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// if the form is rejected, stat in this mode
		if (!readUserForm(data, state)) return;

		// go back to edit
		state.setAttribute("mode", "edit");

	}	// doAdd_member
*/
	/**
	* doAdd_member_more called when "eventSubmit_doAdd_member_more" is in the request parameters
	* to add a new member for the site, then stay there for more
	*/
/*	public void doAdd_member_more(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// if the form is rejected, stat in this mode
		if (!readUserForm(data, state)) return;

		// stay in mode
		addAlert(state,  "User added to site.  Enter another, or press Cancel if done.");

	}	// doAdd_member_more
*/
	/**
	* doCancel_member called when "eventSubmit_doCancel_member" is in the request parameters to cancel site edits
	*/
/*	public void doCancel_member(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_member
*/

	/**
	* Read the new member form.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
/*	private boolean readUserForm(RunData data, SessionState state)
	{
		// read the user id from the form
		String userId = data.getParameters().getString("userid");

		// get the site
		SiteEdit site = (SiteEdit) state.getAttribute("site");
		if (site == null)
		{
			addAlert(state,  "You do not have permission to edit this site");
			return false;
		}

		// get the Realm
		RealmEdit realm = (RealmEdit) state.getAttribute("realm");

		try
		{
			// find the user
			User user = UserDirectoryService.getUser(userId);
			
			// give the "access" role
			realm.addUserRole(user, realm.getRole("access"));
		}
		catch (IdUnusedException e)
		{
			addAlert(state,  "The user id is not recognized");
			return false;
		}
		
		return true;

	}	// readUserForm
*/

	/**
	* If the site is set for email, make sure there's a MailArchive channel (mailbox) for it,
	* and if not, make sure there's not one.
	* @param site The site to sync.
	*/
/*	private void syncWithMailArchive(SiteEdit site)
	{
		// if no mail archive service, there's nothing to sync with
		if (MailArchiveService.getInstance() == null) return;

		// get the enabled setting
		boolean emailEnabled = false;
		try
		{
			emailEnabled = site.getProperties().getBooleanProperty(ResourceProperties.PROP_SITE_EMAIL_ARCHIVE);
		}
		catch (Exception e) {}

		// form the site's email channel name
		String channelId = "main";	// %%% what? where defined?
		String context = site.getId();
		String channelRef = MailArchiveService.channelReference(context, channelId);

		// see if there's a channel
		MessageChannel channel = null;
		try
		{
			channel = MailArchiveService.getChannel(channelRef);
		}
		catch (IdUnusedException e) {}
		catch (PermissionException e) {}

		// if enabled, make sure we have an enabled channel
		if (emailEnabled)
		{
			if (channel == null)
			{
				try
				{
					// create a channel and mark it as enabled
					MessageChannelEdit edit = MailArchiveService.addMailArchiveChannel(channelRef);
					edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_CHANNEL_ENABLED, "true");
					MailArchiveService.commitChannel(edit);
					channel = edit;
				}
				catch (IdUsedException e) {}
				catch (IdInvalidException e) {}
				catch (PermissionException e) {}
			}

			// make sure the existing channel is enabled
			else
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
					catch (IdUnusedException ignore) {}
					catch (PermissionException ignore) {}
					catch (InUseException ignore) {}
				}
			}
		}

		// email not enabled, make sure that the channel, if it exists, is not enabled
		else
		{
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
					catch (IdUnusedException ignore) {}
					catch (PermissionException ignore) {}
					catch (InUseException ignore) {}
				}
			}
		}

		// now sync up with notification - if we have that feature
		if (NotificationService.getInstance() == null) return;

		String notificationId = site.getProperties().getProperty(ResourceProperties.PROP_SITE_EMAIL_NOTIFICATION_ID);

		Notification notification = null;		
		if (notificationId != null)
		{
			try
			{
				notification = NotificationService.getNotification(notificationId);
			}
			catch (Exception e) {}
		}

		if (emailEnabled)
		{
			// make sure we have a notification property and that it exists
			if (notification == null)
			{
				// add the notification
				NotificationEdit edit = NotificationService.addNotification();
				// %%% we need a public event() from messageService
				edit.setFunction("mail." + MessageChannel.SECURE_POST_CHANNEL);
				edit.setResourceFilter(MailArchiveService.
						messageReference(context, channelId, ""));
				edit.setAction(new EmailSiteEmailNotification(site.getId()));
				NotificationService.commitEdit(edit);
				notification = edit;
				
				// save the notification id
				site.getPropertiesEdit().addProperty(ResourceProperties.PROP_SITE_EMAIL_NOTIFICATION_ID, notification.getId());
			}
		}

		else
		{
			// make sure any notification we have stored is removed and we forget about it
			if (notification != null)
			{
				try
				{
					NotificationEdit edit = NotificationService.editNotification(notification.getId());
					NotificationService.removeNotification(edit);
					notification = null;
				}
				catch (IdUnusedException ignore) {}
				//catch (PermissionException ignore) {}
				catch (InUseException ignore) {}
			}

			site.getPropertiesEdit().removeProperty(ResourceProperties.PROP_SITE_EMAIL_NOTIFICATION_ID);
		}

	}	// syncWithMailArchive
*/

	/**
	* Clean up all possible state value when done an edit.
	*/
	private void cleanState(SessionState state)
	{
		state.removeAttribute("site");
		state.removeAttribute("page");
		state.removeAttribute("tool");
//		state.removeAttribute("realm");
		state.removeAttribute("new");
		state.removeAttribute("newPage");
		state.removeAttribute("newTool");

	}	// cleanState

	/**
	* Create a list of the valid layout names.
	* @return A List (String) of the value layout names.
	*/
	private List layoutsList()
	{
		List rv = new Vector();
		for (int i = 0; i < SitePage.LAYOUT_NAMES.length; i++)
		{
			rv.add(SitePage.LAYOUT_NAMES[i]);
		}
		return rv;

	}	// layoutsList

}	// SitesAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/SitesAction.java,v 1.20 2005/02/11 21:21:59 janderse.umich.edu Exp $
*
**********************************************************************************/
