/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveService.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.email;

// import
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>MailArchiveService is the extension to MessageService configured for the Mail Archive.</p>
* <p>Channels are MailArchiveChannels, and Messages are
* MailArchiveMessages with MailArchiveMessageHeaders.</p>
* <p>Security in the mail archive service, in addition to that defined in the channels,
* include:<ul>
* <li>mailarchive.channel.add</li></ul></p>
* <li>mailarchive.channel.remove</li></ul></p>
* <p>Usage Events are generated:<ul>
* <li>mailarchive.channel.add - mail archive channel resource id</li>
* <li>mailarchive.channel.remove - mail archive channel resource id</li></ul></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.MailArchiveChannel
*/
public interface MailArchiveService
	extends MessageService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = MailArchiveService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "mailarchive";

	/** Security lock / event root for generic message events to make it a mail event. */
	public static final String SECURE_MAIL_ROOT = "mail.";

	/** Security lock / event for reading channel / message. */
	public static final String SECURE_MAIL_READ = SECURE_MAIL_ROOT + SECURE_READ;

	/** Security lock / event for adding channel / message. */
	public static final String SECURE_MAIL_ADD = SECURE_MAIL_ROOT + SECURE_ADD;

	/** Security lock / event for removing one's own message. */
	public static final String SECURE_MAIL_REMOVE_OWN = SECURE_MAIL_ROOT + SECURE_REMOVE_OWN;

	/** Security lock / event for removing anyone's message or channel. */
	public static final String SECURE_MAIL_REMOVE_ANY = SECURE_MAIL_ROOT + SECURE_REMOVE_ANY;

	/** Security lock / event for updating one's own message or the channel. */
	public static final String SECURE_MAIL_UPDATE_OWN = SECURE_MAIL_ROOT + SECURE_UPDATE_OWN;

	/** Security lock / event for updating any message. */
	public static final String SECURE_MAIL_UPDATE_ANY = SECURE_MAIL_ROOT + SECURE_UPDATE_ANY;

	/** Security lock / event for accessing someone elses draft. */
	public static final String SECURE_MAIL_READ_DRAFT = SECURE_MAIL_ROOT + SECURE_READ_DRAFT;
	
	/** Message header that indicates the original outer-envelope Content-Type of an archived message */
	public static final String HEADER_OUTER_CONTENT_TYPE = "X-Content-Type-Outer-Envelope";
	
	/** Message header that indicates the Content-Type of the message body of an archived message - 
	 * this may be different from the original outer-envelope Content-Type (outer might be multipart)
	 */
	public static final String HEADER_INNER_CONTENT_TYPE = "X-Content-Type-Message-Body";
	
	/**
	* A (MailArchiveChannel) cover for getChannel() to return a specific mail archive group.
	* @param ref The channel reference.
	* @return the MailArchiveChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for a mail archive channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public MailArchiveChannel getMailArchiveChannel(String ref)
		throws IdUnusedException, PermissionException;

	/**
	* A (MailArchiveChannel) cover for add() to add a new announcement channel.
	* @param ref The channel reference.
	* @return The newly created group.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a group.
	*/
	public MailArchiveChannelEdit addMailArchiveChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException;

}	// MailArchiveService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveService.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
*
**********************************************************************************/
