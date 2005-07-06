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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/WckNode.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.sakaiproject.metaobj.repository.*;
import org.sakaiproject.metaobj.repository.mgt.intf.ProtectedWebdavStore;
import org.sakaiproject.metaobj.repository.model.*;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.utils.BeanFactory;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.lock.ObjectLockedException;

import java.io.InputStream;
import java.util.*;


/**
 * User: rpembry
 * Date: Dec 14, 2004
 * Time: 9:40:16 AM
 */
public class WckNode implements RepositoryNode {

   protected final static org.apache.commons.logging.Log logger =
      org.apache.commons.logging.LogFactory.getLog(WckNode.class);

   // The URI is broken into two parts, a "root" and a "name"
   // where the root is the URI of the parent container

   private String root;
   private String name;
   private RepositoryManager repositoryManager;
   private WckRepositoryManagerHelper repositoryManagerHelper;
   private Type type;
   private Id cachedId;
   private Collection children = null;
   private NodeMetadata nodeMetadata = null;
   private TreeNode treeNode = null;
   private Map authzMap;
   private Boolean locked = null;
   private WckNode parent = null;

   boolean isRoot=false;
   private static final String CHARACTER_ENCODING = "UTF-8";

   protected WckNode(NodeMetadata node, TreeNode treeNode, WckNode parent) {
      this.nodeMetadata = node;
      this.treeNode = treeNode;
      this.parent = parent;
      name = node.getName();
      root = treeNode.getParent().getUri();
   }

   public WckNode(String uri) {
      construct(uri,false);
   }
   public WckNode(String uri, boolean isRoot) {
      construct(uri,isRoot);
   }

   protected void construct(String uri, boolean isRoot) {
      if (logger.isDebugEnabled()) logger.debug("constructing WckNode("+uri+","+isRoot+")");
      this.isRoot=isRoot;
      decomposeName(uri);
      if (logger.isDebugEnabled()) logger.debug("exists: "+exists());
   }

   public WckNode(Id id) {
      if (logger.isDebugEnabled()) logger.debug("WckNode(" + id + ")");
      ProtectedWebdavStore store = getWck();
      try {
         TreeNodeMetadata treeNodeMetadata = store.getNode(id);

         this.nodeMetadata = treeNodeMetadata.getNodeMetadata();
         this.treeNode = treeNodeMetadata.getTreeNode();
         name = nodeMetadata.getName();
         root = treeNode.getParent().getUri();
      } finally {
         store.close();
      }
   }

   public String getNodeSiteId() {
      return getRepositoryManager().getWorksiteId(getPath());
   }
 
  public boolean isRoot() {
     logger.debug("isRoot()");
     return isRoot;
  }

   public String toString() {
      return "Node:["+root+" / "+name+"]";
   }


   /**
    * Get Node's unique identifier.
    *
    * @return String Id
    */
   public Id getId() {
      logger.debug("getId()");
      if (cachedId==null) {
         cachedId=lookupId();
      }
      return cachedId;

   }

   protected Id lookupId() {
      return getTechnicalMetadata().getId();
   }

   /**
    * Return this object's children
    *
    * @return Enumeration Children's uris
    */
   public Collection getChildren() {
      if (children == null) {
         children = getChildrenInternal();
      }
      return children;
   }

   protected Collection getChildrenInternal() {
      logger.debug("getChildren()");
      ProtectedWebdavStore store = getWck();
      Collection result=new TreeSet(new WckNodeComparator());
      List children;
      try {
         children=store.getChildren(getId());
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         store.close();
      }

      for (Iterator i=children.iterator();i.hasNext();) {
         TreeNodeMetadata child = (TreeNodeMetadata)i.next();
         result.add(new WckNode(child.getNodeMetadata(), child.getTreeNode(), this));
      }
      return result;
   }

   public boolean hasChildren() {
      logger.debug("hasChildren()");
      return !getChildren().isEmpty();
   }

   public String getPath() {
      logger.debug("getPath()");
      return getRepositoryManagerHelper().combine(root,name);
   }

   /**
    * Return this Node's parent
    *
    * @return Parent Node
    */
   public Node getParent() {
      if (parent == null) {
         if (root.length()>0) {
            parent = new WckNode(root);
         } else {
            parent = new WckNode("/",true); //TODO shouldn't use slash...
         }
      }
      return parent;
   }

   public PersistentNode persistent() {
      logger.debug("persistent()");
      return this;
   }

   public String getName() {
      logger.debug("getName()");
      return name;
   }
   public String getDisplayName() {
      logger.debug("getName()");
      return name;
   }

   public NodeMetadata getTechnicalMetadata() {
      if (nodeMetadata == null) {
         nodeMetadata = getTechnicalMetadataInternal();
      }
      return nodeMetadata;
   }

   protected NodeMetadata getTechnicalMetadataInternal() {
      String uri = getPath();
      NodeMetadata meta = null;

      ProtectedWebdavStore store = getWck();

      try {
         meta = store.getNodeMetadata(uri);
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         store.close();
      }

      return meta;
   }

   public boolean isCollection() {
      logger.debug("isCollection()");
      return getTechnicalMetadata().getTypeId().equals(RepositoryConstants.FOLDER_TYPE.getId());
   }

   public Node getChild(String relativePath) {
      Node result=new WckNode(getRepositoryManagerHelper().combine(getPath(),relativePath));
      if (!result.exists()) {
         return null;
      }
      return result;
   }

   public boolean hasChild(String newName) {
      logger.debug("hasChild()");
      String newFolder=getRepositoryManagerHelper().combine(getPath(),newName);
      return wckExists(newFolder);
   }

   public Agent getOwner() {
      //TODO use a UserType for Agent
      return getTechnicalMetadata().getOwner();
   }

   public boolean exists() {
      logger.debug("exists()");
      return wckExists(getPath());
   }

   protected boolean wckExists(String folder) {
      boolean exists=false;
      ProtectedWebdavStore store = getWck();

      try {
         exists = store.objectExists(folder);
      } catch (Exception e) {
      // intentionally ignore this
      } finally {
         store.close();
      }
      return exists;
   }

   /**
    * Create a new container Node that is a child of this Node
    *
    * @param name for new Container
    */
   public Node createChildContainer(String name) {
      logger.debug("createChildContainer("+name+")");
      String newFolder=getRepositoryManagerHelper().combine(getPath(),name);
      logger.debug("newFolder: "+newFolder);

      if (wckExists(newFolder)) {
         logger.warn("createChildContainer called for existing container");
         logger.debug("createChildContainer() is unexpectedly creating new WckNode");
         return new WckNode(newFolder);
      }

      ProtectedWebdavStore store = getWck();

      try {
         store.createFolder(newFolder);
      } catch (Exception e) {
         throw new OspException(OspException.CREATE_FAILED, new Throwable(e));
      } finally {
         store.close();
      }

      logger.debug("createChildContainer() is creating new WckNode");
      return new WckNode(newFolder);
   }

   protected Id getAgentId() {
      return getAuthnManager().getAgent().getId();
   }

   /**
    * Attempts to delete given child Node
    *
    * @param child
    */
   public void removeChild(Node child) {
      logger.debug("removeChild("+child+")");
      String path=child.getPath();
      ProtectedWebdavStore store = getWck();

      try {
         store.removeObject(path);
      } catch (Exception e) {
         throw new OspException(OspException.DELETE_FAILED, new Throwable(e));
      } finally {
         store.close();
      }
   }

   /**
    * This is the Stream that the user manipulates. Some homes may want to keep it empty, or
    * place a message in it.
    *
    * @param displayName
    * @param in
    * @return
    */
   public RepositoryNode createFile(String contentType, String displayName, InputStream in, Type type) {
      String newFile=getRepositoryManagerHelper().combine(getPath(),displayName);

      if (wckExists(newFile)) {
         logger.warn("createFile called for existing container");
         throw new OspException("Can't create file, because it already exists: "+newFile);
      }

      ReadableObjectHome home = getRepositoryManagerHelper().getHome(type.getId().getValue());

      if (!(home instanceof WritableObjectHome)) {
         throw new OspException("Can't create file, because it's home isn't writable.");
      }

      WritableObjectHome write = (WritableObjectHome)home;
      Artifact art;
      try {
         art = write.store(displayName, contentType, type, in);
      } catch (PersistenceException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      createNode(art.getId(), art.getDisplayName());

      return new WckNode(newFile);
   }

   public RepositoryNode createNode(Id newNode, String displayName) {
      String newFile=getRepositoryManagerHelper().combine(getPath(),displayName);
      if (wckExists(newFile)) {
         logger.warn("createFile called for existing container");
         throw new OspException("Can't create file, because it already exists: "+newFile);
      }

      ProtectedWebdavStore store = getWck();
      try {
         store.createNode(getId(), newNode, displayName);
      } finally {
         store.close();
      }

      return new WckNode(newFile);
   }

   /**
    * Attempts to move this Node by assigning a new parent.
    *
    * @param newParent
    */
   public void moveToNode(Node newParent) {
      logger.debug("moveToNode()");
      ProtectedWebdavStore store = getWck();

      try {
         String uri = getPath();
         String parentUri = store.getUri(newParent.getId());
         store.macroMove(uri,getRepositoryManagerHelper().combine(parentUri,name),false);
      } catch (Exception e) {
         throw new OspException(OspException.MOVE_FAILED, new Throwable(e));
      } finally {
         store.close();
      }
   }

   /**
    * Attempts to delete this Node regardless of its contents
    */
   public void destroy() {
      logger.debug("destroy()");
      ProtectedWebdavStore store = getWck();

      try {
         store.removeObject(getPath());
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         store.close();
      }

   }

   public void rename(String name) {
      logger.debug("rename()");
      ProtectedWebdavStore store = getWck();

      try {
         String uri = getPath();
         store.macroMove(uri,getRepositoryManagerHelper().combine(root,name),false);
      } catch (Exception e) {
         throw new OspException(OspException.RENAME_FAILED, new Throwable(e));
      } finally {
         store.close();
      }
   }

   public void setTechnicalMetadata(NodeMetadata data) {
      logger.debug("setTechnicalMetadata()");
      NodeMetadata old = getTechnicalMetadata();

      if (!old.getName().equals(data.getName())) {
         rename(data.getName());
      }

      if (data.getType().getId().equals(RepositoryNode.FILE_TYPE)) {
         WritableObjectHome home = (WritableObjectHome)getRepositoryManagerHelper().getHome(
            data.getType().getId().getValue());

         try {
            FileArtifact art = (FileArtifact)home.load(data.getId());
            art.getNodeMetadata().setMimeType(data.getMimeTypeObject());
            home.store(art);
         } catch (PersistenceException e) {
            logger.error("", e);
            throw new OspException(e);
         }

      }
   }

   public InputStream getStream() {
      NodeMetadata metadata = getTechnicalMetadata();
      ReadableObjectHome home = getRepositoryManagerHelper().getHome(
         metadata.getType().getId().getValue());
      return home.getStream(metadata.getId());
   }

   public RepositoryNode copy() {
      logger.debug("copy()");
      return copy(createCopyName(getName()), getParent().getId());
   }

   public RepositoryNode copy(String newName, Id parentId) {
      logger.debug("copy()");
      ProtectedWebdavStore store = getWck();
      PersistentNode parent = getRepositoryManager().getNode(parentId).persistent();
      String target = getRepositoryManagerHelper().combine(parent.getPath(), newName);
      try {
         store.macroCopy(getPath(), target, false, true);
      } catch (ServiceAccessException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AccessDeniedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ObjectNotFoundException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ObjectAlreadyExistsException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ObjectLockedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return new WckNode(target);
   }

   protected String createCopyName(String name) {
      String newName = "copy-" + name;
      boolean dupe = getParent().hasChild(newName);

      int counter = 1;

      while (dupe) {
         counter++;
         newName = "copy" + counter + "-" + name;
         dupe = getParent().hasChild(newName);
      }

      return newName;
   }

   public void writeStream(InputStream in) {
      ProtectedWebdavStore store = getWck();
      NodeMetadata metadata = getTechnicalMetadata();

      try {
         store.setResourceContent(getPath(), in, metadata.getMimeType(), CHARACTER_ENCODING);
      } catch (ServiceAccessException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (AccessDeniedException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ObjectNotFoundException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ObjectLockedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public Type getType() {
      return getTechnicalMetadata().getType();
   }

   public MimeType getMimeType() {
      logger.debug("getMimeType()");
      return getTechnicalMetadata().getMimeTypeObject();
   }

   public String getExternalUri() {
      logger.debug("getExternalUri()");
      ReadableObjectHome home = getHome();
      return home.getExternalUri(getId(), getName());
   }

   /**
    * xsl safe uri's (ie no & changed to &amp;)
    * @return
    */
   public String getExternalUriForXsl() {
      logger.debug("getExternalUri()");
      ReadableObjectHome home = getHome();
      return home.getExternalUri(getId(), getName()).replaceAll("&","&amp;");
   }


   protected void decomposeName(String uri) {
      logger.debug("decomposeName()");
      StringBuffer rootName=new StringBuffer();
      if (uri.charAt(uri.length() - 1) == '/') {
         uri = uri.substring(0, uri.length() - 1);
      }

      int pos = uri.lastIndexOf('/');

      while (pos != -1 && pos != 0) {
         String name = uri.substring(pos + 1);
         rootName.append(uri.substring(0, pos));
         uri = name;
         pos = uri.lastIndexOf('/');
      }

      this.name = uri;
      this.root = rootName.toString();
   }

   protected ReadableObjectHome getHome(String type) {
      return getRepositoryManagerHelper().getHome(type);
   }

   protected AgentManager getAgentManager() {
      return getRepositoryManagerHelper().getAgentManager();
   }

   protected RepositoryManager getRepositoryManager() {
      if (repositoryManager == null){
         repositoryManager = (RepositoryManager)BeanFactory.getInstance().getBean(
            "repositoryManager", RepositoryManager.class);
      }
      return repositoryManager;
   }

   public AuthenticationManager getAuthnManager() {
      return getRepositoryManagerHelper().getAuthManager();

   }

   public ProtectedWebdavStore getWck() {
      return getRepositoryManagerHelper().getWck();
   }

   protected ReadableObjectHome getHome() {
      NodeMetadata metadata = getTechnicalMetadata();
      return getRepositoryManagerHelper().getHome(metadata.getType().getId().getValue());
   }

   public WckRepositoryManagerHelper getRepositoryManagerHelper() {
      if (repositoryManagerHelper == null){
         repositoryManagerHelper = (WckRepositoryManagerHelper)BeanFactory.getInstance().getBean(
            WckRepositoryManagerHelper.class.getName(), WckRepositoryManagerHelper.class);
      }
      return repositoryManagerHelper;
   }

   public void setRepositoryManagerHelper(WckRepositoryManagerHelper repositoryManagerHelper) {
      this.repositoryManagerHelper = repositoryManagerHelper;
   }

   public Map getAuthzMap() {
      if (authzMap == null) {
         authzMap = new RepositoryNodeAuthzMap(getCurrentAgent(),
            getRepositoryManager().getAuthorizationFacade(), this);
      }

      return authzMap;
   }

   public boolean isWorksiteRoot() {
      return false;
   }

   public Agent getAgentWorksiteRole(Agent agent) {
      if (getParent() != null) {
         return getParent().getAgentWorksiteRole(agent);
      }
      return null;
   }

   public boolean isUserRoot() {
      if (!isWorksiteRoot() && !isCollection()) {
         Node node = getRepositoryManager().getRootNode(getOwner());
         return node.getId().equals(getId());
      }
      return false;
   }

   protected Agent getCurrentAgent() {
      return getRepositoryManager().getAuthManager().getAgent();
   }

   public boolean isLocked() {
      if (locked == null) {
         if (isCollection()) {
            locked = new Boolean(getRepositoryManager().getLockManager().containsLockedNode(this));
         }
         else {
            locked = new Boolean(getRepositoryManager().getLockManager().isLocked(this));
         }
      }
      return locked.booleanValue();
   }

   public void setParent(WckNode parent) {
      this.parent = parent;
   }

}
