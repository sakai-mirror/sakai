/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.content;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.control.SchemaBean;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.tool.helper.AttachmentAction;
import org.sakaiproject.tool.helper.PermissionsAction;
import org.sakaiproject.util.ContentHostingComparator;
import org.sakaiproject.util.FileItem;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
* <p>ResourceAction is a ContentHosting application</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class ResourcesAction
	extends VelocityPortletPaneledAction
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("content");
    
	/** the maximun size for file upload */
	private static final String FILE_UPLOAD_MAX_SIZE = "file_upload_max_size";
	
	/** portlet configuration parameter values**/
	public static final String RESOURCES_MODE_RESOURCES = "resources";
	public static final String RESOURCES_MODE_DROPBOX = "dropbox";
	public static final String RESOURCES_MODE_HELPER = "helper";
	
	/** opened collection list */
	private static final String EXPANDED_COLLECTIONS = "expanded_collections";
	
	/** The null/empty string */
	private static final String NULL_STRING = "";

	/** The string used when pasting the same resource to the same folder */
	private static final String DUPLICATE_STRING = rb.getString("copyof") + " ";
	
	/** The string used when pasting shirtcut of the same resource to the same folder */
	private static final String SHORTCUT_STRING = rb.getString("shortcut");

	/** The copyright character (Note: could be "\u00a9" if we supported UNICODE for specials -ggolden */
	private static final String COPYRIGHT_SYMBOL = rb.getString("cpright1");

	/** The String of new copyright */
	private static final String NEW_COPYRIGHT = "newcopyright";
	
	/** The maximum number of characters allowed in a new resource ID */
	private static final int RESOURCE_ID_MAX_LENGTH = 254;

	/** The resource not exist string */
	private static final String RESOURCE_NOT_EXIST_STRING = rb.getString("notexist1");
	
	/** The title invalid string */
	private static final String RESOURCE_INVALID_TITLE_STRING = rb.getString("titlecannot");
	
	/** The copy, cut, paste not operate on collection string */
	private static final String RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING = rb.getString("notsupported");
	
	/** State attribute for state initialization.  */
	private static final String STATE_INITIALIZED = "resources.initialized";

	/** The content hosting service in the State. */
	private static final String STATE_CONTENT_SERVICE = "resources.content_service";

	/** The resources, helper or dropbox mode. */
	public static final String STATE_RESOURCES_MODE = "resources.resources_mode";
	
	/** The name of a state attribute indicating whether the resources tool/helper is allowed to show all sites the user has access to */
	public static final String STATE_SHOW_ALL_SITES = "resources.allow_user_to_see_all_sites";

	/** The name of a state attribute indicating whether the wants to see other sites if that is enabled */
	public static final String STATE_SHOW_OTHER_SITES = "resources.user_chooses_to_see_other_sites";

	/** The content type image lookup service in the State. */
	private static final String STATE_CONTENT_TYPE_IMAGE_SERVICE = "resources.content_type_image_service";

	/** The user copyright string */
	private static final String	STATE_MY_COPYRIGHT = "resources.mycopyright";

	/** copyright path -- MUST have same value as AccessServlet.COPYRIGHT_PATH */
	public static final String COPYRIGHT_PATH = Entity.SEPARATOR + "copyright";	

	/** The collection id being browsed. */
	private static final String STATE_COLLECTION_ID = "resources.collection_id";

	/** The id of the "home" collection (can't go up from here.) */
	private static final String STATE_HOME_COLLECTION_ID = "resources.collection_home";

	/** The display name of the "home" collection (can't go up from here.) */
	private static final String STATE_HOME_COLLECTION_DISPLAY_NAME = "resources.collection_home_display_name";

	/** The inqualified input field */
	private static final String STATE_UNQUALIFIED_INPUT_FIELD = "resources.unqualified_input_field";
	
	/** The collection id path */
	private static final String STATE_COLLECTION_PATH = "resources.collection_path";

	/** The name of the state attribute containing BrowseItems for all content collections the user has access to */
	private static final String STATE_COLLECTION_ROOTS = "resources.collection_rootie_tooties";

	/** The sort by */
	private static final String STATE_SORT_BY = "resources.sort_by";

	/** The sort ascending or decending */
	private static final String STATE_SORT_ASC = "resources.sort_asc";

	/** The copy flag */
	private static final String STATE_COPY_FLAG = "resources.copy_flag";

	/** The cut flag */
	private static final String STATE_CUT_FLAG = "resources.cut_flag";
	
	/** The can-paste flag */
	private static final String STATE_PASTE_ALLOWED_FLAG = "resources.can_paste_flag";

	/** The move flag */
	private static final String STATE_MOVE_FLAG = "resources.move_flag";

	/** The select all flag */
	private static final String STATE_SELECT_ALL_FLAG = "resources.select_all_flag";

	/** The name of the state attribute indicating whether the hierarchical list is expanded */
	private static final String STATE_EXPAND_ALL_FLAG = "resources.expand_all_flag";

	/** The name of the state attribute indicating whether the hierarchical list needs to be expanded */
	private static final String STATE_NEED_TO_EXPAND_ALL = "resources.need_to_expand_all";

	/** The name of the state attribute containing a java.util.Set with the id's of selected items */
	private static final String STATE_LIST_SELECTIONS = "resources.ignore_delete_selections";

	/** The from state name */
	private static final String STATE_FROM = "resources.from";
	
	/** The root of the navigation breadcrumbs for a folder, either the home or another site the user belongs to */
	private static final String STATE_NAVIGATION_ROOT = "resources.navigation_root";

	/************** the more context *****************************************/

	/** The more id */
	private static final String STATE_MORE_ID = "resources.more_id";

	/** The more collection id */
	private static final String STATE_MORE_COLLECTION_ID = "resources.more_collection_id";

	/** The more from */
	private static final String STATE_MORE_FROM = "resources.more_from";

	/************** the properties context *****************************************/

	/** The properties id */
	private static final String STATE_PROPERTIES_ID = "resources.properties_id";
	private static final String STATE_PROPERTIES_COPYRIGHT = "resources.properties_copyright";
	private static final String STATE_PROPERTIES_COPYRIGHT_CHOICE = "resources.properties_copyright_choice";
	private static final String STATE_PROPERTIES_COPYRIGHT_ALERT = "resources.properties_copyright_alert";
	private static final String STATE_PROPERTIES_COLLECTION_ID = "resources.properties_collection_id";
	private static final String STATE_PROPERTIES_INTENT = "resources.properties_intent";

	/************** the edit context *****************************************/

	/** The edit id */
	private static final String STATE_EDIT_ALERTS = "resources.edit_alerts";
	private static final String STATE_EDIT_ID = "resources.edit_id";
	private static final String STATE_EDIT_ITEM = "resources.edit_item";
	private static final String STATE_EDIT_COLLECTION_ID = "resources.edit_collection_id";
	private static final String STATE_EDIT_INTENT = "resources.properties_intent";
	private static final String STATE_SHOW_FORM_ITEMS = "resources.show_form_items";

	/************** the create contexts *****************************************/

	private static final String STATE_CREATE_TYPE = "resources.create_type";
	private static final String STATE_CREATE_ITEMS = "resources.create_items";
	private static final String STATE_CREATE_COLLECTION_ID = "resources.create_collection_id";
	private static final String STATE_CREATE_NUMBER = "resources.create_number";
	private static final String STATE_CREATE_ACTUAL_COUNT = "resources.create_actual_count";
	private static final String STATE_CREATE_ALERTS = "resources.create_alerts";
	private static final String STATE_CREATE_MISSING_ITEM = "resources.create_missing_item";
	private static final String STATE_STRUCTOBJ_ROOTNAME = "resources.create_structured_object_root";
	private static final String STATE_STRUCTOBJ_TYPE = "resources.create_structured_object_type";
	private static final String STATE_STRUCTOBJ_HOMES = "resources.create_structured_object_home";
	private static final String STATE_STRUCT_OBJ_SCHEMA = "resources.create_structured_object_schema";

	private static final String MIME_TYPE_DOCUMENT_PLAINTEXT = "text/plain";
	private static final String MIME_TYPE_DOCUMENT_HTML = "text/html";
	public static final String MIME_TYPE_STRUCTOBJ = "application/x-osp";
	
	private static final String TYPE_FOLDER = "folder";
	private static final String TYPE_UPLOAD = "file";
	private static final String TYPE_URL = "Url";
	private static final String TYPE_FORM = MIME_TYPE_STRUCTOBJ;
	
	private static final int CREATE_MAX_ITEMS = 10;
	
	private static final int INTEGER_WIDGET_LENGTH = 12;
	private static final int DOUBLE_WIDGET_LENGTH = 18;

	/************** the metadata extension of edit/create contexts *****************************************/

	private static final String STATE_METADATA_GROUPS = "resources.metadata.types";
	
	private static final String INTENT_REVISE_FILE = "revise";
	private static final String INTENT_REPLACE_FILE = "replace";
	
	/************** the helper context (file-picker) *****************************************/
	
	/** State attribute for the Vector of References, one for each attachment.
	Using tools can pre-populate, and can read the results from here. */
	public static final String STATE_ATTACHMENTS = "resources.attachments";

	/** State attribute for where there is at least one attachment before invoking attachment tool */
	public static final String STATE_HAS_ATTACHMENT_BEFORE = "resources.has_attachment_before";

	/** The part of the message after "Attachments for:", set by the client tool. */
	public static final String STATE_FROM_TEXT = "attachment.from_text";

	/** The name of the state attribute containing a list of new items to be attached */
	private static final String STATE_HELPER_NEW_ITEMS = "resources.helper_new_items";
	
	/** The name of the state attribute indicating that the list of new items has changed */
	private static final String STATE_HELPER_CHANGED = "resources.helper_changed";
	
	/** The name of the state attribute indicating which dropbox(es) the items should be saved in */
	public static final String STATE_SAVE_ATTACHMENT_IN_DROPBOX = "resources.save_attachment_in_dropbox";

	/************** the delete context *****************************************/

	/** The delete ids */
	private static final String STATE_DELETE_IDS = "resources.delete_ids";
	
	/** The not empty delete ids */
	private static final String STATE_NOT_EMPTY_DELETE_IDS = "resource.not_empty_delete_ids";

	/** The name of the state attribute containing a list of BrowseItem objects corresponding to resources selected for deletion */
	private static final String STATE_DELETE_ITEMS = "resources.delete_items";

	/** The name of the state attribute containing a list of BrowseItem objects corresponding to nonempty folders selected for deletion */
	private static final String STATE_DELETE_ITEMS_NOT_EMPTY = "resources.delete_items_not_empty";

	/** The name of the state attribute containing a list of BrowseItem objects selected for deletion that cannot be deleted */
	private static final String STATE_DELETE_ITEMS_CANNOT_DELETE = "resources.delete_items_cannot_delete";

	/************** the cut items context *****************************************/

	/** The cut item ids */
	private static final String STATE_CUT_IDS = "resources.revise_cut_ids";

	/************** the copied items context *****************************************/

	/** The copied item ids */
	private static final String STATE_COPIED_IDS = "resources.revise_copied_ids";

	/** The copied item id */
	private static final String STATE_COPIED_ID = "resources.revise_copied_id";
	
	/************** the moved items context *****************************************/

	/** The copied item ids */
	private static final String STATE_MOVED_IDS = "resources.revise_moved_ids";

	/** Modes. */
	private static final String MODE_LIST = "list";
	private static final String MODE_EDIT = "edit";
	private static final String MODE_DAV = "webdav";
	private static final String MODE_CREATE = "create";
	public  static final String MODE_HELPER = "helper";
	private static final String MODE_DELETE_CONFIRM = "deleteConfirm";
	private static final String MODE_MORE = "more";
	private static final String MODE_PROPERTIES = "properties";

	/** modes for attachment helper */
	public static final String MODE_ATTACHMENT_SELECT = "resources.attachment_select";
	public static final String MODE_ATTACHMENT_CREATE = "resources.attachment_create";
	public static final String MODE_ATTACHMENT_CONFIRM = "resources.attachment_confirm";
	public static final String MODE_ATTACHMENT_DONE = "resources.attachment_done";
	
	/** vm files for each mode. */
	private static final String TEMPLATE_LIST = "content/chef_resources_list";
	private static final String TEMPLATE_EDIT = "content/chef_resources_edit";
	private static final String TEMPLATE_CREATE = "content/chef_resources_create";
	private static final String TEMPLATE_DAV = "content/chef_resources_webdav";
	private static final String TEMPLATE_SELECT = "content/chef_resources_select";
	private static final String TEMPLATE_ATTACH = "content/chef_resources_attach";

	private static final String TEMPLATE_MORE = "content/chef_resources_more";
	private static final String TEMPLATE_DELETE_CONFIRM = "content/chef_resources_deleteConfirm";
	private static final String TEMPLATE_PROPERTIES = "content/chef_resources_properties";
	// private static final String TEMPLATE_REPLACE = "_replace";
	
	/** the site title */
	private static final String STATE_SITE_TITLE = "site_title";
	
	/** copyright related info */
	private static final String COPYRIGHT_TYPES = "copyright_types";
	private static final String COPYRIGHT_TYPE = "copyright_type";
	private static final String DEFAULT_COPYRIGHT = "default_copyright";
	private static final String COPYRIGHT_ALERT = "copyright_alert";
	private static final String DEFAULT_COPYRIGHT_ALERT = "default_copyright_alert";
	private static final String COPYRIGHT_FAIRUSE_URL = "copyright_fairuse_url";
	private static final String NEW_COPYRIGHT_INPUT = "new_copyright_input";
	private static final String COPYRIGHT_SELF_COPYRIGHT = rb.getString("cpright2");
	private static final String COPYRIGHT_NEW_COPYRIGHT = rb.getString("cpright3");
	private static final String COPYRIGHT_ALERT_URL = ServerConfigurationService.getAccessUrl() + COPYRIGHT_PATH;

	private static final int MAXIMUM_ATTEMPTS_FOR_UNIQUENESS = 1000;

	/** The default value for whether to show all sites in file-picker (used if global value can't be read from server config service) */
	static public final boolean SHOW_ALL_SITES_IN_FILE_PICKER = false;

	/** The default value for whether to show all sites in resources tool (used if global value can't be read from server config service) */
	private static final boolean SHOW_ALL_SITES_IN_RESOURCES = false;

	/** The default value for whether to show all sites in dropbox (used if global value can't be read from server config service) */
	private static final boolean SHOW_ALL_SITES_IN_DROPBOX = false;




	/**
	* Build the context for normal display
	*/
	public String buildMainPanelContext (	VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));

		context.put("copyright_alert_url", COPYRIGHT_ALERT_URL);

		// if we are in edit permissions...
		String helperMode = (String) state.getAttribute(PermissionsAction.STATE_MODE);
		if (helperMode != null)
		{
			String template = PermissionsAction.buildHelperContext(portlet, context, data, state);
			if (template == null)
			{
				addAlert(state, rb.getString("therisprob"));
			}
			else
			{
				return template;
			}
		}

		String template = null;

		// place if notification is enabled and current site is not of My Workspace type
		boolean isUserSite = SiteService.isUserSite(PortalService.getCurrentSiteId());
		context.put("notification", new Boolean(!isUserSite && notificationEnabled(state)));

		// get the mode
		String mode = (String) state.getAttribute (STATE_MODE);

		if (mode.equals (MODE_LIST))
		{
//			// enable the observer when inside the list view
//			ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//			o.setDeliveryId(clientWindowId(state, portlet.getID()));
//			o.enable();	
			
			// build the context for add item
			template = buildListContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_HELPER))
		{
//			// enable the observer when inside the list view
//			ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//			o.setDeliveryId(clientWindowId(state, portlet.getID()));
//			o.enable();	
			
			// build the context for add item
			template = buildHelperContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_CREATE))
		{
//			// enable the observer when inside the list view
//			ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//			o.setDeliveryId(clientWindowId(state, portlet.getID()));
//			o.enable();	
			
			// build the context for add item
			template = buildCreateContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_DELETE_CONFIRM))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the basic step of delete confirm page
			template = buildDeleteConfirmContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_MORE))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context to display the property list
			template = buildMoreContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_PROPERTIES))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context to display the property list
			template = buildPropertiesContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_EDIT))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context to display the property list
			template = buildEditContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_OPTIONS))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			template = buildOptionsPanelContext (portlet, context, data, state);
		}
		else if(mode.equals(MODE_DAV))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			template = buildWebdavContext (portlet, context, data, state);
		}
	
		return template;

	}	// buildMainPanelContext

	/**
	* Build the context for the list view
	*/
	public String buildListContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		context.put("expandedCollections", state.getAttribute(EXPANDED_COLLECTIONS));
		
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		
		context.put("TYPE_FOLDER", TYPE_FOLDER);
		context.put("TYPE_UPLOAD", TYPE_UPLOAD);

		Set selectedItems = (Set) state.getAttribute(STATE_LIST_SELECTIONS);
		if(selectedItems == null)
		{
			selectedItems = new TreeSet();
			state.setAttribute(STATE_LIST_SELECTIONS, selectedItems);
		}
		context.put("selectedItems", selectedItems);
		
		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", contentService);
		
		boolean inMyWorkspace = SiteService.isUserSite(PortalService.getCurrentSiteId());
		context.put("inMyWorkspace", Boolean.toString(inMyWorkspace));
		
		boolean atHome = false;

		boolean dropboxMode = ((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX);
		if (dropboxMode)
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		else 
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		
		// make sure the channedId is set
		String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		context.put ("collectionId", collectionId);
		String navRoot = (String) state.getAttribute(STATE_NAVIGATION_ROOT);
		String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
		
		String siteTitle = (String) state.getAttribute (STATE_SITE_TITLE);
		if (collectionId.equals(homeCollectionId))
		{
			atHome = true;
			context.put ("collectionDisplayName", state.getAttribute (STATE_HOME_COLLECTION_DISPLAY_NAME));
		}
		else
		{
			// should be not PermissionException thrown at this time, when the user can successfully navigate to this collection
			try
			{
				context.put("collectionDisplayName", contentService.getCollection(collectionId).getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME));
			}
			catch (IdUnusedException e){}
			catch (TypeException e) {}
			catch (PermissionException e) {}
		}
		if(!inMyWorkspace && !dropboxMode && atHome && SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
		{
			context.put("showPermissions", Boolean.TRUE.toString());		
			//buildListMenu(portlet, context, data, state);
		}

		context.put("atHome", Boolean.toString(atHome));
		
		List cPath = getCollectionPath(state);
		context.put ("collectionPath", cPath);

		// set the sort values
		String sortedBy = (String) state.getAttribute (STATE_SORT_BY);
		String sortedAsc = (String) state.getAttribute (STATE_SORT_ASC);
		context.put ("currentSortedBy", sortedBy);
		context.put ("currentSortAsc", sortedAsc);
		context.put("TRUE", Boolean.TRUE.toString());
		
		boolean showRemoveAction = false;
		boolean showMoveAction = false;
		boolean showCopyAction = false;
		
		Set highlightedItems = new TreeSet();
		
		try
		{
			contentService.checkCollection (collectionId);
			context.put ("collectionFlag", Boolean.TRUE.toString());

			String copyFlag = (String) state.getAttribute (STATE_COPY_FLAG);
			if (copyFlag.equals (Boolean.TRUE.toString()))
			{ 
				context.put ("copyFlag", copyFlag);
				List copiedItems = (List) state.getAttribute(STATE_COPIED_IDS);
				// context.put ("copiedItem", state.getAttribute (STATE_COPIED_ID));
				highlightedItems.addAll(copiedItems);
				// context.put("copiedItems", copiedItems);
			}

			String moveFlag = (String) state.getAttribute (STATE_MOVE_FLAG);
			if (moveFlag.equals (Boolean.TRUE.toString()))
			{ 
				context.put ("moveFlag", moveFlag);
				List movedItems = (List) state.getAttribute(STATE_MOVED_IDS);
				highlightedItems.addAll(movedItems);
				// context.put ("copiedItem", state.getAttribute (STATE_COPIED_ID));
				// context.put("movedItems", movedItems);
			}

			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			ContentCollection coll = contentService.getCollection(collectionId);
			expandedCollections.put(collectionId, coll);
			
			state.removeAttribute(STATE_PASTE_ALLOWED_FLAG);
			
			List all_roots = new Vector();
			List this_site = new Vector();
			List members = getBrowseItems(collectionId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, navRoot.equals(homeCollectionId), state);
			if(members != null && members.size() > 0)
			{
				BrowseItem root = (BrowseItem) members.remove(0);
				showRemoveAction = showRemoveAction || root.hasDeletableChildren();
				showMoveAction = showMoveAction || root.hasDeletableChildren();
				showCopyAction = showCopyAction || root.hasCopyableChildren();
				
				if(atHome && dropboxMode)
				{
					root.setName(siteTitle + " " + rb.getString("gen.drop"));
				}
				else if(atHome)
				{
					root.setName(siteTitle + " " + rb.getString("gen.reso"));
				}
				context.put("site", root);
				root.addMembers(members);
				this_site.add(root);
				all_roots.add(root);
			}
			context.put ("this_site", this_site);

			boolean show_all_sites = false;
			List other_sites = new Vector();
			
			String allowed_to_see_other_sites = (String) state.getAttribute(STATE_SHOW_ALL_SITES);
			String show_other_sites = (String) state.getAttribute(STATE_SHOW_OTHER_SITES);
			context.put("show_other_sites", show_other_sites);
			if(Boolean.TRUE.toString().equals(allowed_to_see_other_sites))
			{
				context.put("allowed_to_see_other_sites", Boolean.TRUE.toString());
				show_all_sites = Boolean.TRUE.toString().equals(show_other_sites);
			}
			
			if(atHome && show_all_sites)
			{
				// add user's personal workspace
				User user = UserDirectoryService.getCurrentUser();
				String userId = user.getId();
				String userName = user.getDisplayName();
				String wsId = SiteService.getUserSiteId(userId);
				String wsCollectionId = ContentHostingService.getSiteCollection(wsId);
				if(! collectionId.equals(wsCollectionId))
				{
	                	members = getBrowseItems(wsCollectionId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, false, state);
	                	if(members != null && members.size() > 0)
				    {
				        BrowseItem root = (BrowseItem) members.remove(0);
						showRemoveAction = showRemoveAction || root.hasDeletableChildren();
						showMoveAction = showMoveAction || root.hasDeletableChildren();
						showCopyAction = showCopyAction || root.hasCopyableChildren();
						
				        root.addMembers(members);
				        root.setName(userName + " " + rb.getString("gen.wsreso"));
				        other_sites.add(root);
				        all_roots.add(root);
				    }
				}
				
                	// add all other sites user has access to
				/*
				 * NOTE: This does not (and should not) get all sites for admin.  
				 *       Getting all sites for admin is too big a request and
				 *       would result in too big a display to render in html.
				 */
				Map othersites = ContentHostingService.getCollectionMap();
				Iterator siteIt = othersites.keySet().iterator();
				while(siteIt.hasNext())
				{
	                  String displayName = (String) siteIt.next();
	                  String collId = (String) othersites.get(displayName);
	                  if(! collectionId.equals(collId) && ! wsCollectionId.equals(collId))
	                  {
	                      members = getBrowseItems(collId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, false, state);
	                      if(members != null && members.size() > 0)
	                      {
	                          BrowseItem root = (BrowseItem) members.remove(0);
	                          root.addMembers(members);
	                          root.setName(displayName);
	                          other_sites.add(root);
	                          all_roots.add(root);
	                      }
	                  }
                  }
			}
			
			context.put ("other_sites", other_sites);
			state.setAttribute(STATE_COLLECTION_ROOTS, all_roots);
			// context.put ("root", root);

			if(state.getAttribute(STATE_PASTE_ALLOWED_FLAG) != null)
			{
				context.put("paste_place_showing", state.getAttribute(STATE_PASTE_ALLOWED_FLAG));
			}
			
			if(showRemoveAction)
			{
				context.put("showRemoveAction", Boolean.TRUE.toString());
			}
			
			if(showMoveAction)
			{
				context.put("showMoveAction", Boolean.TRUE.toString());
			}
			
			if(showCopyAction)
			{
				context.put("showCopyAction", Boolean.TRUE.toString());
			}
			
		}
		catch (IdUnusedException e)
		{
			addAlert(state, rb.getString("cannotfind"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		catch(TypeException e)
		{
			Log.error("chef", this + "TypeException.");
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		catch(PermissionException e)
		{
			addAlert(state, rb.getString("notpermis1"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", contentService.newResourceProperties ());

		try
		{
			// TODO: why 'site' here?
			Site site = SiteService.getSite(PortalService.getCurrentSiteId());
			context.put("siteTitle", site.getTitle());
		}
		catch (IdUnusedException e)
		{
			// Log.error("chef", this + e.toString());
		}
		
		context.put("expandallflag", state.getAttribute(STATE_EXPAND_ALL_FLAG));
		state.removeAttribute(STATE_NEED_TO_EXPAND_ALL);

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		justDelivered(state);

		// pick the "show" template based on the standard template name
		// String template = (String) getContext(data).get("template");
	
		return TEMPLATE_LIST;
		
	}	// buildListContext

	/**
	* Build the context for the helper view
	*/
	public static String buildHelperContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		if(state.getAttribute(STATE_INITIALIZED) == null)
		{
			initStateAttributes(state, portlet);
			state.setAttribute(VelocityPortletPaneledAction.STATE_HELPER, ResourcesAction.class.getName());
			
			List attachments = (List) state.getAttribute(STATE_ATTACHMENTS);
			if(attachments == null)
			{
				attachments = EntityManager.newReferenceList();
			}
			
			List attached = new Vector();
			
			Set selectedItems = (Set) state.getAttribute(STATE_LIST_SELECTIONS);
			if(selectedItems == null)
			{
				selectedItems = new TreeSet();
				state.setAttribute(STATE_LIST_SELECTIONS, selectedItems);
			}
			context.put("selectedItems", selectedItems);
			
			Iterator it = attachments.iterator();
			while(it.hasNext())
			{
				try
				{
					Reference ref = (Reference) it.next();
					String itemId = ref.getId();
					ResourceProperties properties = ref.getProperties();
					String displayName = properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
					String containerId = ref.getContainer();
					String accessUrl = ref.getUrl();
					String contentType = properties.getProperty(ResourceProperties.PROP_CONTENT_TYPE);
					
					AttachItem item = new AttachItem(itemId, displayName, containerId, accessUrl);
					item.setContentType(contentType);
					attached.add(item);
				}
				catch(Exception ignore) {}
			}
			state.setAttribute(STATE_HELPER_NEW_ITEMS, attached);
			
		}
		
		String helper_mode = (String) state.getAttribute(STATE_RESOURCES_MODE);
		
		String template = null;
		if(MODE_ATTACHMENT_SELECT.equals(helper_mode))
		{
			template = buildSelectAttachmentContext(portlet, context, data, state);
		}
		else if(MODE_ATTACHMENT_CREATE.equals(helper_mode))
		{
			template = buildCreateContext(portlet, context, data, state);
		}
		return template;
	}
	
	/**
	* Build the context for selecting attachments
	*/
	public static String buildSelectAttachmentContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		
		initStateAttributes(state, portlet);
		
		state.setAttribute(VelocityPortletPaneledAction.STATE_HELPER, ResourcesAction.class.getName());
		
		Set highlightedItems = new TreeSet();
		
		List new_items = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
		if(new_items == null)
		{
			new_items = new Vector();
			state.setAttribute(STATE_HELPER_NEW_ITEMS, new_items);
		}
		context.put("attached", new_items);
		context.put("last", new Integer(new_items.size() - 1));
		
		if(state.getAttribute(STATE_HELPER_CHANGED) != null)
		{
			context.put("list_has_changed", "true");
		}
		
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		
		context.put("TYPE_FOLDER", TYPE_FOLDER);
		context.put("TYPE_UPLOAD", TYPE_UPLOAD);

		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		// context.put ("service", contentService);
		
		boolean inMyWorkspace = SiteService.isUserSite(PortalService.getCurrentSiteId());
		// context.put("inMyWorkspace", Boolean.toString(inMyWorkspace));
			
		boolean atHome = false;

		boolean dropboxMode = ((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX);
		
		// make sure the channedId is set
		String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		context.put ("collectionId", collectionId);
		String navRoot = (String) state.getAttribute(STATE_NAVIGATION_ROOT);
		String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
		
		String siteTitle = (String) state.getAttribute (STATE_SITE_TITLE);
		if (collectionId.equals(homeCollectionId))
		{
			atHome = true;
			//context.put ("collectionDisplayName", state.getAttribute (STATE_HOME_COLLECTION_DISPLAY_NAME));
		}
		else
		{
			/*
			// should be not PermissionException thrown at this time, when the user can successfully navigate to this collection
			try
			{
				context.put("collectionDisplayName", contentService.getCollection(collectionId).getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME));
			}
			catch (IdUnusedException e){}
			catch (TypeException e) {}
			catch (PermissionException e) {}
			*/
		}

		List cPath = getCollectionPath(state);
		context.put ("collectionPath", cPath);

		// set the sort values
		String sortedBy = (String) state.getAttribute (STATE_SORT_BY);
		String sortedAsc = (String) state.getAttribute (STATE_SORT_ASC);
		context.put ("currentSortedBy", sortedBy);
		context.put ("currentSortAsc", sortedAsc);
		context.put("TRUE", Boolean.TRUE.toString());
		
		try
		{
			try
			{
				contentService.checkCollection (collectionId);
				// context.put ("collectionFlag", Boolean.TRUE.toString());
				
			}
			catch(IdUnusedException e)
			{
				try
				{
					contentService.addCollection(collectionId);
				}
				catch(Exception ignore)
				{
				}
				contentService.checkCollection (collectionId);
			}
			
			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			ContentCollection coll = contentService.getCollection(collectionId);
			expandedCollections.put(collectionId, coll);
			
			state.removeAttribute(STATE_PASTE_ALLOWED_FLAG);
			
			List this_site = new Vector();
			User[] submitters = (User[]) state.getAttribute(STATE_SAVE_ATTACHMENT_IN_DROPBOX);
			if(submitters != null)
			{
				String dropboxId = Dropbox.getCollection();
				if(dropboxId == null)
				{
					Dropbox.createCollection();
					dropboxId = Dropbox.getCollection();
				}
				
				if(dropboxId == null)
				{
					// do nothing
				}
				else if(Dropbox.isDropboxMaintainer())
				{
					for(int i = 0; i < submitters.length; i++)
					{
						User submitter = submitters[i]; 
						String dbId = dropboxId + StringUtil.trimToZero(submitter.getId()) + "/";
						ContentCollection db = ContentHostingService.getCollection(dbId);
						expandedCollections.put(dbId, db);
						List dbox = getBrowseItems(dbId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, false, state);
						if(dbox != null && dbox.size() > 0)
						{
							BrowseItem root = (BrowseItem) dbox.remove(0);
							// context.put("site", root);
							root.setName(submitter.getDisplayName() + " " + rb.getString("gen.drop"));
							root.addMembers(dbox);
							this_site.add(root);
						}
					}
				}
				else
				{
					ContentCollection db = ContentHostingService.getCollection(dropboxId);
					expandedCollections.put(dropboxId, db);
					List dbox = getBrowseItems(dropboxId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, false, state);
					if(dbox != null && dbox.size() > 0)
					{
						BrowseItem root = (BrowseItem) dbox.remove(0);
						// context.put("site", root);
						root.setName(Dropbox.getDisplayName());
						root.addMembers(dbox);
						this_site.add(root);
					}
				}
			}
			List members = getBrowseItems(collectionId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, navRoot.equals(homeCollectionId), state);
			if(members != null && members.size() > 0)
			{
				BrowseItem root = (BrowseItem) members.remove(0);
				if(atHome && dropboxMode)
				{
					root.setName(siteTitle + " " + rb.getString("gen.drop"));
				}
				else if(atHome)
				{
					root.setName(siteTitle + " " + rb.getString("gen.reso"));
				}
				context.put("site", root);
				root.addMembers(members);
				this_site.add(root);
			}
			

			context.put ("this_site", this_site);
			
			boolean show_all_sites = false;
			
			String allowed_to_see_other_sites = (String) state.getAttribute(STATE_SHOW_ALL_SITES);
			String show_other_sites = (String) state.getAttribute(STATE_SHOW_OTHER_SITES);
			context.put("show_other_sites", show_other_sites);
			if(Boolean.TRUE.toString().equals(allowed_to_see_other_sites))
			{
				context.put("allowed_to_see_other_sites", Boolean.TRUE.toString());
				show_all_sites = Boolean.TRUE.toString().equals(show_other_sites);
			}

			if(show_all_sites)
			{
				List other_sites = new Vector();
				/*
				 * NOTE: This does not (and should not) get all sites for admin.  
				 *       Getting all sites for admin is too big a request and
				 *       would result in too big a display to render in html.
				 */
				Map othersites = ContentHostingService.getCollectionMap();
				Iterator siteIt = othersites.keySet().iterator();
				while(siteIt.hasNext())
				{
					String displayName = (String) siteIt.next();
					String collId = (String) othersites.get(displayName);
					if(! collectionId.equals(collId))
					{
						members = getBrowseItems(collId, expandedCollections, highlightedItems, sortedBy, sortedAsc, (BrowseItem) null, false, state);
						if(members != null && members.size() > 0)
						{
							BrowseItem root = (BrowseItem) members.remove(0);
							root.addMembers(members);
							root.setName(displayName);
							other_sites.add(root);
						}
					}
				}

				context.put ("other_sites", other_sites);
			}

			// context.put ("root", root);
			context.put("expandedCollections", expandedCollections);
			state.setAttribute(EXPANDED_COLLECTIONS, expandedCollections);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, rb.getString("cannotfind"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		catch(TypeException e)
		{
			// Log.error("chef", this + "TypeException.");
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		catch(PermissionException e)
		{
			addAlert(state, rb.getString("notpermis1"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
		}
		
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", contentService.newResourceProperties ());

		try
		{
			// TODO: why 'site' here?
			Site site = SiteService.getSite(PortalService.getCurrentSiteId());
			context.put("siteTitle", site.getTitle());
		}
		catch (IdUnusedException e)
		{
			// Log.error("chef", this + e.toString());
		}

		context.put("expandallflag", state.getAttribute(STATE_EXPAND_ALL_FLAG));
		state.removeAttribute(STATE_NEED_TO_EXPAND_ALL);

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		// justDelivered(state);

		// pick the "show" template based on the helper's template name
		// return TEMPLATE_SELECT;
		return TEMPLATE_ATTACH;
		
	}	// buildSelectAttachmentContext

	/**
	* Expand all the collection resources and put in EXPANDED_COLLECTIONS attribute.
	*/
	public void doList ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		state.setAttribute (STATE_MODE, MODE_LIST);
		
	}	// doList


	/**
	* Build the context for add display
	*/
	public String buildWebdavContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		
		boolean inMyWorkspace = SiteService.isUserSite(PortalService.getCurrentSiteId());
		context.put("inMyWorkspace", Boolean.toString(inMyWorkspace));
		
		context.put("server_url", ServerConfigurationService.getServerUrl());
		context.put("site_id", PortalService.getCurrentSiteId());
		context.put("site_title", state.getAttribute(STATE_SITE_TITLE));
		context.put("user_id", UserDirectoryService.getCurrentUser().getId());
		context.put ("dav_group", "/dav/group/");
		context.put ("dav_user", "/dav/user/");

		return TEMPLATE_DAV;

	}	// buildWebdavContext

	/**
	* Build the context for delete confirmation page
	*/
	public String buildDeleteConfirmContext (	VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("collectionId", state.getAttribute (STATE_COLLECTION_ID) );
		
		//%%%% FIXME
		context.put ("collectionPath", state.getAttribute (STATE_COLLECTION_PATH));
		
		List deleteItems = (List) state.getAttribute(STATE_DELETE_ITEMS);
		List nonEmptyFolders = (List) state.getAttribute(STATE_DELETE_ITEMS_NOT_EMPTY);

		context.put ("deleteItems", deleteItems);
		
		Iterator it = nonEmptyFolders.iterator();
		while(it.hasNext())
		{
			BrowseItem folder = (BrowseItem) it.next();
			addAlert(state, rb.getString("folder2") + " " + folder.getName() + " " + rb.getString("contain2") + " ");
		}

		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));
		
		//not show the public option when in dropbox mode
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// not show the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());

		// String template = (String) getContext(data).get("template");
		return TEMPLATE_DELETE_CONFIRM;

	}	// buildDeleteConfirmContext

	/**
	* Build the context to show the list of resource properties
	*/
	public String buildMoreContext (	VelocityPortlet portlet,
									Context context,
									RunData data,
									SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService service = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", service);

		String id = (String) state.getAttribute (STATE_MORE_ID);
		context.put ("id", id);
		String collectionId = (String) state.getAttribute (STATE_MORE_COLLECTION_ID);
		context.put ("collectionId", collectionId);
		String homeCollectionId = (String) (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		context.put("homeCollectionId", homeCollectionId);
		List cPath = getCollectionPath(state);
		context.put ("collectionPath", cPath);
		
		context.put ("from", (String) state.getAttribute (STATE_MORE_FROM));

		// for the resources of type URL or plain text, show the content also
		try
		{
			ResourceProperties properties = service.getProperties (id);
			context.put ("properties", properties);

			String isCollection = properties.getProperty (ResourceProperties.PROP_IS_COLLECTION);
			if ((isCollection != null) && isCollection.equals (Boolean.FALSE.toString()))
			{
				String copyrightAlert = properties.getProperty(properties.getNamePropCopyrightAlert());
				context.put("hasCopyrightAlert", copyrightAlert);

				String type = properties.getProperty (ResourceProperties.PROP_CONTENT_TYPE);
				if (type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_PLAINTEXT) || type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_HTML) || type.equalsIgnoreCase (ResourceProperties.TYPE_URL))
				{
					ContentResource moreResource = service.getResource (id);
					// read the body
					String body = "";
					byte[] content = null;
					try
					{
						content = moreResource.getContent();
						if (content != null)
						{
							body = new String(content);
						}
					}
					catch(ServerOverloadException e) 
					{
						// this represents server's file system is temporarily unavailable
						// report problem to user? log problem?
					}
					context.put ("content", body);
				}	// if
			}	// if
			
			else
			{
				// setup for quota - ADMIN only, collection only
				if (SecurityService.isSuperUser())
				{
					try
					{
						// Getting the quota as a long validates the property
						long quota = properties.getLongProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
						context.put("hasQuota", Boolean.TRUE);
						context.put("quota", properties.getProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA));
					}
					catch (Exception any) {}
				}
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state,RESOURCE_NOT_EXIST_STRING);
			context.put("notExistFlag", new Boolean(true));
		}
		catch (TypeException e)
		{
			addAlert(state, rb.getString("typeex") + " ");
		}
		catch (PermissionException e)
		{
			addAlert(state," " + rb.getString("notpermis2") + " " + id + ". ");
		}	// try-catch

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			context.put("notExistFlag", new Boolean(false));
		}
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview
			boolean pubview = ContentHostingService.isInheritingPubView(id);
			if (!pubview) pubview = ContentHostingService.isPubView(id);
			context.put("pubview", new Boolean(pubview));
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		
		if (state.getAttribute(COPYRIGHT_TYPES) != null)
		{
			List copyrightTypes = (List) state.getAttribute(COPYRIGHT_TYPES);
			context.put("copyrightTypes", copyrightTypes);
		}

		metadataGroupsIntoContext(state, context);
		
		// String template = (String) getContext(data).get("template");
		return TEMPLATE_MORE;

	}	// buildMoreContext

	/**
	* Build the context to show the editable list of resource properties
	*/
	public String buildPropertiesContext (VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService service = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", service);
		context.put ("from", state.getAttribute (STATE_FROM));
		context.put ("mycopyright", (String) state.getAttribute (STATE_MY_COPYRIGHT));

		String collectionId = (String) state.getAttribute (STATE_PROPERTIES_COLLECTION_ID);
		context.put ("collectionId", collectionId);
		String id =(String) state.getAttribute (STATE_PROPERTIES_ID);
		context.put ("id", id);
		String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		context.put("homeCollectionId", homeCollectionId);
		List collectionPath = getCollectionPath(state);	
		context.put ("collectionPath", collectionPath);
		
		String intent = (String) state.getAttribute(STATE_PROPERTIES_INTENT);
		if(intent == null)
		{
			intent = INTENT_REVISE_FILE;
			state.setAttribute(STATE_PROPERTIES_INTENT, intent);
		}
		context.put("intent", intent);
		context.put("REVISE", INTENT_REVISE_FILE);
		context.put("REPLACE", INTENT_REPLACE_FILE);
		
		ResourceProperties properties = null;
		// populate the property list
		try
		{
			properties = service.getProperties (id);
			context.put("replaceable", new Boolean(replaceable(properties)));
		}
		catch (IdUnusedException e)
		{
			addAlert(state, RESOURCE_NOT_EXIST_STRING);
		}
		catch (PermissionException e)
		{
			addAlert(state," " + rb.getString("notpermis2") + " " + id);
		}
		metadataGroupsIntoContext(state, context);
		
		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));

		// for the resources of type URL or plain text, show the content also
		if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.FALSE.toString()))
		{
			String type = properties.getProperty (ResourceProperties.PROP_CONTENT_TYPE);
			if ((type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_PLAINTEXT)) || (type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_HTML)) || (type.equalsIgnoreCase (ResourceProperties.TYPE_URL)))
			{
				try
				{
					ContentResource propertiesResource = service.getResource (id);
					// read the body
					String body = "";
					byte[] content = null;
					try
					{
						content = propertiesResource.getContent();
						if (content != null)
						{
							body = new String (content);
						}
					}
					catch(ServerOverloadException e) 
					{
						// this represents server's file system is temporarily unavailable
						// report problem to user? log problem?
					}
					context.put ("content", body);
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (TypeException e)
				{
					addAlert(state, " " + rb.getString("typeex") + " ");
				}
				catch (PermissionException e)
				{
					addAlert(state," " + rb.getString("notpermis2") + " " + id + ". ");
				}
			}
		}
		
		// for collections only
		else
		{
			// setup for quota - ADMIN only, collection only
			if (SecurityService.isSuperUser())
			{
				try
				{
					// Getting the quota as a long validates the property value
					long quota = properties.getLongProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
					context.put("hasQuota", Boolean.TRUE);
					context.put("quota", properties.getProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA));
				}
				catch (Exception any)
				{
					context.put("hasQuota", Boolean.FALSE);
				}

				context.put("setQuota", Boolean.TRUE);
			}
		}

		context.put ("properties", properties);

		// copyright
		copyrightChoicesIntoContext(state, context);
		context.put("copyrightChoice", state.getAttribute (STATE_PROPERTIES_COPYRIGHT_CHOICE));
		context.put("newcopyright", state.getAttribute (STATE_PROPERTIES_COPYRIGHT));
		String alert = (String) state.getAttribute(STATE_PROPERTIES_COPYRIGHT_ALERT);
		if (alert != null && alert.equalsIgnoreCase(Boolean.TRUE.toString()))
		{
			context.put("copyrightAlert", Boolean.TRUE);
		}
		/*
		else
		{
			context.put("copyrightAlert", Boolean.FALSE);
		}
		*/
		context.put("copyright_choice", state.getAttribute(STATE_PROPERTIES_COPYRIGHT_CHOICE));
		context.put("copyright", state.getAttribute(STATE_PROPERTIES_COPYRIGHT));


		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview
			context.put("pubviewset", new Boolean(ContentHostingService.isInheritingPubView(id)));
			context.put("pubview", new Boolean(ContentHostingService.isPubView(id)));

		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		
		// String template = (String) getContext(data).get("template");
		return TEMPLATE_PROPERTIES;

	}	// buildPropertiesContext

	/**
	* Build the context to edit the editable list of resource properties
	*/
	public String buildEditContext (VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{

		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("from", state.getAttribute (STATE_FROM));
		context.put ("mycopyright", (String) state.getAttribute (STATE_MY_COPYRIGHT));

		String collectionId = (String) state.getAttribute (STATE_EDIT_COLLECTION_ID);
		context.put ("collectionId", collectionId);
		String id =(String) state.getAttribute (STATE_EDIT_ID);
		context.put ("id", id);
		String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		context.put("homeCollectionId", homeCollectionId);
		List collectionPath = getCollectionPath(state);	
		context.put ("collectionPath", collectionPath);

		if(homeCollectionId.equals(id))
		{
			context.put("atHome", Boolean.TRUE.toString());
		}
		
		String intent = (String) state.getAttribute(STATE_PROPERTIES_INTENT);
		if(intent == null)
		{
			intent = INTENT_REVISE_FILE;
			state.setAttribute(STATE_PROPERTIES_INTENT, intent);
		}
		context.put("intent", intent);
		context.put("REVISE", INTENT_REVISE_FILE);
		context.put("REPLACE", INTENT_REPLACE_FILE);
		
		String show_form_items = (String) state.getAttribute(STATE_SHOW_FORM_ITEMS);
		if(show_form_items != null)
		{
			context.put("show_form_items", show_form_items);
		}

		// put the item into context
		EditItem item = (EditItem)state.getAttribute(STATE_EDIT_ITEM);

		context.put("item", item);
		
		if(item.isStructuredArtifact())
		{
			context.put("formtype", item.getFormtype());
			state.setAttribute(STATE_STRUCTOBJ_TYPE, item.getFormtype());
			
			List listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
			context.put("homes", listOfHomes);

			context.put("STRING", ResourcesMetadata.WIDGET_STRING);
			context.put("TEXTAREA", ResourcesMetadata.WIDGET_TEXTAREA);
			context.put("BOOLEAN", ResourcesMetadata.WIDGET_BOOLEAN);
			context.put("INTEGER", ResourcesMetadata.WIDGET_INTEGER);
			context.put("DOUBLE", ResourcesMetadata.WIDGET_DOUBLE);
			context.put("DATE", ResourcesMetadata.WIDGET_DATE);
			context.put("TIME", ResourcesMetadata.WIDGET_TIME);
			context.put("DATETIME", ResourcesMetadata.WIDGET_DATETIME);
			context.put("ANYURI", ResourcesMetadata.WIDGET_ANYURI);
			context.put("ENUM", ResourcesMetadata.WIDGET_ENUM);
			context.put("NESTED", ResourcesMetadata.WIDGET_NESTED);
			
			context.put("today", TimeService.newTime());
			
			context.put("TRUE", Boolean.TRUE.toString());
		}

		// copyright
		copyrightChoicesIntoContext(state, context);

		// put schema for metadata into context
		metadataGroupsIntoContext(state, context);

		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		
		// String template = (String) getContext(data).get("template");

		return TEMPLATE_EDIT;

	}	// buildEditContext

	/**
	* Navigate in the resource hireachy
	*/
	public static void doNavigate ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		if (state.getAttribute (STATE_SELECT_ALL_FLAG)!=null && state.getAttribute (STATE_SELECT_ALL_FLAG).equals (Boolean.TRUE.toString()))
		{
			state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
		}
		
		if (state.getAttribute (STATE_EXPAND_ALL_FLAG)!=null && state.getAttribute (STATE_EXPAND_ALL_FLAG).equals (Boolean.TRUE.toString()))
		{
			state.setAttribute (STATE_EXPAND_ALL_FLAG, Boolean.FALSE.toString());
		}

		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = data.getParameters ().getStrings ("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);
		
		String collectionId = data.getParameters().getString ("collectionId");
		String navRoot = data.getParameters().getString("navRoot");
		state.setAttribute(STATE_NAVIGATION_ROOT, navRoot);
		
		// the exception message
		
		try
		{
			ContentHostingService.checkCollection(collectionId);
		}
		catch(PermissionException e)
		{
			addAlert(state, " " + rb.getString("notpermis3") + " " );
		}
		catch (IdUnusedException e)
		{
			addAlert(state, " " + rb.getString("notexist2") + " ");
		}
		catch (TypeException e)
		{
			addAlert(state," " + rb.getString("notexist2") + " ");
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			String oldCollectionId = (String) state.getAttribute(STATE_COLLECTION_ID);
			// update this folder id in the set to be event-observed
			removeObservingPattern(oldCollectionId, state);
			addObservingPattern(collectionId, state);
			
			state.setAttribute(STATE_COLLECTION_ID, collectionId);
			state.setAttribute(EXPANDED_COLLECTIONS, new HashMap());
		}

		String mode = (String) state.getAttribute(STATE_MODE);
		if(MODE_HELPER.equals(mode))
		{
			state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
		}
		else
		{
			state.setAttribute (STATE_MODE, MODE_LIST);
		}

	}	// doNavigate

	/**
	* Show information about WebDAV
	*/
	public void doShow_webdav ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		state.setAttribute (STATE_MODE, MODE_DAV);

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

	}	// doShow_webdav
	
	/**
	 * initiate creation of one or more resource items (folders, file uploads, html docs, text docs, or urls)
	 * default type is folder
	 */
	public static void doCreate(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		String itemType = params.getString("itemType");
		if(itemType == null || "".equals(itemType))
		{
			itemType = TYPE_UPLOAD;
		}
		
		List new_items = new Vector();
		for(int i = 0; i < CREATE_MAX_ITEMS; i++)
		{
			new_items.add(new EditItem(itemType));
		}
		state.setAttribute(STATE_CREATE_ITEMS, new_items);
		state.setAttribute(STATE_CREATE_NUMBER, new Integer(1));
		state.setAttribute(STATE_CREATE_TYPE, itemType);
		state.setAttribute(STATE_CREATE_ALERTS, new HashSet());
		state.setAttribute(STATE_CREATE_MISSING_ITEM, new HashSet());
		state.removeAttribute(STATE_STRUCTOBJ_TYPE);
		state.removeAttribute(STATE_STRUCTOBJ_HOMES);
		
		String collectionId = params.getString ("collectionId");
		state.setAttribute(STATE_CREATE_COLLECTION_ID, collectionId);

		String mode = (String) state.getAttribute(STATE_MODE);
		if(MODE_HELPER.equals(mode))
		{
			state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_CREATE);
		}
		else
		{
			state.setAttribute (STATE_MODE, MODE_CREATE);
		} 
		
	}	// doCreate
	
	/**
	 * initiate creation of one or more resource items (file uploads, html docs, text docs, or urls -- not folders)
	 * default type is file upload
	 */
	public static void doCreateitem(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		String itemType = params.getString("itemType");
		String flow = params.getString("flow");
		String mode = (String) state.getAttribute(STATE_MODE);
		String helper_mode = (String) state.getAttribute(STATE_RESOURCES_MODE);
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		Set missing = new HashSet();
		
		if(flow == null || flow.equals("cancel"))
		{
			if(MODE_HELPER.equals(mode))
			{
				helper_mode = MODE_ATTACHMENT_SELECT;
			}
			else
			{
				mode = MODE_LIST;
			}
		}
		else if(flow.equals("updateNumber"))
		{
			captureMultipleValues(state, params, false);
			int number = params.getInt("numberOfItems");
			Integer numberOfItems = new Integer(number);
			state.setAttribute(STATE_CREATE_NUMBER, numberOfItems);
			
			// clear display of error messages
			state.setAttribute(STATE_CREATE_ALERTS, new HashSet());
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				EditItem item = (EditItem) it.next();
				item.clearMissing();
			}
			state.setAttribute(STATE_CREATE_ITEMS, items);
			state.removeAttribute(STATE_MESSAGE);
		}
		else if(flow.equals("create") && TYPE_FOLDER.equals(itemType))
		{
			// Get the items
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			
			if(alerts.isEmpty())
			{
				// Save the items
				createFolders(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create") && TYPE_UPLOAD.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create") && MIME_TYPE_DOCUMENT_HTML.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create") && MIME_TYPE_DOCUMENT_PLAINTEXT.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create") && TYPE_URL.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createUrls(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create") && TYPE_FORM.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createStructuredArtifacts(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					if(MODE_HELPER.equals(mode))
					{
						helper_mode = MODE_ATTACHMENT_SELECT;
					}
					else
					{
						mode = MODE_LIST;
					}
					state.removeAttribute(STATE_CREATE_ITEMS);
				}
			}
		}
		else if(flow.equals("create"))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			alerts.add("Invalid item type");
			state.setAttribute(STATE_CREATE_ALERTS, alerts);
		}
		else if(flow.equals("updateDocType"))
		{
			// captureMultipleValues(state, params, false);
			String formtype = params.getString("formtype");
			if(formtype == null || formtype.equals(""))
			{
				alerts.add("Must select a form type");
				missing.add("formtype");
			}
			state.setAttribute(STATE_STRUCTOBJ_TYPE, formtype);
			setupStructuredObjects(state);
		}
		else if(flow.equals("addInstance"))
		{
			captureMultipleValues(state, params, false);
			String field = params.getString("field");
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			EditItem item = (EditItem) items.get(0);
			addInstance(field, item.getProperties());
			ResourcesMetadata form = item.getForm();
			List flatList = form.getFlatList();
			item.setProperties(flatList);
		}
		else if(flow.equals("showOptional"))
		{
			captureMultipleValues(state, params, false);
			int twiggleNumber = params.getInt("twiggleNumber", 0);
			String metadataGroup = params.getString("metadataGroup");
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			if(items != null && items.size() > twiggleNumber)
			{
				EditItem item = (EditItem) items.get(twiggleNumber);
				if(item != null)
				{
					item.showMetadataGroup(metadataGroup);
				}
			}
			
			// clear display of error messages
			state.setAttribute(STATE_CREATE_ALERTS, new HashSet());
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				EditItem item = (EditItem) it.next();
				item.clearMissing();
			}
			state.setAttribute(STATE_CREATE_ITEMS, items);
		}
		else if(flow.equals("hideOptional"))
		{
			captureMultipleValues(state, params, false);
			int twiggleNumber = params.getInt("twiggleNumber", 0);						
			String metadataGroup = params.getString("metadataGroup");
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			if(items != null && items.size() > twiggleNumber)
			{
				EditItem item = (EditItem) items.get(twiggleNumber);
				if(item != null)
				{
					item.hideMetadataGroup(metadataGroup);
				}
			}
			
			// clear display of error messages
			state.setAttribute(STATE_CREATE_ALERTS, new HashSet());
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				EditItem item = (EditItem) it.next();
				item.clearMissing();
			}
			state.setAttribute(STATE_CREATE_ITEMS, items);
		}
		
		alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		Iterator alertIt = alerts.iterator();
		while(alertIt.hasNext())
		{
			String alert = (String) alertIt.next();
			addAlert(state, alert);
		}
		alerts.clear();
		state.setAttribute(STATE_CREATE_MISSING_ITEM, missing);
		
		// remove?
		state.setAttribute(STATE_CREATE_ALERTS, alerts);

		if(MODE_HELPER.equals(mode))
		{
			state.setAttribute(STATE_RESOURCES_MODE, helper_mode);
		}
		else
		{
			state.setAttribute (STATE_MODE, mode);
		}
				
	}	// doCreateitem
	
	/**
	 * Add a new StructuredArtifact to ContentHosting for each EditItem in the state attribute named STATE_CREATE_ITEMS.  
	 * The number of items to be added is indicated by the state attribute named STATE_CREATE_NUMBER, and
	 * the items are added to the collection identified by the state attribute named STATE_CREATE_COLLECTION_ID. 
	 * @param state
	 */
	private static void createStructuredArtifacts(SessionState state) 
	{
		// List listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
		List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);

		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		int numberOfItems = 1;
		if(number != null)
		{
			numberOfItems = number.intValue();
		}
		
		SchemaBean rootSchema = (SchemaBean) state.getAttribute(STATE_STRUCT_OBJ_SCHEMA);
		SchemaNode rootNode = rootSchema.getSchema();
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		
		outerloop: for(int i = 0; i < numberOfItems; i++)
		{
			EditItem item = (EditItem) items.get(i);
			if(item.isBlank())
			{
				continue;
			}
			SaveArtifactAttempt attempt = new SaveArtifactAttempt(item, rootNode);
			validateStructuredArtifact(attempt);
			List errors = attempt.getErrors();
			
			if(errors.isEmpty())
			{
				try
				{
					ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
					resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, item.getName());
					resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());
					resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
					resourceProperties.addProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE, item.getFormtype());
					List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
					saveMetadata(resourceProperties, metadataGroups, item);
					String filename = Validator.escapeResourceName(item.getName()).trim();
					String extension = ".xml";
					int attemptNum = 0;
					String attemptStr = "";
					String newResourceId = collectionId + filename + attemptStr + extension;

					if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
					{
						alerts.add(rb.getString("toolong") + " " + newResourceId);
						continue outerloop;
					}

					boolean tryingToAddItem = true;
					while(tryingToAddItem)
					{
						
						try
						{
							ContentResource resource = ContentHostingService.addResource (newResourceId,
																						MIME_TYPE_STRUCTOBJ,
																						item.getContent(),
																						resourceProperties, 
																						item.getNotification());
							tryingToAddItem = false;
							// ResourceProperties rp = resource.getProperties();
							// item.setAdded(true);
							

							if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
							{
								// deal with pubview when in resource mode//%%%
								if (! item.isPubviewset())
								{
									ContentHostingService.setPubView(resource.getId(), item.isPubview());
								}
							}
							
							HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
							if(!currentMap.containsKey(collectionId))
							{
								try
								{
									currentMap.put (collectionId,ContentHostingService.getCollection (collectionId));
									state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
					
									// add this folder id into the set to be event-observed
									addObservingPattern(collectionId, state);
								}
								catch (IdUnusedException ignore)
								{
								}
								catch (TypeException ignore)
								{
								}
								catch (PermissionException ignore)
								{
								} 
							}
							state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
							
							String mode = (String) state.getAttribute(STATE_MODE);
							if(MODE_HELPER.equals(mode))
							{
								attachItem(newResourceId, state);
							}
						}
						catch (IdUsedException e)
						{
							boolean foundUnusedId = false;
							for(int trial = 1;!foundUnusedId && trial < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS; trial++)
							{
								attemptNum++;
								attemptStr = Integer.toString(attemptNum);
							

								// add extension if there was one
								newResourceId = collectionId + filename + "-" + attemptStr + extension;
								
								if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
								{
									alerts.add(rb.getString("toolong") + " " + newResourceId);
									state.setAttribute(STATE_CREATE_ALERTS, alerts);
									return;
								}
								
								try 
								{
									ContentHostingService.getResource(newResourceId);
								}
								catch (IdUnusedException ee)
								{
									foundUnusedId = true;
								}
								// TODO: should these be here?
								catch (PermissionException ee)
								{
									foundUnusedId = true;
								}
								catch (TypeException ee)
								{
									foundUnusedId = true;
								}
							}
						}
						catch(PermissionException e)
						{
							alerts.add(rb.getString("notpermis12"));
							tryingToAddItem = false;
						}
						catch(IdInvalidException e)
						{
							alerts.add(rb.getString("title") + " " + e.getMessage ());
							tryingToAddItem = false;
						}
						catch(InconsistentException e)
						{
							alerts.add(RESOURCE_INVALID_TITLE_STRING);
							tryingToAddItem = false;
						}
						catch(OverQuotaException e)
						{
							alerts.add(rb.getString("overquota"));
							tryingToAddItem = false;
						}
						catch(ServerOverloadException e)
						{
							alerts.add(rb.getString("failed"));
							tryingToAddItem = false;
						}
					}
				}
				catch(Throwable e)
				{			

				}
			}
			else
			{
				Iterator errorIt = errors.iterator();
				while(errorIt.hasNext())
				{
					ValidationError error = (ValidationError) errorIt.next();
					alerts.add(error.getDefaultMessage());
				}
			}

		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}
	
	/**
	 * Convert from a hierarchical list of ResourcesMetadata objects to an org.w3.dom.Document,
	 * then to a string representation, then to a metaobj ElementBean.  Validate the ElementBean
	 * against a SchemaBean.  If it validates, save the string representation. Otherwise, on 
	 * return, the parameter contains a non-empty list of ValidationError objects describing the 
	 * problems. 
	 * @param attempt A wrapper for the EditItem object which contains the hierarchical list of 
	 * ResourcesMetadata objects for this form.  Also contains an initially empty list of 
	 * ValidationError objects that describes any of the problems found in validating the form.
	 */
	private static void validateStructuredArtifact(SaveArtifactAttempt attempt)
	{
		EditItem item = attempt.getItem();
		ResourcesMetadata form = item.getForm();

		Stack processStack = new Stack();
		processStack.push(form);
		Map parents = new Hashtable();
		Document doc = Xml.createDocument();
		
		int count = 0;
		
		while(!processStack.isEmpty())
		{
			Object object = processStack.pop();
			if(object instanceof ResourcesMetadata)
			{

				ResourcesMetadata element = (ResourcesMetadata) object;
				Element node = doc.createElement(element.getLocalname());
				
				if(element.isNested())
				{
					processStack.push(new ElementCarrier(node, element.getDottedname()));
					List children = element.getNestedInstances();
					//List children = element.getNested();
					
					for(int k = children.size() - 1; k >= 0; k--)
					{
						ResourcesMetadata child = (ResourcesMetadata) children.get(k);
						processStack.push(child);
						parents.put(child.getDottedname(), node);
					}
				}
				else
				{
					List values = element.getInstanceValues();
					Iterator valueIt = values.iterator();
					while(valueIt.hasNext())
					{
						Object value = valueIt.next();
						if(value == null)
						{
							// do nothing
						}
						else if(value instanceof String)
						{
							node.appendChild(doc.createTextNode((String)value));
						}
						else if(value instanceof Time)
						{
							Time time = (Time) value;
							TimeBreakdown breakdown = time.breakdownLocal();
							int year = breakdown.getYear();
							int month = breakdown.getMonth();
							int day = breakdown.getDay();
							String date = "" + year + (month < 10 ? "-0" : "-") + month + (day < 10 ? "-0" : "-") + day;
							node.appendChild(doc.createTextNode(date));
						}
						else if(value instanceof Date)
						{
							Date date = (Date) value;
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							String formatted = df.format(date);
							node.appendChild(doc.createTextNode(formatted));
						}
						else
						{
							node.appendChild(doc.createTextNode(value.toString()));
						}
					}
					
					Element parent = (Element) parents.get(element.getDottedname());
					if(parent == null)
					{
						doc.appendChild(node);
						count++;
					}
					else
					{
						parent.appendChild(node);
					}
				}
			}
			else if(object instanceof ElementCarrier)
			{
				ElementCarrier carrier = (ElementCarrier) object;
				Element node = carrier.getElement();
				Element parent = (Element) parents.get(carrier.getParent());
				if(parent == null)
				{
					doc.appendChild(node);
					count++;
				}
				else
				{
					parent.appendChild(node);
				}
			}
			
		}
		
		String content = Xml.writeDocumentToString(doc);
		item.setContent(content);
		
		StructuredArtifactValidationService validator = (StructuredArtifactValidationService) ComponentManager.get("org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService");
		List errors = new ArrayList();
		
		// convert the String representation to an ElementBean object.  If that fails,
		// add an error and return.
		ElementBean bean = null;
		
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader(content);
		try 
		{
			org.jdom.Document jdoc = builder.build(reader);
			bean = new ElementBean(jdoc.getRootElement(), attempt.getSchema(), true);
		} 
		catch (JDOMException e) 
		{
			// add message to list of errors
			errors.add(new ValidationError("","",null,"JDOMException"));
		} 
		catch (IOException e) 
		{
			// add message to list of errors
			errors.add(new ValidationError("","",null,"IOException"));
		}
		
		// call this.validate(bean, rootSchema, errors) and add results to errors list. 
		if(bean == null)
		{
			// add message to list of errors
			errors.add(new ValidationError("","",null,"Bean is null"));
		}
		else
		{
			errors.addAll(validator.validate(bean));
		}
		attempt.setErrors(errors);
		
	}	// validateStructuredArtifact

	/**
	 * Add a new folder to ContentHosting for each EditItem in the state attribute named STATE_CREATE_ITEMS.  
	 * The number of items to be added is indicated by the state attribute named STATE_CREATE_NUMBER, and
	 * the items are added to the collection identified by the state attribute named STATE_CREATE_COLLECTION_ID. 
	 * @param state
	 */
	protected static void createFolders(SessionState state) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_folders = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfFolders = 1;
		numberOfFolders = number.intValue();
		
		outerloop: for(int i = 0; i < numberOfFolders; i++)
		{
			EditItem item = (EditItem) new_folders.get(i);
			if(item.isBlank())
			{
				continue;
			}
			String newCollectionId = collectionId + Validator.escapeResourceName(item.getName()) + Entity.SEPARATOR;
			
			if(newCollectionId.length() > RESOURCE_ID_MAX_LENGTH)
			{
				alerts.add(rb.getString("toolong") + " " + newCollectionId);
				continue outerloop;
			}

			ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();

			try
			{
				resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, item.getName());
				resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());
				List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
				saveMetadata(resourceProperties, metadataGroups, item);
			
				ContentCollection collection = ContentHostingService.addCollection (newCollectionId, resourceProperties);

				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					// deal with pubview in resource mode//%%%
					// boolean pubviewset = ContentHostingService.isInheritingPubView(collection.getId());
					if (!item.isPubviewset())
					{
						ContentHostingService.setPubView(collection.getId(), item.isPubview());
					}
				}
			}
			catch (IdUsedException e)
			{
				alerts.add(rb.getString("resotitle") + " " + item.getName() + " " + rb.getString("used4"));
			}
			catch (IdInvalidException e)
			{
				alerts.add(rb.getString("title") + " " + e.getMessage ());
			}
			catch (PermissionException e)
			{
				alerts.add(rb.getString("notpermis5") + " " + item.getName());
			}
			catch (InconsistentException e)
			{
				alerts.add(RESOURCE_INVALID_TITLE_STRING);
			}	// try-catch
		}
		
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
		if(!currentMap.containsKey(collectionId))
		{
			try
			{
				currentMap.put (collectionId,ContentHostingService.getCollection (collectionId));
				state.setAttribute(EXPANDED_COLLECTIONS, currentMap);

				// add this folder id into the set to be event-observed
				addObservingPattern(collectionId, state);
			}
			catch (IdUnusedException ignore)
			{
			}
			catch (TypeException ignore)
			{
			}
			catch (PermissionException ignore)
			{
			} 
		}
		state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createFolders

	/**
	 * Add a new file to ContentHosting for each EditItem in the state attribute named STATE_CREATE_ITEMS.  
	 * The number of items to be added is indicated by the state attribute named STATE_CREATE_NUMBER, and
	 * the items are added to the collection identified by the state attribute named STATE_CREATE_COLLECTION_ID. 
	 * @param state
	 */
	protected static void createFiles(SessionState state) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_files = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfItems = 1;
		numberOfItems = number.intValue();
		
		outerloop: for(int i = 0; i < numberOfItems; i++)
		{
			EditItem item = (EditItem) new_files.get(i);
			if(item.isBlank())
			{
				continue;
			}
			
			ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
			
			resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, item.getName());							
			resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());

			resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, item.getCopyrightInfo());
			resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, item.getCopyrightStatus());
			if (item.hasCopyrightAlert())
			{
				resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, Boolean.toString(item.hasCopyrightAlert()));
			}
			else
			{
				resourceProperties.removeProperty (ResourceProperties.PROP_COPYRIGHT_ALERT);
			}
			
			resourceProperties.addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.FALSE.toString());
			if(item.isHtml())
			{
				resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
			}
			List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
			saveMetadata(resourceProperties, metadataGroups, item);
			String filename = Validator.escapeResourceName(item.getFilename()).trim();
			if("".equals(filename))
			{
				filename = Validator.escapeResourceName(item.getName());
			}
			
			resourceProperties.addProperty(ResourceProperties.PROP_ORIGINAL_FILENAME, filename);

			int extensionIndex = filename.lastIndexOf (".");
			String extension = "";
			if (extensionIndex > 0)
			{
				extension = filename.substring (extensionIndex);
				filename = filename.substring(0, extensionIndex);
			}
			int attemptNum = 0;
			String attempt = "";
			String newResourceId = collectionId + filename + attempt + extension;

			if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
			{
				alerts.add(rb.getString("toolong") + " " + newResourceId);
				continue outerloop;
			}
			
			boolean tryingToAddItem = true;
			while(tryingToAddItem)
			{
				
				try
				{
					ContentResource resource = ContentHostingService.addResource (newResourceId,
																				item.getMimeType(),
																				item.getContent(),
																				resourceProperties, item.getNotification());
					tryingToAddItem = false;
					// ResourceProperties rp = resource.getProperties();
					item.setAdded(true);
					

					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// deal with pubview when in resource mode//%%%
						if (! item.isPubviewset())
						{
							ContentHostingService.setPubView(resource.getId(), item.isPubview());
						}
					}
					String mode = (String) state.getAttribute(STATE_MODE);
					if(MODE_HELPER.equals(mode))
					{
						attachItem(newResourceId, state);
					}
				}
				catch (IdUsedException e)
				{
					boolean foundUnusedId = false;
					for(int trial = 1;!foundUnusedId && trial < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS; trial++)
					{
						attemptNum++;
						attempt = Integer.toString(attemptNum);
					

						// add extension if there was one
						newResourceId = collectionId + filename + "-" + attempt + extension;
						
						if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
						{
							alerts.add(rb.getString("toolong") + " " + newResourceId);
							continue outerloop;
						}

						try 
						{
							ContentHostingService.getResource(newResourceId);
						}
						catch (IdUnusedException ee)
						{
							foundUnusedId = true;
						}
						// TODO: should these be here? Or should these be caught outside the for-loop?
						catch (PermissionException ee)
						{
							// TODO: is this really an unused ID?
							foundUnusedId = true;
						}
						catch (TypeException ee)
						{
							// TODO: is this really an unused ID?
							foundUnusedId = true;
						}
					}
				}
				catch(PermissionException e)
				{
					alerts.add(rb.getString("notpermis12"));
					tryingToAddItem = false;
				}
				catch(IdInvalidException e)
				{
					alerts.add(rb.getString("title") + " " + e.getMessage ());
					tryingToAddItem = false;
				}
				catch(InconsistentException e)
				{
					alerts.add(RESOURCE_INVALID_TITLE_STRING);
					tryingToAddItem = false;
				}
				catch (ServerOverloadException e)
				{
					alerts.add(rb.getString("failed"));
					tryingToAddItem = false;
				}
				catch(OverQuotaException e)
				{
					alerts.add(rb.getString("overquota"));
					tryingToAddItem = false;
				}
			}
			HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
			if(!currentMap.containsKey(collectionId))
			{
				try
				{
					currentMap.put (collectionId,ContentHostingService.getCollection (collectionId));
					state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
	
					// add this folder id into the set to be event-observed
					addObservingPattern(collectionId, state);
				}
				catch (IdUnusedException ignore)
				{
				}
				catch (TypeException ignore)
				{
				}
				catch (PermissionException ignore)
				{
				} 
			}
			state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
				
		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createFiles
	
	/**
	 * Process user's request to add an instance of a particular field to a structured object.
	 * @param data
	 */
	public static void doInsertValue(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		captureMultipleValues(state, params, false);
		
		String field = params.getString("field");
		
		EditItem item = null;
		String mode = (String) state.getAttribute(STATE_MODE);
		if (MODE_CREATE.equals(mode))
		{
			int index = params.getInt("index");
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			item = (EditItem) items.get(index);
		}
		else if(MODE_EDIT.equals(mode))
		{
			item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);
		}
		
		if(item != null)
		{
			addInstance(field, item.getProperties());
		}
		
	}	// doInsertValue
	
	/**
	 * Search a flat list of ResourcesMetadata properties for one whose localname matches "field".  
	 * If found and the field can have additional instances, increment the count for that item.
	 * @param field
	 * @param properties
	 * @return true if the field is found, false otherwise.
	 */
	protected static boolean addInstance(String field, List properties)
	{
		Iterator propIt = properties.iterator();
		boolean found = false;
		while(!found && propIt.hasNext())
		{
			ResourcesMetadata property = (ResourcesMetadata) propIt.next();
			if(field.equals(property.getDottedname()))
			{
				found = true;
				property.incrementCount();
			}
		}
		return found;
	}
	
	public static void doAttachitem(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();

		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		String itemId = params.getString("itemId");
		attachItem(itemId, state);

		state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);

	}
	
	public static void doAttachupload(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String max_file_size_mb = (String) state.getAttribute(FILE_UPLOAD_MAX_SIZE);
		int max_bytes = 1096 * 1096;
		try
		{
			max_bytes = Integer.parseInt(max_file_size_mb) * 1096 * 1096;
		}
		catch(Exception e)
		{
			// if unable to parse an integer from the value 
			// in the properties file, use 1 MB as a default
			max_file_size_mb = "1";
			max_bytes = 1096 * 1096;
		}

		FileItem fileitem = null;
		try
		{
			fileitem = params.getFileItem("upload");
		}
		catch(Exception e)
		{
			
		}
		if(fileitem == null)
		{
			// "The user submitted a file to upload but it was too big!"
			addAlert(state, rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
		}
		else if (fileitem.getFileName() == null || fileitem.getFileName().length() == 0)
		{
			addAlert(state, rb.getString("choosefile7"));
		}
		else if (fileitem.getFileName().length() > 0)
		{
			String filename = Validator.getFileName(fileitem.getFileName());
			byte[] bytes = fileitem.get();
			String contentType = fileitem.getContentType();
			
			if(bytes.length >= max_bytes)
			{
				addAlert(state, rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
			}
			else if(bytes.length > 0)
			{
				// we just want the file name part - strip off any drive and path stuff
				String name = Validator.getFileName(filename);
				String resourceId = Validator.escapeResourceName(name);

				// make a set of properties to add for the new resource
				ResourcePropertiesEdit props = ContentHostingService.newResourceProperties();
				props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
				props.addProperty(ResourceProperties.PROP_DESCRIPTION, filename);

				// make an attachment resource for this URL
				try
				{
					String siteId = PortalService.getCurrentSiteId();
					String toolName = ToolManager.getCurrentTool().getTitle();
					
					ContentResource attachment = ContentHostingService.addAttachmentResource(resourceId, siteId, toolName, contentType, bytes, props);
		
					List attached = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
					if(attached == null)
					{
						attached = new Vector();
					}
					
					String containerId = ContentHostingService.getContainingCollectionId (attachment.getId());
					String accessUrl = ContentHostingService.getUrl(attachment.getId());
					
					AttachItem item = new AttachItem(attachment.getId(), filename, containerId, accessUrl);
					item.setContentType(contentType);
					attached.add(item);
					state.setAttribute(STATE_HELPER_CHANGED, Boolean.TRUE.toString());
					state.setAttribute(STATE_HELPER_NEW_ITEMS, attached);
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis4"));
				}
				catch(OverQuotaException e)
				{
					addAlert(state, rb.getString("overquota"));
				}
				catch(ServerOverloadException e)
				{
					addAlert(state, rb.getString("failed"));
				}
				catch(Exception ignore)
				{
					// other exceptions should be caught earlier
				}
			}
			else 
			{
				addAlert(state, rb.getString("choosefile7"));
			}
		}

		state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);

	}	// doAttachupload
	
	public static void doAttachurl(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String url = params.getCleanString("url");
		
		ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
		resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, url);							
		resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, url);

		resourceProperties.addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.FALSE.toString());

		try
		{
			url = validateURL(url);
			
			byte[] newUrl = url.getBytes();
			String newResourceId = Validator.escapeResourceName(url);
			
			String siteId = PortalService.getCurrentSiteId();
			String toolName = ToolManager.getCurrentTool().getTitle();
		
			ContentResource attachment = ContentHostingService.addAttachmentResource(newResourceId, siteId, toolName, ResourceProperties.TYPE_URL, newUrl, resourceProperties);

			List attached = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
			if(attached == null)
			{
				attached = new Vector();
			}
			
			String containerId = ContentHostingService.getContainingCollectionId (attachment.getId());
			String accessUrl = ContentHostingService.getUrl(attachment.getId());
			
			AttachItem item = new AttachItem(attachment.getId(), url, containerId, accessUrl);
			item.setContentType(ResourceProperties.TYPE_URL);
			attached.add(item);
			state.setAttribute(STATE_HELPER_CHANGED, Boolean.TRUE.toString());
			state.setAttribute(STATE_HELPER_NEW_ITEMS, attached);
		}
		catch(MalformedURLException e)
		{
			// invalid url
			addAlert(state, rb.getString("validurl") + " \"" + url + "\" " + rb.getString("invalid"));
		}
		catch (PermissionException e)
		{
			addAlert(state, rb.getString("notpermis4"));
		}
		catch(OverQuotaException e)
		{
			addAlert(state, rb.getString("overquota"));
		}
		catch(ServerOverloadException e)
		{
			addAlert(state, rb.getString("failed"));
		}
		catch(Exception ignore)
		{
			// other exceptions should be caught earlier
		}

		state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);

	}
	
	public static void doRemoveitem(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		String itemId = params.getString("itemId");
		
		List attached = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
		if(attached == null)
		{
			attached = new Vector();
		}
		AttachItem item = null;
		boolean found = false;
		
		Iterator it = attached.iterator();
		while(!found && it.hasNext())
		{
			item = (AttachItem) it.next();
			if(item.getId().equals(itemId))
			{
				found = true;
			}
		}

		if(found && item != null)
		{
			attached.remove(item);
			state.setAttribute(STATE_HELPER_CHANGED, Boolean.TRUE.toString());
		}

		state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);

	}
	
	public static void doAddattachments(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		List attached = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
		
		// add to the attachments vector
		List attachments = EntityManager.newReferenceList();
		
		Iterator it = attached.iterator();
		while(it.hasNext())
		{
			AttachItem item = (AttachItem) it.next();
			
			try
			{
				Reference ref = EntityManager.newReference(ContentHostingService.getReference(item.getId()));
				attachments.add(ref);
			}
			catch(Exception e)
			{
			}
		}
		cleanupState(state);
		state.setAttribute(STATE_ATTACHMENTS, attachments);

		// if there is at least one attachment
		if (attachments.size() > 0)
		{
			state.setAttribute(AttachmentAction.STATE_HAS_ATTACHMENT_BEFORE, Boolean.TRUE);
		}
		
		// end up in main mode
		state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_DONE);
		
	}
	
	public static void attachItem(String itemId, SessionState state)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		List attached = (List) state.getAttribute(STATE_HELPER_NEW_ITEMS);
		if(attached == null)
		{
			attached = new Vector();
		}
		
		boolean found = false;
		Iterator it = attached.iterator();
		while(!found && it.hasNext())
		{
			AttachItem item = (AttachItem) it.next();
			if(item.getId().equals(itemId))
			{
				found = true;
			}
		}
		
		if(!found)
		{
			try
			{
				ContentResource res = contentService.getResource(itemId);				
				ResourceProperties props = res.getProperties();
				
				ResourcePropertiesEdit newprops = contentService.newResourceProperties();
				newprops.set(props);
				
				byte[] bytes = res.getContent();
				String contentType = res.getContentType();
				String filename = Validator.getFileName(itemId);
				String resourceId = Validator.escapeResourceName(filename);
				
				String siteId = PortalService.getCurrentSiteId();
				String toolName = ToolManager.getCurrentTool().getTitle();
				
				ContentResource attachment = ContentHostingService.addAttachmentResource(resourceId, siteId, toolName, contentType, bytes, props);
				
				String displayName = newprops.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				String containerId = contentService.getContainingCollectionId (attachment.getId());
				String accessUrl = contentService.getUrl(attachment.getId());
						
				AttachItem item = new AttachItem(attachment.getId(), displayName, containerId, accessUrl);
				item.setContentType(contentType);
				attached.add(item);
				state.setAttribute(STATE_HELPER_CHANGED, Boolean.TRUE.toString());
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis4"));
			}
			catch(OverQuotaException e)
			{
				addAlert(state, rb.getString("overquota"));
			}
			catch(ServerOverloadException e)
			{
				addAlert(state, rb.getString("failed"));
			}
			catch(Exception ignore)
			{
				// other exceptions should be caught earlier
			}
		}
		state.setAttribute(STATE_HELPER_NEW_ITEMS, attached);
	}
	
	/**
	 * Add a new URL to ContentHosting for each EditItem in the state attribute named STATE_CREATE_ITEMS.  
	 * The number of items to be added is indicated by the state attribute named STATE_CREATE_NUMBER, and
	 * the items are added to the collection identified by the state attribute named STATE_CREATE_COLLECTION_ID. 
	 * @param state
	 */
	protected static void createUrls(SessionState state) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_urls = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfItems = 1;
		numberOfItems = number.intValue();
		
		outerloop: for(int i = 0; i < numberOfItems; i++)
		{
			EditItem item = (EditItem) new_urls.get(i);
			if(item.isBlank())
			{
				continue;
			}
			
			ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
			resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, item.getName());							
			resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());

			resourceProperties.addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.FALSE.toString());
			List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
			saveMetadata(resourceProperties, metadataGroups, item);

			byte[] newUrl = item.getFilename().getBytes();
			String baseId = Validator.escapeResourceName(item.getName());
			int attemptNum = 0;
			String attemptStr = "";
			String newResourceId = collectionId + baseId + attemptStr;
		
			if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
			{
				alerts.add(rb.getString("toolong") + " " + newResourceId);
				continue outerloop;
			}
			
			boolean tryingToAddItem = true;

			while(tryingToAddItem)
			{
				try
				{
					ContentResource resource = ContentHostingService.addResource (newResourceId,
																				ResourceProperties.TYPE_URL,
																				newUrl,
																				resourceProperties, item.getNotification());
					tryingToAddItem = false;
					// ResourceProperties rp = resource.getProperties();
					item.setAdded(true);
					

					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// deal with pubview when in resource mode//%%%
						if (! item.isPubviewset())
						{
							ContentHostingService.setPubView(resource.getId(), item.isPubview());
						}
					}
					String mode = (String) state.getAttribute(STATE_MODE);
					if(MODE_HELPER.equals(mode))
					{
						attachItem(newResourceId, state);
					}
					
				}
				catch (IdUsedException e)
				{
					boolean foundUnusedId = false;
					for(int trial = 1;!foundUnusedId && trial < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS; trial++)
					{
						attemptNum++;
						attemptStr = "-" + Integer.toString(attemptNum);
					

						// add attempt number if there was one
						newResourceId = collectionId + baseId + attemptStr;
						
						if(newResourceId.length() > RESOURCE_ID_MAX_LENGTH)
						{
							alerts.add(rb.getString("toolong") + " " + newResourceId);
							continue outerloop;
						}
						
						try 
						{
							ContentHostingService.getResource(newResourceId);
						}
						catch (IdUnusedException ee)
						{
							foundUnusedId = true;
						}
						// TODO: should these be here? Or should these be caught outside the for-loop?
						catch (PermissionException ee)
						{
							// TODO: does this really mean it's an unused ID?
							foundUnusedId = true;
						}
						catch (TypeException ee)
						{
							// TODO: does this really mean it's an unused ID?
							foundUnusedId = true;
						}
					}
				}
				catch(PermissionException e)
				{
					alerts.add(rb.getString("notpermis13"));
					tryingToAddItem = false;
				}
				catch(IdInvalidException e)
				{
					alerts.add(rb.getString("title") + " " + e.getMessage ());
					tryingToAddItem = false;
				}
				catch (ServerOverloadException e)
				{
					alerts.add(rb.getString("failed"));
					tryingToAddItem = false;
				}
				catch(InconsistentException e)
				{
					alerts.add(RESOURCE_INVALID_TITLE_STRING);
					tryingToAddItem = false;
				}
				catch(OverQuotaException e)
				{
					alerts.add(rb.getString("overquota"));
					tryingToAddItem = false;
				}
			}
		}
		
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
		if(!currentMap.containsKey(collectionId))
		{
			try
			{
				currentMap.put (collectionId,ContentHostingService.getCollection (collectionId));
				state.setAttribute(EXPANDED_COLLECTIONS, currentMap);

				// add this folder id into the set to be event-observed
				addObservingPattern(collectionId, state);
			}
			catch (IdUnusedException ignore)
			{
			}
			catch (TypeException ignore)
			{
			}
			catch (PermissionException ignore)
			{
			} 
		}
		state.setAttribute(EXPANDED_COLLECTIONS, currentMap);

		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createUrls
	
	/**
	* Build the context for creating folders and items
	*/
	public static String buildCreateContext (VelocityPortlet portlet,
												Context context,
												RunData data,
												SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		
		context.put("TYPE_FOLDER", TYPE_FOLDER);
		context.put("TYPE_UPLOAD", TYPE_UPLOAD);
		context.put("TYPE_HTML", MIME_TYPE_DOCUMENT_HTML);
		context.put("TYPE_TEXT", MIME_TYPE_DOCUMENT_PLAINTEXT);
		context.put("TYPE_URL", TYPE_URL);
		context.put("TYPE_FORM", TYPE_FORM);
		
		context.put("max_upload_size", state.getAttribute(FILE_UPLOAD_MAX_SIZE));
		
		List new_items = (List) state.getAttribute(STATE_CREATE_ITEMS);
		context.put("new_items", new_items);
		Integer numberOfItems = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		context.put("numberOfItems", numberOfItems);
		context.put("max_number", new Integer(CREATE_MAX_ITEMS));
		String itemType = (String) state.getAttribute(STATE_CREATE_TYPE);
		context.put("itemType", itemType);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		context.put("collectionId", collectionId);
		String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		context.put("homeCollectionId", homeCollectionId);
		List collectionPath = getCollectionPath(state);	
		context.put ("collectionPath", collectionPath);

		if(homeCollectionId.equals(collectionId))
		{
			context.put("atHome", Boolean.TRUE.toString());
		}
			
		String show_form_items = (String) state.getAttribute(STATE_SHOW_FORM_ITEMS);
		if(show_form_items != null)
		{
			context.put("show_form_items", show_form_items);
		}

		// copyright
		copyrightChoicesIntoContext(state, context);

		// put schema for metadata into context
		metadataGroupsIntoContext(state, context);

		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		
		if(TYPE_FORM.equals(itemType))
		{
			List listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
			if(listOfHomes == null)
			{
				setupStructuredObjects(state);
				listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
			}
			context.put("homes", listOfHomes);
			
			String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
			context.put("formtype", formtype);
			
			String rootname = (String) state.getAttribute(STATE_STRUCTOBJ_ROOTNAME);
			context.put("rootname", rootname);
			
			context.put("STRING", ResourcesMetadata.WIDGET_STRING);
			context.put("TEXTAREA", ResourcesMetadata.WIDGET_TEXTAREA);
			context.put("BOOLEAN", ResourcesMetadata.WIDGET_BOOLEAN);
			context.put("INTEGER", ResourcesMetadata.WIDGET_INTEGER);
			context.put("DOUBLE", ResourcesMetadata.WIDGET_DOUBLE);
			context.put("DATE", ResourcesMetadata.WIDGET_DATE);
			context.put("TIME", ResourcesMetadata.WIDGET_TIME);
			context.put("DATETIME", ResourcesMetadata.WIDGET_DATETIME);
			context.put("ANYURI", ResourcesMetadata.WIDGET_ANYURI);
			context.put("ENUM", ResourcesMetadata.WIDGET_ENUM);
			context.put("NESTED", ResourcesMetadata.WIDGET_NESTED);
			
			context.put("today", TimeService.newTime());
			
			context.put("DOT", ResourcesMetadata.DOT);
		}
		Set missing = (Set) state.removeAttribute(STATE_CREATE_MISSING_ITEM);
		context.put("missing", missing);
		
		// String template = (String) getContext(data).get("template");
		return TEMPLATE_CREATE;

	}

	/**
	* show the resource properties
	*/
	public void doMore ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		// the hosted item ID
		String id = NULL_STRING;

		// from which page
		String from = NULL_STRING;

		// the collection id
		String collectionId = NULL_STRING;

		try
		{
			id = params.getString ("id");
			if (id!=null)
			{
				// set the collection/resource id for more context
				state.setAttribute (STATE_MORE_ID, id);
			}
			else
			{
				// get collection/resource id from the state object
				id =(String) state.getAttribute (STATE_MORE_ID);
			}

			// set the more from from attribute
			from = params.getString ("from");
			state.setAttribute (STATE_MORE_FROM, from);

			collectionId = params.getString ("collectionId");
			state.setAttribute(STATE_MORE_COLLECTION_ID, collectionId);

			if (collectionId.equals ((String) state.getAttribute (STATE_HOME_COLLECTION_ID)))
			{
				try
				{
					// this is a test to see if the collection exists.  If not, it is created.
					ContentCollection collection = ContentHostingService.getCollection (collectionId);
				}
				catch (IdUnusedException e )
				{
					try
					{
						// default copyright
						String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

						ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
						String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
						resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, ContentHostingService.getProperties (homeCollectionId).getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME));

						ContentCollection collection = ContentHostingService.addCollection (homeCollectionId, resourceProperties);
					}
					catch (IdUsedException ee)
					{
						addAlert(state, rb.getString("idused"));
					}
					catch (IdUnusedException ee)
					{
						addAlert(state,RESOURCE_NOT_EXIST_STRING);
					}
					catch (IdInvalidException ee)
					{
						addAlert(state, rb.getString("title") + " " + ee.getMessage ());
					}
					catch (PermissionException ee)
					{
						addAlert(state, rb.getString("permisex"));
					}
					catch (InconsistentException ee)
					{
						addAlert(state, RESOURCE_INVALID_TITLE_STRING);
					}
				}
				catch (TypeException e )
				{
					addAlert(state, rb.getString("typeex"));
				}
				catch (PermissionException e )
				{
					addAlert(state, rb.getString("permisex"));
				}
			}
		}
		catch (NullPointerException eE)
		{
			addAlert(state," " + rb.getString("nullex") + " " + id + ". ");
		}

		// is there no error?
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// go to the more state
			state.setAttribute (STATE_MODE, MODE_MORE);

			// reserving where it is from
			if (from.equals ("show"))
			{
				state.setAttribute (STATE_MORE_FROM, MODE_LIST);
			}
			// state.setAttribute (STATE_COLLECTION_ID, collectionId);

		}	// if-else

	}	// doMore

	/**
	* doDelete to delete the selected collection or resource items
	*/
	public void doDelete ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		ParameterParser params = data.getParameters ();
		
		List Items = (List) state.getAttribute(STATE_DELETE_ITEMS);

		// Vector deleteIds = (Vector) state.getAttribute (STATE_DELETE_IDS);

		// delete the lowest item in the hireachy first
		Hashtable deleteItems = new Hashtable();
		// String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		int maxDepth = 0;
		int depth = 0;
		
		Iterator it = Items.iterator();
		while(it.hasNext())
		{
			BrowseItem item = (BrowseItem) it.next();
			depth = ContentHostingService.getDepth(item.getId(), item.getRoot());
			if (depth > maxDepth)
			{
				maxDepth = depth;
			}
			List v = (List) deleteItems.get(new Integer(depth));
			if(v == null)
			{
				v = new Vector();
			}
			v.add(item);
			deleteItems.put(new Integer(depth), v);
		}
		
		boolean isCollection = false;
		for (int j=maxDepth; j>0; j--)
		{
			List v = (List) deleteItems.get(new Integer(j));
			if (v==null)
			{
				v = new Vector();
			}
			Iterator itemIt = v.iterator();
			while(itemIt.hasNext())
			{
				BrowseItem item = (BrowseItem) itemIt.next();
				try
				{
					if (item.isFolder())
					{
						ContentHostingService.removeCollection(item.getId());
					}						
					else
					{
						ContentHostingService.removeResource(item.getId());
					}
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis6") + " " + item.getName() + ". ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (TypeException e)
				{
					addAlert(state, rb.getString("deleteres") + " " + item.getName() + " " + rb.getString("wrongtype"));
				}	
				catch (ServerOverloadException e)
				{
					addAlert(state, rb.getString("failed"));
				}
				catch (InUseException e)
				{
					addAlert(state, rb.getString("deleteres") + " " + item.getName() + " " + rb.getString("locked"));
				}// try - catch
			}	// for

		}	// for
			
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// delete sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);

			if (((String) state.getAttribute (STATE_SELECT_ALL_FLAG)).equals (Boolean.TRUE.toString()))
			{
				state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
			}

		}	// if-else

	}	// doDelete

	/**
	* doCancel to return to the previous state
	*/
	public static void doCancel ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		String from = data.getParameters ().getString ("from");
		String mode = (String) state.getAttribute(STATE_MODE);
		String helper_mode = (String) state.getAttribute(STATE_RESOURCES_MODE);
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		if(MODE_HELPER.equals(mode) && MODE_ATTACHMENT_SELECT.equals(helper_mode))
		{
			cleanupState(state);
			state.removeAttribute(STATE_ATTACHMENTS);
			state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_DONE);
		}
		else if(MODE_HELPER.equals(mode) && MODE_ATTACHMENT_CREATE.equals(helper_mode))
		{
			state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
		}
		else if(MODE_HELPER.equals(mode))
		{
			state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
		}
		else if (from != null)
		{
			state.setAttribute (STATE_MODE, from);
		}
		else
		{
			state.setAttribute (STATE_MODE, MODE_LIST);
		}

	}	// doCancel

	/**
	* Paste the previously copied/cutted item(s)
	*/
	public void doHandlepaste ( RunData data)
	{
		ParameterParser params = data.getParameters ();

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// get the cut items to be pasted
		Vector pasteCutItems = (Vector) state.getAttribute (STATE_CUT_IDS);

		// get the copied items to be pasted
		Vector pasteCopiedItems = (Vector) state.getAttribute (STATE_COPIED_IDS);

		String collectionId = params.getString ("collectionId");
		String originalDisplayName = NULL_STRING;

		// handle cut and paste
		if (((String) state.getAttribute (STATE_CUT_FLAG)).equals (Boolean.TRUE.toString()))
		{
			for (int i = 0; i < pasteCutItems.size (); i++)
			{
				String currentPasteCutItem = (String) pasteCutItems.get (i);
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (currentPasteCutItem);

					originalDisplayName = properties.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
					/*
					if (Boolean.TRUE.toString().equals(properties.getProperty (ResourceProperties.PROP_IS_COLLECTION)))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
						}
					}
					else
					{
					*/
						// paste the resource
						ContentResource resource = ContentHostingService.getResource (currentPasteCutItem);
						ResourceProperties p = ContentHostingService.getProperties(currentPasteCutItem);
						String id = collectionId + Validator.escapeResourceName(p.getProperty(ResourceProperties.PROP_DISPLAY_NAME));

						// cut-paste to the same collection?
						boolean cutPasteSameCollection = false;
						String displayName = p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

						// till paste successfully or it fails
						ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
						// add the properties of the pasted item
						Iterator propertyNames = properties.getPropertyNames ();
						while ( propertyNames.hasNext ())
						{
							String propertyName = (String) propertyNames.next ();
							if (!properties.isLiveProperty (propertyName))
							{
								if (propertyName.equals (ResourceProperties.PROP_DISPLAY_NAME)&&(displayName.length ()>0))
								{
									resourceProperties.addProperty (propertyName, displayName);
								}
								else
								{
									resourceProperties.addProperty (propertyName, properties.getProperty (propertyName));
								}	// if-else
							}	// if
						}	// while

						try
						{
							// paste the cutted resource to the new collection - no notification
							ContentResource newResource = ContentHostingService.addResource (id, resource.getContentType (), resource.getContent (), resourceProperties, NotificationService.NOTI_NONE);
						}
						catch (InconsistentException e)
						{
							addAlert(state,RESOURCE_INVALID_TITLE_STRING);
						}
						catch (OverQuotaException e)
						{
							addAlert(state, rb.getString("overquota"));
						}
						catch (IdInvalidException e)
						{
							addAlert(state, rb.getString("title") + " " + e.getMessage ());
						}
						catch(ServerOverloadException e)
						{
							// this represents temporary unavailability of server's filesystem 
							// for server configured to save resource body in filesystem 
							addAlert(state, rb.getString("failed"));
						}
						catch (IdUsedException e)
						{	
							// cut and paste to the same collection; stop adding new resource
							if (id.equals(currentPasteCutItem))
							{
								cutPasteSameCollection = true;
							}
							else
							{
								addAlert(state, rb.getString("notaddreso") + " " + id + rb.getString("used2"));
								/*
								// pasted to the same folder as before; add "Copy of "/ "copy (n) of" to the id									
								if (countNumber==1)
								{
									displayName = DUPLICATE_STRING + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
									id = collectionId + Validator.escapeResourceName(displayName);
								}
								else
								{
									displayName = "Copy (" + countNumber + ") of " + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
									id = collectionId + Validator.escapeResourceName(displayName);
								}
								countNumber++;
								*/
							}
						}	// try-catch

						if (!cutPasteSameCollection)
						{
							// remove the cutted resource
							ContentHostingService.removeResource (currentPasteCutItem);
						}
							
					// }	// if-else
				}
				catch (InUseException e)
				{
					addAlert(state, rb.getString("someone") + " " + originalDisplayName + ". ");
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis7") + " " + originalDisplayName + ". ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (ServerOverloadException e)
				{
					addAlert(state, rb.getString("failed"));
				}
				catch (TypeException e)
				{
					addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
				}	// try-catch

			}	// for
		}	// cut

		// handling copy and paste
		if (Boolean.toString(true).equalsIgnoreCase((String) state.getAttribute (STATE_COPY_FLAG)))
		{
			for (int i = 0; i < pasteCopiedItems.size (); i++)
			{
				String currentPasteCopiedItem = (String) pasteCopiedItems.get (i);
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (currentPasteCopiedItem);
					originalDisplayName = properties.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
					// copy, cut and paste not operated on collections
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
						}
					}
					else
					{
						// paste the resource
						ContentResource resource = ContentHostingService.getResource (currentPasteCopiedItem);
						ResourceProperties p = ContentHostingService.getProperties(currentPasteCopiedItem);
						String displayName = DUPLICATE_STRING + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
						String id = collectionId + Validator.escapeResourceName(displayName);

						ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();

						// add the properties of the pasted item
						Iterator propertyNames = properties.getPropertyNames ();
						while ( propertyNames.hasNext ())
						{
							String propertyName = (String) propertyNames.next ();
							if (!properties.isLiveProperty (propertyName))
							{
								if (propertyName.equals (ResourceProperties.PROP_DISPLAY_NAME)&&(displayName.length ()>0))
								{
									resourceProperties.addProperty (propertyName, displayName);
								}
								else
								{
									resourceProperties.addProperty (propertyName, properties.getProperty (propertyName));
								}
							}
						}
						try
						{
							// paste the copied resource to the new collection
							ContentResource newResource = ContentHostingService.addResource (id, resource.getContentType (), resource.getContent (), resourceProperties, NotificationService.NOTI_NONE);
						}
						catch (InconsistentException e)
						{
							addAlert(state,RESOURCE_INVALID_TITLE_STRING);
						}
						catch (IdInvalidException e)
						{
							addAlert(state,rb.getString("title") + " " + e.getMessage ());
						}
						catch (OverQuotaException e)
						{
							addAlert(state, rb.getString("overquota"));
						}
						catch (ServerOverloadException e)
						{
							addAlert(state, rb.getString("failed"));
						}
						catch (IdUsedException e)
						{
							addAlert(state, rb.getString("notaddreso") + " " + id + rb.getString("used2"));
							/*
							// copying
							// pasted to the same folder as before; add "Copy of " to the id
							if (countNumber > 1)
							{
								displayName = "Copy (" + countNumber + ") of " + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);				
							}
							else if (countNumber == 1)
							{
								displayName = "Copy of " + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);													
							}
							id = collectionId + Validator.escapeResourceName(displayName);
							countNumber++;
							*/
						}	// try-catch

					}	// if-else
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis8") + " " + originalDisplayName + ". ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (TypeException e)
				{
					addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
				}	// try-catch

			}	// for
		}	// copy

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// delete sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			
			// reset the cut flag
			if (((String)state.getAttribute (STATE_CUT_FLAG)).equals (Boolean.TRUE.toString()))
			{
				state.setAttribute (STATE_CUT_FLAG, Boolean.FALSE.toString());
			}
			
			// reset the copy flag
			if (Boolean.toString(true).equalsIgnoreCase((String)state.getAttribute (STATE_COPY_FLAG)))
			{
				state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());
			}
			
			// try to expand the collection
			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			if(! expandedCollections.containsKey(collectionId))
			{
				org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
				try
				{
					ContentCollection coll = contentService.getCollection(collectionId);
					expandedCollections.put(collectionId, coll);
				}
				catch(Exception ignore){}
			}			
		}

	}	// doHandlepaste

	/**
	* Paste the shortcut(s) of previously copied item(s)
	*/
	public void doHandlepasteshortcut ( RunData data)
	{
		ParameterParser params = data.getParameters ();

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// get the items to be pasted
		Vector pasteItems = new Vector ();

		if (((String) state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
		{
			pasteItems = (Vector) ( (Vector) state.getAttribute (STATE_COPIED_IDS)).clone ();
		}
		if (((String) state.getAttribute (STATE_CUT_FLAG)).equals (Boolean.TRUE.toString()))
		{
			addAlert(state, rb.getString("choosecp"));
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			String collectionId = params.getString ("collectionId");
	
			String originalDisplayName = NULL_STRING;
	
			for (int i = 0; i < pasteItems.size (); i++)
			{
				String currentPasteItem = (String) pasteItems.get (i);
	
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (currentPasteItem);
	
					originalDisplayName = properties.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
	
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						// paste the collection
					}
					else
					{
						// paste the resource
						ContentResource resource = ContentHostingService.getResource (currentPasteItem);
						ResourceProperties p = ContentHostingService.getProperties(currentPasteItem);
						String displayName = SHORTCUT_STRING + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
						String id = collectionId + Validator.escapeResourceName(displayName);
	
						//int countNumber = 2;
						ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
						// add the properties of the pasted item
						Iterator propertyNames = properties.getPropertyNames ();
						while ( propertyNames.hasNext ())
						{
							String propertyName = (String) propertyNames.next ();
							if ((!properties.isLiveProperty (propertyName)) && (!propertyName.equals (ResourceProperties.PROP_DISPLAY_NAME)))
							{
								resourceProperties.addProperty (propertyName, properties.getProperty (propertyName));
							}
						}
						// %%%%% should be _blank for items that can be displayed in browser, _self for others
						// resourceProperties.addProperty (ResourceProperties.PROP_OPEN_NEWWINDOW, "_self");
						resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, displayName);

						try
						{
							ContentResource referedResource= ContentHostingService.getResource (currentPasteItem);
							ContentResource newResource = ContentHostingService.addResource (id, ResourceProperties.TYPE_URL, referedResource.getUrl ().getBytes (), resourceProperties, NotificationService.NOTI_NONE);
						}
						catch (InconsistentException e)
						{
							addAlert(state, RESOURCE_INVALID_TITLE_STRING);
						}
						catch (OverQuotaException e)
						{
							addAlert(state, rb.getString("overquota"));
						}
						catch (IdInvalidException e)
						{
							addAlert(state, rb.getString("title") + " " + e.getMessage ());
						}
						catch (ServerOverloadException e)
						{
							addAlert(state, rb.getString("failed"));
						}
						catch (IdUsedException e)
						{
							addAlert(state, rb.getString("notaddreso") + " " + id + rb.getString("used2"));
							/*
							 // pasted shortcut to the same folder as before; add countNumber to the id
							displayName = "Shortcut (" + countNumber + ") to " + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
							id = collectionId + Validator.escapeResourceName(displayName);
							countNumber++;
							*/
						}	// try-catch
					}	// if-else
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis9") + " " +  currentPasteItem.substring (currentPasteItem.lastIndexOf (Entity.SEPARATOR)+1) + ". ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (TypeException e)
				{
					addAlert(state, rb.getString("pasteitem") + " " +  currentPasteItem.substring (currentPasteItem.lastIndexOf (Entity.SEPARATOR)+1) + " " + rb.getString("mismatch"));
				}	// try-catch
	
			}	// for
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			if (((String) state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
			{
				// reset the copy flag
				state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());
			}
			
			// paste shortcut sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
		}

	}	// doHandlepasteshortcut
	
	/**
	* Edit the editable collection/resource properties
	*/
	public static void doEdit ( RunData data )
	{
		ParameterParser params = data.getParameters ();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.removeAttribute(STATE_CREATE_ITEMS);
		state.removeAttribute(STATE_EDIT_ITEM);
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		String id = NULL_STRING;
		id = params.getString ("id");
		if(id == null || id.length() == 0)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile2"));
			return;
		}
		
		state.setAttribute (STATE_EDIT_ID, id);

		String collectionId = (String) params.getString("collectionId");
		state.setAttribute(STATE_EDIT_COLLECTION_ID, collectionId);

		//Resource resource = null;
		

		// populate an EditItem object with values from the resource
		// then put the EditItem in state
		try
		{
			ResourceProperties properties = ContentHostingService.getProperties(id); 

			boolean isCollection = false;
			try
			{
				isCollection = properties.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
			}
			catch(EmptyException e)
			{
				// assume isCollection is false if property is not set
			}

			String itemType = "";
			byte[] content = null;
			if(isCollection)
			{
				itemType = "folder";
			}
			else
			{
				ContentResource resource = ContentHostingService.getResource(id);
				itemType = resource.getContentType();
				content = resource.getContent();
			}
				
			String itemName = properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

			EditItem item = new EditItem(id, itemName, itemType);
			
			if(content != null)
			{
				item.setContent(content);
			}
			
			String dummyId = collectionId.trim();
			if(dummyId.endsWith(Entity.SEPARATOR))
			{
				dummyId += "dummy";
			}
			else
			{
				dummyId += Entity.SEPARATOR + "dummy";
			}
			
			String containerId = ContentHostingService.getContainingCollectionId (id);
			item.setContainer(containerId);
			
			boolean canRead = ContentHostingService.allowGetCollection(id);
			boolean canAddFolder = ContentHostingService.allowAddCollection(id);
			boolean canAddItem = ContentHostingService.allowAddResource(id);
			boolean canDelete = ContentHostingService.allowRemoveResource(id);
			boolean canRevise = ContentHostingService.allowUpdateResource(id);
			item.setCanRead(canRead);
			item.setCanRevise(canRevise);
			item.setCanAddItem(canAddItem);
			item.setCanAddFolder(canAddFolder);
			item.setCanDelete(canDelete);
			// item.setIsUrl(isUrl);
			
			if(item.isUrl())
			{
				String url = new String(content);
				item.setFilename(url);
			}
			else if(item.isStructuredArtifact())
			{
				String formtype = properties.getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);
				state.setAttribute(STATE_STRUCTOBJ_TYPE, formtype);
				state.setAttribute(STATE_EDIT_ITEM, item);
				setupStructuredObjects(state);
				Document doc = Xml.readDocumentFromString(new String(content));
				Element root = doc.getDocumentElement();
				importStructuredArtifact(root, item.getForm());
				List flatList = item.getForm().getFlatList();
				item.setProperties(flatList);
			}
			else if(item.isHtml() || item.isPlaintext() || item.isFileUpload())
			{
				String filename = properties.getProperty(ResourceProperties.PROP_ORIGINAL_FILENAME);
				if(filename == null)
				{
					// this is a hack to deal with the fact that original filenames were not saved for some time.
					if(containerId != null && item.getId().startsWith(containerId) && containerId.length() < item.getId().length())
					{
						filename = item.getId().substring(containerId.length());
					}
				}
				
				if(filename == null)
				{
					item.setFilename(itemName);
				}
				else
				{
					item.setFilename(filename);
				}
			}
			
			String description = properties.getProperty(ResourceProperties.PROP_DESCRIPTION);
			item.setDescription(description);
			
			try
			{
				Time creTime = properties.getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				String createdTime = creTime.toStringLocalShortDate() + " " + creTime.toStringLocalShort();
				item.setCreatedTime(createdTime);
			}
			catch(Exception e)
			{
				String createdTime = properties.getProperty(ResourceProperties.PROP_CREATION_DATE);
				item.setCreatedTime(createdTime);
			}
			try
			{
				String createdBy = properties.getUserProperty(ResourceProperties.PROP_CREATOR).getDisplayName();
				item.setCreatedBy(createdBy);
			}
			catch(Exception e)
			{
				String createdBy = properties.getProperty(ResourceProperties.PROP_CREATOR);
				item.setCreatedBy(createdBy);
			}
			try
			{
				Time modTime = properties.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
				String modifiedTime = modTime.toStringLocalShortDate() + " " + modTime.toStringLocalShort();
				item.setModifiedTime(modifiedTime);
			}
			catch(Exception e)
			{
				String modifiedTime = properties.getProperty(ResourceProperties.PROP_MODIFIED_DATE);
				item.setModifiedTime(modifiedTime);
			}
			try
			{
				String modifiedBy = properties.getUserProperty(ResourceProperties.PROP_MODIFIED_BY).getDisplayName();
				item.setModifiedBy(modifiedBy);
			}
			catch(Exception e)
			{
				String modifiedBy = properties.getProperty(ResourceProperties.PROP_MODIFIED_BY);
				item.setModifiedBy(modifiedBy);
			}
			
			String url = ContentHostingService.getUrl(id);
			item.setUrl(url);
	
			String size = "";
			if(properties.getProperty(ResourceProperties.PROP_CONTENT_LENGTH) != null)
			{
				size = properties.getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH) + " (" + Validator.getFileSizeWithDividor(properties.getProperty(ResourceProperties.PROP_CONTENT_LENGTH)) +" bytes)";
			}
			item.setSize(size);
			
			String copyrightStatus = properties.getProperty(properties.getNamePropCopyrightChoice());
			item.setCopyrightStatus(copyrightStatus);
			String copyrightInfo = properties.getPropertyFormatted(properties.getNamePropCopyright());
			item.setCopyrightInfo(copyrightInfo);
			String copyrightAlert = properties.getProperty(properties.getNamePropCopyrightAlert());

			if("true".equalsIgnoreCase(copyrightAlert))
			{
				item.setCopyrightAlert(true);
			}
			else
			{
				item.setCopyrightAlert(false);
			}
			
			boolean pubviewset = ContentHostingService.isInheritingPubView(containerId) || ContentHostingService.isPubView(containerId);
			item.setPubviewset(pubviewset);
			boolean pubview = pubviewset;
			if (!pubview)
			{
				pubview = ContentHostingService.isPubView(id);
			}
			item.setPubview(pubview);
			
			Map metadata = new Hashtable();
			List groups = (List) state.getAttribute(STATE_METADATA_GROUPS);
			if(groups != null && ! groups.isEmpty())
			{
				Iterator it = groups.iterator();
				while(it.hasNext())
				{
					MetadataGroup group = (MetadataGroup) it.next();
					Iterator propIt = group.iterator();
					while(propIt.hasNext())
					{
						ResourcesMetadata prop = (ResourcesMetadata) propIt.next();
						String name = prop.getFullname();
						String widget = prop.getWidget();
						if(widget.equals(ResourcesMetadata.WIDGET_DATE) || widget.equals(ResourcesMetadata.WIDGET_DATETIME) || widget.equals(ResourcesMetadata.WIDGET_TIME))
						{
							Time time = TimeService.newTime();
							try
							{
								time = properties.getTimeProperty(name);
							}
							catch(EmptyException ignore)
							{
								// use "now" as default in that case
							}
							catch(TypeException ignore)
							{
								// use "now" as default
							}
							metadata.put(name, time);
						}
						else
						{
							String value = properties.getPropertyFormatted(name);
							metadata.put(name, value);
						}
					}
				}
				item.setMetadata(metadata);				
			}
			else
			{
				item.setMetadata(new Hashtable());
			}
			// for collections only
			if(item.isFolder())
			{
				// setup for quota - ADMIN only, collection only
				if (SecurityService.isSuperUser())
				{
					
					item.setCanSetQuota(true);
					try
					{
						long quota = properties.getLongProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
						item.setHasQuota(true);
						item.setQuota(Long.toString(quota));
					}
					catch (Exception any)
					{
					}
				}
			}

			state.setAttribute(STATE_EDIT_ITEM, item);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, RESOURCE_NOT_EXIST_STRING);
		}
		catch (PermissionException e)
		{
			addAlert(state, rb.getString("notpermis2") + " " + id + ". " );
		}
		catch(TypeException e)
		{
			addAlert(state," " + rb.getString("typeex") + " "  + id);
		}
		catch(ServerOverloadException e)
		{
			// this represents temporary unavailability of server's filesystem 
			// for server configured to save resource body in filesystem 
			addAlert(state, rb.getString("failed"));
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// got resource and sucessfully populated item with values
			state.setAttribute (STATE_MODE, MODE_EDIT);
			state.setAttribute(STATE_EDIT_ALERTS, new HashSet());
			
		}
		
	}	// doEdit

	/**
	 * This method updates the session state with information needed to create or modify
	 * structured artifacts in the resources tool.  Among other things, it obtains a list 
	 * of "forms" available to the user and places that list in state indexed as 
	 * "STATE_STRUCTOBJ_HOMES".  If the current formtype is known (in state indexed as
	 * "STATE_STRUCTOBJ_TYPE"), the list of properties associated with that form type is
	 * generated.  If we are in a "create" context, the properties are added to each of 
	 * the items in the list of items indexed as "STATE_CREATE_ITEMS".  If we are in an 
	 * "edit" context, the properties are added to the current item being edited (a state
	 * attribute indexed as "STATE_EDIT_ITEM").  The metaobj SchemaBean associated with
	 * the current form and its root SchemaNode object are also placed in state for later 
	 * reference.
	 */
	public static void setupStructuredObjects(SessionState state)
	{
		String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
		
		HomeFactory factory = (HomeFactory) ComponentManager.get("homeFactory");	
		
		Map homes = factory.getHomes(StructuredArtifactHomeInterface.class);
		List listOfHomes = new Vector();
		Iterator it = homes.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			try
			{
				Object obj = homes.get(key);
				listOfHomes.add(obj);
			}
			catch(Exception ignore)
			{}
		}
		state.setAttribute(STATE_STRUCTOBJ_HOMES, listOfHomes);
		
		StructuredArtifactHomeInterface home = null;
		SchemaBean rootSchema = null;
		ResourcesMetadata elements = null;
		
		if(formtype == null || formtype.equals(""))
		{
			formtype = "";
			state.setAttribute(STATE_STRUCTOBJ_TYPE, formtype);
		}
		else if(listOfHomes.isEmpty())
		{
			// hmmm
		}
		else
		{
			home = (StructuredArtifactHomeInterface) factory.getHome(formtype);
		}
		
		if(home != null)
		{
			rootSchema = new SchemaBean(home.getRootNode(), home.getSchema(), formtype, home.getType().getDescription());
			List fields = rootSchema.getFields();
			String docRoot = rootSchema.getFieldName();
			elements = new ResourcesMetadata("", docRoot, "", "", ResourcesMetadata.WIDGET_NESTED, ResourcesMetadata.WIDGET_NESTED);
			elements.setDottedparts(docRoot);
			elements.setContainer(null);
			elements = createHierarchicalList(elements, fields, 1);
			
			state.setAttribute(STATE_STRUCTOBJ_ROOTNAME, docRoot);
			state.setAttribute(STATE_STRUCT_OBJ_SCHEMA, rootSchema);
			
			if(state.getAttribute(STATE_CREATE_ITEMS) != null)
			{
				List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
				Integer numberOfItems = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
				List flatList = elements.getFlatList();
				
				for(int i = 0; i < numberOfItems.intValue(); i++)
				{
					EditItem item = (EditItem) items.get(i);
					item.setRootname(docRoot);
					item.setFormtype(formtype);
					item.setProperties(flatList);
					item.setForm(elements);
				}
				
				state.setAttribute(STATE_CREATE_ITEMS, items);
			}
			else if(state.getAttribute(STATE_EDIT_ITEM) != null)
			{
				EditItem item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);
				item.setRootname(docRoot);
				item.setFormtype(formtype);
				item.setForm(elements);
			}
		}
		
	}	// setupStructuredArtifacts
	
	/**
	 * This method navigates through a list of SchemaNode objects representing fields in a form, 
	 * creates a ResourcesMetadata object for each field and adds those as nested fields within
	 * a root element.  If a field contains nested fields, a recursive call adds nested fields
	 * in the corresponding ResourcesMetadata object. 
	 * @param element The root element to which field descriptions are added.
	 * @param fields A list of metaobj SchemaNode objects.
	 * @param depth The depth of nesting, corresponding to the amount of indent that will be used
	 * when displaying the list.
	 * @return The update root element.
	 */
	private static ResourcesMetadata createHierarchicalList(ResourcesMetadata element, List fields, int depth) 
	{
		List properties = new Vector();
		for(Iterator fieldIt = fields.iterator(); fieldIt.hasNext(); )
		{
			SchemaBean field = (SchemaBean) fieldIt.next();
			SchemaNode node = field.getSchema();
			Map annotations = field.getAnnotations();
			Pattern pattern = null;
			String localname = field.getFieldName();
			String description = field.getDescription();
			String label = (String) annotations.get("label");
			if(label == null || label.trim().equals(""))
			{
				label = description;
			}
			
			Class javaclass = node.getObjectType();
			String typename = javaclass.getName();
			String widget = ResourcesMetadata.WIDGET_STRING;
			int length =  0;
			List enumerals = null;
			
			if(field.getFields().size() > 0)
			{
				widget = ResourcesMetadata.WIDGET_NESTED;
			}
			else if(node.hasEnumerations())
			{
				enumerals = node.getEnumeration();
				typename = String.class.getName();
				widget = ResourcesMetadata.WIDGET_ENUM;
			}
			else if(typename.equals(String.class.getName()))
			{
				length = node.getType().getMaxLength();
				if(length > 100 || length < 1)
				{
					widget = ResourcesMetadata.WIDGET_TEXTAREA;
				}
				else if(length > 50)
				{
					length = 50;
				}
				
				pattern = node.getType().getPattern();
			}
			else if(typename.equals(Date.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_DATE;
			}
			else if(typename.equals(Boolean.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_BOOLEAN;
			}
			else if(typename.equals(URI.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_ANYURI;
			}
			else if(typename.equals(Number.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_INTEGER;
				
				//length = node.getType().getTotalDigits();
				length = INTEGER_WIDGET_LENGTH;
			}
			else if(typename.equals(Double.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_DOUBLE;
				length = DOUBLE_WIDGET_LENGTH;
			}
			int minCard = node.getMinOccurs();
			int maxCard = node.getMaxOccurs();
			if(maxCard < 1)
			{
				maxCard = Integer.MAX_VALUE;
			}
			if(minCard < 0)
			{
				minCard = 0;
			}
			minCard = java.lang.Math.max(0,minCard);
			maxCard = java.lang.Math.max(1,maxCard);
			int currentCount = java.lang.Math.min(java.lang.Math.max(1,minCard),maxCard);
			
			ResourcesMetadata prop = new ResourcesMetadata(element.getDottedname(), localname, label, description, typename, widget);
			List parts = new Vector(element.getDottedparts());
			parts.add(localname);
			prop.setDottedparts(parts);
			prop.setContainer(element);
			if(ResourcesMetadata.WIDGET_NESTED.equals(widget))
			{
				prop = createHierarchicalList(prop, field.getFields(), depth + 1);
			}
			prop.setMinCardinality(minCard);
			prop.setMaxCardinality(maxCard);
			prop.setCurrentCount(currentCount);
			prop.setDepth(depth);
			
			if(enumerals != null)
			{
				prop.setEnumeration(enumerals);
			}
			if(length > 0)
			{
				prop.setLength(length);
			}
			
			if(pattern != null)
			{
				prop.setPattern(pattern);
			}
			
			properties.add(prop);
		}

		element.setNested(properties);
		
		return element;
		
	}	// createHierarchicalList

	/**
	 * This method captures property values from an org.w3c.dom.Document and inserts them
	 * into a hierarchical list of ResourcesMetadata objects which describes the structure
	 * of the form.  The values are added by inserting nested instances into the properties.
	 * 
	 * @param element	An org.w3c.dom.Element containing values to be imported.
	 * @param properties	A hierarchical list of ResourcesMetadata objects describing a form 
	 */
	public static void importStructuredArtifact(Node node, ResourcesMetadata property)
	{
		if(property == null || node == null)
		{
			return;
		}
		
		String tagname = property.getLocalname();
		String nodename = node.getLocalName();
		if(! tagname.equals(nodename))
		{
			// return;
		}
		
		if(property.getNested().size() == 0)
		{
			boolean value_found = false;
			Node child = node.getFirstChild();
			while(! value_found && child != null)
			{
				if(child.getNodeType() == Node.TEXT_NODE)
				{
					Text value = (Text) child;
					if(ResourcesMetadata.WIDGET_DATE.equals(property.getWidget()) || ResourcesMetadata.WIDGET_DATETIME.equals(property.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(property.getWidget()))
					{
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Time time = TimeService.newTime();
						try 
						{
							Date date = df.parse(value.getData());
							time = TimeService.newTime(date.getTime());
						} 
						catch(Exception ignore)
						{
							// use "now" as default in that case
						}
						property.setValue(0, time);
					}
					else
					{
						property.setValue(0, value.getData());
					}
				}
				child = child.getNextSibling();
			}
		}
		else if(node instanceof Element)
		{
			// a nested element
			Iterator nestedIt = property.getNested().iterator();
			while(nestedIt.hasNext())
			{
				ResourcesMetadata prop = (ResourcesMetadata) nestedIt.next();
				NodeList nodes = ((Element) node).getElementsByTagName(prop.getLocalname());
				if(nodes == null)
				{
					continue;
				}
				for(int i = 0; i < nodes.getLength(); i++)
				{
					Node n = nodes.item(i);
					if(n != null)
					{
						ResourcesMetadata instance = prop.addInstance();
						if(instance != null)
						{
							importStructuredArtifact(n, instance);
						}
					}
				}
			}
		}

	}	// importStructuredArtifact
	
	/**
	* Edit the editable collection/resource properties
	*/
	public void doProperties ( RunData data)
	{
		ParameterParser params = data.getParameters ();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		String id = NULL_STRING;
		String from = params.getString ("from");
		if ((from!=null)&&(from.equals ("revise")))
		{
			String[] reviseIds = data.getParameters ().getStrings ("selectedMembers");
			if (reviseIds == null)
			{
				// there is no resource selected, show the alert message to the user
				addAlert(state, rb.getString("choosefile2"));
			}
			else if (reviseIds.length > 1 )
			{
				// there is more than one resource selected, show the alert message to the user
				addAlert(state, rb.getString("chooseonly"));
			}
			else
			{
				id = reviseIds[0];
				state.setAttribute (STATE_FROM, MODE_LIST);
			}
		}
		else
		{
			id = params.getString ("id");
			state.setAttribute (STATE_FROM, MODE_LIST);
		}

		if (id.length ()>0)
		{
			state.setAttribute (STATE_PROPERTIES_ID, id);

			String collectionId = (String) params.getString("collectionId");
			state.setAttribute(STATE_PROPERTIES_COLLECTION_ID, collectionId);

			ResourceProperties properties = null;

			// populate the property list
			try
			{
				properties = ContentHostingService.getProperties (id);
				
				String copyright = properties.getPropertyFormatted(properties.getNamePropCopyright());
				if (! copyright.equals((String) state.getAttribute(STATE_MY_COPYRIGHT)))
				{
					state.setAttribute(STATE_PROPERTIES_COPYRIGHT, copyright);
				}
				
				state.setAttribute(STATE_PROPERTIES_COPYRIGHT_CHOICE, properties.getProperty(properties.getNamePropCopyrightChoice()));
				state.setAttribute(STATE_PROPERTIES_COPYRIGHT_ALERT, properties.getProperty(properties.getNamePropCopyrightAlert()));	
			}
			catch (IdUnusedException e)
			{
				addAlert(state, RESOURCE_NOT_EXIST_STRING);
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis2") + " " + id + ". " );
			}
		}	// if

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// find properties sucessful
			state.setAttribute (STATE_MODE, MODE_PROPERTIES);
		}

	}	// doProperties

	/**
	* Modify the properties
	*/
	public void doModifyproperties ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String flow = params.getString("flow");
		if(flow == null || "cancel".equals(flow))
		{
			doCancel(data);
			return;
		}
		
		int noti = NotificationService.NOTI_OPTIONAL;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// set noti to none if in dropbox mode
			noti = NotificationService.NOTI_NONE;
		}
		else
		{
			// read the notification options
			String notification = params.getString("notify");
			if ("r".equals(notification))
			{
				noti = NotificationService.NOTI_REQUIRED;
			}
			else if ("n".equals(notification))
			{
				noti = NotificationService.NOTI_NONE;
			}
		}

		// read the quota fields
		String setQuota = params.getString("setQuota");
		String hasQuota = params.getString("hasQuota");
		String quota = params.getString("quota");

		String id = (String) state.getAttribute (STATE_PROPERTIES_ID);
		state.setAttribute (STATE_PROPERTIES_ID, id);

		String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

		String collectionId = (String) state.getAttribute (STATE_PROPERTIES_COLLECTION_ID);

		// the unqualify fields
		String u_fields = "";
		
		String filename = "";
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{			
			boolean changing_intent = false;
			boolean copyrightRequired = true;
			boolean isFolder = false;

			// populate the property list
			try
			{
				// get an edit
				ContentCollectionEdit cedit = null;
				ContentResourceEdit redit = null;
				ResourcePropertiesEdit pedit = null;
				
				try
				{
					isFolder = ContentHostingService.getProperties(id).getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
				}
				catch(EmptyException e)
				{
					// assume false if unable to retrieve value of PROP_IS_COLLECTION
				}
				if (isFolder)
				{
					cedit = ContentHostingService.editCollection(id);
					pedit = cedit.getPropertiesEdit();
					copyrightRequired = false;
				}
				else
				{
					redit = ContentHostingService.editResource(id);
					pedit = redit.getPropertiesEdit();
				}
				
				if(!isFolder)
				{
					// for the resource type of URL or plain text, update the content also
					String type = pedit.getProperty (ResourceProperties.PROP_CONTENT_TYPE);
					
					if ((type!=null) && ((type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_PLAINTEXT)) || (type.equalsIgnoreCase (MIME_TYPE_DOCUMENT_HTML))))
					{
						// choose between revising file and replacing it
						String intent = params.getString("intent");
						String oldintent = (String) state.getAttribute(STATE_PROPERTIES_INTENT);
						if(intent == null)
						{
							intent = INTENT_REVISE_FILE;
							state.setAttribute(STATE_PROPERTIES_INTENT, intent);

						}
						if(!intent.equals(oldintent))
						{
							changing_intent = true;
						}
					}	// if

					if(!changing_intent)
					{
						String content = params.getCleanString ("content");
						if(content != null)
						{
							redit.setContentType(type);
							try
							{
								redit.setContent(content.getBytes("UTF-8"));
							}
							catch (UnsupportedEncodingException e)
							{
								redit.setContent(content.getBytes());
								e.printStackTrace();
							}
						}

						// check for file replacement
						FileItem fileitem = params.getFileItem("fileName");
						if(fileitem != null)
						{
							byte[] bytes = fileitem.get();
							String contenttype = fileitem.getContentType();
							filename = Validator.getFileName(fileitem.getFileName());
							
							redit.setContentType(contenttype);
							redit.setContent(bytes);
						}
					}
					
					if(type.equalsIgnoreCase (ResourceProperties.TYPE_URL))
					{
						copyrightRequired = false;

						String url = params.getString("Url").trim();
						if (url.equals (NULL_STRING))
						{
							// ignore the empty url field
						}
						else if (url.indexOf ("://") == -1)
						{
							// if it's missing the transport, add http://
							url = "http://" + url;
						}

						if(!url.equals(NULL_STRING))
						{
							// valid protocol?
							try
							{
								// test to see if the input validates as a URL. 
								// Checks string for format only.
								URL u = new URL(url);
								redit.setContent(url.getBytes());
							}
							catch (MalformedURLException e1)
							{
								try
								{
									Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
									Matcher matcher = pattern.matcher(url);
									if(matcher.matches())
									{
										// if URL has "unknown" protocol, check remaider with
										// "http" protocol and accept input it that validates.
										URL test = new URL("http://" + matcher.group(2));
										redit.setContent(url.getBytes());
									}
									else
									{
										// invalid url
										addAlert(state, rb.getString("validurl"));
									}
								}
								catch (MalformedURLException e2)
								{
									// invalid url
									addAlert(state, rb.getString("validurl"));
								}
							}
						}
					}
				}
				
				boolean pubviewset = false;
				boolean pubview = false;
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					pubviewset = ContentHostingService.isInheritingPubView(id);
					if (!pubviewset)
					{
						pubview = params.getBoolean("pubview");
					}
				}
				
				if (!(isFolder && (id.equals ((String) state.getAttribute (STATE_HOME_COLLECTION_ID)))))
				{
					String displayName = (params.getString (ResourceProperties.PROP_DISPLAY_NAME)).trim();
					if(displayName.length() == 0 && filename.length() != 0)
					{
						displayName = Validator.getFileName(filename); 
					}
					
					if (displayName.length ()==0)
					{
						// title field is null
						addAlert(state, rb.getString("titlenotnull"));
						u_fields = u_fields + ResourceProperties.PROP_DISPLAY_NAME;
						pedit.addProperty (ResourceProperties.PROP_DISPLAY_NAME, "");
					}
					else
					{
						pedit.addProperty (ResourceProperties.PROP_DISPLAY_NAME, displayName);
					}
				}	// the home collection's title is not modificable

				if (copyrightRequired)
				{
					String copyright = StringUtil.trimToNull(params.getString ("copyright"));
					String newcopyright = StringUtil.trimToNull(params.getCleanString (NEW_COPYRIGHT));
					String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert"));
					if (copyright != null)
					{
						if (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)))
						{
							if (newcopyright != null)
							{
								pedit.addProperty (ResourceProperties.PROP_COPYRIGHT, newcopyright);
							}
							else
							{
								addAlert(state, rb.getString("specifycp2"));
							}
						}
						else if (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyright.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
						{
							pedit.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
						}

						pedit.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, copyright);
					}
					
					if (copyrightAlert != null)
					{
						pedit.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, copyrightAlert);
					}
					else
					{
						pedit.removeProperty (ResourceProperties.PROP_COPYRIGHT_ALERT);
					}
				}

				String description = params.getCleanString (ResourceProperties.PROP_DESCRIPTION);
				if ((description.length ()>0) && (!description.equals (pedit.getProperty (ResourceProperties.PROP_DESCRIPTION))))
				{
					pedit.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
				}
				
				// deal with quota (collections only)
				if ((cedit != null) && (setQuota != null))
				{
					if (hasQuota != null)
					{
						// set the quota
						pedit.addProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA, quota);
					}

					else
					{
						// clear the quota
						pedit.removeProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
					}
				}

				List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
				if(metadataGroups != null && !metadataGroups.isEmpty())
				{
					MetadataGroup group = null;
					Iterator it = metadataGroups.iterator();
					while(it.hasNext())
					{
						group = (MetadataGroup) it.next();
						if(group.isShowing())
						{
							Iterator props = group.iterator();
							while(props.hasNext())
							{
								ResourcesMetadata prop = (ResourcesMetadata) props.next();
								String value = params.getString(prop.getFullname());
								if(value != null)
								{
									pedit.addProperty(prop.getFullname(), value);
								}
						
							}
						}
					}
				}

				// commit the change
				if (cedit != null)
				{
					ContentHostingService.commitCollection(cedit);
					
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// when in resource mode
						if (!pubviewset)
						{
							ContentHostingService.setPubView(cedit.getId(), pubview);
						}
					}
				}
				else
				{
					ContentHostingService.commitResource(redit, noti);
					
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// when in resource mode
						if (!pubviewset)
						{
							ContentHostingService.setPubView(redit.getId(), pubview);
						}
					}
				}
				
			}
			catch (TypeException e)
			{
				addAlert(state," " + rb.getString("typeex") + " "  + id);
			}
			catch (IdUnusedException e)
			{
				addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis10") + " " + id + ". " );
			}
			catch (InUseException e)
			{
				addAlert(state, rb.getString("someone") + " " + id + ". ");
			}
			catch (ServerOverloadException e)
			{
				addAlert(state, rb.getString("failed"));
			}
			catch (OverQuotaException e)
			{
				addAlert(state, rb.getString("changing1") + " " + id + " " + rb.getString("changing2"));
			}
		}	// if - else

		if (state.getAttribute(STATE_MESSAGE) != null)
		{
			// modify properties not sucessful
			// set the unqualified fields
			Vector v = (Vector) emptyVector (1).clone ();
			v.set (1, u_fields);
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, v);
		}
		else
		{
			// modify properties sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			// clear the unqualified fields
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, (Vector) emptyVector (1).clone ());
			// clear state variables
			initPropertiesContext(state);
		}	//if-else

	}	// doModifyproperties
	
	protected static String validateURL(String url) throws MalformedURLException
	{
		if (url.equals (NULL_STRING))
		{
			// ignore the empty url field
		}
		else if (url.indexOf ("://") == -1)
		{
			// if it's missing the transport, add http://
			url = "http://" + url;
		}

		if(!url.equals(NULL_STRING))
		{
			// valid protocol?
			try
			{
				// test to see if the input validates as a URL. 
				// Checks string for format only.
				URL u = new URL(url);
			}
			catch (MalformedURLException e1)
			{
				try
				{
					Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
					Matcher matcher = pattern.matcher(url);
					if(matcher.matches())
					{
						// if URL has "unknown" protocol, check remaider with
						// "http" protocol and accept input it that validates.
						URL test = new URL("http://" + matcher.group(2));
					}
					else
					{
						throw e1;
					}
				}
				catch (MalformedURLException e2)
				{
					throw e1;
				}
			}
		}
		return url;
	}
	
	/**
	 * Retrieve values for an item from edit context.  Edit context contains just one item at a time of a known type 
	 * (folder, file, text document, structured-artifact, etc).  This method retrieves the data apppropriate to the 
	 * type and updates the values of the EditItem stored as the STATE_EDIT_ITEM attribute in state. 
	 * @param state
	 * @param params
	 * @param item
	 */
	protected void captureValues(SessionState state, ParameterParser params)
	{

		EditItem item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);
		Set alerts = (Set) state.getAttribute(STATE_EDIT_ALERTS);
		if(alerts == null)
		{
			alerts = new HashSet();
		}
		String intent = params.getString("intent");
		String oldintent = (String) state.getAttribute(STATE_EDIT_INTENT);
		boolean upload_file = item.isFileUpload() || ((item.isHtml() || item.isPlaintext()) && INTENT_REPLACE_FILE.equals(intent) && INTENT_REPLACE_FILE.equals(oldintent));
		boolean revise_file = (item.isHtml() || item.isPlaintext()) && INTENT_REVISE_FILE.equals(intent) && INTENT_REVISE_FILE.equals(oldintent);
		
		String name = params.getString("name");
		if(name == null || "".equals(name.trim()))
		{
			alerts.add(rb.getString("titlenotnull"));
			// addAlert(state, rb.getString("titlenotnull"));
		}
		else
		{
			item.setName(name.trim());
		}
		
		String description = params.getString("description");
		if(description == null)
		{
			item.setDescription("");
		}
		else
		{
			item.setDescription(description);
		}
		
		item.setContentHasChanged(false);
		
		if(upload_file)
		{
			String max_file_size_mb = (String) state.getAttribute(FILE_UPLOAD_MAX_SIZE);
			int max_bytes = 1096 * 1096;
			try
			{
				max_bytes = Integer.parseInt(max_file_size_mb) * 1096 * 1096;
			}
			catch(Exception e)
			{
				// if unable to parse an integer from the value 
				// in the properties file, use 1 MB as a default
				max_file_size_mb = "1";
				max_bytes = 1096 * 1096;
			}
			/*
			 // params.getContentLength() returns m_req.getContentLength()
			if(params.getContentLength() >= max_bytes)
			{
				alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
			}
			else
			*/
			{
				// check for file replacement
				FileItem fileitem = params.getFileItem("fileName");
				if(fileitem == null)
				{
					// "The user submitted a file to upload but it was too big!"
					alerts.clear();
					alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
					//item.setMissing("fileName");
				}
				else if (fileitem.getFileName() == null || fileitem.getFileName().length() == 0)
				{
					if(item.getContent() == null || item.getContent().length <= 0)
					{
						// "The user submitted the form, but didn't select a file to upload!"
						alerts.add(rb.getString("choosefile") + ". ");
						//item.setMissing("fileName");
					}
				}
				else if (fileitem.getFileName().length() > 0)
				{
					String filename = Validator.getFileName(fileitem.getFileName());
					byte[] bytes = fileitem.get();
					String contenttype = fileitem.getContentType();
					
					if(bytes.length >= max_bytes)
					{
						alerts.clear();
						alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
						// item.setMissing("fileName");					
					}
					else if(bytes.length > 0)
					{
						item.setContent(bytes);
						item.setContentHasChanged(true);
						item.setMimeType(contenttype);
						item.setFilename(filename);									
					}
				}
			}
		}
		else if(revise_file)
		{
			// check for input from editor (textarea)
			String content = params.getString("content");
			if(content != null)
			{
				item.setContent(content);
				item.setContentHasChanged(true);
			}
		}
		else if(item.isUrl())
		{
			String url = params.getString("Url");
			if(url == null || url.trim().equals(""))
			{
				item.setFilename("");
				alerts.add(rb.getString("validurl"));
			}
			else
			{
				// valid protocol?
				item.setFilename(url);
				try
				{
					// test format of input
					URL u = new URL(url);
				}
				catch (MalformedURLException e1)
				{
					try
					{
						// if URL did not validate, check whether the problem was an 
						// unrecognized protocol, and accept input if that's the case.
						Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
						Matcher matcher = pattern.matcher(url);
						if(matcher.matches())
						{
							URL test = new URL("http://" + matcher.group(2));
						}
						else
						{
							url = "http://" + url;
							URL test = new URL(url);
							item.setFilename(url);					
						}
					}
					catch (MalformedURLException e2)
					{
						// invalid url
						alerts.add(rb.getString("validurl"));
					}
				}
			}
		}
		else if(item.isFolder())
		{
			if(item.canSetQuota())
			{
				// read the quota fields
				String setQuota = params.getString("setQuota");
				boolean hasQuota = params.getBoolean("hasQuota");
				item.setHasQuota(hasQuota);
				if(hasQuota)
				{
					int q = params.getInt("quota");
					item.setQuota(Integer.toString(q));
				}
			}
		}
		else if(item.isStructuredArtifact())
		{
			String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
			String formtype_check = params.getString("formtype");
			
			if(formtype_check == null || formtype_check.equals(""))
			{
				alerts.add(rb.getString("type"));
				item.setMissing("formtype");
			}
			else if(formtype_check.equals(formtype))
			{
				item.setFormtype(formtype);
				capturePropertyValues(params, item, item.getProperties());
			}
		}
		
		if(item.isFileUpload() || item.isHtml() || item.isPlaintext())
		{
			// check for copyright status
			// check for copyright info
			// check for copyright alert
			
			String copyrightStatus = StringUtil.trimToNull(params.getString ("copyrightStatus"));
			String copyrightInfo = StringUtil.trimToNull(params.getCleanString ("copyrightInfo"));
			String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert"));

			if (copyrightStatus != null)
			{
				if (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyrightStatus.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)))
				{
					if (copyrightInfo != null)
					{
						item.setCopyrightInfo( copyrightInfo );
					}
					else
					{
						alerts.add(rb.getString("specifycp2"));
						// addAlert(state, rb.getString("specifycp2"));
					}
				}
				else if (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyrightStatus.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
				{
					item.setCopyrightInfo((String) state.getAttribute (STATE_MY_COPYRIGHT));
				}

				item.setCopyrightStatus( copyrightStatus );
			}
			item.setCopyrightAlert(copyrightAlert != null);
		}

		boolean pubviewset = item.isPubviewset();
		boolean pubview = false;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			if (!pubviewset)
			{
				pubview = params.getBoolean("pubview");
				item.setPubview(pubview);
			}
		}
		
		int noti = NotificationService.NOTI_OPTIONAL;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// set noti to none if in dropbox mode
			noti = NotificationService.NOTI_NONE;
		}
		else
		{
			// read the notification options
			String notification = params.getString("notify");
			if ("r".equals(notification))
			{
				noti = NotificationService.NOTI_REQUIRED;
			}
			else if ("n".equals(notification))
			{
				noti = NotificationService.NOTI_NONE;
			}
		}
		item.setNotification(noti);

		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups != null && ! metadataGroups.isEmpty())
		{
			Iterator groupIt = metadataGroups.iterator();
			while(groupIt.hasNext())
			{
				MetadataGroup group = (MetadataGroup) groupIt.next();
				if(group.isShowing())
				{
					Iterator propIt = group.iterator();
					while(propIt.hasNext())
					{
						ResourcesMetadata prop = (ResourcesMetadata) propIt.next();
						String propname = prop.getFullname();
						if(ResourcesMetadata.WIDGET_DATE.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_DATETIME.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(prop.getWidget()))
						{
							int year = 0;
							int month = 0;
							int day = 0;
							int hour = 0;
							int minute = 0;
							int second = 0;
							int millisecond = 0;
							String ampm = "";
							
							if(prop.getWidget().equals(ResourcesMetadata.WIDGET_DATE) || 
								prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
							{
								year = params.getInt(propname + "_year", year);
								month = params.getInt(propname + "_month", month);
								day = params.getInt(propname + "_day", day);


							}
							if(prop.getWidget().equals(ResourcesMetadata.WIDGET_TIME) || 
								prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
							{
								hour = params.getInt(propname + "_hour", hour);
								minute = params.getInt(propname + "_minute", minute);
								second = params.getInt(propname + "_second", second);
								millisecond = params.getInt(propname + "_millisecond", millisecond);
								ampm = params.getString(propname + "_ampm").trim();

								if("pm".equalsIgnoreCase("ampm"))
								{
									if(hour < 12)
									{
										hour += 12;
									}
								}
								else if(hour == 12)
								{
									hour = 0;
								}
							}
							if(hour > 23)
							{
								hour = hour % 24;
								day++;
							}
							
							Time value = TimeService.newTimeLocal(year, month, day, hour, minute, second, millisecond);
							item.setMetadataItem(propname,value);

						}
						else
						{
							
							String value = params.getString(propname);
							if(value != null)
							{
								item.setMetadataItem(propname, value);
							}
						}
					}
				}
			}
		}
		state.setAttribute(STATE_EDIT_ITEM, item);
		state.setAttribute(STATE_EDIT_ALERTS, alerts);

	}	// captureValues
	
	/**
	 * Retrieve from an html form all the values needed to create a new resource
	 * @param item The EditItem object in which the values are temporarily stored.
	 * @param index The index of the item (used as a suffix in the name of the form element)
	 * @param state
	 * @param params
	 * @param markMissing Indicates whether to mark required elements if they are missing.
	 * @return
	 */
	public static Set captureValues(EditItem item, int index, SessionState state, ParameterParser params, boolean markMissing)
	{
		Set item_alerts = new HashSet();
		boolean blank_entry = true;
		item.clearMissing();

		String name = params.getString("name" + index);
		if(name == null || name.trim().equals(""))
		{
			if(markMissing)
			{
				item_alerts.add(rb.getString("titlenotnull"));
				item.setMissing("name");
			}
			item.setName("");
			// addAlert(state, rb.getString("titlenotnull"));
		}
		else
		{
			item.setName(name);
			blank_entry = false;
		}
		
		String description = params.getString("description" + index);
		if(description == null || description.trim().equals(""))
		{
			item.setDescription("");
		}
		else
		{
			item.setDescription(description);
			blank_entry = false;
		}
		
		item.setContentHasChanged(false);
		
		if(item.isFileUpload())
		{
			String max_file_size_mb = (String) state.getAttribute(FILE_UPLOAD_MAX_SIZE);
			int max_bytes = 1096 * 1096;
			try
			{
				max_bytes = Integer.parseInt(max_file_size_mb) * 1096 * 1096;
			}
			catch(Exception e)
			{
				// if unable to parse an integer from the value 
				// in the properties file, use 1 MB as a default
				max_file_size_mb = "1";
				max_bytes = 1096 * 1096;
			}
			/*
			 // params.getContentLength() returns m_req.getContentLength()
			if(params.getContentLength() >= max_bytes)
			{
				item_alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
			}
			else
			*/
			{
				// check for file replacement
				FileItem fileitem = null;
				try
				{
					fileitem = params.getFileItem("fileName" + index);
				}
				catch(Exception e)
				{
					// this is an error in Firefox, Mozilla and Netscape
					// "The user didn't select a file to upload!"
					if(item.getContent() == null || item.getContent().length <= 0)
					{
						item_alerts.add(rb.getString("choosefile") + " " + (index + 1) + ". ");
						item.setMissing("fileName");
					}
				}
				if(fileitem == null)
				{
					// "The user submitted a file to upload but it was too big!"
					item_alerts.clear();
					item_alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
					item.setMissing("fileName");
				}
				else if (fileitem.getFileName() == null || fileitem.getFileName().length() == 0)
				{
					if(item.getContent() == null || item.getContent().length <= 0)
					{
						// "The user submitted the form, but didn't select a file to upload!"
						item_alerts.add(rb.getString("choosefile") + " " + (index + 1) + ". ");
						item.setMissing("fileName");
					}
				}
				else if (fileitem.getFileName().length() > 0)
				{
					String filename = Validator.getFileName(fileitem.getFileName());
					byte[] bytes = fileitem.get();
					String contenttype = fileitem.getContentType();
					
					if(bytes.length >= max_bytes)
					{
						item_alerts.clear();
						item_alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
						item.setMissing("fileName");					
					}
					else if(bytes.length > 0)
					{
						item.setContent(bytes);
						item.setContentHasChanged(true);
						item.setMimeType(contenttype);
						item.setFilename(filename);									
						blank_entry = false;
					}
					else 
					{
						item_alerts.add(rb.getString("choosefile") + " " + (index + 1) + ". ");
						item.setMissing("fileName");
					}
				}

			}
		}
		else if(item.isPlaintext())
		{
			// check for input from editor (textarea)
			String content = params.getString("content" + index);
			if(content != null)
			{
				item.setContentHasChanged(true);
				item.setContent(content);
				blank_entry = false;
			}
			item.setMimeType(MIME_TYPE_DOCUMENT_PLAINTEXT);
		}
		else if(item.isHtml())
		{
			// check for input from editor (textarea)
			String content = params.getCleanString("content" + index);
			StringBuffer alertMsg = new StringBuffer();
			content = FormattedText.processHtmlDocument(content, alertMsg);
			if (alertMsg.length() > 0)
			{
				item_alerts.add(alertMsg.toString());
			}
			if(content != null && !content.equals(""))
			{
				item.setContent(content);
				item.setContentHasChanged(true);
				blank_entry = false;
			}
			item.setMimeType(MIME_TYPE_DOCUMENT_HTML);
		}
		else if(item.isUrl())
		{
			String url = params.getString("Url" + index);
			if(url == null || url.trim().equals(""))
			{
				item.setFilename("");
				item_alerts.add(rb.getString("specifyurl"));
				item.setMissing("Url");
			}
			else
			{
				item.setFilename(url);
				blank_entry = false;
				// is protocol supplied and, if so, is it recognized?
				try
				{
					// check format of input
					URL u = new URL(url);
				}
				catch (MalformedURLException e1)
				{
					try
					{
						// if URL did not validate, check whether the problem was an 
						// unrecognized protocol, and accept input if that's the case.
						Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
						Matcher matcher = pattern.matcher(url);
						if(matcher.matches())
						{
							URL test = new URL("http://" + matcher.group(2));
						}
						else
						{
							url = "http://" + url;
							URL test = new URL(url);
							item.setFilename(url);					
						}
					}
					catch (MalformedURLException e2)
					{
						// invalid url
						item_alerts.add(rb.getString("validurl"));
						item.setMissing("Url");
					}
				}
			}
		}
		else if(item.isStructuredArtifact())
		{
			String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
			String formtype_check = params.getString("formtype");
			
			if(formtype_check == null || formtype_check.equals(""))
			{
				item_alerts.add("Must select a form type");
				item.setMissing("formtype");
			}
			else if(formtype_check.equals(formtype))
			{
				item.setFormtype(formtype);
				capturePropertyValues(params, item, item.getProperties());
				// blank_entry = false;
			}
			item.setMimeType(MIME_TYPE_STRUCTOBJ);

		}
		if(item.isFileUpload() || item.isHtml() || item.isPlaintext())
		{
			// check for copyright status
			// check for copyright info
			// check for copyright alert
			
			String copyrightStatus = StringUtil.trimToNull(params.getString ("copyright" + index));
			String copyrightInfo = StringUtil.trimToNull(params.getCleanString ("newcopyright" + index));
			String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert" + index));

			if (copyrightStatus != null)
			{
				if (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyrightStatus.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)))
				{
					if (copyrightInfo != null)
					{
						item.setCopyrightInfo( copyrightInfo );
					}
					else
					{
						item_alerts.add(rb.getString("specifycp2"));
						// addAlert(state, rb.getString("specifycp2"));
					}
				}
				else if (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyrightStatus.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
				{
					item.setCopyrightInfo((String) state.getAttribute (STATE_MY_COPYRIGHT));
				}

				item.setCopyrightStatus( copyrightStatus );
			}
			item.setCopyrightAlert(copyrightAlert != null);

		}

		boolean pubviewset = item.isPubviewset();
		boolean pubview = false;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			if (!pubviewset)
			{
				pubview = params.getBoolean("pubview" + index);
				item.setPubview(pubview);
			}
		}
		
		int noti = NotificationService.NOTI_OPTIONAL;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// set noti to none if in dropbox mode
			noti = NotificationService.NOTI_NONE;
		}
		else
		{
			// read the notification options
			String notification = params.getString("notify" + index);
			if ("r".equals(notification))
			{
				noti = NotificationService.NOTI_REQUIRED;
			}
			else if ("n".equals(notification))
			{
				noti = NotificationService.NOTI_NONE;
			}
		}
		item.setNotification(noti);
		
		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups != null && ! metadataGroups.isEmpty())
		{
			Iterator groupIt = metadataGroups.iterator();
			while(groupIt.hasNext())
			{
				MetadataGroup group = (MetadataGroup) groupIt.next();
				if(item.isGroupShowing(group.getName()))
				{
					Iterator propIt = group.iterator();
					while(propIt.hasNext())
					{
						ResourcesMetadata prop = (ResourcesMetadata) propIt.next();
						String propname = prop.getFullname();
						if(ResourcesMetadata.WIDGET_DATE.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_DATETIME.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(prop.getWidget()))
						{
							int year = 0;
							int month = 0;
							int day = 0;
							int hour = 0;
							int minute = 0;
							int second = 0;
							int millisecond = 0;
							String ampm = "";
							
							if(prop.getWidget().equals(ResourcesMetadata.WIDGET_DATE) || 
								prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
							{
								year = params.getInt(propname + "_" + index + "_year", year);
								month = params.getInt(propname + "_" + index + "_month", month);
								day = params.getInt(propname + "_" + index + "_day", day);
							}
							if(prop.getWidget().equals(ResourcesMetadata.WIDGET_TIME) || 
								prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
							{
								hour = params.getInt(propname + "_" + index + "_hour", hour);
								minute = params.getInt(propname + "_" + index + "_minute", minute);
								second = params.getInt(propname + "_" + index + "_second", second);
								millisecond = params.getInt(propname + "_" + index + "_millisecond", millisecond);
								ampm = params.getString(propname + "_" + index + "_ampm").trim();

								if("pm".equalsIgnoreCase(ampm))
								{
									if(hour < 12)
									{
										hour += 12;
									}
								}
								else if(hour == 12)
								{
									hour = 0;
								}
							}
							if(hour > 23)
							{
								hour = hour % 24;
								day++;
							}
							
							Time value = TimeService.newTimeLocal(year, month, day, hour, minute, second, millisecond);
							item.setMetadataItem(propname,value);

						}
						else
						{
							String value = params.getString(propname + "_" + index);
							if(value != null)
							{
								item.setMetadataItem(propname, value);
							}
						}
					}
				}
			}
		}
		item.markAsBlank(blank_entry);
		
		return item_alerts;
		
	}
	
	/**
	 * Retrieve values for one or more items from create context.  Create context contains up to ten items at a time  
	 * all of the same type (folder, file, text document, structured-artifact, etc).  This method retrieves the data 
	 * apppropriate to the type and updates the values of the EditItem objects stored as the STATE_CREATE_ITEMS 
	 * attribute in state. If the third parameter is "true", missing/incorrect user inputs will generate error messages
	 * and attach flags to the input elements.  
	 * @param state
	 * @param params
	 * @param markMissing Should this method generate error messages and add flags for missing/incorrect user inputs?
	 */
	protected static void captureMultipleValues(SessionState state, ParameterParser params, boolean markMissing)
	{
		Integer numberOfItems = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		int actualCount = 0;
		Set first_item_alerts = null;
		
		String max_file_size_mb = (String) state.getAttribute(FILE_UPLOAD_MAX_SIZE);
		int max_bytes = 1096 * 1096;
		try
		{
			max_bytes = Integer.parseInt(max_file_size_mb) * 1096 * 1096;
		}
		catch(Exception e)
		{
			// if unable to parse an integer from the value 
			// in the properties file, use 1 MB as a default
			max_file_size_mb = "1";
			max_bytes = 1096 * 1096;
		}

		/*
		// params.getContentLength() returns m_req.getContentLength()
		if(params.getContentLength() > max_bytes)
		{
			alerts.add(rb.getString("size") + " " + max_file_size_mb + "MB " + rb.getString("exceeded2"));
			state.setAttribute(STATE_CREATE_ALERTS, alerts);
			
			return;
		}
		*/
		for(int i = 0; i < numberOfItems.intValue(); i++)
		{
			EditItem item = (EditItem) items.get(i);
			Set item_alerts = captureValues(item, i, state, params, markMissing);
			if(i == 0)
			{
				first_item_alerts = item_alerts;
			}
			else if(item.isBlank())
			{
				item.clearMissing();
			}
			if(! item.isBlank())
			{
				alerts.addAll(item_alerts);
				actualCount ++;
			}
		}
		if(actualCount > 0)
		{
			EditItem item = (EditItem) items.get(0);
			if(item.isBlank())
			{
				item.clearMissing();
			}
		}
		else if(markMissing)
		{
			alerts.addAll(first_item_alerts);
		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		state.setAttribute(STATE_CREATE_ACTUAL_COUNT, Integer.toString(actualCount));

	}	// captureMultipleValues
	
	
	
	protected static void capturePropertyValues(ParameterParser params, EditItem item, List properties)
	{
		// use the item's properties if they're not supplied
		if(properties == null)
		{
			properties = item.getProperties();
		}
		// if max cardinality > 1, value is a list (Iterate over members of list)
		// else value is an object, not a list
		
		// if type is nested, object is a Map (iterate over name-value pairs for the properties of the nested object) 
		// else object is type to store value, usually a string or a date/time
		
		Iterator it = properties.iterator();
		while(it.hasNext())
		{
			ResourcesMetadata prop = (ResourcesMetadata) it.next();
			String propname = prop.getDottedname();

			if(ResourcesMetadata.WIDGET_NESTED.equals(prop.getWidget()))
			{
				// do nothing
			}
			else if(ResourcesMetadata.WIDGET_BOOLEAN.equals(prop.getWidget()))
			{
				String value = params.getString(propname);
				if(value == null || Boolean.FALSE.toString().equals(value))
				{
					prop.setValue(0, Boolean.FALSE.toString());
				}
				else
				{
					prop.setValue(0, Boolean.TRUE.toString());
				}
			}
			else if(ResourcesMetadata.WIDGET_DATE.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_DATETIME.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(prop.getWidget()))
			{
				int year = 0;
				int month = 0;
				int day = 0;
				int hour = 0;
				int minute = 0;
				int second = 0;
				int millisecond = 0;
				String ampm = "";
				
				if(prop.getWidget().equals(ResourcesMetadata.WIDGET_DATE) || 
					prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
				{
					year = params.getInt(propname + "_year", year);
					month = params.getInt(propname + "_month", month);
					day = params.getInt(propname + "_day", day);
				}
				if(prop.getWidget().equals(ResourcesMetadata.WIDGET_TIME) || 
					prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
				{
					hour = params.getInt(propname + "_hour", hour);
					minute = params.getInt(propname + "_minute", minute);
					second = params.getInt(propname + "_second", second);
					millisecond = params.getInt(propname + "_millisecond", millisecond);
					ampm = params.getString(propname + "_ampm");

					if("pm".equalsIgnoreCase(ampm))
					{
						if(hour < 12)
						{
							hour += 12;
						}
					}
					else if(hour == 12)
					{
						hour = 0;
					}
				}
				if(hour > 23)
				{
					hour = hour % 24;
					day++;
				}
				
				Time value = TimeService.newTimeLocal(year, month, day, hour, minute, second, millisecond);
				prop.setValue(0, value);
			}
			else
			{
				String value = params.getString(propname);
				if(value != null)
				{
					prop.setValue(0, value);
				}
			}
		}
		
	}	// capturePropertyValues
	
	/**
	* Modify the properties
	*/
	public void doSavechanges ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String flow = params.getString("flow").trim();

		if(flow == null || "cancel".equals(flow))
		{
			doCancel(data);
			return;
		}
		
		// get values from form and update STATE_EDIT_ITEM attribute in state
		captureValues(state, params);

		EditItem item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);

		if(flow.equals("showMetadata"))
		{
			doShow_metadata(data);
			return;
		}
		else if(flow.equals("hideMetadata"))
		{
			doHide_metadata(data);
			return;
		}
		else if(flow.equals("intentChanged"))
		{
			doToggle_intent(data);
			return;
		}
		else if(flow.equals("addInstance"))
		{
			String field = params.getString("field");
			addInstance(field, item.getProperties());
			ResourcesMetadata form = item.getForm();
			List flatList = form.getFlatList();
			item.setProperties(flatList);
			return;
		}
		

		Set alerts = (Set) state.getAttribute(STATE_EDIT_ALERTS);

		if(item.isStructuredArtifact())
		{
			SchemaBean bean = (SchemaBean) state.getAttribute(STATE_STRUCT_OBJ_SCHEMA);
			SaveArtifactAttempt attempt = new SaveArtifactAttempt(item, bean.getSchema());
			validateStructuredArtifact(attempt);
	
			Iterator errorIt = attempt.getErrors().iterator();
			while(errorIt.hasNext())
			{
				ValidationError error = (ValidationError) errorIt.next();
				alerts.add(error.getDefaultMessage());
			}
		}
		
		if(alerts.isEmpty())
		{
			// populate the property list
			try
			{
				// get an edit
				ContentCollectionEdit cedit = null;
				ContentResourceEdit redit = null;
				ResourcePropertiesEdit pedit = null;
				
				if(item.isFolder())
				{
					cedit = ContentHostingService.editCollection(item.getId());
					pedit = cedit.getPropertiesEdit();
				}
				else
				{
					redit = ContentHostingService.editResource(item.getId());
					pedit = redit.getPropertiesEdit();
				}
				if(!item.isFolder())
				{
					if(item.isUrl())
					{
						redit.setContent(item.getFilename().getBytes());						
					}
					else if(item.isStructuredArtifact())
					{
						redit.setContentType(item.getMimeType());
						redit.setContent(item.getContent());
					}
					else if(item.contentHasChanged())
					{
						redit.setContentType(item.getMimeType());
						redit.setContent(item.getContent());
					}
					
					String copyright = StringUtil.trimToNull(params.getString ("copyright"));
					String newcopyright = StringUtil.trimToNull(params.getCleanString (NEW_COPYRIGHT));
					String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert"));
					if (copyright != null)
					{
						if (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)))
						{
							if (newcopyright != null)
							{
								pedit.addProperty (ResourceProperties.PROP_COPYRIGHT, newcopyright);
							}
							else
							{
								alerts.add(rb.getString("specifycp2"));
								// addAlert(state, rb.getString("specifycp2"));
							}
						}
						else if (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyright.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
						{
							String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);
							pedit.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
						}

						pedit.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, copyright);
					}
					
					if (copyrightAlert != null)
					{
						pedit.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, copyrightAlert);
					}
					else
					{
						pedit.removeProperty (ResourceProperties.PROP_COPYRIGHT_ALERT);
					}
				}
				
				if (!(item.isFolder() && (item.getId().equals ((String) state.getAttribute (STATE_HOME_COLLECTION_ID)))))
				{
					pedit.addProperty (ResourceProperties.PROP_DISPLAY_NAME, item.getName());
				}	// the home collection's title is not modificable

				pedit.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());
				// deal with quota (collections only)
				if ((cedit != null) && item.canSetQuota())
				{
					if (item.hasQuota())
					{
						// set the quota
						pedit.addProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA, item.getQuota());
					}
					else
					{
						// clear the quota
						pedit.removeProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
					}
				}
				
				List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
				
				state.setAttribute(STATE_EDIT_ALERTS, alerts);
				saveMetadata(pedit, metadataGroups, item);
				alerts = (Set) state.getAttribute(STATE_EDIT_ALERTS);

				// commit the change
				if (cedit != null)
				{
					ContentHostingService.commitCollection(cedit);
				}
				else
				{
					ContentHostingService.commitResource(redit, item.getNotification());
				}
				
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					// when in resource mode
					if (!item.isPubviewset())
					{
						ContentHostingService.setPubView(item.getId(), item.isPubview());
					}
				}
				
				// need to refresh collection containing current edit item make changes show up 
				String containerId = ContentHostingService.getContainingCollectionId(item.getId());
				ContentCollection container = ContentHostingService.getCollection(containerId);
				Map expandedCollections = (Map) state.getAttribute(EXPANDED_COLLECTIONS);
				expandedCollections.remove(containerId);
				expandedCollections.put(containerId, container);
			}
			catch (TypeException e)
			{
				alerts.add(rb.getString("typeex") + " "  + item.getId());
				// addAlert(state," " + rb.getString("typeex") + " "  + item.getId());
			}
			catch (IdUnusedException e)
			{
				alerts.add(RESOURCE_NOT_EXIST_STRING);
				// addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}
			catch (PermissionException e)
			{
				alerts.add(rb.getString("notpermis10") + " " + item.getId());
				// addAlert(state, rb.getString("notpermis10") + " " + item.getId() + ". " );
			}
			catch (InUseException e)
			{
				alerts.add(rb.getString("someone") + " " + item.getId());
				// addAlert(state, rb.getString("someone") + " " + item.getId() + ". ");
			}
			catch (ServerOverloadException e)
			{
				alerts.add(rb.getString("failed"));
			}
			catch (OverQuotaException e)
			{
				alerts.add(rb.getString("changing1") + " " + item.getId() + " " + rb.getString("changing2"));
				// addAlert(state, rb.getString("changing1") + " " + item.getId() + " " + rb.getString("changing2"));
			}
		}	// if - else

		if(alerts.isEmpty())
		{
			// modify properties sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			// clear state variables
			initPropertiesContext(state);
		}	//if-else
		else
		{
			Iterator alertIt = alerts.iterator();
			while(alertIt.hasNext())
			{
				String alert = (String) alertIt.next();
				addAlert(state, alert);
			}
			alerts.clear();
			state.setAttribute(STATE_EDIT_ALERTS, alerts);
			// state.setAttribute(STATE_CREATE_MISSING_ITEM, missing);
		}

	}	// doSavechanges
	
	/**
	 * @param pedit
	 * @param metadataGroups
	 * @param metadata
	 */
	private static void saveMetadata(ResourcePropertiesEdit pedit, List metadataGroups, EditItem item) 
	{
		if(metadataGroups != null && !metadataGroups.isEmpty())
		{
			MetadataGroup group = null;
			Iterator it = metadataGroups.iterator();
			while(it.hasNext())
			{
				group = (MetadataGroup) it.next();
				Iterator props = group.iterator();
				while(props.hasNext())
				{
					ResourcesMetadata prop = (ResourcesMetadata) props.next();
					
					if(ResourcesMetadata.WIDGET_DATETIME.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_DATE.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(prop.getWidget()))
					{
						Time val = (Time)item.getMetadata().get(prop.getFullname());
						if(val != null)
						{
							pedit.addProperty(prop.getFullname(), val.toString());							
						}
					}
					else
					{
						String val = (String) item.getMetadata().get(prop.getFullname());
						pedit.addProperty(prop.getFullname(), val);
					}
				}
			}
		}
		
	}

	/**
	 * @param data
	 */
	protected void doToggle_intent(RunData data) 
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		String intent = params.getString("intent");
		state.setAttribute(STATE_EDIT_INTENT, intent);
		
	}	// doToggle_intent
	
	/**
	 * @param data
	 */
	public static void doHideOtherSites(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		state.setAttribute(STATE_SHOW_OTHER_SITES, Boolean.FALSE.toString());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = params.getStrings("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);

	}

	
	/**
	 * @param data
	 */
	public static void doShowOtherSites(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = params.getStrings("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);

		state.setAttribute(STATE_SHOW_OTHER_SITES, Boolean.TRUE.toString());
	}

	/**
	 * @param data
	 */
	public static void doHide_metadata(RunData data)
	{
		ParameterParser params = data.getParameters ();
		String name = params.getString("group");
		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups != null && ! metadataGroups.isEmpty())
		{
			boolean found = false;
			MetadataGroup group = null;
			Iterator it = metadataGroups.iterator();
			while(!found && it.hasNext())
			{
				group = (MetadataGroup) it.next();
				found = (name.equals(Validator.escapeUrl(group.getName())) || name.equals(group.getName()));
			}
			if(found)
			{
				group.setShowing(false);
			}
		}

	}	// doHide_metadata

	/**
	 * @param data
	 */
	public void doShow_metadata(RunData data)
	{
		ParameterParser params = data.getParameters ();
		String name = params.getString("group");
		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups != null && ! metadataGroups.isEmpty())
		{
			boolean found = false;
			MetadataGroup group = null;
			Iterator it = metadataGroups.iterator();
			while(!found && it.hasNext())
			{
				group = (MetadataGroup) it.next();
				found = (name.equals(Validator.escapeUrl(group.getName())) || name.equals(group.getName()));
			}
			if(found)
			{
				group.setShowing(true);
			}
		}
		
	}	// doShow_metadata

	/**
	* Sort based on the given property
	*/
	public static void doSort ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = data.getParameters ().getStrings ("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);
		
		String criteria = params.getString ("criteria");

		if (criteria.equals ("title"))
		{
			criteria = ResourceProperties.PROP_DISPLAY_NAME;
		}
		else if (criteria.equals ("size"))
		{
			criteria = ResourceProperties.PROP_CONTENT_LENGTH;
		}
		else if (criteria.equals ("created by"))
		{
			criteria = ResourceProperties.PROP_CREATOR;
		}
		else if (criteria.equals ("last modified"))
		{
			criteria = ResourceProperties.PROP_MODIFIED_DATE;
		}

		// current sorting sequence
		String asc = NULL_STRING;
		if (!criteria.equals (state.getAttribute (STATE_SORT_BY)))
		{
			state.setAttribute (STATE_SORT_BY, criteria);
			asc = Boolean.TRUE.toString();
			state.setAttribute (STATE_SORT_ASC, asc);
		}
		else
		{
			// current sorting sequence
			asc = (String) state.getAttribute (STATE_SORT_ASC);

			//toggle between the ascending and descending sequence
			if (asc.equals (Boolean.TRUE.toString()))
			{
				asc = Boolean.FALSE.toString();
			}
			else
			{
				asc = Boolean.TRUE.toString();
			}
			state.setAttribute (STATE_SORT_ASC, asc);
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// sort sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);

		}	// if-else

	}	// doSort

	/**
	* set the state name to be "deletecofirm" if any item has been selected for deleting
	*/
	public void doDeleteconfirm ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		Set deleteIdSet  = new TreeSet();

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		String[] deleteIds = data.getParameters ().getStrings ("selectedMembers");
		if (deleteIds == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile3"));
		}
		else
		{
			deleteIdSet.addAll(Arrays.asList(deleteIds));
			List deleteItems = new Vector();
			List notDeleteItems = new Vector();
			List nonEmptyFolders = new Vector();
			List roots = (List) state.getAttribute(STATE_COLLECTION_ROOTS);
			Iterator rootIt = roots.iterator();
			while(rootIt.hasNext())
			{
				BrowseItem root = (BrowseItem) rootIt.next();
				
				List members = root.getMembers();
				Iterator memberIt = members.iterator();
				while(memberIt.hasNext())
				{
					BrowseItem member = (BrowseItem) memberIt.next();
					if(deleteIdSet.contains(member.getId()))
					{
						if(member.isFolder())
						{
							if(ContentHostingService.allowRemoveCollection(member.getId()))
							{
								deleteItems.add(member);
								if(! member.isEmpty())
								{
									nonEmptyFolders.add(member);
								}
							}
							else
							{
								notDeleteItems.add(member);
							}
						}
						else if(ContentHostingService.allowRemoveResource(member.getId()))
						{
							deleteItems.add(member);
						}
						else
						{
							notDeleteItems.add(member);
						}
					}
				}
			}
			
			if(! notDeleteItems.isEmpty())
			{
				String notDeleteNames = "";
				boolean first_item = true;
				Iterator notIt = notDeleteItems.iterator();
				while(notIt.hasNext())
				{
					BrowseItem item = (BrowseItem) notIt.next();
					if(first_item)
					{
						notDeleteNames = item.getName();
						first_item = false;
					}
					else if(notIt.hasNext())
					{
						notDeleteNames += ", " + item.getName();
					}
					else
					{
						notDeleteNames += " and " + item.getName();
					}
				}
				addAlert(state, rb.getString("notpermis14") + notDeleteNames);
			}

			
			/*
					//htripath-SAK-1712 - Set new collectionId as resources are not deleted under 'more' requirement.
					if(state.getAttribute(STATE_MESSAGE) == null){
					  String newCollectionId=ContentHostingService.getContainingCollectionId(currentId);
					  state.setAttribute(STATE_COLLECTION_ID, newCollectionId);
					}
			*/	
			
			// delete item
			state.setAttribute (STATE_DELETE_ITEMS, deleteItems);
			state.setAttribute (STATE_DELETE_ITEMS_NOT_EMPTY, nonEmptyFolders);			
		}	// if-else
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute (STATE_MODE, MODE_DELETE_CONFIRM);
			state.setAttribute(STATE_LIST_SELECTIONS, deleteIdSet);
		}


	}	// doDeleteconfirm


	/**
	* set the state name to be "cut" if any item has been selected for cutting
	*/
	public void doCut ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		String[] cutItems = data.getParameters ().getStrings ("selectedMembers");
		if (cutItems == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile5"));
			state.setAttribute (STATE_MODE, MODE_LIST);
		}
		else
		{
			Vector cutIdsVector = new Vector ();
			String nonCutIds = NULL_STRING;

			String cutId = NULL_STRING;
			for (int i = 0; i < cutItems.length; i++)
			{
				cutId = cutItems[i];
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (cutId);
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
						}
					}
					else
					{
						if (ContentHostingService.allowRemoveResource (cutId))
						{
							cutIdsVector.add (cutId);
						}
						else
						{
							nonCutIds = nonCutIds + " " + properties.getProperty (ResourceProperties.PROP_DISPLAY_NAME) + "; ";
						}
					}
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis15"));
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}	// try-catch
			}

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				if (nonCutIds.length ()>0)
				{
					addAlert(state, rb.getString("notpermis16") +" " + nonCutIds);
				}

				if (cutIdsVector.size ()>0)
				{
					state.setAttribute (STATE_CUT_FLAG, Boolean.TRUE.toString());
					if (((String) state.getAttribute (STATE_SELECT_ALL_FLAG)).equals (Boolean.TRUE.toString()))
					{
						state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
					}

					Vector copiedIds = (Vector) state.getAttribute (STATE_COPIED_IDS);
					for (int i = 0; i < cutIdsVector.size (); i++)
					{
						String currentId = (String) cutIdsVector.elementAt (i);
						if ( copiedIds.contains (currentId))
						{
							copiedIds.remove (currentId);
						}
					}
					if (copiedIds.size ()==0)
					{
						state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());
					}

					state.setAttribute (STATE_COPIED_IDS, copiedIds);

					state.setAttribute (STATE_CUT_IDS, cutIdsVector);
				}
			}
		}	// if-else

	}	// doCut

	/**
	* set the state name to be "copy" if any item has been selected for copying
	*/
	public void doCopy ( RunData data )
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		Vector copyItemsVector = new Vector ();

		String[] copyItems = data.getParameters ().getStrings ("selectedMembers");
		if (copyItems == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile6"));
			state.setAttribute (STATE_MODE, MODE_LIST);
		}
		else
		{
			String copyId = NULL_STRING;
			for (int i = 0; i < copyItems.length; i++)
			{
				copyId = copyItems[i];
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (copyId);
					/*
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
						}
					}
					*/
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis15"));
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}	// try-catch
			}

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				state.setAttribute (STATE_COPY_FLAG, Boolean.TRUE.toString());
				
				copyItemsVector.addAll(Arrays.asList(copyItems));
				ContentHostingService.eliminateDuplicates(copyItemsVector);
				state.setAttribute (STATE_COPIED_IDS, copyItemsVector);
				
			}	// if-else
		}	// if-else

	}	// doCopy

	/**
	* Handle user's selection of items to be moved.
	*/
	public void doMove ( RunData data )
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		List moveItemsVector = new Vector();

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}
		
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		String[] moveItems = data.getParameters ().getStrings ("selectedMembers");
		if (moveItems == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile6"));
			state.setAttribute (STATE_MODE, MODE_LIST);
		}
		else
		{
			String moveId = NULL_STRING;
			for (int i = 0; i < moveItems.length; i++)
			{
				moveId = moveItems[i];
				try
				{
					ResourceProperties properties = ContentHostingService.getProperties (moveId);
					/*
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
						}
					}
					*/
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis15"));
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}	// try-catch
			}

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				state.setAttribute (STATE_MOVE_FLAG, Boolean.TRUE.toString());
				
				moveItemsVector.addAll(Arrays.asList(moveItems));
				
				ContentHostingService.eliminateDuplicates(moveItemsVector);
				
				state.setAttribute (STATE_MOVED_IDS, moveItemsVector);
				
			}	// if-else
		}	// if-else

	}	// doMove
	

	/**
	 * If copy-flag is set to false, erase the copied-id's list and set copied flags to false
	 * in all the browse items.  If copied-id's list is empty, set copy-flag to false and set
	 * copied flags to false in all the browse items. If copy-flag is set to true and copied-id's
	 * list is not empty, update the copied flags of all browse items so copied flags for the 
	 * copied items are set to true and all others are set to false.
	 */
	protected void setCopyFlags(SessionState state)
	{
		String copyFlag = (String) state.getAttribute(STATE_COPY_FLAG);
		List copyItemsVector = (List) state.getAttribute(STATE_COPIED_IDS);
		
		if(copyFlag == null)
		{
			copyFlag = Boolean.FALSE.toString();
			state.setAttribute(STATE_COPY_FLAG, copyFlag);
		}
		
		if(copyFlag.equals(Boolean.TRUE.toString()))
		{
			if(copyItemsVector == null)
			{
				copyItemsVector = new Vector();
				state.setAttribute(STATE_COPIED_IDS, copyItemsVector);
			}
			if(copyItemsVector.isEmpty())
			{
				state.setAttribute(STATE_COPY_FLAG, Boolean.FALSE.toString());
			}
		}
		else
		{
			copyItemsVector = new Vector();
			state.setAttribute(STATE_COPIED_IDS, copyItemsVector);
		}
		
		List roots = (List) state.getAttribute(STATE_COLLECTION_ROOTS);
		Iterator rootIt = roots.iterator();
		while(rootIt.hasNext())
		{
			BrowseItem root = (BrowseItem) rootIt.next();
			boolean root_copied = copyItemsVector.contains(root.getId());
			root.setCopied(root_copied);
			
			List members = root.getMembers();
			Iterator memberIt = members.iterator();
			while(memberIt.hasNext())
			{
				BrowseItem member = (BrowseItem) memberIt.next();
				boolean member_copied = copyItemsVector.contains(member.getId());
				member.setCopied(member_copied);
			}
		}
		state.setAttribute(STATE_COLLECTION_ROOTS, roots);

	}	// setCopyFlags

	/**
	* Expand all the collection resources.
	*/
	static public void doExpandall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = params.getStrings("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);

		// expansion actually occurs in getBrowseItems method.
		state.setAttribute(STATE_EXPAND_ALL_FLAG,  Boolean.TRUE.toString());
		state.setAttribute(STATE_NEED_TO_EXPAND_ALL, Boolean.TRUE.toString());
		
	}	// doExpandall

	/**
	* Unexpand all the collection resources
	*/
	public static void doUnexpandall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = params.getStrings ("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);
		
		state.setAttribute(EXPANDED_COLLECTIONS, new HashMap());
		state.setAttribute(STATE_EXPAND_ALL_FLAG, Boolean.FALSE.toString());

	}	// doUnexpandall
	
	/**
	* Build the menu.
	*/
	private void buildListMenu (	VelocityPortlet portlet,
									Context context,
									RunData data,
									SessionState state)
	{
		context.put("tlang",rb);

		// build a test menu
		Menu bar = new Menu (portlet, data, "ResourcesAction");
		bar.add ( new MenuEntry (rb.getString("overview"),false,"") );
		bar.add( new MenuEntry(rb.getString("permissions"), "doPermissions") );

		context.put (Menu.CONTEXT_MENU, bar);
		context.put (Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));
        
	}	// buildListMenu

	/**
	* Populate the state object, if needed - override to do something!
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData data)
	{
		super.initState(state, portlet, data);
		
		if(state.getAttribute(STATE_INITIALIZED) == null)
		{
			initCopyContext(state); 
			initMoveContext(state); 
		}
		
		initStateAttributes(state, portlet);
				
	}	// initState
	
	/**
	* Remove the state variables used internally, on the way out.
	*/
	static private void cleanupState(SessionState state)
	{
		state.removeAttribute(STATE_FROM_TEXT);
		state.removeAttribute(STATE_HAS_ATTACHMENT_BEFORE);
		state.removeAttribute(STATE_SAVE_ATTACHMENT_IN_DROPBOX);
		
		state.removeAttribute(COPYRIGHT_FAIRUSE_URL);
		state.removeAttribute(COPYRIGHT_NEW_COPYRIGHT);
		state.removeAttribute(COPYRIGHT_SELF_COPYRIGHT);
		state.removeAttribute(COPYRIGHT_TYPES);
		state.removeAttribute(DEFAULT_COPYRIGHT_ALERT);
		state.removeAttribute(DEFAULT_COPYRIGHT);
		state.removeAttribute(EXPANDED_COLLECTIONS);
		state.removeAttribute(FILE_UPLOAD_MAX_SIZE);
		state.removeAttribute(NEW_COPYRIGHT_INPUT);
		state.removeAttribute(STATE_COLLECTION_ID);
		state.removeAttribute(STATE_COLLECTION_PATH);
		state.removeAttribute(STATE_CONTENT_SERVICE);
		state.removeAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE);
		state.removeAttribute(STATE_EDIT_INTENT);
		state.removeAttribute(STATE_EXPAND_ALL_FLAG);
		state.removeAttribute(STATE_HELPER_NEW_ITEMS);
		state.removeAttribute(STATE_HELPER_CHANGED);
		state.removeAttribute(STATE_HOME_COLLECTION_DISPLAY_NAME);
		state.removeAttribute(STATE_HOME_COLLECTION_ID);
		state.removeAttribute(STATE_LIST_SELECTIONS);
		state.removeAttribute(STATE_MY_COPYRIGHT);
		state.removeAttribute(STATE_NAVIGATION_ROOT);
		state.removeAttribute(STATE_PASTE_ALLOWED_FLAG);
		state.removeAttribute(STATE_PROPERTIES_INTENT);
		state.removeAttribute(STATE_SELECT_ALL_FLAG);
		state.removeAttribute(STATE_SHOW_ALL_SITES);
		state.removeAttribute(STATE_SITE_TITLE);
		state.removeAttribute(STATE_SORT_ASC);
		state.removeAttribute(STATE_SORT_BY);
		
		state.removeAttribute(STATE_INITIALIZED);
		state.removeAttribute(VelocityPortletPaneledAction.STATE_HELPER);

	}	// cleanupState

	
	public static void initStateAttributes(SessionState state, VelocityPortlet portlet)
	{
		if (state.getAttribute (STATE_INITIALIZED) != null) return;

		if (state.getAttribute(FILE_UPLOAD_MAX_SIZE) == null)
		{
			state.setAttribute(FILE_UPLOAD_MAX_SIZE, ServerConfigurationService.getString("content.upload.max", "1"));
		}
		
		state.setAttribute (STATE_CONTENT_SERVICE, ContentHostingService.getInstance());
		state.setAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE, ContentTypeImageService.getInstance());

		TimeBreakdown timeBreakdown = (TimeService.newTime()).breakdownLocal ();
		String mycopyright = COPYRIGHT_SYMBOL + " " + timeBreakdown.getYear () +", " + UserDirectoryService.getCurrentUser().getDisplayName () + ". All Rights Reserved. ";
		state.setAttribute (STATE_MY_COPYRIGHT, mycopyright);
		
		if(state.getAttribute(STATE_MODE) == null)
		{
			state.setAttribute (STATE_MODE, MODE_LIST);
			state.setAttribute (STATE_FROM, NULL_STRING);
		}
		state.setAttribute (STATE_SORT_BY, ResourceProperties.PROP_DISPLAY_NAME);

		state.setAttribute (STATE_SORT_ASC, Boolean.TRUE.toString());

		state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
		
		state.setAttribute (STATE_EXPAND_ALL_FLAG, Boolean.FALSE.toString());

		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());
		
		state.setAttribute (STATE_COLLECTION_PATH, new Vector ());
		
		// In helper mode, calling tool should set attribute STATE_RESOURCES_MODE
		String resources_mode = (String) state.getAttribute(STATE_RESOURCES_MODE);
		if(resources_mode == null)
		{
			// get resources mode from tool registry
			resources_mode = portlet.getPortletConfig().getInitParameter("resources_mode");
			state.setAttribute(STATE_RESOURCES_MODE, resources_mode);
		}
		
		boolean show_other_sites = false;
		if(RESOURCES_MODE_HELPER.equals(resources_mode))
		{
			show_other_sites = ServerConfigurationService.getBoolean("resources.show_all_collections.helper", SHOW_ALL_SITES_IN_FILE_PICKER);
		}
		else if(RESOURCES_MODE_DROPBOX.equals(resources_mode))
		{
			show_other_sites = ServerConfigurationService.getBoolean("resources.show_all_collections.dropbox", SHOW_ALL_SITES_IN_DROPBOX);
		}
		else
		{
			show_other_sites = ServerConfigurationService.getBoolean("resources.show_all_collections.tool", SHOW_ALL_SITES_IN_RESOURCES);
		}
		/** This attribute indicates whether "Other Sites" twiggle should show */
		state.setAttribute(STATE_SHOW_ALL_SITES, Boolean.toString(show_other_sites));
		/** This attribute indicates whether "Other Sites" twiggle should be open */
		state.setAttribute(STATE_SHOW_OTHER_SITES, Boolean.FALSE.toString());
		
		// set the home collection to the parameter, if present, or the default if not
		String home = StringUtil.trimToNull(portlet.getPortletConfig().getInitParameter("home"));
		state.setAttribute (STATE_HOME_COLLECTION_DISPLAY_NAME, home);
		if ((home == null) || (home.length() == 0))
		{
			// no home set, see if we are in dropbox mode
			if (resources_mode.equalsIgnoreCase (RESOURCES_MODE_DROPBOX))
			{
				home = Dropbox.getCollection();
				
				// if it came back null, we will pretend not to be in dropbox mode
				if (home != null)
				{
					state.setAttribute(STATE_HOME_COLLECTION_DISPLAY_NAME, Dropbox.getDisplayName());

					// create/update the collection of folders in the dropbox
					Dropbox.createCollection();
				}
			}

			// if we still don't have a home, 
			if ((home == null) || (home.length() == 0))
			{
				home = ContentHostingService.getSiteCollection(ToolManager.getCurrentPlacement().getContext());
				
				// TODO: what's the 'name' of the context? -ggolden
				// we'll need this to create the home collection if needed
				state.setAttribute (STATE_HOME_COLLECTION_DISPLAY_NAME, ToolManager.getCurrentPlacement().getContext()
						/*SiteService.getSiteDisplay(PortalService.getCurrentSiteId()) */);
			}
		}
		state.setAttribute (STATE_HOME_COLLECTION_ID, home);
		state.setAttribute (STATE_COLLECTION_ID, home);
		state.setAttribute (STATE_NAVIGATION_ROOT, home);
		
		HomeFactory factory = (HomeFactory) ComponentManager.get("homeFactory");	
		Map homes = factory.getHomes(StructuredArtifactHomeInterface.class);
		if(! homes.isEmpty())
		{
			state.setAttribute(STATE_SHOW_FORM_ITEMS, Boolean.TRUE.toString());
		}
		
		// state.setAttribute (STATE_COLLECTION_ID, state.getAttribute (STATE_HOME_COLLECTION_ID));
		
		if (state.getAttribute(STATE_SITE_TITLE) == null)
		{
			String title = "";
			try
			{
				title = ((Site) SiteService.getSite(PortalService.getCurrentSiteId())).getTitle();
			}
			catch (IdUnusedException e)
			{
			}
			state.setAttribute(STATE_SITE_TITLE, title);
		}

		HashMap expandedCollections = new HashMap();
		//expandedCollections.add (state.getAttribute (STATE_HOME_COLLECTION_ID));
		state.setAttribute(EXPANDED_COLLECTIONS, expandedCollections);
		
		if (state.getAttribute(COPYRIGHT_TYPES) == null)
		{
			if (ServerConfigurationService.getStrings("copyrighttype") != null)
			{
				state.setAttribute(COPYRIGHT_TYPES, new ArrayList(Arrays.asList(ServerConfigurationService.getStrings("copyrighttype"))));
			}
		}
		
		if (state.getAttribute(DEFAULT_COPYRIGHT) == null)
		{
			if (ServerConfigurationService.getString("default.copyright") != null)
			{
				state.setAttribute(DEFAULT_COPYRIGHT, ServerConfigurationService.getString("default.copyright"));
			}
		}
		
		if (state.getAttribute(DEFAULT_COPYRIGHT_ALERT) == null)
		{
			if (ServerConfigurationService.getString("default.copyright.alert") != null)
			{
				state.setAttribute(DEFAULT_COPYRIGHT_ALERT, ServerConfigurationService.getString("default.copyright.alert"));
			}
		}
		
		if (state.getAttribute(NEW_COPYRIGHT_INPUT) == null)
		{
			if (ServerConfigurationService.getString("newcopyrightinput") != null)
			{
				state.setAttribute(NEW_COPYRIGHT_INPUT, ServerConfigurationService.getString("newcopyrightinput"));
			}
		}
		
		if (state.getAttribute(COPYRIGHT_FAIRUSE_URL) == null)
		{
			if (ServerConfigurationService.getString("fairuse.url") != null)
			{
				state.setAttribute(COPYRIGHT_FAIRUSE_URL, ServerConfigurationService.getString("fairuse.url"));
			}
		}
		
		if (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) == null)
		{
			if (ServerConfigurationService.getString("copyrighttype.own") != null)
			{
				state.setAttribute(COPYRIGHT_SELF_COPYRIGHT, ServerConfigurationService.getString("copyrighttype.own"));
			}
		}
		
		if (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) == null)
		{
			if (ServerConfigurationService.getString("copyrighttype.new") != null)
			{
				state.setAttribute(COPYRIGHT_NEW_COPYRIGHT, ServerConfigurationService.getString("copyrighttype.new"));
			}
		}
		
		if(state.getAttribute(STATE_PROPERTIES_INTENT) == null)
		{
			state.setAttribute(STATE_PROPERTIES_INTENT, INTENT_REVISE_FILE); 
		}
		
		if(state.getAttribute(STATE_EDIT_INTENT) == null)
		{
			state.setAttribute(STATE_EDIT_INTENT, INTENT_REVISE_FILE); 
		}

		// get resources mode from tool registry
		String optional_properties = portlet.getPortletConfig().getInitParameter("optional_properties");
		if(optional_properties != null && "true".equalsIgnoreCase(optional_properties))
		{
			initMetadataContext(state);
		}

		state.setAttribute (STATE_INITIALIZED, Boolean.TRUE.toString());
		
	}
	
	/**
	* Setup our observer to be watching for change events for the collection
 	*/
 	private void updateObservation(SessionState state, String peid)
 	{
// 		ContentObservingCourier observer = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//		
// 		// the delivery location for this tool
// 		String deliveryId = clientWindowId(state, peid);
// 		observer.setDeliveryId(deliveryId);
	}

	/**
	 * Add additional resource pattern to the observer
	 *@param pattern The pattern value to be added
	 *@param state The state object
	 */
	private static void addObservingPattern(String pattern, SessionState state)
	{
//		// get the observer and add the pattern
//		ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//		o.addResourcePattern(ContentHostingService.getReference(pattern));
//		
//		// add it back to state
//		state.setAttribute(STATE_OBSERVER, o);
		
	}	// addObservingPattern
	
	/**
	 * Remove a resource pattern from the observer
	 *@param pattern The pattern value to be removed
	 *@param state The state object
	 */
	private static void removeObservingPattern(String pattern, SessionState state)
	{
//		// get the observer and remove the pattern
//		ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//		o.removeResourcePattern(ContentHostingService.getReference(pattern));
//		
//		// add it back to state
//		state.setAttribute(STATE_OBSERVER, o);
		
	}	// removeObservingPattern
	
	/**
	* initialize the copy context
	*/
	private static void initCopyContext (SessionState state)
	{
		state.setAttribute (STATE_COPIED_IDS, new Vector ());

		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

	}	// initCopyContent

	/**
	* initialize the copy context
	*/
	private static void initMoveContext (SessionState state)
	{
		state.setAttribute (STATE_MOVED_IDS, new Vector ());

		state.setAttribute (STATE_MOVE_FLAG, Boolean.FALSE.toString());

	}	// initCopyContent


	/**
	* initialize the cut context
	*/
	private void initCutContext (SessionState state)
	{
		state.setAttribute (STATE_CUT_IDS, new Vector ());

		state.setAttribute (STATE_CUT_FLAG, Boolean.FALSE.toString());

	}	// initCutContent

	/**
	* initialize properties context
	*/
	private void initPropertiesContext (SessionState state)
	{
		state.removeAttribute(STATE_PROPERTIES_ID);
		state.removeAttribute(STATE_PROPERTIES_COPYRIGHT_CHOICE);
		state.removeAttribute(STATE_PROPERTIES_COPYRIGHT);

	}	// initPropertiesContext

	/**
	* find out whether there is a duplicate item in testVector
	* @param testVector The Vector to be tested on
	* @param testSize The integer of the test range
	* @return The index value of the duplicate ite
	*/
	private int repeatedName (Vector testVector, int testSize)
	{
		for (int i=1; i <= testSize; i++)
		{
			String currentName = (String) testVector.get (i);
			for (int j=i+1; j <= testSize; j++)
			{
				String comparedTitle = (String) testVector.get (j);
				if (comparedTitle.length()>0 && currentName.length()>0 && comparedTitle.equals (currentName))
				{
					return j;
				}
			}
		}
		return 0;

	}   // repeatedName

	/**
	* Is the id already exist in the current resource?
	* @param testVector The Vector to be tested on
	* @param testSize The integer of the test range
	* @parma isCollection Looking for collection or not
	* @return The index value of the exist id
	*/
	private int foundInResource (Vector testVector, int testSize, String collectionId, boolean isCollection)
	{
		try
		{
			ContentCollection c = ContentHostingService.getCollection(collectionId);
			Iterator membersIterator = c.getMemberResources().iterator();
			while (membersIterator.hasNext())
			{
				ResourceProperties p = ((Entity) membersIterator.next()).getProperties();
				String displayName = p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
				if (displayName != null)
				{
					String collectionOrResource = p.getProperty(ResourceProperties.PROP_IS_COLLECTION);
					for (int i=1; i <= testSize; i++)
					{
						String testName = (String) testVector.get(i);
						if ((testName != null) && (displayName.equals (testName)) 
						      &&  ((isCollection && collectionOrResource.equals (Boolean.TRUE.toString()))
								        || (!isCollection && collectionOrResource.equals(Boolean.FALSE.toString()))))
						{
							return i;
						}
					}	// for
				}
			}
		}
		catch (IdUnusedException e){}
		catch (TypeException e){}
		catch (PermissionException e){}
		
		return 0;
		
	}	// foundInResource

	/**
	* empty String Vector object with the size sepecified
	* @param size The Vector object size -1
	* @return The Vector object consists of null Strings
	*/
	private Vector emptyVector (int size)
	{
		Vector v = new Vector ();
		for (int i=0; i <= size; i++)
		{
			v.add (i, "");
		}
		return v;

	}	// emptyVector

	/**
	*  Setup for customization
	**/
	public String buildOptionsPanelContext( VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state) 
	{
		context.put("tlang",rb);
		String home = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
		Reference ref = EntityManager.newReference(ContentHostingService.getReference(home));
		String siteId = ref.getContext();

		context.put("form-submit", BUTTON + "doConfigure_update");
		context.put("form-cancel", BUTTON + "doCancel_options");
		context.put("description", "Setting options for Resources in worksite "
				+ SiteService.getSiteDisplay(siteId));

		// pick the "-customize" template based on the standard template name
		String template = (String)getContext(data).get("template");
		return template + "-customize";

	}	// buildOptionsPanelContext

	/**
	* Handle the configure context's update button
	*/
	public void doConfigure_update(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// we are done with customization... back to the main (browse) mode
		state.setAttribute(STATE_MODE, MODE_LIST);

		// commit the change
		// saveOptions();
		cancelOptions();

	}   // doConfigure_update
	
	/**
	* doCancel_options called for form input tags type="submit" named="eventSubmit_doCancel"
	* cancel the options process
	*/
	public void doCancel_options(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// cancel the options
		cancelOptions();

		// we are done with customization... back to the main (MODE_LIST) mode
		state.setAttribute(STATE_MODE, MODE_LIST);

	}   // doCancel_options

	/**
	* Add the collection id into the expanded collection list
	 * @throws PermissionException
	 * @throws TypeException
	 * @throws IdUnusedException
	*/
	public static void doExpand_collection(RunData data) throws IdUnusedException, TypeException, PermissionException
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
		
		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = params.getStrings ("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);
		
		String id = params.getString("collectionId");
		currentMap.put (id,ContentHostingService.getCollection (id)); 
		
		state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
		// add this folder id into the set to be event-observed
		addObservingPattern(id, state);
	
	}	// doExpand_collection

	/**
	* Remove the collection id from the expanded collection list
	*/
	static public void doCollapse_collection(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
		
		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		String collectionId = params.getString("collectionId");
		
		// save the current selections
		Set selectedSet  = new TreeSet();
		String[] selectedItems = data.getParameters ().getStrings ("selectedMembers");
		if(selectedItems != null)
		{
			selectedSet.addAll(Arrays.asList(selectedItems));
		}
		state.setAttribute(STATE_LIST_SELECTIONS, selectedSet);
		
		HashMap newSet = new HashMap();
		Iterator l = currentMap.keySet().iterator ();
		while (l.hasNext ())
		{
			// remove the collection id and all of the subcollections
//		    Resource collection = (Resource) l.next();
//			String id = (String) collection.getId();
		    String id = (String) l.next();
		    
			if (id.indexOf (collectionId)==-1)
			{
	//			newSet.put(id,collection);
				newSet.put(id,currentMap.get(id));
			}
		}
		
		state.setAttribute(EXPANDED_COLLECTIONS, newSet);
		
		// remove this folder id into the set to be event-observed		
		removeObservingPattern(collectionId, state);
	
	}	// doCollapse_collection
	
	/**
	 * @param state
	 * @param homeCollectionId
	 * @param currentCollectionId
	 * @return
	 */
	public static List getCollectionPath(SessionState state)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		// make sure the channedId is set
		String currentCollectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
		String navRoot = (String) state.getAttribute(STATE_NAVIGATION_ROOT);
		
		LinkedList collectionPath = new LinkedList();
		
		String previousCollectionId = "";
		Vector pathitems = new Vector();
		while(currentCollectionId != null && ! currentCollectionId.equals(navRoot) && ! currentCollectionId.equals(previousCollectionId))
		{
			pathitems.add(currentCollectionId);
			previousCollectionId = currentCollectionId;
			currentCollectionId = contentService.getContainingCollectionId(currentCollectionId);
		}
		pathitems.add(navRoot);

		if(!navRoot.equals(homeCollectionId))
		{
			pathitems.add(homeCollectionId);
		}

		Iterator items = pathitems.iterator();
		while(items.hasNext())
		{
			String id = (String) items.next();
			try
			{
				ResourceProperties props = contentService.getProperties(id);
				String name = props.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				PathItem item = new PathItem(id, name);
				
				boolean canRead = contentService.allowGetCollection(id) || contentService.allowGetResource(id);
				item.setCanRead(canRead);
				
				String url = contentService.getUrl(id);
				item.setUrl(url);
				
				item.setLast(collectionPath.isEmpty());
				if(id.equals(homeCollectionId))
				{
					item.setRoot(homeCollectionId);
				}
				else
				{
					item.setRoot(navRoot);
				}
				
				try
				{
					boolean isFolder = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
					item.setIsFolder(isFolder);
				}
				catch (EmptyException e1)
				{
				}
				catch (TypeException e1)
				{
				}
				
				collectionPath.addFirst(item);
					
			}
			catch (PermissionException e)
			{
			}
			catch (IdUnusedException e)
			{
			}
		}
		return collectionPath;
	}
	
	/**
	 * Get the items in this folder that should be seen.
	 * @param collectionId - String version of 
	 * @param expandedCollections - Hash of collection resources
	 * @param sortedBy  - pass through to ContentHostingComparator
	 * @param sortedAsc - pass through to ContentHostingComparator
	 * @param parent - The folder containing this item
	 * @param isLocal - true if navigation root and home collection id of site are the same, false otherwise
	 * @param state - The session state
	 * @return a List of BrowseItem objects
	 */
	protected static List getBrowseItems(String collectionId, HashMap expandedCollections, Set highlightedItems, String sortedBy, String sortedAsc, BrowseItem parent, boolean isLocal, SessionState state)
	{
		boolean need_to_expand_all = Boolean.TRUE.toString().equals((String)state.getAttribute(STATE_NEED_TO_EXPAND_ALL));
		
		List newItems = new LinkedList();
		try
		{
			// find the ContentHosting service
			org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);

			// get the collection
			// try using existing resource first
			ContentCollection collection = null;
			
			// get the collection
			if (expandedCollections.containsKey(collectionId)) 
			{
				collection = (ContentCollection) expandedCollections.get(collectionId);
			}
			else 
			{
				collection = ContentHostingService.getCollection(collectionId);
				if(need_to_expand_all)
				{
					expandedCollections.put(collectionId, collection);
					state.setAttribute(EXPANDED_COLLECTIONS, expandedCollections);
				}
			}
				
			String dummyId = collectionId.trim();
			if(dummyId.endsWith(Entity.SEPARATOR))
			{
				dummyId += "dummy";
			}
			else
			{
				dummyId += Entity.SEPARATOR + "dummy";
			}
			
			boolean canRead = false;
			boolean canDelete = false;
			boolean canRevise = false;
			boolean canAddFolder = false;
			boolean canAddItem = false;
			boolean canUpdate = false;
			int depth = 0;
			
			if(parent == null || ! parent.canRead())
			{
				canRead = contentService.allowGetCollection(collectionId);
			}
			else
			{
				canRead = parent.canRead();
			}
			if(parent == null || ! parent.canDelete())
			{
				canDelete = contentService.allowRemoveResource(collectionId);
			}
			else
			{
				canDelete = parent.canDelete();
			}
			if(parent == null || ! parent.canRevise())
			{
				canRevise = contentService.allowUpdateResource(collectionId);
			}
			else
			{
				canRevise = parent.canRevise();
			}
			if(parent == null || ! parent.canAddFolder())
			{
				canAddFolder = contentService.allowAddCollection(dummyId);
			}
			else
			{
				canAddFolder = parent.canAddFolder();
			}
			if(parent == null || ! parent.canAddItem())
			{
				canAddItem = contentService.allowAddResource(dummyId);
			}
			else
			{
				canAddItem = parent.canAddItem();
			}
			if(parent == null || ! parent.canUpdate())
			{
				canUpdate = AuthzGroupService.allowUpdate(collectionId);
			}
			else
			{
				canUpdate = parent.canUpdate();
			}			
			if(parent != null)
			{
				depth = parent.getDepth() + 1;
			}
			
			if(canAddItem)
			{
				state.setAttribute(STATE_PASTE_ALLOWED_FLAG, Boolean.TRUE.toString());
			}
			boolean hasDeletableChildren = canDelete;
			boolean hasCopyableChildren = canRead;
			
			String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);

			ResourceProperties cProperties = collection.getProperties();
			String folderName = cProperties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
			if(collectionId.equals(homeCollectionId))
			{
				folderName = (String) state.getAttribute(STATE_HOME_COLLECTION_DISPLAY_NAME);
			}
			BrowseItem folder = new BrowseItem(collectionId, folderName, "folder");
			if(parent == null)
			{
				folder.setRoot(collectionId);
			}
			else
			{
				folder.setRoot(parent.getRoot());
			}
			
			if(highlightedItems == null || highlightedItems.isEmpty())
			{
				// do nothing
			}
			else if(parent != null && parent.isHighlighted())
			{
				folder.setInheritsHighlight(true);
				folder.setHighlighted(true);
			}
			else if(highlightedItems.contains(collectionId))
			{
				folder.setHighlighted(true);
				folder.setInheritsHighlight(false);
			}
			
			String containerId = contentService.getContainingCollectionId (collectionId);
			folder.setContainer(containerId);
			
			folder.setCanRead(canRead);
			folder.setCanRevise(canRevise);
			folder.setCanAddItem(canAddItem);
			folder.setCanAddFolder(canAddFolder);
			folder.setCanDelete(canDelete);
			folder.setCanUpdate(canUpdate);
			
			try
			{
				Time createdTime = cProperties.getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				String createdTimeString = createdTime.toStringLocalShortDate();
				folder.setCreatedTime(createdTimeString);
			}
			catch(Exception e)
			{
				String createdTimeString = cProperties.getProperty(ResourceProperties.PROP_CREATION_DATE);
				folder.setCreatedTime(createdTimeString);
			}
			try
			{
				String createdBy = cProperties.getUserProperty(ResourceProperties.PROP_CREATOR).getDisplayName();
				folder.setCreatedBy(createdBy);
			}
			catch(Exception e)
			{
				String createdBy = cProperties.getProperty(ResourceProperties.PROP_CREATOR);
				folder.setCreatedBy(createdBy);
			}
			try
			{
				Time modifiedTime = cProperties.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
				String modifiedTimeString = modifiedTime.toStringLocalShortDate();
				folder.setModifiedTime(modifiedTimeString);
			}
			catch(Exception e)
			{
				String modifiedTimeString = cProperties.getProperty(ResourceProperties.PROP_MODIFIED_DATE);
				folder.setModifiedTime(modifiedTimeString);
			}
			try
			{
				String modifiedBy = cProperties.getUserProperty(ResourceProperties.PROP_MODIFIED_BY).getDisplayName();
				folder.setModifiedBy(modifiedBy);
			}
			catch(Exception e)
			{
				String modifiedBy = cProperties.getProperty(ResourceProperties.PROP_MODIFIED_BY);
				folder.setModifiedBy(modifiedBy);
			}
			
			String url = contentService.getUrl(collectionId);
			folder.setUrl(url);
			try
			{
				int collection_size = contentService.getCollectionSize(collectionId);
				folder.setIsEmpty(collection_size < 1);
			}
			catch(Throwable e)
			{
				folder.setIsEmpty(true);
			}
			folder.setDepth(depth);
			newItems.add(folder);
			
			if(need_to_expand_all || expandedCollections.containsKey (collectionId))
			{
				// Get the collection members from the 'new' collection
				List newMembers = collection.getMemberResources ();
							
				Collections.sort (newMembers, new ContentHostingComparator (sortedBy, Boolean.valueOf (sortedAsc).booleanValue ()));
				// loop thru the (possibly) new members and add to the list 
				Iterator it = newMembers.iterator();
				while(it.hasNext())
				{
					Entity resource = (Entity) it.next();
					ResourceProperties props = resource.getProperties();
					
					String itemId = resource.getId();
					
					boolean isCollection = false;
					try
					{
						isCollection = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
					}
					catch(EmptyException e)
					{
						// assume isCollection is false if property is not set
					}
					
					if(isCollection)
					{
						List offspring = getBrowseItems(itemId, expandedCollections, highlightedItems, sortedBy, sortedAsc, folder, isLocal, state);
						if(! offspring.isEmpty())
						{
							BrowseItem child = (BrowseItem) offspring.get(0);
							hasDeletableChildren = hasDeletableChildren || child.hasDeletableChildren();
							hasCopyableChildren = hasCopyableChildren || child.hasCopyableChildren();
						}
						
						// add all the items in the subfolder to newItems
						newItems.addAll(offspring);
					}
					else
					{
						String itemType = ((ContentResource)resource).getContentType();
						String itemName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
						BrowseItem newItem = new BrowseItem(itemId, itemName, itemType);
						
						newItem.setContainer(collectionId);
						newItem.setRoot(folder.getRoot());
						
						newItem.setCanDelete(canDelete);
						newItem.setCanRevise(canRevise);
						newItem.setCanRead(canRead);
						newItem.setCanCopy(canRead);
						newItem.setCanAddItem(canAddItem); // true means this user can add an item in the folder containing this item (used for "duplicate") 
						
						if(highlightedItems == null || highlightedItems.isEmpty())
						{
							// do nothing
						}
						else if(folder.isHighlighted())
						{
							newItem.setInheritsHighlight(true);
							newItem.setHighlighted(true);
						}
						else if(highlightedItems.contains(itemId))
						{
							newItem.setHighlighted(true);
							newItem.setInheritsHighlight(false);
						}

						try
						{
							Time createdTime = props.getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
							String createdTimeString = createdTime.toStringLocalShortDate();
							newItem.setCreatedTime(createdTimeString);
						}
						catch(Exception e)
						{
							String createdTimeString = props.getProperty(ResourceProperties.PROP_CREATION_DATE);
							newItem.setCreatedTime(createdTimeString);
						}
						try
						{
							String createdBy = props.getUserProperty(ResourceProperties.PROP_CREATOR).getDisplayName();
							newItem.setCreatedBy(createdBy);
						}
						catch(Exception e)
						{
							String createdBy = props.getProperty(ResourceProperties.PROP_CREATOR);
							newItem.setCreatedBy(createdBy);
						}
						try
						{
							Time modifiedTime = props.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
							String modifiedTimeString = modifiedTime.toStringLocalShortDate();
							newItem.setModifiedTime(modifiedTimeString);
						}
						catch(Exception e)
						{
							String modifiedTimeString = props.getProperty(ResourceProperties.PROP_MODIFIED_DATE);
							newItem.setModifiedTime(modifiedTimeString);
						}
						try
						{
							String modifiedBy = props.getUserProperty(ResourceProperties.PROP_MODIFIED_BY).getDisplayName();
							newItem.setModifiedBy(modifiedBy);
						}
						catch(Exception e)
						{
							String modifiedBy = props.getProperty(ResourceProperties.PROP_MODIFIED_BY);
							newItem.setModifiedBy(modifiedBy);
						}
						
						String size = props.getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH);
						newItem.setSize(size);
						
						String target = Validator.getResourceTarget(props);
						newItem.setTarget(target);
				
						String newUrl = contentService.getUrl(itemId);
						newItem.setUrl(newUrl);
						
						try
						{
							boolean copyrightAlert = props.getBooleanProperty(ResourceProperties.PROP_COPYRIGHT_ALERT);
							newItem.setCopyrightAlert(copyrightAlert);
						}
						catch(EmptyException e)
						{}
						catch(TypeException e)
						{}			
						newItem.setDepth(depth + 1);
						newItems.add(newItem);
					}
				}
				
			}
			folder.seDeletableChildren(hasDeletableChildren);
			folder.setCopyableChildren(hasCopyableChildren);
			// return newItems;
		}
		catch (IdUnusedException ignore)
		{
			// this condition indicates a site that does not have a resources collection (mercury?)
		}
		catch (TypeException e)
		{
			addAlert(state, "TypeException.");
		}
		catch (PermissionException e)
		{
			addAlert(state, "PermissionException");
		}
		
		return newItems;
	
	}	// getBrowseItems
	
	/**
	* set the state name to be "copy" if any item has been selected for copying
	*/
	public void doCopyitem ( RunData data )
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		String itemId = data.getParameters ().getString ("itemId");
		
		if (itemId == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile6"));
			state.setAttribute (STATE_MODE, MODE_LIST);
		}
		else
		{
			try
			{
				ResourceProperties properties = ContentHostingService.getProperties (itemId);
				/*
				if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
				{
					String alert = (String) state.getAttribute(STATE_MESSAGE);
					if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
					{
						addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
					}
				}
				*/
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis15"));
			}
			catch (IdUnusedException e)
			{
				addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}	// try-catch

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				state.setAttribute (STATE_COPY_FLAG, Boolean.TRUE.toString());

				state.setAttribute (STATE_COPIED_ID, itemId);
			}	// if-else
		}	// if-else

	}	// doCopyitem
	
	/**
	* Paste the previously copied item(s)
	*/
	public static void doPasteitems ( RunData data)
	{
		ParameterParser params = data.getParameters ();

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		List items = (List) state.getAttribute(STATE_COPIED_IDS);

		String collectionId = params.getString ("collectionId");
		
		Iterator itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			// get the copied item to be pasted
			String itemId = (String) itemIter.next();
				
			String originalDisplayName = NULL_STRING;
	
			try
			{
				String id = ContentHostingService.copyIntoFolder(itemId, collectionId);
				String mode = (String) state.getAttribute(STATE_MODE);
				if(MODE_HELPER.equals(mode))
				{
					attachItem(id, state);
				}
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis8") + " " + originalDisplayName + ". ");
			}
			catch (IdUnusedException e)
			{
				addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}
			catch (InUseException e)
			{
				addAlert(state, rb.getString("someone") + " " + originalDisplayName);
			}
			catch (TypeException e)
			{
				addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
			}
			catch(IdUsedException e)
			{
				addAlert(state, rb.getString("toomany"));
			}
			catch(ServerOverloadException e)
			{
				addAlert(state, rb.getString("failed"));
			}
			catch(InconsistentException e)
			{
				addAlert(state, rb.getString("recursive") + " " + itemId);
			}
			catch (OverQuotaException e)
			{
				addAlert(state, rb.getString("overquota"));
			}	// try-catch
				
			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				// delete sucessful
				String mode = (String) state.getAttribute(STATE_MODE);
				if(MODE_HELPER.equals(mode))
				{
					state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
				}
				else
				{
					state.setAttribute (STATE_MODE, MODE_LIST);
				} 
				
				// try to expand the collection
				HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
				if(! expandedCollections.containsKey(collectionId))
				{
					org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
					try
					{
						ContentCollection coll = contentService.getCollection(collectionId);
						expandedCollections.put(collectionId, coll);
					}
					catch(Exception ignore){}
				}			
	
				// reset the copy flag
				if (((String)state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
				{
					state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());
				}
			}
		
		}
		
	}	// doPasteitems

	/**
	* Paste the item(s) selected to be moved
	*/
	public static void doMoveitems ( RunData data)
	{
		ParameterParser params = data.getParameters ();

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		List items = (List) state.getAttribute(STATE_MOVED_IDS);

		String collectionId = params.getString ("collectionId");
		
		Iterator itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			// get the copied item to be pasted
			String itemId = (String) itemIter.next();
				
			String originalDisplayName = NULL_STRING;
	
			try
			{
				/*
				ResourceProperties properties = ContentHostingService.getProperties (itemId);
				originalDisplayName = properties.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
	
				// copy, cut and paste not operated on collections
				if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
				{
					String alert = (String) state.getAttribute(STATE_MESSAGE);
					if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
					{
						addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
					}
				}
				else
				*/
				{
					ContentHostingService.moveIntoFolder(itemId, collectionId);
				}	// if-else
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis8") + " " + originalDisplayName + ". ");
			}
			catch (IdUnusedException e)
			{
				addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}
			catch (InUseException e)
			{
				addAlert(state, rb.getString("someone") + " " + originalDisplayName);
			}
			catch (TypeException e)
			{
				addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
			}
			catch (InconsistentException e)
			{
				addAlert(state, rb.getString("recursive") + " " + itemId);
			}
			catch(IdUsedException e)
			{
				addAlert(state, rb.getString("toomany"));
			}
			catch(ServerOverloadException e)
			{
				addAlert(state, rb.getString("failed"));
			}
			catch (OverQuotaException e)
			{
				addAlert(state, rb.getString("overquota"));
			}	// try-catch
				
			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				// delete sucessful
				String mode = (String) state.getAttribute(STATE_MODE);
				if(MODE_HELPER.equals(mode))
				{
					state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
				}
				else
				{
					state.setAttribute (STATE_MODE, MODE_LIST);
				} 
				
				// try to expand the collection
				HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
				if(! expandedCollections.containsKey(collectionId))
				{
					org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
					try
					{
						ContentCollection coll = contentService.getCollection(collectionId);
						expandedCollections.put(collectionId, coll);
					}
					catch(Exception ignore){}
				}			
	
				// reset the copy flag
				if (((String)state.getAttribute (STATE_MOVE_FLAG)).equals (Boolean.TRUE.toString()))
				{
					state.setAttribute (STATE_MOVE_FLAG, Boolean.FALSE.toString());
				}
			}
		
		}
		
	}	// doMoveitems


	/**
	* Paste the previously copied item(s)
	*/
	public static void doPasteitem ( RunData data)
	{
		ParameterParser params = data.getParameters ();

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// get the copied item to be pasted
		String itemId = params.getString("itemId");

		String collectionId = params.getString ("collectionId");
		
		String originalDisplayName = NULL_STRING;

		try
		{
			ResourceProperties properties = ContentHostingService.getProperties (itemId);
			originalDisplayName = properties.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);

			// copy, cut and paste not operated on collections
			if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
			{
				String alert = (String) state.getAttribute(STATE_MESSAGE);
				if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
				{
					addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
				}
			}
			else
			{
				// paste the resource
				ContentResource resource = ContentHostingService.getResource (itemId);
				ResourceProperties p = ContentHostingService.getProperties(itemId);
				String displayName = DUPLICATE_STRING + p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

				// cut-paste to the same collection?
				boolean cutPasteSameCollection = false;

				int countNumber = 1;

				ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();

				// add the properties of the pasted item
				Iterator propertyNames = properties.getPropertyNames ();
				while ( propertyNames.hasNext ())
				{
					String propertyName = (String) propertyNames.next ();
					if (!properties.isLiveProperty (propertyName))
					{
						if (propertyName.equals (ResourceProperties.PROP_DISPLAY_NAME)&&(displayName.length ()>0))
						{
							resourceProperties.addProperty (propertyName, displayName);
						}
						else
						{
							resourceProperties.addProperty (propertyName, properties.getProperty (propertyName));
						}
					}
				}

				String newDisplayName = resourceProperties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
				int index = displayName.lastIndexOf(".");
				String base = displayName.substring(0, index);
				String ext = displayName.substring(index);
				
				boolean copy_completed = false;
				int num_tries = 0;
				while(! copy_completed && num_tries < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
				{
					String id = collectionId + Validator.escapeResourceName(displayName);
					try
					{
						// paste the copied resource to the new collection
						ContentResource newResource = ContentHostingService.addResource (id, resource.getContentType (), resource.getContent (), resourceProperties, NotificationService.NOTI_NONE);
						
						String mode = (String) state.getAttribute(STATE_MODE);
						if(MODE_HELPER.equals(mode))
						{
							attachItem(id, state);
						}
						copy_completed = true;
					}
					catch (IdUsedException e)
					{
						num_tries++;
						displayName = base + "-" + num_tries + ext;
						resourceProperties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, newDisplayName + " (" + num_tries + ")");
					}
					catch (InconsistentException e)
					{
						addAlert(state,RESOURCE_INVALID_TITLE_STRING);
					}
					catch (IdInvalidException e)
					{
						addAlert(state,rb.getString("title") + " " + e.getMessage ());
					}
					catch(ServerOverloadException e)
					{
						// this represents temporary unavailability of server's filesystem 
						// for server configured to save resource body in filesystem 
						addAlert(state, rb.getString("failed"));
					}
					catch (OverQuotaException e)
					{
						addAlert(state, rb.getString("overquota"));
					}	// try-catch
					
				}	// while

			}	// if-else
		}
		catch (PermissionException e)
		{
			addAlert(state, rb.getString("notpermis8") + " " + originalDisplayName + ". ");
		}
		catch (IdUnusedException e)
		{
			addAlert(state,RESOURCE_NOT_EXIST_STRING);
		}
		catch (TypeException e)
		{
			addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
		}	// try-catch
			
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// delete sucessful
			String mode = (String) state.getAttribute(STATE_MODE);
			if(MODE_HELPER.equals(mode))
			{
				state.setAttribute(STATE_RESOURCES_MODE, MODE_ATTACHMENT_SELECT);
			}
			else
			{
				state.setAttribute (STATE_MODE, MODE_LIST);
			} 
			
			// try to expand the collection
			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			if(! expandedCollections.containsKey(collectionId))
			{
				org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
				try
				{
					ContentCollection coll = contentService.getCollection(collectionId);
					expandedCollections.put(collectionId, coll);
				}
				catch(Exception ignore){}
			}			

			// reset the copy flag
			if (((String)state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
			{
				state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());
			}
		}
	
	}	// doPasteitem

	/**
	* Fire up the permissions editor for the current folder's permissions
	*/
	public void doFolder_permissions(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		ParameterParser params = data.getParameters();

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		// get the current collection id and the related site
		String collectionId = params.getString("collectionId"); //(String) state.getAttribute (STATE_COLLECTION_ID);
		String title = "";
		try
		{
			title = ContentHostingService.getProperties(collectionId).getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		}
		catch (PermissionException e)
		{
			addAlert(state, rb.getString("notread"));
		}
		catch (IdUnusedException e)
		{
			addAlert(state, rb.getString("notfindfol"));
		}
		
		Reference ref = EntityManager.newReference(ContentHostingService.getReference(collectionId));
		String siteId = ref.getContext();

		// setup for editing the permissions of this resource
		state.setAttribute(PermissionsAction.STATE_REALM_ID, ref.getReference());

		// ... using this realm's roles
		state.setAttribute(PermissionsAction.STATE_REALM_ROLES_ID, SiteService.siteReference(siteId));

		// ... with this description
		state.setAttribute(PermissionsAction.STATE_DESCRIPTION, rb.getString("setpermis") + " " + title);

		// ... showing only locks that are prpefixed with this
		state.setAttribute(PermissionsAction.STATE_PREFIX, "content.");

		// start the helper
		state.setAttribute(PermissionsAction.STATE_MODE, PermissionsAction.MODE_MAIN);

	}	// doFolder_permissions

	/**
	* Fire up the permissions editor for the tool's permissions
	*/
	public void doPermissions(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		// cancel copy if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_COPY_FLAG)))
		{
			initCopyContext(state);
		}

		// cancel move if there is one in progress
		if(! Boolean.FALSE.toString().equals(state.getAttribute (STATE_MOVE_FLAG)))
		{
			initMoveContext(state);
		}

		// should we save here?
		state.setAttribute(STATE_LIST_SELECTIONS, new TreeSet());

		// get the current home collection id and the related site
		String collectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		Reference ref = EntityManager.newReference(ContentHostingService.getReference(collectionId));
		String siteRef = SiteService.siteReference(ref.getContext());

		// setup for editing the permissions of the site for this tool, using the roles of this site, too
		state.setAttribute(PermissionsAction.STATE_REALM_ID, siteRef);
		state.setAttribute(PermissionsAction.STATE_REALM_ROLES_ID, siteRef);

		// ... with this description
		state.setAttribute(PermissionsAction.STATE_DESCRIPTION, rb.getString("setpermis1")
				+ SiteService.getSiteDisplay(ref.getContext()));

		// ... showing only locks that are prpefixed with this
		state.setAttribute(PermissionsAction.STATE_PREFIX, "content.");

		// start the helper
		state.setAttribute(PermissionsAction.STATE_MODE, PermissionsAction.MODE_MAIN);

	}	// doPermissions

	/**
	* is notification enabled?
	*/
	protected boolean notificationEnabled(SessionState state)
	{
		return true;

	}	// notificationEnabled
	
	/**
	 * Processes the HTML document that is coming back from the browser 
	 * (from the formatted text editing widget).
	 * @param state Used to pass in any user-visible alerts or errors when processing the text
	 * @param strFromBrowser The string from the browser
	 * @return The formatted text
	 */
	private String processHtmlDocumentFromBrowser(SessionState state, String strFromBrowser)
	{
		StringBuffer alertMsg = new StringBuffer();
		String text = FormattedText.processHtmlDocument(strFromBrowser, alertMsg);
		if (alertMsg.length() > 0) addAlert(state, alertMsg.toString());
		return text;
	}
	
	/**
	 * 
	 * Whether a resource item can be replaced
	 * @param p The ResourceProperties object for the resource item
	 * @return true If it can be replaced; false otherwise
	 */
	private boolean replaceable(ResourceProperties p)
	{
		boolean rv = true;
		
		if (p.getPropertyFormatted (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
		{
			rv = false;
		}
		else if (p.getProperty (ResourceProperties.PROP_CONTENT_TYPE).equals (ResourceProperties.TYPE_URL))
		{
			rv = false;
		}
		String displayName = p.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
		if (displayName.indexOf(SHORTCUT_STRING) != -1)
		{
			rv = false;
		}
		
		return rv;
		
	}	// replaceable
	
	/**
	 * 
	 * put copyright info into context
	 */
	private static void copyrightChoicesIntoContext(SessionState state, Context context)
	{

		//copyright
		if (state.getAttribute(COPYRIGHT_FAIRUSE_URL) != null)
		{
			context.put("fairuseurl", state.getAttribute(COPYRIGHT_FAIRUSE_URL));
		}
		if (state.getAttribute(NEW_COPYRIGHT_INPUT) != null)
		{
			context.put("newcopyrightinput", state.getAttribute(NEW_COPYRIGHT_INPUT));
		}
		if (state.getAttribute(COPYRIGHT_TYPES) != null)
		{
			List copyrightTypes = (List) state.getAttribute(COPYRIGHT_TYPES);
			context.put("copyrightTypes", copyrightTypes);
			context.put("copyrightTypesSize", new Integer(copyrightTypes.size()-1));
		}

		
	}	// copyrightChoicesIntoContext

	/**
	 * Add variables and constants to the velocity context to render an editor
	 * for inputing and modifying optional metadata properties about a resource.
	 */
	private static void metadataGroupsIntoContext(SessionState state, Context context)
	{

		context.put("STRING", ResourcesMetadata.WIDGET_STRING);
		context.put("TEXTAREA", ResourcesMetadata.WIDGET_TEXTAREA);
		context.put("BOOLEAN", ResourcesMetadata.WIDGET_BOOLEAN);
		context.put("INTEGER", ResourcesMetadata.WIDGET_INTEGER);
		context.put("DOUBLE", ResourcesMetadata.WIDGET_DOUBLE);
		context.put("DATE", ResourcesMetadata.WIDGET_DATE);
		context.put("TIME", ResourcesMetadata.WIDGET_TIME);
		context.put("DATETIME", ResourcesMetadata.WIDGET_DATETIME);
		context.put("ANYURI", ResourcesMetadata.WIDGET_ANYURI);
		
		context.put("today", TimeService.newTime());
		
		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups != null && !metadataGroups.isEmpty())
		{
			context.put("metadataGroups", metadataGroups);
		}

	}	// metadataGroupsIntoContext
	
	/**
	 * initialize the metadata context
	 */
	private static void initMetadataContext(SessionState state)
	{
		// define MetadataSets map
		List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
		if(metadataGroups == null)
		{
			metadataGroups = new Vector();
			state.setAttribute(STATE_METADATA_GROUPS, metadataGroups);
		}
		// define DublinCore
		if(!metadataGroups.contains(new MetadataGroup("Optional properties")))
		{
			MetadataGroup dc = new MetadataGroup("Optional properties");
			// dc.add(ResourcesMetadata.PROPERTY_DC_TITLE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_DESCRIPTION);
			dc.add(ResourcesMetadata.PROPERTY_DC_ALTERNATIVE);
			dc.add(ResourcesMetadata.PROPERTY_DC_CREATOR);
			dc.add(ResourcesMetadata.PROPERTY_DC_PUBLISHER);
			dc.add(ResourcesMetadata.PROPERTY_DC_SUBJECT);
			dc.add(ResourcesMetadata.PROPERTY_DC_CREATED);
			dc.add(ResourcesMetadata.PROPERTY_DC_ISSUED);
			// dc.add(ResourcesMetadata.PROPERTY_DC_MODIFIED);
			// dc.add(ResourcesMetadata.PROPERTY_DC_TABLEOFCONTENTS);
			dc.add(ResourcesMetadata.PROPERTY_DC_ABSTRACT);
			dc.add(ResourcesMetadata.PROPERTY_DC_CONTRIBUTOR);
			// dc.add(ResourcesMetadata.PROPERTY_DC_TYPE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_FORMAT);
			// dc.add(ResourcesMetadata.PROPERTY_DC_IDENTIFIER);
			// dc.add(ResourcesMetadata.PROPERTY_DC_SOURCE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_LANGUAGE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_COVERAGE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_RIGHTS);
			dc.add(ResourcesMetadata.PROPERTY_DC_AUDIENCE);
			dc.add(ResourcesMetadata.PROPERTY_DC_EDULEVEL);
			metadataGroups.add(dc);
			state.setAttribute(STATE_METADATA_GROUPS, metadataGroups);
		}
		/*
		// define DublinCore
		if(!metadataGroups.contains(new MetadataGroup("Test of Datatypes")))
		{
			MetadataGroup dc = new MetadataGroup("Test of Datatypes");
			dc.add(ResourcesMetadata.PROPERTY_DC_TITLE);
			dc.add(ResourcesMetadata.PROPERTY_DC_DESCRIPTION);
			dc.add(ResourcesMetadata.PROPERTY_DC_ANYURI);
			dc.add(ResourcesMetadata.PROPERTY_DC_DOUBLE);
			dc.add(ResourcesMetadata.PROPERTY_DC_DATETIME);
			dc.add(ResourcesMetadata.PROPERTY_DC_TIME);
			dc.add(ResourcesMetadata.PROPERTY_DC_DATE);
			dc.add(ResourcesMetadata.PROPERTY_DC_BOOLEAN);
			dc.add(ResourcesMetadata.PROPERTY_DC_INTEGER);
			metadataGroups.add(dc);
			state.setAttribute(STATE_METADATA_GROUPS, metadataGroups);
		}
		*/	
	}
	
	/**
	 * Internal class that encapsulates all information about a resource that is needed in the browse mode
	 */
	public static class BrowseItem
	{
		// attributes of all resources
		protected String m_name;
		protected String m_id;
		protected String m_type;
		protected boolean m_canRead;
		protected boolean m_canRevise;
		protected boolean m_canDelete;
		protected boolean m_canCopy;
		protected boolean m_isCopied;
		protected boolean m_canAddItem;
		protected boolean m_canAddFolder;

		protected List m_members;
		protected boolean m_isEmpty;
		protected boolean m_isHighlighted;
		protected boolean m_inheritsHighlight;
		protected String m_createdBy;
		protected String m_createdTime;
		protected String m_modifiedBy;
		protected String m_modifiedTime;
		protected String m_size;
		protected String m_target;
		protected String m_container;
		protected String m_root;
		protected int m_depth;
		protected boolean m_hasDeletableChildren;
		protected boolean m_hasCopyableChildren;
		protected boolean m_copyrightAlert;
		protected String m_url;
		protected boolean m_isLocal;
		protected boolean m_isAttached;
		private boolean m_isMoved;
		private boolean m_canUpdate;
		
				
		/**
		 * @param id
		 * @param name
		 * @param type
		 */
		public BrowseItem(String id, String name, String type)
		{
			m_name = name;
			m_id = id;
			m_type = type;
			
			// set defaults
			m_members = new LinkedList();
			m_canRead = false;
			m_canRevise = false;
			m_canDelete = false;
			m_canCopy = false;
			m_isEmpty = true;
			m_isCopied = false;
			m_isMoved = false;
			m_isAttached = false;
			m_hasDeletableChildren = false;
			m_hasCopyableChildren = false;
			m_createdBy = "";
			m_modifiedBy = "";
			// m_createdTime = TimeService.newTime().toStringLocalDate();
			// m_modifiedTime = TimeService.newTime().toStringLocalDate();
			m_size = "";
			m_depth = 0;
			m_copyrightAlert = false;
			m_url = "";
			m_target = "";
			m_root = "";
			
			m_isHighlighted = false;
			m_inheritsHighlight = false;
			
			m_canAddItem = false;
			m_canAddFolder = false;
			m_canUpdate = false;
		}
		
		/**
		 * @param name
		 */
		public void setName(String name)
		{
			m_name = name;
		}
		
		/**
		 * @param root
		 */
		public void setRoot(String root)
		{
			m_root = root;
		}
		
		/**
		 * @return
		 */
		public String getRoot()
		{
			return m_root;
		}
		
		/**
		 * @return
		 */
		public List getMembers()
		{
			List rv = new LinkedList();
			if(m_members != null)
			{
				rv.addAll(m_members);
			}
			return rv;
		}
		
		/**
		 * @param members
		 */
		public void addMembers(Collection members)
		{
			if(m_members == null)
			{
				m_members = new LinkedList();
			}
			m_members.addAll(members);
		}
		
		/**
		 * @return
		 */
		public boolean canAddItem()
		{
			return m_canAddItem;
		}

		/**
		 * @return
		 */
		public boolean canDelete()
		{
			return m_canDelete;
		}

		/**
		 * @return
		 */
		public boolean canRead()
		{
			return m_canRead;
		}

		/**
		 * @return
		 */
		public boolean canRevise()
		{
			return m_canRevise;
		}

		/**
		 * @return
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @return
		 */
		public String getName()
		{
			return m_name;
		}

		/**
		 * @return
		 */
		public int getDepth()
		{
			return m_depth;
		}

		/**
		 * @param depth
		 */
		public void setDepth(int depth)
		{
			m_depth = depth;
		}

		/**
		 * @param canCreate
		 */
		public void setCanAddItem(boolean canAddItem)
		{
			m_canAddItem = canAddItem;
		}

		/**
		 * @param canDelete
		 */
		public void setCanDelete(boolean canDelete)
		{
			m_canDelete = canDelete;
		}

		/**
		 * @param canRead
		 */
		public void setCanRead(boolean canRead)
		{
			m_canRead = canRead;
		}

		/**
		 * @param canRevise
		 */
		public void setCanRevise(boolean canRevise)
		{
			m_canRevise = canRevise;
		}

		/**
		 * @return
		 */
		public boolean isFolder()
		{
			return TYPE_FOLDER.equals(m_type);
		}

		/**
		 * @return
		 */
		public String getType()
		{
			return m_type;
		}

		/**
		 * @return
		 */
		public boolean canAddFolder()
		{
			return m_canAddFolder;
		}

		/**
		 * @param b
		 */
		public void setCanAddFolder(boolean canAddFolder)
		{
			m_canAddFolder = canAddFolder;
		}

		/**
		 * @return
		 */
		public boolean canCopy()
		{
			return m_canCopy;
		}

		/**
		 * @param canCopy
		 */
		public void setCanCopy(boolean canCopy)
		{
			m_canCopy = canCopy;
		}

		/**
		 * @return
		 */
		public boolean hasCopyrightAlert()
		{
			return m_copyrightAlert;
		}

		/**
		 * @param copyrightAlert
		 */
		public void setCopyrightAlert(boolean copyrightAlert)
		{
			m_copyrightAlert = copyrightAlert;
		}

		/**
		 * @return
		 */
		public String getUrl()
		{
			return m_url;
		}

		/**
		 * @param url
		 */
		public void setUrl(String url)
		{
			m_url = url;
		}

		/**
		 * @return
		 */
		public boolean isCopied()
		{
			return m_isCopied;
		}

		/**
		 * @param isCopied
		 */
		public void setCopied(boolean isCopied)
		{
			m_isCopied = isCopied;
		}

		/**
		 * @return
		 */
		public boolean isMoved()
		{
			return m_isMoved;
		}

		/**
		 * @param isCopied
		 */
		public void setMoved(boolean isMoved)
		{
			m_isMoved = isMoved;
		}

		/**
		 * @return
		 */
		public String getCreatedBy()
		{
			return m_createdBy;
		}

		/**
		 * @return
		 */
		public String getCreatedTime()
		{
			return m_createdTime;
		}

		/**
		 * @return
		 */
		public String getModifiedBy()
		{
			return m_modifiedBy;
		}

		/**
		 * @return
		 */
		public String getModifiedTime()
		{
			return m_modifiedTime;
		}

		/**
		 * @return
		 */
		public String getSize()
		{
			if(m_size == null)
			{
				m_size = "";
			}
			return m_size;
		}

		/**
		 * @param creator
		 */
		public void setCreatedBy(String creator)
		{
			m_createdBy = creator;
		}

		/**
		 * @param time
		 */
		public void setCreatedTime(String time)
		{
			m_createdTime = time;
		}

		/**
		 * @param modifier
		 */
		public void setModifiedBy(String modifier)
		{
			m_modifiedBy = modifier;
		}

		/**
		 * @param time
		 */
		public void setModifiedTime(String time)
		{
			m_modifiedTime = time;
		}

		/**
		 * @param size
		 */
		public void setSize(String size)
		{
			m_size = size;
		}

		/**
		 * @return
		 */
		public String getTarget()
		{
			return m_target;
		}

		/**
		 * @param target
		 */
		public void setTarget(String target)
		{
			m_target = target;
		}

		/**
		 * @return
		 */
		public boolean isEmpty()
		{
			return m_isEmpty;
		}

		/**
		 * @param isEmpty
		 */
		public void setIsEmpty(boolean isEmpty)
		{
			m_isEmpty = isEmpty;
		}

		/**
		 * @return
		 */
		public String getContainer()
		{
			return m_container;
		}

		/**
		 * @param container
		 */
		public void setContainer(String container)
		{
			m_container = container;
		}

		public void setIsLocal(boolean isLocal)
		{
			m_isLocal = isLocal;
		}
		
		public boolean isLocal()
		{
			return m_isLocal;
		}
		
		/**
		 * @return Returns the isAttached.
		 */
		public boolean isAttached() 
		{
			return m_isAttached;
		}
		/**
		 * @param isAttached The isAttached to set.
		 */
		public void setAttached(boolean isAttached) 
		{
			this.m_isAttached = isAttached;
		}

		/**
		 * @return Returns the hasCopyableChildren.
		 */
		public boolean hasCopyableChildren() 
		{
			return m_hasCopyableChildren;
		}

		/**
		 * @param hasCopyableChildren The hasCopyableChildren to set.
		 */
		public void setCopyableChildren(boolean hasCopyableChildren) 
		{
			this.m_hasCopyableChildren = hasCopyableChildren;
		}

		/**
		 * @return Returns the hasDeletableChildren.
		 */
		public boolean hasDeletableChildren() 
		{
			return m_hasDeletableChildren;
		}

		/**
		 * @param hasDeletableChildren The hasDeletableChildren to set.
		 */
		public void seDeletableChildren(boolean hasDeletableChildren) 
		{
			this.m_hasDeletableChildren = hasDeletableChildren;
		}

		/**
		 * @return Returns the canUpdate.
		 */
		public boolean canUpdate() 
		{
			return m_canUpdate;
		}

		/**
		 * @param canUpdate The canUpdate to set.
		 */
		public void setCanUpdate(boolean canUpdate) 
		{
			m_canUpdate = canUpdate;
		}
		
		public void setHighlighted(boolean isHighlighted)
		{
			m_isHighlighted = isHighlighted;
		}
		
		public boolean isHighlighted()
		{
			return m_isHighlighted;
		}
		
		public void setInheritsHighlight(boolean inheritsHighlight)
		{
			m_inheritsHighlight = inheritsHighlight;
		}
		
		public boolean inheritsHighlighted()
		{
			return m_inheritsHighlight;
		}
				
	}	// inner class BrowseItem
	
	
	/**
	 * Inner class encapsulates information about resources (folders and items) for editing
	 */
	public static class EditItem
		extends BrowseItem
	{
		protected String m_copyrightStatus;
		protected String m_copyrightInfo;
		// protected boolean m_copyrightAlert;
		protected boolean m_pubview;
		protected boolean m_pubviewset;
		protected String m_filename;
		protected byte[] m_content;
		protected String m_mimetype;
		protected String m_description;
		protected Map m_metadata;
		protected boolean m_hasQuota;
		protected boolean m_canSetQuota;
		protected String m_quota;
		protected boolean m_isUrl;
		protected boolean m_contentHasChanged;
		protected int m_notification;

		protected String m_formtype;
		protected String m_rootname;
		protected Map m_structuredArtifact;
		protected List m_properties;

		protected Set m_metadataGroupsShowing;
		
		protected Set m_missingInformation;
		protected boolean m_hasBeenAdded;
		private ResourcesMetadata m_form;
		private boolean m_isBlank;
		
		
		/**
		 * @param id
		 * @param name
		 * @param type
		 */
		public EditItem(String id, String name, String type) 
		{
			super(id, name, type);
			m_contentHasChanged = false;
			m_metadata = new Hashtable();
			m_structuredArtifact = new Hashtable();
			m_metadataGroupsShowing = new HashSet();
			m_mimetype = type;
			m_content = null;
			m_notification = NotificationService.NOTI_NONE;
			m_hasQuota = false;
			m_canSetQuota = false;
			m_formtype = "";
			m_rootname = "";
			m_missingInformation = new HashSet();
			m_hasBeenAdded = false;
			m_properties = new Vector();
			m_isBlank = true;
			
		}
		
		/**
		 * Set marker indicating whether current item is a blank entry
		 * @param isBlank
		 */
		public void markAsBlank(boolean isBlank) 
		{
			m_isBlank = isBlank;
		}
		
		/**
		 * Access marker indicating whether current item is a blank entry
		 * @return true if current entry is blank, false otherwise
		 */
		public boolean isBlank()
		{
			return m_isBlank;
		}

		/**
		 * Change the root ResourcesMetadata object that defines the form for a Structured Artifact. 
		 * @param form
		 */
		public void setForm(ResourcesMetadata form) 
		{
			m_form = form;			
		}
		
		/**
		 * Access the root ResourcesMetadata object that defines the form for a Structured Artifact.
		 * @return the form.
		 */
		public ResourcesMetadata getForm()
		{
			return m_form;
		}

		/**
		 * @param properties
		 */
		public void setProperties(List properties) 
		{
			m_properties = properties;
			
		}
		
		public List getProperties()
		{
			return m_properties;
		}

		/**
		 * Replace current values of Structured Artifact with new values.
		 * @param map The new values.
		 */
		public void setValues(Map map) 
		{
			m_structuredArtifact = map;
			
		}

		/**
		 * Access the entire set of values stored in the Structured Artifact
		 * @return The set of values.
		 */
		public Map getValues()
		{
			return m_structuredArtifact;
			
		}

		/**
		 * @param id
		 * @param name
		 * @param type
		 */
		public EditItem(String type) 
		{
			this("", "", type);
		}
		/**
		 * @param id
		 */
		public void setId(String id)
		{
			m_id = id;
		}
		
		/**
		 * Show the indicated metadata group for the item 
		 * @param group
		 */
		public void showMetadataGroup(String group)
		{
			m_metadataGroupsShowing.add(group);
		}
		
		/**
		 * Hide the indicated metadata group for the item
		 * @param group
		 */
		public void hideMetadataGroup(String group)
		{
			m_metadataGroupsShowing.remove(group);
			m_metadataGroupsShowing.remove(Validator.escapeUrl(group));
		}
		
		/**
		 * Query whether the indicated metadata group is showing for the item
		 * @param group
		 * @return true if the metadata group is showing, false otherwise
		 */
		public boolean isGroupShowing(String group)
		{
			return m_metadataGroupsShowing.contains(group) || m_metadataGroupsShowing.contains(Validator.escapeUrl(group));
		}
		
		/**
		 * @return
		 */
		public boolean isFileUpload() 
		{
			return !isFolder() && !isUrl() && !isHtml() && !isPlaintext() && !isStructuredArtifact();
		}

		/**
		 * @param type
		 */
		public void setType(String type)
		{
			m_type = type;
		}
		
		/**
		 * @param mimetype
		 */
		public void setMimeType(String mimetype)
		{
			m_mimetype = mimetype;
		}
		
		/**
		 * @return
		 */
		public String getMimeType()
		{
			return m_mimetype;
		}
		
		/**
		 * @param formtype
		 */
		public void setFormtype(String formtype)
		{
			m_formtype = formtype;
		}
		
		/**
		 * @return
		 */
		public String getFormtype()
		{
			return m_formtype;
		}
		
		/**
		 * @return Returns the copyrightInfo.
		 */
		public String getCopyrightInfo() {
			return m_copyrightInfo;
		}
		/**
		 * @param copyrightInfo The copyrightInfo to set.
		 */
		public void setCopyrightInfo(String copyrightInfo) {
			m_copyrightInfo = copyrightInfo;
		}
		/**
		 * @return Returns the copyrightStatus.
		 */
		public String getCopyrightStatus() {
			return m_copyrightStatus;
		}
		/**
		 * @param copyrightStatus The copyrightStatus to set.
		 */
		public void setCopyrightStatus(String copyrightStatus) {
			m_copyrightStatus = copyrightStatus;
		}
		/**
		 * @return Returns the description.
		 */
		public String getDescription() {
			return m_description;
		}
		/**
		 * @param description The description to set.
		 */
		public void setDescription(String description) {
			m_description = description;
		}
		/**
		 * @return Returns the filename.
		 */
		public String getFilename() {
			return m_filename;
		}
		/**
		 * @param filename The filename to set.
		 */
		public void setFilename(String filename) {
			m_filename = filename;
		}
		/**
		 * @return Returns the metadata.
		 */
		public Map getMetadata() {
			return m_metadata;
		}
		/**
		 * @param metadata The metadata to set.
		 */
		public void setMetadata(Map metadata) {
			m_metadata = metadata;
		}
		/**
		 * @param name
		 * @param value
		 */
		public void setMetadataItem(String name, Object value)
		{
			m_metadata.put(name, value);
		}
		/**
		 * @return Returns the pubview.
		 */
		public boolean isPubview() {
			return m_pubview;
		}
		/**
		 * @param pubview The pubview to set.
		 */
		public void setPubview(boolean pubview) {
			m_pubview = pubview;
		}
		/**
		 * @return Returns the pubviewset.
		 */
		public boolean isPubviewset() {
			return m_pubviewset;
		}
		/**
		 * @param pubviewset The pubviewset to set.
		 */
		public void setPubviewset(boolean pubviewset) {
			m_pubviewset = pubviewset;
		}
		/**
		 * @return Returns the content.
		 */
		public byte[] getContent() {
			return m_content;
		}
		/**
		 * @return Returns the content as a String.
		 */
		public String getContentstring() 
		{
			String rv = "";
			if(m_content != null && m_content.length > 0)
			{
				rv = new String( m_content );
			}
			return rv;
		}
		/**
		 * @param content The content to set.
		 */
		public void setContent(byte[] content) {
			m_content = content;
		}
		/**
		 * @param content The content to set.
		 */
		public void setContent(String content) {
			try
			{
				m_content = content.getBytes("UTF-8");
			}
			catch(UnsupportedEncodingException e)
			{
				m_content = content.getBytes();
			}
		}
		/**
		 * @return Returns the canSetQuota.
		 */
		public boolean canSetQuota() {
			return m_canSetQuota;
		}
		/**
		 * @param canSetQuota The canSetQuota to set.
		 */
		public void setCanSetQuota(boolean canSetQuota) {
			m_canSetQuota = canSetQuota;
		}
		/**
		 * @return Returns the hasQuota.
		 */
		public boolean hasQuota() {
			return m_hasQuota;
		}
		/**
		 * @param hasQuota The hasQuota to set.
		 */
		public void setHasQuota(boolean hasQuota) {
			m_hasQuota = hasQuota;
		}
		/**
		 * @return Returns the quota.
		 */
		public String getQuota() {
			return m_quota;
		}
		/**
		 * @param quota The quota to set.
		 */
		public void setQuota(String quota) {
			m_quota = quota;
		}
		/**
		 * @return true if content-type of item indicates it represents a URL, false otherwise
		 */
		public boolean isUrl()
		{
			return TYPE_URL.equals(m_type) || ResourceProperties.TYPE_URL.equals(m_mimetype);
		}
		/**
		 * @return true if content-type of item indicates it represents a URL, false otherwise
		 */
		public boolean isStructuredArtifact()
		{
			return TYPE_FORM.equals(m_type);
		}
		/**
		 * @return true if content-type of item is "text/text" (plain text), false otherwise
		 */
		public boolean isPlaintext()
		{
			return MIME_TYPE_DOCUMENT_PLAINTEXT.equals(m_mimetype) || MIME_TYPE_DOCUMENT_PLAINTEXT.equals(m_type);
		}
		/**
		 * @return true if content-type of item is "text/html" (an html document), false otherwise
		 */
		public boolean isHtml()
		{
			return MIME_TYPE_DOCUMENT_HTML.equals(m_mimetype) || MIME_TYPE_DOCUMENT_HTML.equals(m_type);
		}
		
		public boolean contentHasChanged()
		{
			return m_contentHasChanged;
		}
		
		public void setContentHasChanged(boolean changed)
		{
			m_contentHasChanged = changed;
		}
		
		public void setNotification(int notification)
		{
			m_notification = notification;
		}
		
		public int getNotification()
		{
			return m_notification;
		}
		
		/**
		 * @return Returns the artifact.
		 */
		public Map getStructuredArtifact() 
		{
			return m_structuredArtifact;
		}
		/**
		 * @param artifact The artifact to set.
		 */
		public void setStructuredArtifact(Map artifact) 
		{
			this.m_structuredArtifact = artifact;
		}
		/**
		 * @param name
		 * @param value
		 */
		public void setValue(String name, Object value)
		{
			setValue(name, 0, value);
		}
		/**
		 * @param name
		 * @param index
		 * @param value
		 */
		public void setValue(String name, int index, Object value)
		{
			List list = getList(name);
			try
			{
				list.set(index, value);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				list.add(value);
			}
			m_structuredArtifact.put(name, list);
		}
		/**
		 * Access a value of a structured artifact field of type String.
		 * @param name	The name of the field to access.
		 * @return the value, or null if the named field is null or not a String.
		 */
		public String getString(String name)
		{
			if(m_structuredArtifact == null)
			{
				m_structuredArtifact = new Hashtable();
			}
			Object value = m_structuredArtifact.get(name);
			String rv = "";
			if(value == null)
			{
				// do nothing
			}
			else if(value instanceof String)
			{
				rv = (String) value;
			}
			else
			{
				rv = value.toString();
			}
			return rv;
		}

		public Object getValue(String name, int index)
		{
			List list = getList(name);
			Object rv = null;
			try
			{
				rv = list.get(index);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				// return null
			}
			return rv;
			
		}
		
		/**
		 * Access a particular value in a Structured Artifact, as identified by the parameter "name".  This
		 * implementation of the method assumes that the name is a series of String identifiers delimited 
		 * by the ResourcesAction.ResourcesMetadata.DOT String. 
		 * @param name The delimited identifier for the item.
		 * @return The value identified by the name, or null if the name does not identify a valid item.
		 */
		public Object getValue(String name)
		{
			String[] names = name.split(ResourcesMetadata.DOT);
			Object rv = m_structuredArtifact;
			if(rv != null && (rv instanceof Map) && ((Map) rv).isEmpty())
			{
				rv = null;
			}
			for(int i = 1; rv != null && i < names.length; i++)
			{
				if(rv instanceof Map)
				{
					rv = ((Map) rv).get(names[i]);
				}
				else
				{
					rv = null;
				}
			}
			return rv;
			
		}

		/**
		 * Access a list of values associated with a named property of a structured artifact.
		 * @param name The name of the property.
		 * @return The list of values associated with that name, or an empty list if the property is not defined.
		 */
		public List getList(String name)
		{
			if(m_structuredArtifact == null)
			{
				m_structuredArtifact = new Hashtable();
			}
			Object value = m_structuredArtifact.get(name);
			List rv = new Vector();
			if(value == null)
			{
				m_structuredArtifact.put(name, rv);
			}
			else if(value instanceof Collection)
			{
				rv.addAll((Collection)value);
			}
			else
			{
				rv.add(value);
			}
			return rv;
			
		}
		
		/**
		 * @return
		 */
		/*
		public Element exportStructuredArtifact(List properties)
		{
			return null;
		}
		*/
	
		/**
		 * @return Returns the name of the root of a structured artifact definition.
		 */
		public String getRootname() 
		{
			return m_rootname;
		}
		/**
		 * @param rootname The name to be assigned for the root of a structured artifact.
		 */
		public void setRootname(String rootname) 
		{
			m_rootname = rootname;
		}
		
		/**
		 * Add a property name to the list of properties missing from the input. 
		 * @param propname The name of the property.
		 */
		public void setMissing(String propname)
		{
			m_missingInformation.add(propname);
		}
		
		/**
		 * Query whether a particular property is missing
		 * @param propname The name of the property
		 * @return The value "true" if the property is missing, "false" otherwise.
		 */
		public boolean isMissing(String propname)
		{
			return m_missingInformation.contains(propname) || m_missingInformation.contains(Validator.escapeUrl(propname));
		}
		
		/**
		 * Empty the list of missing properties.
		 */
		public void clearMissing()
		{
			m_missingInformation.clear();
		}
		
		public void setAdded(boolean added)
		{
			m_hasBeenAdded = added;
		}
		
		public boolean hasBeenAdded()
		{
			return m_hasBeenAdded;
		}

		
	}	// inner class EditItem
	
	
	/**
	 * Inner class encapsulates information about folders (and final item?) in a collection path (a.k.a. breadcrumb)
	 */
	public static class PathItem
	{
		protected String m_url;
		protected String m_name;
		protected String m_id;
		protected boolean m_canRead;
		protected boolean m_isFolder;
		protected boolean m_isLast;
		protected String m_root;
		protected boolean m_isLocal;
		
		public PathItem(String id, String name)
		{
			m_id = id;
			m_name = name;
			m_canRead = false;
			m_isFolder = false;
			m_isLast = false;
			m_url = "";
			m_isLocal = true;
		}
		
		/**
		 * @return
		 */
		public boolean canRead()
		{
			return m_canRead;
		}

		/**
		 * @return
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @return
		 */
		public boolean isFolder()
		{
			return m_isFolder;
		}

		/**
		 * @return
		 */
		public boolean isLast()
		{
			return m_isLast;
		}

		/**
		 * @return
		 */
		public String getName()
		{
			return m_name;
		}

		/**
		 * @param canRead
		 */
		public void setCanRead(boolean canRead)
		{
			m_canRead = canRead;
		}

		/**
		 * @param id
		 */
		public void setId(String id)
		{
			m_id = id;
		}

		/**
		 * @param isFolder
		 */
		public void setIsFolder(boolean isFolder)
		{
			m_isFolder = isFolder;
		}

		/**
		 * @param isLast
		 */
		public void setLast(boolean isLast)
		{
			m_isLast = isLast;
		}

		/**
		 * @param name
		 */
		public void setName(String name)
		{
			m_name = name;
		}

		/**
		 * @return
		 */
		public String getUrl()
		{
			return m_url;
		}

		/**
		 * @param url
		 */
		public void setUrl(String url)
		{
			m_url = url;
		}

		/**
		 * @param root
		 */
		public void setRoot(String root)
		{
			m_root = root;
		}
		
		/**
		 * @return
		 */
		public String getRoot()
		{
			return m_root;
		}
		
		public void setIsLocal(boolean isLocal)
		{
			m_isLocal = isLocal;
		}
		
		public boolean isLocal()
		{
			return m_isLocal;
		}
		
	}	// inner class PathItem
	
	/**
	 * 
	 * inner class encapsulates information about groups of metadata tags (such as DC, LOM, etc.)
	 * 
	 */
	public static class MetadataGroup
		extends Vector
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -821054142728929236L;
		protected String m_name;
		protected boolean m_isShowing;
		
		/**
		 * @param name
		 */
		public MetadataGroup(String name)
		{
			super();
			m_name = name;
			m_isShowing = false;
		}

		/**
		 * @return
		 */
		public boolean isShowing()
		{
			return m_isShowing;
		}

		/**
		 * @param isShowing
		 */
		public void setShowing(boolean isShowing)
		{
			m_isShowing = isShowing;
		}
		

		/**
		 * @return
		 */
		public String getName()
		{
			return m_name;
		}

		/**
		 * @param name
		 */
		public void setName(String name)
		{
			m_name = name;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 * needed to determine List.contains()
		 */
		public boolean equals(Object obj)
		{
			MetadataGroup mg = (MetadataGroup) obj;
			boolean rv = (obj != null) && (m_name.equals(mg));
			return rv;
		}

	}

	public static class AttachItem
	{
		protected String m_id;
		protected String m_displayName;
		protected String m_accessUrl;
		protected String m_collectionId;
		protected String m_contentType;

		/**
		 * @param id
		 * @param displayName
		 * @param collectionId
		 * @param accessUrl
		 */
		public AttachItem(String id, String displayName, String collectionId, String accessUrl)
		{
			m_id = id;
			m_displayName = displayName;
			m_collectionId = collectionId;
			m_accessUrl = accessUrl;
			
		}
		
		/**
		 * @return Returns the accessUrl.
		 */
		public String getAccessUrl() 
		{
			return m_accessUrl;
		}
		/**
		 * @param accessUrl The accessUrl to set.
		 */
		public void setAccessUrl(String accessUrl) 
		{
			m_accessUrl = accessUrl;
		}
		/**
		 * @return Returns the collectionId.
		 */
		public String getCollectionId() 
		{
			return m_collectionId;
		}
		/**
		 * @param collectionId The collectionId to set.
		 */
		public void setCollectionId(String collectionId) 
		{
			m_collectionId = collectionId;
		}
		/**
		 * @return Returns the id.
		 */
		public String getId() 
		{
			return m_id;
		}
		/**
		 * @param id The id to set.
		 */
		public void setId(String id) 
		{
			m_id = id;
		}
		/**
		 * @return Returns the name.
		 */
		public String getDisplayName() 
		{
			return m_displayName;
		}
		/**
		 * @param name The name to set.
		 */
		public void setDisplayName(String name) 
		{
			m_displayName = name;
		}
		
		/**
		 * @return Returns the contentType.
		 */
		public String getContentType() 
		{
			return m_contentType;
		}
		
		/**
		 * @param contentType
		 */
		public void setContentType(String contentType) 
		{
			this.m_contentType = contentType;
			
		}
		
	}	// Inner class AttachItem
	
	public static class ElementCarrier
	{
		protected Element element;
		protected String parent;
		
		public ElementCarrier(Element element, String parent)
		{
			this.element = element;
			this.parent = parent;
			
		}
		
		public Element getElement() 
		{
			return element;
		}
		
		public void setElement(Element element) 
		{
			this.element = element;
		}
		
		public String getParent() 
		{
			return parent;
		}
		
		public void setParent(String parent) 
		{
			this.parent = parent;
		}

	}
	
	public static class SaveArtifactAttempt
	{
		protected EditItem item;
		protected List errors;
		protected SchemaNode schema;
		
		public SaveArtifactAttempt(EditItem item, SchemaNode schema) 
		{
			this.item = item;
			this.schema = schema;
		}
		
		/**
		 * @return Returns the errors.
		 */
		public List getErrors() 
		{
			return errors;
		}

		/**
		 * @param errors The errors to set.
		 */
		public void setErrors(List errors) 
		{
			this.errors = errors;
		}
		
		/**
		 * @return Returns the item.
		 */
		public EditItem getItem() 
		{
			return item;
		}
		
		/**
		 * @param item The item to set.
		 */
		public void setItem(EditItem item)
		{
			this.item = item;
		}
		
		/**
		 * @return Returns the schema.
		 */
		public SchemaNode getSchema() 
		{
			return schema;
		}
		
		/**
		 * @param schema The schema to set.
		 */
		public void setSchema(SchemaNode schema) 
		{
			this.schema = schema;
		}
		
	}
	
}	// ResourcesAction
