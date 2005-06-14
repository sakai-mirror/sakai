/*
 * Created on 2005-1-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.assessment.facade;

import java.sql.SQLException;
import java.util.*;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.Hibernate;

import org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;

import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;


/**
 * @author cwen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuthzQueriesFacade
	extends HibernateDaoSupport
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthzQueriesFacade.class);
  private final static String HQL_QUERY_CHECK_AUTHZ =
    "select from " +
		"org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData as data" +
		" where data.agentIdString = :agentId and data.functionId = :functionId" + 
		" and data.qualifierId = :qualifierId";
  private final static String HQL_QUERY_BY_AGENT_FUNC = 
    "select from org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData " +
    "as item where item.agentIdString = :agentId and item.functionId = :functionId";
  private final static String HQL_QUERY_ASSESS_BY_AGENT_FUNC = "select asset from " +
		"org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData as asset, " +
		"org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData as authz " +
		"where asset.assessmentBaseId=authz.qualifierId and " +
		"authz.agentIdString = :agentId and authz.functionId = :functionId";
  
  public void saveAuthorization(AuthorizationData ad)
  {
    //getHibernateTemplate().saveOrUpdate(ad);
    Session session = getSession();
    Transaction tx;
    try
    {
      tx = session.beginTransaction();
      session.save(ad);
      tx.commit();
      session.close();
    }
    catch (HibernateException e)
    {
      e.printStackTrace();
    }
  }
  
    // this method is added by daisyf on 02/22/05
  public boolean isAuthorized(final String agentId, 
      final String functionId, final String qualifierId)
  {
    String query = "select a from AuthorizationData a where a.functionId=? and a.qualifierId=?";
    List authorizationList = getHibernateTemplate().find(query,
                             new Object[] { functionId, qualifierId },
                             new net.sf.hibernate.type.Type[] { Hibernate.STRING, Hibernate.STRING });    

    String siteAgentId = org.sakaiproject.service.framework.portal.cover.PortalService.getCurrentSiteId();
    String currentAgentId = UserDirectoryService.getCurrentUser().getId();
    if(siteAgentId == null)
      throw new IllegalArgumentException("Null Argument");
    System.out.println("**** siteAgentId"+siteAgentId);
    for (int i=0; i<authorizationList.size(); i++){
      AuthorizationData a = (AuthorizationData) authorizationList.get(i);
      String agId = a.getAgentIdString();
      // --- pretend to have asked site and verify that this is a group ---
      // --- put the site call here ---

      // ask if current user is a member of the group, if so return true
      // else false.
      if (("AUTHENTICATED_USERS").equals(agId) && (currentAgentId!=null)){
        return true;
      }
      else if (("ANONYMOUS_USERS").equals(agId)){
        return true;
      }
      else if(siteAgentId.equals(agId))
      {
        return true;
      }
    }
    return false;
  }

  public boolean checkAuthorization(final String agentId, 
      final String functionId, final String qualifierId)
  {
/*    if (agentId == null || functionId == null || qualifierId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }*/
    if (functionId == null || qualifierId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    final String queryAgentId = org.sakaiproject.service.framework.portal.cover.PortalService.getCurrentSiteId();
    
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query query = session.createQuery(HQL_QUERY_CHECK_AUTHZ);
        //query.setString("agentId", agentId);
        if(agentId == null)
          query.setString("agentId", queryAgentId);
        else
          query.setString("agentId", agentId);
        query.setString("functionId", functionId);
        query.setString("qualifierId", qualifierId);
        return query.uniqueResult();
        //return query.list();
      }
    };
    Object result = (AuthorizationData)getHibernateTemplate().execute(hcb);
    
    if(result != null)
      return true;
    else
      return false;
  }

  public AuthorizationData createAuthorization(
      String agentId, String functionId,
      String qualifierId)
  {
    if (agentId == null || functionId == null || qualifierId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      AuthorizationData ad = new AuthorizationData();

      Calendar cal = Calendar.getInstance();
      Date lastModifiedDate = cal.getTime();
      
      ad.setAgentIdString(agentId);
      ad.setFunctionId(functionId);
      ad.setQualifierId(qualifierId);
      ad.setLastModifiedBy("someone");
      ad.setLastModifiedDate(lastModifiedDate);
      saveAuthorization(ad);
      return ad;
    }
  }
  
  public ArrayList getAssessments(final String agentId, final String functionId)
  {
    ArrayList returnList = new ArrayList();
    if (agentId == null || functionId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      HibernateCallback hcb = new HibernateCallback()
      {                
        public Object doInHibernate(Session session) throws HibernateException,
            SQLException
        {
          Query query = session.createQuery(HQL_QUERY_BY_AGENT_FUNC);
          query.setString("agentId", agentId);
          query.setString("functionId", functionId);
          return query.list();
        }
      };
      List result = (List)getHibernateTemplate().execute(hcb);
      for (int i=0; i<result.size();i++){
        AuthorizationData ad = (AuthorizationData) result.get(i);
        returnList.add(ad);
      }
    }
    
    return returnList;
  }
  
  public ArrayList getAssessmentsByAgentAndFunction(final String agentId, final String functionId)
  {
    ArrayList returnList = new ArrayList();
    if (agentId == null || functionId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      HibernateCallback hcb = new HibernateCallback()
      {                
        public Object doInHibernate(Session session) throws HibernateException,
        SQLException
        {
          Query query = session.createQuery(HQL_QUERY_ASSESS_BY_AGENT_FUNC);
          query.setString("agentId", agentId);
          query.setString("functionId", functionId);
          return query.list();
        }
      };
      List result = (List)getHibernateTemplate().execute(hcb);
      for(int i=0; i<result.size(); i++)
      {
        AssessmentBaseData ad = (AssessmentBaseData)result.get(i);
        returnList.add(ad);
      }
    }
    
    return returnList;
  }
}
