/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/security/impl/sakai/WorksiteAuthorizer.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.app.ApplicationAuthorizer;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.exception.IdUnusedException;

import java.util.List;


public class WorksiteAuthorizer implements ApplicationAuthorizer {
   protected final transient Log logger = LogFactory.getLog(getClass());

   protected List functions;

   /**
    * This method will ask the application specific functional authorizer to determine authorization.
    *
    * @param facade   this can be used to do explicit auths if necessary
    * @param agent
    * @param function
    * @param id
    * @return null if the authorizer has no opinion, true if authorized, false if explicitly not authorized.
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent, String function, Id id) {

      try {
         // expect function==WorksiteManager.WORKSITE_MAINTAIN
         return checkRoleAccess(agent, function, id);

         /*
         Site currentSite = SiteService.getSite(PortalService.getCurrentSiteId());
         logger.debug(currentSite.getDescription());
         Realm siteRealm = RealmService.getRealm("/site/" + currentSite.getId());

         if (agent instanceof AgentWrapper) {
            Set roles = siteRealm.collectUserRoles(((AgentWrapper)agent).getSakaiUser());
            for (Iterator i = roles.iterator(); i.hasNext();) {
               Role role = (Role) i.next();
               logger.debug(role.getId());
            }
         }
         */
      } catch (org.sakaiproject.exception.IdUnusedException e) {
         logger.info("current worksite not known", e);
         return null;
      }
   }

   protected Boolean checkRoleAccess(Agent agent, String function, Id worksiteId) throws IdUnusedException {
      boolean returned = false;

      Realm siteRealm = RealmService.getRealm("/site/" + worksiteId.getValue());
      String maintain = siteRealm.getMaintainRole();
      if (agent instanceof AgentWrapper) {
         Role role = siteRealm.getUserRole(((AgentWrapper)agent).getSakaiUser().getId());
//         for (Iterator i = roles.iterator(); i.hasNext();) {
//            Role role = (Role) i.next();
            if (role.getId().equals(maintain)) {
               return new Boolean(true);
            }
//         }
      }

      return new Boolean(returned);
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

}
