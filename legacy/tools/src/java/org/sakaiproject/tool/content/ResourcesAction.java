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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuDivider;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.metaobj.shared.control.SchemaBean;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.RoleEdit;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.SiteService.SortType;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.tool.helper.PermissionsAction;
import org.sakaiproject.util.ContentHostingComparator;
import org.sakaiproject.util.FileItem;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
	private static final String RESOURCES_MODE_RESOURCES = "resources";
	private static final String RESOURCES_MODE_DROPBOX = "dropbox";
	
	/** opened collection list */
	private static final String EXPANDED_COLLECTIONS = "expanded_collections";
	
	/** The null/empty string */
	private static final String NULL_STRING = "";

	/** The string used when pasting the same resource to the same folder */
	private static final String DUPLICATE_STRING = rb.getString("copyof");
	
	/** The string used when pasting shirtcut of the same resource to the same folder */
	private static final String SHORTCUT_STRING = rb.getString("shortcut");

	/** The copyright character (Note: could be "\u00a9" if we supported UNICODE for specials -ggolden */
	private static final String COPYRIGHT_SYMBOL = rb.getString("cpright1");

	/** The String of new copyright */
	private static final String NEW_COPYRIGHT = "newcopyright";

	/** The resource not exist string */
	private static final String RESOURCE_NOT_EXIST_STRING = rb.getString("notexist1");
	
	/** The title invalid string */
	private static final String RESOURCE_INVALID_TITLE_STRING = rb.getString("titlecannot");
	
	/** The copy, cut, paste not operate on collection string */
	private static final String RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING = rb.getString("notsupported");
	
	/** The multi max add number */
	private static final int MULTI_ADD_NUMBER = 10;
	
	/** State attribute for state initialization.  */
	private static final String STATE_INITIALIZED = "resources.initialized";

	/** The content hosting service in the State. */
	private static final String STATE_CONTENT_SERVICE = "resources.content_service";

	/** The resources or dropbox mode. */
	private static final String STATE_RESOURCES_MODE = "resources.resources_mode";

	/** The content type image lookup service in the State. */
	private static final String STATE_CONTENT_TYPE_IMAGE_SERVICE = "resources.content_type_image_service";

	/** The user copyright string */
	private static final String	STATE_MY_COPYRIGHT = "resources.mycopyright";

	/** copyright path -- MUST have same value as AccessServlet.COPYRIGHT_PATH */
	public static final String COPYRIGHT_PATH = Resource.SEPARATOR + "copyright";	

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

	/** The select all flag */
	private static final String STATE_SELECT_ALL_FLAG = "resources.select_all_flag";

	/** The expand all flag */
	private static final String STATE_EXPAND_ALL_FLAG = "resources.expand_all_flag";

	/** The from state name */
	private static final String STATE_FROM = "resources.from";
	
	/** The root of the navigation breadcrumbs for a folder, either the home or another site the user belongs to */
	private static final String STATE_NAVIGATION_ROOT = "resources.navigation_root";

	/************** the add file context *****************************************/
	
	/** The add file number */
	private static final String STATE_ADD_FILE_NUMBER = "resources.add_file_number";

	/** The add file full name, including the OS specific path info */
	private static final String STATE_ADD_FILE_OS_NAME = "resources.add_file_os_name";

	/** The add file title */
	private static final String STATE_ADD_FILE_TITLE = "resources.add_file_title";

	/** The id of the folder to which the file is to be added */
	private static final String STATE_ADD_FILE_COLLECTION_ID = "resources.add_file_collection";

	/** The add file content: vector of byte[] */
	private static final String STATE_ADD_FILE_CONTENT = "resources.add_file_content";

	/** The add file content type */
	private static final String STATE_ADD_FILE_CONTENT_TYPE = "resources.add_file_content_type";

	/** The add file copyright */
	private static final String STATE_ADD_FILE_COPYRIGHT = "resources.add_file_copyright";

	/** The add file new copyright */
	private static final String STATE_ADD_FILE_NEW_COPYRIGHT = "resources.add_file_new_copyright";
	
	/** Show a copyright alert or not when opening the file */
	private static final String STATE_ADD_FILE_COPYRIGHT_ALERT = "resources.add_file_copyright_alert";

	/** The add file description */
	private static final String STATE_ADD_FILE_DESCRIPTION = "resources.add_file_description";
	
	/** The add file pubview */
	private static final String STATE_ADD_FILE_PUBVIEW = "resources.add_file_pubview";
	

	/************** the add folder context *****************************************/

	/** The add folder number */
	private static final String STATE_ADD_FOLDER_NUMBER = "resources.add_folder_number";

	/** The add folder title */
	private static final String STATE_ADD_FOLDER_TITLE = "resources.add_folder_title";

	/** The folder to which the folder is to be added */
	private static final String STATE_ADD_FOLDER_COLLECTION_ID = "resources.add_folder_collection";

	/** The add folder description */
	private static final String STATE_ADD_FOLDER_DESCRIPTION = "resources.add_folder_description";

	/** The add folder copyright */
	private static final String STATE_ADD_FOLDER_COPYRIGHT = "resources.add_folder_copyright";
	
	/** The add folder pubview */
	private static final String STATE_ADD_FOLDER_PUBVIEW = "resources.add_folder_pubview";

	/************** the add document context *****************************************/

	/** The add document content */
	private static final String STATE_ADD_DOCUMENT_CONTENT = "resources.add_document_content";

	/** The add document copyright */
	private static final String STATE_ADD_DOCUMENT_COPYRIGHT = "resources.add_document_copyright";
	
	/** The add document copyright alert */
	private static final String STATE_ADD_DOCUMENT_COPYRIGHT_ALERT = "resources.add_document_copyright_alert";

	/** The add document description */
	private static final String STATE_ADD_DOCUMENT_DESCRIPTION = "resources.add_document_description";

	/** The add document new copyright */
	private static final String STATE_ADD_DOCUMENT_NEW_COPYRIGHT = "resources.add_document_new_copyright";

	/** The add document title*/
	private static final String STATE_ADD_DOCUMENT_TITLE = "resources.add_document_title";
	
	/** The add document pubview */
	private static final String STATE_ADD_DOCUMENT_PUBVIEW = "resources.add_document_pubview";

	/************** the add URL context *****************************************/

	/** The add URL number */
	private static final String STATE_ADD_URL_NUMBER = "resources.add_url_number";

	/** The add URL copyright*/
	private static final String STATE_ADD_URL_COPYRIGHT = "resources.add_url_copyright";
	
	/** The add URL copyright alert*/
	private static final String STATE_ADD_URL_COPYRIGHT_ALERT = "resources.add_url_copyright_alert";

	/** The add URL description */
	private static final String STATE_ADD_URL_DESCRIPTION = "resources.add_url_description";

	/** The add URL new copyright */
	private static final String STATE_ADD_URL_NEW_COPYRIGHT = "resources.add_url_new_copyright";

	/** The add URL title */
	private static final String STATE_ADD_URL_TITLE = "resources.add_url_title";

	/** The add URL URL */
	private static final String STATE_ADD_URL_URL = "resources.add_url_url";

	/** The add url pubview */
	private static final String STATE_ADD_URL_PUBVIEW = "resources.add_url_pubview";
	
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
	private static final String STATE_EDIT_ID = "resources.edit_id";
	private static final String STATE_EDIT_ITEM = "resources.edit_item";
	private static final String STATE_EDIT_COLLECTION_ID = "resources.edit_collection_id";
	private static final String STATE_EDIT_INTENT = "resources.properties_intent";

	/************** the create contexts *****************************************/

	private static final String STATE_CREATE_TYPE = "resources.create_type";
	private static final String STATE_CREATE_ITEMS = "resources.create_items";
	private static final String STATE_CREATE_COLLECTION_ID = "resources.create_collection_id";
	private static final String STATE_CREATE_INTENT = "resources.create_intent";
	private static final String STATE_CREATE_NUMBER = "resources.create_number";
	private static final String STATE_CREATE_ALERTS = "resources.create_alerts";
	private static final String STATE_CREATE_MISSING_ITEM = "resources.create_missing_item";
	private static final String STATE_STRUCTOBJ_ROOTNAME = "resources.create_structured_object_root";
	private static final String STATE_STRUCTOBJ_TYPE = "resources.create_structured_object_type";
	private static final String STATE_STRUCTOBJ_HOMES = "resources.create_structured_object_home";
	private static final String STATE_STRUCTOBJ_PROPERTIES = "resources.create_structured_object_properties";
	
	private static final String TYPE_FOLDER = "folder";
	private static final String TYPE_UPLOAD = "file";
	private static final String TYPE_URL = "Url";
	private static final String TYPE_FORM = "StructuredArtifact";
	
	private static final int CREATE_MAX_ITEMS = 10;

	/************** the metadata extension of edit/create contexts *****************************************/

	private static final String STATE_METADATA_GROUPS = "resources.metadata.types";
	private static final String STATE_METADATA_GROUP = "resources.metadata.type";
	private static final String STATE_METADATA_PROPERTIES = "resources.metadata.properties";
	private static final String DUBLIN_CORE = "Dublin Core Metadata"; 
	
	private static final String INTENT_REVISE_FILE = "revise";
	private static final String INTENT_REPLACE_FILE = "replace";

	/************** the delete context *****************************************/

	/** The delete ids */
	private static final String STATE_DELETE_IDS = "resources.delete_ids";
	
	/** The not empty delete ids */
	private static final String STATE_NOT_EMPTY_DELETE_IDS = "resource.not_empty_delete_ids";

	/************** the replace context *****************************************/

	/** The replace ids */
	private static final String STATE_REPLACE_ID = "resources.replace_id";

	/** The replace file os names */
	private static final String STATE_REPLACE_OS_NAME = "resources.replace_os_name";

	/** The replace file titles */
	private static final String STATE_REPLACE_TITLE = "resources.replace_title";

	/** The replace file content */
	private static final String STATE_REPLACE_CONTENT = "resources.replace_content";

	/** The replace file contemt type */
	private static final String STATE_REPLACE_CONTENT_TYPE = "resources.replace_content_type";

	/************** the revise document context *****************************************/

	/** The revise document id */
	private static final String STATE_REVISE_ID = "resources.revise_id";

	/************** the cut items context *****************************************/

	/** The cut item ids */
	private static final String STATE_CUT_IDS = "resources.revise_cut_ids";

	/************** the copied items context *****************************************/

	/** The copied item ids */
	private static final String STATE_COPIED_IDS = "resources.revise_copied_ids";

	/** The copied item id */
	private static final String STATE_COPIED_ID = "resources.revise_copied_id";

	/** Modes. */
	private static final String MODE_LIST = "list";
	private static final String MODE_EDIT = "edit";
	private static final String MODE_DAV = "webdav";
	private static final String MODE_CREATE = "create";

	private static final String MODE_BROWSE = "show";
	private static final String MODE_ADD_ITEM = "add";
	private static final String MODE_ADD_FILE_BASIC = "addFileBasic";
	private static final String MODE_ADD_FILE_OPTIONS = "addFileOptions";
	private static final String MODE_ADD_FOLDER = "addFolder";
	private static final String MODE_ADD_URL = "addURL";
	private static final String MODE_ADD_DOCUMENT_PLAINTEXT = "addSimpleText";
	private static final String MODE_ADD_DOCUMENT_HTML = "addHTMLDocument";
	private static final String MODE_DElETE_CONFIRM = "deleteConfirm";
	private static final String MODE_MORE = "more";
	private static final String MODE_PROPERTIES = "properties";
	private static final String MODE_REPLACE = "replace";
	private static final String MODE_REVISE_DOCUMENT = "reviseSimpleText";
	private static final String MODE_DELETE_CONFIRM = "deleteConfirm";
	private static final String MODE_SHOW_COPYRIGHT_ALERT = "showcopyrightalert";

	/** vm files for each mode. */
	private static final String TEMPLATE_LIST = "_list";
	private static final String TEMPLATE_EDIT = "_edit";
	private static final String TEMPLATE_CREATE = "_create";
	private static final String TEMPLATE_DAV = "_webdav";

	private static final String TEMPLATE_MORE = "_more";
	private static final String TEMPLATE_DELETE_CONFIRM = "_deleteConfirm";

	// obsolete ?
	private static final String TEMPLATE_ADD_FILE_BASIC = "_addFileBasic";
	private static final String TEMPLATE_ADD_FOLDER = "_addFolderBasic";
	private static final String TEMPLATE_ADD_DOCUMENT_PLAINTEXT = "_addTextDocument";//"_addSimpleText";
	private static final String TEMPLATE_ADD_DOCUMENT_HTML = "_addHTMLDocument";
	private static final String TEMPLATE_ADD_URL = "_addURLBasic";

	// obsolete ?
	private static final String TEMPLATE_BROWSE = "_show";
	private static final String TEMPLATE_ADD_ITEM = "_addItem";
	private static final String TEMPLATE_ADD_FILE_OPTIONS = "_addFileOptions";
	private static final String TEMPLATE_PROPERTIES = "_properties";
	private static final String TEMPLATE_REPLACE = "_replace";
	
	/** the site title */
	private static final String STATE_SITE_TITLE = "site_title";
	
	private static final String MIME_TYPE_DOCUMENT_PLAINTEXT = "text/plain";
	private static final String MIME_TYPE_DOCUMENT_HTML = "text/html";
	
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

		if (mode.equals (MODE_BROWSE))
		{
//			// enable the observer when inside the list view
//			ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//			o.setDeliveryId(clientWindowId(state, portlet.getID()));
//			o.enable();	
			
			// build the context for the browse show
			template = buildBrowseContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_LIST))
		{
//			// enable the observer when inside the list view
//			ContentObservingCourier o = (ContentObservingCourier) state.getAttribute(STATE_OBSERVER);
//			o.setDeliveryId(clientWindowId(state, portlet.getID()));
//			o.enable();	
			
			// build the context for add item
			template = buildListContext (portlet, context, data, state);
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
		else if (mode.equals (MODE_ADD_ITEM))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for add item
			template = buildAddItemContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_FILE_BASIC))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the basic step of adding file
			template = buildAddFileBasicContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_FILE_OPTIONS))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the advanced step of adding file
			template = buildAddFileOptionsContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_FOLDER))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the basic step of adding folder
			template = buildAddFolderContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_DOCUMENT_PLAINTEXT))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the basic step of adding document
			template = buildAddDocumentPlaintextContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_DOCUMENT_HTML))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context for the basic step of adding document
			template = buildAddDocumentHtmlContext (portlet, context, data, state);
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
		else if (mode.equals (MODE_REPLACE))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context to display the file replacing interface
			template = buildReplaceContext (portlet, context, data, state);
		}
		else if (mode.equals (MODE_ADD_URL))
		{
//			// disable the observer when not inside the list view
//			((ContentObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
			
			// build the context of adding Urls
			template = buildAddUrlContext (portlet, context, data, state);
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

		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", contentService);
		
		boolean inMyWorkspace = SiteService.isUserSite(PortalService.getCurrentSiteId());
		context.put("inMyWorkspace", Boolean.toString(inMyWorkspace));
			
		/*
		// user
		String userId = UserDirectoryService.getCurrentUser().getId();
		Set realms = RealmService.unlockRealms(userId, "content.read");
		Iterator realmIt = realms.iterator();
		while(realmIt.hasNext())
		{
			String realmId = (String) realmIt.next();
			try
			{
				Realm realm = RealmService.getRealm(realmId);
				String ref = realm.getReference();
				String collId = ContentHostingService.getSiteCollection(ref);
				ContentCollection collection = ContentHostingService.getCollection(collId);
				if(collection != null)
				{
				}
			}
			catch(IdUnusedException e)
			{
			}
			catch(PermissionException e)
			{
			}
			catch(TypeException e)
			{
			}
		}
		*/
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
		if(atHome && SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
		{
			buildListMenu(portlet, context, data, state);
		}

		context.put("atHome", Boolean.toString(atHome));


		
		List cPath = getCollectionPath(state);
		context.put ("collectionPath", cPath);

		String testCollection = collectionId + "test/";
		String testResource = collectionId + "test";

		// set the sort values
		String sortedBy = (String) state.getAttribute (STATE_SORT_BY);
		String sortedAsc = (String) state.getAttribute (STATE_SORT_ASC);
		context.put ("currentSortedBy", sortedBy);
		context.put ("currentSortAsc", sortedAsc);
		context.put("TRUE", Boolean.TRUE.toString());
		
		try
		{
			contentService.checkCollection (collectionId);
			context.put ("collectionFlag", Boolean.TRUE.toString());

			String copyFlag = (String) state.getAttribute (STATE_COPY_FLAG);
			context.put ("copyFlag", copyFlag);
			if (copyFlag.equals (Boolean.TRUE.toString()))
			{
				context.put ("copiedItem", state.getAttribute (STATE_COPIED_ID));
			}

			context.put("expandallflag", state.getAttribute(STATE_EXPAND_ALL_FLAG));

			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			ContentCollection coll = contentService.getCollection(collectionId);
			expandedCollections.put(collectionId, coll);
			
			state.removeAttribute(STATE_PASTE_ALLOWED_FLAG);
			
			List roots = new Vector();
			List members = getBrowseItems(collectionId, expandedCollections, sortedBy, sortedAsc, (BrowseItem) null, navRoot.equals(homeCollectionId), state);
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
				roots.add(root);
			}
			
			if(atHome)
			{
				// get all of user's sites
				List mySites = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
						null, null, null,
						SortType.TITLE_ASC,
						new PagingPosition(1,50));
				
				// add user's MyWorkspace to beginning of list
				String userId = UserDirectoryService.getCurrentUser().getId();
				String workspaceId = SiteService.getUserSiteId(userId);
				mySites.add(0, SiteService.getSite(workspaceId));
				// find resources tools and add their root folders to the list of user's roots
				Iterator siteIt = mySites.iterator();
				while(siteIt.hasNext())
				{
					Site site = (Site) siteIt.next();
					List pages = site.getPages();
					Iterator pageIt = pages.iterator();
					while(pageIt.hasNext())
					{
						SitePage page = (SitePage) pageIt.next();
						List tools = page.getTools();
						Iterator toolIt = tools.iterator();
						while(toolIt.hasNext())
						{
							ToolConfiguration tool = (ToolConfiguration) toolIt.next();
							if(tool == null || tool.getTool() == null)
							{
								//
							}
							else if("sakai.dropbox".equals(tool.getTool().getId()))
							{
								String collId = Dropbox.getCollection(tool.getContext());
								if(! collectionId.equals(collId))
								{
									members = getBrowseItems(collId, expandedCollections, sortedBy, sortedAsc, (BrowseItem) null, false, state);
									if(members != null && members.size() > 0)
									{
										BrowseItem root = (BrowseItem) members.remove(0);
										root.addMembers(members);
										root.setName(site.getTitle() + " " + rb.getString("gen.drop"));
										roots.add(root);
									}
								}
							}
							else if("sakai.resources".equals(tool.getTool().getId()))
							{
								String collId = ContentHostingService.getSiteCollection(tool.getContext());
								if(! collectionId.equals(collId))
								{
									members = getBrowseItems(collId, expandedCollections, sortedBy, sortedAsc, (BrowseItem) null, false, state);
									if(members != null && members.size() > 0)
									{
										BrowseItem root = (BrowseItem) members.remove(0);
										root.addMembers(members);
										root.setName(site.getTitle() + " " + rb.getString("gen.reso"));
										roots.add(root);
									}
								}
							}
						}
					}
				}
			}
			context.put ("roots", roots);
			// context.put ("root", root);
			
			if(state.getAttribute(STATE_PASTE_ALLOWED_FLAG) != null)
			{
				context.put("paste_place_showing", state.getAttribute(STATE_PASTE_ALLOWED_FLAG));
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
		
		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		justDelivered(state);

		// pick the "show" template based on the standard template name
		String template = (String) getContext(data).get("template");
	
		return template + TEMPLATE_LIST;
		
	}	// buildListContext

	/**
	* Expand all the collection resources and put in EXPANDED_COLLECTIONS attribute.
	*/
	public void doList ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters();
		
		
		
		state.setAttribute (STATE_MODE, MODE_LIST);
		
	}	// doList


	/**
	* Build the context for the normal view
	*/
	public String buildBrowseContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		context.put("expandedCollections", state.getAttribute(EXPANDED_COLLECTIONS));
		
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));

		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", contentService);

		boolean atHome = false;

		// make sure the channedId is set
		String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
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

			
		// current browse collection id
		//String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		context.put ("collectionId", collectionId);

		Vector collectionPath = new Vector ();
		String currentCollectionId = collectionId;

		while ((contentService.getContainingCollectionId (currentCollectionId).length ()>0) && (!currentCollectionId.equals (state.getAttribute (STATE_HOME_COLLECTION_ID))))
		{
			String containingId = contentService.getContainingCollectionId (currentCollectionId);
			collectionPath.insertElementAt (containingId, 0);
			currentCollectionId = containingId;
		}

		state.setAttribute (STATE_COLLECTION_PATH, collectionPath);
		context.put ("collectionPath", collectionPath);

		String testCollection = collectionId + "test/";
		String testResource = collectionId + "test";

		// set the sort values
		String sortedBy = (String) state.getAttribute (STATE_SORT_BY);
		String sortedAsc = (String) state.getAttribute (STATE_SORT_ASC);
		context.put ("currentSortedBy", sortedBy);
		context.put ("currentSortAsc", sortedAsc);

		boolean allowAdd = (((contentService.allowAddCollection (testCollection))||(contentService.allowAddResource (testResource)))
							&& ((contentService.allowGetProperties (testCollection))||(contentService.allowGetProperties (testResource))));
		boolean allowReplace = (contentService.allowGetProperties (testResource) && (contentService.allowUpdateResource (testResource)));
		boolean allowRevise = (contentService.allowGetProperties (testResource) && (contentService.allowUpdateResource (testResource)) && (contentService.allowAddProperty (testResource)));
		boolean allowGetProperties = contentService.allowGetProperties (testResource);
		boolean allowRemove = false;
		
		// does the folder contain any item?
		boolean emptyFolder = true;
		try
		{
			contentService.checkCollection (collectionId);
			context.put ("collectionFlag", Boolean.TRUE.toString());

			String cutFlag = (String) state.getAttribute (STATE_CUT_FLAG);
			String copyFlag = (String) state.getAttribute (STATE_COPY_FLAG);
			context.put ("cutFlag", cutFlag);
			context.put ("copyFlag", copyFlag);
			if (cutFlag.equals (Boolean.TRUE.toString()))
			{
				context.put ("cutItems", state.getAttribute (STATE_CUT_IDS));
			}
			if (copyFlag.equals (Boolean.TRUE.toString()))
			{
				context.put ("copiedItems", state.getAttribute (STATE_COPIED_IDS));
			}

			context.put ("selectallflag", state.getAttribute (STATE_SELECT_ALL_FLAG));
			context.put("expandallflag", state.getAttribute(STATE_EXPAND_ALL_FLAG));

			HashMap expandedCollections = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
			
			List members = getShowMembers(collectionId, expandedCollections, sortedBy, sortedAsc, state);
			context.put ("collectionMembers", members);
			if (members.size()>0)
			{
				emptyFolder = false;
			}
			// iterate through the shown resource members and decide on the allowRemove value
			for (int i = 0; (!allowRemove && i<members.size()); i++)	
			{
				Resource resource = (Resource) members.get(i);
				boolean isCollection = false;
				try {
					isCollection = resource.getProperties().getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
				} catch (EmptyException e1) {
					// log as a warning. Treat as a resource.
					if(Log.isWarnEnabled())
					{
						Log.warn("chef", this + "EmptyException for:"+resource.getId() + " "+ e1);
					}     
				}
				String id = resource.getId();

				if (isCollection)
				{
					if (ContentHostingService.allowRemoveCollection(id))
					{
						allowRemove = true;
					}
				}
				else
				{
					if (ContentHostingService.allowRemoveResource(id))
					{
						allowRemove = true;
					}
				}
			}
			
			buildMenu (portlet, context, data, state, allowAdd, allowRemove, allowReplace, allowRevise, emptyFolder, atHome);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, rb.getString("cannotfind"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
			buildMenu (portlet, context, data, state, allowAdd, allowRemove, allowReplace, allowRevise, emptyFolder, atHome);
		}
		catch(TypeException e)
		{
			Log.error("chef", this + "TypeException.");
			context.put ("collectionFlag", Boolean.FALSE.toString());
			buildMenu (portlet, context, data, state, false, false, false, false, emptyFolder, atHome);
		}
		catch(PermissionException e)
		{
			addAlert(state, rb.getString("notpermis1"));
			context.put ("collectionFlag", Boolean.FALSE.toString());
			buildMenu (portlet, context, data, state, false, false, false, false, emptyFolder, atHome);
		}
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
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
		
		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		justDelivered(state);

		// pick the "show" template based on the standard template name
		String template = (String) getContext(data).get("template");
	
		return template + TEMPLATE_BROWSE;
		
	}	// buildBrowseContext

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

		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_DAV;

	}	// buildWebdavContext

	/**
	* Build the context for add display
	*/
	public String buildAddItemContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		context.put ("collectionId", (String)state.getAttribute (STATE_COLLECTION_ID) );

		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_ITEM;

	}	// buildAddItemContext

	/**
	* Build the context for basic add file display
	*/
	public String buildAddFileBasicContext (	VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		// get the file number for multiple file upload
		Integer fileNumberInteger = (Integer) state.getAttribute (STATE_ADD_FILE_NUMBER);
		context.put ("currentFileNumber", fileNumberInteger);
		
		// look for a failed upload, which leaves the /special/upload in the URL %%%
		if (StringUtil.trimToNull(data.getParameters().getString("special")) != null)
		{
			if (fileNumberInteger.intValue() > 1)
			{
				addAlert(state, rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded1"));
			}
			else
			{
				addAlert(state, rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded2"));
			}
		}

		context.put ("osNames", state.getAttribute (STATE_ADD_FILE_OS_NAME));
		context.put ("titles", state.getAttribute (STATE_ADD_FILE_TITLE));
		
		//copyright
		copyrightChoicesIntoContext(state, context);
		context.put("copyrights", state.getAttribute (STATE_ADD_FILE_COPYRIGHT));
		context.put("newcopyrights", state.getAttribute (STATE_ADD_FILE_NEW_COPYRIGHT));
		context.put("copyrightAlerts", state.getAttribute(STATE_ADD_FILE_COPYRIGHT_ALERT));
		
		// moved from optional add file properties page
		context.put ("descriptions", state.getAttribute (STATE_ADD_FILE_DESCRIPTION));
		String currentCollectionId = (String)state.getAttribute (STATE_ADD_FILE_COLLECTION_ID);
		context.put ("collectionId", currentCollectionId );

		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		Vector collectionPath = new Vector ();
		while ((contentService.getContainingCollectionId (currentCollectionId).length ()>0) && (!currentCollectionId.equals (state.getAttribute (STATE_HOME_COLLECTION_ID))))
		{
			String containingId = contentService.getContainingCollectionId (currentCollectionId);
			collectionPath.insertElementAt (containingId, 0);
			currentCollectionId = containingId;
		}
		context.put ("collectionPath", collectionPath);
		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));
		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview inheritance from parent folder
			String collectionReference = ContentHostingService.getReference(currentCollectionId);
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			context.put("pubviewset", new Boolean(pubviewset));
			if (!pubviewset)
			{
				// if the pub view is not inherited, we need to read the user input
				context.put ("pubviews", state.getAttribute(STATE_ADD_FILE_PUBVIEW));
			}
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());
		
		context.put("maxUploadSize", state.getAttribute(FILE_UPLOAD_MAX_SIZE));
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_FILE_BASIC;

	}	// buildAddFileBasicContext

	/**
	* Build the context for adding file optional metadata display
	*/
	public String buildAddFileOptionsContext (	VelocityPortlet portlet,
												Context context,
												RunData data,
												SessionState state)
	{
		context.put("tlang",rb);
		// get the file number for multiple file upload
		Integer fileNumberInteger = (Integer) state.getAttribute (STATE_ADD_FILE_NUMBER);
		context.put ("fileNumber", fileNumberInteger);
		context.put ("osNames", state.getAttribute (STATE_ADD_FILE_OS_NAME));
		context.put ("titles", state.getAttribute (STATE_ADD_FILE_TITLE));
		context.put ("descriptions", state.getAttribute (STATE_ADD_FILE_DESCRIPTION));
		context.put ("copyrights", state.getAttribute (STATE_ADD_FILE_COPYRIGHT));
		context.put ("newcopyrights", state.getAttribute (STATE_ADD_FILE_NEW_COPYRIGHT));
		context.put ("collectionId", state.getAttribute (STATE_COLLECTION_ID));

		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));

		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));

		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_FILE_OPTIONS;

	}	// buildAddFileOptionsContext

	/**
	* Build the context for basic add folder display
	*/
	public String buildAddFolderContext (	VelocityPortlet portlet,
										Context context,
										RunData data,
										SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		Integer folderNumberInteger = (Integer) state.getAttribute (STATE_ADD_FOLDER_NUMBER);
		context.put ("currentFolderNumber", folderNumberInteger);
				
		context.put ("titles", state.getAttribute (STATE_ADD_FOLDER_TITLE));
		context.put ("descriptions", state.getAttribute (STATE_ADD_FOLDER_DESCRIPTION));
		String folderId = (String)state.getAttribute (STATE_ADD_FOLDER_COLLECTION_ID);
		context.put ("folderId", folderId);
		
		String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		context.put("homeCollectionId", homeCollectionId);
		List collectionPath = getCollectionPath(state);	
		context.put ("collectionPath", collectionPath);

		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));

		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));

		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview inheritance from parent folder
			String collectionReference = ContentHostingService.getReference(folderId);
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			context.put("pubviewset", new Boolean(pubviewset));
			if (!pubviewset)
			{
				// if not inherited pub view, we need to read user input
				context.put ("pubviews", state.getAttribute(STATE_ADD_FOLDER_PUBVIEW));
			}
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// not show the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_FOLDER;

	}	// buildAddFolderContext

	public String buildAddDocumentPlaintextContext (	VelocityPortlet portlet,
			Context context,
			RunData data,
			SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		buildAddDocumentContext(portlet, context, data, state);
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_DOCUMENT_PLAINTEXT;
	}
	
	
	public String buildAddDocumentHtmlContext (	VelocityPortlet portlet,
			Context context,
			RunData data,
			SessionState state)
	{
		context.put("tlang",rb);
		buildAddDocumentContext(portlet, context, data, state);
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_DOCUMENT_HTML;
	}
	
	/**
	* Build the context for adding document display
	*/
	private void buildAddDocumentContext (	VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		String currentCollectionId = (String)state.getAttribute (STATE_ADD_FILE_COLLECTION_ID);
		context.put ("collectionId", currentCollectionId );
		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		Vector collectionPath = new Vector ();
		while ((contentService.getContainingCollectionId (currentCollectionId).length ()>0) && (!currentCollectionId.equals (state.getAttribute (STATE_HOME_COLLECTION_ID))))
		{
			String containingId = contentService.getContainingCollectionId (currentCollectionId);
			collectionPath.insertElementAt (containingId, 0);
			currentCollectionId = containingId;
		}
		context.put ("collectionPath", collectionPath);
		context.put ("title", state.getAttribute (STATE_ADD_DOCUMENT_TITLE));
		context.put ("description", state.getAttribute (STATE_ADD_DOCUMENT_DESCRIPTION));
		context.put ("content", state.getAttribute (STATE_ADD_DOCUMENT_CONTENT));
		
		// copyright
		copyrightChoicesIntoContext(state, context);
		String copyright = StringUtil.trimToNull((String) state.getAttribute (STATE_ADD_DOCUMENT_COPYRIGHT));
		if (copyright == null && state.getAttribute(DEFAULT_COPYRIGHT) != null)
		{
			copyright = (String) state.getAttribute(DEFAULT_COPYRIGHT);
			state.setAttribute (STATE_ADD_DOCUMENT_COPYRIGHT, copyright);
		}
		context.put ("copyrightChoice", copyright);
		String copyrightAlert = StringUtil.trimToNull((String) state.getAttribute (STATE_ADD_DOCUMENT_COPYRIGHT_ALERT));
		if (copyrightAlert == null)
		{
			state.setAttribute (STATE_ADD_DOCUMENT_COPYRIGHT_ALERT, state.getAttribute(DEFAULT_COPYRIGHT_ALERT));
			copyrightAlert = (String) state.getAttribute(DEFAULT_COPYRIGHT_ALERT);
		}
		if (copyrightAlert != null)
		{
			context.put ("copyrightAlert", copyrightAlert);
		}
		
		if (state.getAttribute (STATE_ADD_DOCUMENT_NEW_COPYRIGHT) != null)
		{
			context.put ("newcopyright", state.getAttribute (STATE_ADD_DOCUMENT_NEW_COPYRIGHT));
		}

		context.put ("unqualified_fields", ((Vector) state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD)).get (1));

		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));

		// not show the public option when in dropbox mode
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview inheritance from parent folder
			String collectionReference = ContentHostingService.getReference(currentCollectionId);
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			context.put("pubviewset", new Boolean(pubviewset));
			if (!pubviewset)
			{
				// if not inherited pub view, we need to read user input
				context.put ("pubview", state.getAttribute(STATE_ADD_DOCUMENT_PUBVIEW));
			}
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// not show the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());
		
	}	// buildAddDocumentContext

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
		context.put ("deleteResourceIds", state.getAttribute (STATE_DELETE_IDS));
		
		String notEmptyDeleteIds = (String) state.getAttribute(STATE_NOT_EMPTY_DELETE_IDS);
		
		if (notEmptyDeleteIds.length ()>0)
		{
			if (notEmptyDeleteIds.indexOf (",")>-1)
			{
				addAlert(state, rb.getString("folder1") + " " + notEmptyDeleteIds + " " + rb.getString("contain") + " ");
			}
			else
			{
				addAlert(state, rb.getString("folder2") + " " + notEmptyDeleteIds + " " + rb.getString("contain") + " ");
			}
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

		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_DELETE_CONFIRM;

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
					if (moreResource.getContent () != null)
					{
						body = new String (moreResource.getContent ());
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
			boolean pubview = getPubViewInheritance(ContentHostingService.getReference(id));
			if (!pubview) pubview = getPubView(ContentHostingService.getReference(id));
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
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_MORE;

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
					if (propertiesResource.getContent () != null)
					{
						body = new String (propertiesResource.getContent ());
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
			context.put("pubviewset", new Boolean(getPubViewInheritance(ContentHostingService.getReference(id))));
			context.put("pubview", new Boolean(getPubView(ContentHostingService.getReference(id))));

		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_PROPERTIES;

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

		// put the item into context
		EditItem item = (EditItem)state.getAttribute(STATE_EDIT_ITEM);

		context.put("item", item);

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
		
		String template = (String) getContext(data).get("template");

		return template + TEMPLATE_EDIT;

	}	// buildEditContext

	/**
	* Build the context to replace a file
	*/
	public String buildReplaceContext (	VelocityPortlet portlet,
	Context context,
	RunData data,
	SessionState state)
	{
		context.put("tlang",rb);
		int replaceItemNum = ((Vector) state.getAttribute (STATE_REPLACE_ID)).size ()-1;
		// look for a failed upload, which leaves the /special/upload in the URL %%%
		if (StringUtil.trimToNull(data.getParameters().getString("special")) != null)
		{
			if (replaceItemNum > 1)
			{
				addAlert(state, rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded1"));
			}
			else
			{
				addAlert(state, rb.getString("size") + " "  + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded2"));
			}
		}

		context.put ("collectionId", (String)state.getAttribute (STATE_COLLECTION_ID) );
		context.put ("collectionPath", state.getAttribute (STATE_COLLECTION_PATH));
		context.put ("replaceItems", state.getAttribute (STATE_REPLACE_ID));
		context.put ("replaceNumber", new Integer (replaceItemNum));
		context.put ("osNames", state.getAttribute (STATE_REPLACE_OS_NAME));
		context.put ("titles", state.getAttribute (STATE_REPLACE_TITLE));
		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));

		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService service = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		context.put ("service", service);

		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// notshow the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_REPLACE;

	}	// buildReplaceContext

	/**
	* Build the context for adding Urls
	*/
	public String buildAddUrlContext (VelocityPortlet portlet,
												Context context,
												RunData data,
												SessionState state)
	{
		context.put("tlang",rb);
		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
		Integer UrlNumberInteger = (Integer)state.getAttribute (STATE_ADD_URL_NUMBER);
		context.put ("currentUrlNumber", UrlNumberInteger);
		
		context.put ("urls", state.getAttribute (STATE_ADD_URL_URL));
		context.put ("titles", state.getAttribute (STATE_ADD_URL_TITLE));
		context.put ("descriptions", state.getAttribute (STATE_ADD_URL_DESCRIPTION));
		
		/*
		copyrightChoicesIntoContext(state, context);
		context.put("copyrights", state.getAttribute (STATE_ADD_URL_COPYRIGHT));
		context.put("newcopyrights", state.getAttribute (STATE_ADD_URL_NEW_COPYRIGHT));
		context.put("copyrightAlerts", state.getAttribute(STATE_ADD_URL_COPYRIGHT_ALERT));
		*/
		
		context.put ("unqualified_fields", state.getAttribute (STATE_UNQUALIFIED_INPUT_FIELD));

		// revise the document document
		String currentCollectionId = (String)state.getAttribute (STATE_ADD_FILE_COLLECTION_ID);
		context.put ("collectionId", currentCollectionId);
				
		// find the ContentHosting service
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		Vector collectionPath = new Vector ();
		while ((contentService.getContainingCollectionId (currentCollectionId).length ()>0) && (!currentCollectionId.equals (state.getAttribute (STATE_HOME_COLLECTION_ID))))
		{
			String containingId = contentService.getContainingCollectionId (currentCollectionId);
			collectionPath.insertElementAt (containingId, 0);
			currentCollectionId = containingId;
		}
		context.put ("collectionPath", collectionPath);

		// default copyright
		String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

		context.put ("service", state.getAttribute (STATE_CONTENT_SERVICE));

		// find the ContentTypeImage service
		context.put ("contentTypeImageService", state.getAttribute (STATE_CONTENT_TYPE_IMAGE_SERVICE));
	
		// not show the public option when in dropbox mode
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			context.put("dropboxMode", Boolean.FALSE);
			
			// find out about pubview inheritance from parent folder
			String collectionReference = ContentHostingService.getReference(currentCollectionId);
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			context.put("pubviewset", new Boolean(pubviewset));
			if (!pubviewset)
			{
				// if not inherited pub view, we need to read user input
				context.put ("pubviews", state.getAttribute(STATE_ADD_URL_PUBVIEW));
			}
		}
		else if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_DROPBOX))
		{
			// not show the public option or notification when in dropbox mode
			context.put("dropboxMode", Boolean.TRUE);
		}
		context.put("homeCollection", (String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		context.put("siteTitle", state.getAttribute(STATE_SITE_TITLE));
		context.put ("resourceProperties", ContentHostingService.newResourceProperties ());

		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_ADD_URL;

	}	// buildAddUrlContext

	/**
	* Navigate in the resource hireachy
	*/
	public void doNavigate ( RunData data )
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

		state.setAttribute (STATE_MODE, MODE_LIST);

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

	}	// doNavigate

	/**
	* Show information about WebDAV
	*/
	public void doShow_webdav ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		state.setAttribute (STATE_MODE, MODE_DAV);


	}	// doShow_webdav
	
	/**
	 * initiate creation of one or more resource items (file uploads, html docs, text docs, or urls -- not folders)
	 * default type is file upload
	 */
	public void doCreate(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String itemType = params.getString("itemType");

		List new_items = new Vector();
		for(int i = 0; i < CREATE_MAX_ITEMS; i++)
		{
			new_items.add(new CreateItem(itemType));
		}
		state.setAttribute(STATE_CREATE_ITEMS, new_items);
		state.setAttribute(STATE_CREATE_NUMBER, new Integer(1));
		state.setAttribute(STATE_CREATE_TYPE, itemType);
		state.setAttribute(STATE_CREATE_ALERTS, new HashSet());
		state.setAttribute(STATE_CREATE_MISSING_ITEM, new HashSet());
		state.removeAttribute(STATE_STRUCTOBJ_TYPE);
		state.removeAttribute(STATE_STRUCTOBJ_PROPERTIES);
		state.removeAttribute(STATE_STRUCTOBJ_HOMES);
		
		String collectionId = params.getString ("collectionId");
		state.setAttribute(STATE_CREATE_COLLECTION_ID, collectionId);
		
		state.setAttribute (STATE_MODE, MODE_CREATE);
		
	}	// doCreate
	
	/**
	 * initiate creation of one or more resource items (file uploads, html docs, text docs, or urls -- not folders)
	 * default type is file upload
	 */
	public void doCreateitem(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String itemType = params.getString("itemType");
		String flow = params.getString("flow");
		String mode = (String) state.getAttribute(STATE_MODE);
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		Set missing = new HashSet();
		
		if(flow == null || flow.equals("cancel"))
		{
			mode = MODE_LIST;
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
				CreateItem item = (CreateItem) it.next();
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
					mode = MODE_LIST;
				}
			}
		}
		else if(flow.equals("create") && TYPE_UPLOAD.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state, params);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					mode = MODE_LIST;
				}
			}
		}
		else if(flow.equals("create") && MIME_TYPE_DOCUMENT_HTML.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state, params);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					mode = MODE_LIST;
				}
			}
		}
		else if(flow.equals("create") && MIME_TYPE_DOCUMENT_PLAINTEXT.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createFiles(state, params);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					mode = MODE_LIST;
				}
			}
		}
		else if(flow.equals("create") && TYPE_URL.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createUrls(state, params);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					mode = MODE_LIST;
				}
			}
		}
		else if(flow.equals("create") && TYPE_FORM.equals(itemType))
		{
			captureMultipleValues(state, params, true);
			alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
			if(alerts.isEmpty())
			{
				createStructuredArtifact(state);
				alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
				if(alerts.isEmpty())
				{
					mode = MODE_LIST;
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
		else if(flow.equals("showOptional"))
		{
			captureMultipleValues(state, params, false);
			int twiggleNumber = params.getInt("twiggleNumber", 0);
			String metadataGroup = params.getString("metadataGroup");
			List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
			if(items != null && items.size() > twiggleNumber)
			{
				CreateItem item = (CreateItem) items.get(twiggleNumber);
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
				CreateItem item = (CreateItem) it.next();
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
				CreateItem item = (CreateItem) items.get(twiggleNumber);
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
				CreateItem item = (CreateItem) it.next();
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
		
		state.setAttribute (STATE_MODE, mode);
		
	}	// doCreateitem
	
	/**
	 * @param state
	 */
	private void createStructuredArtifact(SessionState state) 
	{
		String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
		String docRoot = (String) state.getAttribute(STATE_STRUCTOBJ_ROOTNAME);
		// List listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
		List properties = (List) state.getAttribute(STATE_STRUCTOBJ_PROPERTIES);
		List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Iterator itemIt = items.iterator();
		while(itemIt.hasNext())
		{
			EditItem item = (EditItem) itemIt.next();
			Document doc = Xml.createDocument();
			Element root = doc.createElement(docRoot);
			doc.appendChild(root);
			int count = 0;
			
			Iterator propIt = properties.iterator();
			while(propIt.hasNext())
			{
				ResourcesMetadata prop = (ResourcesMetadata) propIt.next();
				String propname = prop.getLocalname();
				List values = item.getList(propname);
				Iterator valueIt = values.iterator();
				while(valueIt.hasNext())
				{
					try
					{
						Object value = valueIt.next();
						if(value == null)
						{
							
						}
						else
						{
							Element el = doc.createElement(propname);
							root.appendChild(el);
							if(value instanceof String)
							{
								// root.setAttribute(propname, (String) value);
								el.setNodeValue((String) value);
							}
							else
							{
								// root.setAttribute(propname, value.toString());
								el.setNodeValue(value.toString());
							}
							count++;
						}
					}
					catch(Throwable e)
					{			

					}
				}
			}
			if(count > 0)
			{
				try
				{
					String sss = Xml.writeDocumentToString(doc);			
					
				}
				catch(Throwable e)
				{			
	
				}
			}
		}			
		
	}

	/**
	 * @param state
	 */
	protected void createFolders(SessionState state) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_folders = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfFolders = 1;
		numberOfFolders = number.intValue();
		
		for(int i = 0; i < numberOfFolders; i++)
		{
			CreateItem item = (CreateItem) new_folders.get(i);
			String newCollectionId = collectionId + Validator.escapeResourceName(item.getName()) + Resource.SEPARATOR;

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
					// boolean pubviewset = getPubViewInheritance(ContentHostingService.getReference(collection.getId()));
					if (!item.isPubviewset())
					{
						setPubView(ContentHostingService.getReference(collection.getId()), item.isPubview());
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
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createFolders

	/**
	 * @param state
	 * @param params TODO
	 */
	protected void createFiles(SessionState state, ParameterParser params) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_files = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfItems = 1;
		numberOfItems = number.intValue();
		
		for(int i = 0; i < numberOfItems; i++)
		{
			CreateItem item = (CreateItem) new_files.get(i);
			
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
				if(item.isHtml())
				{
					filename = item.getName() + ".html";
				}
				else if(item.isPlaintext())
				{
					filename = item.getName() + ".txt";
				}
				else
				{
					filename = item.getName();
				}
					
				filename = Validator.escapeResourceName(filename);
			}
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
							setPubView(ContentHostingService.getReference(resource.getId()), item.isPubview());
						}
					}
				}
				catch (IdUsedException e)
				{
					boolean foundUnusedId = false;
					for(int trial = 1;!foundUnusedId && trial < 100; trial++)
					{
						attemptNum++;
						attempt = Integer.toString(attemptNum);
					

						// add extension if there was one
						newResourceId = collectionId + filename + "-" + attempt + extension;
						
						try 
						{
							ContentHostingService.getResource(newResourceId);
						}
						catch (IdUnusedException ee)
						{
							foundUnusedId = true;
						}
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
				}
				catch(IdInvalidException e)
				{
					alerts.add(rb.getString("title") + " " + e.getMessage ());
				}
				catch(InconsistentException e)
				{
					alerts.add(RESOURCE_INVALID_TITLE_STRING);
				}
				catch(OverQuotaException e)
				{
					alerts.add(rb.getString("overquota"));
				}
			}
				
		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createFiles
	
	
	public void setupStructuredObjects(SessionState state)
	{
		String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
		
		HomeFactory factory = (HomeFactory) ComponentManager.get("homeFactory");	
		
		Map homes = factory.getHomes();
		List listOfHomes = new Vector();
		Iterator it = homes.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			listOfHomes.add(homes.get(key));
		}
		state.setAttribute(STATE_STRUCTOBJ_HOMES, listOfHomes);
		
		StructuredArtifactHomeInterface home = null;
		SchemaBean rootSchema = null;
		List properties = new Vector();
		
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
			String namespace = rootSchema.getSchemaName() + ".";
			List fields = rootSchema.getFields();
			properties = createPropertiesList(namespace, fields);
			String docRoot = rootSchema.getFieldName();
			state.setAttribute(STATE_STRUCTOBJ_ROOTNAME, docRoot);
		}
		state.setAttribute(STATE_STRUCTOBJ_PROPERTIES, properties);
		
		/*
		*/

	}
	
	
	protected List createPropertiesList(String namespace, List fields)
	{
		List properties = new Vector();
		List nested = new Vector();
		for(Iterator fieldIt = fields.iterator(); fieldIt.hasNext(); )
		{
			SchemaBean field = (SchemaBean) fieldIt.next();
			// String namespace = field.getFieldNamePathReadOnly();
			SchemaNode node = field.getSchema();
			Map annotations = field.getAnnotations();
			
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
				nested.addAll(createPropertiesList(namespace, field.getFields()));
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
				if(length > 100 || length < 0)
				{
					widget = ResourcesMetadata.WIDGET_TEXTAREA;
				}
				else if(length > 50)
				{
					length = 50;
				}
				else if(length < 2)
				{
					length = 2;
				}
				
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
			else if(typename.equals(Integer.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_INTEGER;
			}
			else if(typename.equals(Double.class.getName()))
			{
				widget = ResourcesMetadata.WIDGET_DOUBLE;
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
			
			ResourcesMetadata prop = new ResourcesMetadata(namespace, localname, label, description, typename, widget);
			prop.setMinCardinality(minCard);
			prop.setMaxCardinality(maxCard);
			prop.setCurrentCount(currentCount);
			if(enumerals != null)
			{
				prop.setEnumeration(enumerals);
			}
			if(length > 0)
			{
				prop.setLength(length);
			}
			prop.setNested(nested);
			
			properties.add(prop);
		}
		return properties;
		
	}
	
	/**
	 * @param state
	 * @param params TODO
	 */
	protected void createUrls(SessionState state, ParameterParser params) 
	{
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		List new_urls = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Integer number = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		String collectionId = (String) state.getAttribute(STATE_CREATE_COLLECTION_ID);
		int numberOfItems = 1;
		numberOfItems = number.intValue();
		
		for(int i = 0; i < numberOfItems; i++)
		{
			CreateItem item = (CreateItem) new_urls.get(i);
			
			ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
			String title = item.getName();
			resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, title);							
			resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, item.getDescription());

			resourceProperties.addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.FALSE.toString());
			List metadataGroups = (List) state.getAttribute(STATE_METADATA_GROUPS);
			saveMetadata(resourceProperties, metadataGroups, item);

			byte[] newUrl = item.getFilename().getBytes();
			String baseId = Validator.escapeResourceName(title);
			int attemptNum = 0;
			String attemptStr = "";
			String newResourceId = collectionId + baseId + attemptStr;
		
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
							setPubView(ContentHostingService.getReference(resource.getId()), item.isPubview());
						}
					}
				}
				catch (IdUsedException e)
				{
					boolean foundUnusedId = false;
					for(int trial = 1;!foundUnusedId && trial < 100; trial++)
					{
						attemptNum++;
						attemptStr = "-" + Integer.toString(attemptNum);
					

						// add attempt number if there was one
						newResourceId = collectionId + baseId + attemptStr;
						
						try 
						{
							ContentHostingService.getResource(newResourceId);
						}
						catch (IdUnusedException ee)
						{
							foundUnusedId = true;
						}
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
					alerts.add(rb.getString("notpermis13"));
				}
				catch(IdInvalidException e)
				{
					alerts.add(rb.getString("title") + " " + e.getMessage ());
				}
				catch(InconsistentException e)
				{
					alerts.add(RESOURCE_INVALID_TITLE_STRING);
				}
				catch(OverQuotaException e)
				{
					alerts.add(rb.getString("overquota"));
				}
			}
		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);
		
	}	// createUrls
	
	/**
	* Build the context for creating folders and items
	*/
	public String buildCreateContext (VelocityPortlet portlet,
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
			String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
			context.put("formtype", formtype);
			setupStructuredObjects(state);
			
			List listOfHomes = (List) state.getAttribute(STATE_STRUCTOBJ_HOMES);
			context.put("homes", listOfHomes);
			List properties = (List) state.getAttribute(STATE_STRUCTOBJ_PROPERTIES);
			context.put("properties", properties);

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
		}
		Set missing = (Set) state.removeAttribute(STATE_CREATE_MISSING_ITEM);
		context.put("missing", missing);
		
		String template = (String) getContext(data).get("template");
		return template + TEMPLATE_CREATE;

	}

	/**
	* show the resource properties
	*/
	public void doMore ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

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
	* set the state name to be "add"
	*/
	public void doNewitem ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_MODE, MODE_ADD_ITEM);

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

	}	// doNewitem

	/**
	* doAdditem called for the specific type of object to be added
	*/
	public void doAdditem ( RunData data)
	{
		addItem(data, data.getParameters ());
	}
	
	/**
	 * to handle add various item type request
	 */
	protected void addItem ( RunData data, ParameterParser params)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String mycopyright =(String) state.getAttribute (STATE_MY_COPYRIGHT);

		String collectionId = params.getString ("collectionId");
		state.setAttribute(STATE_ADD_FILE_COLLECTION_ID, collectionId);
		
		try
		{
			ContentHostingService.checkCollection ((String) state.getAttribute (STATE_HOME_COLLECTION_ID));
		}
		catch (IdUnusedException e )
		{
			try
			{
				ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
				String homeCollectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
				String displayName = (String) state.getAttribute (STATE_HOME_COLLECTION_DISPLAY_NAME);
				if ((displayName == null) || (displayName.length() == 0)) displayName = homeCollectionId;
				resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, displayName);

				ContentCollection collection = ContentHostingService.addCollection (homeCollectionId, resourceProperties);				
			}
			catch (IdUsedException ee)
			{
				addAlert(state, rb.getString("idused"));
			}
			catch (IdInvalidException ee)
			{
				addAlert(state, rb.getString("title") + " " + ee.getMessage ());
			}
			catch (PermissionException ee)
			{
				addAlert(state, rb.getString("notpermis4"));
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
		
		String itemType = params.getString ("itemType");


		if (state.getAttribute(STATE_MESSAGE) == null && itemType != null)
		{
			if ("file".equalsIgnoreCase(itemType))
			{
				int newFileNumber = 1;
				String s = params.getString ("fileNumber");
				if (s != null)
				{
					newFileNumber = (new Integer(s)).intValue();
					state.setAttribute (STATE_ADD_FILE_NUMBER, new Integer (newFileNumber));
					
					// save the already inputed infos
					int oldFileNumber = 1;
					s = params.getString ("fileNumber");
					if (s != null)
					{
						oldFileNumber = (new Integer(s)).intValue();
					}

					Vector osNames = (Vector) state.getAttribute(STATE_ADD_FILE_OS_NAME);
					Vector titles = emptyVector (MULTI_ADD_NUMBER + 1);
					Vector contents = (Vector) state.getAttribute(STATE_ADD_FILE_CONTENT);
					Vector types =(Vector) state.getAttribute(STATE_ADD_FILE_CONTENT_TYPE);
					Vector copyrights = emptyVector (MULTI_ADD_NUMBER + 1);
					// moved from the set properties page
					Vector newcopyrights = emptyVector (MULTI_ADD_NUMBER + 1);
					Vector descriptions = emptyVector (MULTI_ADD_NUMBER + 1);
					Vector pubviews = emptyVector (MULTI_ADD_NUMBER + 1);
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						pubviews = (Vector) state.getAttribute(STATE_ADD_FILE_PUBVIEW);
					}
					// count the user specified input file number
					for (int i=1; i <= (newFileNumber<oldFileNumber?newFileNumber:oldFileNumber); i++)
					{
						FileItem fi = params.getFileItem ("filename" + i);
						// get the file name
						String title = params.getString ("title"+i);
						if (title!=null)
						{
							titles.set (i, title.trim());
						}

						String copyright = params.getString ("copyright" + i);
						if (copyright!=null)
						{
							copyrights.set (i, copyright);
						}
						else if (state.getAttribute(DEFAULT_COPYRIGHT) != null)
						{
							copyrights.set(i, state.getAttribute(DEFAULT_COPYRIGHT));
						}

						String newcopyright = params.getCleanString (NEW_COPYRIGHT + i);
			
						if (newcopyright!=null)
						{
							newcopyrights.set (i, newcopyright);
						}

						// clean the input
						String description = params.getCleanString ("description" + i);
						if (description!=null)
						{
							descriptions.set (i, description);
						}

						if (fi!=null)
						{
							// there is a upload file
							// Note: we call get() from the FileItem to get a pointer to the actual bytes of the file,
							// without making a copy of these.
							contents.set (i, fi.get ());
							osNames.set (i, fi.getFileName ());
							types.set (i, fi.getContentType ());
						}

						// the user did not fill in the optional field for the file name
						// use the original file name instead
						if (title == null || title.length ()==0)
						{
							// get the add file name
							title = Validator.getFileName ((String) osNames.get (i));
						}
						titles.set (i, title);
						
						if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
						{
							// get pubview information
							boolean pubview = (new Boolean(params.getString("pubview" + i))).booleanValue();
 							pubviews.set(i, new Boolean(pubview));
						}
						
					}	// for

					// reset infos more than new file number
					for (int j=(newFileNumber<oldFileNumber?newFileNumber+1:oldFileNumber+1); j <= (newFileNumber>oldFileNumber?newFileNumber:oldFileNumber); j++)
					{
						osNames.set(j, "");
						contents.set(j, "");
						types.set(j, "");
					}
					// update the state attribute
					state.setAttribute (STATE_ADD_FILE_CONTENT, contents);
					state.setAttribute (STATE_ADD_FILE_CONTENT_TYPE, types);
					state.setAttribute (STATE_ADD_FILE_OS_NAME, osNames);
					state.setAttribute (STATE_ADD_FILE_TITLE, titles);
					state.setAttribute (STATE_ADD_FILE_COPYRIGHT, copyrights);
					state.setAttribute (STATE_ADD_FILE_NEW_COPYRIGHT, newcopyrights);
					state.setAttribute (STATE_ADD_FILE_DESCRIPTION, descriptions);
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						state.setAttribute (STATE_ADD_FILE_PUBVIEW, pubviews);
					}
				}
				else
				{
					initAddFileContext (state);
				}
				state.setAttribute (STATE_MODE, MODE_ADD_FILE_BASIC);
			}
			else if ("emptyFolder".equalsIgnoreCase(itemType))
			{
				int newFolderNumber = 1;
				String s = params.getString ("folderNumber");
				if (s != null)
				{
					newFolderNumber = (new Integer(s)).intValue();
					state.setAttribute (STATE_ADD_FOLDER_NUMBER, new Integer (newFolderNumber));
					state.setAttribute (STATE_ADD_FOLDER_COLLECTION_ID, collectionId);
					
					int oldFolderNumber = Integer.valueOf (params.getString ("folderNumber")).intValue ();

					Vector titles = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector descriptions = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector pubviews = emptyVector(MULTI_ADD_NUMBER + 1);
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						pubviews = (Vector) state.getAttribute(STATE_ADD_FOLDER_PUBVIEW);
					}

					for (int i=1; i <= (newFolderNumber<oldFolderNumber?newFolderNumber:oldFolderNumber); i++ )
					{
						// set titles
						String title = params.getString ("name" + i);
						if (title != null)
						{
							titles.add (i, title.trim());
						}
						//set descriptions
						descriptions.add (i, params.getCleanString ("description" + i));
						
						if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
						{
							// get pubview information
							boolean pubview = (new Boolean(params.getString("pubview" + i))).booleanValue();
							pubviews.set(i, new Boolean(pubview));
						}
					}	// for

					state.setAttribute (STATE_ADD_FOLDER_TITLE, titles);
					state.setAttribute (STATE_ADD_FOLDER_DESCRIPTION, descriptions);	
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						state.setAttribute (STATE_ADD_FOLDER_PUBVIEW, pubviews);
					}
					
				}
				else
				{

					state.setAttribute (STATE_ADD_FOLDER_COLLECTION_ID, collectionId);
					initAddFolderContext (state);
				}
				state.setAttribute (STATE_MODE, MODE_ADD_FOLDER);

			}	// add folder
			else if (params.getString ("itemType").equals ("plaintextdocument"))
			{
				initAddDocumentContext (state);
				// get parent hosted item ID
				state.setAttribute (STATE_MODE, MODE_ADD_DOCUMENT_PLAINTEXT);

			}	// add plaintext document
			else if ("htmldocument".equalsIgnoreCase(itemType))
			{
				initAddDocumentContext (state);
				// get parent hosted item ID
				state.setAttribute (STATE_MODE, MODE_ADD_DOCUMENT_HTML);

			}	// add html document
			else if ("Url".equalsIgnoreCase (itemType))
			{
				int newUrlNumber = 1;
				String s = params.getString ("urlNumber");
				if (s != null)
				{
					newUrlNumber = (new Integer(s)).intValue();
					state.setAttribute (STATE_ADD_URL_NUMBER, new Integer (newUrlNumber));
					
					// save the already inputed infos
					int oldUrlNumber = 1;
					s = params.getString ("UrlNumber");
					if (s != null)
					{
						oldUrlNumber = (new Integer(s)).intValue();
					}

					// initialize the add url context
					Vector urls = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector titles = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector descriptions = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector copyrights = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector newcopyrights = emptyVector(MULTI_ADD_NUMBER + 1);
					Vector unqualified_fields = emptyVector (MULTI_ADD_NUMBER + 1);
					Vector pubviews = emptyVector(MULTI_ADD_NUMBER + 1);
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						pubviews = (Vector) state.getAttribute(STATE_ADD_URL_PUBVIEW);
					}
					
					for (int i=1; i <= (newUrlNumber<oldUrlNumber?newUrlNumber:oldUrlNumber); i++ )
					{
						// the URL web address
						String url = params.getString ("Url" + i);
						if (url != null)
						{
							urls.set (i, url.trim());
						}

						// the URL title
						String title = params.getString ("title" + i);
						if (title != null)
						{
							titles.set (i, title.trim());
						}

						// the URL description
						descriptions.set (i, params.getCleanString ("description" + i));

						// the URL copyright
						String copyright = params.getString ("copyright" + i);
						if (copyright!=null)
						{
							copyrights.set (i, copyright);
						}
						else if (state.getAttribute(DEFAULT_COPYRIGHT) != null)
						{
							copyrights.set(i, state.getAttribute(DEFAULT_COPYRIGHT));
						}

						// the URL new copyright
						newcopyrights.set (i, params.getCleanString (NEW_COPYRIGHT + i));
						
						if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
						{
							// get pubview information
							boolean pubview = (new Boolean(params.getString("pubview" + i))).booleanValue();
							pubviews.set(i, new Boolean(pubview));
						}
						
					}	// for

					state.setAttribute (STATE_ADD_URL_URL, urls);
					state.setAttribute (STATE_ADD_URL_TITLE, titles);
					state.setAttribute (STATE_ADD_URL_DESCRIPTION, descriptions);
					state.setAttribute (STATE_ADD_URL_COPYRIGHT, copyrights);
					state.setAttribute (STATE_ADD_URL_NEW_COPYRIGHT, newcopyrights);
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						state.setAttribute (STATE_ADD_URL_PUBVIEW, pubviews);
					}
				}
				else
				{
					initAddURLContext (state);
				}

				state.setAttribute (STATE_MODE, MODE_ADD_URL);
			}
		}

	}	// doAdditem

	/**
	* Add file without seting the properties
	*/
	public void doAdd_file_basic ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String option = params.getString("option");
		if (option.equalsIgnoreCase("change"))
		{
			// change add file number
			addItem(data, params);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel(data);
		}
		else
		{
			// add file(s)
			int fileNumber = Integer.valueOf (params.getString ("fileNumber")).intValue ();
			
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

			String collectionId = params.getString("collectionId"); // (String) state.getAttribute (STATE_COLLECTION_ID);
			state.setAttribute(STATE_ADD_FILE_COLLECTION_ID, collectionId);

			// default copyright
			String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

			// add file context
			Vector osNames = (Vector) state.getAttribute(STATE_ADD_FILE_OS_NAME);
			Vector titles = emptyVector (MULTI_ADD_NUMBER + 1);
			Vector contents = (Vector) state.getAttribute(STATE_ADD_FILE_CONTENT);
			Vector types =(Vector) state.getAttribute(STATE_ADD_FILE_CONTENT_TYPE);
			Vector copyrights = emptyVector (MULTI_ADD_NUMBER + 1);
			Vector copyrightAlerts = emptyVector (MULTI_ADD_NUMBER + 1);
			Vector pubviews = emptyVector (MULTI_ADD_NUMBER + 1);
			if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
			{
				pubviews = (Vector) state.getAttribute(STATE_ADD_FILE_PUBVIEW);
			}

			// moved from the set properties page
			Vector newcopyrights = emptyVector (MULTI_ADD_NUMBER + 1);
			Vector descriptions = emptyVector (MULTI_ADD_NUMBER + 1);

			// the error input fields
			Vector unqualified_fields = emptyVector (MULTI_ADD_NUMBER + 1);

			// count the user specified input file number
			int realFileNumber = 0;
			for (int i=1; i <= fileNumber; i++)
			{
				// unqualified field names
				String u_fields = "";

				FileItem fi = params.getFileItem ("filename" + i);
				byte[] b = null;

				HttpServletRequest req = data.getRequest();

				if (!req.getContentType().startsWith("multipart"))
				{
					addAlert(state, rb.getString("alert.notmultipartsubmit"));
				}
				else if ("size_limit_exceeded".equals(req.getAttribute("upload.status")))
				{
					if (fileNumber > 1)
					{
						addAlert(state, rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded1"));
					}
					else
					{
						addAlert(state, rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded2"));
					}
				}
				else if ("exception".equals(req.getAttribute("upload.status")))
				{
				   addAlert(state, rb.getString("alert.uploadstatusexception") + req.getAttribute("upload.exception") );
				}
				else if ((fi.getFileName() == null || fi.getFileName().length() == 0) && StringUtil.trimToNull((String) osNames.get(i)) == null)
				{
					//if there is a input(file name, new copyright, or description), but no upload file, generate error message
					
					if (((String)titles.get(i)).length ()!=0 
						|| ((String)newcopyrights.get(i)).length()!=0 
						|| ((String)descriptions.get(i)).length()!=0)
					{
						addAlert(state, rb.getString("choosefile") + " " + i + ". ");
					}
				}
				else if (fi != null)
				{
					b = fi.get();
					if (b.length <= 1 && StringUtil.trimToNull((String) osNames.get(i)) == null)
					{
						addAlert(state, rb.getString("alert.notvalidfile") +fi.getFileName() + " " + rb.getString("invalid"));
					}
				}

				// get the file name
				String title = StringUtil.trimToNull(params.getString ("title"+i));
				if (title!=null)
				{
					titles.set (i, title.trim());
				}

				// set the copyright
				String copyright = StringUtil.trimToNull(params.getString ("copyright" + i));
				if (copyright!=null)
				{
					copyrights.set (i, copyright);
				}

				String newcopyright = StringUtil.trimToNull(params.getCleanString (NEW_COPYRIGHT + i));
				if (newcopyright!=null)
				{
					newcopyrights.set (i, newcopyright);
				}

				if (params.getString("copyrightAlert" + i) != null)
				{
					copyrightAlerts.set(i, Boolean.TRUE.toString());
				}
				else
				{
					copyrightAlerts.set(i, null);
				}
				
				String description = StringUtil.trimToNull(params.getCleanString ("description" + i));
				if (description!=null)
				{
					descriptions.set (i, description);
				}

				if (fi!=null)
				{
					if (b!= null && b.length != 0)
					{
						//there is a upload file
						// Note: we call get() from the FileItem to get a pointer to the actual bytes of the file,
						// without making a copy of these.
						contents.set (i, b);
						osNames.set (i, fi.getFileName ());
						String contentType = fi.getContentType ();
						types.set (i, contentType);
					}
				}
				
				//the user did not fill in the optional field for the file name
				// use the original file name instead
				if (title == null && StringUtil.trimToNull((String) osNames.get (i)) != null)
				{
					// get the add file name
					title = Validator.getFileName (StringUtil.trimToNull((String) osNames.get (i)));
				}

				if (title != null)
				{
					realFileNumber = realFileNumber + 1;
					titles.set (i, title);
				}
				
				/*
				String pubviewString = "";
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					pubviewString = params.getString("pubview" + i);
					if (pubviewString != null)
					{
						pubviews.set(i, new Boolean(pubviewString)); 
					}
				}
				*/
		
				boolean pubview = false;
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					pubview = params.getBoolean("pubview" + i);
					pubviews.set(i, new Boolean(pubview));
				}

				if (copyright != null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && newcopyright == null)
				{
					addAlert(state, rb.getString("specifycp") + " " + i + ". ");
				}
				unqualified_fields.add (i, u_fields);
			}	// for

			//update the state attribute
			state.setAttribute (STATE_ADD_FILE_CONTENT, contents);
			state.setAttribute (STATE_ADD_FILE_CONTENT_TYPE, types);
			state.setAttribute (STATE_ADD_FILE_OS_NAME, osNames);
			state.setAttribute (STATE_ADD_FILE_TITLE, titles);
			state.setAttribute (STATE_ADD_FILE_COPYRIGHT, copyrights);
			state.setAttribute (STATE_ADD_FILE_NEW_COPYRIGHT, newcopyrights);
			state.setAttribute (STATE_ADD_FILE_DESCRIPTION, descriptions);
			if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
			{
				state.setAttribute (STATE_ADD_FILE_PUBVIEW, pubviews);
			}			
			
			if (realFileNumber == 0)
			{
				addAlert(state, rb.getString("specifyfile"));
			}

			int repeatedNames_index =  repeatedName (titles, fileNumber);
			if (repeatedNames_index >0)
			{
				String n = (String) titles.get (repeatedNames_index);
				if (n.length ()>0)
				{
					addAlert(state,rb.getString("notaddfile") + " " + n + ". ");
					unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index) + ResourceProperties.PROP_DISPLAY_NAME);
				}
			}
			else
			{
				repeatedNames_index = foundInResource (titles, fileNumber, collectionId, false);

				if (repeatedNames_index>0)
				{
					String n = (String) titles.get (repeatedNames_index);
					if (n.length ()>0)
					{
						addAlert(state, rb.getString("title") + " " + n + " " + rb.getString("ofitem") + "  " + repeatedNames_index + " " + rb.getString("used"));
						unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index)  + ResourceProperties.PROP_DISPLAY_NAME);
					}
				}
				else
				{
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
						// the vector to hold the index of those file been successfully added to resource
						Vector addedFileIndex = new Vector();

						String currentName = NULL_STRING;
						for (int i=1; i <= fileNumber; i++)
						{
							if (((String) titles.get(i)).length()>0)
							{
								// add the file on the basic step
								// check to see if the osName has a file extension
								String name = Validator.getFileName((String) osNames.get(i));
								String newResourceId = collectionId + Validator.escapeResourceName(name);					
								ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
								resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, (String) titles.get (i));							

								String copyrightChoice = StringUtil.trimToNull((String) copyrights.get (i));
								if (copyrightChoice != null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyrightChoice.equals (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && StringUtil.trimToNull((String) newcopyrights.get (i)) != null)
								{
									resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, (String) newcopyrights.get (i));
								}
								else if (copyrightChoice != null && state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyrightChoice.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
								{
									resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
								}
								resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, copyrightChoice);
								if ((String) copyrightAlerts.get(i) != null)
								{
									resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, (String) copyrightAlerts.get(i));
								}
								resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, (String) descriptions.get (i));

								// manipulating the file until it's acceptable("Escaping" it).
								// So, if it has a bad character, change it. If it's not unique, add something to make it unique.  etc.

								boolean needAdd = true;
								while (needAdd)
								{
									try
									{
										ContentResource resource = ContentHostingService.addResource (newResourceId,
																										(String) types.get (i),
																										(byte[]) contents.get (i),
																										resourceProperties, noti);
										needAdd = false;
										addedFileIndex.add(new Integer(i));
	
										if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
										{
											// deal with pubview when in resource mode//%%%
											boolean pubviewset = getPubViewInheritance(ContentHostingService.getReference(resource.getId()));
											if (!pubviewset)
											{
												setPubView(ContentHostingService.getReference(resource.getId()), ((Boolean) pubviews.get(i)).booleanValue());
											}
										}
									}
									catch (IdUsedException e)
									{
										//manipulate the resource id until it is unique
										int foundTimes = 1;
										boolean found = true;
										// add a security guard so that looping times is never over 100
										while (found && foundTimes < 100)
										{
											int numIndex = newResourceId.lastIndexOf ((new Integer(foundTimes-1)).toString ());
											int extensionIndex = newResourceId.lastIndexOf (".");
											String extension = "";
											if ((extensionIndex > 0) && (extensionIndex > newResourceId.lastIndexOf ("/")))
											{
												extension = newResourceId.substring (extensionIndex);
												newResourceId = newResourceId.substring(0, extensionIndex);
											}
											
											// make the id unique, for example, /file.doc, /file1.doc /file2.doc, /file3.doc
											if (numIndex == -1)		// for example, /file.doc
											{
												// no repeatance number yet in resource id
												newResourceId = newResourceId + foundTimes;
											}
											else
											{
												if (newResourceId.length() == numIndex + (new Integer(foundTimes)).toString().length())
												{
													// there is repeatance number in the resource id, for example, /file2.doc
													newResourceId = newResourceId.substring (0, numIndex) + foundTimes;
												}
												else
												{
													newResourceId = newResourceId + foundTimes;
												}
											}
		
											// add extension if there was one
											newResourceId = newResourceId + extension;
											
											try 
											{
												ContentHostingService.getResource(newResourceId);
												foundTimes++;
											}
											catch (IdUnusedException ee)
											{
												found = false;
											}
											catch (PermissionException ee)
											{
												// ignore
												found = false;
											}
											catch (TypeException ee)
											{
												// ignore
												found = false;
											}
										}
									}
									catch (IdInvalidException e)
									{
										addAlert(state, rb.getString("invalid1"));
										needAdd = false;
									}
									catch (PermissionException e)
									{
										addAlert(state,rb.getString("notpermis4"));
										needAdd = false;
									}
									catch (OverQuotaException e)
									{
										addAlert(state,rb.getString("overquota"));
										needAdd = false;
									}
									catch (InconsistentException e)
									{
										addAlert(state, rb.getString("invalid1"));
										needAdd = false;
									}   // try-catch
								}	// while
							}
						}	// for

						// if there is at least one file failed to be uploaded
						if (state.getAttribute(STATE_MESSAGE) != null)
						{
							// clear added file infos from the state attribute 
							for (int k=0; k<addedFileIndex.size(); k++)
							{
								int tempIndex = ((Integer) addedFileIndex.get(k)).intValue();
								contents.removeElementAt(tempIndex);							
								types.removeElementAt(tempIndex);							
								osNames.removeElementAt(tempIndex);							
								titles.removeElementAt(tempIndex);							
								copyrights.removeElementAt(tempIndex);		
								newcopyrights.removeElementAt(tempIndex);							
								descriptions.removeElementAt(tempIndex);
								if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
								{
									pubviews.removeElementAt(tempIndex);
								}
								unqualified_fields.removeElementAt(tempIndex);
							}	// for

							// reset the file number
							fileNumber = fileNumber-addedFileIndex.size();
						}	// if - there is at least one file failed to be uploaded
					}	// if	- state.getAttribute(STATE_MESSAGE) == null
				}   // if-else	- repeatedNames
			}   // if-else	- repeatedNames

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				// no error in adding the file
				state.setAttribute (STATE_MODE, MODE_LIST);
				initAddFileContext (state);
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector (MULTI_ADD_NUMBER + 1));
				
				HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
				String id = params.getString("collectionId");
				if(!currentMap.containsKey(id))
				{
					try
					{
						currentMap.put (id,ContentHostingService.getCollection (id));
						state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
						// add this folder id into the set to be event-observed
						addObservingPattern(id, state);
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
			}
			else
			{
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, unqualified_fields);
			}	// if-else: error message equals null?
		}

	}   // doAdd_file_basic

	/**
	* Set add file properties
	*/
	public void doSet_file_properties ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		String collectionId = (String) state.getAttribute (STATE_ADD_FILE_COLLECTION_ID);

		// default copyright
		String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

		int fileNumber = Integer.valueOf (params.getString ("fileNumber")).intValue ();
		state.setAttribute (STATE_ADD_FILE_NUMBER, new Integer (fileNumber));

		// the add file contet
		Vector osNames = (Vector) state.getAttribute (STATE_ADD_FILE_OS_NAME);
		Vector titles = emptyVector (MULTI_ADD_NUMBER + 1);
		Vector contents = (Vector) state.getAttribute(STATE_ADD_FILE_CONTENT);
		Vector types = (Vector) state.getAttribute(STATE_ADD_FILE_CONTENT_TYPE);
		Vector copyrights = emptyVector (MULTI_ADD_NUMBER + 1);
		
		// the error input fields
		Vector unqualified_fields = emptyVector (MULTI_ADD_NUMBER + 1);

		for (int i=1; i <= fileNumber; i++)
		{
			// unqualified field names
			String u_fields = "";

			FileItem fi = params.getFileItem ("filename" + i);


			if ((fi==null)&&(((String)osNames.get (i)).length ()==0))
			{
				// there is no upload file
				addAlert(state, rb.getString("choosefile") + " " + i + ". ");
				titles.set (i, NULL_STRING);
				u_fields = ResourceProperties.PROP_DISPLAY_NAME;
			}
			else
			{
				if (fi!=null)
				{
					// there is a upload file
					// Note: we use the FileItem's get() call to get a pointer to the actual bytes of the file
					// without making a copy of them.
					contents.set (i, fi.get ());
					osNames.set (i, fi.getFileName ());
					types.set (i, fi.getContentType ());
				}

				// get the file name
				String name = (params.getString ("title"+i)).trim();

				// the user did not fill in the optional field for the file name
				// use the original file name instead
				if (name.length ()==0)
				{
					// get the add file name
					name = Validator.getFileName ((String) osNames.get (i));
				}
				titles.set (i, name);

				copyrights.set (i, state.getAttribute (STATE_MY_COPYRIGHT));
			}	// if-else --- is file specified?
			unqualified_fields.add (i, u_fields);
		}	// for
		state.setAttribute (STATE_ADD_FILE_CONTENT, contents);
		state.setAttribute (STATE_ADD_FILE_CONTENT_TYPE, types);
		state.setAttribute (STATE_ADD_FILE_OS_NAME, osNames);
		state.setAttribute (STATE_ADD_FILE_TITLE, titles);
		state.setAttribute (STATE_ADD_FILE_COPYRIGHT, copyrights);

		int repeatedNames_index =  repeatedName (titles, fileNumber);
		if (repeatedNames_index>0)
		{
			String n = (String) titles.get (repeatedNames_index);
			if (n.length ()>0)
			{
				addAlert(state, rb.getString("notaddfile") + " " + n + ". ");
				unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index) + n);
			}
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// no error in retrieving the file data: title, inputstream, filenumber, etc
			state.setAttribute (STATE_MODE, MODE_ADD_FILE_OPTIONS);
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector (MULTI_ADD_NUMBER + 1));
		}
		else
		{
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, unqualified_fields);
		}	// if-else: error message equals null?

	}   // doSet_file_properties
	
	/**
	* Add folder
	*/
	public void doAddfolder ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String option = params.getString("option");
		if (option.equalsIgnoreCase("change"))
		{
			// change the folder number
			addItem(data, params);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel(data);
		}
		else
		{		
			// add folder
			String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);
			
			// get the parent collection id
			String collectionId = params.getString ("collectionId");

			state.setAttribute (STATE_ADD_FOLDER_COLLECTION_ID, collectionId);

			int folderNumber = Integer.valueOf (params.getString ("folderNumber")).intValue ();

			state.setAttribute (STATE_ADD_FOLDER_NUMBER, new Integer (folderNumber));

			Vector titles = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector descriptions = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector pubviews = emptyVector(MULTI_ADD_NUMBER + 1);
			if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
			{
				pubviews = (Vector) state.getAttribute(STATE_ADD_FOLDER_PUBVIEW);
			}
			Vector unqualified_fields = emptyVector (MULTI_ADD_NUMBER + 1);

			for (int i=1; i<= folderNumber; i++ )
			{
				// unqualified field names
				String u_fields = "";

				String name = (params.getString ("name" + i)).trim();
				String description = params.getCleanString ("description" + i);


				// is folder name specified?
				if (name.length ()==0)
				{
					addAlert(state, rb.getString("specifyfd") + " " + i +". ");
					u_fields = ResourceProperties.PROP_DISPLAY_NAME;
				}
				// set titles
				titles.add (i, name);

				//set descriptions
				descriptions.add (i, description);
				
				/*
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					String pubviewString = params.getString("pubview" + i);
					if (pubviewString != null)
					{
						pubviews.set(i, new Boolean(pubviewString)); 
					}
				}
				*/
				
				boolean pubview = false;
				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					pubview = params.getBoolean("pubview" + i);
					pubviews.set(i, new Boolean(pubview));
				}

				// set the unqualified field
				unqualified_fields.add (i, u_fields);
			}	// for

			state.setAttribute (STATE_ADD_FOLDER_TITLE, titles);
			state.setAttribute (STATE_ADD_FOLDER_DESCRIPTION, descriptions);

			if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
			{
				state.setAttribute (STATE_ADD_FOLDER_PUBVIEW, pubviews);
			}

			int  repeatedNames_index =  repeatedName (titles, folderNumber);
			if (repeatedNames_index > 0)
			{
				String n = (String) titles.get (repeatedNames_index);
				if (n.length ()>0)
				{
					addAlert(state, rb.getString("notaddfolder") + " " + n + ". ");
					unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index) + n);
				}
			}
			else
			{
				repeatedNames_index = foundInResource (titles, folderNumber, collectionId, true);

				if (repeatedNames_index>0)
				{
					String n = (String) titles.get (repeatedNames_index);
					addAlert(state," " + rb.getString("title") + " " + n + " " + rb.getString("offolder") + " " + repeatedNames_index + " " + rb.getString("used"));
					unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index)  + n);
				}
				else
				{
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
						// Vector titles = (Vector)state.getAttribute (STATE_ADD_FOLDER_TITLE);
						// Vector descriptions = (Vector)state.getAttribute (STATE_ADD_FOLDER_DESCRIPTION);
						for (int i=1; i <= folderNumber; i++)
						{
							// new folder resource id
							String newCollectionId = collectionId + Validator.escapeResourceName((String)((Vector)state.getAttribute (STATE_ADD_FOLDER_TITLE)).get (i)) + Resource.SEPARATOR;

							ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();

							try
							{
								resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, (String)titles.get (i));
								resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, (String)descriptions.get (i));
							
								ContentCollection collection = ContentHostingService.addCollection (newCollectionId, resourceProperties);

								state.setAttribute (STATE_ADD_FOLDER_NUMBER, new Integer (((Integer) state.getAttribute (STATE_ADD_FOLDER_NUMBER)).intValue ()-1));

								if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
								{
									// deal with pubview in resource mode//%%%
									boolean pubviewset = getPubViewInheritance(ContentHostingService.getReference(collection.getId()));
									if (!pubviewset)
									{
										setPubView(ContentHostingService.getReference(collection.getId()), ((Boolean) pubviews.get(i)).booleanValue());
									}
								}		
							}
							catch (IdUsedException e)
							{
								addAlert(state, rb.getString("title") + " " + ((Vector) state.getAttribute (STATE_ADD_FOLDER_TITLE)).get (i) +  " " + rb.getString("used1"));
							}
							catch (IdInvalidException e)
							{
								addAlert(state, rb.getString("title") + " " + e.getMessage ());
							}
							catch (PermissionException e)
							{
								addAlert(state, rb.getString("notpermis5") + " " + ((Vector) state.getAttribute (STATE_ADD_FOLDER_TITLE)).get (i) + ". ");
							}
							catch (InconsistentException e)
							{
								addAlert(state, RESOURCE_INVALID_TITLE_STRING);
							}	// try-catch
						}	// for
					}	// if
				}	// if-else
			}	// if-else

			if (state.getAttribute(STATE_MESSAGE) != null)
			{
				// add folder not sucessful
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, unqualified_fields);
			}
			else
			{
				// add folder sucessful
				state.setAttribute (STATE_MODE, MODE_LIST);
				initAddFolderContext (state);
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector (MULTI_ADD_NUMBER + 1));
				
				HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
				String id = params.getString("collectionId");
				if(!currentMap.containsKey(id))
				{
					try
					{
						currentMap.put (id,ContentHostingService.getCollection (id));
						state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
						// add this folder id into the set to be event-observed
						addObservingPattern(id, state);
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
			}	// if-else
		}	// if-else
		
	}	// doAddfolder

	/**
	* doDelete to delete the selected collection or resource items
	*/
	public void doDelete ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		ParameterParser params = data.getParameters ();

		Vector deleteIds = (Vector) state.getAttribute (STATE_DELETE_IDS);

		// delete the lowest item in the hireachy first
		Hashtable deleteItems = new Hashtable();
		String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		int maxDepth = 0;
		int depth = 0;
		String currentId = "";
		for (int i=0; i < deleteIds.size (); i++)
		{
			currentId = (String)deleteIds.elementAt (i);
			depth = ContentHostingService.getDepth(currentId, collectionId);
			if (depth > maxDepth)
			{
				maxDepth = depth;
			}
			Vector v = (Vector) deleteItems.get(new Integer(depth));
			if (v == null)
			{
				v = new Vector();
			}
			v.add(currentId);
			deleteItems.put(new Integer(depth), v);
		}	// for
		
		boolean isCollection = false;
		for (int j=maxDepth; j>0; j--)
		{
			Vector v = (Vector) deleteItems.get(new Integer(j));
			if (v!=null)
			{
				for (int k = 0; k<v.size (); k++)
				{
					currentId = (String) v.get (k);
					isCollection = false;
					String displayName = NULL_STRING;
					try
					{
						String collectionProperty = ContentHostingService.getProperties (currentId).getProperty (ResourceProperties.PROP_IS_COLLECTION);
						displayName = ContentHostingService.getProperties (currentId).getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
						
						if (collectionProperty == null)
						{
							isCollection = (new Boolean(collectionProperty)).booleanValue();
							if (isCollection)
							{
								ContentHostingService.removeCollection (currentId);
							}
							else
							{
								ContentHostingService.removeResource (currentId);
							}
						}
						else
						{
							// for those resource without properties; judge by id
							if (currentId.endsWith(Resource.SEPARATOR))
							{
								ContentHostingService.removeCollection (currentId);
							}
							else
							{
								ContentHostingService.removeResource(currentId);
							}
						}
					}
					catch (PermissionException e)
					{
						addAlert(state, rb.getString("notpermis6") + " " + displayName + ". ");
					}
					catch (IdUnusedException e)
					{
						addAlert(state,RESOURCE_NOT_EXIST_STRING);
					}
					catch (TypeException e)
					{
						addAlert(state, rb.getString("deleteres") + " " + displayName + " " + rb.getString("wrongtype"));
					}	
					catch (InUseException e)
					{
						addAlert(state, rb.getString("deleteres") + " " + displayName + " " + rb.getString("locked"));
					}// try - catch
				}	// for
			}	// if
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
	public void doCancel ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		String currentMode = (String) state.getAttribute (STATE_MODE);
		if (currentMode.equals (MODE_ADD_FILE_BASIC))
		{
			initAddFileContext (state);
		}
		else if (currentMode.equals (MODE_ADD_FOLDER))
		{
			initAddFolderContext (state);
		}
		else if (currentMode.equals (MODE_ADD_DOCUMENT_PLAINTEXT))
		{
			initAddDocumentContext (state);
		}
		else if (currentMode.equals (MODE_ADD_DOCUMENT_HTML))
		{
			initAddDocumentContext (state);
		}
		else if  (currentMode.equals (MODE_ADD_URL))
		{
			initAddURLContext (state);
		}
		else if  (currentMode.equals (MODE_REPLACE))
		{
			initReplaceContext (state);
		}
		else if  (currentMode.equals (MODE_PROPERTIES))
		{
			initPropertiesContext (state);
		}

		/*
		String collectionId = data.getParameters ().getString ("collectionId");
		if (collectionId != null)
		{
			state.setAttribute (STATE_COLLECTION_ID, collectionId);
		}
		*/
		
		String from = data.getParameters ().getString ("from");
		if (from != null)
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
						// paste the resource
						ContentResource resource = ContentHostingService.getResource (currentPasteCutItem);
						ResourceProperties p = ContentHostingService.getProperties(currentPasteCutItem);
						String id = collectionId + Validator.escapeResourceName(p.getProperty(ResourceProperties.PROP_DISPLAY_NAME));

						// cut-paste to the same collection?
						boolean cutPasteSameCollection = false;
						String displayName = p.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

						// count number
						int countNumber = 1;
						
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
							
					}	// if-else
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
				catch (TypeException e)
				{
					addAlert(state, rb.getString("pasteitem") + " " + originalDisplayName + " " + rb.getString("mismatch"));
				}	// try-catch

			}	// for
		}	// cut

		// handling copy and paste
		if (((String) state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
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
			if (((String)state.getAttribute (STATE_COPY_FLAG)).equals (Boolean.TRUE.toString()))
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
	
						// add successful?
						boolean addResourceSuccess = false;
						boolean addResourceFailed = false;
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
							addResourceSuccess = true;
						}
						catch (InconsistentException e)
						{
							addAlert(state, RESOURCE_INVALID_TITLE_STRING);
							addResourceFailed = true;
						}
						catch (OverQuotaException e)
						{
							addAlert(state, rb.getString("overquota"));
							addResourceFailed = true;
						}
						catch (IdInvalidException e)
						{
							addAlert(state, rb.getString("title") + " " + e.getMessage ());
							addResourceFailed = true;
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
					addAlert(state, rb.getString("notpermis9") + " " +  currentPasteItem.substring (currentPasteItem.lastIndexOf (Resource.SEPARATOR)+1) + ". ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (TypeException e)
				{
					addAlert(state, rb.getString("pasteitem") + " " +  currentPasteItem.substring (currentPasteItem.lastIndexOf (Resource.SEPARATOR)+1) + " " + rb.getString("mismatch"));
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
	public void doEdit ( RunData data )
	{
		ParameterParser params = data.getParameters ();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

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
			if(dummyId.endsWith(Resource.SEPARATOR))
			{
				dummyId += "dummy";
			}
			else
			{
				dummyId += Resource.SEPARATOR + "dummy";
			}
			
			String containerId = ContentHostingService.getContainingCollectionId (id);
			item.setContainer(containerId);
			
			boolean canRead = ContentHostingService.allowGetCollection(id);
			boolean canAddFolder = ContentHostingService.allowAddCollection(id);
			boolean canAddItem = ContentHostingService.allowAddResource(id);
			boolean canDelete = ContentHostingService.allowRemoveResource(id);
			boolean canRevise = ContentHostingService.allowUpdateResource(id);
			boolean canCopy = ContentHostingService.allowGetCollection(id);
			// boolean isUrl = (ResourceProperties.TYPE_URL.equals(itemType));

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
	
			String size = properties.getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH) + " (" + Validator.getFileSizeWithDividor(properties.getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH)) +" bytes)";
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
			
			String collectionReference = ContentHostingService.getReference(containerId);
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			item.setPubviewset(pubviewset);
			boolean pubview = pubviewset;
			if (!pubview)
			{
				pubview = getPubView(ContentHostingService.getReference(id));
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
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// got resource and sucessfully populated item with values
			state.setAttribute (STATE_MODE, MODE_EDIT);
		}
		
	}	// doEdit

	/**
	* Edit the editable collection/resource properties
	*/
	public void doProperties ( RunData data)
	{
		ParameterParser params = data.getParameters ();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

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

			String displayName = NULL_STRING;

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
							filename = fileitem.getFileName();
							
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
					pubviewset = getPubViewInheritance(ContentHostingService.getReference(id));
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
							setPubView(cedit.getReference(), pubview);
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
							setPubView(redit.getReference(), pubview);
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
	
	/**
	 * @param state
	 * @param params
	 * @param item
	 */
	protected void captureValues(SessionState state, ParameterParser params)
	{
		EditItem item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);
		String intent = params.getString("intent");

		String oldintent = (String) state.getAttribute(STATE_EDIT_INTENT);
		boolean intent_has_changed = (item.isHtml() || item.isPlaintext()) && !intent.equals(oldintent);
		
		String name = params.getString("name");
		if(name == null)
		{
			addAlert(state, rb.getString("titlenotnull"));
		}
		else
		{
			item.setName(name);
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

		// this condition is satisfied if the value of "intent" is the same as before 
		// OR if the item is not plain-text or html
		if( ! intent_has_changed )
		{
			// check for input from editor (textarea)
			String content = params.getString("content");
			if(content != null)
			{
				item.setContent(content);
				item.setContentHasChanged(true);
			}
			
			// check for file replacement
			FileItem fileitem = params.getFileItem("fileName");
			if(fileitem == null)
			{
				// "The user submitted a file to upload but it was too big!"
			}
			else if (fileitem.getFileName() == null || fileitem.getFileName().length() == 0)
			{
			   // "The user submitted the form, but didn't select a file to upload!"
			}
			else if (fileitem.getFileName().length() > 0)
			{
				String filename = fileitem.getFileName();
				byte[] bytes = fileitem.get();
				String contenttype = fileitem.getContentType();
				
				if(bytes.length > 0)
				{
					item.setContent(bytes);
					item.setContentHasChanged(true);
					item.setMimeType(contenttype);
					item.setFilename(filename);									
				}
			}
		}
		if(item.isUrl())
		{
			String url = params.getString("Url");
			if(url == null || url.equals(""))
			{
				item.setFilename("");
			}
			else
			{
				if(!url.equals(NULL_STRING))
				{
					// valid protocol?
					try
					{
						URL u = new URL(url);
						item.setFilename(url);
					}
					catch (MalformedURLException e1)
					{
						try
						{
							Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
							Matcher matcher = pattern.matcher(url);
							if(matcher.matches())
							{
								URL test = new URL("http://" + matcher.group(2));
								item.setFilename(url);							}
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
		else
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
						addAlert(state, rb.getString("specifycp2"));
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


	}	// captureValues
	
	/**
	 * @param state
	 * @param params
	 * @param item
	 */
	protected void captureMultipleValues(SessionState state, ParameterParser params, boolean markMissing)
	{
		Integer numberOfItems = (Integer) state.getAttribute(STATE_CREATE_NUMBER);
		List items = (List) state.getAttribute(STATE_CREATE_ITEMS);
		Set alerts = (Set) state.getAttribute(STATE_CREATE_ALERTS);
		
		for(int i = 0; i < numberOfItems.intValue(); i++)
		{
			CreateItem item = (CreateItem) items.get(i);
			item.clearMissing();

			String name = params.getString("name" + i);
			if(name == null || name.trim().equals(""))
			{
				if(markMissing)
				{
					alerts.add(rb.getString("titlenotnull"));
					item.setMissing("name");
				}
				item.setName("");
				// addAlert(state, rb.getString("titlenotnull"));
			}
			else
			{
				item.setName(name);
			}
			
			String description = params.getString("description" + i);
			if(description == null || description.trim().equals(""))
			{
				item.setDescription("");
			}
			else
			{
				item.setDescription(description);
			}
			
			item.setContentHasChanged(false);
			
			if(item.isFileUpload())
			{
				// check for file replacement
				FileItem fileitem = null;
				try
				{
					fileitem = params.getFileItem("fileName" + i);
					
				}
				catch(Exception e)
				{
					// this is an error in Firefox, Mozilla and Netscape
					// "The user didn't select a file to upload!"
					if(item.getContent() == null || item.getContent().length <= 0)
					{
						alerts.add(rb.getString("choosefile") + " " + (i + 1) + ". ");
						item.setMissing("fileName");
					}
				}
				if(fileitem == null)
				{
					// "The user submitted a file to upload but it was too big!"
					alerts.add(rb.getString("size") + " " + state.getAttribute(FILE_UPLOAD_MAX_SIZE) + "MB " + rb.getString("exceeded2"));
					item.setMissing("fileName");
				}
				else if (fileitem.getFileName() == null || fileitem.getFileName().length() == 0)
				{
					if(item.getContent() == null || item.getContent().length <= 0)
					{
						// "The user submitted the form, but didn't select a file to upload!"
						alerts.add(rb.getString("choosefile") + " " + (i + 1) + ". ");
						item.setMissing("fileName");
					}
				}
				else if (fileitem.getFileName().length() > 0)
				{
					String filename = fileitem.getFileName();
					byte[] bytes = fileitem.get();
					String contenttype = fileitem.getContentType();
					
					if(bytes.length > 0)
					{
						item.setContent(bytes);
						item.setContentHasChanged(true);
						item.setMimeType(contenttype);
						item.setFilename(filename);									
					}
					else 
					{
						alerts.add(rb.getString("choosefile") + " " + (i + 1) + ". ");
						item.setMissing("fileName");
					}
				}
			}
			else if(item.isPlaintext())
			{
				// check for input from editor (textarea)
				String content = params.getString("content" + i);
				if(content != null)
				{
					item.setContentHasChanged(true);
					item.setContent(content);
				}
				item.setMimeType(MIME_TYPE_DOCUMENT_PLAINTEXT);
			}
			else if(item.isHtml())
			{
				// check for input from editor (textarea)
				String content = params.getCleanString("content" + i);
				StringBuffer alertMsg = new StringBuffer();
				content = FormattedText.processHtmlDocument(content, alertMsg);
				if (alertMsg.length() > 0)
				{
					alerts.add(alertMsg.toString());
				}
				if(content != null && !content.equals(""))
				{
					item.setContent(content);
					item.setContentHasChanged(true);
				}
				item.setMimeType(MIME_TYPE_DOCUMENT_HTML);
			}
			else if(item.isUrl())
			{
				String url = params.getString("Url" + i);
				if(url == null || url.equals(""))
				{
					item.setFilename("");
					alerts.add(rb.getString("specifyurl"));
					item.setMissing("Url");
				}
				else
				{
					if(!url.equals(NULL_STRING))
					{
						// valid protocol?
						try
						{
							URL u = new URL(url);
							item.setFilename(url);
						}
						catch (MalformedURLException e1)
						{
							try
							{
								Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
								Matcher matcher = pattern.matcher(url);
								if(matcher.matches())
								{
									URL test = new URL("http://" + matcher.group(2));
									item.setFilename(url);							}
								else
								{
									// invalid url
									alerts.add(rb.getString("validurl"));
									item.setMissing("Url");
									// addAlert(state, rb.getString("validurl"));
								}
							}
							catch (MalformedURLException e2)
							{
								// invalid url
								alerts.add(rb.getString("validurl"));
								item.setMissing("Url");
								// addAlert(state, rb.getString("validurl"));
							}
						}
						
					}
				}
			}
			else if(item.isStructuredArtifact())
			{
				String formtype = (String) state.getAttribute(STATE_STRUCTOBJ_TYPE);
				List properties = (List) state.getAttribute(STATE_STRUCTOBJ_PROPERTIES);
				
				String formtype_check = params.getString("formtype");
				
				if(formtype_check == null || formtype_check.equals(""))
				{
					alerts.add("Must select a form type");
					item.setMissing("formtype");
				}
				else if(formtype_check.equals(formtype))
				{
					Iterator it = properties.iterator();
					while(it.hasNext())
					{
						ResourcesMetadata prop = (ResourcesMetadata) it.next();
						int count = prop.getCurrentCount();
						for(int j = 0; j < count; j++)
						{
							if(ResourcesMetadata.WIDGET_DATE.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_DATETIME.equals(prop.getWidget()) || ResourcesMetadata.WIDGET_TIME.equals(prop.getWidget()))
							{
								String propname = prop.getFullname() + "_" + j;
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
									year = params.getInt(propname + "_" + i + "_year", year);
									month = params.getInt(propname + "_" + i + "_month", month);
									day = params.getInt(propname + "_" + i + "_day", day);
								}
								if(prop.getWidget().equals(ResourcesMetadata.WIDGET_TIME) || 
									prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
								{
									hour = params.getInt(propname + "_" + i + "_hour", hour);
									minute = params.getInt(propname + "_" + i + "_minute", minute);
									second = params.getInt(propname + "_" + i + "_second", second);
									millisecond = params.getInt(propname + "_" + i + "_millisecond", millisecond);
									ampm = params.getString(propname + "_" + i + "_ampm").trim();

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
								item.setValue(prop.getLocalname(), j, value);
							}
							else
							{
								String value = params.getString(prop.getFullname() + "_" + j + "_" + i);
								item.setValue(prop.getLocalname(), j, value);
							}
						}
					}
				}

			}
			if(item.isFileUpload() || item.isHtml() || item.isPlaintext())
			{
				// check for copyright status
				// check for copyright info
				// check for copyright alert
				
				String copyrightStatus = StringUtil.trimToNull(params.getString ("copyright" + i));
				String copyrightInfo = StringUtil.trimToNull(params.getCleanString ("newcopyright" + i));
				String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert" + i));

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
					pubview = params.getBoolean("pubview" + i);
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
				String notification = params.getString("notify" + i);
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
									year = params.getInt(propname + "_" + i + "_year", year);
									month = params.getInt(propname + "_" + i + "_month", month);
									day = params.getInt(propname + "_" + i + "_day", day);
								}
								if(prop.getWidget().equals(ResourcesMetadata.WIDGET_TIME) || 
									prop.getWidget().equals(ResourcesMetadata.WIDGET_DATETIME))
								{
									hour = params.getInt(propname + "_" + i + "_hour", hour);
									minute = params.getInt(propname + "_" + i + "_minute", minute);
									second = params.getInt(propname + "_" + i + "_second", second);
									millisecond = params.getInt(propname + "_" + i + "_millisecond", millisecond);
									ampm = params.getString(propname + "_" + i + "_ampm").trim();

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
								String value = params.getString(propname + "_" + i);
								if(value != null)
								{
									item.setMetadataItem(propname, value);
								}
							}
						}
					}
				}
			}
			
		}
		state.setAttribute(STATE_CREATE_ALERTS, alerts);

	}	// captureMultipleValues
	
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
		

		captureValues(state, params);
		

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
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			EditItem item = (EditItem) state.getAttribute(STATE_EDIT_ITEM);
			boolean intent_has_changed = false;

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
								addAlert(state, rb.getString("specifycp2"));
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
				
				saveMetadata(pedit, metadataGroups, item);

				// commit the change
				if (cedit != null)
				{
					ContentHostingService.commitCollection(cedit);
					
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// when in resource mode
						if (!item.isPubviewset())
						{
							setPubView(cedit.getReference(), item.isPubview());
						}
					}
				}
				else
				{
					ContentHostingService.commitResource(redit, item.getNotification());
					
					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// when in resource mode
						if (!item.isPubviewset())
						{
							setPubView(redit.getReference(), item.isPubview());
						}
					}
				}
				
			}
			catch (TypeException e)
			{
				addAlert(state," " + rb.getString("typeex") + " "  + item.getId());
			}
			catch (IdUnusedException e)
			{
				addAlert(state,RESOURCE_NOT_EXIST_STRING);
			}
			catch (PermissionException e)
			{
				addAlert(state, rb.getString("notpermis10") + " " + item.getId() + ". " );
			}
			catch (InUseException e)
			{
				addAlert(state, rb.getString("someone") + " " + item.getId() + ". ");
			}
			catch (OverQuotaException e)
			{
				addAlert(state, rb.getString("changing1") + " " + item.getId() + " " + rb.getString("changing2"));
			}
		}	// if - else

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// modify properties sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			// clear state variables
			initPropertiesContext(state);
		}	//if-else

	}	// doSavechanges
	
	/**
	 * @param pedit
	 * @param metadataGroups
	 * @param metadata
	 */
	private void saveMetadata(ResourcePropertiesEdit pedit, List metadataGroups, EditItem item) 
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
	public void doHide_metadata(RunData data)
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
	* doReplacefile replace the selected file with a new file
	*/
	public void doReplacefile ( RunData data)
	{
		ParameterParser params = data.getParameters ();
		String option = params.getString("option");
		if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel(data);
		}
		else if (option.equalsIgnoreCase("replace"))
		{
			//replace file
			SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
			
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
	
			// get parent folder id and folder name
			String collectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
	
			// get the list of items to be replaced
			Vector replaceItems = (Vector) state.getAttribute (STATE_REPLACE_ID);
	
			int r_size = replaceItems.size ()-1;
	
			Vector unqualified_fields = (Vector) emptyVector (r_size).clone ();
	
			Vector osNames = (Vector) state.getAttribute (STATE_REPLACE_OS_NAME);
			Vector titles = (Vector) state.getAttribute (STATE_REPLACE_TITLE);
			Vector contents = (Vector) state.getAttribute (STATE_REPLACE_CONTENT);
			Vector types = (Vector) state.getAttribute (STATE_REPLACE_CONTENT_TYPE);
	
			for (int i=1; i <= r_size; i++)
			{
				String currentReplaceItem = (String) replaceItems.get (i);
				
				if (currentReplaceItem.length ()!=0)
				{
					String u_fields = "";
					
					FileItem fi = params.getFileItem ("fileName" + i);
	
					if ((fi==null)&&(((String)osNames.get (i)).length ()==0))
					{
						// no new file specified to replace the old one
						addAlert(state, rb.getString("specifyrepla"));
						u_fields = u_fields + ResourceProperties.PROP_DISPLAY_NAME;
					}
					else
					{
						if (fi!=null)
						{
							// there is a upload file
							contents.set (i, fi.get ());
	
							osNames.set (i, fi.getFileName ());
	
							types.set (i, fi.getContentType ());
	
						}
	
						String title = (params.getString ("title" + i)).trim();
						if (title.length ()==0)
						{
							// if the title field is empty, use the OS name of the mew file
							title = Validator.getFileName ((String) osNames.get (i));
						}
						titles.set (i, title);
	
					}	// if-else
					unqualified_fields.add (i, u_fields);
				}
			}	// for
	
			state.setAttribute (STATE_REPLACE_CONTENT, contents);
	
			state.setAttribute (STATE_REPLACE_CONTENT_TYPE, types);
	
			state.setAttribute (STATE_REPLACE_OS_NAME, osNames);
	
			state.setAttribute (STATE_REPLACE_TITLE, titles);
	
			int repeatedNames_index =  repeatedName (titles, r_size);
			if (repeatedNames_index >0)
			{
				String n = (String) titles.get (repeatedNames_index);
				if (n.length ()>0)
				{
					addAlert(state, rb.getString("notaddfile2") + " " + n + ". ");
					unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index) + n);
				}
			}
			else
			{			
				repeatedNames_index = foundInResource (titles, r_size, collectionId, false);
				if (repeatedNames_index>0)
				{
					try
					{
						String n = (String) titles.get (repeatedNames_index);
						String s = ContentHostingService.getProperties((String)replaceItems.get(repeatedNames_index)).getProperty(ResourceProperties.PROP_DISPLAY_NAME);
	
						// repeated title is in the resource but not to itself
						if (n.length ()>0 && (!s.equalsIgnoreCase (n)))
						{
							addAlert(state," " + rb.getString("title") + " " + n + " " + rb.getString("ofrepfile") + repeatedNames_index + " " + rb.getString("used"));
							unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index)  + n);
						}	// if
					}
					catch (PermissionException e)
					{
						addAlert(state, rb.getString("notview"));
					}
					catch (IdUnusedException e)
					{
						addAlert(state, rb.getString("cannotfind2"));
					}
				}	// if
			}	// if-else
			
			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				for (int i=1; i<=r_size; i++)
				{
					String currentReplaceItem = (String) replaceItems.get (i);
					if (((String)replaceItems.get (i)).length ()!=0)
					{
						String displayName = NULL_STRING;
						try
						{
							// get an edit
							ContentResourceEdit edit = ContentHostingService.editResource(currentReplaceItem);
							
							// update
							edit.setContentType((String) ((Vector)state.getAttribute (STATE_REPLACE_CONTENT_TYPE)).get (i));
							edit.setContent((byte[]) ((Vector)state.getAttribute (STATE_REPLACE_CONTENT)).get (i));
							edit.getPropertiesEdit().addProperty (
									ResourceProperties.PROP_DISPLAY_NAME,
									(String) ((Vector)state.getAttribute (STATE_REPLACE_TITLE)).get (i));
							ContentHostingService.commitResource(edit, noti);
						}
						catch (InUseException e)
						{
							addAlert(state, rb.getString("someone") + " " + displayName + ". ");
						}
						catch (PermissionException e)
						{
							addAlert(state, rb.getString("notpermis11") + " " + displayName + ". ");
						}
						catch (OverQuotaException e)
						{
							addAlert(state, rb.getString("changing1") + " " + displayName + " " + rb.getString("changing2"));
						}
						catch (IdUnusedException e)
						{
							addAlert(state,RESOURCE_NOT_EXIST_STRING);
						}
						catch (TypeException e)
						{
							addAlert(state, rb.getString("replacereso") + " "  + displayName + " " + rb.getString("typeerror"));
						}	// try-catch
					}	// if
				}	// for
			}	// if
	
			if (state.getAttribute(STATE_MESSAGE) != null)
			{
				// replace file not sucessful
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, unqualified_fields);
			}
			else
			{
				// replace file sucessful
				state.setAttribute (STATE_MODE, MODE_LIST);
				if (((String) state.getAttribute (STATE_SELECT_ALL_FLAG)).equals (Boolean.TRUE.toString()))
				{
					state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
				}
	
				initReplaceContext (state);
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector (MULTI_ADD_NUMBER + 1));
	
			}	// if-else
		}

	}	// doReplacefile

	public void doAdddocumentplaintext(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String content = params.getCleanString ("content");
		state.setAttribute (STATE_ADD_DOCUMENT_CONTENT, content);

		doAdddocument(data, MIME_TYPE_DOCUMENT_PLAINTEXT, content);
	}
	
	public void doAdddocumenthtml(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String content = params.getCleanString ("content");
		
		content = processHtmlDocumentFromBrowser(state, content);
				
		state.setAttribute (STATE_ADD_DOCUMENT_CONTENT, content);

		doAdddocument(data, MIME_TYPE_DOCUMENT_HTML, content);
	}	
	
	/**
	* Add document file
	*/
	private void doAdddocument ( RunData data, String contentType, String content)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();

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

		String collectionId = NULL_STRING;
		InputStream inputStream = null;
		
		String copyright = StringUtil.trimToNull(params.getString ("copyright"));
		if (copyright != null)
		{
			state.setAttribute (STATE_ADD_DOCUMENT_COPYRIGHT, copyright);
		}
		
		String newcopyright = StringUtil.trimToNull(params.getCleanString (NEW_COPYRIGHT));
		if (newcopyright != null)
		{
			state.setAttribute (STATE_ADD_DOCUMENT_NEW_COPYRIGHT, newcopyright);
		}
		
		String copyrightAlert = StringUtil.trimToNull(params.getString("copyrightAlert"));
		if (copyrightAlert != null)
		{
			state.setAttribute(STATE_ADD_DOCUMENT_COPYRIGHT_ALERT, copyrightAlert);
		}
		
		/*
		String pubviewString = "";
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			pubviewString = params.getString("pubview");
			if (pubviewString != null)
			{
				state.setAttribute(STATE_ADD_DOCUMENT_PUBVIEW, new Boolean(pubviewString));
			}
		}
		*/
		
		boolean pubview = false;
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			pubview = params.getBoolean("pubview");
			state.setAttribute(STATE_ADD_DOCUMENT_PUBVIEW, new Boolean(pubview));
		}

		// get parent folder id and folder name
		collectionId = params.getString ("collectionId");

		// get the add file title
		String title = (params.getString ("title")).trim();
		state.setAttribute (STATE_ADD_DOCUMENT_TITLE, title);

		// get the add file description
		String description = params.getCleanString ("description");
		state.setAttribute (STATE_ADD_DOCUMENT_DESCRIPTION, description);

		// the unqualify fields
		String u_fields = "";
		if (title.equals (NULL_STRING))
		{
			// the document file display name has not been specified
			addAlert(state, rb.getString("specifytitle"));
			u_fields = u_fields + ResourceProperties.PROP_DISPLAY_NAME;
		}
		if (content.equals (NULL_STRING))
		{
			addAlert(state, rb.getString("specifyfile2"));
			u_fields = u_fields + "content";
		}
		if (copyright!=null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && newcopyright == null)
		{
			// there is no input for the new copyright
			addAlert(state, rb.getString("specifycp2"));
			u_fields = u_fields + ResourceProperties.PROP_COPYRIGHT;
		}
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// add the document file

			// new resource id
			String newResourceId = collectionId + Validator.escapeResourceName(title);

			Vector titles = new Vector ();
			titles.add (0, "");
			titles.add (1, title);

			// test whether the title has ready exist
			int repeatedNames_index = foundInResource (titles, 1, collectionId, false);

			if (repeatedNames_index!=0)
			{
				u_fields = u_fields + " " + title;
				addAlert(state," " + rb.getString("title")+ " " + title + " " + rb.getString("used3"));
			}
			else
			{
				ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
				resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, title);
				resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
				resourceProperties.addProperty (ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
				
				// copyright
				if (copyright != null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && newcopyright != null)
				{
					resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, newcopyright);
				}
				else if (copyright != null && state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyright.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
				{
					resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
				}
				if (copyright != null)
				{
					resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, copyright);
				}
				if (copyrightAlert != null)
				{
					resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, copyrightAlert);
				}
				
				try
				{
					byte[] contentBytes;
					try
					{
						contentBytes = content.getBytes("UTF-8");
					}
					catch (UnsupportedEncodingException e)
					{
						contentBytes = content.getBytes();
						e.printStackTrace();
					}
					ContentResource resource = ContentHostingService.addResource (newResourceId, contentType, contentBytes, resourceProperties, noti);

					if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
					{
						// deal with pubview in resource mode //%%%
						boolean pubviewset = getPubViewInheritance(ContentHostingService.getReference(resource.getId()));
						if (!pubviewset)
						{
							setPubView(ContentHostingService.getReference(resource.getId()), pubview);
						}
					}
				}
				catch (IdUsedException e)
				{
					addAlert(state, rb.getString("resotitle") + " " + title + " " + rb.getString("used4"));
				}
				catch (IdInvalidException e)
				{
					addAlert(state, rb.getString("title") + " " + title + " " + rb.getString("invalid"));
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("nopermis12"));
				}
				catch (OverQuotaException e)
				{
					addAlert(state, rb.getString("overquota"));
				}
				catch (InconsistentException e)
				{
					addAlert(state, RESOURCE_INVALID_TITLE_STRING);
				}	// try-catch
			}

		}	// if-else

		if (state.getAttribute(STATE_MESSAGE) != null)
		{
			// add document file not sucessful

			// set the unqualified fields
			Vector v = (Vector) emptyVector (1).clone ();
			v.set (1, u_fields);
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, v);
		}
		else
		{
			// add document file sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			initAddDocumentContext (state);

			// clear the unqualified fields
			state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, (Vector) emptyVector (1).clone ());

			// make sure the user can view contents of enclosing folder
			HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
			String id = params.getString("collectionId");
			if(!currentMap.containsKey(id))
			{
				try
				{
					currentMap.put (id,ContentHostingService.getCollection (id));
					state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
					// add this folder id into the set to be event-observed
					addObservingPattern(id, state);
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
		}	// if-else
		
	}	// doAdddocument

	/**
	* Add Url
	*/
	public void doAddurl ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		// cancel copy if there is one in progress
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		// get the submit option
		String option = params.getString("option");
		if (option.equalsIgnoreCase("change"))
		{
			// change url number
			addItem(data, params);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel(data);
		}
		else
		{
			// add url(s)
			String mycopyright = (String) state.getAttribute (STATE_MY_COPYRIGHT);
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

			// get the parent collection id
			String collectionId = params.getString ("collectionId");

			// state.setAttribute (STATE_COLLECTION_ID, collectionId);
			

			int UrlNumber = Integer.valueOf (params.getString ("urlNumber")).intValue ();

			state.setAttribute (STATE_ADD_URL_NUMBER, new Integer (UrlNumber));

			// initialize the add url context
			Vector urls = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector titles = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector descriptions = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector copyrights = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector copyrightAlerts = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector newcopyrights = emptyVector(MULTI_ADD_NUMBER + 1);
			Vector unqualified_fields = emptyVector (MULTI_ADD_NUMBER + 1);
			Vector pubviews = emptyVector (MULTI_ADD_NUMBER + 1);
			if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
			{
				pubviews = (Vector) state.getAttribute(STATE_ADD_URL_PUBVIEW);
			}


			boolean sameURLName = false;
			for (int i=1; i<= UrlNumber; i++ )
			{
				// the unqualified field names
				String u_fields = "";

				// the URL web address
				String url = (params.getString ("Url" + i)).trim();
				if (url.equals (NULL_STRING))
				{
					// the url field is null
					addAlert(state, rb.getString("specifyurl"));

					u_fields = u_fields + "URLcontent";
				}
				else if (url.indexOf ("://") == -1)
				{
					// if it's missing the transport, add http://
					url = "http://" + url;
				}

				// valid protocol?
				try
				{
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
							URL test = new URL("http://" + matcher.group(2));
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
				urls.set (i, url);

				// the URL title
				String title = (params.getString ("title" + i)).trim();
				if (title.equals (NULL_STRING))
				{
					// the name field is null, get the url substring as the title, www.google.com for http://www.google.com for example.
					title = url;
				}
				titles.set (i, title);

				// the URL description
				String description = params.getCleanString ("description" + i);
				descriptions.set (i, description);

				//set the copyright
				String copyright = StringUtil.trimToNull(params.getString ("copyright" + i));
				if (copyright!=null)
				{
					copyrights.set (i, copyright);
				}

				String newcopyright = StringUtil.trimToNull(params.getCleanString (NEW_COPYRIGHT + i));
				if (newcopyright!=null)
				{
					newcopyrights.set (i, newcopyright);
				}

				if (params.getString("copyrightAlert" + i) != null)
				{
					copyrightAlerts.set(i, Boolean.TRUE.toString());
				}
				else
				{
					copyrightAlerts.set(i, null);
				}

				if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
				{
					boolean pubview = false;
					pubview = params.getBoolean("pubview" + i);
					pubviews.set(i, new Boolean(pubview));
					/*
					// set pubview
					String pubviewString = params.getString("pubview" + i);
					if (pubviewString != null)
					{
						pubviews.set(i, new Boolean(pubviewString)); 
					}
					*/
				}

				if (copyright != null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyright.equals(state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && newcopyright == null)
				{
					addAlert(state, rb.getString("specifycp3") + " " + i + ". ");
				}
				
				unqualified_fields.set (i, u_fields);
			}	// for

			state.setAttribute (STATE_ADD_URL_URL, urls);
			state.setAttribute (STATE_ADD_URL_TITLE, titles);
			state.setAttribute (STATE_ADD_URL_DESCRIPTION, descriptions);
			state.setAttribute (STATE_ADD_URL_COPYRIGHT, copyrights);
			state.setAttribute (STATE_ADD_URL_COPYRIGHT_ALERT, copyrightAlerts);
			state.setAttribute (STATE_ADD_URL_NEW_COPYRIGHT, newcopyrights);

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				int  repeatedNames_index =  repeatedName (titles, UrlNumber);
				if (repeatedNames_index > 0)
				{
					String n = (String) titles.get (repeatedNames_index);
					if (n.length ()>0)
					{
						addAlert(state, rb.getString("notaddurl") + " " + n + ". ");
						unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index) + n);
					}
				}
				else
				{
					repeatedNames_index = foundInResource (titles, UrlNumber, collectionId, false);

					if (repeatedNames_index>0)
					{
						String n = (String) titles.get (repeatedNames_index);
						addAlert(state, " " + rb.getString("title") + " " + n + " " + rb.getString("ofurl") + repeatedNames_index + " " + rb.getString("used"));
						unqualified_fields.set (repeatedNames_index, (String) unqualified_fields.get (repeatedNames_index)  + n);
					}
					else
					{
						for (int i=1; i<=UrlNumber; i++)
						{

							// new Url resource id
							String currentTitle = (String) ((Vector) state.getAttribute (STATE_ADD_URL_TITLE)).get (i);
							String newResourceId = collectionId + Validator.escapeResourceName (currentTitle);

							ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties ();
							resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, currentTitle);
							resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, (String) ((Vector) state.getAttribute (STATE_ADD_URL_DESCRIPTION)).get (i));
							
							// copyrights
							String copyrightChoice = StringUtil.trimToNull((String) copyrights.get (i));
							if (copyrightChoice != null && state.getAttribute(COPYRIGHT_NEW_COPYRIGHT) != null && copyrightChoice.equals (state.getAttribute(COPYRIGHT_NEW_COPYRIGHT)) && StringUtil.trimToNull((String) newcopyrights.get (i)) != null)
							{
								resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, (String) newcopyrights.get (i));
							}
							else if (copyrightChoice != null && state.getAttribute(COPYRIGHT_SELF_COPYRIGHT) != null && copyrightChoice.equals (state.getAttribute(COPYRIGHT_SELF_COPYRIGHT)))
							{
								resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
							}
							resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT_CHOICE, (String) copyrights.get(i));
							if ((String) copyrightAlerts.get(i) != null)
							{
								resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT_ALERT, (String) copyrightAlerts.get(i));
							}

							try
							{
								ContentResource resource = ContentHostingService.addResource (newResourceId, ResourceProperties.TYPE_URL, ((String) ((Vector) state.getAttribute (STATE_ADD_URL_URL)).get (i)).getBytes (), resourceProperties, noti);

								if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
								{
									// deal with pubview in resource mode//%%%
									boolean pubviewset = getPubViewInheritance(ContentHostingService.getReference(resource.getId()));
									if (!pubviewset)
									{
										setPubView(ContentHostingService.getReference(resource.getId()), ((Boolean) pubviews.get(i)).booleanValue());
									}
								}
							}
							catch (IdUsedException e)
							{
								addAlert(state, rb.getString("titleurl") + " " + currentTitle + " " + rb.getString("used4"));
							}
							catch (IdInvalidException e)
							{
								addAlert(state, rb.getString("titleurl") + " " + e.getMessage ());
							}
							catch (PermissionException e)
							{
								addAlert(state, rb.getString("notpermis13") + currentTitle + ". ");
							}
							catch (OverQuotaException e)
							{
								addAlert(state, rb.getString("overquota"));
							}
							catch (InconsistentException e)
							{
								addAlert(state, RESOURCE_INVALID_TITLE_STRING);
							}	// try-catch
						}	// for
					}	// if-else
				}	// if-else
			}	// if

			if (state.getAttribute(STATE_MESSAGE) != null)
			{
				// add url not sucessful
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, unqualified_fields);
			}
			else
			{
				// add url sucessful
				state.setAttribute (STATE_MODE, MODE_LIST);
				initAddURLContext (state);
				state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector (MULTI_ADD_NUMBER + 1));
				
				HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);		
				String id = params.getString("collectionId");
				if(!currentMap.containsKey(id))
				{
					try
					{
						currentMap.put (id,ContentHostingService.getCollection (id));
						state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
						// add this folder id into the set to be event-observed
						addObservingPattern(id, state);
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
			}	// if-else
		}	// if-else

	}	// doAddurl

	/**
	* Sort based on the given property
	*/
	public void doSort ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();

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

		String[] deleteIds = data.getParameters ().getStrings ("selectedMembers");
		if (deleteIds == null)
		{
			// there is no resource selected, show the alert message to the user
			addAlert(state, rb.getString("choosefile3"));
		}
		else
		{
			Vector deleteIdsVector = new Vector ();
			String notEmptyDeleteIds = NULL_STRING;
			String nonDeleteIds = NULL_STRING;
			for (int i=0; i<deleteIds.length; i++)
			{
				String currentId = deleteIds[i];
				try
				{
					ResourceProperties p = ContentHostingService.getProperties (currentId);
					String name = p.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
					if (p.getPropertyFormatted (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						if (ContentHostingService.allowRemoveCollection (currentId))
						{
							deleteIdsVector.add (currentId);
							
							// check for if the collection contains any resource
							ContentCollection c = ContentHostingService.getCollection(currentId);
							Iterator iterator = c.getMembers().iterator();
							if (iterator.hasNext ())
							{
								if (notEmptyDeleteIds.length ()>0)
								{
									notEmptyDeleteIds = notEmptyDeleteIds + ", " + name;
								}
								else
								{
									notEmptyDeleteIds = name;
								}
							}
						}
						else
						{
							if (nonDeleteIds.length ()>0)
							{
								nonDeleteIds = nonDeleteIds + ", " +  name;	
							}
							else
							{
								// first item
								nonDeleteIds = name;
							}
						}
					}
					else
					{
						if (ContentHostingService.allowRemoveResource (currentId))
						{
							deleteIdsVector.add (currentId);
						}
						else
						{
							if (nonDeleteIds.length ()>0)
							{
								nonDeleteIds = nonDeleteIds + "; " +  name;	
							}
							else
							{
								// first item
								nonDeleteIds = name;
							}
						}
					}
				}
				catch (PermissionException e)
				{
				}
				catch (IdUnusedException e)
				{
					addAlert(state, rb.getString("resource") + " " + "\"" + currentId +  "\"" + " " + rb.getString("notexist"));
				}
				catch (TypeException e)
				{
				}
			}
			
			if (nonDeleteIds.length ()>0)
			{
				addAlert(state, rb.getString("notpermis14") + " " + nonDeleteIds );
			}
		
			// delete item
			state.setAttribute (STATE_DELETE_IDS, deleteIdsVector);
			state.setAttribute (STATE_NOT_EMPTY_DELETE_IDS, notEmptyDeleteIds);
			
		}	// if-else

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute (STATE_MODE, MODE_DELETE_CONFIRM);
		}


	}	// doDeleteconfirm

	/**
	* set the state name to be "replace" if any item has been selected for replacing
	*/
	public void doPrereplace ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// replace items
		Vector replaceItemsVector = new Vector ();
		replaceItemsVector.add (0, "");
		Vector titles = new Vector ();
		titles.add(0, "");

		String[] replaceItems = data.getParameters ().getStrings ("selectedMembers");
		if (replaceItems == null)
		{
			// from revise
			String id = (String) state.getAttribute (STATE_PROPERTIES_ID);

			if (id==null)
			{
				// there is no resource selected, show the alert message to the user
				addAlert(state, rb.getString("choosefile4"));
				state.setAttribute (STATE_MODE, MODE_LIST);
			}
			else
			{
				replaceItemsVector.add ((String) id);
			}
			
			// save properties changes
			doModifyproperties(data);
		}
		else
		{
			for (int i=0; i <replaceItems.length; i++)
			{
				replaceItemsVector.add (replaceItems[i]);
			}
		}

		boolean qualify = true;
					
		if (replaceItemsVector.size () > MULTI_ADD_NUMBER )
		{
			addAlert(state, rb.getString("notreplace") + " " + MULTI_ADD_NUMBER + " " + rb.getString("itemsone"));
		}
		else
		{
			for (int i=1; i < replaceItemsVector.size (); i++)
			{
				String displayName = NULL_STRING;
				String id = (String) replaceItemsVector.get (i);

				try
				{
					ResourceProperties p = ContentHostingService.getProperties (id);
					displayName = p.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);	
					// prepopulate the title field with the original display name
					titles.add(i, displayName);
					
					if (qualify)
					{
						// any item which cannot be replaced?
						qualify = replaceable(p);
					}
				}
				catch (IdUnusedException e)
				{
					addAlert(state,RESOURCE_NOT_EXIST_STRING);
				}
				catch (PermissionException e)
				{
					addAlert(state, rb.getString("notpermis2") + " " + id);
				}
			}	// for
		}	// if-else

		if (!qualify)
		{
			addAlert(state, rb.getString("notreplace1"));
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			if (replaceItemsVector.size () > 0)
			{
				state.setAttribute (STATE_REPLACE_ID, replaceItemsVector);
			}
			state.setAttribute (STATE_REPLACE_TITLE, titles);
			state.setAttribute (STATE_MODE, MODE_REPLACE);
		}	// if-else

	}	// doPrereplace

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
					if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
					{
						String alert = (String) state.getAttribute(STATE_MESSAGE);
						if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
						{
							addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
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
				state.setAttribute (STATE_COPY_FLAG, Boolean.TRUE.toString());
				if (((String) state.getAttribute (STATE_SELECT_ALL_FLAG)).equals (Boolean.TRUE.toString()))
				{
					state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
				}

				Vector cutIds = (Vector) state.getAttribute (STATE_CUT_IDS);
				for (int i = 0; i < copyItems.length; i++)
				{
					if (cutIds.contains (copyItems[i]))
					{
						cutIds.remove (copyItems[i]);
					}
					copyItemsVector.add (copyItems[i]);
				}
				if (cutIds.size ()==0)
				{
					state.setAttribute (STATE_CUT_FLAG, Boolean.FALSE.toString());
				}
				state.setAttribute (STATE_CUT_IDS, cutIds);

				state.setAttribute (STATE_COPIED_IDS, copyItemsVector);
			}	// if-else
		}	// if-else

	}	// doCopy

	/**
	* select all the item
	*/
	public void doSelectall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		state.setAttribute (STATE_SELECT_ALL_FLAG,  Boolean.TRUE.toString());

	}	// doSelectall

	/**
	* Unselect all the item
	*/
	public void doUnselectall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());

	}	// doSelectall
	
	/**
	* Expand all the collection resources and put in EXPANDED_COLLECTIONS attribute.
	*/
	public void doExpandall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters();
		
		HashMap expandedCollectionResources = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
		String currentCollectionId = params.getString("collectionId");
		
		try
		{
			ContentCollection currentCollection = ContentHostingService.getCollection(currentCollectionId);

			Iterator allCollectionIterator = threadIterator(currentCollection.getMemberResources().iterator());
//			Iterator allCollectionIterator = threadIterator(currentCollection.getMembers().iterator());
			while (allCollectionIterator.hasNext ())
			{
				// String newId = (String) allCollectionIterator.next ();
			    Resource collection = (Resource) allCollectionIterator.next();
				if (!expandedCollectionResources.containsKey(collection.getId()))
				{
					expandedCollectionResources.put (collection.getId(),collection);
				}
			}
		}
		catch (Exception ignore){}
		
		state.setAttribute(EXPANDED_COLLECTIONS, expandedCollectionResources);
		state.setAttribute (STATE_EXPAND_ALL_FLAG,  Boolean.TRUE.toString());
	}	// doExpandall

	/**
	* Unexpand all the collection resources
	*/
	public void doUnexpandall ( RunData data)
	{
		// get the state object
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		state.setAttribute(EXPANDED_COLLECTIONS, new HashMap());
		state.setAttribute (STATE_EXPAND_ALL_FLAG, Boolean.FALSE.toString());

	}	// doUnexpandall
	
	/**
	* Return an iterator on the resource collection objects
	* to the specified iteration of messages.  They will be returned in depth first order.
	* @param topLevel The iterator of messages to which those returned are some depth of response.
	* @return an iterator on the DiscussionMessages that are in the response threads
	* to the specified iteration of messages, in depth first order (may be empty).
	*/
	protected Iterator threadIterator(Iterator topLevel)
	{
		// start the depth iteration stack with the topics of the channel
		final Stack stack = new Stack();

		Vector v = new Vector();

		while (topLevel.hasNext ())
		{
		    Resource r = (Resource) topLevel.next();
			try
			{
				if (r.getProperties().getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION))		
				{
				    v.add(r);
				}
			}
			catch (Exception ignore){}
		}
		stack.push(v.iterator ());

		return new
			Iterator()
			{
				// if we found one in hasNext(), store it here till the next()
				private Object m_next = null;

				// the depth stack of iterators
				private Stack m_stack = stack;

				// messages already returned
				private Set m_alreadyReturned = new HashSet();

				public Object next()
				{
					// make sure hasNext has been called
					hasNext();

					if (m_next == null) throw new NoSuchElementException();

					// consume the next
					Object rv = m_next;
					m_next = null;

					// keep track of what we have already returned to avoid looping
					m_alreadyReturned.add(rv);

					return rv;

				}   // next

				public boolean hasNext()
				{										
					// if known next not yet used, we have next
					if (m_next != null) return true;

					// clear off completed iterators from the stack
					while ((!m_stack.empty())
							&&  (!((Iterator)m_stack.peek()).hasNext()))
					{
						m_stack.pop();
					}

					// if the stack is now empty, we have no next
					if (m_stack.empty())
					{
						return false;
					}

					// setup the next from the stack
					m_next = ((Iterator)m_stack.peek()).next();

					// if this next has replies, push an iterator of them in the stack
					// ... but don't do it if we've alread returned this message, else we might get a loop!
					if (!m_alreadyReturned.contains(m_next))
					{
						try
						{
							List members = ((ContentCollection) m_next).getMemberResources();
							Vector vNew = new Vector();
							if ((members!=null) && (members.size ()!=0))
							{
								for (int j=0; j<members.size(); j++)
								{
									try
									{						  
									    Resource resource = (Resource) members.get(j);
									    boolean isCollection = resource.getProperties().getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);							    
				//						ResourceProperties p = ContentHostingService.getProperties(((Resource)members.get(j)).getId());
										if (isCollection)
										{
										    vNew.add(resource);
	//										vNew.add(((Resource)members.get(j)).getId());
										}
									}
	//								catch (PermissionException ignore){}
									catch (EmptyException ignore){}
	//								catch (IdUnusedException ignore){}
									catch (TypeException ignore){}
								}
								m_stack.push(vNew.iterator ());
							}
						}
						catch (Exception ignore){}
					}

					return true;

				}   // hasNext

				public void remove() {throw new UnsupportedOperationException();}

			};  // Iterator

	}   // threadIterator

	
	/**
	* Build the menu.
	*/
	private void buildMenu (	VelocityPortlet portlet,
									Context context,
									RunData data,
									SessionState state,
									boolean allowAdd,
									boolean allowRemove,
									boolean allowReplace,
									boolean allowRevise,
									boolean emptyFolder,
									boolean atHome)
	{
		context.put("tlang",rb);
		String copyFlag = ((String)state.getAttribute (STATE_COPY_FLAG));
		String cutFlag = ((String)state.getAttribute (STATE_CUT_FLAG));
		String formName = "showForm";

		// build a test menu
		Menu bar = new Menu (portlet, data, "ResourcesAction");
// this item can be added to the menu for testing of the new list context, which will replace the show context soon
//		bar.add(new MenuEntry("List", null, true, MenuItem.CHECKED_NA, "doList", formName));
		bar.add ( new MenuEntry (rb.getString("new"), null, allowAdd, MenuItem.CHECKED_NA, "doNewitem", formName) );
		bar.add ( new MenuEntry (rb.getString("delete"), null, (!emptyFolder && allowRemove), MenuItem.CHECKED_NA, "doDeleteconfirm", formName) );
		bar.add ( new MenuEntry (rb.getString("cut"), null, ((allowAdd)&&(!emptyFolder)), MenuItem.CHECKED_NA, "doCut", formName) );
		bar.add ( new MenuEntry (rb.getString("copy"), null, ((allowAdd)&&(!emptyFolder)), MenuItem.CHECKED_NA, "doCopy", formName) );
		if (copyFlag != null && copyFlag.equals (Boolean.TRUE.toString()) || cutFlag != null && cutFlag.equals (Boolean.TRUE.toString()))
		{
			// has cut or copy
			bar.add ( new MenuEntry (rb.getString("paste"), null, allowAdd, MenuItem.CHECKED_NA, "doHandlepaste", formName) );
			bar.add ( new MenuEntry (rb.getString("pastesh"), null, allowAdd, MenuItem.CHECKED_NA, "doHandlepasteshortcut", formName) );
		}
		else
		{
			//no cut nor copy yet
			bar.add ( new MenuEntry (rb.getString("paste"), null, false, MenuItem.CHECKED_NA, "doHandlepaste", formName) );
			bar.add ( new MenuEntry (rb.getString("pastesh"), null, false, MenuItem.CHECKED_NA, "doHandlepasteshortcut", formName) );
		}
		bar.add ( new MenuEntry (rb.getString("replace"), null, ((allowReplace)&&(!emptyFolder)), MenuItem.CHECKED_NA, "doPrereplace", formName) );
		bar.add ( new MenuEntry (rb.getString("revise"), null, ((allowRevise)&&(!emptyFolder)), MenuItem.CHECKED_NA, "doProperties", formName));

		if (state.getAttribute (STATE_SELECT_ALL_FLAG).equals (Boolean.FALSE.toString()))
		{
			bar.add ( new MenuEntry (rb.getString("selectall"), null, !emptyFolder, MenuItem.CHECKED_NA, "doSelectall"));
		}
		else
		{
			bar.add ( new MenuEntry (rb.getString("unselectall"), null, !emptyFolder, MenuItem.CHECKED_NA, "doUnselectall"));
		}
		
		if (state.getAttribute (STATE_EXPAND_ALL_FLAG).equals (Boolean.FALSE.toString()))
		{
			bar.add ( new MenuEntry (rb.getString("expandall"), null, !emptyFolder, MenuItem.CHECKED_NA, "doExpandall", formName));
		}
		else
		{
			bar.add ( new MenuEntry (rb.getString("collapseall"), null, !emptyFolder, MenuItem.CHECKED_NA, "doUnexpandall", formName));
		}

		// not for dropbox
		if (!RESOURCES_MODE_DROPBOX.equalsIgnoreCase((String) state.getAttribute(STATE_RESOURCES_MODE)))
		{
			// add permissions & options, if allowed
			if (SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
			{
				bar.add( new MenuDivider());

				// add options if allowed
				// addOptionsMenu(bar, (JetspeedRunData) data);

				// when at the home collection, offer the permissions for the tool
				if (atHome)
				{
					bar.add( new MenuEntry(rb.getString("permissions"), "doPermissions") );
				}
				// otherwise, offer the permissions for the current folder
				else
				{
					bar.add( new MenuEntry(rb.getString("fpermissions"), "doFolder_permissions") );
				}
			}
		}

		context.put (Menu.CONTEXT_MENU, bar);
		context.put (Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));
        
	}	// buildMenu

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

		state.setAttribute (STATE_MODE, MODE_LIST);
		state.setAttribute (STATE_SORT_BY, ResourceProperties.PROP_DISPLAY_NAME);

		state.setAttribute (STATE_SORT_ASC, Boolean.TRUE.toString());

		state.setAttribute (STATE_SELECT_ALL_FLAG, Boolean.FALSE.toString());
		
		state.setAttribute (STATE_EXPAND_ALL_FLAG, Boolean.FALSE.toString());

		state.setAttribute (STATE_FROM, NULL_STRING);

		state.setAttribute (STATE_COLLECTION_PATH, new Vector ());
		
		// get resources mode from tool registry
		String resources_mode = portlet.getPortletConfig().getInitParameter("resources_mode");
		state.setAttribute(STATE_RESOURCES_MODE, resources_mode);

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
		
		initCopyContext (state);

		initCutContext (state);

		initAddFileContext (state);

		initAddFolderContext (state);

		initAddDocumentContext (state);

		initAddURLContext (state);

		initReplaceContext (state);
		
		// get resources mode from tool registry
		String optional_properties = portlet.getPortletConfig().getInitParameter("optional_properties");
		if(optional_properties != null && "true".equalsIgnoreCase(optional_properties))
		{
			initMetadataContext(state);
		}
		
//		// setup the observer to notify our MONITOR_PANEL panel(inside the Main panel)
//		if (state.getAttribute(STATE_OBSERVER) == null)
//		{			
//			// the delivery location for this tool
//			String deliveryId = clientWindowId(state, portlet.getID());
//
//			// the html element to update on delivery
//			String elementId = mainPanelUpdateId(portlet.getID());
//
//			// we want the resources in this collection, but not those
//			// below, except if folders are expanded... -ggolden %%%
//			HashSet patterns = new HashSet();
//			patterns.add(ContentHostingService.getReference((String) state.getAttribute (STATE_COLLECTION_ID)));
//
//			ContentObservingCourier o = new ContentObservingCourier(deliveryId, elementId, patterns);
//			o.enable();
//			state.setAttribute(STATE_OBSERVER, o);
//		}
		
		state.setAttribute (STATE_INITIALIZED, Boolean.TRUE.toString());
		
	}	// initState
	
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
	private void addObservingPattern(String pattern, SessionState state)
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
	private void removeObservingPattern(String pattern, SessionState state)
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
	private void initCopyContext (SessionState state)
	{
		state.setAttribute (STATE_COPIED_IDS, new Vector ());

		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

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
	* initialize the add file context
	*/
	private void initAddFileContext (SessionState state)
	{
		state.setAttribute (STATE_ADD_FILE_NUMBER, new Integer (1));
	
		Vector emptyVector = emptyVector (MULTI_ADD_NUMBER + 1);
		state.setAttribute (STATE_ADD_FILE_OS_NAME, emptyVector.clone());
		state.setAttribute (STATE_ADD_FILE_TITLE, emptyVector.clone());
		state.setAttribute (STATE_ADD_FILE_COPYRIGHT, emptyVector.clone());
		// default copyright choice
		Vector copyrightVector = (Vector) emptyVector.clone();
		int i = 0;
		if (state.getAttribute(DEFAULT_COPYRIGHT) != null)
		{
			for (i = 0; i<copyrightVector.size();i++)
			{
				copyrightVector.set(i, state.getAttribute(DEFAULT_COPYRIGHT));
			}
		}
		state.setAttribute(STATE_ADD_FILE_COPYRIGHT, copyrightVector);
		//default copyright alert choice
		Vector copyrightAlertVector = (Vector) emptyVector.clone();
		if (state.getAttribute(DEFAULT_COPYRIGHT_ALERT) != null)
		{
			for (i = 0; i<copyrightVector.size();i++)
			{
				copyrightAlertVector.set(i, state.getAttribute(DEFAULT_COPYRIGHT_ALERT));
			}
		}
		state.setAttribute(STATE_ADD_FILE_COPYRIGHT_ALERT, copyrightAlertVector);
		state.setAttribute (STATE_ADD_FILE_CONTENT_TYPE, emptyVector.clone());
		state.setAttribute (STATE_ADD_FILE_NEW_COPYRIGHT, emptyVector.clone());
		state.setAttribute (STATE_ADD_FILE_DESCRIPTION, emptyVector.clone());
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			// init add file pubview with false as default
			String collectionReference = ContentHostingService.getReference((String) state.getAttribute(STATE_COLLECTION_ID));
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);

			Vector pubViewVector = new Vector();
			for (i=0; i<MULTI_ADD_NUMBER+1; i++)
			{
				if (pubviewset)
				{
					pubViewVector.add (i, new Boolean(true));
				}
				else
				{
					pubViewVector.add (i, new Boolean(false));
				}
			}
			state.setAttribute (STATE_ADD_FILE_PUBVIEW, pubViewVector);
		}
		
		state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector.clone());
		
		Vector emptyByteVector = new Vector ();
		for (i=0; i<MULTI_ADD_NUMBER+1; i++)
		{
			emptyByteVector.add (i, new byte[1]);
		}
		state.setAttribute (STATE_ADD_FILE_CONTENT, emptyByteVector);

	}	// initAddFileContext

	/**
	* initial the add folder context
	*/
	private void initAddFolderContext (SessionState state)
	{
		state.setAttribute (STATE_ADD_FOLDER_NUMBER, new Integer (1));
		
		Vector emptyVector = emptyVector (MULTI_ADD_NUMBER + 1);
		state.setAttribute (STATE_ADD_FOLDER_TITLE, emptyVector.clone());
		state.setAttribute (STATE_ADD_FOLDER_COPYRIGHT, emptyVector.clone());
		state.setAttribute (STATE_ADD_FOLDER_DESCRIPTION, emptyVector.clone());
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			// init add folder pubview with false as default
			String collectionReference = ContentHostingService.getReference((String) state.getAttribute(STATE_COLLECTION_ID));
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			Vector pubViewVector = new Vector();
			for (int i=0; i<MULTI_ADD_NUMBER+1; i++)
			{
				if (pubviewset)
				{
					pubViewVector.add (i, new Boolean(true));
				}
				else
				{
					pubViewVector.add (i, new Boolean(false));
				}
			}
			state.setAttribute (STATE_ADD_FOLDER_PUBVIEW, pubViewVector);
		}
		
		state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector.clone());

	}	// initAddFolderContext

	/**
	* initialize the add document file context
	*/
	private void initAddDocumentContext (SessionState state)
	{
		state.setAttribute (STATE_ADD_DOCUMENT_CONTENT, NULL_STRING);
		state.setAttribute (STATE_ADD_DOCUMENT_TITLE, NULL_STRING);
		state.setAttribute (STATE_ADD_DOCUMENT_COPYRIGHT, NULL_STRING);
		state.setAttribute (STATE_ADD_DOCUMENT_NEW_COPYRIGHT, NULL_STRING);
		state.setAttribute (STATE_ADD_DOCUMENT_DESCRIPTION, NULL_STRING);
		
		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			// init add document pubview with false as default
			String collectionReference = ContentHostingService.getReference((String) state.getAttribute(STATE_COLLECTION_ID));
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			if (pubviewset)
			{
				state.setAttribute (STATE_ADD_DOCUMENT_PUBVIEW, new Boolean(true));
			}
			else
			{
				state.setAttribute (STATE_ADD_DOCUMENT_PUBVIEW, new Boolean(false));
			}
		}

		Vector emptyVector = emptyVector (MULTI_ADD_NUMBER + 1);
		state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, emptyVector);

	}	// initAddDocumentContext

	/**
	*	initialize the add URL context
	*/
	private void initAddURLContext (SessionState state)
	{
		state.setAttribute (STATE_ADD_URL_NUMBER, new Integer (1));
				
		Vector v = emptyVector (MULTI_ADD_NUMBER + 1);
		state.setAttribute (STATE_ADD_URL_TITLE, v.clone());
		//default copyright choice
		Vector copyrightVector = (Vector) v.clone();
		int i = 0;
		if (state.getAttribute(DEFAULT_COPYRIGHT) != null)
		{
			for (i = 0; i<copyrightVector.size();i++)
			{
				copyrightVector.set(i, state.getAttribute(DEFAULT_COPYRIGHT));
			}
		}
		state.setAttribute(STATE_ADD_URL_COPYRIGHT, copyrightVector);
		//default copyright alert choice
		Vector copyrightAlertVector = (Vector) v.clone();
		if (state.getAttribute(DEFAULT_COPYRIGHT_ALERT) != null)
		{
			for (i = 0; i<copyrightVector.size();i++)
			{
				copyrightAlertVector.set(i, state.getAttribute(DEFAULT_COPYRIGHT_ALERT));
			}
		}
		state.setAttribute(STATE_ADD_URL_COPYRIGHT_ALERT, copyrightAlertVector);
		state.setAttribute (STATE_ADD_URL_URL, v.clone());
		state.setAttribute (STATE_ADD_URL_NEW_COPYRIGHT, v.clone());
		state.setAttribute (STATE_ADD_URL_DESCRIPTION, v.clone());
		
		// TODO: we are getting null pointer on the "if" line below - lets try to catch it
		if (state == null)
		{
			Log.warn("chef", "ResourcesAction initAddURLContext state null");
		}
		if (state.getAttribute(STATE_RESOURCES_MODE) == null)
		{
			String home = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
			Log.warn("chef", "ResourcesAction initAddURLContext state STATE_RESOURCES_MODE missing: home:" + home);	
		}
		// TODO:

		if (((String) state.getAttribute(STATE_RESOURCES_MODE)).equalsIgnoreCase(RESOURCES_MODE_RESOURCES))
		{
			// init add URL pubview with false as default
			String collectionReference = ContentHostingService.getReference((String) state.getAttribute(STATE_COLLECTION_ID));
			boolean pubviewset = getPubViewInheritance(collectionReference) || getPubView(collectionReference);
			Vector pubViewVector = new Vector();
			for (i=0; i<MULTI_ADD_NUMBER+1; i++)
			{
				if (pubviewset)
				{
					pubViewVector.add (i, new Boolean(true));
				}
				else
				{
					pubViewVector.add (i, new Boolean(false));
				}
			}
			state.setAttribute (STATE_ADD_URL_PUBVIEW, pubViewVector);
		}
		
		state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, v.clone());

	}	// initAddURLContext

	/**
	* initialize replace context
	*/
	private void initReplaceContext (SessionState state)
	{
		Vector v = emptyVector (MULTI_ADD_NUMBER + 1);
		state.setAttribute (STATE_REPLACE_ID, v.clone());
		state.setAttribute (STATE_REPLACE_OS_NAME, v.clone());
		state.setAttribute (STATE_REPLACE_TITLE, v.clone());
		state.setAttribute (STATE_REPLACE_CONTENT_TYPE, v.clone());
		state.setAttribute (STATE_UNQUALIFIED_INPUT_FIELD, v.clone());
		
		Vector emptyByteVector = new Vector ();
		for (int i=0; i<MULTI_ADD_NUMBER + 1; i++)
		{
			emptyByteVector.add (i, new byte[1]);
		}
		state.setAttribute (STATE_REPLACE_CONTENT, emptyByteVector);

	}	// initReplaceContext
	
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
				ResourceProperties p = ((Resource) membersIterator.next()).getProperties();
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
		Reference ref = new Reference(ContentHostingService.getReference(home));
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
	public void doExpand_collection(RunData data) throws IdUnusedException, TypeException, PermissionException
	{		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
		
		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		String id = params.getString("collectionId");
		currentMap.put (id,ContentHostingService.getCollection (id)); 
		
		state.setAttribute(EXPANDED_COLLECTIONS, currentMap);
		
		// add this folder id into the set to be event-observed
		addObservingPattern(id, state);
	
	}	// doExpand_collection

	/**
	* Remove the collection id from the expanded collection list
	*/
	public void doCollapse_collection(RunData data)
	{		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		HashMap currentMap = (HashMap) state.getAttribute(EXPANDED_COLLECTIONS);
		
		//get the ParameterParser from RunData
		ParameterParser params = data.getParameters ();
		String collectionId = params.getString("collectionId");
		
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
	public List getCollectionPath(SessionState state)
	{
		org.sakaiproject.service.legacy.content.ContentHostingService contentService = (org.sakaiproject.service.legacy.content.ContentHostingService) state.getAttribute (STATE_CONTENT_SERVICE);
		// make sure the channedId is set
		String currentCollectionId = (String) state.getAttribute (STATE_COLLECTION_ID);
		String homeCollectionId = (String) state.getAttribute(STATE_HOME_COLLECTION_ID);
		String navRoot = (String) state.getAttribute(STATE_NAVIGATION_ROOT);
		
		LinkedList collectionPath = new LinkedList();
		
		Vector pathitems = new Vector();
		while(currentCollectionId != null && ! currentCollectionId.equals(navRoot))
		{
			pathitems.add(currentCollectionId);
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
				// TODO : remove this
				e.printStackTrace();
			}
			catch (IdUnusedException e)
			{
				// TODO : remove this
				e.printStackTrace();
			}
		}
		return collectionPath;
	}
	
	/**
	 * Get the members under this folder that should be seen.
	 * @param collectionId - String version of 
	 * @param expandedCollections - Hash of collection resources
	 * @param sortedBy  - pass through to ContentHostingComparator
	 * @param sortedAsc - pass through to ContentHostingComparator
	 * @param state - The session state
	 */
	public List getShowMembers(String collectionId, HashMap expandedCollections, String sortedBy, String sortedAsc, SessionState state)
	{		
		try
		{
			// get the collection
		    // try using existing resource first
		    
			ContentCollection collection = null;

			// get the collection
			if (expandedCollections.containsKey(collectionId)) {
			    collection = (ContentCollection) expandedCollections.get(collectionId);
			}
			else {
			    collection = ContentHostingService.getCollection(collectionId);
			}	
			
			// Get the collection members from the 'new' collection
			List newMembers = collection.getMemberResources ();
			Collections.sort (newMembers, new ContentHostingComparator (sortedBy, Boolean.valueOf (sortedAsc).booleanValue ()));

			// loop thru the (possibly) new members and add to the list 
			int size = newMembers.size();
			Hashtable moreMembers = new Hashtable();
			for (int i = 0; i< size; i++)
			{
		//		String nextId = ((Resource)newMembers.get(i)).getId();
			    Resource resource = (Resource) newMembers.get(i);
			    boolean isCollection = resource.getProperties().getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
				
			    // If this is a collection, and it is already in the list of expanded collections 
			    // then return a vector of it's members.
				if (isCollection)
				{
				    ContentCollection subCollection = (ContentCollection) resource;
				    // need to see if it should be expanded
					if (expandedCollections.containsKey (subCollection.getId()))
					{
						if (collection.getMemberResources().size()!=0)
						{
							moreMembers.put(new Integer(i), getShowMembers(subCollection.getId(), expandedCollections, sortedBy, sortedAsc, state));
						}
					}
				}
			}
			
			Enumeration keys = moreMembers.keys();
			while (keys.hasMoreElements())
			{
				Integer index = (Integer) keys.nextElement();
				newMembers.addAll(index.intValue()+1, (Vector) moreMembers.get(index));
			}
			return newMembers;
		}
		catch (IdUnusedException e)
		{
			addAlert(state,"IdUnusedException.");
		}
		catch (TypeException e)
		{
			addAlert(state, "TypeException.");
		}
		catch (PermissionException e)
		{
			addAlert(state, "PermissionException");
		} catch (EmptyException e) 
		{
           addAlert(state, "EmptyException");
        }
		return new Vector();
	
	}	// getShowMembers

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
	public List getBrowseItems(String collectionId, HashMap expandedCollections, String sortedBy, String sortedAsc, BrowseItem parent, boolean isLocal, SessionState state)
	{		
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
			}
				
			String dummyId = collectionId.trim();
			if(dummyId.endsWith(Resource.SEPARATOR))
			{
				dummyId += "dummy";
			}
			else
			{
				dummyId += Resource.SEPARATOR + "dummy";
			}
			
			boolean canRead = false;
			boolean canDelete = false;
			boolean canRevise = false;
			boolean canAddFolder = false;
			boolean canAddItem = false;
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
			
			if(parent != null)
			{
				depth = parent.getDepth() + 1;
			}
			
			if(canAddItem)
			{
				state.setAttribute(STATE_PASTE_ALLOWED_FLAG, Boolean.TRUE.toString());
			}
			
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
			
			String containerId = contentService.getContainingCollectionId (collectionId);
			folder.setContainer(containerId);
			
			folder.setCanRead(canRead);
			folder.setCanRevise(canRevise);
			folder.setCanAddItem(canAddItem);
			folder.setCanAddFolder(canAddFolder);
			folder.setCanDelete(canDelete);

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

			
			// folder.setIsEmpty(newMembers.size() == 0);
			folder.setIsEmpty(false);			
			folder.setDepth(depth);
			newItems.add(folder);
			
			if(expandedCollections.containsKey (collectionId))
			{
				// Get the collection members from the 'new' collection
				List newMembers = collection.getMemberResources ();
							
				Collections.sort (newMembers, new ContentHostingComparator (sortedBy, Boolean.valueOf (sortedAsc).booleanValue ()));
				// loop thru the (possibly) new members and add to the list 
				Iterator it = newMembers.iterator();
				while(it.hasNext())
				{
					Resource resource = (Resource) it.next();
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
						// add all the items in the subfolder to newItems
						newItems.addAll(getBrowseItems(itemId, expandedCollections, sortedBy, sortedAsc, folder, isLocal, state));
					}
					else
					{
						String itemType = ((ContentResource)resource).getContentType();
						String itemName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
						BrowseItem newItem = new BrowseItem(itemId, itemName, itemType);
						
						newItem.setContainer(collectionId);
						
						newItem.setCanDelete(canDelete);
						newItem.setCanRevise(canRevise);
						newItem.setCanRead(canRead);
						newItem.setCanCopy(canRead);
						newItem.setCanAddItem(canAddItem); // true means this user can add an item in the folder containing this item (used for "duplicate") 
						
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
			// return newItems;
		}
		catch (IdUnusedException e)
		{
			// addAlert(state,"IdUnusedException.");
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
				if (properties.getProperty (ResourceProperties.PROP_IS_COLLECTION).equals (Boolean.TRUE.toString()))
				{
					String alert = (String) state.getAttribute(STATE_MESSAGE);
					if (alert == null || ((alert != null) && (alert.indexOf(RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING) == -1)))
					{
						addAlert(state, RESOURCE_INVALID_OPERATION_ON_COLLECTION_STRING);
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
	public void doPasteitem ( RunData data)
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
				String id = collectionId + Validator.escapeResourceName(displayName);

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
				catch (IdUsedException e)
				{
					addAlert(state, rb.getString("notaddreso") + " " + id + rb.getString("used2"));
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
			
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// delete sucessful
			state.setAttribute (STATE_MODE, MODE_LIST);
			
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
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

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
		
		Reference ref = new Reference(ContentHostingService.getReference(collectionId));
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
		state.setAttribute (STATE_COPY_FLAG, Boolean.FALSE.toString());

		// get the current home collection id and the related site
		String collectionId = (String) state.getAttribute (STATE_HOME_COLLECTION_ID);
		Reference ref = new Reference(ContentHostingService.getReference(collectionId));
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
	private void copyrightChoicesIntoContext(SessionState state, Context context)
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

	// TODO: move these to content hosting API
	
	private void metadataGroupsIntoContext(SessionState state, Context context)
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
	* Update this resource's realm so that it matches the setting for public view.
	* @param ref The resource reference
	* @param pubview The public view setting.
	* @throws InUseException if the ream is not availabe for modification.
	*/
	protected void setPubView(String ref, boolean pubview)
	{
		// edit the realm
		RealmEdit edit = null;

		try
		{
			edit = RealmService.editRealm(ref);
		}
		catch (IdUnusedException e)
		{
			// if no realm yet, and we need one, make one
			if (pubview)
			{
				try
				{
					edit = RealmService.addRealm(ref);
				}
				catch (IdInvalidException ee)
				{
				}
				catch (IdUsedException ee)
				{
				}
				catch (PermissionException ee)
				{
				}
			}
		}
		catch (PermissionException e)
		{
		}
		catch (InUseException e)
		{
		}

		// if we have no realm and don't need one, we are done
		if ((edit == null) && (!pubview))
			return;

		// if we need a realm and didn't get an edit, exception
		if ((edit == null) && pubview)
			return;

		boolean changed = false;
		boolean delete = false;

		// align the realm with our positive setting
		if (pubview)
		{
			// make sure the anon role exists and has "content.read" - the only client of pubview
			RoleEdit role = edit.getRoleEdit(RealmService.ANON_ROLE);
			if (role == null)
			{
				try
				{
					role = edit.addRole(RealmService.ANON_ROLE);
				}
				catch (IdUsedException ignore) {}
			}

			if (!role.contains("content.read"))
			{
				role.add("content.read");
				changed = true;
			}
		}

		// align the realm with our negative setting
		else
		{
			// get the role
			RoleEdit role = edit.getRoleEdit(RealmService.ANON_ROLE);
			if (role != null)
			{
				if (role.contains("content.read"))
				{
					changed = true;
					role.remove("content.read");
				}

				if (role.isEmpty())
				{
					edit.removeRole(role.getId());
					changed = true;
				}
			}

			// if "empty", we can delete the realm
			if (edit.isEmpty())
				delete = true;
		}

		// if we want the realm deleted
		if (delete)
		{
			try
			{
				RealmService.removeRealm(edit);
			}
			catch (PermissionException e)
			{
				RealmService.cancelEdit(edit);
			}
		}

		// if we made a change
		else if (changed)
		{
			RealmService.commitEdit(edit);
		}

		else
		{
			RealmService.cancelEdit(edit);
		}

	} // setPubView

	/**
	* Does this resource support public view?
	* @param ref The resource reference
	* @return true if this resource supports public view, false if not.
	*/
	protected boolean getPubView(String ref)
	{
		// get the realm
		try
		{
			Realm realm = RealmService.getRealm(ref);

			// if the realm has no anon role, no pub view
			Role anon = realm.getRole(RealmService.ANON_ROLE);
			if (anon == null)
				return false;

			// if the role doesn't have "content.read", no pub view
			if (!anon.contains("content.read"))
				return false;
		}
		catch (IdUnusedException e)
		{
			// if no realm, no pub view
			return false;
		}

		return true;

	} // getPubView

	/**
	* Does this resource inherit a public view setting from it's relevant set of realms, other than its own?
	* @param ref The resource reference
	* @return true if this resource inherits public view, false if not.
	*/
	protected boolean getPubViewInheritance(String ref)
	{
		// make a reference, and get the relevant realm ids
		Reference r = new Reference(ref);
		List realms = r.getRealms();
		for (Iterator iRealms = realms.iterator(); iRealms.hasNext();)
		{
			String realmId = (String) iRealms.next();
			if (!realmId.equals(ref))
			{
				if (getPubView(realmId))
					return true;
			}
		}

		return false;

	} // getPubViewInheritance
	
	/**
	 * initialize the metadata context
	 */
	private void initMetadataContext(SessionState state)
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
			dc.add(ResourcesMetadata.PROPERTY_DC_TABLEOFCONTENTS);
			dc.add(ResourcesMetadata.PROPERTY_DC_ABSTRACT);
			dc.add(ResourcesMetadata.PROPERTY_DC_CONTRIBUTOR);
			// dc.add(ResourcesMetadata.PROPERTY_DC_TYPE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_FORMAT);
			// dc.add(ResourcesMetadata.PROPERTY_DC_IDENTIFIER);
			// dc.add(ResourcesMetadata.PROPERTY_DC_SOURCE);
			// dc.add(ResourcesMetadata.PROPERTY_DC_LANGUAGE);
			dc.add(ResourcesMetadata.PROPERTY_DC_COVERAGE);
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
	public class BrowseItem
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
		protected String m_createdBy;
		protected String m_createdTime;
		protected String m_modifiedBy;
		protected String m_modifiedTime;
		protected String m_size;
		protected String m_target;
		protected String m_container;
		protected String m_root;
		protected int m_depth;
		protected boolean m_copyrightAlert;
		protected String m_url;
		protected boolean m_isLocal;
		
				
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
			
			m_canAddItem = false;
			m_canAddFolder = false;
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
		
	}	// inner class BrowseItem
	
	
	/**
	 * Inner class encapsulates information about resources (folders and items) for editing
	 */
	public class EditItem
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
		protected String m_formid;
		protected Map m_structuredArtifact;

		protected Set m_metadataGroupsShowing;
		
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
			m_notification = NotificationService.NOTI_OPTIONAL;
			m_hasQuota = false;
			m_canSetQuota = false;
			m_formtype = "";
			m_formid = "";
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
		
		public Object getValue(String name)
		{
			return getValue(name, 0);
		}

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
		 * @param element
		 */
		/*
		public void importStructuredArtifact(Element element)
		{
			
		}
		*/
		/**
		 * @return
		 */
		/*
		public Element exportStructuredArtifact(List properties)
		{
			return null;
		}
		*/
	
	}	// inner class EditItem
	
	/**
	 * Inner class encapsulates information about resources (folders and items) during creation process
	 */
	public class CreateItem 
		extends EditItem
	{
		protected Set m_missingInformation;
		protected boolean m_hasBeenAdded;
		
		/**
		 * constructor supplies null values for id, name and type (for use before those values are known)
		 */
		public CreateItem()
		{
			super("", "", "");
			m_missingInformation = new HashSet();
			m_hasBeenAdded = false;

		}
		
		/**
		 * constructor supplies null values for id and name (for use before those values are known)
		 * @param type the type of item being created
		 */
		public CreateItem(String type)
		{
			super("", "", type);
			m_missingInformation = new HashSet();
		}
		
		/**
		 * @param id
		 */
		public void setId(String id)
		{
			m_id = id;
		}
		
		public void setMissing(String propname)
		{
			m_missingInformation.add(propname);
		}
		
		public boolean isMissing(String propname)
		{
			return m_missingInformation.contains(propname) || m_missingInformation.contains(Validator.escapeUrl(propname));
		}
		
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

	}	// class CreateItem
	
	
	/**
	 * Inner class encapsulates information about folders (and final item?) in a collection path (a.k.a. breadcrumb)
	 */
	public class PathItem
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
	public class MetadataGroup
		extends Vector
	{
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

}	// ResourcesAction



