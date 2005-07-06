/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/intf/DownloadableManager.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.intf;

import java.util.Map;
import java.io.OutputStream;
import java.io.IOException;

public interface DownloadableManager {

   public void packageForDownload(Map params, OutputStream out) throws IOException;
}
