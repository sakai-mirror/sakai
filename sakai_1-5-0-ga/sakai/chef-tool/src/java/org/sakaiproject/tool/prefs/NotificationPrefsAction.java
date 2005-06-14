/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/NotificationPrefsAction.java,v 1.7 2005/01/10 22:06:33 csev.umich.edu Exp $
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

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.PreferencesEdit;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
* <p>NotificationPrefsAction is the CHEF tool to let a user view and set their notification preferences.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class NotificationPrefsAction
	extends VelocityPortletPaneledAction
{
	/** Edit mode state. */
	protected final static String MODE_EDIT = "edit";

	/** State that holds the edit. */
	protected final static String STATE_EDIT = "edit";

	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		context.put("form_submit", BUTTON + "doSave");
		context.put("form_cancel", BUTTON + "doCancel");

		// get the notification preferences for the current user
		User user = UserDirectoryService.getCurrentUser();
		String id = user.getId();
		context.put("preferences_for", user.getDisplayName());

		Preferences prefs = PreferencesService.getPreferences(id);

/*
		ResourceProperties props = prefs.getProperties(NotificationService.PREFS_DEFAULT);
		int i = NotificationService.PREF_NONE;
		try
		{
			i = (int) props.getLongProperty(Integer.toString(NotificationService.NOTI_OPTIONAL));
		}
		catch (Throwable ignore) {};
		context.put("default", new Integer(i));
*/

		// type specific: %%% these are hard coded! -ggolden
		buildTypePrefsContext(AnnouncementService.SERVICE_NAME, "annc", context, prefs);
		buildTypePrefsContext(MailArchiveService.SERVICE_NAME, "mail", context, prefs);
		buildTypePrefsContext(ContentHostingService.SERVICE_NAME, "rsrc", context, prefs);
		
		int i = getIntegerPref(PortalService.SERVICE_NAME, "refresh",prefs);
		context.put("refresh", new Integer(i));
		/*
		buildTypePrefsContext(CalendarService.SERVICE_NAME, "schd", context, prefs);
*/

		if (MODE_EDIT.equals(state.getAttribute(STATE_MODE)))
		{
			return (String)getContext(rundata).get("template") + "_edit";
		}
		else
		{
			// build the menu
			Menu bar = new Menu(portlet, rundata, (String) state.getAttribute(STATE_ACTION));
			bar.add( new MenuEntry("Edit", "doEdit"));
			context.put(Menu.CONTEXT_MENU, bar);
		}

		return (String)getContext(rundata).get("template");

	}	// buildMainPanelContext

	/**
	* Handle the edit action.
	*/
	public void doEdit(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		
		// get the notification preferences for the current user
		String id = UsageSessionService.getSessionUserId();

		// get an edit
		try
		{
			PreferencesEdit edit = PreferencesService.edit(id);
			state.setAttribute(STATE_EDIT, edit);
	
			state.setAttribute(STATE_MODE, MODE_EDIT);
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to edit these preferences.");
		}
		catch (InUseException e)
		{
			addAlert(state, "These preferences are currently being edited elsewhere.");
		}
		catch (IdUnusedException e)
		{
			// add prefs for this id
			try
			{
				PreferencesEdit edit = PreferencesService.add(id);
				state.setAttribute(STATE_EDIT, edit);
		
				state.setAttribute(STATE_MODE, MODE_EDIT);
			}
			catch (PermissionException e2)
			{
				addAlert(state, "You do not have permission to edit these preferences.");
			}
			catch (IdUsedException e2)
			{
				addAlert(state, "For some reason, these preferences cannot be edited just now.");
				Log.warn("chef", this + ".doSave(): whil adding after IdUnused: " + e2);
			}
		}
	}	// doEdit

	/**
	* Handle the save action.
	*/
	public void doSave(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		PreferencesEdit edit = (PreferencesEdit) state.getAttribute(STATE_EDIT);

		// update the default settings from the form
		ResourcePropertiesEdit props = edit.getPropertiesEdit(NotificationService.PREFS_DEFAULT);

		// read the defaults
/*
		props.addProperty(Integer.toString(NotificationService.NOTI_OPTIONAL),
				data.getParameters().getString("default"));
*/
		// read each type specific %%% hard coded! -ggolden
		readTypePrefs(AnnouncementService.SERVICE_NAME, "annc", edit, data);
		readTypePrefs(MailArchiveService.SERVICE_NAME, "mail", edit, data);
		readTypePrefs(ContentHostingService.SERVICE_NAME, "rsrc", edit, data);
		
		setStringPref(PortalService.SERVICE_NAME, "refresh", edit, data.getParameters().getString("refresh"));
		
		/*
		readTypePrefs(CalendarService.SERVICE_NAME, "schd", edit, data);
		*/

		// update the edit and release it
		PreferencesService.commit(edit);

		state.removeAttribute(STATE_EDIT);
		state.removeAttribute(STATE_MODE);

	}	// doSave

	/**
	* Handle the cancel action.
	*/
	public void doCancel(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		// update the edit and release it
		PreferencesEdit edit = (PreferencesEdit) state.getAttribute(STATE_EDIT);

		PreferencesService.cancel(edit);

		state.removeAttribute(STATE_EDIT);
		state.removeAttribute(STATE_MODE);

	}	// doCancel

	/**
	* Retrieve a preference and convert it to an integer
	* @param pres_base The name of the group of properties (i.e. a service name)
	* @param type The particular property
	* @param prefs The full set of preferences for the current logged in user.
	*/
	protected int getIntegerPref(String pref_base, String type, Preferences prefs)
	{
		ResourceProperties props = prefs.getProperties(pref_base);
		int i = 0;
		try
		{
			i = (int) props.getLongProperty(type);
		}
		catch (Throwable ignore) {};
		return i;

	}	// getIntegerPref
	
	/**
	* Set an integer preference.
	* @param pres_base The name of the group of properties (i.e. a service name)
	* @param type The particular property
	* @param edit An edit version of the full set of preferences for the current logged in user.
	* @param newval The string to be the new preference.
	*/
	protected void setStringPref(String pref_base, String type, PreferencesEdit edit, String newval)
	{
		ResourcePropertiesEdit props = edit.getPropertiesEdit(pref_base);

		props.addProperty(type,newval);		
	}	// setStringPref
	
	/**
	* Add the two context references for defaults for this type.
	* @param type The resource type (i.e. a service name).
	* @param prefix The prefix for context references.
	* @param context The context.
	* @param prefs The full set of preferences.
	*/
	protected void buildTypePrefsContext(String type, String prefix, Context context, Preferences prefs)
	{
		ResourceProperties props = prefs.getProperties(NotificationService.PREFS_TYPE + type);
		int i = NotificationService.PREF_NONE;
		try
		{
			i = (int) props.getLongProperty(Integer.toString(NotificationService.NOTI_OPTIONAL));
		}
		catch (Throwable ignore) {};
		context.put(prefix, new Integer(i));

	}	// buildTypePrefsContext

	/**
	* Read the two context references for defaults for this type from the form.
	* @param type The resource type (i.e. a service name).
	* @param prefix The prefix for context references.
	* @param edit The preferences being edited.
	* @param data The rundata with the form fields.
	*/
	protected void readTypePrefs(String type, String prefix, PreferencesEdit edit, RunData data)
	{
		// update the default settings from the form
		ResourcePropertiesEdit props = edit.getPropertiesEdit(NotificationService.PREFS_TYPE + type);

		// read the defaults
		props.addProperty(Integer.toString(NotificationService.NOTI_OPTIONAL),
				data.getParameters().getString(prefix));

	}	// readTypePrefs

}	// NotificationPrefsAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/prefs/NotificationPrefsAction.java,v 1.7 2005/01/10 22:06:33 csev.umich.edu Exp $
*
**********************************************************************************/
