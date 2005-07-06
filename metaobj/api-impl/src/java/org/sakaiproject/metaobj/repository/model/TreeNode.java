/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/TreeNode.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.repository.intf.NodeTreeService;
import org.sakaiproject.metaobj.utils.BeanFactory;

import java.util.Collection;

public class TreeNode {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id rowId;
   private Id nodeId;
   private String uri;
   private String name;
   private TreeNode parent;
   private NodeTreeService parentService = null;

   public Id getRowId() {
      return rowId;
   }

   public void setRowId(Id rowId) {
      this.rowId = rowId;
   }

   public Collection getChildren() {
      return getParentService().getChildren(this);
   }

   protected NodeTreeService getParentService() {
      if (parentService == null) {
         parentService = (NodeTreeService) BeanFactory.getInstance().getBean(
            NodeTreeService.class.getName(), NodeTreeService.class);
      }
      return parentService;
   }

   public Id getId() {
      return nodeId;
   }

   public void setId(Id id) {
      this.nodeId = id;
   }

   public Id getNodeId() {
      return nodeId;
   }

   public void setNodeId(Id nodeId) {
      this.nodeId = nodeId;
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public TreeNode getParent() {
      return parent;
   }

   public void setParent(TreeNode parent) {
      this.parent = parent;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }


   public TreeNode getChild(String name) {
      return getParentService().getChild(this, name);
   }
}
