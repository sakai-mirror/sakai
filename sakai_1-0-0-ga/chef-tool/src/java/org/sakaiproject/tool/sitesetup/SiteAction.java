/**********************************************************************************
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitesetup/SiteAction.java,v 1.94 2004/10/07 01:57:38 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.sitesetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.dissertation.cover.DissertationService;
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
import org.sakaiproject.util.UmiacClient;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.delivery.RefreshSiteNavDelivery;
import org.sakaiproject.util.delivery.RefreshTopDelivery;

/**
* <p>SiteAction is a CHEF Site setup and configuration tool used
* at the University of Michigan.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.94 $
*/

// Not all of the templates have been implemented.

public class SiteAction extends NewPagedResourceAction
{
	/** portlet configuration parameter values**/
	private static final String SITE_MODE_SITESETUP = "sitesetup";
	private static final String SITE_MODE_SITEINFO= "siteinfo";
	private static final String STATE_SITE_MODE = "site_mode";
	
	// template root chef_site
	/*
	 * TEMPLATE_INDEX = 
		0 "-list",
		1 "-type",
		2 "-courseContact",
		3 "-courseClass", 
		4 "-courseFeatures",
		5 "-dissertationContact",
		6 "-dissertationProgram",
		7 "-dissertationFeatures",
		8 "-projectContact",
		9 "-projectInformation",
		10 "-projectFeatures",
		11 "-courseInformation",
		12 "-coursePublish",
		13 "-otherInvite",
		14 "-otherPublish",
		15 "-customize",
		16 "-basicInformation",
		17 "-addRemoveParticipant",
		18 "-addRemoveSection",
		19 "-sectionInformation",
		20 "-deleteSection",
		21 "-addRemoveFeature",
		22 "-customLayout",
		23 "-customLink",
		24 "-customPage",
		25 "-roster",
		26 "-customFeatures",
		27 "-listParticipants",
		28 "-addParticipant",
		29 "-removeParticipants",
		30 "-changeRoles",
		31 "-confirmSiteDelete",
		32 "-makePublic",
		33 "-setType",
		34 "-publishUnpublish",
		35 "-roster-sorted",
		36 "-addMyWorkspaceFeature",
		37 "-removeMyWorkspaceFeature",
		38 "-projectConfirm",
		39 "-courseManual",
		40 "-courseConfirm",
		41 "-sitePublishUnpublish",
		42 "-siteInfo-list",
		43 "-siteInfo-editInfo",
		44 "-siteInfo-editInfoConfirm",
		45 "-addRemoveFeatureConfirm",
		46 "-publishUnpublish-sendEmail",
		47 "-publishUnpublish-confirm",
		48 "-gradtoolsConfirm",
		49 "-siteInfo-editAccess",
		50 "-addParticipant-sameRole",
		51 "-addParticipant-differentRole",
		52 "-addParticipant-notification",
		53 "-addParticipant-confirm",
		54 "-siteInfo-editAccess-globalAccess",
		55 "-siteInfo-editAccess-globalAccess-confirm",
		56 "-changeRoles-confirm",
		57 "-removeClass",
		58 "-addRemoveClassConfirm",
		59 "-addClass-pickTerm"
	 */
	protected final static String[] TEMPLATE = 
	{
		"-list",
		"-type",
		"-courseContact",
		"-courseClass", 
		"-courseFeatures",
		"-dissertationContact",
		"-dissertationProgram",
		"-dissertationFeatures",
		"-projectContact",
		"-projectInformation",
		"-projectFeatures",
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
		"-confirmSiteDelete",
		"-makePublic",
		"-setType",
		"-publishUnpublish",
		"-roster-sorted",
		"-addMyWorkspaceFeature",
		"-removeMyWorkspaceFeature",
		"-projectConfirm",
		"-courseManual",
		"-courseConfirm",
		"-sitePublishUnpublish",
		"-siteInfo-list",
		"-siteInfo-editInfo",
		"-siteInfo-editInfoConfirm",
		"-addRemoveFeatureConfirm",
		"-publishUnpublish-sendEmail",
		"-publishUnpublish-confirm",
		"-gradtoolsConfirm",
		"-siteInfo-editAccess",
		"-addParticipant-sameRole",
		"-addParticipant-differentRole",
		"-addParticipant-notification",
		"-addParticipant-confirm",
		"-siteInfo-editAccess-globalAccess",
		"-siteInfo-editAccess-globalAccess-confirm",
		"-changeRoles-confirm",
		"-removeClass",
		"-addRemoveClassConfirm",
		"-addClass-pickTerm"
	};
	
	private final static String[] TERMS =
	{
		"Fall 2003",
		"Winter 2004",
		"Spring 2004",
		"Spring-Summer 2004",
		"Summer 2004",
		"Fall 2004",
		"Winter 2005"
	};
	
	/** Non-unit-specific skins. See setupSkins for unit-specific skins. */
	private final static String COURSE_UNPUBLISHED_SKIN = "ctng.css";
	private final static String COURSE_PUBLISHED_UM_SKIN = "um.css";
	private final static String PROJECT_UNPUBLISHED_SKIN = "pro.css";
	private final static String PROJECT_PUBLISHED_SKIN = "prp.css";

	/** Used to check if there is a site instance in state */
	private boolean siteInState = false;
	
	/** Name of state attribute for SiteEdit instance  */
	private static final String STATE_SITE_INSTANCE = "site.instance";
	
	/** Name of state attribute for Site Information  */
	private static final String STATE_SITE_INFO = "site.info";
	
	/** Name of state attribute for CHEF site type  */
	private static final String STATE_SITE_TYPE = "CTNG:site-type";
	
	/** Name of the state attribute of the External Id property
	*   
	* If the course and section selected are from a list provided by UMIAC
	* then the site's external id will be generated automatically. Otherwise, a
	* request to create the site is sent to support, and the external id is created
	* manually. The external id must exist to connect a course site with it's
	* student roster(s) and other catalaog information.
	*/
	private final static String PROP_EXTERNAL_ID = "CTNG:external-id";
	
	//Names of state attributes corresponding to properties of a CTNG site
	/** %%% seems not to be used
	private final static String PROP_SITE_CONTACT_INSTITUTION = "CTNG:contact-institution";
	private final static String PROP_SITE_CONTACT_AFFILIATION = "CTNG:contact-affiliation";
	*/
	private final static String PROP_SITE_CONTACT_EMAIL = "CTNG:contact-email";
	private final static String PROP_SITE_CONTACT_NAME = "CTNG:contact-name";
	/** %%% seems not to be used
	private final static String PROP_SITE_CONTACT_HONORIFIC = "CTNG:contact-honorific";
	private final static String PROP_SITE_CONTACT_OFFICE = "CTNG:contact-office";
	private final static String PROP_SITE_CONTACT_PHONE = "CTNG:contact-phone";
	*/
	private final static String PROP_SITE_TYPE = "CTNG:site-type";
	private final static String PROP_SITE_TITLE = "CTNG:title";
	private final static String PROP_SITE_DESCRIPTION = "CTNG:description";
	private final static String PROP_SITE_SHORT_DESCRIPTION = "CTNG:short-description";
	private final static String PROP_SITE_TERM = "CTNG:term";
	private final static String PROP_SITE_SUBJECT = "CTNG:subject";
	private final static String PROP_SITE_COURSE = "CTNG:course";
	private final static String PROP_SITE_SECTIONS = "CTNG:sections"; // a character-delimited string of one or more sections
	/** %%% seems not to be used
	private final static String PROP_SITE_REUSE = "CTNG:reuse";
	private final static String PROP_SITE_RELATED_CLASS = "CTNG:related-class";
	private final static String PROP_SITE_RELATED_PROJECT = "CTNG:related-project";
	*/
	private final static String PROP_SITE_ICON_URL = "CTNG:site-icon-url";
	private final static String PROP_SITE_SITE_INFO_URL = "CTNG:site-info-url";
	private final static String PROP_SITE_SKIN = "CTNG:site-skin";
	private final static String PROP_SITE_ADDITIONAL = "CTNG:additional";
	public final static String PROP_SITE_INCLUDE = "CTNG:site-include";
	public final static String PROP_SITE_REQUEST_COURSE_SECTIONS = "site-request-course-sections";;
	
	// Names of academic terms
	// Note: These numbers are chronologically sequential and not UMIAC's "term" numbers
	private final static int FALL_2003 = 0;
	private final static int WINTER_2004 = 1;
	private final static int SPRING_2004 = 2;
	private final static int SPRING_SUMMER_2004 = 3;
	private final static int SUMMER_2004 = 4;
	private final static int FALL_2004 = 5;
	private final static int WINTER_2005 =6;
	
	// the vocabulary for semester display
	private final static String FALL_03_CODE = "F03";
	private final static String WINTER_04_CODE = "W04";
	private final static String SPRING_04_CODE = "Sp04";
	private final static String SPRING_SUMMER_04_CODE = "SpSu04";
	private final static String SUMMER_04_CODE = "Su04";
	private final static String FALL_04_CODE = "F04";
	private final static String WINTER_05_CODE ="W05";
	
	// %%% UMIAC lists for testing, remove
	private List FALL_2003_List = new Vector();
	private List WINTER_2004_List = new Vector();
	private List SPRING_2004_List = new Vector();
	private List SPRING_SUMMER_2004_List = new Vector();
	private List SUMMER_2004_List = new Vector();
	private List FALL_2004_List = new Vector();
	private List WINTER_2005_List = new Vector();
	
	/**
	*  CTNG ToolRegistration Site Types
	*
	*/
	public static final String SITE_TYPE_COURSE = "CTNG-course";
	public static final String SITE_TYPE_PROJECT = "CTNG-project";
	public static final String SITE_TYPE_MYWORKSPACE = "CTNG-MyWorkspace";
	public static final String SITE_TYPE_GRADTOOLS_STUDENT = "GradToolsStudent";
	public static final String SITE_TYPE_GRADTOOLS_RACKHAM = "GradToolsRackham";
	public static final String SITE_TYPE_GRADTOOLS_DEPARTMENT = "GradToolsDepartment";
	public static final String SITE_TYPE_WORKSHOP = "CTNG-workshop";
	public static final String SITE_TYPE_REQUESTED = "CTNG-requested";
	public static final String SITE_TYPE_UNDEFINED = "CTNG-undefined";
	
	/**
	*  The list of "standard" CTNG Site Types %%% this seems not to be used
	*
	public static final String[] SITE_TYPES =
	{
		SITE_TYPE_UNDEFINED, SITE_TYPE_REQUESTED, SITE_TYPE_COURSE,
		SITE_TYPE_GRADTOOLS_STUDENT, SITE_TYPE_GRADTOOLS_RACKHAM,SITE_TYPE_GRADTOOLS_DEPARTMENT,
		SITE_TYPE_PROJECT, SITE_TYPE_WORKSHOP
	};
	*/
	
	/** State variable and constant values for sorting site list */
	private static final String STATE_SORT_FIELD = "site.list.sort.field";
	private static final String SORT_FIELD_TITLE = "title";
	private static final String SORT_FIELD_DESCRIPTION = "description";
	private static final String SORT_FIELD_TYPE = "type";
	private static final String SORT_FIELD_OWNER = "owner";
	private static final String SORT_FIELD_TERM = "term";
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
	private static final String SORTED_BY_TERM = "term";
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
	private static final String SORTED_BY_PARTICIPANT_COURSE = "participant_course";
	private static final String SORTED_BY_PARTICIPANT_SECTION = "participant_section";
	private static final String SORTED_BY_PARTICIPANT_ID = "participant_id";
	private static final String SORTED_BY_PARTICIPANT_CREDITS = "participant_credits";
			
	/** Name of the state attribute holding the site list column to sort by */
	private static final String SORTED_ASC = "site.sort.asc";
	
	/** State attribute for list of sites to be deleted. */
	private static final String STATE_SITE_REMOVALS = "site.removals";
	
	/** Name of the state attribute holding the site list View selected */
	private static final String STATE_VIEW_SELECTED = "site.view.selected";
		
	/** the list of View selection options **/
	private final static String MY_PROJECT_SITES = "My Project Sites";
	private final static String MY_COURSE_SITES = "My Course Sites";
	private final static String ALL_MY_SITES = "All My Sites";
	private final static String MY_WORKSPACE = "My Workspace";
	
	/** Names of lists related to tools */
	private static final String STATE_TOOL_REGISTRATION_LIST = "toolRegistrationList"; 
	private static final String STATE_TOOL_REGISTRATION_SELECTED_LIST = "toolRegistrationSelectedList"; 
	private static final String STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST = "toolRegistrationOldSelectedList"; 
	private static final String STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME = "toolRegistrationOldSelectedHome"; 
	private static final String STATE_TOOL_EMAIL_ADDRESS = "toolEmailAddress";
	private static final String STATE_TOOL_HOME_SELECTED = "toolHomeSelected";
	private static final String STATE_COURSE_TOOL_LIST = "courseToolList";  
	private static final String STATE_PROJECT_TOOL_LIST = "projectToolList";
	/** %%% seems not to be used
	private static final String STATE_DISSERTATION_TOOL_LIST = "dissertationToolList";
	*/
	private static final String STATE_WORKSHOP_TOOL_LIST = "workshopToolList";
	private static final String STATE_MYWORKSPACE_TOOL_LIST = "myworkspaceToolList";
	private static final String STATE_GRADTOOLS_STUDENT_TOOL_LIST = "gradToolsStudentToolList";
	private static final String STATE_GRADTOOLS_RACKHAM_TOOL_LIST = "gradToolsRackhamToolList";
	private static final String STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST = "gradToolsDepartmentToolList";
	private static final String STATE_UNDEFINED_TOOL_LIST = "undefinedToolList";
	private final static String STATE_TOOL_LIST = "toolList";
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
	private final static String AFFILIATION_LIST = "affiliationList"; // the list of U-M university affiliations
	private final static String COURSE_LIST = "courseList"; // the U-M course catalog listing
	private final static String HONORIFIC_LIST = "honorificList"; // the form of address preferred by site contact
	private final static String RELATED_PROJECT_LIST = "relatedProjectList";
	private final static String RELATED_CLASS_LIST = "relatedClassList";
	private final static String TERM_LIST = "termList"; // the list of U-M academic terms
	private final static String SECTION_LIST = "sectionList"; // the list of sections granted access to a CTNG site
	private final static String SITE_SECTION_LIST = "siteSectionList";
	private final static String STATE_UNPUBLISHED_SITES_LIST = "unpublishedSitesList"; // completed but unpublished editable sites
	private final static String STATE_PUBLISHED_SITES_LIST = "publishedSitesList"; // completed and published editable sites
	private final static String STATE_REQUESTED_SITES_LIST = "requestedSitesList"; // a manual entry of subject, course, and section leads toa requested site
	private final static String STATE_CURRENT_TERM_SITES_LIST = "currentTermSitesList"; // a list for navigation of sites
	private final static String STATE_OTHER_SITES_LIST = "otherSitesList"; // a list for navigation of sites
	private static final String STATE_SITE_LIST = "site.list"; /** Name of state attribute for the list of all editable sites  */
	private final static String SUBJECT_LIST = "subjectList"; // the list of U-M academic subjects
	private final static String SUBJECT_TITLE_LIST = "subjectTitleList";
	private final static String STATE_WORKSITE_SETUP_PAGE_LIST = "wSetupPageList"; // the list of site pages consistent with Worksite Setup page patterns
	private final static String STATE_OFF_PATTERN_PAGE_LIST = "offPatternPageList"; //the list of site pages that are not consistent with Worksite Setup page patterns
	private final static String STATE_TERM_COURSE_SECTION_LIST = "termCourseSectionList"; // the list of sections of the specified term having this person as Instructor
	
	// Name for state attribute objects
	private final static String STATE_PROFILE = "profile";
	private final static String STATE_CLASS_ROSTER = "site.class.roster";
	private final static String STATE_STUDENTS_TO_SORT = "site.students.sort";
	private final static String STATE_SUBJECT_AFFILIATES = "site.subject.affiliates";
	
	/** State attributes names for requested site course information fields */
	private final static String STATE_TERM_REQUEST = "site.term.request";
	private final static String STATE_SUBJECT_REQUEST = "site.subject.request";
	private final static String STATE_COURSE_REQUEST = "site.course.request";
	private final static String STATE_SECTION_REQUEST = "stite.section.equest";
	
	/** State attributes names for manual course creation */
	private final static String STATE_MANUAL_ADD_COURSE_NUMBER = "manual_add_course_number";
	private final static String STATE_MANUAL_ADD_COURSE_SUBJECTS = "manual_add_course_subjects";
	private final static String STATE_MANUAL_ADD_COURSE_COURSES = "manual_add_course_courses";
	private final static String STATE_MANUAL_ADD_COURSE_SECTIONS = "manual_add_course_";
	
	/** The name of the state form field containing additional information for a course request */
	private static final String FORM_ADDITIONAL = "form.additional";
	
	/** %%% in transition from putting all form variables in state to putting profile and siteinfo aggregates in state */
	private final static String FORM_TITLE = "form_title";
	private final static String FORM_INCLUDE = "form_include";
	private final static String FORM_DESCRIPTION = "form_description";
	private final static String FORM_AFFILIATION = "form_affiliation";
	private final static String FORM_AFFILIATION_TEXT = "form_affiliation_text";
	private final static String FORM_HONORIFIC = "form_honorific";
	private final static String FORM_INSTITUTION = "form_institution";
	private final static String FORM_SUBJECT = "form_subject";
	private final static String FORM_COURSE = "form_course";
	private final static String FORM_SECTION = "form_section";
	private final static String FORM_OFFICE = "form_office";
	private final static String FORM_PHONE = "form_phone";
	private final static String FORM_EMAIL = "form_email";
	private final static String FORM_TERM = "form_term";
	private final static String FORM_REUSE = "form_reuse";
	private final static String FORM_RELATED_CLASS = "form_related_class";
	private final static String FORM_RELATED_PROJECT = "form_related_project";
	private final static String FORM_NAME = "form_name";
	private final static String FORM_PERSON = "form_person";
	private final static String FORM_SHORT_DESCRIPTION = "form_short_description";
	private final static String FORM_SELECTED = "form_selected";
	private final static String FORM_UMID = "form_umid";
	/** site info edit form variables */
	private final static String FORM_SITEINFO_TITLE = "siteinfo_title";
	private final static String FORM_SITEINFO_TERM = "siteinfo_term";
	private final static String FORM_SITEINFO_DESCRIPTION = "siteinfo_description";
	private final static String FORM_SITEINFO_SHORT_DESCRIPTION = "siteinfo_short_description";
	private final static String FORM_SITEINFO_SKIN = "siteinfo_skin";
	private final static String FORM_SITEINFO_INCLUDE = "siteinfo_include";
	private final static String FORM_SITEINFO_CONTACT_NAME = "siteinfo_contact_name";
	private final static String FORM_SITEINFO_CONTACT_EMAIL = "siteinfo_contact_email";
	
	private final static String FORM_WILL_NOTIFY = "form_will_notify";
	
	/** Context action */
	private static final String CONTEXT_ACTION = "SiteAction";
	
	/** The number of templates in a new site template sequence*/
	private static final int NUMBER_OF_TEMPLATES = 5;
	
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
	
	/** The name of the state attribute whose value is the Term selected from the courseClass drop down list */
	private final static String STATE_TERM_SELECTED = "stateTermSelected";

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
	private static final String NO_STATE_PROFILE_STRING = "NO_STATE_PROFILE";
	
	/** The alert message shown when Revise... has been clicked but more than one site was checked */
	private static final String MORE_THAN_ONE_SITE_SELECTED_STRING = "Please check only one site to Revise... at a time";
	
	/** The state attribute alerting user of a sent course request */
	private static final String REQUEST_SENT = "site.request.sent";
	
	/** The state attributes in the make public vm */
	private static final String STATE_JOINABLE = "state_joinable";
	private static final String STATE_JOINERROLE = "state_joinerRole";
	
	/** Invalid email address warning */
	private static final String INVALID_EMAIL = "The Email id must be made up of alpha numeric characters or any of !#$&'*+-=?^_`{|}~. (no spaces).";
	
	/** Whether the instructor choosed manual add after entering the auto add page */
	private static final String STATE_MANUAL_ADD = "state_manual_add";
	private static final String STATE_AUTO_ADD = "state_auto_add";
	private static final String STATE_AUTO_MANUAL_ADD = "state_auto_manual_add";
	
	/** the list of selected user */
	private static final String STATE_SELECTED_USER_LIST = "state_selected_user_list";
	
	/** is the admin type user access the tool for the first time */
	private static final String ADMIN_AND_FIRST_USE = "admin_and_first_use";
	
	/** default term number */
	private static final String STATE_CURRENT_TERM = "5";
	
	/** production site name */
	private static final String PRODUCTION_SITE_NAME = "CTools";
	private static final String PRODUCTION_SITE_URL = "http://ctools.umich.edu";
	/** university name */
	private static final String UNIVERSITY_NAME = "UM";
	private static final String UNIVERSITY_FULL_NAME = "University of Michigan";
	/** url for applying friend account */
	private static final String APPLY_FRIEND_ACCOUNT_URL = "http://weblogin.umich.edu/friend";
	
	/** role description */
	private Hashtable ROLE_DESCRIPTION = new Hashtable();
	private static final String STATE_SELECTED_PARTICIPANT_ROLES = "state_selected_participant_roles";
	private static final String STATE_SELECTED_PARTICIPANTS = "state_selected_participants";
	private static final String STATE_PARTICIPANT_LIST = "state_participant_list";
	private static final String STATE_ADD_PARTICIPANTS = "state_add_participants";
	
	/** for changing participant roles*/
	private static final String STATE_CHANGEROLE_SAMEROLE = "state_changerole_samerole";
	private static final String STATE_CHANGEROLE_SAMEROLE_ROLE = "state_changerole_samerole_role";
	
	/** for add/remove classes */
	private static final String STATE_ADD_CLASS_UMIAC = "state_add_class_umiac";
	private static final String STATE_ADD_CLASS_MANUAL = "state_add_class_manual";
	private static final String STATE_REMOVE_CLASS_UMIAC = "state_remove_class_umiac";
	private static final String STATE_REMOVE_CLASS_MANUAL = "state_remove_class_manual";
	private static final String STATE_REMOVEABLE_USER_LIST = "state_removeable_user_list";
	private static final String STATE_UNREMOVEABLE_USER_LIST = "state_unremoveable_user_list";
	
	/**
	* Populate the state object, if needed.
	* @param state The state object.
	* @param portlet The portlet.
	* @param rundata The current request's rundata.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);
		
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
		
		// populate the role definition
		if (ROLE_DESCRIPTION.size() == 0)
		{
			ROLE_DESCRIPTION.put("Affiliate", "Can read, add, and revise content on sites in a particular department");
			ROLE_DESCRIPTION.put("Assistant", "Can read, add, and revise most content on the site");
			ROLE_DESCRIPTION.put("Candidate", "Can create and add participants to a Grad Tools site");
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
		state.removeAttribute(STATE_PROFILE);
		state.removeAttribute(STATE_SITE_INFO);
		state.removeAttribute(STATE_SITE_TYPE);
		state.removeAttribute("participantToAdd");
		state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
		state.removeAttribute(STATE_TOOL_EMAIL_ADDRESS);
		state.removeAttribute(STATE_TOOL_HOME_SELECTED);
		state.removeAttribute(STATE_SELECTED_USER_LIST);
		state.removeAttribute(STATE_TERM_COURSE_SECTION_LIST);
		state.removeAttribute("umiacCourseSectionList");
		state.removeAttribute("manualCourseSectionList");
		state.removeAttribute("selectedUmiacCourseSection");
		state.removeAttribute(STATE_TERM_REQUEST);
		state.removeAttribute(STATE_SUBJECT_REQUEST);
		state.removeAttribute(STATE_COURSE_REQUEST);
		state.removeAttribute(STATE_SECTION_REQUEST);
		state.removeAttribute(STATE_SITE_TOOL_LIST);
		state.removeAttribute(STATE_TOOL_LIST);
		state.removeAttribute(STATE_FEATURE_LIST);
		state.removeAttribute(FEATURE_LIST);
		state.removeAttribute(STATE_JOINABLE);
		state.removeAttribute(STATE_JOINERROLE);
		state.removeAttribute(STATE_MANUAL_ADD);
		state.removeAttribute(STATE_AUTO_MANUAL_ADD);
		state.setAttribute(STATE_TERM_SELECTED, STATE_CURRENT_TERM);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_NUMBER);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_COURSES);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS);
		state.removeAttribute(STATE_NEWS_TITLES);
		state.removeAttribute(STATE_WEB_CONTENT_TITLES);
		state.removeAttribute(STATE_COURSE_TOOL_LIST);
		state.removeAttribute(STATE_PROJECT_TOOL_LIST);
		state.removeAttribute(STATE_SITE_QUEST_UNIQNAME);
		state.removeAttribute(STATE_ADD_CLASS_UMIAC);
		setupToolLists(state);
	}	// cleanState
	
	private List getTermCourseSectionList(SessionState state, ParameterParser params)
	{
		// The sections of the specified term having this person as Instructor
		List termCourseSectionList = new Vector();
		
		// "Official" courses previously selected for access to site
		List umiacCourseSectionList = (Vector)state.getAttribute("umiacCourseSectionList");
		
		CourseListItem item = new CourseListItem();
		CourseListItem cli = new CourseListItem();
		boolean add = true;
		
		int term = -1;
		String umiac_uniqname = StringUtil.trimToZero(UsageSessionService.getSessionUserId());
		String umiac_year = "";
		String umiac_term = "";
		
		SiteInfo siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
		Integer form_term = new Integer(siteInfo.term);
		
		// If the session user selects a different term, clear courses belonging to the previous term
		boolean term_changed = false;
		if (params.getString("selectTerm") != null)
		{
			term = Integer.parseInt(params.getString("selectTerm"));
			if ((String)state.getAttribute(STATE_TERM_SELECTED) != null)
			{
				// if there is a previous selected term
				int termInState = Integer.parseInt((String)state.getAttribute(STATE_TERM_SELECTED));
				term_changed = term == termInState ? false: true;
				if (term_changed)
				{
					state.setAttribute(STATE_TERM_SELECTED, params.getString("selectTerm"));
				}
			}
			else
			{
				// if there is no previous selected term
				state.setAttribute(STATE_TERM_SELECTED, params.getString("selectTerm"));
			}
			state.setAttribute(STATE_TERM_REQUEST, Integer.toString(term));
			siteInfo.term = term;
			state.setAttribute(STATE_SITE_INFO, siteInfo);
			updateSiteInfo(params, state);
		}
		
		int template_index = Integer.parseInt((String)state.getAttribute(STATE_TEMPLATE_INDEX));
		if (term_changed)
		{
			List umiacListCleared = (Vector) state.getAttribute("umiacCourseSectionList");
			List manualListCleared = (Vector) state.getAttribute("manualCourseSectionList");
			// Clear courses if term changes and it is a new site
			if ((umiacListCleared != null) && (template_index == 3))
			{
				umiacListCleared.clear();
				state.setAttribute("umiacCourseSectionList", umiacListCleared);
			}
			if ((manualListCleared != null)  && (template_index == 3))
			{
				manualListCleared.clear();
				state.setAttribute("manualCourseSectionList", manualListCleared);
			}
			state.setAttribute(STATE_TERM_SELECTED, Integer.toString(term));
		}
		
		UmiacClient uClient = UmiacClient.getInstance();
		
		// is there a valid UmiacClient?
		if (uClient != null && StringUtil.trimToNull(uClient.getHost()) != null && uClient.getPort() > 0)
		{
			/** 
			* Build query parameters for UMIAC
			* Note: the "term" variable below is a chronologically sequential ordering 
			*	and not a UMIAC "term_id". UMIAC "term_id" is expressed as a single 
			*	digit number:
			*	1 - SUMMER
			*	2 - FALL
			*	3 - WINTER
			*	4 - SPRING
			*	5 - SPRING/SUMMER
			*/
			switch(term)
			{
				case 0:
					// Fall, 2003: 2, 2003
					//
					umiac_term = "2";
					umiac_year = "2003";
					break;
				case 1:
					// Winter, 2004: 3, 2003
					//
					umiac_term = "3";
					umiac_year = "2004";
					break;
				case 2:
					// Spring, 2004: 4, 2004
					//
					umiac_term = "4";
					umiac_year = "2004";
					break;
				case 3:
					// Spring/Summer 2004: 5, 2004
					//
					umiac_term = "5";
					umiac_year = "2004";
					break;
				case 4:
					// Summer, 2004: 1, 2004
					//
					umiac_term = "1";
					umiac_year = "2004";
					break;
				case 5:
					// Fall, 2004: 2, 2004
					//
					umiac_term = "2";
					umiac_year = "2004";
					break;
				case 6:
					// Winter, 2005: 3, 2005
					umiac_term = "3";
					umiac_year = "2005";
			}
			
			try
			{
				/*
				*	Send UMIAC query for sections (including cross-lists) for this term and Instructor
				*	getInstructorSections returns 12 strings: year, term_id, campus_code, 
				*	subject, catalog_nbr, class_section, title, url, component, role, 
				*	subrole, "CL" if cross-listed, blank if not
				*/
				for (ListIterator i = uClient.getInstructorSections(umiac_uniqname, umiac_year, umiac_term).listIterator(); i.hasNext(); )
				{
					String[] res = (String[]) i.next();
					item = buildCourseListItem(state, res);
					//For chef_site-courseClass.vm, display all Instructor sections with check boxes
					if(STATE_TEMPLATE_INDEX.equals("3"))
					{
						termCourseSectionList.add(item);
					}
					else
					{
						//For chef_site-addRemoveSection.vm, display only those sections that haven't been selected yet
						add = true;
						for (int j = 0; j < umiacCourseSectionList.size(); j++)
						{
							cli = (CourseListItem)umiacCourseSectionList.get(j);
							if (cli.getKey().equals(item.getKey())) add = false;
						}
						if (add) 
						{
							termCourseSectionList.add(item);
						}
					}
				}
			}
			catch (IdUnusedException ignore)
			{
				//no results
				termCourseSectionList.clear();
			}
		}	// if
		
		state.setAttribute(STATE_TERM_COURSE_SECTION_LIST, termCourseSectionList);
		
		return termCourseSectionList;
		
	}	// getTermCourseSectionList
	
	/**
	* buildInstructorSectionsList
	* Build the CourseListItem list for this Instructor for the requested Term
	*
	*/
	private void buildInstructorSectionsList(SessionState state, ParameterParser params, Context context)
	{
		//Site information
		// The sections of the specified term having this person as Instructor
		Vector termList = (Vector) state.getAttribute(TERM_LIST);
		context.put ("umiacCourseSectionList", state.getAttribute("umiacCourseSectionList"));
		context.put ("manualCourseSectionList", state.getAttribute("manualCourseSectionList"));
		context.put (FORM_TERM, new Integer((String)state.getAttribute(STATE_TERM_SELECTED)));
		context.put (TERM_LIST, termList);
		context.put(STATE_TERM_COURSE_SECTION_LIST, (List) state.getAttribute(STATE_TERM_COURSE_SECTION_LIST));
		
	} // buildInstructorSectionsList
	
	/**
	* Implement this to return a list of all the resources that there are to page.
	* Sort them as appropriate, and apply search criteria.
	*/
	protected List readAllResources(SessionState state)
	{
		// the hashtable contains term strings
		Hashtable terms = new Hashtable();
		terms.put("0", FALL_03_CODE);
		terms.put("1", WINTER_04_CODE);
		terms.put("2", SPRING_04_CODE);
		terms.put("3", SPRING_SUMMER_04_CODE);
		terms.put("4", SUMMER_04_CODE);
		terms.put("5", FALL_04_CODE);
		terms.put("6", WINTER_05_CODE);
				
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
					//default sort is by
					// 1. myworkspace 
					// 2. current term courses, 
					// 3. dissertation site (if applicable),  
					// 4. projects, 
					// 5. then previous semester classes sorted in groups by term 
					// Alphabetical within each of these groupings
					Vector currentTermCourseSites = new Vector();
					Vector dissertationSites = new Vector();
					Vector projectSites = new Vector();
					Vector previousCoursesSites = new Vector();
					Vector myworkspaceSite = new Vector();
					Vector otherSites = new Vector();
					Vector allSites = new Vector();
					// sort the whole site list by title ascending first
					sortedSites = new SortedIterator (sites.iterator (), new SiteComparator (SORTED_BY_TITLE, Boolean.TRUE.toString()));
					while (sortedSites.hasNext())
					{
						Site site = (Site) sortedSites.next();
						ResourceProperties properties = site.getProperties();
						String type = properties.getProperty(PROP_SITE_TYPE);
						if (type != null) 
						{
							if(type.equals(SITE_TYPE_COURSE) && properties.getProperty(PROP_SITE_TERM).equals(STATE_CURRENT_TERM))
							{
								// current course site first
								currentTermCourseSites.add(site);
							}
							else if(type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
							{
								// dissertation site second
								dissertationSites.add(site);
							}
							else if(type.equals(SITE_TYPE_PROJECT))
							{
								// project sites
								projectSites.add(site);
							}
							else if (type.equals(SITE_TYPE_MYWORKSPACE))
							{
								// my workspace site
								myworkspaceSite.add(site);
							}
							else if (type.equals(SITE_TYPE_COURSE))
							{
								// previous terms courses
								previousCoursesSites.add(site);
							}
							else
							{
								// otherwise
								otherSites.add(site);
							}
						}
						else
						{
							// other types of sites
							otherSites.add(site);
						}
					}
					
					// sort the previous terms course sites by term
					SortedIterator sortedPreviousCourseSites = new SortedIterator (previousCoursesSites.iterator (), new SiteComparator (SORTED_BY_TERM, Boolean.FALSE.toString()));
					
					
					// concat all the sites to one list
					allSites.addAll(myworkspaceSite);
					allSites.addAll(currentTermCourseSites);
					allSites.addAll(dissertationSites);
					allSites.addAll(projectSites);
					while (sortedPreviousCourseSites.hasNext())
					{
						allSites.add(sortedPreviousCourseSites.next());
					}
					allSites.addAll(otherSites);
					
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
						
						// filtering if searching on worksite title, owner, description, course term
						if (StringUtil.containsIgnoreCase(site.getTitle(),search) || 
							StringUtil.containsIgnoreCase(sProperties.getProperty(PROP_SITE_SHORT_DESCRIPTION),search) || 
							StringUtil.containsIgnoreCase(sProperties.getProperty(ResourceProperties.PROP_CREATOR),search)||
							(sProperties.getProperty(PROP_SITE_TYPE) != null && 
							sProperties.getProperty(PROP_SITE_TYPE).equalsIgnoreCase(SITE_TYPE_COURSE) &&
							sProperties.getProperty(PROP_SITE_TERM) != null &&
							StringUtil.containsIgnoreCase((String) terms.get(sProperties.getProperty(PROP_SITE_TERM)), search)))
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
		else if (((String)state.getAttribute(STATE_TEMPLATE_INDEX)).equals("35"))
		{
			Vector students = (Vector) state.getAttribute(STATE_STUDENTS_TO_SORT );
			
			//if called from the roster list page
			String sortedBy = (String) state.getAttribute (SORTED_BY);
			String sortedAsc = (String) state.getAttribute (SORTED_ASC);
		
			SortedIterator sortedStudents = new SortedIterator (students.iterator (), new StudentComparator (sortedBy, sortedAsc));
		
			String search = (String) state.getAttribute(STATE_SEARCH);
		
			while (sortedStudents.hasNext())
			{
				Student student = (Student) sortedStudents.next();
				if (search != null)
				{
					// filtering if searching on worksite title, owner, description
					if (StringUtil.containsIgnoreCase(student.getName(),search) || 
						StringUtil.containsIgnoreCase(student.getUniqname(),search) || 
						StringUtil.containsIgnoreCase(student.getId(),search) ||
						StringUtil.containsIgnoreCase(student.getLevel(),search) ||
						StringUtil.containsIgnoreCase(student.getCredits(),search))
					{
						rv.add(student);
					}
				}	
				else
				{
					rv.add(student);
				}
			}
		} //STATE_TEMPLATE_INDEX = 35
		
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
		Profile profile = (Profile) state.getAttribute(STATE_PROFILE);
		
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
		// "Official" courses from UMIAC
		List umiacCourseSectionList = (Vector)state.getAttribute("umiacCourseSectionList");
				
		//used with My Worspace tool configuration
		List currentTools = new Vector();
		List currentPages = new Vector();

		List studentList = new Vector();
		List termList = (Vector) state.getAttribute(TERM_LIST);
		
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
				  
				List views = new Vector();
				views.add("All My Sites");
				views.add("My Project Sites");
				views.add("My Course Sites");
				views.add("My Workspace");
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
				
				// the hashtable contains term strings
				Hashtable terms = new Hashtable();
				terms.put("0", FALL_03_CODE);
				terms.put("1", WINTER_04_CODE);
				terms.put("2", SPRING_04_CODE);
				terms.put("3", SPRING_SUMMER_04_CODE);
				terms.put("4", SUMMER_04_CODE);
				terms.put("5", FALL_04_CODE);
				terms.put("6", WINTER_05_CODE);
				context.put("termsTable", terms);
				
				Menu bar2 = new Menu(portlet, data, (String) state.getAttribute(STATE_ACTION));
				// add the search commands
				addSearchMenus(bar2, state);
				context.put("menu2", bar2);
				
				return (String)getContext(data).get("template") + TEMPLATE[0];
			case 1: 
				/*  buildContextForTemplate chef_site-type.vm
				*	
				*/
				try
				{
					//the Grad Tools site option is only presented to UM grad students
					String userId = StringUtil.trimToZero(UsageSessionService.getSessionUserId());
					
					//am I a grad student?
					Boolean isGradStudent = new Boolean(DissertationService.getIsCandidate(userId));
					context.put("isGradStudent", isGradStudent);
					
					//if I am a grad student, do I already have a Grad Tools site?
					boolean noGradToolsSite = true;
					if(isGradStudent.booleanValue())
					{
						List allSites = SiteService.getAllowedSites(false, true, SITE_TYPE_GRADTOOLS_STUDENT);
						if(allSites.size()!= 0)
							noGradToolsSite = false;
					}
					context.put("noGradToolsSite", new Boolean(noGradToolsSite));
				}
				catch(Exception e)
				{
					if(Log.isWarnEnabled())
					{
						Log.warn("chef", this + ".buildContextForTemplate chef_site-type.vm " + e);
					}
				}
				context.put("form_type", siteInfo.site_type);
				context.put("termList", termList);
				
				// Clean out site in state, if there is one (i.e., we went Back), before creating a new one
				cleanState(state);
				context.put("selectedTerm", new Integer((String)state.getAttribute(STATE_TERM_SELECTED)));
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
				buildInstructorSectionsList(state, params, context); // shared with case 18
				if (state.getAttribute(STATE_SITE_INSTANCE) != null)
				{
					if (state.getAttribute(STATE_ADD_CLASS_UMIAC) != null)
					{
						List l = (List) state.getAttribute(STATE_ADD_CLASS_UMIAC);
						context.put("selectedUmiacCourseSection", l);
					}
					context.put("siteEdit", Boolean.TRUE);
					context.put("siteTitle", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				}
				else
				{
					context.put("siteEdit", Boolean.FALSE);
					if (state.getAttribute("selectedUmiacCourseSection") != null)
					{
						List l = (List) state.getAttribute("selectedUmiacCourseSection");
						context.put("selectedUmiacCourseSection", l);
					}
				}
				if (state.getAttribute(STATE_MANUAL_ADD) != null)
				{
					context.put("manualAdd", state.getAttribute(STATE_MANUAL_ADD));
				}		
				if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITESETUP))
				{
					context.put("backIndex", "1");
				}	
				else if (((String) state.getAttribute(STATE_SITE_MODE)).equalsIgnoreCase(SITE_MODE_SITEINFO))
				{	
					context.put("backIndex", "59");
				}
				return (String)getContext(data).get("template") + TEMPLATE[3];
			case 4:
				/*   buildContextForTemplate chef_site-courseFeatures.vm
				* 
				*/
				if (state.getAttribute(STATE_AUTO_MANUAL_ADD) != null)
				{
					context.put("from_auto_manual", state.getAttribute(STATE_AUTO_MANUAL_ADD));
				}
				else
				{
					context.put("from_auto_manual", Boolean.FALSE);
				}
				toolRegistrationList = (Vector) state.getAttribute(STATE_COURSE_TOOL_LIST);
				toolRegistrationSelectedList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);	
				context.put (STATE_TOOL_REGISTRATION_SELECTED_LIST, toolRegistrationSelectedList); // String toolId's
				context.put (STATE_TOOL_REGISTRATION_LIST, toolRegistrationList );
				String emailId = (String) state.getAttribute(STATE_TOOL_EMAIL_ADDRESS);
				if (emailId != null)
				{
					context.put("emailId", emailId);
				}
				context.put("serverName", ServerConfigurationService.getServerName());
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
				context.put("title", siteInfo.title);
				if (state.getAttribute(STATE_AUTO_ADD) != null)
				{
					context.put("autoAdd", state.getAttribute(STATE_AUTO_ADD));
				}
				// titles for news tools
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
				return (String)getContext(data).get("template") + TEMPLATE[4];
			case 5:
				/*  buildContextForTemplate chef_site-dissertationContact.vm
				*	Not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[5];
			case 6: 
				/*  buildContextForTemplate chef_site-dissertationProgram.vm
				*	Not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[6];
			case 7: 
				/*  buildContextForTemplate chef_site-dissertationFeatures.vm 
				*	Not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[7];
			case 8: 
				/*  buildContextForTemplate chef_site-projectContact.vm
				*	Not implemented
				*/
				return (String)getContext(data).get("template") + TEMPLATE[8];
			case 9: 
				/*   buildContextForTemplate chef_site-projectInformation.vm 
				* 
				*/ 
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
				/*   buildContextForTemplate chef_site-projectFeatures.vm
				* 
				*/
				toolRegistrationList = (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST);
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
				if (state.getAttribute(STATE_AUTO_MANUAL_ADD) != null)
				{
					context.put("from_auto_manual", state.getAttribute(STATE_AUTO_MANUAL_ADD));
				}
				else
				{
					context.put("from_auto_manual", Boolean.FALSE);
				}
				buildInstructorSectionsList(state, params, context); // shared with case 18
				// buildInstructorSectionsList puts in context
				context.put("form_subject", siteInfo.subject);
				context.put("form_course", siteInfo.course);
				context.put("form_section", siteInfo.sections);
				context.put("form_additional", siteInfo.additional);
				context.put("form_description", siteInfo.description);
				context.put("form_short_description", siteInfo.short_description);
				context.put("form_skin", siteInfo.skin);
				
				// defalt the site contact person to the site creator
				if (siteInfo.site_contact_name.equals(NULL_STRING) && siteInfo.site_contact_email.equals(NULL_STRING))
				{
					User user = UserDirectoryService.getCurrentUser();
					siteInfo.site_contact_name = user.getDisplayName();
					siteInfo.site_contact_email = user.getEmail();
				}
				context.put("form_site_contact_name", siteInfo.site_contact_name);
				context.put("form_site_contact_email", siteInfo.site_contact_email);
				context.put("skins", state.getAttribute("skins"));
				context.put("title", siteInfo.title);
				context.put("selectedUmiacCourseSection", state.getAttribute("selectedUmiacCourseSection"));
				if (state.getAttribute(STATE_MANUAL_ADD) != null && ((Boolean) state.getAttribute(STATE_MANUAL_ADD)).booleanValue())
				{
					context.put("fromManualAdd", Boolean.TRUE);
					int number = ((Integer) state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER)).intValue();
					context.put("manualAddNumber", new Integer(number - 1));
					context.put("manualAddSubjects", state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS));
					context.put("manualAddCourses", state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES));
					context.put("manualAddSections", state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS));
					context.put("back", "39");
				}
				else
				{
					context.put("fromManualAdd", Boolean.FALSE);
					context.put("back", "3");
				}
				if (state.getAttribute(STATE_AUTO_ADD) != null)
				{
					context.put("autoAdd", state.getAttribute(STATE_AUTO_ADD));
				}
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
				String type = ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getProperties().getProperty(PROP_SITE_TYPE);
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
				context.put("form_title", siteInfo.title);
				context.put("form_description", siteInfo.description);
				context.put("form_short_description", siteInfo.short_description);
				if (siteInfo.site_type.equals(SITE_TYPE_PROJECT))
				{
					context.put("form_projectsite", Boolean.TRUE);
					context.put("form_include", new Boolean(siteInfo.include));
				}
				if (siteInfo.site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
				{
					context.put("form_gradtoolssite", Boolean.TRUE);
				}
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
				* 
				*/
				buildInstructorSectionsList(state, params, context);
				// buildInstructorSectionsList puts in context
				context.put("form_term", state.getAttribute(STATE_TERM_SELECTED));
				context.put("form_additional", siteInfo.additional);
				context.put("value_uniqname", state.getAttribute(STATE_SITE_QUEST_UNIQNAME));
				int number = 1;
				if (state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER) != null)
				{
					number = ((Integer) state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER)).intValue();
					context.put("currentNumber", new Integer(number));
				}
				context.put("currentNumber", new Integer(number));
				context.put("listSize", new Integer(number-1));
				context.put("subjects", state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS));
				context.put("courses", state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES));
				context.put("sections", state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS));
				if (state.getAttribute(STATE_ADD_CLASS_UMIAC) != null)
				{
					List l = (List) state.getAttribute(STATE_ADD_CLASS_UMIAC);
					context.put("selectedUmiacCourseSection", l);
					context.put("backIndex", "3");
				}
				else
				{
					context.put("backIndex", "59");
				}
				context.put("siteTitle", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
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
				type = site.getProperties().getProperty(PROP_SITE_TYPE);					
				boolean course_site = (type!=null && type.equals(SITE_TYPE_COURSE)) ? true: false;
				boolean project_site = (type!=null && type.equals(SITE_TYPE_PROJECT))? true: false;
				boolean gradtools_student_site = (type!=null && type.equals(SITE_TYPE_GRADTOOLS_STUDENT)) ? true: false;
				boolean gradtools_rackham_site = (type!=null && type.equals(SITE_TYPE_GRADTOOLS_RACKHAM)) ? true: false;
				boolean gradtools_department_site = (type!=null && type.equals(SITE_TYPE_GRADTOOLS_DEPARTMENT)) ? true: false;
				boolean myworkspace_site = false;
				if (SiteService.isUserSite(site.getId()) || (type!=null && type.equals(SITE_TYPE_MYWORKSPACE)))
				{
					myworkspace_site = true;
				}
				boolean undefined_site = (type!=null && type.equals(SITE_TYPE_UNDEFINED)) ? true: false;		
				
				// Put up tool lists filtered by category
				String listVariable = NULL_STRING;
				String courseOrProject = NULL_STRING;
				if(course_site)
				{
					listVariable = STATE_COURSE_TOOL_LIST;
					courseOrProject = "course";
					context.put ("course_site", Boolean.TRUE.toString());
				}
				else if (project_site)
				{
					listVariable = STATE_PROJECT_TOOL_LIST;
					courseOrProject = "project";
					context.put ("project_site", Boolean.TRUE.toString());
				}
				else if (gradtools_student_site)
				{
					listVariable = STATE_GRADTOOLS_STUDENT_TOOL_LIST;
					courseOrProject = "gradtools_student";
					context.put ("gradtools_student_site", Boolean.TRUE.toString());
				}
				else if (gradtools_rackham_site)
				{
					listVariable = STATE_GRADTOOLS_RACKHAM_TOOL_LIST;
					courseOrProject = "gradtools_rackham";
					context.put ("gradtools_rackham_site", Boolean.TRUE.toString());
				}
				else if (gradtools_department_site)
				{
					listVariable = STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST;
					courseOrProject = "gradtools_department";
					context.put ("gradtools_department_site", Boolean.TRUE.toString());
				}
				else if (myworkspace_site)
				{
					listVariable = STATE_MYWORKSPACE_TOOL_LIST;
					courseOrProject = "myworkspace";
					context.put ("myworkspace_site", Boolean.TRUE.toString());
				}
				else if (undefined_site)
				{
					listVariable = STATE_UNDEFINED_TOOL_LIST;
					courseOrProject = "undefined";
				}
				else
				{
					Log.warn("chef", "SiteAction.buildContextForTemplate, case 21: - unknown STATE_SITE_TYPE");
				}
				context.put("courseOrProject", courseOrProject);
				
				//see if wSetupPageList contains Home
				boolean check_home = false;
				boolean hasNews = false;
				boolean hasWebContent = false;
				List wSetupPages = (Vector)state.getAttribute(STATE_WORKSITE_SETUP_PAGE_LIST);

				context.put(STATE_TOOL_REGISTRATION_SELECTED_LIST, state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST));
				
				context.put("newsTitles", (Hashtable) state.getAttribute(STATE_NEWS_TITLES));
				context.put("wcTitles", (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES));
				
				context.put (STATE_TOOL_REGISTRATION_LIST, state.getAttribute(listVariable));
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
				if(((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
				{
					context.put ("course_site", Boolean.TRUE.toString());
				}
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
				//rosterMenu.add( new MenuEntry("Sort...", "doMenu_roster_sort"));
				context.put("menu", rosterMenu);

				// get students
				studentList.addAll(roster(state, umiacCourseSectionList));
				participantList = getParticipantList(state);
				context.put("participantList", participantList);
				context.put("studentList", studentList);
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				if (siteInfo.site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
				{
					context.put("form_gradtoolssite", Boolean.TRUE);
				}
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
				if(((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
				{
					context.put ("course_site", Boolean.TRUE);
				}
				else
				{
					context.put ("course_site", Boolean.FALSE);
				}
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
				context.put("university", UNIVERSITY_NAME);
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
						
						List unremoveableList = (List) state.getAttribute(STATE_UNREMOVEABLE_USER_LIST);
						List unremoveableParticipant = new Vector();
						
						for (int k = 0; k < unremoveableList.size(); k++)
						{
							User user = UserDirectoryService.getUser((String) unremoveableList.get(k));
							Participant participant = new Participant();
							participant.name = user.getSortName();
							participant.uniqname = user.getId();
							Set uRoles = realm.getUserRoles(user);
							participant.roles = uRoles;
							unremoveableParticipant.add(participant);
						}
						context.put("unremoveableList", unremoveableParticipant);
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
				/*  buildContextForTemplate chef_site-confirmSiteDelete.vm
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
									Log.warn("chef", "SiteAction.buildContextForTemplate chef_site-confirmSiteDelete.vm: IdUnusedException");	
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
				* 
				*/
				Vector students_to_sort = new Vector();
				students_to_sort.addAll(roster(state, umiacCourseSectionList));
				
				//add participants to the students to be sorted
				participantList = getParticipantList(state);
				for (ListIterator i = participantList.listIterator(); i.hasNext(); )
				{
					Participant p = (Participant) i.next();
					Student s = new Student();
					s.name = p.getName();
					s.uniqname = p.getUniqname();
					s.id = "";
					s.level = "";
					s.credits = "";
					s.role = "";
					students_to_sort.add(s);
				}	
				state.setAttribute(STATE_STUDENTS_TO_SORT, students_to_sort);
				
				List students = prepPage(state);
				context.put("studentList", students);
					
				sortedBy = (String) state.getAttribute (SORTED_BY);
				sortedAsc = (String) state.getAttribute (SORTED_ASC);
				if(sortedBy!=null) context.put ("currentSortBy", sortedBy);
				if(sortedAsc!=null) context.put ("currentSortAsc", sortedAsc);

				context.put("totalPageNumber", new Integer(totalPageNumber(state)));
				context.put("searchString", state.getAttribute(STATE_SEARCH));
				context.put("form_search", FORM_SEARCH);
				context.put("formPageNumber", FORM_PAGE_NUMBER);
				context.put("prev_page_exists", state.getAttribute(STATE_PREV_PAGE_EXISTS));
				context.put("next_page_exists", state.getAttribute(STATE_NEXT_PAGE_EXISTS));
				context.put("current_page", state.getAttribute(STATE_CURRENT_PAGE));
				
				context.put("participantList", participantList);
				//context.put("studentList", studentList);
				context.put("SiteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				
				// default to be no paging
				context.put("paged", state.getAttribute(STATE_PAGING));
				
				return (String)getContext(data).get("template") + TEMPLATE[35];
			case 36:
				/*  buildContextForTemplate chef_site-addMyWorkspaceFeature.vm
				* 
				*/
				Vector singleToolRegistrations = new Vector();
				Vector multipleToolRegistrations = new Vector();
				
				/* put ToolRegistration objects in context in the order found in getMyWorkspaceToolOrder */
				
				//ToolRegistration id's in order
				List position = getMyWorkspaceToolOrder();
		
				//get CTNG-MyWorkspace ToolRegistration's
				toolRegistrationList = ServerConfigurationService.getToolRegistrations();
				if(toolRegistrationList.isEmpty())
				{
					Log.warn("chef", "SiteAction.setupToolLists -  toolRegistrationList.isEmpty ");
				}
				else
				{
					//go through ordered tool id list adding ToolRegistration's
					for (ListIterator i = position.listIterator(); i.hasNext();)
					{
						String atPosition = (String) i.next();
						
						//get the corresponding ToolRegistration
						for (ListIterator j = toolRegistrationList.listIterator(); j.hasNext(); )
						{
							ToolRegistration tr = (ToolRegistration) j.next();
							if(tr.getId().equals(atPosition))
							{
								//check that this ToolRegistration is in the CTNG-MyWorkspace category
								List categoriesList = tr.getCategories();
								for (ListIterator k = categoriesList.listIterator(); k.hasNext(); )
								{
									String category = (String) k.next();
									if(category.equals(SITE_TYPE_MYWORKSPACE))
									{	
										//we allow multiple instances of these two tools on My Workspace
										if((tr.getId().equals("chef.iframe")) || (tr.getId().equals("chef.news")))
										{
											//for purposes of grouping on the form, put in separate colections
											
											//multiple instances of tool/page on My Workspace
											multipleToolRegistrations.add(tr);
										}
										else
										{
											//single instance of tool/page on My Workspace
											singleToolRegistrations.add(tr);
										}
									}
								}
							}
						}
					}
				}
				//put ToolRegistrations in context
				context.put("singleToolRegistrations", singleToolRegistrations);
				context.put("multipleToolRegistrations", multipleToolRegistrations);
				
				//get the tools already in My Workspace, so we know whether to show a checkbox
				currentTools = getMyWorkspaceToolConfigurations(state);
				Collection noDups = new HashSet(currentTools);
				context.put("MyWorkspaceTools", noDups);
				
				//see if there is a Home page so we know whether to have a Home checkbox or not
				List allPages = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).getPages();
				for (ListIterator i = allPages.listIterator(); i.hasNext();)
					{
						SitePage thisPage = (SitePage) i.next();
						if(thisPage.getTitle().equals("Home"))
						{
							context.put("existsHome", new Boolean(true));
						}
				}
				return (String)getContext(data).get("template") + TEMPLATE[36];
			case 37:
				/*  buildContextForTemplate chef_site-removeMyWorkspaceFeature.vm
				* 
				*/
				
				/** put in context an ordered list of SitePages with description 
				 SitePage title identifies which to remove among multiple tool instances **/
				
				currentPages = getMyWorkspaceSitePages(state);
				List pageList = new Vector();

				//put pages in relative order
				List pageOrder = getMyWorkspacePageOrder();
				
				//iterate through pageOrder, adding pages with known titles
				for (ListIterator i = pageOrder.listIterator(); i.hasNext();)
				{
						String title = (String) i.next();
						
						//iterate through currentPages
						for (ListIterator j = currentPages.listIterator(); j.hasNext();)
						{
							SitePage p = (SitePage) j.next();
							
							if(title.equals(p.getTitle()))
							{
								//don't allow removal of 5 tools
								if(!((p.getTitle().equals("Worksite Setup"))
									|| (p.getTitle().equals("Membership")) 
									|| (p.getTitle().equals("Help"))
									|| (p.getTitle().equals("Home"))
									|| (p.getTitle().equals("Preferences"))))
								{
									pageList.add(p);
								}
							}
						}
				}
				
				boolean known = false;
				
				//iterate through currentPages, adding pages with unknown (custom) titles
				for (ListIterator i = currentPages.listIterator(); i.hasNext();)
				{
					SitePage p = (SitePage) i.next();
					known = false;
						
					//iterate through pageOrder
					for (ListIterator j = pageOrder.listIterator(); j.hasNext();)
						{
							String title = (String) j.next();
							if(title.equals(p.getTitle()))
							{
								known = true;
							}
						}
						if(!known) pageList.add(p);
				}
				context.put("pages", pageList);
				return (String)getContext(data).get("template") + TEMPLATE[37];
			case 38:
				/*  buildContextForTemplate chef_site-projectConfirm.vm
				* 
				*/
				siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
				context.put("title", siteInfo.title);
				context.put("description", siteInfo.description);
				context.put("short_description", siteInfo.short_description);
				context.put("siteContactName", siteInfo.site_contact_name);
				context.put("siteContactEmail", siteInfo.site_contact_email);
				toolRegistrationList = (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST);
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
				*	
				*/ 
				if (state.getAttribute(STATE_SITE_INSTANCE) != null)
				{
					context.put("editSite", Boolean.TRUE);
					context.put("siteTitle", ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
				}
				else
				{
					context.put("editSite", Boolean.FALSE);
				}
				if (state.getAttribute(STATE_AUTO_MANUAL_ADD) != null)
				{
					context.put("from_auto_manual", state.getAttribute(STATE_AUTO_MANUAL_ADD));
				}
				else
				{
					context.put("from_auto_manual", Boolean.FALSE);
				}
				buildInstructorSectionsList(state, params, context); // shared with case 18
				// buildInstructorSectionsList puts in context
				context.put("form_subject", siteInfo.subject);
				context.put("form_course", siteInfo.course);
				context.put("form_section", siteInfo.sections);
				context.put("form_additional", siteInfo.additional);
				context.put("form_title", siteInfo.title);
				context.put("form_description", siteInfo.description);
				context.put("value_uniqname", state.getAttribute(STATE_SITE_QUEST_UNIQNAME));
				number = 1;
				if (state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER) != null)
				{
					number = ((Integer) state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER)).intValue();
					context.put("currentNumber", new Integer(number));
				}
				context.put("currentNumber", new Integer(number));
				context.put("listSize", new Integer(number-1));
				context.put("subjects", state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS));
				context.put("courses", state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES));
				context.put("sections", state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS));
				if (state.getAttribute("selectedUmiacCourseSection") != null)
				{
					List l = (List) state.getAttribute("selectedUmiacCourseSection");
					context.put("selectedUmiacCourseSection", l);
					context.put("size", new Integer(l.size()-1));
				}
				if (state.getAttribute(STATE_AUTO_ADD) != null)
				{
					context.put("fromManualAdd", Boolean.TRUE);
					context.put("autoAdd", state.getAttribute(STATE_AUTO_ADD));
					context.put("back", "3");
				}
				else
				{
					context.put("fromManualAdd", Boolean.FALSE);
					context.put("back", "1");
				}				
				return (String)getContext(data).get("template") + TEMPLATE[39];
			case 40:
				/*  buildContextForTemplate chef_site-courseConfirm.vm
				* 
				*/
				if (state.getAttribute(STATE_AUTO_MANUAL_ADD) != null)
				{
					context.put("from_auto_manual", state.getAttribute(STATE_AUTO_MANUAL_ADD));
				}
				else
				{
					context.put("from_auto_manual", Boolean.FALSE);
				}		
				siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
				context.put("title", siteInfo.title);
				context.put("description", siteInfo.description);
				context.put("short_description", siteInfo.short_description);
				context.put("siteContactName", siteInfo.site_contact_name);
				context.put("siteContactEmail", siteInfo.site_contact_email);
				context.put("skin", siteInfo.skin);
				context.put("skins", state.getAttribute("skins"));
				context.put("additional", siteInfo.additional);
				toolRegistrationList = (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST);
				toolRegistrationSelectedList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
				context.put (STATE_TOOL_REGISTRATION_SELECTED_LIST, toolRegistrationSelectedList); // String toolId's
				context.put (STATE_TOOL_REGISTRATION_LIST, toolRegistrationList ); // %%% use Tool
				context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
				context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
				context.put("serverName", ServerConfigurationService.getServerName());
				context.put("include", new Boolean(siteInfo.include));
				context.put("selectedUmiacCourseSection", state.getAttribute("selectedUmiacCourseSection"));
				if (state.getAttribute(STATE_MANUAL_ADD) != null && ((Boolean) state.getAttribute(STATE_MANUAL_ADD)).booleanValue())
				{
					context.put("fromManualAdd", Boolean.TRUE);
					int number2 = ((Integer) state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER)).intValue();
					context.put("manualAddNumber", new Integer(number2 - 1));
					context.put("manualAddSubjects", state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS));
					context.put("manualAddCourses", state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES));
					context.put("manualAddSections", state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS));
				}
				else
				{
					context.put("fromManualAdd", Boolean.FALSE);
				}
				if (state.getAttribute(STATE_AUTO_ADD) != null)
				{
					context.put("autoAdd", state.getAttribute(STATE_AUTO_ADD));
				}
				context.put("newsTitles", (Hashtable) state.getAttribute(STATE_NEWS_TITLES));
				context.put("wcTitles", (Hashtable) state.getAttribute(STATE_WEB_CONTENT_TITLES));
				
				return (String)getContext(data).get("template") + TEMPLATE[40];
			case 41:
				/*  buildContextForTemplate chef_site-sitePublishUnpublish.vm
				* 
				*/
				if (((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
				{
					context.put("courseOrProject", "course");
				}
				else if (((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_PROJECT))
				{
					context.put("courseOrProject", "project");
				}
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
					
					boolean isGradToolsStudent = false;
					boolean isGradToolsRackham = false;
					boolean isGradToolsDepartment = false;
					if(((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_GRADTOOLS_STUDENT))
						isGradToolsRackham = true;
					if(((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_GRADTOOLS_RACKHAM))
						isGradToolsRackham = true;
					if(((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_GRADTOOLS_DEPARTMENT))
						isGradToolsDepartment = true;
					
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
							if(!(isGradToolsRackham || isGradToolsDepartment))
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
					if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
					{
						// course site
						context.put("isCourseSite", Boolean.TRUE);
						if (siteProperties.getProperty(PROP_SITE_TERM) != null)
						{
							context.put("siteTerm", TERMS[Integer.parseInt(siteProperties.getProperty(PROP_SITE_TERM))]);
						}
						
						if (allowUpdateSite)
						{
							sortedBy = (String) state.getAttribute (SORTED_BY);
							sortedAsc = (String) state.getAttribute (SORTED_ASC);
							if(sortedBy!=null) context.put ("currentSortedBy", sortedBy);
							if(sortedAsc!=null) context.put ("currentSortAsc", sortedAsc);
							
							// get participant list
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
						
						//get section list
						setCourseSectionList(state);
					 	context.put("umiacCourseSectionList", state.getAttribute("umiacCourseSectionList"));				
						context.put("manualCourseSectionList", state.getAttribute("manualCourseSectionList"));				
							
					}
					else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
					{
						// project site
						context.put("isProjectSite", Boolean.TRUE);
						boolean allowViewRoster = SiteService.allowViewRoster(siteId);
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
					else if (isGradToolsStudent || isGradToolsRackham || isGradToolsDepartment)
					{
						// Grad Tools site
						context.put("isGradToolsSite", Boolean.TRUE);
						boolean allowViewRoster = SiteService.allowViewRoster(siteId);
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
							participantsList = getParticipantList(state);
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
				site_type = (String)state.getAttribute(STATE_SITE_TYPE); 
				if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
				{
					// course site
					context.put("isCourseSite", Boolean.TRUE);
					context.put("siteTerm", TERMS[Integer.parseInt((String) state.getAttribute(FORM_SITEINFO_TERM))]);
					context.put("terms", TERMS);
				}
				else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
				{
					context.put("isProjectSite", Boolean.TRUE);
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
				if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
				{
					// course site
					context.put("isCourseSite", Boolean.TRUE);
					context.put("siteTerm", TERMS[Integer.parseInt((String) state.getAttribute(FORM_SITEINFO_TERM))]);
					context.put("oSiteTerm", TERMS[Integer.parseInt(siteProperties.getProperty(PROP_SITE_TERM))]);
				}
				else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
				{
					context.put("isProjectSite", Boolean.TRUE);
				}
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
				if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
				{
					context.put(STATE_TOOL_REGISTRATION_LIST, (Vector) state.getAttribute(STATE_COURSE_TOOL_LIST));
				}
				else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_GRADTOOLS_STUDENT)) 
				{
					context.put(STATE_TOOL_REGISTRATION_LIST, (Vector) state.getAttribute(STATE_GRADTOOLS_STUDENT_TOOL_LIST));
				}
				else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_GRADTOOLS_RACKHAM)) 
				{
					context.put(STATE_TOOL_REGISTRATION_LIST, (Vector) state.getAttribute(STATE_GRADTOOLS_RACKHAM_TOOL_LIST));
				}
				else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_GRADTOOLS_DEPARTMENT)) 
				{
					context.put(STATE_TOOL_REGISTRATION_LIST, (Vector) state.getAttribute(STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST));
				}
				else
				{
					myworkspace_site = false;
					if (SiteService.isUserSite(siteEdit.getId()))
					{
						if (SiteService.getSiteUserId(siteEdit.getId()).equals(UsageSessionService.getSessionUserId()))
						{
							myworkspace_site = true;
						}
					}
					if (myworkspace_site)
					{
						context.put(STATE_TOOL_REGISTRATION_LIST, state.getAttribute(STATE_MYWORKSPACE_TOOL_LIST));
					}
					else
					{
						context.put(STATE_TOOL_REGISTRATION_LIST, (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST));
					}
				}
				
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
		case 48:
			/*  buildContextForTemplate chef_site-gradtoolsConfirm.vm
			* 
			*/
			siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
			context.put("title", siteInfo.title);
			context.put("description", siteInfo.description);
			context.put("short_description", siteInfo.short_description);
			toolRegistrationList = (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST);
			toolRegistrationSelectedList = (Vector) state.getAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
			context.put (STATE_TOOL_REGISTRATION_SELECTED_LIST, toolRegistrationSelectedList); // String toolId's
			context.put (STATE_TOOL_REGISTRATION_LIST, toolRegistrationList ); // %%% use Tool
			context.put("check_home", state.getAttribute(STATE_TOOL_HOME_SELECTED));
			context.put("emailId", state.getAttribute(STATE_TOOL_EMAIL_ADDRESS));
			context.put("serverName", ServerConfigurationService.getServerName());
			context.put("include", new Boolean(siteInfo.include));
			return (String)getContext(data).get("template") + TEMPLATE[48];
		case 49:
			/*  buildContextForTemplate chef_siteInfo-editAccess.vm
			* 
			*/
			site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
			context.put("siteTitle", site.getTitle());
			
			siteProperties = site.getProperties();
			site_type = (String)state.getAttribute(STATE_SITE_TYPE);
			
			boolean isGradToolsSite = false;
			if(site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT) || site_type.equals(SITE_TYPE_GRADTOOLS_RACKHAM) || site_type.equals(SITE_TYPE_GRADTOOLS_DEPARTMENT))
				isGradToolsSite = true;
			
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
				if (site_type.equalsIgnoreCase(SITE_TYPE_COURSE))
				{
					b.add( new MenuEntry("Add Classes...", "doMenu_siteInfo_addClass"));
					b.add( new MenuEntry("Remove Classes...", "doMenu_siteInfo_removeClass"));
				}
				if (!isGradToolsSite)
				{
					b.add( new MenuEntry("Global Access...", "doMenu_siteInfo_globalAccess"));
				}
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
			
			if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
			{
				// course site
				context.put("isCourseSite", Boolean.TRUE);
						
				if (siteProperties.getProperty(PROP_SITE_TERM) != null)
				{
					context.put("siteTerm", TERMS[Integer.parseInt(siteProperties.getProperty(PROP_SITE_TERM))]);
				}
						
				if (allowUpdateSite)
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
							
					// if the site has some requested but not yet approved sections
					context.put("manualCourseSectionList", state.getAttribute("manualCourseSectionList"));				
					context.put("participantList", participantsList);
				}
			}
			else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
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
			else if(isGradToolsSite)
			{
				// GradTools site
				context.put("isGradToolsSite", Boolean.TRUE);
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
			* 
			*/
			buildInstructorSectionsList(state, params, context);
			umiacCourseSectionList = (Vector)state.getAttribute("umiacCourseSectionList");
			List manualCourseSectionList = (Vector)state.getAttribute("manualCourseSectionList");
			context.put("removeClassUmiac", state.getAttribute(STATE_REMOVE_CLASS_UMIAC));
			context.put("removeClassManual", state.getAttribute(STATE_REMOVE_CLASS_MANUAL));
			if (umiacCourseSectionList.size() != 0 || manualCourseSectionList.size() != 0)
			{
				context.put ("removable", Boolean.TRUE.toString()); 
			}
			context.put("siteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			return (String)getContext(data).get("template") + TEMPLATE[57];
		case 58: 
			/*  buildContextForTemplate chef_site-addRemoveClassConfirm.vm
			* 
			*/
			buildInstructorSectionsList(state, params, context);
			if (state.getAttribute(STATE_REMOVE_CLASS_UMIAC) != null || state.getAttribute(STATE_REMOVE_CLASS_MANUAL) != null)
			{
				context.put("remove", Boolean.TRUE); 
				context.put("removeClassUmiac", state.getAttribute(STATE_REMOVE_CLASS_UMIAC));
				context.put("removeClassManual", state.getAttribute(STATE_REMOVE_CLASS_MANUAL));
				context.put("backIndex", "57");
			}
			else
			{
				context.put("remove", Boolean.FALSE);
			}
			if (state.getAttribute(STATE_ADD_CLASS_UMIAC) != null || state.getAttribute(STATE_ADD_CLASS_MANUAL) != null)
			{
				context.put("add", Boolean.TRUE);
				context.put("addClassUmiac", state.getAttribute(STATE_ADD_CLASS_UMIAC));
				context.put("addClassManual", state.getAttribute(STATE_ADD_CLASS_MANUAL));
				if (state.getAttribute(STATE_ADD_CLASS_MANUAL) != null)
				{
					context.put("backIndex", "18");
				}
				else
				{
					context.put("backIndex", "3");
				}
			}
			else
			{
				context.put("add", Boolean.FALSE);
			}
			
			context.put("siteTitle", (String)((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle());
			return (String)getContext(data).get("template") + TEMPLATE[58];
		case 59: 
			/*  buildContextForTemplate chef_site-addClass-pickTerm.vm
			* 
			*/
			buildInstructorSectionsList(state, params, context);
			Site currentSite = (Site)state.getAttribute(STATE_SITE_INSTANCE);
			context.put("siteTitle", currentSite.getTitle());
			context.put("termList", termList);
			context.put("selectedTerm", new Integer((String)state.getAttribute(STATE_TERM_SELECTED)));
				
			return (String)getContext(data).get("template") + TEMPLATE[59];
		}
		// should never be reached
		return (String)getContext(data).get("template") + TEMPLATE[0];
		
	} // buildContextForTemplate
	
	/**
	* buildSectionsFromRealm 
	* a course site/realm id in one of three formats,
	* for a single section, for multiple sections of the same course, or
	* for a cross-listing having multiple courses. buildSectionsFromRealm 
	* parses a realm id into year, term, campus_code, catalog_nbr, section components.
	* @param id is a String representation of the course realm id (external id).
	*/
	private List buildSectionsFromRealm(String id)
	{
		Vector rv = new Vector();
		if(id == null || id == NULL_STRING)
		{
			return rv;
		}
		CourseListItem cli = new CourseListItem();
		String course_part = NULL_STRING;
		String section_part = NULL_STRING;
		String key = NULL_STRING;
		try
		{
			//Break Provider Id into course_nbr parts
			List course_nbrs = new ArrayList(Arrays.asList(id.split("\\+")));
		
			//Iterate through course_nbrs
			for (ListIterator i = course_nbrs.listIterator(); i.hasNext(); )
			{
				String course_nbr = (String) i.next();
			
				//Course_nbr pattern will be for either one section or more than one section
				if (course_nbr.indexOf("[") == -1)
				{
					// This course_nbr matches the pattern for one section
					cli = keyToCourseListItem(course_nbr);
					rv.add(cli);
				}
				else
				{
					// This course_nbr matches the pattern for more than one section
					course_part = course_nbr.substring(0, course_nbr.indexOf("[")); // includes trailing ","
					section_part = course_nbr.substring(course_nbr.indexOf("[")+1, course_nbr.indexOf("]"));
					String[] sect = section_part.split(",");
					for (int j = 0; j < sect.length; j++)
					{
						key = course_part + sect[j];
						cli = keyToCourseListItem(key);
						rv.add(cli);
					}	
				}
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", "SiteAction.buildSectionsFromRealm " + e.getMessage());
		}
		return rv;
	
	} // buildSectionsFromRealm - a simpler version
	
	/**
	* buildRequestedSections
	* form the list of requested sections from the string
	* @param id is a String representation of the requested sections
	*/
	private List buildRequestedSections(SessionState state, String id)
	{
		// the sections in UMIAC
		List umiacSections = (List) state.getAttribute("umiacCourseSectionList");
		
		Vector rv = new Vector();
		if(id == null || id == NULL_STRING)
		{
			return rv;
		}
		String course_part = NULL_STRING;
		String section_part = NULL_STRING;
		String key = NULL_STRING;
		try
		{
			//Break Provider Id into course_nbr parts
			List course_nbrs = new ArrayList(Arrays.asList(id.split("\\+")));
		
			//Iterate through course_nbrs
			for (ListIterator i = course_nbrs.listIterator(); i.hasNext(); )
			{
				CourseListItem cli = new CourseListItem();
				String course_nbr = (String) i.next();
				try
				{
					String[] res = course_nbr.split(",");
					cli.key = course_nbr;
					cli.year = res[0];
					cli.term_code = res[1];
					cli.campus_code = res[2];
					cli.subject = res[3];
					cli.catalog_nbr = res[4];
					cli.title = "";
					cli.url = "";
					cli.class_section = res[5];
					cli.role = "";
					cli.subrole = "";
					cli.crosslist = "";
					
					boolean found = false;
					for (ListIterator j = umiacSections.listIterator(); j.hasNext();)
					{
						CourseListItem umiacSection = (CourseListItem) j.next();
						if (umiacSection.year.equals(cli.year)
							&& umiacSection.term_code.equals(cli.term_code)
							&& umiacSection.subject.equals(cli.subject)
							&& umiacSection.catalog_nbr.equals(cli.catalog_nbr)
							&& umiacSection.class_section.equals(cli.class_section))
						{
							found = true;
						}
					}
					if (!found)
					{
						rv.add(cli);
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					Log.warn("chef", this + e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", this + e.getMessage());
		}
		return rv;
	
	} // buildRequestedSections
	
	private CourseListItem keyToCourseListItem (String key)
	{
		CourseListItem cli = new CourseListItem();
		try
		{
			String[] res = key.split(",");
			cli.key = key;
			cli.year = res[0];
			cli.term_code = res[1];
			cli.campus_code = res[2];
			cli.subject = res[3];
			cli.catalog_nbr = res[4];
			cli.title = "";
			//cli.class_url = "";
			cli.url = "";
			cli.class_section = res[5];
			//cli.section_url = "";
			cli.role = "";
			cli.subrole = "";
			cli.crosslist = "";
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			Log.warn("chef", "SiteAction.keyToCourseListItem " + e.getMessage());
		}
		return cli;
		
	} // keyToCourseListItem
	
	/**
	* buildExternalRealm creates a site/realm id in one of three formats,
	* for a single section, for multiple sections of the same course, or
	* for a cross-listing having multiple courses
	* @param sectionList is a Vector of CourseListItem
	* @param id The site id
	*/
	private String buildExternalRealm(String id, SessionState state, List sectionList)
	{
		String realm = "/site/" + id;
		if (!RealmService.allowUpdateRealm(realm))
		{
			addAlert(state, "You do not have permission to link to course roster(s).");
			return null;
		}
		if (!((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
		{
			Log.warn("chef", "SiteAction.buildExternalRealm, STATE_SITE_TYPE IS NOT SITE_TYPE_COURSE");
			return null;
		}

		boolean same_course = true;
		// No sections in list
		if (sectionList.size() == 0) 
		{
			return null;
		}
		// One section in list
		else if (sectionList.size() == 1) 
		{
			// 2002,2,A,EDUC,406,001
			return ((CourseListItem)sectionList.get(0)).getKey();
		}
		// More than one section in list
		else
		{
			String full_key = ((CourseListItem)sectionList.get(0)).getKey();
			String course = full_key.substring(0, full_key.lastIndexOf(","));
			same_course = true;
			for (ListIterator i = sectionList.listIterator(); i.hasNext(); )
			{
				CourseListItem item = (CourseListItem) i.next();
				if (item.getKey().indexOf(course) == -1) same_course = false; // If there is a difference in course part, multiple courses
			}
			// Same course but with multiple sections
			if (same_course)
			{
				StringBuffer sections = new StringBuffer();
				sections.append(course);
				sections.append(",[");
				boolean first_section = true;
				for (ListIterator i = sectionList.listIterator(); i.hasNext(); )
				{
					CourseListItem item = (CourseListItem) i.next();
					// remove the "," from the first section string
					String section = new String();
					if (first_section)
					{
						section = item.key.substring(item.key.lastIndexOf(",")+1,item.key.length());
					}
					else
					{
						section = item.key.substring(item.key.lastIndexOf(","),item.key.length());
					}
					first_section = false;
					sections.append(section);
				}
				sections.append("]");
				// 2002,2,A,EDUC,406,[001,002,003]
				return sections.toString();
			}
			// Multiple courses 
			else
			{
				// First, put course section keys next to each other to establish the course demarcation points
				Vector keys = new Vector();
				for (int i = 0; i < sectionList.size(); i++ )
				{
					CourseListItem item = (CourseListItem) sectionList.get(i);
					keys.add(item.getKey());
				}
				Collections.sort(keys);
				StringBuffer buf = new StringBuffer();
				StringBuffer section_buf = new StringBuffer();
				String last_course = null;
				String last_section = null;
				String to_buf = null;
				char plus = '+';
				boolean add_course = true;
				// Compare previous and next keys. When the course changes, build a component part of the id.
				for (int i = 0; i < keys.size(); i++)
				{
					// Go through the list of keys, comparing this key with the previous key
					String this_key= (String) keys.get(i);
					String this_course = this_key.substring(0, this_key.lastIndexOf(","));
					String this_section = this_key.substring(this_key.lastIndexOf(","), this_key.length());
					last_course = this_course;
					if(i != 0)
					{
						// This is not the first key in the list, so it has a previous key
						String previous_key = (String) keys.get(i-1);
						String previous_course = previous_key.substring(0, previous_key.lastIndexOf(","));
						String previous_section = previous_key.substring(previous_key.lastIndexOf(","), previous_key.length());
						if (previous_course.equals(this_course))
						{
							same_course = true;
							section_buf.append(previous_section);
						}
						else
						{
							same_course = false; // Different course, so wrap up the realm component for the previous course
							buf.append(previous_course);
							section_buf.append(previous_section);
							if (section_buf.lastIndexOf(",") == 0) // ,001
							{
								to_buf = section_buf.toString();
								buf.append(to_buf);
							}
							else
							{
								buf.append(",[");
								to_buf = section_buf.toString();
								buf.append(to_buf.substring(1)); // 001,002
								buf.append("]");	
							}
							section_buf.setLength(0);
							buf.append("+");
						}
						last_section = this_section;
					} // one comparison
				}
				// Hit the end of the list, so wrap up the realm component for the last course in the list
				if (same_course)
				{
					buf.append(last_course);
					buf.append(",[");
					buf.append((section_buf.toString()).substring(1));
					buf.append(last_section);
					// There must be more than one section, because there the last course was the same as this course
					buf.append ("]");
				}
				else
				{
					// There can't be more than one section, because the last course was different from this course
					buf.append(last_course);
					buf.append(last_section);
				}
				// 2003,3,A,AOSS,172,001+2003,3,A,NRE,111,001+2003,3,A,ENVIRON,111,001+2003,3,A,SOC,111,001
				return buf.toString();
			}
		}
		
	} // buildExternalRealm

	/**
	* doNew_site is called when the Site list tool bar New... button is clicked
	* 
	*/
	public void doNew_site ( RunData data )
		throws Exception
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "1");
		
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
	* called when "eventSubmit_doAdd_myworkspace_feature" is in the request parameters
	*/
	public void doAdd_myworkspace_feature( RunData data )
	{
		/** we need to add selected pages and tool configurations, then re-order the
		 pages to the usual arrangement for the left hand menu */

		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "15");
		ParameterParser params = data.getParameters ();
		
		//was anything selected?
		if (params.getStrings ("selected") == null)
		{
			addAlert(state, "No tools were selected.");
			state.setAttribute(STATE_TEMPLATE_INDEX, "36");
			return;
		}
		
		//get selected pages, including Home
		List selectedList = new ArrayList(Arrays.asList(params.getStrings ("selected")));
		
		//get surrent tool configurations
		List currentTools = getMyWorkspaceToolConfigurations(state);
		
		//get current pages
		List currentPages = getMyWorkspaceSitePages(state);
		
		//page titles in relative order for menu
		List pageOrder = getMyWorkspacePageOrder();
		
		//get tool registrations for creating new tool configurations
		List trList = new Vector();
		List toolRegistrationList = ServerConfigurationService.getToolRegistrations();
		if(toolRegistrationList.isEmpty())
		{
			Log.warn("chef", "SiteAction.setupToolLists -  toolRegistrationList.isEmpty ");
		}
		else
		{
			//for each tool registration
			for (ListIterator i = toolRegistrationList.listIterator(); i.hasNext(); )
			{
				ToolRegistration tr = (ToolRegistration) i.next();
				
				//get it's categories
				List categoriesList = tr.getCategories();
				
				//and see if it is included in My Workspace tools
				for (ListIterator j = categoriesList.listIterator(); j.hasNext(); )
				{
					String category = (String) j.next();
					if(category.equals(SITE_TYPE_MYWORKSPACE))
					{
						trList.add(tr);
					}
				}
			}
		}
		
		//if a single instance tool exists, don't show a checkbox for adding it
		boolean exists = false;
	
		//iterate through the selected tool list
		for (ListIterator i = selectedList.listIterator(); i.hasNext();)
		{
			String selected = (String) i.next();
			
			// Home is a special case, with several tools on the page. "Home" is hard-coded in chef_site-addMyWorkspaceFeatures.vm.
			if (selected.equals("Home"))
			{
				try
				{
					SitePageEdit page = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).addPage();
					page.setTitle("Home");
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);

					ToolConfigurationEdit motd = page.addTool();
					motd.setToolId("chef.motd");
					motd.setTitle("Message of the Day");
					
					ToolConfigurationEdit iframe = page.addTool();
					iframe.setToolId("chef.iframe");
					iframe.setTitle("My Workspace Information");
					iframe.getPropertiesEdit().addProperty("special", "worksite");
					
				}
				catch (Exception e)
				{
					Log.warn("chef", this + ".doAdd_myworspace_feature Exception: " + e.getMessage());
				}
			} // Home
			else
			{
				//iterate through the tool registrations
				for (ListIterator j = trList.listIterator(); j.hasNext();)
				{
					ToolRegistration tr = (ToolRegistration) j.next();

					//if the tool registration matches a tool to add
					if(tr.getId().equals(selected))
					{
						//just go ahead and add if it's a multiple instance tool
						if((selected.equals("chef.iframe")) || (selected.equals("chef.news")))
						{
							SitePageEdit newPage = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).addPage();
						
							//if the user provided a unique title for a multiple instance tool, use it
							String newTitle = StringUtil.trimToNull(params.getString(selected));
							if(newTitle != null)
							{
								newPage.setTitle(newTitle);
							}
							else
							{
								newPage.setTitle(tr.getTitle());
							}
							newPage.setLayout(SitePage.LAYOUT_SINGLE_COL);
						
							//add a tool based on the tool registration
							ToolConfigurationEdit newTool = newPage.addTool(tr);
						}
						else
						{
							//if tool aready exists, skip, otherwise, add.
							exists = false;
							for (ListIterator k = currentTools.listIterator(); k.hasNext();)
							{
								ToolConfiguration tc = (ToolConfiguration) k.next();
								if(tc.getToolId().equals(tr.getId())) 
								{
									exists = true;
								}
							}
							if(!exists)
							{
								SitePageEdit newPage = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).addPage();
								newPage.setTitle(tr.getTitle());
								newPage.setLayout(SitePage.LAYOUT_SINGLE_COL);
								ToolConfigurationEdit newTool = newPage.addTool(tr);
							}
						}
					}
				}
			}
		}
		
		//order pages by tool(0) on page in this order
		List referenceOrder = new Vector();
		referenceOrder.add("chef.motd");
		referenceOrder.add("chef.sitesetup");
		referenceOrder.add("chef.membership");
		referenceOrder.add("chef.schedule");
		referenceOrder.add("chef.announcements");
		referenceOrder.add("chef.resources");
		referenceOrder.add("chef.news");
		referenceOrder.add("chef.iframe");
		referenceOrder.add("chef.noti.prefs");
		referenceOrder.add("chef.siteinfo");
		referenceOrder.add("chef.contactSupport");

		int last = 0;
		int orderIndex = 0;
		int moves = 0;
		
		//get the pages to order
		List pages = (Vector) ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).getPageEdits();
			
		//iterate through reference order
		for (ListIterator i = referenceOrder.listIterator(); i.hasNext();)
		{
			String tid = (String) i.next();
			
			//iterate through pages, moving all pages that match a reference order
			for (ListIterator j = pages.listIterator(); j.hasNext();)
			{
				SitePageEdit p = (SitePageEdit) j.next();
				
				ToolConfiguration t = (ToolConfiguration) p.getTools().get(0);
					
				//when a page matches a reference order, move it
				if((t.getToolId()).equals(tid))
				{
					//moves equals position of page relative to last page moved up
					orderIndex = pages.indexOf(p);
					moves = orderIndex - last;
					for (int n = 0; n < moves; n++)
					{
						p.moveUp();
					}
					last++;
				}
			}	
		}
		commitSite(state);
		
	} //doAdd_myworkspace_feature
	
	/**
	* called when "eventSubmit_doAdd_myworkspace_feature" is in the request parameters
	*/
	public void doRemove_myworkspace_feature( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		state.setAttribute (STATE_TEMPLATE_INDEX, "15");
		ParameterParser params = data.getParameters ();
		
		//check to see if any pages were selected
		if (params.getStrings ("selected") == null)
		{
			addAlert(state, "No tools were selected.");
			state.setAttribute(STATE_TEMPLATE_INDEX, "37");
			return;
		}
		
		//get the page id's of pages to remove
		List selectedList = new ArrayList(Arrays.asList(params.getStrings ("selected")));
		
		//get the current pages
		List currentPages = getMyWorkspaceSitePages(state);
		
		//iterate through the current pages
		for (ListIterator i = currentPages.listIterator(); i.hasNext();)
		{
			SitePage page = (SitePage) i.next();
			
			//iterate though selected page id's
			for (ListIterator j = selectedList.listIterator(); j.hasNext();)
			{
				String remove = (String) j.next();
				
				//if the curent page'd id matches a selected page id, remove the page
				if(page.getId().equals(remove))
				{	
					SitePageEdit pg = ((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).getPageEdit(remove);
					List toolList = pg.getToolEdits();
					for (ListIterator k = toolList.listIterator(); k.hasNext(); )
					{
						ToolConfigurationEdit tool = (ToolConfigurationEdit) k.next();
						pg.removeTool(tool);
					}
					((SiteEdit)state.getAttribute(STATE_SITE_INSTANCE)).removePage(pg);
				}
			}
		}
		commitSite(state);
		
	} //doRemove_myworkspace_feature
	
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
		
		// start out with fresh site information
		Profile profile = new Profile();
		SiteInfo siteInfo = new SiteInfo();
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		state.setAttribute(STATE_PROFILE, profile);
		state.removeAttribute(STATE_TOOL_REGISTRATION_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_LIST);
		state.removeAttribute(STATE_TOOL_REGISTRATION_OLD_SELECTED_HOME);
		
		ParameterParser params = data.getParameters ();
		int index = Integer.valueOf(params.getString ("template-index")).intValue();
		actionForTemplate("continue", index, params, state);
		if (params.getString ("itemType").equals ("class"))
		{
			state.setAttribute (STATE_SITE_TYPE, SITE_TYPE_COURSE);
			
			// start out with fresh course listings
			List umiacCourseSectionList = new Vector();
			List manualCourseSectionList = new Vector();
			state.removeAttribute(STATE_TERM_COURSE_SECTION_LIST);
			state.setAttribute("umiacCourseSectionList", umiacCourseSectionList);
			state.setAttribute("manualCourseSectionList", manualCourseSectionList);
			
			// manual add or auto add?
			List sectionList = getTermCourseSectionList(state, params);
			if (sectionList.size() > 0)
			{
				// there is at least one section in specified term with the current user as instructor
				// go to the automatically-populated course site creation page
				state.setAttribute(STATE_AUTO_ADD, Boolean.TRUE);
				state.setAttribute(STATE_TEMPLATE_INDEX, "3");		
			}
			else
			{
				// there is no course 
				// go to the manual creation page
				state.setAttribute(STATE_MANUAL_ADD, Boolean.TRUE);
				state.setAttribute(STATE_MANUAL_ADD_COURSE_NUMBER, new Integer("1"));
				state.setAttribute(STATE_TEMPLATE_INDEX, "39");
			}
			// %%% 6/6/03 skipping -courseContact to make it easier to add profiles later 
		}
		else if (params.getString ("itemType").equals ("gradtools"))
		{
			state.setAttribute (STATE_SITE_TYPE, SITE_TYPE_GRADTOOLS_STUDENT);
			updateSiteInfo(params, state);
			
			//skip directly to confirm creation of site 
			state.setAttribute (STATE_TEMPLATE_INDEX, "48");
		}
		else if (params.getString ("itemType").equals ("diss"))
		{
			state.setAttribute (STATE_SITE_TYPE, SITE_TYPE_GRADTOOLS_STUDENT);
			// %%% 6/6/03 skip -dissertationContact to make it easier to add profiles later 
			state.setAttribute (STATE_TEMPLATE_INDEX, "6");
		}
		else if (params.getString ("itemType").equals ("project"))
		{
			state.setAttribute (STATE_SITE_TYPE, SITE_TYPE_PROJECT);
			state.setAttribute (STATE_TEMPLATE_INDEX, "9");
		}
		
		siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);
		siteInfo.site_type = (String) state.getAttribute (STATE_SITE_TYPE);
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		
	}	// doSite_type
	
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
	* Set site type to SITE_TYPE_COURSE or SITE_TYPE_PROJECT and skin as appropriate
	*
	*/
	public void doSet_site_type ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		ResourcePropertiesEdit rp = site.getPropertiesEdit();
		
		//read radio buttons and set site type
		ParameterParser params = data.getParameters ();
		if (params.getString ("itemType").equals ("class"))
		{
			siteInfo.site_type = SITE_TYPE_COURSE;
			state.setAttribute(STATE_TEMPLATE_INDEX, "2");
		}
		else if (params.getString ("itemType").equals ("project"))
		{
			siteInfo.site_type = SITE_TYPE_PROJECT;
			state.setAttribute(STATE_TEMPLATE_INDEX, "9");
		}
		state.setAttribute(STATE_SITE_TYPE, siteInfo.site_type);
		
		rp.addProperty(PROP_SITE_TYPE, siteInfo.site_type);
		
		//if published get skin from attribute if it is set
		if(site.getStatus() == Site.SITE_STATUS_PUBLISHED)
		{
			siteInfo.skin = site.getSkin();
			if(siteInfo.skin == null || siteInfo.skin == NULL_STRING)
			{
				if((siteInfo.site_type).equals(SITE_TYPE_COURSE))
				{
					siteInfo.skin= COURSE_PUBLISHED_UM_SKIN;
				}
				else if ((siteInfo.site_type).equals(SITE_TYPE_PROJECT))
				{
					siteInfo.skin= PROJECT_PUBLISHED_SKIN;
				}
				else
				{
					//log it
				}	
			}
		}
		else
		{
			if(siteInfo.skin == null || siteInfo.skin == NULL_STRING)
			{
				if((siteInfo.site_type).equals(SITE_TYPE_COURSE))
				{
					siteInfo.skin= COURSE_UNPUBLISHED_SKIN;
				}
				else if ((siteInfo.site_type).equals(SITE_TYPE_PROJECT))
				{
					siteInfo.skin= PROJECT_UNPUBLISHED_SKIN;
				}
				else
				{
					//log it
				}	
			}
			if(site.getProperties().getProperty(PROP_SITE_SKIN) != null)
			{
				siteInfo.skin = site.getProperties().getProperty(PROP_SITE_SKIN);
			}
			else
			{
				//Otherwise, use a default until the tool can create PROP_SITE_SKIN
				if(siteInfo.site_type.equals(SITE_TYPE_COURSE))
				{
					siteInfo.skin = COURSE_UNPUBLISHED_SKIN;
				}
				else
				{
					siteInfo.skin = PROJECT_UNPUBLISHED_SKIN;
				}
			}
		}
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		state.setAttribute(STATE_SITE_INSTANCE, site);
		
		//save the site type
		updateSiteAttributes(state);
		updateSiteProperties(state);
		commitSite(state);

		int index = Integer.valueOf(params.getString ("template-index")).intValue();
		String direction = "continue";
		actionForTemplate("continue", index, params, state);

	}	// doSet_site_type
	
	
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
	* doFinish_grad_tools is called when creation of a Grad Tools site is confirmed
	*/
	public void doFinish_grad_tools ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ()); 
		ParameterParser params = data.getParameters ();

		//set up for the coming template
		state.setAttribute(STATE_TEMPLATE_INDEX, params.getString ("continue"));
		int index = Integer.valueOf(params.getString ("template-index")).intValue();
		actionForTemplate("continue", index, params, state);

		//add the pre-configured Grad Tools tools to a new site
		addGradToolsFeatures(state);

		// get the main CHEF window to refresh, to get an update to tabs (and not the float window)
		CourierService.deliver(new RefreshSiteNavDelivery(PortalService.getCurrentClientWindowId(null)));
		
		resetPaging(state);
		
	}// doFinish_grad_tools
	
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
			
			// Construct a tab, set Realm Provider Id, add affliates, and send a notice to Support or send a request to Support
			if (((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE)) finishCourseSite(state, params);

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
			state.removeAttribute(STATE_UNREMOVEABLE_USER_LIST);
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
			state.removeAttribute(FORM_SITEINFO_TERM);
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
	
			removeAddClassContext(state);
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
				removeAddClassContext(state);
				state.setAttribute(STATE_TEMPLATE_INDEX, "49");
			}
		}
		else if (currentIndex.equals("57"))
		{
			//from removing class
			//remove edit object
			removeSiteEditFromState(state);
	
			removeRemoveClassContext(state);
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
		String courseOrProject = params.getString("courseOrProject");
		String stateVariable = null;

		if (courseOrProject.equalsIgnoreCase("course"))
		{
			stateVariable = STATE_COURSE_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("project"))
		{
			stateVariable = STATE_PROJECT_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("gradtools_student"))
		{
			stateVariable = STATE_GRADTOOLS_STUDENT_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("gradtools_rackham"))
		{
			stateVariable = STATE_GRADTOOLS_RACKHAM_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("gradtools_department"))
		{
			stateVariable = STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("myworkspace"))
		{
			stateVariable = STATE_MYWORKSPACE_TOOL_LIST;
		}
		else
		{
			stateVariable = STATE_UNDEFINED_TOOL_LIST;
		}

		// dispatch
		if (option.equalsIgnoreCase("addNews"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, stateVariable);
				insertTool(state, stateVariable, "chef.news", STATE_NEWS_TITLES, NEWS_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("addWebContent"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, stateVariable);
				insertTool(state, stateVariable, "chef.iframe", STATE_WEB_CONTENT_TITLES, WEB_CONTENT_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("save"))
		{
			updateSelectedToolList(state, params, stateVariable);
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
			
			// get site roster - participant come from provider
			List roster = roster(state,(Vector) state.getAttribute("umiacCourseSectionList"));
			
			List removeableUser = new Vector();
			List unremoveableUser = new Vector();
			
			boolean foundInRoster = false;
			
			for (ListIterator i = removeUser.listIterator(); i.hasNext(); )
			{
				String id = (String) i.next();
				foundInRoster = false;
				for (ListIterator j = roster.listIterator(); j.hasNext() && !foundInRoster; )
				{
					Student student = (Student) j.next();
					if (id.equals(student.getUniqname()))
					{
						foundInRoster = true;
						unremoveableUser.add(id);
					}
				}	
				
				if (!foundInRoster)
				{
					removeableUser.add(id);
				}
			}	

			// all or some selected user(s) can be removed, go to confirmation page
			if (removeableUser.size() > 0)
			{
				state.setAttribute (STATE_TEMPLATE_INDEX, "29");
			}
			else
			{
				addAlert(state, "You cannot remove participants officially registered for this course. However, you can change their roles in the site by clicking the Change Role(s) command (...under Revise, Edit Access.)");
			}
			
			state.setAttribute (STATE_REMOVEABLE_USER_LIST, removeableUser);
			state.setAttribute (STATE_UNREMOVEABLE_USER_LIST, unremoveableUser);
			
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
		
		//course site
		ResourceProperties siteProperties = site.getProperties();
		if (siteProperties.getProperty(PROP_SITE_TERM) != null)
		{
			state.setAttribute(STATE_TERM_SELECTED, siteProperties.getProperty(PROP_SITE_TERM));
		}
		
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
		
		//course site
		ResourceProperties siteProperties = site.getProperties();
		if (siteProperties.getProperty(PROP_SITE_TERM) != null)
		{
			state.setAttribute(STATE_TERM_SELECTED, siteProperties.getProperty(PROP_SITE_TERM));
		}
		
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
	* doMenu_roster_sort
	*/
	public void doMenu_roster_sort( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		state.setAttribute (STATE_TEMPLATE_INDEX, "35");
		
	} // doMenu_roster_sort
	
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
	* dispatch to different functions based on the option value in the parameter
	*/
	public void doManual_add_course ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
			
		String option = params.getString("option");
		if (option.equalsIgnoreCase("change") || option.equalsIgnoreCase("add"))
		{
			readCourseSectionInfo(state, params);
		
			String uniqname = StringUtil.trimToNull(params.getString("uniqname"));
			state.setAttribute(STATE_SITE_QUEST_UNIQNAME, uniqname);
		
			updateSiteInfo(params, state);
			
			if (option.equalsIgnoreCase("add"))
			{
			
				if (uniqname == null)
				{
					addAlert(state, "Please enter a uniqname.");
				}
				else
				{
					try
					{
						User instructor = UserDirectoryService.getUser(uniqname);
					}
					catch (IdUnusedException e)
					{
						addAlert(state, "Please enter a valid uniqname for the instructor of record. ");
					}
				}
				if (state.getAttribute(STATE_MESSAGE) == null)
				{
					doAdd_course_section(state, params, "umiacCourseSectionList", "manualCourseSectionList");
					
					state.setAttribute(STATE_TEMPLATE_INDEX, "11");
				}
			}
		}
		else if (option.equalsIgnoreCase("back"))
		{
			doBack(data);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			doCancel_create(data);
		}
			
	}	// doManual_add_course
	
	/**
	* read the input information of subject, course and section in the manual site creation page
	*/
	private void readCourseSectionInfo (SessionState state, ParameterParser params)
	{
		// how many course/section to include in the site?
		String newNumber = params.getString("number");
		if (newNumber == null)
		{
			newNumber = "1";
		}
		state.setAttribute(STATE_MANUAL_ADD_COURSE_NUMBER, new Integer(newNumber));
		int n = Integer.parseInt(newNumber);
		
		Vector subjects = new Vector();
		Vector courses = new Vector();
		Vector sections = new Vector();
		boolean validInput = true;
		int validInputSites = 0;
		
		String option = params.getString("option");
		
		// read the user input
		for (int i = 0; i < n; i++)
		{
			String subject = StringUtil.trimToZero(params.getString("subject" + i));
			String course = StringUtil.trimToZero(params.getString("course" + i));
			String section = StringUtil.trimToZero(params.getString("section" + i));
			if (subject.length() != 0 && course.length() != 0 && section.length() != 0)
			{
				// if all the inputs are not empty
				validInputSites++;
			}
			else if (subject.length() == 0 && course.length() == 0 && section.length() == 0)
			{
				// if all inputs are empty
			}
			else
			{
				validInput = false;
			}
			subjects.add(subject);
			courses.add(course);
			sections.add(section);
		}	// for
		
		if (!option.equalsIgnoreCase("change"))
		{
			if (!validInput || validInputSites == 0)
			{
				// not valid input
				addAlert(state, "A required field is missing. Please fill in all required fields.");
			}
			else
			{
				// valid input, adjust the add course number
				state.setAttribute(STATE_MANUAL_ADD_COURSE_NUMBER, new Integer(validInputSites));
			}
		}
		
		// set state attributes
		state.setAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS, subjects);
		state.setAttribute(STATE_MANUAL_ADD_COURSE_COURSES, courses);
		state.setAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS, sections);
		state.setAttribute(FORM_ADDITIONAL, StringUtil.trimToZero(params.getString("additional")));
		
	}	// readCourseSectionInfo
	
	
	/**
	* doAdd_course_section is called when "eventSubmit_doAdd_course_section" is in the request parameters
	* from template chef_site-courseClass.vm
	*/
	public void doAdd_course_section ( SessionState state, ParameterParser params, String umiacListName, String manualListName)
	{
		try
		{
			// get form data
			String form_term = params.getString("term");
			String additional = params.getString("additional");
			String title = params.getString ("title");

			// get state attributes
			Vector subjects = state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS) != null ? (Vector) state.getAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS) : new Vector(); 
			Vector courses = state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES) != null ? (Vector) state.getAttribute(STATE_MANUAL_ADD_COURSE_COURSES) : new Vector();
			Vector sections = state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS) != null ? (Vector) state.getAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS) : new Vector();
			
			// The list of sections & cross lists for the specified term having this user listed as Instructor 
			List termCourseSectionList = (state.getAttribute(STATE_TERM_COURSE_SECTION_LIST) == null) ? 
				new Vector() : (List) state.getAttribute(STATE_TERM_COURSE_SECTION_LIST);
			
			// The list of courses previously selected from UMIAC listing
			List umiacCourseSectionList = new Vector();
			
			// The list of courses previously selected by manual form entry
			List manualCourseSectionList = new Vector();
			
			List umiacChosenList = new Vector();
			
			// Read template data
			if (state.getAttribute(STATE_ADD_CLASS_UMIAC) != null)
			{
				umiacChosenList = (List) state.getAttribute(STATE_ADD_CLASS_UMIAC); // list of CourseListItem keys	
			}
			// Check the form data
			boolean manual_selection = false;
			if (state.getAttribute(STATE_MANUAL_ADD) != null)
			{
				manual_selection = ((Boolean) state.getAttribute(STATE_MANUAL_ADD)).booleanValue();
			}
			
			boolean has_subject = subjects.size() != 0 ? true : false;
			boolean has_course = courses.size() != 0 ? true : false;
			boolean has_section = sections.size() != 0 ? true : false;

			// If permission is missing, alert
			if (!SiteService.allowAddSite(title))
			{
				addAlert(state, "You do not have permission to add " + title + ".");
				return;
			}
			
			// Option is to use manual entry of subject, course, section so clear the UMIAC list of it's contents.
			if (manual_selection)
			{
				if (state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER) != null)
				{
					int number = ((Integer) state.getAttribute(STATE_MANUAL_ADD_COURSE_NUMBER)).intValue();
					
					for (int i = 0; i < number; i++)
					{
						String subject = (String) subjects.get(i);
						String course = (String) courses.get(i);
						String section = (String) sections.get(i);
	
						// This is a minimal CourseListItem created with the manually entered course, subject, and section
						CourseListItem cli_manual = new CourseListItem();
						if (form_term == null && state.getAttribute(STATE_TERM_SELECTED) != null)
						{
							form_term = (String)state.getAttribute(STATE_TERM_SELECTED);
						}
						
						switch(Integer.parseInt(form_term))
						{
							case 0:
								// Fall, 2003: 2, 2003
								//
								cli_manual.term_code = "2";
								cli_manual.year = "2003";
								break;
							case 1:
								// Winter, 2004: 3, 2004
								//
								cli_manual.term_code = "3";
								cli_manual.year = "2004";
								break;
							case 2:
								// Spring, 2004: 4, 2004
								//
								cli_manual.term_code = "4";
								cli_manual.year = "2004";
								break;
							case 3:
								// Spring/Summer 2004: 5, 2004
								//
								cli_manual.term_code = "5";
								cli_manual.year = "2004";
								break;
							case 4:
								// Summer, 2004: 1, 2004
								//
								cli_manual.term_code = "1";
								cli_manual.year = "2004";
								break;
							case 5:
								// Fall, 2004: 2, 2004
								//
								cli_manual.term_code = "2";
								cli_manual.year = "2004";
								break;
							case 6:
								// Winter, 2005: 3, 2005
								//
								cli_manual.term_code = "3";
								cli_manual.year = "2005";
								break;
						}
						cli_manual.campus_code = "A";
						cli_manual.subject = subject;
						cli_manual.catalog_nbr = course;
						cli_manual.class_section = section;
						cli_manual.title = "";
						cli_manual.url = "";
						cli_manual.role = "";
						cli_manual.subrole = "";
						cli_manual.key = cli_manual.year + "," + cli_manual.term_code + "," + cli_manual.campus_code + "," 
							+ cli_manual.subject + "," + cli_manual.catalog_nbr + "," + cli_manual.class_section;
						manualCourseSectionList.add(cli_manual);
					}
					state.setAttribute(manualListName, manualCourseSectionList);
					
					// for email request to support
					state.setAttribute(STATE_COURSE_REQUEST, courses);
					state.setAttribute(STATE_SUBJECT_REQUEST, subjects);
					state.setAttribute(STATE_SECTION_REQUEST, sections);
				}
					
			} // manual form entry
			
			// If not manual form entry, look for courses selected from the UMIAC list
			boolean adds = umiacChosenList.size() == 0 ? false : true;
			if (adds)
			{
				// Iterate through the list building a list of "official" courses
				for (ListIterator i = umiacChosenList.listIterator(); i.hasNext(); )
				{
					String umiacId = (String) i.next();
					for (ListIterator j = termCourseSectionList.listIterator(); j.hasNext(); )
					{
						CourseListItem umiacCLI = (CourseListItem) j.next();
						String termCourseSectionListId = (String) umiacCLI.getKey();
						if (umiacId.equals(termCourseSectionListId))
						{
							umiacCourseSectionList.add(umiacCLI);
						}
					}
				}
				state.setAttribute(umiacListName, removeDuplicateCourseListItems(umiacCourseSectionList));
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", "SiteAction.doAdd_course_section Exception " + e.toString());
		}
		
	} // doAdd_course_section
	
	/**
	* deleteCourseSection is used remove course and section entries selected from a list of
	* courses for which this person is listed as instructor with UMIAC or a list of courses entered
	* manually by filling in the provided form
	*/
	public void deleteCourseSection ( SessionState state )
	{
		List umiacCourseSectionList = (Vector)state.getAttribute("umiacCourseSectionList");
		List manualCourseSectionList = (Vector)state.getAttribute("manualCourseSectionList");
		List umiacDeletionList = new Vector();
		List manualDeletionList = new Vector();
		if (state.getAttribute(STATE_REMOVE_CLASS_UMIAC) != null)
		{
			// build the deletions list
			umiacDeletionList = (List) state.getAttribute(STATE_REMOVE_CLASS_UMIAC);
			
			// make the deletions
			for (ListIterator i = umiacDeletionList.listIterator(); i.hasNext(); )
			{
				CourseListItem umiacDelete = (CourseListItem) i.next();
				umiacCourseSectionList.remove(umiacDelete);
			}
			state.setAttribute("umiacCourseSectionList", umiacCourseSectionList);
		}

		if(state.getAttribute(STATE_REMOVE_CLASS_MANUAL) != null)
		{
			// build the deletions list
			manualDeletionList = (List) state.getAttribute(STATE_REMOVE_CLASS_MANUAL);			

			// make the deletions
			for (ListIterator i = manualDeletionList.listIterator(); i.hasNext(); )
			{
				CourseListItem manualDelete = (CourseListItem) i.next();
				manualCourseSectionList.remove(manualDelete);
			}
			state.setAttribute("manualCourseSectionList", manualCourseSectionList);
		}
		
	} // deleteCourseSection
	
	/**
	 * Clean the state object for the context of class removal
	 * 
	 */
	private void removeRemoveClassContext(SessionState state)
	{
		state.removeAttribute(STATE_REMOVE_CLASS_UMIAC);
		state.removeAttribute(STATE_REMOVE_CLASS_MANUAL);
		
	}	// removeRemoveClassContext
	
	
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
		if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
		{
			// course site
			if (siteProperties.getProperty(PROP_SITE_TERM) != null)
			{
				state.setAttribute(FORM_SITEINFO_TERM, siteProperties.getProperty(PROP_SITE_TERM));
			}
		}
		else if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
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
		state.removeAttribute("manualCourseSectionList");
		state.removeAttribute("umiacCourseSectionList");
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
			
			if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
			{
				// course site
				siteProperties.addProperty(PROP_SITE_TERM, (String) state.getAttribute(FORM_SITEINFO_TERM));
			}
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

		if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_COURSE)) 
		{
			// course site
			String skin = (String) state.getAttribute(FORM_SITEINFO_SKIN);
			if (skin != null)
			{
				siteEdit.setSkin(skin);
				siteProperties.addProperty(PROP_SITE_SKIN, skin);
			}	
		}
		
		if(site_type != null && site_type.equalsIgnoreCase(SITE_TYPE_PROJECT)) 
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
			state.removeAttribute(FORM_SITEINFO_TERM);
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
	*  setupTerms
	* 
	*/
	private void setupTerms(SessionState state)
	{
		List termList = new Vector();
		for (int i = 0; i < TERMS.length; i++)
		{
			IdAndText item = new IdAndText();
			item.id = i;
			item.text = TERMS[i];
			termList.add(item);
		}
		state.setAttribute(TERM_LIST, termList);
		
	} // setupTerms
	
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
		rv.add("ctng.home");
		rv.add("chef.schedule");
		rv.add("chef.resources");
		rv.add("chef.announcements");
		rv.add("chef.iframe");
		rv.add("chef.news");
		rv.add("chef.membership");
		rv.add("chef.sitesetup");
		rv.add("chef.noti.prefs");
		rv.add("chef.siteinfo");
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
		toolOrder.add("chef.aboutGradTools");
		toolOrder.add("chef.dissertation");
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
		toolOrder.add("chef.sitesetup");
		toolOrder.add("chef.noti.prefs");
		toolOrder.add("chef.singleuser");
		toolOrder.add("chef.siteinfo");
		toolOrder.add("chef.dissertation.upload");
		toolOrder.add("chef.gradToolsHelp");
		toolOrder.add("chef.contactSupport");
		
		state.setAttribute("toolOrder", toolOrder);
	
	} // setupToolOrder
	
	/**
	* Add these Unit affliates to sites in these
	* Subject areas with Instructor role
	*
	*/
	private void setupSubjectAffiliates(SessionState state)
	{
		Vector affiliates = new Vector();
		
		//Business
		
		SubjectAffiliates ACC = new SubjectAffiliates();
		ACC.subject = "ACC";
		ACC.campus = "A";
		ACC.uniqnames.add("debiec");
		ACC.uniqnames.add("ferryk");
		ACC.uniqnames.add("jcaracc");
		ACC.uniqnames.add("tle");
		affiliates.add(ACC);
		
		SubjectAffiliates BA = new SubjectAffiliates();
		BA.subject = "BA";
		BA.campus = "A";
		BA.uniqnames.add("debiec");
		BA.uniqnames.add("ferryk");
		BA.uniqnames.add("jcaracc");
		BA.uniqnames.add("tle");
		affiliates.add(BA);
		
		SubjectAffiliates BE = new SubjectAffiliates();
		BE.subject = "BE";
		BE.campus = "A";
		BE.uniqnames.add("debiec");
		BE.uniqnames.add("ferryk");
		BE.uniqnames.add("jcaracc");
		BE.uniqnames.add("tle");
		affiliates.add(BE);
		
		SubjectAffiliates CIS = new SubjectAffiliates();
		CIS.subject = "CIS";
		CIS.campus = "A";
		CIS.uniqnames.add("debiec");
		CIS.uniqnames.add("ferryk");
		CIS.uniqnames.add("jcaracc");
		CIS.uniqnames.add("tle");
		affiliates.add(CIS);
		
		SubjectAffiliates CSIB = new SubjectAffiliates();
		CSIB.subject = "CSIB";
		CSIB.campus = "A";
		CSIB.uniqnames.add("debiec");
		CSIB.uniqnames.add("ferryk");
		CSIB.uniqnames.add("jcaracc");
		CSIB.uniqnames.add("tle");
		affiliates.add(CSIB);
		
		SubjectAffiliates ES = new SubjectAffiliates();
		ES.subject = "ES";
		ES.campus = "A";
		ES.uniqnames.add("debiec");
		ES.uniqnames.add("ferryk");
		ES.uniqnames.add("jcaracc");
		ES.uniqnames.add("tle");
		affiliates.add(ES);
		
		SubjectAffiliates EMBA = new SubjectAffiliates();
		EMBA.subject = "EMBA";
		EMBA.campus = "A";
		EMBA.uniqnames.add("debiec");
		EMBA.uniqnames.add("ferryk");
		EMBA.uniqnames.add("jcaracc");
		EMBA.uniqnames.add("tle");
		affiliates.add(EMBA);
		
		SubjectAffiliates FIN = new SubjectAffiliates();
		FIN.subject = "FIN";
		FIN.campus = "A";
		FIN.uniqnames.add("debiec");
		FIN.uniqnames.add("ferryk");
		FIN.uniqnames.add("jcaracc");
		FIN.uniqnames.add("tle");
		affiliates.add(FIN);
		
		SubjectAffiliates LHC = new SubjectAffiliates();
		LHC.subject = "LHC";
		LHC.campus = "A";
		LHC.uniqnames.add("debiec");
		LHC.uniqnames.add("ferryk");
		LHC.uniqnames.add("jcaracc");
		LHC.uniqnames.add("tle");
		affiliates.add(LHC);
		
		SubjectAffiliates MKT = new SubjectAffiliates();
		MKT.subject = "MKT";
		MKT.campus = "A";
		MKT.uniqnames.add("debiec");
		MKT.uniqnames.add("ferryk");
		MKT.uniqnames.add("jcaracc");
		MKT.uniqnames.add("tle");
		affiliates.add(MKT);
		
		SubjectAffiliates OMS = new SubjectAffiliates();
		OMS.subject = "OMS";
		OMS.campus = "A";
		OMS.uniqnames.add("debiec");
		OMS.uniqnames.add("ferryk");
		OMS.uniqnames.add("jcaracc");
		OMS.uniqnames.add("tle");
		affiliates.add(OMS);
		
		SubjectAffiliates OM = new SubjectAffiliates();
		OM.subject = "OM";
		OM.campus = "A";
		OM.uniqnames.add("debiec");
		OM.uniqnames.add("ferryk");
		OM.uniqnames.add("jcaracc");
		OM.uniqnames.add("tle");
		affiliates.add(OM);
		
		SubjectAffiliates OB = new SubjectAffiliates();
		OB.subject = "OB";
		OB.campus = "A";
		OB.uniqnames.add("debiec");
		OB.uniqnames.add("ferryk");
		OB.uniqnames.add("jcaracc");
		OB.uniqnames.add("tle");
		affiliates.add(OB);
		
		SubjectAffiliates PC = new SubjectAffiliates();
		PC.subject = "PC";
		PC.campus = "A";
		PC.uniqnames.add("debiec");
		PC.uniqnames.add("ferryk");
		PC.uniqnames.add("jcaracc");
		PC.uniqnames.add("tle");
		affiliates.add(PC);
		
		SubjectAffiliates RE = new SubjectAffiliates();
		RE.subject = "RE";
		RE.campus = "A";
		RE.uniqnames.add("debiec");
		RE.uniqnames.add("ferryk");
		RE.uniqnames.add("jcaracc");
		RE.uniqnames.add("tle");
		affiliates.add(RE);
		
		SubjectAffiliates SMS = new SubjectAffiliates();
		SMS.subject = "SMS";
		SMS.campus = "A";
		SMS.uniqnames.add("debiec");
		SMS.uniqnames.add("ferryk");
		SMS.uniqnames.add("jcaracc");
		SMS.uniqnames.add("tle");
		affiliates.add(SMS);
		
		//Media Union Library
		
		SubjectAffiliates AEROSP = new SubjectAffiliates();
		AEROSP.subject = "AEROSP";
		AEROSP.campus = "A";
		AEROSP.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(AEROSP);
				
		SubjectAffiliates AMCULT = new SubjectAffiliates();
		AMCULT.subject = "AMCULT ";
		AMCULT.campus = "A";
		AMCULT.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(AMCULT);
				
		SubjectAffiliates ARCH = new SubjectAffiliates();
		ARCH.subject = "ARCH";
		ARCH.campus = "A";
		ARCH.uniqnames.add("aaelreserves@hotmail.com");
		ARCH.uniqnames.add("karbogas");
		affiliates.add(ARCH);
				
		SubjectAffiliates  ARTDES = new SubjectAffiliates();
		ARTDES.subject = "ARTDES";
		ARTDES.campus = "A";
		ARTDES.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(ARTDES);
				
		SubjectAffiliates AOSS = new SubjectAffiliates();
		AOSS.subject = "AOSS";
		AOSS.campus = "A";
		AOSS.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(AOSS);
				
		SubjectAffiliates AUTO = new SubjectAffiliates();
		AUTO.subject = "AUTO";
		AUTO.campus = "A";
		AUTO.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(AUTO);
				
		SubjectAffiliates BIOMEDE = new SubjectAffiliates();
		BIOMEDE.subject = "BIOMEDE";
		BIOMEDE.campus = "A";
		BIOMEDE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(BIOMEDE);
				
		SubjectAffiliates CHE = new SubjectAffiliates();
		CHE.subject = "CHE";
		CHE.campus = "A";
		CHE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(CHE);
				
		SubjectAffiliates CEE = new SubjectAffiliates();
		CEE.subject = "CEE";
		CEE.campus = "A";
		CEE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(CEE);
				
		SubjectAffiliates EECS = new SubjectAffiliates();
		EECS.subject = "EECS";
		EECS.campus = "A";
		EECS.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(EECS);
				
		SubjectAffiliates ENGR = new SubjectAffiliates();
		ENGR.subject = "ENGR";
		ENGR.campus = "A";
		ENGR.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(ENGR);
				
		SubjectAffiliates ENSCEN = new SubjectAffiliates();
		ENSCEN.subject = "ENSCEN";
		ENSCEN.campus = "A";
		ENSCEN.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(ENSCEN);
				
		SubjectAffiliates IOE = new SubjectAffiliates();
		IOE.subject = "IOE";
		IOE.campus = "A";
		IOE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(IOE);
				
		SubjectAffiliates MFG = new SubjectAffiliates();
		MFG.subject = " MFG";
		MFG.campus = "A";
		MFG.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MFG);
				
		SubjectAffiliates MATSCIE = new SubjectAffiliates();
		MATSCIE.subject = "MATSCIE";
		MATSCIE.campus = "A";
		MATSCIE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MATSCIE);
				
		SubjectAffiliates MATH = new SubjectAffiliates();
		MATH.subject = "MATH";
		MATH.campus = "A";
		MATH.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MATH);
				
		SubjectAffiliates MECHENG = new SubjectAffiliates();
		MECHENG.subject = "MECHENG";
		MECHENG.campus = "A";
		MECHENG.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MECHENG);
				
		SubjectAffiliates MOVESCI = new SubjectAffiliates();
		MOVESCI.subject = "MOVESCI";
		MOVESCI.campus = "A";
		MOVESCI.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MOVESCI);
				
		SubjectAffiliates MUSICOL = new SubjectAffiliates();
		MUSICOL.subject = "MUSICOL";
		MUSICOL.campus = "A";
		MUSICOL.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(MUSICOL);
				
		SubjectAffiliates NRE = new SubjectAffiliates();
		NRE.subject = "NRE";
		NRE.campus = "A";
		NRE.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(NRE);
				
		SubjectAffiliates NAVARCH = new SubjectAffiliates();
		NAVARCH.subject = "NAVARCH";
		NAVARCH.campus = "A";
		NAVARCH.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(NAVARCH);
				
		SubjectAffiliates NERS = new SubjectAffiliates();
		NERS.subject = "NERS";
		NERS.campus = "A";
		NERS.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(NERS);
				
		SubjectAffiliates PHRMACOL = new SubjectAffiliates();
		PHRMACOL.subject = "PHRMACOL";
		PHRMACOL.campus = "A";
		PHRMACOL.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(PHRMACOL);
				
		SubjectAffiliates STATS = new SubjectAffiliates();
		STATS.subject = "STATS";
		STATS.campus = "A";
		STATS.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(STATS);
				
		SubjectAffiliates TCHNCLCM = new SubjectAffiliates();
		TCHNCLCM.subject = "TCHNCLCM";
		TCHNCLCM.campus = "A";
		TCHNCLCM.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add( TCHNCLCM);
				
		SubjectAffiliates UD = new SubjectAffiliates();
		UD.subject = "UD";
		UD.campus = "A";
		UD.uniqnames.add("aaelreserves@hotmail.com");
		UD.uniqnames.add("karbogas");
		affiliates.add(UD);
		
		SubjectAffiliates UP = new SubjectAffiliates();
		UP.subject = "UP";
		UP.campus = "A";
		UP.uniqnames.add("aaelreserves@hotmail.com");
		UP.uniqnames.add("karbogas");
		affiliates.add(UP);
		
		SubjectAffiliates THEORY = new SubjectAffiliates();
		THEORY.subject = "THEORY";
		THEORY.campus = "A";
		THEORY.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(THEORY);
		
		SubjectAffiliates VOICELIT = new SubjectAffiliates();
		VOICELIT.subject = "VOICELIT";
		VOICELIT.campus = "A";
		VOICELIT.uniqnames.add("aaelreserves@hotmail.com");
		affiliates.add(VOICELIT);
		
		// public health sites
		SubjectAffiliates BIOSTAT = new SubjectAffiliates();
		BIOSTAT.subject = "BIOSTAT";
		BIOSTAT.campus = "A";
		BIOSTAT.uniqnames.add("dhunshe");
		affiliates.add(BIOSTAT);

		SubjectAffiliates EHS = new SubjectAffiliates();
		EHS.subject = "EHS";
		EHS.campus = "A";
		EHS.uniqnames.add("dhunshe");
		affiliates.add(EHS);

		SubjectAffiliates EPID = new SubjectAffiliates();
		EPID.subject = "EPID";
		EPID.campus = "A";
		EPID.uniqnames.add("dhunshe");
		affiliates.add(EPID);

		SubjectAffiliates HBEHED = new SubjectAffiliates();
		HBEHED.subject = "HBEHED";
		HBEHED.campus = "A";
		HBEHED.uniqnames.add("dhunshe");
		affiliates.add(HBEHED);

		SubjectAffiliates HMP = new SubjectAffiliates();
		HMP.subject = "HMP";
		HMP.campus = "A";
		HMP.uniqnames.add("dhunshe");
		affiliates.add(HMP);

		// school of social work
		SubjectAffiliates AG = new SubjectAffiliates();
		AG.subject = "AG";
		AG.campus = "A";
		AG.uniqnames.add("arall");
		AG.uniqnames.add("cherdt");
		affiliates.add(AG);
		
		SubjectAffiliates CHLDY = new SubjectAffiliates();
		CHLDY.subject = "CHLDY";
		CHLDY.campus = "A";
		CHLDY.uniqnames.add("arall");
		CHLDY.uniqnames.add("cherdt");
		affiliates.add(CHLDY);
		
		SubjectAffiliates CSS = new SubjectAffiliates();
		CSS.subject = "CSS";
		CSS.campus = "A";
		CSS.uniqnames.add("arall");
		CSS.uniqnames.add("cherdt");
		affiliates.add(CSS);
		
		SubjectAffiliates COMORG = new SubjectAffiliates();
		COMORG.subject = "COMORG";
		COMORG.campus = "A";
		COMORG.uniqnames.add("arall");
		COMORG.uniqnames.add("cherdt");
		affiliates.add(COMORG);
		
		SubjectAffiliates DOC = new SubjectAffiliates();
		DOC.subject = "DOC";
		DOC.campus = "A";
		DOC.uniqnames.add("arall");
		DOC.uniqnames.add("cherdt");
		affiliates.add(DOC);
	
		SubjectAffiliates EVAL = new SubjectAffiliates();
		EVAL.subject = "EVAL";
		EVAL.campus = "A";
		EVAL.uniqnames.add("arall");
		EVAL.uniqnames.add("cherdt");
		affiliates.add(EVAL);
		
		SubjectAffiliates HLTH = new SubjectAffiliates();
		HLTH.subject = "HLTH";
		HLTH.campus = "A";
		HLTH.uniqnames.add("arall");
		HLTH.uniqnames.add("cherdt");
		affiliates.add(HLTH);

		SubjectAffiliates HB = new SubjectAffiliates();
		HB.subject = "HB";
		HB.campus = "A";
		HB.uniqnames.add("arall");
		HB.uniqnames.add("cherdt");
		affiliates.add(HB);		
		
		SubjectAffiliates INTP = new SubjectAffiliates();
		INTP.subject = "INTP";
		INTP.campus = "A";
		INTP.uniqnames.add("arall");
		INTP.uniqnames.add("cherdt");
		affiliates.add(INTP);
		
		SubjectAffiliates MHS = new SubjectAffiliates();
		MHS.subject = "MHS";
		MHS.campus = "A";
		MHS.uniqnames.add("arall");
		MHS.uniqnames.add("cherdt");
		affiliates.add(MHS);
		
		SubjectAffiliates MHLTH = new SubjectAffiliates();
		MHLTH.subject = "MHLTH";
		MHLTH.campus = "A";
		MHLTH.uniqnames.add("arall");
		MHLTH.uniqnames.add("cherdt");
		affiliates.add(MHLTH);
		
		SubjectAffiliates PE = new SubjectAffiliates();
		PE.subject = "P&E";
		PE.campus = "A";
		PE.uniqnames.add("arall");
		PE.uniqnames.add("cherdt");
		affiliates.add(PE);
		
		SubjectAffiliates RES = new SubjectAffiliates();
		RES.subject = "RES";
		RES.campus = "A";
		RES.uniqnames.add("arall");
		RES.uniqnames.add("cherdt");
		affiliates.add(RES);
		
		SubjectAffiliates SWPS = new SubjectAffiliates();
		SWPS.subject = "SWPS";
		SWPS.campus = "A";
		SWPS.uniqnames.add("arall");
		SWPS.uniqnames.add("cherdt");
		affiliates.add(SWPS);
		
		SubjectAffiliates SOCWK = new SubjectAffiliates();
		SOCWK.subject = "SOCWK";
		SOCWK.campus = "A";
		SOCWK.uniqnames.add("arall");
		SOCWK.uniqnames.add("cherdt");
		affiliates.add(SOCWK);
		
		SubjectAffiliates SPEC = new SubjectAffiliates();
		SPEC.subject = "SPEC";
		SPEC.campus = "A";
		SPEC.uniqnames.add("arall");
		SPEC.uniqnames.add("cherdt");
		affiliates.add(SPEC);
		
		SubjectAffiliates UNDG = new SubjectAffiliates();
		UNDG.subject = "UNDG";
		UNDG.campus = "A";
		UNDG.uniqnames.add("arall");
		UNDG.uniqnames.add("cherdt");
		affiliates.add(UNDG);
		
		// College of Architecture and Urban Planning		
		SubjectAffiliates INTERHUM = new SubjectAffiliates();
		INTERHUM.subject = "INTERHUM";
		INTERHUM.campus = "A";
		INTERHUM.uniqnames.add("karbogas");
		affiliates.add(INTERHUM);
		
		SubjectAffiliates INTERNS = new SubjectAffiliates();
		INTERNS.subject = "INTERNS";
		INTERNS.campus = "A";
		INTERNS.uniqnames.add("karbogas");
		affiliates.add(INTERNS);
		
		SubjectAffiliates INTERSS = new SubjectAffiliates();
		INTERSS.subject = "INTERSS";
		INTERSS.campus = "A";
		INTERSS.uniqnames.add("karbogas");
		affiliates.add(INTERSS);
		
		// law school
		SubjectAffiliates LAW = new SubjectAffiliates();
		LAW.subject = "LAW";
		LAW.campus = "A";
		LAW.uniqnames.add("shawndel");
		affiliates.add(LAW);
		
		// School of Nursing
		SubjectAffiliates NURS = new SubjectAffiliates();
		NURS.subject = "NURS";
		NURS.campus = "A";
		NURS.uniqnames.add("gmoney");
		NURS.uniqnames.add("espring");
		NURS.uniqnames.add("degroote");
		NURS.uniqnames.add("tkm");
		NURS.uniqnames.add("craftd");
		NURS.uniqnames.add("antoniom");
		NURS.uniqnames.add("jlmcb");
		affiliates.add(NURS);
		
		state.setAttribute(STATE_SUBJECT_AFFILIATES, affiliates);
		
	} //setupSubjectAffiliates
	
	/**
	 * @params - SessionState state
	* @params - String subject is the University's Subject code
	* @return - Collection of uniqnames of affiliates for this subject
	*/
	private Collection getSubjectAffiliates(SessionState state, String subject)
	{
		Collection rv = null;
		List allAffiliates = (Vector) state.getAttribute(STATE_SUBJECT_AFFILIATES);
		
		//iterate through the subjects looking for this subject
		for (Iterator i = allAffiliates.iterator(); i.hasNext(); )
		{
			SubjectAffiliates sa = (SubjectAffiliates)i.next();
			if(sa.subject.equals(subject)) return sa.uniqnames;
		}
		return rv;
		
	} //getSubjectAffiliates
	
	/**
	*  setupSkins
	* 
	*/
	private void setupSkins(SessionState state)
	{
		List skins = new Vector();
		
		Skin chef = new Skin();
		chef.css = COURSE_PUBLISHED_UM_SKIN;
		chef.unit = "None";
		skins.add(chef);
		Skin arc = new Skin();
		arc.css = "arc.css";
		arc.unit = "College of Architecture and Urban Planning";
		skins.add(arc);
		Skin art = new Skin();
		art.css = "art.css";
		art.unit =  "School of Art and Design";
		skins.add(art);
		Skin bus = new Skin();
		bus.css = "bus.css";
		bus.unit =  "Business School";
		skins.add(bus);
		Skin den = new Skin();
		den.css = "den.css";
		den.unit = "School of Dentistry";
		skins.add(den);
		Skin edu = new Skin();
		edu.css = "edu.css";
		edu.unit = "School of Education";
		skins.add(edu);
		Skin eng = new Skin();
		eng.css = "eng.css";
		eng.unit = "College of Engineering";
		skins.add(eng);
		Skin inf = new Skin();
		inf.css = "inf.css";
		inf.unit =  "School of Information";
		skins.add(inf);
		Skin kin = new Skin();
		kin.css = "kin.css";
		kin.unit =  "Division of Kinesiology";
		skins.add(kin);
		Skin law = new Skin();
		law.css = "law.css";
		law.unit = "Law School";
		skins.add(law);
		Skin lsa = new Skin();
		lsa.css = "lsa.css";
		lsa.unit = "LS&A";
		skins.add(lsa);
		Skin med = new Skin();
		med.css = "med.css";
		med.unit = "Medical School";
		skins.add(med);
		Skin mus = new Skin();
		mus.css = "mus.css";
		mus.unit = "School of Music";
		skins.add(mus);
		Skin nre = new Skin();
		nre.css = "nre.css";
		nre.unit = "School of Natural Resources and Environment";
		skins.add(nre);
		Skin nur = new Skin();
		nur.css = "nur.css";
		nur.unit = "School of Nursing";
		skins.add(nur);
		Skin off = new Skin();
		off.css = "off.css";
		off.unit = "Officer Education Programs";
		skins.add(off);
		Skin pha = new Skin();
		pha.css = "pha.css";
		pha.unit = "College of Pharmacy";
		skins.add(pha);
		Skin sph = new Skin();
		sph.css = "sph.css";
		sph.unit = "School of Public Health";
		skins.add(sph);
		Skin spp = new Skin();
		spp.css = "spp.css";
		spp.unit = "School of Public Policy";
		skins.add(spp);
		Skin rac = new Skin();
		rac.css = "rac.css";
		rac.unit =  "Rackham School of Graduate Studies";
		Skin ssw = new Skin();
		ssw.css = "ssw.css";
		ssw.unit = "School of Social Work";
		skins.add(ssw);
		Skin umd = new Skin();
		umd.css = "umd.css";
		umd.unit = "UM Dearborn";
		skins.add(umd);
		
		state.setAttribute("skins", skins);
	
	} // setupSkins
	
	/**
	*  init
	* 
	*/
	private void init (VelocityPortlet portlet, RunData data, SessionState state)
	{
		ParameterParser params = data.getParameters ();
		state.setAttribute(STATE_ACTION, "SiteAction");
		setupFormNamesAndConstants(state);
		setupSkins(state);
		setupTerms(state);
		setupToolOrder(state);
		setupToolLists(state);
		setupSubjectAffiliates(state);
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

			 ResourceProperties rp = site.getProperties();

			 if (rp.getProperty(PROP_SITE_TYPE) != null)
			 {
				 state.setAttribute(STATE_SITE_TYPE, rp.getProperty(PROP_SITE_TYPE));
			 }
			 else
			 {
				 state.setAttribute(STATE_SITE_TYPE, SITE_TYPE_UNDEFINED);
			 }
			 
			 // set site course list
			 setCourseSectionList(state);
			
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
		
		// string keys selected from list
		List umiacCourseSelectionList = (Vector) state.getAttribute("umiacCourseSelectionList");
		List manualCourseSelectionList = (Vector) state.getAttribute("manualCourseSelectionList");
		
		// course list items selected
		List umiacCourseSectionList = (Vector) state.getAttribute("umiacCourseSectionList");
		List manualCourseSectionList = (Vector) state.getAttribute("manualCourseSectionList");
		
		Vector idsSelected = new Vector();
		SiteInfo siteInfo = new SiteInfo();
		
		switch (index)
		{
			case 0: 
				/* actionForTemplate chef_site-list.vm
				*
				* actionForTemplate chef_site-list.vm
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
				state.setAttribute(STATE_SITE_TYPE, SITE_TYPE_COURSE);
				updateProfile(params, state);
				break;
			case 3: 
				/* actionForTemplate chef_site-courseClass.vm
				*/
				if (forward)
				{
					List umiacChosenList = new Vector();
					if (params.getStrings("umiacAdds") == null && params.getString("manualAdds") == null)
					{
						addAlert(state, "Please select the course from the list or choose to manually input the course information. ");
					}
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
						// The list of courses selected from UMIAC listing
						if (params.getStrings("umiacAdds") != null)
						{
							umiacChosenList = new ArrayList(Arrays.asList(params.getStrings("umiacAdds"))); // list of CourseListItem keys
							state.setAttribute(STATE_ADD_CLASS_UMIAC, umiacChosenList);
							if (state.getAttribute(STATE_SITE_INSTANCE) != null)
							{
								doAdd_course_section(state, params, STATE_ADD_CLASS_UMIAC, STATE_ADD_CLASS_MANUAL);
							}
							else
							{
								doAdd_course_section(state, params, "umiacCourseSectionList", "manualCourseSectionList");
								state.setAttribute("selectedUmiacCourseSection", umiacChosenList);
							}
						}

						if (state.getAttribute(STATE_MESSAGE) == null)
						{	
							if (params.getString("manualAdds") != null)
							{
								state.setAttribute(STATE_MANUAL_ADD, Boolean.TRUE);
								state.setAttribute(STATE_AUTO_MANUAL_ADD, Boolean.TRUE);
								if (state.getAttribute(STATE_SITE_INSTANCE) != null)
								{
									// if revising a site, go to the manually add section page
									state.setAttribute(STATE_TEMPLATE_INDEX, "18");
								}
								else
								{
									// if creating a new site
									state.setAttribute(STATE_TEMPLATE_INDEX, "39");
								}
							}
							else
							{
								state.setAttribute(STATE_MANUAL_ADD, Boolean.FALSE);
								state.setAttribute(STATE_AUTO_MANUAL_ADD, Boolean.FALSE);
								if (state.getAttribute(STATE_SITE_INSTANCE) != null)
								{
									// if revising a site, go to the confirmation page of adding classes
									state.setAttribute(STATE_TEMPLATE_INDEX, "58");
								}
								else
								{
									// if creating a site, go the the site information entry page
									state.setAttribute(STATE_TEMPLATE_INDEX, "11");
								}
							}
						}
					}
				}
				break;
			case 4: 
				// actionForTemplate chef_site-courseFeatures.vm
				//
				if (forward)
				{
					getFeatures(params, state);
				}
				break;
			case 5: 
				/* actionForTemplate chef_site-dissertationContact.vm
				*  Not implemented
				*/
				state.setAttribute(STATE_SITE_TYPE, SITE_TYPE_GRADTOOLS_STUDENT);
				updateProfile(params, state);
				break;
			case 6: 
				/* actionForTemplate chef_site-dissertationProgram.vm
				*  Not implemented
				*/
				break;
			case 7: 
				/* actionForTemplate chef_site-dissertationFeatures.vm
				*  Not implemented
				*/
				getFeatures(params, state);
				if(forward) commitSite(state, Site.SITE_STATUS_UNPUBLISHED);
				break;
			case 8: 
				/* actionForTemplate chef_site-projectContact.vm
				*  Not implemented
				*/
				state.setAttribute(STATE_SITE_TYPE, SITE_TYPE_PROJECT);
				updateProfile(params, state);
				break;
			case 9: 
				/* actionForTemplate chef_site-projectInformation.vm
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
				/* actionForTemplate chef_site-projectFeatures.vm
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
				updateSiteInfo(params, state);
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
				if (forward)
				{
					readCourseSectionInfo(state, params);
					//read the request uniqname
					String uniqname = StringUtil.trimToNull(params.getString("uniqname"));
					state.setAttribute(STATE_SITE_QUEST_UNIQNAME, uniqname);
					if (uniqname == null)
					{
						addAlert(state, "Please enter a uniqname.");
					}
					else
					{
						try
						{
							User instructor = UserDirectoryService.getUser(uniqname);
							doAdd_course_section(state, params, STATE_ADD_CLASS_UMIAC, STATE_ADD_CLASS_MANUAL);
						}
						catch (IdUnusedException e)
						{
							addAlert(state, "Please enter a valid uniqname for the instructor of record. ");
						}
					}
					if (state.getAttribute(STATE_MESSAGE) == null)
					{
						state.setAttribute(STATE_TEMPLATE_INDEX, "58");
					}
				}
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
					state.removeAttribute(STATE_UNREMOVEABLE_USER_LIST);
					
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
				/* actionForTemplate chef_site-confirmSiteDelete.vm
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
				*
				*/
				updateSiteInfo(params, state);
				if (forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
				}
				break;
			case 34:
				/* actionForTemplate chef_site-publishUnpublish.vm
				*
				*/
				updateSiteInfo(params, state);
				break;
			case 35:
				/* actionForTemplate chef_site-rosterSort.vm
				*
				*/
				updateSiteInfo(params, state);
				if (forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
				}
				break;
			case 36:
				/* actionForTemplate chef_site-addMyWorkspaceFeature.vm
				*
				*/
				updateSiteInfo(params, state);
				if (forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
				}
				break;
			case 37:
				/* actionForTemplate chef_site-removeMyWorkspaceFeature.vm
				*
				*/
				updateSiteInfo(params, state);
				if (forward) 
				{
					updateSiteProperties(state);
					updateSiteAttributes(state);
				}
				break;
			case 38:
				/* actionForTemplate chef_site-projectConfirm.vm
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
						
						String term = StringUtil.trimToNull(params.getString("term"));
						if (term != null)
						{
							state.setAttribute(FORM_SITEINFO_TERM, term);
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
			case 48:
				/* actionForTemplate chef_site-gradtoolsConfirm.vm
				*
				*/
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
				if (forward)
				{
					boolean umiacDeletes = (params.getStrings("umiacDeletes") == null) ? false : true;
					if (umiacDeletes)
					{
						// build the deletions list
						Vector umiacDeletionList = new Vector();
						List umiacChosenList = new ArrayList(Arrays.asList(params.getStrings("umiacDeletes")));
						for (ListIterator i = umiacChosenList.listIterator(); i.hasNext(); )
						{
							String umiacId = (String) i.next();
							for (ListIterator j = umiacCourseSectionList.listIterator(); j.hasNext(); )
							{
								CourseListItem umiacCLI = (CourseListItem) j.next();
								String umiacCourseSectionListId = (String) umiacCLI.getKey();
								if (umiacId.equals(umiacCourseSectionListId))
								{
									umiacDeletionList.add(umiacCLI);
								}
							}
						}
						state.setAttribute(STATE_REMOVE_CLASS_UMIAC, umiacDeletionList);
					}
					boolean manualDeletes = (params.getStrings("manualDeletes") == null) ? false : true;
					if(manualDeletes)
					{
						// build the deletions list
						Vector manualDeletionList = new Vector();
						List manualChosenList = new ArrayList(Arrays.asList(params.getStrings("manualDeletes")));
						for (ListIterator i = manualChosenList.listIterator(); i.hasNext(); )
						{
							String manualId = (String) i.next();
							for (ListIterator j = manualCourseSectionList.listIterator(); j.hasNext(); )
							{
								CourseListItem manualCLI = (CourseListItem) j.next();
								String manualCourseSectionListId = (String) manualCLI.getKey();
								if (manualId.equals(manualCourseSectionListId))
								{
									manualDeletionList.add(manualCLI);
								}
							}
						}
						state.setAttribute(STATE_REMOVE_CLASS_MANUAL, manualDeletionList);
					}
					if (!umiacDeletes && !manualDeletes)
					{
						addAlert(state, "Please choose class(es) to delete.");
					}
				}
				else
				{
					removeRemoveClassContext(state);
					removeSiteEditFromState(state);
				}
				break;
			case 58:
				/*  actionForTemplate chef_site-addRemoveClassConfirm.vm
				* 
				*/
				if (forward)
				{
					if (state.getAttribute(STATE_REMOVE_CLASS_UMIAC) != null || state.getAttribute(STATE_REMOVE_CLASS_MANUAL) != null)
					{
						deleteCourseSection(state);
						finishSectionAccess(state, new Vector(), new Vector());
						// clean state objects
						removeRemoveClassContext(state);
						
					}
					else if (state.getAttribute(STATE_ADD_CLASS_UMIAC) != null || state.getAttribute(STATE_ADD_CLASS_MANUAL) != null)
					{
						List addedUmiacList = (state.getAttribute(STATE_ADD_CLASS_UMIAC) == null)?new Vector():(List) state.getAttribute(STATE_ADD_CLASS_UMIAC);
						List umiacList = (state.getAttribute("umiacCourseSectionList") == null)?new Vector():(List) state.getAttribute("umiacCourseSectionList");
						umiacList.addAll(addedUmiacList);
						state.setAttribute("umiacCourseSectionList", umiacList);
						
						List addedManualList = (state.getAttribute(STATE_ADD_CLASS_MANUAL) == null)?new Vector():(List) state.getAttribute(STATE_ADD_CLASS_MANUAL);
						List manualList = (state.getAttribute("manualCourseSectionList") == null)?new Vector():(List) state.getAttribute("manualCourseSectionList");
						manualList.addAll(addedManualList);
						state.setAttribute("manualCourseSectionList", manualList);
						
						finishSectionAccess(state, addedUmiacList, addedManualList);
						
						// clean state objects
						removeAddClassContext(state);
					}
				}
				break;
			case 59: 
				/*  actionForTemplate chef_site-addClass-pickTerm.vm
				* 
				*/
				if (forward)
				{
					// start out with fresh course listings
					state.removeAttribute(STATE_TERM_COURSE_SECTION_LIST);
		
					// manual add or auto add?
					List sectionList = getTermCourseSectionList(state, params);
					if (sectionList.size() > 0)
					{
						// there is at least one section in specified term with the current user as instructor
						// go to the automatically-populated course site creation page
						state.setAttribute(STATE_AUTO_ADD, Boolean.TRUE);
						state.setAttribute(STATE_TEMPLATE_INDEX, "3");		
					}
					else
					{
						// there is no course 
						// go to the manual creation page
						state.setAttribute(STATE_MANUAL_ADD, Boolean.TRUE);
						state.setAttribute(STATE_MANUAL_ADD_COURSE_NUMBER, new Integer("1"));
						state.setAttribute(STATE_TEMPLATE_INDEX, "18");
					}
				}
				else
				{
					removeAddClassContext(state);
					removeSiteEditFromState(state);
				}
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
	* %%% legacy, references to which need to be cleaned up and then this removed
	*
	*/
	private void updateProfile (ParameterParser params, SessionState state)
	{
		Profile profile = (Profile) state.getAttribute(STATE_PROFILE);
		if (params.getString ("institution") != null)
		{
			profile.institution = params.getString ("institution");
		}
		if (params.getString ("affiliation") != null)
		{
			profile.affiliation = Integer.parseInt(params.getString ("affiliation"));
		}
		if (params.getString ("honorific") != null)
		{
			profile.honorific = Integer.parseInt(params.getString ("honorific")); 
		}
		if (params.getString ("office") != null)
		{
			profile.office = params.getString ("office");
		}
		if (params.getString ("phone") != null)
		{
			profile.phone = params.getString ("phone");
		}
		if (params.getString ("email") != null)
		{
			profile.email = params.getString ("email");
		}
		state.setAttribute(STATE_PROFILE, profile);
		
	} // updateProfile
	
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
		
		//if a GradTools site use pre-defined site info and exclude from public listing
		if(siteInfo.site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
		{
			User currentUser = UserDirectoryService.getCurrentUser();
			siteInfo.title = "Grad Tools - " + currentUser.getId();
			siteInfo.description = "Grad Tools site for  " + currentUser.getDisplayName();
			siteInfo.short_description = "Grad Tools - " + currentUser.getId();
			siteInfo.include = false;
		}
		else
		{
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
			if (params.getString ("course") != null)
			{
				siteInfo.course = params.getString ("course");
			}		
			if (params.getString ("section") != null)
			{
				siteInfo.sections = params.getString ("section");
			}
			if (params.getString ("term") != null)
			{
				siteInfo.term = Integer.parseInt(params.getString ("term"));
				state.setAttribute(STATE_TERM_REQUEST, params.getString ("term"));
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
			if(choice.equalsIgnoreCase("ctng.home"))
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
				wSetupHome.toolId = "ctng.home";
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
					ResourceProperties rp = toolRegFound.getDefaultConfiguration();
					ResourcePropertiesEdit rpe = tool.getPropertiesEdit();
					rpe.addAll(rp); // set tool properties to the default tool configuration
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
				else if (toolId.equalsIgnoreCase("ctng.home"))
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
	* addGradToolsFeatures adds features to a new Grad Tools site
	*
	*/
	private void addGradToolsFeatures(SessionState state)
	{
		//try adding a new site
		SiteEdit site = null;
		String id = IdService.getUniqueId();
		
		if(SiteService.allowAddSite(id))
		{
			try
			{
				site = SiteService.addSite(id);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures addSite(" + id + ") " + e);
				}
			}
		}
		if(site==null)
		{
			addAlert(state, "Unable to configurate a Grad Tools site.");
			return;
		}
		
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		
		// set site properties
		site.setTitle(siteInfo.title);
		site.setSkin(PROJECT_PUBLISHED_SKIN);
		site.setStatus(Site.SITE_STATUS_PUBLISHED);
		ResourcePropertiesEdit siteRp = site.getPropertiesEdit();
		siteRp.addProperty(PROP_SITE_SHORT_DESCRIPTION, siteInfo.short_description);
		siteRp.addProperty(PROP_SITE_TYPE, SITE_TYPE_GRADTOOLS_STUDENT);
		siteRp.addProperty(PROP_SITE_SKIN, PROJECT_PUBLISHED_SKIN);
		siteRp.addProperty(PROP_SITE_TERM, Integer.toString(siteInfo.term));
		siteRp.addProperty(PROP_SITE_INCLUDE, (new Boolean(false).toString()));	 
		
		// Grad Tools site has a preset menu of tools, so just do it all here and now
		ResourceProperties rp = null;
		ResourcePropertiesEdit rpe = null;
		ToolRegistration tr = null;
		SitePageEdit page = null;
		ToolConfigurationEdit tool = null;
		
		// %%% this should go in an configuration file
		try
		{
			/** About Grad Tools */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.aboutGradTools");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures About Grad Tools " + e);
				}
			}

			/** Dissertation Checklist */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.dissertation");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Dissertation Checklist " + e);
				}
			}

			/** Schedule */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.schedule");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
				//rpe.addProperty("MergedCalendarReferences", "/calendar/calendar/" + id + "/main_,_/calendar/calendar/rackham/main");
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Schedule " + e);
				}
			}
			
			/** Resources */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.resources");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Resources " + e);
				}
			}
			
			/** Discussion */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.discussion");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Discussion " + e);
				}
			}
			
			/** Chat */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.chat");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Chat " + e);
				}
			}

			/** Email Archive */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.mailbox");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Email Archive " + e);
				}
			}
			
			/** Site Info */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.siteinfo");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Site Info " + e);
				}
			}

			/** Grad Tools Help */
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.gradToolsHelp");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Grad Tools Help " + e);
				}
			}
			
			/** Help %%% removed for now
			try
			{
				tr = ServerConfigurationService.getToolRegistration("chef.contactSupport");
				page = site.addPage();
				page.setTitle(tr.getTitle());
				page.setLayout(SitePage.LAYOUT_SINGLE_COL);
				tool = page.addTool();
				tool.setToolId(tr.getId());
				tool.setTitle(tr.getTitle());
				tool.setLayoutHints("0,0");
				rp = tr.getDefaultConfiguration();
				rpe = tool.getPropertiesEdit();
				rpe.addAll(rp);
			}
			catch(Exception e)
			{
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures Help " + e);
				}
			}
			**/
		
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
			try
			{
				SiteService.commitEdit(site);
			}
			catch(Exception e)
			{
				SiteService.cancelEdit(site);
				
				if(Log.isWarnEnabled())
				{
					Log.warn("chef", this + ".addGradToolsFeatures commitEdit " + e);
				}
			}
		}
		catch(Exception e)
		{
			if(Log.isWarnEnabled())
			{
				Log.warn("chef", this + ".addGradToolsFeatures " + e);
			}
		}

	} // addGradToolsFeatures

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
				if (toolId.equalsIgnoreCase("ctng.home"))
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
				}
				else if (!toolId.equalsIgnoreCase("chef.siteinfo") && !toolId.equalsIgnoreCase("chef.contactSupport"))
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
				// Home is a special case, with several tools on the page. "ctng.home" is hard coded in chef_site-addRemoveFeatures.vm.
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

					tool.setTitle("Worksite Information");
					tool.setLayoutHints("0,0");
					tool.getPropertiesEdit().addProperty("special", "worksite");

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
		siteInfo.setToolId("chef.siteinfo");
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
	
	/*
	* buildCourseListItem
	*
	* takes a Vector of String[] from UmiacClient getInstructorClasses 
	* and returns a CourseListItem, one per String[]
	* Should be replaced by InstructorSection which reflects the
	* UMIAC output more closely.
	*
	* year = res[0]
	* term = res[1]
	* campus = res[2]
	* subject = res[3]
	* catalog_nbr = res[4]
	* title = res[5]
	* legacy = res[6], which is always blank
	* class_section = res[7]
	* url = res[8]
	* units_taken = res[9] (is blank for Instructors)
	* component = res[10]; (LEC, DIS, LAB, etc.)
	* role = res[11]
	* subrole = res[12]
	* enrl_status = res[13] 
	*/
	public CourseListItem buildCourseListItem(SessionState state, String[] res)
	{
		CourseListItem course = new CourseListItem();
		try
		{
			course.year = res[0];
			course.term_code = res[1];
			course.campus_code = res[2];
			course.subject = res[3];
			course.catalog_nbr = res[4];
			course.class_section = res[5];
			course.title = res[6];
			course.url = res[7];
			course.component = res[8]; //(LEC, DIS, LAB, etc.)
			course.role = res[9];
			course.subrole = res[10];
			course.key = res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "," + res[5];
			// if course is crosslisted, a "CL" is added at the end
			if (res.length == 12)
			{
				course.crosslist = "CL";
			}
			else
			{
				course.crosslist = "";
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// If UMIAC returns an error message the array will have res[0] as the message and no other elements
			addAlert(state, res[0] + " message from UMIAC. Please ask CourseTools NG Support for help.");
			Log.warn("chef", "SiteAction.buildCourseListItem ArrayIndexOutOfBoundsException - UMIAC message " + res[0]);
		}
		return course;
		
	} // buildCourseListItem
	
	public void finishSiteEdit(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = site.getProperties().getProperty(PROP_SITE_TYPE);
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				site.setSkin(COURSE_UNPUBLISHED_SKIN);
			}
			else
			{
				site.setSkin(PROJECT_UNPUBLISHED_SKIN);
			}
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				site.setSkin(siteInfo.skin);
			}
			else
			{
				site.setSkin(PROJECT_PUBLISHED_SKIN);
			}
		}
		
		SiteService.commitEdit(site);
		
	} // finishSiteEdit
	
	public void saveSiteStatus(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = site.getProperties().getProperty(PROP_SITE_TYPE);
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				site.setSkin(COURSE_UNPUBLISHED_SKIN);
			}
			else
			{
				site.setSkin(PROJECT_UNPUBLISHED_SKIN);
			}
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				if(siteInfo.skin.equals(NULL_STRING))
				{
					site.setSkin(COURSE_PUBLISHED_UM_SKIN);
				}
				else
				{
					site.setSkin(siteInfo.skin);
				}
			}
			else
			{
				site.setSkin(PROJECT_PUBLISHED_SKIN);
			}
		}
		
	} // saveSiteStatus

	public void commitSite(SessionState state, int status)
	{
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		site.setStatus(status);
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String site_type = (String)state.getAttribute(STATE_SITE_TYPE);
		
		//%% debugging
		/*if(site_type == null || !(site_type.equals(SITE_TYPE_COURSE) || site_type.equals(SITE_TYPE_PROJECT)))
		{
			addAlert(state, "commitSite site type error");
			return;
		}*/
		
		String skin = NULL_STRING;
		String id = site.getId();
		site.setStatus(status);
		if(status == Site.SITE_STATUS_UNPUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				site.setSkin(COURSE_UNPUBLISHED_SKIN);
			}
			else
			{
				site.setSkin(PROJECT_UNPUBLISHED_SKIN);
			}
		}
		else if(status == Site.SITE_STATUS_PUBLISHED)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				if(siteInfo.skin.equals(NULL_STRING))
				{
					site.setSkin(COURSE_PUBLISHED_UM_SKIN);
				}
				else
				{
					site.setSkin(siteInfo.skin);
				}
			}
			else
			{
				site.setSkin(PROJECT_PUBLISHED_SKIN);
			}
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
			if(siteInfo.site_type != null)  rpe.addProperty(PROP_SITE_TYPE, siteInfo.site_type);
			if(siteInfo.skin != null) rpe.addProperty(PROP_SITE_SKIN, siteInfo.skin);
			rpe.addProperty(PROP_SITE_TERM, Integer.toString(siteInfo.term));
			rpe.addProperty(PROP_SITE_SHORT_DESCRIPTION, siteInfo.short_description);
			if (siteInfo.site_type.equals(SITE_TYPE_PROJECT))
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
	
	private void addSubjectAffliates(SessionState state, List umiacCourseSectionList)
	{
		
		Vector subjAffiliates = new Vector();
		Vector affiliates = new Vector();
		String subject = "";
		String affiliate = "";
		
		//get all subject and campus pairs for this site
		for (ListIterator i = umiacCourseSectionList.listIterator(); i.hasNext(); )
		{
			CourseListItem cli = (CourseListItem) i.next();
			if(cli.subject != null && cli.subject != "") subject = cli.subject;
			
			//handle the special case of a UMD subject
			if(cli.campus_code.equals("D")) subject = subject + "_D";
			subjAffiliates.add(subject);
		}
		
		// remove duplicates
		Collection noDups = new HashSet(subjAffiliates);
		
		// get affliates for subjects
		for (Iterator i = noDups.iterator(); i.hasNext(); )
		{
			subject = (String)i.next();
			
			Collection uniqnames = getSubjectAffiliates(state, subject);
			try
			{
				affiliates.addAll(uniqnames);
			}
			catch(Exception ignore){}
		}
		
		// remove duplicates
		Collection addAffiliates = new HashSet(affiliates);
		
		// try to add uniqnames with appropriate role
		for (Iterator i = addAffiliates.iterator(); i.hasNext(); )
		{
			affiliate = (String)i.next();
			try
			{
				User user = UserDirectoryService.getUser(affiliate);
				String realmId = "/site/" + ((SiteEdit) state.getAttribute(STATE_SITE_INSTANCE)).getId();
				if (RealmService.allowUpdateRealm(realmId))
				{
					try
					{
						RealmEdit realmEdit = RealmService.editRealm(realmId);
						Role role = realmEdit.getRole("Affiliate");
						realmEdit.addUserRole(user, role);
						RealmService.commitEdit(realmEdit);
					}
					catch(Exception ignore) {}
				}
			}
			catch(Exception ignore){}
		}
		
	} //addSubjectAffliates
	
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
			addAlert(state, "Please enter " + UNIVERSITY_NAME  + " uniqname(s) or " + UNIVERSITY_NAME + " friend account(s) to add to this site.");
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
					if(uniqname.indexOf(at) != -1)
					{
						addAlert(state, uniqname + " is not a valid uniqname. Please check the uniqname or enter this users email address as " + UNIVERSITY_NAME + " friend account to create a new account");
					}
					else
					{
						Participant participant = new Participant();
						try
						{
							User u = UserDirectoryService.getUser(uniqname);
							participant.name = u.getDisplayName();
							participant.uniqname = uniqname;
							pList.add(participant);
							
							// no role selected yet
							//selectedRoles.put(uniqname, "no_role_selected");
						}
						catch (IdUnusedException e) 
						{
							addAlert(state, uniqname + " is not a valid " + UNIVERSITY_NAME + " uniqname. ");
						}
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
			
					if(friend.indexOf(at) == -1 || friend.indexOf("umich.edu") != -1)
					{
						// must be a valid, non university email address	
						addAlert(state, "Friend account " + friend + " does not have a valid Non-" + UNIVERSITY_NAME + " email address. ");
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
		String message_subject = PRODUCTION_SITE_NAME + " Site Notification";
		String content = "";
		StringBuffer buf = new StringBuffer();
		buf.setLength(0);
		
		// email body differs between newly added friend account and other users
		buf.append(userName + ":\n\n");
		buf.append("You have been added to the following " + PRODUCTION_SITE_NAME + " site:\n" + siteTitle + "\n" + "by " + UserDirectoryService.getCurrentUser().getDisplayName() + ". \n\n");
		if (newFriend)
		{
			// for friend account
			buf.append("To log in, you must first get a Friend Account. A Friend Account is a special kind of account that is used to give non-" + UNIVERSITY_FULL_NAME + " members access to the general " + UNIVERSITY_NAME + " web environment. ");
			buf.append("The " + UNIVERSITY_NAME + " web environment includes many tools and services, one of which is " + PRODUCTION_SITE_NAME + ". \n\n");
			buf.append("To get a Friend Account, open the following site:\n" + APPLY_FRIEND_ACCOUNT_URL + "\n");
			buf.append("and follow the steps listed.\n\n");
			buf.append("Once you have your friend account, you can log in to " + PRODUCTION_SITE_NAME + " as follows:\n");
			buf.append("1. Open " + PRODUCTION_SITE_NAME + ":\n" + PRODUCTION_SITE_URL + "\n");
			buf.append("2. Click the Login button.\n");
			buf.append("3. Type your Friend account login and password, and click Login. \n");
		}
		else
		{
			buf.append("To log in:\n");
			buf.append("1. Open " + PRODUCTION_SITE_NAME + ":\n" + PRODUCTION_SITE_URL + "\n");
			buf.append("2. Click the Login button.\n");
			buf.append("3. Type your uniqname and password, and click Login. \n");
		}
		buf.append("4. To go to the site, click on the site tab. (You will see two or more tabs in a row across the upper part of the screen.)");
			
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
	* get the title for newly added site, store it in SiteInfo object
	* 
	*/
	private void setTitleForNewSite(SessionState state)
	{
		List umiacCourseSectionList = (Vector) state.getAttribute("umiacCourseSectionList");
		List manualCourseSectionList = (Vector) state.getAttribute("manualCourseSectionList");
		String tab = NULL_STRING;
		
		// If cleanState() has removed SiteInfo, get a new instance into state
		SiteInfo siteInfo = new SiteInfo();
		if (state.getAttribute(STATE_SITE_INFO) != null)
		{
			siteInfo = (SiteInfo) state.getAttribute(STATE_SITE_INFO);	
		}
		
		//add a new site
		String id = siteInfo.site_id;
		
		if (((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
		{	
			if ((umiacCourseSectionList != null) && (umiacCourseSectionList.size() != 0))
			{
				String providerRealm = buildExternalRealm(id, state, umiacCourseSectionList);
				tab = getCourseTab(state, providerRealm);
			} // official course list
			else if ((manualCourseSectionList != null) && (manualCourseSectionList.size() != 0))
			{
				// for a requested course site, get subject, catalog_nbr, and section(s) from request
				CourseListItem cli = (CourseListItem) manualCourseSectionList.get(0);
				tab = cli.subject + " " + cli.catalog_nbr + " " + cli.class_section;
			}
	
			siteInfo.title = tab;
		}
		
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		
	}	// setTitleForNewSite
	
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
				if (siteInfo.site_type.equals(SITE_TYPE_COURSE))
				{
					skin = COURSE_UNPUBLISHED_SKIN;
				}
				else
				{
					skin = PROJECT_UNPUBLISHED_SKIN;
				}

				site.setSkin(skin);
				site.setDescription(description);
				if (title != null)
				{
					site.setTitle(title);
				}
				
				ResourcePropertiesEdit rp = site.getPropertiesEdit();
				rp.addProperty(PROP_SITE_SHORT_DESCRIPTION, siteInfo.short_description);
				rp.addProperty(PROP_SITE_TYPE, siteInfo.site_type);
				rp.addProperty(PROP_SITE_SKIN, siteInfo.skin);
				rp.addProperty(PROP_SITE_TERM, Integer.toString(siteInfo.term));
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
		if(filter.equals(MY_PROJECT_SITES))
		{
			sites = SiteService.getAllowedSites(false, false, SITE_TYPE_PROJECT);
		}
		else if (filter.equals(MY_COURSE_SITES))
		{
			sites = SiteService.getAllowedSites(false, false, SITE_TYPE_COURSE);
		}
		else if (filter.equals(ALL_MY_SITES))
		{
			sites = SiteService.getAllowedSites(false, false, null);
			if (sMyWorkspace != null)
			{
				sites.add(sMyWorkspace);
			}
		}
		else if (filter.equals(MY_WORKSPACE))
		{
			if (sMyWorkspace != null)
			{
				sites.add(sMyWorkspace);
			}
		}
		else
		{
			Log.warn("chef", this + ".filterSiteList: unrecognized site list view select value");
		}
		
		return sites;
		
	} //filterSiteList
	
	/**
	* 
	* 
	*/
	private void finishCourseSite (SessionState state, ParameterParser params)
	{
		List umiacCourseSectionList = (Vector) state.getAttribute("umiacCourseSectionList");
		List manualCourseSectionList = (Vector) state.getAttribute("manualCourseSectionList");
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		String realm = "/site/" + id;
		
		if (((String)state.getAttribute(STATE_SITE_TYPE)).equals(SITE_TYPE_COURSE))
		{
			if ((umiacCourseSectionList != null) && (umiacCourseSectionList.size() != 0))
			{
				String providerRealm = buildExternalRealm(id, state, umiacCourseSectionList);
				try
				{
					RealmEdit realmEdit = RealmService.editRealm(realm);
					realmEdit.setProviderRealmId(providerRealm);
					RealmService.commitEdit(realmEdit);

					site.setTitle(getCourseTab(state, providerRealm));
				}
				catch (IdUnusedException e)
				{
					Log.warn("chef", "SiteAction.finishCourseSite IdUnusedException, not found, or not an RealmEdit object");
					addAlert(state, "The expected realm object was not found.");
					return;
				}
				catch (PermissionException e)
				{
					Log.warn("chef", "SiteAction.finishCourseSite PermissionException, user does not have permission to edit RealmEdit object.");
					addAlert(state, "You do not have permission to change the access to this site.");
					return;
				}
				catch (InUseException e)
				{
					addAlert(state, "The access to this site is currently being edited by another user.");
					return;
				}	
				catch (Exception e)
				{
					addAlert(state, "A problem was encountered in setting section access. Please contact " + PRODUCTION_SITE_NAME + " support for assistance.");
					return;
				}
				
				sendSiteNotification(state, umiacCourseSectionList);
				addSubjectAffliates(state, umiacCourseSectionList);
			} // official course list
			
			if ((manualCourseSectionList != null) && (manualCourseSectionList.size() != 0))
			{
				// store the manually requested sections in one site property
				CourseListItem cli = (CourseListItem) manualCourseSectionList.get(0);
				if ((umiacCourseSectionList == null) || (umiacCourseSectionList.size() == 0))
				{
					site.setTitle(cli.subject + " " + cli.catalog_nbr + " " + cli.class_section);
				}
				
				String manualSections = "";
				for (int j = 0; j < manualCourseSectionList.size(); j++)
				{
					cli = (CourseListItem) manualCourseSectionList.get(j);
					manualSections = manualSections.concat( cli.year + "," + cli.term_code + "," + cli.campus_code + "," + cli.subject + "," + cli.catalog_nbr + "," + cli.class_section + "+");
				}			
				
				ResourcePropertiesEdit rp = site.getPropertiesEdit();
				rp.addProperty(PROP_SITE_REQUEST_COURSE_SECTIONS, manualSections);
				
				sendSiteRequest(state, "new", manualCourseSectionList);
			}
			
		}
		
	} // finishCourseSite
	
	/**
	* 
	* 
	*/
	private void finishSectionAccess (SessionState state, List notifyClasses, List requestClasses)
	{
		List umiacCourseSectionList = (Vector) state.getAttribute("umiacCourseSectionList");
		List manualCourseSectionList = (Vector) state.getAttribute("manualCourseSectionList");
		SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		String realm = "/site/" + id;
		
		if ((umiacCourseSectionList == null) || (umiacCourseSectionList.size() == 0))
		{
			//no section access so remove Provider Id
			try
			{
				RealmEdit realmEdit1 = RealmService.editRealm(realm);
				realmEdit1.setProviderRealmId(NULL_STRING);
				RealmService.commitEdit(realmEdit1);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + " IdUnusedException, " + realm + " not found, or not an RealmEdit object");
				addAlert(state, "The expected realm object was not found.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return; 
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + " PermissionException, user does not have permission to edit RealmEdit object " + realm + ". ");
				addAlert(state, "You do not have permission to change the access to this site.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return;
			}
			catch (InUseException e)
			{
				addAlert(state, "The access to this site " + realm + " is currently being edited by another user.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return;
			}	
		}
		
		if ((umiacCourseSectionList != null) && (umiacCourseSectionList.size() != 0))
		{
			// section access so rewrite Provider Id
			String externalRealm = buildExternalRealm(id, state, umiacCourseSectionList);
			try
			{
				RealmEdit realmEdit2 = RealmService.editRealm(realm);
				realmEdit2.setProviderRealmId(externalRealm);
				RealmService.commitEdit(realmEdit2);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + " IdUnusedException, " + realm + " not found, or not an RealmEdit object");
				addAlert(state, "The expected realm object was not found.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return;
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + " PermissionException, user does not have permission to edit RealmEdit object " + realm + ". ");
				addAlert(state, "You do not have permission to change the access to this site.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return;
			}
			catch (InUseException e)
			{
				addAlert(state, "The access to this site " + realm + " is currently being edited by another user.");
				state.setAttribute(STATE_TEMPLATE_INDEX, "18");
				return;
			}	
	
		}
		
		if ((manualCourseSectionList != null) && (manualCourseSectionList.size() != 0))
		{
			// store the manually requested sections in one site property
			String manualSections = "";
			for (int j = 0; j < manualCourseSectionList.size(); j++)
			{
				CourseListItem cli = (CourseListItem) manualCourseSectionList.get(j);
				manualSections = manualSections + cli.year + "," + cli.term_code + "," + cli.campus_code + "," + cli.subject + "," + cli.catalog_nbr + "," + cli.class_section + "+";
			}
			ResourcePropertiesEdit rp = site.getPropertiesEdit();
			rp.addProperty(PROP_SITE_REQUEST_COURSE_SECTIONS, manualSections);
		}
		else
		{
			ResourcePropertiesEdit rp = site.getPropertiesEdit();
			rp.removeProperty(PROP_SITE_REQUEST_COURSE_SECTIONS);
		}
		commitSiteAndRemoveEdit(state);
		
		if (requestClasses.size() > 0)
		{
			// send out class request notifications
			sendSiteRequest(state, "change", requestClasses);
		}
		
		if (notifyClasses.size() > 0)
		{
			// send out class access confirmation notifications
			sendSiteNotification(state, notifyClasses);
		}
		
	} // finishSectionAccess
	
	/**
	 * remove related state variable for adding class
	 * @param state SessionState object
	 */
	private void removeAddClassContext(SessionState state)
	{
		// remove related state variables 
		state.removeAttribute(STATE_ADD_CLASS_UMIAC);
		state.removeAttribute(STATE_ADD_CLASS_MANUAL);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_NUMBER);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_SUBJECTS);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_COURSES);
		state.removeAttribute(STATE_MANUAL_ADD_COURSE_SECTIONS);
		state.removeAttribute(STATE_SITE_QUEST_UNIQNAME);
		state.removeAttribute(STATE_MANUAL_ADD);
		state.removeAttribute(STATE_AUTO_ADD);
		state.removeAttribute(STATE_COURSE_REQUEST);
		state.removeAttribute(STATE_SUBJECT_REQUEST);
		state.removeAttribute(STATE_SECTION_REQUEST);
		state.removeAttribute(STATE_TERM_SELECTED);
		ResourceProperties siteProperties = ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getProperties();
		state.setAttribute(STATE_TERM_SELECTED, siteProperties.getProperty(PROP_SITE_TERM));
		
	}	// removeAddClassContext

	/**
	* Notification sent when a course site needs to be set up by Support
	* 
	*/
	private void sendSiteRequest(SessionState state, String request, List requestClasses)
	{
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		
		Site site = (Site) state.getAttribute(STATE_SITE_INSTANCE);
		String id = site.getId();
		String title = site.getTitle();
		
		Time time = TimeService.newTime();
		String local_time = time.toStringLocalTime();
		String local_date = time.toStringLocalDate();
		
		String term_name = NULL_STRING;
		int term = Integer.parseInt((String)state.getAttribute(/*STATE_TERM_REQUEST*/STATE_TERM_SELECTED));
		switch(term)
		{
			case 0: 
				term_name = "Fall, 2003";
				break;
			case 1:
				term_name = "Winter, 2004";
				break;
			case 2:
				term_name = "Spring, 2004";
				break;
			case 3:
				term_name = "Spring/Summer, 2004";
				break;
			case 4:
				term_name = "Summer, 2004";
				break;
			case 5:
				term_name = "Fall, 2004";
				break;
			case 6:
				term_name = "Winter, 2005";
				break;
		}
		
		String from = NULL_STRING;
		String to = NULL_STRING;
		String headerTo = NULL_STRING;
		String replyTo = NULL_STRING;
		String message_subject = NULL_STRING;
		String content = NULL_STRING;
		
		String sessionUserName = UserDirectoryService.getCurrentUser().getDisplayName();
		String sessionUserUniqname = StringUtil.trimToZero(UsageSessionService.getSessionUserId());
		String additional = NULL_STRING;
		if (request.equals("new"))
		{
			additional = siteInfo.getAdditional();
		}
		else
		{
			additional = (String)state.getAttribute(FORM_ADDITIONAL);
		}
		
		// get the request email from configuration
		String requestEmail = ServerConfigurationService.getString("setup.request", null);
		if (requestEmail == null)
		{
			Log.warn("chef", this + " - no 'setup.request' in configuration");
		}
		
		//To site quest uniqname - the instructor of record's
		boolean sendEmailToUniqname = false;
		StringBuffer buf = new StringBuffer();
		String requestUniqname = (String) state.getAttribute(STATE_SITE_QUEST_UNIQNAME);
		if (requestUniqname != null)
		{
			try
			{
				User instructor = UserDirectoryService.getUser(requestUniqname);
				from = requestEmail;
				to = instructor.getEmail();
				headerTo = instructor.getEmail();
				replyTo = requestEmail;
				message_subject = "Official Course Site: " + term_name;
				buf.append("Hello, \n\n");
				buf.append("You are receiving this message at the recommendation of " + sessionUserName + ", ");
				buf.append("who has requested an official course website for the following class(es):\n");
				buf.append(term_name + "\n");
				for (int i = 0; i < requestClasses.size(); i++)
				{
					CourseListItem cli = (CourseListItem) requestClasses.get(i);
					buf.append("Subject:\t" + cli.subject + "\n");
					buf.append("Course:\t" + cli.catalog_nbr + "\n");
					buf.append("Section:\t" + cli.class_section + "\n\n");	
				}
				buf.append("\nAccording to our data sources, " + sessionUserName + " is not the instructor of record.\n");
				buf.append("Can you verify that "  + sessionUserName + " is associated with the class(es) listed above?\n");
				buf.append("Please respond to this message with information about " + sessionUserName + "'s appointment and the legitimacy of this site request. If you feel unable to respond, please forward this message to a departmental contact with the authority to approve or deny the request.\n\n");
				buf.append("Thank you,\n");
				buf.append(PRODUCTION_SITE_NAME + " Support Team");
				content = buf.toString();	
				
				// send the email
				EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
				
				// email has been sent successfully
				sendEmailToUniqname = true;
			}
			catch (IdUnusedException ee)
			{
			}	// try
		}
		
		//To Support
		from = UserDirectoryService.getCurrentUser().getEmail();
		to = requestEmail;
		headerTo = requestEmail;
		replyTo = UserDirectoryService.getCurrentUser().getEmail();
		message_subject = "Site Request from " + sessionUserName;
		buf.setLength(0);
		buf.append("To:\t\t" + PRODUCTION_SITE_NAME + " Support\n");
		buf.append("\nFrom:\t" + sessionUserName + "\n");
		if (request.equals("new"))
		{
			buf.append("Subj:\tSite Request\n");
		}
		else
		{
			buf.append("Subj:\tSite Change Request\n");
		}
		buf.append("Date:\t" + local_date + " " + local_time + "\n\n");
		if (request.equals("new"))
		{
			buf.append("I am requesting approval of a " + PRODUCTION_SITE_NAME + " Course Site for ");
		}
		else
		{
			buf.append("I am requesting approval of access to a " + PRODUCTION_SITE_NAME + " Course Site for ");
		}
		buf.append(term_name);
		int nbr_sections = requestClasses.size();
		if (nbr_sections >1)
		{
			buf.append(" for these " + Integer.toString(nbr_sections) + " sections:\n\n");
		}
		else
		{
			buf.append(" for this section:\n\n");
		}
		for (int i = 0; i < nbr_sections; i++)
		{
			CourseListItem cli = (CourseListItem) requestClasses.get(i);
			buf.append("Subject:\t" + cli.subject + "\n");
			buf.append("Course:\t" + cli.catalog_nbr + "\n");
			buf.append("Section:\t" + cli.class_section + "\n\n");	
		}
		buf.append("Name:\t\t" + sessionUserName + " (uniqname " + sessionUserUniqname + ")\n");
		buf.append("Email:\t" + replyTo + "\n\n");
		buf.append("Site title:\t" + title + "\n");
		buf.append("Site id:\t" +  id + "\n");
		buf.append("Special Instruction:\n"  + additional + "\n\n");
		
		if (sendEmailToUniqname)
		{
			buf.append("The site request authorization email has been sent successfully to uniqname " + requestUniqname + " as requested. ");
		}
		else
		{
			buf.append("The site request authorization email could not be sent to uniqname " + requestUniqname + " as requested. ");
		}
		content = buf.toString();
		EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
		
		//To the Instructor
		User curUser = UserDirectoryService.getCurrentUser();
		from = requestEmail;
		to = curUser.getEmail();
		headerTo = to;
		replyTo = to;
		buf.setLength(0);
		buf.append("Your request is being processed. You will receive email when the class roster has been added to your site. ");
		buf.append("In the meantime, you can continue with the site setup process and add course materials to your site.\n\n");
		buf.append("This is a copy of the Course Site request that you made.\n\n");
		buf.append(content);
		buf.append("\nIf you wish to provide additional information, please send email to " + requestEmail);
		content = buf.toString();
		EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);
		state.setAttribute(REQUEST_SENT, new Boolean(true));

	} //  sendSiteRequest
	
	/**
	* Notification sent when a course site is set up automatcally
	* 
	*/
	private void sendSiteNotification(SessionState state, List notifySites)
	{
		SiteInfo siteInfo = (SiteInfo)state.getAttribute(STATE_SITE_INFO);
		String id = ((Site)state.getAttribute(STATE_SITE_INSTANCE)).getId();
		String title = ((Site)state.getAttribute(STATE_SITE_INSTANCE)).getTitle();
		Time time = TimeService.newTime();
		String local_time = time.toStringLocalTime();
		String local_date = time.toStringLocalDate();
		String message_subject = "Official Course Site created by " + UserDirectoryService.getCurrentUser().getDisplayName();
		String term_name = NULL_STRING;
		int term = Integer.parseInt((String)state.getAttribute(STATE_TERM_REQUEST));
		switch(term)
		{
			case 0: 
				term_name = "Fall, 2003";
				break;
			case 1:
				term_name = "Winter, 2004";
				break;
			case 2:
				term_name = "Spring, 2004";
				break;
			case 3:
				term_name = "Spring/Summer, 2004";
				break;
			case 4:
				term_name = "Summer, 2004";
				break;
			case 5:
				term_name = "Fall, 2004";
				break;
			case 6:
				term_name = "Winter, 2005";
				break;
		}
		String from = NULL_STRING;
		String to = NULL_STRING;
		String headerTo = NULL_STRING;
		String replyTo = NULL_STRING;
		String sender = UserDirectoryService.getCurrentUser().getDisplayName();
		String uniqname = StringUtil.trimToZero(UsageSessionService.getSessionUserId());

		// get the request email from configuration
		String requestEmail = ServerConfigurationService.getString("setup.request", null);
		if (requestEmail == null)
		{
			Log.warn("chef", this + " - no 'setup.request' in configuration");
		}
		
		//To Support
		from = UserDirectoryService.getCurrentUser().getEmail();
		to = requestEmail;
		headerTo = requestEmail;
		replyTo = UserDirectoryService.getCurrentUser().getEmail();
		StringBuffer buf = new StringBuffer();
		buf.append("\nFrom Worksite Setup to " + PRODUCTION_SITE_NAME + " Support:\n\n");
		buf.append("Official Course Site '" + title + "' (id " + id + "), was set up by ");
		buf.append(sender + " (" + uniqname + ", email " + replyTo + ") ");
		buf.append("on " + local_date + " at " + local_time + " ");
		buf.append("for " + term_name + ", ");
		int nbr_sections = notifySites.size();
		if (nbr_sections >1)
		{
			buf.append("with access to UMIAC rosters for these " + Integer.toString(nbr_sections) + " sections:\n\n");
		}
		else
		{
			buf.append(" with access to the UMIAC roster for this section:\n\n");
		}
		
		for (int i = 0; i < nbr_sections; i++)
		{
			CourseListItem cli = (CourseListItem) notifySites.get(i);
			buf.append("Subject:\t" + cli.subject + "\n");
			buf.append("Course:\t" + cli.catalog_nbr + "\n");
			buf.append("Section:\t" + cli.class_section + "\n\n");	
		}
		String content = buf.toString();
		EmailService.send(from, to, message_subject, content, headerTo, replyTo, null);

	} //  sendSiteNotification
	
	/**
	* %%% legacy properties, to be cleaned up
	* 
	*/
	private void sitePropertiesIntoState (SessionState state)
	{
		try
		{
			SiteEdit site = (SiteEdit)state.getAttribute(STATE_SITE_INSTANCE);
			//Profile profile = new Profile();
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

			//set from site properties
			ResourceProperties rp = site.getProperties();
			String site_type = rp.getProperty(PROP_SITE_TYPE);
			
			//choose either SITE_TYPE_COURSE or SITE_TYPE_PROJECT of SITE_TYPE_GRADTOOLS_STUDENT
			if(site_type == null || site_type.equals(NULL_STRING) || 
				(!(site_type.equals(SITE_TYPE_COURSE) || site_type.equals(SITE_TYPE_PROJECT) || site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT)))) 
			{ 
				site_type = SITE_TYPE_UNDEFINED;
				siteInfo.site_type = SITE_TYPE_UNDEFINED;
				state.setAttribute(STATE_SITE_TYPE,  SITE_TYPE_UNDEFINED);
				state.setAttribute(STATE_SITE_INFO, siteInfo);
				return;
			}
			
			//propertes may not exist, so check for nulls
			siteInfo.site_type = rp.getProperty(PROP_SITE_TYPE);
			
			//unless course type, sites should not have terms %%%
			if(rp.getProperty(PROP_SITE_TERM) != null)
			{
				siteInfo.term = Integer.parseInt(rp.getProperty(PROP_SITE_TERM));
			}
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
					if(siteInfo.site_type.equals(SITE_TYPE_COURSE))
					{
						siteInfo.skin = COURSE_UNPUBLISHED_SKIN;
					}
					else
					{
						siteInfo.skin = PROJECT_UNPUBLISHED_SKIN;
					}
				}
			}
			if(site_type.equals(SITE_TYPE_COURSE) || site_type.equals(SITE_TYPE_PROJECT) || site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
			{
				state.setAttribute(STATE_SITE_TYPE, siteInfo.site_type);
			}
			else
			{
				state.setAttribute(STATE_SITE_TYPE, SITE_TYPE_UNDEFINED);
			}
			//state.setAttribute(STATE_PROFILE, profile);
			state.setAttribute(STATE_SITE_INFO, siteInfo);
		}
		catch (Exception e)
		{
			Log.warn("chef", "SiteAction.sitePropertiesIntoState " + e.getMessage());
		}
		
	} // sitePropertiesIntoState 
	
	/**
	* removeDuplicateCourseListItems 
	* remove duplicates from a list of CourseListItems
	* @param courseListItems is the Vector of CourseListItems to be processed
	* returns a List of CourseListItems without duplicates
	*/
	public List removeDuplicateCourseListItems ( List courseListItems )
	{
		List rv = new Vector();
		if (courseListItems != null && courseListItems.size() != 0)
		{
			CourseListItem cli_i = (CourseListItem) courseListItems.get(0);
			rv.add(cli_i); // We know there is at least one course list item
			for ( int i = 1; i < courseListItems.size(); i++ )
			{
				boolean add = true;
				cli_i = (CourseListItem) courseListItems.get(i);
				String key_i = (String) cli_i.getKey();
				for ( int j = 0; j < rv.size(); j++ )
				{
					CourseListItem cli_j = (CourseListItem) courseListItems.get(j);
					String key_j = (String) cli_j.getKey();
					if (key_i.equals(key_j))
					{
						add = false;
					}
				}
				if(add) 
				{
					rv.add(cli_i);
				}
			}
		}
		return rv;
		
	} // removeDuplicateCourseListItems
	
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
				site_type = s.getProperties().getProperty(PROP_SITE_TYPE);
				isUserSite = SiteService.isUserSite(s.getId());
			}
			if(site_type == null || site_type.equals(""))
			{
				site_type = SITE_TYPE_UNDEFINED;
			}
			if (site_type.equals(SITE_TYPE_COURSE)) 
			{
				toolList = (Vector) state.getAttribute(STATE_COURSE_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_PROJECT))
			{
				toolList = (Vector) state.getAttribute(STATE_PROJECT_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
			{
				toolList = (Vector) state.getAttribute(STATE_GRADTOOLS_STUDENT_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_RACKHAM))
			{
				toolList = (Vector) state.getAttribute(STATE_GRADTOOLS_RACKHAM_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_DEPARTMENT))
			{
				toolList = (Vector) state.getAttribute(STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_MYWORKSPACE) || isUserSite)
			{
				toolList = (Vector) state.getAttribute(STATE_MYWORKSPACE_TOOL_LIST);
			}
			else if (site_type.equals(SITE_TYPE_UNDEFINED))
			{
				toolList = (Vector) state.getAttribute(STATE_UNDEFINED_TOOL_LIST);
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
		String listVariable = null;
		String site_type = (site.getProperties()).getProperty(PROP_SITE_TYPE);
		if (site_type != null)
		{
			if (site_type.equals(SITE_TYPE_COURSE))
			{
				listVariable = STATE_COURSE_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_PROJECT))
			{
				listVariable = STATE_PROJECT_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_STUDENT))
			{
				listVariable = STATE_GRADTOOLS_STUDENT_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_RACKHAM))
			{
				listVariable = STATE_GRADTOOLS_RACKHAM_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_GRADTOOLS_DEPARTMENT))
			{
				listVariable = STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_MYWORKSPACE))
			{
				listVariable = STATE_MYWORKSPACE_TOOL_LIST;
			}
			else if (site_type.equals(SITE_TYPE_UNDEFINED))
			{
				listVariable = STATE_UNDEFINED_TOOL_LIST;
			}
		}
		else
		{
			if (SiteService.isUserSite(site.getId()))
			{
				listVariable = STATE_MYWORKSPACE_TOOL_LIST;
			}
			else
			{
				Log.warn("chef", "SiteAction.buildContextForTemplate, case 21: - unknown STATE_SITE_TYPE");
			}
		}
		
		List toolRegList = (List) state.getAttribute(listVariable);
		
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
						wSetupTool = "ctng.home";
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
		
		state.setAttribute(listVariable, toolRegList);
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
		List toolList = new Vector(); // for template feature list, Tool object list
		List courseToolList = new Vector();
		List projectToolList = new Vector();
		List gradToolsStudentToolList = new Vector();
		List gradToolsRackhamToolList = new Vector();
		List gradToolsDepartmentToolList = new Vector();
		List workshopToolList = new Vector();
		List myworkspaceToolList = new Vector();
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
				toolList.add(tool);
				for (ListIterator k = categoriesList.listIterator(); k.hasNext(); )
				{
					Tool t = new Tool();
					t.id = tr.getId();
					t.title = tr.getTitle();
					t.description = tr.getDescription();
					t.selected = false;
					String category = (String) k.next();
					if(category.equals(SITE_TYPE_COURSE))
					{
						courseToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_PROJECT))
					{
						projectToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_GRADTOOLS_STUDENT))
					{
						gradToolsStudentToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_GRADTOOLS_RACKHAM))
					{
						gradToolsRackhamToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_GRADTOOLS_DEPARTMENT))
					{
						gradToolsDepartmentToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_WORKSHOP))
					{
						workshopToolList.add(t);
					}
					else if (category.equals(SITE_TYPE_MYWORKSPACE))
					{
						myworkspaceToolList.add(t);
					}
				}
				//for sites created outside Worksite Setup
				state.setAttribute(STATE_UNDEFINED_TOOL_LIST, toolList);
				
				//for sites created/edited in Worksite Setup
				state.setAttribute(STATE_COURSE_TOOL_LIST, courseToolList);
				state.setAttribute(STATE_PROJECT_TOOL_LIST, projectToolList);
				state.setAttribute(STATE_GRADTOOLS_STUDENT_TOOL_LIST, gradToolsStudentToolList);
				state.setAttribute(STATE_GRADTOOLS_RACKHAM_TOOL_LIST, gradToolsRackhamToolList);
				state.setAttribute(STATE_GRADTOOLS_DEPARTMENT_TOOL_LIST, gradToolsDepartmentToolList);
				state.setAttribute(STATE_WORKSHOP_TOOL_LIST, workshopToolList);
				state.setAttribute(STATE_MYWORKSPACE_TOOL_LIST, myworkspaceToolList);
			}
			
		}
		state.setAttribute(STATE_TOOL_REGISTRATION_LIST, toolRegistrationList);
		state.setAttribute(FEATURE_LIST, featureList); // IdAndText type
		state.setAttribute(STATE_TOOL_LIST, toolList); // Tool type
		
	} // setupToolLists
	
	private void setupFormNamesAndConstants(SessionState state)
	{
		TimeBreakdown timeBreakdown = (TimeService.newTime ()).breakdownLocal ();
		String mycopyright = COPYRIGHT_SYMBOL + " " + timeBreakdown.getYear () + ", " + UserDirectoryService.getCurrentUser().getDisplayName () + ". All Rights Reserved. ";
		state.setAttribute (STATE_MY_COPYRIGHT, mycopyright);
		state.setAttribute (STATE_SITE_INSTANCE, null);
		state.setAttribute (STATE_INITIALIZED, Boolean.TRUE.toString());
		state.setAttribute(STATE_TERM_SELECTED, STATE_CURRENT_TERM);
		Profile profile = new Profile();
		SiteInfo siteInfo = new SiteInfo();
		Participant participant = new Participant();
		participant.name = NULL_STRING;
		participant.uniqname = NULL_STRING;
		state.setAttribute(STATE_PROFILE, profile);
		state.setAttribute(STATE_SITE_INFO, siteInfo);
		state.setAttribute("form_participantToAdd", participant);
		state.setAttribute(FORM_ADDITIONAL, NULL_STRING);
		List umiacCourseSectionList = new Vector();
		List manualCourseSectionList = new Vector();
		List studentList = new Vector();
		state.setAttribute(STATE_CLASS_ROSTER, studentList);
		state.setAttribute("umiacCourseSectionList", umiacCourseSectionList);
		state.setAttribute("manualCourseSectionList", manualCourseSectionList);
		//legacy
		state.setAttribute(FORM_TERM, "0"); 
		state.setAttribute(FORM_AFFILIATION, "0");
		state.setAttribute(FORM_HONORIFIC,"0");
		state.setAttribute(FORM_REUSE, "0"); 
		state.setAttribute(FORM_RELATED_CLASS, "0");
		state.setAttribute(FORM_RELATED_PROJECT, "0");
		state.setAttribute(FORM_INSTITUTION, "0");
		//sundry form variables
		state.setAttribute(FORM_OFFICE,"");
		state.setAttribute(FORM_PHONE,"");
		state.setAttribute(FORM_EMAIL,"");
		state.setAttribute(FORM_SUBJECT,"");
		state.setAttribute(FORM_COURSE,"");
		state.setAttribute(FORM_DESCRIPTION,"");
		state.setAttribute(FORM_TITLE,"");
		state.setAttribute(FORM_NAME,"");
		state.setAttribute(FORM_SHORT_DESCRIPTION,"");
		
	}	// setupFormNamesAndConstants
	
	/**
	* doAdd_manual_sections 
	*/
	public void doAdd_manual_sections ( RunData data )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();
		
		String option = params.getString("option");
		//dispatch
		if (option.equalsIgnoreCase("continue"))
		{
			// continue
			doContinue(data);			
		} 
		else if (option.equalsIgnoreCase("back"))
		{
			// cancel
			doBack(data);
		}
		else if (option.equalsIgnoreCase("cancel"))
		{
			// cancel
			doCancel(data);
		}
		else if (option.equalsIgnoreCase("change"))
		{
			// cancel
			readCourseSectionInfo(state, params);
		}
	}	// doAdd_manual_sections
	
	/**
	 * A dispatch funtion when selecting course features
	 */
	public void doAdd_features ( RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		ParameterParser params = data.getParameters ();

		String option = params.getString("option");
		
		// get the corresponding state variable name
		String courseOrProject = params.getString("courseOrProject");
		String stateVariable = null;
		if (courseOrProject.equalsIgnoreCase("course"))
		{
			stateVariable = STATE_COURSE_TOOL_LIST;
		}
		else if (courseOrProject.equalsIgnoreCase("project"))
		{
			stateVariable = STATE_PROJECT_TOOL_LIST;
		}
		
		// dispatch
		if (option.equalsIgnoreCase("addNews"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, stateVariable);
				insertTool(state, stateVariable, "chef.news", STATE_NEWS_TITLES, NEWS_DEFAULT_TITLE);
			}
		}
		else if (option.equalsIgnoreCase("addWebContent"))
		{
			if (params.getStrings("selectedTools")!= null)
			{
				updateSelectedToolList(state, params, stateVariable);
				insertTool(state, stateVariable, "chef.iframe", STATE_WEB_CONTENT_TITLES, WEB_CONTENT_DEFAULT_TITLE);
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
	 * @param listVariable The state variable name
	 */
	private void updateSelectedToolList (SessionState state, ParameterParser params, String listVariable)
	{
		// the list of available tools
		List toolList = (Vector) state.getAttribute(listVariable);
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
			if (toolId.equalsIgnoreCase("ctng.home"))
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
	 * @param listVariable The state variable name
	 * @param toolId The id for the inserted tool
	 * @param stateTitleVariable The titles
	 * @param defaultTitle The default title for the inserted tool
	 */
	private void insertTool(SessionState state, String listVariable, String toolId, String stateTitlesVariable, String defaultTitle)
	{
		//the list of available tools
		List toolList = (Vector) state.getAttribute(listVariable);
		
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
		
			state.setAttribute(listVariable, toolList);
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
		List umiacCourseSectionList = (Vector)state.getAttribute("umiacCourseSectionList");
		List participantList = new Vector();
		
		String siteId = ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		String site_type = (String) state.getAttribute(STATE_SITE_TYPE); 
		if(site_type != null)
		{
			if ((site_type.equalsIgnoreCase(SITE_TYPE_COURSE) && SiteService.allowUpdateSite(siteId))
				|| (site_type.equalsIgnoreCase(SITE_TYPE_PROJECT) && (SiteService.allowUpdateSite(siteId) || SiteService.allowViewRoster(siteId))))
			{	
				participantList.addAll(roster(state, umiacCourseSectionList));
			}
			
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
	
	private void setCourseSectionList(SessionState state)
	{
		//	If site has a Realm Provider Id, get the associated umiacCourseSectionList for roster(s)
		List umiacCourseSectionList = buildSectionsFromRealm(getExternalRealmId(state));
		state.setAttribute("umiacCourseSectionList", umiacCourseSectionList);
		
		// if the site has some requested but not yet approved sections
		List manualCourseSectionList = new Vector();
		String siteId = ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
		try
		{
			ResourceProperties rp = SiteService.getSite(siteId).getProperties();
			manualCourseSectionList = buildRequestedSections(state, (String)rp.getProperty(PROP_SITE_REQUEST_COURSE_SECTIONS));
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Cannot find site " + siteId + ". ");
		}
		state.setAttribute("manualCourseSectionList", manualCourseSectionList);
		
	}	// setCourseSectionList
	
	/**
	 * Get roster member for site from provider
	 * @param state The SessionState object
	 * @param umiacCourseSectionList The course list
	 * @return List of Student objects 
	 */
	private List roster(SessionState state, List umiacCourseSectionList)
	{
		//students in roster
		List rosterList = new Vector();
		CourseListItem listItem = new CourseListItem();
		
		// is there a valid UmiacClient?
		UmiacClient uClient = UmiacClient.getInstance();
		if (uClient != null && StringUtil.trimToNull(uClient.getHost()) != null && uClient.getPort() > 0)
		{
			if(umiacCourseSectionList != null && umiacCourseSectionList.size() != 0)
			{
				for (int i = 0; i < umiacCourseSectionList.size(); i++)
				{
					//Get students by section
					List rv = new Vector();
					
					listItem = new CourseListItem();
					listItem.year = ((CourseListItem)umiacCourseSectionList.get(i)).getYear();
					listItem.term_code = ((CourseListItem)umiacCourseSectionList.get(i)).getTerm_code();
					listItem.campus_code = ((CourseListItem)umiacCourseSectionList.get(i)).getCampus_code();
					listItem.subject = ((CourseListItem)umiacCourseSectionList.get(i)).getSubject();
					listItem.catalog_nbr = ((CourseListItem)umiacCourseSectionList.get(i)).getCatalog_nbr();
					listItem.class_section = ((CourseListItem)umiacCourseSectionList.get(i)).getClass_section();
					
					Vector v = uClient.getClassList(listItem.year, listItem.term_code, listItem.campus_code, listItem.subject, listItem.catalog_nbr, listItem.class_section);
					if (v != null && v.size() > 0)
					{
						for (int k = 0;k < v.size(); k++)
						{
							String[] res = (String[]) v.get(k);
							Student student = new Student();
							student.name = res[0];
							student.uniqname = res[1];
							student.id = res[2];
							student.level = res[3];
							student.credits = res[4];
								
							//If student already has a defined role in the realm already; it will replace the role from provider
							String realmId = "/site/" + ((Site) state.getAttribute(STATE_SITE_INSTANCE)).getId();
							try
							{
								Realm realm = RealmService.getRealm(realmId);
								User user = UserDirectoryService.getUser(student.uniqname);
								Set roles = realm.getUserRoles(user);
								if (roles.size() != 0)
								{
									Iterator rIterator = roles.iterator();
									if (rIterator.hasNext())
									{
										String role = ((Role) rIterator.next()).getId();
										student.role = role;
									}
								}
								else
								{
									student.role = res[5];
								}
								
								student.course = listItem.subject + " " + listItem.catalog_nbr;
								student.section = listItem.class_section;
								
								if (student.getId() != null && student.getId().length() > 0)
								{
									rv.add(student);
								}
							}
							catch (IdUnusedException e)
							{
								Log.warn("chef", this + " IdUnusedException " + realmId);
							}
							
						}
					}
					
					// add all students from this section
					rosterList.addAll(rv);
				}	
			}
		}
		
		// all students from site rosters
		return rosterList;
		
	}	// roster
	
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
				String s1 = ((Site) o1).getProperties().getProperty(PROP_SITE_TYPE);
				String s2 = ((Site) o2).getProperties().getProperty(PROP_SITE_TYPE);
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
			else if (m_criterion.equals (SORTED_BY_TERM))
			{
				int i1 = 0;
				int i2 = 0;
				String s1 = ((Site) o1).getProperties().getProperty(PROP_SITE_TERM );
				String s2 = ((Site) o2).getProperties().getProperty(PROP_SITE_TERM );
				if((s1!=null) && (!s1.equals("")))
				{
					i1 = Integer.parseInt(s1);
				}
				if((s2!=null) && (!s2.equals("")))
				{
					i2 = Integer.parseInt(s2);
				}
				String s1type = ((Site) o1).getProperties().getProperty(PROP_SITE_TYPE);
				String s2type = ((Site) o2).getProperties().getProperty(PROP_SITE_TYPE);
				
				//unless for a course, site should not have term - for now put it out of range %%%
				if((s1type==null) || (!s1type.equals(SITE_TYPE_COURSE)))
				{
					i1 = 99999;
				}
				if((s2type==null) || (!s2type.equals(SITE_TYPE_COURSE)))
				{
					i2 = 99999;
				}
				if(i1 == i2)
				{
					result = 0;
				}
				else if (i1 > i2)
				{
					result = 1;
				}
				else if (i1 < i2)
				{
					result = -1;
				}
				else
				{
					result = -1;
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
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_COURSE))
			{
				String s1 = null;
				if (o1.getClass().equals(Participant.class))
				{
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getCourse();	
				}
				
				String s2 = null;
				if (o2.getClass().equals(Participant.class))
				{
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getCourse();	
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
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_SECTION))
			{
				String s1 = null;
				if (o1.getClass().equals(Participant.class))
				{
					
				}
				else if (o1.getClass().equals(Student.class))
				{
					s1 = ((Student) o1).getSection();	
				}
				
				String s2 = null;
				if (o2.getClass().equals(Participant.class))
				{
				}
				else if (o2.getClass().equals(Student.class))
				{
					s2 = ((Student) o2).getSection();	
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
			else if (m_criterion.equals (SORTED_BY_PARTICIPANT_CREDITS))
			{
				int i1 = 0;
				if (o1.getClass().equals(Participant.class))
				{
					i1 = -1;
				}
				else if (o1.getClass().equals(Student.class))
				{
					try
					{
						i1 = Integer.parseInt(((Student) o1).getCredits());
					}
					catch (Exception e)
					{
						i1 = 0;
					}
				}
				
				int i2 = 0;
				if (o2.getClass().equals(Participant.class))
				{
					i2 = -1;
				}
				else if (o2.getClass().equals(Student.class))
				{
					try
					{
						i2 = Integer.parseInt(((Student) o2).getCredits());
					}	
					catch (Exception e)
					{
						i2 = 0;
					}
				}		
				
				if(i1 == i2)
				{
					result = 0;
				}
				else if (i1 > i2)
				{
					result = 1;
				}
				else if (i1 < i2)
				{
					result = -1;
				}
				else
				{
					result = -1;
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
		
	/**
	* InstructorSection
	*
	* A utility class representing UMIAC getInstructorClasses
	* output as a vector of InstructorSection objects (one per 
	* response line). UMIAC getInstructorClasses returns, in
	* order, these strings.
	*/
	private class InstructorSection
	{
		String year = "";
		String term_code = "";
		String campus_code = "";
		String subject = "";
		String catalog_nbr = "";
		String title = "";
		String class_section = "";
		String url = "";
		String component = ""; // (LEC, DIS, LAB, etc.)
		String role = ""; //Instructor, Student, Guest
		String subrole = "";
		
	} // InstructorSection
	
	/**
	* SubjectAffiliates
	*
	* A utility class representing CTNG Affiliates within
	* academic subject areas.
	*/
	public class SubjectAffiliates
	{
		String subject = "";
		String campus = "";
		Collection uniqnames = new Vector();
		public String getSubject() { return subject; }
		public String getCampus() { return campus; }
		public Collection getUniqnames() { return uniqnames; }
		
	} //Affiliates
	
	/**
	* %%% Used in testing, CourseListItem should be replaced with InstructorSection
	* which corresponds to the UMIAC getInstructorClasses API.
	* 
	*/
	public class CourseListItem
	{
		/**
		* corresponding to UMIAC getInstructorSections output fields
		*
		* year
		* term_code
		* campus_code
		* subject
		* catalog_nbr
		* class_section
		* title
		* url
		* component LEC, DIS, LAB, etc.
		* role  Instructor, Student, Guest
		* subrole
		* crosslist CL or blank
		* key (year, term_code, campus_code, subject, catalog_nbr, class_section)
		* 
		*
		*/
		public String year = NULL_STRING;
		public String term_code = NULL_STRING;
		public String campus_code = NULL_STRING;
		public String subject = NULL_STRING;
		public String catalog_nbr = NULL_STRING;
		public String class_section = NULL_STRING;
		public String title = NULL_STRING;
		public String url = NULL_STRING;
		public String component = NULL_STRING;
		public String role = NULL_STRING;
		public String subrole = NULL_STRING;
		public String crosslist = NULL_STRING;
		public String key = NULL_STRING;
		
		public String getYear() {  return year; }
		public String getTerm_code() {  return term_code; }
		public String getCampus_code() {  return campus_code; }
		public String getSubject() {  return subject; }
		public String getCatalog_nbr() {  return catalog_nbr; }
		public String getClass_section() {  return class_section; }
		public String getTitle() {  return title; }
		public String getUrl() {  return url; }
		public String getComponent() {  return component; }
		public String getRole() {  return role; }
		public String getSubrole() {  return subrole; }
		public String getCrosslist() {  return crosslist; }
		public String getKey() {  return key; }
		
	} // CourseListItem
	
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
	
	// profile should come from external source for CTNG courses
	public class Profile
	{
		public String institution = ""; // the home institution for the author
		public int affiliation = 0; // index of select option for the affiliation for the author information, to compare with int id of IdAndText
		public int subject = 0; // index of select option for the subject of the course
		public int course = 0; // index of select option for the name of the course
		public int honorific = 0; // index of select option for the "form of address" for the author information to compare with int id of IdAndText
		public String office = ""; // the "office address" for the author information
		public String phone = ""; // the phone number for the author information
		public String email = "";  // the email address for the author information

		public int getAffiliation() { return affiliation; }
		public int getSubject() { return subject; }
		public int getCourse() { return course; }
		public int getHonorific() { return honorific; }
		public String getOffice() { return office; }
		public String getPhone() { return phone; }
		public String getEmail() { return email; } 
		
	} // Profile
	
	public class SiteInfo
	{
		public int term = 0; // index of select option for the academic term for course
		public String course = NULL_STRING;
		public String subject = NULL_STRING;
		public String site_id = NULL_STRING; // getId of Resource
		public String external_id = NULL_STRING; // if matches site_id connects site with U-M course information
		public String site_type = SITE_TYPE_UNDEFINED;
		public String iconUrl = NULL_STRING;
		public String infoUrl = NULL_STRING;
		public String skin = NULL_STRING;
		public boolean joinable = false;
		public String joinerRole = NULL_STRING;
		public String title = NULL_STRING; // the short name of the site
		public String short_description = NULL_STRING; // the short (20 char) description of the site
		public String description = NULL_STRING;  // the longer description of the site
		public String additional = NULL_STRING; // additional information on crosslists, etc.
		public String sections = NULL_STRING; // a character-delimited string of one or more class sections with access to a CTNG site
		public int status = -1;
		public boolean include = true;	// include the site in the Sites index; default is true.
		public String site_contact_name = NULL_STRING;	// site contact name
		public String site_contact_email = NULL_STRING;	// site contact email
		
		public int getTerm() { return term; }
		public String getCourse() { return course; }
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
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitesetup/SiteAction.java,v 1.94 2004/10/07 01:57:38 ggolden.umich.edu Exp $
**********************************************************************************/
