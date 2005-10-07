/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004,2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.service.legacy.message;

import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.entity.AttachmentContainerEdit;
import org.sakaiproject.service.legacy.site.Section;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
 * <p>
 * MessageHeader is the base Interface for a Sakai Message headers. Header fields common to all message service message headers are defined here.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface MessageHeaderEdit extends MessageHeader, AttachmentContainerEdit
{
	/**
	 * Set the date/time the message was sent to the channel.
	 * 
	 * @param date
	 *        The date/time the message was sent to the channel.
	 */
	void setDate(Time date);

	/**
	 * Set the User who sent the message to the channel.
	 * 
	 * @param user
	 *        The User who sent the message to the channel.
	 */
	void setFrom(User user);

	/**
	 * Set the draft status of the message.
	 * 
	 * @param draft
	 *        True if the message is a draft, false if not.
	 */
	void setDraft(boolean draft);

	/**
	 * Add a Section to the list of sections for this message.
	 * 
	 * @param section
	 *        The Section to add to those for this message.
	 * @throws PermissionException
	 *         if the end user does not have ADD permission for the message in the section.
	 */
	void addSection(Section section) throws PermissionException;

	/**
	 * Remove this Section from the list of sections for this message.
	 * 
	 * @param section
	 *        The Section to remove from those for this message.
	 * @throws PermissionException
	 *         if the end user does not have ADD permission for the message in the section.
	 */
	void removeSection(Section section) throws PermissionException;

	/**
	 * Set the access mode for the message - how we compute who has access to the message.
	 * 
	 * @param access
	 *        The MessageAccess access mode for the message.
	 */
	void setAccess(MessageAccess access);
}
