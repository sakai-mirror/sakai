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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/control/NodeWrapper.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.control;

import org.sakaiproject.metaobj.repository.RepositoryManager;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.model.FilePickerState;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.utils.BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 3, 2004
 * Time: 1:34:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeWrapper implements Cloneable {

   public static final String CURRENT_USER_ROOT = "org.theospi.repository.currentRoot";
   private static final String INDENT_CHAR = "&nbsp;&nbsp;&nbsp;";

   protected final Log logger = LogFactory.getLog(getClass());

   private RepositoryManager manager = null;
   private Id nodeId = null;

   private Node cachedNode = null;

   private boolean expanded = false;
   private Boolean selected = null;
   private List children = new ArrayList();
   private int level = 0;
   private Node selectedNode;
   private Node rootNode = null;
   private FilePickerState filePickerState = null;

   public NodeWrapper(Node rootNode, RepositoryManager manager, Node node) {
      setManager(manager);
      setNode(node);
      setRootNode(rootNode);
   }

   public NodeWrapper(Node rootNode, RepositoryManager manager,
                      Id nodeId) {
      setManager(manager);
      setNodeId(nodeId);
      setRootNode(rootNode);
   }

   public NodeWrapper(Node rootNode, RepositoryManager manager) {
      this.manager = manager;
      setRootNode(rootNode);
   }

   public RepositoryManager getManager() {
      return manager;
   }

   public void setManager(RepositoryManager manager) {
      this.manager = manager;
   }

   public Node getNode() {
      if (cachedNode == null) {
         cachedNode = manager.getNode(nodeId);
      }

      return cachedNode;
   }

   public void resetCache() {
      cachedNode = null;

      for (Iterator i = children.iterator(); i.hasNext();) {
         NodeWrapper currentNode = (NodeWrapper) i.next();
         currentNode.resetCache();
      }
   }

   public void setNode(Node node) {
      cachedNode = node;
      nodeId = node.getId();
   }

   public FilePickerState getFilePickerState() {
      return filePickerState;
   }

   public void setFilePickerState(FilePickerState filePickerState) {
      this.filePickerState = filePickerState;
   }

   public Id getNodeId() {
      return nodeId;
   }

   public void setNodeId(Id nodeId) {
      this.nodeId = nodeId;
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public boolean isSelected() {
      if (selected == null) {
         return false;
      }

      return selected.booleanValue();
   }

   public Boolean getSelected() {
      return selected;
   }

   public void setSelected(Boolean selected) {
      if (selected != null) {
         setSelected(selected.booleanValue());
      }
   }

   public void setSelected(boolean selected) {
      this.selected = new Boolean(selected);
   }

   public List getChildren() {

      if (!getNode().isCollection()) {
         return new ArrayList();
      }

      List newList = new ArrayList();

      Collection realNodeList = getNode().getChildren();

      for (Iterator i = realNodeList.iterator(); i.hasNext();) {
         Node currentNode = (Node) i.next();

         int index = children.indexOf(new NodeWrapper(getRootNode(), getManager(),
            currentNode.getId()));

         if (index == -1) {
            NodeWrapper newWrapper = new NodeWrapper(getRootNode(), getManager(), currentNode);
            newList.add(newWrapper);
            newWrapper.setFilePickerState(getFilePickerState());
         } else {
            NodeWrapper wrapper = (NodeWrapper)children.get(index);
            wrapper.setNode(currentNode);
            wrapper.setFilePickerState(getFilePickerState());
            newList.add(wrapper);
         }
      }

      children = newList;
      return children;
   }

   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof NodeWrapper)) return false;

      final NodeWrapper nodeWrapper = (NodeWrapper) o;

      if (!nodeId.equals(nodeWrapper.nodeId)) return false;

      return true;
   }

   public int getLevel() {
      return level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public String getIndent() {
      String indent = new String();

      for (int i = 0; i < level; i++) {
         indent += INDENT_CHAR;
      }

      return indent;
   }

   public boolean isMoveTarget() {
      if (selectedNode != null) {
         if (!getNode().isCollection()) {
            return false;
         }

         if (cachedNode.getPath().equals(selectedNode.getPath())) {
            return false;
         }

         return !(this.cachedNode.getPath().startsWith(selectedNode.getPath() + "/"));
      }

      return false;
   }

   public void setSelectedNode(Node selectedNode) {
      this.selectedNode = selectedNode;
   }

   public List getParents() {
      List parents = new ArrayList();

      Node parent = getManager().getNode(getNode().getParent().getId()); // reload node to get correct display name

      while (!parent.getParent().isRoot()) {
         parents.add(0, parent);
         parent = getManager().getNode(parent.getParent().getId()); // reload node to get correct display name
      }

      return parents;
   }


   public boolean isNotEmpty() {
      try {
         if (!getNode().isCollection()) {
            return false;
         }
         return getNode().hasChildren();
      } catch (RuntimeException exp) {
         logger.error("", exp);
         throw exp;
      }
   }

   public Node getRootNode() {
      return rootNode;
   }

   public void setRootNode(Node rootNode) {
      this.rootNode = rootNode;
   }

   public List getLightChildren() {
      return children;
   }

   public boolean isPermissionsEditable() {
      return getManager().canMaintain(getNode());
   }

   protected AuthorizationFacade getAuthzManager() {
      return (AuthorizationFacade)BeanFactory.getInstance().getBean(
         "authzManager", AuthorizationFacade.class);
   }

   public boolean isFilePickerSelected() {
      if (getFilePickerState() == null) {
         return false;
      }
      return getNodeId().equals(getFilePickerState().getSelected());
   }

   public boolean isSelectable() {
      if (getFilePickerState() == null) {
         return false;
      }
      else {
         return getFilePickerState().allowSelectNode(getNode());
      }
   }

   public boolean isVisible() {
      if (getFilePickerState() == null) {
         return true;
      }
      else {
         return getFilePickerState().includeNode(getNode());
      }
   }

   /**
    * Creates and returns a copy of this object.  The precise meaning
    * of "copy" may depend on the class of the object. The general
    * intent is that, for any object <tt>x</tt>, the expression:
    * <blockquote>
    * <pre>
    * x.clone() != x</pre></blockquote>
    * will be true, and that the expression:
    * <blockquote>
    * <pre>
    * x.clone().getClass() == x.getClass()</pre></blockquote>
    * will be <tt>true</tt>, but these are not absolute requirements.
    * While it is typically the case that:
    * <blockquote>
    * <pre>
    * x.clone().equals(x)</pre></blockquote>
    * will be <tt>true</tt>, this is not an absolute requirement.
    * <p/>
    * By convention, the returned object should be obtained by calling
    * <tt>super.clone</tt>.  If a class and all of its superclasses (except
    * <tt>Object</tt>) obey this convention, it will be the case that
    * <tt>x.clone().getClass() == x.getClass()</tt>.
    * <p/>
    * By convention, the object returned by this method should be independent
    * of this object (which is being cloned).  To achieve this independence,
    * it may be necessary to modify one or more fields of the object returned
    * by <tt>super.clone</tt> before returning it.  Typically, this means
    * copying any mutable objects that comprise the internal "deep structure"
    * of the object being cloned and replacing the references to these
    * objects with references to the copies.  If a class contains only
    * primitive fields or references to immutable objects, then it is usually
    * the case that no fields in the object returned by <tt>super.clone</tt>
    * need to be modified.
    * <p/>
    * The method <tt>clone</tt> for class <tt>Object</tt> performs a
    * specific cloning operation. First, if the class of this object does
    * not implement the interface <tt>Cloneable</tt>, then a
    * <tt>CloneNotSupportedException</tt> is thrown. Note that all arrays
    * are considered to implement the interface <tt>Cloneable</tt>.
    * Otherwise, this method creates a new instance of the class of this
    * object and initializes all its fields with exactly the contents of
    * the corresponding fields of this object, as if by assignment; the
    * contents of the fields are not themselves cloned. Thus, this method
    * performs a "shallow copy" of this object, not a "deep copy" operation.
    * <p/>
    * The class <tt>Object</tt> does not itself implement the interface
    * <tt>Cloneable</tt>, so calling the <tt>clone</tt> method on an object
    * whose class is <tt>Object</tt> will result in throwing an
    * exception at run time.
    *
    * @return a clone of this instance.
    * @throws CloneNotSupportedException if the object's class does not
    *                                    support the <code>Cloneable</code> interface. Subclasses
    *                                    that override the <code>clone</code> method can also
    *                                    throw this exception to indicate that an instance cannot
    *                                    be cloned.
    * @see Cloneable
    */
   public Object clone() throws CloneNotSupportedException {
      NodeWrapper clone = (NodeWrapper)super.clone();

      clone.manager = manager;
      clone.nodeId = nodeId;
      clone.cachedNode = cachedNode;
      clone.expanded = expanded;
      clone.selected = selected;
      clone.children = new ArrayList();

      for (Iterator i=getLightChildren().iterator();i.hasNext();) {
         clone.children.add(((NodeWrapper)i.next()).clone());
      }

      clone.level = level;
      clone.selectedNode = selectedNode;
      clone.rootNode = rootNode;
      clone.filePickerState = filePickerState;

      return clone;
   }
}
