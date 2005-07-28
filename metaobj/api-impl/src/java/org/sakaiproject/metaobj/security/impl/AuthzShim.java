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
package org.sakaiproject.metaobj.security.impl;

import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 30, 2005
 * Time: 4:57:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthzShim implements AuthorizationFacade {

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {

   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
   }

   public boolean isAuthorized(String function, Id id) {
      return true;
   }

   public boolean isAuthorized(Agent agent, String function, Id id) {
      return true;
   }

   public List getAuthorizations(Agent agent, String function, Id id) {
      return new ArrayList();
   }

   public void createAuthorization(Agent agent, String function, Id id) {
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
   }

   public void deleteAuthorizations(Id qualifier) {
   }
}
