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
package org.sakaiproject.metaobj.utils.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.*;

/**
 * <p>Loads the ApplicationContext from xml files listed in a property file.
 * The xml files referenced in the properties should be in the classpath.
 * The format of the property file should be as follows:</p>
 * <p/>
 * <br>1=help-config.xml
 * <br>2=help-web-config.xml
 * <br>3=home-config.xml
 * <br>...
 * <p/>
 * <p>The numbering controls the loading order of the files.  This
 * allows for overriding of beans configured in earlier xml definition files.
 * Without the numbering we can not gaurantee what order the files will load. </p>
 */
public class ApplicationContextFactory {
   private Log logger = LogFactory.getLog(this.getClass());
   private static ApplicationContextFactory factory = new ApplicationContextFactory();

   private ApplicationContextFactory() {
   }

   public static ApplicationContextFactory getInstance() {
      return factory;
   }

   /**
    * @param properties
    * @return
    */
   public ApplicationContext createContext(Properties properties) {
      try {
         return new ClassPathXmlApplicationContext(getConfigLocations(properties));
      }
      catch (Exception e) {
         throw new RuntimeException("problem loading ApplicationContext: " + e.getMessage(), e);
      }
   }

   public String[] getConfigLocations(Properties props) throws IOException {
      SortedMap configFiles = new TreeMap();
      for (Enumeration e = props.keys(); e.hasMoreElements();) {
         Integer key = new Integer((String) e.nextElement());
         String curFile = props.getProperty(key.toString());
         curFile = (!curFile.startsWith("/")) ? "/" + curFile : curFile;
         configFiles.put(key, curFile);
         logger.info("registering '" + curFile + "' in position " + key + " as spring bean definition file");
      }
      return convertToArray(configFiles.values());
   }

   protected String[] convertToArray(Collection collection) {
      return (String[]) collection.toArray(new String[collection.size()]);
   }
}
