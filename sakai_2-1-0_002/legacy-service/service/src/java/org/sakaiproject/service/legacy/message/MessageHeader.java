/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
import java.util.Collection;
import java.util.Stack;

import org.sakaiproject.service.legacy.entity.AttachmentContainer;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * MessageHeader is the base Interface for a Sakai Message headers. Header fields common to all message service message headers are defined here.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface MessageHeader extends AttachmentContainer
{
	/**
	 * <p>
	 * MessageAccess enumerates different access modes for the message: channel-wide or grouped.
	 * </p>
	 */
	public class MessageAccess
	{
		private final String m_id;

		private MessageAccess(String id)
		{
			m_id = id;
		}

		public String toString()
		{
			return m_id;
		}

		static public MessageAccess fromString(String access)
		{
			// if (PUBLIC.m_id.equals(access)) return PUBLIC;
			if (CHANNEL.m_id.equals(access)) return CHANNEL;
			if (GROUPED.m_id.equals(access)) return GROUPED;
			return null;
		}

		/** public access to the message: pubview */
		// public static final MessageAccess PUBLIC = new MessageAccess("public");

		/** channel (site) level access to the message */
		public static final MessageAccess CHANNEL = new MessageAccess("channel");

		/** grouped access; only members of the getGroup() groups (authorization groups) have access */
		public static final MessageAccess GROUPED = new MessageAccess("grouped");
	}

	/**
	 * Access the unique (within the channel) message id.
	 * 
	 * @return The unique (within the channel) message id.
	 */
	String getId();

	/**
	 * Access the date/time the message was sent to the channel.
	 * 
	 * @return The date/time the message was sent to the channel.
	 */
	Time getDate();

	/**
	 * Access the User who sent the message to the channel.
	 * 
	 * @return The User who sent the message to the channel.
	 */
	User getFrom();

	/**
	 * Access the draft status of the message.
	 * 
	 * @return True if the message is a draft, false if not.
	 */
	boolean getDraft();

	/**
	 * Access the groups defined for this message.
	 * 
	 * @return A Collection (String) of group refs (authorization group ids) defined for this message; empty if none are defined.
	 */
	Collection getGroups();

	/**
	 * Access the access mode for the message - how we compute who has access to the message.
	 * 
	 * @return The MessageAccess access mode for the message.
	 */
	MessageAccess getAccess();

	/**
	 * Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	 * 
	 * @param doc
	 *        The DOM doc to contain the XML (or null for a string return).
	 * @param stack
	 *        The DOM elements, the top of which is the containing element of the new "resource" element.
	 * @return The newly added element.
	 */
	Element toXml(Document doc, Stack stack);
}
