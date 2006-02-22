/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.security.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.security.Authorization;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.mgt.PermissionManager;
import org.sakaiproject.metaobj.security.mgt.ToolPermissionManager;
import org.sakaiproject.metaobj.security.model.Permission;
import org.sakaiproject.metaobj.security.model.PermissionsEdit;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.site.Site;

import java.util.*;

public class PermissionManagerImpl implements PermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager;
   private AuthorizationFacade authzManager;

   private Map tools;

   public List getWorksiteRoles(PermissionsEdit edit) {
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               edit.getSiteId());

         Set roles = siteRealm.getRoles();
         List returned = new ArrayList();
         returned.addAll(siteRealm.getRoles());
         return returned;
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public PermissionsEdit fillPermissions(PermissionsEdit edit) {
      edit.setPermissions(new ArrayList());

      edit = fillPermissionsInternal(edit, edit.getQualifier(), false);

      ToolPermissionManager mgr = getToolManager(edit);
      List quals = mgr.getReadOnlyQualifiers(edit);

      for (Iterator i = quals.iterator(); i.hasNext();) {
         Id qualifier = (Id) i.next();
         fillPermissionsInternal(edit, qualifier, true);
      }

      return edit;
   }

   protected PermissionsEdit fillPermissionsInternal(PermissionsEdit edit, Id qualifier, boolean readOnly) {

      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               edit.getSiteId());

         Set roles = siteRealm.getRoles();

         for (Iterator i = roles.iterator(); i.hasNext();) {
            Role role = (Role) i.next();
            Agent currentRole = getAgentManager().getWorksiteRole(role.getId(), edit.getSiteId());
            List authzs = getAuthzManager().getAuthorizations(currentRole, null, qualifier);

            for (Iterator j = authzs.iterator(); j.hasNext();) {
               Authorization authz = (Authorization) j.next();
               edit.getPermissions().add(new Permission(currentRole, authz.getFunction(), readOnly));
            }
         }
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return edit;
   }

   public void updatePermissions(PermissionsEdit edit) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = null;

      PermissionsEdit orig = (PermissionsEdit) edit.clone();
      orig = fillPermissions(orig);
      origPermissions = orig.getPermissions();

      for (Iterator i = edit.getPermissions().iterator(); i.hasNext();) {
         Permission perm = (Permission) i.next();

         if (origPermissions.contains(perm)) {
            origPermissions.remove(perm);
         }
         else if (!perm.isReadOnly()) {
            manager.createAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
         }
      }

      for (Iterator i = origPermissions.iterator(); i.hasNext();) {
         Permission perm = (Permission) i.next();

         manager.deleteAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
      }

   }

   public void duplicatePermissions(Id srcQualifier, Id targetQualifier, Site newSite) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = manager.getAuthorizations(null, null, srcQualifier);

      for (Iterator i = origPermissions.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         Agent agent = authz.getAgent();
         if (newSite != null && agent instanceof OspRole) {
            agent = getAgentManager().getTempWorksiteRole(((OspRole) agent).getRoleName(), newSite.getId());
         }

         if (agent != null) {
            manager.createAuthorization(agent, authz.getFunction(), targetQualifier);
         }
      }
   }

   public List getAppFunctions(PermissionsEdit edit) {
      ToolPermissionManager mgr = getToolManager(edit);

      return mgr.getFunctions(edit);
   }

   protected ToolPermissionManager getToolManager(PermissionsEdit edit) {
      return (ToolPermissionManager) getTools().get(edit.getName());
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Map getTools() {
      return tools;
   }

   public void setTools(Map tools) {
      this.tools = tools;
   }
}
