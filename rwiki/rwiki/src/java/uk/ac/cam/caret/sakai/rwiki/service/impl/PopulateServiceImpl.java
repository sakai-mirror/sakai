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
package uk.ac.cam.caret.sakai.rwiki.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.PopulateService;

/**
 * @author andrew
 *
 */
public class PopulateServiceImpl implements PopulateService {

    private Logger log;
    private HashMap seenRealms = new HashMap();
    private List seedPages;
    
    private RWikiCurrentObjectDao dao;
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.PopulateService#populateRealm(java.lang.String, java.lang.String)
     */
    public void populateRealm(String user, String realm)
            throws PermissionException {
        
        synchronized (seenRealms) {
            if (seenRealms.get(realm) != null ) {
                // Already populated!
                return;
            }
            for ( Iterator i = seedPages.iterator(); i.hasNext(); ) {
                if (log.isDebugEnabled()) {
                    log.debug("Populating realm: " + realm);
                }
                
                RWikiCurrentObject seed = (RWikiCurrentObject) i.next();
                
                String name = NameHelper.globaliseName(seed.getName(), realm);
                if (dao.findByGlobalName(name) == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Creating Page: " + name);
                    }
                    RWikiCurrentObject rwo = dao.createRWikiObject(name, realm);
                    seed.copyTo(rwo);
                    rwo.setName(name);
                    rwo.setRealm(realm);
                    dao.update(rwo,null);
                }
            }
            seenRealms.put(realm, realm);
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
