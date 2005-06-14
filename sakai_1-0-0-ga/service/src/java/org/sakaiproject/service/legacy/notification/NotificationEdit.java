/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/notification/NotificationEdit.java,v 1.4 2004/06/22 03:14:49 ggolden Exp $
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
package org.sakaiproject.service.legacy.notification;

// imports
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.resource.Edit;

/**
* <p>NotificationEdit is a modifiable Notification. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface NotificationEdit
	extends Notification, Edit
{
	/**
	* Do the notification.
	* @param event The event that matched criteria to cause the notification.
	*/
	public void notify(Event event);

	/**
	* Set the Event function, clearing any that have already been set.
	* @param event The Event function to watch for.
	*/
	public void setFunction(String function);

	/**
	* Add another Event function.
	* @param event Another Event function to watch for.
	*/
	public void addFunction(String function);

	/**
	* Set the resource reference filter.
	* @param filter The resource reference filter.
	*/
	public void setResourceFilter(String filter);

	/**
	* Set the action helper that handles the notify() action.
	* @param action The action helper that handles the notify() action.
	*/
	public void setAction(NotificationAction action);

}   // Notification

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/notification/NotificationEdit.java,v 1.4 2004/06/22 03:14:49 ggolden Exp $
*
**********************************************************************************/
