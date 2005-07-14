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

package org.sakaiproject.service.legacy.notification.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>NotificationService is a static Cover for the {@link org.sakaiproject.service.legacy.notification.NotificationService NotificationService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class NotificationService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.notification.NotificationService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.notification.NotificationService) ComponentManager.get(org.sakaiproject.service.legacy.notification.NotificationService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.notification.NotificationService) ComponentManager.get(org.sakaiproject.service.legacy.notification.NotificationService.class);
		}
	}
	private static org.sakaiproject.service.legacy.notification.NotificationService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.notification.NotificationService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.notification.NotificationService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_NOTIFICATION = org.sakaiproject.service.legacy.notification.NotificationService.SECURE_ADD_NOTIFICATION;
	public static java.lang.String SECURE_UPDATE_NOTIFICATION = org.sakaiproject.service.legacy.notification.NotificationService.SECURE_UPDATE_NOTIFICATION;
	public static java.lang.String SECURE_REMOVE_NOTIFICATION = org.sakaiproject.service.legacy.notification.NotificationService.SECURE_REMOVE_NOTIFICATION;
	public static int NOTI_NONE = org.sakaiproject.service.legacy.notification.NotificationService.NOTI_NONE;
	public static int NOTI_REQUIRED = org.sakaiproject.service.legacy.notification.NotificationService.NOTI_REQUIRED;
	public static int NOTI_OPTIONAL = org.sakaiproject.service.legacy.notification.NotificationService.NOTI_OPTIONAL;
	public static int PREF_NONE = org.sakaiproject.service.legacy.notification.NotificationService.PREF_NONE;
	public static int PREF_IGNORE = org.sakaiproject.service.legacy.notification.NotificationService.PREF_IGNORE;
	public static int PREF_DIGEST = org.sakaiproject.service.legacy.notification.NotificationService.PREF_DIGEST;
	public static int PREF_IMMEDIATE = org.sakaiproject.service.legacy.notification.NotificationService.PREF_IMMEDIATE;
	public static java.lang.String PREFS_DEFAULT = org.sakaiproject.service.legacy.notification.NotificationService.PREFS_DEFAULT;
	public static java.lang.String PREFS_TYPE = org.sakaiproject.service.legacy.notification.NotificationService.PREFS_TYPE;
	public static java.lang.String PREFS_SITE = org.sakaiproject.service.legacy.notification.NotificationService.PREFS_SITE;
	public static java.lang.String PREFS_NOTI = org.sakaiproject.service.legacy.notification.NotificationService.PREFS_NOTI;

	public static void commitEdit(org.sakaiproject.service.legacy.notification.NotificationEdit param0)
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.notification.NotificationEdit param0)
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static org.sakaiproject.service.legacy.notification.NotificationEdit addNotification()
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.addNotification();
	}

	public static org.sakaiproject.service.legacy.notification.NotificationEdit addTransientNotification()
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.addTransientNotification();
	}

	public static org.sakaiproject.service.legacy.notification.Notification getNotification(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.getNotification(param0);
	}

	public static org.sakaiproject.service.legacy.notification.NotificationEdit editNotification(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.editNotification(param0);
	}

	public static void removeNotification(org.sakaiproject.service.legacy.notification.NotificationEdit param0)
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return;

		service.removeNotification(param0);
	}

	public static java.lang.String notificationReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.notificationReference(param0);
	}

	public static org.sakaiproject.service.legacy.notification.Notification findNotification(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.notification.NotificationService service = getInstance();
		if (service == null)
			return null;

		return service.findNotification(param0, param1);
	}
}



