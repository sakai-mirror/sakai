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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/NodeMetadata.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
/*
 * Created on May 3, 2004
 *
 */
package org.sakaiproject.metaobj.repository.model;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.shared.model.MimeType;

import java.util.Date;

/**
 * @author rpembry
 */
public class NodeMetadata {
   private Id id;
   private Type type;
   private String name;
   private Date lastModified;
   private Date creation;
   private long size;
   private Agent owner;
   private MimeType mimeType;
   private Id worksiteId;

   public NodeMetadata() {
      mimeType = new MimeType();
   }

   public NodeMetadata(NodeMetadata copy) {
      this.id = copy.id;
      this.creation = copy.creation != null?
         new Date(copy.creation.getTime()):null;
      this.lastModified = copy.lastModified != null?
         new Date(copy.lastModified.getTime()):null;
      this.mimeType = copy.mimeType != null?
         new MimeType(copy.getMimeType()):null;
      this.name = copy.name;
      this.owner = copy.owner;
      this.size = copy.size;
      this.type = copy.type;
      this.worksiteId = copy.worksiteId;
   }


   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return Returns the creation.
    */
   public Date getCreation() {
      return creation;
   }

   /**
    * @param creation The creation to set.
    */
   public void setCreation(Date creation) {
      this.creation = creation;
   }

   /**
    * @return Returns the lastModified.
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * @param lastModified The lastModified to set.
    */
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    * @return Returns the size.
    */
   public long getSize() {
      return size;
   }

   /**
    * @param size The size to set.
    */
   public void setSize(long size) {
      this.size = size;
   }

   /**
    * @return Returns the type.
    */
   public Type getType() {
      return type;
   }

   /**
    * @param type The type to set.
    */
   public void setType(Type type) {
      this.type = type;
   }

   public Id getTypeId() {
      return getType().getId();
   }

   /**
    * @return Returns the id
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set
    */
   public void setId(Id id) {
      this.id = id;
   }

   public String getMimeType() {
      return getMimeTypeObject().getValue();
   }

   public void setMimeType(MimeType mimeType) {
      this.mimeType = mimeType;
   }

   public void setMimeType(String mimeType) {
      setMimeType(new MimeType(mimeType));
   }

   public MimeType getMimeTypeObject() {
      return mimeType;
   }

   public String getPrimaryMimeType() {
      return getMimeTypeObject().getPrimaryType();
   }

   public void setPrimaryMimeType(String primaryMimeType) {
      getMimeTypeObject().setPrimaryType(primaryMimeType);
   }

   public String getSubMimeType() {
      return getMimeTypeObject().getSubType();
   }

   public void setSubMimeType(String subMimeType) {
      getMimeTypeObject().setSubType(subMimeType);
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public Id getWorksiteId() {
      return worksiteId;
   }

   public void setWorksiteId(Id worksiteId) {
      this.worksiteId = worksiteId;
   }

}
