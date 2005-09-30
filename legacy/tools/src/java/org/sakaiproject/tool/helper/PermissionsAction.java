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
package org.sakaiproject.tool.helper;

// imports
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.RoleEdit;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
* <p>PermissionsAction is a helper Action that other tools can use to edit their permissions.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class PermissionsAction
{
	
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("helper");
    
	/** State attributes for Permissions mode - when it is MODE_DONE the tool can process the results. */
	public static final String STATE_MODE = "pemissions.mode";

	/** State attribute for the realm id - users should set before starting. */
	public static final String STATE_REALM_ID = "permission.realmId";

	/** State attribute for the realm id - users should set before starting. */
	public static final String STATE_REALM_ROLES_ID = "permission.realmRolesId";

	/** State attribute for the description of what's being edited - users should set before starting. */
	public static final String STATE_DESCRIPTION = "permission.description";

	/** State attribute for the lock/ability string prefix to be presented / edited - users should set before starting. */
	public static final String STATE_PREFIX = "permission.prefix";

	/** State attributes for storing the realm being edited. */
	private static final String STATE_REALM_EDIT = "permission.realm";

	/** State attributes for storing the abilities, filtered by the prefix. */
	private static final String STATE_ABILITIES = "permission.abilities";

	/** State attribute for storing the roles to display. */
	private static final String STATE_ROLES = "permission.roles";

	/** State attribute for storing the abilities of each role for this resource. */
	private static final String STATE_ROLE_ABILITIES = "permission.rolesAbilities";
	
	/** Modes. */
	public static final String MODE_MAIN = "main";

	/** vm files for each mode. TODO: path too hard coded */
	private static final String TEMPLATE_MAIN = "helper/chef_permissions";

	//SAK-2053- separate realm to view dropbox of other users
	private static final String DBOX_STATE_REALM_EDIT = "dropboxpermission.realm";
	private static final String DBOX_STATE_ABILITIES = "dropboxpermission.abilities";
	private static final String DBOX_STATE_ROLES = "dropboxpermission.roles";
	private static final String DBOX_DISPLAY="dropbox.displayall";
	/** 
	* build the context.
	* @return The name of the template to use.
	*/
	static public String buildHelperContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// in state is the realm id
		context.put("thelp",rb);
		String realmId = (String) state.getAttribute(STATE_REALM_ID);

		// in state is the realm to use for roles - if not, use realmId
		String realmRolesId = (String) state.getAttribute(STATE_REALM_ROLES_ID);

		// get the realm locked for editing
		RealmEdit edit = (RealmEdit) state.getAttribute(STATE_REALM_EDIT);
		if (edit == null)
		{
			try
			{
				edit = RealmService.editRealm(realmId);
				state.setAttribute(STATE_REALM_EDIT, edit);
			}
			catch (IdUnusedException e)
			{
				try
				{
					// we can create the realm
					edit = RealmService.addRealm(realmId);
					state.setAttribute(STATE_REALM_EDIT, edit);
				}
				catch (IdInvalidException ee)
				{
					Log.warn("chef", "PermissionsAction.buildHelperContext: addRealm: " + ee);
					cleanupState(state);
					return null;
				}
				catch (IdUsedException ee)
				{
					Log.warn("chef", "PermissionsAction.buildHelperContext: addRealm: " + ee);
					cleanupState(state);
					return null;
				}
				catch (PermissionException ee)
				{
					Log.warn("chef", "PermissionsAction.buildHelperContext: addRealm: " + ee);
					cleanupState(state);
					return null;
				}
			}
			catch (PermissionException e)
			{
				Log.warn("chef", "PermissionsAction.buildHelperContext: editRealm: " + e);
				cleanupState(state);
				return null;
			}
			catch (InUseException e)
			{
				// Log.warn("chef", "PermissionsAction.buildHelperContext: editRealm: " + e);
				cleanupState(state);
				return null;
			}
		}

		// in state is the prefix for abilities to present
		String prefix = (String) state.getAttribute(STATE_PREFIX);

		// in state is the list of abilities we will present
		List abilities = (List) state.getAttribute(STATE_ABILITIES);
		if (abilities == null)
		{
			// get all abilities
			List abilitiesAll = ServerConfigurationService.getLocks();
	
			// filter these for those we are interested in
			abilities = new Vector();
			for (Iterator iLocks = abilitiesAll.iterator(); iLocks.hasNext(); )
			{
				String ability = (String) iLocks.next();
				if (ability.startsWith(prefix))
				{
					abilities.add(ability);
				}
			}

			state.setAttribute(STATE_ABILITIES, abilities);
		}

		// in state is the description of the edit
		String description = (String) state.getAttribute(STATE_DESCRIPTION);

		// the list of roles
		List roles = (List) state.getAttribute(STATE_ROLES);
		if (roles == null)
		{
			// get the roles from the edit, unless another is specified
			Realm roleRealm = edit;
			if (realmRolesId != null)
			{
				try
				{
					roleRealm = RealmService.getRealm(realmRolesId);
				}
				catch (Exception e)
				{
					Log.warn("chef", "PermissionsAction.buildHelperContext: getRolesRealm: " + realmRolesId + " : " + e);
				}
			}
			roles = new Vector();
			roles.addAll(roleRealm.getRoles());
			Collections.sort(roles);
			state.setAttribute(STATE_ROLES, roles);
		}
		// the abilities not including this realm for each role
		Map rolesAbilities = (Map) state.getAttribute(STATE_ROLE_ABILITIES);
		if (rolesAbilities == null)
		{
			rolesAbilities = new Hashtable();
			state.setAttribute(STATE_ROLE_ABILITIES, rolesAbilities);

			// get this resource's role Realms,those that refine the role definitions, but not it's own
			Reference ref = EntityManager.newReference(edit.getId());
			Collection realms = ref.getRealms();
			
			realms.remove(ref.getReference());

			for (Iterator iRoles = roles.iterator(); iRoles.hasNext(); )
			{
				Role role = (Role) iRoles.next();
				Set locks = RealmService.getLocks(role.getId(), realms);
				rolesAbilities.put(role.getId(), locks);
			}
		}

		context.put("realm", edit);
		context.put("prefix", prefix);
		context.put("abilities", abilities);
		context.put("description", description);
		if (roles.size()>0)
		{
			context.put("roles", roles);
		}
		context.put("rolesAbilities", rolesAbilities);

		// set me as the helper class
		state.setAttribute(VelocityPortletPaneledAction.STATE_HELPER, PermissionsAction.class.getName());

		// make sure observers are disabled
		VelocityPortletPaneledAction.disableObservers(state);

		return TEMPLATE_MAIN;

	}	// buildHelperContext

	/**
	* Remove the state variables used internally, on the way out.
	*/
	private static void cleanupState(SessionState state)
	{
		state.removeAttribute(STATE_REALM_ID);
		state.removeAttribute(STATE_REALM_EDIT);
		state.removeAttribute(STATE_PREFIX);
		state.removeAttribute(STATE_ABILITIES);
		state.removeAttribute(STATE_DESCRIPTION);
		state.removeAttribute(STATE_ROLES);
		state.removeAttribute(STATE_ROLE_ABILITIES);
		state.removeAttribute(STATE_MODE);
		state.removeAttribute(VelocityPortletPaneledAction.STATE_HELPER);

		// re-enable observers
		VelocityPortletPaneledAction.enableObservers(state);

	}	// cleanupState

	/**
	* Handle the eventSubmit_doSave command to save the edited permissions.
	*/
	static public void doSave(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		RealmEdit edit = (RealmEdit) state.getAttribute(STATE_REALM_EDIT);

		// read the form, updating the edit
		readForm(data, edit, state);

		// commit the change
		RealmService.commitEdit(edit);

		// clean up state
		cleanupState(state);

	}	// doSave

	/**
	* Handle the eventSubmit_doCancel command to abort the edits.
	*/
	static public void doCancel(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// cancel the edit
		RealmEdit edit = (RealmEdit) state.getAttribute(STATE_REALM_EDIT);
		if (edit != null)
		{
			RealmService.cancelEdit(edit);
		}

		// clean up state
		cleanupState(state);

	}	// doCancel

	/**
	* Read the permissions form.
	*/
	static private void readForm(RunData data, RealmEdit edit, SessionState state)
	{
		List abilities = (List) state.getAttribute(STATE_ABILITIES);
		List roles = (List) state.getAttribute(STATE_ROLES);

		// look for each role's ability field
		for (Iterator iRoles = roles.iterator(); iRoles.hasNext(); )
		{
			Role role = (Role) iRoles.next();

			for (Iterator iLocks = abilities.iterator(); iLocks.hasNext(); )
			{
				String lock = (String) iLocks.next();

				String checked = data.getParameters().getString(role.getId() + lock);
				if (checked != null)
				{
					// we have an ability!  Make sure there's a role
					RoleEdit myRole = edit.getRoleEdit(role.getId());
					if (myRole == null)
					{
						try
						{
							myRole = edit.addRole(role.getId());
						}
						catch (IdUsedException e)
						{
							Log.warn("chef", "PermissionsAction.readForm: addRole after getRoleEdit null: " + role.getId() + " : " + e);
						}
					}
					myRole.add(lock);
				}
				else
				{
					// if we do have this role, make sure there's not this lock
					RoleEdit myRole = edit.getRoleEdit(role.getId());
					if (myRole != null)
					{
						myRole.remove(lock);
					}
				}
			}
		}

	}	// readForm

  /**
   * SAK-2053 
   * This method is called from ResoucesAction to read the status of content.dropbox realm for a role.
   * @param portlet
   * @param context
   * @param data
   * @param state
   * @return
   */
  public static boolean checkDispOthrDropbox(VelocityPortlet portlet, Context context, RunData data, SessionState state)
  {
    boolean dispOthrDropbox=false;
	  
		// in state is the realm id
		String realmId = SiteService.siteReference(PortalService.getCurrentSiteId());
		
		RealmEdit edit = (RealmEdit) state.getAttribute(DBOX_STATE_REALM_EDIT);
		if (edit == null)
		{
      try
      {
        edit =(RealmEdit)RealmService.editRealm(realmId);
        state.setAttribute(DBOX_STATE_REALM_EDIT, edit);
      }
      catch (IdUnusedException e)
      {
        try
        {
          edit=RealmService.addRealm(realmId) ;
          state.setAttribute(DBOX_STATE_REALM_EDIT, edit);
        }
        catch (IdInvalidException e1)
        {
          state.removeAttribute(DBOX_STATE_REALM_EDIT);
          return false;
        }
        catch (IdUsedException e1)
        {
          state.removeAttribute(DBOX_STATE_REALM_EDIT);
          return false;
        }
        catch (PermissionException e1)
        {
          state.removeAttribute(DBOX_STATE_REALM_EDIT);
          return false;
        }
        
      }
      catch (PermissionException e)
      {
        state.removeAttribute(DBOX_STATE_REALM_EDIT);
        return false;
      }
      catch (InUseException e)
      {
        state.removeAttribute(DBOX_STATE_REALM_EDIT);
        return false;
      }
		}
    //abilities
    List abilities = (List) state.getAttribute(DBOX_STATE_ABILITIES);
    if (abilities == null){
      abilities=new Vector();
      for (Iterator iter = ServerConfigurationService.getLocks().iterator(); iter.hasNext();){
        String element = (String) iter.next();
        if(element.startsWith("content.")){
          abilities.add(element) ;
        }
      }
      state.setAttribute(DBOX_STATE_ABILITIES,abilities);
    }
      
    //Role	    
    List roles = (List) state.getAttribute(DBOX_STATE_ROLES);
    if (roles == null){
      Realm roleRealm=edit;
      try
      {
        roleRealm=RealmService.getRealm(realmId) ;
      }
      catch (IdUnusedException e)
      {
        Log.warn("chef", "PermissionsAction.checkDispOthrDropbox: getRolesRealm: " + " : " + e);
      }
      
      roles = new Vector();
      roles.addAll(roleRealm.getRoles());
      Collections.sort(roles);
      state.setAttribute(DBOX_STATE_ROLES,roles);
    }

		// look for each role's ability field
		for (Iterator iRoles = roles.iterator(); iRoles.hasNext(); )
		{
			Role role = (Role) iRoles.next();
			  String lock = "content.dropbox";
			  RoleEdit myRole = edit.getRoleEdit(role.getId());
			  if(myRole.contains(lock))
			  {
			    dispOthrDropbox=true;				  
			  }
		}
		//unlock 
		RealmEdit editlock = (RealmEdit) state.getAttribute(DBOX_STATE_REALM_EDIT);
		if (editlock != null)
		{
			RealmService.cancelEdit(editlock);
			state.removeAttribute(DBOX_STATE_REALM_EDIT);
			state.removeAttribute(DBOX_STATE_ABILITIES);
			state.removeAttribute(DBOX_STATE_ROLES);	
			
			state.removeAttribute(STATE_MODE);				
		}			
	  return dispOthrDropbox;
  }

}	// PermissionsAction



