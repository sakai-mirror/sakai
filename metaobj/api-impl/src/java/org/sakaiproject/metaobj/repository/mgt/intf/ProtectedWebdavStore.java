package org.sakaiproject.metaobj.repository.mgt.intf;

import org.apache.slide.simple.store.*;
import org.apache.commons.pool.ObjectPool;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.model.TreeNodeMetadata;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.List;

/**
 * This is a marker interface to prevent inadvertent direct use of a BasicWebdavStore,
 * which would circumvent authorization checks.
 *
 * User: rpembry
 * Date: Jan 24, 2005
 * Time: 10:09:26 AM
 */
public interface ProtectedWebdavStore extends WebdavStoreIdExtension, BasicWebdavStore,
   WebdavStoreLockExtension, WebdavStoreBulkPropertyExtension, 
   WebdavStoreMacroCopyExtension, WebdavStoreMacroMoveExtension, WebdavStoreMacroDeleteExtension {

   public void setPool(ObjectPool pool);
   public void close();

   public NodeMetadata getNodeMetadata(String uri);

   public NodeMetadata createNode(Id parentId, Id nodeId, String displayName);

   public List getChildren(Id parentId);

   public TreeNodeMetadata getNode(Id id);
}
