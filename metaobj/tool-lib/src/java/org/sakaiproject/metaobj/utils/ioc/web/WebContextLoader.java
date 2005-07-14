/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/ioc/web/WebContextLoader.java,v 1.2 2005/06/29 18:21:26 jellis Exp $
 * $Revision: 1.2 $
 * $Date: 2005/06/29 18:21:26 $
 */
package org.sakaiproject.metaobj.utils.ioc.web;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.sakaiproject.metaobj.utils.ioc.ApplicationContextFactory;
import org.sakaiproject.component.kernel.component.SpringCompMgr;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

import javax.servlet.ServletContext;
import java.util.Properties;
import java.io.IOException;

public class WebContextLoader extends ContextLoader {
   private final Log logger = LogFactory.getLog(ContextLoader.class);
   private static final String WEB_INF_PREFIX = "/WEB-INF";

   /**
	 * Instantiate the root WebApplicationContext for this loader, either a default
	 * XmlWebApplicationContext or a custom context class if specified.
	 * This implementation expects custom contexts to implement ConfigurableWebApplicationContext.
	 * Can be overridden in subclasses.
	 * @throws BeansException if the context couldn't be initialized
	 * @see #CONTEXT_CLASS_PARAM
	 * @see #DEFAULT_CONTEXT_CLASS
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent)
			throws BeansException {
      String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
      Class contextClass = DEFAULT_CONTEXT_CLASS;
      if (contextClassName != null) {
         try {
            contextClass = Class.forName(contextClassName, true, Thread.currentThread().getContextClassLoader());
         }
         catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load context class [" + contextClassName + "]", ex);
         }
         if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException(
               "Custom context class [" + contextClassName + "] is not of type ConfigurableWebApplicationContext");
         }
      }
      ConfigurableWebApplicationContext wac =
          (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
      //wac.setParent(parent);
      wac.setParent(((SpringCompMgr) ComponentManager
				.getInstance()).getApplicationContext());
      wac.setServletContext(servletContext);
      String contextFileName = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);

      try {
         wac.setConfigLocations(getConfigLocations(contextFileName));
      } catch (IOException e) {
         throw new ApplicationContextException(
               "I/O error loading '" + contextFileName +
               "' from classpath while creating application context: " + e.getMessage() , e);
      }
      wac.refresh();
      return wac;
	}

   protected String[] getConfigLocations(String contextFileName) throws IOException {
      Properties props = new Properties();
      props.load(this.getClass().getResourceAsStream(contextFileName));
      String[] configLocations = ApplicationContextFactory.getInstance().getConfigLocations(props);
      for (int i=0;i<configLocations.length;i++){
         configLocations[i] = WEB_INF_PREFIX + configLocations[i];
      }
      return configLocations;
   }

}
