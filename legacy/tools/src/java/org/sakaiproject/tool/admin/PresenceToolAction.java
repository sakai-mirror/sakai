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
package org.sakaiproject.tool.admin;

// imports
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.ResourceBundle;

import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuDivider;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.util.courier.PresenceObservingCourier;

/**
* <p>PresenceToolAction is the CHEF presence display tool showing everyone everywhere.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class PresenceToolAction
	extends VelocityPortletPaneledAction
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("admin");
	/** The display modes. */
	protected static final String STATE_DISPLAY_MODE = "display_mode";
	protected static final String MODE_LOCATIONS = "locations";
	protected static final String MODE_SESSIONS = "sessions";
	protected static final String MODE_SERVERS = "servers";

	/**
	* Populate the state object, if needed.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		// setup the observer to notify our main panel
		if (state.getAttribute(STATE_OBSERVER) == null)
		{
			// get the current tool placement
			Placement placement = ToolManager.getCurrentPlacement();

			// location is just placement
			String location = placement.getId();

			// setup the observer to watch for all presence, disabled so we start in manual mode
			PresenceObservingCourier courier = new PresenceObservingCourier(location);
			courier.setResourcePattern(null);
			courier.disable();
			state.setAttribute(STATE_OBSERVER, courier);

			// init the display mode
			state.setAttribute(STATE_DISPLAY_MODE, MODE_SERVERS);
		}

	}   // initState

	/** 
	* build the context for the Main (List) panel
	* @return (optional) template name for this panel
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		context.put("tlang",rb);
		String template = (String)getContext(rundata).get("template");

		if (!SecurityService.isSuperUser())
		{
			addAlert(state, rb.getString("presence.import"));
			return template;
		}

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		PresenceObservingCourier observer = (PresenceObservingCourier) state.getAttribute(STATE_OBSERVER);
		observer.justDelivered();

		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry(rb.getString("presence.locations"), "doLocations"));		
		bar.add( new MenuEntry(rb.getString("presence.sessions"), "doSessions"));		
		bar.add( new MenuEntry(rb.getString("presence.servers"), "doServers"));
		bar.add( new MenuDivider());
		bar.add( new MenuEntry((observer.getEnabled() ? rb.getString("presence.manualref") : rb.getString("presence.autoref")), "doAuto"));
		if (!observer.getEnabled())
		{
			bar.add( new MenuEntry(rb.getString("presence.ref"), "doRefresh"));		
		}
		context.put(Menu.CONTEXT_MENU, bar);

		// for locations list mode
		if (MODE_LOCATIONS.equals(state.getAttribute(STATE_DISPLAY_MODE)))
		{
			template += "-List";

			// get the list of all presence locations
			List locations = PresenceService.getLocations();
			context.put("locations", locations);

			context.put("service", PresenceService.getInstance());
		}

		// for sessions display mode
		else if (MODE_SESSIONS.equals(state.getAttribute(STATE_DISPLAY_MODE)))
		{
			template += ".sessions-List";

			// get sessions by server (keys are already sorted by server)
			Map sessionsByServer = UsageSessionService.getOpenSessionsByServer();
			context.put("servers", sessionsByServer);

			List serverList = new Vector();
			serverList.addAll(sessionsByServer.keySet());
			context.put("serverList", serverList);

			int count = 0;
			for (Iterator i = sessionsByServer.values().iterator(); i.hasNext();)
			{
				List sessions = (List) i.next();
				count += sessions.size();
			}
			context.put("total", new Integer(count));
		}
		
		// for servers display mode
		else if (MODE_SERVERS.equals(state.getAttribute(STATE_DISPLAY_MODE)))
		{
			template += ".servers-List";

			// get the set of all servers with current presence
			Map servers = UsageSessionService.getOpenSessionsByServer();
			context.put("servers", servers);

			List serverList = new Vector();
			serverList.addAll(servers.keySet());
			Collections.sort(serverList);
			context.put("serverList", serverList);

			int count = 0;
			for (Iterator i = servers.values().iterator(); i.hasNext();)
			{
				List sessions = (List) i.next();
				count += sessions.size();
			}
			context.put("total", new Integer(count));
		}

		// the url for the online courier, using a 30 second refresh
		setVmCourier(rundata.getRequest(), 30);

		return template;

	}	// buildMainPanelContext

	/**
	* Switch to locations mode
	*/
	public void doLocations(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// go to remove confirm mode
		state.setAttribute(STATE_DISPLAY_MODE, MODE_LOCATIONS);

	}	// doLocations

	/**
	* Switch to sessions mode
	*/
	public void doSessions(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// go to remove confirm mode
		state.setAttribute(STATE_DISPLAY_MODE, MODE_SESSIONS);

	}	// doSessions

	/**
	* Switch to servers mode
	*/
	public void doServers(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// go to remove confirm mode
		state.setAttribute(STATE_DISPLAY_MODE, MODE_SERVERS);

	}	// doServers

	/**
	* Toggle auto-update
	*/
	public void doAuto(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// get the observer
		PresenceObservingCourier observer = (PresenceObservingCourier) state.getAttribute(STATE_OBSERVER);
		boolean enabled = observer.getEnabled();
		if (enabled)
		{
			observer.disable();
		}
		else
		{
			observer.enable();
		}

	}	// doAuto

	/**
	* The action for when the user want's an update
	*/
	public void doRefresh(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

	}	// doRefresh

}	// PresenceToolAction



