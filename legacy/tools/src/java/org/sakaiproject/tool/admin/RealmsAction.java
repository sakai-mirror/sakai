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
import java.util.List;
import java.util.ResourceBundle;
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
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.courier.EventObservingCourier;
import org.sakaiproject.util.java.StringUtil;

/**
* <p>RealmsAction is the CHEF realms editor.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class RealmsAction
	extends PagedResourceActionII
{
	
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("admin");
    
	/**
	 * {@inheritDoc}
	 */
	
	
	protected List readResourcesPage(SessionState state, int first, int last)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		
		return AuthzGroupService.getAuthzGroups(search, new PagingPosition(first, last));
	}

	/**
	 * {@inheritDoc}
	 */
	protected int sizeResources(SessionState state)
	{
		// search?
		String search = StringUtil.trimToNull((String) state.getAttribute(STATE_SEARCH));
		
		return AuthzGroupService.countAuthzGroups(search);
	}

	/**
	* Populate the state object, if needed.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

//		// setup the observer to notify our main panel
//		if (state.getAttribute(STATE_OBSERVER) == null)
//		{
//			// the delivery location for this tool
//			String deliveryId = clientWindowId(state, portlet.getID());
//
//			// the html element to update on delivery
//			String elementId = mainPanelUpdateId(portlet.getID());
//
//			// the event resource reference pattern to watch for
//			String pattern = AuthzGroupService.realmReference("");
//
//			state.setAttribute(STATE_OBSERVER, new EventObservingCourier(deliveryId, elementId, pattern));
//		}

	}   // initState

	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		context.put("tlang",rb);
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

		else if (mode.equals("newRole"))
		{
			template = buildNewRoleContext(state, context);
		}
		else if (mode.equals("editRole"))
		{
			template = buildEditRoleContext(state, context);
		}
		else if (mode.equals("saveasRole"))
		{
			template = buildSaveasRoleContext(state, context);
		}

		else if (mode.equals("newUser"))
		{
			template = buildNewUserContext(state, context);
		}
		else if (mode.equals("editUser"))
		{
			template = buildEditUserContext(state, context);
		}

		else
		{
			Log.warn("chef", "RealmsAction: mode: " + mode);
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
		// prepare the paging of realms
		List realms = prepPage(state);

		// put the service in the context (used for allow update calls on each realm)
		context.put("service", AuthzGroupService.getInstance());

		// put all realms into the context
		context.put("realms", realms);

		// build the menu
		Menu bar = new Menu();
		if (AuthzGroupService.allowAdd(""))
		{
			bar.add( new MenuEntry(rb.getString("realm.new"), "doNew") );
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

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		justDelivered(state);

		return "_list";

	}	// buildListContext

	/**
	* Build the context for the new realm mode.
	*/
	private String buildNewContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "realm-form");

		return "_edit";

	}	// buildNewContext

	/**
	* Build the context for the edit realm mode.
	*/
	private String buildEditContext(SessionState state, Context context)
	{
		// get the realm to edit
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		// get the roles defined in the realm
		List roles = new Vector();
		roles.addAll(realm.getRoles());
		Collections.sort(roles);
		context.put("roles", roles);

		// get a list of the users who have individual grants in the realm
		List grants = new Vector();
		grants.addAll(realm.getMembers());
		Collections.sort(grants);
		context.put("grants", grants);

		// name the html form for user edit fields
		context.put("form-name", "realm-form");

		// build the menu
		// we need the form fields for the remove...
		Menu bar = new Menu();
		if (AuthzGroupService.allowRemove(realm.getId()))
		{
			bar.add( new MenuEntry(rb.getString("realm.remove"), null, true, MenuItem.CHECKED_NA, "doRemove", "realm-form") );
		}
		
		bar.add( new MenuEntry(rb.getString("realm.add"), null, true, MenuItem.CHECKED_NA, "doNew_role", "realm-form") );
		bar.add( new MenuEntry(rb.getString("realm.grant"), null, true, MenuItem.CHECKED_NA, "doNew_user", "realm-form") );

		bar.add( new MenuEntry(rb.getString("realm.save"), null, true, MenuItem.CHECKED_NA, "doSaveas_request", "realm-form") );

		context.put(Menu.CONTEXT_MENU, bar);

		return "_edit";

	}	// buildEditContext

	/**
	* Build the context for the new realm mode.
	*/
	private String buildConfirmRemoveContext(SessionState state, Context context)
	{
		// get the realm to edit
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		return "_confirm_remove";

	}	// buildConfirmRemoveContext

	/**
	* Build the context for the role save as
	*/
	private String buildSaveasRoleContext(SessionState state, Context context)
	{
		// get the realm to edit
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		// get the role
		Role role = (Role) state.getAttribute("role");
		context.put("role", role);

		return "_saveas_role";

	}	// buildSaveasRoleContext

	/**
	* Build the context for the new role mode.
	*/
	private String buildNewRoleContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "role-form");

		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		// get all abilities
		List allLocks = ServerConfigurationService.getLocks();
		Collections.sort(allLocks);
		context.put("allLocks", allLocks);

		// get all roles
		List allRoles = new Vector();
		allRoles.addAll(realm.getRoles());
		Collections.sort(allRoles);
		context.put("allRoles", allRoles);

		return "_edit_role";

	}	// buildNewRoleContext

	/**
	* Build the context for the edit role mode.
	*/
	private String buildEditRoleContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "role-form");

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		// get the role
		Role role = (Role) state.getAttribute("role");
		context.put("role", role);

		// get all abilities
		List allLocks = ServerConfigurationService.getLocks();
		Collections.sort(allLocks);
		context.put("allLocks", allLocks);

		// get all roles
		List allRoles = new Vector();
		allRoles.addAll(realm.getRoles());
		Collections.sort(allRoles);
		context.put("allRoles", allRoles);

		// build the menu 
		Menu bar = new Menu();
		bar.add( new MenuEntry(rb.getString("realm.removerol"), null, true, MenuItem.CHECKED_NA, "doRemove_role") );
		bar.add( new MenuEntry(rb.getString("realm.copyrol"), null, true, MenuItem.CHECKED_NA, "doSaveas_role", "role-form") );
		context.put(Menu.CONTEXT_MENU, bar);

		return "_edit_role";

	}	// buildEditRoleContext

	/**
	* Build the context for the new user grant mode.
	*/
	private String buildNewUserContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "user-form");

		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		context.put("realm", realm);

		// get all roles
		List allRoles = new Vector();
		allRoles.addAll(realm.getRoles());
		Collections.sort(allRoles);
		context.put("allRoles", allRoles);

		return "_edit_user";

	}	// buildNewUserContext

	/**
	* Build the context for the edit user grant mode.
	*/
	private String buildEditUserContext(SessionState state, Context context)
	{
		// name the html form for user edit fields
		context.put("form-name", "user-form");

		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		User user = (User) state.getAttribute("user");

		context.put("realm", realm);
		context.put("user", user);

		// get this user's role - only if not provided
		Member grant = realm.getMember(user.getId());
		if ((grant != null) && (!grant.isProvided()) && (grant.getRole() != null))
		{
			context.put("roles", grant.getRole());
		}

		// get all roles
		List allRoles = new Vector();
		allRoles.addAll(realm.getRoles());
		Collections.sort(allRoles);
		context.put("allRoles", allRoles);

		// build the menu 
		Menu bar = new Menu();
		bar.add( new MenuEntry(rb.getString("realm.removeall"), null, true, MenuItem.CHECKED_NA, "doRemove_user") );
		context.put(Menu.CONTEXT_MENU, bar);

		return "_edit_user";

	}	// buildEditUserContext

	/**
	* Build the context for the save-as mode.
	*/
	private String buildSaveasContext(SessionState state, Context context)
	{
		// get the realm being edited
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		context.put("realm", realm);

		return "_saveas";

	}	// buildSaveasContext

	/**
	* Handle a request to save-as the realm as a new realm.
	*/
	public void doSaveas(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form
		String id = data.getParameters().getString("id");

		// get the realm to copy from
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		try
		{
			// make a new site with this id and as a structural copy of site
			AuthzGroup newRealm = AuthzGroupService.addAuthzGroup(id, realm, UserDirectoryService.getCurrentUser().getId());
		}
		catch (IdUsedException e)
		{
			addAlert(state,  rb.getString("realm.iduse"));
			return;
		}
		catch (IdInvalidException e)
		{
			addAlert(state,  rb.getString("realm.idinvalid"));
			return;
		}
		catch (PermissionException e)
		{
			addAlert(state,  rb.getString("realm.notpermis"));
			return;
		}

		doCancel(data, context);

		// TODO: hard coding this frame id is fragile, portal dependent, and needs to be fixed -ggolden
		schedulePeerFrameRefresh("sitenav");

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
	* Go into saveas mode
	*/
	public void doSaveas_request(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		// go to saveas mode
		state.setAttribute("mode", "saveas");

	}	// doSaveas_request

	/**
	* Handle a request for a new realm.
	*/
	public void doNew(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "new");

		// mark the realm as new, so on cancel it can be deleted
		state.setAttribute("new", "true");

		// disable auto-updates while in view mode
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();

	}	// doNew

	/**
	* Handle a request to edit a realm.
	*/
	public void doEdit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String id = data.getParameters().getString("id");

		// get the realm
		try
		{
			AuthzGroup realm = AuthzGroupService.getAuthzGroup(id);
			state.setAttribute("realm", realm);

			state.setAttribute("mode", "edit");

			// disable auto-updates while in view mode
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "RealmsAction.doEdit: realm not found: " + id);

			addAlert(state, rb.getString("realm.realm") + " " + id + " " + rb.getString("realm.notfound"));
			state.removeAttribute("mode");

			// make sure auto-updates are enabled
			enableObserver(state);
		}
//		catch (PermissionException e)
//		{
//			addAlert(state, rb.getString("realm.notpermis1") + " " + id);
//			state.removeAttribute("mode");
//
//			// make sure auto-updates are enabled
//			enableObserver(state);
//		}

	}	// doEdit

	/**
	* Handle a request to save the realm edit (from the realm edit form).
	*/
	public void doSave(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		doSave_edit(data, context);

	}	// doSave

	/**
	* Handle a request to save the edit from either page or tools list mode - no form to read in.
	*/
	public void doSave_edit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// commit the change
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		if (realm != null)
		{
			try
			{
				AuthzGroupService.save(realm);
			}
			catch (IdUnusedException e)
			{
				// TODO: IdUnusedException
			}
			catch (PermissionException e)
			{
				// TODO: PermissionException
			}
		}

		// cleanup
		cleanState(state);

		// return to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

		// TODO: hard coding this frame id is fragile, portal dependent, and needs to be fixed -ggolden
		schedulePeerFrameRefresh("sitenav");

	}	// doSave_edit

	/**
	* doCancel called when "eventSubmit_doCancel" is in the request parameters to cancel realm edits
	*/
	public void doCancel(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		if (realm != null)
		{
			// if this was a new, delete the realm
			if ("true".equals(state.getAttribute("new")))
			{
				// remove the realm
				try
				{
					AuthzGroupService.removeAuthzGroup(realm);
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("realm.notpermis2") + " " + realm.getId());
				}
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
	* doRemove called when "eventSubmit_doRemove" is in the request parameters to confirm removal of the realm
	*/
	public void doRemove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		// go to remove confirm mode
		state.setAttribute("mode", "confirm");

		// disable auto-updates while in view mode
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();

	}	// doRemove

	/**
	* doRemove_confirmed called when "eventSubmit_doRemove_confirmed" is in the request parameters to remove the realm
	*/
	public void doRemove_confirmed(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		
		// remove the realm
		try
		{
			AuthzGroupService.removeAuthzGroup(realm);
		}
		catch (PermissionException e)
		{
			addAlert(state, rb.getString("realm.notpermis2") + " " + realm.getId());
		}

		// cleanup
		cleanState(state);

		// go to main mode
		state.removeAttribute("mode");

		// make sure auto-updates are enabled
		enableObserver(state);

	}	// doRemove_confirmed

	/**
	* doCancel_remove called when "eventSubmit_doCancel_remove" is in the request parameters to cancel realm removal
	*/
	public void doCancel_remove(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_remove

	/**
	* Read the realm form and update the realm in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readRealmForm(RunData data, SessionState state)
	{
		// read the form
		String id = data.getParameters().getString("id");
		String provider = data.getParameters().getString("provider");
		String maintain = data.getParameters().getString("maintain");

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		// add if needed
		if (realm == null)
		{
			try
			{
				realm = AuthzGroupService.addAuthzGroup(id);
				
				// put the realm in the state
				state.setAttribute("realm", realm);
			}
			catch (IdUsedException e)
			{
				addAlert(state, rb.getString("realm.iduse"));
				return false;
			}
			catch (IdInvalidException e)
			{
				addAlert(state, rb.getString("realm.idinvalid"));
				return false;
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("realm.notpermis3"));
				return false;
			}
		}

		// update
		if (realm != null)
		{
			realm.setProviderGroupId(provider);
			realm.setMaintainRole(maintain);
		}

		return true;

	}	// readRealmForm

	/**
	* Handle a request to create a new role in the realm edit.
	*/
	public void doNew_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute("mode", "newRole");

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		// mark the realm as new, so on cancel it can be deleted
		state.setAttribute("newRole", "true");

	}	// doNew_role

	/**
	* Edit an existing page.
	*/
	public void doEdit_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		state.setAttribute("mode", "editRole");

		String id = data.getParameters().getString("target");

		// get the role
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		Role role = realm.getRole(id);
		state.setAttribute("role", role);

	}	// doEdit_role

	/**
	* Handle a request to remove the role being edited.
	*/
	public void doRemove_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");
		Role role = (Role) state.getAttribute("role");

		// remove the role (no confirm)
		realm.removeRole(role.getId());

		// done with the role
		state.removeAttribute("role");

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doRemove_role

	/**
	* Handle a request to remove the role being edited.
	*/
	public void doSaveas_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRoleForm(data, state)) return;

		// go to saveas_role mode
		state.setAttribute("mode", "saveasRole");

	}	// doSaveas_role

	/**
	* Handle a request to saveas with this name.
	*/
	public void doSave_role_as(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form
		if (!readRoleSaveAsForm(data, state)) return;

		// cleanup
		state.removeAttribute("role");

		// go back to edit mode
		state.setAttribute("mode", "edit");

	}	// doSave_role_as

	/**
	* Handle a request to cancel role saveas.
	*/
	public void doCancel_role_saveas(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// cleanup
		state.removeAttribute("role");

		// go back to edit mode
		state.setAttribute("mode", "edit");

	}	// doCancel_role_saveas

	/**
	* Handle a request to save the realm edit (from the role edit form).
	*/
	public void doSave_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRoleForm(data, state)) return;

		doSave_edit(data, context);

	}	// doSave_role

	/**
	* Handle a request to be done role editing - return to the edit mode.
	*/
	public void doDone_role(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRoleForm(data, state)) return;

		// cleanup
		state.removeAttribute("role");

		// go back to edit mode
		state.setAttribute("mode", "edit");

	}	// doDone_role

	/**
	* Read the user form and update the realm in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readRoleForm(RunData data, SessionState state)
	{
		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		// get the locks
		String[] locks = data.getParameters().getStrings("locks");

		// we are setting for either a new role or this role
		Role role = (Role) state.getAttribute("role");
		if (role == null)
		{
			// read the form
			String id = StringUtil.trimToNull(data.getParameters().getString("id"));
			
			// if the field is missing, and there are no locks, just be done with no change
			if ((id == null) && (locks == null)) return true;

			if (id == null)
			{
				addAlert(state, rb.getString("realm.please"));
				return false;
				// TODO: would be nice to read the locks, and restore them when the form returns -ggolden
			}
			
			// create the role
			try
			{
				role = realm.addRole(id);
			}
			catch (IdUsedException e)
			{
				addAlert(state, rb.getString("realm.arole") + id + rb.getString("realm.defined"));
				return false;
				// TODO: would be nice to read the locks, and restore them when the form returns -ggolden
			}
		}

		// clear out the role
		role.disallowAll();

		// description
		role.setDescription(StringUtil.trimToNull(data.getParameters().getString("description")));

		// for each lock set, give it to the role
		if (locks != null)
		{
			for (int i = 0; i < locks.length; i++)
			{
				role.allowFunction(locks[i]);
			}
		}

		return true;

	}	// readRoleForm

	/**
	* Read the role save as form and make the new role in the realm in edit.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readRoleSaveAsForm(RunData data, SessionState state)
	{
		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		// we will copy this role
		Role role = (Role) state.getAttribute("role");

		// read the form
		String id = StringUtil.trimToNull(data.getParameters().getString("id"));

		if (id == null)
		{
			addAlert(state, rb.getString("realm.please"));
			return false;
		}
		
		// create the role
		try
		{
			Role newRole = realm.addRole(id, role);
		}
		catch (IdUsedException e)
		{
			addAlert(state, rb.getString("realm.arole") + id + rb.getString("realm.defined"));
			return false;
		}

		return true;

	}	// readRoleSaveAsForm

	/**
	* create a new user ability grant in the realm edit.
	*/
	public void doNew_user(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		state.setAttribute("mode", "newUser");

		// mark the realm as new, so on cancel it can be deleted
		state.setAttribute("newUser", "true");

	}	// doNew_user

	/**
	* Edit an existing user ability grant.
	*/
	public void doEdit_user(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readRealmForm(data, state)) return;

		String id = data.getParameters().getString("target");
		try
		{
			User user = UserDirectoryService.getUser(id);
			state.setAttribute("user", user);
			state.setAttribute("mode", "editUser");
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", this + "doEdit_user(): user not found: " + id);
			state.setAttribute("message", "internal error: user not found.");
		}

	}	// doEdit_user

	/**
	* Handle a request to remove all grants to the user.
	*/
	public void doRemove_user(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// the user we are editing
		User user = (User) state.getAttribute("user");

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		// clear out this user's settings
		realm.removeMember(user.getId());

		// done with the user
		state.removeAttribute("user");

		// return to edit mode
		state.setAttribute("mode", "edit");

	}	// doRemove_user

	/**
	* Handle a request to save the realm edit (from the user edit form).
	*/
	public void doSave_user(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readUserForm(data, state)) return;

		doSave_edit(data, context);

	}	// doSave_user

	/**
	* Handle a request to be done user editing - return to the edit mode.
	*/
	public void doDone_user(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// read the form - if rejected, leave things as they are
		if (!readUserForm(data, state)) return;

		// cleanup
		state.removeAttribute("user");

		// go back to edit mode
		state.setAttribute("mode", "edit");

	}	// doDone_user

	/**
	* Read the user form and update the realm in state.
	* @return true if the form is accepted, false if there's a validation error (an alertMessage will be set)
	*/
	private boolean readUserForm(RunData data, SessionState state)
	{
		// get the role
		String roles = StringUtil.trimToNull(data.getParameters().getString("roles"));

		// we are setting for either a new user or this user
		User user = (User) state.getAttribute("user");
		if (user == null)
		{
			// read the form
			String id = StringUtil.trimToNull(data.getParameters().getString("id"));
			
			// if the field is missing, and there are no roles, just be done with no change
			if ((id == null) && (roles == null)) return true;

			try
			{
				user = UserDirectoryService.getUser(id);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, rb.getString("realm.user"));
				return false;
				// TODO: would be nice to read the roles, and restore them when the form returns -ggolden
			}
		}

		// get the realm
		AuthzGroup realm = (AuthzGroup) state.getAttribute("realm");

		// if the user is set to have the same role the user already has, do nothing
		Member grant = realm.getMember(user.getId());

		// if no change, change nothing
		if ((roles == null) && ((grant == null) || (grant.isProvided()))) return true;
		if ((roles != null) && (grant != null) && (grant.getRole().getId().equals(roles)) && !grant.isProvided()) return true;

		// clear out this user's settings
		realm.removeMember(user.getId());

		// if there's a role, give it
		if (roles != null)
		{
			// TODO: active, provided
			realm.addMember(user.getId(), roles, true, false);
		}

		return true;

	}	// readUserForm

	/**
	* Clean up all possible state value when done an edit.
	*/
	private void cleanState(SessionState state)
	{
		state.removeAttribute("realm");
		state.removeAttribute("role");
		state.removeAttribute("user");
		state.removeAttribute("new");
		state.removeAttribute("newRole");
		state.removeAttribute("newUser");
		state.removeAttribute("allRoles");
		state.removeAttribute("allLocks");
		state.removeAttribute("roles");
		state.removeAttribute("locks");

	}	// cleanState

}	// RealmsAction



