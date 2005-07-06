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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/RepositoryAuthorizerImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt;

import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.RepositoryManager;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.RepositoryFunctions;
import org.sakaiproject.metaobj.repository.intf.NodeMetadataService;
import org.sakaiproject.metaobj.repository.mgt.intf.LockManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.Authorization;
import org.sakaiproject.metaobj.security.app.ApplicationAuthorizer;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 21, 2004
 * Time: 1:07:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryAuthorizerImpl implements ApplicationAuthorizer {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private HomeFactory homeFactory;
   private RepositoryManager repositoryManager;
   private IdManager idManager;
   private LockManager lockManager;
   private List functions;
   private NodeMetadataService nodeMetadataService;

   /**
    * This method will ask the application specific functional authorizer to determine authorization.
    *
    * @param facade   this can be used to do explicit auths if necessary
    * @param agent
    * @param function
    * @param id
    * @return null if the authorizer has no opinion, true if authorized, false if explicitly not authorized.
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent, String function, Id id) {
      if (id==null) {
         logger.error("unable to authorize null id");
         return null;
      }

   //TODO: this is a very very very expensive call...
      NodeMetadata resource=null;

      try {
         resource = getNodeMetadataService().getNode(id);
      } catch (HibernateObjectRetrievalFailureException e) {
         // hope that a load failure means it doesn't belong to Repository
         return null;
      } catch (Exception e) {
         logger.error("RepositoryAuthorizor is ignoring: "+e.getMessage());
         return null;
      }

      if (resource == null) {
         // must not be a file/folder so who cares? not me!
         return null;
      }

      // deny writes to artifacts whose home is StructuredArtifactDefinition and set to system only
      // this allows admins to make changes to the artifacts while in system only mode
      if (function.equals(RepositoryFunctions.WRITE)){
         ReadableObjectHome home = getHomeFactory().getHome(resource.getType().getId().getValue());
         if (home != null && home.isSystemOnly() && !agent.isInRole(Agent.ROLE_ADMIN)){
            return new Boolean(false);
         }
      }

      Node node = getRepositoryManager().getNode(id);

      if (lockedExplicitlyOrImplictly(function,node)) {
         return new Boolean(false);
      }


      if (node.getParent() != null &&
         node.getParent().getParent() != null &&
         node.getParent().getParent().isRoot() &&
         (function.equals(RepositoryFunctions.DELETE) ||
         function.equals(RepositoryFunctions.MOVE) ||
         function.equals(RepositoryFunctions.EDIT))) {
         // can't do that to system folder
         return new Boolean(false);
      }

      if (resource.getOwner().getId().equals(agent.getId())) {
         // owner can do anything
         return new Boolean(true);
      }

      String worksiteId = node.getNodeSiteId();

      if (worksiteId == null) {
         return null;
      }

      List roles = agent.getWorksiteRoles(worksiteId);

      // getting worksite node to test it
      
      for (Iterator i=roles.iterator();i.hasNext();) {
         // test this role with each directory
         Authorization authz = getAuthorization(node,
            (Agent)i.next(), function, facade);

         if (authz != null) {
            return new Boolean(true);
         }
      }

      return null;
   }

   protected boolean lockedExplicitlyOrImplictly(String function, Node node) {
      if (logger.isDebugEnabled()) {
         logger.debug("implicit_lock: lockedExplicitlyOrImplictly("+function+","+node+")");
      }

      if (RepositoryFunctions.READ.equals(function)) {
         logger.debug("implicit_lock: ignoring READ");
         return false;
      }
      if (RepositoryFunctions.MOVE.equals(function)) {
         logger.debug("implicit_lock: ignoring MOVE");
         return false;
      }
      // Check Explicit Locks

      if (lockManager.isLocked(node)) {
         logger.debug("implicit_lock: explicit lock found");
         return true;
      }

      // Check Implicit locks for DELETE
      if (!RepositoryFunctions.DELETE.equals(function)) {
         logger.debug("implicit_lock: ignoring non-DELETE");
         return false;
      }

      if (!node.isCollection()) {
         logger.debug("implicit_lock: ignoring non collection");
         return false;
      }

      if (lockManager.containsLockedNode(node)) {
         logger.debug("implicit_lock: collection is locked, as it contains a locked node");
         return true;
      }

      if (logger.isDebugEnabled()) {
         logger.debug("implicit_lock: not implicitly locked("+node+")");
      }
      return false;

   }



   protected Authorization getAuthorization(Node node, Agent agent,
                                            String function, AuthorizationFacade facade) {
      while (!node.isRoot()) {
         List authzs = facade.getAuthorizations(agent, function, node.getId());

         if (authzs.size() > 0) {
            return (Authorization)authzs.get(0);
         }

         node = node.getParent();
      }

      return null;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public RepositoryManager getRepositoryManager() {
      return repositoryManager;
   }

   public void setRepositoryManager(RepositoryManager repositoryManager) {
      this.repositoryManager = repositoryManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public NodeMetadataService getNodeMetadataService() {
      return nodeMetadataService;
   }

   public void setNodeMetadataService(NodeMetadataService nodeMetadataService) {
      this.nodeMetadataService = nodeMetadataService;
   }

}
