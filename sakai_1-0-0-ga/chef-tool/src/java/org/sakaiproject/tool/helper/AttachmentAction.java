/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/helper/AttachmentAction.java,v 1.15 2004/09/30 20:19:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.helper;

// imports
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ContentHostingComparator;
import org.sakaiproject.util.FileItem;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Validator;

/**
* <p>AttachmentAction is a helper Action that other tools can use to edit their attachments.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.15 $
*/
public class AttachmentAction
{
	/** State attributes for Attachments mode - when it's MODE_DONE the tool can process the results. */
	public static final String STATE_MODE = "attachment.mode";

	/** State attribute for where there is at least one attachment before invoking attachment tool */
	public static final String STATE_HAS_ATTACHMENT_BEFORE = "attachment.has_attachment_before";
	
	/** State attribute for the Vector of References, one for each attachment.
		Using tools can pre-populate, and can read the results from here. */
	public static final String STATE_ATTACHMENTS = "attachment.attachments";

	/** The part of the message after "Attachments for:", set by the client tool. */
	public static final String STATE_FROM_TEXT = "attachment.from_text";

	/** State attributes for private use. */
	private static final String STATE_ATTACHMENT = "attachment.attachment";

	/** The collection id being browsed. */
	private static final String STATE_BROWSE_COLLECTION_ID = "attachment.collection_id";

	/** The id of the "home" collection (can't go up from here. */
	private static final String STATE_HOME_COLLECTION_ID = "attachment.collection_home";

	/** Modes. */
	public static final String MODE_DONE = "done";
	public static final String MODE_MAIN = "main";
	private static final String MODE_BROWSE = "browse";
	private static final String MODE_PROPERTIES = "props";
	private static final String MODE_UPLOAD = "upload";
	private static final String MODE_URL = "url";

	// TODO: path too hard coded
	/** vm files for each mode. */
	private static final String TEMPLATE_MAIN = "helper/chef_attachment_main";
	private static final String TEMPLATE_BROWSE = "helper/chef_attachment_browse";
	private static final String TEMPLATE_PROPERTIES = "/helper/chef_attachment_properties";
	private static final String TEMPLATE_UPLOAD = "helper/chef_attachment_upload";
	private static final String TEMPLATE_URL = "helper/chef_attachment_url";

	/** the maximun size for file upload */
	private static final String FILE_UPLOAD_MAX_SIZE = "20MB";

	/** 
	* build the context.
	* @return The name of the template to use.
	*/
	static public String buildHelperContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// look for a failed upload, which leaves the /special/upload in the URL %%%
		if (StringUtil.trimToNull(rundata.getParameters().getString("special")) != null)
		{
			VelocityPortletPaneledAction.addAlert(state,"The upload size limit of " + FILE_UPLOAD_MAX_SIZE + " has been exceeded.");
		}

		// make sure we have attachments
		Vector attachments = (ReferenceVector) state.getAttribute(STATE_ATTACHMENTS);
		if (attachments == null)
		{
			attachments = new ReferenceVector();
			state.setAttribute(STATE_ATTACHMENTS, attachments);
		}

		// make sure we have a from text to display
		if (state.getAttribute(STATE_FROM_TEXT) == null)
		{
			state.setAttribute(STATE_FROM_TEXT, "");
		}

		// set me as the helper class
		state.setAttribute(VelocityPortletPaneledAction.STATE_HELPER, AttachmentAction.class.getName());
		
		// set the "from" message
		context.put("from", "Attachments for " + state.getAttribute(STATE_FROM_TEXT));

		// get the mode
		String mode = (String) state.getAttribute(STATE_MODE);
		if (mode == null) mode = MODE_MAIN;

		if (mode.equals(MODE_MAIN))
		{
			return buildMainContext(portlet, context, rundata, state);
		}
		else if (mode.equals(MODE_BROWSE))
		{
			return buildBrowseContext(portlet, context, rundata, state);
		}
		else if (mode.equals(MODE_PROPERTIES))
		{
			return buildPropertiesContext(portlet, context, rundata, state);
		}
		else if (mode.equals(MODE_UPLOAD))
		{
			return buildUploadContext(portlet, context, rundata, state);
		}
		else if (mode.equals(MODE_URL))
		{
			return buildUrlContext(portlet, context, rundata, state);
		}
		else
		{
			// %%%
		}
		
		return null;

	}	// buildHelperContext

	/** 
	* build the context for the main display
	* @return The name of the template to use.
	*/
	static public String buildMainContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// place the attribute vector (of References) into the context
		ReferenceVector attachments = (ReferenceVector) state.getAttribute(STATE_ATTACHMENTS);
		context.put("attachments", attachments);

		// make the content type image service available
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());

		// the menu
		buildMenu(portlet, context, rundata, state, true, (attachments.size() > 0));
		
		// for toolbar
		context.put("enabled", new Boolean(true));
		context.put("anyattachment", new Boolean(attachments.size() > 0));
		context.put("has_attachment_before", state.getAttribute(STATE_HAS_ATTACHMENT_BEFORE));

		return TEMPLATE_MAIN;

	}	// buildMainContext

	/** 
	* build the context for the browsing for resources display
	* @return The name of the template to use.
	*/
	static public String buildBrowseContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// make sure the channedId is set
		String id = (String) state.getAttribute(STATE_BROWSE_COLLECTION_ID);
		if (id == null)
		{
			id = ContentHostingService.getSiteCollection(PortalService.getCurrentSiteId());
			state.setAttribute(STATE_BROWSE_COLLECTION_ID, id);
			state.setAttribute(STATE_HOME_COLLECTION_ID, id);
		}
		
		context.put("contentHostingService", ContentHostingService.getInstance());

		String collectionDisplayName = null;
		List members = null;

		try
		{
			// get this collection's display name
			ContentCollection collection = ContentHostingService.getCollection(id);
			collectionDisplayName = collection.getProperties()
				.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);

			// get the full set of member objects
			members = collection.getMemberResources();

			// sort by display name, ascending
			Collections.sort(members, new ContentHostingComparator(ResourceProperties.PROP_DISPLAY_NAME, true));
		}
		catch (Exception e)
		{
			collectionDisplayName = SiteService.getSiteDisplay(PortalService.getCurrentSiteId());
			members = new Vector();
		}
		
		context.put("collectionDisplayName", collectionDisplayName);
		context.put("collectionMembers", members);
		context.put("includeUp", new Boolean(!id.equals(state.getAttribute(STATE_HOME_COLLECTION_ID))));

		// place the attribute vector (of References) into the context
		context.put("attachments", state.getAttribute(STATE_ATTACHMENTS));

		// make the content type image service available
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());

		// the menu
		buildMenu(portlet, context, rundata, state, false, false);
		context.put("enabled", new Boolean(false));
		context.put("anyattachment", new Boolean(false));

		return TEMPLATE_BROWSE;

	}	// buildBrowseContext

	/** 
	* build the context for the upload display
	* @return The name of the template to use.
	*/
	static public String buildUploadContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// the menu
		buildMenu(portlet, context, rundata, state, false, false);
		// for toolbar
		context.put("enabled", new Boolean(false));
		context.put("anyattachment", new Boolean(false));

		return TEMPLATE_UPLOAD;

	}	// buildUploadContext

	/** 
	* build the context for the url display
	* @return The name of the template to use.
	*/
	static public String buildUrlContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// the menu
		buildMenu(portlet, context, rundata, state, false, false);
		// for toolbar
		context.put("enabled", new Boolean(false));
		context.put("anyattachment", new Boolean(false));
		
		return TEMPLATE_URL;

	}	// buildUrlContext

	/**
	* Build the menu.
	*/
	private static void buildMenu(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state,
										boolean enabled,
										boolean anyAttachments)
	{
		Menu bar = new Menu(portlet, rundata, "AttachmentAction");
		String formName = "mainForm";

		bar.add( new MenuEntry("Local File...", null, enabled, MenuItem.CHECKED_NA, "doUpload", formName));
		bar.add( new MenuEntry("Website URL...", null, enabled, MenuItem.CHECKED_NA, "doUrl", formName));
		bar.add( new MenuEntry("From Resources...", null, enabled, MenuItem.CHECKED_NA, "doBrowse", formName));
		bar.add( new MenuEntry("Remove Selected", null, enabled && anyAttachments, MenuItem.CHECKED_NA, "doRemove", formName));
		bar.add( new MenuEntry("Remove All", null, enabled && anyAttachments, MenuItem.CHECKED_NA, "doRemove_all", formName));

		context.put(Menu.CONTEXT_MENU, bar);
		context.put(Menu.CONTEXT_ACTION, "AttachmentAction");
		state.setAttribute(MenuItem.STATE_MENU, bar);

	}	// buildMenu

	/** 
	* build the context for the properties editing for attachment display
	* @return The name of the template to use.
	*/
	static public String buildPropertiesContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// %%% more context setup

		// put in the single Reference for the selected attachment
		context.put("attachment", state.getAttribute(STATE_ATTACHMENT));

		return TEMPLATE_PROPERTIES;

	}	// buildPropertiesContext

	/**
	* Remove the state variables used internally, on the way out.
	*/
	static private void cleanupState(SessionState state)
	{
		state.removeAttribute(STATE_ATTACHMENT);
		state.removeAttribute(STATE_BROWSE_COLLECTION_ID);
		state.removeAttribute(STATE_HOME_COLLECTION_ID);
		state.removeAttribute(STATE_FROM_TEXT);
		state.removeAttribute(STATE_HAS_ATTACHMENT_BEFORE);
		state.removeAttribute(VelocityPortletPaneledAction.STATE_HELPER);

	}	// cleanupState

	/**
	* Handle the eventSubmit_doSave command to save the edited attachments.
	*/
	static public void doSave(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());	
		// end up in done mode
		state.setAttribute(STATE_MODE, MODE_DONE);

		// clean up state
		cleanupState(state);	
		
	}	// doSave

	/**
	* Handle the eventSubmit_doCancel command to abort the edits.
	*/
	static public void doCancel(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// end up in done mode
		state.setAttribute(STATE_MODE, MODE_DONE);

		// remove the attachments from the state to indicate that cancel was done
		state.removeAttribute(STATE_ATTACHMENTS);

		// clean up state
		cleanupState(state);

	}	// doCancel

	/**
	* Handle the eventSubmit_doBrowse command to go into browse for a resource on the site mode.
	*/
	static public void doBrowse(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// end up in browse mode
		state.setAttribute(STATE_MODE, MODE_BROWSE);

	}	// doBrowse

	/**
	* Handle the eventSubmit_doUpload command to go into upload resource mode.
	*/
	static public void doUpload(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// end up in upload mode
		state.setAttribute(STATE_MODE, MODE_UPLOAD);

	}	// doUpload

	/**
	* Handle the eventSubmit_doUrl command to go into enter url mode.
	*/
	static public void doUrl(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// end up in url mode
		state.setAttribute(STATE_MODE, MODE_URL);

	}	// doUrl

	/**
	* Handle the eventSubmit_doAdd command to add attachments.
	*/
	static public void doAdd(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// add to the attachments vector
		Vector attachments = (Vector) state.getAttribute(STATE_ATTACHMENTS);

		// see if the user entered a url to add
		String url = data.getParameters().getString("url");
		if (url != null) url = url.trim();
		if ((url != null) && (url.length() > 0))
		{
			// if it's missing the transport, add http://
			if (url.indexOf("://") == -1) url = "http://" + url;
			
			// make a set of properties to add for the new resource
			ResourcePropertiesEdit props = ContentHostingService.newResourceProperties();
			props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, url);
			props.addProperty(ResourceProperties.PROP_DESCRIPTION, url);

			// make an attachment resource for this URL
			try
			{
				ContentResource attachment = ContentHostingService.addAttachmentResource(
							Validator.escapeResourceName(url),	// use the url as the name
							ResourceProperties.TYPE_URL,
							url.getBytes(),
							props);
	
				// add a dereferencer for this to the attachments
				attachments.add(new Reference(attachment.getReference()));
			}
			catch (Exception any)
			{
				Log.warn("chef", "AttachmentAction" + ".doAdd: exception adding attachment resource (urlName: " + Validator.escapeResourceName(url) + "): " + any.toString());
			}

		}	// if ((url != null) && (url.length() > 0))

		// see if the user uploaded a file
		FileItem file = data.getParameters().getFileItem("file");
		if (file != null)
		{
			// the file content byte[]
			byte[] in  = file.get();

			// the content type
			String contentType = file.getContentType();

			// the file name - as reported by the browser
			String browserFileName = file.getFileName();
			
			// we just want the file name part - strip off any drive and path stuff
			String name = Validator.getFileName(browserFileName);
			String resourceId = Validator.escapeResourceName(name);

			// make a set of properties to add for the new resource
			ResourcePropertiesEdit props = ContentHostingService.newResourceProperties();
			props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
			props.addProperty(ResourceProperties.PROP_DESCRIPTION, browserFileName);

			// make an attachment resource for this URL
			try
			{
				ContentResource attachment = ContentHostingService.addAttachmentResource(resourceId, contentType, in, props);
	
				// add a dereferencer for this to the attachments
				attachments.add(new Reference(attachment.getReference()));
			}
			catch (Exception any)
			{
				Log.warn("chef", "AttachmentAction" + ".doAdd: exception adding attachment resource (fileName: " + name + "): " + any.toString());
			}

		}	// if (file!= null)

		// if there is at least one attachment
		if (attachments.size() > 0)
		{
			state.setAttribute(STATE_HAS_ATTACHMENT_BEFORE, Boolean.TRUE);
		}
		
		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doAdd

	/**
	* Handle the eventSubmit_doRemove command to remove the selected attachment(s).
	*/
	static public void doRemove(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// modify the attachments vector
		Vector attachments = (Vector) state.getAttribute(STATE_ATTACHMENTS);

		// read the form to figure out which attachment(s) to remove.
		String[] selected = data.getParameters().getStrings("select");
		
		// if nothing selected, and there's just one attachment, remove it
		if (selected == null)
		{
			if (attachments.size() == 1)
			{
				attachments.clear();
			}
			else
			{
				// leave a message
				state.setAttribute(VelocityPortletPaneledAction.STATE_MESSAGE, "Alert: No attachments were selected to be removed.");
			}
		}
			
		else
		{
			// run through these 1 based indexes backwards, so we can remove each without invalidating the rest
			// ASSUME: they are in ascending order
			for (int i = selected.length-1; i >= 0; i--)
			{
				try
				{
					int index = Integer.parseInt(selected[i]) - 1;
					attachments.removeElementAt(index);
				}
				catch (Exception e)
				{
					Log.warn("chef", "AttachmentAction" + ".doRemove(): processing selected [" + i + "] : " + e.toString());
				}
			}
		}

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doRemove

	/**
	* Handle the eventSubmit_doRemove_all command to remove the selected attachment(s).
	*/
	static public void doRemove_all(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// modify the attachments vector
		Vector attachments = (Vector) state.getAttribute(STATE_ATTACHMENTS);
		attachments.clear();

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doRemove_all

	/**
	* Handle the eventSubmit_doProperties command to edit the selected attachment's properties.
	* Note: not yet used.
	*/
	static public void doProperties(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// modify the attachments vector
		Vector attachments = (Vector) state.getAttribute(STATE_ATTACHMENTS);

		// read the form to figure out which %%% attachment from the vector to edit.
		Reference attachment = null;
		state.setAttribute(STATE_ATTACHMENT, attachment);

		// end up in properties mode
		state.setAttribute(STATE_MODE, MODE_PROPERTIES);

	}	// doProperties

	/**
	* Handle the eventSubmit_doCancel_browse command to abort the browse.
	*/
	static public void doCancel_browse(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// clean up any browse state
		state.removeAttribute(STATE_BROWSE_COLLECTION_ID);
		state.removeAttribute(STATE_HOME_COLLECTION_ID);

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doCancel_browse

	/**
	* Handle the eventSubmit_doCancel_add command to abort an add.
	*/
	static public void doCancel_add(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doCancel_add

	/**
	* Handle the eventSubmit_doBrowse_option command to process inputs from the browse form:
	* go up, go down, or be done.
	*/
	static public void doBrowse_option(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// which option was choosen?
		String option = data.getParameters().getString("option");
		if (option.equals("cancel"))
		{
			doCancel_add(data);
		}
		else
		{		
			// read the form / state to figure out which attachment(s) to add.
			String[] ids = data.getParameters().getStrings("selectedMembers");
			Vector idVector = new Vector();
			if ((ids != null) && (ids.length > 0))
			{
				for (int index = 0; index < ids.length; index++)
				{
					idVector.add(ids[index]);
				}
			}
			updateAttachments(state, idVector);
			// if there is at least one resource chosed to be added
			if (idVector.size() > 0)
			{
				state.setAttribute(STATE_HAS_ATTACHMENT_BEFORE, Boolean.TRUE);
			}

			if (option.equals("up"))
			{
				// get the current collection
				String id = (String) state.getAttribute(STATE_BROWSE_COLLECTION_ID);

				// if we are at the home collection, we go no up-er
				if (id.equals(state.getAttribute(STATE_HOME_COLLECTION_ID))) return;

				// get the containing collection
				String containingId = ContentHostingService.getContainingCollectionId(id);

				// make sure the user can read that
				if (ContentHostingService.allowGetCollection(containingId))
				{
					state.setAttribute(STATE_BROWSE_COLLECTION_ID, containingId);
				}
				else
				{
					state.setAttribute(VelocityPortletPaneledAction.STATE_MESSAGE, "Alert: You do not have permission to view that folder.");
				}

				// end up in browse mode
				state.setAttribute(STATE_MODE, MODE_BROWSE);
			}

			else if(option.equals("down"))
			{
				// get the collection id to move to
				String id = data.getParameters().getString("itemId");

				// make sure the user can read that
				if (ContentHostingService.allowGetCollection(id))
				{
					state.setAttribute(STATE_BROWSE_COLLECTION_ID, id);
				}
				else
				{
					state.setAttribute(VelocityPortletPaneledAction.STATE_MESSAGE, "Alert: You do not have permission to view that folder.");
				}

				// end up in browse mode
				state.setAttribute(STATE_MODE, MODE_BROWSE);
			}
			else	// done
			{
				// clean up any browse state
				state.removeAttribute(STATE_BROWSE_COLLECTION_ID);
				state.removeAttribute(STATE_HOME_COLLECTION_ID);

				// end up in main mode
				state.setAttribute(STATE_MODE, MODE_MAIN);
			}
		}
		
	}	// doBrowse_option

	/**
	* Update the attachments list based on which ids were selected at this browse level.
	*/
	static private void updateAttachments(SessionState state, Vector ids)
	{
		String id = (String) state.getAttribute(STATE_BROWSE_COLLECTION_ID);
		ReferenceVector attachments = (ReferenceVector) state.getAttribute(STATE_ATTACHMENTS);

		List members = null;
		try
		{
			// get the set of member ids
			ContentCollection collection = ContentHostingService.getCollection(id);
			members = collection.getMembers();

			// for each member
			for (int i = 0; i < members.size(); i++)
			{
				String memberId = (String) members.get(i);
				String ref = ContentHostingService.getReference(memberId);
	
				// if the member id is in the list of ids selected
				if (ids.contains(memberId))
				{
					// make sure it is in the attachments
					if (!attachments.contains(ref))
					{
						attachments.add(new Reference(ref));
					}
				}
				else
				{
					// make sure its NOT in the attachments
					attachments.remove(ref);
				}
			}
		}
		catch (Exception e) {}

	}	// updateAttachments

	/**
	* Handle the eventSubmit_doCancel_properties command to abort the properties edit.
	* Note: not yet used.
	*/
	static public void doCancel_properties(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// clean up any properties state %%%
		state.removeAttribute(STATE_ATTACHMENT);

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doCancel_properties

	/**
	* Handle the eventSubmit_doUpdate_properties command to keep the edited properties.
	* Note: not yet used.
	*/
	static public void doUpdate_properties(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// modify the attachment Reference
		Reference attachment = (Reference) state.getAttribute(STATE_ATTACHMENT);

		// read the form / state to get the %%% changes and make them

		// clean up any properties state %%%
		state.removeAttribute(STATE_ATTACHMENT);

		// end up in main mode
		state.setAttribute(STATE_MODE, MODE_MAIN);

	}	// doUpdate_properties
	
}	// AttachmentAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/helper/AttachmentAction.java,v 1.15 2004/09/30 20:19:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
