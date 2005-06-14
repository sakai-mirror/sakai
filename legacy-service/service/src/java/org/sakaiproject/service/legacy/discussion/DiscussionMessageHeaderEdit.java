/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionMessageHeaderEdit.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.message.MessageHeaderEdit;

/**
* <p>DiscussionMessageHeaderEdit is an editable DiscussionMessageHeader</p> 
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface DiscussionMessageHeaderEdit
	extends DiscussionMessageHeader, MessageHeaderEdit
{
	/**
	* Set the subject of the discussion message.
	* @param subject The subject of the discussion message.
	*/
	public void setSubject(String subject);

	/**
	* Set the category of the discussion message.
	* @param category The category of the discussion message.
	*/
	public void setCategory(String category);

	/**
	* Set the local or resource id of the message this one is a reply to, used in threading.
	* @return The id of the message this one is a reply to, used in threading, or null if none.
	*/
	public void setReplyTo(String id);

}	// DiscussionMessageHeaderEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionMessageHeaderEdit.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
