/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveMessageHeader.java,v 1.5 2004/06/22 03:14:47 ggolden Exp $
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

// imports
import java.util.List;

import org.sakaiproject.service.legacy.message.MessageHeader;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>MailArchiveMessageHeader is the Interface for a CHEF Mail Archive Message header.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface MailArchiveMessageHeader
	extends MessageHeader
{
	/**
	* Access the subject of the message.
	* @return The subject of the message.
	*/
	public String getSubject();

	/**
	* Access the from: address of the message.
	* @return The from: address of the message.
	*/
	public String getFromAddress();

	/**
	* Access the date: sent of the message.
	* @return The date: sent of the message.
	*/
	public Time getDateSent();

	/**
	* Access the entire set of mail headers the message.
	* @return The entire set of mail headers of the message (List of String).
	*/
	public List getMailHeaders();

}	// MailArchiveMessageHeader

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveMessageHeader.java,v 1.5 2004/06/22 03:14:47 ggolden Exp $
*
**********************************************************************************/
