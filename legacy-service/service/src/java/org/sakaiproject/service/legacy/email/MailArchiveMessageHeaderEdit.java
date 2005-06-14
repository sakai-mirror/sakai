/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveMessageHeaderEdit.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.message.MessageHeaderEdit;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>MailArchiveMessageHeader is the Interface for a CHEF Mail Archive Message header.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public interface MailArchiveMessageHeaderEdit
	extends MailArchiveMessageHeader, MessageHeaderEdit
{
	/**
	* Set the subject of the message.
	* @param subject The subject of the message.
	*/
	public void setSubject(String subject);

	/**
	* Set the the from: address  of the message.
	* @param from The from: address  of the message.
	*/
	public void setFromAddress(String from);

	/**
	* Set the date: sent  of the message.
	* @param sent The the date: sent of the message.
	*/
	public void setDateSent(Time sent);

	/**
	* Set the entire set of mail headers of the message.
	* @param headers The the entire set of mail headers of the message (List of String).
	*/
	public void setMailHeaders(List headers);

}	// MailArchiveMessageHeaderEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/email/MailArchiveMessageHeaderEdit.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
*
**********************************************************************************/
