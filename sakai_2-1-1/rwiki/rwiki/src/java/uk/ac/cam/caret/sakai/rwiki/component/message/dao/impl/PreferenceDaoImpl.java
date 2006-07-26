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

import uk.ac.cam.caret.sakai.rwiki.component.message.model.impl.PreferenceImpl;
import uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PreferenceDao;
import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference;

/**
 * @author ieb
 *
 */
public class PreferenceDaoImpl extends HibernateDaoSupport implements
        PreferenceDao {
    private Logger log;

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PreferenceDao#findByUser(java.lang.String)
     */
    public Preference findByUser(final String user) {
        long start = System.currentTimeMillis();
        try {
            // there is no point in sorting by version, since there is only one version in 
            // this table.
            // also using like is much slower than eq
            HibernateCallback callback = new HibernateCallback() {
                public Object doInHibernate(Session session)
                throws HibernateException {
                    return session.createCriteria(Preference.class).add(
                            Expression.eq("user", user)).list();
                }
            };
            List found = (List) getHibernateTemplate().execute(callback);
            if (found.size() == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Found " + found.size() + " objects with name "
                            + user );
                }
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug("Found " + found.size() + " objects with name " + user
                        + " returning most recent one.");
            }
            return (Preference) found.get(0);
        } finally {
            long finish = System.currentTimeMillis();
            TimeLogger.printTimer("PagePresenceDaoImpl.findByUserId: " + user,start,finish);
        }    
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PreferenceDao#createPreference(java.lang.String, java.lang.String)
     */
    public Preference createPreference(String user, String preference) {
        Preference pref = new PreferenceImpl();
        pref.setLastseen(new Date());
        pref.setPreference(preference);
        pref.setUser(user);
        return pref;
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.dao.PreferenceDao#update(java.lang.Object)
     */
    public void update(Object o) {
        getHibernateTemplate().saveOrUpdate(o);
        
    }
    
    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }



}
