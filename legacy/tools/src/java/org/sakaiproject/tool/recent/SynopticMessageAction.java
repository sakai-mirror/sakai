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
package org.sakaiproject.tool.recent;

// imports
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PortletConfig;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.java.StringUtil;

/**
* <p>SynopticMessageAction is a the CHEF synopsis tool for messages (chat, announcement, discussion).</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class SynopticMessageAction
	extends VelocityPortletPaneledAction
{
	
	private static ResourceBundle rb = ResourceBundle.getBundle("recent");
	
	/** portlet configuration parameter names. */
	private static final String PARAM_CHANNEL = "channel";
	private static final String PARAM_DAYS = "days";
	private static final String PARAM_ITEMS = "items";
	private static final String PARAM_LENGTH = "length";
	private static final String PARAM_SHOW_BODY = "show-body";
	private static final String PARAM_SHOW_NEWLINES = "show-newlines";
	private static final String PARAM_SERVICE = "message-service";
	private static final String PARAM_SHOW_SUBJECT = "show-subject";

	/** Configure form field names. */
	private static final String FORM_CHANNEL = "channel";
	private static final String FORM_DAYS = "days";
	private static final String FORM_ITEMS = "items";
	private static final String FORM_LENGTH = "length";
	private static final String FORM_SHOW_BODY = "show-body";
	private static final String FORM_SHOW_SUBJECT = "show-subject";

	/** Control form field names. */
	private static final String FORM_MESSAGE = "message";

	/** state attribute names. */
	private static final String STATE_CHANNEL_REF = "channelId";
	private static final String STATE_ERROR = "error";
	private static final String STATE_SERVICE = "service";
	private static final String STATE_SERVICE_NAME = "service-name";
	private static final String STATE_UPDATE = "update";
	private static final String STATE_CHANNEL_PROBLEM = "channel-problem";
	private static final String STATE_DAYS = "days";
	private static final String STATE_AFTER_DATE = "afterdate";
	private static final String STATE_ITEMS = "items";
	private static final String STATE_LENGTH = "length";
	private static final String STATE_SHOW_BODY = "show-body";
	private static final String STATE_SHOW_SUBJECT = "show-subject";
	private static final String STATE_SHOW_NEWLINES = "show-newlines";
	private static final String STATE_SUBJECT_OPTION = "allow-option-of-showing-subject";

	/** channel problem messages */
	private static final String CHANNEL_PERMISSION = rb.getString("youdonot");

	/**
	* Populate the state object, if needed - override to do something!
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		if (state.getAttribute(STATE_CHANNEL_REF) == null)
		{
			PortletConfig config = portlet.getPortletConfig();
			
			MessageService service = (MessageService) state.getAttribute(STATE_SERVICE);
			if (service == null)
			{
				// which message service?
				String serviceName = config.getInitParameter(PARAM_SERVICE);
				
				// deal with old CHEF 1.2.10 settings
				if (serviceName.startsWith("org.chefproject"))
				{
					// get the servlet setting, ignoring the others
					serviceName = config.get3InitParameter(PARAM_SERVICE)[0];
				}

				state.setAttribute(STATE_SERVICE_NAME, serviceName);

				service = getMessageService(serviceName);
				state.setAttribute(STATE_SERVICE, service);
			}

			// read the channel from configuration, or, if not specified, use the default for the page
			String channel = StringUtil.trimToNull(config.getInitParameter(PARAM_CHANNEL));
			if (channel == null)
			{
				channel = service.channelReference(ToolManager.getCurrentPlacement().getContext(), SiteService.MAIN_CONTAINER);
			}
			state.setAttribute(STATE_CHANNEL_REF, channel);
			
			// read the days parameter
			if (state.getAttribute(STATE_DAYS) == null)
			{
				int days = 10;
				try
				{
					days = Integer.parseInt(config.getInitParameter(PARAM_DAYS));
				}
				catch (Exception e) {}

				state.setAttribute(STATE_DAYS, new Integer(days));

				long startTime = System.currentTimeMillis() - ((long)days * 24l * 60l * 60l * 1000l);
				state.setAttribute(STATE_AFTER_DATE, TimeService.newTime(startTime));
			}

			// read the items parameter
			if (state.getAttribute(STATE_ITEMS) == null)
			{
				try
				{
					state.setAttribute(STATE_ITEMS, new Integer(config.getInitParameter(PARAM_ITEMS)));
				}
				catch (Exception e)
				{
					// use a default value
					state.setAttribute(STATE_ITEMS, new Integer(3));
				}
			}

			// read the length parameter
			if (state.getAttribute(STATE_LENGTH) == null)
			{
				try
				{
					state.setAttribute(STATE_LENGTH, new Integer(config.getInitParameter(PARAM_LENGTH)));
				}
				catch (Exception e)
				{
					// use a default value
					state.setAttribute(STATE_LENGTH, new Integer(50));
				}
			}
			
			// read the show-subject parameter
			if (state.getAttribute(STATE_SHOW_SUBJECT) == null)
			{
				try
				{
					state.setAttribute(STATE_SHOW_SUBJECT,
						new Boolean(config.getInitParameter(PARAM_SHOW_SUBJECT)));
				}
				catch (Exception e)
				{
					// use a default value
					state.setAttribute(STATE_SHOW_SUBJECT, new Boolean(false));
				}
			}
			
			// read the show-body parameter
			if (state.getAttribute(STATE_SHOW_BODY) == null)
			{
				try
				{
					state.setAttribute(STATE_SHOW_BODY,
						new Boolean(config.getInitParameter(PARAM_SHOW_BODY)));
				}
				catch (Exception e)
				{
					// use a default value
					state.setAttribute(STATE_SHOW_BODY, new Boolean(false));
				}
			}
			
			// read the show-newlines parameter
			if (state.getAttribute(STATE_SHOW_NEWLINES) == null)
			{
				try
				{
					state.setAttribute(STATE_SHOW_NEWLINES,
						new Boolean(config.getInitParameter(PARAM_SHOW_NEWLINES)));
				}
				catch (Exception e)
				{
					// use a default value
					state.setAttribute(STATE_SHOW_NEWLINES, new Boolean(false));
				}
			}

//			// setup the observer to notify our main panel
//			if (state.getAttribute(STATE_OBSERVER) == null)
//			{
//				// the delivery location for this tool
//				String deliveryId = clientWindowId(state, portlet.getID());
//				
//				// the html element to update on delivery
//				String elementId = mainPanelUpdateId(portlet.getID());
//
//				// the event resource reference pattern to watch for
//				Reference r = new Reference(channel);
//				String pattern = service.messageReference(r.getContext(), r.getId(), "");
//
//				state.setAttribute(STATE_OBSERVER, new EventObservingCourier(deliveryId, elementId, pattern));
//			}
		}

	}   // initState

	/** 
	* build the context for the Main panel
	* @return (optional) template name for this panel
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		
		context.put("tlang",rb);
		
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());

//		// TODO: TIMING
//		if (CurrentService.getInThread("DEBUG") == null)
//			CurrentService.setInThread("DEBUG", new StringBuffer());
//		long startTime = System.currentTimeMillis();

		// different title of Option link for different tools. 
		Tool tool = ToolManager.getCurrentTool();
		context.put("toolId", tool.getId());
		
		// handle options mode
		if (MODE_OPTIONS.equals(state.getAttribute(STATE_MODE)))
		{
			return buildOptionsPanelContext(portlet, context, rundata, state);
		}

		// build the menu
		Menu bar = new Menu(portlet, rundata, (String) state.getAttribute(STATE_ACTION));
		
		// add options if allowed
		addOptionsMenu(bar, (JetspeedRunData) rundata);
		if (!bar.getItems().isEmpty())
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

		// set the message length (leave as an Integer)
		context.put("length", state.getAttribute(STATE_LENGTH));

		// set useSubject - true to display the message subject (else use the body)
		context.put("showSubject", state.getAttribute(STATE_SHOW_SUBJECT));
		
		// set showBody - true to display the message body
		// message subject is always displayed for recent discussion tool - handled by vm
		context.put("showBody", state.getAttribute(STATE_SHOW_BODY));

		// whether to show newlines in the message body, or not
		context.put("show_newlines", ((Boolean) state.getAttribute(STATE_SHOW_NEWLINES)).toString());

		try
		{
			MessageService service = (MessageService) state.getAttribute(STATE_SERVICE);
			String channelRef = (String) state.getAttribute(STATE_CHANNEL_REF);
			Time afterDate = (Time) state.getAttribute(STATE_AFTER_DATE);
			int items = ((Integer) state.getAttribute(STATE_ITEMS)).intValue();
			
			List messages = new Vector();
			String serviceName = (String) state.getAttribute(STATE_SERVICE_NAME);
			if (serviceName != null && serviceName.equals(DiscussionService.SERVICE_NAME))
			{
				// showing the draft messages of discussion
				messages = service.getMessages(channelRef, afterDate, items, false, true, false);
			}
			else
			{
				messages = service.getMessages(channelRef, afterDate, items, false, false, false);
			}
			context.put("messages", messages);
		}
		catch (PermissionException e)
		{
			addAlert(state, CHANNEL_PERMISSION);
		}

		// inform the observing courier that we just updated the page...
		// if there are pending requests to do so they can be cleared
		justDelivered(state);

		String rv = (String)getContext(rundata).get("template") + "-List";
		
//		// TODO: TIMING
//		long endTime = System.currentTimeMillis();
//		if (endTime-startTime > /*5*/000)
//		{
//			StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
//			if (buf != null)
//			{
//				buf.insert(0,"synopticMessageAction: "
//						+ state.getAttribute(STATE_CHANNEL_REF)
//						+ " time: " + (endTime - startTime));
//			}
//		}

		return rv;

	}   // buildMainPanelContext

	/** 
	* Setup for the options panel.
	*/
	public String buildOptionsPanelContext( VelocityPortlet portlet, 
											Context context,
											RunData rundata,
											SessionState state)
	{
		context.put("tlang",rb);
		String serviceName = (String) state.getAttribute(STATE_SERVICE_NAME);
		String tool_title = rb.getString("tool_title");
		String tool_name = rb.getString("tool_name");
		String one_item = rb.getString("one_item");
		String all_items = rb.getString("channel_analog");
		String channel_analog = rb.getString("channel_analog");
		Boolean allow_show_subject = new Boolean(true);
		Boolean allow_channel_choice = new Boolean(false);

		if (serviceName.equals(ChatService.SERVICE_NAME))
		{
			tool_title = rb.getString("ctool_title");
			tool_name = rb.getString("ctool_name");
			one_item = rb.getString("cone_item");
			all_items = rb.getString("call_items");
			channel_analog = rb.getString("cchannel_analog");
			allow_show_subject = new Boolean(false);
			allow_channel_choice = new Boolean(true);
		}
		else if (serviceName.equals(DiscussionService.SERVICE_NAME))
		{
			tool_title = rb.getString("dtool_title");
			tool_name = rb.getString("dtool_name");
			one_item = rb.getString("done_item");
			all_items = rb.getString("dall_items");
		}
		else if (serviceName.equals(AnnouncementService.SERVICE_NAME))
		{
			tool_title = rb.getString("atool_title");
			tool_name = rb.getString("atool_name");
			one_item = rb.getString("aone_item");
			all_items = rb.getString("aall_items");
		}
		
		// provide "tool_title" as title for options page
		context.put("tool_title", tool_title);
		
		// provide "tool_name" as name of the tool
		context.put("tool_name", tool_name);
		
		// provide "one_item" as a reference to a single message
		context.put("one_item", one_item);
		
		// provide "all_items" as a reference to all messages collectively
		context.put("all_items", all_items);
		
		// provide "allow_show_subject" with the value for whether to allow user to choose between subject or body
		context.put("allow_show_subject", allow_show_subject.toString());
		if(allow_show_subject.booleanValue())
		{
				context.put("showBody", ((Boolean) state.getAttribute(STATE_SHOW_BODY)).toString());
			
				// provide "showSubject" with the value for showing subject (true) or body (false) 
				context.put("showSubject", ((Boolean) state.getAttribute(STATE_SHOW_SUBJECT)).toString());
		}
				
		// provide "allow_channel_choice" with the value for whether to allow user to choose the channel
		context.put("allow_channel_choice", allow_channel_choice.toString());
		if(allow_channel_choice.booleanValue())
		{
			// provide "channel_analog" with the word(s) used to refer to a channel, such as "Chat Room"
			context.put("channel_analog", channel_analog);
	
			// provide "default_channel" with the dafault channel-id for the user/group
			context.put("default_channel", SiteService.MAIN_CONTAINER);
		
			// provide "channel" with the current channel's id
			String placementContext = ToolManager.getCurrentPlacement().getContext();
			String defaultChannel = ((MessageService) state.getAttribute(STATE_SERVICE)).channelReference(placementContext, SiteService.MAIN_CONTAINER);
			String sitePrefix = defaultChannel.substring(0, defaultChannel.lastIndexOf(SiteService.MAIN_CONTAINER));
			String currentChannel = ((String) state.getAttribute(STATE_CHANNEL_REF)).substring(sitePrefix.length());
			context.put("channel", currentChannel);
	
			// provide "channels" as  a list of channels belonging to this site

//			// TODO: TIMING
//			if (CurrentService.getInThread("DEBUG") == null)
//				CurrentService.setInThread("DEBUG", new StringBuffer());
//			long startTime = System.currentTimeMillis();

			Iterator aChannel = ((MessageService) state.getAttribute(STATE_SERVICE)).getChannelIds(placementContext).iterator();

//			// TODO: TIMING
//			long endTime = System.currentTimeMillis();
//			if (endTime-startTime > /*5*/000)
//			{
//				StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
//				if (buf != null)
//				{
//					buf.insert(0,"synopticMessageAction.options: "
//							+ state.getAttribute(STATE_CHANNEL_REF)
//							+ " time: " + (endTime - startTime));
//				}
//			}

			List channel_list = new Vector();
			while(aChannel.hasNext())
			{
				String theChannel = (String) aChannel.next();
				if(!theChannel.equals(SiteService.MAIN_CONTAINER))
				{
					channel_list.add(theChannel);
				}
			}
			context.put("channels", channel_list);
		}

		// provide "days" with the days value
		context.put("days", ((Integer) state.getAttribute(STATE_DAYS)).toString());

		// provide "items" with the items value
		context.put("items", ((Integer) state.getAttribute(STATE_ITEMS)).toString());

		// provide "length" with the items value
		context.put("length", ((Integer) state.getAttribute(STATE_LENGTH)).toString());

		// provide the form field names
		context.put("channel_form", FORM_CHANNEL);
		context.put("days_form", FORM_DAYS);
		context.put("items_form", FORM_ITEMS);
		context.put("length_form", FORM_LENGTH);
		context.put("show_body_form", FORM_SHOW_BODY);
		context.put("show_subject_form", FORM_SHOW_SUBJECT);

		// set the action for form processing
		context.put("action", state.getAttribute(STATE_ACTION));
		context.put("form-submit", BUTTON + "doUpdate");
		context.put("form-cancel", BUTTON + "doCancel");

		context.put("selectedChars", state.getAttribute(STATE_LENGTH));
		
		// pick the "-customize" template based on the standard template name
		String template = (String)getContext(rundata).get("template");
		return template + "-customize";

	}   // buildOptionsPanelContext
	
	
	/**
	 * doUpdate handles user clicking "Done" in Options panel (called for form input tags
	 *   type="submit" named="eventSubmit_doUpdate")
	 */
	public void doUpdate(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		String serviceName = (String) state.getAttribute(STATE_SERVICE_NAME);
		
		boolean allow_show_subject = true;
		boolean allow_channel_choice = false;
		
		if(serviceName.equals(ChatService.SERVICE_NAME))
		{
			allow_show_subject = false;
			allow_channel_choice = true;
		}
		
		// showSubject
		if(allow_show_subject)
		{
			if(serviceName.equals(DiscussionService.SERVICE_NAME))
			{
				// always show subject for Recent Discussion
				String showBody = data.getParameters().getString(FORM_SHOW_BODY);
				try
				{
					Boolean sb = new Boolean(showBody);
					if (!sb.equals((Boolean) state.getAttribute(STATE_SHOW_BODY)))
					{
						state.setAttribute(STATE_SHOW_BODY, sb);
						state.setAttribute(STATE_UPDATE, STATE_UPDATE);
					}
				}
				catch (Exception ignore) {}
			}
			else 
				if(serviceName.equals(AnnouncementService.SERVICE_NAME))
				{
					String showSubject = data.getParameters().getString(FORM_SHOW_SUBJECT);
					try
					{
						Boolean ss = new Boolean(showSubject);
						if (!ss.equals((Boolean) state.getAttribute(STATE_SHOW_SUBJECT)))
						{
							state.setAttribute(STATE_SHOW_SUBJECT, ss);
							state.setAttribute(STATE_UPDATE, STATE_UPDATE);
						}
					}
					catch (Exception ignore) {}
				}
		}

		// channel
		if(allow_channel_choice)
		{
			String placementContext = ToolManager.getCurrentPlacement().getContext();
			String newChannel = data.getParameters().getString(FORM_CHANNEL);
			String currentChannel = ((String) state.getAttribute(STATE_CHANNEL_REF)).substring(placementContext.length() + 1);

			if (newChannel != null && !newChannel.equals(currentChannel))
			{
				String channel_ref = ((MessageService) state.getAttribute(STATE_SERVICE)).channelReference(placementContext, newChannel);
				state.setAttribute(STATE_CHANNEL_REF, channel_ref);
				if (Log.getLogger("chef").isDebugEnabled())
					Log.debug("chef", this + ".doUpdate(): newChannel: " + channel_ref);
//				updateObservationOfChannel(state, peid);
				
				// update the tool config
				Placement placement = ToolManager.getCurrentPlacement();
				placement.getPlacementConfig().setProperty(PARAM_CHANNEL, (String)state.getAttribute(STATE_CHANNEL_REF));
				
				// deliver an update to the title panel (to show the new title)
				String titleId = titlePanelUpdateId(peid);
				schedulePeerFrameRefresh(titleId);
			}
		}

		// days
		String daysValue = data.getParameters().getString(FORM_DAYS);
		try
		{
			Integer days = new Integer(daysValue);
			if (!days.equals((Integer) state.getAttribute(STATE_DAYS)))
			{
				state.setAttribute(STATE_DAYS, days);
				state.setAttribute(STATE_UPDATE, STATE_UPDATE);
				
				// recompute this which is used for selecting the messages for display
				long startTime = System.currentTimeMillis() - (days.longValue() * 24l * 60l * 60l * 1000l);
				state.setAttribute(STATE_AFTER_DATE, TimeService.newTime(startTime));
			}
		}
		catch (Exception ignore) {}

		// items
		String itemsValue = data.getParameters().getString(FORM_ITEMS);
		try
		{
			Integer items = new Integer(itemsValue);
			if (!items.equals((Integer) state.getAttribute(STATE_ITEMS)))
			{
				state.setAttribute(STATE_ITEMS, items);
				state.setAttribute(STATE_UPDATE, STATE_UPDATE);
			}
		}
		catch (Exception ignore) {}

		// length
		String lengthValue = data.getParameters().getString(FORM_LENGTH);
		try
		{
			Integer length = new Integer(lengthValue);
			if (!length.equals((Integer) state.getAttribute(STATE_LENGTH)))
			{
				state.setAttribute(STATE_LENGTH, length);
				state.setAttribute(STATE_UPDATE, STATE_UPDATE);
			}
		}
		catch (Exception ignore) {}

		// update the tool config
		Placement placement = ToolManager.getCurrentPlacement();
		placement.getPlacementConfig().setProperty(PARAM_CHANNEL, (String)state.getAttribute(STATE_CHANNEL_REF));
		placement.getPlacementConfig().setProperty(PARAM_DAYS, ((Integer) state.getAttribute(STATE_DAYS)).toString());
		placement.getPlacementConfig().setProperty(PARAM_ITEMS, ((Integer) state.getAttribute(STATE_ITEMS)).toString());
		placement.getPlacementConfig().setProperty(PARAM_LENGTH, ((Integer) state.getAttribute(STATE_LENGTH)).toString());
		placement.getPlacementConfig().setProperty(PARAM_SHOW_BODY, ((Boolean)state.getAttribute(STATE_SHOW_BODY)).toString());
		placement.getPlacementConfig().setProperty(PARAM_SHOW_SUBJECT, ((Boolean)state.getAttribute(STATE_SHOW_SUBJECT)).toString());

		// commit the change
		saveOptions();

		// we are done with customization... back to the main mode
		state.removeAttribute(STATE_MODE);

		// enable auto-updates while in view mode
		enableObservers(state);

	}   // doUpdate

	/**
	* doCancel handles user clicking "Cancel" in Options panel
	*/
	public void doCancel(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// we are done with customization... back to the main mode
		state.removeAttribute(STATE_MODE);

		// cancel the options
		cancelOptions();

		// enable auto-updates while in view mode
		enableObservers(state);

	}   // doCancel

	/** 
	 * Improves performance by returning the appropriate MessageService
	 * through the service Cover classes instead of through the ComponentManager
	 * (for certain well-known services)
	 */
	private static final MessageService getMessageService(String ifaceName)
	{
		if (!ComponentManager.CACHE_MESSAGE_SERVICES) return (MessageService) ComponentManager.get(ifaceName);
		if (ifaceName.equals(ChatService.SERVICE_NAME)) return ChatService.getInstance();
		else if (ifaceName.equals(AnnouncementService.SERVICE_NAME)) return AnnouncementService.getInstance();
		else if (ifaceName.equals(DiscussionService.SERVICE_NAME)) return DiscussionService.getInstance();
		else if (ifaceName.equals(MailArchiveService.SERVICE_NAME)) return MailArchiveService.getInstance();
		else return (MessageService) ComponentManager.get(ifaceName);		
	}	
	
	/**
	* A filter
	*/
//	private class SynopticMessageFilter
//		implements Filter
//	{
//		/** The number of days back to accept messages. */
//		private int m_days = 0;
//
//		/** The maximum number of items to accept. */
//		private int m_items = 0;
//
//		/** How many items we have accepted so far. */
//		private int m_itemsAccepted = 0;
//
//		/** The cutoff time - messages before this are rejected. */
//		private Time m_cutoff = null;
//
//		/**
//		* Construct.
//		* @param days The number of days back to accept messages.
//		* @param items The maximum number of items to accept.
//		*/
//		public SynopticMessageFilter(int days, int items)
//		{
//			m_days = days;
//			m_items = items;
//			
//			// compute the cutoff - Note: use the filter fast - the clock is ticking.
//			m_cutoff = TimeService.newTime(System.currentTimeMillis() - ((long)days * 24l * 60l * 60l * 1000l));
//		}
//
//		/**
//		* Does this object satisfy the criteria of the filter?
//		* @param o The object to test.
//		* @return true if the object is accepted by the filter, false if not.
//		*/
//		public boolean accept(Object o)
//		{
//			// only allow the specified number of items
//			if (m_itemsAccepted >= m_items) return false;
//			
//			// we want to test only messages
//			if (!(o instanceof Message)) return false;
//
//			// if the item is older than m_days days ago, reject it.
//			if (((Message) o).getHeader().getDate().before(m_cutoff)) return false;
//			
//			// accept this one, and count
//			m_itemsAccepted++;
//			return true;
//
//		}	// accept
//
//	}	// SynopticMessageFilter

}   // SynopticMessageAction



