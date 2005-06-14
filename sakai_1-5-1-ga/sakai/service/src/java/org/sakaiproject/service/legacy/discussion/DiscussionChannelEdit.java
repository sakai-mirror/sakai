/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionChannelEdit.java,v 1.5.4.1 2005/03/21 18:44:34 lance.indiana.edu Exp $
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

// import
import java.util.List;

import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;

/**
* <p>DiscussionChannel is an editable DiscussionChannel</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5.4.1 $
*/
public interface DiscussionChannelEdit
	extends DiscussionChannel, MessageChannelEdit
{
	/**
	* Remove a category for this channel.
	* @param category The category string to be removed.
	* @return true if the category can be removed, false if not.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public boolean removeCategory(String category)
		throws PermissionException;
	
	/**
	* Set categories for this channel.
	* @param category The category string to be set.
	* @exception PermissionException If the user does not have write permission to the channel.
	*/
	public void setCategories(List categories)
		throws PermissionException;
	
}	// DiscussionChannelEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionChannelEdit.java,v 1.5.4.1 2005/03/21 18:44:34 lance.indiana.edu Exp $
*
**********************************************************************************/
