/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/NotificationCache.java,v 1.7 2004/08/29 20:01:40 ggolden.umich.edu Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.Cacher;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.notification.Notification;

/**
* <p>A Cache of objects with keys with a limited lifespan.</p>
* <p>When the object expires, the cache calls upon a CacheRefresher to update the key's value.
* The update is done in a separate thread.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class NotificationCache
	implements Cacher, Observer
{
	/** Map holding cached entries (by reference). */
	protected Map m_map = null;

	/** Map of notification function to Set of notifications - same objects as in m_map. */
	protected Map m_functionMap = null;

	/** The object that will deal with expired entries. */
	protected CacheRefresher m_refresher = null;

	/** The string that all resources in this cache will start with. */
	protected String m_resourcePattern = null;

	/** If true, we are disabled. */
	protected boolean m_disabled = false;

	/** If true, we have all the entries that there are in the cache. */
	protected boolean m_complete = false;

	/** If true, we are going to hold any events we see in the m_heldEvents list for later processing. */
	protected boolean m_holdEventProcessing = false;

	/** The events we are holding for later processing. */
	protected List m_heldEvents = new Vector();

	/**
	* Construct the Cache.  Attempts to keep complete on Event notification by calling the refresher.
	* @param refresher The object that will handle refreshing of event notified modified or added entries.
	* @param pattern The "startsWith()" string for all resources that may be in this cache.
	*/
	public NotificationCache(CacheRefresher refresher, String pattern)
	{
		m_map = new HashMap();
		m_functionMap = new HashMap();

		// register as a cacher
		MemoryService.registerCacher(this);

		m_refresher = refresher;
		m_resourcePattern = pattern;

		// register to get events - first, before others
		EventTrackingService.addPriorityObserver(this);

	}	// NotificationCache

	/**
	* Clean up.
	*/
	protected void finalize()
	{
		// unregister as a cacher
		MemoryService.unregisterCacher(this);

		// ungister to get events
		EventTrackingService.deleteObserver(this);

	}	// finalize

	/**
	* Cache an object.
	* @param key The key with which to find the object.
	* @param payload The object to cache.
	* @param duration The time to cache the object (seconds).
	*/
	public synchronized void put(Notification payload)
	{
		if (disabled()) return;

		m_map.put(payload.getReference(), payload);

		// put in m_functionMap, too, for each function of the notification
		List funcs = payload.getFunctions();
		for (Iterator iFuncs = funcs.iterator(); iFuncs.hasNext();)
		{
			String func = (String) iFuncs.next();

			List notifications = (List) m_functionMap.get(func);
			if (notifications == null)
			{
				notifications = new Vector();
				m_functionMap.put(func, notifications);
			}
		
			if (!notifications.contains(payload)) notifications.add(payload);
		}

	}	// cache

	/**
	* Test for an entry in the cache.
	* @param key The cache key.
	* @return true if the key maps to an entry, false if not.
	*/
	public synchronized boolean containsKey(Object key)
	{
		if (disabled()) return false;

		// is it there?
		if (m_map.containsKey(key))
		{
			return true;
		}

		return false;

	}	// containsKey

	/**
	* Get the entry associated with the key, or null if not there
	* @param key The cache key.
	* @return The entry associated with the key, or null if the a null is cached, or the key is not found
	* (Note: use containsKey() to remove this ambiguity).
	*/
	public synchronized Notification get(Object key)
	{
		if (disabled()) return null;

		return (Notification) m_map.get(key);

	}	// get

	/**
	* Get all the non-null entries.
	* @return all the non-null entries, or an empty list if none.
	*/
	public List getAll()
	{
		List rv = new Vector();

		if (disabled()) return rv;
		if (m_map.isEmpty()) return rv;

		// get the keys as of now
		Object [] keys = m_map.keySet().toArray();
		
		// for each entry in the cache
		for (int i = 0; i < keys.length; i++)
		{
			Object payload = m_map.get(keys[i]);

			// skip nulls
			if (payload == null) continue;

			// we'll take it 
			rv.add(payload);
		}

		return rv;

	}	// getAll

	/**
	* Get all the Notification entries that are watching this Event function.
	* @param function The function to use to select Notifications.
	* @return all the Notification entries that are watching this Event function.
	*/
	public List getAll(String function)
	{
		return (List) m_functionMap.get(function);

	}	// getAll

	/**
	* Get all the keys
	* @return The List of key values (Object).
	*/
	public List getKeys()
	{
		List rv = new Vector();
		rv.addAll(m_map.keySet());
		return rv;

	}	// getKeys

	/**
	* Get all the keys, eache modified to remove the resourcePattern prefix.
	* Note: only works with String keys.
	* @return The List of keys converted from references to ids (String).
	*/
	public List getIds()
	{
		List rv = new Vector();

		List keys = new Vector();
		keys.addAll(m_map.keySet());

		Iterator it = keys.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			int i = key.indexOf(m_resourcePattern);
			if (i != -1) key = key.substring(i+m_resourcePattern.length());
			rv.add(key);
		}

		return rv;

	}	// getKeys

	/**
	* Clear all entries.
	*/
	public synchronized void clear()
	{
		m_map.clear();
		m_functionMap.clear();
		m_complete = false;

	}	// clear

	/**
	* Remove this entry from the cache.
	* @param key The cache key.
	*/
	public synchronized void remove(Object key)
	{
		if (disabled()) return;

		Notification payload = (Notification) m_map.get(key);
		m_map.remove(key);
		
		if (payload == null) return;
		
		// remove it from the function map for each function
		List funcs = payload.getFunctions();
		for (Iterator iFuncs = funcs.iterator(); iFuncs.hasNext();)
		{
			String func = (String) iFuncs.next();

			List notifications = (List) m_functionMap.get(func);
			if (notifications != null)
			{
				notifications.remove(payload);
				if (notifications.isEmpty())
				{
					m_functionMap.remove(func);
				}
			}
		}

	}	// remove

	/**
	* Disable the cache.
	*/
	public void disable()
	{
		m_disabled = true;
		EventTrackingService.deleteObserver(this);
		clear();

	}	// disable

	/**
	* Enable the cache.
	*/
	public void enable()
	{
		m_disabled = false;
		EventTrackingService.addPriorityObserver(this);

	}	// enable

	/**
	* Is the cache disabled?
	* @return true if the cache is disabled, false if it is enabled.
	*/
	public boolean disabled()
	{
		return m_disabled;

	}	// disabled

	/**
	* Are we complete?
	* @return true if we have all the possible entries cached, false if not.
	*/
	public boolean isComplete()
	{
		if (disabled()) return false;

		return m_complete;

	}	// isComplete

	/**
	* Set the cache to be complete, containing all possible entries.
	*/
	public void setComplete()
	{
		if (disabled()) return;

		m_complete = true;

	}	// isComplete

	/**
	* Set the cache to hold events for later processing to assure an atomic "complete" load.
	*/
	public synchronized void holdEvents()
	{
		m_holdEventProcessing = true;

	}	// holdEvents

	/**
	* Restore normal event processing in the cache, and process any held events now.
	*/
	public synchronized void processEvents()
	{
		m_holdEventProcessing = false;

		for (int i = 0; i < m_heldEvents.size(); i++)
		{
			Event event = (Event) m_heldEvents.get(i);
			continueUpdate(event);
		}
		
		m_heldEvents.clear();

	}	// holdEvents

	/*******************************************************************************
	* Cacher implementation
	*******************************************************************************/

	/**
	* Clear out as much as possible anything cached; re-sync any cache that is needed to be kept.
	*/
	public void resetCache()
	{
		clear();

	}	// resetCache

	/**
	 * Return the size of the cacher - indicating how much memory in use.
	 * @return The size of the cacher.
	 */
	public long getSize()
	{
		return m_map.size();
	}

	/**
	 * Return a description of the cacher.
	 * @return The cacher's description.
	 */
	public String getDescription()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("NotificationCache:");
		if (m_disabled)
		{
			buf.append(" disabled:");
		}
		if (m_complete)
		{
			buf.append(" complete:");
		}
		if (m_resourcePattern != null)
		{
			buf.append(" pattern: " + m_resourcePattern);
		}
		if (m_refresher != null)
		{
			buf.append(" refresher: " + m_refresher.toString());
		}
		
		return buf.toString();
	}

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
		if (disabled()) return;

		// arg is Event
		if (!(arg instanceof Event)) return;
		Event event = (Event) arg;

		// if this is just a read, not a modify event, we can ignore it
		if (!event.getModify()) return;

		String key = event.getResource();

		// if this resource is not in my pattern of resources, we can ignore it
		if (!key.startsWith(m_resourcePattern)) return;

		// if we are holding event processing
		if (m_holdEventProcessing)
		{
			m_heldEvents.add(event);
			return;
		}

		continueUpdate(event);

	}	// update

	/**
	* Complete the update, given an event that we know we need to act upon.
	* @param event The event to process.
	*/
	private void continueUpdate(Event event)
	{
		String key = event.getResource();

		if (Logger.isDebugEnabled())
			Logger.debug(this + ".update() [" + m_resourcePattern + "] resource: " + key + " event: " + event.getEvent());

		// do we have this in our cache?
		Object oldValue = get(key);
		if (m_map.containsKey(key))
		{
			// invalidate our copy
			remove(key);
		}

		// if we are being complete, we need to get this cached.
		if (m_complete)
		{
			// we can only get it cached if we have a refresher
			if (m_refresher != null)
			{
				// ask the refresher for the value
				Notification value = (Notification) m_refresher.refresh(key, oldValue, event);
				if (value != null)
				{
					put(value);
				}
			}
			else
			{
				// we can no longer claim to be complete
				m_complete = false;
			}
		}
		
	}	// continueUpdate

}	// Cache

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/NotificationCache.java,v 1.7 2004/08/29 20:01:40 ggolden.umich.edu Exp $
*
**********************************************************************************/
