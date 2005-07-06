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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/WckRepositoryManager.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.sakaiproject.metaobj.repository.RepositoryManager;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.mgt.intf.MimeTypeManager;
import org.sakaiproject.metaobj.repository.model.Quota;
import org.sakaiproject.metaobj.repository.model.impl.WorksiteRootNode;
import org.sakaiproject.metaobj.repository.model.impl.*;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.repository.mgt.intf.ProtectedWebdavStore;
import org.sakaiproject.metaobj.repository.mgt.intf.LockManager;
import org.sakaiproject.metaobj.repository.model.QuotaDao;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.exception.IdUnusedException;
import java.util.Iterator;
import java.util.List;

/**
 * User: rpembry
 * Date: Dec 17, 2004
 * Time: 11:41:48 AM
 */
public class WckRepositoryManager implements RepositoryManager {
   protected final static org.apache.commons.logging.Log logger =
      org.apache.commons.logging.LogFactory.getLog(WckRepositoryManager.class);

   private AuthenticationManager authManager = null;
   private AgentManager agentManager = null;
   private IdManager idManager = null;
   private Type userRootNodeType = null;
   private WorksiteManager worksiteManager;
   private AuthorizationFacade authzManager;
   private QuotaDao quotaDao;
   private WckRepositoryManagerHelper wckRepositoryManagerHelper;
   private LockManager lockManager;

   private ProtectedWebdavStore wck=null;

   private String baseDir;

   private MimeTypeManager mimeTypeManager;
   private String host;


   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.repository.repositoryManager#getRootNode(java.lang.String)
    */
   public Node getRootNode(Agent agent) {
      return getAgentRoot(agent);
   }


   protected Node getGlobalUsersRoot() {
      logger.debug("getGlobalUsersRoot()");
      Node globalRoot = getGlobalRoot();

      if (globalRoot.hasChild(USER_ROOT)) {
         if (logger.isDebugEnabled()) {
            logger.debug("found: "+USER_ROOT+")");
         }
         return globalRoot.getChild(USER_ROOT);
      }
      else {
         if (logger.isDebugEnabled()) {
            logger.debug("creating: "+USER_ROOT+")");
         }
         return globalRoot.persistent().createChildContainer(USER_ROOT);
      }

   }

   protected boolean uriMatchesTemplate(String uri, String template) {
      boolean result=false;
      if (uri.startsWith(template)) {
         int length=template.length();
         String rest=uri.substring(length);

         //remove leading slashes
         while (rest.startsWith("/")) {
            rest=rest.substring(1);
         }
         //remove trailing slashes
         while (rest.endsWith("/")) {
            rest=rest.substring(0,rest.length()-1);
         }

         if (rest.indexOf("/")==-1) {
            result=true;
         }
      }
      return result;
   }

   public boolean isUserFolder(String uri) {
      boolean result=false;
      if (logger.isDebugEnabled()) {
         logger.debug("isUserFolder("+uri+")");
      }

      String template=this.getUserPath(uri);

      result=uriMatchesTemplate(uri,template);

      if (logger.isDebugEnabled()) {
         logger.debug("isUserFolder --> "+result);
      }
      return result;
   }

   public boolean isWorksiteFolder(String uri) {
      boolean result=false;
      if (logger.isDebugEnabled()) {
         logger.debug("isWorksiteFolder("+uri+")");
      }
      String template=this.getWorksitePath(uri);

      result=uriMatchesTemplate(uri,template);

      if (logger.isDebugEnabled()) {
         logger.debug("isWorksiteFolder --> "+result);
      }
      return result;

   }


   /**
    * Returns the giver Agent's root node (i.e. home directory)
    *
    * @param agent
    * @return Agent's Node
    */
   public Node getAgentRoot(Agent agent) {
      logger.debug("getAgentRoot()");
      String agentNode;
      if (logger.isDebugEnabled()) {
         if (agent==null) {
            logger.debug("getRootNode(null)");
         } else {
            logger.debug("getRootNode(" +  agent.getId()+")");
         }
      }

      if (agent==null) {
         logger.warn("null agent");
         agentNode="NULL";
       } else {
          agentNode=agent.getId().getValue();
       }

      if (agentNode.length()==0) {
         logger.warn("empty agent id");
         agentNode="EMPTY";
      }

      logger.debug("getRootNode(" +  agentNode+")");

      Node globalRoot = getGlobalUsersRoot();
      if (logger.isDebugEnabled()) {
         logger.debug("globalRoot: "+globalRoot.getPath());
      }
      Node userNode = globalRoot.getChild(agentNode);

      if (userNode == null || !userNode.exists()) {
         logger.debug("creating root node: "+agentNode);
         userNode=globalRoot.persistent().createChildContainer(agentNode);
      }
      else {
         logger.debug("found root node: "+agentNode);
      }
      return userNode;
      
   }

   protected String extractId(String path, String basePath) {
      if (path.startsWith(basePath) && !path.equals(basePath)) {
         int length = basePath.length();

         String worksiteId = path.substring(length + 1);
         int pos = worksiteId.indexOf('/');

         if (pos != -1) {
            worksiteId = worksiteId.substring(0, pos);
         }
         return worksiteId;
      }
      else {
         return null;
      }
   }

   // Some implementers might prefer e.g. /files/worksites/Ch/Chessclub
   protected String getWorksitePath(String path) {
      String result=combine(baseDir, WORKSITE_ROOT);
      return result;
   }

   // Some implementers might prefer e.g. /files/users/rp/rpembry
   protected String getUserPath(String path) {
      String result=combine(baseDir, USER_ROOT);
      return result;
   }


   public String getWorksiteId(String path) {
      return extractId(path, getWorksitePath(path));
   }

   public List getMimeTypes() {
      return getMimeTypeManager().getMimeTypes();
   }

   public Node getAllAgentNodes(Agent agent) {
      return getAgentRootNodeWrapper(agent);
   } 
 
   public Node getWorksiteNode(Id siteId) {
      return getWorksiteNode(siteId, true);
   }

   protected Node getAgentRootNodeWrapper(Agent agent) {
      UserRootNodeCollection rootNodeWrapper = new UserRootNodeCollection(agent,
         new UserRootNodeCollectionId(agent, getUserRootNodeType()));

      rootNodeWrapper.addChild(getAgentRoot(agent));

      // now go through all worksite nodes, etc
      List userSites = getWorksiteManager().getUserSites();

      for (Iterator i=userSites.iterator();i.hasNext();) {
         Site current = (Site)i.next();

         List repositories = getWorksiteManager().getSiteTools("osp.repository",
            current);

         if (repositories.size() > 0) {
            rootNodeWrapper.addChild(getWorksiteNode(current, true));
         }
      }

      return rootNodeWrapper;
   }

   public Node getWorksiteNode(Id siteId, boolean create) {
      if (siteId == null) {
         siteId = getWorksiteManager().getCurrentWorksiteId();
         if (siteId == null) {
            return null;
         }
      }

      Site site = null;
      try {
         if (SiteService.isUserSite(siteId.getValue())) {
            Agent agent = getAgentManager().getAgent(
               SiteService.getSiteUserId(siteId.getValue()));
            return getAgentRoot(agent);
         }
         site = SiteService.getSite(siteId.getValue());
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return getWorksiteNode(site, create);
   }


   public Node getGlobalRoot() {
      //return new WckNode( WckNode.combine(getBaseDir(), ""));
      logger.debug("getGlobalRoot() is creating new WckNode");

      String name=combine(baseDir,""); //insures trailling slash
      return new WckNode( baseDir,true);
   }

   protected String combine(String baseDir, String s) {
      return getWckRepositoryManagerHelper().combine(baseDir, s);
   }


   public Node getNode(Id id) {
      if (logger.isDebugEnabled()) {
         logger.debug("getNode("+id+")");
      }

      if (id==null) {
         logger.warn("attempt to request NULL node");
         return null;
      }
      if (id instanceof UserRootNodeCollectionId) {
         UserRootNodeCollectionId userId = (UserRootNodeCollectionId)id;
         return getAllAgentNodes(userId.getAgent());
      }
      if (id.getValue().length()==0) {
         logger.warn("setting empty Id to literal EMPTY");
         id=idManager.getId("EMPTY");
      }
      logger.debug("getting node by id " + id.getValue());

      ProtectedWebdavStore store = getWck();

      try {
         String uri = store.getUri(id);
         logger.debug("getNode() is creating new WckNode");
         return new WckNode(uri);
      } catch (Exception e) {
         logger.error("Unable to retrieve requested node due to Exception: " + id, e);
         return null;
      } finally {
         store.close();
      }

   }

   public Quota getQuota(Agent agent) {
      Id agentId = agent.getId();
      return quotaDao.getOrCreateQuota(agentId);
   }

   public String getBaseDir() {
      return baseDir;
   }

   public void setBaseDir(String baseDir) {
      this.baseDir = baseDir;
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   protected Node getWorksiteNode(Site worksite, boolean create) {
      logger.debug("getWorksiteNode()");
      Node parent = getWorksiteRoot();
      Node returned = null;
 
      if (!create || parent.hasChild(worksite.getId())) {
         returned = parent.getChild(worksite.getId());
      }
      else if (create) {
         returned = parent.persistent().createChildContainer(worksite.getId());
      }
      else {
         return null;
      }

      if (returned != null) {
         return new WorksiteRootNode(returned.getId(), worksite);
      }
      else  {
         return null;
      }
   }

   public Node getWorksiteRoot() {
      logger.debug("getWorksiteRoot()");
      Node globalRoot = getGlobalRoot();
      if (globalRoot.hasChild(WORKSITE_ROOT)) {
         return globalRoot.getChild(WORKSITE_ROOT);
      }
      else {
         return globalRoot.persistent().createChildContainer(WORKSITE_ROOT);
      }
   }

   public boolean canMaintain(Node node) {
      if (PortalService.getCurrentSiteId() != null &&
         PortalService.getCurrentSiteId().equals(node.getNodeSiteId())) {
         return getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(node.getNodeSiteId()));
      }
      return false;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

  public Type getUserRootNodeType() {
      return userRootNodeType;
   }

   public void setUserRootNodeType(Type userRootNodeType) {
      this.userRootNodeType = userRootNodeType;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public AuthorizationFacade getAuthorizationFacade() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }


   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public QuotaDao getQuotaDao() {
      return quotaDao;
   }

   public void setQuotaDao(QuotaDao quotaDao) {
      this.quotaDao = quotaDao;
   }

   public ProtectedWebdavStore getWck() {
      return wck;
   }

   public void setWck(ProtectedWebdavStore wck) {
      this.wck = wck;
   }

   public MimeTypeManager getMimeTypeManager() {
      return mimeTypeManager;
   }

   public void setMimeTypeManager(MimeTypeManager mimeTypeManager) {
      this.mimeTypeManager = mimeTypeManager;
   }

   public WckRepositoryManagerHelper getWckRepositoryManagerHelper() {
      return wckRepositoryManagerHelper;
   }

   public void setWckRepositoryManagerHelper(WckRepositoryManagerHelper wckRepositoryManagerHelper) {
      this.wckRepositoryManagerHelper = wckRepositoryManagerHelper;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }
}
