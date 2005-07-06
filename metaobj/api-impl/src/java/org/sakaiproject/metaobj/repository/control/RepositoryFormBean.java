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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/control/RepositoryFormBean.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.control;

import org.sakaiproject.metaobj.repository.model.Quota;
import org.sakaiproject.metaobj.repository.Node;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 4, 2004
 * Time: 10:33:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryFormBean {

   private String parentId;
   private String nodeId;
   private String nodeName;
   private String action = null;
   private NodeFormBean renameForm = new NodeFormBean();
   private NodeFormBean createForm = new NodeFormBean();
   private NodeFormBean moveForm = new NodeFormBean();
   private Quota quota = null;

   private List nodeTreeList = null;
   private NodeWrapper nodeWrapper = null;
   private Node currentWorksiteNode;

   public RepositoryFormBean() {
   }

   public String getParentId() {
      return parentId;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   public String getNodeId() {
      return nodeId;
   }

   public void setNodeId(String nodeId) {
      this.nodeId = nodeId;
   }

   public String getNodeName() {
      return nodeName;
   }

   public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
   }

   public List getNodeTreeList() {
      return nodeTreeList;
   }

   public void setNodeTreeList(List nodeTreeList) {
      this.nodeTreeList = nodeTreeList;
   }

   public NodeWrapper getNodeWrapper() {
      return nodeWrapper;
   }

   public void setNodeWrapper(NodeWrapper nodeWrapper) {
      this.nodeWrapper = nodeWrapper;
   }

   public NodeFormBean getRenameForm() {
      return renameForm;
   }

   public void setRenameForm(NodeFormBean renameForm) {
      this.renameForm = renameForm;
   }

   public NodeFormBean getCreateForm() {
      return createForm;
   }

   public void setCreateForm(NodeFormBean createForm) {
      this.createForm = createForm;
   }

   public NodeFormBean getMoveForm() {
      return moveForm;
   }

   public void setMoveForm(NodeFormBean moveForm) {
      this.moveForm = moveForm;
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public Quota getQuota() {
      return quota;
   }

   public void setQuota(Quota quota) {
      this.quota = quota;
   }

   public Node getCurrentWorksiteNode() {
      return currentWorksiteNode;
   }

   public void setCurrentWorksiteNode(Node currentWorksiteNode) {
      this.currentWorksiteNode = currentWorksiteNode;
   }

}
