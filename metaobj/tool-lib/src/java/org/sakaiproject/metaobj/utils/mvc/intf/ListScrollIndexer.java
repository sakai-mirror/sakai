/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/intf/ListScrollIndexer.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.List;
import java.util.Map;

public interface ListScrollIndexer {

   public List indexList(Map request, Map model, List srouceList);
   
}
