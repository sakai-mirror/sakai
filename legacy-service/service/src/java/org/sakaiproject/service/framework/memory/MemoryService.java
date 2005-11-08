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

package org.sakaiproject.service.framework.memory;

import org.sakaiproject.exception.PermissionException;

/**
 * <p>MemoryService is the interface for the Sakai Memory service.</p>
 * <p>This tracks memory users (cachers), runs a periodic garbage collection to keep memory available,
 * and can be asked to report memory usage.</p>
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface MemoryService
{
	/**
	 * Report the amount of available memory.
	 * @return the amount of available memory.
	 */
	long getAvailableMemory();

	/**
	 * Cause less memory to be used by clearing any optional caches.
	 * @exception PermissionException if the current user does not have permission to do this.
	 */
	void resetCachers() throws PermissionException;

	/**
	 * Register as a cache user
	 */
	void registerCacher(Cacher cacher);

	/**
	 * Unregister as a cache user
	 */
	void unregisterCacher(Cacher cacher);

	/**
	 * Construct a Cache.  Attempts to keep complete on Event notification by
	 * calling the refresher.
	 * @param refresher The object that will handle refreshing of event notified modified or added entries.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache -
	 * if null, don't watch events for updates.
	 */
	Cache newCache(CacheRefresher refresher, String pattern);

	/**
	 * Construct a special Cache that uses hard references.
	 * Attempts to keep complete on Event notification by
	 * calling the refresher.
	 * @param refresher The object that will handle refreshing of event notified modified or added entries.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache -
	 * if null, don't watch events for updates.
	 */
	Cache newHardCache(CacheRefresher refresher, String pattern);

	/**
	 * Construct a Cache.  Automatic refresh handling if refresher is not null.
	 * @param refresher The object that will handle refreshing of expired entries.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 */
	Cache newCache(CacheRefresher refresher, long sleep);

	/**
	 * Construct a Cache.  Automatic refresh handling if refresher is not null.
	 * @param refresher The object that will handle refreshing of expired entries.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 */
	Cache newHardCache(CacheRefresher refresher, long sleep);

	/**
	 * Construct a Cache.  No automatic refresh: expire only, from time and events.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache - if null, don't watch events for expiration.
	 */
	Cache newHardCache(long sleep, String pattern);

	/**
	 * Construct a Cache.  No automatic refresh handling.
	 */
	Cache newCache();

	/**
	 * Construct a Cache.  No automatic refresh handling.
	 */
	Cache newHardCache();

	/**
	 * Construct a special Site Cache.  No automatic refresh: expire only, from time and events.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache - if null, don't watch events for updates.
	 */
	SiteCache newSiteCache(long sleep, String pattern);
	
	/**
	 * Construct a multi-ref Cache.  No automatic refresh: expire only, from time and events.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 */
	MultiRefCache newMultiRefCache(long sleep);

	/**
	 * Get a status report of memory users.
	 * @return A status report of memory users.
	 */
	String getStatus();
}



