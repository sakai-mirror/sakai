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
package org.sakaiproject.service.legacy.email;

// import
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>MailArchiveChannel is the extension to the MessageChannel interface for a CHEF
* Mail Archive service.
* Messages in the MailArchiveChannel are MailArchiveMessages with
* MailArchiveMessageHeaders.</p>
* <p>Security on the channel include:<ul>
* <li>mailarchive.channel.read</li>
* <li>mailarchive.channel.remove.any</li>
* <li>mailarchive.channel.remove.own</li>
* <li>mailarchive.channel.post</li>
* </ul>Security Roles for the channel include:<ul>
* <li>mailarchive.member: read, remove.own, post</li>
* <li>mailarchive.administrator: mailarchive.member, remove.any</li></ul></p>
* <p>Usage Events generated:<ul>
* <li>mailarchive.channel.read - mailarchive message resource id</li>
* <li>mailarchive.channel.remove.any - mailarchive message resource id</li>
* <li>mailarchive.channel.remove.own - mailarchive message resource id</li>
* <li>mailarchive.channel.post - mailarchive message resource id</li></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.MailArchiveMessage
*/
public interface MailArchiveChannel
	extends MessageChannel
{
	/**
	* A (MailArchiveMessage) cover for getMessage to return a specific mail archive group message,
	* as specified by message id.
	* @param messageId The id of the message to get.
	* @return the MailArchiveMessage that has the specified id.
	* @exception IdUnusedException If this name is not a defined message in this announcement channel.
	* @exception PermissionException If the user does not have any permissions to read the message.
	*/
	public MailArchiveMessage getMailArchiveMessage(String messageId)
		throws IdUnusedException, PermissionException;

	/**
	* a (MailArchiveMessage) cover for addMessage to add a new message to this channel.
	* @param subject The message header subject.
	* @param fromAddress The mail from: address from the message.
	* @param dateSent The date: sent from the message.
	* @param mailHeaders The full set of mail headers from the message.
	* @param attachments The message header attachments, a vector of Reference objects.
	* @param body The message body.
	* @return The newly added message.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public MailArchiveMessage addMailArchiveMessage(String subject,
					String fromAddress, Time dateSent, List mailHeaders,
					List attachments, String body)
		throws PermissionException;

	/** @return true if the channel enabled, false if not. */
	public boolean getEnabled();
	
	/** @return true if the channel is open to messages from outside the membership, false if not. */
	public boolean getOpen();

	/**
	* check permissions for addMessage() for the given user.
	* @param user The user.
	* @return true if the specified user is allowed to addMessage(...), false if not.
	*/
	public boolean allowAddMessage(User user);

}	// MailArchiveChannel



