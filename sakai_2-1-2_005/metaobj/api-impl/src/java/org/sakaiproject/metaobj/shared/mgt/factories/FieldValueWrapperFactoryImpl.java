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
package org.sakaiproject.metaobj.shared.mgt.factories;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.FieldValueWrapperFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 25, 2004
 * Time: 6:48:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldValueWrapperFactoryImpl implements FieldValueWrapperFactory {

   private Map wrappedClassMap = null;
   protected final Log logger = LogFactory.getLog(getClass());


   public FieldValueWrapperFactoryImpl() {
   }

   public boolean checkWrapper(Class clazz) {
      return (wrappedClassMap.get(clazz) != null);
   }

   public FieldValueWrapper wrapInstance(Class clazz) {
      Class wrapperClass = (Class) wrappedClassMap.get(clazz);

      FieldValueWrapper returnedWrapper = null;

      try {
         returnedWrapper = (FieldValueWrapper) wrapperClass.newInstance();
      }
      catch (InstantiationException e) {
         RuntimeException exp = new IllegalArgumentException("Invalid wrapper class");
         exp.initCause(e);
         throw exp;
      }
      catch (IllegalAccessException e) {
         RuntimeException exp = new IllegalArgumentException("Invalid wrapper class");
         exp.initCause(e);
         throw exp;
      }

      return returnedWrapper;
   }

   public FieldValueWrapper wrapInstance(Object obj) {

      FieldValueWrapper returnedWrapper = wrapInstance(obj.getClass());

      returnedWrapper.setValue(obj);

      return returnedWrapper;
   }


   public void setWrappedClassMap(Map wrappedClassMap) {
      this.wrappedClassMap = wrappedClassMap;
   }

   public void setWrappedClassNamesMap(Properties wrappedClassNamesMap) throws ClassNotFoundException {
      wrappedClassMap = new Hashtable();

      for (Iterator i = wrappedClassNamesMap.keySet().iterator(); i.hasNext();) {
         String key = (String) i.next();
         String value = wrappedClassNamesMap.getProperty(key);

         wrappedClassMap.put(Class.forName(key), Class.forName(value));
      }

      setWrappedClassMap(wrappedClassMap);
   }
}














