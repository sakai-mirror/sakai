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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/security/impl/sakai/AgentWrapper.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.utils.BeanFactory;

import java.util.List;
import java.util.ArrayList;


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
         Realm siteRealm = RealmService.getRealm("/site/" +
            worksiteId);

         Role role = siteRealm.getUserRole(getSakaiUser().getId());

         if (role != null) {
            returned.add(getSecurityBase().convertRole(role, siteRealm));
         }
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return returned;
   }

   public List getWorksiteRoles() {
      return getWorksiteRoles(PortalService.getCurrentSiteId());
   }

   public User getSakaiUser() {
      return sakaiUser;
   }

   public org.sakaiproject.metaobj.shared.mgt.AgentManager getAgentManager() {
      return (org.sakaiproject.metaobj.shared.mgt.AgentManager)BeanFactory.getInstance().getBean("agentManager");
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
