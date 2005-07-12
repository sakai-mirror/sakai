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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/impl/AgentImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/impl/AgentImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.model.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;

import java.util.List;
import java.util.ArrayList;

public class AgentImpl implements Agent {
   static public final String ID = "id";
   static public final String REAL_NAME = "realName";
   static public final String EMAIL = "email";
   static public final String ROLES = "roles";

   private Id id;
   private StructuredArtifact profile;
   private String[] roles;
   private HomeFactory homeFactory;
   private String displayName;
   private String md5Password;
   private String password;
   private String role;
   private boolean initialized = false;

   public AgentImpl() {

   }

   public AgentImpl(Id id) {
      this.id = id;
   }

   public AgentImpl(StructuredArtifact profile) {
      this.profile = profile;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
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
      return displayName;
   }

   public boolean isInRole(String role) {

      for (int i = 0; i < getRoles().length; i++) {
         if (role.equals(getRoles()[i])) {
            return true;
         }
      }

      return false;
   }

   public boolean isInitialized() {
      return initialized;
   }

   public void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }


   /**
    * I imagine this will probably move into a authz call and out of here
    *
    * @return
    */
   public String[] getRoles() {
/*      if (roles == null) {
         //TODO implement for multiple roles
         roles = new String[1];
         roles[0] = (String) profile.get("role");
      }
*/
      roles = new String[1];
      roles[0] = role;

      return roles;
   }

   public void setRoles(String[] roles) {
      this.roles = roles;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public String getPassword() {
      return getMd5Password();
   }

   public String getClearPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
      this.md5Password = DigestUtils.md5Hex(password);
   }

   public String getMd5Password() {
      return md5Password;
   }

   public void setMd5Password(String md5Password) {
      this.md5Password = md5Password;
   }

   public String getRole() {
      return role;
   }

   public List getWorksiteRoles(String worksiteId) {
      return new ArrayList();
   }

   public List getWorksiteRoles() {
      return new ArrayList();
   }

   public void setRole(String role) {
      this.role = role;
   }

   public String naturalKey() {
      return "" + displayName;
   }

   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AgentImpl)) return false;

      final AgentImpl agent = (AgentImpl) o;
      return naturalKey().equals(agent.naturalKey());
   }

   public int hashCode() {
      return naturalKey().hashCode();
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

