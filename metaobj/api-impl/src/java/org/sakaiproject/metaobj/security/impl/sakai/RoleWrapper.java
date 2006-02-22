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
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleWrapper implements OspRole {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id = null;
   private Role sakaiRole = null;
   private AuthzGroup sakaiRealm = null;

   public RoleWrapper(Id id, Role sakaiRole, AuthzGroup sakaiRealm) {
      this.id = id;
      this.sakaiRealm = sakaiRealm;
      this.sakaiRole = sakaiRole;
   }

   public Id getId() {
      return id;
   }

   public Artifact getProfile() {
      return null;
   }

   public void setProfile(Artifact profile) {

   }

   public Object getProperty(String key) {
      return null;
   }

   public String getDisplayName() {
      return getSakaiRole().getId();
   }

   public boolean isInRole(String role) {
      return role.equals(id.getValue());
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return id.getValue();
   }

   public List getWorksiteRoles(String worksiteId) {
      return new ArrayList();
   }

   public List getWorksiteRoles() {
      return new ArrayList();
   }

   public boolean isRole() {
      return true;
   }

   public AuthzGroup getSakaiRealm() {
      return sakaiRealm;
   }

   public Role getSakaiRole() {
      return sakaiRole;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof RoleWrapper)) {
         return false;
      }

      final RoleWrapper roleWrapper = (RoleWrapper) o;

      if (id != null ? !id.equals(roleWrapper.id) : roleWrapper.id != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      return (id != null ? id.hashCode() : 0);
   }

   /**
    * Returns the name of this principal.
    *
    * @return the name of this principal.
    */
   public String getName() {
      return getDisplayName();
   }

   /**
    * gets the name of the role idependant of the site it belongs to
    *
    * @return
    */
   public String getRoleName() {
      return getSakaiRole().getId();
   }
}
