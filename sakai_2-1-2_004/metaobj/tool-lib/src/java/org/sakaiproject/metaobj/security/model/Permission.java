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
import org.sakaiproject.metaobj.shared.model.Agent;

public class Permission {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Agent agent;
   private String function;
   private boolean readOnly = false;


   public Permission() {
   }

   public Permission(Agent agent, String function) {
      this.agent = agent;
      this.function = function;
   }

   public Permission(Agent agent, String function, boolean readOnly) {
      this.agent = agent;
      this.function = function;
      this.readOnly = readOnly;
   }

   public String getFunction() {
      return function;
   }

   public void setFunction(String function) {
      this.function = function;
   }

   public Agent getAgent() {
      return agent;
   }

   public void setAgent(Agent agent) {
      this.agent = agent;
   }

   public boolean isReadOnly() {
      return readOnly;
   }

   public void setReadOnly(boolean readOnly) {
      this.readOnly = readOnly;
   }

   public String toString() {
      return getAgent().getDisplayName() + "~" + getFunction();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Permission)) {
         return false;
      }

      final Permission permission = (Permission) o;

      if (agent != null ? !agent.equals(permission.agent) : permission.agent != null) {
         return false;
      }
      if (function != null ? !function.equals(permission.function) : permission.function != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result;
      result = (agent != null ? agent.hashCode() : 0);
      result = 29 * result + (function != null ? function.hashCode() : 0);
      return result;
   }
}
