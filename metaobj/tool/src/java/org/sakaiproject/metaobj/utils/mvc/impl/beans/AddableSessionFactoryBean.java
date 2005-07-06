/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/beans/AddableSessionFactoryBean.java,v 1.1 2005/06/30 17:34:21 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/30 17:34:21 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.LocalSessionFactoryBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.sakaiproject.metaobj.shared.model.OspException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

public class AddableSessionFactoryBean extends LocalSessionFactoryBean implements ApplicationContextAware {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ApplicationContext applicationContext;

   /**
    * To be implemented by subclasses that want to to perform custom
    * post-processing of the Configuration object after this FactoryBean
    * performed its default initialization.
    *
    * @param config the current Configuration object
    * @throws net.sf.hibernate.HibernateException
    *          in case of Hibernate initialization errors
    */
   protected void postProcessConfiguration(Configuration config) throws HibernateException {
      super.postProcessConfiguration(config);

      Map beanMap = applicationContext.getBeansOfType(AdditionalHibernateMappings.class, true, true);

      if (beanMap == null) {
         return;
      }

      Collection beans = beanMap.values();

      try {
         for (Iterator i=beans.iterator();i.hasNext();) {
            AdditionalHibernateMappings mappings = (AdditionalHibernateMappings)i.next();
               mappings.processConfig(config);
         }
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   /**
    * Set the ApplicationContext that this object runs in.
    * Normally this call will be used to initialize the object.
    * <p>Invoked after population of normal bean properties but before an init
    * callback like InitializingBean's afterPropertiesSet or a custom init-method.
    * Invoked after ResourceLoaderAware's setResourceLoader.
    *
    * @param applicationContext ApplicationContext object to be used by this object
    * @throws org.springframework.context.ApplicationContextException
    *          in case of applicationContext initialization errors
    * @throws org.springframework.beans.BeansException
    *          if thrown by application applicationContext methods
    * @see org.springframework.beans.factory.BeanInitializationException
    */
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
