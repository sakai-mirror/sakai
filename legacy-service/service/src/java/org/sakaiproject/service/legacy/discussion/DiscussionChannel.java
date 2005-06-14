/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionChannel.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.discussion;

// import
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.resource.ReferenceVector;

/**
* <p>DiscussionChannel is the extension to the MessageChanel interface for a CHEF
* Discussion service discussion channel.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface DiscussionChannel
	extends MessageChannel
{
	/**
	* A (DiscussionMessage) cover for getMessage to return a specific discussion channel message,
	* as specified by message id.
	* @param messageId The id of the message to get.
	* @return the DiscussionMessage that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this discussion channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	*/
	public DiscussionMessage getDiscussionMessage(String messageId)
		throws IdUnusedException, PermissionException;
	
	/**
	* A (DiscussionMessage) cover for getMessage to return a specific discussion channel message,
	* as specified by message id, and ignore exceptions if any
	* @param messageId The id of the message to get.
	* @return the DiscussionMessage that has the specified id.
	*/
	public DiscussionMessage getDiscussionMessageNoException(String messageId);

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
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* check permissions for addMessage() - if the message is a new Topic.
	* @return true if the user is allowed to addMessage(...) for a new Topic, false if not.
	*/
	public boolean allowAddTopicMessage();
	
	/**
	* check permissions for removing category
	* @return true if the user is allowed to remove category, false if not.
	*/
	public boolean allowRemoveCategory();

	/**
	* Add a new category for this channel.
	* @param category The new category string to add.
	* @return true if the category was new, false if it is already there.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public boolean addCategory(String category)
		throws PermissionException;

	/**
	* Remove a category for this channel.
	* @param category The category string to be removed.
	* @return true if the category can be removed, false if not.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public boolean removeCategory(String category)
		throws PermissionException;
	
	/**
	* A (DiscussionMessage) cover for addMessage to add a new message to this channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param replyTo The message id to which this message is a reply.
	* @return The newly added message, locked for update.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public DiscussionMessageEdit addDiscussionMessage(String replyTo)
		throws PermissionException;

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
		throws PermissionException;

	/**
	* Access the list of catagories defined in the channel.
	* @param scanMessage if true, scan the channel's messages for catagories set in messages that may not be in the
	* channel's catagory list, if false, use just the channel's catagory list.
	* @return A List (String) of all the categories defined in the channel.
	*/
	public List getCategories(boolean scanMessages);

	/**
	 * Access the message ids for the topics (i.e. messages that are not a response to any other message) in the category.
	 * @param category The category value.
	 * @return A List (Sting) of message id values for each message that is a topic in the category.
	 */
	public List getTopicMsgIds(String category);

	/**
	* Return an Iterator on the DiscussionMessages that are "topics", i.e.
	* are not a response to any other message.
	* Note: use DiscussionMessage.getReplies() to get the direct replies to any
	* specific message.
	* @param category Filter the responses to messages in just this category (optional, may be null).
	* @return an Iterator on the DiscussionMessages that are "topics", i.e.
	* are not a response to any other message.
	*/
	public Iterator getTopics(String category);

	/**
	* Return an iterator on the DiscussionMessages that are in the response thread
	* to the specified message.  They will be returned in depth first order.
	* @param message The message to which those returned are some depth of response.
	* @return an iterator on the DiscussionMessages that are in the response thread
	* to the specified topic messages, in depth first order (may be empty).
	*/
	public Iterator getThread(DiscussionMessage message);

	/**
	* Access the most recent reply to the thread descending from this message, if any.
	* @param message The message that forms the top of a thread.
	* @return the most recent DiscussionMessage reply to this thread, or null if there are no replies.
	*/
	public DiscussionMessage getThreadLatestReply(DiscussionMessage message);

	/**
	* Count the number of replies to this thread (i.e. the size of the getThread() iterator).
	* @param message The message that forms the top of a thread.
	* @return the number of replies to this thread.
	*/
	public int getThreadNumberOfReplies(DiscussionMessage message);

	/**
	* Return an iterator on all messages in thread - depth first order.
	* @param category Filter the responses to messages in just this category (optional, may be null).
	* @return an iterator on all messages in thread - depth first order
	* (may be empty).
	*/
	public Iterator getThreads(String category);

}	// DiscussionChannel

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionChannel.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
