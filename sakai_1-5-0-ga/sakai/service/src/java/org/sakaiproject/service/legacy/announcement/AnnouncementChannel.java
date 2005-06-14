/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementChannel.java,v 1.6 2004/06/22 03:14:41 ggolden Exp $
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
package org.sakaiproject.service.legacy.announcement;

// import
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.resource.ReferenceVector;

/**
* <p>AnnouncementChannel is the extension to the MessageChanel interface for a Sakai
* Announcement service announcement channel.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
* @see org.sakaiproject.core.AnnouncementMessage
*/
public interface AnnouncementChannel
	extends MessageChannel
{
	/**
	* A (AnnouncementMessage) cover for getMessage to return a specific announcement channel message,
	* as specified by message id.
	* @param messageId The id of the message to get.
	* @return the AnnouncementMessage that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this announcement channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	*/
	public AnnouncementMessage getAnnouncementMessage(String messageId)
		throws IdUnusedException, PermissionException;

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
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* a (AnnouncementMessage) cover for addMessage to add a new message to this channel.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @return The newly added message, locked for update.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public AnnouncementMessageEdit addAnnouncementMessage()
		throws PermissionException;

	/**
	* a (AnnouncementMessage) cover for addMessage to add a new message to this channel.
	* @param subject The message header subject.
	* @param draft The message header draft indication.
	* @param attachments The message header attachments, a vector of Reference objects.
	* @param body The message body.
	* @return The newly added message.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public AnnouncementMessage addAnnouncementMessage(String subject,
				boolean draft, ReferenceVector attachments, String body)
		throws PermissionException;

}	// AnnouncementChannel

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementChannel.java,v 1.6 2004/06/22 03:14:41 ggolden Exp $
*
**********************************************************************************/
