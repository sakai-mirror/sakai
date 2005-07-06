/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/impl/NodeMetadataServiceImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.repository.intf.NodeMetadataService;
import org.sakaiproject.metaobj.repository.intf.NodeTreeService;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.model.TreeNode;
import org.sakaiproject.metaobj.repository.RepositoryNode;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import java.util.Date;

public class NodeMetadataServiceImpl extends HibernateDaoSupport implements NodeMetadataService  {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AuthenticationManager authenticationManager;
   private NodeTreeService nodeTreeService;
   private AgentManager agentManager;

   public void delete(Id nodeId) {
      NodeMetadata node = getNodeInternal(nodeId);
      getHibernateTemplate().delete(node);
   }

   protected NodeMetadata getNodeInternal(Id nodeId) {
      try {
         NodeMetadata node = (NodeMetadata)getSession().load(NodeMetadata.class, nodeId);

         if (node != null && node.getOwner() == null) {
            // set it to admin, then admin can delete
            node.setOwner(getAgentManager().getAdminAgent());
         }
         return node;
      } catch (HibernateException e) {
         logger.warn("", e);
         return null;
      }
   }

   public NodeMetadata copyNode(NodeMetadata copy) {
      return createNode(copy.getName(), copy.getType(),
         copy.getMimeTypeObject(), copy.getSize());
   }

   public Long getTotalFileSize(Id agentId) {
      String queryString =
         "select sum(artifact_size), count(*) " +
         "from NodeMetadata " +
         "where typeId=? and owner_id=?";

      try {
         Query query = getSession().createQuery(queryString);
         query.setString(0, RepositoryNode.FILE_TYPE.getValue());
         query.setString(1, agentId.getValue());
         Object result = query.uniqueResult();
         if (result != null) {
            return Long.valueOf(result.toString());
         }
      } catch (HibernateException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return new Long(0);
   }

   public Long getTotalFileSize(Id agentId, Id excludedFile) {
      String queryString =
         "select sum(artifact_size), count(*) " +
         "from NodeMetadata " +
         "where typeId=? and owner_id=? and id != ?";

      try {
         Query query = getSession().createQuery(queryString);
         query.setString(0, RepositoryNode.FILE_TYPE.getValue());
         query.setString(1, agentId.getValue());
         query.setString(2, excludedFile.getValue());
         Object result = query.uniqueResult();
         if (result != null) {
            return Long.valueOf(result.toString());
         }
      } catch (HibernateException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return new Long(0);
   }

   public NodeMetadata createNode(String name, Type type) {
      return store(createBaseNode(name, type));
   }

   public NodeMetadata createNode(String name, Type type, MimeType mimeType, long size) {
      NodeMetadata node = createBaseNode(name, type);
      node.setMimeType(mimeType);
      node.setSize(size);
      return store(node);
   }

   protected NodeMetadata createBaseNode(String name, Type type) {
      NodeMetadata newNode = new NodeMetadata();
      newNode.setCreation(new Date());
      newNode.setOwner(getAgentId());
      newNode.setName(name);
      newNode.setType(type);

      return newNode;
   }

   public NodeMetadata getNode(Id nodeId) {
      NodeMetadata node = getNodeInternal(nodeId);
      if (node == null) {
         return null;
      }
      return new NodeMetadata(node);
   }

   public NodeMetadata store(NodeMetadata nodeMetadata) {
      if (nodeMetadata.getId() != null) {
         TreeNode node = getNodeTreeService().getTreeNode(nodeMetadata.getId());

         if (node != null) {
            getNodeTreeService().rename(node, nodeMetadata.getName());
         }
      }
      nodeMetadata.setLastModified(new Date());
      getHibernateTemplate().saveOrUpdateCopy(nodeMetadata);
      return nodeMetadata;
   }

   protected Agent getAgentId() {
      return getAuthenticationManager().getAgent();
   }

   public AuthenticationManager getAuthenticationManager() {
      return authenticationManager;
   }

   public void setAuthenticationManager(AuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public NodeTreeService getNodeTreeService() {
      return nodeTreeService;
   }

   public void setNodeTreeService(NodeTreeService nodeTreeService) {
      this.nodeTreeService = nodeTreeService;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

}
