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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/impl/UserRootNodeCollection.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.PersistentNode;
import org.sakaiproject.metaobj.repository.RepositoryFunctions;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.*;

public class UserRootNodeCollection implements Node {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List children = new ArrayList();
   private Agent owner;
   private Id nodeId;
   private Map authzMap = new RootNodeMap();

   public UserRootNodeCollection(Agent owner, Id nodeId) {
      this.owner = owner;
      this.nodeId = nodeId;
   }

   public boolean exists() {
      return true;
   }

   public Node getChild(String relativePath) {
      for (Iterator i=getChildren().iterator();i.hasNext();) {
         Node node = (Node)i.next();

         if (node.getName().equals(relativePath)) {
            return node;
         }
      }

      return null;
   }

   /**
    * Return this object's children
    *
    * @return Enumeration Children's uris
    */
   public Collection getChildren() {
      return children;
   }

   /**
    * Get Node's unique identifier.
    *
    * @return String Id
    */
   public Id getId() {
      return nodeId;
   }

   public String getName() {
      return owner.getDisplayName();
   }

   public Agent getOwner() {
      return owner;
   }

   /**
    * Return this Node's parent
    *
    * @return Parent Node
    */
   public Node getParent() {
      // no parent
      return null;
   }

   public String getPath() {
      return "";
   }

   public NodeMetadata getTechnicalMetadata() {
      return null;
   }

   public boolean hasChild(String newName) {
      return getChild(newName) != null;
   }

   public boolean hasChildren() {
      return true;
   }

   public boolean isCollection() {
      return true;
   }

   public boolean isRoot() {
      return true;
   }

   public PersistentNode persistent() {
      // read-only
      return null;
   }

   public void addChild(Node child) {
      getChildren().add(child);
   }

   public String getDisplayName() {
      return getName();
   }

   public String getNodeSiteId() {
      return null;
   }

   public boolean isWorksiteRoot() {
      return false;
   }

   public Agent getAgentWorksiteRole(Agent agent) {
      return null;
   }

   public boolean isUserRoot() {
      return false;
   }

   public Map getAuthzMap() {
      return authzMap;
   }

   private class RootNodeMap extends HashMap {

      /**
       * Returns the value to which the specified key is mapped in this identity
       * hash map, or <tt>null</tt> if the map contains no mapping for this key.
       * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
       * that the map contains no mapping for the key; it is also possible that
       * the map explicitly maps the key to <tt>null</tt>. The
       * <tt>containsKey</tt> method may be used to distinguish these two cases.
       *
       * @param key the key whose associated value is to be returned.
       * @return the value to which this map maps the specified key, or
       *         <tt>null</tt> if the map contains no mapping for this key.
       * @see #put(Object, Object)
       */
      public Object get(Object key) {
         if (RepositoryFunctions.READ.equals(RepositoryFunctions.PREFIX + key.toString())) {
            return new Boolean(true);
         }
         else {
            return new Boolean(false);
         }
      }
   }
}
