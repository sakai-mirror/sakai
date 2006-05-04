/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageChannel.java,v 1.4 2004/06/22 03:14:48 ggolden Exp $
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

// import
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.util.Filter;
import org.w3c.dom.Element;

/**
* <p>MessageChannel is the base interface for Sakai communications service message channels.
* Extensions to this interface configure types of communications channels (Chat, Announcements,
* etc.)</p>
* <p>Channels contains collections of messages, each Message )or extension) object.
* Each chat channel has a unique channel id (read only), and is a Sakai Resource.</p>
* <p>The chat channel can be asked:<ul>
* <li>for an iterator on the messages, with a filter</li>
* <li>to find a specific message</li>
* <li>to add a new message</li>
* <li>to update an existing message</li>
* <li>to remove an existing message</li></ul></p>
* <p>The chat channel can be subscribed to providing notification when:<ul>
* <li>a new message has been posted</li>
* <li>a message has been changed</li>
* <li>a message has been removed</li>
* <li>the channel has been removed</li></ul></p>
* <p>Security on the channel include:<ul>
* <li>message.channel.read</li>
* <li>message.channel.remove.any</li>
* <li>message.channel.remove.own</li>
* <li>message.channel.post</li>
* </ul>Security Roles for the channel include:<ul>
* <li>message.member: read, remove.own, post</li>
* <li>message.administrator: chat.member, remove.any</li></ul></p>
* <p>Event handling is defined in the specific extension classes.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.4 $
* @see org.sakaiproject.service.legacy.MessageService
* @see org.sakaiproject.core.Message
*/
public interface MessageChannel
	extends Resource
{
	/**
	* Access the context of the resource.
	* @return The context.
	*/
	public String getContext();

	/**
	* check permissions for getMessages() or getMessage().
	* @return true if the user is allowed to get messages from this channel, false if not.
	*/
	public boolean allowGetMessages();

	/**
	* Return a list of all or filtered messages in the channel.
	* The order in which the messages will be found in the iteration is by date, oldest
	* first if ascending is true, newest first if ascending is false.
	* @param filter A filtering object to accept messages, or null if no filtering is desired.
	* @param ascending Order of messages, ascending if true, descending if false
	* @return a list of channel Message objects or specializations of Message objects (may be empty).
	* @exception PermissionException if the user does not have read permission to the channel.
	*/
	public List getMessages(Filter filter, boolean ascending)
		throws PermissionException;

	/**
	* Return a specific channel message, as specified by message name.
	* @param messageId The id of the message to get.
	* @return the Message that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	*/
	public Message getMessage(String messageId)
		throws IdUnusedException, PermissionException;

	/**
	* check permissions for editMessage()
	* @param id The message id.
	* @return true if the user is allowed to update the message, false if not.
	*/
	public boolean allowEditMessage(String messageId);

	/**
	* Return a specific channel message, as specified by message name, locked for update.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param messageId The id of the message to get.
	* @return the Message that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this channel.
	* @exception PermissionException If the user does not have any permissions to edit the message.
	* @exception InUseException if the message is locked for edit by someone else.
	*/
	public MessageEdit editMessage(String messageId)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a MessageEdit object, and release the lock.
	* The MessageEdit is disabled, and not to be used after this call.
	* @param user The UserEdit object to commit.
	*/
	public void commitMessage(MessageEdit edit);

	/**
	* Commit the changes made to a MessageEdit object, and release the lock.
	* The MessageEdit is disabled, and not to be used after this call.
	* @param user The UserEdit object to commit.
	* @param priority The notification priority for this commit.
	*/
	public void commitMessage(MessageEdit edit, int priority);

	/**
	* Cancel the changes made to a MessageEdit object, and release the lock.
	* The MessageEdit is disabled, and not to be used after this call.
	* @param user The UserEdit object to commit.
	*/
	public void cancelMessage(MessageEdit edit);

	/**
	* check permissions for addMessage().
	* @return true if the user is allowed to addMessage(...), false if not.
	*/
	public boolean allowAddMessage();

	/**
	* Add a new message to this channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @return The newly added message, locked for update.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public MessageEdit addMessage()
		throws PermissionException;

	/**
	* check permissions for removeMessage().
	* @param message The message from this channel to remove.
	* @return true if the user is allowed to removeMessage(...), false if not.
	*/
	public boolean allowRemoveMessage(Message message);

	/**
	* Remove a message from the channel - it must be locked from editMessage().
	* @param message The message from this channel to remove.
	* @exception PermissionException if the user does not have permission to remove the message.
	*/
	public void removeMessage(MessageEdit message)
		throws PermissionException;

	/**
	* Merge in a new message as defined in the xml.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The message information in XML in a DOM element.
	* @return The newly added message, locked for update.
	* @exception PermissionException If the user does not have write permission to the channel.
	* @exception IdUsedException if the user id is already used.
	*/
	public MessageEdit mergeMessage(Element el)
		throws PermissionException, IdUsedException;

}	// MessageChannel

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageChannel.java,v 1.4 2004/06/22 03:14:48 ggolden Exp $
*
**********************************************************************************/