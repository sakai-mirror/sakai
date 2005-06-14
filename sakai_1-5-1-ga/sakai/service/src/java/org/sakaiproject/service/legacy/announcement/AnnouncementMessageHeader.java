/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementMessageHeader.java,v 1.7 2004/09/10 03:59:31 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.message.MessageHeader;

/**
* <p>AnnouncementMessageHeader is the Interface for a Sakai Announcement Message header.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.7 $
*/
public interface AnnouncementMessageHeader
	extends MessageHeader
{
	/**
	* Access the subject of the announcement.
	* @return The subject of the announcement.
	*/
	public String getSubject();

}	// AnnouncementMessageHeader

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/announcement/AnnouncementMessageHeader.java,v 1.7 2004/09/10 03:59:31 ggolden.umich.edu Exp $
*
**********************************************************************************/
