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
import org.sakaiproject.service.legacy.event.Event;
import org.w3c.dom.Element;

/**
* <p>NotificationAction is the helper class that does the notify() for a notification. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface NotificationAction
{
	/**
	* Set from an xml element.
	* @param el The xml element.
	*/
	public void set(Element el);

	/**
	* Set from another.
	* @param other The other to copy.
	*/
	public void set(NotificationAction other);

	/**
	* Make a new one like me.
	* @return A new action just like me.
	*/
	public NotificationAction getClone();

	/**
	* Fill this xml element with the attributes.
	* @param el The xml element.
	*/
	public void toXml(Element el);

	/**
	* Do the notification.
	* @param notification The notification responding to the event.
	* @param event The event that matched criteria to cause the notification.
	*/
	public void notify(Notification notification, Event event);

}   // NotificationAction


