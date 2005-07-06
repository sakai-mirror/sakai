/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/RepositoryNodeAuthzMap.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.RepositoryFunctions;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.Authorization;
import org.sakaiproject.metaobj.shared.model.Agent;

import java.util.*;

public class RepositoryNodeAuthzMap extends HashMap {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Node node;
   private boolean owner = false;

   /**
    * Constructs an empty <tt>HashMap</tt> with the default initial capacity
    * (16) and the default load factor (0.75).
    */
   public RepositoryNodeAuthzMap(Agent currentAgent, AuthorizationFacade authorizationFacade, Node node) {
      this.node = node;
      init(currentAgent, authorizationFacade);
   }

   protected void init(Agent currentAgent, AuthorizationFacade authorizationFacade) {
      owner = node.getOwner().getId().equals(currentAgent.getId());

      if (node.isCollection() && !owner) {
         initAuthzs(currentAgent, authorizationFacade);
      }
   }

   protected void initAuthzs(Agent currentAgent, AuthorizationFacade authorizationFacade) {
      List authzs = authorizationFacade.getAuthorizations(getWorksiteRole(currentAgent),
         null, node.getId());

      for (Iterator i=authzs.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         put(authz.getFunction(), new Boolean(true));
      }
   }

   protected Agent getWorksiteRole(Agent currentAgent) {
      return node.getAgentWorksiteRole(currentAgent);
   }

   /**
    * Returns the value to which the specified key is mapped in this identity
    * hash map, or <tt>null</tt> if the map contains no mapping for this key.
    * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
    * that the map contains no mapping for the key; it is also possible that
    * the map explicitly maps the key to <tt>null</tt>. The
    * <tt>containsKey</tt> method may be used to distinguish these two cases.
    *
    * @param key the key whose associated value is to be returned.
    * @return the value to which this map maps the specified key, or
    *         <tt>null</tt> if the map contains no mapping for this key.
    * @see #put(Object, Object)
    */
   public Object get(Object key) {
      if (owner) {
         return new Boolean(true);
      }

      if (!node.isCollection()) {
         return node.getParent().getAuthzMap().get(key);
      }

      Object returned = super.get(RepositoryFunctions.PREFIX + "." + key.toString());
      if (returned == null && !node.isWorksiteRoot() && !node.isRoot()) {
         return node.getParent().getAuthzMap().get(key);
      }
      else if (returned == null) {
         return new Boolean(false);
      }

      return returned;
   }

}
