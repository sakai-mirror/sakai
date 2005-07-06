/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/intf/NodeMetadataService.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.intf;

import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.shared.model.MimeType;

public interface NodeMetadataService {

   public NodeMetadata createNode(String name, Type type);

   public NodeMetadata createNode(String name, Type type, MimeType mimeType, long size);

   public NodeMetadata getNode(Id nodeId);

   public NodeMetadata store(NodeMetadata nodeMetadata);

   public void delete(Id nodeId);

   public NodeMetadata copyNode(NodeMetadata copy);

   public Long getTotalFileSize(Id agentId);

   public Long getTotalFileSize(Id agentId, Id excludedFile);
}
