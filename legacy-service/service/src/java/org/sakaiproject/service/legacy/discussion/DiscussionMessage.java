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
package org.sakaiproject.service.legacy.discussion;

// import
import java.util.Iterator;

import org.sakaiproject.service.legacy.message.Message;

/**
* <p>DiscussionMessage is the Interface for a CHEF Discussion message.</p>
* <p>The discussion message has header fields (from, date) and a body (text).  Each
* message also has an id, unique within the channel.  All fields are read only.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface DiscussionMessage
	extends Message
{
	/**
	* A (DiscussionMessageHeader) cover for getHeader to access the discussion message header.
	* @return The discussion message header.
	*/
	public DiscussionMessageHeader getDiscussionHeader();

	/**
	* Iterate over the messages that are a reply to this message
	* @return an Iterator on DiscussionMessage objects that are replies to this message
	* (may be empty).
	*/
	public Iterator getReplies();

	/**
	* Are there any replys to this message?
	* @return true if there are replies to this message, false if now.
	*/
	public boolean hasReplies();

	/**
	* Return the depth of replyTo value for this message.
	* Messages that are not a reply to any other message have depth = 0.
	* If the message is a reply to a message, this message's depth is the replyTo's depth + 1.
	* @return The depth of replyTo value for this message.
	*/
	public int getReplyToDepth();

	/**
	* Access the most recent reply to this message, if any.
	* @return the most recent DiscussionMessage reply to this message, or null if there are no replies.
	*/
	public DiscussionMessage getLatestReply();

	/**
	* Count the number of replies to this message (i.e. the size of the getReplies iterator).
	* @return the number of replies to this message.
	*/
	public int getNumberOfReplies();

}   // DiscussionMessage



