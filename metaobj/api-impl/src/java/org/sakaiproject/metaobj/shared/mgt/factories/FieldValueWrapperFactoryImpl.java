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
      } catch (InstantiationException e) {
         RuntimeException exp = new IllegalArgumentException("Invalid wrapper class");
         exp.initCause(e);
         throw exp;
      } catch (IllegalAccessException e) {
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














