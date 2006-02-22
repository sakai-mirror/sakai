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
package org.sakaiproject.metaobj.shared.model.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;

import java.util.ArrayList;
import java.util.List;

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

   public boolean isRole() {
      return false;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public String naturalKey() {
      return "" + displayName;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof AgentImpl)) {
         return false;
      }

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

