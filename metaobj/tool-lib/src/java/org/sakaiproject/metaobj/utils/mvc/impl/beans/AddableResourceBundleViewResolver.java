/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.utils.mvc.impl.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

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
    *
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
      for (Iterator i = baseNames.iterator(); i.hasNext();) {
         ResourceBundle bundle = ResourceBundle.getBundle((String) i.next(), locale,
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
