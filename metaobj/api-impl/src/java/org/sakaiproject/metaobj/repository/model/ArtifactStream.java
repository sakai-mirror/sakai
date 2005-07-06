/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/ArtifactStream.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;

import java.sql.Blob;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.hibernate.lob.BlobImpl;

public class ArtifactStream {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id;
   private Id typeId;
   private Id rowId;
   private Blob dataStream;

   public Blob getDataStream() {
      return dataStream;
   }

   public void setDataStream(Blob dataStream) {
      this.dataStream = dataStream;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public Id getRowId() {
      return rowId;
   }

   public void setRowId(Id rowId) {
      this.rowId = rowId;
   }

   public Id getTypeId() {
      return typeId;
   }

   public void setTypeId(Id typeId) {
      this.typeId = typeId;
   }

   public void setStream(InputStream stream) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         byte[] b = new byte[1];
         while (stream.read(b) != -1) {
            out.write(b);
         }
         this.dataStream = new BlobImpl(out.toByteArray());
      } catch (IOException e) {
         logger.error("",e);
      }
   }

}
