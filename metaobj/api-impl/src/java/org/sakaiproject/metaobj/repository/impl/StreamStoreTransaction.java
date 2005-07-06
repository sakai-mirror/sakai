/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/impl/StreamStoreTransaction.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.sakaiproject.metaobj.repository.intf.StreamStore;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.RepositoryNode;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class StreamStoreTransaction extends TransactionSynchronizationAdapter implements StreamStore {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List work = new ArrayList();
   private StreamStoreImpl parent;
   private Id txId;
   private boolean transacted;

   public StreamStoreTransaction(StreamStoreImpl parent, boolean transacted) {
      this.parent = parent;
      this.transacted = transacted;
      txId = parent.getTxId();
   }

   public void afterCompletion(int status) {
      super.afterCompletion(status);

      for (Iterator i=work.iterator();i.hasNext();) {
         UnitOfWork unit = (UnitOfWork)i.next();
         if (status == STATUS_COMMITTED) {
            unit.commit();
         }
         else {
            unit.rollback();
         }
         i.remove();
      }

      TransactionSynchronizationManager.unbindResource(StreamStoreImpl.CURRENT_THREAD_TRANSACTION);
   }

   public void copyStreams(NodeMetadata from, NodeMetadata to) {
      File file = getFile(from, ACTIVITY_TMP);
      if (!file.exists()) {
         file = getFile(from, ACTIVITY_NONE);
      }
      if (file.exists()) {
         store(to, RepositoryNode.FILE_TYPE, loadFile(from));
      }
   }

   public void delete(NodeMetadata nodeMetadata) {
      File file = getFile(nodeMetadata, ACTIVITY_NONE);
      file.renameTo(getFile(nodeMetadata, ACTIVITY_DELETE));
      removeFile(nodeMetadata, ACTIVITY_TMP);
      if (transacted) {
         work.add(new UnitOfWork(ACTIVITY_DELETE, nodeMetadata));
      }
      else {
         new UnitOfWork(ACTIVITY_DELETE,  nodeMetadata).commit();
      }
   }

   public InputStream load(NodeMetadata nodeMetadata, Id streamType) {
      return loadFile(nodeMetadata);
   }

   public long store(NodeMetadata nodeMetadata, Id streamType, InputStream stream) {
      File file = getFile(nodeMetadata, ACTIVITY_TMP);
      OutputStream os;
      try {
         os = new FileOutputStream(file);

         byte buffer[] = new byte[1024 * 100];
         int read = 0;

         while(read != -1) {
            read = stream.read(buffer);
            if (read != -1) {
               os.write(buffer, 0, read);
            }
         }

         os.close();
      } catch (FileNotFoundException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      removeFile(nodeMetadata, ACTIVITY_DELETE);
      if (transacted) {
         work.add(new UnitOfWork(ACTIVITY_TMP, nodeMetadata));
      }
      else {
         new UnitOfWork(ACTIVITY_TMP, nodeMetadata).commit();
      }
      return file.length();
   }

   protected InputStream loadFile(NodeMetadata nodeMetadata) {
      File file = getFile(nodeMetadata, ACTIVITY_TMP);
      if (!file.exists()) {
         file = getFile(nodeMetadata, ACTIVITY_NONE);
      }
      try {
         return new FileInputStream(file);
      } catch (FileNotFoundException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   protected File getFile(NodeMetadata nodeMetadata, int type) {
      if (type == ACTIVITY_NONE) {
         return new File(parent.getUserHome(nodeMetadata),
            nodeMetadata.getId().getValue());
      }
      return new File(parent.getUserHome(nodeMetadata),
         nodeMetadata.getId().getValue() + "." + type + "." + txId);
   }

   protected void removeFile(NodeMetadata nodeMetadata, int activity) {
      File file = getFile(nodeMetadata, activity);
      if (file.exists()) {
         file.delete();
      }
   }

   protected void replaceFile(NodeMetadata nodeMetadata, int activity) {
      File file = getFile(nodeMetadata, activity);
      File oldFile = getFile(nodeMetadata, ACTIVITY_NONE);

      if (oldFile.exists()) {
         oldFile.delete();
      }

      if (file.exists()) {
         file.renameTo(oldFile);
      }
   }

   private final static int ACTIVITY_NONE = 0;
   private final static int ACTIVITY_TMP = 1;
   private final static int ACTIVITY_DELETE = 2;

   private class UnitOfWork {
      private NodeMetadata node;
      private int activity;

      public UnitOfWork(int activity, NodeMetadata node) {
         this.activity = activity;
         this.node = node;
      }

      public void rollback() {
         if (activity == ACTIVITY_DELETE) {
            replaceFile(node, ACTIVITY_DELETE);
         }
         else if (activity == ACTIVITY_TMP) {
            removeFile(node, ACTIVITY_TMP);
         }
      }

      public void commit() {
         if (activity == ACTIVITY_DELETE) {
            removeFile(node, ACTIVITY_DELETE);
         }
         else if (activity == ACTIVITY_TMP) {
            replaceFile(node, ACTIVITY_TMP);
         }
      }
   }
}
