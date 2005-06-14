/*********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/discussion/ThreadedDiscussionIIAction.java,v 1.27 2004/09/30 20:21:44 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.discussion;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PortletConfig;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.content.ContentTypeImageService;
import org.sakaiproject.service.legacy.discussion.DiscussionChannel;
import org.sakaiproject.service.legacy.discussion.DiscussionChannelEdit;
import org.sakaiproject.service.legacy.discussion.DiscussionMessage;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageEdit;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageHeader;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageHeaderEdit;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.tool.helper.AttachmentAction;
import org.sakaiproject.tool.helper.PermissionsAction;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.observer.EventObservingCourier;
import org.sakaiproject.util.observer.ObservingCourier;
import org.sakaiproject.util.delivery.RefreshDelivery;

/**
 * <p>DiscussionAction is the action class for the dicussion tool.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.27 $
 */

public class ThreadedDiscussionIIAction
	extends VelocityPortletPaneledAction
{ 
	/** channel exist */
	private static final String CHANNEL_EXIST = "channel_exist";
	
	/** the String used to seperate the categories inside the configration **/
	private static final String CATEGORY_SEPERATOR_STRING = ",";
	
	private static final String FORM_THREAD_OR_NOT = "thread_or_not";
	
	/** the attachments***/
	private static final String ATTACHMENTS = "threadeddiscussionII.attachments";

	/** the channel id ***/
	private static final String STATE_CHANNEL_REF = "threadeddiscussionII.channel_id";

	/** the category ***/
	private static final String STATE_CURRENT_CATEGORY = "threadeddiscussionII.current_category";

	/** the initialized flag ***/
	private static final String STATE_INITIALIZED_FLAG = "threadeddiscussionII.initialized_flag";

	/** the state mode ***/
	private static final String STATE_MODE = "threadeddiscussionII.state_mode";    

	/** the category show list ***/
	private static final String STATE_CATEGORIES_SHOW_LIST = "threadeddiscussionII.state_category_show_list";    

	/** the sorted by ***/
	private static final String STATE_SORTED_BY = "threadeddiscussionII.state_sorted_by";
	
	/** sorted by last posted date **/
	private static final String STATE_SORTED_BY_LASTPOSTEDDATE = "lastposteddate";
	
	/** sorted by last posted by **/
	private static final String STATE_SORTED_BY_LASTPOSTEDBY = "lastpostedby";
	
	/** sorted by topic **/
	private static final String STATE_SORTED_BY_TOPIC = "topic";

	/** sorted by subject **/
	private static final String STATE_SORTED_BY_SUBJECT = "subject";

	/** sorted by author **/
	private static final String STATE_SORTED_BY_AUTHOR = "author";
	
	/** sorted by date **/
	private static final String STATE_SORTED_BY_DATE = "date";
	
	/** sorted by category alphabetically **/
	private static final String STATE_SORTED_BY_CATEGORY_ALPHA = "category";
	/** sorted by category lasted posted by **/
	private static final String STATE_SORTED_BY_CATEGORY_LASTPOSTEDBY = "category_lastpostedby";
    /** sorted by category last posted date**/
	private static final String STATE_SORTED_BY_CATEGORY_LASTPOSTEDDATE = "category_lastposteddate";
	
	/** the sorted ascending **/
	private static final String STATE_SORTED_ASC = "threadeddiscussionII.state_sorted_asc";

	/** the expand all topic flag **/
	private static final String STATE_EXPAND_ALL_TOPIC_FLAG = "threadeddiscussionII.state_expand_all_topic_flag";    
	
	/** the expand message flag **/
	private static final String STATE_EXPAND_MESSAGE_LIST = "threadeddiscussionII.state_expand_message_list"; 
	
	/** the expand category list **/
	private static final String STATE_EXPAND_CATEGORY_LIST = "threadeddiscussionII.state_expand_category_list";  

	/** the show message list **/
	private static final String STATE_SHOW_MESSAGE_LIST = "threadeddiscussionII.state_show_message_list";    
	
	/** The content type image lookup service in the State. */
	private static final String STATE_CONTENT_TYPE_IMAGE_SERVICE = "attachment.content_type_image_service";

	/************ new topic context *********************/
	/** the new topic category */
	private static final String NEW_TOPIC_CATEGORY = "threadeddiscussionII.new_topic_category";

	/** create new category? */
	private static final String NEW_TOPIC_NEW_CATEGORY = "threadeddiscussionII.new_topic_new_category";

	/** the new topic message body */
	private static final String NEW_TOPIC_BODY = "threadeddiscussionII.new_topic_body";

	/** the new topic subject */
	private static final String NEW_TOPIC_SUBJECT = "threadeddiscussionII.new_topic_subject";
	
	/** the new topic reply message style */
	private static final String NEW_TOPIC_REPLY_STYLE = "threadeddiscussionII.new_topic_reply_style";
    
    /************ draft topic context *********************/
    /** the draft message category */
    private static final String DRAFT_MESSAGE_CATEGORY = "threadeddiscussionII.draft_message_category";
    
    /** the draft message body */
    private static final String DRAFT_MESSAGE_BODY = "threadeddiscussionII.draft_message_body";
    
    /** the draft message subject */
    private static final String DRAFT_MESSAGE_SUBJECT = "threadeddiscussionII.draft_message_subject";
    
    /** the draft message reply style */
    private static final String DRAFT_MESSAGE_REPLY_STYLE = "threadeddiscussionII.draft_message_reply_style";

	/** the previous topic id */
	private static final String STATE_PREVIOUS_TOPIC_ID = "threadeddiscussionII.previous_topic_id";

	/** the next topic id */
	private static final String STATE_NEXT_TOPIC_ID = "threadeddiscussionII.next_topic_id";

	/** the previous message id */
	private static final String STATE_PREVIOUS_MESSAGE_ID = "threadeddiscussionII.previous_message_id";

	/** the next message id */
	private static final String STATE_NEXT_MESSAGE_ID = "threadeddiscussionII.next_message_id";

	/*********** the respond context *********************/
	/** the respond message body */
	private static final String RESPOND_BODY = "threadeddiscussionII.respond_body";

	/** the respond message subject */
	private static final String RESPOND_SUBJECT = "threadeddiscussionII.respond_subject";

	/** the respond from */
	private static final String RESPOND_REPLY_TO = "threadeddiscussionII.respond_reply_to";

	/** the respond attachment */
	private static final String RESPOND_ATTACHMENT = "threadeddiscussionII.respond_attachment";

	/** The null/empty string */
	private static final String NULL_STRING = "";
	
	/** The permission alert message eader */
	private static final String PERMISSION_HEADER_STRING = "You are not allowed to ";

	/************** delete message context ********************/
	/** the delete message id **/
	private static final String DELETE_MESSAGE_ID = "threadeddiscussionII.delete_message_id";

	/** Modes. */
	private static final String MODE_NORMAL_VIEW = "frameView";
	
	/** portlet configuration parameter names. */
	private static final String PARAM_CHANNEL = "channel";
	private static final String PARAM_ASCENDING = "ascending";
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_DISPLAY_DATE = "display-date";
	private static final String PARAM_DISPLAY_TIME = "display-time";
	private static final String PARAM_DISPLAY_USER = "display-user";

	/** Configure form field names. */
	private static final String FORM_CHANNEL = "channel";
	private static final String FORM_ASCENDING = "ascending";

	/** Control form field names. */
	private static final String FORM_MESSAGE = "message";

	/** names and values of request parameters to select sub-panels */
	private static final String MONITOR_PANEL = "List";
	private static final String CONTROL_PANEL = "Control";
	private static final String TOOLBAR_PANEL = "Toolbar";
	private static final String NEXT_PANEL = "Next";

	/** state attribute names. */
	private static final String STATE_ASCENDING = "ascending";
	private static final String STATE_ERROR = "error";	
	private static final String STATE_UPDATE = "update";
	private static final String STATE_CHANNEL_PROBLEM = "channel-problem";
	
	/** channel problem messages */
	private static final String CHANNEL_MISSING = "This discussion is not available.";
	private static final String CHANNEL_PERMISSION = "You do not have permission to view this discussion.";
	private static final String CHANNEL_CREATE_PERMISSION = "You do not have permission to create this discussion.";

	/** UI messages. */
	private static final String PERMISSION_POST_MESSAGE = "You do not have permission to post to this discussion.";
	private static final String POST_PROBLEM_MESSAGE = "There was a problem sending the prior message.";
    private static final String STATE_DISPLAY_MESSAGE = "display_message";
	private static final String STATE_LIST_PANNEL_UPDATED = "state_list_pannel_updated";
    
	/** the alert message to be shown when there is a InUseException */
    private static final String INUSE_ERROR_MESSAGE = "Someone else is editing this ";
	
	/** the category to be deleted */
	private static final String DELETE_CATEGORY = "delete_category";
	
	/** the id of the showing content topic */
	private static final String STATE_SHOW_CONTENT_TOPIC_ID = "show_content_topic_id";
	
	/** state mode when showing the topic content */
	private static final String MODE_SHOW_TOPIC_CONTENT = "mode_show_topic_content";
	
	/** the state flag of expand all */
	private static final String STATE_EXPAND_ALL_FLAG = "state_expand_all_flag";

	/** state mode when showing the new topic form */
	private static final String	MODE_NEW_TOPIC = "mode_new_topic";
	
	/** state mode when showing the new category form */
	private static final String	MODE_NEW_CATEGORY = "mode_new_category";
	
	/** state mode when showing the reply form */
	private static final String	MODE_REPLY = "mode_reply";
	
	/** state mode when showing the reply preview */
	private static final String	MODE_REPLY_PREVIEW = "mode_reply_preview";

	/** state mode when confirm deleting category*/
	private static final String	MODE_DELETE_CATEGORY_CONFIRM = "mode_delete_category_confirm";
	
	/** state mode when confirm deleting message */
	private static final String	MODE_DELETE_MESSAGE_CONFIRM = "mode_delete_message_confirm";
	
	/** visited messages */
	private static final String VISITED_MESSAGES = "visited_messages";
	
	/** left_right layout */
	private static final String STATE_LEFT_RIGHT_LAYOUT = "state_left_right_layout";
	
	/** search criteria */
	private static final String STATE_SEARCH = "state_search";
	
	/** boolean value indicating the need to update the current message shown */
	private static final String STATE_SEARCH_REFRESH = "state_search_refresh";
	
	/** Form fields. */
	protected static final String FORM_SEARCH = "search";
	
	/** state selected view */
	private static final String STATE_SELECTED_VIEW = "state_selected_view";
		
	/**
	* Populate the state object, if needed.
	* @param state The state object.
	* @param config The portlet config.
	* @param pageSessionId The is of the current portal page session.
	* @param elementId The id of the portlet element that wants to be notified on change.
	* @param rundata The current request's rundata.
	*/
	protected void initState(	SessionState state, 
							VelocityPortlet portlet,
							JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		PortletConfig config = portlet.getPortletConfig();
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		String channelExist = (String) state.getAttribute(CHANNEL_EXIST);
		if (channelExist == null)
		{
			
			if ( channelId == null)
			{				
				// read the channel from configuration, or, if not specified, use the default for the page
				channelId = StringUtil.trimToNull(config.getInitParameter("channel"));
				if (channelId == null)
				{
					channelId = DiscussionService.channelReference(PortalService.getCurrentSiteId(), SiteService.MAIN_CONTAINER);
				}
				state.setAttribute(STATE_CHANNEL_REF, channelId);
			}

			DiscussionChannel channel = null;
			try
			{
				channel = (DiscussionChannel) DiscussionService.getDiscussionChannel(channelId);
			}
			catch (IdUnusedException e)
			{
				try
				{
					DiscussionChannelEdit channelEdit = DiscussionService.addDiscussionChannel(channelId);
					DiscussionService.commitChannel(channelEdit);
					channel = (DiscussionChannel) channelEdit;
				}
				catch (IdUsedException ee)
				{
					Log.error("chef", this +"" + ee);
				}
				catch (IdInvalidException ee)
				{
					Log.error("chef", this + "" + ee);
				}
				catch (PermissionException ee)
				{
					Log.error("chef", this + "no permission");
				}
			}
			catch (PermissionException e) {}
		
			if (channel!=null)
			{
				state.setAttribute(CHANNEL_EXIST, "true");

// TODO: Note: calling addCatagory here, which uses the state's STATE_EXPAND_CATEGORY_LIST which is not yet set is not good -ggolden
//				// make sure that the channel has categories as seeded in the config
//				String[] configCategories = parseCategoryString(StringUtil.trimToNull(config.getInitParameter("categories")));
//				if (configCategories != null)
//				{
//					for (int i = 0; i < configCategories.length; i++)
//					{					
//						addCategory(state, channel, configCategories[i]);
//					}
//				}

				if (state.getAttribute(STATE_CATEGORIES_SHOW_LIST) == null)
				{
					// get the catagories as defined without scanning all messages for more
					List categories = channel.getCategories(false);

					Hashtable h = new Hashtable();
					for (Iterator i = categories.iterator(); i.hasNext();)
					{
						String cat = (String) i.next();

						// store the ids of topic messages
						List topicMsgIds = channel.getTopicMsgIds(cat);
						h.put (cat, topicMsgIds);
					}
					state.setAttribute(STATE_CATEGORIES_SHOW_LIST, h);

					HashSet s = new HashSet();
					s.addAll(categories);
					state.setAttribute(STATE_EXPAND_CATEGORY_LIST, s);

					Hashtable t = new Hashtable();
					for (Iterator i = categories.iterator(); i.hasNext();)
					{
						String cat = (String) i.next();
						HashSet v = new HashSet();
						t.put (cat, v);
					}
					state.setAttribute(STATE_EXPAND_MESSAGE_LIST, t);

				}
			}

			// setup the observer to notify our MONITOR_PANEL panel(inside the Main panel)
			if (state.getAttribute(STATE_OBSERVER) == null)
			{			
				// the delivery location for this tool
				String deliveryId = clientWindowId(state, portlet.getID());

				// the html element to update on delivery
				String elementId = mainPanelUpdateId(portlet.getID()) + "." + MONITOR_PANEL;

				// the event resource reference pattern to watch for
				Reference r = new Reference(channelId);
				String pattern = DiscussionService.messageReference(r.getContext(), r.getId(), "");

				state.setAttribute(STATE_OBSERVER, new EventObservingCourier(deliveryId, elementId, pattern));
			}
		}

		if (state.getAttribute(STATE_DISPLAY_MESSAGE) == null)
		{
			state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(""));
		}

		/** The content type image lookup service in the State. */
		ContentTypeImageService iService = (ContentTypeImageService) state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE);
		if (iService == null)
		{
			iService = org.sakaiproject.service.legacy.content.cover.ContentTypeImageService.getInstance();
			state.setAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE, iService);
		}	// if

		if (state.getAttribute(STATE_SORTED_BY) == null)
		{
			state.setAttribute(STATE_SORTED_BY, STATE_SORTED_BY_DATE);
		}
		
		if (state.getAttribute(STATE_SORTED_ASC) == null)
		{
			state.setAttribute(STATE_SORTED_ASC, Boolean.TRUE.toString());
		}

		if (state.getAttribute(ATTACHMENTS)== null)
		{
			state.setAttribute(ATTACHMENTS, new ReferenceVector());
		}

		if (state.getAttribute(STATE_ASCENDING) == null)
		{
			state.setAttribute(STATE_ASCENDING, new Boolean(config.getInitParameter(PARAM_ASCENDING)));
		}

		if (state.getAttribute(STATE_EXPAND_ALL_FLAG) == null)
		{
			state.setAttribute(STATE_EXPAND_ALL_FLAG, new Boolean(false));
		}
		
		if (state.getAttribute(STATE_LEFT_RIGHT_LAYOUT) == null)
		{
			state.setAttribute(STATE_LEFT_RIGHT_LAYOUT, new Boolean(false));
		}
		
		if (state.getAttribute(VISITED_MESSAGES) == null)
		{
			state.setAttribute(VISITED_MESSAGES, new HashSet());
		}
		
		// show the list of assignment view first
		if (state.getAttribute(STATE_SELECTED_VIEW) == null)
		{
			state.setAttribute(STATE_SELECTED_VIEW, "2 Row Layout");
		}
		
		if (state.getAttribute(STATE_SEARCH) == null)
		{
			state.setAttribute(STATE_SEARCH, "");
		}
		
		// make sure the observer is in sync with state
		updateObservationOfChannel(state, portlet.getID());

	}   // initState
	
	/**
	 * Action is to use when doAttachment
	 * when "add attachments" is clicked
	 * @param runData The RunData object
	 * @param context The context object
	 **/
	public void doAttachments (RunData data, Context context)
	{		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		ParameterParser params = data.getParameters ();
		//state.setAttribute(STATE_MODE, "attach");

		// setup... we'll use the attachment action's mode
		state.setAttribute(AttachmentAction.STATE_MODE, AttachmentAction.MODE_MAIN);
		String subject = params.getString("subject");
		String stateFromText = "the discussion item";
		if (subject != null && subject.length() > 0)
		{
			stateFromText = "discussion item " + '"' + subject + '"';
		}
		state.setAttribute(AttachmentAction.STATE_FROM_TEXT, stateFromText);

		String mode = (String) state.getAttribute(STATE_MODE);
        if (mode != null && mode.equals(MODE_NEW_TOPIC))
        {
            subject = ((String) params.getString ("subject")).trim();
			state.setAttribute(NEW_TOPIC_SUBJECT, subject);
			
			String body = params.getCleanString ("body");
			body = processFormattedTextFromBrowser(state, body);
			state.setAttribute(NEW_TOPIC_BODY, body);

			String style = params.getString ("style");
			state.setAttribute(NEW_TOPIC_REPLY_STYLE, style);
			
			boolean newCategory = true;
			String category = ((String) params.getString ("newcategory")).trim();		
			if (category.length()==0)
			{
				// no new category input
				state.setAttribute(NEW_TOPIC_NEW_CATEGORY, new Boolean(Boolean.FALSE.toString()));
				category = ((String) params.getString("category")).trim();
                state.setAttribute(NEW_TOPIC_CATEGORY, category);
            }
            else
            {
                // new category input
                state.setAttribute(NEW_TOPIC_NEW_CATEGORY, new Boolean(Boolean.TRUE.toString()));
                state.setAttribute(NEW_TOPIC_CATEGORY, category);
            }
         }
        else if (mode!=null && mode.equals(MODE_REPLY))
        {
			try
            {
                DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
				
				// save the input infos for the respond message
				String replyToType = params.getString("replyto");
				state.setAttribute(RESPOND_REPLY_TO, replyToType);

				String replyToId = "";
				if (replyToType.equalsIgnoreCase ("tomessage"))
				{
					replyToId = params.getString("messageId");
				}
				else if (replyToType.equalsIgnoreCase ("totopic"))
				{
					replyToId = params.getString("topicId");
				}
				//state.setAttribute(RESPOND_REPLY_TO_ID, replyToId);
				
				subject = ((String) params.getString ("subject")).trim();
				state.setAttribute(RESPOND_SUBJECT, subject);

				String body = params.getCleanString ("body");
				body = processFormattedTextFromBrowser(state, body);
				state.setAttribute(RESPOND_BODY, body);
            }
            catch (Exception e)
            {
            }
        }
                    
		Vector attachments = (Vector) state.getAttribute(ATTACHMENTS);
		if (attachments!=null)
		{
			if (attachments.size() > 0)
			{
				state.setAttribute(AttachmentAction.STATE_HAS_ATTACHMENT_BEFORE, Boolean.TRUE);
			}
			else
			{
				state.setAttribute(AttachmentAction.STATE_HAS_ATTACHMENT_BEFORE, Boolean.FALSE);
			}
			state.setAttribute(AttachmentAction.STATE_ATTACHMENTS, attachments.clone());
		}
		
		state.setAttribute(AttachmentAction.STATE_MODE, AttachmentAction.MODE_MAIN);
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
	
	}	//doAttachments	
    
   /**
	* Setup our observer to be watching for change events for our channel.
	* @param state The state object.
	*/
	private void updateObservationOfChannel(SessionState state, String peid)
	{
		EventObservingCourier observer = (EventObservingCourier) state.getAttribute(STATE_OBSERVER);

		// make sure the pattern matches the channel we are looking at
		String channel = (String) state.getAttribute(STATE_CHANNEL_REF);
		Reference r = new Reference(channel);
		String pattern = DiscussionService.messageReference(r.getContext(), r.getId(), "");
		observer.setResourcePattern(pattern);

		// the delivery location for this tool
		String deliveryId = clientWindowId(state, peid);
		observer.setDeliveryId(deliveryId);
		
	}   // updateObservationOfChannel

	/**
	* If the state indicates an update is needed, update the portlet's configuration.
	* @param state The session state.
	* @param portlet The portlet to update.
	* @param data The current request run data.
	*/
	private void updatePortlet(SessionState state, VelocityPortlet portlet, RunData data)
	{
		// check the flag
		if (state.getAttribute(STATE_UPDATE) == null) return;

		// change the portlet's configuration
		portlet.setAttribute(PARAM_CHANNEL, (String)state.getAttribute(STATE_CHANNEL_REF), data);
		portlet.setAttribute(PARAM_ASCENDING, ((Boolean)state.getAttribute(STATE_ASCENDING)).toString(), data);

		// clear the flag
		state.removeAttribute(STATE_UPDATE);

	}	// updatePortlet

	/** 
	* build the context for the Layout panel
	* @return (optional) template name for this panel
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// if we are in edit permissions...
		String helperMode = (String) state.getAttribute(PermissionsAction.STATE_MODE);
		if (helperMode != null)
		{
			String template = PermissionsAction.buildHelperContext(portlet, context, rundata, state);
			if (template == null)
			{
				addAlert(state, "There is a problem setting Permissions at this time.");
			}
			else
			{
				return template;
			}
		}

		// if we are in edit attachments...
		String attachment_mode = (String) state.getAttribute(AttachmentAction.STATE_MODE);
		if (attachment_mode != null)
		{
			// if the attachment mode is not done, defer to the AttachmentAction
			if (!attachment_mode.equals(AttachmentAction.MODE_DONE))
			{
				return AttachmentAction.buildHelperContext(portlet, context, rundata, state);
			}
			// when done, get the new attachments (if null, no change was made)
			Vector attachments = (Vector) state.getAttribute(AttachmentAction.STATE_ATTACHMENTS);
			if (attachments != null)
			{
				state.setAttribute(ATTACHMENTS, attachments);
			}

			// clean up
			state.removeAttribute(AttachmentAction.STATE_MODE);
			state.removeAttribute(AttachmentAction.STATE_ATTACHMENTS);
		}
		context.put("attachments", state.getAttribute(ATTACHMENTS));
		
		String state_mode = (String) state.getAttribute(STATE_MODE);
		
		if (state_mode!=null) 
		{
			if (state_mode.equals (MODE_SHOW_TOPIC_CONTENT))
			{
				return buildContentContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_NEW_TOPIC))
			{
				return buildNewTopicContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_NEW_CATEGORY))
			{
				return buildNewCategoryContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_REPLY))
			{
				return buildReplyContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_REPLY_PREVIEW))
			{
				return buildReplyPreviewContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_DELETE_CATEGORY_CONFIRM))
			{
				return buildDeleteCategoryConfirmContext(portlet, context, rundata, state);
			}
			else if (state_mode.equals(MODE_DELETE_MESSAGE_CONFIRM))
			{
				return buildDeleteMessageConfirmContext(portlet, context, rundata, state);
			}
		}
		
		// assure that the portlet parameters are current with the user's configuration choices
		updatePortlet(state, portlet, rundata);

		context.put("panel-control", CONTROL_PANEL);
		context.put("panel-monitor", MONITOR_PANEL);
		context.put("panel-toolbar", TOOLBAR_PANEL);
		context.put("panel-next", NEXT_PANEL);

		context.put("layout_left_right", (Boolean) state.getAttribute(STATE_LEFT_RIGHT_LAYOUT));
		return (String)getContext(rundata).get("template") + "-Layout";
		
	}   // buildMainPanelContext
	
	/** 
	* build the context for the Layout / view panel
	* @return (optional) template name for this panel
	*/
	private String buildLayoutAttachContext(VelocityPortlet portlet, 
												Context context,
												JetspeedRunData rundata,
												SessionState state)
	{
		ParameterParser params = rundata.getParameters ();
		

		// setup... we'll use the attachment action's mode
		state.setAttribute(AttachmentAction.STATE_MODE, AttachmentAction.MODE_MAIN);
		state.setAttribute(AttachmentAction.STATE_FROM_TEXT, "ThreadedDiscussion item " + "\"" + params.getString ("subject") + "\"");
		
		Vector attachments = (Vector) state.getAttribute(ATTACHMENTS);
		if (attachments!=null)
		{
			state.setAttribute(AttachmentAction.STATE_ATTACHMENTS, attachments.clone());
		}		
		return (String)getContext(rundata).get("template") +"-List";
		
	}   // buildLayoutAttachContext
	
	
	/** 
	* build the context for the menu panel
	* @return (optional) template name for this panel
	*/
	public String buildToolbarPanelContext(VelocityPortlet portlet, 
											Context context,
											RunData data,
											SessionState state)
	{
		String 	form = "";
		boolean outlineFlag = true;
		
		boolean allowNewTopic = false;		
		boolean allowViewContent = false;
		
		boolean hasPrevious = false;		
		boolean hasNext = false;
		
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			
			DisplayMessage dMessage = (DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE);
			String currentId = dMessage.getId();
			if (!currentId.equals(""))
			{
				DiscussionMessage message = channel.getDiscussionMessage(currentId);
				String category = message.getDiscussionHeader().getCategory();
				Vector messageIds = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);
				hasPrevious = (messageIds.indexOf(currentId) == 0)?false:true;
				hasNext = ((messageIds.indexOf(currentId) == messageIds.size() - 1))?false:true;
			}
			
			// detect whether channel is existed
			if (channel!=null)
			{			
				allowNewTopic =channel.allowAddTopicMessage(); 
				allowViewContent = channel.allowGetMessages();
			}			
			else
			{
				allowNewTopic = DiscussionService.allowAddChannel(channelId);
				allowViewContent = DiscussionService.allowGetChannel(channelId);
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}
				
		// build menu
		Menu bar = new Menu (portlet, data, "ThreadedDiscussionIIAction");
		bar.add( new MenuEntry("New Topic...", null, allowNewTopic, MenuItem.CHECKED_NA, "doSet_new_topic"));
		bar.add( new MenuEntry("New Category...", null, allowNewTopic, MenuItem.CHECKED_NA, "doSet_new_category"));

		if (hasPrevious)
		{
			bar.add( new MenuEntry("Previous Message", null, true, MenuItem.CHECKED_NA, "doPre_message"));
		}
		if (hasNext)
		{
			bar.add( new MenuEntry("Next Message", null, true, MenuItem.CHECKED_NA, "doNext_message"));
		}
		
		if ( !((Boolean) state.getAttribute(STATE_EXPAND_ALL_FLAG)).booleanValue())
		{
			bar.add( new MenuEntry("Expand All", null, true, MenuItem.CHECKED_NA, "doExpand_all"));
		}
		else
		{
			bar.add( new MenuEntry("Collapse All", null, true, MenuItem.CHECKED_NA, "doCollapse_all"));
		}
		
		// Set menu state attribute
		SessionState stateForMenus = ((JetspeedRunData)data).getPortletSessionState (portlet.getID ());
		stateForMenus.setAttribute (MenuItem.STATE_MENU, bar);

		// add permissions, if allowed
		if (SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
		{
			bar.add( new MenuEntry("Permissions...", "doPermissions") );
		}
		
		context.put (Menu.CONTEXT_MENU, bar);
		context.put (Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));
		
		add2ndToolbarFields(data, context);
		
		return null;
		
	}	// buildControlMenu
	
	/** 
	* build the context for the menu panel
	* @return (optional) template name for this panel
	*/
	public void buildControlMenu(	VelocityPortlet portlet,
	Context context,
											RunData data,
											SessionState state,
											DiscussionChannel channel,
											String channelId)
	{
		String 	form = "";
		boolean outlineFlag = true;
		String expandFunction = NULL_STRING;
		String collapseFunction = NULL_STRING;
			
		boolean allowNewTopic = false;		
		// is this user going to be able to post?
		boolean allowSend = false;

		// detect whether channel is existed
		if (channel!=null)
		{			
			allowNewTopic =channel.allowAddTopicMessage(); 
		}
		else
		{
			allowNewTopic = DiscussionService.allowAddChannel(channelId);
		}

		// if no channel, we can check the user's allow add discussion Channel(channelId)
		if (channel != null)
		{
			allowSend = channel.allowAddMessage();
		}
		else
		{
			allowSend = DiscussionService.allowAddChannel(channelId);
		}
		
		String id = ((DisplayMessage)state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
		
	}	// buildcontrolmenu
	
	/** 
	* build the context for the List panel
	* @return (optional) template name for this panel
	*/
	public String buildListPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		String search = (String) state.getAttribute(STATE_SEARCH);
		if (search == null || search.equals(""))
		{
			context.put("searching", Boolean.FALSE);
		}
		else
		{
			context.put("searching", Boolean.TRUE);
		}
		
		Vector searchResultList = new Vector();
			
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		
		// the sorting defaults for the outline view
		String sortedBy = "";
		String sortedAsc = (String) state.getAttribute(STATE_SORTED_ASC);

		// set default sort attribute 
		sortedBy = (String) state.getAttribute(STATE_SORTED_BY);
		if ((!sortedBy.equals(STATE_SORTED_BY_TOPIC)) && (!sortedBy.equals(STATE_SORTED_BY_AUTHOR)) && (!sortedBy.equals(STATE_SORTED_BY_DATE)))
		{
			sortedBy = STATE_SORTED_BY_DATE;
			state.setAttribute(STATE_SORTED_BY, sortedBy);
		}
		context.put ("currentSortedBy", sortedBy);
		context.put ("currentSortAsc", sortedAsc);
		
		boolean allowNewTopic = false;		
		boolean allowViewContent = false;
		boolean allowRemoveCategory = false;
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);

			// detect whether channel is existed
			if (channel!=null)
			{			
				allowNewTopic =channel.allowAddTopicMessage(); 
				allowViewContent = channel.allowGetMessages();
				allowRemoveCategory = channel.allowRemoveCategory();
				context.put ("channel", channel);

				// get the sorted categories Vector according to the current sorting criteria
				List categories = sortedCategories(channel, sortedBy, sortedAsc);
				if (categories != null)
				{
					context.put ("categories", categories);
				}

				// provide the expanded category list
				HashSet expandedCategories = (HashSet) state.getAttribute(STATE_EXPAND_CATEGORY_LIST);
				context.put ("expandedCategoryList", expandedCategories);

				if (categories != null)
				{
					for (Iterator i=categories.iterator(); i.hasNext();)
					{
						String currentCategory = (String) i.next();

						// the category is expanded; get the most recent topics
						if (expandedCategories.contains (currentCategory))
						{
							Hashtable showList =  (Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST);
							Vector v = (Vector) showList.get(currentCategory);
							if (v == null)
								v = new Vector();
							Iterator topics = channel.getTopics(currentCategory);
							while (topics.hasNext ())
							{
								DiscussionMessage topic = (DiscussionMessage) topics.next ();
								// the topic is newly added, add the topic
								if (!v.contains (topic.getId()))
								{
									int index = 0;
									// the replied to message has already been expanded				
									if (topic.getDiscussionHeader().getDraft())
									{
										try
										{
											// filter out drafts not by this user (unless this user is a super user) %%% not sure I like this -zqian
											channel.getDiscussionMessage(topic.getId());

											// if the new message is a draft message, add to the bottom of drafts
											int firstNondraftIndex = -1;
											for (int j = 0; (j<v.size () && firstNondraftIndex == -1);j++)
											{
												DiscussionMessage next = (DiscussionMessage) channel.getDiscussionMessage((String) v.get (j));
												index = index + 1;
												if (!next.getDiscussionHeader().getDraft())
												{
													firstNondraftIndex = index;
												}								
											}
											if (firstNondraftIndex == -1)
											{
												v.add(topic.getId());
											}
											else
											{
												v.add (firstNondraftIndex, topic.getId());
											}
										}
										catch (PermissionException e){}
										catch (IdUnusedException e){}
									}
									else
									{
										// add it to the end
										v.add (topic.getId());
									}	// if-else

								}
							}

							// check for the expanded message's responses
							Hashtable t =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
							HashSet s = (HashSet)t.get (currentCategory);
							if (s!=null)
							{
								Iterator expandMessageInCategory = ((HashSet) t.get (currentCategory)).iterator ();
								while (expandMessageInCategory.hasNext ())
								{
									DiscussionMessage nextMessage = (DiscussionMessage) expandMessageInCategory.next ();
									Iterator repliesToNextMessage = nextMessage.getReplies();
									// only test for the direct response
									int depth = nextMessage.getReplyToDepth()+1;
									while (repliesToNextMessage.hasNext ())
									{
										DiscussionMessage testReply = (DiscussionMessage) repliesToNextMessage.next ();
										if ((testReply.getReplyToDepth()==depth) && (!v.contains (testReply.getId())))
										{
											int index = v.indexOf (nextMessage.getId());
											// the replied to message has already been expanded				
											if (testReply.getDiscussionHeader().getDraft())
											{
												try
												{
													// filter out drafts not by this user (unless this user is a super user) %%% not sure I like this -zqian
													channel.getDiscussionMessage(testReply.getId());

													// if the new message is a draft message, add to the bottom of drafts
													int firstNondraftIndex = -1;
													int k = index;
													while (k < v.size() && (firstNondraftIndex == -1))
													{
														DiscussionMessage next = (DiscussionMessage) channel.getDiscussionMessage((String) v.get(k));
														index = index + 1;
														if (!next.getDiscussionHeader().getDraft())
														{
															firstNondraftIndex = index;
														}								
														k++;
													}
													v.add (firstNondraftIndex, testReply.getId());
												}
												catch (PermissionException e){}
												catch (IdUnusedException e){}

											}
											else
											{
												// if the new message is not a draft message, add to the bottom of non-drafts
												index = v.indexOf (nextMessage.getId()) + 1;
												String id = nextMessage.getId();
												String replyToId = id;
												while (replyToId.equals(id) && index < v.size())
												{
													DiscussionMessage m = (DiscussionMessage) channel.getDiscussionMessage((String) v.get(index));
													String newId = m.getDiscussionHeader().getReplyTo();
													if (!newId.equals(id))
													{
														replyToId = newId; 
													}
													else
													{
														index++;
													}
												}
												v.add(index, testReply.getId());
											}	// if-else: draft vs. non draft									
										}	// if: the message is not contained in the show list
									}	// while: check for all response to the message
								}	// while: check for all opened message
							}	// if: there is expanded message

							// update the show list in that category
							showList.put(currentCategory, v);
							
							// find the search result
							if ((search != null) && (!search.equals("")))
							{
								for (int k = 0; k<v.size(); k++)
								{
									DiscussionMessage message = channel.getDiscussionMessage((String) v.get(k));
									if (	StringUtil.containsIgnoreCase(message.getDiscussionHeader().getSubject(),search)
											||	StringUtil.containsIgnoreCase(FormattedText.convertFormattedTextToPlaintext(message.getBody()), search))
										{
											searchResultList.add(message.getId());
										}
								}
							}
							state.setAttribute(STATE_CATEGORIES_SHOW_LIST, showList);
						}	//if:  for every opened category
					}	// for: for all categories
				}	// if: categories not null
				
				if (searchResultList.size()>0 && state.getAttribute(STATE_SEARCH_REFRESH) != null)
				{
					// if the current message is not in result list, make the current message to be the first one of search result
					state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage((String) searchResultList.get(0)));	
					state.removeAttribute(STATE_SEARCH_REFRESH);
				}
				context.put("searchResultList", searchResultList);
				
				// provide the category show list
				context.put ("categoriesShowList", state.getAttribute(STATE_CATEGORIES_SHOW_LIST));
				// the topics has been expanded for outline
				context.put ("expandAllTopics", state.getAttribute (STATE_EXPAND_ALL_TOPIC_FLAG));
				// provide the expanded message list
				context.put ("expandedMessageList", state.getAttribute(STATE_EXPAND_MESSAGE_LIST));
			}
			else
			{
				allowNewTopic = DiscussionService.allowAddChannel(channelId);
				allowViewContent = DiscussionService.allowGetChannel(channelId);
			}

			// inform the observing courier that we just updated the page...
			// if there are pending requests to do so they can be cleared
			((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).justDelivered();
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}

		context.put("allowEditChannel", new Boolean(DiscussionService.allowEditChannel(channelId)));
		context.put ("action", (String) state.getAttribute(STATE_ACTION));
		context.put ("service", DiscussionService.getInstance());
		context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));

		context.put("allow_new_topic", new Boolean(allowNewTopic));
		context.put("allow_new_category", new Boolean(allowNewTopic));
		context.put("allow_remove_category", new Boolean(allowRemoveCategory));

		context.put("panel-control", CONTROL_PANEL);
		context.put("panel-monitor", MONITOR_PANEL);
		context.put("panel-target", MONITOR_PANEL);
		context.put("updated", (String) state.getAttribute(STATE_LIST_PANNEL_UPDATED));
		state.setAttribute(STATE_LIST_PANNEL_UPDATED, "false");

		context.put("currentMessage", state.getAttribute(STATE_DISPLAY_MESSAGE));
		
		context.put("visitedMessages", state.getAttribute(VISITED_MESSAGES));
		
		return null;

	}   // buildListPanelContext

	/** 
	* build the context for the Control panel
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildControlPanelContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		boolean allowNewTopic = false;		
		boolean allowViewContent = false;

		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		DiscussionChannel channel = null;
		try
		{
			channel = DiscussionService.getDiscussionChannel(channelId);
			
			// possible error message
			String error = (String) state.getAttribute(STATE_ERROR);
			if (error !=  null)
			{
				context.put("message", error);
			}

			// is this user going to be able to post?
			boolean allowed = false;

			// if no channel, we can check the user's allow add discussion Channel(channelId)
			if (channel != null)
			{
				allowed = channel.allowAddMessage();
			}
			else
			{
				allowed = DiscussionService.allowAddChannel(channelId);
			}
			if (!allowed)
			{
				context.put("message", PERMISSION_POST_MESSAGE);	// %%% or no message?
			}
			context.put("allow-send", new Boolean(allowed));

            DisplayMessage dMessage = (DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE);
            if (dMessage!=null)
            {
                String mId = dMessage.getId();
                if (!mId.equals(""))
                {
                    try
                    {
                        DiscussionMessage m = channel.getDiscussionMessage(mId);
                        context.put("currentMessage", m);

                        DiscussionMessageHeader mHeader = m.getDiscussionHeader();
                        String replyTo = (String) state.getAttribute(RESPOND_REPLY_TO);
                        context.put("attachments", state.getAttribute(ATTACHMENTS));
                        if (mHeader.getDraft())
                        {
                            // a draft message
                            context.put("draftCategory", state.getAttribute(DRAFT_MESSAGE_CATEGORY));
                            context.put("draftBody", state.getAttribute(DRAFT_MESSAGE_BODY));
                            context.put("draftSubject", state.getAttribute(DRAFT_MESSAGE_SUBJECT));
                            if (m.getReplyToDepth() == 0)
                            {
                                String style = (String) state.getAttribute(DRAFT_MESSAGE_REPLY_STYLE);
                                if (style == null)
                                {
                                    style = "thread";
                                }
                                context.put("draftStyle", style);
                            }
                        }
                        else
                        {
                            // respond to posted message    
                            if (replyTo == null)
                            {
                                replyTo = "totopic";
                            }
                            context.put("replyto", replyTo);
                            context.put("subject", state.getAttribute(RESPOND_SUBJECT));
                            context.put("body", state.getAttribute(RESPOND_BODY));
                        }

                        DiscussionMessage topic = m;
                        while (topic.getReplyToDepth()!=0)
                        {
							replyTo = topic.getDiscussionHeader().getReplyTo();
                            topic = channel.getDiscussionMessage(replyTo);
                        }
                        context.put("topic", topic);
                    }
                    catch (Exception e)
                    {
                    }   // try - catch
                }   // if - else    
            }   // based on the DisplayMessage type
            context.put ("channel", channel);
        }
        catch (IdUnusedException e)
        {
            addAlert(state, "Can not find the discussion channel. ");
        }
        catch (PermissionException e)
        {
            addAlert(state, "You do not have the permission to this discussion channel. ");
        }
		
		// detect whether channel is existed
		if (channel!=null)
		{			
			allowNewTopic =channel.allowAddTopicMessage(); 
			allowViewContent = channel.allowGetMessages();
		}
		else
		{
			allowNewTopic = DiscussionService.allowAddChannel(channelId);
			allowViewContent=DiscussionService.allowGetChannel(channelId);
		}
		context.put("panel-target", CONTROL_PANEL);

		// put this pannel's name for the return url
		context.put("panel-control", CONTROL_PANEL);

		// set the action for form processing
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

		// set the form field name for the send button
		context.put("form-submit", BUTTON + "doSend");

		// set the form field name for the send button
		context.put("form-message", FORM_MESSAGE);
		
		context.put ("date", TimeService.newTime());
		context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));

		context.put ("action", (String) state.getAttribute(STATE_ACTION));
		
		
		buildControlMenu(portlet, context, rundata, state, channel, channelId);
		return null;

	}   // buildControlPanelContext

	/** 
	* build the context for showing topic content
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildContentContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		String topicId = (String) state.getAttribute(STATE_SHOW_CONTENT_TOPIC_ID);

		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			context.put("topic", channel.getDiscussionMessage(topicId));
			context.put("channel", channel);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the selected topic. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to view the topic. ");
		}

		context.put ("action", (String) state.getAttribute(STATE_ACTION));
		return (String)getContext(rundata).get("template") + "-topic_content";
		
	}	// buildContentContext
	
	/** 
	* build the context for creating new topic
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildNewTopicContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{		
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			context.put ("categories",  sortedCategories(channel, STATE_SORTED_BY_CATEGORY_ALPHA, Boolean.TRUE.toString ()));
			context.put ("date", TimeService.newTime());
			context.put ("category", state.getAttribute(NEW_TOPIC_CATEGORY));
			context.put ("newcategory", state.getAttribute(NEW_TOPIC_NEW_CATEGORY));
			context.put("newtopicbody", state.getAttribute(NEW_TOPIC_BODY));
			context.put("newtopicsubject", state.getAttribute(NEW_TOPIC_SUBJECT));
			context.put("newtopicreplystyle", state.getAttribute(NEW_TOPIC_REPLY_STYLE));
			context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));
			context.put ("action", (String) state.getAttribute(STATE_ACTION));
			context.put("attachments", state.getAttribute(RESPOND_ATTACHMENT));
			
			return (String)getContext(rundata).get("template") + "-Newtopic";
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}		
		
		return (String)getContext(rundata).get("template") + "-Layout";
		
	}	// buildNewTopicContext
	
	/** 
	* build the context for creating new category
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildNewCategoryContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		String currentMessageId = ((DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			context.put ("categories",  sortedCategories(channel, STATE_SORTED_BY_CATEGORY_ALPHA, Boolean.TRUE.toString ()));
			context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));
			context.put ("action", (String) state.getAttribute(STATE_ACTION));
			
			return (String)getContext(rundata).get("template") + "-Newcategory";
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}		

		return (String)getContext(rundata).get("template") + "-Layout";
		
	}	// buildNewCategoryContext
	
	/** 
	* build the context for confirming the delete of message
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildDeleteMessageConfirmContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{		
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			DiscussionMessage message = channel.getDiscussionMessage((String) state.getAttribute(DELETE_MESSAGE_ID));
			context.put("message", message);
			context.put("delete_messages", channel.getThread(message));

			return (String)getContext(rundata).get("template") + "-DeleteTopicConfirm";
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to delete the message. ");
		}		
		
		return (String)getContext(rundata).get("template") + "-Layout";
		
	}	// buildDeleteMessageConfirmContext
	
	/** 
	* build the context for confirming the delete of category
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildDeleteCategoryConfirmContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		
		String category = (String) state.getAttribute(DELETE_CATEGORY);
		context.put("category", category);
		
		// get all the messages within the category, that will be deleted
		try
		{
			DiscussionChannel channel = (DiscussionChannel) DiscussionService.getChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			Iterator messages = channel.getThreads(category);
			context.put("delete_messages", messages);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
			return (String)getContext(rundata).get("template");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to delete the discussion category. ");
			return (String)getContext(rundata).get("template");
		}	

		return (String)getContext(rundata).get("template") + "-DeleteCategoryConfirm";
		
	}	// buildDeleteCategoryConfirmContext
	
	/** 
	* build the context for the reply form
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildReplyContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		String currentMessageId = ((DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			try
			{
				DiscussionMessage m = channel.getDiscussionMessage(currentMessageId);
				context.put("currentMessage", m);
				
				String replyTo = (String) state.getAttribute(RESPOND_REPLY_TO);
				// respond to posted message    
				if (replyTo == null)
				{
					replyTo = "totopic";
				}
				context.put("replyto", replyTo);
				context.put("subject", state.getAttribute(RESPOND_SUBJECT));
				context.put("body", state.getAttribute(RESPOND_BODY));
							
				DiscussionMessage topic = m;
				while (topic.getReplyToDepth()!=0)
				{
					replyTo = topic.getDiscussionHeader().getReplyTo();
					topic = channel.getDiscussionMessage(replyTo);
				}
				context.put("topic", topic);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Can not find the selected message. ");
			}
			catch (PermissionException e)
			{
				addAlert(state, "You are not allowed to view the selected message. ");
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}		

		context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));
		context.put("attachments", state.getAttribute(ATTACHMENTS));
		context.put ("action", (String) state.getAttribute(STATE_ACTION));
		return (String)getContext(rundata).get("template") + "-Reply";
		
	}	// buildReplyContext
	
	/** 
	* build the context for the reply preview
	* (has a send field)
	* @return (optional) template name for this panel
	*/
	public String buildReplyPreviewContext(VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		String replyToId = (String) state.getAttribute(RESPOND_REPLY_TO);
				
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			try
			{
				DiscussionMessage m = channel.getDiscussionMessage(replyToId);
				context.put("currentMessage", m);
				
				DiscussionMessage topic = m;
				String replyTo = "";
				while (topic.getReplyToDepth()!=0)
				{
					replyTo = topic.getDiscussionHeader().getReplyTo();
					topic = channel.getDiscussionMessage(replyTo);
				}
				context.put("topic", topic);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Can not find the selected message. ");
			}
			catch (PermissionException e)
			{
				addAlert(state, "You are not allowed to view the selected message. ");
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}		

		context.put("subject",	state.getAttribute(RESPOND_SUBJECT));
		context.put("body", state.getAttribute(RESPOND_BODY));
		context.put("attachments", state.getAttribute(RESPOND_ATTACHMENT));
		context.put ("contentTypeImageService", state.getAttribute(STATE_CONTENT_TYPE_IMAGE_SERVICE));
	
		context.put ("action", (String) state.getAttribute(STATE_ACTION));
		return (String)getContext(rundata).get("template") + "-Reply_Preview";
		
	}	// buildReplyPreviewContext
	
	/**
	 * Post the draft message
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doPost ( RunData data, Context context )
	{
		ParameterParser params = data.getParameters();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		
		String messageId = params.getString ("messageId");
		String subject = ((String) params.getString ("subject")).trim();
		if (subject.length ()==0)
		{
			state.setAttribute(DRAFT_MESSAGE_SUBJECT, "");
			addAlert(state,"Please specify the subject for the message. ");
		}
		String body = params.getCleanString ("body");
		body = processFormattedTextFromBrowser(state, body);
		String style = params.getString ("style");

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			try
			{
				DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
				DiscussionMessageEdit postMessage = channel.editDiscussionMessage (messageId);
                
				postMessage.getDiscussionHeaderEdit ().setSubject (subject);
				postMessage.setBody (body);
                
                // add the reply style property
				if (postMessage.getReplyToDepth()==0)
				{
					ResourcePropertiesEdit pEdit = postMessage.getPropertiesEdit();
					pEdit.addProperty(ResourceProperties.PROP_REPLY_STYLE, style);
				}
				postMessage.getDiscussionHeaderEdit().setDraft(false);
				postMessage.getDiscussionHeaderEdit().replaceAttachments((ReferenceVector) state.getAttribute(ATTACHMENTS));
				
				channel.commitMessage(postMessage);
				state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(postMessage.getId()));
			}
			catch (IdUnusedException e)
			{
				addAlert(state,"The id " + messageId + " has not been used. ");
			}
			catch(PermissionException e)
			{
				addAlert(state,PERMISSION_HEADER_STRING + "post the message. ");
			}
			catch (InUseException e)
			{
				addAlert(state, "The message is locked for another usage. ");
			}
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
            state.removeAttribute(DRAFT_MESSAGE_CATEGORY);
            state.removeAttribute(DRAFT_MESSAGE_SUBJECT);
            state.removeAttribute(DRAFT_MESSAGE_BODY);
            state.removeAttribute(DRAFT_MESSAGE_REPLY_STYLE);
			state.setAttribute(ATTACHMENTS, null);
			
							// update the list panel
			String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + MONITOR_PANEL);

			// post sucessful
		}	//if-else

	}	// doPost
    
    /**
	 * Save the draft message
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doSave ( RunData data, Context context )
	{
		ParameterParser params = data.getParameters();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String messageId = params.getString ("messageId");
		String subject = ((String) params.getString ("subject")).trim();
		if (subject.length ()==0)
		{
			state.setAttribute(DRAFT_MESSAGE_SUBJECT, "");
			addAlert(state,"Please specify the subject for the message. ");
		}
		String body = params.getCleanString ("body");
		body = processFormattedTextFromBrowser(state, body);
		state.setAttribute(DRAFT_MESSAGE_BODY, body);
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			try
			{
				DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
				DiscussionMessageEdit postMessage = channel.editDiscussionMessage (messageId);
                
				postMessage.getDiscussionHeaderEdit ().setSubject (subject);
				postMessage.setBody (body);
				
                // add the reply style property
				if (postMessage.getReplyToDepth()==0)
				{
					String style = params.getString ("style");
					state.setAttribute(DRAFT_MESSAGE_REPLY_STYLE, style);
					ResourcePropertiesEdit pEdit = postMessage.getPropertiesEdit();
					pEdit.addProperty(ResourceProperties.PROP_REPLY_STYLE, style);
				}           
				postMessage.getDiscussionHeaderEdit().setDraft(true);
				postMessage.getDiscussionHeaderEdit().replaceAttachments((ReferenceVector) state.getAttribute(ATTACHMENTS));
				
				channel.commitMessage(postMessage);
				state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(postMessage.getId()));
			}
			catch (IdUnusedException e)
			{
				addAlert(state,"The id " + messageId + " has not been used. ");
			}
			catch(PermissionException e)
			{
				addAlert(state,PERMISSION_HEADER_STRING + "view the message. ");
			}
			catch (InUseException e)
			{
				addAlert(state, "The message is locked for another usage. ");
			}
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// save successful
			state.setAttribute(ATTACHMENTS, null);
			
							// update the list panel
				String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + MONITOR_PANEL);

		}	//if-else

	}	// doSave
	
	/**
	 * Start a new category
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doNew_category ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));

			ParameterParser params = data.getParameters();
			String category = ((String) params.getString ("newcategory")).trim ();

			if (category.length()==0)
			{
				addAlert(state, "Please enter the category name. ");
			}
			else
			{
				// Note: removed code to detect that the category already exists - we just "fail" quietly to add it again -ggolden
				addCategory (state, channel, category);
				Hashtable h = (Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST);
				h.put(category, new Vector());
				state.setAttribute(STATE_CATEGORIES_SHOW_LIST, h);

				// update the list panel
				String peid = ((JetspeedRunData) data).getJs_peid();
				CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + MONITOR_PANEL);
				
				// clean the input frame
				state.removeAttribute(STATE_DISPLAY_MESSAGE);
				
				// clean the state mode
				state.removeAttribute(STATE_MODE);
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}
	}	// doNew_category
	
	/**
	 * Start a new topic
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doNew_topic ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));

			ParameterParser params = data.getParameters();
			String subject = ((String) params.getString ("subject")).trim();
			state.setAttribute(NEW_TOPIC_SUBJECT, subject);
			
			String body = params.getCleanString ("body");
			body = processFormattedTextFromBrowser(state, body);
			state.setAttribute(NEW_TOPIC_BODY, body);

			String style = params.getString ("style");
			state.setAttribute(NEW_TOPIC_REPLY_STYLE, style);
			
			boolean newCategory = true;
			String category = ((String) params.getString ("newcategory")).trim();		
			if (category.length()==0)
			{
				// no new category input
				state.setAttribute(NEW_TOPIC_NEW_CATEGORY, new Boolean("false"));
				category = ((String) params.getString("category")).trim();
				state.setAttribute(NEW_TOPIC_CATEGORY, category);
				if (category.length()==0)
				{
					// no category specified
					addAlert(state, "Please choose a category. ");
				}
				newCategory = false;
			}
			else
			{
				// get the new category
				state.setAttribute(NEW_TOPIC_NEW_CATEGORY, new Boolean("true"));
				state.setAttribute(NEW_TOPIC_CATEGORY, category);
				// Note: removed the check that the category already exists - we just quietly "fail" to add it again -ggoldne
			}	// if

			if (state.getAttribute(STATE_MESSAGE) == null)
			{
				try
				{
					// message is a draft?
					boolean draft = false;

					String newTopic_option = params.getString ("eventSubmit_doNew_topic");
					if (newTopic_option.equals ("Save Draft"))
					{
						draft = true;
					}
					else if (newTopic_option.equals ("Post Topic"))
					{
						draft = false;
					}	// if: draft?

					if (subject.length ()==0)
					{
						addAlert(state,"Please specify the topic. ");
					}
					else
					{
						boolean sameTopic = false;
						Iterator l = channel.getTopics (category);
						while (l.hasNext ())
						{
							if (subject.equals (((DiscussionMessage) l.next ()).getDiscussionHeader ().getSubject ()))
							{
								addAlert(state,"Same topic has been posted. Please use a different topic. ");
								sameTopic = true;
							}
						}

						if (!sameTopic)
						{
							// add the reply style property
							DiscussionMessageEdit addedMessageEdit = channel.addDiscussionMessage(""); 
							addedMessageEdit.setBody(body);
							
							DiscussionMessageHeaderEdit hEdit = addedMessageEdit.getDiscussionHeaderEdit();
							hEdit.setCategory(category);
							hEdit.setSubject(subject);
							hEdit.setDraft(draft);
							hEdit.replaceAttachments((ReferenceVector) state.getAttribute(ATTACHMENTS));
							
							ResourcePropertiesEdit pEdit = addedMessageEdit.getPropertiesEdit();
							pEdit.addProperty(ResourceProperties.PROP_REPLY_STYLE, style);
							channel.commitMessage(addedMessageEdit);

							// if the category is newly added
							if (newCategory)
							{						
								addCategory(state, channel, category);
							}
							setCategoryExpanded(state, category, true, channel);
							
							// make it the current message
							showMessage(data, addedMessageEdit.getId());
			                	String peid = ((JetspeedRunData) data).getJs_peid();
			                	CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + CONTROL_PANEL);

						}	// if-else
					}
				}
				catch (PermissionException e)
				{
					addAlert(state,PERMISSION_HEADER_STRING + "start a new topic. ");
				}
			}	// if category has been specified.
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}		
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// add new topic sucessful
            state.removeAttribute(NEW_TOPIC_NEW_CATEGORY);
            state.removeAttribute(NEW_TOPIC_CATEGORY);
            state.removeAttribute(NEW_TOPIC_SUBJECT);
            state.removeAttribute(NEW_TOPIC_BODY);
            state.removeAttribute(NEW_TOPIC_REPLY_STYLE);
			
			// clean the state mode
			state.removeAttribute(STATE_MODE);
			
		}	//if-else

	}	// doNew_topic
	
	/** 
	* Handle a user posting a respond message
	**/	
	public void doRespond(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		ParameterParser params = runData.getParameters();
		
		String subject = StringUtil.trimToNull((String) params.getString ("subject"));
	
		// message is a draft?
		boolean draft = false;

		// get info from input
		String body = params.getCleanString ("body");
		body = processFormattedTextFromBrowser(state, body);
		state.setAttribute(RESPOND_BODY, body);

		state.setAttribute(RESPOND_SUBJECT, subject);
		
		if (subject == null)
		{
			addAlert(state,"Please specify the subject for the message. ");
		}
		else
		{
			//get the reply to message id 
			String replyType = params.getString("replyto");
			state.setAttribute(RESPOND_REPLY_TO, replyType);
	
			String replyToId = "";
			if (replyType.equalsIgnoreCase ("tomessage"))
			{
				replyToId = params.getString("messageId");
			}
			else if (replyType.equalsIgnoreCase ("totopic"))
			{
				replyToId = params.getString("topicId");
			}
		
			try
			{
				DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
					
				try
				{
					//get the message thread before adding a new message
					DiscussionMessage originalMessage = channel.getDiscussionMessage (replyToId);
					String category = originalMessage.getDiscussionHeader().getCategory();
					Vector v = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);
					int index = v.indexOf (originalMessage.getId());
					Iterator l = channel.getThread(originalMessage);

					// insert the response message
					DiscussionMessage addedMessage = channel.addDiscussionMessage (category, subject, draft, replyToId, (ReferenceVector) state.getAttribute(ATTACHMENTS), body);
					
					// show the added message as the current message
					showMessage(runData, addedMessage.getId());
					
					if (!isMessageExpanded(state, originalMessage))
					{
						// the replied to message has not been expanded
						setMessageExpanded(state, originalMessage, true, channel);
					}
					else
					{
						if ((l==null) || (!l.hasNext()))
						{
							// if there is no reply message yet
							v.add(index+1, addedMessage.getId());
						}
						else
						{
							// if the new message is not a draft message, add to the bottom of non-drafts
							int finalIndexOfShownThread = index;
							while (l.hasNext ())
							{
								int newIndex = v.indexOf (((DiscussionMessage) l.next ()).getId());
								if (newIndex!=-1)
								{
									finalIndexOfShownThread = newIndex;
								}
							}
							v.add(finalIndexOfShownThread+1, addedMessage.getId());
							setCategoryShowList(state, category, v);
						}	// if - else
					}	// if-else

				}
				catch (PermissionException e)
				{
					addAlert(state,PERMISSION_HEADER_STRING + "respond. ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,"Can not find the replying to message. ");
				}
			}
			catch (PermissionException e)
			{
				addAlert(state,"You do not have permission to the discussion channel. ");
			}
			catch (IdUnusedException e)
			{
				addAlert(state,"Can not find the discussion channel. ");
			}
		}	// if-else
			
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(ATTACHMENTS,  null);
			// respond sucessful
            state.removeAttribute(RESPOND_REPLY_TO);
            state.removeAttribute(RESPOND_SUBJECT);
            state.removeAttribute(RESPOND_BODY);
			state.removeAttribute(RESPOND_ATTACHMENT);
			state.removeAttribute(STATE_MODE);
			
		}	//if-else
		
	}	// doRespond
	
	/** 
	* Handle a user posting a drafted respond message
	**/	
	public void doRespond_draft(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		ParameterParser params = runData.getParameters();
		//get the reply to message id 
		String replyType = params.getString("replyto");
		String replyToId = "";
		if (replyType.equalsIgnoreCase ("tomessage"))
		{
			replyToId = params.getString("messageId");
		}
		else if (replyType.equalsIgnoreCase ("totopic"))
		{
			replyToId = params.getString("topicId");
		}
		
		//get info from input
		String body = params.getCleanString ("body");
		body = processFormattedTextFromBrowser(state, body);
		String subject = ((String) params.getString ("subject")).trim();

		state.setAttribute(RESPOND_SUBJECT, subject);
		state.setAttribute(RESPOND_BODY, body);

		if (subject.length ()==0)
		{
			addAlert(state,"Please specify the subject for the message. ");
		}
		else
		{
			try
			{
				DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
	
				try
				{
					// message is a draft?
					boolean draft = true;
	
					//get the message thread before adding a new message
					DiscussionMessage originalMessage = channel.getDiscussionMessage (replyToId);
					String category = originalMessage.getDiscussionHeader().getCategory();
					Vector v = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);
					int index = v.indexOf (originalMessage.getId());
					Iterator l = channel.getThread(originalMessage);

					// insert the response message
					DiscussionMessage addedMessage = channel.addDiscussionMessage (category, subject, draft, replyToId, (ReferenceVector) state.getAttribute(ATTACHMENTS), body);
					showMessage(runData, addedMessage.getId());
					
					if (!isMessageExpanded(state, originalMessage))
					{
						// the replied to message has not been expanded
						setMessageExpanded(state, originalMessage, true, channel);
					}
					else
					{
						if ((l==null) || (!l.hasNext()))
						{
							// if there is no reply message yet
							v.add(index+1, addedMessage.getId());
						}
						else
						{
							// the replied to message has already been expanded				
							// if the new message is a draft message, add to the bottom of drafts
							int firstNondraftIndex = -1;
							while (l.hasNext () && (firstNondraftIndex == -1))
							{
								DiscussionMessage next = (DiscussionMessage) l.next ();
								index = index + 1;
								if (!next.getDiscussionHeader().getDraft())
								{
									firstNondraftIndex = index;
								}								
							}
	
							// all are drafts for now
							if (firstNondraftIndex == -1)
							{
								firstNondraftIndex = index + 1; 
							}
							v.add (firstNondraftIndex, addedMessage.getId());
							setCategoryShowList(state, category, v);
						}	// if - else
						
					}	// if-else

				}
				catch (PermissionException e)
				{
					addAlert(state,PERMISSION_HEADER_STRING + "respond. ");
				}
				catch (IdUnusedException e)
				{
					addAlert(state,"Can not find the replying to message. ");
				}
			}
			catch (PermissionException e)
			{
			addAlert(state,"You do not have permission to the discussion channel. ");
			}
			catch (IdUnusedException e)
			{
			addAlert(state,"Can not find the discussion channel. ");
			}
		}	// if
			
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			// respond sucessful
            state.removeAttribute(RESPOND_REPLY_TO);
            state.removeAttribute(RESPOND_SUBJECT);
            state.removeAttribute(RESPOND_BODY);
			state.removeAttribute(RESPOND_ATTACHMENT);
			state.removeAttribute(STATE_MODE);
			
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));

		}	//if-else
		
	}	// doRespondDraft
	
	/**
	 *  Cancel from the reply page
	 */
	public void doCancel_reply ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.removeAttribute(RESPOND_REPLY_TO);
		state.removeAttribute(RESPOND_SUBJECT);
		state.removeAttribute(RESPOND_BODY);
		state.removeAttribute(RESPOND_ATTACHMENT);
		state.removeAttribute(ATTACHMENTS);
		state.removeAttribute(STATE_MODE);
				
	}	//doCancel_reply
	
	/**
	 *  Cancel from the reply preview page
	 */
	public void doCancel_reply_preview ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute(STATE_MODE, MODE_REPLY);

	}	//doCancel_reply
	
	/**
	 * confirm the deletion of category
	 */
	public void doDelete_category_confirm ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		ParameterParser params = data.getParameters();
		String category = params.getString("category");	
		state.setAttribute(STATE_MODE, MODE_DELETE_CATEGORY_CONFIRM);
		state.setAttribute(DELETE_CATEGORY, category);

		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
	}	//doDelete_category_confirm
	
	/**
	 * cancel the deletion of category
	 */
	public void doCancel_delete_category ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute(DELETE_CATEGORY, "");
		state.removeAttribute(STATE_MODE);
	}	// doCancel_delelete_category

	/**
	 * delete the category
	 */
	public void doDelete_category ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		ParameterParser params = data.getParameters();
		String category = params.getString("category");
		try
		{
			//first remove the category from the channel
			DiscussionChannelEdit channel = (DiscussionChannelEdit) DiscussionService.editChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			channel.removeCategory(category);
			DiscussionService.commitChannel(channel);
			
			// remove the category from the set of categories expanded on the user interface
			HashSet expandedCategories = (HashSet) state.getAttribute(STATE_EXPAND_CATEGORY_LIST);
			expandedCategories.remove(category);

			//second delete all the messages in the category
			DiscussionChannel channel2 = (DiscussionChannel) DiscussionService.getChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			Iterator messages = channel2.getThreads(category);
			while (messages.hasNext())
			{
				DiscussionMessage message = (DiscussionMessage) messages.next();
				channel2.removeMessage(channel2.editDiscussionMessage(message.getId()));
			}		
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to delete the discussion category. ");
		}	
		catch (InUseException e)
		{
			addAlert(state, INUSE_ERROR_MESSAGE + " discussion channel. ");
		}
		
		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.setAttribute(DELETE_CATEGORY, "");
			state.removeAttribute(STATE_MODE);

			String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		}

	}	// doDelete_category

	
	/**
	 * confirm the deletion of message
	 */
	public void doDelete_message_confirm ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String messageId = data.getParameters().getString("messageId");	

		state.setAttribute(STATE_MODE, MODE_DELETE_MESSAGE_CONFIRM);
		state.setAttribute(DELETE_MESSAGE_ID, messageId);

		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
	}	// doDelete_message_confirm
	

	public void doDelete_message ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		ParameterParser params = data.getParameters();
		String messageId = params.getString("messageId");

		try
		{
			DiscussionChannel channel = (DiscussionChannel) DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			DiscussionMessageEdit message = (DiscussionMessageEdit) channel.editMessage(messageId);
			String category = message.getDiscussionHeader().getCategory();
			
			// remove all the replies to the message
			Iterator replies = channel.getThread(message);
			while (replies.hasNext())
			{
				DiscussionMessageEdit reply = channel.editDiscussionMessage(((DiscussionMessage)replies.next()).getId());
				
				// remove the message from the show list
				Hashtable t = (Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST);
				Vector v = (Vector) t.get(category);
				v.remove(reply.getId());
				t.put (category, v);
				state.setAttribute(STATE_CATEGORIES_SHOW_LIST, t);
				
				// remove the message from the expanded message list if necessary
				Hashtable expandMessageTable =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
				HashSet expandMessageCategory = (HashSet) expandMessageTable.get(category);
				if (expandMessageCategory!=null)
				{
					expandMessageCategory.remove (reply.getId());
				}				
				
				// remove the message from the channel
				channel.removeMessage(reply);
			}
			
			// remove the message
			channel.removeMessage(message);
			
			// remove the message from the show list
			Hashtable t = (Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST);
			Vector v = (Vector) t.get(category);
			v.remove(messageId);
			t.put (category, v);
			state.setAttribute(STATE_CATEGORIES_SHOW_LIST, t);
			
			// remove the message from the expanded message list if necessary
			Hashtable expandMessageTable =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
			HashSet expandMessageCategory = (HashSet) expandMessageTable.get(category);
			if (expandMessageCategory!=null)
			{
				expandMessageCategory.remove (messageId);
			}	
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to delete the message. ");
		}	
		catch (InUseException e)
		{
			addAlert(state, INUSE_ERROR_MESSAGE + " discussion channel. ");
		}

		if (state.getAttribute(STATE_MESSAGE) == null)
		{
			state.removeAttribute(STATE_DISPLAY_MESSAGE);
			state.removeAttribute(STATE_MODE);
			state.setAttribute(DELETE_CATEGORY, "");

			String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		}
		
	}	// dodelete_message
	
	/**
	 * cancel the deletion of message
	 */
	public void doCancel_delete_message ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		state.setAttribute(ATTACHMENTS, null);
		state.setAttribute(DELETE_MESSAGE_ID, "");
		state.removeAttribute(STATE_MODE);
	}	// doCancel_delelete_category
	
	/**
	* Handle the eventSubmit_doCancel_show_topic_content command to abort the show topic content page.
	*/
	public void doCancel_show_topic_content(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.removeAttribute(STATE_DISPLAY_MESSAGE);
		state.removeAttribute(STATE_SHOW_CONTENT_TOPIC_ID);
		state.removeAttribute(STATE_MODE);		

	}	// doCancel_show_topic_content
    
    /**
	* Handle the eventSubmit_doCancel_draft to stop showing the draft message
	*/
	public void doCancel_draft(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.removeAttribute(DRAFT_MESSAGE_CATEGORY);
        state.removeAttribute(DRAFT_MESSAGE_SUBJECT);
        state.removeAttribute(DRAFT_MESSAGE_BODY);
        state.removeAttribute(DRAFT_MESSAGE_REPLY_STYLE);
        state.removeAttribute(STATE_DISPLAY_MESSAGE);
        state.setAttribute(ATTACHMENTS, null);
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + MONITOR_PANEL);
		
	}	// doCancel_draft
    
   /**
	* Handle the eventSubmit_doCancel_new_topic to stop showing the new topic page
	*/
	public void doCancel_new_topic(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.removeAttribute(NEW_TOPIC_NEW_CATEGORY);
        state.removeAttribute(NEW_TOPIC_CATEGORY);
        state.removeAttribute(NEW_TOPIC_SUBJECT);
        state.removeAttribute(NEW_TOPIC_BODY);
        state.removeAttribute(NEW_TOPIC_REPLY_STYLE);
        state.removeAttribute(STATE_DISPLAY_MESSAGE);
		
		// clean state mode
		state.removeAttribute(STATE_MODE);
		
        state.setAttribute(ATTACHMENTS, null);
	}	// doCancel_new_topic
    
    /**
	* Handle the eventSubmit_doCancel_new_category to stop showing the new category page
	*/
	public void doCancel_new_category(RunData data)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.removeAttribute(STATE_DISPLAY_MESSAGE);
		
		// clean state mode
		state.removeAttribute(STATE_MODE);
		
	}	// doCancel_new_category
	
	/**
	 * To show the topic content
	 * when "add attachments" is clicked
	 * @param runData The RunData object
	 * @param context The context object
	 **/
	public void doShow_topic_content (RunData data, Context context)
	{		
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		ParameterParser params = data.getParameters ();
		String topicId = params.getString("topicId");
		state.setAttribute(STATE_SHOW_CONTENT_TOPIC_ID, topicId);
		state.setAttribute(STATE_MODE, MODE_SHOW_TOPIC_CONTENT);		
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
	
	}	//doShow_topic_content
	
	/**
	 * Show the discussion message
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doShow ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		ParameterParser params = data.getParameters();
		
		// set current message id
		String messageId = params.getString ("messageId");
		
		showMessage(data, messageId);
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + TOOLBAR_PANEL);
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + CONTROL_PANEL);
	}	//doShow

	/**
	 *
	 */
	private void showMessage(RunData data, String messageId)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		try
		{
			String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			
			try
			{
				DiscussionMessage m = channel.getDiscussionMessage(messageId);
                	DiscussionMessageHeader mHeader = m.getDiscussionHeader();
				if (mHeader.getDraft())
				{
                    state.setAttribute(DRAFT_MESSAGE_CATEGORY, mHeader.getCategory());
                    state.setAttribute(DRAFT_MESSAGE_BODY, m.getBody());
                    state.setAttribute(DRAFT_MESSAGE_SUBJECT, mHeader.getSubject());
                    if (m.getReplyToDepth() == 0)
                    {
                        ResourceProperties mProperties = m.getProperties();
                        state.setAttribute(DRAFT_MESSAGE_REPLY_STYLE, mProperties.getProperty(mProperties.getNamePropReplyStyle()));
                    }
                    state.setAttribute(ATTACHMENTS, mHeader.getAttachments());
				}
				else
				{
					state.setAttribute(ATTACHMENTS, null);
				}
				DisplayMessage dMessage = new DisplayMessage(messageId);
                	state.setAttribute(STATE_DISPLAY_MESSAGE, dMessage);
	
				// update the visited messages list
				HashSet visited = (HashSet) state.getAttribute(VISITED_MESSAGES);
				visited.add(messageId);
				state.setAttribute(VISITED_MESSAGES, visited);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Can not find the selected message. ");
			}
			catch (PermissionException e)
			{
				addAlert(state, "You are not allowed to view the selected message. ");
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}		
	}	// showMessage
	
	/**
	 * Preview the response
	 */
	public void doPreview(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		ParameterParser params = data.getParameters ();
		
		// save the input infos for the respond message
		String replyType = params.getString("replyto");		
		String replyToId = "";
		if (replyType.equalsIgnoreCase ("tomessage"))
		{
			replyToId = params.getString("messageId");
		}
		else if (replyType.equalsIgnoreCase ("totopic"))
		{
			replyToId = params.getString("topicId");
		}
		state.setAttribute(RESPOND_REPLY_TO, replyToId);

		String subject = ((String) params.getString ("subject")).trim();
		state.setAttribute(RESPOND_SUBJECT, StringUtil.trimToZero(subject));

		String body = params.getCleanString ("body");
		body = processFormattedTextFromBrowser(state, body);
		state.setAttribute(RESPOND_BODY, body);
		
		Vector attachments = (Vector) state.getAttribute(ATTACHMENTS);
		state.setAttribute(RESPOND_ATTACHMENT, attachments);
		
		if (subject == null || subject.length() == 0)
		{
			addAlert(state, "Please specify the subject for the message. ");
		}
		else
		{
			state.setAttribute(STATE_MODE, MODE_REPLY_PREVIEW);
		}
				
	}	// doPreview
	
	/**
	 * set the form for replying
	 */
	public void doSet_reply(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		String currentMessageId = ((DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
		state.setAttribute(RESPOND_REPLY_TO, currentMessageId);
		state.setAttribute(RESPOND_ATTACHMENT, new Vector());
		state.setAttribute(STATE_MODE, MODE_REPLY);
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		
	}	// doSet_reply
	
	/**
	 * get the control pannel ready for input new topic
	 */
	public void doSet_new_topic(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.setAttribute(ATTACHMENTS, null);
		state.setAttribute(STATE_MODE, MODE_NEW_TOPIC);
		addAlert(state, "");
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));

	}	// doSet_new_topic

	
	/**
	 * get the control pannel ready for input new category
	 */
	public void doSet_new_category(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
        state.setAttribute(ATTACHMENTS, null);
		state.setAttribute(STATE_MODE, MODE_NEW_CATEGORY);
		addAlert(state, "");
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		
	}	// doSet_new_category
	
	/** 
	* Setup for options. %%% Note: not sure this and doUpdate are correct, not currently invoked (no menu entry) -ggolden
	*/
	public String buildOptionsPanelContext( VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		// set the action for form processing
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));
		context.put("form-submit", BUTTON + "doUpdate");

		// pick the "-customize" template based on the standard template name
		String template = (String)getContext(rundata).get("template");
		return template + "-customize";

	}   // buildOptionsPanelContext

	/**
	* doUpdate called for form input tags type="submit" named="eventSubmit_doUpdate"
	*/
	public void doUpdate(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// channel
		String channel = data.getParameters().getString(FORM_CHANNEL);
		if (!channel.equals((String) state.getAttribute(STATE_CHANNEL_REF)))
		{
			state.setAttribute(STATE_CHANNEL_REF, channel);
			if (Log.getLogger("chef").isDebugEnabled())
				Log.debug("chef", this + ".doUpdate(): channel: " + channel);
			updateObservationOfChannel(state, ((JetspeedRunData)data).getJs_peid());
			state.setAttribute(STATE_UPDATE, STATE_UPDATE);
		}

		// ascending
		String ascending = data.getParameters().getString(FORM_ASCENDING);
		Boolean asc = new Boolean(ascending);
		if (!asc.equals((Boolean) state.getAttribute(STATE_ASCENDING)))
		{
			state.setAttribute(STATE_ASCENDING, asc);
			state.setAttribute(STATE_UPDATE, STATE_UPDATE);
		}

		// we are done with customization
		// %%% clear or change mode to something like list to leave the options mode

	}   // doUpdate
	
	/**
	 * parse the category String
	 */
	private String[] parseCategoryString (String categories)
	{
		if ((categories == null) || (categories.trim().length() == 0)) return null;

		if (categories.indexOf(CATEGORY_SEPERATOR_STRING) == -1)
		{
			String[] rv = new String[1];
			rv[0] = categories;
			return rv;
		}

		return StringUtil.split(categories, CATEGORY_SEPERATOR_STRING);
		
	}	// parseCategoryString
	
	/**
	 * get the categories sorted under the sorting criteria
	 */ 
	private List sortedCategories(DiscussionChannel channel, String sortedBy, String sortedAsc)
	{
		// categories
		List categories = channel.getCategories(false);
		if (categories == null) return null;

		// return the categories alpha sorted
		Collections.sort(categories);

		return categories;

	}   // sortedCategories
	
	/**
	 * transforms the Iterator to Vector
	 */
	private Vector iterator_to_vector(Iterator l)
	{
		Vector v = new Vector();
		while (l.hasNext ())
		{
			v.add (l.next());
		}
		return v;
	}	// iterator_to_vector

	/**
	 * set the category in the expanded category list?
	 */
	private void setCategoryExpanded(SessionState state, String category, boolean expand, DiscussionChannel channel)
	{
		HashSet s =(HashSet) state.getAttribute(STATE_EXPAND_CATEGORY_LIST);

		if (expand)
		{
			s.add(category);

			// the show topics in category			
			Vector topics = iterator_to_vector(channel.getTopics(category));	
			Vector drafts = new Vector ();
			Vector nonDrafts = new Vector ();
			Vector showTopicsList = new Vector ();

			// devide the drafted messages and posted messages; drafted message come first
			for (int j=0; j< topics.size (); j++)
			{
				try
				{
					DiscussionMessage m = (DiscussionMessage)topics.get(j);
					
					// filter out drafts not by this user (unless this user is a super user) %%% not sure I like this -zqian
					channel.getMessage(m.getId());
					if ( m.getDiscussionHeader ().getDraft ())
					{
						drafts.add (m.getId());
					}
					else
					{
						nonDrafts.add (m.getId());
					}
				}
				catch (PermissionException e){}
				catch (IdUnusedException e){}
			}
			showTopicsList.addAll (drafts);
			showTopicsList.addAll(nonDrafts);
			setCategoryShowList(state, category, showTopicsList);
		}
		else
		{
			s.remove (category);
			setCategoryShowList(state, category, new Vector());

			// hide the show message	
			Hashtable t =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
			t.put (category, new HashSet());
		}	
	}	// setCategoryExpanded
	
	/** 
	* Toggle the state attribute
	* @param runData
	* @param stateName The name of the state attribute to toggle
	**/	
	private void toggleState(RunData runData, String stateName)
	{
		// access the portlet element id to find our state
		// %%% use CHEF api instead of Jetspeed to get state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// toggle the state setting
		boolean newValue = !((Boolean) state.getAttribute(stateName)).booleanValue();
		state.setAttribute(stateName, new Boolean(newValue));

	}	// toggleState
	
	/**
	 * Show the whole message hireachy
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doExpand_all(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));

			// the sorting defaults for the outline view
			String sortedBy = "";
			String sortedAsc = (String) state.getAttribute(STATE_SORTED_ASC);

			// set default sort attribute 
			sortedBy = (String) state.getAttribute(STATE_SORTED_BY);
			if ((!sortedBy.equals(STATE_SORTED_BY_TOPIC)) && (!sortedBy.equals(STATE_SORTED_BY_AUTHOR)) && (!sortedBy.equals(STATE_SORTED_BY_DATE)))
			{
				sortedBy = STATE_SORTED_BY_DATE;
				state.setAttribute(STATE_SORTED_BY, sortedBy);
			}

			// get the sorted categories Vector according to the current sorting criteria
			List categories = sortedCategories(channel, sortedBy, sortedAsc);
			if (categories != null)
			{
				for (Iterator i=categories.iterator(); i.hasNext();)
				{
					String cat = (String) i.next();

					setCategoryExpanded(state, cat, true, channel);
					Iterator topics = channel.getTopics(cat);
					while (topics.hasNext ())
					{
						// expand all the message in the thread
						DiscussionMessage m = (DiscussionMessage) topics.next();
						setMessageExpanded(state, m, true, channel);
						Iterator replyThread = channel.getThread(m);
						while (replyThread.hasNext ())
						{
							setMessageExpanded(state, (DiscussionMessage) replyThread.next (), true, channel);
						}
					}
				}
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}

		toggleState(data, STATE_EXPAND_ALL_FLAG);
		
		// schedule a refresh of the "List" panel
		String peid = ((JetspeedRunData)data).getJs_peid();
		String address = ((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).getDeliveryId();
		String elementID = ((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).getElementId();
		CourierService.deliver(new RefreshDelivery(address, elementID));
		
	}	// doExpand_all
		
	/**
	 * Hide the whole message hireachy
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doCollapse_all(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));

			// the sorting defaults for the outline view
			String sortedBy = "";
			String sortedAsc = (String) state.getAttribute(STATE_SORTED_ASC);

			// set default sort attribute 
			sortedBy = (String) state.getAttribute(STATE_SORTED_BY);
			if ((!sortedBy.equals(STATE_SORTED_BY_TOPIC)) && (!sortedBy.equals(STATE_SORTED_BY_AUTHOR)) && (!sortedBy.equals(STATE_SORTED_BY_DATE)))
			{
				sortedBy = STATE_SORTED_BY_DATE;
				state.setAttribute(STATE_SORTED_BY, sortedBy);
			}
			// get the sorted categories Vector according to the current sorting criteria
			List categories = sortedCategories(channel, sortedBy, sortedAsc);
			if (categories != null)
			{
				for (Iterator i=categories.iterator(); i.hasNext();)
				{
					setCategoryExpanded(state, (String) i.next(), false, channel);
				}
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}
		
		// remove current display message due to close of categories
		state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(""));
		
		toggleState(data, STATE_EXPAND_ALL_FLAG);
		
		// schedule a refresh of the "List" panel
		String peid = ((JetspeedRunData)data).getJs_peid();
		String address = ((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).getDeliveryId();
		String elementID = ((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).getElementId();
		CourierService.deliver(new RefreshDelivery(address, elementID));
		
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + CONTROL_PANEL);
		
	}	// doCollapse_all
		
	/**
	 * is the message id in the expanded message list?
	 */
	private boolean isMessageExpanded(SessionState state, DiscussionMessage message)
	{
		String category = message.getDiscussionHeader().getCategory();
		Hashtable expandMessageTable =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
		HashSet s = (HashSet) expandMessageTable.get(category);
		if (s!=null)
			return s.contains (message);
		else
			return false;

	}	// isMessageExpanded
	
	/**
	 * set the message id in the expanded message list?
	 */
	private void setMessageExpanded(SessionState state, DiscussionMessage message, boolean expand, DiscussionChannel channel)
	{	
		// get the current category message list
		String category = message.getDiscussionHeader().getCategory();
		Vector v = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);

		// get the show message
		Hashtable expandMessageTable =(Hashtable) state.getAttribute(STATE_EXPAND_MESSAGE_LIST);
		HashSet expandMessageCategory = (HashSet) expandMessageTable.get(category);
		if (expandMessageCategory == null)
		{
			expandMessageCategory = new HashSet();
		}

		// get the index of the original message
		int index = v.indexOf (message.getId());

		if (expand)
		{
			expandMessageCategory.add (message);

			// divide the drafted messages and posted messages, drafted message come first
			Vector drafts = new Vector ();
			Vector nonDrafts = new Vector ();
			Vector showMessagesList = new Vector ();
			Vector replies = iterator_to_vector(message.getReplies());
			for (int j=0; j< replies.size (); j++)
			{
				DiscussionMessage m = (DiscussionMessage)replies.get(j);
				try
				{
					// filter out drafts not by this user (unless this user is a super user) %%% not sure I like this -zqian
					channel.getDiscussionMessage(m.getId());
					if ( m.getDiscussionHeader ().getDraft ())
					{
						drafts.add (m.getId());
					}
					else
					{
						nonDrafts.add (m.getId());
					}
				}
				catch (IdUnusedException e){}
				catch (PermissionException e){}
			}
			showMessagesList.addAll (drafts);
			showMessagesList.addAll (nonDrafts);

			v.addAll (index+1, showMessagesList);
		}
		else
		{
			expandMessageCategory.remove (message);
			// remove the whole thread
			Vector replies = iterator_to_vector(channel.getThread(message));
			for (int j=0; j< replies.size (); j++)
			{
				DiscussionMessage m = (DiscussionMessage)replies.get(j);
				if (v.contains (m.getId()))
				{
					v.remove (m.getId());
				}
			}	
		}	

		setCategoryShowList(state, category, v);
		expandMessageTable.put (category, expandMessageCategory);
	}	// setMessageExpanded
	
	/**
	 * set the show list for each category
	 */
	private void setCategoryShowList (SessionState state, String category, Vector showList)
	{
		Hashtable t = (Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST);
		t.put (category, showList);
		state.setAttribute(STATE_CATEGORIES_SHOW_LIST, t);
	}	// setCategoryShowList
	
	/**
	 * doExpandmessage expand the message with the content and replies
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doExpand_message ( RunData data, Context context )
	{
		String messageId = data.getParameters().getString("messageId");
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		try
		{		
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));

			try
			{
				setMessageExpanded(state, channel.getDiscussionMessage(messageId), true, channel);
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Can not find the message. ");
			}
			catch (PermissionException e)
			{
				addAlert(state, PERMISSION_HEADER_STRING + "to view the topic. ");
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You are not allowed to view the discussion channel. ");
		}

	}	// doExpand_message
	
	/**
	 * doCollapsemessage hides all the messages inside the category
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doCollapse_message ( RunData data, Context context )
	{
		String messageId = data.getParameters().getString("messageId");
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		try
		{
			DiscussionChannel channel = DiscussionService.getDiscussionChannel((String) state.getAttribute(STATE_CHANNEL_REF));
			try
			{
				DiscussionMessage message = channel.getDiscussionMessage(messageId);
				
				// set the message itself to be collapsed
				setMessageExpanded(state, message, false, channel);
				
				// the current message been displayed
				String currentMessageId = ((DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
				boolean found = false;
				
				Iterator replies = channel.getThread(message);
				while (replies.hasNext ())
				{
					DiscussionMessage nextReply = (DiscussionMessage) replies.next (); 
					
					// set the message's reply to be collapsed
					setMessageExpanded(state, nextReply, false, channel);
					
					if ((!found) && currentMessageId.equals(nextReply.getId()))
					{
						// remove the current message due to close of thread
						state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(""));
						found = true;
					}
					
				}
			}
			catch (IdUnusedException e)
			{
				addAlert(state, "Can not find the message. ");
			}
			catch (PermissionException e)
			{
				addAlert(state, PERMISSION_HEADER_STRING + "to view the topic. ");
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}

		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + CONTROL_PANEL);
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + TOOLBAR_PANEL);
	}	// doCollapse_message
	
	/**
	 * doExpandcategory shows all the topics inside the category
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template8
	 */
	public void doExpand_category ( RunData data, Context context )
	{
		String category = data.getParameters().getString("category");
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			setCategoryExpanded(state, category, true, channel);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}

	}	// doExpand_category

	/**
	 * doCollapsecategory hides all the topics inside the category
	 * @param runData The RunData object for this request
	 * @param context The Context used by the template
	 */
	public void doCollapse_category ( RunData data, Context context )
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String category = data.getParameters().getString("category");
		
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);

			String currentId = ((DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE)).getId();
			if (!currentId.equals(""))
			{
				DiscussionMessage message = channel.getDiscussionMessage(currentId);
				String mCategory = message.getDiscussionHeader().getCategory();
				if (mCategory.equals(category))
				{
					// remove current display message due to close of category
					state.setAttribute(STATE_DISPLAY_MESSAGE, new DisplayMessage(""));
				}
			}
			setCategoryExpanded(state, category, false, channel);
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}
				
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + CONTROL_PANEL);
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + TOOLBAR_PANEL);

	}	// doCollapse_category
	
	private class DiscussionComparator
	implements Comparator
	{
		/**
		 * the criteria
		 */
		String m_criteria = null;

		/**
		 * the criteria
		 */
		String m_asc = null;

		/**
		 * constructor
		 * @param criteria The sort criteria string
		 * @param asc The sort order string. "true" if ascending; "false" otherwise.
		 */
		public DiscussionComparator (String criteria, String asc)
		{
			m_criteria = criteria;
			m_asc = asc;

		}	// constructor

		/**
		 * 	implementing the compare function
		 * @param o1 The first object
		 * @param o2 The second object
		 * @return The compare result. 1 is o1 < o2; -1 otherwise
		 */
		public int compare ( Object o1, Object o2)
		{
			int result = -1;

			if ((m_criteria.equals (STATE_SORTED_BY_TOPIC))||(m_criteria.equals (STATE_SORTED_BY_SUBJECT)))
			{
				// sorted by the discussion message subject
				result =  ((DiscussionMessage) o1).getDiscussionHeader ().getSubject ().
				compareToIgnoreCase (((DiscussionMessage)o2).getDiscussionHeader ().getSubject ());
			}
			else if (m_criteria.equals (STATE_SORTED_BY_DATE))
			{
				// sorted by the discussion message date
				if (((DiscussionMessage) o1).getDiscussionHeader ().getDate ().
				before (((DiscussionMessage)o2).getDiscussionHeader ().getDate ()))
				{
					result = -1;
				}
				else
				{
					result = 1;
				}
			}
			else if (m_criteria.equals (STATE_SORTED_BY_LASTPOSTEDDATE))
			{
				// sorted by the discussion message date
				DiscussionMessage m1 = null;
				DiscussionMessage m2 = null;

				if (((DiscussionMessage) o1).getLatestReply ()!= null)
				{
					m1 = ((DiscussionMessage) o1).getLatestReply ();
				}
				else
				{
					m1 = (DiscussionMessage) o1;
				}

				if (((DiscussionMessage) o2).getLatestReply ()!= null)
				{
					m2 = ((DiscussionMessage) o2).getLatestReply ();
				}
				else
				{
					m2 = (DiscussionMessage) o2;
				}

				if (((DiscussionMessage) m1).getDiscussionHeader ().getDate ().
				before (((DiscussionMessage)m2).getDiscussionHeader ().getDate ()))
				{
					result = -1;
				}
				else
				{
					result = 1;
				}
			}
			else if (m_criteria.equals (STATE_SORTED_BY_LASTPOSTEDBY))
			{
				// sorted by the discussion message date
				DiscussionMessage m1 = null;
				DiscussionMessage m2 = null;

				if (((DiscussionMessage) o1).getLatestReply ()!= null)
				{
					m1 = ((DiscussionMessage) o1).getLatestReply ();
				}
				else
				{
					m1 = (DiscussionMessage) o1;
				}

				if (((DiscussionMessage) o2).getLatestReply ()!= null)
				{
					m2 = ((DiscussionMessage) o2).getLatestReply ();
				}
				else
				{
					m2 = (DiscussionMessage) o2;
				}
				result =  ((DiscussionMessage) m1).getDiscussionHeader ().getFrom ().getSortName ().
				compareToIgnoreCase (((DiscussionMessage) m2).getDiscussionHeader ().getFrom ().getSortName ());
			}
			else if (m_criteria.equals (STATE_SORTED_BY_AUTHOR))
			{
				// sorted by the discussion message subject
				result =  ((DiscussionMessage) o1).getDiscussionHeader ().getFrom ().getSortName ().
				compareToIgnoreCase (((DiscussionMessage)o2).getDiscussionHeader ().getFrom ().getSortName ());
			}
			// category alpha a-z or z-a sorting
			else if (m_criteria.equals (STATE_SORTED_BY_CATEGORY_ALPHA))
			{
				// alpha sort by category name
				return result =  ((String) o1).compareToIgnoreCase ((String) o2);
			}
            // category last posted by sorting
			else if (m_criteria.equals (STATE_SORTED_BY_CATEGORY_LASTPOSTEDBY))
			{
                DiscussionMessage m1 =((CategoryLastReply) o1).getLastReply();
                DiscussionMessage m2 =((CategoryLastReply) o2).getLastReply();
                
                if ( m1 == null )
                {
                    result = -1;
                }
                else if ( m2 == null )
                {
                    result = 1;
                }
                else
                {
                    result = m1.getDiscussionHeader().getFrom().getSortName().compareToIgnoreCase(m2.getDiscussionHeader().getFrom().getSortName());
                }
			}
     		// category last posted date sorting
			else if (m_criteria.equals (STATE_SORTED_BY_CATEGORY_LASTPOSTEDDATE))
			{
                DiscussionMessage m1 =((CategoryLastReply) o1).getLastReply();
                DiscussionMessage m2 =((CategoryLastReply) o2).getLastReply();
                if ( m1==null )
                {
                    result = -1;
                }
                else if ( m2 == null)
                {
                    result = 1;
                }
                else
                {
                    if (m1.getDiscussionHeader().getDate()
                    .before(m2.getDiscussionHeader().getDate()))
                    {
                        result = -1;
                    }
                    else
                    {
                        result = 1;
                    }
                }
			}

			// sort ascending or descending
			if (m_asc.equals (Boolean.FALSE.toString()))
			{
				result = -result;
			}
			return result;
		}	// compare
	}	// DiscussionComparator
	
    /**
     * The object contains the category and its last replied 
     */
    private class CategoryLastReply
    {
        // the category attribute
        String m_category;
        
        // the DiscussionMessage attribute
        DiscussionMessage m_lastReply;
        
        /**
         *  constructor
         */
        public CategoryLastReply(String category, DiscussionMessage lastReply)
        {
            m_category = category;
            m_lastReply = lastReply;
           
        }   // constructor
        
        /**
         * get the category
         */
        public String getCategory()
        {
            return m_category;
            
        }   // getCategory
        
        /**
         * set the category
         */
        public void setCategory(String category)
        {
            m_category = category;
            
        }   // setCategor
        
        /**
         * get the last DiscussionMessage object inside the category
         */
        public DiscussionMessage getLastReply()
        {
            return m_lastReply;
            
        }   // getLastReply
        
        /**
         * set the last DiscussionMessage object inside the category
         */ 
        public void setLastReply(DiscussionMessage lastReply)
        {
            m_lastReply = lastReply;
            
        }   // setLastReply 
        
    }   // class CategoryLastReply
	
	/**
	 *	the class which is using by one of the iFrames to show the content of selected message
	 */
    public class DisplayMessage
    {
        String m_id = null;
        
        public DisplayMessage(String id)
        {
            m_id = id;
        }
        
        /**
         *
         */
        public void setId(String id)
        {
            m_id = id;
        }
        
        /**
         *
         */
        public String getId()
        {
            return m_id;
        }

    }	// DisplayMessage

    /**
     * add the category to the state attribute
     */
    private void addCategory (SessionState state, DiscussionChannel channel, String category)
    {
		try
		{
			// update the discussion channel object
			DiscussionChannelEdit edit = (DiscussionChannelEdit) DiscussionService.editChannel(channel.getReference());
			edit.addCategory(category);
			DiscussionService.commitChannel(edit);
			HashSet expandedCategories = (HashSet) state.getAttribute(STATE_EXPAND_CATEGORY_LIST);
			expandedCategories.add(category);
		}
		catch (PermissionException ignore) {}
		catch (IdUnusedException ignore) {}
		catch (InUseException ignore) {}

	}	// addCategory

	/**
	* Fire up the permissions editor
	*/
	public void doPermissions(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String channelRefStr = (String) state.getAttribute(STATE_CHANNEL_REF);
		Reference channelRef = new Reference(channelRefStr);
		String siteRef = SiteService.siteReference(channelRef.getContext());

		// setup for editing the permissions of the site for this tool, using the roles of this site, too
		state.setAttribute(PermissionsAction.STATE_REALM_ID, siteRef);
		state.setAttribute(PermissionsAction.STATE_REALM_ROLES_ID, siteRef);

		// ... with this description
		state.setAttribute(PermissionsAction.STATE_DESCRIPTION, "Set permissions for Discussion in worksite "
				+ SiteService.getSiteDisplay(channelRef.getContext()));

		// ... showing only locks that are prpefixed with this
		state.setAttribute(PermissionsAction.STATE_PREFIX, "disc.");

		// start the helper
		state.setAttribute(PermissionsAction.STATE_MODE, PermissionsAction.MODE_MAIN);

		// schedule a main refresh
		String toolId = PortalService.getCurrentToolId();
		String address = clientWindowId(state, toolId);
		String mainPanelId = mainPanelUpdateId(toolId);
		CourierService.deliver(address, mainPanelId);

	}	// doPermissions

		/** 
	* Handle a \ request.
	**/	
	public void doSearch(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// read the search form field into the state object
		String search = StringUtil.trimToNull(runData.getParameters().getString(FORM_SEARCH));

		// set the flag to go to the prev page on the next list
		if (search == null)
		{
			state.removeAttribute(STATE_SEARCH);
		}
		else
		{
			state.setAttribute(STATE_SEARCH, search);
		}
		
		state.setAttribute(STATE_SEARCH_REFRESH, Boolean.TRUE);
		
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		
		//disable auto-updates while in view mode
		((EventObservingCourier) state.getAttribute(STATE_OBSERVER)).disable();
		
	}	// doSearch

	/** 
	* Handle a Search Clear request.
	**/	
	public void doSearch_clear(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// clear the search
		state.removeAttribute(STATE_SEARCH);

		// turn on auto refresh
		enableObserver(state);
		
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid)+ "." + MONITOR_PANEL);

		//make sure auto-updates are enabled
		enableObserver(state);
	
	}	// doSearch_clear
	
	
	/**
	* Enable the observer, unless we are in search mode, where we want it disabled.
	*/
	public void enableObserver(SessionState state)
	{
		// get the observer
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			// we leave it disabled if we are searching, or if the user has last selected to be manual
			if (state.getAttribute(STATE_SEARCH) != null)
			{
				observer.disable();
			}
			else
			{
				observer.enable();
			}
		}

	}	// enableObserver

		/**
	 *
	 */
	public void doView (RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		
		String viewMode = data.getParameters ().getString("view");
		state.setAttribute(STATE_SELECTED_VIEW, viewMode);
		
		if (viewMode.equalsIgnoreCase("2 Row Layout"))
		{
			// not left - right layout
			state.setAttribute(STATE_LEFT_RIGHT_LAYOUT, new Boolean(false));
			
			String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));

		}
		else if (viewMode.equalsIgnoreCase("2 Column Layout"))
		{
			// not left - right layout
			state.setAttribute(STATE_LEFT_RIGHT_LAYOUT, new Boolean(true));
			
			String peid = ((JetspeedRunData) data).getJs_peid();
			CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));

		}
	}	// doView
	
	/**
	 * put those variables related to 2ndToolbar into context 
	 */
	private void add2ndToolbarFields(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());

		context.put("searchString", state.getAttribute(STATE_SEARCH));
		context.put("form_search", FORM_SEARCH);
		context.put("selectedView", state.getAttribute(STATE_SELECTED_VIEW));

	}	//add2ndToolbarFields
	
	/**
	 * navigate to previous message in the category
	 */
	public void doPre_message(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			
			DisplayMessage dMessage = (DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE);
			String currentId = dMessage.getId();
			if (!currentId.equals(""))
			{
				DiscussionMessage message = channel.getDiscussionMessage(currentId);
				String category = message.getDiscussionHeader().getCategory();
				Vector messageIds = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);
				dMessage.setId((String) messageIds.get(messageIds.indexOf(currentId) - 1));
				state.setAttribute(STATE_DISPLAY_MESSAGE, dMessage);
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));

	}	//	doPre_message
	
	/**
	 * Navigate to next message in the category
	 */
	public void doNext_message(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
		String channelId = (String) state.getAttribute(STATE_CHANNEL_REF);
		try
		{
			// get the current channel ID from state object
			DiscussionChannel channel = DiscussionService.getDiscussionChannel(channelId);
			
			DisplayMessage dMessage = (DisplayMessage) state.getAttribute(STATE_DISPLAY_MESSAGE);
			String currentId = dMessage.getId();
			if (!currentId.equals(""))
			{
				DiscussionMessage message = channel.getDiscussionMessage(currentId);
				String category = message.getDiscussionHeader().getCategory();
				Vector messageIds = (Vector) ((Hashtable) state.getAttribute(STATE_CATEGORIES_SHOW_LIST)).get(category);
				dMessage.setId((String) messageIds.get(messageIds.indexOf(currentId) + 1));
				state.setAttribute(STATE_DISPLAY_MESSAGE, dMessage);
			}
		}
		catch (IdUnusedException e)
		{
			addAlert(state, "Can not find the discussion channel. ");
		}
		catch (PermissionException e)
		{
			addAlert(state, "You do not have the permission to this discussion channel. ");
		}
		
		String peid = ((JetspeedRunData) data).getJs_peid();
		CourierService.deliver(clientWindowId(state, peid), VelocityPortletPaneledAction.mainPanelUpdateId(peid));
		
	}	//	doNext_message
	
	/**
	 * Processes formatted text that is coming back from the browser 
	 * (from the formatted text editing widget).
	 * @param state Used to pass in any user-visible alerts or errors when processing the text
	 * @param strFromBrowser The string from the browser
	 * @return The formatted text
	 */
	private String processFormattedTextFromBrowser(SessionState state, String strFromBrowser)
	{
		StringBuffer alertMsg = new StringBuffer();
		try
		{
			String text = FormattedText.processFormattedText(strFromBrowser, alertMsg);
			if (alertMsg.length() > 0) addAlert(state, alertMsg.toString());
			return text;
		}
		catch (Exception e)
		{
			Log.warn("chef", this + ": ", e);
			return strFromBrowser;
		}
	}
	
}	// ThreadedDiscussionIIAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/discussion/ThreadedDiscussionIIAction.java,v 1.27 2004/09/30 20:21:44 ggolden.umich.edu Exp $
*
**********************************************************************************/
