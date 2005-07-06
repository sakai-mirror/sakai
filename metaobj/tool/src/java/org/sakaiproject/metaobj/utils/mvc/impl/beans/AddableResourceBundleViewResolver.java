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
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/beans/AddableResourceBundleViewResolver.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;

import java.util.*;

public class AddableResourceBundleViewResolver extends ResourceBundleViewResolver {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List baseNames;
   private Map cachedFactories = new HashMap();
   private String defaultParentView;

   public List getBaseNames() {
      return baseNames;
   }

   public void setBaseNames(List baseNames) {
      this.baseNames = baseNames;
   }

   /**
    * Set the default parent for views defined in the ResourceBundle.
    * This avoids repeated "yyy1.parent=xxx", "yyy2.parent=xxx" definitions
    * in the bundle, especially if all defined views share the same parent.
    * <p>The parent will typically define the view class and common attributes.
    * Concrete views might simply consist of an URL definition then:
    * a la "yyy1.url=/my.jsp", "yyy2.url=/your.jsp".
    * <p>View definitions that define their own parent or carry their own
    * class can still override this. Strictly speaking, the rule that a
    * default parent setting does not apply to a bean definition that
    * carries a class is there for backwards compatiblity reasons.
    * It still matches the typical use case.
    * @param defaultParentView the default parent view
    */
   public void setDefaultParentView(String defaultParentView) {
      this.defaultParentView = defaultParentView;
   }

   /**
    * Initialize the BeanFactory from the ResourceBundle, for the given locale.
    * Synchronized because of access by parallel threads.
    */
   protected synchronized BeanFactory initFactory(Locale locale) throws MissingResourceException, BeansException {
      BeanFactory parsedBundle = isCache() ? (BeanFactory) this.cachedFactories.get(locale) : null;
      if (parsedBundle != null) {
         return parsedBundle;
      }

      DefaultListableBeanFactory factory = new DefaultListableBeanFactory(getApplicationContext());
      PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(factory);
      reader.setDefaultParentBean(this.defaultParentView);
      for (Iterator i=baseNames.iterator();i.hasNext();) {
         ResourceBundle bundle = ResourceBundle.getBundle((String)i.next(), locale,
            Thread.currentThread().getContextClassLoader());
         reader.registerBeanDefinitions(bundle);
      }
      factory.registerCustomEditor(Resource.class, new ResourceEditor(getApplicationContext()));

      if (isCache()) {
         factory.preInstantiateSingletons();
         this.cachedFactories.put(locale, factory);
      }
      return factory;
   }

   public void destroy() throws BeansException {
      for (Iterator it = this.cachedFactories.values().iterator(); it.hasNext();) {
         ConfigurableBeanFactory factory = (ConfigurableBeanFactory) it.next();
         factory.destroySingletons();
      }
      this.cachedFactories.clear();
   }


}
