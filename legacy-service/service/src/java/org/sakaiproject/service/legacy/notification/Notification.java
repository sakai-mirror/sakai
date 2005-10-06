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
package org.sakaiproject.service.legacy.notification;

// imports
import java.util.List;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.event.Event;

/**
* <p>Notification the interface for classes that act to notify, used with the GenericNotificationService. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface Notification
	extends Entity
{
	/**
	* Do the notification.
	* @param event The event that matched criteria to cause the notification.
	*/
	public void notify(Event event);

	/**
	* Get the Event function.  Only Events with this function code will trigger the notification.
	* @return The Event function to watch for.
	*/
	public String getFunction();

	/**
	* Get all the Event functions for this notification.
	* @return a List (String) of Event functions to watch for.
	*/
	public List getFunctions();

	/**
	* Check if the notification watches for events with this function code.
	* @param event The Event function to test.
	* @return true if this notification watches for evens with this function code, false if not.
	*/
	public boolean containsFunction(String function);

	/**
	* Get the resource reference filter.  Only Events with references matching this will trigger the notification.
	* @return The resource reference filter.
	*/
	public String getResourceFilter();

	/**
	* Get the action helper that handles the notify() action.
	* @return The action helper that handles the notify() action.
	*/
	public NotificationAction getAction();

}   // Notification



