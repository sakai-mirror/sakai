/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/WckRepositoryManagerHelper.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.repository.mgt.intf.ProtectedWebdavStore;
import org.sakaiproject.metaobj.security.AuthenticationManager;

public class WckRepositoryManagerHelper {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager;
   private ProtectedWebdavStore wck;
   private AuthenticationManager authManager;
   private HomeFactory homeFactory;

   public ReadableObjectHome getHome(String type) {
      return (ReadableObjectHome) getHomeFactory().getHome(type);
   }

   public String combine(String str1, String str2) {
      logger.debug("combine()");
      if (str2.startsWith("/")) str2 = str2.substring(1);
      if (str1.endsWith("/")) return str1 + str2;
      return str1 + "/" + str2;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public ProtectedWebdavStore getWck() {
      return wck;
   }

   public void setWck(ProtectedWebdavStore wck) {
      this.wck = wck;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }
}
