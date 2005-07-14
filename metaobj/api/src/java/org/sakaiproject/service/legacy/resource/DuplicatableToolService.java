/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.service.legacy.resource;

import org.sakaiproject.service.legacy.site.ToolConfiguration;

import java.util.List;

public interface DuplicatableToolService {

   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds);


}
