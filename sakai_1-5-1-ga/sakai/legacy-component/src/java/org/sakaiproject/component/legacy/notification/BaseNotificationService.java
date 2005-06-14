/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/BaseNotificationService.java,v 1.12 2004/09/30 20:20:03 ggolden.umich.edu Exp $
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

// imports
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>%%%</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.12 $
*/
public abstract class BaseNotificationService implements NotificationService, Observer, StorageUser, CacheRefresher
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** A Cache for this service - Notification objects stored by notification reference. */
	protected NotificationCache m_cache = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** Transient notifications (NotificationEdit). */
	protected List m_transients = null;

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
	*/
	protected abstract Storage newStorage();

	/**
	* Does the resource reference match the filter?
	* @param filter The resource reference filter.
	* @param ref The resource reference string.
	* @return true if the filter matches the ref, false if not.
	*/
	protected boolean match(String filter, String ref)
	{
		if (filter == null)
			return true;
		if (filter.length() == 0)
			return true;

		if (ref.startsWith(filter))
			return true;

		return false;

	} // match

	/**
	* Access the partial URL that forms the root of resource URLs.
	* @param relative if true, form within the access path only (i.e. starting with /content)
	* @return the partial URL that forms the root of resource URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	* Access the notification id extracted from a notification reference.
	* @param ref The notification reference string.
	* @return The the notification id extracted from a notification reference.
	*/
	protected String notificationId(String ref)
	{
		String start = getAccessPoint(true) + Resource.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1)
			return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // notificationId

	/**
	* Access the external URL which can be used to access the resource from outside the system.
	* @param id The notification id.
	* @return The the external URL which can be used to access the resource from outside the system.
	*/
	protected String notificationUrl(String id)
	{
		return getAccessPoint(false) + Resource.SEPARATOR + id;

	} // notificationUrl

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: event tracking service */
	protected EventTrackingService m_eventTrackingService = null;

	/**
	 * Dependency: event tracking service.
	 * @param service The event tracking service.
	 */
	public void setEventTrackingService(EventTrackingService service)
	{
		m_eventTrackingService = service;
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
			// prepare for transients
			m_transients = new Vector();

			m_relativeAccessPoint = REFERENCE_ROOT;

			m_logger.info(this +".init()");

			// construct storage and read
			m_storage = newStorage();
			m_storage.open();

			// make the cache
			m_cache = new NotificationCache(this, notificationReference(""));

			// start watching the events - only those generated on this server, not those from elsewhere
			m_eventTrackingService.addLocalObserver(this);

			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		// done with event watching
		m_eventTrackingService.deleteObserver(this);

		// clean up cache
		m_cache.clear();
		m_cache = null;

		// clean up storage
		m_storage.close();
		m_storage = null;

		// clean up transients
		m_transients.clear();
		m_transients = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* NotificationService implementation
	*******************************************************************************/

	/**
	* Establish a new notification, locked for edit.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @return a new Notification, locked for edit.
	*/
	public NotificationEdit addNotification()
	/*throws PermissionException*/
	{
		// check security (throws if not permitted)
		// unlock(SECURE_ADD_NOTIFICATION, notificationReference(id));

		// get a new unique id
		String id = IdService.getUniqueId();

		// reserve a notification with this id from the info store - if it's in use, this will return null
		NotificationEdit notification = m_storage.put(id);
		/*if (notification == null)
		{
			throw new IdUsedException(id);
		}*/

		 ((BaseNotificationEdit) notification).setEvent(SECURE_ADD_NOTIFICATION);

		return notification;

	} // addNotification

	/**
	* Establish a new transient notification.
	* Transient notifications are processed by the service but not stored in storage.
	* Modification to the notification can be done at any time, do not use edit(), commit() or remove() on it.
	* @return a new transient Notification.
	*/
	public NotificationEdit addTransientNotification()
	{
		// the id is not unique, and not really used
		String id = "transient";

		// create an object, not through storage
		NotificationEdit notification = new BaseNotificationEdit(id);

		// remember it
		m_transients.add(notification);

		// no event, no other cluster server knows about it - it's transient and local
		return notification;

	} // addTransientNotification

	/**
	* Access a notification object.
	* @param id The notification id string.
	* @return A notification object containing the notification information.
	* @exception IdUnusedException if not found.
	*/
	public Notification getNotification(String id) throws IdUnusedException
	{
		Notification notification = null;

		// if we have it cached, use it (hit or miss)
		String key = notificationReference(id);
		if (m_cache.containsKey(key))
		{
			notification = (Notification) m_cache.get(key);
		}

		// if not in the cache, see if we have it in our info store
		else
		{
			notification = m_storage.get(id);

			// cache it (hit or miss)
			m_cache.put(notification);
		}

		// if not found
		if (notification == null)
			throw new IdUnusedException(id);

		// track it - we don't track Notification access -ggolden
		// m_eventTrackingService.post(m_eventTrackingService.newEvent(SECURE_ACCESS_NOTIFICATION, notification.getReference()));

		return notification;

	} // getNotification

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The notification id.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String notificationReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;

	} // notificationReference

	/**
	* Get a locked notification object for editing. Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The notification id string.
	* @return A NotificationEdit object for editing.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to mess with this notification.
	* @exception InUseException if the notification is being edited by another user.
	*/
	public NotificationEdit editNotification(String id) throws IdUnusedException, /*PermissionException,*/
	InUseException
	{
		// check security (throws if not permitted)
		//unlock(SECURE_UPDATE_NOTIFICATION, notificationReference(id));

		// check for existance
		if ((m_cache.get(notificationReference(id)) == null) && (!m_storage.check(id)))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the notification with a lock from the info store
		NotificationEdit notification = m_storage.edit(id);
		if (notification == null)
			throw new InUseException(id);

		((BaseNotificationEdit) notification).setEvent(SECURE_UPDATE_NOTIFICATION);

		return notification;

	} // editNotification

	/**
	* Commit the changes made to a NotificationEdit object, and release the lock.
	* @param user The NotificationEdit object to commit.
	*/
	public void commitEdit(NotificationEdit notification)
	{
		// check for closed edit
		if (!notification.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitEdit(): closed NotificationEdit", e);
			}
			return;
		}

		// update the properties
		//addLiveUpdateProperties(notification.getPropertiesEdit());

		// complete the edit
		m_storage.commit(notification);

		// track it
		m_eventTrackingService.post(
			m_eventTrackingService.newEvent(
				((BaseNotificationEdit) notification).getEvent(),
				notification.getReference(),
				true));

		// close the edit object
		 ((BaseNotificationEdit) notification).closeEdit();

	} // commitEdit

	/**
	* Cancel the changes made to a NotificationEdit object, and release the lock.
	* @param user The NotificationEdit object to cancel.
	*/
	public void cancelEdit(NotificationEdit notification)
	{
		// check for closed edit
		if (!notification.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelEdit(): closed NotificationEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancel(notification);

		// close the edit object
		 ((BaseNotificationEdit) notification).closeEdit();

	} // cancelEdit

	/**
	* Remove this notification
	* - it must be a notification with a lock from editNotification().
	* The NotificationEdit is disabled, and not to be used after this call.
	* @param id The notification id.
	* @exception PermissionException if the current notification does not have permission to remove this notification.
	*/
	public void removeNotification(NotificationEdit notification)
	/*throws PermissionException*/
	{
		// check for closed edit
		if (!notification.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeNotification(): closed NotificationEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		//unlock(SECURE_REMOVE_NOTIFICATION, notification.getReference());

		// complete the edit
		m_storage.remove(notification);

		// track it
		m_eventTrackingService.post(
			m_eventTrackingService.newEvent(SECURE_REMOVE_NOTIFICATION, notification.getReference(), true));

		// close the edit object
		 ((BaseNotificationEdit) notification).closeEdit();

	} // removeNotification

	/**
	* Access all notification objects that are watching this event function.
	* @param function The event function.
	* @return A Set of notification objects that are watching this event function.
	*/
	public List getNotifications(String function)
	{
		List notifications = null;

		// if we have disabled the cache, don't use if
		if (m_cache.disabled())
		{
			notifications = m_storage.getAll(function);
		}

		else
		{
			// if the cache is complete, use it
			if (m_cache.isComplete())
			{
				notifications = m_cache.getAll(function);
			}

			// otherwise get all the notifications from storage
			else
			{
				// Note: while we are getting from storage, storage might change.  These can be processed
				// after we get the storage entries, and put them in the cache, and mark the cache complete.
				// -ggolden
				synchronized (m_cache)
				{
					// if we were waiting and it's now complete...
					if (m_cache.isComplete())
					{
						notifications = m_cache.getAll(function);
					}

					else
					{
						// save up any events to the cache until we get past this load
						m_cache.holdEvents();

						// get them all for caching
						List all = m_storage.getAll();

						// update the cache, and mark it complete
						for (int i = 0; i < all.size(); i++)
						{
							Notification notification = (Notification) all.get(i);
							m_cache.put(notification);
						}

						m_cache.setComplete();

						// get those for just this function
						notifications = m_cache.getAll(function);

						// now we are complete, process any cached events
						m_cache.processEvents();
					}
				}
			}
		}

		// if none found in storage
		if (notifications == null)
		{
			notifications = new Vector();
		}

		// add transients
		for (Iterator it = m_transients.iterator(); it.hasNext();)
		{
			Notification notification = (Notification) it.next();
			if (notification.containsFunction(function))
			{
				notifications.add(notification);
			}
		}

		return notifications;

	} // getNotifications

	/**
	* Find a notification object.
	* @param function The function setting of the notification object.
	* @param filter The resourceFilter setting of the notification object.
	* @return A notification object matching the criteria, or null if none found.
	*/
	public Notification findNotification(String function, String filter)
	{
		// start with all those for this function (just 'cause we have a nice method to get them -ggolden)
		List notifications = getNotifications(function);
		for (Iterator iNotifications = notifications.iterator(); iNotifications.hasNext();)
		{
			Notification notification = (Notification) iNotifications.next();
			if (notification.getResourceFilter().equals(filter))
			{
				return notification;
			}
		}

		return null;

	} // findNotification

	/*******************************************************************************
	* Observer implementation
	*******************************************************************************/

	/**
	* This method is called whenever the observed object is changed. An
	* application calls an <tt>Observable</tt> object's
	* <code>notifyObservers</code> method to have all the object's
	* observers notified of the change.
	*
	* default implementation is to cause the courier service to deliver to the
	* interface controlled by my controller.  Extensions can override.
	*
	* @param   o     the observable object.
	* @param   arg   an argument passed to the <code>notifyObservers</code>
	*                 method.
	*/
	public void update(Observable o, Object arg)
	{
		// arg is Event
		if (!(arg instanceof Event))
			return;
		Event event = (Event) arg;

		// check the event function against the functions we have notifications watching for
		String function = event.getEvent();

		// for each notification watching for this event
		List notifications = getNotifications(function);
		for (Iterator it = notifications.iterator(); it.hasNext();)
		{
			Notification notification = (Notification) it.next();

			// if the resource matches the notification's resource filter
			if (match(notification.getResourceFilter(), event.getResource()))
			{
				// cause the notification to run
				notification.notify(event);
			}
		}

	} // update

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open and be ready to read / write.
		*/
		public void open();

		/**
		* Close.
		*/
		public void close();

		/**
		* Check if a notification by this id exists.
		* @param id The notification id.
		* @return true if a nitificaion by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Add a new notification with this id.
		* @param id The notification id.
		* @return The locked notification with this id, or null if in use.
		*/
		public NotificationEdit put(String id);

		/**
		* Get the notification with this id, or null if not found.
		* @param id The notification id.
		* @return The notification with this id, or null if not found.
		*/
		public Notification get(String id);

		/**
		* Get a List of all the notifications that are interested in this Event function.
		* @param function The Event function
		* @return The List (Notification) of all the notifications that are interested in this Event function.
		*/
		public List getAll(String function);

		/**
		* Get a List of all notifications.
		* @return The List (Notification) of all notifications.
		*/
		public List getAll();

		/**
		* Get a lock on the notification with this id, or null if a lock cannot be gotten.
		* @param id The user id.
		* @return The locked Notification with this id, or null if this records cannot be locked.
		*/
		public NotificationEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param user The notification to commit.
		*/
		public void commit(NotificationEdit notification);

		/**
		* Cancel the changes and release the lock.
		* @param user The notification to commit.
		*/
		public void cancel(NotificationEdit notification);

		/**
		* Remove this notification.
		* @param user The notification to remove.
		*/
		public void remove(NotificationEdit notification);

	} // Storage

	/*******************************************************************************
	* StorageUser implementation
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other)
	{
		return null;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{
		return new BaseNotification(id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{
		return new BaseNotification(element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{
		return new BaseNotification((Notification) other);
	}

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Resource other)
	{
		return null;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseNotificationEdit e = new BaseNotificationEdit(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseNotificationEdit e = new BaseNotificationEdit(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseNotificationEdit e = new BaseNotificationEdit((Notification) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r)
	{
		return null;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Resource r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Resource r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Resource r)
	{
		return null;
	}

	/*******************************************************************************
	* CacheRefresher implementation (no container)
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		// key is a reference, but our storage wants an id
		String id = notificationId((String) key);

		// get this from storage
		Notification notification = m_storage.get(id);

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".refresh(): " + key + " : " + id);

		return notification;

	} // refresh

	/*******************************************************************************
	* Notification implementation
	*******************************************************************************/

	public class BaseNotification implements Notification
	{
		/** The Event(s) function we are watching for. */
		protected List m_functions = null;

		/** The resource reference filter. */
		protected String m_filter = null;

		/** The resource id. */
		protected String m_id = null;

		/** The resource properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The action helper class. */
		protected NotificationAction m_action = null;

		/**
		* Construct.
		* @param id The id to use.
		*/
		public BaseNotification(String id)
		{
			// generate a new id
			m_id = id;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// setup for functions
			m_functions = new Vector();

		} // BaseNotification

		/**
		* Construct from another Notification.
		* @param notification The other notification to copy values from.
		*/
		public BaseNotification(Notification other)
		{
			setAll(other);

		} // BaseNotification

		/**
		* Construct from an existing definition, in xml.
		* @param el The message in XML in a DOM element.
		*/
		public BaseNotification(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// setup for functions
			m_functions = new Vector();

			m_id = el.getAttribute("id");

			// the first function
			String func = StringUtil.trimToNull(el.getAttribute("function"));
			if (func != null)
			{
				m_functions.add(func);
			}

			m_filter = StringUtil.trimToNull(el.getAttribute("filter"));

			// the children (properties, action helper)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}

				// look for the helper element
				else if (element.getTagName().equals("action"))
				{
					// the class name
					String className = StringUtil.trimToNull(element.getAttribute("class"));
					if (className != null)
					{
						// create the class
						try
						{
							m_action = (NotificationAction) Class.forName(className).newInstance();

							// let it pick up it's settings
							m_action.set(element);
						}
						catch (Exception e)
						{
							m_logger.warn(this +" exception creating action helper: " + e.toString());
						}
					}
				}
				else if (element.getTagName().equals("function"))
				{
					func = StringUtil.trimToNull(element.getAttribute("id"));
					m_functions.add(func);
				}
			}

		} // BaseNotification

		/**
		* Take all values from this object.
		* @param other The notification object to take values from.
		*/
		protected void setAll(Notification other)
		{
			BaseNotification bOther = (BaseNotification) other;
			m_id = bOther.m_id;
			m_filter = bOther.m_filter;

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(bOther.m_properties);

			m_functions = new Vector();
			m_functions.addAll(bOther.m_functions);

			if (bOther.m_action != null)
			{
				m_action = bOther.m_action.getClone();
			}

		} // setAll

		/**
		* Do the notification.
		* @param event The event that matched criteria to cause the notification.
		*/
		public void notify(Event event)
		{
			if (m_action != null)
			{
				m_action.notify(this, event);
			}

		} // notify

		/**
		* Get the Event function.  Only Events with this function code will trigger the notification.
		* @return The Event function to watch for.
		*/
		public String getFunction()
		{
			return (String) m_functions.get(0);

		} // getFunction

		/**
		* Get the resource reference filter.  Only Events with references matching this will trigger the notification.
		* @return The resource reference filter.
		*/
		public String getResourceFilter()
		{
			return m_filter;

		} // getResourceFilter

		/**
		* Get all the Event functions for this notification.
		* @return a List (String) of Event functions to watch for.
		*/
		public List getFunctions()
		{
			List rv = new Vector();
			rv.addAll(m_functions);

			return rv;

		} // getFunctions

		/**
		* Check if the notification watches for events with this function code.
		* @param event The Event function to test.
		* @return true if this notification watches for evens with this function code, false if not.
		*/
		public boolean containsFunction(String function)
		{
			return m_functions.contains(function);

		} // containsFunction

		/**
		* Get the action helper that handles the notify() action.
		* @return The action helper that handles the notify() action.
		*/
		public NotificationAction getAction()
		{
			return m_action;

		} // getAction

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return notificationUrl(m_id);

		} // getId

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return notificationReference(m_id);

		} // getReference

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the resource's properties.
		* @return The resource's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element notification = doc.createElement("notification");
			if (stack.isEmpty())
			{
				doc.appendChild(notification);
			}
			else
			{
				((Element) stack.peek()).appendChild(notification);
			}

			stack.push(notification);

			notification.setAttribute("id", getId());

			// first function
			if (m_functions.size() >= 1)
			{
				notification.setAttribute("function", (String) m_functions.get(0));
			}

			if (m_filter != null)
				notification.setAttribute("filter", m_filter);

			// properties
			m_properties.toXml(doc, stack);

			// action
			if (m_action != null)
			{
				Element action = doc.createElement("action");
				notification.appendChild(action);
				action.setAttribute("class", m_action.getClass().getName());
				m_action.toXml(action);
			}

			// more functions
			if (m_functions.size() > 1)
			{
				for (int i = 1; i < m_functions.size(); i++)
				{
					String func = (String) m_functions.get(i);
					Element funcEl = doc.createElement("function");
					notification.appendChild(funcEl);
					funcEl.setAttribute("id", func);
				}
			}
			stack.pop();

			return notification;

		} // toXml

	} // BaseNotification

	/*******************************************************************************
	* NotificationEdit implementation
	*******************************************************************************/

	public class BaseNotificationEdit extends BaseNotification implements NotificationEdit, SessionStateBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct.
		* @param id The notification id.
		*/
		public BaseNotificationEdit(String id)
		{
			super(id);

		} // BaseNotificationEdit

		/**
		* Construct from an existing definition, in xml.
		* @param el The message in XML in a DOM element.
		*/
		public BaseNotificationEdit(Element el)
		{
			super(el);

		} // BaseNotificationEdit

		/**
		* Construct from another Notification.
		* @param notification The other notification to copy values from.
		*/
		public BaseNotificationEdit(Notification other)
		{
			super(other);

		} // BaseNotificationEdit

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // finalize

		/**
		* Set the Event function, clearing any that have already been set.
		* @param event The Event function to watch for.
		*/
		public void setFunction(String function)
		{
			m_functions.clear();
			m_functions.add(function);

		} // setFunction

		/**
		* Add another Event function.
		* @param event Another Event function to watch for.
		*/
		public void addFunction(String function)
		{
			m_functions.add(function);

		} // addFunction

		/**
		* Set the resource reference filter.
		* @param filter The resource reference filter.
		*/
		public void setResourceFilter(String filter)
		{
			m_filter = filter;

		} // setResourceFilter

		/**
		* Set the action helper that handles the notify() action.
		* @param action The action helper that handles the notify() action.
		*/
		public void setAction(NotificationAction action)
		{
			m_action = action;

		} // setAction

		/**
		* Take all values from this object.
		* @param other The notification object to take values from.
		*/
		protected void set(Notification other)
		{
			setAll(other);

		} // set

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		} // valueUnbound

	} // BaseNotificationEdit

} // BaseNotificationService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/BaseNotificationService.java,v 1.12 2004/09/30 20:20:03 ggolden.umich.edu Exp $
*
**********************************************************************************/
