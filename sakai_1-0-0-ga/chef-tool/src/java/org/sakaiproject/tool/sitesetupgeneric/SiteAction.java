/**********************************************************************************
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitesetupgeneric/SiteAction.java,v 1.11 2004/10/15 01:23:45 zqian.umich.edu Exp $
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
package org.sakaiproject.tool.sitesetupgeneric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.NewPagedResourceAction;
import org.sakaiproject.cheftool.PortletConfig;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.email.cover.EmailService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.SitePageEdit;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.ToolConfigurationEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.delivery.RefreshSiteNavDelivery;
import org.sakaiproject.util.delivery.RefreshTopDelivery;

/**
* <p>SiteAction is a CHEF Site setup and configuration tool used
* at the University of Michigan.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.11 $
*/

// Not all of the templates have been implemented.

public class SiteAction extends NewPagedResourceAction
{
	/** portlet configuration parameter values**/
	private static final String SITE_MODE_SITESETUP = "sitesetup";
	private static final String SITE_MODE_SITEINFO= "siteinfo";
	private static final String STATE_SITE_MODE = "site_mode";

	protected final static String[] TEMPLATE = 
	{
		"-list",
		"-type",
		"-courseContact",
		"-courseClass", 
		"-courseFeatures",
		"",
		"",
		"",
		"-projectContact",
		"-newSiteInformation",
		"-newSiteFeatures",
		"-courseInformation",
		"-coursePublish",
		"-otherInvite",
		"-otherPublish",
		"-customize",
		"-basicInformation",
		"-addRemoveParticipant",
		"-addRemoveSection",
		"-sectionInformation",
		"-deleteSection",
		"-addRemoveFeature",
		"-customLayout",
		"-customLink",
		"-customPage",
		"-roster",
		"-customFeatures",
		"-listParticipants",
		"-addParticipant",
		"-removeParticipants",
		"-changeRoles",
		"-siteDeleteConfirm",
		"-makePublic",
		"-setType",
		"-publishUnpublish",
		"-roster-sorted",
		"-addMyWorkspaceFeature",
		"-removeMyWorkspaceFeature",
		"-newSiteConfirm",
		"-courseManual",
		"-courseConfirm",
		"-newSitePublishUnpublish",
		"-siteInfo-list",
		"-siteInfo-editInfo",
		"-siteInfo-editInfoConfirm",
		"-addRemoveFeatureConfirm",
		"-publishUnpublish-sendEmail",
		"-publishUnpublish-confirm",
		"",
		"-siteInfo-editAccess",
		"-addParticipant-sameRole",
		"-addParticipant-differentRole",
		"-addParticipant-notification",
		"-addParticipant-confirm",
		"-siteInfo-editAccess-globalAccess",
		"-siteInfo-editAccess-globalAccess-confirm",
		"-changeRoles-confirm",
		"-removeClass",
		"-addRemoveClassConfirm"
	};
	
	/** Non-unit-specific skins. */
	private final static String PROJECT_UNPUBLISHED_SKIN = "pro.css";
	private final static String PROJECT_PUBLISHED_SKIN = "prp.css";

	/** Used to check if there is a site instance in state */
	private boolean siteInState = false;
	
	/** Name of state attribute for SiteEdit instance  */
	private static final String STATE_SITE_INSTANCE = "site.instance";
	
	/** Name of state attribute for Site Information  */
	private static final String STATE_SITE_INFO = "site.info";
	
	/** Name of state attribute for CHEF site type  */
	private static final String STATE_SITE_TYPE = "site-type";
	
	/** Name of state attribute for poissible site types */
	private static final String STATE_SITE_TYPES = "site_types";
	
	//Names of state attributes corresponding to properties of a site
	private final static String PROP_SITE_CONTACT_EMAIL = "contact-email";
	private final static String PROP_SITE_CONTACT_NAME = "contact-name";
	private final static String PROP_SITE_TITLE = "title";
	private final static String PROP_SITE_DESCRIPTION = "description";
	private final static String PROP_SITE_SHORT_DESCRIPTION = "short-description";
	private final static String PROP_SITE_SUBJECT = "subject";
	private final static String PROP_SITE_ICON_URL = "site-icon-url";
	private final static String PROP_SITE_SITE_INFO_URL = "site-info-url";
	private final static String PROP_SITE_SKIN = "site-skin";
	private final static String PROP_SITE_ADDITIONAL = "additional";
	public final static String PROP_SITE_INCLUDE = "site-include";
	
	/** State variable and constant values for sorting site list */
	private static final String STATE_SORT_FIELD = "site.list.sort.field";
	private static final String SORT_FIELD_TITLE = "title";
	private static final String SORT_FIELD_DESCRIPTION = "description";
	private static final String SORT_FIELD_TYPE = "type";
	private static final String SORT_FIELD_OWNER = "owner";
	private static final String SORT_FIELD_STATUS = "status";
	private static final String SORT_FIELD_STUDENT_NAME = "student_name";
	private static final String SORT_FIELD_UNIQNAME = "uniqname";
	private static final String SORT_FIELD_STUDENT_ID = "student_id";
	private static final String SORT_FIELD_LEVEL = "level";
	private static final String SORT_FIELD_CREDITS = "credits";
	
	/** Name of the state attribute holding the site list column list is sorted by */
	private static final String SORTED_BY = "site.sorted.by";
	
	/** the list of criteria for sorting */
	private static final String SORTED_BY_TITLE = "title";
	private static final String SORTED_BY_DESCRIPTION = "description";
	private static final String SORTED_BY_TYPE = "type";
	private static final String SORTED_BY_OWNER = "owner";
	private static final String SORTED_BY_STATUS = "status";
	private static final String SORTED_BY_CREATION_DATE = "creationdate";
	private static final String SORTED_BY_JOINABLE = "joinable";
	private static final String SORTED_BY_STUDENT_NAME = "student_name";
	private static final String SORTED_BY_UNIQNAME = "uniqname";
	private static final String SORTED_BY_STUDENT_ID = "student_id";
	private static final String SORTED_BY_LEVEL = "level";
	private static final String SORTED_BY_CREDITS = "credits";
	private static final String SORTED_BY_PARTICIPANT_NAME = "participant_name";
	private static final String SORTED_BY_PARTICIPANT_UNIQNAME = "participant_uniqname";
	private static final String SORTED_BY_PARTICIPANT_ROLE = "participant_role";
	private static final String SORTED_BY_PARTICIPANT_ID = "participant_id";
			
	/** Name of the state attribute holding the site list column to sort by */
	private static final String SORTED_ASC = "site.sort.asc";
	
	/** State attribute for list of sites to be deleted. */
	private static final String STATE_SITE_REMOVALS = "site.removals";
	
	/** Name of the state attribute holding the site list View selected */
	private static final String STATE_VIEW_SELECTED = "site.view.selected";
		
	/** the list of View selection options **/
	private final static String ALL_MY_SITES = "All My Sites";
	private final static String MY_WORKSPACE = "My Workspace";
	
	/** Names of lists related to tools */
	private static final String STATE_TOOL_LIST = "toolList";
	private static final String STATE_TOOL_REGISTRATION_LIST = "toolRegistrationList"; 
	private static final String STATE_TOOL_REGISTRATION_SELECTED_LIST = "toolRegistrationSelectedList"; 
	private static final String STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST = "toolRegistrationOldSelectedList"; 
	private static final String STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME = "toolRegistrationOldSelectedHome"; 
	private static final String STATE_TOOL_EMAIL_ADDRESS = "toolEmailAddress";
	private static final String STATE_TOOL_HOME_SELECTED = "toolHomeSelected"; 
	private static final String STATE_PROJECT_TOOL_LIST = "projectToolList";
	
	private static final String STATE_FEATURE_LIST = "site.feature.list";
	private final static String FEATURE_LIST = "featureList";
	private static final String STATE_SITE_TOOL_LIST = "site.tool.list";
	private final static String STATE_NEWS_TITLES = "newstitles";
	private final static String NEWS_DEFAULT_TITLE = "News";
	private final static String STATE_WEB_CONTENT_TITLES = "webcontenttitles";
	private final static String WEB_CONTENT_DEFAULT_TITLE = "Web Content";
	private final static String STATE_SITE_QUEST_UNIQNAME = "site_quest_uniqname";
	
	// %%% get rid of the IdAndText tool lists and just use ToolConfiguration or ToolRegistration lists
	// %%% same for CourseItems
	
	// Names for other state attributes that are lists
	private final static String STATE_UNPUBLISHED_SITES_LIST = "unpublishedSitesList"; // completed but unpublished editable sites
	private final static String STATE_PUBLISHED_SITES_LIST = "publishedSitesList"; // completed and published editable sites
	private final static String STATE_REQUESTED_SITES_LIST = "requestedSitesList"; // a manual entry of subject, course, and section leads toa requested site
	private final static String STATE_OTHER_SITES_LIST = "otherSitesList"; // a list for navigation of sites
	private static final String STATE_SITE_LIST = "site.list"; /** Name of state attribute for the list of all editable sites  */
	private final static String SUBJECT_LIST = "subjectList"; // the list of U-M academic subjects
	private final static String SUBJECT_TITLE_LIST = "subjectTitleList";
	private final static String STATE_WORKSITE_SETUP_PAGE_LIST = "wSetupPageList"; // the list of site pages consistent with Worksite Setup page patterns
	private final static String STATE_OFF_PATTERN_PAGE_LIST = "offPatternPageList"; //the list of site pages that are not consistent with Worksite Setup page patterns
	
	/** The name of the state form field containing additional information for a course request */
	private static final String FORM_ADDITIONAL = "form.additional";
	
	/** %%% in transition from putting all form variables in state*/
	private final static String FORM_TITLE = "form_title";
	private final static String FORM_INCLUDE = "form_include";
	private final static String FORM_DESCRIPTION = "form_description";
	private final static String FORM_HONORIFIC = "form_honorific";
	private final static String FORM_INSTITUTION = "form_institution";
	private final static String FORM_SUBJECT = "form_subject";
	private final static String FORM_PHONE = "form_phone";
	private final static String FORM_EMAIL = "form_email";
	private final static String FORM_REUSE = "form_reuse";
	private final static String FORM_RELATED_CLASS = "form_related_class";
	private final static String FORM_RELATED_PROJECT = "form_related_project";
	private final static String FORM_NAME = "form_name";
	private final static String FORM_PERSON = "form_person";
	private final static String FORM_SHORT_DESCRIPTION = "form_short_description";
	private final static String FORM_SELECTED = "form_selected";
	/** site info edit form variables */
	private final static String FORM_SITEINFO_TITLE = "siteinfo_title";
	private final static String FORM_SITEINFO_DESCRIPTION = "siteinfo_description";
	private final static String FORM_SITEINFO_SHORT_DESCRIPTION = "siteinfo_short_description";
	private final static String FORM_SITEINFO_SKIN = "siteinfo_skin";
	private final static String FORM_SITEINFO_INCLUDE = "siteinfo_include";
	private final static String FORM_SITEINFO_CONTACT_NAME = "siteinfo_contact_name";
	private final static String FORM_SITEINFO_CONTACT_EMAIL = "siteinfo_contact_email";
	
	private final static String FORM_WILL_NOTIFY = "form_will_notify";
	
	/** Context action */
	private static final String CONTEXT_ACTION = "SiteAction";
	
	/** The name of the Attribute for display template index */
	private static final String STATE_TEMPLATE_INDEX = "site.templateIndex";
	
	/** The name of the Attribute for template names */ 
	private static final String STATE_TEMPLATE_ARRAY = "site.templateArray";
	
	/** State attribute for state initialization.  */
	private static final String STATE_INITIALIZED = "site.initialized";
	
	/** The action for menu  */
	private static final String STATE_ACTION = "site.action";
	
	/** The alert message */
	private static final String STATE_ALERT_MESSAGE = "alertMessage";
	
	/** The sort by */
	private static final String STATE_SORT_BY = "site.sort_by";
	
	/** The sort ascending or decending */
	private static final String STATE_SORT_ASC = "site.sort_asc";

	/** The user copyright string */
	private static final String	STATE_MY_COPYRIGHT = "resources.mycopyright";
	
	/** The copyright character */
	private static final String COPYRIGHT_SYMBOL = "copyright (c)";
	
	/** The delimiting character between sections in PROP_SITE_SECTIONS */
	private static final String SECTION_DELIMITER = ":";

	/* The null/empty string */
	private static final String NULL_STRING = "";
	
	/** The alert message shown when a site has been completed */
	private static final String FINISHED_STRING = "Your site set up is complete. You may now add content to and then publish your site.";
	
	/** The alert message shown when no site has been selected for the requested action. */
	private static final String NO_SITE_SELECTED_STRING = "No site(s) checked. Please check a site or sites.";
	
	/** The alert message shown when Finsh is clicked by not tool were selected */
	private static final String NO_FEATURES_SELECTED_STRING = "You have not checked any features for your site. You may do this by click the Back button or later by clicking Revise, then Add or remove features.";
	
	/** Alert messages in doGet_site */
	private static final String NO_STATE_SITE_INSTANCE_STRING = "NO_STATE_SITE_INSTANCE";
	private static final String NO_STATE_SITE_INFO_STRING = " NO_STATE_SITE_INFO";
	
	/** The alert message shown when Revise... has been clicked but more than one site was checked */
	private static final String MORE_THAN_ONE_SITE_SELECTED_STRING = "Please check only one site to Revise... at a time";
	
	/** The state attribute alerting user of a sent course request */
	private static final String REQUEST_SENT = "site.request.sent";
	
	/** The state attributes in the make public vm */
	private static final String STATE_JOINABLE = "state_joinable";
	private static final String STATE_JOINERROLE = "state_joinerRole";
	
	/** Invalid email address warning */
	private static final String INVALID_EMAIL = "The Email id must be made up of alpha numeric characters or any of !#$&'*+-=?^_`{|}~. (no spaces).";
	
	/** the list of selected user */
	private static final String STATE_SELECTED_USER_LIST = "state_selected_user_list";
	
	/** is the admin type user access the tool for the first time */
	private static final String ADMIN_AND_FIRST_USE = "admin_and_first_use";
	
	/** role description */
	private Hashtable ROLE_DESCRIPTION = new Hashtable();
	private static final String STATE_SELECTED_PARTICIPANT_ROLES = "state_selected_participant_roles";
	private static final String STATE_SELECTED_PARTICIPANTS = "state_selected_participants";
	private static final String STATE_PARTICIPANT_LIST = "state_participant_list";
	private static final String STATE_ADD_PARTICIPANTS = "state_add_participants";
	
	/** for changing participant roles*/
	private static final String STATE_CHANGEROLE_SAMEROLE = "state_changerole_samerole";
	private static final String STATE_CHANGEROLE_SAMEROLE_ROLE = "state_changerole_samerole_role";
	
	/** for remove user */
	private static final String STATE_REMOVEABLE_USER_LIST = "state_removeable_user_list";
	
	/**
	* Populate the state object, if needed.
	* @param state The state object.
	* @param portlet The portlet.
	* @param rundata The current request's rundata.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);
		
		PortletConfig config = portlet.getPortletConfig();
		String t = StringUtil.trimToNull(config.getInitParameter("siteTypes"));
		if (t != null)
		{
			state.setAttribute(STATE_SITE_TYPES, new ArrayList(Arrays.asList(t.split(","))));
		}
		else
		{
			state.setAttribute(STATE_SITE_TYPES, new Vector());
		}
		
		//get site tool mode from tool registry
		String site_mode = portlet.getPortletConfig().getInitParameter(STATE_SITE_MODE);
		state.setAttribute(STATE_SITE_MODE, site_mode);
		
		if (state.getAttribute(ADMIN_AND_FIRST_USE) == null)
		{
			if (SecurityService.isSuperUser())
			{
				state.setAttribute(ADMIN_AND_FIRST_USE, Boolean.TRUE.toString());
			}
			else
			{
				state.setAttribute(ADMIN_AND_FIRST_USE, Boolean.FALSE.toString());
			}
		}
		
		// default view
		if(state.getAttribute(STATE_VIEW_SELECTED) == null)
		{
			state.setAttribute(STATE_VIEW_SELECTED, ALL_MY_SITES);
		}
		
		// populate the role definition
		if (ROLE_DESCRIPTION.size() == 0)
		{
			ROLE_DESCRIPTION.put("Assistant", "Can read, add, and revise most content on the site");
			ROLE_DESCRIPTION.put("Instructor", "Can read, revise, delete and add both content and participants to a site");
			ROLE_DESCRIPTION.put("Member", "Can read, revise, delete, and add their own content to a site");
			ROLE_DESCRIPTION.put("Observer", "Can read content on the site");
			ROLE_DESCRIPTION.put("Owner", "Can read, revise, delete and add both content and participants to a site, and revise or delete the site ");
			ROLE_DESCRIPTION.put("Student", "Can read content and add content to a site where appropriate");
		}

	}   // initState
	
	/**
	* cleanState removes the current site instance and it's properties from state
	*/
	private void cleanState(SessionState state)
	{
		state.removeAttribute(STATE_SITE_INSTANCE);
		state.removeAttribute(STATE_SITE_INFO);
		state.removeAttribute(STATE_SITE_TYPE);
		state.removeAttribute("participantToAdd");
		state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
		state.removeAttribute(STATE_TOOL_EMAIL_ADDRESS);
		state.removeAttribute(STATE_TOOL_HOME_SELECTED);
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.removeAttribute(STATE_SITE_TOOL_LIST);
		state.removeAttribute(STATE_FEATURE_LIST);
		state.removeAttribute(FEATURE_LIST);
		state.removeAttribute(STATE_JOINABLE);
		state.removeAttribute(STATE_JOINERROLE);
		state.removeAttribute(STATE_NEWS_TITLES);
		state.removeAttribute(STATE_WEB_CONTENT_TITLES);
		state.removeAttribute(STATE_TOOL_LIST);
		state.removeAttribute(STATE_SITE_QUEST_UNIQNAME);
		setupToolLists(state);
	}	// cleanState
	
	/**
	* Implement this to return a list of all the resources that there are to page.
	* Sort them as appropriate, and apply search criteria.
	*/
	protected List readAllResources(SessionState state)
	{			
		Vector rv = new Vector();
		
		//if called from the site list page
		if(((String)state.getAttribute(STATE_TEMPLATE_INDEX)).equals("0"))
		{
			String adminAndFirstUse = (String) state.getAttribute(ADMIN_AND_FIRST_USE);
			if ((new Boolean(adminAndFirstUse)).booleanValue())
			{
				// show no site when the admin first opens the tool
				state.setAttribute(ADMIN_AND_FIRST_USE, Boolean.FALSE.toString());
			}
			else
			{
				// show the list of site for user other than admins
				//filter the site list for the View selected, user-editable My Workspace
				List sites = filterSiteList(state);
				String sortedBy = (String) state.getAttribute (SORTED_BY);
				String sortedAsc = (String) state.getAttribute (SORTED_ASC);
				Iterator sortedSites = null;
				if (sortedBy != null)
				{
					sortedSites = new SortedIterator (sites.iterator (), new SiteComparator (sortedBy, sortedAsc));
				}
				else
				{
					//default sort is by site type
					Vector allSites = new Vector();
					Hashtable siteTypesSites = new Hashtable();
					List siteTypes = (List) state.getAttribute(STATE_SITE_TYPES);
					for (int i=0; i < siteTypes.size(); i++)
					{
						siteTypesSites.put(siteTypes.get(i), new Vector());
					}
					siteTypesSites.put("myworkspace", new Vector());
					
					// sort the whole site list by title ascending first
					sortedSites = new SortedIterator (sites.iterator (), new SiteComparator (SORTED_BY_TITLE, Boolean.TRUE.toString()));
					while (sortedSites.hasNext())
					{
						Site site = (Site) sortedSites.next();
						ResourceProperties properties = site.getProperties();
						String type = site.getType();
						if (type != null) 
						{
							if (siteTypes.contains(type))
							{
								Vector typeSites = (Vector) siteTypesSites.get(type);
								typeSites.add(site);
								siteTypesSites.put(type, typeSites);
							}
						}
						else if (SiteService.isUserSite(site.getId()))
						{
							Vector myWorkspaceSite = (Vector) siteTypesSites.get("myworkspace");
							myWorkspaceSite.add(site);
							siteTypesSites.put("myworkspace", myWorkspaceSite);
						}
						
					}
					
					// concat all the sites to one list
					for (int i=0; i < siteTypes.size(); i++)
					{
						allSites.addAll((Vector) siteTypesSites.get(siteTypes.get(i)));
					}
					allSites.addAll((Vector) siteTypesSites.get("myworkspace"));
					
					// get the iterator
					sortedSites = allSites.iterator();
				}			
			
			
				String search = (String) state.getAttribute(STATE_SEARCH);
				while (sortedSites.hasNext())
				{
					Site site = (Site) sortedSites.next();
					if (search != null)
					{
						ResourceProperties sProperties = site.getProperties();
						
						// filtering if searching on worksite title, owner, description
						if (StringUtil.containsIgnoreCase(site.getTitle(),search) || 
							StringUtil.containsIgnoreCase(sProperties.getProperty(PROP_SITE_SHORT_DESCRIPTION),search) || 
							StringUtil.containsIgnoreCase(sProperties.getProperty(ResourceProperties.PROP_CREATOR),search))
						{
							rv.add(site);
						}
					}	
					else
					{
						rv.add(site);
					}	// if
				}	// while
			}	// if
			
		}//STATE_TEMPLATE_INDEX = 0
		
		return rv;
		
	} //readAllResources
	
	/**
	* Build the context for normal display
	* @param portlet The velocity portlet object
	* @param context The context object
	* @param data The RunData object
	*/
	public String buildMainPanelContext (	VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state)
	{
		String template = null;
		context.put ("action", CONTEXT_ACTION);
		//updatePortlet(state, portlet, data);
		if (state.getAttribute (STATE_INITIALIZED) == null)
		{
			init (portlet, data, state);
		}
		int index = Integer.valueOf((String)state.getAttribute(STATE_TEMPLATE_INDEX)).intValue();
		template = buildContextForTemplate(index, portlet, context, data, state);
		return template;
		
	} // buildMainPanelContext
	
	/**
	* Build the context for each template using template_index parameter passed in a form hidden field.
	* Each case is associated with a template. (Not all templates implemented). See String[] TEMPLATES.
	* @param index is the number contained in the template's template_index 
	* @param portlet The velocity portlet object
	* @param context The context object
	* @param data The RunData object
	*/
	
	private String buildContextForTemplate (int index, VelocityPortlet portlet,
			Context context,
			RunData data,
			SessionState state)
	{			
		String realmId = "";
		String site_type = "";
		String sortedBy = "";
		String sortedAsc = "";
		ParameterParser params = data.getParameters ();
		
		context.put("alertMessage", state.getAttribute(STATE_MESSAGE));
		
		// If cleanState() has removed SiteInfo, get a new instance into state
		SiteInfo siteInfo = new SiteInfo();
		if (state.getAttribute(STATE_SITE_INFO) != null)
		{
			siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);	
		}
		else
		{
			state.setAttribute(STATE_SITE_INFO, siteInfo);	
		}
		// Lists used in more than one template
				
		//used with My Worspace tool configuration
		List currentTools = new Vector();
		List currentPages = new Vector();

		List studentList = new Vector();
		
		// Access
		List participantList = new Vector();
		List roles = new Vector();
		
		// the title hashtables 
		Hashtable newsTitles = new Hashtable();
		Hashtable wcTitles = new Hashtable();
		
		List toolRegistrationList = new Vector();
		List toolRegistrationSelectedList = new Vector();
		
		ResourceProperties siteProperties = null;
		
		switch (index)
		{
			case 0: 
				/*  buildContextForTemplate chef_site-list.vm
				*
				*/
				//make sure auto-updates are enabled
				Hashtable views = new Hashtable();
				views.put(ALL_MY_SITES, ALL_MY_SITES);
				List sTypes = (List) state.getAttribute(STATE_SITE_TYPES);
				for(int sTypeIndex = 0; sTypeIndex < sTypes.size(); sTypeIndex++)
				{
					String type = (String) sTypes.get(sTypeIndex);
					views.put("My " + type + " Sites", type);
				}
				views.put(MY_WORKSPACE, "myworkspace");
				context.put("views", views);
				
				if(state.getAttribute(STATE_VIEW_SELECTED) != null)
				{
					context.put("viewSelected", (String) state.getAttribute(STATE_VIEW_SELECTED));
				}

				String portalUrl = ServerConfigurationService.getPortalUrl();
				context.put("portalUrl", portalUrl);
				
				List sites = prepPage(state);
				context.put("sites", sites);
				
				String search = (String) state.getAttribute(STATE_SEARCH);
				context.put("search_term", search);
					
				sortedBy = (String) state.getAttribute (SORTED_BY);
				sortedAsc = (String) state.getAttribute (SORTED_ASC);
				if(sortedBy!=null) context.put ("currentSortedBy", sortedBy);
				if(sortedAsc!=null) context.put ("currentSortAsc", sortedAsc);

				context.put("totalPageNumber", new Integer(totalPageNumber(state)));
				context.put("searchString", state.getAttribute(STATE_SEARCH));
				context.put("form_search", FORM_SEARCH);
				context.put("formPageNumber", FORM_PAGE_NUMBER);
				context.put("prev_page_exists", state.getAttribute(STATE_PREV_PAGE_EXISTS));
				context.put("next_page_exists", state.getAttribute(STATE_NEXT_PAGE_EXISTS));
				context.put("current_page", state.getAttribute(STATE_CURRENT_PAGE));
				
				context.put("site_status_unpublished", new Integer(Site.SITE_STATUS_UNPUBLISHED));
				context.put("site_status_published", new Integer (Site.SITE_STATUS_PUBLISHED));
				
				// put the service in the context (used for allow update calls on each site)
				context.put("service", SiteService.getInstance());
				
				// top menu bar
				Menu bar = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				if (SiteService.allowAddSite(null))
				{
					bar.add( new MenuEntry("New...", "doNew_site"));
				}
				bar.add( new MenuEntry("Revise", null, true, MenuItem.CHECKED_NA, "doGet_site", "sitesForm"));
				bar.add( new MenuEntry("Delete", null, true, MenuItem.CHECKED_NA, "doMenu_site_delete",  "sitesForm"));
				context.put("menu", bar);
				
				// default to be no pageing
				context.put("paged", state.getAttribute(STATE_PAGING));
				
				Menu bar2 = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				
				// add the search commands
				addSearchMenus(bar2, state);
				context.put("menu2", bar2);
				
				return (String)getContext(data).get("template") + TEMPLATE[0];
			case 1: 
				/*  buildContextForTemplate chef_site-type.vm
				*	
				*/
				context.put("siteTypes", state.getAttribute(STATE_SITE_TYPES));
				context.put("form_type", siteInfo.site_type);
				
				// Clean out site in state, if there is one (i.e., we went Back), before creating a new one
				cleanState(state);
				return (String)getContext(data).get("template") + TEMPLATE[1];
			case 2:
				/*	buildContextForTemplate chef_site-courseContact.vm 
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[2];
			case 3: 
				/*   buildContextForTemplate chef_site-courseClass.vm
				*	
				*/
				return (String)getContext(data).get("template") + TEMPLATE[3];
			case 9: 
				/*   buildContextForTemplate chef_site-newSiteInformation.vm 
				* 
				*/
				context.put("siteTypes", state.getAttribute(STATE_SITE_TYPES));
				String siteType = (String) state.getAttribute(STATE_SITE_TYPE);
				context.put (FORM_TITLE,siteInfo.title);
				context.put (FORM_INCLUDE, new Boolean(siteInfo.include));
				context.put(FORM_SHORT_DESCRIPTION, siteInfo.short_description);
				context.put (FORM_DESCRIPTION,siteInfo.description);
				
				// defalt the site contact person to the site creator
				if (siteInfo.site_contact_name.equals(NULL_STRING) && siteInfo.site_contact_email.equals(NULL_STRING))
				{
					User user = UserDirectoryService.getCurrentUser();
					siteInfo.site_contact_name = user.getDisplayName();
					siteInfo.site_contact_email = user.getEmail();
				}
				context.put("form_site_contact_name", siteInfo.site_contact_name);
				context.put("form_site_contact_email", siteInfo.site_contact_email);
				return (String)getContext(data).get("template") + TEMPLATE[9];
			case 10: 
				/*   buildContextForTemplate chef_site-newSiteFeatures.vm
				* 
				*/
				siteType = (String) state.getAttribute(STATE_SITE_TYPE);
				toolRegistrationList = (List) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(siteType);
				toolRegistrationSelectedList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
				context.put (STATE_TOOL_REGISTRATION_SELECTED_LIST, toolRegistrationSelectedList); // String toolId's
				context.put (STATE_TOOL_REGISTRATION_LIST, toolRegistrationList ); // %%% use ToolRegistrations for template list
				context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
				context.put("serverName", ServerConfigurationService.getServerName());
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
				//titles for news tools
				newsTitles = (Hashtable) state.getAttribute(STATE_NEWS_TITLES);
				if (newsTitles == null)
				{
					newsTitles = new Hashtable();
					newsTitles.put("chef.news", NEWS_DEFAULT_TITLE);
					state.setAttribute(STATE_NEWS_TITLES, newsTitles);
				}
				context.put("newsTitles", newsTitles);
				// titles for web content tools
				wcTitles = (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES);
				if (wcTitles == null)
				{
					wcTitles = new Hashtable();
					wcTitles.put("chef.iframe", WEB_CONTENT_DEFAULT_TITLE);
					state.setAttribute(STATE_WEB_CONTENT_TITLES, wcTitles);
				}
				context.put("wcTitles", wcTitles);
				return (String)getContext(data).get("template") + TEMPLATE[10];
			case 11: 
				/*  buildContextForTemplate chef_site-courseInformation.vm
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[11];
			case 12: 
				/*  buildContextForTemplate chef_site-coursePublish.vm 
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[12];
			case 13: 
				/*  buildContextForTemplate chef_site-otherInvite.vm
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[13];
			case 14: 
				/*  buildContextForTemplate chef_site-otherPublish.vm 
				*	Not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[14];
			case 15: 
				/*   buildContextForTemplate chef_site-customize.vm 
				* 
				*/
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				String type = ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getType();
				context.put("type", type);
				
				// Top menu bar option to return to the site list
				Menu back_bar = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				back_bar.add( new MenuEntry("Back to list...", "doBack_to_list"));
				context.put("menu", back_bar);
				if(((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId().equals(SiteService.getUserSiteId(UsageSessionService.getSessionUserId()))
					&& SecurityService.isSuperUser())
				{
					addAlert(state, "Administration Workspace re-configuration is not currently supported in this tool.");
					context.put("admin", new Boolean(true));
					
				}
				return (String)getContext(data).get("template") + TEMPLATE[15];
			case 16: 
				/*   buildContextForTemplate chef_site-basicInformation.vm
				*
				*/
				return (String)getContext(data).get("template") + TEMPLATE[16];
			case 17: 
				/*   buildContextForTemplate chef_site-sectionAccess.vm
				* 
				*/
				Menu section_menu = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				section_menu.add( new MenuEntry("Add", null, true, MenuItem.CHECKED_NA, "doMenu_section_add", "sectionAccessForm"));
				section_menu.add( new MenuEntry("Delete", null, true, MenuItem.CHECKED_NA, "doMenu_section_delete", "sectionAccessForm"));
				context.put("section_menu", section_menu);
				return (String)getContext(data).get("template") + TEMPLATE[17];
			case 18: 
				/*  buildContextForTemplate chef_site-addRemoveSection.vm
				*   not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[18];
			case 19: 
				/*   buildContextForTemplate chef_site-sectionInformation.vm 
				*
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[19];
			case 20: 
				/*   buildContextForTemplate chef_site-deleteSection.vm 
				*
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[20];
			case 21: 
				/*  buildContextForTemplate chef_site-addRemoveFeatures.vm 
				*
				*/
				Site site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
				context.put("SiteTitle", site.getTitle());
				type = site.getType();
				
				boolean myworkspace_site = false;
				//Put up tool lists filtered by category
				List siteTypes = (List) state.getAttribute(STATE_SITE_TYPES);
				if (siteTypes.contains(type))
				{
					myworkspace_site = false;
				}
				
				if (SiteService.isUserSite(site.getId()) || (type!=null && type.equalsIgnoreCase("myworkspace")))
				{
					myworkspace_site = true;
					type="myworkspace";
				}
				
				context.put ("myworkspace_site", new Boolean(myworkspace_site));
				
				//see if wSetupPageList contains Home
				boolean check_home = false;
				boolean hasNews = false;
				boolean hasWebContent = false;
				List wSetupPages = (Vector)state.getAttribute(STATE_WORKSITE_SETUP_PAGE_LIST);

				context.put(STATE_TOOL_REGISTRATION_SELECTED_LIST, state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST));
				
				context.put("newsTitles", (Hashtable) state.getAttribute(STATE_NEWS_TITLES));
				context.put("wcTitles", (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES));
				
				context.put (STATE_TOOL_REGISTRATION_LIST, ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(type));
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
	
				//get the email alias when an Email Archive tool has been selected
				if (state.getAttribute(STATE_TOOL_EMAIL_ADDRESS) == null && state.getAttribute(STATE_MESSAGE) == null)
				{
					String channelReference = MailArchiveService.channelReference(((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId(), SiteService.MAIN_CONTAINER);
					List aliases = AliasService.getAliases(channelReference, 1, 1);
					if (aliases.size() > 0)
					{
						state.setAttribute(STATE_TOOL_EMAIL_ADDRESS, ((Alias) aliases.get(0)).getId());
					}
				}
				if (state.getAttribute(STATE_TOOL_EMAIL_ADDRESS) != null)
				{	
					context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
				}
				context.put("serverName", ServerConfigurationService.getServerName());
				
				context.put("backIndex", "42");
							
				return (String)getContext(data).get("template") + TEMPLATE[21];
			case 22: 
				/*   buildContextForTemplate chef_site-customLayout.vm 
				* 
				*/ 
				List skins = (Vector) state.getAttribute("skins");
				context.put("form_css", siteInfo.skin);
				context.put("skins", skins);
				context.put("form_iconUrl", siteInfo.iconUrl);
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				return (String)getContext(data).get("template") + TEMPLATE[22];
			case 23: 
				/*   buildContextForTemplate chef_site-customLink.vm 
				*	 Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[23];
			case 24: 
				/*  buildContextForTemplate chef_site-customPage.vm
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[24];
			case 25: 
				/*   buildContextForTemplate chef_site-roster.vm
				* 
				*/ 
				Menu rosterMenu = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				rosterMenu.add( new MenuEntry("Back to revising...", "doMenu_customize"));
				context.put("menu", rosterMenu);

				// get students
				participantList = getParticipantList(state);
				context.put("participantList", participantList);
				context.put("studentList", studentList);
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				return (String)getContext(data).get("template") + TEMPLATE[25];
			case 26: 
				/*  buildContextForTemplate chef_site-customFeatures.vm
				*	Not implemented
				*/ 
				return (String)getContext(data).get("template") + TEMPLATE[26];
			case 27: 
				/*  buildContextForTemplate chef_site-listParticipants.vm 
				*   Checkboxes for removal of participants
				*/ 
				Menu participant_bar = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				participant_bar.add( new MenuEntry("Add Participant(s)...", "doMenu_add_participant"));
				participant_bar.add( new MenuEntry("Remove Participant(s)...", null, true, MenuItem.CHECKED_NA, "doMenu_remove_participants", "removeUsersForm"));
				participant_bar.add( new MenuEntry("Change Role(s)...", null, true, MenuItem.CHECKED_NA, "doMenu_change_roles", "removeUsersForm"));
				participant_bar.add( new MenuEntry("Back to revising...", "doMenu_customize"));
				context.put("menu", participant_bar);
				participantList = getParticipantList(state);
				context.put("participantList", participantList);
				context.put("participantSelectedList", state.getAttribute(STATE_SELECTED_USER_LIST));
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				return (String)getContext(data).get("template") + TEMPLATE[27];
			case 28: 
				/*  buildContextForTemplate chef_site-addParticipant.vm 
				* 
				*/
				context.put("title", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				roles = getRoles(state);
				context.put("roles", roles);
				if(state.getAttribute("form_uniqnames")!=null)
				{
					context.put("form_uniqnames", (String)state.getAttribute("form_uniqnames"));
				}
				if(state.getAttribute("form_friends")!=null)
				{
					context.put("form_friends", (String)state.getAttribute("form_friends"));
				}
				if(state.getAttribute("form_same_role") != null)
				{
					context.put("form_same_role", ((Boolean) state.getAttribute("form_same_role")).toString());
				}
				else
				{
					context.put("form_same_role", "not_found");
				}
				context.put("backIndex", "49");
				return (String)getContext(data).get("template") + TEMPLATE[28];
			case 29: 
				/*  buildContextForTemplate chef_site-removeParticipants.vm 
				* 
				*/
				context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				realmId = "/site/" + ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
				try
				{
					Realm realm = RealmService.getRealm(realmId);
					try
					{
						List removeableList = (List) state.getAttribute(STATE_REMOVEABLE_USER_LIST);
						List removeableParticipants = new Vector();
						
						for (int k = 0; k < removeableList.size(); k++)
						{
							User user = UserDirectoryService.getUser((String) removeableList.get(k));
							Participant participant = new Participant();
							participant.name = user.getSortName();
							participant.uniqname = user.getId();
							Set uRoles = realm.getUserRoles(user);
							participant.roles = uRoles;
							removeableParticipants.add(participant);
						}
						context.put("removeableList", removeableParticipants);
					}
					catch (IdUnusedException ee)
					{
					}
				}
				catch (IdUnusedException e)
				{
				}
				
				context.put("backIndex", "49");
				return (String)getContext(data).get("template") + TEMPLATE[29];
			case 30: 
				/*  buildContextForTemplate chef_site-changeRoles.vm
				* 
				*/
				context.put("same_role", state.getAttribute(STATE_CHANGEROLE_SAMEROLE));
				roles = getRoles(state);
				context.put("roles", roles);
				context.put("roleDescription", ROLE_DESCRIPTION);
				context.put("currentRole", state.getAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE));
			
				Vector participantSelectedList = new Vector();
				List selectedUserIds = (List) state.getAttribute(STATE_SELECTED_USER_LIST);
				
				participantList = new Vector();
				Hashtable selectedParticipantRoles = new Hashtable();
				
				participantList = (List) state.getAttribute(STATE_PARTICIPANT_LIST);
				context.put("participantSelectedList", state.getAttribute(STATE_SELECTED_PARTICIPANTS));
				context.put("selectedRoles", state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES));
				context.put("siteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				return (String)getContext(data).get("template") + TEMPLATE[30];
			case 31: 
				/*  buildContextForTemplate chef_site-siteDeleteConfirm.vm
				* 
				*/
				String site_title = NULL_STRING;
				String[] removals = (String[]) state.getAttribute(STATE_SITE_REMOVALS);
				List remove = new Vector();
				String user = UsageSessionService.getSessionUserId();
				String workspace = SiteService.getUserSiteId(user);
				if( removals != null && removals.length != 0 )
				{
					for (int i = 0; i < removals.length; i++ )
					{
						String id = (String) removals[i];
						if(!(id.equals(workspace)))
						{
							try
							{
								site_title = SiteService.getSite(id).getTitle();
							}
							catch (IdUnusedException e)
							{
								Log.warn("chef", "SiteAction.doSite_delete_confirmed - IdUnusedException " + id);
								addAlert(state, "Site with id " + id + " could not be located. Please ask Course Tools NG Support to assist you. ");
							}
							if(SiteService.allowRemoveSite(id))
							{
								try
								{
									Site removeSite = SiteService.getSite(id);
									remove.add(removeSite);
								}
								catch (IdUnusedException e)
								{
									Log.warn("chef", "SiteAction.buildContextForTemplate chef_site-siteDeleteConfirm.vm: IdUnusedException");	
								}
							}
							else
							{
								addAlert(state, site_title + " could not be deleted, because you do not have permission. ");
							}
						}
						else
						{
							addAlert(state, "Your Workspace may not be deleted with this tool.");
						}
					}
					if(remove.size() == 0)
					{
						addAlert(state, "Click Cancel to return to your site list.");
					}
				}
				context.put("removals", remove);
				return (String)getContext(data).get("template") + TEMPLATE[31];
			case 32:
				/*  buildContextForTemplate chef_site-makePublic.vm
				* 
				*/
				roles = getRoles(state);
				context.put("roles", roles);
				//context.put("form_joinable", new Boolean(siteInfo.joinable));
				//context.put("form_joinerRole", siteInfo.joinerRole);
				if (state.getAttribute(STATE_JOINABLE) == null)
				{
					state.setAttribute(STATE_JOINABLE, new Boolean(siteInfo.joinable));
				}
				if (state.getAttribute(STATE_JOINERROLE) == null)
				{
					state.setAttribute(STATE_JOINERROLE, siteInfo.joinerRole);
				}
				context.put("form_joinable", state.getAttribute(STATE_JOINABLE));
				context.put("form_joinerRole", state.getAttribute(STATE_JOINERROLE));
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				return (String)getContext(data).get("template") + TEMPLATE[32];
			case 33:
				/*  buildContextForTemplate chef_site-setType.vm
				* 
				*/
				context.put("form_type", siteInfo.site_type);
				return (String)getContext(data).get("template") + TEMPLATE[33];
			case 34:
				/*  buildContextForTemplate chef_site-publishUnpublish.vm
				* 
				*/
				int publish = ((SiteInfo)state.getAttribute(STATE_SITE_INFO)).getStatus();
				if (publish == Site.SITE_STATUS_PUBLISHED)
				{
					context.put("publish", Boolean.TRUE);
				}
				else
				{
					context.put("publish", Boolean.FALSE);
				}
				context.put("publish_int_string", new Integer(Site.SITE_STATUS_PUBLISHED));
				context.put("unpublish_int_string", new Integer(Site.SITE_STATUS_UNPUBLISHED));
				context.put("backIndex", "42");
				return (String)getContext(data).get("template") + TEMPLATE[34];
			case 35:
				/*  buildContextForTemplate chef_site-roster-sort.vm
				*   not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[35];
			case 36:
				/*  buildContextForTemplate chef_site-addMyWorkspaceFeature.vm
				* not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[36];
			case 37:
				/*  buildContextForTemplate chef_site-removeMyWorkspaceFeature.vm
				* not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[37];
			case 38:
				/*  buildContextForTemplate chef_site-newSiteConfirm.vm
				* 
				*/
				siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
				context.put("title", siteInfo.title);
				context.put("description", siteInfo.description);
				context.put("short_description", siteInfo.short_description);
				context.put("siteContactName", siteInfo.site_contact_name);
				context.put("siteContactEmail", siteInfo.site_contact_email);
				siteType = (String) state.getAttribute(STATE_SITE_TYPE);
				toolRegistrationList = (Vector) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(siteType);
				toolRegistrationSelectedList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
				context.put (STATE_TOOL_REGISTRATION_SELECTED_LIST, toolRegistrationSelectedList); // String toolId's
				context.put (STATE_TOOL_REGISTRATION_LIST, toolRegistrationList ); // %%% use Tool
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
				context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
				context.put("serverName", ServerConfigurationService.getServerName());
				context.put("include", new Boolean(siteInfo.include));
				context.put("newsTitles", (Hashtable) state.getAttribute(STATE_NEWS_TITLES));
				context.put("wcTitles", (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES));
				return (String)getContext(data).get("template") + TEMPLATE[38];
			case 39:
				/*   buildContextForTemplate chef_site-courseManual.vm
				*	not implemented
				*/ 	
				return (String)getContext(data).get("template") + TEMPLATE[39];
			case 40:
				/*  buildContextForTemplate chef_site-courseConfirm.vm
				* not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[40];
			case 41:
				/*  buildContextForTemplate chef_site-newSitePublishUnpublish.vm
				* 
				*/
				return (String)getContext(data).get("template") + TEMPLATE[41];	
			case 42:
				/*  buildContextForTemplate chef_site-siteInfo-list.vm
				* 
				*/
				try
				{
					site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
					boolean isMyWorkspace = false;
					if (SiteService.isUserSite(site.getId()))
					{
						if (SiteService.getSiteUserId(site.getId()).equals(UsageSessionService.getSessionUserId()))
						{
							isMyWorkspace = true;
						}
					}
					
					String siteId = site.getId();
					context.put("siteTitle", site.getTitle());
					
					context.put("siteJoinable", new Boolean(site.isJoinable()));
					siteProperties = site.getProperties(); 
					context.put("siteCreationDate", siteProperties.getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE));
					
					boolean allowUpdateSite = SiteService.allowUpdateSite(siteId);
					List participantsList = new Vector();
					if (allowUpdateSite)
					{	
						// instructor's view
						context.put("allowUpdate", Boolean.TRUE);
						// top menu bar
						Menu b = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
						
						if (!isMyWorkspace)
						{
							b.add( new MenuEntry("Edit Site Info...", "doMenu_edit_site_info"));
						}
						b.add( new MenuEntry("Edit Tools...", "doMenu_edit_site_tools"));
						if (!isMyWorkspace)
						{
							b.add( new MenuEntry("Edit Access...", "doMenu_edit_site_access"));
							b.add( new MenuEntry("Publish...", "doMenu_publish_site"));
						}
						if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITESETUP))
						{
							b.add( new MenuEntry("Go To List View", "doMenu_back_to_site_list"));
						}
						context.put("menu", b);					
					}
					else
					{
						// student's view
						context.put("allowUpate", Boolean.FALSE);
					}
					
					//set participant list	
					setSiteParticipantList(state);
					
					site_type = (String) state.getAttribute(STATE_SITE_TYPE); 
					if(site_type != null) 
					{
						boolean allowViewRoster = SiteService.allowViewRoster(siteId);
						if (allowUpdateSite || allowViewRoster)
						{
							sortedBy = (String) state.getAttribute (SORTED_BY);
							sortedAsc = (String) state.getAttribute (SORTED_ASC);
							if(sortedBy!=null) context.put ("currentSortedBy", sortedBy);
							if(sortedAsc!=null) context.put ("currentSortAsc", sortedAsc);
							participantsList = (List) state.getAttribute(STATE_PARTICIPANT_LIST);
							Iterator sortedParticipants = null;
							if (sortedBy != null)
							{
								sortedParticipants = new SortedIterator (participantsList.iterator (), new SiteComparator (sortedBy, sortedAsc));
								participantsList.clear();
								while (sortedParticipants.hasNext())
								{
									participantsList.add(sortedParticipants.next());
								}
							}
							context.put("participantList", participantsList);
						}
					}
					
					// site contact information
					String contactName = siteProperties.getProperty(PROP_SITE_CONTACT_NAME);
					String contactEmail = siteProperties.getProperty(PROP_SITE_CONTACT_EMAIL);
					if (contactName == null && contactEmail == null)
					{
						String creatorId = siteProperties.getProperty(ResourceProperties.PROP_CREATOR);
						try
						{
							User u = UserDirectoryService.getUser(creatorId);
							String email = u.getEmail();
							if (email != null)
							{
								contactEmail = u.getEmail();	
							}
							contactName = u.getDisplayName();
						}
						catch (IdUnusedException e)
						{
						}
					}
					if (contactName != null)
					{
						context.put("contactName", contactName);
					}
					if (contactEmail != null)
					{
						context.put("contactEmail", contactEmail);
					}
				
				}
				catch (Exception e)
				{
					Log.warn("chef", this + " site info list: " + e.toString());
				}
				
				return (String)getContext(data).get("template") + TEMPLATE[42];		
			case 43:
				/*  buildContextForTemplate chef_site-siteInfo-editInfo.vm
				* 
				*/
				siteProperties = ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getProperties();
				
				context.put("title", state.getAttribute(FORM_SITEINFO_TITLE));
				if (SecurityService.isSuperUser())
				{
					context.put("isSuperUser", Boolean.TRUE);
				}
				else
				{
					context.put("isSuperUser", Boolean.FALSE);
				}
				context.put("description", state.getAttribute(FORM_SITEINFO_DESCRIPTION));
				context.put("short_description", state.getAttribute(FORM_SITEINFO_SHORT_DESCRIPTION));
				context.put("skins", state.getAttribute("skins"));
				context.put("skin", state.getAttribute(FORM_SITEINFO_SKIN));
				context.put("include", state.getAttribute(FORM_SITEINFO_INCLUDE));
				context.put("form_site_contact_name", state.getAttribute(FORM_SITEINFO_CONTACT_NAME));
				context.put("form_site_contact_email", state.getAttribute(FORM_SITEINFO_CONTACT_EMAIL));

				return (String)getContext(data).get("template") + TEMPLATE[43];	
			case 44:
				/*  buildContextForTemplate chef_site-siteInfo-editInfoConfirm.vm
				* 
				*/
				SiteEdit siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
				siteProperties = siteEdit.getProperties();
				
				context.put("oTitle", siteEdit.getTitle());
				context.put("title", state.getAttribute(FORM_SITEINFO_TITLE));
				
				site_type = (String)state.getAttribute(STATE_SITE_TYPE); 
				context.put("description", state.getAttribute(FORM_SITEINFO_DESCRIPTION));
				context.put("oDescription", siteEdit.getDescription());
				context.put("short_description", state.getAttribute(FORM_SITEINFO_SHORT_DESCRIPTION));
				context.put("oShort_description", siteProperties.getProperty(PROP_SITE_SHORT_DESCRIPTION));
				context.put("skin", state.getAttribute(FORM_SITEINFO_SKIN));
				context.put("oSkin", siteProperties.getProperty(PROP_SITE_SKIN));
				context.put("skins", state.getAttribute("skins"));
				context.put("include", state.getAttribute(FORM_SITEINFO_INCLUDE));
				String oInclude = (String) siteProperties.getProperty(PROP_SITE_INCLUDE);
				if (oInclude != null && oInclude.equalsIgnoreCase(Boolean.TRUE.toString()))
				{
					context.put("oInclude", Boolean.TRUE.toString());
				}
				else
				{
					context.put("oInclude", Boolean.FALSE.toString());
				}
				context.put("name", state.getAttribute(FORM_SITEINFO_CONTACT_NAME));
				context.put("oName", siteProperties.getProperty(PROP_SITE_CONTACT_NAME));
				context.put("email", state.getAttribute(FORM_SITEINFO_CONTACT_EMAIL));
				context.put("oEmail", siteProperties.getProperty(PROP_SITE_CONTACT_EMAIL));
				
				return (String)getContext(data).get("template") + TEMPLATE[44];		
			case 45:
				/*  buildContextForTemplate chef_site-addRemoveFeatureConfirm.vm
				* 
				*/
				siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
				context.put("title", siteEdit.getTitle());
				
				site_type = (String)state.getAttribute(STATE_SITE_TYPE); 
				myworkspace_site = false;
				if (SiteService.isUserSite(siteEdit.getId()))
				{
					if (SiteService.getSiteUserId(siteEdit.getId()).equals(UsageSessionService.getSessionUserId()))
					{
						myworkspace_site = true;
						site_type = "myworkspace";
					}
				}
				
				context.put(STATE_TOOL_REGISTRATION_LIST, ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(site_type));
				
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
				context.put("selectedTools", state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST));
				context.put("oldSelectedTools", state.getAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST));	
				context.put("oldSelectedHome", state.getAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME));
				context.put("continueIndex", "42");
				if (state.getAttribute(STATE_TOOL_EMAIL_ADDRESS) != null)
				{	
					context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
				}
				context.put("serverName", ServerConfigurationService.getServerName());
				context.put("newsTitles", (Hashtable) state.getAttribute(STATE_NEWS_TITLES));
				context.put("wcTitles", (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES));
				
				return (String)getContext(data).get("template") + TEMPLATE[45];		
			case 46:
				/*  buildContextForTemplate chef_site-publishUnpublish-sendEmail.vm
				* 
				*/
				siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
				context.put("title", siteEdit.getTitle());
				context.put("willNotify", state.getAttribute(FORM_WILL_NOTIFY));
				return (String)getContext(data).get("template") + TEMPLATE[46];		
			case 47:
				/*  buildContextForTemplate chef_site-publishUnpublish-confirm.vm
				* 
				*/
				siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
				context.put("title", siteEdit.getTitle());
				context.put("continueIndex", "42");
				SiteInfo sInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
				if (sInfo.getStatus() == Site.SITE_STATUS_PUBLISHED)
				{
					context.put("publish", Boolean.TRUE);
					context.put("backIndex", "46");
				}
				else
				{
					context.put("publish", Boolean.FALSE);
					context.put("backIndex", "34");
				}
				context.put("willNotify", state.getAttribute(FORM_WILL_NOTIFY));
				return (String)getContext(data).get("template") + TEMPLATE[47];	
		case 49:
			/*  buildContextForTemplate chef_siteInfo-editAccess.vm
			* 
			*/
			site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
			context.put("siteTitle", site.getTitle());
			
			siteProperties = site.getProperties();
			site_type = (String)state.getAttribute(STATE_SITE_TYPE);
			
			boolean allowUpdateSite = SiteService.allowUpdateSite(site.getId());
			List participantsList = new Vector();
			if (allowUpdateSite)
			{	
				// instructor's view
				context.put("allowUpdate", Boolean.TRUE);
				// top menu bar
				Menu b = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				b.add( new MenuEntry("Add participant...", "doMenu_siteInfo_addParticipant"));
				b.add( new MenuEntry("Remove Participant(s)...", null, true, MenuItem.CHECKED_NA, "doMenu_siteInfo_removeParticipant", "editParticipantForm"));
				b.add( new MenuEntry("Change Role(s)...", null, true, MenuItem.CHECKED_NA, "doMenu_siteInfo_changeRole", "editParticipantForm"));
				b.add( new MenuEntry("Global Access...", "doMenu_siteInfo_globalAccess"));
				b.add( new MenuEntry("Go to Site Info", "doMenu_siteInfo_cancel_access"));
				context.put("menu", b);					
			}
			else
			{
				// student's view
				context.put("allowUpate", Boolean.FALSE);
			}
				
			// set site participant list
			setSiteParticipantList(state);
			
			if(site_type != null) 
			{
				// project site
				context.put("isProjectSite", Boolean.TRUE);
				boolean allowViewRoster = SiteService.allowViewRoster(site.getId());
				if (allowViewRoster)
				{
					context.put("viewRoster", Boolean.TRUE);
				}
				else
				{
					context.put("viewRoster", Boolean.FALSE);
				}
						
				if (allowUpdateSite || allowViewRoster)
				{
					sortedBy = (String) state.getAttribute (SORTED_BY);
					sortedAsc = (String) state.getAttribute (SORTED_ASC);
					if(sortedBy!=null) context.put ("currentSortedBy", sortedBy);
					if(sortedAsc!=null) context.put ("currentSortAsc", sortedAsc);
					participantsList = (List) state.getAttribute(STATE_PARTICIPANT_LIST);
					Iterator sortedParticipants = null;
					if (sortedBy != null)
					{
						sortedParticipants = new SortedIterator (participantsList.iterator (), new SiteComparator (sortedBy, sortedAsc));
						participantsList.clear();
						while (sortedParticipants.hasNext())
						{
							participantsList.add(sortedParticipants.next());
						}
					}
					context.put("participantList", participantsList);
				}
			}
			
			// the selected participants
			List ids = (List) state.getAttribute(STATE_SELECTED_USER_LIST);
			List selectedParticipants = new Vector();
			realmId = "/site/" + ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
			if (ids != null)
			{
				try
				{
					Realm realm = RealmService.getRealm(realmId);
					try
					{
		
						for (int k = 0; k < ids.size(); k++)
						{
							User u = UserDirectoryService.getUser((String) ids.get(k));
							Participant participant = new Participant();
							participant.name = u.getSortName();
							participant.uniqname = u.getId();
							Set uRoles = realm.getUserRoles(u);
							participant.roles = uRoles;
							selectedParticipants.add(participant);
						}
					}
					catch (IdUnusedException ee)
					{
					}
				}
				catch (IdUnusedException e)
				{
				}
			}
			context.put("selectedParticipants", selectedParticipants);
			return (String)getContext(data).get("template") + TEMPLATE[49];
		case 50:
			/*  buildContextForTemplate chef_site-addParticipant-sameRole.vm
			* 
			*/
			context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			context.put("roleDescription", ROLE_DESCRIPTION);
			context.put("roles", getRoles(state));
			context.put("participantList", state.getAttribute(STATE_ADD_PARTICIPANTS));
			context.put("form_selectedRole", state.getAttribute("form_selectedRole"));
			return (String)getContext(data).get("template") + TEMPLATE[50];
		case 51:
			/*  buildContextForTemplate chef_site-addParticipant-differentRole.vm
			* 
			*/
			context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			context.put("roleDescription", ROLE_DESCRIPTION);
			context.put("roles", getRoles(state));
			context.put("selectedRoles", state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES));
			context.put("participantList", state.getAttribute(STATE_ADD_PARTICIPANTS));
			return (String)getContext(data).get("template") + TEMPLATE[51];
		case 52:
			/*  buildContextForTemplate chef_site-addParticipant-notification.vm
			* 
			*/
			context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			if (state.getAttribute("form_selectedNotify") == null)
			{
				state.setAttribute("form_selectedNotify", Boolean.FALSE);
			}
			context.put("notify", state.getAttribute("form_selectedNotify"));
			boolean same_role = ((Boolean) state.getAttribute("form_same_role")).booleanValue();
			if (same_role)
			{
				context.put("backIndex", "50");
			}
			else
			{
				context.put("backIndex", "51");
			}
			return (String)getContext(data).get("template") + TEMPLATE[52];
		case 53:
			/*  buildContextForTemplate chef_site-addParticipant-confirm.vm
			* 
			*/
			context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			context.put("participants", state.getAttribute(STATE_ADD_PARTICIPANTS));
			context.put("notify", state.getAttribute("form_selectedNotify"));
			context.put("roles", getRoles(state));
			context.put("same_role", state.getAttribute("form_same_role"));
			context.put("selectedRoles", state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES));
			context.put("selectedRole", state.getAttribute("form_selectedRole"));
			return (String)getContext(data).get("template") + TEMPLATE[53];
		case 54:
			/*  buildContextForTemplate chef_siteInfo-editAccess-globalAccess.vm
			* 
			*/
			Site s = (Site) state.getAttribute(STATE_SITE_INSTANCE);
			context.put("title", s.getTitle());
			context.put("roles", getRoles(state));
			if (state.getAttribute("form_joinable") == null)
			{
				state.setAttribute("form_joinable", new Boolean(s.isJoinable()));
			}
			context.put("form_joinable", state.getAttribute("form_joinable"));
			if (state.getAttribute("form_joinerRole") == null)
			{
				state.setAttribute("form_joinerRole", s.getJoinerRole());
			}
			context.put("form_joinerRole", state.getAttribute("form_joinerRole"));
			context.put("roleDescription", ROLE_DESCRIPTION);
			return (String)getContext(data).get("template") + TEMPLATE[54];
		case 55:
			/*  buildContextForTemplate chef_siteInfo-editAccess-globalAccess-confirm.vm
			* 
			*/
			context.put("title", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			context.put("form_joinable", state.getAttribute("form_joinable"));
			context.put("form_joinerRole", state.getAttribute("form_joinerRole"));
			return (String)getContext(data).get("template") + TEMPLATE[55];
		case 56:
			/*  buildContextForTemplate chef_changeRoles-confirm.vm
			 * 
			 */
			Boolean sameRole = (Boolean) state.getAttribute(STATE_CHANGEROLE_SAMEROLE);
			context.put("sameRole", sameRole);
			if (sameRole.booleanValue())
			{
				// same role
				context.put("currentRole", state.getAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE));
			}
			else
			{
				context.put("selectedRoles", state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES));
			}
			
			roles = getRoles(state);
			context.put("roles", roles);
			context.put("roleDescription", ROLE_DESCRIPTION);
			
			context.put("participantSelectedList", state.getAttribute(STATE_SELECTED_PARTICIPANTS));
			if (state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES) != null)
			{
				context.put("selectedRoles", state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES));
			}
			context.put("siteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			return (String)getContext(data).get("template") + TEMPLATE[56];
		case 57: 
			/*  buildContextForTemplate chef_site-removeClass.vm
			*   not implemented
			*/
			return (String)getContext(data).get("template") + TEMPLATE[57];
		case 58: 
			/*  buildContextForTemplate chef_site-addRemoveClassConfirm.vm
			*  not implemented
			*/
			return (String)getContext(data).get("template") + TEMPLATE[58];
		}
		// should never be reached
		return (String)getContext(data).get("template") + TEMPLATE[0];
		
	} // buildContextForTemplate

	/**
	* doNew_site is called when the Site list tool bar New... button is clicked
	* 
	*/
	public void doNew_site ( RunData data )
		throws Exception
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		List siteTypes = (List) state.getAttribute(STATE_SITE_TYPES);
		if ((siteTypes != null) && siteTypes.size() == 1)
		{
			// skip the page of choosing site type
			setNewSiteType(state, (String) siteTypes.get(0));
		}
		else
		{
			state.setAttribute (STATE_TEMPLATE_INDEX, "1");
		}
		
	}	// doNew_site
	
	/**
	* doMenu_site_delete is called when the Site list tool bar Delete button is clicked
	* 
	*/
	public void doMenu_site_delete ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		if (params.getStrings ("selectedMembers") == null)
		{
			addAlert(state, NO_SITE_SELECTED_STRING);
			state.setAttribute(STATE_TEMPLATE_INDEX, "0");
			return;
		}
		String[] removals = (String[]) params.getStrings ("selectedMembers");
		state.setAttribute(STATE_SITE_REMOVALS, removals );
		
		//present confirm delete template
		state.setAttribute(STATE_TEMPLATE_INDEX, "31");
		
	} // doMenu_site_delete
	
	public void doSite_delete_confirmed ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		if (params.getStrings ("selectedMembers") == null)
		{
			Log.warn("chef", "SiteAction.doSite_delete_confirmed selectedMembers null");
			state.setAttribute(STATE_TEMPLATE_INDEX, "0"); // return to the site list
			return;
		}
		List chosenList = new ArrayList(Arrays.asList(params.getStrings ("selectedMembers"))); // Site id's of checked sites
		if(!chosenList.isEmpty())
		{
			for (ListIterator i = chosenList.listIterator(); i.hasNext(); )
			{
				String id = (String)i.next();
				String site_title = NULL_STRING;
				try
				{
					site_title = SiteService.getSite(id).getTitle();
				}
				catch (IdUnusedException e)
				{
					Log.warn("chef", "SiteAction.doSite_delete_confirmed - IdUnusedException " + id);
					addAlert(state, "Site with id " + id + " could not be located. Please ask Course Tools NG Support to assist you. ");
				}
				if(SiteService.allowRemoveSite(id))
				{
					
					try
					{
						SiteEdit site = SiteService.editSite(id);
						site_title = site.getTitle();
						SiteService.removeSite(site);
						//SiteService.commitEdit(site);
					}
					catch (IdUnusedException e)
					{
						Log.warn("chef", "SiteAction.doSite_delete_confirmed - IdUnusedException " + id);
						addAlert(state, "Site with id " + id + " could not be located. Please ask Course Tools NG Support to assist you. ");
					}
					catch (PermissionException e)
					{
						Log.warn("chef", "SiteAction.doSite_delete_confirmed -  PermissionException, site " + id);
						addAlert(state, site_title + " could not be deleted. You do not have permission to remove this site. ");
					}
					catch (InUseException e)
					{
						Log.warn("chef", "SiteAction.doSite_delete_confirmed -  InUseException, site  " + id);
						addAlert(state, site_title + " could not be deleted. You are already editing this site in another tool, or someone else is editing this site. ");
					}
				}
				else
				{
					Log.warn("chef", "SiteAction.doSite_delete_confirmed -  allowRemoveSite failed for site " + id);
					addAlert(state, site_title + " could not be deleted. You do not have permission to remove this site. ");
				}
			}
		}
		state.setAttribute(STATE_TEMPLATE_INDEX, "0"); // return to the site list

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));
		
	} // doSite_delete_confirmed
	
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	* is called from site list menu entry Revise... to get a locked site as editable and to go to the correct template to begin
	* DB version of writes changes to disk at site commit whereas XML version writes at server shutdown
	*/
	public void doGet_site ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		ParameterParser params = data.getParameters ();
		
		//check form filled out correctly
		if (params.getStrings ("selectedMembers") == null)
		{
			addAlert(state, NO_SITE_SELECTED_STRING);
			state.setAttribute(STATE_TEMPLATE_INDEX, "0");
			return;
		}
		List chosenList = new ArrayList(Arrays.asList(params.getStrings ("selectedMembers"))); // Site id's of checked sites
		if(!chosenList.isEmpty())
		{
			if(chosenList.size() != 1)
			{
				addAlert(state, MORE_THAN_ONE_SITE_SELECTED_STRING);
				state.setAttribute(STATE_TEMPLATE_INDEX, "0");
				return;
			}
			
			getReviseSite(state, (String) chosenList.get(0));
		}
		
	} // doGet_site
	
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	*/
	public void doMenu_site_reuse ( RunData data )
		throws Exception
	{
		// called from chef_site-list.vm after a site has been selected from list
		// create a new SiteEdit object based on selected SiteEdit object and put in state
		//
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "1");
		
	}	// doMenu_site_reuse
	
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	*/
	public void doMenu_site_revise ( RunData data )
		throws Exception
	{
		// called from chef_site-list.vm after a site has been selected from list
		// get site as SiteEdit object, check SiteCreationStatus and SiteType of site, put in state, and set STATE_TEMPLATE_INDEX correctly
		// set mode to state_mode_site_type
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "1");
		
	}	// doMenu_site_revise
	
	/**
	* doView_sites is called when "eventSubmit_doView_sites" is in the request parameters
	*/
	public void doView_sites ( RunData data )
		throws Exception
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		state.setAttribute (STATE_VIEW_SELECTED, params.getString("view"));
		state.setAttribute (STATE_TEMPLATE_INDEX, "0");
		
		resetPaging(state);
		
	}	// doView_sites
	
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	*/
	public void doView ( RunData data )
		throws Exception
	{
		// called from chef_site-list.vm with a select option to build query of sites
		// 
		// 
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "1");
	}	// doView
	
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	*/
	public void doSite_type ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		
		ParameterParser params = data.getParameters ();
		int index = Integer.valueOf(params.getString ("template-index")).intValue();
		actionForTemplate("continue", index, params, state);
		
		setNewSiteType(state, params.getString ("itemType"));
		
	}	// doSite_type
	
	/**
	 * set the site type for new site
	 * 
	 * @param state The SessionState object
	 * @param type The type String
	 */
	private void setNewSiteType(SessionState state, String type)
	{
		state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
	
		//start out with fresh site information
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.site_type = type;
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		
		state.setAttribute (STATE_SITE_TYPE, type);
		state.setAttribute (STATE_TEMPLATE_INDEX, "9");
	}
	
	
	/**
	* Set the field on which to sort the list of students
	* 
	*/
	public void doSort_roster ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get the field on which to sort the student list
		ParameterParser params = data.getParameters ();
		String criterion = params.getString ("criterion");
		
		// current sorting sequence
		String asc = "";
		if (!criterion.equals (state.getAttribute (SORTED_BY)))
		{
			state.setAttribute (SORTED_BY, criterion);
			asc = Boolean.TRUE.toString ();
			state.setAttribute (SORTED_ASC, asc);
		}
		else
		{
			// current sorting sequence
			asc = (String) state.getAttribute (SORTED_ASC);
			
			//toggle between the ascending and descending sequence
			if (asc.equals (Boolean.TRUE.toString ()))
			{
				asc = Boolean.FALSE.toString ();
			}
			else
			{
				asc = Boolean.TRUE.toString ();
			}
			state.setAttribute (SORTED_ASC, asc);
		}
		
	} //doSort_roster 
	
	/**
	* Set the field on which to sort the list of sites 
	* 
	*/
	public void doSort_sites ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//call this method at the start of a sort for proper paging
		resetPaging(state);
		
		//get the field on which to sort the site list
		ParameterParser params = data.getParameters ();
		String criterion = params.getString ("criterion");
		
		// current sorting sequence
		String asc = "";
		if (!criterion.equals (state.getAttribute (SORTED_BY)))
		{
			state.setAttribute (SORTED_BY, criterion);
			asc = Boolean.TRUE.toString ();
			state.setAttribute (SORTED_ASC, asc);
		}
		else
		{
			// current sorting sequence
			asc = (String) state.getAttribute (SORTED_ASC);
			
			//toggle between the ascending and descending sequence
			if (asc.equals (Boolean.TRUE.toString ()))
			{
				asc = Boolean.FALSE.toString ();
			}
			else
			{
				asc = Boolean.TRUE.toString ();
			}
			state.setAttribute (SORTED_ASC, asc);
		}
		
	} //doSort_sites	
	
	/**
	* doContinue is called when "eventSubmit_doContinue" is in the request parameters
	*/
	public void doContinue ( RunData data )
	{
		// Put current form data in state and continue to the next template, make any permanent changes
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		ParameterParser params = data.getParameters ();
		int index = Integer.valueOf(params.getString ("template-index")).intValue();

		// Let actionForTemplate know to make any permanent changes before continuing to the next template
		String direction = "continue";
		actionForTemplate(direction, index, params, state);
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			if (index == 34)
			{
				// go to the send site publish email page if "publish" option is chosen
				SiteInfo sInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
				int status = sInfo.getStatus();
				if (status == Site.SITE_STATUS_PUBLISHED)
				{
					state.setAttribute(STATE_TEMPLATE_INDEX, "46");		
				}
				else
				{
					state.setAttribute(STATE_TEMPLATE_INDEX, "47");
				}
			}
			else if (params.getString ("continue") != null)
			{
				state.setAttribute(STATE_TEMPLATE_INDEX, params.getString ("continue"));
			}
		}
		
	}// doContinue
	
	/**
	* doBack is called when "eventSubmit_doBack" is in the request parameters
	* Pass parameter to actionForTemplate to request action for backward direction
	*/
	public void doBack ( RunData data )
	{
		// Put current form data in state and return to the previous template
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		ParameterParser params = data.getParameters ();
		int currentIndex = Integer.parseInt((String)state.getAttribute(STATE_TEMPLATE_INDEX));
		state.setAttribute(STATE_TEMPLATE_INDEX, params.getString ("back"));
		
		// Let actionForTemplate know not to make any permanent changes before continuing to the next template
		String direction = "back";
		actionForTemplate(direction, currentIndex, params, state);
	
	}// doContinue
	
	/**
	* doFinish is called when a site has enough information to be saved as an unpublished site
	*/
	public void doFinish ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		ParameterParser params = data.getParameters ();

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, params.getString ("continue"));
			int index = Integer.valueOf(params.getString ("template-index")).intValue();
			actionForTemplate("continue", index, params, state);
			
			addNewSite(params, state);
			
			addFeatures(state);
		
			// save
			commitSite(state);
					
		}
		
	}// doFinish
	
	/**
	* doCancel called when "eventSubmit_doCancel_create" is in the request parameters to c
	*/
	public void doCancel_create ( RunData data )
	{
		// Don't put current form data in state, just return to the previous template
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		state.setAttribute(STATE_TEMPLATE_INDEX, "0");
		
	} // doCancel_create
	
	/**
	* doCancel called when "eventSubmit_doCancel" is in the request parameters to c
	* int index = Integer.valueOf(params.getString ("template-index")).intValue();
	*/
	public void doCancel ( RunData data )
	{
		// Don't put current form data in state, just return to the previous template
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		ParameterParser params = data.getParameters ();
		
		String currentIndex = (String)state.getAttribute(STATE_TEMPLATE_INDEX);
		
		String backIndex = params.getString("back");
		state.setAttribute(STATE_TEMPLATE_INDEX, backIndex);
		if (currentIndex.equals("21"))
		{
			state.removeAttribute(STATE_TOOL_EMAIL_ADDRESS);
			state.removeAttribute(STATE_MESSAGE);
			removeEditToolState(state);
			removeSiteEditFromState(state);
		}
		else if (currentIndex.equals("28"))
		{
			//remove related state variables 
			removeAddParticipantContext(state);
			removeSiteEditFromState(state);
			
			params = data.getParameters ();
			state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("back"));
		}
		else if (currentIndex.equals("29"))
		{
			state.removeAttribute(STATE_REMOVEABLE_USER_LIST);
		}
		else if (currentIndex.equals("34"))
		{
			state.removeAttribute(FORM_WILL_NOTIFY);
			removeSiteEditFromState(state);
		}
		else if (currentIndex.equals("47") || currentIndex.equals("46"))
		{
			state.removeAttribute(FORM_WILL_NOTIFY);
			state.setAttribute(STATE_TEMPLATE_INDEX, "42");
			removeSiteEditFromState(state);
		}
		else if (currentIndex.equals("43") || currentIndex.equals("44"))
		{
			// clean state attributes
			state.removeAttribute(FORM_SITEINFO_TITLE);
			state.removeAttribute(FORM_SITEINFO_DESCRIPTION);
			state.removeAttribute(FORM_SITEINFO_SHORT_DESCRIPTION);
			state.removeAttribute(FORM_SITEINFO_SKIN);
			state.removeAttribute(FORM_SITEINFO_INCLUDE);
			state.setAttribute(STATE_TEMPLATE_INDEX, "42");
			removeSiteEditFromState(state);
		}
		else if (currentIndex.equals("45"))
		{
			params = data.getParameters ();
			state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("cancelIndex"));
			removeSiteEditFromState(state);
			removeEditToolState(state);
		}
		else if (currentIndex.equals("50") || currentIndex.equals("51") || currentIndex.equals("52") || currentIndex.equals("53"))
		{
			// from adding participant pages
			//remove related state variables 
			removeAddParticipantContext(state);
			//remove edit object
			removeSiteEditFromState(state);
			
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		else if (currentIndex.equals("54") || currentIndex.equals("55"))
		{
			// from change global access
			state.removeAttribute("form_joinable");
			state.removeAttribute("form_joinerRole");
			
			// remove edit object
			removeSiteEditFromState(state);
			
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		else if (currentIndex.equals("30") || currentIndex.equals("56"))
		{
			//from change role
			//remove edit object
			removeSiteEditFromState(state);
			
			removeChangeRoleContext(state);
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		else if (currentIndex.equals("18") || currentIndex.equals("57") || currentIndex.equals("58") || currentIndex.equals("59"))
		{
			//from adding class
			//remove edit object
			removeSiteEditFromState(state);
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		else if (currentIndex.equals("3"))
		{
			//from adding class
			//remove edit object
			removeSiteEditFromState(state);
			if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITESETUP))
			{
				state.setAttribute(STATE_TEMPLATE_INDEX, "0");
			}	
			else if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITEINFO))
			{	
				state.setAttribute(STATE_TEMPLATE_INDEX, "49");
			}
		}
		else if (currentIndex.equals("57"))
		{
			//from removing class
			//remove edit object
			removeSiteEditFromState(state);
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		
	} // doCancel
	
	/** 
	 * remove SiteEdit object from state, use a Site object instead
	 * @param state SessionState object
	 */
	private void removeSiteEditFromState(SessionState state)
	{
		// cancel edit 
		try
		{
			SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
			String id = site.getId();
			SiteService.cancelEdit(site);

			Site updated_site = SiteService.getSite(id);
			state.setAttribute(STATE_SITE_INSTANCE, updated_site);
		}
		catch (ClassCastException ignore)
		{
			Log.warn("chef", "SiteAction.doCancel no SiteEdit in state");
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", this + e.toString());
		}	
	}	// removeSiteEdit
	
	/**
	* doMenu_customize is called when "eventSubmit_doBack" is in the request parameters
	* Pass parameter to actionForTemplate to request action for backward direction
	*/
	public void doMenu_customize ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		state.setAttribute(STATE_TEMPLATE_INDEX, "15");
	
	}// doMenu_customize
	 	 
	/**
	* doBack_to_list cancels an outstanding site edit, cleans state and returns to the site list
	* 
	*/
	public void doBack_to_list ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		if (state.getAttribute(STATE_SITE_INSTANCE) != null)
		{
			try
			{
				SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
				SiteService.cancelEdit(site);
			}
			catch (ClassCastException ignore)
			{
				Log.warn("chef", "SiteAction.doCancel no site in state");
			}
			// Re-initialize and return to the site list.
		}
		cleanState(state);
		setupFormNamesAndConstants(state);
		state.setAttribute(STATE_TEMPLATE_INDEX, "0");

	}	// doBack_to_list
		
	/**
	* do called when "eventSubmit_do" is in the request parameters to c
	*/
	public void doAdd_custom_link ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		if ((params.getString("name")) == null || (params.getString("url") == null))
		{
			ToolRegistration tr = ServerConfigurationService.getToolRegistration("chef.iframe");
			String toolRegId = (String) tr.getId();
			SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
			SitePageEdit page = site.addPage();
			page.setTitle(params.getString("name")); // the visible label on the tool menu
			ToolConfigurationEdit tool = page.addTool();
			tool.setToolId(toolRegId);
			tool.setTitle(params.getString("name"));
			ResourceProperties rp = tr.getDefaultConfiguration();
			ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
			rpe.addAll(rp); // set tool properties to the default tool configuration
			commitSite(state);
		}
		else
		{
			addAlert(state, "Required field(s) missing. Please enter all required fields.");
			state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("template-index"));
		}
		
	} // doAdd_custom_link
	
	/**
	* doAdd_remove_features is called when Make These Changes is clicked in chef_site-addRemoveFeatures
	*/
	public void doAdd_remove_features ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		String option = params.getString("option");
		
		// get the corresponding state variable name
		Site s = (Site) state.getAttribute(STATE_SITE_INSTANCE);
		String siteType = s.getType();
		if (siteType == null)
		{
			if (SiteService.isUserSite(s.getId()))
			{
				siteType = "myworkspace";
			}
		}
		// dispatch
		if (option.equalsIgnoreCase("addNews"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, siteType);
				insertTool(state, siteType, "chef.news", STATE_NEWS_TITLES, NEWS_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("addWebContent"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, siteType);
				insertTool(state, siteType, "chef.iframe", STATE_WEB_CONTENT_TITLES, WEB_CONTENT_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("save"))
		{
			updateSelectedToolList(state, params, siteType);
			//getFeatures(params, state);
			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				state.setAttribute(STATE_TEMPLATE_INDEX, "45");
			}
		}
		else if (option.equalsIgnoreCase("Cancel"))
		{
			//cancel
			doCancel(data);
		}
	} // doAdd_remove_features
	
	/**
	* doSave_revised_features
	*/
	public void doSave_revised_features ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		getRevisedFeatures(params, state);
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		SiteService.commitEdit(site);
		
		//now that the site exists, we can set the email alias when an Email Archive tool has been selected
		String alias = StringUtil.trimToNull((String) state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
		if (alias != null)
		{	
			String channelReference = MailArchiveService.channelReference(id, SiteService.MAIN_CONTAINER);
			try
			{
				AliasService.setAlias(alias, channelReference);
			}
			catch (IdUsedException ee) 
			{
			}
			catch (IdInvalidException ee) 
			{
				addAlert(state, "Alias " + alias + " is invalid.");
			}
			catch (PermissionException ee) 
			{
				addAlert(state, "You do not have permission to add alias. ");
			}
		}
		
		if (state.getAttribute(STATE_MESSAGE)== null)
		{
			// clean state variables
			state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
			state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST);
			state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
			setupToolLists(state);
			
			try
			{
				Site updated_site = SiteService.getSite(id);
				state.setAttribute(STATE_SITE_INSTANCE, updated_site);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + " IdUnusedException " + id + " not found");
			}

			state.setAttribute(STATE_TEMPLATE_INDEX, params.getString ("continue"));
		}
		
		//get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshTopDelivery(PortalService.getCurrentClientWindowId(null)));
				
	}	// doSave_revised_features

	/**
	* doMenu_add_participant
	*/
	public void doMenu_add_participant ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.setAttribute (STATE_TEMPLATE_INDEX, "28");
		
		//get site edit
		siteEditIntoState(state);
		
	} //  doMenu_add_participant
	
	/**
	* doMenu_siteInfo_addParticipant
	*/
	public void doMenu_siteInfo_addParticipant ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get site edit
		siteEditIntoState(state);
		
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute (STATE_TEMPLATE_INDEX, "28");
		}
		
	} //  doMenu_siteInfo_addParticipant
	
	/**
	 * doMenu_siteInfo_removeParticipant
	 */
	public void doMenu_siteInfo_removeParticipant( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		ParameterParser params = data.getParameters ();
	
		if (params.getStrings ("selectedUser") == null)
		{
			addAlert(state, "No users available/selected for removal.");
		}
		else
		{
			List removeUser = Arrays.asList(params.getStrings ("selectedUser")); 
			
			// all or some selected user(s) can be removed, go to confirmation page
			if (removeUser.size() > 0)
			{
				state.setAttribute (STATE_TEMPLATE_INDEX, "29");
			}
			else
			{
				addAlert(state, "You cannot remove participants officially registered for this course. However, you can change their roles in the site by clicking the Change Role(s) command (...under Revise, Edit Access.)");
			}
			
			state.setAttribute (STATE_REMOVEABLE_USER_LIST, removeUser);
		}
			
	}	// doMenu_siteInfo_removeParticipant
	
	/**
	* doMenu_siteInfo_changeRole 
	*/
	public void doMenu_siteInfo_changeRole ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		ParameterParser params = data.getParameters ();
		if (params.getStrings ("selectedUser") == null)
		{
			state.removeAttribute(STATE_SELECTED_USER_LIST);
			addAlert(state, "No users available/selected for changing roles.");
		}
		else
		{
			state.setAttribute (STATE_CHANGEROLE_SAMEROLE, Boolean.TRUE);

			List selectedUserIds = Arrays.asList(params.getStrings ("selectedUser"));
			state.setAttribute (STATE_SELECTED_USER_LIST, selectedUserIds);
			
			// get roles for selected participants
			setSelectedParticipantRoles(state);
			
			//get site edit
			siteEditIntoState(state);
			
			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				state.setAttribute (STATE_TEMPLATE_INDEX, "30");	
			}
		} 
	
	} // doMenu_siteInfo_changeRole
	
	/**
	 *  doMenu_siteInfo_globalAccess
	 */
	public void doMenu_siteInfo_globalAccess(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get site edit
		siteEditIntoState(state);
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "54");
		}
		
	}	//doMenu_siteInfo_globalAccess
	
	/**
	 * doMenu_siteInfo_addClass
	 */
	public void doMenu_siteInfo_addClass( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get site edit
		siteEditIntoState(state);
		Site site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
		
		state.setAttribute(STATE_TEMPLATE_INDEX, "59");
		
	}	// doMenu_siteInfo_addClass
	
	/**
	 * doMenu_siteInfo_removeClass
	 */
	public void doMenu_siteInfo_removeClass( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		//get site edit
		siteEditIntoState(state);
		Site site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
		
		state.setAttribute(STATE_TEMPLATE_INDEX, "57");
		
	}	// doMenu_siteInfo_removeClass
	
	/**
	 * doMenu_siteInfo_cancel_access
	 * @param data
	 */
	public void doMenu_siteInfo_cancel_access( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.setAttribute(STATE_TEMPLATE_INDEX, "42");
		
	}	// doMenu_siteInfo_cancel_access
	
	/**
	* doMenu_change_roles 
	*/
	public void doMenu_change_roles ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		if (params.getStrings ("removeUser") != null)
		{
			state.setAttribute(STATE_SELECTED_USER_LIST, new ArrayList(Arrays.asList(params.getStrings ("removeUser"))));
			state.setAttribute (STATE_TEMPLATE_INDEX, "30");
		}
		else
		{
			addAlert(state, "No users available/selected for changing roles.");
		}
		
	} // doMenu_change_roles
	
	/**
	* doMenu_section_add
	* 
	*/
	public void doMenu_section_add ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute(STATE_TEMPLATE_INDEX, "18"); // chef_site-addSection.vm
		ParameterParser params = data.getParameters ();
		int index = 17; // chef_site-sectionAccess.vm
		actionForTemplate("continue", index, params, state);
		
	} // doMenu_section_add
	
	/**
	* doMenu_section_revise
	* 
	*/
	public void doMenu_section_revise ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute(STATE_TEMPLATE_INDEX, "19"); // chef_site-reviseSection.vm
		ParameterParser params = data.getParameters ();
		int index = 17; // chef_site-sectionAccess.vm
		actionForTemplate("continue", index, params, state);
		
	} // doMenu_section_revise
	
	/**
	*  doMenu_section_delete
	* 
	*/
	public void doMenu_section_delete ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute(STATE_TEMPLATE_INDEX, "20");
		ParameterParser params = data.getParameters ();
		int index = 17; // -sectionAccess
		actionForTemplate("continue", index, params, state);
		
	} // doMenu_section_delete
	
	
	/**
	*  doMenu_edit_site_info
	* 
	*/
	public void doMenu_edit_site_info ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		// get site edit
		siteEditIntoState(state);
		
		SiteEdit siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		ResourceProperties siteProperties = siteEdit.getProperties(); 
		state.setAttribute(FORM_SITEINFO_TITLE, siteEdit.getTitle());
		
		String site_type = (String)state.getAttribute(STATE_SITE_TYPE); 
		if(site_type != null && !site_type.equalsIgnoreCase("myworkspace")) 
		{
			String include = (String) siteProperties.getProperty(PROP_SITE_INCLUDE);
			if (include != null && include.equalsIgnoreCase(Boolean.TRUE.toString()))
			{
				state.setAttribute(FORM_SITEINFO_INCLUDE, Boolean.TRUE.toString());
			}
			else
			{
				state.setAttribute(FORM_SITEINFO_INCLUDE, Boolean.FALSE.toString());
			}
		}
		state.setAttribute(FORM_SITEINFO_DESCRIPTION, siteEdit.getDescription());
		state.setAttribute(FORM_SITEINFO_SHORT_DESCRIPTION, siteProperties.getProperty(PROP_SITE_SHORT_DESCRIPTION));
		state.setAttribute(FORM_SITEINFO_SKIN, siteEdit.getSkin());
		if (siteProperties.getProperty(PROP_SITE_SKIN) != null)
		{
			state.setAttribute(FORM_SITEINFO_SKIN, siteProperties.getProperty(PROP_SITE_SKIN));
		}
		
		// site contact information
		String contactName = siteProperties.getProperty(PROP_SITE_CONTACT_NAME);
		String contactEmail = siteProperties.getProperty(PROP_SITE_CONTACT_EMAIL);
		if (contactName == null && contactEmail == null)
		{
			String creatorId = siteProperties.getProperty(ResourceProperties.PROP_CREATOR);
			try
			{
				User u = UserDirectoryService.getUser(creatorId);
				String email = u.getEmail();
				if (email != null)
				{
					contactEmail = u.getEmail();	
				}
				contactName = u.getDisplayName();
			}
			catch (IdUnusedException e)
			{
			}
		}
		if (contactName != null)
		{
			state.setAttribute(FORM_SITEINFO_CONTACT_NAME, contactName);
		}
		if (contactEmail != null)
		{
			state.setAttribute(FORM_SITEINFO_CONTACT_EMAIL, contactEmail);
		}
				
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "43");
		}
		
	} // doMenu_edit_site_info
	
	/**
	*  doMenu_edit_site_tools
	* 
	*/
	public void doMenu_edit_site_tools ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		// get site edit
		siteEditIntoState(state);
		
		// get the tools
		siteToolsIntoState(state);
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "21");
		}
		
	} // doMenu_edit_site_tools
	
	/**
	*  doMenu_edit_site_access
	* 
	*/
	public void doMenu_edit_site_access ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		
	} // doMenu_edit_site_access

	/**
	*  doMenu_publish_site
	* 
	*/
	public void doMenu_publish_site ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		// get the site edit
		siteEditIntoState(state);
		
		// get the site properties
		sitePropertiesIntoState(state);
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "34");
		}
	
	} // doMenu_publish_site
	
	/**
	*  Back to worksite setup's list view
	* 
	*/
	public void doMenu_back_to_site_list ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.removeAttribute(STATE_SITE_TYPE);
		state.removeAttribute(STATE_SITE_INSTANCE);
					 
		state.setAttribute(STATE_TEMPLATE_INDEX, "0");
	
	} // doMenu_back_to_site_list
	
	/**
	 * Get an SiteEdit object of current site into state
	 * @param state The session state object
	 * @param data The data object
	 */
	private void siteEditIntoState(SessionState state)
	{
		String siteId = ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		try
		{
			SiteEdit siteEdit = SiteService.editSite(siteId);
			state.setAttribute(STATE_SITE_INSTANCE, siteEdit);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Cannot find the specified site " + siteId + ". ");
			Log.warn("chef", this + e.toString());
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to edit the site " + siteId + ".");
			Log.warn("chef", this + e.toString());
		}
		catch (InUseException e)
		{
			addAlert(state, "This site " + siteId + " is not available for editing right now. "); 
			addAlert(state, "You are already editing this site " + siteId + " in another tool, or someone else is editing this site.");	
			Log.warn("chef", this + e.toString());
		}
		
	}	// SiteEditIntoState
	
	/**
	*  doSave_site_info
	* 
	*/
	public void doSave_siteInfo ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		SiteEdit siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		ResourcePropertiesEdit siteProperties = siteEdit.getPropertiesEdit();
		String site_type = (String)state.getAttribute(STATE_SITE_TYPE); 
			
		if (SecurityService.isSuperUser())
		{
			siteEdit.setTitle((String) state.getAttribute(FORM_SITEINFO_TITLE));
		}	

		String description = (String) state.getAttribute(FORM_SITEINFO_DESCRIPTION);
		if (description != null)
		{
			siteEdit.setDescription(description);
		}

		String short_description = (String) state.getAttribute(FORM_SITEINFO_SHORT_DESCRIPTION);
		if (short_description != null)
		{
			siteProperties.addProperty(PROP_SITE_SHORT_DESCRIPTION, short_description);
		}

		if(site_type != null) 
		{
			// project site
			siteProperties.addProperty(PROP_SITE_INCLUDE, (String) state.getAttribute(FORM_SITEINFO_INCLUDE));
		}
		
		// site contact information
		String contactName = (String) state.getAttribute(FORM_SITEINFO_CONTACT_NAME);
		if (contactName != null)
		{
			siteProperties.addProperty(PROP_SITE_CONTACT_NAME, contactName);
		}
		
		String contactEmail = (String) state.getAttribute(FORM_SITEINFO_CONTACT_EMAIL);
		if (contactEmail != null)
		{
			siteProperties.addProperty(PROP_SITE_CONTACT_EMAIL, contactEmail);
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			String id = siteEdit.getId();
			SiteService.commitEdit(siteEdit);
			try
			{
				Site updated_site = SiteService.getSite(id);
				state.setAttribute(STATE_SITE_INSTANCE, updated_site);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
			}
			
			// clean state attributes
			state.removeAttribute(FORM_SITEINFO_TITLE);
			state.removeAttribute(FORM_SITEINFO_DESCRIPTION);
			state.removeAttribute(FORM_SITEINFO_SHORT_DESCRIPTION);
			state.removeAttribute(FORM_SITEINFO_SKIN);
			state.removeAttribute(FORM_SITEINFO_INCLUDE);
			state.removeAttribute(FORM_SITEINFO_CONTACT_NAME);
			state.removeAttribute(FORM_SITEINFO_CONTACT_EMAIL);
			
			// back to site info view
			state.setAttribute(STATE_TEMPLATE_INDEX, "42");
			
			//get the main CHEF window to refresh, to get an update to tabs (and not the float window)
			CourierService.deliver(new RefreshTopDelivery(PortalService.getCurrentClientWindowId(null)));	
				
		}
	} 	// doSave_siteInfo
	
	/**
	* Ordered list of types of tools
	*
	*/
	private List getMyWorkspaceSitePages(SessionState state)
	{
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		List sitePages = site.getPages();
		return sitePages;
		
	}//getMyWorkSpaceSitePage
	
	/**
	* Ordered list of types of tools
	*
	*/
	private List getMyWorkspaceToolConfigurations(SessionState state)
	{
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		List sitePages = site.getPages();
		List tcList = new Vector();
		for (ListIterator i = sitePages.listIterator(); i.hasNext();)
		{
			SitePage page = (SitePage) i.next();
			List toolConfigs = page.getTools();
			for (ListIterator j = toolConfigs.listIterator(); j.hasNext();)
			{
				ToolConfiguration tc = (ToolConfiguration) j.next();
				tcList.add(tc);
			}
		}
		return tcList;
		
	}//getMyWorkspaceToolConfigurations
	
	/**
	* Ordered list of types of tools
	*
	*/
	private List getMyWorkspaceToolOrder()
	{
		List rv = new Vector();
		rv.add("home");
		rv.add("chef.schedule");
		rv.add("chef.resources");
		rv.add("chef.announcements");
		rv.add("chef.iframe");
		rv.add("chef.news");
		rv.add("chef.membership");
		rv.add("chef.sitesetupgeneric");
		rv.add("chef.noti.prefs");
		rv.add("chef.siteinfogeneric");
		rv.add("chef.contactSupport");
		return rv;
	
	} // getMyWorkspaceToolOrder
	
	/**
	* Lookup table for pages and their relative order in the menu
	*
	*/
	private List getMyWorkspacePageOrder()
	{
		List rv = new Vector();
		rv.add("Home");
		rv.add("Worksite Setup");
		rv.add("Membership");
		rv.add("Schedule");
		rv.add("Announcements");
		rv.add("Resources");
		rv.add(NEWS_DEFAULT_TITLE);
		rv.add(WEB_CONTENT_DEFAULT_TITLE);
		rv.add("Preferences");
		rv.add("Help");
		return rv;
	
	} // setupToolOrder
	
	/**
	* Include these tools in this order on features pages
	*
	*/
	private void setupToolOrder(SessionState state)
	{
		List toolOrder = new Vector();
		
		toolOrder.add("chef.membership");
		toolOrder.add("chef.schedule");
		toolOrder.add("chef.announcements");
		toolOrder.add("chef.resources");
		toolOrder.add("chef.discussion");
		toolOrder.add("chef.assignment");
		toolOrder.add("chef.dropbox");
		toolOrder.add("chef.chat");
		toolOrder.add("chef.mailbox");
		toolOrder.add("chef.news");
		toolOrder.add("chef.iframe");
		toolOrder.add("chef.sitesetupgeneric");
		toolOrder.add("chef.noti.prefs");
		toolOrder.add("chef.singleuser");
		toolOrder.add("chef.siteinfogeneric");
		toolOrder.add("chef.contactSupport");
		
		state.setAttribute("toolOrder", toolOrder);
	
	} // setupToolOrder
	
	/**
	*  init
	* 
	*/
	private void init (VelocityPortlet portlet, RunData data, SessionState state)
	{
		ParameterParser params = data.getParameters ();
		state.setAttribute(STATE_ACTION, "SiteAction");
		setupFormNamesAndConstants(state);
		setupToolOrder(state);
		setupToolLists(state);
		if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITESETUP))
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "0");
		}
		else if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITEINFO))
		{
			getReviseSite(state, PortalService.getCurrentSiteId());
		}
		
	} // init
	
	/**
	 * Get site information for revise screen
	 */
	private void getReviseSite(SessionState state, String siteId)
	{
		//	one site has been selected
		 state.setAttribute(STATE_TEMPLATE_INDEX, "42");
		 if (state.getAttribute(STATE_SELECTED_USER_LIST) == null)
		 {
			 state.setAttribute(STATE_SELECTED_USER_LIST, new Vector());
		 }
		 try
		 {
			 Site site = SiteService.getSite(siteId);
			 state.setAttribute(STATE_SITE_INSTANCE, site);

			 String type = site.getType();
			 state.setAttribute(STATE_SITE_TYPE, type);
			
		 }
		 catch (IdUnusedException e)
		 {
			 Log.warn("chef", this + e.toString());
		 }
			 
	}	// getReviseSite
	
	/**
	*  doUpdate_participant_roles
	* 
	*/
	public void doUpdate_participant_roles(RunData data)
	{ 
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
				
		List selected = (List) state.getAttribute(STATE_SELECTED_USER_LIST);

		String realmId = "/site/" + ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		if (RealmService.allowUpdateRealm(realmId))
		{
			try
			{
				RealmEdit realmEdit = RealmService.editRealm(realmId);
				List users = (List) state.getAttribute(STATE_SELECTED_USER_LIST);
				List roles = getRoles(state);
				// remove all roles and then add back those that were checked
				for (int i=0; i<users.size(); i++)
				{
					String id = (String) users.get(i);
					try
					{
						User user = UserDirectoryService.getUser(id);
				
						// for each user, get the roles that have been checked and add them
						for (ListIterator j = roles.listIterator(); j.hasNext();)
						{
							Role role = (Role) j.next();
							try
							{
								realmEdit.removeUserRole(user, role);
							}
							catch (Exception ignore)
							{
								addAlert(state, "Problem encountered removing user role.");
								Log.warn("chef", "SiteAction.updateParticipantRoles Exception ignore " + realmId);
							}
						}
						
						boolean sameRole = ((Boolean) state.getAttribute(STATE_CHANGEROLE_SAMEROLE)).booleanValue();
						Hashtable selectedRoles = (Hashtable) state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES);
						for (int j = 0; j < selected.size(); j++)
						{
							String roleId = null;
							// get user's role
							if (sameRole)
							{
								roleId = (String) state.getAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE);
							}
							else
							{
								roleId =  (String) selectedRoles.get(id);
							}
							Role r = realmEdit.getRole(roleId);
							realmEdit.addUserRole(user, r);
						}
					}
					catch (IdUnusedException e)
					{
						addAlert(state, "Cannot find user with id " + id);
					}
					
				}
				
				RealmService.commitEdit(realmEdit);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Problem encountered updating user roles.");
				Log.warn("chef", "SiteAction.updateParticipantRoles  IdUnusedException " + realmId);
			}
			catch( PermissionException e)
			{
				addAlert(state, "You do not have permission to change roles.");
				Log.warn("chef", "SiteAction.updateParticipantRoles  PermissionException " + realmId);
			}
			catch (InUseException e)
			{
				addAlert(state, "Site is in use and cannot be changed.");
				Log.warn("chef", "SiteAction.updateParticipantRoles  InUnuseException " + realmId);
			}
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// clean state variables
			removeChangeRoleContext(state);
			
			// release site edit
			commitSiteAndRemoveEdit(state);
			
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		
	} // doUpdate_participant_roles
	
	/**
	 * remove related state variable for changing participants roles
	 * @param state SessionState object
	 */
	private void removeChangeRoleContext(SessionState state)
	{
		// remove related state variables
		state.removeAttribute(STATE_CHANGEROLE_SAMEROLE);
		state.removeAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE);
		state.removeAttribute(STATE_ADD_PARTICIPANTS);
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.removeAttribute(STATE_SELECTED_PARTICIPANT_ROLES);
		
	}	// removeChangeRoleContext
	
	
	/**
	* If the state indicates an update is needed, update the portlet's configuration.
	* @param state The session state.
	* @param portlet The portlet to update.
	* @param data The current request run data.
	*/
	private void updatePortlet(SessionState state, VelocityPortlet portlet, RunData data)
	{
		// check the flag
		if (state.getAttribute("update") == null) return;
		state.removeAttribute("update");

	}	// updatePortlet
	
	/**
	/* Actions for vm templates under the "chef_site" root. This method is called by doContinue.
	*  Each template has a hidden field with the value of template-index that becomes the value of
	* index for the switch statement here. Some cases not implemented.
	* @param state the SessionState object
	*/
	private void actionForTemplate ( String direction, int index, ParameterParser params, SessionState state)
	{
		//	Continue - make any permanent changes, Back - keep any data entered on the form
		boolean forward = direction.equals("continue") ? true : false;
		
		String template_index = (String)state.getAttribute(STATE_TEMPLATE_INDEX);
		
		Vector idsSelected = new Vector();
		SiteInfo siteInfo = new SiteInfo();
		
		switch (index)
		{
			case 0: 
				/* actionForTemplate chef_site-list.vm
				*
				*/
				break;
			case 1: 
				/* actionForTemplate chef_site-type.vm
				 *
				 */
				break;
			case 2: 
				/* actionForTemplate chef_site-courseContact.vm
				*  Not implemented
				*/
				break;
			case 3: 
				/* actionForTemplate chef_site-courseClass.vm
				*/
				break;
			case 4: 
				// actionForTemplate chef_site-courseFeatures.vm
				//
				if (forward)
				{
					getFeatures(params, state);
				}
				break;
			case 8: 
				/* actionForTemplate chef_site-projectContact.vm
				*  Not implemented
				*/
				break;
			case 9: 
				/* actionForTemplate chef_site-newSiteInformation.vm
				*
				*/
				if(state.getAttribute(STATE_SITE_INFO) != null)
				{
					siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
				}
				if (params.getString ("include") != null)
				{
					siteInfo.include = true;
				}
				else
				{
					siteInfo.include = false;
				}
				state.setAttribute(STATE_SITE_INFO, siteInfo);
				updateSiteInfo(params, state);
				String form_title = params.getString ("title");
				// alerts after clicking Continue but not Back
				if(forward)
				{
					if ((form_title == null) || (form_title.trim().length() == 0))
					{
						addAlert(state, "Please enter the required fields.");
						state.setAttribute(STATE_TEMPLATE_INDEX, "9");
						return;
					}
					if (!SiteService.allowAddSite(form_title))
					{
						addAlert(state, "You do not have permission to add " + form_title + ".");
						return;
					}
					updateSiteAttributes(state);
				}
				break;
			case 10: 
				/* actionForTemplate chef_site-newSiteFeatures.vm
				*
				*/
				if (forward)
				{
					getFeatures(params, state);
				}
				break;
			case 11: 
				/* actionForTemplate chef_site-courseInformation.vm
				*  Not implemented
				*/
				//if (forward) updateSiteProperties(state);
				break;
			case 12: 
				/* actionForTemplate chef_site-coursePublish.vm
				*  Not implemented
				*/
				//updateSiteProperties(state);
				if(forward) commitSite(state, Site.SITE_STATUS_PUBLISHED);
				break;
			case 13:
				/* actionForTemplate chef_site-otherInvite.vm
				*  Not implemented
				*/
				break;
			case 14: 
				/* actionForTemplate chef_site-otherPublish.vm
				*  Not implemented
				*/
				//updateSiteProperties(state);
				if (forward) commitSite(state, Site.SITE_STATUS_PUBLISHED);
				break;
			case 15: 
				/* actionForTemplate chef_site-customize.vm
				*
				*/
				break;
			case 16: 
				/* actionForTemplate chef_site-basicInformation.vm
				*
				*/
				if(state.getAttribute(STATE_SITE_INFO) != null)
				{
					siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
				}
				if (params.getString ("include") != null)
				{
					siteInfo.include = true;
				}
				else
				{
					siteInfo.include = false;
				}
				state.setAttribute(STATE_SITE_INFO, siteInfo);
				updateSiteInfo(params, state);
				if(forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
					commitSite(state);
				}
				break;
			case 17: 
				/* actionForTemplate chef_site-sectionAccess.vm
				*
				*/
				break;
			case 18: 
				/* actionForTemplate chef_site-addRemoveSection.vm
				*
				*/
				break;
			case 19: 
				/* actionForTemplate chef_site-sectionInformation.vm
				*
				*/
				break;
			case 20:
				/* actionForTemplate chef_site-deleteSection.vm
				*
				*/
				break;
			case 21: 
				/* actionForTemplate chef_site-addRemoveFeature.vm
				*
				*/
				break;
			case 22: 
				/* actionForTemplate chef_site-customLayout.vm
				*
				*/
				customizeLayout(forward, params, state);
				updateSiteInfo(params, state);
				if (forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
				}
				break;
			case 23:
				/* actionForTemplate chef_site-customLink.vm
				*  Not implemented
				*/
				break;
			case 24:
				/* actionForTemplate chef_site-customPage.vm
				*  Not implemented
				*/
				break;
			case 25: 
				/* actionForTemplate chef_site-roster.vm
				* 
				*/ 
				break;
			case 26:
				/* actionForTemplate chef_site-customFeatures.vm 
				*  Not implemented
				*/
				break;
			case 27:
				/* actionForTemplate chef_site-listParticipants.vm
				*
				*/
				getParticipantList(state);
				break;
			case 28:
				/* actionForTemplate chef_site-addParticipant.vm 
				*
				*/
				if(forward) 
				{
					checkAddParticipant(params, state);
				}
				else
				{
					// remove related state variables
					removeAddParticipantContext(state);
					//remove edit object
					removeSiteEditFromState(state);
			
				}
				break;
			case 29:
				/* actionForTemplate chef_site-removeParticipants.vm
				*
				*/
				if (forward)
				{
					removeParticipants(state);
				}
				else
				{
					// remove context variables
					state.removeAttribute(STATE_REMOVEABLE_USER_LIST);
					
					//remove edit object
					removeSiteEditFromState(state);
				}
				break;
			case 30:
				/* actionForTemplate chef_site-changeRoles.vm
				*
				*/
				if (forward)
				{
					if (!((Boolean) state.getAttribute(STATE_CHANGEROLE_SAMEROLE)).booleanValue())
					{
						getSelectedRoles(state, params, STATE_SELECTED_USER_LIST);
					}
					else
					{
						String role = params.getString("role_to_all");
						if (role == null)
						{
							addAlert(state, "Please choose a role. ");
						}
						else
						{
							state.setAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE, role);
						}
					}
				}
				else
				{
					removeChangeRoleContext(state);
					//remove edit object
					removeSiteEditFromState(state);
				}
				break;
			case 31:
				/* actionForTemplate chef_site-siteDeleteConfirm.vm
				*
				*/
				break;
			case 32:
				/* actionForTemplate chef_site-makePublic.vm
				*
				*/
				if(forward)
				{
					if (params.getString ("joinerRole") != null)
					{
						state.setAttribute(STATE_JOINERROLE, params.getString ("joinerRole"));
					}
					if (params.getString ("joinable") != null)
					{
						boolean joinable = params.getBoolean("joinable");
						state.setAttribute(STATE_JOINABLE, new Boolean(joinable));
						if(!joinable) state.removeAttribute(STATE_JOINERROLE);
					}	
					if (params.getString("joinable").equalsIgnoreCase("true") && params.getString("joinerRole") == null)
					{
						state.setAttribute(STATE_TEMPLATE_INDEX, "32");		
						addAlert(state, "Please select a role for those joining your site.");
						return;
					}
				}
				
				if (forward) 
				{
					updateSiteInfo(params, state);
				}
				else
				{
					state.removeAttribute(STATE_JOINABLE);
					state.removeAttribute(STATE_JOINERROLE);
				}
				updateSiteProperties(state);
				updateSiteAttributes(state);

				break;
			case 33:
				/* actionForTemplate chef_site-setType.vm
				*  not implemented
				*/
				break;
			case 34:
				/* actionForTemplate chef_site-publishUnpublish.vm
				*
				*/
				updateSiteInfo(params, state);
				break;
			case 35:
				/* actionForTemplate chef_site-rosterSort.vm
				*  not implemented
				*/
				break;
			case 36:
				/* actionForTemplate chef_site-addMyWorkspaceFeature.vm
				*  not implemented
				*/
				break;
			case 37:
				/* actionForTemplate chef_site-removeMyWorkspaceFeature.vm
				*  not implemented
				*/
				break;
			case 38:
				/* actionForTemplate chef_site-newSiteConfirm.vm
				*
				*/
				break;
			case 39:
				/* actionForTemplate chef_site-courseManual.vm
				*
				*/
				break;
			case 40:
				/* actionForTemplate chef_site-courseConfirm.vm
				*
				*/
				break;
			case 41:
				/* actionForTemplate chef_site_sitePublishUnpublish.vm
				*
				*/
				break;
			case 42:
				/* actionForTemplate chef_site_siteInfo-list.vm
				*
				*/
				break;
			case 43:
				/* actionForTemplate chef_site_siteInfo-editInfo.vm
				*
				*/
				if (forward)
				{
					SiteEdit siteEdit = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
					ResourcePropertiesEdit siteProperties = siteEdit.getPropertiesEdit();
					if (SecurityService.isSuperUser())
					{
						String title = StringUtil.trimToNull(params.getString("title"));
						state.setAttribute(FORM_SITEINFO_TITLE, title);
						if (title == null)
						{
							addAlert(state, "Please specify site title. ");
						}
					}	
	
					String description = StringUtil.trimToNull(params.getString("description"));
					state.setAttribute(FORM_SITEINFO_DESCRIPTION, description);
						
					String short_description = StringUtil.trimToNull(params.getString("short_description"));
					state.setAttribute(FORM_SITEINFO_SHORT_DESCRIPTION, short_description);
						
					String skin = StringUtil.trimToNull(params.getString("skin"));		
					if (skin != null)
					{
						state.setAttribute(FORM_SITEINFO_SKIN, skin);
					}
					
					String include = StringUtil.trimToNull(params.getString("include"));		
					if (include != null && include.equalsIgnoreCase(Boolean.FALSE.toString()))
					{
						state.setAttribute(FORM_SITEINFO_INCLUDE, Boolean.FALSE.toString());
					}
					else
					{
						state.setAttribute(FORM_SITEINFO_INCLUDE, Boolean.TRUE.toString());
					}
					
					// site contact information
					String contactName = StringUtil.trimToZero(params.getString ("siteContactName"));
					state.setAttribute(FORM_SITEINFO_CONTACT_NAME, contactName);
					
					String email = StringUtil.trimToZero(params.getString ("siteContactEmail"));
					String[] parts = email.split("@");
					if(email.length() > 0 && (email.indexOf("@") == -1 || parts.length != 2 || parts[0].length() == 0 || !Validator.checkEmailLocal(parts[0])))
					{
						// invalid email
						addAlert(state, email + " is an invalid email address:" + INVALID_EMAIL);
					}
					state.setAttribute(FORM_SITEINFO_CONTACT_EMAIL, email);
					
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
						state.setAttribute(STATE_TEMPLATE_INDEX, "44");
					}
				}
				break;			
			case 44:
				/* actionForTemplate chef_site_siteInfo-editInfoConfirm.vm
				*
				*/
				break;
			case 45:
				/* actionForTemplate chef_site_siteInfo-addRemoveFeatureConfirm.vm
				*
				*/
				break;
			case 46:
				/* actionForTemplate chef_site_siteInfo-publishUnpublish-sendEmail.vm
				*
				*/
				if (forward)
				{
					String notify = params.getString("notify");
					if (notify != null)
					{
						state.setAttribute(FORM_WILL_NOTIFY, new Boolean(notify));
					}
				}
				break;
			case 47:
				/* actionForTemplate chef_site_siteInfo--publishUnpublish-confirm.vm
				*
				*/
				if (forward) 
				{
					int oldStatus =  ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).getStatus();
					int newStatus = ((SiteInfo) state.getAttribute(STATE_SITE_INFO)).getStatus();
					saveSiteStatus(state, newStatus);
				
					if (oldStatus == Site.SITE_STATUS_UNPUBLISHED || newStatus == Site.SITE_STATUS_PUBLISHED)
					{
						// if site's status been changed from unpublish to publish and notification is selected, send out notification to participants.
						if (((Boolean) state.getAttribute(FORM_WILL_NOTIFY)).booleanValue())
						{
							// %%% place holder for sending email
						}
					}
	
					// commit site edit
					SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
					String id = site.getId();
					SiteService.commitEdit(site);
					
					//get the main CHEF window to refresh, to get an update to tabs (and not the float window)
					CourierService.deliver(new RefreshTopDelivery(PortalService.getCurrentClientWindowId(null)));	
				}
				break;
			case 49:
				/*  actionForTemplate chef_siteInfo-editAccess.vm
				* 
				*/
			case 50:
				/*  actionForTemplate chef_site-addParticipant-sameRole.vm
				* 
				*/
				String roleId = params.getString("selectRole");
				if (( roleId == null || roleId.equals("no_role_selected")) && (forward))
				{
					addAlert(state, "Please select a role. ");
				}
				else
				{
					state.setAttribute("form_selectedRole", params.getString("selectRole"));
				}
				break;
			case 51:
				/*  actionForTemplate chef_site-addParticipant-differentRole.vm
				* 
				*/
				if (forward)
				{
					getSelectedRoles(state, params, STATE_ADD_PARTICIPANTS);
				}
				break;
			case 52:
				/*  actionForTemplate chef_site-addParticipant-notification.vm
				* 
				*/
				if (params.getString("notify") == null)
				{
					if (forward)
					addAlert(state, "Please select the notification choice. ");
				}
				else
				{
					state.setAttribute("form_selectedNotify", new Boolean(params.getString("notify")));
				}
				break;
			case 53:
				/*  actionForTemplate chef_site-addParticipant-confirm.vm
				* 
				*/
				break;
			case 54:
				/*  actionForTemplate chef_siteInfo-editAccess-globalAccess.vm
				* 
				*/
				if (forward)
				{
					String joinable = params.getString("joinable");
					state.setAttribute("form_joinable", Boolean.valueOf(joinable));
					String joinerRole = params.getString("joinerRole");
					state.setAttribute("form_joinerRole", joinerRole);
					if (joinable.equals("true"))
					{
						if (joinerRole == null)
						{
							addAlert(state, "Please select a role. ");
						}
					}
				}
				else
				{
					//remove edit object
					removeSiteEditFromState(state);
				}
				break;
			case 55:
				/*  actionForTemplate chef_site-siteInfo-editAccess-globalAccess-confirm.vm
				* 
				*/
				break;
			case 56:
				/*  actionForTemplate chef_site-changeRoles-confirm.vm
				* 
				*/
				break;
			case 57:
				/*  actionForTemplate chef_site-removeClass.vm
				* 
				*/
				break;
			case 58:
				/*  actionForTemplate chef_site-addRemoveClassConfirm.vm
				* 
				*/
				break;
		}
		
	}// actionFor Template
	
	/**
	 * Sets selected roles for multiple users
	 * @param state The SessionState object
	 * @param params The ParameterParser object
	 * @param listName The state variable
	 */
	private void getSelectedRoles(SessionState state, ParameterParser params, String listName)
	{
		Hashtable pSelectedRoles = (Hashtable) state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES);
		if (pSelectedRoles == null)
		{
			pSelectedRoles = new Hashtable();
		}
		List userList = (List) state.getAttribute(listName);
		for (int i = 0; i < userList.size(); i++)
		{
			String userId = null;
			
			if(listName.equalsIgnoreCase(STATE_ADD_PARTICIPANTS))
			{
				userId = ((Participant) userList.get(i)).getUniqname();
			}
			else if (listName.equalsIgnoreCase(STATE_SELECTED_USER_LIST))
			{
				userId = (String) userList.get(i);
			}
			
			if (userId != null)
			{
				String rId = params.getString("role" + userId);
				if (rId.equalsIgnoreCase("no_role_selected"))
				{
					addAlert(state, "Please select a role for " + userId + ". ");
					pSelectedRoles.remove(userId);
				}
				else
				{
					pSelectedRoles.put(userId, rId);
				}
			}
		}
		state.setAttribute(STATE_SELECTED_PARTICIPANT_ROLES, pSelectedRoles);
		
	}	// getSelectedRoles

	/**
	 * dispatch function for changing participants roles
	 * @param data RunData object
	 */
	public void doSiteinfo_edit_role(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();

		String option = params.getString("option");
		// dispatch
		if (option.equalsIgnoreCase("same_role_true"))
		{
			state.setAttribute(STATE_CHANGEROLE_SAMEROLE, Boolean.TRUE);
			state.setAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE, params.getString("role_to_all"));
		}
		else if (option.equalsIgnoreCase("same_role_false"))
		{
			state.setAttribute(STATE_CHANGEROLE_SAMEROLE, Boolean.FALSE);
			state.removeAttribute(STATE_CHANGEROLE_SAMEROLE_ROLE);
			if (state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES) == null)
			{
				state.setAttribute(STATE_SELECTED_PARTICIPANT_ROLES, new Hashtable());
			}
		}
		else if (option.equalsIgnoreCase("continue"))
		{
			doContinue(data);
		}
		else if (option.equalsIgnoreCase("back"))
		{
			doBack(data);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			doCancel(data);
		}
	}	// doSiteinfo_edit_globalAccess
	
	
	/**
	 * dispatch function for changing site global access
	 * @param data RunData object
	 */
	public void doSiteinfo_edit_globalAccess(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();

		String option = params.getString("option");

		// dispatch
		if (option.equalsIgnoreCase("joinable"))
		{
			state.setAttribute("form_joinable", Boolean.TRUE);
			Site s = (Site) state.getAttribute(STATE_SITE_INSTANCE);
			state.setAttribute("form_joinerRole", s.getJoinerRole());
		}
		else if (option.equalsIgnoreCase("unjoinable"))
		{
			state.setAttribute("form_joinable", Boolean.FALSE);
			state.removeAttribute("form_joinerRole"); 
		}
		else if (option.equalsIgnoreCase("continue"))
		{
			doContinue(data);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			doCancel(data);
		}
	}	// doSiteinfo_edit_globalAccess
	
	/**
	 * save changes to site global access
	 * @param data RunData object
	 */
	public void doSiteinfo_save_globalAccess(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		SiteEdit s = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		boolean joinable = ((Boolean) state.getAttribute("form_joinable")).booleanValue();
		s.setJoinable(joinable);
		if (joinable)
		{
			// set the joiner role
			String joinerRole = (String) state.getAttribute("form_joinerRole");
			s.setJoinerRole(joinerRole);
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			//release site edit
			commitSiteAndRemoveEdit(state);
			
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		
	}	// doSiteinfo_save_globalAccess
	
	/**
	* updateSiteAttributes
	*
	*/
	private void updateSiteAttributes (SessionState state)
	{
		SiteInfo siteInfo = new SiteInfo();
		if (state.getAttribute(STATE_SITE_INFO) != null)
		{
			siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
		}
		else
		{
			Log.warn("chef", "SiteAction.updateSiteAttributes STATE_SITE_INFO == null");
			return;
		}
		if (state.getAttribute(STATE_SITE_INSTANCE) != null)
		{

			SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
			if (StringUtil.trimToNull(siteInfo.title) != null)
			{
				site.setTitle(siteInfo.title);
			}
			if (siteInfo.description != null) 
			{
				site.setDescription(siteInfo.description);
			}
			if (siteInfo.status != -1)
			{
				site.setStatus(siteInfo.status);
			}
			if (StringUtil.trimToNull(siteInfo.skin) != null)
			{
				if(site.getStatus() == Site.SITE_STATUS_PUBLISHED)
				{
					site.setSkin(siteInfo.skin);
				}
			}
			if (StringUtil.trimToNull(siteInfo.iconUrl) != null)
			{
				site.setIconUrl(siteInfo.iconUrl);
			}
			site.setJoinable(siteInfo.joinable);
			if (StringUtil.trimToNull(siteInfo.joinerRole) != null)
			{
				site.setJoinerRole(siteInfo.joinerRole);
			}
			// Make changes and then put changed site back in state
			String id = site.getId();
			SiteService.commitEdit(site);
			try
			{
				SiteEdit updated_site = SiteService.editSite(id);
				state.setAttribute(STATE_SITE_INSTANCE, updated_site);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
			}
			catch (PermissionException e)
			{
				addAlert(state, "You do not have permission to make changes to this site.");
				Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
			}
			catch (InUseException e)
			{
				addAlert(state, "Cannot save changes to site because site is being used by another person.");
				Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
			}
		}
		 
	} // updateSiteAttributes
	
	/**
	* %%% legacy properties, to be removed
	*/
	private void updateSiteInfo (ParameterParser params, SessionState state)
	{
		SiteInfo siteInfo = new SiteInfo();
		if(state.getAttribute(STATE_SITE_INFO) != null)
		{
			siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
		}
		siteInfo.site_type = (String) state.getAttribute(STATE_SITE_TYPE);
		
		if (params.getString ("title") != null)
		{
			siteInfo.title = params.getString ("title");
		}
		if (params.getString ("description") != null)
		{
			siteInfo.description = params.getString ("description");
		}
		if (params.getString ("short_description") != null)
		{
			siteInfo.short_description = params.getString ("short_description");
		}
		if (params.getString ("additional") != null)
		{
			siteInfo.additional = params.getString ("additional");
		}
		if (params.getString ("subject") != null)
		{
			siteInfo.subject = params.getString ("subject");
		}		
		if (params.getString ("section") != null)
		{
			siteInfo.sections = params.getString ("section");
		}
		if (params.getString ("iconUrl") != null)
		{
			siteInfo.iconUrl = params.getString ("iconUrl");
		}
		if (params.getString ("skin") != null)
		{
			siteInfo.skin = params.getString ("skin");
		}
		if (params.getString ("joinerRole") != null)
		{
			siteInfo.joinerRole = params.getString ("joinerRole");
		}
		if (params.getString ("joinable") != null)
		{
			boolean joinable = params.getBoolean("joinable");
			siteInfo.joinable = joinable;
			if(!joinable) siteInfo.joinerRole = NULL_STRING;
		}
		if (params.getString ("itemStatus") != null)
		{
			siteInfo.status = Integer.parseInt(params.getString ("itemStatus"));
		}
		
		// site contact information
		String name = StringUtil.trimToZero(params.getString ("siteContactName"));
		siteInfo.site_contact_name = name;
		String email = StringUtil.trimToZero(params.getString ("siteContactEmail"));
		if (email != null)
		{
			String[] parts = email.split("@");
		
			if(email.length() > 0 && (email.indexOf("@") == -1 || parts.length != 2 || parts[0].length() == 0 || !Validator.checkEmailLocal(parts[0])))
			{
				// invalid email
				addAlert(state, email + " is an invalid email address:" + INVALID_EMAIL);
			}
			siteInfo.site_contact_email = email;
		}
		state.setAttribute(STATE_SITE_INFO, siteInfo);

	} // updateSiteInfo
	
	/**
	* getExternalRealmId
	*
	*/
	private String getExternalRealmId (SessionState state)
	{
		Site site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
		String realmId = "/site/" + site.getId();
		String rv = null;
		try
		{
			Realm realm = RealmService.getRealm(realmId);
			rv = realm.getProviderRealmId();
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.getExternalRealmId, site realm not found");
		}
		return rv;
		
	} // getExternalRealmId
	
	/**
	* getParticipantList
	*
	*/
	private List getParticipantList(SessionState state)
	{
		List users = new Vector();
		List participants = new Vector();
		String realmId = "/site/" + ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		try
		{
			Realm realm = RealmService.getRealm(realmId);
			users.addAll(realm.getUsers());
			Collections.sort(users);
			for (int i = 0; i < users.size(); i++)
			{
				User user = (User) users.get(i);
				Participant participant = new Participant();
				participant.name = user.getSortName();
				participant.uniqname = user.getId();
				Set roles = realm.getUserRoles(user);
				participant.roles = roles;
				participants.add(participant);
			}
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.updateParticipantList  IdUnusedException " + realmId);
		}
		return participants;
		
	} // getParticipantList
	
	/**
	* getRoles
	*
	*/
	private List getRoles (SessionState state)
	{
		List roles = new Vector();
		String realmId = "/site/" + ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		try
		{
			Realm realm = RealmService.getRealm(realmId);
			roles.addAll(realm.getRoles());
			Collections.sort(roles);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.getRoles IdUnusedException " + realmId);
		}
		return roles;
		
	} // getRoles
	
	/**
	* getUsers
	*
	*/
	private List getUsers (SessionState state)
	{
		List users = new Vector();
		String realmId = "/site/" + ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		try
		{
			Realm realm = RealmService.getRealm(realmId);
			users.addAll(realm.getUsers());
			Collections.sort(users);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.getUsers IdUnusedException " + realmId);
		}
		return users;
		
	} // getUsers

	private void getRevisedFeatures(ParameterParser params, SessionState state)
	{	
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		
		//get the list of Worksite Setup configured pages
		List wSetupPageList = (List)state.getAttribute(STATE_WORKSITE_SETUP_PAGE_LIST);
		
		WorksiteSetupPage wSetupPage = new WorksiteSetupPage();
		WorksiteSetupPage wSetupHelp = new WorksiteSetupPage();
		WorksiteSetupPage wSetupSiteInfo = new WorksiteSetupPage();
		WorksiteSetupPage wSetupHome = new WorksiteSetupPage();
		List toolConfigurations = new Vector();
		List pageList = new Vector();
		int lastIndex = 0;
		int helpIndex = 0;
		int helpMoves = 0;
		int homeMoves = 0;
		int siteInfoIndex = 0;
		int siteInfoMoves = 0;
		
		//declare some flags used in making decisions about Home, whether to add, remove, or do nothing
		boolean homeInChosenList = false;
		boolean homeInWSetupPageList = false;
		
		//are there WorkSite Setup configured pages (including Help)?
		boolean thereAreSuchPages = wSetupPageList.size() > 0 ? true : false;
		
		List chosenList = (List) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		
		//if features were selected, diff wSetupPageList and chosenList to get page adds and removes
		// boolean values for adding synoptic views
		boolean hasAnnouncement = false;
		boolean hasChat = false;
		boolean hasDiscussion = false;
		boolean hasNews = false;
		boolean hasWebContent = false;
		
		//Special case - Worksite Setup Home comes from a hardcoded checkbox on the vm template rather than toolRegistrationList
		//see if Home was chosen
		for (ListIterator j = chosenList.listIterator(); j.hasNext(); )
		{
			String choice = (String) j.next();
			if(choice.equalsIgnoreCase("home"))
			{ 
				homeInChosenList = true; 
			}
			else if (choice.equals("chef.mailbox"))
			{
				String alias = (String) state.getAttribute(STATE_TOOL_EMAIL_ADDRESS);
				if (alias == null)
				{
					addAlert(state, "Please specify an email address for Email Archive tool. ");
				}
				else if (!Validator.checkEmailLocal(alias))
				{
					addAlert(state, INVALID_EMAIL);
				}
				else
				{
					try
					{
						String channelReference = MailArchiveService.channelReference(site.getId(), SiteService.MAIN_CONTAINER);
						//first, clear any alias set to this channel				
						AliasService.removeTargetAliases(channelReference);	// check to see whether the alias has been used
						try
						{
							String target = AliasService.getTarget(alias);
							if (target != null)
							{
								addAlert(state, "The email alias is already in use. ");
							}
						}
						catch (IdUnusedException ee)
						{
							try
							{
								AliasService.setAlias(alias, channelReference);
							}
							catch (IdUsedException exception) {}
							catch (IdInvalidException exception) {}
							catch (PermissionException exception) {}
						}	
					}
					catch (PermissionException exception) {}
				}
			}
			else if (choice.equals("chef.announcements"))
			{
				hasAnnouncement = true; 
			}
			else if (choice.equals("chef.chat"))
			{
				hasChat = true; 
			}
			else if (choice.equals("chef.discussion"))
			{
				hasDiscussion = true; 
			}
			else if (choice.equals("chef.news"))
			{
				hasNews = true; 
			}
			else if (choice.equals("chef.iframe"))
			{
				hasWebContent = true; 
			}
		}
		
		//see if Home and/or Help in the wSetupPageList (can just check title here, because we checked patterns before adding to the list)
		for (ListIterator i = wSetupPageList.listIterator(); i.hasNext(); )
		{
			wSetupPage = (WorksiteSetupPage) i.next();
			if((wSetupPage.getPageTitle()).equals("Home")){ homeInWSetupPageList = true; }
		}
		
		if (homeInChosenList)
		{
			SitePageEdit page = null;
			if (homeInWSetupPageList)
			{
				//if Home is chosen and Home is in the wSetupPageList, remove synoptic tools
				WorksiteSetupPage homePage = new WorksiteSetupPage();
				for (ListIterator i = wSetupPageList.listIterator(); i.hasNext(); )
				{
					WorksiteSetupPage comparePage = (WorksiteSetupPage) i.next();
					if((comparePage.getPageTitle()).equals("Home")) { homePage = comparePage; }
				}
				page = site.getPageEdit(homePage.getPageId());
				List toolList = page.getToolEdits();
				for (ListIterator j = toolList.listIterator(); j.hasNext(); )
				{
					ToolConfigurationEdit tool = (ToolConfigurationEdit) j.next();
					if (tool.getToolId().equals("chef.synoptic.announcement")
						|| tool.getToolId().equals("chef.synoptic.discussion")
						|| tool.getToolId().equals("chef.synoptic.chat"))
					{
						page.removeTool(tool);	
					}
				}
			}	
			else
			{
				//if Home is chosen and Home is not in wSetupPageList, add Home to site and wSetupPageList
				page = site.addPage();
				
				page.setTitle("Home");
	
				wSetupHome.pageId = page.getId();
				wSetupHome.pageTitle = page.getTitle();
				wSetupHome.toolId = "home";
				wSetupPageList.add(wSetupHome);
			
				//Add worksite information tool
				ToolConfigurationEdit tool = page.addTool();
				tool.setToolId("chef.iframe");
				ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
				tool.getPropertiesEdit().clear();
				tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());

				tool.setTitle("Worksite Information");
				tool.setLayoutHints("0,0");
				tool.getPropertiesEdit().addProperty("special", "worksite");
			}
					  
			try
			{
				if (hasAnnouncement)
				{
					//Add synoptic announcements tool
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.synoptic.announcement");
					ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
					tool.getPropertiesEdit().clear();
					tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());

					tool.setTitle("Recent Announcements");
					tool.setLayoutHints("0,1");
				}
				
				if (hasDiscussion)
				{			
					//Add synoptic discussion tool
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.synoptic.discussion");
					ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
					tool.getPropertiesEdit().clear();
					tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());

					tool.setTitle("Recent Discussion Items");
					tool.setLayoutHints("1,1");
				}
							
				if (hasChat)
				{
					//Add synoptic chat tool
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.synoptic.chat");
					ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
					tool.getPropertiesEdit().clear();
					tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());

					tool.setTitle("Recent Chat Messages");
					tool.setLayoutHints("2,1");
				}
				
				if (hasAnnouncement || hasDiscussion || hasChat)
				{
					page.setLayout(SitePage.LAYOUT_DOUBLE_COL);
				}
				else
				{
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				}
				
			}
			catch (Exception e)
			{
				Log.warn("chef", "SiteAction.getFeatures Exception " + e.getMessage());
			}
		} // add Home
		
		//if Home is in wSetupPageList and not chosen, remove Home feature from wSetupPageList and site
		if (!homeInChosenList && homeInWSetupPageList)
		{
			//remove Home from wSetupPageList
			WorksiteSetupPage removePage = new WorksiteSetupPage();
			for (ListIterator i = wSetupPageList.listIterator(); i.hasNext(); )
			{
				WorksiteSetupPage comparePage = (WorksiteSetupPage) i.next();
				if((comparePage.getPageTitle()).equals("Home")) { removePage = comparePage; }
			}
			SitePageEdit siteHome = site.getPageEdit(removePage.getPageId());
			site.removePage(siteHome);
			wSetupPageList.remove(removePage);
			
		}
		
		//declare flags used in making decisions about whether to add, remove, or do nothing
		boolean inChosenList;
		boolean inWSetupPageList;
		
		Hashtable newsTitles = (Hashtable) state.getAttribute(STATE_NEWS_TITLES);
		Hashtable wcTitles = (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES);
		
		List toolRegistrationList = orderTools(state, ServerConfigurationService.getToolRegistrations());

		// first looking for any tool for removal
		Vector removePageIds = new Vector();
		for (ListIterator k =  wSetupPageList.listIterator(); k.hasNext(); )
		{
			wSetupPage = (WorksiteSetupPage)k.next();
			String pageToolId = wSetupPage.getToolId();
			
			// use page id + tool id for multiple News and Web Content tool
			if (pageToolId.indexOf("chef.news") != -1 || pageToolId.indexOf("chef.iframe") != -1)
			{
				pageToolId = wSetupPage.getPageId() + pageToolId;
			}
			
			inChosenList = false;
	
			for (ListIterator j = chosenList.listIterator(); j.hasNext(); )
			{
				String toolId = (String) j.next();
				if(pageToolId.equals(toolId)) 
				{ 
					inChosenList = true;
				}
			}
				
			if (!inChosenList)
			{
				removePageIds.add(wSetupPage.getPageId());
			}
		}
		for  (int i = 0; i < removePageIds.size(); i++)
		{
			//if the tool exists in the wSetupPageList, remove it from the site
			String removeId = (String) removePageIds.get(i);
			SitePageEdit sitePage = site.getPageEdit(removeId);
			site.removePage(sitePage);
			
			// and remove it from wSetupPageList
			for (ListIterator k =  wSetupPageList.listIterator(); k.hasNext(); )
			{
				wSetupPage = (WorksiteSetupPage)k.next();
				if (!wSetupPage.getPageId().equals(removeId))
				{
					wSetupPage = null;
				}
			}
			if (wSetupPage != null)
			{
				wSetupPageList.remove(wSetupPage);
			}
		} 
		
		// then looking for any tool to add
		for (ListIterator j = chosenList.listIterator(); j.hasNext(); )
		{
			String toolId = (String) j.next();
			
			//Is the tool in the wSetupPageList?
			inWSetupPageList = false;
			for (ListIterator k =  wSetupPageList.listIterator(); k.hasNext(); )
			{
				wSetupPage = (WorksiteSetupPage)k.next();
				String pageToolId = wSetupPage.getToolId();
				
				// use page Id + toolId for multiple News and Web Content tool
				if (pageToolId.indexOf("chef.news") != -1 || pageToolId.indexOf("chef.iframe") != -1)
				{
					pageToolId = wSetupPage.getPageId() + pageToolId;
				}
				
				if(pageToolId.equals(toolId)) 
				{ 
					inWSetupPageList = true;
					// but for News and Web Content tool, need to change the title
					if (toolId.indexOf("chef.news") != -1)
					{
						((SitePageEdit) site.getPageEdit(wSetupPage.pageId)).setTitle((String) newsTitles.get(pageToolId));
					}
					else if (toolId.indexOf("chef.iframe") != -1)
					{
						((SitePageEdit) site.getPageEdit(wSetupPage.pageId)).setTitle((String) wcTitles.get(pageToolId));
					}
				}		
			}
			if (inWSetupPageList)
			{
				// if the tool already in the list, do nothing so to save the option settings 
			}
			else
			{
				// if in chosen list but not in wSetupPageList, add it to the site (one tool on a page)
				ToolRegistration toolRegFound = null;
				for (ListIterator i = toolRegistrationList.listIterator(); i.hasNext(); )
				{
					ToolRegistration toolReg = (ToolRegistration) i.next();
					if (toolId.indexOf(toolReg.getId()) != -1)
					{
						toolRegFound = toolReg;
					}
				}
				
				if (toolRegFound != null)
				{
					// we know such a tool, so add it
					WorksiteSetupPage addPage = new WorksiteSetupPage();
					SitePageEdit page = site.addPage();
					addPage.pageId = page.getId();
					if (toolId.indexOf("chef.news") != -1)
					{
						// set News tool title
						page.setTitle((String) newsTitles.get(toolId));
					}
					else if (toolId.indexOf("chef.iframe") != -1)
					{
						// set Web Content tool title
						page.setTitle((String) wcTitles.get(toolId));
					}
					else
					{
						// other tools with default title
						page.setTitle(toolRegFound.getTitle());
					}
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId(toolRegFound.getId());
					addPage.toolId = toolId;
					wSetupPageList.add(addPage);
					tool.setTitle(toolRegFound.getTitle());
					ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
					rpe.addAll(toolRegFound.getDefaultConfiguration()); // set tool properties to the default tool configuration
					if (toolId.indexOf("chef.iframe") != -1)
					{
						if (SiteService.isUserSite(site.getId()))
						{
							// default to show myworkspace information
							rpe.addProperty("special", "worksite");
						}
						else
						{
							// default to show worksite's description
							rpe.addProperty("special", "worksite");
						}
					}
				}
			}
		}	// for
		
		// any multiple help tools?
		int helpTools = 0;
		WorksiteSetupPage rmPage = new WorksiteSetupPage();
		for (ListIterator i = wSetupPageList.listIterator(); i.hasNext(); )
		{
			wSetupPage = (WorksiteSetupPage) i.next();
			if (wSetupPage.getToolId().equals("chef.contactSupport")){helpTools = helpTools + 1; }
			rmPage = wSetupPage;
		}
		while (helpTools > 1)
		{	
			helpTools = 0;
			
			// remove redundant help tool one at a time
			SitePageEdit sitePage = site.getPageEdit(rmPage.getPageId());
			site.removePage(sitePage);
			wSetupPageList.remove(rmPage);
			
			// check again
			for (ListIterator i = wSetupPageList.listIterator(); i.hasNext(); )
			{
				wSetupPage = (WorksiteSetupPage) i.next();
				if (wSetupPage.getToolId().equals("chef.contactSupport")){helpTools = helpTools + 1; }
				rmPage = wSetupPage;
			}
		}

		// Order tools - move toolRegistrationList tools
		int toolRegistrationNumber = 0;
		int sitePageNumber = 0;
		int movesUp = 0;
		List toolOrder = new Vector();
		toolOrder = orderTools(state, ServerConfigurationService.getToolRegistrations());
		for (ListIterator i = toolOrder.listIterator(0); i.hasNext(); )
		{
			ToolRegistration tool = (ToolRegistration)i.next();
			toolRegistrationNumber = toolOrder.indexOf(tool);
			
			//if toolRegistration is found in wSetupPageList
			for (ListIterator j = wSetupPageList.listIterator(); j.hasNext(); )
			{
				WorksiteSetupPage wspage = (WorksiteSetupPage)j.next();
				if((tool.getId()).equals(wspage.getToolId()))
				{
					//if page is in site, move it
					pageList = site.getPageEdits();
					if(pageList != null && pageList.size() != 0)
					{
						for (ListIterator k = pageList.listIterator(); k.hasNext(); )
						{
							SitePageEdit page = (SitePageEdit)k.next();
							if((page.getId()).equals(wspage.getPageId())) 
							{
								sitePageNumber = pageList.indexOf(page);
								movesUp = sitePageNumber - toolRegistrationNumber;
								for (int n = 0; n < movesUp; n++)
								{
									page.moveUp();
								}
							}
						}
					}
				}
			}
		}
		
		if (homeInChosenList)
		{
			//Order tools - move Home to the top 
			pageList = site.getPageEdits();
			if(pageList != null && pageList.size() != 0)
			{
				lastIndex = pageList.size();
				for (ListIterator i = pageList.listIterator(); i.hasNext(); )
				{
					SitePageEdit page = (SitePageEdit)i.next();
					if((page.getTitle()).equals("Home")) 
					{
						homeMoves = pageList.indexOf(page);
						for (int n = 0; n < homeMoves; n++)
						{
							page.moveUp();
						}
					}
				}
			}
		}
		
		//Order tools - move site info to second last
		pageList = site.getPageEdits();
		if(pageList != null && pageList.size() != 0)
		{
			lastIndex = pageList.size();
			for (ListIterator i = pageList.listIterator(); i.hasNext(); )
			{
				SitePageEdit page = (SitePageEdit)i.next();
				if((page.getTitle()).equals("Site Info")) 
		 		{
					siteInfoMoves = pageList.indexOf(page);
					for (int n = siteInfoMoves; n < lastIndex; n++)
					{
						page.moveDown();
					}
				}
			}
		}
		
		//Order tools - move help to last
		pageList = site.getPageEdits();
		if(pageList != null && pageList.size() != 0)
		{
			lastIndex = pageList.size();
			for (ListIterator i = pageList.listIterator(); i.hasNext(); )
			{
				SitePageEdit page = (SitePageEdit)i.next();
				if((page.getTitle()).equals("Help")) 
				{
					helpMoves = pageList.indexOf(page);
					for (int n = helpMoves; n < lastIndex; n++)
					{
						page.moveDown();
					}
				}
			}
		}
		
	} // getRevisedFeatures
	
	/**
	* getFeatures gets features for a new site
	*
	*/
	private void getFeatures(ParameterParser params, SessionState state)
	{
		List idsSelected = new Vector();
		//if (params.getStrings ("selectedTools") == null) { addAlert(state, NO_FEATURES_SELECTED_STRING); }
			
		boolean homeSelected = false;
		boolean emailSelected = false;
			
		// the news tool titles
		Hashtable titles = (Hashtable) state.getAttribute(STATE_NEWS_TITLES);
		// the web content tool titles
		Hashtable wcTitles = (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES);
			
		// Add new pages and tools, if any
		if (params.getStrings ("selectedTools") != null)
		{
			List l = new ArrayList(Arrays.asList(params.getStrings ("selectedTools"))); // toolId's & titles of chosen tools
				
			for (int i = 0; i < l.size(); i++)
			{
				String toolId = (String) l.get(i);

				//if the tool is chef.mailbox, check if the email address is valid
				if (toolId.equals("chef.mailbox"))
				{
					emailSelected = true;
						
					String emailId = StringUtil.trimToNull(params.getString("emailId"));
					state.setAttribute(STATE_TOOL_EMAIL_ADDRESS, emailId);
					if (emailId == null)
					{
						addAlert(state, "Please specify an email address for Email Archive tool. ");
					}
					else if (!Validator.checkEmailLocal(emailId))
					{
						addAlert(state, INVALID_EMAIL);
					}
		  				
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
		  				
						// check to see whether the alias has been used
						try
						{
							String target = AliasService.getTarget(emailId);
							if (target != null)
							{
								addAlert(state, "The email alias is already in use. ");
							}
						}
						catch (IdUnusedException ee){}	
					}	
				}
				else if (toolId.equalsIgnoreCase("home"))
				{
					homeSelected = true;
				}
				else if (toolId.indexOf("chef.news") != -1)
				{
					String title = StringUtil.trimToNull(params.getString("titlefor" + toolId));
					if (title == null)
					{
						// if there is no input, make the title for news tool default to news
						title = NEWS_DEFAULT_TITLE;
					}
					titles.put(toolId, title);
				}
				else if (toolId.indexOf("chef.iframe") != -1)
				{
					String wcTitle = StringUtil.trimToNull(params.getString("titlefor" + toolId));
					if (wcTitle == null)
					{
						// if there is no input, make the title for news tool default to news
						wcTitle = WEB_CONTENT_DEFAULT_TITLE;
					}
					wcTitles.put(toolId, wcTitle);
				}
				idsSelected.add(toolId);
			}
			if (!emailSelected)
			{
				state.removeAttribute(STATE_TOOL_EMAIL_ADDRESS);
			}
				
			state.setAttribute(STATE_TOOL_HOME_SELECTED, new Boolean(homeSelected));
		}
		for (int i = 0; i < idsSelected.size(); i++)
		{
			String toolId = (String) idsSelected.get(i);
		}
		
		state.setAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST, idsSelected); // List of ToolRegistration toolId's
		state.setAttribute(STATE_NEWS_TITLES, titles);
	
	}	// getFeatures
	
	/**
	* addFeatures adds features to a new site
	*
	*/
	private void addFeatures(SessionState state)
	{
		List toolRegistrationList = (Vector)state.getAttribute(STATE_TOOL_REGISTRATION_LIST);
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);

		List pageList = new Vector();
		int moves = 0;
		int lastIndex = 0;
		
		List chosenList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		
		// titles for news tools
		Hashtable newsTitles = (Hashtable) state.getAttribute(STATE_NEWS_TITLES);
		// titles for web content tools
		Hashtable wcTitles = (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES);
		
		if (chosenList.size() > 0)
		{
			boolean hasHome = false;
			boolean hasAnnouncement = false;
			boolean hasChat = false;
			boolean hasDiscussion = false;
			
			ToolRegistration toolRegFound = null;
			for (ListIterator i = chosenList.listIterator(); i.hasNext(); )
			{
				String toolId = (String) i.next();
				
				// find the tool in the tool registration list
				toolRegFound = null;
				for (ListIterator j = toolRegistrationList.listIterator(); j.hasNext() && toolRegFound == null; )
				{
					ToolRegistration toolReg = (ToolRegistration) j.next();
					if (toolId.indexOf(toolReg.getId()) != -1)
					{
						toolRegFound = toolReg;
					}
				}
				
				// for tools other than home
				if (toolId.equalsIgnoreCase("home"))
				{
					// add home tool later 
					hasHome = true;
				}
				else if (toolId.indexOf("chef.news") != -1)
				{
					// adding multiple news tool
					String newsTitle = (String) newsTitles.get(toolId);
					SitePageEdit page = site.addPage();
					page.setTitle(newsTitle); // the visible label on the tool menu
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.news");
					tool.setTitle(newsTitle);
					tool.setLayoutHints("0,0");
					
					//set tool properties to the default tool configuration
				  	ResourceProperties rp = toolRegFound.getDefaultConfiguration();
				  	ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
				  	rpe.addAll(rp);
				}
				else if (toolId.indexOf("chef.iframe") != -1)
				{
					// adding multiple web content tool
					String wcTitle = (String) wcTitles.get(toolId);
					SitePageEdit page = site.addPage();
					page.setTitle(wcTitle); // the visible label on the tool menu
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.iframe");
					tool.setTitle(wcTitle);
					tool.setLayoutHints("0,0");
					
					//set tool properties to the default tool configuration
					ResourceProperties rp = toolRegFound.getDefaultConfiguration();
					ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
					rpe.addAll(rp);
					// default to display the worksite description
					rpe.addProperty("special", "worksite");
				}
				else if (!toolId.equalsIgnoreCase("chef.siteinfogeneric") && !toolId.equalsIgnoreCase("chef.contactSupport"))
				{
					SitePageEdit page = site.addPage();
					page.setTitle(toolRegFound.getTitle()); // the visible label on the tool menu
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId(toolRegFound.getId());
					tool.setTitle(toolRegFound.getTitle());
					tool.setLayoutHints("0,0");
					
					//set tool properties to the default tool configuration
				  	ResourceProperties rp = toolRegFound.getDefaultConfiguration();
					ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
					rpe.addAll(rp);
					
				} // Other features
				
				// booleans for synoptic views
				if (toolId.equals("chef.announcements"))
				{
					hasAnnouncement = true; 
				}
				else if (toolId.equals("chef.chat"))
				{
					hasChat = true; 
				}
				else if (toolId.equals("chef.discussion"))
				{
					hasDiscussion = true; 
				}
			}	// for
			
			// add home tool
			if (hasHome)
			{
				// Home is a special case, with several tools on the page. "home" is hard coded in chef_site-addRemoveFeatures.vm.
				try
				{
					SitePageEdit page = site.addPage();
					page.setTitle("Home"); // the visible label on the tool menu
					if (hasAnnouncement || hasDiscussion || hasChat)
					{
						page.setLayout(SitePage.LAYOUT_DOUBLE_COL);
					}
					else
					{
						page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					}
					
					//Add worksite information tool
					ToolConfigurationEdit tool = page.addTool();
					tool.setToolId("chef.iframe");
					ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
					tool.getPropertiesEdit().clear();
					tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());
					ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
					//default to display the worksite description
					rpe.addProperty("special", "worksite");

					tool.setTitle("Worksite Information");
					tool.setLayoutHints("0,0");

					if (hasAnnouncement)
					{
						//Add synoptic announcements tool
						tool = page.addTool();
						tool.setToolId("chef.synoptic.announcement");
						reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
						tool.getPropertiesEdit().clear();
						tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());
						
						tool.setTitle("Recent Announcements");
						tool.setLayoutHints("0,1");
					}
								
					if (hasDiscussion)
					{ 
						//Add synoptic announcements tool
						tool = page.addTool();
						tool.setToolId("chef.synoptic.discussion");
						reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
						tool.getPropertiesEdit().clear();
						tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());
	
						tool.setTitle("Recent Discussion Items");
						tool.setLayoutHints("1,1");
					}
						
					if (hasChat)
					{		
						//Add synoptic chat tool
						tool = page.addTool();
						tool.setToolId("chef.synoptic.chat");
						reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
						tool.getPropertiesEdit().clear();
						tool.getPropertiesEdit().addAll(reg.getDefaultConfiguration());
	
						tool.setTitle("Recent Chat Messages");
						tool.setLayoutHints("2,1");
					}
				}
				catch (Exception e)
				{
					Log.warn("chef", "SiteAction.getFeatures Exception " + e.getMessage());
				}
				
				state.setAttribute(STATE_TOOL_HOME_SELECTED, Boolean.TRUE);
				
				//Order tools - move Home to the top 
				pageList = site.getPageEdits();
				if(pageList != null && pageList.size() != 0)
				{
					for (ListIterator i = pageList.listIterator(); i.hasNext(); )
					{
						SitePageEdit page = (SitePageEdit)i.next();
						if((page.getTitle()).equals("Home")) 
						{
							moves = pageList.indexOf(page);
							for (int n = 0; n < moves; n++)
							{
								page.moveUp();
							}
						}
					}
				}
			} // Home feature 
		}
		
		//Add Site Info as the last entry
		SitePageEdit lastPage2 = site.addPage();
		lastPage2.setTitle("Site Info"); // the visible label on the tool menu
		ToolConfigurationEdit siteInfo = lastPage2.addTool();
		siteInfo.setToolId("chef.siteinfogeneric");
		siteInfo.setTitle("Site Info");

		
		// Add Help as the last entry
		SitePageEdit lastPage = site.addPage();
		lastPage.setTitle("Help"); // the visible label on the tool menu
		ToolConfigurationEdit help = lastPage.addTool();
		help.setToolId("chef.contactSupport");
		help.setTitle("Contact Support Form");
		
		//Order tools - move site info to second last
		pageList = site.getPageEdits();
		if(pageList != null && pageList.size() != 0)
		{
			lastIndex = pageList.size();
			for (ListIterator i = pageList.listIterator(); i.hasNext(); )
			{
				SitePageEdit page = (SitePageEdit)i.next();
				if((page.getTitle()).equals("Site Info")) 
				{
					moves = pageList.indexOf(page);
					for (int n = moves; n < lastIndex; n++)
					{
						page.moveDown();
					}
				}
			}
		}
		
		//Order tools - move help to last
		pageList = site.getPageEdits();
		if(pageList != null && pageList.size() != 0)
		{
			lastIndex = pageList.size();
			for (ListIterator i = pageList.listIterator(); i.hasNext(); )
			{
				SitePageEdit page = (SitePageEdit)i.next();
				if((page.getTitle()).equals("Help")) 
				{
					moves = pageList.indexOf(page);
					for (int n = moves; n < lastIndex; n++)
					{
						page.moveDown();
					}
				}
			}
		}

	} // addFeatures
	
	public void finishSiteEdit(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = site.getType();
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			site.setSkin(PROJECT_UNPUBLISHED_SKIN);
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			site.setSkin(PROJECT_PUBLISHED_SKIN);
		}
		
		SiteService.commitEdit(site);
		
	} // finishSiteEdit
	
	public void saveSiteStatus(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = site.getType();
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			site.setSkin(PROJECT_UNPUBLISHED_SKIN);
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			site.setSkin(PROJECT_PUBLISHED_SKIN);
		}
		
	} // saveSiteStatus

	public void commitSite(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		site.setStatus(status);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = (String)state.getAttribute(STATE_SITE_TYPE);
		
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			site.setSkin(PROJECT_UNPUBLISHED_SKIN);
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			site.setSkin(PROJECT_PUBLISHED_SKIN);
		}
		SiteService.commitEdit(site);
		try
		{
			SiteEdit updated_site = SiteService.editSite(id);
			state.setAttribute(STATE_SITE_INSTANCE, updated_site);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to make changes to this site.");
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
		catch (InUseException e)
		{
			addAlert(state, "Cannot save changes to site because site is being used by another person.");
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}

	} // commitSite
	
	public void commitSite(SessionState state)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		SiteService.commitEdit(site);
		try
		{
			SiteEdit updated_site = SiteService.editSite(id);
			state.setAttribute(STATE_SITE_INSTANCE, updated_site);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
			catch (PermissionException e)
		{
			addAlert(state, "You do not have permission to make changes to this site.");
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
		catch (InUseException e)
		{
			addAlert(state, "Cannot save changes to site because site is being used by another person.");
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
		
	}// commitSite
	
	public void commitSiteAndRemoveEdit(SessionState state)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		SiteService.commitEdit(site);
		try
		{
			Site updated_site = SiteService.getSite(id);
			state.setAttribute(STATE_SITE_INSTANCE, updated_site);
		}
		catch (IdUnusedException e)
		{
			Log.warn("chef", "SiteAction.commitSite IdUnusedException " + id + " not found");
		}
	
	}// commitSiteAndRemoveEdit
	
	private void customizeLayout(boolean forward, ParameterParser params, SessionState state)
	{
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String skin = null;
		String iconUrl = null;
		if(params.getString("skin") != null)
		{
			skin = params.getString("skin");
			siteInfo.skin = skin;
		}
		if(params.getString("iconUrl") != null)
		{
			iconUrl = params.getString("iconUrl");
		}
		if(forward)
		{
			try
			{
			
				if (state.getAttribute(STATE_SITE_INSTANCE) != null)
				{
					SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
					String id = site.getId();
					if(skin != null)
					{
						if(site.getStatus() == Site.SITE_STATUS_PUBLISHED)
						{
							site.setSkin(skin);
						}
					}
					if(iconUrl != null)
					{
						site.setIconUrl(iconUrl);
					}
					SiteService.commitEdit(site);
					try
					{
						SiteEdit updated_site = SiteService.editSite(id);
						state.setAttribute(STATE_SITE_INSTANCE, updated_site);
					}
					catch (IdUnusedException e)
					{
						Log.warn("chef", "SiteAction.commitSite IdUnusedException");
					}
					catch (PermissionException e)
					{
						addAlert(state, "You do not have permission to make changes to this site.");
						Log.warn("chef", "SiteAction.commitSite PermissionException");
					}
					catch (InUseException e)
					{
						addAlert(state, "Cannot save changes to site because site is being used by another person.");
						Log.warn("chef", "SiteAction.commitSite IdUnusedException");
					}
				}
			}
			catch (Exception e)
			{
				Log.warn("chef", "SiteAction.customizeLayout Exception " + e.getMessage());
			}
		}
		
	} // customizeLayout

	private void updateSiteProperties (SessionState state)
	{
		try
		{
			//trimToNull can leave properties null
			SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
			SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);	
			ResourcePropertiesEdit rpe = site.getPropertiesEdit();
			if(siteInfo.site_type != null)  rpe.addProperty(ResourceProperties.PROP_SITE_TYPE, siteInfo.site_type);
			if(siteInfo.skin != null) rpe.addProperty(PROP_SITE_SKIN, siteInfo.skin);
			rpe.addProperty(PROP_SITE_SHORT_DESCRIPTION, siteInfo.short_description);
			if (!siteInfo.site_type.equals("myworkspace"))
			{
				rpe.addProperty(PROP_SITE_INCLUDE, (new Boolean(siteInfo.include)).toString());
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", "SiteAction.updateSiteProperties  Exception " + e.getMessage());
		}
		
	} // updateSiteProperties
	
	private void removeParticipants(SessionState state)
	{
		List removals = (List) state.getAttribute(STATE_REMOVEABLE_USER_LIST); 
		List participantSelectedList = removals;
		
		if(!removals.isEmpty())
		{
			for (ListIterator i = removals.listIterator(); i.hasNext(); )
			{
				try
				{
					String id = (String) i.next();
					User user = UserDirectoryService.getUser(id);
					try
					{
						String realmId = "/site/" + ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
						RealmEdit realmEdit = RealmService.editRealm(realmId);
						Participant selected = new Participant();
						selected.name = user.getDisplayName();
						selected.uniqname = user.getId();
						realmEdit.removeUser(user);
						RealmService.commitEdit(realmEdit);
						// remove the deleted participant from the selected list
						participantSelectedList.remove(selected);
					}
					catch (IdUnusedException e)
					{
						Log.warn("chef", "SiteAction.removeParticipants IdUnusedException");
						state.setAttribute(STATE_TEMPLATE_INDEX, "49");
						return;
					}
					catch( PermissionException e)
					{
						Log.warn("chef", "SiteAction.removeParticipants PermissionException");
						state.setAttribute(STATE_TEMPLATE_INDEX, "49"); 
						return;
					}
					catch (InUseException e)
					{
						Log.warn("chef", "SiteAction.removeParticipants InUseException");
						state.setAttribute(STATE_TEMPLATE_INDEX, "49"); 
						return;
					}
				}
				catch (IdUnusedException e)
				{
					Log.warn("chef", "SiteAction.removeParticipants IdUnusedException ");
					state.setAttribute(STATE_TEMPLATE_INDEX, "49");
					return;
				}
			
			}
			state.setAttribute("participantSelectedList", participantSelectedList);
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
		}
		else
		{
			addAlert(state, "There is no user to remove.");
			state.setAttribute(STATE_TEMPLATE_INDEX, "49"); 
		}
		
	} // removeParticipant
	
	private void checkAddParticipant(ParameterParser params, SessionState state)
	{
		// get the participants to be added
		int i;
		Vector pList = new Vector();
		//Hashtable selectedRoles = new Hashtable();
		
		//accept uniqnames and/or friend account names
		String uniqnames = "";
		String friends = "";
		
		//check that there is something with which to work
		uniqnames = StringUtil.trimToNull((params.getString("uniqnames")).toLowerCase());
		friends = StringUtil.trimToNull(params.getString("friends").toLowerCase());
		state.setAttribute("form_uniqnames", uniqnames);
		state.setAttribute("form_friends", friends);
		//if there is no uniquname or friend entered
		if (uniqnames == null && friends == null)
		{
			addAlert(state, "Please enter uniqname(s) or friend account(s) to add to this site.");
			state.setAttribute(STATE_TEMPLATE_INDEX, "28");
			return;
		}
		
		String at = "@";
		
		if (uniqnames != null)
		{
			// adding uniqnames
			String[] uniqnameArray = uniqnames.split("\r\n");
			
			for (i = 0; i < uniqnameArray.length; i++)
			{
				String uniqname = StringUtil.trimToNull(uniqnameArray[i]);
				//if there is some text, try to use it
				uniqname.replaceAll("[ \t\r\n]","");
				if(uniqname != null)
				{
					//automaticially add friend account
					Participant participant = new Participant();
					try
					{
						User u = UserDirectoryService.getUser(uniqname);
						participant.name = u.getDisplayName();
						participant.uniqname = uniqname;
						pList.add(participant);
					}
					catch (IdUnusedException e) 
					{
						addAlert(state, uniqname + " is not a valid uniqname. ");
					}
				}
			}
		}	// uniqnames
		if (friends != null)
		{
			String[] friendArray = friends.split("\r\n");
			for (i = 0; i < friendArray.length; i++)
			{
				String friend = friendArray[i];
				
				//if there is some text, try to use it
				friend.replaceAll("[ \t\r\n]","");
				
				//remove the trailing dots and empty space
				while (friend.endsWith(".") || friend.endsWith(" "))
				{
					friend = friend.substring(0, friend.length() -1);
				}
				
				if(friend != null && friend.length() > 0)
				{
					String[] parts = friend.split(at);
			
					if(friend.indexOf(at) == -1 )
					{
						// must be a valid, non university email address	
						addAlert(state, "Friend account " + friend + " does not have a valid friend email address. ");
					}
					else if((parts.length != 2) || (parts[0].length() == 0))
					{
						// must have both id and address part
						addAlert(state, "Friend account " + friend + " does not have an email id. ");
					}
					else if (!Validator.checkEmailLocal(parts[0]))
					{
						addAlert(state, "Friend account " + friend + " has an invalid email address:" + INVALID_EMAIL);
					}
					else
					{
						Participant participant = new Participant();
						try
						{
							// if the friend user already exists
							User u = UserDirectoryService.getUser(friend);
							participant.name = u.getDisplayName();
							participant.uniqname = friend;
							pList.add(participant);
						}
						catch (IdUnusedException e)
						{
							// if the friend user is not in the system yet
							participant.name = friend;
							participant.uniqname = friend;
							pList.add(participant);
						}
					}
				}	// if
			}	// 	
		} // friends
		state.setAttribute(STATE_ADD_PARTICIPANTS, pList);
		
		boolean same_role = true;
		if (params.getString("same_role") == null)
		{
			addAlert(state, "Please choose the role type. ");
		}
		else
		{
			same_role = params.getString("same_role").equals("true")?true:false;
			state.setAttribute("form_same_role", new Boolean(same_role));
		}
		
		if (state.getAttribute(STATE_MESSAGE) != null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "28");
		}
		else
		{
			if (same_role)
			{
				state.setAttribute(STATE_TEMPLATE_INDEX, "50");
			}
			else
			{
				state.setAttribute(STATE_TEMPLATE_INDEX, "51");
			}
		}
		return;
	
	} // checkAddParticipant
	
	/**
	 * 
	 */
	public void doAdd_participant(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		String siteTitle = ((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle();				
		Hashtable selectedRoles = (Hashtable) state.getAttribute(STATE_SELECTED_PARTICIPANT_ROLES);
		int i;
		
		//accept uniqnames and/or friend account names
		String uniqnames = null;
		String friends = null;
		
		if (state.getAttribute("form_uniqnames") != null)
		{
			uniqnames = (String) state.getAttribute("form_uniqnames");
		}
		if (state.getAttribute("form_friends") != null)
		{
			friends = (String) state.getAttribute("form_friends");
		}
		
		boolean notify = false;
		if (state.getAttribute("form_selectedNotify") != null)
		{
			notify = ((Boolean) state.getAttribute("form_selectedNotify")).booleanValue();
		}
		
		boolean same_role = ((Boolean) state.getAttribute("form_same_role")).booleanValue();
	
		String at = "@";
		String pw = "";
		String notAddedNames = null;
		String notAddedFriends = null;

		Vector addedNames = new Vector();
		if (uniqnames != null)
		{
			// adding uniqnames
			String[] uniqnameArray = uniqnames.split("\r\n");
			for (i = 0; i < uniqnameArray.length; i++)
			{
				String uniqname = StringUtil.trimToNull(uniqnameArray[i].replaceAll("[ \t\r\n]",""));
				if(uniqname != null)
				{
					// get role
					String role = null;
					if (same_role)
					{
						// if all added participants have a same role
						role = (String) state.getAttribute("form_selectedRole");
					}
					else
					{
						// if all added participants have different role
						role = (String) selectedRoles.get(uniqname);
					}
					
					if (addUserRealm(state, uniqname, role))
					{
						// successfully added
						addedNames.add(uniqname);
						
						// send notification
						if (notify)
						{
							String emailId = null;
							String userName = null;
							try
							{
								User u = UserDirectoryService.getUser(uniqname);
								emailId = u.getEmail();
								userName = u.getDisplayName();
							}
							catch (IdUnusedException e)
							{
								Log.debug("chef", this + ": cannot find user " + uniqname + ". ");
							}
							// send notification email
							notifyAddedParticipant(false, emailId, userName, siteTitle);
						}
					}
					else
					{
						notAddedNames=notAddedNames.concat(uniqname);
					}
				}
			}
		}	// uniqnames					

		Vector addedFriends = new Vector();
		if (friends != null)
		{
			String[] friendArray = friends.split("\r\n");
	
			for (i = 0; i < friendArray.length; i++)
			{
				String friend = StringUtil.trimToNull(friendArray[i].replaceAll("[ \t\r\n]",""));
				
				// remove the trailing dots and empty space
				while (friend.endsWith(".") || friend.endsWith(" "))
				{
					friend = friend.substring(0, friend.length() -1);
				}

				if(friend != null)
				{	
					//is the friend account user already exists?
					User u = null;
					try
					{
						u = UserDirectoryService.getUser(friend);
					}
					catch (IdUnusedException e) 
					{
						//if there is no such user yet, add the user
						try
						{
							UserEdit uEdit = UserDirectoryService.addUser(friend);

							//set email address
							uEdit.setEmail(friend);
							
							// set id
							uEdit.setId(friend);
							
							// set the friend user type
							uEdit.setType("friend");

							// set password
							//because of cosign security for friend account, password can be set to anything
							Random generator = new Random(System.currentTimeMillis());
							Integer num = new Integer(generator.nextInt());
							pw = num.toString();
							uEdit.setPassword(pw);
							UserDirectoryService.commitEdit(uEdit);
					
						 }
						 catch(IdInvalidException ee)
						 {
							 addAlert(state, "The friend " + friend + " is invalid. ");
							 Log.debug("chef", this + ".checkAddParticipant: UserDirectoryService addUser exception " + e.getMessage());
						 }
						 catch(IdUsedException ee)
						 {
							 addAlert(state, "The friend " + friend + " has been used. ");
							 Log.debug("chef", this + ".checkAddParticipant: UserDirectoryService addUser exception " + e.getMessage());
						 }
						 catch(PermissionException ee)
						 {
							 addAlert(state, "You do not have permission to add " + friend);
							 Log.debug("chef", this + ".chekcAddParticipant: UserDirectoryService addUser exception " + e.getMessage());
						 }	
					}
					
					// get role 
					String role = null;
					if (same_role)
					{
						// if all added participants have a same role
						role = (String) state.getAttribute("form_selectedRole"); 
					}
					else
					{
						// if all added participants have different role
						role = (String) selectedRoles.get(friend);
					}
						
					// add property role to the friend account
					if (addUserRealm(state, friend, role))
					{
						// friend account has been added successfully
						addedFriends.add(friend);
						
						// send notification
						if (notify)
						{
							// send notification email
							notifyAddedParticipant(true, friend, friend, siteTitle);
						}
					}
					else
					{
						notAddedFriends = notAddedFriends.concat(friend + "\n");
					}
				}	// if
			}	// 	
		} // friends

		if (!(addedNames.size() == 0 && addedFriends.size() == 0) && (notAddedNames != null || notAddedFriends != null))
		{
			// at lease one uniqname account or a friend account added
			addAlert(state, "All other users were added");
		}

		if (notAddedNames == null && notAddedFriends == null)
		{
			// all account has been added successfully
			removeAddParticipantContext(state);
		}
		else
		{
			state.setAttribute("form_uniqnames", notAddedNames);
			state.setAttribute("form_friends", notAddedFriends);
		}
		if (state.getAttribute(STATE_MESSAGE) != null)
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "53");
		}
		else
		{
			state.setAttribute(STATE_TEMPLATE_INDEX, "49");
			commitSiteAndRemoveEdit(state);
		}
		return;
				
	}	// doAdd_participant
	
	/**
	 * remove related state variable for adding participants
	 * @param state SessionState object
	 */
	private void removeAddParticipantContext(SessionState state)
	{
		// remove related state variables 
		state.removeAttribute("form_selectedRole");
		state.removeAttribute("form_uniqnames");
		state.removeAttribute("form_friends");
		state.removeAttribute("form_same_role");
		state.removeAttribute("form_selectedNotify");
		state.removeAttribute(STATE_ADD_PARTICIPANTS);
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.removeAttribute(STATE_SELECTED_PARTICIPANT_ROLES);
		
	}	// removeAddParticipantContext
	
	/**
	 * send email notification to added participant
	 * @param state
	 * @param id
	 * @param role
	 * @return
	 */
	private void notifyAddedParticipant(boolean newFriend, String emailId, String userName, String siteTitle)
	{
		String from = ServerConfigurationService.getString("setup.request", null);
		String to = emailId;
		String headerTo = emailId;
		String replyTo = emailId;
		String message_subject = "Site Notification";
		String content = "";
		StringBuffer buf = new StringBuffer();
		buf.setLength(0);
		
		// email body differs between newly added friend account and other users
		buf.append(userName + ":\n\n");
		buf.append("You have been added to the following site:\n" + siteTitle + "\n" + "by " + UserDirectoryService.getCurrentUser().getDisplayName() + ". \n\n");
		
		content = buf.toString();
		EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
	}	// notifyAddedParticipant
	
	/*
	* If the user account does not exist yet inside the user directory, assign role to it
	*/
	private boolean addUserRealm (SessionState state, String id, String role)
	{
		StringBuffer message = new StringBuffer();
		try
		{
			User user = UserDirectoryService.getUser(id);
			String realmId = "/site/" + ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId();
			if (RealmService.allowUpdateRealm(realmId))
			{
				try
				{
					RealmEdit realmEdit = RealmService.editRealm(realmId);
					realmEdit.addUserRole(user, realmEdit.getRole(role));
					RealmService.commitEdit(realmEdit);
				}
				catch (IdUnusedException e)
				{
					message.append(id + " is not a valid user id. \n");
				}
				catch( PermissionException e)
				{
					message.append("You do not have permission to add " + id + " to this site. \n");
				}
				catch (InUseException e)
				{
					message.append("You are editing this site or someone else is editing this site elsewhere. " + id + " could not be added. \n");
				}
				catch (Exception e)
				{
					message.append("Unable to add " + id + " to this site. \n" + e.toString());
				}
			}
		}
		catch (IdUnusedException ee)
		{
			message.append(id + " could not be added - account name does not exist. \n");
		}	// try
	
		if (message.length() == 0)
		{
			return true;
		}
		else
		{
			addAlert(state, message.toString());
			return false;
		}	// if
	
	}	// addUserRealm
	
	/**
	* addNewSite is called when the site has enough information to create a new site
	* 
	*/
	private void addNewSite(ParameterParser params, SessionState state)
	{
		if(state.getAttribute(STATE_SITE_INSTANCE) != null)
		{
			// There is a SiteEdit in state already, so use it rather than creating a new SiteEdit
			return;
		}
					
		//If cleanState() has removed SiteInfo, get a new instance into state
		SiteInfo siteInfo = new SiteInfo();
		if (state.getAttribute(STATE_SITE_INFO) != null)
		{
			siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);	
		}
		String id = StringUtil.trimToNull(siteInfo.getSiteId());
		if (id == null)
		{
			//get id
			id = IdService.getUniqueId();
			siteInfo.site_id = id;
		}
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		if (state.getAttribute(STATE_MESSAGE) == null)
		{		
			try
			{
				SiteEdit site = SiteService.addSite(id);
				
				siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
				
				String title = StringUtil.trimToNull(siteInfo.title);
				String description = siteInfo.description;
				String realmId = NULL_STRING;
				String externalRealmId = NULL_STRING;
				String skin = NULL_STRING;
				boolean external_id = false;
				boolean requested_site = false;
				skin = PROJECT_UNPUBLISHED_SKIN;

				site.setSkin(skin);
				site.setDescription(description);
				if (title != null)
				{
					site.setTitle(title);
				}
				
				site.setType(siteInfo.site_type);
				
				ResourcePropertiesEdit rp = site.getPropertiesEdit();
				rp.addProperty(PROP_SITE_SHORT_DESCRIPTION, siteInfo.short_description);
				rp.addProperty(PROP_SITE_SKIN, siteInfo.skin);
				rp.addProperty(PROP_SITE_INCLUDE, (new Boolean(siteInfo.include)).toString());
				// site contact information
				rp.addProperty(PROP_SITE_CONTACT_NAME, siteInfo.site_contact_name);
				rp.addProperty(PROP_SITE_CONTACT_EMAIL, siteInfo.site_contact_email);
				
				state.setAttribute(STATE_SITE_INSTANCE, site);	
				
				commitSite(state);
			}
			catch (IdUsedException e)
			{
				addAlert(state, "A site with id " + id + " already exists.");
				state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("template-index"));
				return;
			}
			catch (IdInvalidException e)
			{
				addAlert(state,  "The site id " + id + " is not valid.");
				state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("template-index"));
				return;
			}
			catch (PermissionException e)
			{
				addAlert(state, "You do not have permission to add the site " + id +".");
				state.setAttribute(STATE_TEMPLATE_INDEX, params.getString("template-index"));
				return;
			}
		}
	} // addNewSite
	
	/**
	* Use the Realm Provider Id to build a Site tab
	*
	*/
	private String getCourseTab(SessionState state, String id)
	{
		StringBuffer tab = new StringBuffer();
		String[] courses;
		String[] fields;
		String course = NULL_STRING;
		String sections = NULL_STRING;
		String[] sect;
		String firstCourse = NULL_STRING;
		String firstSections = NULL_STRING;
		
		try
		{
			if (id.indexOf("+") != -1) 
			{
				// This is a crosslisted course; tab is first course and its section(s)
				courses = id.split("\\+");
				firstCourse = courses[0]; // Get the first course
				if (firstCourse.indexOf("[") != -1) 
				{
					// Case 1: A cross-listed course with multiple sections; tab is first course and its sections
					sections = firstCourse.substring(firstCourse.indexOf("[")+1, firstCourse.indexOf("]"));
					fields = firstCourse.split(",");
					tab.append(fields[3]); //Subject
					tab.append(" ");
					tab.append(fields[4]); //Catalog number
					tab.append(" ");
					firstSections = sections.replace(',',' '); //Sections
					tab.append(firstSections);
				}
				else
				{
					// Case 2: A cross-listed course with one section; tab is first course and its section
					fields = firstCourse.split(",");
					tab.append(fields[3]); //Subject
					tab.append(" ");
					tab.append(fields[4]); //Catalog number
					tab.append(" ");
					tab.append(fields[5]); //Section
				}
			}
			else if (id.indexOf("[") != -1) 
			{
				// Case 3: A single course with multiple sections; tab is course and sections
				firstCourse = id.substring(0,id.indexOf("[")-1);
				fields = firstCourse.split(",");
				tab.append(fields[3]); //Subject
				tab.append(" ");
				tab.append(fields[4]); //Catalog number
				tab.append(" ");
				sections = id.substring(id.indexOf("[")+1, id.indexOf("]"));
				firstSections = sections.replace(',', ' ');  //Sections
				tab.append(firstSections);
			}
			else 
			{
				// Case 4: A single course with a single section; tab is course and section
				fields = id.split(",");
				tab.append(fields[3]); //Subject
				tab.append(" ");
				tab.append(fields[4]); //Catalog number
				tab.append(" ");
				tab.append(fields[5]); //Section
			}
		}
		catch (Exception e)
		{
			// if there is a problem, create a generic tab
			Log.warn("chef", "SiteAction.getCourseTab Exception " + e.getMessage() + " " + id);
			tab.append(StringUtil.trimToZero(UsageSessionService.getSessionUserId()));
			tab.append(" class");
		}
		return tab.toString();
		
	}//  getCourseTab
	
	/**
	* @param - String filter, the View select criterion
	* @param - SessionState state, the state object
	*/
	private List filterSiteList(SessionState state)
	{
		List sites = new Vector();
		String filter = "";
		
		//get the site list view
		if(state.getAttribute(STATE_VIEW_SELECTED) != null)
		{
			filter = (String) state.getAttribute(STATE_VIEW_SELECTED);
		}
		else
		{
			filter = ALL_MY_SITES;
		}
		
		Site sMyWorkspace = null;
		String myWorkspaceId = SiteService.getUserSiteId(UsageSessionService.getSessionUserId());
		try
		{
			sMyWorkspace = SiteService.getSite(myWorkspaceId);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Cannot find myworkspace site.");
			Log.warn("chef", this + ".filterSiteList: cannot find workspace " + myWorkspaceId);
		}
			
		//now add sites under the correct views
		if (filter.equals(ALL_MY_SITES))
		{
			sites = SiteService.getAllowedSites(false, false, null);
			if (sMyWorkspace != null)
			{
				sites.add(sMyWorkspace);
			}
		}
		else if (filter.equals("myworkspace"))
		{
			if (sMyWorkspace != null)
			{
				sites.add(sMyWorkspace);
			}
		}
		else
		{
			sites = SiteService.getAllowedSites(false, false, filter);
		}
		
		return sites;
		
	} //filterSiteList
	
	/**
	* %%% legacy properties, to be cleaned up
	* 
	*/
	private void sitePropertiesIntoState (SessionState state)
	{
		try
		{
			SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
			SiteInfo siteInfo = new SiteInfo();
			
			// set from site attributes
			siteInfo.title = site.getTitle();
			siteInfo.description = site.getDescription();
			siteInfo.iconUrl = site.getIconUrl();
			siteInfo.infoUrl = site.getInfoUrl();
			siteInfo.joinable = site.isJoinable();
			siteInfo.joinerRole = site.getJoinerRole();
			siteInfo.status = site.getStatus();
			String include = site.getProperties().getProperty(PROP_SITE_INCLUDE);
			if (include != null && include.equalsIgnoreCase(Boolean.TRUE.toString()))
			{
				siteInfo.include = true;
			}
			else
			{
				siteInfo.include = false;
			}

			String site_type = site.getType();

			//set from site properties
			ResourceProperties rp = site.getProperties();
			siteInfo.short_description = rp.getProperty(PROP_SITE_SHORT_DESCRIPTION);
			if(site.getStatus() == Site.SITE_STATUS_PUBLISHED)
			{
				//If published, get skin from site attribute
				siteInfo.skin = site.getSkin();
			}
			else
			{
				//If not published, if there is a PROP_SITE_SKIN use that
				if(rp.getProperty(PROP_SITE_SKIN) != null)
				{
					siteInfo.skin = rp.getProperty(PROP_SITE_SKIN);
				}
				else
				{
					//Otherwise, use a default until the tool can create PROP_SITE_SKIN
					siteInfo.skin = PROJECT_UNPUBLISHED_SKIN;
				}
			}
			state.setAttribute(STATE_SITE_TYPE, siteInfo.site_type);
			
			state.setAttribute(STATE_SITE_INFO, siteInfo);
		}
		catch (Exception e)
		{
			Log.warn("chef", "SiteAction.sitePropertiesIntoState " + e.getMessage());
		}
		
	} // sitePropertiesIntoState 
	
	/**
	* pageMatchesPattern returns true if a SitePage matches a WorkSite Setup pattern
	* 
	*/
	private boolean  pageMatchesPattern (SessionState state, SitePage page)
	{
		List pageToolList =  page.getTools();
		boolean isUserSite = false;
		
		// if no tools on the page, return false
		if(pageToolList == null || pageToolList.size() == 0) { return false; }

		//for the case where the page has one tool
		ToolConfiguration toolConfiguration = (ToolConfiguration)pageToolList.get(0);
		List  toolRegistrationList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_LIST);
		
		//don't compare tool properties, which may be changed using Options
		List toolList = new Vector();
		int count = pageToolList.size();
		boolean match = false;

		//check Worksite Setup Home pattern
		if(page.getTitle()!=null && page.getTitle().equals("Home"))
		{
			return true;
			
		} // Home
		else if(page.getTitle() != null && page.getTitle().equals("Help"))
		{
			//if the count of tools on the page doesn't match, return false
			if(count != 1) { return false;}
			
			//if the page layout doesn't match, return false
			if(page.getLayout() != SitePage.LAYOUT_SINGLE_COL) { return false; }

			//if tooId isn't chef.contactSupport, return false
			if(!(toolConfiguration.getToolId()).equals("chef.contactSupport")) { return false; }

			return true;
		} // Help
		else if(page.getTitle() != null && page.getTitle().equals("Chat"))
		{
			//if the count of tools on the page doesn't match, return false
			if(count != 1) { return false;}
			
			//if the page layout doesn't match, return false
			if(page.getLayout() != SitePage.LAYOUT_SINGLE_COL) { return false; }
			
			//if the tool doesn't match, return false
			if(!(toolConfiguration.getToolId()).equals("chef.chat")) { return false; }
			
			//if the channel doesn't match value for main channel, return false
			String channel = toolConfiguration.getProperties().getProperty("channel");
			if(channel == null) { return false; }
			if(!(channel.equals(NULL_STRING))) { return false; }
			
			return true;
		} // Chat
		else
		{
			//if the count of tools on the page doesn't match, return false
			if(count != 1) { return false;}
			
			//if the page layout doesn't match, return false
			if(page.getLayout() != SitePage.LAYOUT_SINGLE_COL) { return false; }
			
			//if page title doesn't match, return false
			String site_type = NULL_STRING;
			if (state.getAttribute(STATE_SITE_INSTANCE) != null)
			{
				Site s = (Site) state.getAttribute(STATE_SITE_INSTANCE);
				site_type = s.getType();
				isUserSite = SiteService.isUserSite(s.getId());
			}
			
			if (isUserSite || (site_type!= null && site_type.equalsIgnoreCase("myworkspace")))
			{
				toolList = (Vector) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get("myworkspace");
			}
			else
			{
				toolList = (Vector) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(site_type);
			}	
			
			if(pageToolList != null || pageToolList.size() != 0)
			{
				toolConfiguration = (ToolConfiguration)pageToolList.get(0);

				//if tool attributes don't match, return false
				match = false;
				for (ListIterator i = toolList.listIterator(); i.hasNext(); )
				{
					Tool tool = (Tool) i.next();
					if(toolConfiguration.getTitle() != null)
					{
						if((toolConfiguration.getToolId()).indexOf(tool.getId()) != -1) { match = true; }
					}
				}
				if (!match) { return false; }
			}
		} // Others
		return true;
		
	} // pageMatchesPattern
	
	/**
	* siteToolsIntoState is the replacement for siteToolsIntoState_
	* Make a list of pages and tools that match WorkSite Setup configurations into state
	*/
	private void  siteToolsIntoState (SessionState state)
	{
		String wSetupTool = NULL_STRING;
		List wSetupPageList = new Vector();
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		List pageList = site.getPages();
		
		//Put up tool lists filtered by category
		String site_type = site.getType();
		if (site_type == null)
		{
			if (SiteService.isUserSite(site.getId()))
			{
				site_type = "myworkspace";
			}
			else
			{
				Log.warn("chef", "SiteAction.buildContextForTemplate, case 21: - unknown STATE_SITE_TYPE");
			}
		}
		
		List toolRegList = new Vector();
		if (site_type != null)
		{
			toolRegList = (List) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(site_type);
		}
		
		boolean check_home = false;
		boolean hasNews = false;
		boolean hasWebContent = false;
		int newsToolNum = 0;
		int wcToolNum = 0;
		Hashtable newsTitles = new Hashtable();
		newsTitles.put("chef.news", NEWS_DEFAULT_TITLE);
		Hashtable wcTitles = new Hashtable();
		wcTitles.put("chef.iframe", WEB_CONTENT_DEFAULT_TITLE);
		Vector idSelected = new Vector();
		
		if (!((pageList == null) || (pageList.size() == 0)))
		{
			for (ListIterator i = pageList.listIterator(); i.hasNext(); )
			{
				SitePage page = (SitePage) i.next();

				//collect the pages consistent with Worksite Setup patterns
				if(pageMatchesPattern(state, page))
				{
					if(page.getTitle().equals("Home"))
					{
						wSetupTool = "home";
						check_home = true;
					}
					else
					{
						List pageToolList = page.getTools();
						wSetupTool = ((ToolConfiguration)pageToolList.get(0)).getToolId();
						if (wSetupTool.indexOf("chef.news") != -1) 
						{
							String newsToolId = page.getId() + wSetupTool;
							idSelected.add(newsToolId);
							newsTitles.put(newsToolId, page.getTitle());
							newsToolNum++;
							
							// insert the News tool into the list
							hasNews = false;
							int j = 0;
							Tool newTool = new Tool();
							newTool.title = NEWS_DEFAULT_TITLE;
							newTool.id = newsToolId;
							newTool.selected = false;
							
							for (;j< toolRegList.size() && !hasNews; j++)
							{
								Tool t = (Tool) toolRegList.get(j);
								if (t.getId().equals("chef.news"))
								{
									hasNews = true;
									newTool.description = t.getDescription();
								}
							}
					
							if (hasNews)
							{
								toolRegList.add(j-1, newTool);
							}
							else
							{
								toolRegList.add(newTool);
							}
							 
						}
						else if ((wSetupTool).indexOf("chef.iframe") != -1) 
						{
							String wcToolId = page.getId() + wSetupTool;
							idSelected.add(wcToolId);
							wcTitles.put(wcToolId, page.getTitle());
							wcToolNum++;
							
							Tool newTool = new Tool();
							newTool.title = WEB_CONTENT_DEFAULT_TITLE;
							newTool.id = wcToolId;
							newTool.selected = false;
							
							hasWebContent = false;
							int j = 0;
							for (;j< toolRegList.size() && !hasWebContent; j++)
							{
								Tool t = (Tool) toolRegList.get(j); 
								if (t.getId().equals("chef.iframe"))
								{
									hasWebContent = true;
									newTool.description = t.getDescription();
								}
							}
							if (hasWebContent)
							{
								toolRegList.add(j-1, newTool);
							}
							else
							{
								toolRegList.add(newTool);
							}
						}
						else
						{
							idSelected.add(wSetupTool);
						}
					}
					
					WorksiteSetupPage wSetupPage = new WorksiteSetupPage();
					wSetupPage.pageId = page.getId();
					wSetupPage.pageTitle = page.getTitle();
					wSetupPage.toolId = wSetupTool;
					wSetupPageList.add(wSetupPage);
				}
			}
		}
		
		for(int i = 0; i<idSelected.size(); i++)
		{
			String tempId = (String)idSelected.get(i);
		}
		
		state.setAttribute(STATE_TOOL_HOME_SELECTED, new Boolean(check_home));
		state.setAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST, idSelected); // List of ToolRegistration toolId's
		state.setAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST, idSelected); // List of ToolRegistration toolId's
		state.setAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME, Boolean.valueOf(check_home));
		state.setAttribute(STATE_NEWS_TITLES, newsTitles);
		state.setAttribute(STATE_WEB_CONTENT_TITLES, wcTitles);
		state.setAttribute(STATE_WORKSITE_SETUP_PAGE_LIST, wSetupPageList);
		
	} //siteToolsIntoState
	
	/**
	 * reset the state variables used in edit tools mode
	 * @state The SessionState object
	 */
	private void removeEditToolState(SessionState state)
	{
		state.removeAttribute(STATE_TOOL_HOME_SELECTED);
		state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST); // List of ToolRegistration toolId's
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST); // List of ToolRegistration toolId's
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
		state.removeAttribute(STATE_NEWS_TITLES);
		state.removeAttribute(STATE_WEB_CONTENT_TITLES);
		state.removeAttribute(STATE_WORKSITE_SETUP_PAGE_LIST);
		setupToolLists(state);
	}
		
	private List orderTools(SessionState state, List toolRegistrationList)
	{
		List rv = new Vector();
		List toolOrder = (Vector) state.getAttribute("toolOrder");
		for (ListIterator i = toolOrder.listIterator(); i.hasNext(); )
			{
				String tool_id = (String) i.next();
				for (ListIterator j = toolRegistrationList.listIterator(); j.hasNext(); )
				{
					ToolRegistration tr = (ToolRegistration) j.next();
					if(tr.getId().equals(tool_id))
					{
						rv.add(tr);
					}
				}
			}
		return rv;
		
	} // orderTools
	
	private void setupToolLists(SessionState state)
	{
		// featureList is the IdAndText List correspondiong to the getDefaultToolRegistration ToolConfiguration List
		// toolRegistrationLIst is the List returned by getDefaultToolRegistration
		List featureList = new Vector(); // for template feature list, IdAndText object list, to replace with Tool list
		Hashtable toolList = new Hashtable(); // for storing tool sets according to the tool category
		
		List toolRegistrationList = orderTools(state, ServerConfigurationService.getToolRegistrations());
		if(toolRegistrationList.isEmpty())
		{
			Log.warn("chef", "SiteAction.setupToolLists -  toolRegistrationList.isEmpty ");
		}
		else
		{
			for (ListIterator i = toolRegistrationList.listIterator(); i.hasNext(); )
			{
				ToolRegistration tr = (ToolRegistration) i.next();
				List categoriesList = tr.getCategories();
				Tool tool = new Tool();
				tool.id = tr.getId();
				tool.title = tr.getTitle();
				tool.description = tr.getDescription();
				tool.selected = false;
				for (ListIterator k = categoriesList.listIterator(); k.hasNext(); )
				{
					Tool t = new Tool();
					t.id = tr.getId();
					t.title = tr.getTitle();
					t.description = tr.getDescription();
					t.selected = false;
					String category = (String) k.next();
					
					// update the corrresponding category tool list
					Vector tools = new Vector();
					if (toolList.get(category) != null)
					{
						tools = (Vector) toolList.get(category);
					}
					tools.add(t);
					toolList.put(category, tools);
				}
				
				//for sites created/edited in Worksite Setup
				state.setAttribute(STATE_TOOL_LIST, toolList);
			}
			
		}
		state.setAttribute(STATE_TOOL_REGISTRATION_LIST, toolRegistrationList);
		state.setAttribute(FEATURE_LIST, featureList); // IdAndText type
		
	} // setupToolLists
	
	private void setupFormNamesAndConstants(SessionState state)
	{
		TimeBreakdown timeBreakdown = (TimeService.newTime ()).breakdownLocal ();
		String mycopyright = COPYRIGHT_SYMBOL + " " + timeBreakdown.getYear () + ", " + UserDirectoryService.getCurrentUser().getDisplayName () + ". All Rights Reserved. ";
		state.setAttribute (STATE_MY_COPYRIGHT, mycopyright);
		state.setAttribute (STATE_SITE_INSTANCE, null);
		state.setAttribute (STATE_INITIALIZED, Boolean.TRUE.toString());
		SiteInfo siteInfo = new SiteInfo();
		Participant participant = new Participant();
		participant.name = NULL_STRING;
		participant.uniqname = NULL_STRING;
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		state.setAttribute("form_participantToAdd", participant);
		state.setAttribute(FORM_ADDITIONAL, NULL_STRING);
		//legacy
		state.setAttribute(FORM_HONORIFIC,"0");
		state.setAttribute(FORM_REUSE, "0"); 
		state.setAttribute(FORM_RELATED_CLASS, "0");
		state.setAttribute(FORM_RELATED_PROJECT, "0");
		state.setAttribute(FORM_INSTITUTION, "0");
		//sundry form variables
		state.setAttribute(FORM_PHONE,"");
		state.setAttribute(FORM_EMAIL,"");
		state.setAttribute(FORM_SUBJECT,"");
		state.setAttribute(FORM_DESCRIPTION,"");
		state.setAttribute(FORM_TITLE,"");
		state.setAttribute(FORM_NAME,"");
		state.setAttribute(FORM_SHORT_DESCRIPTION,"");
		
	}	// setupFormNamesAndConstants
	
	/**
	 * A dispatch funtion when selecting course features
	 */
	public void doAdd_features ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();

		String siteType = (String) state.getAttribute(STATE_SITE_TYPE);
		
		String option = params.getString("option");
		
		// dispatch
		if (option.equalsIgnoreCase("addNews"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, siteType);
				insertTool(state, siteType, "chef.news", STATE_NEWS_TITLES, NEWS_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("addWebContent"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, siteType);
				insertTool(state, siteType, "chef.iframe", STATE_WEB_CONTENT_TITLES, WEB_CONTENT_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("continue"))
		{
			// continue
			doContinue(data);
		}
		else if (option.equalsIgnoreCase("back"))
		{
			// back
			doBack(data);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel_create(data);
		}
		
	}	// doAdd_features

	/**
	 * update the selected tool list
	 * @param state The SessionState object
	 * @param params The ParameterParser object
	 * @param siteType The site type
	 */
	private void updateSelectedToolList (SessionState state, ParameterParser params, String siteType)
	{
		// the list of available tools
		List toolList = (Vector) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(siteType);
		for(int i= 0; i < toolList.size(); i++)
		{
			Tool t = (Tool)toolList.get(i);
		}
		
		List selectedTools = new ArrayList(Arrays.asList(params.getStrings ("selectedTools")));
		Vector idSelected = new Vector();
		Hashtable titles = (Hashtable) state.getAttribute(STATE_NEWS_TITLES);
		if (titles == null)
		{
			titles = new Hashtable();
			titles.put("chef.news", NEWS_DEFAULT_TITLE);
			state.setAttribute(STATE_NEWS_TITLES, titles);
		}
		Hashtable wcTitles = (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES);
		if (wcTitles == null)
		{
			wcTitles = new Hashtable();
			wcTitles.put("chef.iframe", WEB_CONTENT_DEFAULT_TITLE);
			state.setAttribute(STATE_WEB_CONTENT_TITLES, wcTitles);
		}
		
		// how many news/web content tool we have
		int newsToolNum = 0;
		int wcToolNum = 0;
		
		for (int i = 0; i < toolList.size(); i++)
		{
			String id = ((Tool) toolList.get(i)).getId();
			if (id.indexOf("chef.news") != -1)
			{
				String title = StringUtil.trimToNull(params.getString("titlefor" + id));
				if (title == null)
				{
					// if there is no input, make the title for news tool default to NEWS_DEFAULT_TITLE
					title = NEWS_DEFAULT_TITLE;
				}
				titles.put(id, title);
			}
			else if (id.indexOf("chef.iframe") != -1)
			{
				String wcTitle = StringUtil.trimToNull(params.getString("titlefor" + id));
				if (wcTitle == null)
				{
					// if there is no input, make the title for Web Content tool default to WEB_CONTENT_DEFAULT_TITLE
					wcTitle = WEB_CONTENT_DEFAULT_TITLE;
				}
				wcTitles.put(id, wcTitle);
			}
		}
		boolean has_home = false;
		String emailId = null;
		for (int i = 0; i < selectedTools.size(); i++)
		{
			String toolId = (String) selectedTools.get(i);
			if (toolId.equalsIgnoreCase("home"))
			{
				has_home = true;
			}
			else if (toolId.equalsIgnoreCase("chef.mailbox"))
			{
				// if Email archive tool is selected, check the email alias
				emailId = StringUtil.trimToNull(params.getString("emailId"));
				if (emailId == null)
				{
					addAlert(state, "Please specify an email address for Email Archive tool. ");
				}
				else 
				{
					if (!Validator.checkEmailLocal(emailId))
					{
						addAlert(state, INVALID_EMAIL);
					}
					else
					{
						//check to see whether the alias has been used by other sites
						try
						{
							String target = AliasService.getTarget(emailId);
							String siteId = ((Site)state.getAttribute(STATE_SITE_INSTANCE)).getId();
							String channelReference = MailArchiveService.channelReference(siteId, SiteService.MAIN_CONTAINER);
							if (target != null && !target.equals(channelReference))
							{
								addAlert(state, "The email alias is already in use. ");
							}
						}
						catch (IdUnusedException ee){}
					}
				}
			}
			idSelected.add(toolId);
		}
		state.setAttribute(STATE_TOOL_HOME_SELECTED, new Boolean(has_home));
		state.setAttribute(STATE_TOOL_EMAIL_ADDRESS, emailId);
		state.setAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST, idSelected); // List of ToolRegistration toolId's
		state.setAttribute(STATE_NEWS_TITLES, titles);
		state.setAttribute(STATE_WEB_CONTENT_TITLES, wcTitles);
		
	}	 // updateSelectedToolList
	
	/**
	 * find the tool in the tool list and insert another tool instance to the list
	 * @param state SessionState object
	 * @param siteType The site type
	 * @param toolId The id for the inserted tool
	 * @param stateTitleVariable The titles
	 * @param defaultTitle The default title for the inserted tool
	 */
	private void insertTool(SessionState state, String siteType, String toolId, String stateTitlesVariable, String defaultTitle)
	{
		//the list of available tools
		List toolList = (Vector) ((Hashtable) state.getAttribute(STATE_TOOL_LIST)).get(siteType);
		
		int toolListedTimes = 0;
		int toolSelectedTimes = 0;
		// the tool list 
		int index = 0;
		while ( index < toolList.size())
		{
			Tool tListed = (Tool) toolList.get(index);
			if (tListed.getId().indexOf(toolId) != -1 )
			{
				toolListedTimes++;
			}
			index ++;
		}
		// go through the selected tool list
		index = 0;
		List toolSelected = (List) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		while (index < toolSelected.size())
		{
			String tId = (String) toolSelected.get(index);
			if (tId.indexOf(toolId) != -1)
			{
				toolSelectedTimes++;
			}
			index ++;
		}
		//We need to insert a specific tool entry only if all the specific tool entries have been selected 
		for (int times = toolListedTimes; times <= toolSelectedTimes && toolSelectedTimes != 0; times++)
		{
			// the titles
			Hashtable titles = (Hashtable) state.getAttribute(stateTitlesVariable);
			if (titles == null)
			{
				titles = new Hashtable();
			}
			
			// find the first occurence of the specified tool?
			boolean toolFound = false;
		
			// find the first occurence of none specified tool?
			boolean firstNoneTool = false;
		
			int i = 0;
			Tool newTool = new Tool();
		
			while (!firstNoneTool && i < toolList.size())
			{
				Tool t = (Tool) toolList.get(i);
				if (!toolFound)
				{
					// look for the first tool occurance
					if (t.getId().indexOf(toolId) != -1)
					{
						toolFound = true;
						newTool.title = t.getTitle();
						newTool.description = t.getDescription();
						newTool.selected = false;
					}
				}
				else
				{
					// look for the first none tool
					if (t.getId().indexOf(toolId) == -1)
					{
						firstNoneTool = true;
						// insert a new tool
						newTool.id = toolId + times;
						toolList.add(i, newTool);
						if (titles.get(newTool.id) == null)
						{
							titles.put(newTool.id, defaultTitle);
						}
					}
				}
				 
				i++;
			}	// while
		
			state.setAttribute(stateTitlesVariable, titles);
			
		} // if
	}	//	insertTool 
	
	/**
	 *  Final step in creating a site - publish the site or leave it unpublished.
	 */
	public void doPublish_created_site ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String siteId = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).getId();
			
		String option = params.getString("option");
		if (option.equalsIgnoreCase("publish"))
		{	
			finishSiteEdit(state, Site.SITE_STATUS_PUBLISHED);
		}
		else if (option.equalsIgnoreCase("unpublish"))
		{	
			finishSiteEdit(state, Site.SITE_STATUS_UNPUBLISHED);
		}
		
		//now that the site exists, we can set the email alias when an Email Archive tool has been selected
		String alias = StringUtil.trimToNull((String) state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
		if (alias != null)
		{	
			String channelReference = MailArchiveService.channelReference(siteId, SiteService.MAIN_CONTAINER);
			try
			{
				AliasService.setAlias(alias, channelReference);
			}
			catch (IdUsedException ee) 
			{
				addAlert(state, "Alias " + alias + " already exists.");
			}
			catch (IdInvalidException ee) 
			{
				addAlert(state, "Alias " + alias + " is invalid.");
			}
			catch (PermissionException ee) 
			{
				addAlert(state, "You do not have permission to add alias. ");
			}
		}

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));
			
		resetPaging(state);
		
		// clean state variables
		cleanState(state);
		
		state.setAttribute(STATE_TEMPLATE_INDEX, "0");		
		
	}	// doPublish_created_site
	
	/**
	 * 
	 * get site's participant list
	 */
	private void setSiteParticipantList(SessionState state)
	{
		List participantList = new Vector();
		
		String siteId = ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		String site_type = (String) state.getAttribute(STATE_SITE_TYPE); 
		if(site_type != null)
		{
			participantList.addAll(getParticipantList(state));
		}	// if
		  
		state.setAttribute(STATE_PARTICIPANT_LIST, participantList);
	
	}	// setSiteParticipantList
	
	/**
	 * 
	 * set selected participant role Hashtable
	 */
	private void setSelectedParticipantRoles(SessionState state)
	{
		List selectedUserIds = (List) state.getAttribute(STATE_SELECTED_USER_LIST);
		List participantList = (List) state.getAttribute(STATE_PARTICIPANT_LIST);
		List selectedParticipantList = new Vector();
		
		Hashtable selectedParticipantRoles = new Hashtable();
		
		if(!selectedUserIds.isEmpty() && participantList != null)
		{
			for (int i = 0; i < participantList.size(); i++)
			{
				String id= "";
				Object o = (Object) participantList.get(i);
				if (o.getClass().equals(Participant.class))
				{
					// get participant roles
					id = ((Participant) o).getUniqname();
					Iterator pRoles = (((Participant) o).getRoles()).iterator();
					if (pRoles.hasNext())
					{
						selectedParticipantRoles.put(id, pRoles.next());
					}
				}
				else if (o.getClass().equals(Student.class))
				{
					// get participant from roster role
					id = ((Student) o).getUniqname();
					selectedParticipantRoles.put(id, ((Student)o).getRole());
				}
				if (selectedUserIds.contains(id))
				{
					selectedParticipantList.add(participantList.get(i));
				}
			}
		}
		state.setAttribute(STATE_SELECTED_PARTICIPANT_ROLES, selectedParticipantRoles);
		state.setAttribute(STATE_SELECTED_PARTICIPANTS, selectedParticipantList);
		
	}	//  setSelectedParticipantRoles
	
	/**
	 * the SiteComparator class
	 */
	private class SiteComparator
		implements Comparator
	{
		/**
		 * the criteria
		 */
		String m_criterion = null;
		String m_asc = null;
		
		/**
		 * constructor
		 * @param criteria The sort criteria string
		 * @param asc The sort order string. TRUE_STRING if ascending; "false" otherwise.
		 */
		public SiteComparator (String criterion, String asc)
		{
			m_criterion = criterion;
			m_asc = asc;
			
		}	// constructor
		
		/**
		* implementing the Comparator compare function
		* @param o1 The first object
		* @param o2 The second object
		* @return The compare result. 1 is o1 < o2; -1 otherwise
		*/
		public int compare ( Object o1, Object o2)
		{
			int result = -1;
			
			if(m_criterion==null) m_criterion = SORTED_BY_TITLE;
			
			/************* for sorting site list *******************/
			if (m_criterion.equals (SORTED_BY_TITLE))
			{
				// sorted by the worksite title
				String s1 = ((Site) o1).getTitle();
				String s2 = ((Site) o2).getTitle();
				result =  s1.compareToIgnoreCase (s2);
			}
			else if (m_criterion.equals (SORTED_BY_DESCRIPTION))
			{
				
				// sorted by the site short description
				String s1 = ((Site) o1).getProperties().getProperty(PROP_SITE_SHORT_DESCRIPTION);
				String s2 = ((Site) o2).getProperties().getProperty(PROP_SITE_SHORT_DESCRIPTION);
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_TYPE))
			{
				// sorted by the site type
				String s1 = ((Site) o1).getType();
				String s2 = ((Site) o2).getType();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_OWNER))
			{
				// sorted by the site creator
				String s1 = ((Site) o1).getProperties().getProperty("CHEF:creator");
				String s2 = ((Site) o2).getProperties().getProperty("CHEF:creator");
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_STATUS))
			{
				// sort by the status, published or unpublished
				int i1 = ((Site)o1).getStatus();
				int i2 = ((Site)o2).getStatus();
				if (i1 > i2)
				{
					result = 1;
				}
				else
				{
					result = -1;
				}
			}
			else if (m_criterion.equals (SORTED_BY_JOINABLE))
			{
				// sort by whether the site is joinable or not
				boolean b1 = ((Site)o1).isJoinable();
				boolean b2 = ((Site)o2).isJoinable();
				if (b1 == b2)
				{
					result = 0;
				}
				else if (b1 == true)
				{
					result = 1;
				}
				else
				{
					result = -1;
				}
			}
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_NAME))
			{
				// sort by whether the site is joinable or not
				String s1 = null;
				if (o1.getClass().equals(Participant.class))
				{
					s1 = ((Participant) o1).getName();	
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getName();	
				}
				
				String s2 = null;
				if (o2.getClass().equals(Participant.class))
				{
					s2 = ((Participant) o2).getName();	
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getName();	
				}
				
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_UNIQNAME))
			{
				// sort by whether the site is joinable or not
				String s1 = null;
				if (o1.getClass().equals(Participant.class))
				{
					s1 = ((Participant) o1).getUniqname();	
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getUniqname();	
				}
				
				String s2 = null;
				if (o2.getClass().equals(Participant.class))
				{
					s2 = ((Participant) o2).getUniqname();	
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getUniqname();	
				}				

				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_ROLE))
			{
				String s1 = "";
				if (o1.getClass().equals(Participant.class))
				{
					Iterator roles1 = (((Participant) o1).getRoles()).iterator();
					while (roles1.hasNext())
					{
						s1 = s1.concat(((Role) roles1.next()).getId());
					}
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getRole();	
				}
				
				String s2 = "";
				if (o2.getClass().equals(Participant.class))
				{
					Iterator roles2 = (((Participant) o2).getRoles()).iterator();

					while (roles2.hasNext())
					{
						s2 = s2.concat(((Role) roles2.next()).getId());
					}
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getRole();	
				}		
				
				if (s1.length() == 0 && s2.length() == 0)
				{
					result = 0;
				}
				else if (s2.length() == 0)
				{
					result = 1;
				}
				else if (s1.length() == 0)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_ID))
			{
				String s1 = null;
				if (o1.getClass().equals(Participant.class))
				{
					
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getId();	
				}
				
				String s2 = null;
				if (o2.getClass().equals(Participant.class))
				{
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getId();	
				}		
				
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_CREATION_DATE))
			{
				// sort by the site's creation date
				Time t1 = null;  
				Time t2 = null;
	
				// get the times
				try
				{
					t1 = ((Site)o1).getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				}
				catch (EmptyException e)
				{
				}
				catch (TypeException e)
				{
				}
	
				try
				{
					t2 = ((Site)o2).getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				}
				catch (EmptyException e)
				{
				}
				catch (TypeException e)
				{
				}
				if (t1==null)
				{
					result = -1;
				}
				else if (t2==null)
				{
					result = 1;
				}
				else if (t1.before (t2))
				{
					result = -1;
				}
				else
				{
					result = 1;
				}
			}
			
			if(m_asc == null) m_asc = Boolean.TRUE.toString ();
			
			// sort ascending or descending
			if (m_asc.equals (Boolean.FALSE.toString ()))
			{
				result = -result;
			}
			
			return result;
			
		}	// compare
		
	} //SiteComparator
	
	/**
	* the StudentComparator class
	*/
	private class StudentComparator
		implements Comparator
	{
		/**
		 * the criteria
		 */
		String m_criterion = null;
		String m_asc = null;
		
		/**
		 * constructor
		 * @param criteria The sort criteria string
		 * @param asc The sort order string. TRUE_STRING if ascending; "false" otherwise.
		 */
		public StudentComparator (String criterion, String asc)
		{
			m_criterion = criterion;
			m_asc = asc;
			
		}	// constructor
		
		/**
		* implementing the Comparator compare function
		* @param o1 The first object
		* @param o2 The second object
		* @return The compare result. 1 is o1 < o2; 0 is o1.equals(o2); -1 otherwise
		*/
		public int compare ( Object o1, Object o2)
		{
			int result = -1;
			
			if(m_criterion==null) m_criterion = SORTED_BY_STUDENT_NAME;
			
			/************* for sorting site list *******************/
			if (m_criterion.equals (SORTED_BY_STUDENT_NAME))
			{
				// sorted by the String student name
				String s1 = ((Student) o1).getName();
				String s2 = ((Student) o2).getName();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result =  s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_UNIQNAME))
			{
				// sorted by the String student uniqname
				String s1 = ((Student) o1).getUniqname();
				String s2 = ((Student) o2).getUniqname();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_STUDENT_ID))
			{
				// sorted by the String U-M id
				String s1 = ((Student) o1).getId();
				String s2 = ((Student) o2).getId();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_LEVEL))
			{
				// sorted by the String course level
				String s1 = ((Student) o1).getLevel();
				String s2 = ((Student) o2).getLevel();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_CREDITS))
			{
				// sorted by the String credits for course
				String s1 = ((Student) o1).getCredits();
				String s2 = ((Student) o2).getCredits();
				if (s1==null && s2==null)
				{
					result = 0;
				}
				else if (s2==null)
				{
					result = 1;
				}
				else if (s1==null)
				{
					result = -1;
				}
				else
				{
					result = s1.compareToIgnoreCase (s2);
				}
			}
			else if (m_criterion.equals (SORTED_BY_CREATION_DATE))
			{
				// sort by the site's creation date
				Time t1 = null;  
				Time t2 = null;
				
				// get the times
				try
				{
					t1 = ((Site)o1).getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				}
				catch (EmptyException e)
				{
				}
				catch (TypeException e)
				{
				}
				
				try
				{
					t2 = ((Site)o2).getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				}
				catch (EmptyException e)
				{
				}
				catch (TypeException e)
				{
				}
				if (t1==null)
				{
					result = -1;
				}
				else if (t2==null)
				{
					result = 1;
				}
				else if (t1.before (t2))
				{
					result = -1;
				}
				else
				{
					result = 1;
				}
			}
			
			if(m_asc == null) m_asc = Boolean.TRUE.toString ();
			
			// sort ascending or descending
			if (m_asc.equals (Boolean.FALSE.toString ()))
			{
				result = -result;
			}
			return result;
			
		}	// compare
		
	} //StudentComparator
	
	public class Skin
	{
		public String css;
		public String unit;
		
		public String getCss() { return css; }
		public String getUnit() { return unit; }
	}
	
	// a utility class for form select options
	public class IdAndText
	{
		public int id;
		public String text;

		public int getId() { return id;}
		public String getText() { return text;}
		
	} // IdAndText
	
	// a utility class for working with ToolConfigurations and ToolRegistrations
	// %%% convert featureList from IdAndText to Tool so getFeatures item.id = chosen-feature.id is a direct mapping of data
	public class Tool
	{
		public String id = NULL_STRING;
		public String title = NULL_STRING;
		public String description = NULL_STRING;
		public boolean selected = false;
		
		public String getId() { return id; }
		public String getTitle() { return title; }
		public String getDescription() { return description; }
		public boolean getSelected() { return selected; }
		
	} // Tool
	
	/*
	* WorksiteSetupPage is a utility class for working with site pages configured by Worksite Setup
	*
	*/
	public class WorksiteSetupPage
	{
		public String pageId = NULL_STRING;
		public String pageTitle = NULL_STRING;
		public String toolId = NULL_STRING;
		
		public String getPageId() { return pageId; }
		public String getPageTitle() { return pageTitle; }
		public String getToolId() { return toolId; }
		
	} // WorksiteSetupPage
	
	/**
	* Participant in site access roles
	*
	*/
	public class Participant
	{
		public String name = NULL_STRING;
		public String uniqname = NULL_STRING;
		public Set roles = new HashSet(); 
		
		public String getName() {return name; }
		public String getUniqname() {return uniqname; }
		public Set getRoles() { return roles; } // cast to Role
		public boolean hasRole(String id)
		{
			boolean m_hasRole = false;
			for (Iterator i = roles.iterator(); i.hasNext(); )
			{
				Role role = (Role)i.next();
				if (role.getId().equals(id)) m_hasRole = true;
			}
			return m_hasRole;
		}
		
	} // Participant
	
	/**
	* Student in roster
	*
	*/
	public class Student
	{
		public String name = NULL_STRING;
		public String uniqname = NULL_STRING;
		public String id = NULL_STRING;
		public String level = NULL_STRING;
		public String credits = NULL_STRING;
		public String role = NULL_STRING;
		public String course = NULL_STRING;
		public String section = NULL_STRING;
		
		public String getName() {return name; }
		public String getUniqname() {return uniqname; }
		public String getId() { return id; }
		public String getLevel() { return level; }
		public String getCredits() { return credits; }
		public String getRole() { return role; }
		public String getCourse() { return course; }
		public String getSection() { return section; }
		
	} // Student
	
	public class SiteInfo
	{
		public String subject = NULL_STRING;
		public String site_id = NULL_STRING; // getId of Resource
		public String external_id = NULL_STRING; // if matches site_id connects site with U-M course information
		public String site_type = "";
		public String iconUrl = NULL_STRING;
		public String infoUrl = NULL_STRING;
		public String skin = NULL_STRING;
		public boolean joinable = false;
		public String joinerRole = NULL_STRING;
		public String title = NULL_STRING; // the short name of the site
		public String short_description = NULL_STRING; // the short (20 char) description of the site
		public String description = NULL_STRING;  // the longer description of the site
		public String additional = NULL_STRING; // additional information on crosslists, etc.
		public String sections = NULL_STRING; // a character-delimited string of one or more class sections with access to a site
		public int status = -1;
		public boolean include = true;	// include the site in the Sites index; default is true.
		public String site_contact_name = NULL_STRING;	// site contact name
		public String site_contact_email = NULL_STRING;	// site contact email
		
		public String getSubject() { return subject; }
		public String getSiteId() {return site_id;}
		public String getSiteType() { return site_type; }
		public String getTitle() { return title; } 
		public String getDescription() { return description; }
		public String getIconUrl() { return iconUrl; }
		public String getInfoUrll() { return infoUrl; }
		public String getSkin() {return skin; }
		public boolean getJoinable() {return joinable; }
		public String getJoinerRole() {return joinerRole; }
		public String getAdditional() { return additional; }
		public String getSections() {return sections; } // this is a character-delimited string of one or more sections
		public int getStatus() { return status; }
		public boolean getInclude() {return include;}
		public String getSiteContactName() {return site_contact_name; }
		public String getSiteContactEmail() {return site_contact_email; }
		
	} // SiteInfo

	public class SiteListItem
	{
		public String id = ""; 
		public String title = "";
		public String by = "";
		public String course_subject = "";
		public List sections = new Vector();
		
		public String getId() { return id; }
		public String getTitle() { return title; }
		public String getBy() { return by; }
		public String getCourse_subject() { return course_subject; }
		public List getSections() { return sections; }
		
	} // SiteListItem
	
}	// SiteAction

/**********************************************************************************
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitesetupgeneric/SiteAction.java,v 1.11 2004/10/15 01:23:45 zqian.umich.edu Exp $
**********************************************************************************/
