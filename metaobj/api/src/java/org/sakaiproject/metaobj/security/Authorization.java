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

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 4:58:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Authorization {
   private Id id;
   private Agent agent;
   private String function;
   private Id qualifier;

   public Authorization() {
   }

   public Authorization(Agent agent, String function, Id qualifier) {
      this.agent = agent;
      this.function = function;
      this.qualifier = qualifier;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public Agent getAgent() {
      return agent;
   }

   public String getFunction() {
      return function;
   }

   public Id getQualifier() {
      return qualifier;
   }

   public void setAgent(Agent agent) {
      this.agent = agent;
   }

   public void setFunction(String function) {
      this.function = function;
   }

   public void setQualifier(Id qualifier) {
      this.qualifier = qualifier;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Authorization)) {
         return false;
      }

      final Authorization authorization = (Authorization) o;

      if (!agent.equals(authorization.agent)) {
         return false;
      }
      if (!function.equals(authorization.function)) {
         return false;
      }
      if (!qualifier.equals(authorization.qualifier)) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result;
      result = agent.hashCode();
      result = 29 * result + function.hashCode();
      result = 29 * result + qualifier.hashCode();
      return result;
   }

}
