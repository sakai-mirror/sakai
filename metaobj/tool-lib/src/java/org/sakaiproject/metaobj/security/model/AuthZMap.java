/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/security/model/AuthZMap.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.security.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.security.AuthorizationFacade;

import java.util.HashMap;

public class AuthZMap extends HashMap {
   private AuthorizationFacade authzFacade;
   private String prefix;
   private Id qualifier;

   public AuthZMap(AuthorizationFacade authzFacade, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = "";
      this.qualifier = qualifier;
   }

   public AuthZMap(AuthorizationFacade authzFacade, String prefix, Id qualifier){
      this.authzFacade = authzFacade;
      this.prefix = prefix;
      this.qualifier = qualifier;
   }

   public Object get(Object key){
      if (super.get(key) == null) {
         super.put(key, new Boolean(authzFacade.isAuthorized(prefix + key.toString(), qualifier)));
      }
      return super.get(key);      
   }
}
