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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/FilePickerState.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.repository.FilePickerFilter;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.control.NodeWrapper;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Map;

public class FilePickerState {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public final static String ACTION_SHOW = "show";
   public final static String ACTION_CANCEL = "cancel";
   public final static String ACTION_PICK = "pick";
   public final static String ACTION_NONE = "none";

   public final static String STATE_SESSION_TAG_PREFIX = "theospi_portfolio_filePickerState";

   private Map params;
   private String filterName;
   private FilePickerFilter filter;
   private String nodeIdField;
   private String nodeNameField;
   private String pickerId;
   private Id selected;
   private String action;
   private NodeWrapper rootNode;
   private String panelId;
   private String pid;

   public FilePickerFilter getFilter() {
      return filter;
   }

   public void setFilter(FilePickerFilter filter) {
      this.filter = filter;
   }

   public String getNodeIdField() {
      return nodeIdField;
   }

   public void setNodeIdField(String nodeIdField) {
      this.nodeIdField = nodeIdField;
   }

   public String getNodeNameField() {
      return nodeNameField;
   }

   public void setNodeNameField(String nodeNameField) {
      this.nodeNameField = nodeNameField;
   }

   public Map getParams() {
      return params;
   }

   public void setParams(Map params) {
      this.params = params;
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public Id getSelected() {
      return selected;
   }

   public void setSelected(Id selected) {
      this.selected = selected;
   }

   public String getFilterName() {
      return filterName;
   }

   public void setFilterName(String filterName) {
      this.filterName = filterName;
   }

   public String getPickerId() {
      return pickerId;
   }

   public void setPickerId(String pickerId) {
      this.pickerId = pickerId;
   }

   public boolean allowSelectNode(Node node) {
      return getFilter().allowSelectNode(node, getParams());
   }

   public boolean includeNode(Node node) {
      return getFilter().includeNode(node, getParams());
   }

   public boolean isCreateAllowed() {
      return getFilter().allowCreateArtifact(getParams());
   }

   public NodeWrapper getRootNode() {
      return rootNode;
   }

   public void setRootNode(NodeWrapper rootNode) {
      this.rootNode = rootNode;
   }

   public String getPanelId() {
      return panelId;
   }

   public void setPanelId(String panelId) {
      this.panelId = panelId;
   }

   public String getPid() {
      return pid;
   }

   public void setPid(String pid) {
      this.pid = pid;
   }

   public static String getSessionTag(Map request) {
      String sessionTag = getPageSessionTag(request) + "_";
      sessionTag += request.get("panelId");

      return sessionTag;
   }

   public static String getPageSessionTag(Map request) {
      String sessionTag = STATE_SESSION_TAG_PREFIX + "_";
      sessionTag += request.get("pid");
      return sessionTag;
   }

}
