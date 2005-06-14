package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
/**
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
*/

/**
 *
 * An Authorization Facade
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @version 1.0
 */
public class AuthzQueriesFacade
          extends HibernateDaoSupport
{

  public  boolean isAuthorized
    (String agentId, String function, String qualifier)
  {
    return true;
  }

  public AuthorizationData createAuthorization
    (String agentId, String functionId, String qualifierId)
  {
    AuthorizationData a = new AuthorizationData(
      agentId, functionId, qualifierId,
      new Date(), new Date(),
      AgentFacade.getAgentString(), new Date(), Boolean.TRUE);
      getHibernateTemplate().save(a);
      return a;
  }

  public void removeAuthorizationByQualifier(String qualifierId) {
    List l = getHibernateTemplate().find(
        "select a from AuthorizationData a where a.qualifierId="+qualifierId);
    getHibernateTemplate().deleteAll(l);
  }

  /** This returns a HashMap containing (String a.qualiferId, AuthorizationData a)
   * agentId is a site for now but can be a user
   */
  public HashMap getAuthorizationToViewAssessments(String agentId) {
    HashMap h = new HashMap();
    List l = getAuthorizationByAgentAndFunction(agentId, "VIEW_ASSESSMENT");
    for (int i=0; i<l.size();i++){
      AuthorizationData a = (AuthorizationData) l.get(i);
      h.put(a.getQualifierId(), a);
    }
    return h;
  }

  public List getAuthorizationByAgentAndFunction(String agentId, String functionId) {
    String query = "select a from AuthorizationData a where a.agentIdString='"+ agentId +
        "' and a.functionId='"+functionId+"'";
    System.out.println("query="+query);
    return getHibernateTemplate().find(query);
  }

  public List getAuthorizationByFunctionAndQualifier(String functionId, String qualifierId) {
    return getHibernateTemplate().find(
        "select a from AuthorizationData a where a.functionId='"+ functionId +
        "' and a.qualifierId='"+qualifierId+"'");
  }

  public boolean checkMembership(String siteId) {
    return true;
    /**
    boolean isMember = false;
    String realmName = "/site/" + siteId;
    Realm siteRealm = RealmService.getRealm(realmName);
    if (siteRealm.getUserRole(AgentFacade.getAgentString()) != null)
      isMember = true;
    return isMember;
        */
  }
}
