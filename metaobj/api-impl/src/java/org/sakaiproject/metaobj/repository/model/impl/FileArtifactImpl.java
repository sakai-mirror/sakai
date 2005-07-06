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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/impl/FileArtifactImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.model.impl;

import org.springframework.web.multipart.MultipartFile;
import org.sakaiproject.metaobj.repository.model.FileArtifact;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 10, 2004
 * Time: 11:51:55 AM
 * To change this template use File | Settings | File Templates.
 * @jira OSP-368 error uploading zero byte file
 */
public class FileArtifactImpl implements FileArtifact {
   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(getClass());

   private ReadableObjectHome home = null;
   private IdManager idManager = null;
   private StructuredArtifact structuredMetadata = null;
   private InputStream file = null;
   private MultipartFile fileForm;
   private NodeMetadata nodeMetadata;

   public FileArtifactImpl() {
      nodeMetadata = new NodeMetadata();
      setMimeType(new MimeType());
   }

   public long getSize() {
      return getNodeMetadata().getSize();
   }

   public void setSize(long size) {
      getNodeMetadata().setSize(size);
   }

   /**
    * @jira OSP-368 error uploading zero byte file
    */
   public InputStream getFile() {
      if (file != null) {
         return file;
      }
      else {
         return getHome().getStream(getArtifactId());
      }
   }

   public void setFile(InputStream data) {
      this.file = data;
   }

   public boolean isFileUpdated() {
      return (file != null);
   }

   public Agent getOwner() {
      return getNodeMetadata().getOwner();
   }

   public String getDisplayName() {
      return getNodeMetadata().getName();
   }

   public void setDisplayName(String displayName) {
      getNodeMetadata().setName(displayName);
   }

   public Id getId() {
      return getNodeMetadata().getId();
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @jira OSP-368 error uploading zero byte file
    */
   public void setFileForm(MultipartFile fileForm) {
      if (getDisplayName() == null) {
         setDisplayName(
            new File(fileForm.getOriginalFilename().replace('\\', File.separatorChar)).getName());
      }

      if (fileForm.getOriginalFilename().length() > 0) {
         if (getMimeType().getPrimaryType() == null) {
            setMimeType(fileForm.getContentType());
         }

         setSize(fileForm.getSize());
         try {
            setFile(fileForm.getInputStream());
         } catch (IOException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }

      this.fileForm = fileForm;
   }

   public MultipartFile getFileForm() {
      return fileForm;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.repository.model.FileArtifact#getMimeType()
    */
   public MimeType getMimeType() {
      return getNodeMetadata().getMimeTypeObject();
   }

   public void setMimeType(MimeType mimeType){
      getNodeMetadata().setMimeType(mimeType);
   }

   public void setMimeType(String mimeType){
      getNodeMetadata().setMimeType(mimeType);
   }

   public StructuredArtifact getStructuredMetadata() {
      return structuredMetadata;
   }

   public void setStructuredMetadata(StructuredArtifact structuredMetadata) {
      this.structuredMetadata = structuredMetadata;
   }

   public String getExternalUri() {
      return getHome().getExternalUri(getId(), getDisplayName());
   }

   /**
    * @jira OSP-368 error uploading zero byte file
    */
   public boolean isEmpty() {
      return getSize() <= 0;
   }

   public Id getArtifactId() {
      return getNodeMetadata().getId();
   }

   public void setArtifactId(Id artifactId) {
      getNodeMetadata().setId(artifactId);
   }

   public NodeMetadata getNodeMetadata() {
      return nodeMetadata;
   }

   public void setNodeMetadata(NodeMetadata nodeMetadata) {
      this.nodeMetadata = nodeMetadata;
   }

}
