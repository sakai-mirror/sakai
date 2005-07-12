/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/aop/AdvisorBeanNameAutoProxyCreator.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.utils.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.Advisor;

public class AdvisorBeanNameAutoProxyCreator extends BeanNameAutoProxyCreator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Advisor[] advisors;

   /**
    * Subclasses may choose to implement this: for example,
    * to change the interfaces exposed
    *
    * @param bean bean about to be autoproxied
    * @param pf   ProxyFactory that will be used to create the proxy
    *             immediably after this method returns
    */
   protected void customizeProxyFactory(Object bean, ProxyFactory pf) {
      super.customizeProxyFactory(bean, pf);
      setAdvisors(pf.getAdvisors());
   }

   public Advisor[] getAdvisors() {
      return advisors;
   }

   public void setAdvisors(Advisor[] advisors) {
      this.advisors = advisors;
   }
}
