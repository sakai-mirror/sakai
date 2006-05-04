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
package uk.ac.cam.caret.sakai.rwiki.component.dao.impl;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.sakaiproject.service.framework.log.Logger;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObjectContentImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiObjectContentDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObjectContent;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObjectContent;
import uk.ac.cam.caret.sakai.rwiki.utils.TimeLogger;

//FIXME: Component

public class RWikiCurrentObjectContentDaoImpl extends HibernateDaoSupport implements RWikiObjectContentDao {
	private Logger log;
	public RWikiObjectContent getContentObject(final RWikiObject parent) {
	       long start = System.currentTimeMillis();
	        try {
	        HibernateCallback callback = new HibernateCallback() {
	            public Object doInHibernate(Session session)
	                    throws HibernateException {
	                return session.createCriteria(RWikiCurrentObjectContent.class).add(
	                        Expression.eq("rwikiid", parent.getId())).list();
	            }
	            
	        };
	        List found = (List) getHibernateTemplate().execute(callback);
	        if (found.size() == 0) {
	            if (log.isDebugEnabled()) {
	                log.debug("Found " + found.size() + " objects with id "
	                        + parent.getId() );
	            }
	            return null;
	        }
	        if (log.isDebugEnabled()) {
	            log.debug("Found " + found.size() + " objects with name " + parent.getId()
	                    + " returning most recent one.");
	        }
	        return (RWikiObjectContent) found.get(0);
	        } finally {
	            long finish = System.currentTimeMillis();
	            TimeLogger.printTimer("RWikiCurrentObjectContentDaoImpl.getContentObject: " + parent.getId(),start,finish);
	        }
	}

	public RWikiObjectContent createContentObject(RWikiObject parent) {
		RWikiCurrentObjectContent rwco = new RWikiCurrentObjectContentImpl();
		
		rwco.setRwikiid(parent.getId());
		return rwco;
	}

	public void update(RWikiObjectContent content) {
		 RWikiCurrentObjectContentImpl impl = (RWikiCurrentObjectContentImpl) content;
	     getHibernateTemplate().saveOrUpdate(impl);
	     
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

}
