/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/message/BaseMessageService.java,v 1.42 2004/09/30 20:20:03 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.message;

// import
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.archive.ArchiveService;
import org.sakaiproject.service.legacy.discussion.DiscussionChannelEdit;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;
import org.sakaiproject.service.legacy.message.MessageEdit;
import org.sakaiproject.service.legacy.message.MessageHeader;
import org.sakaiproject.service.legacy.message.MessageHeaderEdit;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.Filter;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
* <p>BaseMessageService is a simple implementation of the CHEF MessageService as a
* Turbine service.</p>
* <p>BaseMessageService simply stored messages in memory and has no persistence past
* the lifetime of the service object.</p>
* <p>Services Used:<ul>
* <li>SecurityService</li>
* <li>EventTrackingService</li></ul></p>
*
* Note: for simplicity, we implement only the Edit versions of message and header - otherwise we'd want to
* inherit from two places in the extensions.  A non-edit version is implemented by the edit version. -ggolden
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.42 $
*/
public abstract class BaseMessageService implements MessageService, StorageUser, CacheRefresher
{
	/** A Storage object for persistent storage. */
	protected Storage m_storage = null;

	/** A Cache object for caching: channels keyed by reference. (if m_caching) */
	protected Cache m_channelCache = null;

	/** A bunch of caches for messages: keyed by channel id, the cache is keyed by message reference. (if m_caching) */
	protected Hashtable m_messageCaches = null;

	/**
	* Access this service from the inner classes.
	*/
	protected BaseMessageService service()
	{
		return this;
	}

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * @param service The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/** Configuration: cache, or not. */
	protected boolean m_caching = false;

	/**
	 * Configuration: set the locks-in-db
	 * @param path The storage path.
	 */
	public void setCaching(String value)
	{
		m_caching = new Boolean(value).booleanValue();
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
			// construct a storage helper and read
			m_storage = newStorage();
			m_storage.open();

			// make the channel cache
			if (m_caching)
			{
				m_channelCache =
					m_memoryService.newCache(this, getAccessPoint(true) + Resource.SEPARATOR + REF_TYPE_CHANNEL + Resource.SEPARATOR);

				// make the table to hold the message caches
				m_messageCaches = new Hashtable();
			}

			m_logger.info(this +".init(): caching: " + m_caching);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	 * Destroy
	 */
	public void destroy()
	{
		if (m_caching)
		{
			m_channelCache.destroy();
			m_channelCache = null;

			m_messageCaches.clear();
			m_messageCaches = null;
		}

		m_storage.close();
		m_storage = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	 * Report the Service API name being implemented.
	 */
	protected abstract String serviceName();

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected abstract Storage newStorage();

	/**
	* Construct a new message header from XML in a DOM element.
	* @param id The message Id.
	* @return The new message header.
	*/
	protected abstract MessageHeaderEdit newMessageHeader(String id);

	/**
	* Construct a new message header from XML in a DOM element.
	* @param el The XML DOM element that has the header information.
	* @return The new message header.
	*/
	protected abstract MessageHeaderEdit newMessageHeader(Element el);

	/**
	* Construct a new message header as a copy of another.
	* @param other The other header to copy.
	* @return The new message header.
	*/
	protected abstract MessageHeaderEdit newMessageHeader(MessageHeader other);

	/**
	* Form a tracking event string based on a security function string.
	* @param secure The security function string.
	* @return The event tracking string.
	*/
	protected abstract String eventId(String secure);

	/**
	* Return the reference rooot for use in resource references and urls.
	* @return The reference rooot for use in resource references and urls.
	*/
	protected abstract String getReferenceRoot();

	/*******************************************************************************
	* MessageService implementation
	*******************************************************************************/

	/**
	* Access the partial URL that forms the root of resource URLs.
	* @param relative if true, form within the access path only (i.e. starting with /msg)
	* @return the partial URL that forms the root of resource URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + getReferenceRoot();

	} // getAccessPoint

	/**
	* Access the internal reference which can be used to access the channel from within the system.
	* @param context The context.
	* @param id The channel id.
	* @return The the internal reference which can be used to access the channel from within the system.
	*/
	public String channelReference(String context, String id)
	{
		return getAccessPoint(true)
			+ Resource.SEPARATOR
			+ REF_TYPE_CHANNEL
			+ Resource.SEPARATOR
			+ context
			+ Resource.SEPARATOR
			+ id;

	} // channelReference

	/**
	* Access the internal reference which can be used to access the message from within the system.
	* @param context The context.
	* @param channelId The channel id.
	* @param id The message id.
	* @return The the internal reference which can be used to access the message from within the system.
	*/
	public String messageReference(String context, String channelId, String id)
	{
		return getAccessPoint(true)
			+ Resource.SEPARATOR
			+ REF_TYPE_MESSAGE
			+ Resource.SEPARATOR
			+ context
			+ Resource.SEPARATOR
			+ channelId
			+ Resource.SEPARATOR
			+ id;

	} // messageReference

	/**
	* Access the internal reference which can be used to access the message from within the system.
	* @param channelRef The channel reference.
	* @param id The message id.
	* @return The the internal reference which can be used to access the message from within the system.
	*/
	public String messageReference(String channelRef, String id)
	{
		StringBuffer buf = new StringBuffer();

		// start with the channel ref
		buf.append(channelRef);

		// swap channel for msg
		int pos = buf.indexOf(REF_TYPE_CHANNEL);
		buf.replace(pos, pos + REF_TYPE_CHANNEL.length(), REF_TYPE_MESSAGE);

		// add the id
		buf.append(id);

		return buf.toString();
	}

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if allowd, false if not
	*/
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(eventId(lock), resource))
		{
			return false;
		}

		return true;

	} // unlockCheck

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if either allowed, false if not
	*/
	protected boolean unlockCheck2(String lock1, String lock2, String resource)
	{
		if (!SecurityService.unlock(eventId(lock1), resource))
		{
			if (!SecurityService.unlock(eventId(lock2), resource))
			{
				return false;
			}
		}

		return true;

	} // unlockCheck2

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), eventId(lock), resource);
		}

	} // unlock

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access to either.
	*/
	protected void unlock2(String lock1, String lock2, String resource) throws PermissionException
	{
		if (!unlockCheck2(lock1, lock2, resource))
		{
			throw new PermissionException(
				UsageSessionService.getSessionUserId(),
				eventId(lock1) + "/" + eventId(lock2),
				resource);
		}

	} // unlock2

	/**
	* Return a list of all the defined channels.
	* @return a list of MessageChannel (or extension) objects (may be empty).
	*/
	public List getChannels()
	{
		List channels = new Vector();
		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{ 
			channels = m_storage.getChannels();
			return channels;
		}

		// use the cache
		// if the cache is complete, use it
		if (m_channelCache.isComplete())
		{
			// get just the channels in the cache
			channels = m_channelCache.getAll();
		}

		// otherwise get all the channels from storage
		else
		{
			// Note: while we are getting from storage, storage might change.  These can be processed
			// after we get the storage entries, and put them in the cache, and mark the cache complete.
			// -ggolden
			synchronized (m_channelCache)
			{
				// if we were waiting and it's now complete...
				if (m_channelCache.isComplete())
				{
					// get just the channels in the cache
					channels = m_channelCache.getAll();
					return channels;
				}

				// save up any events to the cache until we get past this load
				m_channelCache.holdEvents();

				channels = m_storage.getChannels();
				// update the cache, and mark it complete
				for (int i = 0; i < channels.size(); i++)
				{
					MessageChannel channel = (MessageChannel) channels.get(i);
					m_channelCache.put(channel.getReference(), channel);
				}

				m_channelCache.setComplete();

				// now we are complete, process any cached events
				m_channelCache.processEvents();
			}
		}

		return channels;

	} // getChannels

	/**
	* check permissions for getChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to getChannel(channelId), false if not.
	*/
	public boolean allowGetChannel(String ref)
	{
		return unlockCheck(SECURE_READ, ref);

	} // allowGetChannel

	/**
	* Return a specific channel.
	* @param ref The channel reference.
	* @return the MessageChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for any channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public MessageChannel getChannel(String ref) throws IdUnusedException, PermissionException
	{
		MessageChannel c = findChannel(ref);
		if (c == null)
			throw new IdUnusedException(ref);

		// check security (throws if not permitted)
		unlock(SECURE_READ, ref);

		return c;

	} // getChannel

	/**
	* Find the channel, in cache or info store - cache it if newly found.
	* @param ref The channel reference.
	* @return The channel, if found.
	*/
	protected MessageChannel findChannel(String ref)
	{
		if (ref == null) return null;

		MessageChannel channel = null;

		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{
			// TODO: do we really want to do this? -ggolden
			// if we have done this already in this thread, use that
			channel = (MessageChannel) CurrentService.getInThread(ref);
			if (channel == null)
			{
				channel = m_storage.getChannel(ref);
				
				// "cache" the channel in the current service in case they are needed again in this thread...
				if (channel != null)
				{
					CurrentService.setInThread(ref, channel);
				}
			}

			return channel;
		}

		// use the cache
		// if we have it cached, use it (even if it's cached as a null, a miss)
		if (m_channelCache.containsKey(ref))
		{
			channel = (MessageChannel) m_channelCache.get(ref);
		}

		// if not in the cache, see if we have it in our info store
		else
		{
			channel = m_storage.getChannel(ref);

			// if so, cache it, even misses
			m_channelCache.put(ref, channel);
		}

		return channel;

	} // findChannel

	/**
	* check permissions for addChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to addChannel(channelId), false if not.
	*/
	public boolean allowAddChannel(String ref)
	{
		// check security (throws if not permitted) 
		return unlockCheck(SECURE_ADD, ref);

	} // allowAddChannel

	/**
	* Add a new channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param ref The channel reference.
	* @return The newly created channel, locked for update.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public MessageChannelEdit addChannel(String ref) throws IdUsedException, IdInvalidException, PermissionException
	{
		// check the name's validity
		Validator.checkResourceRef(ref);

		// check for existance
		if (m_storage.checkChannel(ref))
		{
			throw new IdUsedException(ref);
		}

		// check security
		unlock(SECURE_ADD, ref);

		// keep it
		MessageChannelEdit channel = m_storage.putChannel(ref);

		((BaseMessageChannelEdit) channel).setEvent(SECURE_ADD);

		return channel;

	} // addChannel

	/**
	* check permissions for editChannel()
	* @param ref The channel reference.
	* @return true if the user is allowed to update the channel, false if not.
	*/
	public boolean allowEditChannel(String ref)
	{
		// check security (throws if not permitted)
		return unlockCheck2(SECURE_UPDATE_ANY, SECURE_UPDATE_OWN, ref);

	} // allowEditChannel

	/**
	* Return a specific channel, as specified by channel id, locked for update.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param ref The channel reference.
	* @return the Channel that has the specified id.
	* @exception IdUnusedException If this name is not a defined channel.
	* @exception PermissionException If the user does not have any permissions to edit the channel.
	* @exception InUseException if the channel is locked for edit by someone else.
	*/
	public MessageChannelEdit editChannel(String ref) throws IdUnusedException, PermissionException, InUseException
	{
		// check for existance
		if (!m_storage.checkChannel(ref))
		{
			throw new IdUnusedException(ref);
		}

		// check security (throws if not permitted)
		unlock2(SECURE_UPDATE_ANY, SECURE_UPDATE_OWN, ref);

		// ignore the cache - get the channel with a lock from the info store
		MessageChannelEdit edit = m_storage.editChannel(ref);
		if (edit == null)
			throw new InUseException(ref);

		((BaseMessageChannelEdit) edit).setEvent(SECURE_UPDATE_ANY);

		return edit;

	} // editChannel

	/**
	* Commit the changes made to a MessageChannelEdit object, and release the lock.
	* The MessageChannelEdit is disabled, and not to be used after this call.
	* @param user The MessageChannelEdit object to commit.
	*/
	public void commitChannel(MessageChannelEdit edit)
	{
		if (edit == null) return;

		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitEdit(): closed ChannelEdit", e);
			}
			return;
		}

		m_storage.commitChannel(edit);

		// track event (no notification)
		Event event =
			EventTrackingService.newEvent(eventId(((BaseMessageChannelEdit) edit).getEvent()), edit.getReference(), true, NotificationService.NOTI_NONE);

		EventTrackingService.post(event);

		// close the edit object
		((BaseMessageChannelEdit) edit).closeEdit();

	} // commitChannel

	/**
	* Cancel the changes made to a MessageChannelEdit object, and release the lock.
	* The MessageChannelEdit is disabled, and not to be used after this call.
	* @param user The MessageChannelEdit object to commit.
	*/
	public void cancelChannel(MessageChannelEdit edit)
	{
		if (edit == null) return;

		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelChannelEdit(): closed MessageChannelEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancelChannel(edit);

		// close the edit object
		((BaseMessageChannelEdit) edit).closeEdit();

	} // cancelChannel

	/**
	* Check permissions for removeChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to removeChannel(), false if not.
	*/
	public boolean allowRemoveChannel(String ref)
	{
		// check security (throws if not permitted)
		return unlockCheck(SECURE_REMOVE_ANY, ref);

	} // allowRemoveChannel

	/**
	* Remove a channel.
	* Remove a channel - it must be locked from editChannel().
	* @param channel The channel to remove.
	* @exception PermissionException if the user does not have permission to remove a channel.
	*/
	public void removeChannel(MessageChannelEdit channel) throws PermissionException
	{
		if (channel == null) return;

		// check for closed edit
		if (!channel.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeChannel(): closed ChannelEdit", e);
			}
			return;
		}

		// check security
		unlock(SECURE_REMOVE_ANY, channel.getReference());

		m_storage.removeChannel(channel);

		// track event
		Event event = EventTrackingService.newEvent(eventId(SECURE_REMOVE_ANY), channel.getReference(), true);
		EventTrackingService.post(event);

		// mark the channel as removed
		((BaseMessageChannelEdit) channel).setRemoved(event);

		// close the edit object
		((BaseMessageChannelEdit) channel).closeEdit();

		// remove any realm defined for this resource
		try
		{
			RealmService.removeRealm(RealmService.editRealm(channel.getReference()));
		}
		catch (InUseException e)
		{
			m_logger.warn(this +".removeChannel: removing realm for : " + channel.getReference() + " : " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this +".removeChannel: removing realm for : " + channel.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeChannel

	/**
	* Get a message, given a reference.
	* This call avoids the need to have channel security, as long as the user has permissions to the message.
	* @param ref The message reference
	* @return The message.
	* @exception IdUnusedException If this reference does not identify a message.
	* @exception PermissionException If the user does not have any permissions to the message.
	*/
	public Message getMessage(Reference ref) throws IdUnusedException, PermissionException
	{
		// check security on the message(throws if not permitted)
		unlock(SECURE_READ, ref.getReference());

		// %%% could also check type, but need to know which message service we are working for! -ggolden
		if (!ref.getSubType().equals(REF_TYPE_MESSAGE))
		{
			throw new IdUnusedException(ref.getReference());
		}

		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{
			String channelRef = channelReference(ref.getContext(), ref.getContainer());

			// if we have "cached" the entire set of messages in the thread, get that and find our message there
			List msgs = (List) CurrentService.getInThread(channelRef+".msgs");
			if (msgs != null)
			{
				for (Iterator i = msgs.iterator(); i.hasNext();)
				{
					Message m = (Message) i.next();
					if (m.getId().equals(ref.getId()))
					{
						return m;
					}
				}
			}

			// get the message from storage
			Message m = m_storage.getMessage(channelRef, ref.getId());		
			if (m == null)
				throw new IdUnusedException(ref.getId());

			return m;
		}

		// use the cache
		// get the channel, no security check
		MessageChannel c = findChannel(channelReference(ref.getContext(), ref.getContainer()));
		if (c == null)
			throw new IdUnusedException(ref.getContainer());

		Message m = ((BaseMessageChannelEdit) c).findMessage(ref.getId());
		return m;

	} // getMessage

	/**
	* Get all view-permitted messages in a channel, given a channel reference.
	* This call avoids the need to have channel security, as long as the user has permissions to the message.
	* @param ref The channel reference string.
	* @return The List (Messge) of messages.
	* @exception IdUnusedException If this reference does not identify a channel.
	*/
	public List getMessages(String ref) throws IdUnusedException
	{
		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{
			// null - no drafts, "*", all drafts, <userId> drafts created by user id only
			String draftsForId = null;
			if (unlockCheck(SECURE_READ_DRAFT, ref))
			{
				draftsForId = "*";
			}
			else
			{
				draftsForId = UsageSessionService.getSessionUserId();
			}

			List rv = m_storage.getMessages(ref, null, 0, draftsForId);
			Collections.reverse(rv);
			return rv;
		}

		// get the channel, no security check
		MessageChannel c = findChannel(ref);
		if (c == null)
			throw new IdUnusedException(ref);

		// get the messages
		List msgs = ((BaseMessageChannelEdit) c).findFilterMessages(new MessagePermissionFilter(), true);

		return msgs;

	} // getMessages

	/**
	* Cancel the changes made to a MessageEdit object, and release the lock.
	* The MessageChannelEdit is disabled, and not to be used after this call.
	* @param user The MessageEdit object to cancel.
	*/
	public void cancelMessage(MessageEdit edit)
	{
		if ((edit == null) || (((BaseMessageEdit) edit).m_channel == null)) return;
		
		((BaseMessageEdit) edit).m_channel.cancelMessage(edit);
	}

	/**
	 * Access a list of messages in the channel, that are after the date, limited to just the n latest messages,
	 * ordered as specified, including drafts if specified.
	 * Channel read permission is required - draft read on the channel is required to see drafts.
	 * @param afterDate if null, no date limit, else limited to only messages after this date.
	 * @param limitedToLatest if 0, no count limit, else limited to only the latest this number of messages.
	 * @param ascending if true, sort oldest first, else sort latest first.
	 * @param includeDrafts if true, include drafts (if the user has draft permission), else leave them out.
	 * @return A list of Message objects that meet the criteria; may be empty
	 * @exception PermissionException If the current user does not have channel read permission.
	 */
	public List getMessages(String channelRef, Time afterDate, int limitedToLatest, boolean ascending, boolean includeDrafts)
		throws PermissionException
	{
		// channel read security
		unlock(SECURE_READ, channelRef);
		
		// null - no drafts, "*", all drafts, <userId> drafts created by user id only
		String draftsForId = null;
		if (includeDrafts)
		{
			if (unlockCheck(SECURE_READ_DRAFT, channelRef))
			{
				draftsForId = "*";
			}
			else
			{
				draftsForId = UsageSessionService.getSessionUserId();
			}
		}

		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{
			// get messages filtered by date and count and drafts, in descending (latest first) order
			List rv = m_storage.getMessages(channelRef, afterDate, limitedToLatest, draftsForId);
		
			// if ascending, reverse
			if (ascending)
			{
				Collections.reverse(rv);
			}
		
			return rv;
		}

		// use the cache

		// get the channel, no security check
		MessageChannel c = findChannel(channelRef);
		if (c == null)
			return new Vector();

		// get the messages
		List msgs = ((BaseMessageChannelEdit) c).findFilterMessages(new MessageSelectionFilter(afterDate, draftsForId), ascending);

		// sub-select count
		if ((limitedToLatest != 0) && (limitedToLatest < msgs.size()))
		{
			if (ascending)
			{
				msgs = msgs.subList(msgs.size() - limitedToLatest, msgs.size());
			}
			else
			{
				msgs = msgs.subList(0, limitedToLatest);
			}
		}

		return msgs;
	}

	/**
	 * Access a list of channel ids that are defined related to the context.
	 * @param context The context in which to search
	 * @return A List (String) of channel id for channels withing the context.
	 */
	public List getChannelIds(String context)
	{
		if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
		{
			return m_storage.getChannelIdsMatching(channelReference(context,""));
		}

		// use the cache
		List channels = getChannels();
		List rv = new Vector();
		for (Iterator i = channels.iterator(); i.hasNext();)
		{
			MessageChannel channel = (MessageChannel) i.next();
			if (context.equals(channel.getContext()))
			{
				rv.add(channel.getId());
			}
		}
		
		return rv;
	}

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	 * {@inheritdoc}
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
				Message msg = (Message) messages.next();
				msg.toXml(doc, stack);
		
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
	}

	/**
	 * {@inheritDoc}
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, HashMap userIdTrans, Set userListAllowImport)
	{	
		// get the system name: FROM_WT, FROM_CT, FROM_SAKAI
		String source = "";
		// root: <service> node			
		Node parent = root.getParentNode(); // parent: <archive> node containing "system"
		if (parent.getNodeType() == Node.ELEMENT_NODE)
		{
			Element parentEl = (Element)parent;
			source = parentEl.getAttribute("system");
		}
		
		boolean fromWT = source.equalsIgnoreCase(ArchiveService.FROM_WT);
		boolean fromCT = source.equalsIgnoreCase(ArchiveService.FROM_CT);
		boolean fromSakai = source.equalsIgnoreCase(ArchiveService.FROM_SAKAI);
		
		HashSet userSet = (HashSet) userListAllowImport;
		
		Map ids = new HashMap();

		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// get the channel associated with this site
		String channelRef = channelReference(siteId, SiteService.MAIN_CONTAINER);

		int count = 0;

		try
		{
			MessageChannel channel = null;
			try
			{
				channel = getChannel(channelRef);
			}
			catch (IdUnusedException e)
			{
				MessageChannelEdit edit = addChannel(channelRef);
				commitChannel(edit);
				channel = edit;
			}

			// pass the DOM to get new message ids, record the mapping from old to new, and adjust attachments
			NodeList children2 = root.getChildNodes();
			int length2 = children2.getLength();
			for (int i2 = 0; i2 < length2; i2++)
			{
				Node child2 = children2.item(i2);
				if (child2.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element2 = (Element)child2;
	
					// get the "channel" child
					if (element2.getTagName().equals("channel"))
					{
						NodeList children3 = element2.getChildNodes();
						final int length3 = children3.getLength();
						for(int i3 = 0; i3 < length3; i3++)
						{
							Node child3 = children3.item(i3);
							if (child3.getNodeType() == Node.ELEMENT_NODE)
							{
								Element element3 = (Element)child3;
								
								if (element3.getTagName().equals("categories"))
								{
									NodeList children4 = element3.getChildNodes();
									final int length4 = children4.getLength();
									for(int i4 = 0; i4 < length4; i4++)
									{
										Node child4 = children4.item(i4);
										if (child4.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element4 = (Element)child4;
											if (element4.getTagName().equals("category"))
											{
												MessageChannelEdit c = editChannel(channelRef);
												String category = element4.getAttribute("name");
												((DiscussionChannelEdit) c).addCategory(category);
												commitChannel(c);
											}
										}
									}
								}
				
								// for "message" children
								if (element3.getTagName().equals("message"))
								{									
									// get the header child
									NodeList children4 = element3.getChildNodes();
									final int length4 = children4.getLength();
									for(int i4 = 0; i4 < length4; i4++)
									{
										Node child4 = children4.item(i4);
										if (child4.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element4 = (Element)child4;
							
											// for "header" children
											if (element4.getTagName().equals("header"))
											{
												String oldUserId = element4.getAttribute("from");
												
												// userIdTrans is not empty only when from WT
												if (!userIdTrans.isEmpty())
												{
													if (userIdTrans.containsKey(oldUserId))
													{
														element4.setAttribute("from", (String)userIdTrans.get(oldUserId));
													}
												}
												
												
												// adjust the id
												String oldId = element4.getAttribute("id");
												String newId = IdService.getUniqueId();
												ids.put(oldId, newId);
												element4.setAttribute("id", newId);

												// get the attachment kids
												NodeList children5 = element4.getChildNodes();
												final int length5 = children5.getLength();
												for(int i5 = 0; i5 < length5; i5++)
												{
													Node child5 = children5.item(i5);
													if (child5.getNodeType() == Node.ELEMENT_NODE)
													{
														Element element5 = (Element)child5;
										
														// for "attachment" children
														if (element5.getTagName().equals("attachment"))
														{
															// map the attachment area folder name
															String oldUrl = element5.getAttribute("relative-url");
															if (oldUrl.startsWith("/content/attachment/"))
															{
																String newUrl = (String) attachmentNames.get(oldUrl);
																if (newUrl != null)
																{
																	if (newUrl.startsWith("/attachment/"))
																		newUrl = "/content".concat(newUrl);
																		
																	element5.setAttribute("relative-url", newUrl);
																}
															}
															
															// map any references to this site to the new site id
															else if (oldUrl.startsWith("/content/group/" + fromSiteId + "/"))
															{
																String newUrl = "/content/group/"
																	+ siteId
																	+ oldUrl.substring(15 + fromSiteId.length());
																element5.setAttribute("relative-url", newUrl);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			// one more pass to update reply-to (now we have a complte id mapping),
			// and we are ready then to create the message
			children2 = root.getChildNodes();
			length2 = children2.getLength();
			for (int i2 = 0; i2 < length2; i2++)
			{
				Node child2 = children2.item(i2);
				if (child2.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element2 = (Element)child2;
	
					// get the "channel" child
					if (element2.getTagName().equals("channel"))
					{
						NodeList children3 = element2.getChildNodes();
						final int length3 = children3.getLength();
						for(int i3 = 0; i3 < length3; i3++)
						{
							Node child3 = children3.item(i3);
							if (child3.getNodeType() == Node.ELEMENT_NODE)
							{
								Element element3 = (Element)child3;
				
								// for "message" children
								if (element3.getTagName().equals("message"))
								{
									// a flag showing if continuing merging the message
									boolean goAhead = true; 
																		
									// get the header child
									NodeList children4 = element3.getChildNodes();
									final int length4 = children4.getLength();
									for(int i4 = 0; i4 < length4; i4++)
									{
										Node child4 = children4.item(i4);
										if (child4.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element4 = (Element)child4;
							
											// for "header" children
											if (element4.getTagName().equals("header"))
											{
												// adjust the replyTo
												String oldReplyTo = element4.getAttribute("replyTo");
												if ((oldReplyTo != null) && (oldReplyTo.length() > 0))
												{
													String newReplyTo = (String) ids.get(oldReplyTo);
													if (newReplyTo != null)
													{
														element4.setAttribute("replyTo", newReplyTo);
													}
												}
												
												if (fromSakai || fromCT)
												{
													// only merge this message when the userId has the right role
													if (!userSet.contains(element4.getAttribute("from"))) 
													{
														goAhead = false;
													}
													//
													element4.setAttribute("draft", (ArchiveService.SAKAI_msg_draft_import) ? "true" : "false");
												
												}
											}
										}
									}

									// if this message is from CT or Sakai, and created by a user with right role, merge it
									// or, if this message is from WT
									if (fromWT || ((fromCT || fromSakai) && goAhead))
									{
										// create a new message in the channel
										MessageEdit edit = channel.mergeMessage(element3);
										// commit the new message without notification
										channel.commitMessage(edit, NotificationService.NOTI_NONE);
										count++;
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".mergeMessages(): exception in handling " + serviceName() + " : ", any);
		}

		results.append("merging " + getLabel() + " channel " + channelRef + " (" + count + ") messages.\n");
		return results.toString();
		
	} // merge

	/*******************************************************************************
	* MessageChannel implementation
	*******************************************************************************/

	public class BaseMessageChannelEdit extends Observable implements MessageChannelEdit, SessionStateBindingListener
	{
		/** The context in which this channel exists. */
		protected String m_context = null;

		/** The channel id, unique within the context. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** When true, the channel has been removed. */
		protected boolean m_isRemoved = false;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct with a reference.
		* @param ref The channel reference.
		*/
		public BaseMessageChannelEdit(String ref)
		{
			// set the ids
			Reference r = new Reference(ref);
			m_context = r.getContext();
			m_id = r.getId();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

		} // BaseMessageChannelEdit

		/**
		* Construct as a copy of another.
		* @param id The other to copy.
		*/
		public BaseMessageChannelEdit(MessageChannel other)
		{
			// set the ids
			m_context = other.getContext();
			m_id = other.getId();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

		} // BaseMessageChannelEdit

		/**
		* Construct from a channel (and possibly messages) already defined
		* in XML in a DOM tree.
		* The Channel is added to storage.
		* @param el The XML DOM element defining the channel.
		*/
		public BaseMessageChannelEdit(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// read the ids
			m_id = el.getAttribute("id");
			m_context = el.getAttribute("context");

			// the children (properties, ignore messages)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties (ignore possible "message" entries)
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}
			}

		} // BaseMessageChannelEdit

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			deleteObservers();

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelChannel(this);
			}

		} // finalize

		/**
		* Set the channel as removed.
		* @param event The tracking event associated with this action.
		*/
		public void setRemoved(Event event)
		{
			m_isRemoved = true;

			// channel notification
			notify(event);

			// now clear observers
			deleteObservers();

		} // setRemoved

		/**
		* Access the context of the resource.
		* @return The context.
		*/
		public String getContext()
		{
			return m_context;

		} // getContext

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + SEPARATOR + getId() + SEPARATOR; // %%% needs fixing re: context

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return channelReference(m_context, m_id);

		} // getReference

		/**
		* Access the channel's properties.
		* @return The channel's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* check permissions for getMessages() or getMessage().
		* @return true if the user is allowed to get messages from this channel, false if not.
		*/
		public boolean allowGetMessages()
		{
			return unlockCheck(SECURE_READ, getReference());

		} // allowGetMessages

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
			// check security (throws if not permitted)
			unlock(SECURE_READ, getReference());

			// track event
			// EventTrackingService.post(EventTrackingService.newEvent(eventId(SECURE_READ), getReference(), false));

			return findFilterMessages(filter, ascending);

		} // getMessages

		/**
		* Return a specific channel message, as specified by message name.
		* @param messageId The id of the message to get.
		* @return the Message that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		*/
		public Message getMessage(String messageId) throws IdUnusedException, PermissionException
		{
			// check security on the channel (throws if not permitted)
			unlock(SECURE_READ, getReference());

			Message m = findMessage(messageId);

			if (m == null)
				throw new IdUnusedException(messageId);

			// track event
			// EventTrackingService.post(EventTrackingService.newEvent(eventId(SECURE_READ), m.getReference(), false));

			return m;

		} // getMessage

		/**
		* check permissions for editMessage()
		* @param id The message id.
		* @return true if the user is allowed to update the message, false if not.
		*/
		public boolean allowEditMessage(String messageId)
		{
			Message m = findMessage(messageId);
			if (m == null)
				return false;

			// is this the user's own?
			if (m.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
			{
				// own or any
				return unlockCheck2(SECURE_UPDATE_OWN, SECURE_UPDATE_ANY, getReference());
			}

			else
			{
				// just any
				return unlockCheck(SECURE_UPDATE_ANY, getReference());
			}

		} // allowEditMessage

		/**
		* Return a specific channel message, as specified by message name, locked for update.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @param messageId The id of the message to get.
		* @return the Message that has the specified id.
		* @exception IdUnusedException If this name is not a defined message in this channel.
		* @exception PermissionException If the user does not have any permissions to read the message.
		* @exception InUseException if the current user does not have permission to mess with this user.
		*/
		public MessageEdit editMessage(String messageId) throws IdUnusedException, PermissionException, InUseException
		{
			Message m = findMessage(messageId);
			if (m == null)
				throw new IdUnusedException(messageId);

			// is this the user's own?
			String function = null;
			if (m.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
			{
				// own or any
				unlock2(SECURE_UPDATE_OWN, SECURE_UPDATE_ANY, getReference());
				function = SECURE_UPDATE_OWN;
			}
			else
			{
				// just any
				unlock(SECURE_UPDATE_ANY, getReference());
				function = SECURE_UPDATE_ANY;
			}

			// ignore the cache - get the message with a lock from the info store
			MessageEdit msg = m_storage.editMessage(this, messageId);
			if (msg == null)
				throw new InUseException(messageId);

			((BaseMessageEdit) msg).setEvent(function);

			return msg;

		} // editMessage

		/**
		* Commit the changes made to a MessageEdit object, and release the lock.
		* The MessageEdit is disabled, and not to be used after this call.
		* @param user The UserEdit object to commit.
		*/
		public void commitMessage(MessageEdit edit)
		{
			commitMessage(edit, NotificationService.NOTI_OPTIONAL);

		} // commitMessage

		/**
		* Commit the changes made to a MessageEdit object, and release the lock.
		* The MessageEdit is disabled, and not to be used after this call.
		* @param user The UserEdit object to commit.
		* @param priority The notification priority for this commit.
		*/
		public void commitMessage(MessageEdit edit, int priority)
		{
			// check for closed edit
			if (!edit.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".commitEdit(): closed MessageEdit", e);
				}
				return;
			}

			// update the properties
			//addLiveUpdateProperties(edit.getPropertiesEdit());//%%%

			// complete the edit
			m_storage.commitMessage(this, edit);

			// track event
			Event event =
				EventTrackingService.newEvent(
					eventId(((BaseMessageEdit) edit).getEvent()),
					edit.getReference(),
					true,
					priority);
			EventTrackingService.post(event);

			// channel notification
			notify(event);

			// close the edit object
			 ((BaseMessageEdit) edit).closeEdit();

		} // commitMessage

		/**
		* Cancel the changes made to a MessageEdit object, and release the lock.
		* The MessageEdit is disabled, and not to be used after this call.
		* @param user The UserEdit object to commit.
		*/
		public void cancelMessage(MessageEdit edit)
		{
			// check for closed edit
			if (!edit.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".commitEdit(): closed MessageEdit", e);
				}
				return;
			}

			// release the edit lock
			m_storage.cancelMessage(this, edit);

			// close the edit object
			 ((BaseMessageEdit) edit).closeEdit();

		} // cancelMessage

		/**
		* check permissions for addMessage().
		* @return true if the user is allowed to addMessage(...), false if not.
		*/
		public boolean allowAddMessage()
		{
			// check security (throws if not permitted)
			return unlockCheck(SECURE_ADD, getReference());

		} // allowAddMessage

		/**
		* Add a new message to this channel.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @return The newly added message.
		* @exception PermissionException If the user does not have write permission to the channel.
		*/
		public MessageEdit addMessage() throws PermissionException
		{
			// check security (throws if not permitted)
			unlock(SECURE_ADD, getReference());

			String id = null;
			// allocate a new unique message id, using the CHEF Service API cover
			id = IdService.getUniqueId();

			// get a new message in the info store
			MessageEdit msg = m_storage.putMessage(this, id);

			((BaseMessageEdit) msg).setEvent(SECURE_ADD);

			return msg;

		} // addMessage

		/**
		* Merge in a new message as defined in the xml.
		* Must commitEdit() to make official, or cancelEdit() when done!
		* @param el The message information in XML in a DOM element.
		* @return The newly added message, locked for update.
		* @exception PermissionException If the user does not have write permission to the channel.
		* @exception IdUsedException if the user id is already used.
		*/
		public MessageEdit mergeMessage(Element el) throws PermissionException, IdUsedException
		{
			// check security (throws if not permitted)
			unlock(SECURE_ADD, getReference());

			Message msgFromXml = (Message) newResource(this, el);

			// reserve a user with this id from the info store - if it's in use, this will return null
			MessageEdit msg = m_storage.putMessage(this, msgFromXml.getId());
			if (msg == null)
			{
				throw new IdUsedException(msgFromXml.getId());
			}

			// transfer from the XML read object to the Edit
			((BaseMessageEdit) msg).set(msgFromXml);

			((BaseMessageEdit) msg).setEvent(SECURE_ADD);

			return msg;

		} // mergeMessage

		/**
		* check permissions for removeMessage().
		* @param message The message from this channel to remove.
		* @return true if the user is allowed to removeMessage(...), false if not.
		*/
		public boolean allowRemoveMessage(Message message)
		{
			// is this the user's own?
			if (message.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
			{
				// own or any
				return unlockCheck2(SECURE_REMOVE_OWN, SECURE_REMOVE_ANY, getReference());
			}

			else
			{
				// just any
				return unlockCheck(SECURE_REMOVE_ANY, getReference());
			}

		} // allowRemoveMessage

		/**
		* Remove a message from the channel - it must be locked from editMessage().
		* @param message The message from this channel to remove.
		* @exception PermissionException if the user does not have permission to remove the message.
		*/
		public void removeMessage(MessageEdit message) throws PermissionException
		{
			// check for closed edit
			if (!message.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".removeMessage(): closed MessageEdit", e);
				}
				return;
			}

			// is this the user's own?
			String function = null;
			if (message.getHeader().getFrom().getId().equals(UsageSessionService.getSessionUserId()))
			{
				// own or any
				unlock2(SECURE_REMOVE_OWN, SECURE_REMOVE_ANY, getReference());
				function = SECURE_REMOVE_OWN;
			}
			else
			{
				// just any
				unlock(SECURE_REMOVE_ANY, getReference());
				function = SECURE_REMOVE_ANY;
			}

			m_storage.removeMessage(this, message);

			// track event
			Event event = EventTrackingService.newEvent(eventId(function), message.getReference(), true);
			EventTrackingService.post(event);

			// channel notification
			notify(event);

			// close the edit object
			 ((BaseMessageEdit) message).closeEdit();

			// remove any realm defined for this resource
			try
			{
				RealmService.removeRealm(RealmService.editRealm(message.getReference()));
			}
			catch (InUseException e)
			{
				m_logger.warn(this +".removeMessage: removing realm for : " + message.getReference() + " : " + e);
			}
			catch (PermissionException e)
			{
				m_logger.warn(this +".removeMessage: removing realm for : " + message.getReference() + " : " + e);
			}
			catch (IdUnusedException ignore)
			{
			}

		} // removeMessage

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element channel = doc.createElement("channel");

			if (stack.isEmpty())
			{
				doc.appendChild(channel);
			}
			else
			{
				((Element) stack.peek()).appendChild(channel);
			}

			stack.push(channel);

			channel.setAttribute("context", getContext());
			channel.setAttribute("id", getId());

			// properties
			m_properties.toXml(doc, stack);

			stack.pop();

			return channel;

		} // toXml

		/**
		* Notify the channel that it has changed (i.e. when a message has changed)
		* @param event The event that caused the update.
		*/
		public void notify(Event event)
		{
			// notify observers, sending the tracking event to identify the change
			setChanged();
			notifyObservers(event);

		} // notify

		/**
		* Find the message, in cache or info store - cache it if newly found.
		* @param messageId The id of the message.
		* @return The message, if found.
		*/
		protected Message findMessage(String messageId)
		{
			if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
			{
				// if we have "cached" the entire set of messages in the thread, get that and find our message there
				List msgs = (List) CurrentService.getInThread(getReference()+".msgs");
				if (msgs != null)
				{
					for (Iterator i = msgs.iterator(); i.hasNext();)
					{
						Message m = (Message) i.next();
						if (m.getId().equals(messageId))
						{
							return m;
						}
					}
				}

				Message m = m_storage.getMessage(this, messageId);
				return m;
			}

			// use the cache
			Message m = null;
			
			// messages are cached with the full reference as key
			String key = messageReference(m_context, m_id, messageId);
			
			// find the message cache
			Cache msgCache = (Cache) m_messageCaches.get(getReference());
			if (msgCache == null)
			{
				synchronized (m_messageCaches)
				{
					// check again
					msgCache = (Cache) m_messageCaches.get(getReference());
		
					// if still not there, make one
					if (msgCache == null)
					{
						msgCache = m_memoryService.newCache(service(), messageReference(m_context, m_id, ""));
						m_messageCaches.put(getReference(), msgCache);
					}
				}
			}
		
			// if we have it cached, use it (even if it's cached as a null, a miss)
			if (msgCache.containsKey(key))
			{
				m = (Message) msgCache.get(key);
			}
		
			// if not in the cache, see if we have it in our info store
			else
			{
				m = m_storage.getMessage(this, messageId);
		
				// if so, cache it, even misses
				msgCache.put(key, m);
			}

			return m;

		} // findMessage

		/**
		* Find all messages.
		* @return a List of all messages in the channel.
		*/
		protected List findMessages()
		{
			if ((!m_caching) || (m_channelCache == null) || (m_channelCache.disabled()))
			{
				// TODO: do we really want to do this? -ggolden
				// if we have done this already in this thread, use that
				List msgs = (List) CurrentService.getInThread(getReference()+".msgs");
				if (msgs == null)
				{
					msgs = m_storage.getMessages(this);
				
					// "cache" the mesasge in the current service in case they are needed again in this thread...
					CurrentService.setInThread(getReference()+".msgs", msgs);
				}

				return msgs;
			}

			// use the cache
			List msgs = new Vector();
			
			// find the message cache
			Cache msgCache = (Cache) m_messageCaches.get(getReference());
			if (msgCache == null)
			{
				synchronized (m_messageCaches)
				{
					// check again
					msgCache = (Cache) m_messageCaches.get(getReference());
		
					// if still not there, make one
					if (msgCache == null)
					{
						msgCache = m_memoryService.newCache(service(), messageReference(m_context, m_id, ""));
						m_messageCaches.put(getReference(), msgCache);
					}
				}
			}
		
			// if the cache is complete, use it
			if (msgCache.isComplete())
			{
				// get just this channel's messages
				msgs = msgCache.getAll();
			}
		
			// otherwise get all the msgs from storage
			else
			{
				// Note: while we are getting from storage, storage might change.  These can be processed
				// after we get the storage entries, and put them in the cache, and mark the cache complete.
				// -ggolden
				synchronized (msgCache)
				{
					// if we were waiting and it's now complete...
					if (msgCache.isComplete())
					{
						// get just this channel's messages
						msgs = msgCache.getAll();
					}
					else
					{
						// cache up any events to the cache until we get past this load
						msgCache.holdEvents();
		
						msgs = m_storage.getMessages(this);
						// update the cache, and mark it complete
						for (int i = 0; i < msgs.size(); i++)
						{
							Message msg = (Message) msgs.get(i);
							msgCache.put(msg.getReference(), msg);
						}
		
						msgCache.setComplete();
		
						// now we are complete, process any cached events
						msgCache.processEvents();
					}
				}
			}

			return msgs;

		} // findMessages

		/**
		* Find messages, sort, and filter.
		* @param filter A filtering object to accept messages, or null if no filtering is desired.
		* @param ascending Order of messages, ascending if true, descending if false
		* @return All messages, sorted and filtered.
		*/
		public List findFilterMessages(Filter filter, boolean ascending)
		{
			List msgs = findMessages();
			if (msgs.size() == 0)
				return msgs;

			// sort - natural order is date ascending
			Collections.sort(msgs);

			// reverse, if not ascending
			if (!ascending)
			{
				Collections.reverse(msgs);
			}

			// filter out
			if (filter != null)
			{
				List filtered = new Vector();
				for (int i = 0; i < msgs.size(); i++)
				{
					Message msg = (Message) msgs.get(i);
					if (filter.accept(msg))
						filtered.add(msg);
				}
				if (filtered.size() == 0)
					return filtered;
				msgs = filtered;
			}

			return msgs;

		} // findFilterMessages

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		public void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelChannel(this);
			}

		} // valueUnbound

	} // class BaseMessageChannel

	/*******************************************************************************
	* MessageEdit implementation
	*******************************************************************************/

	public class BaseMessageEdit implements MessageEdit, SessionStateBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/** The message header. */
		protected MessageHeaderEdit m_header = null;

		/** The message body. */
		protected String m_body = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** A transient backpointer to the channel */
		protected MessageChannel m_channel = null;

		/**
		* Construct.
		* @param id  The message id.
		*/
		public BaseMessageEdit(MessageChannel channel, String id)
		{
			// store the channel
			m_channel = channel;

			// store the id in a new (appropriate typed) header
			m_header = newMessageHeader(id);

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

		} // BaseMessageEdit

		/**
		* Construct as a copy  of another message.
		* @param other  The other message to copy.
		*/
		public BaseMessageEdit(MessageChannel channel, Message other)
		{
			// store the channel
			m_channel = channel;

			setAll(other);

		} // BaseMessageEdit

		/**
		* Construct from an existing definition, in xml.
		* @param channel The channel in which this message lives.
		* @param el The message in XML in a DOM element.
		*/
		public BaseMessageEdit(MessageChannel channel, Element el)
		{
			this(channel, "");

			// get the body as formatted text if its there
			m_body = Xml.decodeAttribute(el, "body-formatted");
			
			// %%% JANDERSE - Handle conversion from plaintext messages to formatted text messages
			// get the body as plaintext if it's in the attribute
			if (m_body == null || m_body.length() == 0)
			{
				m_body = Xml.decodeAttribute(el, "body");
				m_body = FormattedText.convertPlaintextToFormattedText(m_body);
			}
			 
			// the children (header, body)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) child;

					// look for a header
					if (element.getTagName().equals("header"))
					{
						// re-create a header
						m_header = newMessageHeader(element);
					}

					// or look for a body (old style of encoding)
					else if (element.getTagName().equals("body"))
					{
						if ((element.getChildNodes() != null) && (element.getChildNodes().item(0) != null))
						{
							// %%% JANDERSE - Handle conversion from plaintext messages to formatted text messages
							m_body = element.getChildNodes().item(0).getNodeValue();
							if (m_body != null) m_body = FormattedText.convertPlaintextToFormattedText(m_body);
						}
						if (m_body == null)
						{
							m_body = "";
						}
					}

					// or look for properties
					else if (element.getTagName().equals("properties"))
					{
						// re-create properties
						m_properties = new BaseResourcePropertiesEdit(element);
					}
				}
			}

		} // BaseMessageEdit

		/**
		* Take all values from this object.
		* @param user The other object to take values from.
		*/
		protected void setAll(Message other)
		{
			// copy the header
			m_header = newMessageHeader(other.getHeader());

			// body
			m_body = other.getBody();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

		} // setAll

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if ((m_active) && (m_channel != null))
			{
				m_channel.cancelMessage(this);
			}

			m_channel = null;

		} // finalize

		/**
		* Access the message header.
		* @return The message header.
		*/
		public MessageHeader getHeader()
		{
			return m_header;

		} // getHeader

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_header.getId();

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			if (m_channel == null) return "";
			return m_channel.getUrl() + getId();

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			if (m_channel == null) return "";
			return messageReference(m_channel.getContext(), m_channel.getId(), getId());

		} // getReference

		/**
		* Access the channel's properties.
		* @return The channel's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Access the body text, as a string.
		* @return The body text, as a string.
		*/
		public String getBody()
		{
			return ((m_body == null) ? "" : m_body);

		} // getBodyText

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element message = doc.createElement("message");

			if (stack.isEmpty())
			{
				doc.appendChild(message);
			}
			else
			{
				((Element) stack.peek()).appendChild(message);
			}

			stack.push(message);

			m_header.toXml(doc, stack);

			// store the body encoded in the "body-formatted" attribute
			Xml.encodeAttribute(message, "body-formatted", getBody());

			// TODO: [DONE] store the body encoded in the "body" attribute for CHEF compatibility
			Xml.encodeAttribute(message, "body", FormattedText.convertFormattedTextToPlaintext(getBody()));

			/*
			// Note: the old way to set the body - CDATA is too sensitive to the characters within -ggolden
						Element body = doc.createElement("body");
						message.appendChild(body);
						body.appendChild(doc.createCDATASection(getBody()));
			*/

			// properties
			m_properties.toXml(doc, stack);

			stack.pop();

			return message;

		} // toXml

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Message))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// compare the header's date
			int compare = getHeader().getDate().compareTo(((Message) obj).getHeader().getDate());

			return compare;

		} // compareTo

		/**
		* Are these objects equal?  If they are both Message objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Message))
				return false;
			return ((Message) obj).getId().equals(getId());

		} // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		} // hashCode

		/**
		* Replace the body, as a string.
		* @param body The body, as a string.
		*/
		public void setBody(String body)
		{
			m_body = body;

		} // setBody

		/**
		* Take all values from this object.
		* @param user The other object to take values from.
		*/
		protected void set(Message other)
		{
			setAll(other);

		} // set

		/**
		* Access the message header.
		* @return The message header.
		*/
		public MessageHeaderEdit getHeaderEdit()
		{
			return m_header;

		} // getHeaderEdit

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		public void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if ((m_active) && (m_channel != null))
			{
				m_channel.cancelMessage(this);
			}

		} // valueUnbound

	} // BaseMessageEdit

	/*******************************************************************************
	* MessageHeaderEdit implementation
	*******************************************************************************/

	public class BaseMessageHeaderEdit implements MessageHeaderEdit
	{
		/** The unique (within the channel) message id. */
		protected String m_id = null;

		/** The date/time the message was sent to the channel. */
		protected Time m_date = null;

		/** The User who sent the message to the channel. */
		protected User m_from = null;

		/** The attachments - dereferencer objects. */
		protected ReferenceVector m_attachments = null;

		/** The draft status for the message. */
		protected boolean m_draft = false;

		/**
		* Construct.  Time and From set automatically.
		* @param id The message id.
		*/
		public BaseMessageHeaderEdit(String id)
		{
			m_id = id;
			m_date = TimeService.newTime();
			try
			{
				m_from = UserDirectoryService.getUser(UsageSessionService.getSessionUserId());
			}
			catch (IdUnusedException e)
			{
				m_from = UserDirectoryService.getAnonymousUser();
			}

			// init the AttachmentContainer
			m_attachments = new ReferenceVector();

		} // BaseMessageHeaderEdit

		/**
		* Construct as a copy of another header.
		* @param other The other message header to copy.
		*/
		public BaseMessageHeaderEdit(MessageHeader other)
		{
			m_id = other.getId();
			m_date = TimeService.newTime(other.getDate().getTime());
			m_from = other.getFrom();
			m_draft = other.getDraft();

			m_attachments = new ReferenceVector();
			replaceAttachments(other.getAttachments());

		} // BaseMessageHeaderEdit

		/**
		* Construct, from an already existing XML DOM element.
		* @param el The header in XML in a DOM element.
		*/
		public BaseMessageHeaderEdit(Element el)
		{
			m_id = el.getAttribute("id");
			try
			{
				m_from = UserDirectoryService.getUser(el.getAttribute("from"));
			}
			catch (IdUnusedException e)
			{
				m_from = UserDirectoryService.getAnonymousUser();
			}
			m_date = TimeService.newTimeGmt(el.getAttribute("date"));
			try
			{
				m_draft = new Boolean(el.getAttribute("draft")).booleanValue();
			}
			catch (Throwable any)
			{
			}

			// attachments
			m_attachments = new ReferenceVector();

			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) child;

					// look for an attachment
					if (element.getTagName().equals("attachment"))
					{
						m_attachments.add(new Reference(element.getAttribute("relative-url")));
					}
				}
			}

		} // BaseMessageHeaderEdit

		/**
		* Access the unique (within the channel) message id.
		* @return The unique (within the channel) message id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the date/time the message was sent to the channel.
		* @return The date/time the message was sent to the channel.
		*/
		public Time getDate()
		{
			return m_date;

		} // getDate

		/**
		* Access the User who sent the message to the channel.
		* @return The User who sent the message to the channel.
		*/
		public User getFrom()
		{
			return m_from;

		} // getFrom

		/**
		* Access the draft status of the message.
		* @return True if the message is a draft, false if not.
		*/
		public boolean getDraft()
		{
			return m_draft;

		} // getDraft

		/**
		* Set the draft status of the message.
		* @param draft True if the message is a draft, false if not.
		*/
		public void setDraft(boolean draft)
		{
			m_draft = draft;

		} // setDraft

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element header = doc.createElement("header");
			((Element) stack.peek()).appendChild(header);
			header.setAttribute("id", getId());
			header.setAttribute("from", getFrom().getId());
			header.setAttribute("date", getDate().toString());
			if ((m_attachments != null) && (m_attachments.size() > 0))
			{
				for (int i = 0; i < m_attachments.size(); i++)
				{
					Reference attch = (Reference) m_attachments.elementAt(i);
					Element attachment = doc.createElement("attachment");
					header.appendChild(attachment);
					attachment.setAttribute("relative-url", attch.getReference());
				}
			}

			return header;

		} // toXml

		/**
		* Set the date/time the message was sent to the channel.
		* @param date The date/time the message was sent to the channel.
		*/
		public void setDate(Time date)
		{
			if (!date.equals(m_date))
			{
				m_date.setTime(date.getTime());
			}

		} // setDate

		/**
		* Set the User who sent the message to the channel.
		* @param user The User who sent the message to the channel.
		*/
		public void setFrom(User user)
		{
			if (!user.equals(m_from))
			{
				m_from = user;
			}

		} // setFrom

		/*******************************************************************************
		* AttachmentContainer implementation
		*******************************************************************************/

		/**
		* Access the attachments of the event.
		* @return An copy of the set of attachments (a ReferenceVector containing Reference objects) (may be empty).
		*/
		public ReferenceVector getAttachments()
		{
			return (ReferenceVector) m_attachments.clone();

		} // getAttachments

		/**
		* Add an attachment.
		* @param ref The attachment Reference.
		*/
		public void addAttachment(Reference ref)
		{
			m_attachments.add(ref);

		} // addAttachment

		/**
		* Remove an attachment.
		* @param ref The attachment Reference to remove (the one removed will equal this, they need not be ==).
		*/
		public void removeAttachment(Reference ref)
		{
			m_attachments.remove(ref);

		} // removeAttachment

		/**
		* Replace the attachment set.
		* @param attachments A vector of Reference objects that will become the new set of attachments.
		*/
		public void replaceAttachments(ReferenceVector attachments)
		{
			m_attachments.clear();

			if (attachments != null)
			{
				Iterator it = attachments.iterator();
				while (it.hasNext())
				{
					m_attachments.add(it.next());
				}
			}

		} // replaceAttachments

		/**
		* Clear all attachments.
		*/
		public void clearAttachments()
		{
			m_attachments.clear();

		} // clearAttachments

	} // BasicMessageHeaderEdit

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open and read.
		*/
		public void open();

		/**
		* Write and Close.
		*/
		public void close();

		/**
		* Return the identified channel, or null if not found.
		*/
		public MessageChannel getChannel(String ref);

		/**
		* Return true if the identified channel exists.
		*/
		public boolean checkChannel(String ref);

		/**
		* Get a list of all channels
		*/
		public List getChannels();

		/**
		* Keep a new channel.
		*/
		public MessageChannelEdit putChannel(String ref);

		/**
		* Get a channel locked for update
		*/
		public MessageChannelEdit editChannel(String ref);

		/**
		* Commit a channel edit.
		*/
		public void commitChannel(MessageChannelEdit edit);

		/**
		* Cancel a channel edit.
		*/
		public void cancelChannel(MessageChannelEdit edit);

		/**
		* Forget about a channel.
		*/
		public void removeChannel(MessageChannelEdit channel);

		/**
		* Get a message from a channel.
		*/
		public Message getMessage(MessageChannel channel, String messageId);

		/**
		 * Get a message from a channel (by reference).
		 */
		public Message getMessage(String channelRef, String messgeId);

		/**
		* Get a message from a channel locked for update
		*/
		public MessageEdit editMessage(MessageChannel channel, String messageId);

		/**
		* Commit an edit.
		*/
		public void commitMessage(MessageChannel channel, MessageEdit edit);

		/**
		* Cancel an edit.
		*/
		public void cancelMessage(MessageChannel channel, MessageEdit edit);

		/**
		* Does this messages exist in a channel?
		*/
		public boolean checkMessage(MessageChannel channel, String messageId);

		/**
		* Get the messages from a channel
		*/
		public List getMessages(MessageChannel channel);

		/**
		* Make and lock a new message.
		*/
		public MessageEdit putMessage(MessageChannel channel, String id);

		/**
		* Forget about a message.
		*/
		public void removeMessage(MessageChannel channel, MessageEdit edit);

		/**
		 * Get messages filtered by date and count and drafts, in descending (latest first) order
		 * @param afterDate if null, no date limit, else limited to only messages after this date.
		 * @param limitedToLatest if 0, no count limit, else limited to only the latest this number of messages.
		 * @param draftsForId how to handle drafts: null means no drafts, "*" means all, otherwise drafts only if
		 * created by this userId.
		 * @return A list of Message objects that meet the criteria; may be empty
		 */
		public List getMessages(String channelRef, Time afterDate, int limitedToLatest, String draftsForId);

		/**
		 * Access a list of channel ids from channels with refs that start with (match) context.
		 * @param context The root channel ref to match.
		 * @return A List (String) of channel id for channels within the context.
		 */
		public List getChannelIdsMatching(String root);

	} // Storage

	/*******************************************************************************
	* CacheRefresher implementation (no container)
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		Object rv = null;

		// key is a reference
		Reference ref = new Reference((String) key);

		// get from storage only (not cache!)

		// for a message
		if (REF_TYPE_MESSAGE.equals(ref.getSubType()))
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(
					this
						+ ".refresh(): key "
						+ key
						+ " channel id : "
						+ ref.getContext()
						+ "/"
						+ ref.getContainer()
						+ " message id : "
						+ ref.getId());

			// get channel (Note: from the cache is ok)
			MessageChannel channel = findChannel(channelReference(ref.getContext(), ref.getContainer()));

			// get the message (Note: not from cache! but only from storage)
			if (channel != null)
			{
				rv = m_storage.getMessage(channel, ref.getId());
			}
		}

		// for a channel
		else
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".refresh(): key " + key + " channel id : " + ref.getReference());

			// return the channel from channel getId() (Note: not from cache! but only from storage)
			rv = m_storage.getChannel(ref.getReference());
		}

		return rv;

	} // refresh

	/*******************************************************************************
	* filter
	*******************************************************************************/

	protected class MessagePermissionFilter implements Filter
	{
		/**
		* Does this object satisfy the criteria of the filter?
		*@param	o	The object to test.
		*@return		true if the object is accepted by the filter, false if not.
		*/
		public boolean accept(Object o)
		{
			// we want to test only messages
			if (!(o instanceof Message))
			{
				return false;
			}

			// if the item cannot be read, reject it
			if (!unlockCheck(SECURE_READ, ((Message) o).getReference()))
			{
				return false;
			}

			// accept this one
			return true;

		} // accept

	} // MessagePermissionFilter

	protected class MessageSelectionFilter implements Filter
	{
		protected Time m_afterDate = null;
		protected String m_draftsForId = null;

		public MessageSelectionFilter(Time afterDate, String draftsForId)
		{
			m_afterDate = afterDate;			
			m_draftsForId = draftsForId;
		}

		/**
		* Does this object satisfy the criteria of the filter?
		*@param	o	The object to test.
		*@return		true if the object is accepted by the filter, false if not.
		*/
		public boolean accept(Object o)
		{
			// we want to test only messages
			if (!(o instanceof Message))
			{
				return false;
			}

			if (m_afterDate != null)
			{
				if (!((Message) o).getHeader().getDate().after(m_afterDate))
				{
					return false;
				}
			}

			// if we don't want all drafts
			if (!"*".equals(m_draftsForId))
			{
				if (isDraft((Resource)o))
				{
					// reject all drafts?
					if (	(m_draftsForId == null)
						||	(!getOwnerId((Resource)o).equals(m_draftsForId)))
					{
						return false;
					}
				}
			}

			// accept this one
			return true;

		} // accept

	} // MessagePermissionFilter

} // MessageService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/message/BaseMessageService.java,v 1.42 2004/09/30 20:20:03 ggolden.umich.edu Exp $
*
**********************************************************************************/
