/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/XmlFileNotificationService.java,v 1.7 2004/06/22 03:14:00 ggolden Exp $
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
package org.sakaiproject.component.legacy.notification;

// import
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileNotificationService is ... %%%.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class XmlFileNotificationService extends BaseNotificationService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/notification.xml";

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Set the storage path.
	 * @param path The storage path.
	 */
	public void setStoragePath(String path)
	{
		m_storagePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			// we don't need the cache, since we hold everything in memory already
			m_cache.disable();

			m_logger.info(this +".init(): storage path: " + m_storagePath);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseNotificationService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	} // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class XmlFileStorage extends BaseXmlFileStorage implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "notifications", null, "notification", user);

		} // XmlFileStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public Notification get(String id)
		{
			return (Notification) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		/**
		* Get a List of all the notifications that are interested in this Event function.
		* Note: instead of this looking, we could have an additional "key" in storage of the event... -ggolen
		* @param function The Event function
		* @return The List (Notification) of all the notifications that are interested in this Event function.
		*/
		public List getAll(String function)
		{
			List rv = new Vector();
			if (function == null)
				return rv;

			List all = super.getAllResources(null);
			for (Iterator it = all.iterator(); it.hasNext();)
			{
				Notification notification = (Notification) it.next();
				if (notification.containsFunction(function))
				{
					rv.add(notification);
				}
			}

			return rv;

		} // getAll

		public NotificationEdit put(String id)
		{
			return (NotificationEdit) super.putResource(null, id, null);
		}

		public NotificationEdit edit(String id)
		{
			return (NotificationEdit) super.editResource(null, id);
		}

		public void commit(NotificationEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(NotificationEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(NotificationEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileStorage

} // XmlFileNotificationService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/XmlFileNotificationService.java,v 1.7 2004/06/22 03:14:00 ggolden Exp $
*
**********************************************************************************/
