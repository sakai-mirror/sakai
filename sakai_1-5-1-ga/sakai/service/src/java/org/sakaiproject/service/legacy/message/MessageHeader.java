/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageHeader.java,v 1.6 2004/09/10 03:59:32 ggolden.umich.edu Exp $
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

// import
import java.util.Stack;

import org.sakaiproject.service.legacy.resource.AttachmentContainer;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>MessageHeader is the base Interface for a Sakai Message headers.  Header fields common
* to all message service message headers are defined here.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public interface MessageHeader extends AttachmentContainer
{
	/**
	* Access the unique (within the channel) message id.
	* @return The unique (within the channel) message id.
	*/
	public String getId();

	/**
	* Access the date/time the message was sent to the channel.
	* @return The date/time the message was sent to the channel.
	*/
	public Time getDate();

	/**
	* Access the User who sent the message to the channel.
	* @return The User who sent the message to the channel.
	*/
	public User getFrom();

	/**
	* Access the draft status of the message.
	* @return True if the message is a draft, false if not.
	*/
	public boolean getDraft();

	/**
	* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	* @param doc The DOM doc to contain the XML (or null for a string return).
	* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
	* @return The newly added element.
	*/
	public Element toXml(Document doc, Stack stack);

} // MessageHeader

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/message/MessageHeader.java,v 1.6 2004/09/10 03:59:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
