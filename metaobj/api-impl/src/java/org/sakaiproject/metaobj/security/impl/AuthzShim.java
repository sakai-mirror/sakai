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
package org.sakaiproject.metaobj.security.impl;

import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.legacy.realm.RealmService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.exception.IdUnusedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
   protected final transient Log logger = LogFactory.getLog(getClass());

   private RealmService realmService;
   private PortalService portalService;
   private UserDirectoryService userDirectoryService;

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(function, id)) {
         throw new AuthorizationFailedException(function, id);
      }
   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(agent, function, id)) {
         throw new AuthorizationFailedException(agent, function, id);
      }
   }

   public boolean isAuthorized(String function, Id id) {
      return isAuthorized(null, function, id);
   }

   public boolean isAuthorized(Agent agent, String function, Id id) {
      String agentId = null;
      if (agent == null) {
         agentId = getUserDirectoryService().getCurrentUser().getId();
      }
      if (function.equals("maintain")) {
         return checkMaintain(agentId);
      }
      return getRealmService().unlock(agentId, function, getCurrentRealm());
   }

   protected boolean checkMaintain(String agentId) {
      Realm siteRealm = null;
      try {
         siteRealm = getRealmService().getRealm(getCurrentRealm());
      }
      catch (IdUnusedException e) {
         logger.warn("unkown realm", e);
      }
      String maintain = siteRealm.getMaintainRole();

      return siteRealm.hasRole(agentId, maintain);
   }

   protected String getCurrentRealm() {
      return "/site/" + getPortalService().getCurrentSiteId();
   }

   protected String getReference(Id id) {
      return null;
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

   public RealmService getRealmService() {
      return realmService;
   }

   public void setRealmService(RealmService realmService) {
      this.realmService = realmService;
   }

   public PortalService getPortalService() {
      return portalService;
   }

   public void setPortalService(PortalService portalService) {
      this.portalService = portalService;
   }

   public UserDirectoryService getUserDirectoryService() {
      return userDirectoryService;
   }

   public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }
}
