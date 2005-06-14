
 /**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: QtiQueries.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Disjunction;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.type.Type;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import osid.authorization.Authorization;

/**
 * @author jlannan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QtiQueries extends HibernateDaoSupport{

	private final static org.apache.log4j.Logger LOG =
			org.apache.log4j.Logger.getLogger(QtiQueries.class);
			  	
	public void persistItemResultBean(ItemResultBean irb)
	{    
		LOG.debug("persistItemResultBean()");
		
		ItemResultBean tempIRB = null;
		tempIRB = (ItemResultBean) getHibernateTemplate().get(ItemResultBean.class, irb.getItemResultPK());
		if (tempIRB == null)
			getHibernateTemplate().save(irb);
		else
			getHibernateTemplate().update(irb);
					     		
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);								
	}
  
  public void persistSectionResultBean(SectionResultBean srb)
  {
    LOG.debug("presistSectionResultBean()");
    
    SectionResultBean tempSRB = (SectionResultBean) getHibernateTemplate().get(SectionResultBean.class, srb.getSectionResultPK());
    if (tempSRB == null)
    {
      getHibernateTemplate().save(srb);   
    }
    else
    {
      getHibernateTemplate().update(srb);
    }
    
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);
  }
  
  public void persistAssessmentResultBean(AssessmentResultBean arb)
  {
    LOG.debug("persistAssessmentResultBean()");
    AssessmentResultBean tempARB = (AssessmentResultBean) getHibernateTemplate().get(AssessmentResultBean.class, arb.getAssessmentId());
    if (tempARB == null)
    {
      getHibernateTemplate().save(arb);
    }
    else
    {
      getHibernateTemplate().update(arb);
    }
    
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);
  }
		
	public List returnItemResults(String assessmentId){					
		
		return getHibernateTemplate().find(
		  "select i from ItemResultBean as i where i.itemResultPK.assessmentId = ?",
		  assessmentId, Hibernate.STRING);											     
	}
	
	public ItemResultBean retrieveItemResultBean(ItemResultPK pk)
	{    
			LOG.debug("retrieveItemResultBean()");     
			return (ItemResultBean) getHibernateTemplate().get(ItemResultBean.class, pk);				
	}
  
  public List returnSectionResults(String assessmentId)
  {
      return getHibernateTemplate().find("select i from SectionResultBean as i where i.sectionResultPK.assessmentId = ?", assessmentId, Hibernate.STRING);
  }
  
  public SectionResultBean retrieveSectionResultBean(SectionResultPK pk)
  {
    LOG.debug("retrieveSectionResultBean()");
    return (SectionResultBean) getHibernateTemplate().get(SectionResultBean.class, pk);
  }
  
  public AssessmentResultBean retrieveAssessmentResultBean(String assessmentId)
  {
    LOG.debug("retrieveAssessmentResultBean()");
    return (AssessmentResultBean) getHibernateTemplate().get(AssessmentResultBean.class, assessmentId);
  }
	
	public void persistRealizationBean(RealizationBean rb)
	{    
		LOG.debug("persistRealizationBean()");
		
		RealizationBean tempRB = (RealizationBean) getHibernateTemplate().get(RealizationBean.class, rb.getAssessmentTakenId());
		if (tempRB == null)
			getHibernateTemplate().save(rb);
		else
			getHibernateTemplate().update(rb);
					     		
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);						
	} 			
	
	public List returnGetRealizations(String assessmentTakenId){
		return getHibernateTemplate().find(
					"select r from RealizationBean as r where r.assessmentTakenId = ? and r.submitted = 0",
					assessmentTakenId, Hibernate.STRING);											     
	}
	
	public List returnTakenRealizationList(final Iterator iterAuthzs)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("returnTakenRealizationList(Iterator " + iterAuthzs + ")");
    }        
    
    if (!iterAuthzs.hasNext()){
      // return empty list
      return new ArrayList();
    }

    HibernateCallback callback = new HibernateCallback(){
  		public Object doInHibernate(Session session) throws HibernateException{
		  	Criteria crit = session.createCriteria(RealizationBean.class);
		  	
		  	// aggregate or expressions		  	
				crit.add(aggregateOrExpressionsForRealized(iterAuthzs));				
				return crit.list();
  		}
		};
		
		List returnList = (List) getHibernateTemplate().execute(callback);		
				      		                 
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		
		getHibernateTemplate().execute(callback_flush);		
    return returnList;
  }	
	
	
//	public List getTakenRealizations(Id publishedId)
//	{
//	 	String query =
//			"select r from RealizationBean as r where " +
//			"r.assessmentPublishedId = ? and r.submitted = 1";
//		return getHibernateTemplate().find(query, publishedId.toString(), Hibernate.STRING);		
//	}	
	
	public List returnGetAllRealizations(String assessmentTakenId){
			return getHibernateTemplate().find(
				"select r from RealizationBean as r where r.assessmentTakenId = ?",
				assessmentTakenId, Hibernate.STRING);											     
  }	
	
	public List returnNonSubmittedRealizations(String publishedAssessmentId, String agentId){
			return getHibernateTemplate().find(
						"select r from RealizationBean as r where r.assessmentPublishedId = ? and r.agentId = ? " +
						"and r.submitted = 0",
						new Object[] {publishedAssessmentId, agentId},
						new Type[] {Hibernate.STRING, Hibernate.STRING});											     						
  }	
	
	public RealizationBean returnRealizationBean(String assessmentId){								
	  return (RealizationBean) getHibernateTemplate().get(RealizationBean.class, assessmentId);				
	}	
	
	public void persistSettingsBean(QtiSettingsBean sb)
	{    
		LOG.debug("persistSettingsBean()");
		
		QtiSettingsBean tempSB = (QtiSettingsBean) getHibernateTemplate().get(QtiSettingsBean.class, sb.getId());
		if (tempSB == null)
			getHibernateTemplate().save(sb);
		else
			getHibernateTemplate().update(sb);
					     		
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);						
	}
	
	public QtiSettingsBean returnQtiSettingsBean(String publishedId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("returnQtiSettingsBean(String " + publishedId + ")");
    }

    return (QtiSettingsBean) getHibernateTemplate().get(QtiSettingsBean.class,
        publishedId);
  }
	
	public List returnQtiSettingsBeanList(final Iterator iterAuthzs)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("returnQtiSettingsBeanList(Collection " + iterAuthzs + ")");
    }        
    
    if (!iterAuthzs.hasNext()){
      // return empty list
      return new ArrayList();
    }

    HibernateCallback callback = new HibernateCallback(){
  		public Object doInHibernate(Session session) throws HibernateException{
		  	Criteria crit = session.createCriteria(QtiSettingsBean.class);
		  	
		  	// aggregate or expressions
				crit.add(aggregateOrExpressions(iterAuthzs));
				crit.addOrder(Order.asc("displayName"));
				
				return crit.list();
  		}
		};
		
		List returnList = (List) getHibernateTemplate().execute(callback);		
				      		                 
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		
		getHibernateTemplate().execute(callback_flush);		
    return returnList;
  }	
	
	public void deleteSettingsBean(String publishedId){
		
		// only way to delete w/o catching Hibernate Exception is to get first
		QtiSettingsBean qsb = (QtiSettingsBean) getHibernateTemplate().get(QtiSettingsBean.class, publishedId);
		
		if (qsb != null){
			getHibernateTemplate().delete(qsb);				
		}			
		
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);		 	    														 	
	}
	
	public void deletePublishedAssessmentBean(String publishedId){
		
    //	only way to delete w/o catching Hibernate Exception is to get first
		PublishedAssessmentBean pab = (PublishedAssessmentBean) getHibernateTemplate().get(PublishedAssessmentBean.class, publishedId);

		if (pab != null){
		  getHibernateTemplate().delete(pab);				
		}
		HibernateCallback callback_flush = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				session.flush();
				return null;
			}
		};
		getHibernateTemplate().execute(callback_flush);
			 						
	}		
	
	private Criterion aggregateOrExpressions(Iterator iterAuthzs){
	  Criterion returnCriterion = null;
	  Disjunction disJunct = Expression.disjunction();
	  
	  try{		  	
      	
//		  if (iterAuthzs.hasNext()){
//		    Authorization tempAuthorization = (Authorization) iterAuthzs.next();
//			  String temp = tempAuthorization.getQualifier().getId().toString();
//			  //LOG.debug("adding qualifier id string: " + temp);
//			  returnCriterion = Expression.eq("id", temp);			
//		  }
		  while (iterAuthzs.hasNext()){
		    Authorization tempAuthorization = (Authorization) iterAuthzs.next();
		    String temp = tempAuthorization.getQualifier().getId().toString();
			  //LOG.debug("adding qualifier id string: " + temp);
			  //Criterion tempCriterion = Expression.eq("id", temp);
			  disJunct.add(Expression.eq("id", temp));
			  //returnCriterion = Expression.or(returnCriterion, tempCriterion);			  
		  }
	  }
		catch(Exception e)
	  {
	    LOG.error(e.getMessage(), e);
	    throw new Error(e);
	  }
						 
		return disJunct;
	}
	
	private Criterion aggregateOrExpressionsForRealized(Iterator iterAuthzs){
	  Criterion returnCriterion = null,
	            tempCriterion = null,
	            orCriterion = null;
	  Disjunction disJunct = Expression.disjunction();
	  
	  try{		  	
      		    
	    returnCriterion = Expression.eq("submitted", new Integer(1));	    
	    	    	    
		  while (iterAuthzs.hasNext()){
		    Authorization tempAuthorization = (Authorization) iterAuthzs.next();
		    String temp = tempAuthorization.getQualifier().getId().toString();
			  //LOG.debug("adding qualifier id string: " + temp);
		    		    
			  tempCriterion = Expression.eq("assessmentPublishedId", temp);
			  disJunct.add(tempCriterion);			  			  			 
		  }
		  
		  // add the disJunct criterion to the returnCriterion
		  returnCriterion = Expression.and(returnCriterion, disJunct);		  
		  
	  }
		catch(Exception e)
	  {
	    LOG.error(e.getMessage(), e);
	    throw new Error(e);
	  }
						 
		return returnCriterion;
	}		
}
