/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/message/MessageHeaderEdit.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.message;

import org.sakaiproject.service.legacy.resource.AttachmentContainerEdit;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;

// import

/**
* <p>MessageHeader is the base Interface for a Sakai Message headers.  Header fields common
* to all message service message headers are defined here.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface MessageHeaderEdit extends MessageHeader, AttachmentContainerEdit
{
	/**
	* Set the date/time the message was sent to the channel.
	* @param date The date/time the message was sent to the channel.
	*/
	public void setDate(Time date);

	/**
	* Set the User who sent the message to the channel.
	* @param user The User who sent the message to the channel.
	*/
	public void setFrom(User user);

	/**
	* Set the draft status of the message.
	* @param draft True if the message is a draft, false if not.
	*/
	public void setDraft(boolean draft);

} // MessageHeaderEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/message/MessageHeaderEdit.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
*
**********************************************************************************/
