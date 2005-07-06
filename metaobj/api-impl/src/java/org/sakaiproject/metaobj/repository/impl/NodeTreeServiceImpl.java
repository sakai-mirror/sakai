/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/impl/NodeTreeServiceImpl.java,v 1.2 2005/07/05 20:15:18 jellis Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.impl;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.sakaiproject.metaobj.repository.intf.NodeTreeService;
import org.sakaiproject.metaobj.repository.model.TreeNode;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.PersistenceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class NodeTreeServiceImpl extends HibernateDaoSupport implements NodeTreeService {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String rootNodeName;
   private IdManager idManager;

   public void init() {
   }

   public TreeNode addChild(TreeNode parent, TreeNode newChild) {
      return addChild(parent, newChild.getId(), newChild.getName());
   }

   public TreeNode addChild(TreeNode parent, Id nodeId, String nodeName) {
      parent = ensureLoaded(parent);

      checkDupe(parent, null, nodeName);

      TreeNode newTreeNode = new TreeNode();
      newTreeNode.setParent(parent instanceof RootTreeNode?null:parent);
      newTreeNode.setUri(combineUri(parent.getUri(), nodeName));
      newTreeNode.setId(nodeId);
      newTreeNode.setName(nodeName);

      return (TreeNode)getHibernateTemplate().saveOrUpdateCopy(newTreeNode);
   }

   public Collection delete(TreeNode oldNode) {
      oldNode = ensureLoaded(oldNode);
      List deleted = new ArrayList();
      deleteInternal(oldNode, deleted);
      return deleted;
   }

   protected void deleteInternal(TreeNode oldNode, List deleted) {
      Collection children = oldNode.getChildren();
      for (Iterator i=children.iterator();i.hasNext();) {
         TreeNode child = (TreeNode)i.next();
         deleteInternal(child, deleted);
      }
      deleted.add(oldNode.getId());
      getHibernateTemplate().delete(oldNode);
   }

   public Collection getChildren(TreeNode treeNode) {
      if (treeNode instanceof RootTreeNode) {
         return getHibernateTemplate().find("from TreeNode where parent is null");
      }
      else {
         treeNode = ensureLoaded(treeNode);
         return getHibernateTemplate().find("from TreeNode where parent=?",
            treeNode.getRowId().getValue());
      }
   }

   protected TreeNode getRootNode() {
      RootTreeNode rootNode = new RootTreeNode();
      return rootNode;
   }

   public TreeNode getTreeNode(Id id) {
      Collection nodes = getHibernateTemplate().find(
         "from TreeNode where id=?", id.getValue());

      if (nodes.size() > 0) {
         TreeNode node = (TreeNode) nodes.iterator().next();
         // todo figure this out
         // getHibernateTemplate().evict(node);
         return node;
      }

      return null;
   }

   public TreeNode getTreeNode(String uri) {
      if (uri == null || uri.equals("") || uri.equals("/")) {
         return getRootNode();
      }

      Collection nodes = getHibernateTemplate().find(
         "from TreeNode where uri=?", uri);

      if (nodes.size() > 0) {
         TreeNode node = (TreeNode) nodes.iterator().next();
         // todo figure this out
         // getHibernateTemplate().evict(node);
         return node;
      }

      if (uri.equals("/" + getRootNode())) {
         // create root node
         addChild(getRootNode(), getIdManager().createId(), rootNodeName);
      }

      return null;
   }

   public TreeNode move(TreeNode src, TreeNode newParent) {
      src = ensureLoaded(src);
      newParent = ensureLoaded(newParent);
      Collection children = getDeepChildren(src);
      TreeNode oldParent = src.getParent();

      if (oldParent == null) {
         oldParent = getRootNode();
      }

      for (Iterator i=children.iterator();i.hasNext();) {
         TreeNode node = (TreeNode)i.next();
         node.setUri(replaceUri(node.getUri(),
            oldParent.getUri(), newParent.getUri()));
         getHibernateTemplate().saveOrUpdate(node);
      }

      TreeNode topNode = getTreeNode(src.getId());
      topNode.setParent(newParent);
      topNode.setUri(replaceUri(topNode.getUri(),
         oldParent.getUri(), newParent.getUri()));
      getHibernateTemplate().saveOrUpdate(topNode);
      return topNode;
   }

   public TreeNode rename(TreeNode oldNode, String newName) {
      oldNode = ensureLoaded(oldNode);
      TreeNode parent = oldNode.getParent();
      if (parent == null) {
         parent = getRootNode();
      }

      checkDupe(parent, oldNode, newName);

      String oldNodeUri = oldNode.getUri();
      String newNodeUri = oldNodeUri.substring(0,
         (oldNodeUri.length() - oldNode.getName().length()));
      newNodeUri = combineUri(newNodeUri, newName);

      Collection children = getDeepChildren(oldNode);
      for (Iterator i=children.iterator();i.hasNext();) {
         TreeNode node = (TreeNode)i.next();
         node.setUri(replaceUri(node.getUri(), oldNodeUri, newNodeUri));
         getHibernateTemplate().saveOrUpdate(node);
      }

      oldNode.setUri(newNodeUri);
      oldNode.setName(newName);
      getHibernateTemplate().saveOrUpdate(oldNode);
      return getTreeNode(oldNode.getId());
   }

   protected void checkDupe(TreeNode parent, TreeNode oldNode, String newName) {
      if (oldNode != null && oldNode.getName().equals(newName)) {
         return;
      }

      if (parent.getChild(newName) != null) {
         throw new PersistenceException("File exists {0}",
            new Object[]{newName}, "displayName");
      }
   }

   protected TreeNode ensureLoaded(TreeNode node) {

      if (node instanceof RootTreeNode) {
         return node;
      }
      else if (node.getRowId() != null) {
         return node;
      }
      else if (node.getId() != null) {
         return getTreeNode(node.getId());
      }
      else if (node.getUri() != null) {
         return getTreeNode(node.getUri());
      }
      throw new IllegalArgumentException("Must specify node id or uri to perform operation");
   }

   protected String replaceUri(String oldValue, String oldParent, String newParent) {
      if (oldValue.equals(oldParent)) {
         return newParent;
      }
      String oldEnding = oldValue.substring(oldParent.length());
      return newParent + oldEnding;
   }

   public Collection getDeepChildren(TreeNode node) {
      String uri = node.getUri();

      Criteria crit = getSession().createCriteria(TreeNode.class);
      crit.add(Expression.like("uri", uri + "/", MatchMode.START));
      try {
         return crit.list();
      } catch (HibernateException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      /*
      String startString = uri + '/';
      String endString = uri + ('/' + 1);
      Expression.like()
      return getHibernateTemplate().find(
         "from TreeNode w where uri > ? and uri < ?",
         new Object[]{startString, endString});
      */
   }

   public TreeNode getChild(TreeNode treeNode, String name) {
      String childUri = combineUri(treeNode.getUri(), name);
      return getTreeNode(childUri);
   }

   protected String combineUri(String str1, String str2) {
      if (str2.startsWith("/")) str2 = str2.substring(1);
      if (str1.endsWith("/")) return str1 + str2;
      return str1 + "/" + str2;
   }

   public String getRootNodeName() {
      return rootNodeName;
   }

   public void setRootNodeName(String rootNodeName) {
      this.rootNodeName = rootNodeName;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

}
