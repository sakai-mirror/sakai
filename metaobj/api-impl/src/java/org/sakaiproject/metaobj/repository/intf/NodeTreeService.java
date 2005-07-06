/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/intf/NodeTreeService.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.intf;

import org.sakaiproject.metaobj.repository.model.TreeNode;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Collection;

public interface NodeTreeService {

   public TreeNode getTreeNode(Id id);

   public TreeNode getTreeNode(String uri);

   public TreeNode addChild(TreeNode parent, TreeNode newChild);

   public TreeNode addChild(TreeNode parent, Id nodeId, String nodeName);

   public TreeNode rename(TreeNode oldNode, String newName);

   public TreeNode move(TreeNode src, TreeNode newParent);

   // returns a list of deleted Id's
   public Collection delete(TreeNode oldNode);

   public Collection getChildren(TreeNode treeNode);

   Collection getDeepChildren(TreeNode node);

   TreeNode getChild(TreeNode treeNode, String name);
}
