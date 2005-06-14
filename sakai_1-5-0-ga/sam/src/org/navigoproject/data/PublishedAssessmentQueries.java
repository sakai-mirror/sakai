
 /**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: PublishedAssessmentQueries.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author jlannan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PublishedAssessmentQueries extends HibernateDaoSupport{

	private final static org.apache.log4j.Logger LOG =
			org.apache.log4j.Logger.getLogger(PublishedAssessmentQueries.class);
			  	
	public void persistPublishedAssessment(PublishedAssessmentBean pab)
	{    
		LOG.debug("persistPublishedAssessmentBean()");
		
		PublishedAssessmentBean tempPA = (PublishedAssessmentBean) getHibernateTemplate().get(PublishedAssessmentBean.class, pab.getPublishedId());
		if (tempPA == null)
			getHibernateTemplate().save(pab);
		else
			getHibernateTemplate().update(pab);
					     		
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);						
	}    
		
	public PublishedAssessmentBean getByPublishedId(String publishedAssessmentId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getByPublishedId(String " + publishedAssessmentId + ")");
    }

    PublishedAssessmentBean pab = null;

    return (PublishedAssessmentBean) getHibernateTemplate().get(
        PublishedAssessmentBean.class, publishedAssessmentId);
  }
	
	public List findByCoreId(String coreId){
		
		PublishedAssessmentBean pab = null;							
		
		return getHibernateTemplate().find(
			"select p from PublishedAssessmentBean as p where p.coreId = ?",
		  coreId, Hibernate.STRING);											     

	}
  
  
  public void deleteByPublishedId(String publishedAssessmentId) {
    
    PublishedAssessmentBean pab = getByPublishedId(publishedAssessmentId);    
    getHibernateTemplate().delete(pab); 
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);      
  }
  

  
  public PublishedAssessmentBean findCoreIdByPublishedId(String publishedId){
    
    PublishedAssessmentBean pab = null;             
    
    return (PublishedAssessmentBean) getHibernateTemplate().get(
      PublishedAssessmentBean.class, publishedId);                                 
  }   		

}
