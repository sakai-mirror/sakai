/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/theospi/metaobj/shared/model/OspRole.java,v 1.3 2005/06/30 17:34:18 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.model;

public interface OspRole extends Agent {

   /**
    * gets the name of the role idependant of the site it belongs to
    * @return
    */
   public String getRoleName();
}
