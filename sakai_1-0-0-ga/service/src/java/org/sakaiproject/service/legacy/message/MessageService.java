/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageService.java,v 1.7 2004/09/01 04:38:41 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.message;

import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ResourceService;
import org.sakaiproject.service.legacy.time.Time;

// import

/**
* <p>GenericMessageService is the base interface for the different specific Sakai communications
* service such as Chat, Announcements, etc.</p>
* <p>The service manages a set of message channels, each containing a set of messages.</p>
* <p>Channels and Messages can be worked with through the MessageChannel and
* Message APIs and their extensions.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.7 $
*/
public interface MessageService
	extends ResourceService
{
	/** Security lock / event for reading channel / message. */
	public static final String SECURE_READ = "read";

	/** Security lock / event for adding channel / message. */
	public static final String SECURE_ADD = "new";

	/** Security lock / event for removing one's own message. */
	public static final String SECURE_REMOVE_OWN = "delete.own";

	/** Security lock / event for removing anyone's message or channel. */
	public static final String SECURE_REMOVE_ANY = "delete.any";

	/** Security lock / event for updating one's own message or the channel. */
	public static final String SECURE_UPDATE_OWN = "revise.own";

	/** Security lock / event for updating any message. */
	public static final String SECURE_UPDATE_ANY = "revise.any";

	/** Security lock / event for accessing someone elses draft. */
	public static final String SECURE_READ_DRAFT = "read.drafts";

	/** The Reference type for a channel. */
	public static final String REF_TYPE_CHANNEL = "channel";

	/** The Reference type for a messgae. */
	public static final String REF_TYPE_MESSAGE = "msg";

	/**
	* Return a list of all the defined channels.
	* @return a list of MessageChannel (or extension) objects (may be empty).
	*/
	public List getChannels();

	/**
	* check permissions for getChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to getChannel(channelId), false if not.
	*/
	public boolean allowGetChannel(String ref);

	/**
	* Return a specific channel.
	* @param ref The channel reference.
	* @return the MessageChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for any channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public MessageChannel getChannel(String ref)
		throws IdUnusedException, PermissionException;

	/**
	* check permissions for addChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to addChannel(channelId), false if not.
	*/
	public boolean allowAddChannel(String ref);

	/**
	* Add a new channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param ref The channel reference.
	* @return The newly created channel, locked for update.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public MessageChannelEdit addChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException;

	/**
	* check permissions for editChannel()
	* @param ref The channel reference.
	* @return true if the user is allowed to update the channel, false if not.
	*/
	public boolean allowEditChannel(String ref);

	/**
	* Return a specific channel, as specified by channel id, locked for update.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param ref The channel reference.
	* @return the Channel that has the specified id.
	* @exception IdUnusedException If this name is not a defined channel.
	* @exception PermissionException If the user does not have any permissions to edit the channel.
	* @exception InUseException if the channel is locked for edit by someone else.
	*/
	public MessageChannelEdit editChannel(String ref)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a MessageChannelEdit object, and release the lock.
	* The MessageChannelEdit is disabled, and not to be used after this call.
	* @param user The MessageChannelEdit object to commit.
	*/
	public void commitChannel(MessageChannelEdit edit);

	/**
	* Cancel the changes made to a MessageChannelEdit object, and release the lock.
	* The MessageChannelEdit is disabled, and not to be used after this call.
	* @param user The MessageChannelEdit object to cancel.
	*/
	public void cancelChannel(MessageChannelEdit edit);

	/**
	* Check permissions for removeChannel().
	* @param ref The channel reference.
	* @return true if the user is allowed to removeChannel(), false if not.
	*/
	public boolean allowRemoveChannel(String ref);

	/**
	* Remove a channel - it must be locked from editChannel().
	* @param channel The channel to remove.
	* @exception PermissionException if the user does not have permission to remove a channel.
	*/
	public void removeChannel(MessageChannelEdit channel)
		throws PermissionException;

	/**
	* Access the internal reference which can be used to access the channel from within the system.
	* @param context The context.
	* @param id The channel id.
	* @return The the internal reference which can be used to access the channel from within the system.
	*/
	public String channelReference(String context, String id);

	/**
	* Access the internal reference which can be used to access the message from within the system.
	* @param context The context.
	* @param channelId The channel id.
	* @param id The message id.
	* @return The the internal reference which can be used to access the message from within the system.
	*/
	public String messageReference(String context, String channelId, String id);

	/**
	* Access the internal reference which can be used to access the message from within the system.
	* @param channelRef The channel reference.
	* @param id The message id.
	* @return The the internal reference which can be used to access the message from within the system.
	*/
	public String messageReference(String channelRef, String id);

	/**
	* Get a message, given a reference.
	* This call avoids the need to have channel security, as long as the user has permissions to the message.
	* @param ref The message reference
	* @return The message.
	* @exception IdUnusedException If this reference does not identify a message.
	* @exception PermissionException If the user does not have any permissions to the message.
	*/
	public Message getMessage(Reference ref)
		throws IdUnusedException, PermissionException;
	
	/**
	* Get all view-permitted messages in a channel, given a channel reference.
	* This call avoids the need to have channel security, as long as the user has permissions to the message.
	* @param ref The channel reference string.
	* @return The List (Messge) of messages.
	* @exception IdUnusedException If this reference does not identify a channel.
	*/
	public List getMessages(String ref)
		throws IdUnusedException;

	/**
	* Cancel the changes made to a MessageEdit object, and release the lock.
	* The MessageEdit is disabled, and not to be used after this call.
	* @param user The MessageEdit object to cancel.
	*/
	public void cancelMessage(MessageEdit edit);

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
		throws PermissionException;

	/**
	 * Access a list of channel ids that are defined related to the context.
	 * @param context The context in which to search
	 * @return A List (String) of channel id for channels withing the context.
	 */
	public List getChannelIds(String context);

}	// MessageService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageService.java,v 1.7 2004/09/01 04:38:41 ggolden.umich.edu Exp $
*
**********************************************************************************/
