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
package org.sakaiproject.metaobj.security;

import org.sakaiproject.metaobj.shared.mgt.IdManagerImpl;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 26, 2004
 * Time: 10:36:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnonymousAgent implements Agent {
   public static Id ANONYMOUS_AGENT_ID = new IdManagerImpl().getId("anonymous");

   public Id getId() {
      return ANONYMOUS_AGENT_ID;
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
      return "anonymous";
   }

   public boolean isInRole(String role) {
      return role.equals(Agent.ROLE_ANONYMOUS);
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return Agent.ROLE_ANONYMOUS;
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

   /**
    * Returns the name of this principal.
    *
    * @return the name of this principal.
    */
   public String getName() {
      return getDisplayName();
   }
}
