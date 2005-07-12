package org.sakaiproject.metaobj.security.impl;

import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 30, 2005
 * Time: 4:57:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthzShim implements AuthorizationFacade {

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {

   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
   }

   public boolean isAuthorized(String function, Id id) {
      return true;
   }

   public boolean isAuthorized(Agent agent, String function, Id id) {
      return true;
   }

   public List getAuthorizations(Agent agent, String function, Id id) {
      return new ArrayList();
   }

   public void createAuthorization(Agent agent, String function, Id id) {
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
   }

   public void deleteAuthorizations(Id qualifier) {
   }
}
