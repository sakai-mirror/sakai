/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.component.framework.memory;

// import
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.MultiRefCache;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * <p>
 * MultiRefCacheImpl implements the MultiRefCache.
 * </p>
 * <p>
 * The references that each cache entry are sensitive to are kept in a separate map for easy access.<br />
 * Manipulation of this map is synchronized. This map is not used for cache access, just when items are added and removed.<br />
 * The cache map itself becomes synchronized when it's manipulated (not when reads occur), so this added sync. for the refs fits the existing pattern.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class MultiRefCacheImpl extends MemCache implements MultiRefCache
{
	/** Map of reference string -> Collection of cache keys. */
	protected Map m_refs = null;

	///** Enable for runtime cache consistency checking (likely expensive). */
	//protected final static boolean TEST = true;

	protected class MultiRefCacheEntry extends CacheEntry
	{
		/** These are the entity reference strings that this entry is sensitive to. */
		protected Collection m_refs = new Vector();

		/**
		 * Construct to cache the payload for the duration.
		 * 
		 * @param payload
		 *        The thing to cache.
		 * @param duration
		 *        The time (seconds) to keep this cached.
		 * @param ref
		 *        One entity reference that, if changed, will invalidate this entry.
		 * @param azgRefs
		 *        AuthzGroup refs that, if the changed, will invalidate this entry.
		 */
		public MultiRefCacheEntry(Object payload, int duration, String ref, Collection azgRefs)
		{
			super(payload, duration);
			if (ref != null) m_refs.add(ref);
			if (azgRefs != null) m_refs.addAll(azgRefs);
		}

		/**
		 * @inheritDoc
		 */
		public Collection getRefs()
		{
			return m_refs;
		}
	}

	/**
	 * Construct the Cache - checks for expiration periodically.
	 */
	public MultiRefCacheImpl(BasicMemoryService memoryService, EventTrackingService eventTrackingService, Logger logger, long sleep)
	{
		super(memoryService, eventTrackingService, logger, sleep, "");
		m_softRefs = false;
		m_refs = new ConcurrentReaderHashMap();
	}

	/**
	 * @inheritDoc
	 */
	public void put(Object key, Object payload, int duration, String ref, Collection azgIds)
	{
		if (disabled()) return;

		// make refs for any azg ids
		Collection azgRefs = null;
		if (azgIds != null)
		{
			azgRefs = new Vector();
			for (Iterator i = azgIds.iterator(); i.hasNext();)
			{
				String azgId = (String) i.next();
				azgRefs.add(AuthzGroupService.authzGroupReference(azgId));
			}
		}

		// create our extended cache entry for the cache map - the reference strings are recorded
		m_map.put(key, new MultiRefCacheEntry(payload, duration, ref, azgRefs));

		m_putCount++;

		synchronized (m_refs)
		{
			if (ref != null)
			{
				addRefCachedKey(ref, key);
			}

			if (azgRefs != null)
			{
				for (Iterator i = azgRefs.iterator(); i.hasNext();)
				{
					String azgRef = (String) i.next();
					addRefCachedKey(azgRef, key);
				}
			}

			// perform (likely expensive) cache consistency check
			//if (TEST) checkState();
		}
	}

	/**
	 * @inheritDoc
	 */
	public void put(Object key, Object payload, int duration)
	{
		put(key, payload, duration, null, null);
	}

	/**
	 * @inheritDoc
	 */
	public void put(Object key, Object payload)
	{
		put(key, payload, 0, null, null);
	}

	/**
	 * Make sure there's an entry in refs for this ref that includes this key.
	 * 
	 * @param ref
	 *        The entity reference string.
	 * @param key
	 *        The cache entry key dependent on this entity ref.
	 */
	protected void addRefCachedKey(String ref, Object key)
	{
		Collection cachedKeys = (Collection) m_refs.get(ref);
		if (cachedKeys == null)
		{
			cachedKeys = new Vector();
			m_refs.put(ref, cachedKeys);
		}
		if (!cachedKeys.contains(key))
		{
			cachedKeys.add(key);
		}
	}

	/**
	 * @inheritDoc
	 */
	public void clear()
	{
		super.clear();
		synchronized (m_refs)
		{
			m_refs.clear();
		}
	}

	/**
	 * @inheritDoc
	 */
	public void remove(Object key)
	{
		if (disabled()) return;

		MultiRefCacheEntry cachedEntry = (MultiRefCacheEntry) m_map.remove(key);
		if (cachedEntry == null) return;

		// remove this key from any of the entity references in m_refs that are dependent on this entry
		synchronized (m_refs)
		{
			for (Iterator iRefs = cachedEntry.getRefs().iterator(); iRefs.hasNext();)
			{
				String ref = (String) iRefs.next();
				Collection keys = (Collection) m_refs.get(ref);
				if ((keys != null) && (keys.contains(key)))
				{
					keys.remove(key);

					// remove the ref entry if it no longer has any cached keys in its colleciton
					if (keys.isEmpty())
					{
						m_refs.remove(ref);
					}
				}
			}

			// perform (likely expensive) cache consistency check
			//if (TEST) checkState();
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Cacher implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public String getDescription()
	{
		return "MultiRefCache: " + super.getDescription();
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Observer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public void update(Observable o, Object arg)
	{
		if (disabled()) return;

		// arg is Event
		if (!(arg instanceof Event)) return;
		Event event = (Event) arg;

		// if this is just a read, not a modify event, we can ignore it
		if (!event.getModify()) return;

		// if we are holding event processing
		if (m_holdEventProcessing)
		{
			m_heldEvents.add(event);
			return;
		}

		continueUpdate(event);
	}

	/**
	 * Complete the update, given an event that we know we need to act upon.
	 * 
	 * @param event
	 *        The event to process.
	 */
	protected void continueUpdate(Event event)
	{
		String ref = event.getResource();

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".update() [" + m_resourcePattern + "] resource: " + ref + " event: " + event.getEvent());

		// do we have this in our ref list
		if (m_refs.containsKey(ref))
		{
			// get the copy of the Collection of cache keys for this reference (the actual collection will be reduced as the removes occur)
			Collection cachedKeys = new Vector((Collection) m_refs.get(ref));

			// invalidate all these keys
			for (Iterator iKeys = cachedKeys.iterator(); iKeys.hasNext();)
			{
				remove(iKeys.next());
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean isComplete()
	{
		// we do not support being complete
		return false;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isComplete(String path)
	{
		// we do not support being complete
		return false;
	}

//	/**
//	 * Check that the cache and the m_refs are consistent.
//	 */
//	protected void checkState()
//	{
//		// every cache entry's every ref must have an entry in m_refs, and that entry must include the cache entry's key
//		for (Iterator iEntries = m_map.entrySet().iterator(); iEntries.hasNext();)
//		{
//			Map.Entry entry = (Map.Entry) iEntries.next();
//			MultiRefCacheEntry ce = (MultiRefCacheEntry) entry.getValue();
//			for (Iterator iRefs = ce.getRefs().iterator(); iRefs.hasNext();)
//			{
//				String ref = (String) iRefs.next();
//				Collection keys = (Collection) m_refs.get(ref);
//				if (keys == null)
//					m_logger.warn("** cache entry's ref not found in m_refs: cache key: " + entry.getKey() + " ref: " + ref);
//				if ((keys != null) && (!keys.contains(entry.getKey())))
//					m_logger.warn("** cache entry's ref's m_refs entry does not contain cache key: key: " + entry.getKey()
//							+ " ref: " + ref);
//			}
//		}
//
//		// every reference in m_refs every key must exist in the cache, and must include the ref in the cache entry's refs
//		for (Iterator iRefs = m_refs.entrySet().iterator(); iRefs.hasNext();)
//		{
//			Map.Entry entry = (Map.Entry) iRefs.next();
//			Collection keys = (Collection) entry.getValue();
//			for (Iterator iKeys = keys.iterator(); iKeys.hasNext();)
//			{
//				String key = (String) iKeys.next();
//				if (m_map.get(key) == null)
//					m_logger.warn("** m_ref's entry's key not found in cache: ref: " + entry.getKey() + " cache key: " + key);
//				if ((m_map.get(key) != null) && (!(((MultiRefCacheEntry) m_map.get(key)).getRefs().contains(entry.getKey()))))
//					m_logger.warn("** m_ref's entry's key->cache entry does not have the ref: ref: " + entry.getKey() + " cache key: " + key);
//			}
//		}
//	}
}
