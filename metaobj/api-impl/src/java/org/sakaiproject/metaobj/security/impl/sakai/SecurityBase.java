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
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.user.User;

public class SecurityBase {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private IdManager ospiIdManager = null;
   private WritableObjectHome agentHome = null;

   protected Agent morphAgent(User sakaiUser) {
      Id ospiId = getOspiIdManager().getId(sakaiUser.getId());
      //StructuredArtifact profile = (StructuredArtifact)agentHome.createInstance();

      //profile.put("email", sakaiUser.getEmail());

      //return new AgentWrapper(ospiId,  sakaiUser, profile, this);
      return new AgentWrapper(ospiId, sakaiUser, null, this);
   }


   public IdManager getOspiIdManager() {
      return ospiIdManager;
   }

   public void setOspiIdManager(IdManager ospiIdManager) {
      this.ospiIdManager = ospiIdManager;
   }


   public WritableObjectHome getAgentHome() {
      return agentHome;
   }

   public void setAgentHome(WritableObjectHome agentHome) {
      this.agentHome = agentHome;
   }

   public RoleWrapper convertRole(Role sakaiRole, AuthzGroup siteRealm) {
      if (sakaiRole == null) {
         return null;
      }
      Id roleId = getOspiIdManager().getId(siteRealm.getId() + "/" + sakaiRole.getId());

      return new RoleWrapper(roleId, sakaiRole, siteRealm);
   }
}
