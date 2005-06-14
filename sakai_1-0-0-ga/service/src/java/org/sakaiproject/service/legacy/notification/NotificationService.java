/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/notification/NotificationService.java,v 1.4 2004/06/22 03:14:49 ggolden Exp $
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
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>NotificationService is ... %%%</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface NotificationService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = NotificationService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "notification";

	/** ability / event for notification add. */
	public static final String SECURE_ADD_NOTIFICATION = "notification.add";

	/** ability / event for notification update. */
	public static final String SECURE_UPDATE_NOTIFICATION = "notification.update";

	/** ability / event for notification removal. */
	public static final String SECURE_REMOVE_NOTIFICATION = "notification.remove";

	/** Notification option value for undefined or no notification. */
	public static final int NOTI_NONE = 0;

	/** Notification option value for required notification. */
	public static final int NOTI_REQUIRED = 1;

	/** Notification option value for optional notification. */
	public static final int NOTI_OPTIONAL = 2;

	/** Notification option value for undefined notification. */
	public static final int PREF_NONE = 0;

	/** Notification preference value for blocking notification. */
	public static final int PREF_IGNORE = 1;

	/** Notification preference value for digest notification. */
	public static final int PREF_DIGEST = 2;

	/** Notification preference value for immediate notification. */
	public static final int PREF_IMMEDIATE = 3;

	/** Preferences key for default notification prefs. */
	public static final String PREFS_DEFAULT = "noti:default";

	/** Preferences key for default for a resource type notification prefs - append the resource type. */
	public static final String PREFS_TYPE = "noti:types:";

	/** Preferences key for default for a site notification prefs - append the site id. */
	public static final String PREFS_SITE = "noti:sites:";

	/** Preferences key for a specific notification - append the notification id. */
	public static final String PREFS_NOTI = "noti:notis:";

	/**
	* Establish a new notification, locked for edit.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @return a new Notification, locked for edit.
	*/
	public NotificationEdit addNotification()
		/*throws PermissionException*/;

	/**
	* Establish a new transient notification.
	* Transient notifications are processed by the service but not stored in storage.
	* Modification to the notification can be done at any time, do not use edit(), commit() or remove() on it.
	* @return a new transient Notification.
	*/
	public NotificationEdit addTransientNotification();

	/**
	* Access a notification object.
	* @param id The notification id string.
	* @return A notification object containing the notification information.
	* @exception IdUnusedException if not found.
	*/
	public Notification getNotification(String id)
		throws IdUnusedException;

	/**
	* Get a locked notification object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The notification id string.
	* @return A NotificationEdit object for editing.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current notification does not have permission to mess with this notification.
	* @exception InUseException if the current notification does not have permission to mess with this notification.
	*/
	public NotificationEdit editNotification(String id)
		throws IdUnusedException, /*PermissionException,*/ InUseException;

	/**
	* Commit the changes made to a NotificationEdit object, and release the lock.
	* The NotificationEdit is disabled, and not to be used after this call.
	* @param notification The NotificationEdit object to commit.
	*/
	public void commitEdit(NotificationEdit notification);

	/**
	* Cancel the changes made to a NotificationEdit object, and release the lock.
	* The NotificationEdit is disabled, and not to be used after this call.
	* @param notification The NotificationEdit object to commit.
	*/
	public void cancelEdit(NotificationEdit notification);

	/**
	* Remove this notification
	* - it must be a notification with a lock from editNotification().
	* The NotificationEdit is disabled, and not to be used after this call.
	* @param id The notification id.
	* @exception PermissionException if the current notification does not have permission to remove this notification.
	*/
	public void removeNotification(NotificationEdit notification)
		/*throws PermissionException*/;

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The notification id.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String notificationReference(String id);

	/**
	* Find a notification object.
	* @param function The function setting of the notification object.
	* @param filter The resourceFilter setting of the notification object.
	* @return A notification object matching the criteria, or null if none found.
	*/
	public Notification findNotification(String function, String filter);

}	// NotificationService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/notification/NotificationService.java,v 1.4 2004/06/22 03:14:49 ggolden Exp $
*
**********************************************************************************/
