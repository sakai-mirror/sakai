/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/impl/StreamStoreImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.HibernateTemplate;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.sakaiproject.metaobj.repository.RepositoryNode;
import org.sakaiproject.metaobj.repository.intf.StreamStore;
import org.sakaiproject.metaobj.repository.model.ArtifactStream;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;

import java.io.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

public class StreamStoreImpl extends HibernateDaoSupport implements StreamStore {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String repositoryPath;
   private File repositoryBase;
   private File tmpBase;
   private File userBase;
   private IdManager idManager;
   public static final String CURRENT_THREAD_TRANSACTION =
      "org.sakaiproject.metaobj.repository.impl.StreamStoreTransaction";

   public void delete(NodeMetadata nodeMetadata) {
      Collection streams = getArtifactStreams(nodeMetadata.getId());
      HibernateTemplate template = getHibernateTemplate();
      for (Iterator i=streams.iterator();i.hasNext();) {
         template.delete(i.next());
      }

      getOrCreateTransaction().delete(nodeMetadata);
   }

   public void copyStreams(NodeMetadata from, NodeMetadata to) {

      Collection streams = getArtifactStreams(from.getId());

      for (Iterator i=streams.iterator();i.hasNext();) {
         ArtifactStream stream = (ArtifactStream)i.next();
         try {
            store(to, stream.getTypeId(), stream.getDataStream().getBinaryStream());
         } catch (SQLException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }

      getOrCreateTransaction().copyStreams(from, to);
   }

   protected Collection getArtifactStreams(Id artifactId) {
      return getHibernateTemplate().find("from ArtifactStream where id=?", artifactId.getValue());
   }

   public InputStream load(NodeMetadata nodeMetadata, Id streamType) {
      if (streamType.equals(RepositoryNode.FILE_TYPE)) {
         return getOrCreateTransaction().load(nodeMetadata, streamType);
      }

      ArtifactStream stream = getArtifactStream(nodeMetadata.getId(), streamType);

      if (stream == null) {
         return null;
      }

      try {
         InputStream returned = stream.getDataStream().getBinaryStream();
         return returned;
      } catch (SQLException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected ArtifactStream getArtifactStream(Id artifactId, Id streamType) {
      Collection streams = getHibernateTemplate().find("from ArtifactStream where id=? and typeId=?",
         new Object[]{artifactId.getValue(), streamType.getValue()});

      if (streams.size() > 0) {
         return (ArtifactStream) streams.iterator().next();
      }
      else {
         return null;
      }
   }

   public long store(NodeMetadata nodeMetadata, Id streamType, InputStream stream) {
      if (streamType.equals(RepositoryNode.FILE_TYPE)) {
         return getOrCreateTransaction().store(nodeMetadata, streamType, stream);
      }

      ArtifactStream artifactStream = null;

      try {
         artifactStream = getArtifactStream(nodeMetadata.getId(), streamType);
         if (artifactStream == null) {
            artifactStream = new ArtifactStream();
         }
      } catch (RuntimeException exp) {
         // must be new
         artifactStream = new ArtifactStream();
      }

      CountingInputStream countingInputStream = new CountingInputStream(stream);

      artifactStream.setId(nodeMetadata.getId());
      artifactStream.setTypeId(streamType);
      artifactStream.setStream(countingInputStream);
      getHibernateTemplate().saveOrUpdate(artifactStream);
      getHibernateTemplate().flush();// clear the cache
      return countingInputStream.getRead();
   }

   protected File getUserHome(NodeMetadata nodeMetadata) {
      File userHome = new File(getUserBase(), nodeMetadata.getOwner().getId().getValue());

      if (!userHome.exists()) {
         userHome.mkdirs();
      }
      return userHome;
   }

   public Id getTxId() {
      return getIdManager().createId();
   }

   private class CountingInputStream extends InputStream {

      private InputStream source;
      private long read = 0;

      public CountingInputStream(InputStream source) {
         this.source = source;
      }

      public int read() throws IOException {
         read++;
         return source.read();
      }

      public int read(byte b[]) throws IOException {
         int returned = source.read(b);
         if (returned > 0) {
            read += returned;
         }
         return returned;
      }

      public int read(byte b[], int off, int len) throws IOException {
         int returned = source.read(b, off, len);
         if (returned > 0) {
            read += returned;
         }
         return returned;
      }

      public int available() throws IOException {
         return source.available();
      }

      public void close() throws IOException {
         source.close();    //To change body of overridden methods use File | Settings | File Templates.
      }

      public synchronized void mark(int readlimit) {
         source.mark(readlimit);    //To change body of overridden methods use File | Settings | File Templates.
      }

      public boolean markSupported() {
         return source.markSupported();    //To change body of overridden methods use File | Settings | File Templates.
      }

      public synchronized void reset() throws IOException {
         source.reset();    //To change body of overridden methods use File | Settings | File Templates.
      }

      public long skip(long n) throws IOException {
         return source.skip(n);
      }

      public long getRead() {
         return read;
      }

      public void setRead(long read) {
         this.read = read;
      }
   }

   public void init() {
      repositoryBase = new File(getRepositoryPath());

      if (!repositoryBase.exists()) {
         repositoryBase.mkdirs();
      }

      tmpBase = new File(repositoryBase, "tmp");

      if (!tmpBase.exists()) {
         tmpBase.mkdirs();
      }

      userBase = new File(repositoryBase, "users");

      if (!userBase.exists()) {
         userBase.mkdirs();
      }
   }

   public String getRepositoryPath() {
      return repositoryPath;
   }

   public void setRepositoryPath(String repositoryPath) {
      this.repositoryPath = repositoryPath;
   }

   public File getRepositoryBase() {
      return repositoryBase;
   }

   public void setRepositoryBase(File repositoryBase) {
      this.repositoryBase = repositoryBase;
   }

   public File getTmpBase() {
      return tmpBase;
   }

   public void setTmpBase(File tmpBase) {
      this.tmpBase = tmpBase;
   }

   public File getUserBase() {
      return userBase;
   }

   public void setUserBase(File userBase) {
      this.userBase = userBase;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   protected StreamStoreTransaction getOrCreateTransaction() {
      StreamStoreTransaction tx =
         (StreamStoreTransaction)TransactionSynchronizationManager.getResource(CURRENT_THREAD_TRANSACTION);

      if (tx == null) {
         tx = new StreamStoreTransaction(this, TransactionSynchronizationManager.isSynchronizationActive());
         if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.bindResource(CURRENT_THREAD_TRANSACTION, tx);
            TransactionSynchronizationManager.registerSynchronization(tx);
         }
      }

      return tx;
   }
}
