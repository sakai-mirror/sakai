package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

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

  public static boolean createAuthorization
    (String agentId, String function, String qualifier)
  {
    return true;
  }
}
