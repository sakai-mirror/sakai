/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.message.dao.impl;

import java.util.Date;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.sakaiproject.service.framework.log.Logger;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import uk.ac.cam.caret.sakai.rwiki.component.message.model.impl.MessageImpl;
import uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Message;

/**
 * @author ieb
 *
 */
public class MessageDaoImpl extends HibernateDaoSupport implements
        MessageDao {
    private Logger log;

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#createMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Message createMessage(String pageSpace, String pageName, String sessionid, String user, String message) {
        Message m = new MessageImpl();
        m.setLastseen(new Date());
        m.setPagename(pageName);
        m.setPagespace(pageSpace);
        m.setSessionid(sessionid);
        m.setUser(user);
        m.setMessage(message);
        return m;
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#findBySpace(java.lang.String)
     */
    public List findBySpace(final String pageSpace) {
        long start = System.currentTimeMillis();
        try {
            // there is no point in sorting by version, since there is only one version in 
            // this table.
            // also using like is much slower than eq
            HibernateCallback callback = new HibernateCallback() {
                public Object doInHibernate(Session session)
                throws HibernateException {
                    return session.createCriteria(Message.class).add(
                            Expression.eq("pagespace", pageSpace)).list();
                }
            };
            return (List)getHibernateTemplate().execute(callback);
        } finally {
            long finish = System.currentTimeMillis();
            TimeLogger.printTimer("PagePresenceDaoImpl.findBySpace: " + pageSpace,start,finish);
        }    

    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#findByPage(java.lang.String, java.lang.String)
     */
    public List findByPage(final String pageSpace, final String pageName) {
        long start = System.currentTimeMillis();
        try {
            // there is no point in sorting by version, since there is only one version in 
            // this table.
            // also using like is much slower than eq
            HibernateCallback callback = new HibernateCallback() {
                public Object doInHibernate(Session session)
                throws HibernateException {
                    return session.createCriteria(Message.class).add(
                            Expression.eq("pagespace", pageSpace)).add(
                                    Expression.eq("pagename",pageName)).list();
                }
            };
            return (List)getHibernateTemplate().execute(callback);
        } finally {
            long finish = System.currentTimeMillis();
            TimeLogger.printTimer("PagePresenceDaoImpl.findByPage: " + pageSpace + ":" + pageName,start,finish);
        }    
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#findByUser(java.lang.String)
     */
    public List findByUser(final String user) {
        long start = System.currentTimeMillis();
        try {
            // there is no point in sorting by version, since there is only one version in 
            // this table.
            // also using like is much slower than eq
            HibernateCallback callback = new HibernateCallback() {
                public Object doInHibernate(Session session)
                throws HibernateException {
                    return session.createCriteria(Message.class).add(
                            Expression.eq("user", user)).list();
                }
            };
            return (List)getHibernateTemplate().execute(callback);
        } finally {
            long finish = System.currentTimeMillis();
            TimeLogger.printTimer("PagePresenceDaoImpl.findByUser: " + user,start,finish);
        }    
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#update(java.lang.Object)
     */
    public void update(Object o) {
        getHibernateTemplate().saveOrUpdate(o);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.MessageDao#findBySession(java.lang.String)
     */
    public List findBySession(String session) {
        long start = System.currentTimeMillis();
        try {
            // there is no point in sorting by version, since there is only one version in 
            // this table.
            // also using like is much slower than eq
            HibernateCallback callback = new HibernateCallback() {
                public Object doInHibernate(Session session)
                throws HibernateException {
                    return session.createCriteria(Message.class).add(
                            Expression.eq("sessionid", session)).list();
                }
            };
            return (List)getHibernateTemplate().execute(callback);
        } finally {
            long finish = System.currentTimeMillis();
            TimeLogger.printTimer("PagePresenceDaoImpl.findBySession: " + session,start,finish);
        }    
    }

 
    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }



}
