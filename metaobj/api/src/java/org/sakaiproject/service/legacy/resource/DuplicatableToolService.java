/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/sakaiproject/service/legacy/resource/DuplicatableToolService.java,v 1.3 2005/07/05 20:17:12 jellis Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.service.legacy.resource;

import org.sakaiproject.service.legacy.site.ToolConfiguration;

import java.util.List;

public interface DuplicatableToolService {

   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds);


}
