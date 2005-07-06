/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/TechnicalMetadataHomeInterface.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.mgt;

import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.model.impl.FileArtifactImpl;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;

public interface TechnicalMetadataHomeInterface extends WritableObjectHome {
   public StructuredArtifact loadArtifact(NodeMetadata node);

   public void storeArtifact(FileArtifactImpl artifact) throws PersistenceException;
}
