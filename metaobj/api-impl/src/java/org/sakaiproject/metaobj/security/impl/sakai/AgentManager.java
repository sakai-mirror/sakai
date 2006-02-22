/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
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
package org.sakaiproject.metaobj.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.AnonymousAgent;
import org.sakaiproject.metaobj.security.PasswordGenerator;
import org.sakaiproject.metaobj.shared.mgt.AgentManagerListener;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.impl.AgentImpl;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.legacy.user.UserEdit;

import java.util.*;

public class AgentManager extends SecurityBase implements org.sakaiproject.metaobj.shared.mgt.AgentManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private org.sakaiproject.metaobj.shared.mgt.AgentManager baseAgentManager = null;
   private List listeners;
   private PasswordGenerator passwordGenerator;

   /**
    * @param id
    * @return
    */
   public Agent getAgent(Id id) {
      if (id.equals(AnonymousAgent.ANONYMOUS_AGENT_ID)) {
         return getAnonymousAgent();
      }

      Throwable exception = null;

      Agent returned = null;

      if (id != null) {
         try {
            returned = getAgentInternal(id.getValue());
         }
         catch (IdUnusedException e) {
            exception = e;
         }
      }

      if (returned != null) {
         return returned;
      }

      if (baseAgentManager != null) {
         Agent baseAgent = baseAgentManager.getAgent(id);

         return baseAgent;
      }

      if (exception != null) {
         logger.warn("Unable to find user: " + id, exception);
      }
      else {
         logger.warn("Unable to find user: " + id);
      }

      return null;
   }

   public Agent getAgent(String username) {
      if (username.equals(AnonymousAgent.ANONYMOUS_AGENT_ID.getValue())) {
         return getAnonymousAgent();
      }
      Throwable exception = null;

      Agent returned = null;
      try {
         returned = getAgentInternal(username);
      }
      catch (IdUnusedException e) {
         exception = e;
      }

      if (returned != null) {
         return returned;
      }

      if (baseAgentManager != null) {
         Agent baseAgent = baseAgentManager.getAgent(username);

         return baseAgent;
      }

      if (exception != null) {
         logger.warn("Unable to find user: " + username, exception);
      }
      else {
         logger.warn("Unable to find user: " + username);
      }

      return null;
   }

   public Agent getWorksiteRole(String roleName) {
      return getWorksiteRole(roleName, PortalService.getCurrentSiteId());
   }

   public List getWorksiteRoles(String siteId) {
      List roles = new ArrayList();
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" + siteId);
         for (Iterator i = siteRealm.getRoles().iterator(); i.hasNext();) {
            Role sakaiRole = (Role) i.next();
            roles.add(convertRole(sakaiRole, siteRealm));
         }
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      return roles;
   }

   public Agent getWorksiteRole(String roleName, String siteId) {
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               siteId);

         Role sakaiRole = siteRealm.getRole(roleName);

         return convertRole(sakaiRole, siteRealm);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public Agent getTempWorksiteRole(String roleName, String siteId) {
      Id roleId = getOspiIdManager().getId("/site/" + siteId + "/" + roleName);

      return new RoleWrapper(roleId, null, null);
   }

   protected Agent getAgentInternal(String username) throws IdUnusedException {
      if (username == null) {
         return null;
      }
      if (username.startsWith("/site/")) {
         return getRole(username);
      }

      User sakaiUser = null;

      sakaiUser = getDirectoryService().getUser(username);

      return morphAgent(sakaiUser);
   }

   protected Agent getRole(String username) throws IdUnusedException {
      String roleName;
      String siteId;

      int pos = username.lastIndexOf('/');
      siteId = username.substring(0, pos);
      roleName = username.substring(pos + 1);

      Role role = null;
      AuthzGroup realm = null;
      realm = AuthzGroupService.getAuthzGroup(siteId);
      role = realm.getRole(roleName);

      if (role == null || realm == null) {
         return null;
      }
      return convertRole(role, realm);
   }

   /**
    * @param siteId
    * @return list of agents that are participants in the given siteId
    */
   public List getWorksiteAgents(String siteId) {
      List users = new ArrayList();
      List participants = new ArrayList();
      String realmId = "/site/" + siteId;
      try {
         AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);
         users.addAll(getDirectoryService().getUsers(realm.getUsers()));
         Collections.sort(users);
         for (int i = 0; i < users.size(); i++) {
            User user = (User) users.get(i);
            participants.add(morphAgent(user));
         }
      }
      catch (IdUnusedException e) {
         logger.warn("" + realmId);
      }
      return participants;
   }

   public Agent getAnonymousAgent() {
      return new AnonymousAgent();
   }

   public Agent getAdminAgent() {
      return getAgent("admin");
   }

   /**
    * if type is null return all records
    *
    * @param type   added typed list
    * @param object
    * @return
    */
   public List findByProperty(String type, Object object) {
      if (type.equals("displayName")) {
         try {
            List users = new ArrayList();
            users.add(morphAgent(getDirectoryService().getUser((String) object)));
            return users;
         }
         catch (IdUnusedException e) {
            // user not found, return null
            return null;
         }
      }
      if (type.equals("email")) {
         List users = new ArrayList();
         Collection directoryUsers = getDirectoryService().findUsersByEmail((String) object);
         if ((directoryUsers == null) || (directoryUsers.isEmpty())) {
            return null;
         }
         for (Iterator i = directoryUsers.iterator(); i.hasNext();) {
            User u = (User) i.next();
            users.add(morphAgent(u));
         }
         return users;
      }
      return null;
   }

   /**
    * @param agent
    * @return
    */
   public Agent createAgent(Agent agent) {
      if (!agent.isInRole(Agent.ROLE_GUEST)) {
         // we don't support creating real agents
         throw new UnsupportedOperationException();
      }

      try {
         UserEdit uEdit = org.sakaiproject.service.legacy.user.cover.UserDirectoryService.addUser(agent.getId().getValue());

         //set email address
         uEdit.setEmail(agent.getId().getValue());

         // set id
         uEdit.setId(agent.getId().getValue());

         // set the guest user type
         uEdit.setType("guest");

         String pw = getPasswordGenerator().generate();
         uEdit.setPassword(pw);
         org.sakaiproject.service.legacy.user.cover.UserDirectoryService.commitEdit(uEdit);

         AgentImpl impl = (AgentImpl) agent;
         impl.setPassword(pw);

         for (Iterator i = getListeners().iterator(); i.hasNext();) {
            ((AgentManagerListener) i.next()).createAgent(agent);
         }

         return getAgent(agent.getId());
      }
      catch (RuntimeException exp) {
         throw exp;
      }
      catch (Exception exp) {
         throw new OspException(exp);
      }
   }

   /**
    * @param agent
    */
   public void deleteAgent(Agent agent) {
      throw new UnsupportedOperationException();
   }

   public void updateAgent(Agent agent) {
      throw new UnsupportedOperationException();
   }

   protected UserDirectoryService getDirectoryService() {
      return (UserDirectoryService) ComponentManager.get(UserDirectoryService.class.getName());
   }

   public org.sakaiproject.metaobj.shared.mgt.AgentManager getBaseAgentManager() {
      return baseAgentManager;
   }

   public void setBaseAgentManager(org.sakaiproject.metaobj.shared.mgt.AgentManager baseAgentManager) {
      this.baseAgentManager = baseAgentManager;
   }

   public List getListeners() {
      return listeners;
   }

   public void setListeners(List listeners) {
      this.listeners = listeners;
   }

   public PasswordGenerator getPasswordGenerator() {
      return passwordGenerator;
   }

   public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
      this.passwordGenerator = passwordGenerator;
   }
}
