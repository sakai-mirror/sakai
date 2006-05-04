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
package org.sakaiproject.metaobj.security.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

public class PermissionsEdit {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String name = null;
   private Id qualifier = null;
   private List permissions = null;
   private String siteId;

   public PermissionsEdit() {
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public PermissionsEdit(String name, Id qualifier, String siteId, List permissions) {
      this.name = name;
      this.permissions = permissions;
      this.qualifier = qualifier;
      this.siteId = siteId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List getPermissions() {
      return permissions;
   }

   public void setPermissions(List permissions) {
      this.permissions = permissions;
   }

   public Id getQualifier() {
      return qualifier;
   }

   public void setQualifier(Id qualifier) {
      this.qualifier = qualifier;
   }

   public Object clone() {
      return new PermissionsEdit(getName(), getQualifier(), getSiteId(), getPermissions());
   }
}