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
package org.sakaiproject.service.legacy.chat;

// import
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.message.MessageService;

/**
* <p>ChatService is the extension to GenericMessageService configured for Chat.</p>
* <p>MessageChannels are ChatMessageChannels, and Messages are ChatMessages with
* ChatMessageHeaders.</p>
* <p>Security is defined, see MessageService.</p>
* <p>Usage Events are generated:<ul>
* <li>chat.message.channel.add - chat channel resource id</li>
* <li>chat.message.channel.remove - chat channel resource id</li></ul></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.ChatChannel
* @see org.chefproject.core.ChatMessage
*/
public interface ChatService
	extends MessageService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ChatService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Entity.SEPARATOR + "chat";

	/**
	* A (ChatChannel) cover for getChannel() to return a specific chat channel.
	* @param ref The channel reference.
	* @return the ChatChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for a chat channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public ChatChannel getChatChannel(String ref)
		throws IdUnusedException, PermissionException;

	/**
	* A (ChatChannel) cover for addChannel() to add a new chat channel.
	* @param ref The channel reference.
	* @return The newly created channel.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public ChatChannelEdit addChatChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException;

}	// ChatService



