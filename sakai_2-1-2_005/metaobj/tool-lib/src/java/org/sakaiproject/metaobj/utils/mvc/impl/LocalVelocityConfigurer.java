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
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.sakaiproject.metaobj.utils.mvc.intf.VelocityEngineFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 5, 2005
 * Time: 4:21:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalVelocityConfigurer implements InitializingBean, ApplicationContextAware, VelocityEngineFactory {

   protected final transient Log logger = LogFactory.getLog(getClass());

   private String resourceLoaderPath;

   private boolean preferFileSystemAccess = true;

   private Map velocityProperties;

   private VelocityEngine velocityEngine;

   private WebApplicationContext webApplicationContext;

   /**
    * Prepare the VelocityEngine instance and return it.
    *
    * @return the VelocityEngine instance
    * @throws java.io.IOException if the config file wasn't found
    * @throws org.apache.velocity.exception.VelocityException
    *                             on Velocity initialization failure
    */
   public VelocityEngine createVelocityEngine() throws IOException, VelocityException {
      VelocityEngine velocityEngine = new VelocityEngine();
      Properties props = new Properties();

      // Merge local properties if set.
      if (!this.velocityProperties.isEmpty()) {
         props.putAll(this.velocityProperties);
      }

      // Apply properties to VelocityEngine.
      for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
         Map.Entry entry = (Map.Entry) it.next();
         if (!(entry.getKey() instanceof String)) {
            throw new IllegalArgumentException("Illegal property key [" + entry.getKey() + "]: only Strings allowed");
         }
         velocityEngine.setProperty((String) entry.getKey(), entry.getValue());
      }

      initResourceLoader(velocityEngine);

      try {
         // Perform actual initialization.
         velocityEngine.init();
      }
      catch (IOException ex) {
         throw ex;
      }
      catch (VelocityException ex) {
         throw ex;
      }
      catch (RuntimeException ex) {
         throw ex;
      }
      catch (Exception ex) {
         logger.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
         throw new VelocityException(ex.getMessage());
      }

      return velocityEngine;
   }

   public String getRealPath(String path) {
      return webApplicationContext.getServletContext().getRealPath(path);
   }


   /**
    * Initialize a SpringResourceLoader for the given VelocityEngine.
    *
    * @param velocityEngine the VelocityEngine to configure
    * @see org.springframework.ui.velocity.SpringResourceLoader
    */
   protected void initResourceLoader(VelocityEngine velocityEngine) {
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
      velocityEngine.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
      velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
      velocityEngine.setProperty("file.resource.loader.path", getRealPath(getResourceLoaderPath()));
      velocityEngine.setProperty("file.resource.loader.cache", "true");
      velocityEngine.setProperty("file.resource.loader.modificationCheckInterval", "0");
   }

   /**
    * Initialize VelocityEngineFactory's VelocityEngine
    * if not overridden by a preconfigured VelocityEngine.
    *
    * @see #createVelocityEngine
    */
   public void afterPropertiesSet() throws IOException, VelocityException {
      if (this.velocityEngine == null) {
         this.velocityEngine = createVelocityEngine();
      }
   }

   public VelocityEngine getVelocityEngine() {
      return this.velocityEngine;
   }

   public String getResourceLoaderPath() {
      return resourceLoaderPath;
   }

   public void setResourceLoaderPath(String resourceLoaderPath) {
      this.resourceLoaderPath = resourceLoaderPath;
   }

   public boolean isPreferFileSystemAccess() {
      return preferFileSystemAccess;
   }

   public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
      this.preferFileSystemAccess = preferFileSystemAccess;
   }

   public Map getVelocityProperties() {
      return velocityProperties;
   }

   public void setVelocityProperties(Map velocityProperties) {
      this.velocityProperties = velocityProperties;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.webApplicationContext = (WebApplicationContext) applicationContext;
   }
}
