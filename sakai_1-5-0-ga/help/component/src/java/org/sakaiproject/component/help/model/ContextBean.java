/*
 * $Header: /cvs/help/component/src/java/org/sakaiproject/component/help/model/ContextBean.java,v 1.1 2004/11/19 16:34:16 casong.indiana.edu Exp $
 * $Revision: 1.1 $
 * $Date: 2004/11/19 16:34:16 $
 */
package org.sakaiproject.component.help.model;

import org.sakaiproject.component.help.model.IdentifiableObject;
import org.sakaiproject.service.help.Context;

public class ContextBean extends IdentifiableObject implements Context  {
   private String name;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
