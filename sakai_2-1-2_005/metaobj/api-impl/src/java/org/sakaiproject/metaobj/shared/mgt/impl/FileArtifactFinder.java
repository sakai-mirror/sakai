/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.mgt.impl;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.ResourceProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 17, 2005
 * Time: 2:08:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileArtifactFinder implements ArtifactFinder {

   private ContentHostingService contentHostingService;
   private AgentManager agentManager;
   private IdManager idManager;
   private ReadableObjectHome contentResourceHome = null;

   public Collection findByOwnerAndType(Id owner, String type) {
      return findByOwnerAndType(owner, type, null);
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      String primaryMimeType = null;
      String subMimeType = null;

      if (mimeType != null) {
         primaryMimeType = mimeType.getPrimaryType();
         subMimeType = mimeType.getSubType();
      }

      List artifacts = getContentHostingService().findResources(ResourceProperties.FILE_TYPE,
            primaryMimeType, subMimeType);

      Collection returned = new ArrayList();

      for (Iterator i = artifacts.iterator(); i.hasNext();) {
         ContentResource resource = (ContentResource) i.next();
         ContentResourceArtifact resourceArtifact = createArtifact(resource);
         returned.add(resourceArtifact);
      }

      return returned;
   }

   protected ContentResourceArtifact createArtifact(ContentResource resource) {
      Agent resourceOwner = getAgentManager().getAgent(resource.getProperties().getProperty(ResourceProperties.PROP_CREATOR));
      Id resourceId = getIdManager().getId(getContentHostingService().getUuid(resource.getId()));
      ContentResourceArtifact resourceArtifact = new ContentResourceArtifact(resource, resourceId, resourceOwner);
      return resourceArtifact;
   }

   public Collection findByOwner(Id owner) {
      return null;
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      return null;
   }

   public Collection findByWorksite(Id worksiteId) {
      return null;
   }

   public Artifact load(Id artifactId) {
      String resourceId = getContentHostingService().resolveUuid(artifactId.getValue());

      if (resourceId == null) {
         return null;
      }

      try {
         ContentResource resource = getContentHostingService().getResource(resourceId);
         Artifact returned = createArtifact(resource);
         returned.setHome(getContentResourceHome());
         return returned;
      }
      catch (PermissionException e) {
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         throw new RuntimeException(e);
      }
   }

   public Collection findByType(String type) {
      return null;
   }

   public boolean getLoadArtifacts() {
      return false;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
   }

   public ContentHostingService getContentHostingService() {
      return contentHostingService;
   }

   public void setContentHostingService(ContentHostingService contentHostingService) {
      this.contentHostingService = contentHostingService;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public ReadableObjectHome getContentResourceHome() {
      return contentResourceHome;
   }

   public void setContentResourceHome(ReadableObjectHome contentResourceHome) {
      this.contentResourceHome = contentResourceHome;
   }

}
