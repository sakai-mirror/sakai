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
 * $URL$
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 6, 2004
 * Time: 12:27:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeanFactory implements ApplicationContextAware, ServletContextListener {

   private static BeanFactory instance;
   private ApplicationContext applicationContext;

   public Object getBean(String beanName) {
      return applicationContext.getBean(beanName);
   }

   public Object getBean(String beanName, Class requiredType) {
      return applicationContext.getBean(beanName, requiredType);
   }

   public static BeanFactory getInstance() {
      return instance;
   }

   private static void setInstance(BeanFactory instance) {
      BeanFactory.instance = instance;
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
      setInstance(this);
   }

   public void contextInitialized(ServletContextEvent event) {
      ApplicationContext applicationContext = getInstance().applicationContext;
      if (applicationContext != null) {

         Collection servletContextListeners = applicationContext.getBeansOfType(ServletContextListener.class,
            true, true).values();

         for (Iterator i = servletContextListeners.iterator();i.hasNext();) {
            ServletContextListener listener = (ServletContextListener)i.next();

            if (!(listener instanceof BeanFactory)) {
               listener.contextInitialized(event);
            }
         }

         Map components = applicationContext.getBeansOfType(Component.class,
            true, true);

         for (Iterator i = components.entrySet().iterator();i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            ComponentManager.loadComponent((String)entry.getKey(), entry.getValue());
         }
      }
   }

   public void contextDestroyed(ServletContextEvent event) {
      ApplicationContext applicationContext = getInstance().applicationContext;
      if (applicationContext != null) {
         Collection servletContextListeners = applicationContext.getBeansOfType(ServletContextListener.class,
            true, true).values();

         for (Iterator i = servletContextListeners.iterator();i.hasNext();) {
            ServletContextListener listener = (ServletContextListener)i.next();

            if (!(listener instanceof BeanFactory)) {
               listener.contextDestroyed(event);
            }
         }
      }
   }

   public Map getBeans(Class aClass) {
      return applicationContext.getBeansOfType(aClass, true, false);
   }
}
