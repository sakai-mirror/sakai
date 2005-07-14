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

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.legacy.event.EventTrackingService;

/**
 * <p>HardCache is a MemCache set to use hard, not soft references.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class HardCache extends MemCache
{
	/**
	 * Construct the Cache.  No automatic refresh handling.
	*/
	public HardCache(BasicMemoryService memoryService, EventTrackingService eventTrackingService, Logger logger)
	{
		super(memoryService, eventTrackingService, logger);
		m_softRefs = false;
	}

	/**
	 * Construct the Cache.  Attempts to keep complete on Event notification by
	 * calling the refresher.
	 * @param refresher The object that will handle refreshing of event notified modified or added entries.
	 * @param pattern The "startsWith()" string for all resources that may be in this cache -
	 * if null, don't watch events for updates.
	 */
	public HardCache(
		BasicMemoryService memoryService,
		EventTrackingService eventTrackingService,
		Logger logger,
		CacheRefresher refresher,
		String pattern)
	{
		super(memoryService, eventTrackingService, logger, refresher, pattern);
		m_softRefs = false;
	}

	/**
	 * Construct the Cache.  Automatic refresh handling if refresher is not null.
	 * @param refresher The object that will handle refreshing of expired entries.
	 * @param sleep The number of seconds to sleep between expiration checks.
	 */
	public HardCache(
		BasicMemoryService memoryService,
		EventTrackingService eventTrackingService,
		Logger logger,
		CacheRefresher refresher,
		long sleep)
	{
		super(memoryService, eventTrackingService, logger, refresher, sleep);
		m_softRefs = false;
	}
}



