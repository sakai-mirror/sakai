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
package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.tool.api.PopulateService;

/**
 * @author andrew
 *
 */

// FIXME: Tool

public class PopulateServiceImpl implements PopulateService {
	
	private Logger log;
	private HashMap seenPageSpaces = new HashMap();
	private List seedPages;
	
	private RWikiCurrentObjectDao dao;
	
	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.PopulateService#populateRealm(java.lang.String, java.lang.String)
	 */
	public void populateRealm(String user, String realm, String group)
	throws PermissionException {
		synchronized (seenPageSpaces) {
			if (seenPageSpaces.get(realm) == null ) {
				for ( Iterator i = seedPages.iterator(); i.hasNext(); ) {
					if (log.isDebugEnabled()) {
						log.debug("Populating realm: " + realm);
					}
					
					RWikiCurrentObject seed = (RWikiCurrentObject) i.next();
					
					String name = NameHelper.globaliseName(seed.getName(), realm);
					log.	warn("Populating Realm with "+seed.getName());
					if (dao.findByGlobalName(name) == null) {
						if (log.isDebugEnabled()) {
							log.debug("Creating Page: " + name);
						}
						log.warn("Creating Page :"+name);
						RWikiCurrentObject rwo = dao.createRWikiObject(name, realm);
						seed.copyTo(rwo);
						rwo.setName(name);
						rwo.setRealm(realm);
						dao.update(rwo,null);
						log.warn("Page Created ");
					} else {
						log.warn("Page Already exists ");
					}
				}
				seenPageSpaces.put(realm, realm);
			}
		}
	}
	
	
	
	public Logger getLog() {
		return log;
	}
	
	public void setLog(Logger log) {
		this.log = log;
	}
	
	public List getSeedPages() {
		return seedPages;
	}
	
	public void setSeedPages(List seedPages) {
		this.seedPages = seedPages;
	}
	
	public RWikiCurrentObjectDao getRWikiCurrentObjectDao() {
		return dao;
	}
	
	public void setRWikiCurrentObjectDao(RWikiCurrentObjectDao dao) {
		this.dao = dao;
	}
	
	
	
	
}
