/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/chat/ChatChannel.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.chat;

// import
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.resource.ReferenceVector;

/**
* <p>ChatChannel is the extension to the MessageChanel interface for a CHEF
* Chat service chat channel.  Messages in the ChatChannel are ChatMessages with
* ChatMessageHeaders.</p>
* <p>Security is defined, see MessageChannel.</p>
* <p>Usage Events generated:<ul>
* <li>chat.message.channel.read - chat message resource id</li>
* <li>chat.message.channel.remove.any - chat message resource id</li>
* <li>chat.message.channel.remove.own - chat message resource id</li>
* <li>chat.message.channel.post - chat message resource id</li></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.generic.GenericChatService
* @see org.chefproject.core.ChatMessage
*/
public interface ChatChannel
	extends MessageChannel
{
	/**
	* A (ChatMessage) cover for getMessage to return a specific chat channel message,
	* as specified by message id.
	* @param messageId The id of the message to get.
	* @return the ChatMessage that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this chat channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	*/
	public ChatMessage getChatMessage(String messageId)
		throws IdUnusedException, PermissionException;

	/**
	* A (ChatMessageEdit) cover for editMessage.
	* Return a specific channel message, as specified by message name, locked for update.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param messageId The id of the message to get.
	* @return the Message that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	* @exception InUseException if the current user does not have permission to mess with this user.
	*/
	public ChatMessageEdit editChatMessage(String messageId)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* A (ChatMessageEdit) cover for addMessage.
	* Add a new message to this channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @return The newly added message, locked for update.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public ChatMessageEdit addChatMessage()
		throws PermissionException;

	/**
	* a (ChatMessage) cover for addMessage to add a new message to this channel.
	* @param attachments The message header attachments, a vector of Reference objects.
	* @param body The body text.
	* @return The newly added message.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public ChatMessage addChatMessage(ReferenceVector attachments, String body)
		throws PermissionException;

}	// ChatChannel

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/chat/ChatChannel.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
