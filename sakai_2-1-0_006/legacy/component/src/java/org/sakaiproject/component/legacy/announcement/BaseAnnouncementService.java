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
package org.sakaiproject.component.legacy.announcement;

// import
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.component.legacy.message.BaseMessageService;
import org.sakaiproject.component.legacy.notification.SiteEmailNotificationAnnc;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.announcement.AnnouncementChannel;
import org.sakaiproject.service.legacy.announcement.AnnouncementChannelEdit;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageEdit;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeader;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeaderEdit;
import org.sakaiproject.service.legacy.announcement.AnnouncementService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageHeader;
import org.sakaiproject.service.legacy.message.MessageHeaderEdit;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.Filter;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>BaseAnnouncementService extends the BaseMessageService for the specifics of Announcement.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public abstract class BaseAnnouncementService extends BaseMessageService implements AnnouncementService
{
	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: NotificationService. */
	protected NotificationService m_notificationService = null;

	/**
	 * Dependency: NotificationService.
	 * @param service The NotificationService.
	 */
	public void setNotificationService(NotificationService service)
	{
		m_notificationService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			// register a transient notification for announcements
			NotificationEdit edit = m_notificationService.addTransientNotification();

			// set functions
			edit.setFunction(eventId(SECURE_ADD));
			edit.addFunction(eventId(SECURE_UPDATE_OWN));
			edit.addFunction(eventId(SECURE_UPDATE_ANY));

			// set the filter to any announcement resource (see messageReference())
			edit.setResourceFilter(getAccessPoint(true) + Entity.SEPARATOR + REF_TYPE_MESSAGE);

			// set the action
			edit.setAction(new SiteEmailNotificationAnnc());

			// register functions
			FunctionManager.registerFunction(eventId(SECURE_READ));
			FunctionManager.registerFunction(eventId(SECURE_ADD));
			FunctionManager.registerFunction(eventId(SECURE_REMOVE_ANY));
			FunctionManager.registerFunction(eventId(SECURE_REMOVE_OWN));
			FunctionManager.registerFunction(eventId(SECURE_UPDATE_ANY));
			FunctionManager.registerFunction(eventId(SECURE_UPDATE_OWN));
			FunctionManager.registerFunction(eventId(SECURE_READ_DRAFT));
			FunctionManager.registerFunction(eventId(SECURE_ALL_GROUPS));

			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* StorageUser implementation
	*******************************************************************************/

	/**
	* Construct a new continer given just ids.
	* @param ref The container reference.
	* @return The new containe Resource.
	*/
	public Entity newContainer(String ref)
	{
		return new BaseAnnouncementChannelEdit(ref);
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Entity newContainer(Element element)
	{
		return new BaseAnnouncementChannelEdit(element);
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Entity newContainer(Entity other)
	{
		return new BaseAnnouncementChannelEdit((MessageChannel) other);
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Entity newResource(Entity container, String id, Object[] others)
	{
		return new BaseAnnouncementMessageEdit((MessageChannel) container, id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Entity newResource(Entity container, Element element)
	{
		return new BaseAnnouncementMessageEdit((MessageChannel) container, element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Entity newResource(Entity container, Entity other)
	{
		return new BaseAnnouncementMessageEdit((MessageChannel) container, (Message) other);
	}

	/**
	* Construct a new continer given just ids.
	* @param ref The container reference.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		BaseAnnouncementChannelEdit rv = new BaseAnnouncementChannelEdit(ref);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element)
	{
		BaseAnnouncementChannelEdit rv = new BaseAnnouncementChannelEdit(element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Entity other)
	{
		BaseAnnouncementChannelEdit rv = new BaseAnnouncementChannelEdit((MessageChannel) other);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Entity container, String id, Object[] others)
	{
		BaseAnnouncementMessageEdit rv = new BaseAnnouncementMessageEdit((MessageChannel) container, id);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Entity container, Element element)
	{
		BaseAnnouncementMessageEdit rv = new BaseAnnouncementMessageEdit((MessageChannel) container, element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Entity container, Entity other)
	{
		BaseAnnouncementMessageEdit rv = new BaseAnnouncementMessageEdit((MessageChannel) container, (Message) other);
		rv.activate();
		return rv;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Entity r)
	{
		Object[] rv = new Object[4];
		rv[0] = ((Message) r).getHeader().getDate();
		rv[1] = ((Message) r).getHeader().getFrom().getId();
		rv[2] = ((AnnouncementMessage) r).getAnnouncementHeader().getDraft() ? "1" : "0";
		rv[3] = r.getProperties().getProperty(ResourceProperties.PROP_PUBVIEW) == null ? "0" : "1";
		// rv[3] = ((AnnouncementMessage) r).getAnnouncementHeader().getAccess() == MessageHeader.MessageAccess.PUBLIC ? "1" : "0";

		return rv;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Entity r)
	{
		return ((AnnouncementMessage)r).getAnnouncementHeader().getDraft();
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Entity r)
	{
		return ((Message)r).getHeader().getFrom().getId();
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Entity r)
	{
		return ((Message)r).getHeader().getDate();
	}

	/*******************************************************************************
	* Abstractions, etc. satisfied
	*******************************************************************************/

	/**
	 * Report the Service API name being implemented.
	 */
	protected String serviceName()
	{
		return AnnouncementService.class.getName();
	}

	/**
	* Construct a new message header from XML in a DOM element.
	* @param id The message Id.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(Message msg, String id)
	{
		return new BaseAnnouncementMessageHeaderEdit(msg, id);

	} // newMessageHeader

	/**
	* Construct a new message header from XML in a DOM element.
	* @param el The XML DOM element that has the header information.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(Message msg, Element el)
	{
		return new BaseAnnouncementMessageHeaderEdit(msg, el);

	} // newMessageHeader

	/**
	* Construct a new message header as a copy of another.
	* @param other The other header to copy.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(Message msg, MessageHeader other)
	{
		return new BaseAnnouncementMessageHeaderEdit(msg, other);

	} // newMessageHeader

	/**
	* Form a tracking event string based on a security function string.
	* @param secure The security function string.
	* @return The event tracking string.
	*/
	protected String eventId(String secure)
	{
		return SECURE_ANNC_ROOT + secure;

	} // eventId

	/**
	* Return the reference rooot for use in resource references and urls.
	* @return The reference rooot for use in resource references and urls.
	*/
	protected String getReferenceRoot()
	{
		return REFERENCE_ROOT;

	} // getReferenceRoot

	/**
	 * {@inheritDoc}
	 */
	public boolean parseEntityReference(String reference, Reference ref)
	{
		if (reference.startsWith(REFERENCE_ROOT))
		{
			String[] parts = StringUtil.split(reference, Entity.SEPARATOR);

			String id = null;
			String subType = null;
			String context = null;
			String container = null;

			// the first part will be null, then next the service, the third will be "msg" or "channel"
			if (parts.length > 2)
			{
				subType = parts[2];
				if (REF_TYPE_CHANNEL.equals(subType) || REF_TYPE_CHANNEL_GROUPS.equals(subType))
				{
					// next is the context id
					if (parts.length > 3)
					{
						context = parts[3];

						// next is the channel id
						if (parts.length > 4)
						{
							id = parts[4];
						}
					}
				}
				else if (REF_TYPE_MESSAGE.equals(subType))
				{
					// next three parts are context, channel (container) and mesage id
					if (parts.length > 5)
					{
						context = parts[3];
						container = parts[4];
						id = parts[5];
					}
				}
				else
					Log.warn("chef", this + "parse(): unknown message subtype: " + subType + " in ref: " + reference);
			}
			
			ref.set(SERVICE_NAME, subType, id, container, context);

			return true;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChange(Site site, EntityProducer.ChangeType change)
	{
		String[] toolIds = {"sakai.announcements"};

		// for a delete, just disable
		if (EntityProducer.ChangeType.REMOVE == change)
		{
			disableMessageChannel(site);
		}
		
		// otherwise enable if we now have the tool, disable otherwise
		else
		{
			// collect the tools from the site
			Collection tools = site.getTools(toolIds);

			// if we have the tool
			if (!tools.isEmpty())
			{
				enableMessageChannel(site);
			}
			
			// if we do not
			else
			{
				disableMessageChannel(site);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpAccess getHttpAccess()
	{
		return new HttpAccess()
		{
			public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws PermissionException, IdUnusedException, ServerOverloadException
			{
				/** Resource bundle using current language locale */
			    final ResourceBundle rb = ResourceBundle.getBundle("access");

				// check security on the message (throws if not permitted)
				unlock(SECURE_READ, ref.getReference());

			    try
				{
					AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
					AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

					res.setContentType("text/html; charset=UTF-8");
					PrintWriter out = res.getWriter();
					out.println("<html><head><style type=" + "\"" + "text/css" + "\"" + ">body{margin:	0px;padding:1em;font-family:Verdana,Arial,Helvetica,sans-serif;font-size:80%;}</style></head><body>");

					// header
					out.println("<p>"+rb.getString("java.from") +" " + Validator.escapeHtml(hdr.getFrom().getDisplayName()) + "<br />");
					out.println(rb.getString("java.date")+" " + Validator.escapeHtml(hdr.getDate().toStringLocalFull()) + "<br />");
					out.println(rb.getString("java.subject")+" " + Validator.escapeHtml(hdr.getSubject()) + "</p>");

					// body
					out.println("<p>" + Validator.escapeHtmlFormattedText(msg.getBody()) + "</p>");

					// attachments
					List attachments = hdr.getAttachments();
					if (attachments.size() > 0)
					{
						out.println("<p>"+rb.getString("java.attach")+"</p><p>");
						for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext(); )
						{
							Reference attachment = (Reference) iAttachments.next();
							out.println("<a href=\"" + Validator.escapeHtml(attachment.getUrl()) + "\">" + Validator.escapeHtml(attachment.getUrl()) + "</a><br />");
						}
						out.println("</p>");
					}

					out.println("</body></html>");
				}
				catch (Throwable t)
				{
					throw new IdUnusedException(ref.getReference());
				}
			}
		};
	}

	/*******************************************************************************
	* AnnouncementService implementation
	*******************************************************************************/

	/**
	* Return a specific announcement channel.
	* @param ref The channel reference.
	* @return the AnnouncementChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for a announcement channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public AnnouncementChannel getAnnouncementChannel(String ref) throws IdUnusedException, PermissionException
	{
		return (AnnouncementChannel) getChannel(ref);

	} // getAnnouncementChannel

	/**
	* Add a new announcement channel.
	* @param ref The channel reference.
	* @return The newly created channel.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public AnnouncementChannelEdit addAnnouncementChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException
	{
		return (AnnouncementChannelEdit) addChannel(ref);

	} // addAnnouncementChannel

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "announcement";
	}
	
	/**
	* import tool(s) contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importEntities(String fromContext, String toContext, List resourceIds)
	{	
		// get the channel associated with this site
		String oChannelRef = channelReference(fromContext, SiteService.MAIN_CONTAINER);
		AnnouncementChannel oChannel = null;
		try
		{
			oChannel = (AnnouncementChannel) getChannel(oChannelRef);
			// the "to" message channel
			String nChannelRef = channelReference(toContext, SiteService.MAIN_CONTAINER);
			AnnouncementChannel nChannel = null;
			try
			{
				nChannel = (AnnouncementChannel) getChannel(nChannelRef);
			}
			catch (IdUnusedException e)
			{
				try
				{
					commitChannel(addChannel(nChannelRef));
					
					try
					{
						nChannel = (AnnouncementChannel) getChannel(nChannelRef);
					}
					catch (Exception eee)
					{
						// ignore
					}
				}
				catch (Exception ee)
				{
					// ignore
				}
			}
			
			if (nChannel != null)
			{
				// pass the DOM to get new message ids, record the mapping from old to new, and adjust attachments
				List oMessageList = oChannel.getMessages(null, true);
				AnnouncementMessage oMessage = null;
				AnnouncementMessageHeader oMessageHeader = null;
				AnnouncementMessageEdit nMessage = null;
				for (int i = 0; i < oMessageList.size(); i++)
				{
					// the "from" message
					oMessage = (AnnouncementMessage) oMessageList.get(i);
					String oMessageId = oMessage.getId();
					
					boolean toBeImported = true;
					if (resourceIds != null && resourceIds.size() > 0)
					{
						// if there is a list for import assignments, only import those assignments and relative submissions
						toBeImported = false;
						for (int m=0; m<resourceIds.size() && !toBeImported; m++)
						{
							if (((String)resourceIds.get(m)).equals(oMessageId))
							{
								toBeImported = true;
							}
						}
					}
					
					if (toBeImported)
					{
						oMessageHeader = (AnnouncementMessageHeaderEdit) oMessage.getHeader();
						ResourceProperties oProperties = oMessage.getProperties();
						
						// the "to" message
						nMessage = (AnnouncementMessageEdit) nChannel.addMessage();
						nMessage.setBody(oMessage.getBody());
						// message header
						AnnouncementMessageHeaderEdit nMessageHeader = (AnnouncementMessageHeaderEdit) nMessage.getHeaderEdit();
						nMessageHeader.setDate(oMessageHeader.getDate());
						// when importing, always mark the announcement message as draft
						nMessageHeader.setDraft(true);
						nMessageHeader.setFrom(oMessageHeader.getFrom());
						nMessageHeader.setSubject(oMessageHeader.getSubject());
						//attachment
						List oAttachments = oMessageHeader.getAttachments();
						List nAttachments = m_entityManager.newReferenceList();
						for (int n=0; n<oAttachments.size(); n++)
						{
							Reference oAttachment = (Reference) oAttachments.get(n);
							String oAttachmentId = ((Reference) oAttachments.get(n)).getId();
							if (oAttachmentId.indexOf(fromContext) !=-1)
							{
								// replace old site id with new site id in attachments
								String nAttachmentId = oAttachmentId.replaceAll(fromContext, toContext);
								try
								{
									ContentResource attachment = ContentHostingService.getResource(nAttachmentId);
									nAttachments.add(m_entityManager.newReference(attachment.getReference()));
								}
								catch (Exception any)
								{
									
								}
								
							}
							else
							{
								nAttachments.add(oAttachment);
							}
						}
						nMessageHeader.replaceAttachments(nAttachments);
						//properties
						ResourcePropertiesEdit p = nMessage.getPropertiesEdit();
						p.clear();
						p.addAll(oProperties);
						
						// complete the edit
						nChannel.commitMessage(nMessage);
					}
				}
				
			}	// if
		}
		catch (IdUnusedException e)
		{
			m_logger.warn(this + " MessageChannel " + fromContext + " cannot be found. ");
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".importResources(): exception in handling " + serviceName() + " : ", any);
		}
	}	// importResources

	/*******************************************************************************
	* AnnouncementChannel implementation
	*******************************************************************************/

	public class BaseAnnouncementChannelEdit extends BaseMessageChannelEdit implements AnnouncementChannelEdit
	{
		/**
		* Construct with a reference.
		* @param ref The channel reference.
		*/
		public BaseAnnouncementChannelEdit(String ref)
		{
			super(ref);

		} // BaseAnnouncementChannelEdit

		/**
		* Construct as a copy  of another message.
		* @param other The other message to copy.
		*/
		public BaseAnnouncementChannelEdit(MessageChannel other)
		{
			super(other);

		} // BaseAnnouncementChannelEdit

		/**
		* Construct from a channel (and possibly messages) already defined
		* in XML in a DOM tree.
		* The Channel is added to storage.
		* @param el The XML DOM element defining the channel.
		*/
		public BaseAnnouncementChannelEdit(Element el)
		{
			super(el);

		} // BaseAnnouncementChannelEdit
 
		/**
		* Return a specific announcement channel message, as specified by message name.
		* @param messageId The id of the message to get.
		* @return the AnnouncementMessage that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this announcement channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		*/
		public AnnouncementMessage getAnnouncementMessage(String messageId) throws IdUnusedException, PermissionException
		{
			AnnouncementMessage msg = (AnnouncementMessage) getMessage(messageId);

			// filter out drafts not by this user (unless this user is a super user or has access_draft ability)
			if ((msg.getAnnouncementHeader()).getDraft()
				&& (!SecurityService.isSuperUser())
				&& (!msg.getHeader().getFrom().getId().equals(SessionManager.getCurrentSessionUserId()))
				&& (!unlockCheck(SECURE_READ_DRAFT, msg.getReference())))
			{
				throw new PermissionException(SECURE_READ, msg.getReference());
			}

			return msg;

		} // getAnnouncementMessage

		/**
		* Return a list of all or filtered messages in the channel.
		* The order in which the messages will be found in the iteration is by date, oldest
		* first if ascending is true, newest first if ascending is false.
		* @param filter A filtering object to accept messages, or null if no filtering is desired.
		* @param ascending Order of messages, ascending if true, descending if false
		* @return a list on channel Message objects or specializations of Message objects (may be empty).
		* @exception PermissionException if the user does not have read permission to the channel.
		*/
		public List getMessages(Filter filter, boolean ascending) throws PermissionException
		{
			// filter out drafts this user cannot see
			filter = new PrivacyFilter(filter);

			return super.getMessages(filter, ascending);

		} // getMessages

		/**
		* A (AnnouncementMessageEdit) cover for editMessage.
		* Return a specific channel message, as specified by message name, locked for update.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @param messageId The id of the message to get.
		* @return the Message that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		* @exception InUseException if the current user does not have permission to mess with this user.
		*/
		public AnnouncementMessageEdit editAnnouncementMessage(String messageId)
			throws IdUnusedException, PermissionException, InUseException
		{
			return (AnnouncementMessageEdit) editMessage(messageId);

		} // editAnnouncementMessage

		/**
		* A (AnnouncementMessageEdit) cover for addMessage.
		* Add a new message to this channel.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @return The newly added message, locked for update.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public AnnouncementMessageEdit addAnnouncementMessage() throws PermissionException
		{
			return (AnnouncementMessageEdit) addMessage();

		} //	addAnnouncementMessage

		/**
		* a (AnnouncementMessage) cover for addMessage to add a new message to this channel.
		* @param subject The message header subject.
		* @param draft The message header draft indication.
		* @param attachments The message header attachments, a vector of Reference objects.
		* @param body The message body.
		* @return The newly added message.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public AnnouncementMessage addAnnouncementMessage(
			String subject,
			boolean draft,
			List attachments,
			String body)
			throws PermissionException
		{
			AnnouncementMessageEdit edit = (AnnouncementMessageEdit) addMessage();
			AnnouncementMessageHeaderEdit header = edit.getAnnouncementHeaderEdit();
			edit.setBody(body);
			header.replaceAttachments(attachments);
			header.setSubject(subject);
			header.setDraft(draft);

			commitMessage(edit);

			return edit;

		} // addAnnouncementMessage

	} // class BaseAnnouncementChannelEdit

	/*******************************************************************************
	* AnnouncementMessage implementation
	*******************************************************************************/

	public class BaseAnnouncementMessageEdit extends BaseMessageEdit implements AnnouncementMessageEdit
	{
		/**
		* Construct.
		* @param channel The channel in which this message lives.
		* @param id The message id.
		*/
		public BaseAnnouncementMessageEdit(MessageChannel channel, String id)
		{
			super(channel, id);

		} // BaseAnnouncementMessageEdit

		/**
		* Construct as a copy  of another message.
		* @param other  The other message to copy.
		*/
		public BaseAnnouncementMessageEdit(MessageChannel channel, Message other)
		{
			super(channel, other);

		} // BaseAnnouncementMessageEdit

		/**
		* Construct from an existing definition, in xml.
		* @param channel The channel in which this message lives.
		* @param el The message in XML in a DOM element.
		*/
		public BaseAnnouncementMessageEdit(MessageChannel channel, Element el)
		{
			super(channel, el);

		} // BaseAnnouncementMessageEdit

		/**
		* Access the announcement message header.
		* @return The announcement message header.
		*/
		public AnnouncementMessageHeader getAnnouncementHeader()
		{
			return (AnnouncementMessageHeader) getHeader();

		} // getAnnouncementHeader

		/**
		* Access the announcement message header.
		* @return The announcement message header.
		*/
		public AnnouncementMessageHeaderEdit getAnnouncementHeaderEdit()
		{
			return (AnnouncementMessageHeaderEdit) getHeader();

		} // getAnnouncementHeaderEdit

	} // class BasicAnnouncementMessageEdit

	/*******************************************************************************
	* AnnouncementMessageHeaderEdit implementation
	*******************************************************************************/

	public class BaseAnnouncementMessageHeaderEdit extends BaseMessageHeaderEdit implements AnnouncementMessageHeaderEdit
	{
		/** The subject for the announcement. */
		protected String m_subject = null;

		/**
		* Construct.
		* @param id The unique (within the channel) message id.
		* @param from The User who sent the message to the channel.
		* @param attachments The message header attachments, a vector of Reference objects.
		*/
		public BaseAnnouncementMessageHeaderEdit(Message msg, String id)
		{
			super(msg, id);

		} // BaseAnnouncementMessageHeaderEdit

		/**
		* Construct, from an already existing XML DOM element.
		* @param el The header in XML in a DOM element.
		*/
		public BaseAnnouncementMessageHeaderEdit(Message msg, Element el)
		{
			super(msg, el);

			// extract the subject
			m_subject = el.getAttribute("subject");

		} // BaseAnnouncementMessageHeaderEdit

		/**
		* Construct as a copy of another header.
		* @param other The other message header to copy.
		*/
		public BaseAnnouncementMessageHeaderEdit(Message msg, MessageHeader other)
		{
			super(msg, other);

			m_subject = ((AnnouncementMessageHeader) other).getSubject();

		} // BaseAnnouncementMessageHeaderEdit

		/**
		* Access the subject of the announcement.
		* @return The subject of the announcement.
		*/
		public String getSubject()
		{
			return ((m_subject == null) ? "" : m_subject);

		} // getSubject

		/**
		* Set the subject of the announcement.
		* @param subject The subject of the announcement.
		*/
		public void setSubject(String subject)
		{
			if (StringUtil.different(subject, m_subject))
			{
				m_subject = subject;
			}

		} // setSubject

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			// get the basic work done
			Element header = super.toXml(doc, stack);

			// add draft, subject
			header.setAttribute("subject", getSubject());
			header.setAttribute("draft", new Boolean(getDraft()).toString());

			return header;

		} // toXml

	} // BaseAnnouncementMessageHeader

	/**
	* A filter that will reject announcement message drafts not from the current user, and otherwise
	* use another filter, if defined, for acceptance.
	*/
	protected class PrivacyFilter implements Filter
	{
		/** The other filter to check with.  May be null. */
		protected Filter m_filter = null;

		/**
		* Construct
		* @param filter The other filter we check with.
		*/
		public PrivacyFilter(Filter filter)
		{
			m_filter = filter;

		} //PrivacyFilter

		/**
		* Does this object satisfy the criteria of the filter?
		* @return true if the object is accepted by the filter, false if not.
		*/
		public boolean accept(Object o)
		{
			// first if o is a announcement message that's a draft from another user, reject it
			if (o instanceof AnnouncementMessage)
			{
				AnnouncementMessage msg = (AnnouncementMessage) o;

				if ((msg.getAnnouncementHeader()).getDraft()
					&& (!SecurityService.isSuperUser())
					&& (!msg.getHeader().getFrom().getId().equals(SessionManager.getCurrentSessionUserId()))
					&& (!unlockCheck(SECURE_READ_DRAFT, msg.getReference())))
				{
					return false;
				}
			}

			// now, use the real filter, if present
			if (m_filter != null)
				return m_filter.accept(o);

			return true;

		} // accept

	} // PrivacyFilter

} // BaseAnnouncementService


