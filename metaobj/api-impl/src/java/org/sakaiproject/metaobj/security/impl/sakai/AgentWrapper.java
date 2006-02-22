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
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.user.User;

import java.util.ArrayList;
import java.util.List;


public class AgentWrapper extends IdentifiableObject implements Agent {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private User sakaiUser = null;
   private Id id = null;
   private StructuredArtifact profile = null;
   private SecurityBase securityBase;

   public AgentWrapper(Id id, User sakaiUser, StructuredArtifact profile, SecurityBase securityBase) {
      this.id = id;
      this.sakaiUser = sakaiUser;
      this.profile = profile;
      this.securityBase = securityBase;
   }

   public Id getId() {
      return id;
   }

   public Artifact getProfile() {
      return profile;
   }

   public void setProfile(StructuredArtifact profile) {
      this.profile = profile;
   }

   public Object getProperty(String key) {
      return profile.get(key);
   }

   public String getDisplayName() {
      return sakaiUser.getDisplayName();
   }

   public boolean isInRole(String role) {
      return role.equals(sakaiUser.getType());
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return sakaiUser.getType();
   }

   public List getWorksiteRoles(String worksiteId) {
      List returned = new ArrayList();

      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               worksiteId);

         Role role = siteRealm.getUserRole(getSakaiUser().getId());

         if (role != null) {
            returned.add(getSecurityBase().convertRole(role, siteRealm));
         }
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return returned;
   }

   public List getWorksiteRoles() {
      return getWorksiteRoles(PortalService.getCurrentSiteId());
   }

   public boolean isRole() {
      return false;
   }

   public User getSakaiUser() {
      return sakaiUser;
   }

   public org.sakaiproject.metaobj.shared.mgt.AgentManager getAgentManager() {
      return (org.sakaiproject.metaobj.shared.mgt.AgentManager) ComponentManager.getInstance().get("agentManager");
   }

   public SecurityBase getSecurityBase() {
      return securityBase;
   }

   /**
    * Returns the name of this principal.
    *
    * @return the name of this principal.
    */
   public String getName() {
      return getDisplayName();
   }
}
