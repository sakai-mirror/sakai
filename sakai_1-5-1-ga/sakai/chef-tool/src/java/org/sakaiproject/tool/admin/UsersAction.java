/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/UsersAction.java,v 1.21 2005/02/11 21:21:59 janderse.umich.edu Exp $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PagedResourceActionII;
import org.sakaiproject.cheftool.PortletConfig;
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
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.Setup;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.observer.EventObservingCourier;

/**
* <p>UsersAction is the CHEF users editor.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.21 $
*/
public class UsersAction
	extends PagedResourceActionII
{
	/**
	 * {@inheritDoc}
	 */
	protected List readResourcesPage(SessionState state, int first, int last)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		
		if (search != null)
		{
			return UserDirectoryService.searchUsers(search, first, last);
		}

		return UserDirectoryService.getUsers(first, last);
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
			return UserDirectoryService.countSearchUsers(search);
		}

		return UserDirectoryService.countUsers();
	}

	/**
	* Populate the state object, if needed.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);
		
		PortletConfig config = portlet.getPortletConfig();

		if (state.getAttribute("single-user") == null)
		{
			state.setAttribute("single-user", new Boolean(config.getInitParameter("single-user", "false")));
			state.setAttribute("include-password", new Boolean(config.getInitParameter("include-password", "true")));
		}

		if (state.getAttribute("create-user") == null)
		{
			state.setAttribute("create-user", new Boolean(config.getInitParameter("create-user", "false")));
			state.setAttribute("create-login", new Boolean(config.getInitParameter("create-login", "false")));
		}

		if (state.getAttribute("create-type") == null)
		{
			state.setAttribute("create-type", config.getInitParameter("create-type", ""));
		}

		if (!(((Boolean) state.getAttribute("single-user")).booleanValue()
				|| ((Boolean) state.getAttribute("create-user")).booleanValue()))
		{
			// setup the observer to notify our main panel
			if (state.getAttribute(STATE_OBSERVER) == null)
			{
				// the delivery location for this tool
				String deliveryId = clientWindowId(state, portlet.getID());
			
				// the html element to update on delivery
				String elementId = mainPanelUpdateId(portlet.getID());
			
				// the event resource reference pattern to watch for
				String pattern = UserDirectoryService.userReference("");

				state.setAttribute(STATE_OBSERVER, new EventObservingCourier(deliveryId, elementId, pattern));
			}

			// make sure the observer is in sync with state
			updateObservationOfChannel(state, portlet.getID());
		}

	}   // initState

	/**
	* Setup our observer to be watching for change events for our channel.
	* @param peid The portlet id.
	*/
	private void updateObservationOfChannel(SessionState state, String peid)
	{
		EventObservingCourier observer = (EventObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			// the delivery location for this tool
			String deliveryId = clientWindowId(state, peid);
			observer.setDeliveryId(deliveryId);
		}

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

		// for the create-user create-login case, we set this in the do so we can process the redirect here
		if (state.getAttribute("redirect") != null)
		{
			state.removeAttribute("redirect");
			sendParentRedirect((HttpServletResponse) CurrentService.getInThread(HttpServletResponse.class.getName()),
					ServerConfigurationService.getPortalUrl());
			return template;
		}

		// put $action into context for menus, forms and links
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

		// check mode and dispatch
		String mode = (String) state.getAttribute("mode");
		boolean singleUser = ((Boolean) state.getAttribute("single-user")).booleanValue();
		boolean createUser = ((Boolean) state.getAttribute("create-user")).booleanValue();
		
		if ((singleUser) && (mode != null) && (mode.equals("edit")))
		{
			template = buildEditContext(state, context);
		}
		else if (singleUser)
		{
			String id = UsageSessionService.getSessionUserId();
			state.setAttribute("user-id", id);
			template = buildViewContext(state, context);
		}
		else if (createUser)
		{
			template = buildCreateContext(state, context);
		}
		else if (mode == null)
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
			Log.warn("chef", "UsersAction: mode: " + mode);
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
		context.put("service", UserDirectoryService.getInstance());

		// put all (internal) users into the context
		context.put("users", prepPage(state));

		// build the menu
		Menu bar = new Menu();
		if (UserDirectoryService.allowAddUser(""))
		{
			bar.add( new MenuEntry("New User", null, true, MenuItem.CHECKED_NA, "doNew") );
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
	* Build the context for the new user mode.
	*/
	private String buildNewContext(SessionState state, Context context)
	{
		// include the password fields?
		context.put("incPw", state.getAttribute("include-password"));

		context.put("incType", Boolean.valueOf(true));

		return "_edit";

	}	// buildNewContext

	/**
	* Build the context for the create user mode.
	*/
	private String buildCreateContext(SessionState state, Context context)
	{
		// is the type to be pre-set
		context.put("type", state.getAttribute("create-type"));

		return "_create";

	}	// buildCreateContext

	/**
	* Build the context for the new user mode.
	*/
	private String buildEditContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "user-form");

		// get the user to edit
		UserEdit user = (UserEdit) state.getAttribute("user");
		context.put("user", user);

		// include the password fields?
		context.put("incPw", state.getAttribute("include-password"));

		// include type fields (not if single user)
		boolean singleUser = ((Boolean) state.getAttribute("single-user")).booleanValue();
		context.put("incType", Boolean.valueOf(!singleUser));

		// build the menu
		// we need the form fields for the remove...
		boolean menuPopulated = false;
		Menu bar = new Menu();
		if ((!singleUser) && (UserDirectoryService.allowRemoveUser(user.getId())))
		{
			bar.add( new MenuEntry("Remove User", null, true, MenuItem.CHECKED_NA, "doRemove", "user-form") );
			menuPopulated = true;
		}

		if (menuPopulated)
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		return "_edit";

	}	// buildEditContext

	/**
	* Build the context for the view user mode.
	*/
	private String buildViewContext(SessionState state, Context context)
	{
		if (Log.getLogger("chef").isDebugEnabled())
		{
			Log.debug("chef",this +".buildViewContext");
		}
		
		// get current user's id
		String id = (String) state.getAttribute("user-id");
		
		// get the user and put in state as "user"
		try
		{
			User user = UserDirectoryService.getUser(id);
			context.put("user", user);

			// name the html form for user edit fields
			context.put("form-name", "user-form");

			state.setAttribute("mode", "view");

			// make sure we can do an edit
			try
			{
				UserEdit edit = UserDirectoryService.editUser(id);
				UserDirectoryService.cancelEdit(edit);
				context.put("enableEdit", "true");
			}
			catch (IdUnusedException e) {}
			catch (PermissionException e) {}
			catch (InUseException e) {}

			// disable auto-updates while not in list mode
			disableObservers(state);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "UsersAction.doEdit: user not found: " + id);

			addAlert(state, "User " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		
		return "_view";

	}	// buildViewContext

	/**
	* Build the context for the new user mode.
	*/
	private String buildConfirmRemoveContext(SessionState state, Context context)
	{
		// get the user to edit
		UserEdit user = (UserEdit) state.getAttribute("user");
		context.put("user", user);

		return "_confirm_remove";

	}	// buildConfirmRemoveContext

	/**
	* doNew called when "eventSubmit_doNew" is in the request parameters to add a new user
	*/
	public void doNew(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "new");

		// mark the user as new, so on cancel it can be deleted
		state.setAttribute("new", "true");

		// disable auto-updates while not in list mode
		disableObservers(state);

	}	// doNew

	/**
	* doEdit called when "eventSubmit_doEdit" is in the request parameters to edit a user
	*/
	public void doEdit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");
		state.removeAttribute("user");
		state.removeAttribute("newuser");

		// get the user
		try
		{
			UserEdit user = UserDirectoryService.editUser(id);
			state.setAttribute("user", user);
			state.setAttribute("mode", "edit");

			// disable auto-updates while not in list mode
			disableObservers(state);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "UsersAction.doEdit: user not found: " + id);

			addAlert(state, "User " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to edit User " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (InUseException e)
		{
			addAlert(state, "Someone else is currently editing this User: " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}

	}	// doEdit

	/**
	* doModify called when "eventSubmit_doModify" is in the request parameters to edit a user
	*/
	public void doModify(RunData data, Context context)
	{
		if (Log.getLogger("chef").isDebugEnabled())
		{
			Log.debug("chef",this +".doModify");
		}
		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");
		state.removeAttribute("user");
		state.removeAttribute("newuser");

		// get the user
		try
		{
			UserEdit user = UserDirectoryService.editUser(id);
			state.setAttribute("user", user);
			state.setAttribute("mode", "edit");
			
			// disable auto-updates while not in list mode
			disableObservers(state);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "UsersAction.doEdit: user not found: " + id);

			addAlert(state, "User " + id + " not found");
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to edit User " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
		catch (InUseException e)
		{
			addAlert(state, "Someone else is currently editing this User: " + id);
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}

	}	// doModify

	/**
	* doSave called when "eventSubmit_doSave" is in the request parameters to save user edits
	*/
	public void doSave(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readUserForm(data, state)) return;

		// commit the change
		UserEdit edit = (UserEdit) state.getAttribute("user");
		if (edit != null)
		{
			UserDirectoryService.commitEdit(edit);
		}

		User user = edit;
		if (user == null)
		{
			user = (User) state.getAttribute("newuser");
		}

		// cleanup
		state.removeAttribute("user");
		state.removeAttribute("newuser");
		state.removeAttribute("new");

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

		if ((user != null) && ((Boolean) state.getAttribute("create-login")).booleanValue())
		{
			// login
			Setup.login(user.getId(), (HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName()));

			// redirect to home (on next build)
			state.setAttribute("redirect", "");
		}

	}	// doSave

	/**
	* doCancel called when "eventSubmit_doCancel" is in the request parameters to cancel user edits
	*/
	public void doCancel(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the user
		UserEdit user = (UserEdit) state.getAttribute("user");
		if (user != null)
		{
			// if this was a new, delete the user
			if ("true".equals(state.getAttribute("new")))
			{
				// remove
				try
				{
					UserDirectoryService.removeUser(user);
				}
				catch (PermissionException e)
				{
					addAlert(state, "You do not have permission to remove User " + user.getId());
				}
			}
			else
			{
				UserDirectoryService.cancelEdit(user);
			}
		}

		// cleanup
		state.removeAttribute("user");
		state.removeAttribute("newuser");
		state.removeAttribute("new");

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

	}	// doCancel

	/**
	* doRemove called when "eventSubmit_doRemove" is in the request parameters to confirm removal of the user
	*/
	public void doRemove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readUserForm(data, state)) return;

		// go to remove confirm mode
		state.setAttribute("mode", "confirm");

	}	// doRemove

	/**
	* doRemove_confirmed called when "eventSubmit_doRemove_confirmed" is in the request parameters to remove the user
	*/
	public void doRemove_confirmed(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the user
		UserEdit user = (UserEdit) state.getAttribute("user");

		// remove
		try
		{
			UserDirectoryService.removeUser(user);
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to remove User " + user.getId());
		}

		// cleanup
		state.removeAttribute("user");
		state.removeAttribute("newuser");
		state.removeAttribute("new");

		// go to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

	}	// doRemove_confirmed

	/**
	* doCancel_remove called when "eventSubmit_doCancel_remove" is in the request parameters to cancel user removal
	*/
	public void doCancel_remove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_remove

	/**
	* Read the user form and update the user in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readUserForm(RunData data, SessionState state)
	{
		// read the form
		String id = StringUtil.trimToNull(data.getParameters().getString("id"));
		String firstName = StringUtil.trimToNull(data.getParameters().getString("first-name"));
		String lastName = StringUtil.trimToNull(data.getParameters().getString("last-name"));
		String email = StringUtil.trimToNull(data.getParameters().getString("email"));
		String pw = StringUtil.trimToNull(data.getParameters().getString("pw"));
		
		String mode = (String) state.getAttribute("mode");
		boolean singleUser = ((Boolean) state.getAttribute("single-user")).booleanValue();
		boolean createUser = ((Boolean) state.getAttribute("create-user")).booleanValue();
		
		boolean typeEnable = false;
		String type = null;
		if ((mode != null) && (mode.equalsIgnoreCase("new")))
		{
			typeEnable = true;
		}
		else if ((mode != null) && (mode.equalsIgnoreCase("edit")) && (!singleUser))
		{
			typeEnable = true;
		}
		
		if (typeEnable)
		{
			// for the case of Admin User tool creating new user
			type = StringUtil.trimToNull(data.getParameters().getString("type"));
		}
		else
		{
			if (createUser)
			{
				// for the case of Gateway Account tool creating new user
				type = (String) state.getAttribute("create-type");
			}
		}

		// get the user
		UserEdit user = (UserEdit) state.getAttribute("user");

		// add if needed
		if (user == null)
		{
			try
			{
				// add the user in one step so that all you need is add not update permission
				// (the added might be "anon", and anon has add but not update permission)
				User newUser = UserDirectoryService.addUser(id, firstName, lastName, email, pw, type, null);
				
				// put the user in the state
				state.setAttribute("newuser", newUser);
			}
			catch (IdUsedException e)
			{
				addAlert(state, "The user id is already in use");
				return false;
			}
			catch (IdInvalidException e)
			{
				addAlert(state, "The user id is invalid");
				return false;
			}
			catch (PermissionException e)
			{
				addAlert(state, "You do not have permission to edit this User");
				return false;
			}
		}

		// update
		else
		{
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			if (pw != null) user.setPassword(pw);
			user.setType(type);
		}

		return true;

	}	// readUserForm

}	// UsersAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/UsersAction.java,v 1.21 2005/02/11 21:21:59 janderse.umich.edu Exp $
*
**********************************************************************************/
