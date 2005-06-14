/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/test/mock/org/sakaiproject/mock/BasicMemoryServiceMock.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

package org.sakaiproject.mock;
import org.sakaiproject.component.framework.memory.BasicMemoryService;
import org.sakaiproject.component.framework.memory.HardCache;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.Cacher;
import org.sakaiproject.service.framework.memory.SiteCache;
import org.sakaiproject.service.legacy.event.EventTrackingService;

/**
 * Mock Object for BasicMemoryService
 */
public class BasicMemoryServiceMock extends BasicMemoryService
{
    private EventTrackingService eventTrackingService;
	private Logger logger;
	
	/**
	 * @param eventTrackingService
	 * @param logger
	 */
	public BasicMemoryServiceMock(EventTrackingService eventTrackingService, Logger logger)
	{
	    this.eventTrackingService = eventTrackingService;
	    this.logger = logger;	    
	}
	
	/* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#getAvailableMemory()
     */
    public long getAvailableMemory()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#resetCachers()
     */
    public void resetCachers() throws PermissionException
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#registerCacher(org.sakaiproject.service.framework.memory.Cacher)
     */
    public void registerCacher(Cacher arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#unregisterCacher(org.sakaiproject.service.framework.memory.Cacher)
     */
    public void unregisterCacher(Cacher arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newCache(org.sakaiproject.service.framework.memory.CacheRefresher, java.lang.String)
     */
    public Cache newCache(CacheRefresher arg0, String arg1)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newCache(org.sakaiproject.service.framework.memory.CacheRefresher, long)
     */
    public Cache newCache(CacheRefresher arg0, long arg1)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newCache()
     */
    public Cache newCache()
    {
         return null;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newSiteCache(org.sakaiproject.service.framework.memory.CacheRefresher, java.lang.String)
     */
    public SiteCache newSiteCache(CacheRefresher arg0, String arg1)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newHardCache(org.sakaiproject.service.framework.memory.CacheRefresher, java.lang.String)
     */
    public Cache newHardCache(CacheRefresher refresher, String pattern)
    {
        return new HardCache(this, eventTrackingService, logger, refresher, pattern);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.service.framework.memory.MemoryService#newHardCache(org.sakaiproject.service.framework.memory.CacheRefresher, long)
     */
    public Cache newHardCache(CacheRefresher refresher, long sleep)
    {
    			return new HardCache(this, eventTrackingService, logger, refresher,
                sleep);
     }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.service.framework.memory.MemoryService#newHardCache()
     */
    public Cache newHardCache()
    {
    	return new HardCache(this, eventTrackingService, logger);
    }

}
