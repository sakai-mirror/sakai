/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementMessage.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.announcement;

import org.sakaiproject.service.legacy.message.Message;

// import

/**
* <p>AnnouncementMessage is the Interface for a Sakai Announcement message.</p>
* <p>The announcement message has header fields (from, date) and a body (text).  Each
* message also has an id, unique within the channel.  All fields are read only.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public interface AnnouncementMessage
	extends Message
{
	/**
	* A (AnnouncementMessageHeader) cover for getHeader to access the announcement message header.
	* @return The announcement message header.
	*/
	public AnnouncementMessageHeader getAnnouncementHeader();

}	// AnnouncementMessage

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementMessage.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
*
**********************************************************************************/