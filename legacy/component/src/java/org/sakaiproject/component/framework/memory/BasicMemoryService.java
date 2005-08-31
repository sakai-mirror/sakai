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

// package
package org.sakaiproject.component.framework.memory;

// imports
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.Cacher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.memory.SiteCache;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;

/**
* <p>MemoryService is an implementation for the MemoryService which reports memory usage and
* runs a periodic garbage collection to keep memory available.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class BasicMemoryService implements MemoryService, Observer
{
	/** Event for the memory reset. */
	protected static final String EVENT_RESET = "memory.reset";

	/** Set of registered cachers. */
	protected Set m_cachers = new HashSet();

	/** If true, output verbose caching info. */
	protected boolean m_cacheLogging = false;

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: EventTrackingService. */
	protected EventTrackingService m_eventTrackingService = null;

	/**
	 * Dependency: EventTrackingService.
	 */
	public void setEventTrackingService(EventTrackingService service)
	{
		m_eventTrackingService = service;
	}

	/** Dependency: logging service. */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/**
	 * Configuration: cache verbose debug
	 */
	public void setCacheLogging(boolean value)
	{
		m_cacheLogging = value;
	}

	public boolean getCacheLogging()
	{
		return m_cacheLogging;
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
			// get notified of events to watch for a reset
			m_eventTrackingService.addObserver(this);

			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_eventTrackingService.deleteObserver(this);

		m_cachers.clear();

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* MemoryService implementation
	*******************************************************************************/

	/**
	* Return the amount of available memory.
	* @return the amount of available memory.
	*/
	public long getAvailableMemory()
	{
		return Runtime.getRuntime().freeMemory();

	} // getAvailableMemory

	/**
	* Cause less memory to be used by clearing any optional caches.
	*/
	public void resetCachers() throws PermissionException
	{
		// check that this is a "super" user with the security service
		if (!SecurityService.isSuperUser())
		{
			// TODO: session id or session user id?
			throw new PermissionException(UsageSessionService.getSessionId(), EVENT_RESET, "");
		}

		// post the event so this and any other app servers in the cluster will reset
		m_eventTrackingService.post(m_eventTrackingService.newEvent(EVENT_RESET, "", true));

	} // resetMemory

	/**
	* Compute a status report on all memory users
	*/
	public String getStatus()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("** Memory report\n");

		Iterator it = m_cachers.iterator();
		while (it.hasNext())
		{
			Cacher cacher = (Cacher) it.next();
			buf.append(cacher.getSize() + " in " + cacher.getDescription() + "\n");
		}

		String rv = buf.toString();
		m_logger.info(rv);
		
		return rv;
	}

	/**
	* Do a reset of all cachers
	*/
	protected void doReset()
	{
		if (!m_cachers.isEmpty())
		{
			// tell all our memory users to reset their memory use
			Iterator it = m_cachers.iterator();
			while (it.hasNext())
			{
				Cacher cacher = (Cacher) it.next();
				cacher.resetCache();
			}

			// run the garbage collector now
			System.runFinalization();
			System.gc();
		}

		m_logger.info(this +".doReset():  Low Memory Recovery to: " + Runtime.getRuntime().freeMemory());

	} // doReset

	/**
	* Register as a cache user
	*/
	public void registerCacher(Cacher cacher)
	{
		m_cachers.add(cacher);

	} // registerCacher

	/**
	* Unregister as a cache user
	*/
	public void unregisterCacher(Cacher cacher)
	{
		m_cachers.remove(cacher);

	} // unregisterCacher

	/**
	 * {@inheritDoc}
	 */
	public Cache newCache(CacheRefresher refresher, String pattern)
	{
		return new MemCache(this, m_eventTrackingService, m_logger, refresher, pattern);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newHardCache(CacheRefresher refresher, String pattern)
	{
		return new HardCache(this, m_eventTrackingService, m_logger, refresher, pattern);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newHardCache(long sleep, String pattern)
	{
		return new HardCache(this, m_eventTrackingService, m_logger, sleep, pattern);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newCache(CacheRefresher refresher, long sleep)
	{
		return new MemCache(this, m_eventTrackingService, m_logger, refresher, sleep);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newHardCache(CacheRefresher refresher, long sleep)
	{
		return new HardCache(this, m_eventTrackingService, m_logger, refresher, sleep);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newCache()
	{
		return new MemCache(this, m_eventTrackingService, m_logger);
	}

	/**
	 * {@inheritDoc}
	 */
	public Cache newHardCache()
	{
		return new HardCache(this, m_eventTrackingService, m_logger);
	}

	/**
	 * {@inheritDoc}
	 */
	public SiteCache newSiteCache(CacheRefresher refresher, String pattern)
	{
		return new SiteCacheImpl(this, m_eventTrackingService, m_logger, refresher, pattern);
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
		// arg is Event
		if (!(arg instanceof Event))
			return;
		Event event = (Event) arg;

		// look for the memory reset event
		String function = event.getEvent();
		if (!function.equals(EVENT_RESET))
			return;

		// do the reset here, too!
		doReset();
	}

} // BasicMemoryService



