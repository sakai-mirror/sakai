/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/RepositoryConstants.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository;

import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.shared.model.IdImpl;

public interface RepositoryConstants {

   public final static Type FOLDER_TYPE = new Type(new IdImpl("folder", null));
   public final static Type FILE_TYPE = new Type(new IdImpl("fileArtifact", null));

}
