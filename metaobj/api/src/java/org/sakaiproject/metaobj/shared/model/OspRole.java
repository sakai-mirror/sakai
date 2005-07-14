/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
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
