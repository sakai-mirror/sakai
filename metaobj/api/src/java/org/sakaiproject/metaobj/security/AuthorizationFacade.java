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

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 11:28:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AuthorizationFacade {

   public void checkPermission(String function, Id id) throws AuthorizationFailedException;

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException;

   /**
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(String function, Id id);

   /**
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(Agent agent, String function, Id id);

   /**
    * at least one param must be non-null
    *
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public List getAuthorizations(Agent agent, String function, Id id);

   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id);

   public void deleteAuthorization(Agent agent, String function, Id id);

   public void deleteAuthorizations(Id qualifier);

   public void pushAuthzGroups(Collection authzGroups);

   void pushAuthzGroups(String siteId);
}
