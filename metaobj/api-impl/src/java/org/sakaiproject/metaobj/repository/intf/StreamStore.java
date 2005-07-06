/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/intf/StreamStore.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.intf;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;

import java.io.InputStream;

public interface StreamStore {

   public long store(NodeMetadata nodeMetadata, Id streamType, InputStream stream);

   public InputStream load(NodeMetadata nodeMetadata, Id streamType);

   public void delete(NodeMetadata nodeMetadata);

   public void copyStreams(NodeMetadata from, NodeMetadata to);
}
