/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/theospi/metaobj/shared/mgt/AgentManager.java,v 1.3 2005/06/30 02:44:42 jellis Exp $
 * $Revision: 1.3 $
 * $Date: 2005/06/30 02:44:42 $
 */
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

/* =====================================================================
  *
  * The contents of this file are subject to the OSPI License Version 1.0 (the
  * License).  You may not copy or use this file, in either source code or
  * executable form, except in compliance with the License.  You may obtain a
  * copy of the License at http://www.theospi.org/.
  *
  * Software distributed under the License is distributed on an AS IS basis,
  * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
  * for the specific language governing rights and limitations under the
  * License.
  *
  * Copyrights:
  *
  * Portions Copyright (c) 2003 the r-smart group, inc.
  *
  * Acknowledgements
  *
  * Special thanks to the OSPI Users and Contributors for their suggestions and
  * support.
  */

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
    *
    * @param siteId
    * @return list of roles for the given siteId.  The list is a collection of type Agent.
    */
   public List getWorksiteRoles(String siteId);

}
