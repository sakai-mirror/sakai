/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/active-tool-component/src/java/org/sakaiproject/component/kernel/tool/ActiveToolComponent.java,v 1.17 2005/05/26 20:19:27 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.springframework.orm.hibernate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate.LocalSessionFactoryBean;

public class AddableSessionFactoryBean extends LocalSessionFactoryBean
    implements ApplicationContextAware
{
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
  protected void postProcessConfiguration(Configuration config)
      throws HibernateException
  {
    super.postProcessConfiguration(config);

     // this method is deprecated, but for some reason,
     // stuff doesn't work with the replacement
    String[] names = applicationContext
        .getBeanDefinitionNames(AdditionalHibernateMappings.class);

    try
    {
       List beans = new ArrayList();
      for (int i = 0; i < names.length; i++)
      {
        AdditionalHibernateMappings mappings = (AdditionalHibernateMappings) applicationContext
            .getBean(names[i]);

         beans.add(mappings);
      }

       Collections.sort(beans);

       for (Iterator i=beans.iterator();i.hasNext();) {
          AdditionalHibernateMappings mappings = (AdditionalHibernateMappings) i.next();
          mappings.processConfig(config);
       }

    }
    catch (IOException e)
    {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e);
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
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException
  {
    this.applicationContext = applicationContext;
  }

}
