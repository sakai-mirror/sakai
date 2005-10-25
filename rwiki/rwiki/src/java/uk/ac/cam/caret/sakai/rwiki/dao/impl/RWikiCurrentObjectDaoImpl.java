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
package uk.ac.cam.caret.sakai.rwiki.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.Type;

import org.sakaiproject.service.framework.log.Logger;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.dao.RWikiHistoryObjectDao;
import uk.ac.cam.caret.sakai.rwiki.dao.RWikiObjectContentDao;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiHistoryObject;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.model.impl.RWikiCurrentObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.tool.RWikiServlet;

public class RWikiCurrentObjectDaoImpl extends HibernateDaoSupport implements
        RWikiCurrentObjectDao, ObjectProxy {
    private Logger log;
    
    
    protected RWikiObjectContentDao contentDAO = null;
    
    protected RWikiHistoryObjectDao historyDAO = null;
    

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#exists(java.lang.String)
     */
    public boolean exists(final String name) {
        long start = System.currentTimeMillis();
        try {
            HibernateCallback callback = new HibernateCallback() {

                public Object doInHibernate(Session session)
                        throws HibernateException, SQLException {
                    return session
                            .find(
                                    "select count(*) from RWikiCurrentObjectImpl r where r.name = ?",
                                    name, Hibernate.STRING);
                }

            };

            Integer count = (Integer) getHibernateTemplate().executeFind(
                    callback).get(0);

            return (count.intValue() > 0);
        } finally {
            long finish = System.currentTimeMillis();
            RWikiServlet.printTimer("RWikiObjectDaoImpl.exists: " + name,start,finish);
        }
    }

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#findByGlobalName(java.lang.String)
     */
    public RWikiCurrentObject findByGlobalName(final String name) {
        long start = System.currentTimeMillis();
        try {
        	// there is no point in sorting by version, since there is only one version in 
        	// this table.
        	// also using like is much slower than eq
        HibernateCallback callback = new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                return session.createCriteria(RWikiCurrentObject.class).add(
                        Expression.eq("name", name)).list();
            }
        };
        List found = (List) getHibernateTemplate().execute(callback);
        if (found.size() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Found " + found.size() + " objects with name "
                        + name );
            }
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Found " + found.size() + " objects with name " + name
                    + " returning most recent one.");
        }
        return (RWikiCurrentObject) proxyObject(found.get(0));
        } finally {
            long finish = System.currentTimeMillis();
            RWikiServlet.printTimer("RWikiObjectDaoImpl.findByGlobalName: " + name,start,finish);
        }
    }

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#findByGlobalNameAndContents(java.lang.String, java.lang.String, java.lang.String)
     */
    public List findByGlobalNameAndContents(final String criteria,
            final String user, final String realm) {

        String[] criterias = criteria.split("\\s\\s*");

        final StringBuffer expression = new StringBuffer();
        final List criteriaList = new ArrayList();
        criteriaList.add(realm);
		criteriaList.add("%"+criteria+"%");
		criteriaList.add("%"+criteria+"%");
        
		
		// WARNING: In MySQL like does not produce a case sensitive search so this is Ok
		// Oracle can probaly do it, but would need some set up (maybee)
		// http://asktom.oracle.com/pls/ask/f?p=4950:8:::::F4950_P8_DISPLAYID:16370675423662
		
        for (int i = 0; i < criterias.length; i++) {
            if (!criterias[i].equals("")) {
            		expression.append(" or c.content like ? ");
            		criteriaList.add("%"+criterias[i]+"%");
            }
        }
        if (criteria.equals("")) {
    			expression.append(" or c.content like ? ");
    			criteriaList.add("%%");
        }
        final Type[] types = new Type[criteriaList.size()];
        for ( int i = 0; i < types.length; i++ ) {
        		types[i] = Hibernate.STRING;
        }
        

        HibernateCallback callback = new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
            	return session
                .find(
                        "select distinct r " +
                        "		from RWikiCurrentObjectImpl as r, " +
                        "			RWikiCurrentObjectContentImpl as c " +
                        "   where " +
                        "r.realm = ? and (" +
                        "r.name like ? or " +
                        "          c.content like ? " +
                        expression.toString() +
                        " ) and " +
                        "			r.id = c.rwikiid " +
                        "  order by r.name ",
                        criteriaList.toArray(),
                        types);
            	
            }
        };
        return new ListProxy((List) getHibernateTemplate().execute(callback), this);
    }

  

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#update(uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObject)
     */
    public void update(RWikiCurrentObject rwo, RWikiHistoryObject rwho) {
        // should have already checked
    	    RWikiCurrentObjectImpl impl = (RWikiCurrentObjectImpl) rwo;
        getHibernateTemplate().saveOrUpdate(impl);
        // update the history
        if ( rwho != null ) {
    			rwho.setRwikiobjectid(impl.getId());
			historyDAO.update(rwho);
        }
        // remember to save the content
        impl.getRWikiObjectContent().setRwikiid(rwo.getId());
        contentDAO.update(impl.getRWikiObjectContent());
        
    }

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#createRWikiObject(java.lang.String, java.lang.String)
     */
    public RWikiCurrentObject createRWikiObject(String name, String realm) {
    	

        RWikiCurrentObjectImpl returnable = new RWikiCurrentObjectImpl();
        proxyObject(returnable);
        returnable.setName(name);
        returnable.setRealm(realm);
        returnable.setVersion(new Date());
        returnable.setRevision(new Integer(0));
        // FIXME internationalize!!
        
        returnable.setContent("No page content exists for this "
                + "page, please create");
        return returnable;
    }

    /**
     * Standard Logger
     * @return
     */
    public Logger getLog() {
        return log;
    }

    /**
     * Standard Logger
     * @param log
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#findChangedSince(java.util.Date, java.lang.String)
     */
    public List findChangedSince(final Date since, final String realm) {
        HibernateCallback callback = new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                return session.createCriteria(RWikiCurrentObject.class).add(
                        Expression.ge("version", since)).add(
                        Expression.eq("realm", realm)).addOrder(
                        Order.desc("version")).list();
            }
        };
        return new ListProxy((List) getHibernateTemplate().execute(callback), this);
    }


    /**
     * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#findReferencingPages(java.lang.String)
     */
    public List findReferencingPages(final String name) {
        HibernateCallback callback = new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                return session
                        .find(
                                "select r.name from RWikiCurrentObjectImpl r where referenced like ?",
                                "%::" + name + "::%", Hibernate.STRING);
            }
        };
        return new ListProxy((List) getHibernateTemplate().execute(callback), this);
    }

	/**
	 * @see uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao#getRWikiCurrentObject(uk.ac.cam.caret.sakai.rwiki.model.RWikiObject)
	 */
    public RWikiCurrentObject getRWikiCurrentObject(final RWikiObject reference) {
	        long start = System.currentTimeMillis();
	        try {
	        HibernateCallback callback = new HibernateCallback() {
	            public Object doInHibernate(Session session)
	                    throws HibernateException {
	                return session.createCriteria(RWikiCurrentObject.class).add(
	                        Expression.eq("id", reference.getRwikiobjectid())).list();
	            }
	        };
	        List found = (List) getHibernateTemplate().execute(callback);
	        if (found.size() == 0) {
	            if (log.isDebugEnabled()) {
	                log.debug("Found " + found.size() + " objects with id "
	                        +  reference.getRwikiobjectid());
	            }
	            return null;
	        }
	        if (log.isDebugEnabled()) {
	            log.debug("Found " + found.size() + " objects with id " + reference.getRwikiobjectid()
	                    + " returning most recent one.");
	        }
	        return (RWikiCurrentObject) proxyObject(found.get(0));
	        } finally {
	            long finish = System.currentTimeMillis();
	            RWikiServlet.printTimer("RWikiCurrentObjectDaoImpl.getRWikiCurrentObject: " + reference.getName(),start,finish);
	        }
	}

	public RWikiObjectContentDao getContentDAO() {
		return contentDAO;
	}

	public void setContentDAO(RWikiObjectContentDao contentDAO) {
		this.contentDAO = contentDAO;  
	}

	public Object proxyObject(Object o) {
		if (o != null && o instanceof RWikiCurrentObjectImpl) {
			RWikiCurrentObjectImpl rwCo = (RWikiCurrentObjectImpl) o;
			rwCo.setRwikiObjectContentDao(contentDAO);
		}
		return o;
	}

	public RWikiHistoryObjectDao getHistoryDAO() {
		return historyDAO;
	}

	public void setHistoryDAO(RWikiHistoryObjectDao historyDAO) {
		this.historyDAO = historyDAO;
	}

	public List getAll() {
        HibernateCallback callback = new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                return session.createCriteria(RWikiCurrentObject.class).addOrder(
                        Order.desc("version")).list();
            }
        };
        return new ListProxy((List) getHibernateTemplate().execute(callback), this);
	}
	public void updateObject(RWikiObject rwo) {
        getHibernateTemplate().saveOrUpdate(rwo);
	}
}
