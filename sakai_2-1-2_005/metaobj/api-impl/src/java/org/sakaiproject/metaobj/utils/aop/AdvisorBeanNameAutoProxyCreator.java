/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.metaobj.utils.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;

public class AdvisorBeanNameAutoProxyCreator extends BeanNameAutoProxyCreator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Advisor[] advisors;


   protected void customizeProxyFactory(ProxyFactory proxyFactory) {
      setAdvisors(proxyFactory.getAdvisors());
   }

   /**
    * Subclasses may choose to implement this: for example,
    * to change the interfaces exposed
    *
    * @param bean bean about to be autoproxied
    * @param pf   ProxyFactory that will be used to create the proxy
    *             immediably after this method returns
    *             protected void customizeProxyFactory(Object bean, ProxyFactory pf) {
    *             super.customizeProxyFactory(bean, pf);
    *             setAdvisors(pf.getAdvisors());
    *             }
    */

   public Advisor[] getAdvisors() {
      return advisors;
   }

   public void setAdvisors(Advisor[] advisors) {
      this.advisors = advisors;
   }
}
