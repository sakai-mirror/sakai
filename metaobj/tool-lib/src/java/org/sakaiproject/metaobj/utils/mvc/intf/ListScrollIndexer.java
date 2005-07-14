/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.List;
import java.util.Map;

public interface ListScrollIndexer {

   public List indexList(Map request, Map model, List srouceList);
   
}
