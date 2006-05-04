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
import org.sakaiproject.service.legacy.message.Message;

/**
* <p>MailArchiveMessage is the Interface for a CHEF Mail Archive message.</p>
* <p>The mail archive message has header fields (from, date) and a body (text).  Each
* message also has an id, unique within the group.  All fields are read only.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface MailArchiveMessage
	extends Message
{
	/**
	* A (MailArchiveMessageHeader) cover for getHeader to access the mail archive message header.
	* @return The mail archive message header.
	*/
	public MailArchiveMessageHeader getMailArchiveHeader();

}	// MailArchiveMessage



