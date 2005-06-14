/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionMessageHeader.java,v 1.5 2004/09/10 03:59:32 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.message.MessageHeader;

/**
* <p>DiscussionMessageHeader is the Interface for a CHEF Discussion Message header.</p>
* <p>In addition to the usual body, from and time fields of MessageHeader, discussion messages have:</p>
* <p>Category: a selection from a limited set of strings which categorizes a message.</p>
* <p>Subject: an unrestricted user entered text that describes the message.</p>
* <p>ReplyTo: a message reference to which this message is a direct reply.</p>
* <p>Draft: a value that lets us have messages that are stored but are still in the process of being
* created and are not publically available yet.</p> 
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface DiscussionMessageHeader
	extends MessageHeader
{
	/**
	* Access the subject of the discussion message.
	* @return The subject of the discussion message.
	*/
	public String getSubject();

	/**
	* Access the category of the discussion message.
	* @return The category of the discussion message.
	*/
	public String getCategory();

	/**
	* Access the local or resource id of the message this one is a reply to, used in threading.
	* @return The id of the message this one is a reply to, used in threading, or null if none.
	*/
	public String getReplyTo();

}	// DiscussionMessageHeader

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionMessageHeader.java,v 1.5 2004/09/10 03:59:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
