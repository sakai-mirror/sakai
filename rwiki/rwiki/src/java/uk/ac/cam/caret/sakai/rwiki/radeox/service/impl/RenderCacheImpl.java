/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.radeox.service.impl;

import org.sakaiproject.service.framework.log.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import uk.ac.cam.caret.sakai.rwiki.radeox.service.RenderCache;
/**
 * 
 * @author ieb
 *
 */
public class RenderCacheImpl implements RenderCache {

	private Logger log;
	private Cache cache = null;
	
	public String getRenderedContent(String key) {
		String cacheValue = null;
		try {
		    Element e = cache.get(key);
		    if ( e != null ) {
		    		cacheValue = (String)e.getValue();
		    }
		} catch ( Exception ex ) {
		}
		if ( cacheValue != null ) log.debug("Cache hit for "+key+" size "+cacheValue.length());
		else log.debug("Cache miss for "+key);
		return cacheValue;
	}

	public void putRenderedContent(String key, String content) {
		Element e = new Element(key,content);
		cache.put(e);
		log.debug("Put "+key+" size "+content.length());
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

}
