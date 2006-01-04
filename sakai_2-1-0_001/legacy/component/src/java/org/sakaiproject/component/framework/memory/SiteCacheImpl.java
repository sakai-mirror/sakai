/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.component.framework.memory;

import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.SiteCache;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * <p>SiteCacheImpl implements the SiteCache.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SiteCacheImpl extends MemCache implements SiteCache
{
	/** Map of a tool id to a cached site's tool configuration instance. */
	protected Map m_tools = new ConcurrentReaderHashMap();

	/** Map of a page id to a cached site's SitePage instance. */
	protected Map m_pages = new ConcurrentReaderHashMap();

	/** Map of a section id to a cached site's Section instance. */
	protected Map m_sections = new ConcurrentReaderHashMap();

	/**
	 * Construct the Cache.  No automatic refresh: expire only, from time and events.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache - if null, don't watch events for updates.
	 */
	public SiteCacheImpl(
		BasicMemoryService memoryService,
		EventTrackingService eventTrackingService,
		Logger logger,
		long sleep,
		String pattern)
	{
		super(memoryService, eventTrackingService, logger, sleep, pattern);
		m_softRefs = false;
	}

	/**
	 * Cache an object
	 * @param key The key with which to find the object.
	 * @param payload The object to cache.
	 * @param duration The time to cache the object (seconds).
	 */
	public void put(Object key, Object payload, int duration)
	{
		if (disabled()) return;

		super.put(key, payload, duration);

		// add the payload (Site) tool ids
		if (payload instanceof Site)
		{
			Site site = (Site) payload;
			
			// get the pages and tools, sections and propeties all loaded efficiently
			site.loadAll();
			
			// add the pages and tools to the cache
			for (Iterator pages = site.getPages().iterator(); pages.hasNext();)
			{
				SitePage page = (SitePage) pages.next();
				m_pages.put(page.getId(), page);
				for (Iterator tools = page.getTools().iterator(); tools.hasNext();)
				{
					ToolConfiguration tool = (ToolConfiguration) tools.next();
					m_tools.put(tool.getId(), tool);
				}
			}
			
			// add the sections to the cache
			for (Iterator sections = site.getGroups().iterator(); sections.hasNext();)
			{
				Group section = (Group) sections.next();
				m_sections.put(section.getId(), section);
			}
		}
	}

	/**
	 * Clear all entries.
	 */
	public void clear()
	{
		super.clear();
		
		// clear the tool ids
		m_tools.clear();
		
		// clear the pages
		m_pages.clear();
	}

	/**
	* Remove this entry from the cache.
	* @param key The cache key.
	*/
	public void remove(Object key)
	{		
		if (disabled()) return;

		// get the current payload, if any
		CacheEntry entry = (CacheEntry) m_map.get(key);
		Object payload = null;
		if (entry != null)
		{
			payload = entry.getPayload(null);
		}

		super.remove(key);

		// clear the tool ids for this site
		if ((payload != null) && (payload instanceof Site))
		{
			Site site = (Site) payload;
			for (Iterator pages = site.getPages().iterator(); pages.hasNext();)
			{
				SitePage page = (SitePage) pages.next();
				m_pages.remove(page.getId());
				for (Iterator tools = page.getTools().iterator(); tools.hasNext();)
				{
					ToolConfiguration tool = (ToolConfiguration) tools.next();
					m_tools.remove(tool.getId());
				}
			}

			for (Iterator sections = site.getGroups().iterator(); sections.hasNext();)
			{
				Group section = (Group) sections.next();
				m_sections.remove(section.getId());
			}
		}
	}
	
	/**
	 * @inheritDoc
	 */
	public ToolConfiguration getTool(String toolId)
	{
		return (ToolConfiguration) m_tools.get(toolId);
	}

	/**
	 * @inheritDoc
	 */
	public SitePage getPage(String pageId)
	{
		return (SitePage) m_pages.get(pageId);
	}

	/**
	 * @inheritDoc
	 */
	public Group getGroup(String sectionId)
	{
		return (Group) m_sections.get(sectionId);
	}
}
