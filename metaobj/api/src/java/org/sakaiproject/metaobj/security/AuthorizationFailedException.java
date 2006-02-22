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
import org.sakaiproject.metaobj.shared.model.OspException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 22, 2004
 * Time: 9:27:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationFailedException extends OspException {

   private Agent agent = null;
   private String function = null;
   private Id qualifier = null;

   /**
    *
    */
   public AuthorizationFailedException(String function, Id qualifier) {
      this.function = function;
      this.qualifier = qualifier;
   }

   /**
    *
    */
   public AuthorizationFailedException(Agent agent, String function, Id qualifier) {
      this.agent = agent;
      this.function = function;
      this.qualifier = qualifier;
   }

   /**
    *
    */
   public AuthorizationFailedException() {
      super();
   }

   /**
    * @param cause
    */
   public AuthorizationFailedException(Throwable cause) {
      super(cause);
   }

   /**
    * @param message
    */
   public AuthorizationFailedException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public AuthorizationFailedException(String message, Throwable cause) {
      super(message, cause);
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
}
