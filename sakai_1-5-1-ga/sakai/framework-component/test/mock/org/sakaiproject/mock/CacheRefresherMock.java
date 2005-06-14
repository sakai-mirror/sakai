/**********************************************************************************
 *
 * $Header: /cvs/sakai/framework-component/test/mock/org/sakaiproject/mock/CacheRefresherMock.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.legacy.event.Event;

/**
 * Mock object for CacheRefresher
 */
public class CacheRefresherMock implements CacheRefresher
{
    Map hashMap = new HashMap();

    /**
     * Load the refresher with test values.
     * 
     * @param map
     */
    synchronized public void load(Map map)
    {
        hashMap.putAll(map);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.service.framework.memory.CacheRefresher#refresh(java.lang.Object,
     *      java.lang.Object, org.sakaiproject.service.legacy.event.Event)
     */
    public Object refresh(Object key, Object oldValue, Event event)
    {
        // key is a reference, but our storage wants an id
        String id = (String) key;

        // get whatever we have from storage for the cache for this vale
        return hashMap.get(id);
    }
    
    /**
     * @param key
     * @param value
     */
    synchronized public void put(Object key, Object value)
    {
        hashMap.put(key, value);
    }
    
    /**
     * @param key
     */
    synchronized public void remove(Object key)
    {
        hashMap.remove(key);
    }

}