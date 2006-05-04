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
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

public interface AgentManager {
   /**
    * @param id
    * @return
    */
   public Agent getAgent(Id id);

   public Agent getAgent(String username);

   public Agent getWorksiteRole(String roleName);

   public Agent getWorksiteRole(String roleName, String siteId);

   public Agent getTempWorksiteRole(String roleName, String siteId);

   public Agent getAnonymousAgent();

   public Agent getAdminAgent();

   /**
    * if type is null return all records
    *
    * @param type   added typed list
    * @param object
    * @return
    */
   public List findByProperty(String type, Object object);


   /**
    * @param agent
    * @return
    */
   public Agent createAgent(Agent agent);

   /**
    * @param agent
    */
   public void deleteAgent(Agent agent);

   public void updateAgent(Agent agent);

   /**
    * @param siteId
    * @return list of agents that are participants in the given siteId
    */
   public List getWorksiteAgents(String siteId);

   /**
    * @param siteId
    * @return list of roles for the given siteId.  The list is a collection of type Agent.
    */
   public List getWorksiteRoles(String siteId);

}
