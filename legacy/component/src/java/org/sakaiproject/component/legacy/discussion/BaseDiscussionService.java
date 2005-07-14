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
package org.sakaiproject.component.legacy.discussion;

// import
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.component.legacy.message.BaseMessageService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.discussion.DiscussionChannel;
import org.sakaiproject.service.legacy.discussion.DiscussionChannelEdit;
import org.sakaiproject.service.legacy.discussion.DiscussionMessage;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageEdit;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageHeader;
import org.sakaiproject.service.legacy.discussion.DiscussionMessageHeaderEdit;
import org.sakaiproject.service.legacy.discussion.DiscussionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageHeader;
import org.sakaiproject.service.legacy.message.MessageHeaderEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.Filter;
import org.sakaiproject.util.java.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseDiscussionService extends the BaseMessageService for the specifics of Discussion.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*
* Note: when a message is posted to a channel, it may have a new category.  This category is NOT automatically added to the
* categories of a channel, which may exist without a message.  To get the full list of categories, you must get the channel's
* categories, and add any that the set of messages for the channel has.
*/
public abstract class BaseDiscussionService
	extends BaseMessageService
	implements DiscussionService
{
	/*******************************************************************************
	* StorageUser implementation
	*******************************************************************************/

	/**
	* Construct a new continer given just ids.
	* @param ref The channel reference.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref)
	{
		return new BaseDiscussionChannelEdit(ref);
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element)
	{
		return new BaseDiscussionChannelEdit(element);
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other)
	{
		return new BaseDiscussionChannelEdit((MessageChannel) other);
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{
		return new BaseDiscussionMessageEdit((MessageChannel) container, id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{
		return new BaseDiscussionMessageEdit((MessageChannel) container, element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{
		return new BaseDiscussionMessageEdit((MessageChannel) container, (Message) other);
	}

	/**
	* Construct a new continer given just ids.
	* @param ref The channel reference.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		BaseDiscussionChannelEdit rv = new BaseDiscussionChannelEdit(ref);
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
		BaseDiscussionChannelEdit rv = new BaseDiscussionChannelEdit(element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Resource other)
	{
		BaseDiscussionChannelEdit rv = new BaseDiscussionChannelEdit((MessageChannel) other);
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
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseDiscussionMessageEdit rv = new BaseDiscussionMessageEdit((MessageChannel) container, id);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseDiscussionMessageEdit rv = new BaseDiscussionMessageEdit((MessageChannel) container, element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseDiscussionMessageEdit rv = new BaseDiscussionMessageEdit((MessageChannel) container, (Message) other);
		rv.activate();
		return rv;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r)
	{
		Object[] rv = new Object[6];
		rv[0] = ((Message) r).getHeader().getDate();
		rv[1] = ((Message) r).getHeader().getFrom().getId();
		rv[2] = ((DiscussionMessage) r).getDiscussionHeader().getDraft() ? "1" : "0";
		rv[3] = r.getProperties().getProperty(ResourceProperties.PROP_PUBVIEW) == null ? "0" : "1";
		rv[4] = ((DiscussionMessage) r).getDiscussionHeader().getCategory();
		rv[5] = ((DiscussionMessage) r).getDiscussionHeader().getReplyTo();
		if (rv[5] == null) rv[5] = "";

		return rv;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Resource r)
	{
		return ((DiscussionMessage)r).getDiscussionHeader().getDraft();
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Resource r)
	{
		return ((Message)r).getHeader().getFrom().getId();
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Resource r)
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
		return DiscussionService.class.getName();
	}

	/**
	* Construct a new message header from XML in a DOM element.
	* @param id The message Id.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(String id)
	{
		return new BaseDiscussionMessageHeaderEdit(id);

	}	// newMessageHeader

	/**
	* Construct a new message header from XML in a DOM element.
	* @param el The XML DOM element that has the header information.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(Element el)
	{
		return new BaseDiscussionMessageHeaderEdit(el);

	}	// newMessageHeader

	/**
	* Construct a new message header as a copy of another.
	* @param other The other header to copy.
	* @return The new message header.
	*/
	protected MessageHeaderEdit newMessageHeader(MessageHeader other)
	{
		return new BaseDiscussionMessageHeaderEdit(other);

	}	// newMessageHeader

	/**
	* Form a tracking event string based on a security function string.
	* @param secure The security function string.
	* @return The event tracking string.
	*/
	protected String eventId(String secure)
	{
		return "disc." + secure;

	}	// eventId

	/**
	* Return the reference rooot for use in resource references and urls.
	* @return The reference rooot for use in resource references and urls.
	*/
	protected String getReferenceRoot()
	{
		return REFERENCE_ROOT;

	}	// getReferenceRoot

	/*******************************************************************************
	* DiscussionService implementation
	*******************************************************************************/

	/**
	* Return a specific discussion channel.
	* @param ref The channel reference.
	* @return the DiscussionChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for a discussion channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public DiscussionChannel getDiscussionChannel(String ref)
		throws IdUnusedException, PermissionException
	{
		return (DiscussionChannel) getChannel(ref);

	}	// getDiscussionChannel

	/**
	* Add a new discussion channel.
	* @param ref The channel reference.
	* @return The newly created channel.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public DiscussionChannelEdit addDiscussionChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException
	{
		return (DiscussionChannelEdit) addChannel(ref);

	}	// addDiscussionChannel
	
	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, ReferenceVector attachments)
	{
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// start with an element with our very own (service) name
		Element element = doc.createElement(serviceName());
		((Element)stack.peek()).appendChild(element);
		stack.push(element);

		// get the channel associated with this site
		String channelRef = channelReference(siteId, SiteService.MAIN_CONTAINER);

		results.append("archiving " + getLabel() + " channel " + channelRef + ".\n");

		try
		{
			// do the channel
			MessageChannel channel = getChannel(channelRef);
			Element containerElement = channel.toXml(doc, stack);
			stack.push(containerElement);

			// do the messages in the channel
			Iterator messages = channel.getMessages(null, true).iterator();
			while (messages.hasNext())
			{
				DiscussionMessage msg = (DiscussionMessage) messages.next();
				Element el = msg.toXml(doc, stack);
				
				int depth = msg.getReplyToDepth();
				NodeList children2 = el.getChildNodes();
				int length2 = children2.getLength();
				for (int i2 = 0; i2 < length2; i2++)
				{
					Node child2 = children2.item(i2);
					if (child2.getNodeType() == Node.ELEMENT_NODE)
					{
						Element element2 = (Element)child2;

						// get the "calendar" child
						if (element2.getTagName().equals("header"))
						{
							element2.setAttribute("depth", (new Integer(depth)).toString());
						}
					} // if
				} // for
		
				// collect message attachments
				MessageHeader header = msg.getHeader();
				ReferenceVector atts = header.getAttachments();
				for (int i = 0; i < atts.size(); i++)
				{
					Reference ref = (Reference) atts.elementAt(i);
					// if it's in the attachment area, and not already in the list
					if (	(ref.getReference().startsWith("/content/attachment/"))
						&&	(!attachments.contains(ref)))
					{
						attachments.add(ref);
					}
				}
			}
			
			stack.pop();
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".archve: exception archiving messages for service: "
						+ serviceName() + " channel: "
						+ channelRef);
		}

		stack.pop();

		return results.toString();
	} // archive

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "discussion";
	}

	/**
	* import tool(s) contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importResources(String fromContext, String toContext, List resourceIds)
	{	
		// get the channel associated with this site
		String oChannelRef = channelReference(fromContext, SiteService.MAIN_CONTAINER);
		DiscussionChannel oChannel = null;
		try
		{
			oChannel = getDiscussionChannel(oChannelRef);
			
			// the "to" DiscussionMessage channel
			DiscussionChannel nChannel = null;
			String nChannelRef = channelReference(toContext, SiteService.MAIN_CONTAINER);
			try
			{
				nChannel = (DiscussionChannel) getChannel(nChannelRef);
			}
			catch (IdUnusedException e)
			{
				try
				{
					commitChannel(addDiscussionChannel(nChannelRef));
					
					try
					{
						nChannel = (DiscussionChannel) getChannel(nChannelRef);
					}
					catch (IdUnusedException eee)
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
				// categories
				List oCategories = oChannel.getCategories(true);
				
				for (Iterator it = oCategories.iterator(); it.hasNext(); )
				{
					String category = (String) it.next();
					
					nChannel.addCategory(category);
					
					// only importing topic messages and mark them as draft 
					Iterator oMessageList = oChannel.getTopics(category);
					DiscussionMessage oMessage = null;
					DiscussionMessageHeader oMessageHeader = null;
					DiscussionMessageEdit nMessage = null;
					for (; oMessageList.hasNext();)
					{
						// the "from" message
						oMessage = (DiscussionMessage) oMessageList.next();
						oMessageHeader = oMessage.getDiscussionHeader();
						ResourceProperties oProperties = oMessage.getProperties();
						
						// the "to" message
						nMessage = (DiscussionMessageEdit) nChannel.addMessage();
						nMessage.setBody(oMessage.getBody());
						DiscussionMessageHeaderEdit nMessageHeader = nMessage.getDiscussionHeaderEdit();
						nMessageHeader.setDate(oMessageHeader.getDate());
						nMessageHeader.setDraft(true);
						nMessageHeader.setFrom(oMessageHeader.getFrom());
						nMessageHeader.setSubject(oMessageHeader.getSubject());
						nMessageHeader.setCategory(category);
						
						// attachment
						nMessageHeader.replaceAttachments(oMessageHeader.getAttachments());
						//properties
						ResourcePropertiesEdit p = nMessage.getPropertiesEdit();
						p.clear();
						p.addAll(oProperties);
						
						nChannel.commitMessage(nMessage, NotificationService.NOTI_NONE);
					}	// for
				}	// for
			}	// if 
		}
		catch (IdUnusedException e)
		{
			m_logger.warn(this + " DiscussionChannel " + fromContext + " cannot be found. ");
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".importResources(): exception in handling " + serviceName() + " : ", any);
		}

	}	// importResources
	
	/*******************************************************************************
	* DiscussionChannel implementation
	*******************************************************************************/

	public class BaseDiscussionChannelEdit
		extends BaseMessageChannelEdit
		implements DiscussionChannelEdit
	{
		/** Store categories, each a string. */
		protected List m_categories = new Vector();

		/**
		* Construct with a reference.
		* @param ref The channel reference.
		* @param context The context in which this channel will live.
		* @param id The unique channel id.
		*/
		public BaseDiscussionChannelEdit(String ref)
		{
			super(ref);

		}	// BaseDiscussionChannelEdit

		/**
		* Construct as a copy  of another message.
		* @param other  The other message to copy.
		*/
		public BaseDiscussionChannelEdit(MessageChannel other)
		{
			super(other);
			
			// add the categories
			m_categories.addAll(((BaseDiscussionChannelEdit) other).m_categories);

		}	// BaseDiscussionChannelEdit

		/**
		* Construct from a channel (and possibly messages) already defined
		* in XML in a DOM tree.
		* The Channel is added to storage.
		* @param el The XML DOM element defining the channel.
		*/
		public BaseDiscussionChannelEdit(Element el)
		{
			super(el);

			// extract the categories from the XML
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
				Element element = (Element)child;

				// look for the categories element
				if (!element.getTagName().equals("categories")) continue;

				NodeList categories = element.getChildNodes();
				for (int j = 0; j < categories.getLength(); j++)
				{
					Node category = categories.item(j);
					if (category.getNodeType() != Node.ELEMENT_NODE) continue;

					Element eCategory = (Element) category;
					if (!eCategory.getTagName ().equals("category")) continue;
					
					String cat = eCategory.getAttribute("name");
					if ((cat == null) || (cat.length() == 0)) continue;

					// keep this if we don't already have it
					if (!m_categories.contains(cat)) m_categories.add(cat);
				}
			}						
			
			// Note: remove this once 1.0.6 CHEF is no longer supported.
			// Look for the property style categories
			String categories = getProperties().getProperty(ResourceProperties.PROP_DISCUSSION_CATEGORIES);
			if ((categories != null) && (categories.length() > 0))
			{
				// these used a "," to separate
				String[] cats = StringUtil.split(categories,",");
				for (int i = 0; i < cats.length; i++)
				{
					// keep this if we don't already have it
					if (	(cats[i] != null) && (cats[i].length() > 0)
						&&	(!m_categories.contains(cats[i])))
					{
						m_categories.add(cats[i]);
					}
				}
				
				// clear the property
				getPropertiesEdit().removeProperty(ResourceProperties.PROP_DISCUSSION_CATEGORIES);
			}

		}	// BaseDiscussionChannelEdit

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			// get the basic work done
			Element channel = super.toXml(doc, stack);

			// add categories
			Element categories = doc.createElement("categories");
			channel.appendChild(categories);

			for (int i = 0; i < m_categories.size(); i++)
			{
				Element cElement = doc.createElement("category");
				categories.appendChild(cElement);
				cElement.setAttribute("name", (String) m_categories.get(i));				
			}
			
			return channel;

		}	// toXml

		/**
		* Return a specific discussion channel message, as specified by message name.
		* @param messageId The id of the message to get.
		* @return the DiscussionMessage that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this discussion channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		*/
		public DiscussionMessage getDiscussionMessage(String messageId)
			throws IdUnusedException, PermissionException
		{
			DiscussionMessage msg = (DiscussionMessage) getMessage(messageId);

			// filter out drafts not by this user (unless this user is a super user or has access_draft ability)
			if (	(msg.getDiscussionHeader()).getDraft()
				&&	(!SecurityService.isSuperUser())
				&&	(!msg.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
				&&	(!unlockCheck(SECURE_READ_DRAFT, msg.getReference())))
			{
				throw new PermissionException(UsageSessionService.getSessionUserId(), SECURE_READ, msg.getReference());
			}

			return msg;

		}	// getDiscussionMessage

		/**
		* @inheritDoc
		*/
		public DiscussionMessage getDiscussionMessageNoException(String messageId)
		{
			DiscussionMessage msg = null;
			try
			{
				msg = getDiscussionMessage(messageId);
			}
			catch (Exception ignore) {}
			
			return msg;
			
		}	// getOneMessage
		
		/**
		* Return a list of all or filtered messages in the channel.
		* The order in which the messages will be found in the iteration is by date, oldest
		* first if ascending is true, newest first if ascending is false.
		* @param filter A filtering object to accept messages, or null if no filtering is desired.
		* @param ascending Order of messages, ascending if true, descending if false
		* @return a list on channel Message objects or specializations of Message objects (may be empty).
		* @exception PermissionException if the user does not have read permission to the channel.
		*/
		public List getMessages(Filter filter, boolean ascending)
			throws PermissionException
		{
			// filter out drafts this user cannot see
			filter = new PrivacyFilter(filter);
			
			return super.getMessages(filter, ascending);

		}	// getMessages

		/**
		* A (DiscussionMessageEdit) cover for editMessage.
		* Return a specific channel message, as specified by message name, locked for update.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @param messageId The id of the message to get.
		* @return the Message that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		* @exception InUseException if the current user does not have permission to mess with this user.
		*/
		public DiscussionMessageEdit editDiscussionMessage(String messageId)
			throws IdUnusedException, PermissionException, InUseException
		{
			return (DiscussionMessageEdit) editMessage(messageId);

		}	// editDiscussionMessage

		/**
		* check permissions for addMessage() - if the message is a new Topic.
		* @return true if the user is allowed to addMessage(...) for a new Topic, false if not.
		*/
		public boolean allowAddTopicMessage()
		{
			// check security (throws if not permitted)
			return unlockCheck(SECURE_ADD_TOPIC, getReference());

		}	// allowAddTopicMessage
		
		/**
		* check permissions for removing category
		* @return true if the user is allowed to remove category, false if not.
		*/
		public boolean allowRemoveCategory()
		{
			// check security (throws if not permitted)
			return unlockCheck(SECURE_REMOVE_ANY, getReference());

		}	// allowRemoveCategory

		/**
		* A (DiscussionMessageEdit) cover for addMessage.
		* Add a new message to this channel.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @param replyTo The message id to which this message is a reply.
		* @return The newly added message, locked for update.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public DiscussionMessageEdit addDiscussionMessage(String replyTo)
			throws PermissionException
		{
			// reject if not permitted to post a new Topic, if this is a new Topic
			// (if it has a null or blank replyTo, it's a Topic)
			if ((replyTo == null) || (replyTo.trim().length() == 0))
			{
				unlock(SECURE_ADD_TOPIC, getReference());
			}

			DiscussionMessageEdit edit = (DiscussionMessageEdit) addMessage();
			edit.getDiscussionHeaderEdit().setReplyTo(replyTo);

			return edit;

		}	//	addDiscussionMessage

		/**
		* A (DiscussionMessage) cover for addMessage to add a new message to this channel.
		* @param category The message header category.
		* @param subject The message header subject.
		* @param draft The message header draft setting.
		* @param replyTo The message header message id to which this is a reply.
		* @param attachments The message header attachments, a vector of Reference objects.
		* @param body The message body.
		* @return The newly added message.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public DiscussionMessage addDiscussionMessage(String category, String subject, boolean draft,
					String replyTo, ReferenceVector attachments, String body)
			throws PermissionException
		{
			DiscussionMessageEdit edit = (DiscussionMessageEdit) addMessage();
			DiscussionMessageHeaderEdit header = edit.getDiscussionHeaderEdit();
			edit.setBody(body);
			header.replaceAttachments(attachments);
			header.setSubject(subject);
			header.setDraft(draft);
			header.setReplyTo(replyTo);
			header.setCategory(category);

			commitMessage(edit);

			return edit;

		}	// addDiscussionMessage

		/**
		 * {@inheritDoc}
		 */
		public void setCategories(List categories)
		{
			m_categories = categories;
			
		}   // setCategories
		
		/**
		 * {@inheritDoc}
		 */
		public List getCategories(boolean scanMessages)
		{
			Set categories = new HashSet();
			Vector rv = new Vector();
			
			// start with those stored in the category
			categories.addAll(m_categories);

			// add in the category from each message in the channel if requested
			if (scanMessages)
			{
				Iterator it = findMessages().iterator();
				while (it.hasNext())
				{
					DiscussionMessage msg = (DiscussionMessage) it.next();
					categories.add(msg.getDiscussionHeader().getCategory());
				}
			}

			rv.addAll(categories);
			return rv;

		}   // getCategories
        
		/**
		* Add a new category for this channel.
		* @param category The new category string to add.
		* @return true if the category was new, false if it is already there.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public boolean addCategory(String category)
			throws PermissionException, InUseException
		{

			if (	(category != null) && (category.length() > 0)
				&&	(!m_categories.contains(category)))
			{
				// in-storage modification
				DiscussionChannelEdit e = (DiscussionChannelEdit) m_storage.editChannel(this.getReference());
					
				if (e != null)
				{
					List l = e.getCategories(true);
					l.add(category);
					e.setCategories(l);
					m_storage.commitChannel(e);
					
					//track event (no notification)
					Event event = EventTrackingService.newEvent(eventId(((BaseDiscussionChannelEdit) e).getEvent()), e.getReference(), true, NotificationService.NOTI_NONE);
					EventTrackingService.post(event);
					//channel notification
					notify(event);
					// close the edit object
					((BaseDiscussionChannelEdit) e).closeEdit();
					
					return true;
				}
				else
				{
					throw new InUseException(this.getReference());
				}
			}

			return false;

		}	// addCategory
		
		/**
		* Remove a category and its related messages for this channel.
		* @param category The category string to be removed.
		* @return true if the category can be removed, false if not.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public boolean removeCategory(String category)
			throws InUseException, PermissionException
		{
			unlock(SECURE_REMOVE_ANY, getReference());
			try 
			{
				DiscussionChannelEdit e = (DiscussionChannelEdit) m_storage.editChannel(this.getReference());
				
				if (e != null)
				{
					List l = e.getCategories(true);
					if (	(category != null) && (category.length() > 0)
						&&	(l.contains(category)))
					{
						//delete all the messages in the category
						Iterator messages = m_storage.getMessages(e).iterator();
						while (messages.hasNext())
						{
							DiscussionMessage m = (DiscussionMessage) messages.next();
							if (m.getDiscussionHeader().getCategory().equals(category))
							{
								e.removeMessage(m.getId());
							}
						}
					
						l.remove(category);
						e.setCategories(l);
					
						m_storage.commitChannel(e);
							
						//track event (no notification)
						Event event = EventTrackingService.newEvent(eventId(((BaseDiscussionChannelEdit) e).getEvent()), e.getReference(), true, NotificationService.NOTI_NONE);
						EventTrackingService.post(event);
						//channel notification
						notify(event);
							
						// close the edit object
						((BaseDiscussionChannelEdit) e).closeEdit();
						return true;
					}
					else
					{
						// close the edit object
						m_storage.commitChannel(e);
						((BaseDiscussionChannelEdit) e).closeEdit();
						return false;
					}
				}
				else
				{
					throw new InUseException(this.getReference());
				}
			}
			catch (Exception e){}

			return false;

		}	// removeCategory

		/**
		* Return an Iterator on the DiscussionMessages that are "topics", i.e.
		* are not a response to any other message.
		* Note: use DiscussionMessage.getReplies() to get the direct replies to any
		* specific message.
		* @param category Filter the responses to messages in just this category (optional, may be null).
		* @return an Iterator on the DiscussionMessages that are "topics", i.e.
		* are not a response to any other message.
		*/
		public Iterator getTopics(String category)
		{
			// TODO: storage interface to get this without filtering
			final String catToMatch = category;
			Filter filter = new
				Filter()
				{
					public boolean accept(Object o)
					{
						DiscussionMessage message = (DiscussionMessage)o;
						if ((message.getDiscussionHeader().getReplyTo().length() == 0)
							&&  (   (catToMatch == null)
									|| (catToMatch.length() == 0)
									|| (catToMatch.equals(((DiscussionMessageHeader)message.getHeader()).getCategory())))
							&&	( 	(!message.getDiscussionHeader().getDraft())
									|| (SecurityService.isSuperUser())
									|| (message.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
									|| (unlockCheck(SECURE_READ_DRAFT, message.getReference()))
								)
							)
						{
							return true;
						}
						return false;
					}
				};

			return findFilterMessages(filter, true).iterator();

		}   // getTopics

		/**
		 * {@inheritDoc}
		 */
		public List getTopicMsgIds(String category)
		{
			// TODO: storage interface so get just the ids!
			final String catToMatch = category;
			Filter filter = new
				Filter()
				{
					public boolean accept(Object o)
					{
						DiscussionMessage message = (DiscussionMessage)o;
						if ((message.getDiscussionHeader().getReplyTo().length() == 0)
							&&  (   (catToMatch == null)
									|| (catToMatch.length() == 0)
									|| (catToMatch.equals(((DiscussionMessageHeader)message.getHeader()).getCategory())))
							&&	( 	(!message.getDiscussionHeader().getDraft())
									|| (SecurityService.isSuperUser())
									|| (message.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
									|| (unlockCheck(SECURE_READ_DRAFT, message.getReference()))
								)
							)
						{
							return true;
						}
						return false;
					}
				};

			List found = findFilterMessages(filter, true);
			Vector rv = new Vector();
			for (Iterator i = found.iterator(); i.hasNext();)
			{
				Message msg = (Message) i.next();
				rv.add(msg.getId());
			}
			
			return rv;
		}

		/**
		* Return an iterator on the DiscussionMessages that are in the response thread(s)
		* to the specified iteration of messages.  They will be returned in depth first order.
		* @param topLevel The iterator of messages to which those returned are some depth of response.
		* @return an iterator on the DiscussionMessages that are in the response threads
		* to the specified iteration of messages, in depth first order (may be empty).
		*/
		protected Iterator threadIterator(Iterator topLevel)
		{
			// start the depth iteration stack with the topics of the channel
			final Stack stack = new Stack();
			stack.push(topLevel);

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
						while (	 (!m_stack.empty())
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
							Iterator replies = ((DiscussionMessage)m_next).getReplies();
							if (replies.hasNext())
							{
								m_stack.push(replies);
							}
						}
						
						return true;

					}   // hasNext
	
					public void remove() {throw new UnsupportedOperationException();}
	
				};  // Iterator

		}   // threadIterator

		/**
		* Return an iterator on the DiscussionMessages that are in the response thread
		* to the specified message.  They will be returned in depth first order.
		* @param message The message to which those returned are some depth of response.
		* @return an iterator on the DiscussionMessages that are in the response thread
		* to the specified topic messages, in depth first order (may be empty).
		*/
		public Iterator getThread(DiscussionMessage message)
		{
			// the top level of the threading is the replies to the specified message
			Iterator topLevel = message.getReplies();
			return threadIterator(topLevel);

		}   // getThread

		/**
		* Return an iterator on all messages in thread - depth first order.
		* @param category Filter the responses to messages in just this category (optional, may be null).
		* @return an iterator on all messages in thread - depth first order
		* (may be empty).
		*/
		public Iterator getThreads(String category)
		{
			// the top level of the threading is the topics that are in category
			Iterator topLevel = getTopics(category);
			return threadIterator(topLevel);

		}   // getThreads

		/**
		* Access the most recent reply to the thread descending from this message, if any.
		* @param message The message that forms the top of a thread.
		* @return the most recent DiscussionMessage reply to this thread, or null if there are no replies.
		*/
		public DiscussionMessage getThreadLatestReply(DiscussionMessage message)
		{
			DiscussionMessage latest = null;
			Iterator replies = getThread(message);
			while (replies.hasNext())
			{
				if (latest == null)
				{
					latest = (DiscussionMessage)replies.next();
				}
				else
				{
					DiscussionMessage test = (DiscussionMessage)replies.next();
					if (test.getHeader().getDate().after(latest.getHeader().getDate()))
					{
						latest = test;
					}
				}
			}

			return latest;

		}	// getThreadLatestReply
	
		/**
		* Count the number of replies to this thread (i.e. the size of the getThread() iterator).
		* @param message The message that forms the top of a thread.
		* @return the number of replies to this thread.
		*/
		public int getThreadNumberOfReplies(DiscussionMessage message)
		{
			int count = 0;
			Iterator replies = getThread(message);
			while (replies.hasNext())
			{
				replies.next();
				count++;
			}

			return count;

		}	// getThreadNumberOfReplies
		
	}	// class BaseDiscussionChannelEdit

	/*******************************************************************************
	* DiscussionMessage implementation
	*******************************************************************************/

	public class BaseDiscussionMessageEdit
		extends BaseMessageEdit
		implements DiscussionMessageEdit
	{
		/**
		* Construct.
		* @param channel The channel in which this message lives.
		* @param id The message id.
		*/
		public BaseDiscussionMessageEdit(MessageChannel channel, String id)
		{
			super(channel, id);

		}	// BaseDiscussionMessageEdit

		/**
		* Construct as a copy  of another message.
		* @param other  The other message to copy.
		*/
		public BaseDiscussionMessageEdit(MessageChannel channel, Message other)
		{
			super(channel, other);

		}	// BaseDiscussionMessageEdit

		/**
		* Construct from an existing definition, in xml.
		* @param channel The channel in which this message lives.
		* @param el The message in XML in a DOM element.
		*/
		public BaseDiscussionMessageEdit(MessageChannel channel, Element el)
		{
			super(channel, el);

		}	// BaseDiscussionMessageEdit

		/**
		* Access the discussion message header.
		* @return The discussion message header.
		*/
		public DiscussionMessageHeader getDiscussionHeader()
		{
			return (DiscussionMessageHeader)getHeader();

		}	// getDiscussionHeader

		/**
		* Access the discussion message header.
		* @return The discussion message header.
		*/
		public DiscussionMessageHeaderEdit getDiscussionHeaderEdit()
		{
			return (DiscussionMessageHeaderEdit)getHeader();

		}	// getDiscussionHeaderEdit

		/**
		* Return an array of messages that are a reply to this message.
		* @return an array of DiscussionMessage objects that are replies to this message
		* (may be null).
		*/
		public Iterator getReplies()
		{
			// compare others' replyTo to my local id
			final String id = getId();

			Filter filter = new
				Filter()
				{
					public boolean accept(Object o)
					{
						DiscussionMessage message = (DiscussionMessage)o;
						if (	(message.getDiscussionHeader().getReplyTo().equals(id))
							&&	( 	(!message.getDiscussionHeader().getDraft())
									|| (SecurityService.isSuperUser())
									|| (message.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
									|| (unlockCheck(SECURE_READ_DRAFT, message.getReference()))
								)
							)
						{
							return true;
						}
						return false;
					}
				};

			// ask m_channel for a filtered message iterator
			return ((BaseMessageChannelEdit)m_channel).findFilterMessages(filter, true).iterator();

		}   // getReplies
	
		/**
		* Are there any replys to this message?
		* @return true if there are replies to this message, false if now.
		*/
		public boolean hasReplies()
		{
			return getReplies().hasNext();

		}   // hasReplies

		/**
		* Return the depth of replyTo value for this message.
		* Messages that are not a reply to any other message have depth = 0.
		* If the message is a reply to a message, this message's depth is the replyTo's depth + 1.
		* @return The depth of replyTo value for this message.
		*/
		public int getReplyToDepth()
		{
			return getReplyToDepth(new HashSet());

		}	// getReplyToDepth

		/**
		* Return the depth of replyTo value for this message.
		* Messages that are not a reply to any other message have depth = 0.
		* @param messages A set of messages seen so far in the response chain.
		* If the message is a reply to a message, this message's depth is the replyTo's depth + 1.
		* @return The depth of replyTo value for this message.
		*/
		protected int getReplyToDepth(HashSet messages)
		{
			// if not a reply to, return 0
			if (getDiscussionHeader().getReplyTo().length() == 0)
			{
				return 0;
			}

			// if I am alreay in the reply chain due to some sort of looping, stop here!
			if (messages.contains(this))
			{
				m_logger.warn(this + ".getReplyToDepth(): looping found: " + getId());
				return 0;
			}

			try
			{
				// add this message to the set of those in the reply chain
				messages.add(this);
				
				// if a reply to, add one to the replyTo's depth
				BaseDiscussionMessageEdit replyTo = (BaseDiscussionMessageEdit) ((DiscussionChannel)m_channel)
									.getDiscussionMessage(getDiscussionHeader().getReplyTo());
				return replyTo.getReplyToDepth(messages) + 1;
			}
			catch (IdUnusedException e)
			{
				m_logger.warn(this + ".getReplyToDepth(): exception: " + e.toString());
				return 0;
			}
			catch (PermissionException e)
			{
				m_logger.warn(this + ".getReplyToDepth(): exception: " + e.toString());
				return 0;
			}

		}   // getReplyToDepth

		/**
		* Access the most recent reply to this message, if any.
		* @return the most recent DiscussionMessage reply to this message, or null if there are no replies.
		*/
		public DiscussionMessage getLatestReply()
		{
			DiscussionMessage latest = null;
			Iterator replies = getReplies();
			while (replies.hasNext())
			{
				latest = (DiscussionMessage)replies.next();
			}

			return latest;

		}	// getLatestReply
	
		/**
		* Count the number of replies to this message (i.e. the size of the getReplies iterator).
		* @return the number of replies to this message.
		*/
		public int getNumberOfReplies()
		{
			int count = 0;
			Iterator replies = getReplies();
			while (replies.hasNext())
			{
				replies.next();
				count++;
			}

			return count;

		}	// getNumberOfReplies

	}	// class BasicDiscussionMessageEdit

	/*******************************************************************************
	* DiscussionMessageHeaderEdit implementation
	*******************************************************************************/

	public class BaseDiscussionMessageHeaderEdit
		extends BaseMessageHeaderEdit
		implements DiscussionMessageHeaderEdit
	{
		/** The subject for the discussion. */
		protected String m_subject = null;

		/** The category for the discussion message. */
		protected String m_category = null;

		/** The message id to which this message is a reply to (message id string). */
		protected String m_replyTo = null;

		/**
		* Construct.
		* @param id The unique (within the channel) message id.
		* @param from The User who sent the message to the channel.
		* @param attachments The message header attachments, a vector of Reference objects.
		*/
		public BaseDiscussionMessageHeaderEdit(String id)
		{
			super(id);

		}	// BaseDiscussionMessageHeaderEdit

		/**
		* Construct, from an already existing XML DOM element.
		* @param el The header in XML in a DOM element.
		*/
		public BaseDiscussionMessageHeaderEdit(Element el)
		{
			super(el);
			
			// now extract the subject, draft, category and replyTo
			m_subject = el.getAttribute("subject");
			m_category = el.getAttribute("category");
			m_replyTo = el.getAttribute("replyTo");

		}	// BaseDiscussionMessageHeaderEdit

		/**
		* Construct as a copy of another header.
		* @param other The other message header to copy.
		*/
		public BaseDiscussionMessageHeaderEdit(MessageHeader other)
		{
			super(other);
			
			m_subject = ((DiscussionMessageHeader) other).getSubject();
			m_category = ((DiscussionMessageHeader) other).getCategory();
			m_replyTo = ((DiscussionMessageHeader) other).getReplyTo();

		}	// BaseDiscussionMessageHeaderEdit

		/**
		* Access the subject of the discussion.
		* @return The subject of the discussion.
		*/
		public String getSubject()
		{
			return ((m_subject == null) ? "" : m_subject);

		}	// getSubject

		/**
		* Set the subject of the discussion.
		* @param subject The subject of the discussion.
		*/
		public void setSubject(String subject)
		{
			if (StringUtil.different(subject, m_subject))
			{
				m_subject = subject;
			}

		}	// setSubject

		/**
		* Access the category of the discussion.
		* @return The category of the discussion.
		*/
		public String getCategory()
		{
			return ((m_category == null) ? "" : m_category);

		}   // getCategory

		/**
		* Set the category of the discussion.
		* @param category The category of the discussion.
		*/
		public void setCategory(String category)
		{
			if (StringUtil.different(category, m_category))
			{
				m_category = category;
			}

		}   // setCategory

		/**
		* Access the local or resource id of the message this one is a reply to, used in threading.
		* @return The id of the message this one is a reply to, used in threading, or null if none.
		*/
		public String getReplyTo()
		{
			return ((m_replyTo == null) ? "" : m_replyTo);

		}   // getReplyTo

		/**
		* Set the local or resource id of the message this one is a reply to, used in threading.
		* @param id The local or resource id of the message this one is a reply to, used in threading.
		*/
		public void setReplyTo(String id)
		{
			if (StringUtil.different(id, m_replyTo))
			{
				m_replyTo = id;
			}

		}   // setReplyTo

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
			header.setAttribute("category", getCategory());
			header.setAttribute("replyTo", getReplyTo());

			return header;

		}	// toXml

	}	// BaseDiscussionMessageHeader

	/**
	* A filter that will reject discussion message drafts not from the current user, and otherwise
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

		}	//PrivacyFilter

		/**
		* Does this object satisfy the criteria of the filter?
		* @return true if the object is accepted by the filter, false if not.
		*/
		public boolean accept(Object o)
		{
			// first if o is a discussion message that's a draft from another user, reject it
			if (o instanceof DiscussionMessage)
			{
				DiscussionMessage msg = (DiscussionMessage) o;

				if (	(msg.getDiscussionHeader()).getDraft()
					&&	(!SecurityService.isSuperUser())
					&&	(!msg.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
					&&	(!unlockCheck(SECURE_READ_DRAFT, msg.getReference())))
				{
					return false;
				}
			}

			// now, use the real filter, if present
			if (m_filter != null) return m_filter.accept(o);
			
			return true;

		}	// accept
	
	}	// PrivacyFilter

}	// BaseDiscussionService



