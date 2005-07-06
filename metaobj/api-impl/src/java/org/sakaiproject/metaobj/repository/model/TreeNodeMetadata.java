/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/TreeNodeMetadata.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TreeNodeMetadata {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private TreeNode treeNode = null;
   private NodeMetadata nodeMetadata = null;

   public TreeNodeMetadata(NodeMetadata nodeMetadata, TreeNode treeNode) {
      this.nodeMetadata = nodeMetadata;
      this.treeNode = treeNode;
   }

   public NodeMetadata getNodeMetadata() {
      return nodeMetadata;
   }

   public void setNodeMetadata(NodeMetadata nodeMetadata) {
      this.nodeMetadata = nodeMetadata;
   }

   public TreeNode getTreeNode() {
      return treeNode;
   }

   public void setTreeNode(TreeNode treeNode) {
      this.treeNode = treeNode;
   }
}
