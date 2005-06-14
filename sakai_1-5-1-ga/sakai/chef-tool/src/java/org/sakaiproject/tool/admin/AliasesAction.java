/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/AliasesAction.java,v 1.12 2005/02/11 21:21:59 janderse.umich.edu Exp $
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
import java.util.List;

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
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.alias.AliasEdit;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.observer.EventObservingCourier;

/**
* <p>AliasesAction is the CHEF aliases editor.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.12 $
*/
public class AliasesAction
	extends PagedResourceActionII
{
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
			String pattern = AliasService.aliasReference("");

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

		// put $action into context for menus, forms and links
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

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
		else
		{
			Log.warn("chef", "AliasesAction: mode: " + mode);
			template = buildListContext(state, context);
		}

		String prefix = (String)getContext(rundata).get("template");		
		return prefix + template;

	}	// buildNormalContext

	/**
	* Build the context for the main list mode.
	*/
	private String buildListContext(SessionState state, Context context)
	{
		// put the service in the context
		context.put("service", AliasService.getInstance());

		// put all aliases into the context
		context.put("aliases", prepPage(state));

		// build the menu
		Menu bar = new Menu();
		if (AliasService.allowAdd())
		{
			bar.add( new MenuEntry("New Alias", null, true, MenuItem.CHECKED_NA, "doNew") );
		}

		// add the paging commands
		addListPagingMenus(bar, state);

		// add the search commands
		addSearchMenus(bar, state);

		// add the refresh commands
		addRefreshMenus(bar, state);

		if (bar.size() > 0)
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		return "_list";

	}	// buildListContext

	/**
	* Build the context for the new alias mode.
	*/
	private String buildNewContext(SessionState state, Context context)
	{
		return "_edit";

	}	// buildNewContext

	/**
	* Build the context for the new alias mode.
	*/
	private String buildEditContext(SessionState state, Context context)
	{
		// name the html form for alias edit fields
		context.put("form-name", "alias-form");

		// get the alias to edit
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");
		context.put("alias", alias);

		// build the menu
		// we need the form fields for the remove...
		boolean menuPopulated = false;
		Menu bar = new Menu();
		if (AliasService.allowRemoveAlias(alias.getId()))
		{
			bar.add( new MenuEntry("Remove Alias", null, true, MenuItem.CHECKED_NA, "doRemove", "alias-form") );
			menuPopulated = true;
		}

		if (menuPopulated)
		{
			state.setAttribute(Menu.STATE_MENU, bar);
			context.put(Menu.CONTEXT_MENU, bar);
		}

		return "_edit";

	}	// buildEditContext

	/**
	* Build the context for the new alias mode.
	*/
	private String buildConfirmRemoveContext(SessionState state, Context context)
	{
		// get the alias to edit
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");
		context.put("alias", alias);

		return "_confirm_remove";

	}	// buildConfirmRemoveContext

	/**
	* doNew called when "eventSubmit_doNew" is in the request parameters to add a new alias
	*/
	public void doNew(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "new");

		// mark the alias as new, so on cancel it can be deleted
		state.setAttribute("new", "true");

		// disable auto-updates while not in list mode
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();

	}	// doNew

	/**
	* doEdit called when "eventSubmit_doEdit" is in the request parameters to edit a alias
	*/
	public void doEdit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");

		// get the alias
		try
		{
			AliasEdit alias = AliasService.edit(id);
			state.setAttribute("alias", alias);
			state.setAttribute("mode", "edit");

			// disable auto-updates while not in list mode
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "AliasesAction.doEdit: alias not found: " + id);

			addAlert(state, "Alias " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to edit Alias " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();
		}
		catch (InUseException e)
		{
			addAlert(state, "Someone else is currently editing this Alias: " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();
		}

	}	// doEdit

	/**
	* doSave called when "eventSubmit_doSave" is in the request parameters to save alias edits
	*/
	public void doSave(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readAliasForm(data, state)) return;

		// commit the change
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");
		if (alias != null)
		{
			AliasService.commit(alias);
		}

		// cleanup
		state.removeAttribute("alias");
		state.removeAttribute("new");

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();

	}	// doSave

	/**
	* doCancel called when "eventSubmit_doCancel" is in the request parameters to cancel alias edits
	*/
	public void doCancel(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the alias
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");
		if (alias != null)
		{
			// if this was a new, delete the alias
			if ("true".equals(state.getAttribute("new")))
			{
				// remove
				try
				{
					AliasService.remove(alias);
				}
				catch (PermissionException e)
				{
					addAlert(state, "You do not have permission to remove Alias " + alias.getId());
				}
			}
			else
			{
				AliasService.cancel(alias);
			}
		}

		// cleanup
		state.removeAttribute("alias");
		state.removeAttribute("new");

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();

	}	// doCancel

	/**
	* doRemove called when "eventSubmit_doRemove" is in the request parameters to confirm removal of the alias
	*/
	public void doRemove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readAliasForm(data, state)) return;

		// go to remove confirm mode
		state.setAttribute("mode", "confirm");

	}	// doRemove

	/**
	* doRemove_confirmed called when "eventSubmit_doRemove_confirmed" is in the request parameters to remove the alias
	*/
	public void doRemove_confirmed(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the alias
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");

		// remove
		try
		{
			AliasService.remove(alias);
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to remove Alias " + alias.getId());
		}

		// cleanup
		state.removeAttribute("alias");
		state.removeAttribute("new");

		// go to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).enable();

	}	// doRemove_confirmed

	/**
	* doCancel_remove called when "eventSubmit_doCancel_remove" is in the request parameters to cancel alias removal
	*/
	public void doCancel_remove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_remove

	/**
	* Read the alias form and update the alias in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readAliasForm(RunData data, SessionState state)
	{
		// read the form
		String id = StringUtil.trimToNull(data.getParameters().getString("id"));
		String target = StringUtil.trimToNull(data.getParameters().getString("target"));

		// get the alias
		AliasEdit alias = (AliasEdit) state.getAttribute("alias");

		// add if needed
		if (alias == null)
		{
			try
			{
				// add the alias, getting an edit clone to it
				alias = AliasService.add(id);
				
				// put the alias in the state
				state.setAttribute("alias", alias);
			}
			catch (IdUsedException e)
			{
				addAlert(state, "The alias id is already in use");
				return false;
			}
			catch (IdInvalidException e)
			{
				addAlert(state, "The alias id is invalid");
				return false;
			}
			catch (PermissionException e)
			{
				addAlert(state, "You do not have permission to edit this Alias");
				return false;
			}
		}

		// update
		if (alias != null)
		{
			alias.setTarget(target);
		}

		return true;

	}	// readAliasForm

	/**
	 * {@inheritDoc}
	 */
	protected List readResourcesPage(SessionState state, int first, int last)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		
		if (search != null)
		{
			return AliasService.searchAliases(search, first, last);
		}

		return AliasService.getAliases(first, last);
	}

	/**
	 * {@inheritDoc}
	 */
	protected int sizeResources(SessionState state)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		
		if (search != null)
		{
			return AliasService.countSearchAliases(search);
		}

		return AliasService.countAliases();
	}

}	// AliasesAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/AliasesAction.java,v 1.12 2005/02/11 21:21:59 janderse.umich.edu Exp $
*
**********************************************************************************/
