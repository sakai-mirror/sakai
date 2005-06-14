/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/Reference.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.resource;

// imports
import java.util.List;
import java.util.Vector;

import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.assignment.cover.AssignmentService;
import org.sakaiproject.service.legacy.calendar.Calendar;
import org.sakaiproject.service.legacy.calendar.CalendarEvent;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>
 * Reference holds a reference to a Sakai resource.
 * </p>
 * <p>
 * Note: a Reference is immutable.
 * </p>
 * 
 * @author University of Michigan, Sajau Software Development Team
 * @version $Revision: 1.1 $
 */
public class Reference
{
	/** The reference string. */
	protected String m_reference = null;

	/** The reference type (a service name string). */
	protected String m_type = null;

	/** The reference sub-type. */
	protected String m_subType = null;

	/** The reference primary id. */
	protected String m_id = null;

	/** The reference containment ids. */
	protected String m_container = null;

	protected String m_container2 = null;

	/** Another container, the context id. */
	protected String m_context = null;

	/** These are some more services known. */
	public static final String GRADEBOOK_ROOT = "gradebook";

	public static final String GRADTOOLS_ROOT = "dissertation";

	/**
	 * Construct with a reference string.
	 * 
	 * @param ref
	 *        The resource reference.
	 */
	public Reference(String ref)
	{
		m_reference = ref;
		parse();

	} // Reference

	/**
	 * Construct with a Reference.
	 * 
	 * @param ref
	 *        The resource reference.
	 */
	public Reference(Reference ref)
	{
		m_reference = ref.m_reference;
		m_type = ref.m_type;
		m_subType = ref.m_subType;
		m_id = ref.m_id;
		m_container = ref.m_container;
		m_container2 = ref.m_container2;
		m_context = ref.m_context;

	} // Reference

	/**
	 * Access the reference.
	 * 
	 * @return The reference.
	 */
	public String getReference()
	{
		return m_reference;
	}

	/**
	 * Access the type, a service id string.
	 * 
	 * @return The type, a service id string.
	 */
	public String getType()
	{
		return m_type;
	}

	/**
	 * Access the subType.
	 * 
	 * @return The subType.
	 */
	public String getSubType()
	{
		return m_subType;
	}

	/**
	 * Access the primary id.
	 * 
	 * @return The primary id.
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * Access the list of container ids, from most general to most specific.
	 * 
	 * @return The List (String) of container ids, from most general to most specific.
	 */
	/*
	 * public List getContainers() { List rv = new Vector(); if (m_container != null) rv.add(m_container); if (m_container2 != null) rv.add(m_container2); return rv; } // getContainers
	 */
	/**
	 * Access a single container id, the from most general (or only)
	 * 
	 * @return The single or most general container, if any.
	 */
	public String getContainer()
	{
		return m_container;

	} // getContainer

	/**
	 * Access the second container id, if any.
	 * 
	 * @return The second container id, if any.
	 */
	public String getContainer2()
	{
		return m_container2;

	} // getContainer2

	/**
	 * Access the context id, if any.
	 * 
	 * @return the context id, if any.
	 */
	public String getContext()
	{
		return m_context;

	} // getContext

	/**
	 * Find the ResourceProperties object for this reference.
	 * 
	 * @return A ResourcesProperties object found (or constructed) for this reference.
	 */
	public ResourceProperties getProperties()
	{
		ResourceProperties props = null;

		// for content
		if (m_type.equals(ContentHostingService.SERVICE_NAME))
		{
			try
			{
				props = ContentHostingService.getProperties(m_id);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + ".getProperties(): " + e);
			}
			catch (IdUnusedException e)
			{
				// This just means that the resource once pointed to as an attachment or something has been deleted.
				// Log.warn("chef", this + ".getProperties(): " + e);
			}
		}

		// for messages
		else if ((m_type.equals(ChatService.SERVICE_NAME)) || (m_type.equals(AnnouncementService.SERVICE_NAME))
				|| (m_type.equals(DiscussionService.SERVICE_NAME)) || (m_type.equals(MailArchiveService.SERVICE_NAME)))
		{
			MessageService service = getMessageService(m_type);

			try
			{
				// if this is a channel
				if (MessageService.REF_TYPE_CHANNEL.equals(m_subType))
				{
					MessageChannel channel = service.getChannel(m_reference);
					props = channel.getProperties();
				}

				// otherwise a message
				else if (MessageService.REF_TYPE_MESSAGE.equals(m_subType))
				{
					Message message = service.getMessage(this);
					props = message.getProperties();
				}

				else
					Log.warn("chef", this + ".getProperties(): unknown message ref subtype: " + m_subType + " in ref: "
							+ m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + ".getProperties(): " + e);
			}
			catch (IdUnusedException e)
			{
				// This just means that the resource once pointed to as an attachment or something has been deleted.
				// Log.warn("chef", this + ".getProperties(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getProperties(): " + e);
			}
		}

		// for calendar
		else if (m_type.equals(CalendarService.SERVICE_NAME))
		{
			try
			{
				// if this is a calendar
				if (CalendarService.REF_TYPE_CALENDAR.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(m_reference);
					props = cal.getProperties();
				}

				// otherwise a event
				else if (CalendarService.REF_TYPE_EVENT.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(CalendarService.calendarReference(m_context, m_container));
					CalendarEvent event = cal.getEvent(m_id);
					props = event.getProperties();
				}

				else
					Log.warn("chef", this + ".getProperties(): unknown calendar ref subtype: " + m_subType + " in ref: "
							+ m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + ".getProperties(): " + e);
			}
			catch (IdUnusedException e)
			{
				// This just means that the resource once pointed to as an attachment or something has been deleted.
				// Log.warn("chef", this + ".getProperties(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getProperties(): " + e);
			}
		}

		// for dissertation
		else if (m_type.equals(GRADTOOLS_ROOT))
		{
			Log.warn("chef", this + ".getProperties(): for dissertation, not supported");
//			try
//			{
//				// get properties by type
//				if ("d".equals(m_subType))
//				{
//					Dissertation dissertation = DissertationService.getDissertation(m_reference);
//					props = dissertation.getProperties();
//				}
//				else if ("s".equals(m_subType))
//				{
//					DissertationStep step = DissertationService.getDissertationStep(m_reference);
//					props = step.getProperties();
//				}
//				else if ("p".equals(m_subType))
//				{
//					CandidatePath path = DissertationService.getCandidatePath(m_reference);
//					props = path.getProperties();
//				}
//				else if ("ss".equals(m_subType))
//				{
//					StepStatus status = DissertationService.getStepStatus(m_reference);
//					props = status.getProperties();
//				}
//				else if ("i".equals(m_subType))
//				{
//					CandidateInfo info = DissertationService.getCandidateInfo(m_reference);
//					props = info.getProperties();
//				}
//				else
//					Log.warn("chef", this + ".getProperties(): unknown dissertation ref subtype: " + m_subType + " in ref: "
//							+ m_reference);
//			}
//			catch (PermissionException e)
//			{
//				Log.warn("chef", this + "getProperties(): " + e);
//			}
//			catch (IdUnusedException e)
//			{
//				// This just means that the resource once pointed to as an attachment or something has been deleted.
//				// Log.warn("chef", this + ".getProperties(): " + e);
//			}
//			catch (NullPointerException e)
//			{
//				Log.warn("chef", this + ".getProperties(): " + e);
//			}
		}

		// %%% deal with the other types, too. -grg

		return props;

	} // getProperties

	/**
	 * Find the Resource that references.
	 * 
	 * @return The Resource object that this references.
	 */
	public Resource getResource()
	{
		Resource r = null;

		// for content
		if (m_type.equals(ContentHostingService.SERVICE_NAME))
		{
			try
			{
				ResourceProperties props = ContentHostingService.getProperties(m_id);
				if (props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION))
				{
					r = ContentHostingService.getCollection(m_id);
				}
				else
				{
					r = ContentHostingService.getResource(m_id);
				}
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (TypeException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (EmptyException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getResource(): " + e);
			}
		}

		// for messages
		else if ((m_type.equals(ChatService.SERVICE_NAME)) || (m_type.equals(AnnouncementService.SERVICE_NAME))
				|| (m_type.equals(DiscussionService.SERVICE_NAME)) || (m_type.equals(MailArchiveService.SERVICE_NAME)))
		{
			MessageService service = getMessageService(m_type);
			try
			{
				// if this is a channel
				if (MessageService.REF_TYPE_CHANNEL.equals(m_subType))
				{
					r = service.getChannel(m_reference);
				}

				// otherwise a message
				else if (MessageService.REF_TYPE_MESSAGE.equals(m_subType))
				{
					r = service.getMessage(this);
				}

				// else try {throw new Exception();} catch (Exception e) {Log.warn("chef", this + "getResource(): unknown message ref subtype: " + m_subType + " in ref: " + m_reference, e);}
				else
					Log.warn("chef", this + "getResource(): unknown message ref subtype: " + m_subType + " in ref: " + m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getResource(): " + e);
			}
		}

		// for calendar
		else if (m_type.equals(CalendarService.SERVICE_NAME))
		{
			try
			{
				// if this is a calendar
				if (CalendarService.REF_TYPE_CALENDAR.equals(m_subType))
				{
					r = CalendarService.getCalendar(m_reference);
				}

				// otherwise a event
				else if (CalendarService.REF_TYPE_EVENT.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(CalendarService.calendarReference(m_context, m_container));
					r = cal.getEvent(m_id);
				}

				else
					Log.warn("chef", this + "getProperties(): unknown calendar ref subtype: " + m_subType + " in ref: "
							+ m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getResource(): " + e);
			}
		}

		// for dissertations
		else if (m_type.equals(GRADTOOLS_ROOT))
		{
			Log.warn("chef", this + ".getResource(): for dissertation, not supported");
//			try
//			{
//				if ("d".equals(m_subType))
//				{
//					r = DissertationService.getDissertation(m_reference);
//				}
//				else if ("s".equals(m_subType))
//				{
//					r = DissertationService.getDissertationStep(m_reference);
//				}
//				else if ("p".equals(m_subType))
//				{
//					r = DissertationService.getCandidatePath(m_reference);
//				}
//				else if ("ss".equals(m_subType))
//				{
//					r = DissertationService.getStepStatus(m_reference);
//				}
//				else if ("i".equals(m_subType))
//				{
//					r = DissertationService.getCandidateInfo(m_reference);
//				}
//
//				else
//					Log.warn("chef", this + "getResource(): unknown message ref subtype: " + m_subType + " in ref: " + m_reference);
//			}
//			catch (PermissionException e)
//			{
//				Log.warn("chef", this + "getResource(): " + e);
//			}
//			catch (IdUnusedException e)
//			{
//				Log.warn("chef", this + "getResource(): " + e);
//			}
//			catch (NullPointerException e)
//			{
//				Log.warn("chef", this + ".getResource(): " + e);
//			}
		}

		// for site
		if (m_type.equals(SiteService.SERVICE_NAME))
		{
			try
			{
				r = SiteService.getSite(m_id);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getResource(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getResource(): " + e);
			}
		}

		// %%% deal with the other types, too. -grg

		return r;

	} // getResource

	/**
	 * Access the URL which can be used to access the referenced resource.
	 * 
	 * @return The URL which can be used to access the referenced resource.
	 */
	public String getUrl()
	{
		String url = null;

		// for content
		if (m_type.equals(ContentHostingService.SERVICE_NAME))
		{
			url = ContentHostingService.getUrl(m_id);
		}

		// for messages
		else if ((m_type.equals(ChatService.SERVICE_NAME)) || (m_type.equals(AnnouncementService.SERVICE_NAME))
				|| (m_type.equals(DiscussionService.SERVICE_NAME)) || (m_type.equals(MailArchiveService.SERVICE_NAME)))
		{
			MessageService service = getMessageService(m_type);
			try
			{
				// if this is a channel
				if (MessageService.REF_TYPE_CHANNEL.equals(m_subType))
				{
					MessageChannel channel = service.getChannel(m_reference);
					url = channel.getUrl();
				}

				// otherwise a message
				else if (MessageService.REF_TYPE_MESSAGE.equals(m_subType))
				{
					Message message = service.getMessage(this);
					url = message.getUrl();
				}

				else
					Log.warn("chef", this + "getUrl(): unknown message ref subtype: " + m_subType + " in ref: " + m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + "getUrl(): " + e);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getUrl(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getUrl(): " + e);
			}
		}

		// for calendar
		else if (m_type.equals(CalendarService.SERVICE_NAME))
		{
			try
			{
				// if this is a calendar
				if (CalendarService.REF_TYPE_CALENDAR.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(m_reference);
					url = cal.getUrl();
				}

				// otherwise a event
				else if (CalendarService.REF_TYPE_EVENT.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(CalendarService.calendarReference(m_context, m_container));
					CalendarEvent event = cal.getEvent(m_id);
					url = event.getUrl();
				}

				else
					Log.warn("chef", this + "getUrl(): unknown calendar ref subtype: " + m_subType + " in ref: " + m_reference);
			}
			catch (PermissionException e)
			{
				Log.warn("chef", this + "getProperties(): " + e);
			}
			catch (IdUnusedException e)
			{
				Log.warn("chef", this + "getProperties(): " + e);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getUrl(): " + e);
			}
		}

		// for dissertations
		else if (m_type.equals(GRADTOOLS_ROOT))
		{
			Log.warn("chef", this + ".getUrl(): for dissertation, not supported");
//			try
//			{
//				// get url by type
//				if ("d".equals(m_subType))
//				{
//					Dissertation dissertation = DissertationService.getDissertation(m_reference);
//					url = dissertation.getUrl();
//				}
//				else if ("s".equals(m_subType))
//				{
//					DissertationStep step = DissertationService.getDissertationStep(m_reference);
//					url = step.getUrl();
//				}
//				else if ("p".equals(m_subType))
//				{
//					CandidatePath path = DissertationService.getCandidatePath(m_reference);
//					url = path.getUrl();
//				}
//				else if ("ss".equals(m_subType))
//				{
//					StepStatus status = DissertationService.getStepStatus(m_reference);
//					url = status.getUrl();
//				}
//				else if ("i".equals(m_subType))
//				{
//					CandidateInfo info = DissertationService.getCandidateInfo(m_reference);
//					url = info.getUrl();
//				}
//
//				else
//					Log.warn("chef", this + "getUrl(): unknown message ref subtype: " + m_subType + " in ref: " + m_reference);
//			}
//			catch (PermissionException e)
//			{
//				Log.warn("chef", this + "getUrl(): " + e);
//			}
//			catch (IdUnusedException e)
//			{
//				Log.warn("chef", this + "getUrl(): " + e);
//			}
//			catch (NullPointerException e)
//			{
//				Log.warn("chef", this + ".getUrl(): " + e);
//			}
		}

		// %%% deal with the other types, too. -grg

		return url;

	} // getUrl

	/**
	 * @return a description of the resource referenced.
	 */
	public String getDescription()
	{
		String rv = "unknown";

		// for content
		if (m_type.equals(ContentHostingService.SERVICE_NAME))
		{
			try
			{
				rv = "Content: " + m_id;

				ResourceProperties props = ContentHostingService.getProperties(m_id);
				if (props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION))
				{
					ContentCollection c = ContentHostingService.getCollection(m_id);
					rv = "Collection: " + c.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME) + " ("
							+ c.getId() + ")\n" + " Created: "
							+ c.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
							+ c.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
							+ c.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
							+ limit(c.getProperties().getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION), 30);
				}
				else
				{
					ContentResource r = ContentHostingService.getResource(m_id);
					rv = "Resource: " + r.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME) + " ("
							+ r.getId() + ")\n" + " Created: "
							+ r.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
							+ r.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
							+ r.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
							+ limit(r.getProperties().getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION), 30);
				}
			}
			catch (PermissionException e)
			{
			}
			catch (IdUnusedException e)
			{
			}
			catch (TypeException e)
			{
			}
			catch (EmptyException e)
			{
			}
			catch (NullPointerException e)
			{
			}
		}

		// for messages
		else if ((m_type.equals(ChatService.SERVICE_NAME)) || (m_type.equals(AnnouncementService.SERVICE_NAME))
				|| (m_type.equals(DiscussionService.SERVICE_NAME)) || (m_type.equals(MailArchiveService.SERVICE_NAME)))
		{
			MessageService service = getMessageService(m_type);
			try
			{
				rv = "Message: " + m_reference;

				// if this is a channel
				if (MessageService.REF_TYPE_CHANNEL.equals(m_subType))
				{
					MessageChannel channel = service.getChannel(m_reference);
					rv = "Channel: " + channel.getId() + " (" + channel.getContext() + ")";
				}
			}
			catch (PermissionException e)
			{
			}
			catch (IdUnusedException e)
			{
			}
			catch (NullPointerException e)
			{
			}
		}

		// for calendar
		else if (m_type.equals(CalendarService.SERVICE_NAME))
		{
			try
			{
				rv = "Calendar: " + m_reference;

				// if this is a calendar
				if (CalendarService.REF_TYPE_CALENDAR.equals(m_subType))
				{
					Calendar cal = CalendarService.getCalendar(m_reference);
					rv = "Calendar: " + cal.getId() + " (" + cal.getContext() + ")";
				}

				// otherwise a event
				else if (CalendarService.REF_TYPE_EVENT.equals(m_subType))
				{
					rv = "Event: " + m_reference;
				}
			}
			catch (PermissionException e)
			{
			}
			catch (IdUnusedException e)
			{
			}
			catch (NullPointerException e)
			{
			}
		}

		// for a site
		else if (m_type.equals(SiteService.SERVICE_NAME))
		{
			try
			{
				rv = "Site: " + m_reference;

				Site site = SiteService.getSite(m_id);
				rv = "Site: " + site.getTitle() + " (" + site.getId() + ")\n" + " Created: "
						+ site.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
						+ site.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
						+ site.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
						+ limit((site.getDescription() == null ? "" : site.getDescription()), 30);
			}
			catch (IdUnusedException e)
			{
			}
			catch (NullPointerException e)
			{
			}
		}

		// for a user
		else if (m_type.equals(UserDirectoryService.SERVICE_NAME))
		{
			try
			{
				rv = "User: " + m_reference;

				User user = UserDirectoryService.getUser(m_id);
				rv = "User: " + user.getDisplayName();
			}
			catch (IdUnusedException e)
			{
			}
			catch (NullPointerException e)
			{
			}
		}
		
		// for gradebook
		else if (m_type.equals(GRADEBOOK_ROOT))
		{
			rv = "Gradebook: " + m_context + " / " + m_id;
		}

		return rv;

	} // getDescription

	/**
	 * Compute the set of Realm ids associated with this referenced resource.
	 * 
	 * @return List of Realm ids (String) associated with this referenced resource.
	 */
	public List getRealms()
	{
		if (Log.getLogger("chef").isDebugEnabled()) Log.debug("chef", this + ".getRealms: for ref: " + getReference());

		List rv = new Vector();

		// if the reference is a realm, and not a special one
		// get the list of realms for the realm-referenced resource
		if (getType().equals(RealmService.SERVICE_NAME) && (getId() != null) && (getId().length() > 0)
				&& (!getId().startsWith("!")))
		{
			// add the current user's realm (for what realm stuff everyone can do, i.e. add)
			addUserRealm(rv, UsageSessionService.getSessionUserId());

			// make a new reference on the realm's id
			Reference ref = new Reference(getId());
			rv.addAll(ref.getRealms());
		}

		// for content hosting resources and collections:
		// use the resources realm, all container (folder) realms
		if (getType().equals(ContentHostingService.SERVICE_NAME))
		{
			try
			{
				// try the resource, all the folders above it (don't include /)
				String paths[] = split(getId(), Resource.SEPARATOR);
				boolean container = getId().endsWith(Resource.SEPARATOR);
				if (paths.length > 1)
				{
					String root = ContentHostingService.getReference(Resource.SEPARATOR + paths[1] + Resource.SEPARATOR);
					rv.add(root);

					for (int next = 2; next < paths.length; next++)
					{
						root = root + paths[next];
						if ((next < paths.length - 1) || container)
						{
							root = root + Resource.SEPARATOR;
						}
						rv.add(root);
					}
				}

				// special check for group-user : the grant's in the user's myWorkspace site
				String parts[] = split(getId(), Resource.SEPARATOR);
				if ((parts.length > 3) && (parts[1].equals("group-user")))
				{
					rv.add(SiteService.siteReference(SiteService.getUserSiteId(parts[3])));
				}

				// grant in the site
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}
		}

		// for MessageService messages and channels:
		// the message's realm, the channel's realm
		else if (((getType().equals(ChatService.SERVICE_NAME)) || (getType().equals(AnnouncementService.SERVICE_NAME))
				|| (getType().equals(DiscussionService.SERVICE_NAME)) || (getType().equals(MailArchiveService.SERVICE_NAME))))
		{
			try
			{
				MessageService service = getMessageService(m_type);
				String channelId = null;

				// if a message, try this realm
				if (MessageService.REF_TYPE_MESSAGE.equals(getSubType()))
				{
					// a message
					rv.add(getReference());
					channelId = getContainer();
				}

				// otherwise a channel - get the id
				else
				{
					channelId = getId();
				}

				// try the channel's realm
				rv.add(service.channelReference(getContext(), channelId));

				// grant in the site
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}
		}

		// for calendar calendars and events: the event, the calendar
		else if (getType().equals(CalendarService.SERVICE_NAME))
		{
			try
			{
				String calendarId = null;

				// if an event try this realm
				if (CalendarService.REF_TYPE_EVENT.equals(getSubType()))
				{
					// an event
					rv.add(getReference());
					calendarId = getContainer();
				}

				// otherwise a calendar, get the id
				else
				{
					calendarId = getId();
				}

				// try the calendar's realm
				rv.add(CalendarService.calendarReference(getContext(), calendarId));

				// grant in the site
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}
		}

		// for portal page access: no additional role realms
		// TODO: is this still used? -ggolden
		else if (getType().equals(PortalService.SERVICE_NAME))
		{
			try
			{
				// add a site reference
				if ("group".equals(getContainer()))
				{
					rv.add(SiteService.siteReference(getId()));
				}
				else if ("user".equals(getContainer()))
				{
					rv.add(SiteService.siteReference(SiteService.getUserSiteId(getId())));
				}
				else if ("role".equals(getContainer()) && "user".equals(getId()))
				{
					rv.add(SiteService.siteReference(SiteService.getUserSiteId(UsageSessionService.getSessionUserId())));
				}
				else
				{
					rv.add(SiteService.siteReference(SiteService.getSpecialSiteId(getContainer() + "-" + getId())));
				}

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for site access: user realm
		else if (getType().equals(SiteService.SERVICE_NAME))
		{
			try
			{
				rv.add(SiteService.siteReference(getId()));

				// add the current user's realm
				addUserRealm(rv, UsageSessionService.getSessionUserId());

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for user access: user and template realms
		else if (getType().equals(UserDirectoryService.SERVICE_NAME))
		{
			try
			{
				rv.add(UserDirectoryService.userReference(getId()));

				addUserTemplateRealm(rv, UsageSessionService.getSessionUserId());
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for preferences access: no additional role realms
		else if (getType().equals(PreferencesService.SERVICE_NAME))
		{
			try
			{
				rv.add(UserDirectoryService.userReference(getId()));

				addUserTemplateRealm(rv, UsageSessionService.getSessionUserId());
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for alias access %%% ? what realm? -ggolden
		else if (getType().equals(AliasService.SERVICE_NAME))
		{
		}

		// for (special) realm access:
		// any modification or delete of a realm, checks the realm itself for permission.
		// all users get to add realms (predefined in the "/user/~" realm
		else if (getType().equals(RealmService.SERVICE_NAME))
		{
			try
			{
				rv.add(getReference());

				// add the current user's realm (for what realm stuff everyone can do, i.e. add)
				addUserRealm(rv, UsageSessionService.getSessionUserId());
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for assignments
		else if (getType().equals(AssignmentService.SERVICE_NAME))
		{
			try
			{
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");

				// specific
				rv.add(getReference());

				// mid way - for submission, the assignment reference %%% id is in continer 2
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}

		// for dissertations
		else if (getType().equals(GRADTOOLS_ROOT))
		{
			try
			{
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");

				// specific
				rv.add(getReference());

				// school reference
				String schoolRef = Resource.SEPARATOR + GRADTOOLS_ROOT + Resource.SEPARATOR + getSubType() + Resource.SEPARATOR
						+ getContainer();
				rv.add(schoolRef);
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}
		}

		// for gradebook
		else if (getType().equals(GRADEBOOK_ROOT))
		{
			try
			{
				rv.add(SiteService.siteReference(getContext()));

				// site helper
				rv.add("!site.helper");

				// specific
				rv.add(getReference());
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}
		}

		return rv;

	} // getRealms

	/**
	 * Add the Realm for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	protected void addUserRealm(List rv, String id)
	{
		if (id == null) id = "";

		// the user's realm
		rv.add(UserDirectoryService.userReference(id));

		addUserTemplateRealm(rv, id);
	}

	/**
	 * Add the Realm for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	protected void addUserTemplateRealm(List rv, String id)
	{
		if (id == null) id = "";

		// user type template
		String template = "!user.template";
		try
		{
			User user = UserDirectoryService.getUser(id);
			String type = user.getType();
			if (type != null)
			{
				rv.add(template + "." + type);
			}
		}
		catch (Exception ignore)
		{
		}

		// general user template
		rv.add("!user.template");
	}

	/*
	 * Parse the reference
	 */
	protected void parse()
	{
		String contentAccessBase = ContentHostingService.getUrl("");

		// for content hosting resources and collections
		if ((m_reference != null) && (m_reference.startsWith(ContentHostingService.REFERENCE_ROOT)))
		{
			// parse out the local resource id
			m_id = m_reference.substring(ContentHostingService.REFERENCE_ROOT.length(), m_reference.length());
			m_type = ContentHostingService.SERVICE_NAME;
		}

		// for content hosting resources and collections - full url
		else if ((m_reference != null) && (contentAccessBase != null) && (m_reference.startsWith(contentAccessBase)))
		{
			// parse out the local resource id
			m_id = m_reference.substring(contentAccessBase.length(), m_reference.length());

			m_type = ContentHostingService.SERVICE_NAME;
		}

		// for MessageService messages and channels
		else if ((m_reference != null)
				&& ((m_reference.startsWith(ChatService.REFERENCE_ROOT))
						|| (m_reference.startsWith(AnnouncementService.REFERENCE_ROOT))
						|| (m_reference.startsWith(DiscussionService.REFERENCE_ROOT)) || (m_reference
						.startsWith(MailArchiveService.REFERENCE_ROOT))))
		{
			String[] parts = split(m_reference, Resource.SEPARATOR);

			// the first part will be null, then next the service, the third will be "msg" or "channel"
			if (parts.length > 2)
			{
				m_subType = parts[2];
				if (MessageService.REF_TYPE_CHANNEL.equals(m_subType))
				{
					// next is the context id
					if (parts.length > 3)
					{
						m_context = parts[3];

						// next is the channel id
						if (parts.length > 4)
						{
							m_id = parts[4];
						}
					}
				}
				else if (MessageService.REF_TYPE_MESSAGE.equals(m_subType))
				{
					// next three parts are context, channel (container) and mesage id
					if (parts.length > 5)
					{
						m_context = parts[3];
						m_container = parts[4];
						m_id = parts[5];
					}
				}
				// else try{throw new Exception();} catch (Exception e) {Log.warn("chef", this + "parse(): unknown message subtype: " + m_subType + " in ref: " + m_reference, e);}
				else
					Log.warn("chef", this + "parse(): unknown message subtype: " + m_subType + " in ref: " + m_reference);
			}

			if (m_reference.startsWith(ChatService.REFERENCE_ROOT))
			{
				m_type = ChatService.SERVICE_NAME;
			}
			else if (m_reference.startsWith(AnnouncementService.REFERENCE_ROOT))
			{
				m_type = AnnouncementService.SERVICE_NAME;
			}
			else if (m_reference.startsWith(DiscussionService.REFERENCE_ROOT))
			{
				m_type = DiscussionService.SERVICE_NAME;
			}
			else if (m_reference.startsWith(MailArchiveService.REFERENCE_ROOT))
			{
				m_type = MailArchiveService.SERVICE_NAME;
			}
		}

		// for calendar calendars and events
		else if ((m_reference != null) && (m_reference.startsWith(CalendarService.REFERENCE_ROOT)))
		{
			String[] parts = split(m_reference, Resource.SEPARATOR);

			// the first part will be null, then next the service, the third will be "calendar" or "event"
			if (parts.length > 2)
			{
				m_subType = parts[2];
				if (CalendarService.REF_TYPE_CALENDAR.equals(m_subType))
				{
					// next is the context id
					if (parts.length > 3)
					{
						m_context = parts[3];

						// next is the calendar id
						if (parts.length > 4)
						{
							m_id = parts[4];
						}
					}
				}
				else if (CalendarService.REF_TYPE_EVENT.equals(m_subType))
				{
					// next three parts are context, channel (container) and event id
					if (parts.length > 5)
					{
						m_context = parts[3];
						m_container = parts[4];
						m_id = parts[5];
					}
				}
				else
					Log.warn("chef", this + "parse(): unknown calendar subtype: " + m_subType + " in ref: " + m_reference);
			}

			m_type = CalendarService.SERVICE_NAME;
		}

		// for site access
		else if ((m_reference != null) && (m_reference.startsWith(SiteService.REFERENCE_ROOT)))
		{
			// we will get null, service, siteId
			String[] parts = split(m_reference, Resource.SEPARATOR);

			if (parts.length > 2)
			{
				m_id = parts[2];
			}

			m_type = SiteService.SERVICE_NAME;
		}

		// for user access
		else if ((m_reference != null) && (m_reference.startsWith(UserDirectoryService.REFERENCE_ROOT)))
		{
			// we will get null, service, userId
			String[] parts = split(m_reference, Resource.SEPARATOR);

			if (parts.length > 2)
			{
				m_id = parts[2];
			}

			m_type = UserDirectoryService.SERVICE_NAME;
		}

		// for preferences access
		else if ((m_reference != null) && (m_reference.startsWith(PreferencesService.REFERENCE_ROOT)))
		{
			// we will get null, service, user/preferences Id
			String[] parts = split(m_reference, Resource.SEPARATOR);

			if (parts.length > 2)
			{
				m_id = parts[2];
			}

			m_type = PreferencesService.SERVICE_NAME;
		}

		// for alias access
		else if ((m_reference != null) && (m_reference.startsWith(AliasService.REFERENCE_ROOT)))
		{
			// we will get null, service, userId
			String[] parts = split(m_reference, Resource.SEPARATOR);

			if (parts.length > 2)
			{
				m_id = parts[2];
			}

			m_type = AliasService.SERVICE_NAME;
		}

		// for realm access
		else if ((m_reference != null) && (m_reference.startsWith(RealmService.REFERENCE_ROOT)))
		{
			// the realm id may have separators - we use everything after "/realm/"
			m_id = m_reference.substring(RealmService.REFERENCE_ROOT.length() + 1, m_reference.length());

			m_type = RealmService.SERVICE_NAME;
		}

		// for assignments
		else if ((m_reference != null) && (m_reference.startsWith(AssignmentService.REFERENCE_ROOT)))
		{
			String[] parts = split(m_reference, Resource.SEPARATOR);
			// we will get null, assignment, [a|c|s|grades|submissions], context, [auid], id

			if (parts.length > 2)
			{
				m_subType = parts[2];

				if (parts.length > 3)
				{
					// context is the container
					m_context = parts[3];

					// submissions have the assignment unique id as a container
					if ("s".equals(m_subType))
					{
						if (parts.length > 5)
						{
							m_container = parts[4];
							m_id = parts[5];
						}
					}

					// others don't
					else
					{
						if (parts.length > 4)
						{
							m_id = parts[4];
						}
					}
				}
			}

			m_type = AssignmentService.SERVICE_NAME;
		}

		// for dissertations
		else if ((m_reference != null) && (m_reference.startsWith(Resource.SEPARATOR + GRADTOOLS_ROOT)))
		{
			String[] parts = split(m_reference, Resource.SEPARATOR);
			// we will get null, dissertation, [d|s|p|ss|i], outer context, inner context, id

			if (parts.length > 2)
			{
				m_subType = parts[2];

				if (parts.length > 3)
				{
					// inner context is container
					m_container = parts[3];

					if (parts.length > 4)
					{
						// outer context is container2
						m_context = parts[4];

						if (parts.length > 5)
						{
							m_id = parts[5];
						}
					}
				}
			}

			m_type = GRADTOOLS_ROOT;
		}

		// for gradebook
		else if ((m_reference != null) && (m_reference.startsWith(Resource.SEPARATOR + GRADEBOOK_ROOT)))
		{
			// we will get null, gradebook, context, id
			String[] parts = split(m_reference, Resource.SEPARATOR);

			if (parts.length > 2)
			{
				m_context = parts[2];
			}
			if (parts.length > 3)
			{
				m_id = parts[3];
			}

			m_type = GRADEBOOK_ROOT;
		}

		else
		{
			if (Log.getLogger("chef").isDebugEnabled()) Log.debug("chef", this + ".parse(): unhandled reference: " + m_reference);
		}

		// get the context for a content ref
		if (ContentHostingService.SERVICE_NAME.equals(m_type))
		{
			// parse out the associated site id
			String parts[] = split(m_id, Resource.SEPARATOR);
			if (parts.length >= 3)
			{
				if (parts[1].equals("group"))
				{
					m_context = parts[2];
				}
				else if (parts[1].equals("user"))
				{
					m_context = SiteService.getUserSiteId(parts[2]);
				}
				else if (parts[1].equals("group-user"))
				{
					// use just the group context
					m_context = parts[2];
				}
			}
			else
			{
				m_context = "";
			}
		}

	} // parse

	/**
	 * Limit the string to a certain number of characters, adding "..." if it was truncated
	 * 
	 * @param value
	 *        The string to limit.
	 * @param the
	 *        length to limit to (as an int).
	 * @return The limited string.
	 */
	protected String limit(String value, int length)
	{
		StringBuffer buf = new StringBuffer(value);
		if (buf.length() > length)
		{
			buf.setLength(length);
			buf.append("...");
		}

		return buf.toString();

	} // limit

	/**
	 * Like jdk1.4's String.split...
	 */
	protected String[] split(String source, String splitter)
	{
		// hold the results as we find them
		Vector rv = new Vector();
		int last = 0;
		int next = 0;
		do
		{
			// find next splitter in source
			next = source.indexOf(splitter, last);
			if (next != -1)
			{
				// isolate from last thru before next
				rv.add(source.substring(last, next));
				last = next + splitter.length();
			}
		}
		while (next != -1);
		if (last < source.length())
		{
			rv.add(source.substring(last, source.length()));
		}

		// convert to array
		return (String[]) rv.toArray(new String[rv.size()]);

	} // split

	/**
	 * Improves performance by returning the appropriate MessageService through the service Cover classes instead of through the ComponentManager (for certain well-known services)
	 * 
	 * @param ifaceName
	 */
	private static final MessageService getMessageService(String ifaceName)
	{
		if (!ComponentManager.CACHE_MESSAGE_SERVICES) return (MessageService) ComponentManager.get(ifaceName);
		if (ifaceName.equals(ChatService.SERVICE_NAME))
			return ChatService.getInstance();
		else if (ifaceName.equals(AnnouncementService.SERVICE_NAME))
			return AnnouncementService.getInstance();
		else if (ifaceName.equals(DiscussionService.SERVICE_NAME))
			return DiscussionService.getInstance();
		else if (ifaceName.equals(MailArchiveService.SERVICE_NAME))
			return MailArchiveService.getInstance();
		else
			return (MessageService) ComponentManager.get(ifaceName);
	}

} // Reference

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/Reference.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
