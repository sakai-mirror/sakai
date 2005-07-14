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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/MapWrapper.java,v 1.1 2005/06/30 17:34:21 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/30 17:34:21 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.sakaiproject.metaobj.utils.TypedMap;

import java.beans.PropertyEditor;
import java.util.Map;

public class MapWrapper extends BeanWrapperBase {
   protected final Log logger = LogFactory.getLog(getClass());

   public MapWrapper() {
   }

   /**
    * Create new BeanWrapperImpl for the given object.
    *
    * @param object object wrapped by this BeanWrapper.
    * @throws BeansException if the object cannot be wrapped by a BeanWrapper
    */
   public MapWrapper(Object object) throws BeansException {
      super.setWrappedInstance(object);
   }

   /**
    * Create new BeanWrapperImpl for the given object,
    * registering a nested path that the object is in.
    *
    * @param object     object wrapped by this BeanWrapper.
    * @param nestedPath the nested path of the object
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public MapWrapper(Map object, String nestedPath) throws BeansException {
      super(object, nestedPath);
   }

   public Object getPropertyValue(String propertyName) throws BeansException {
      if (!(getWrappedInstance() instanceof Map)) {
         throw new FatalBeanException(getWrappedInstance().getClass() +
            ": bean is not a map, BeanWrapperImpl might be a better choice");
      }

      if (isNestedProperty(propertyName)) {
         return super.getPropertyValue(propertyName);
      } else {
         return ((Map) getWrappedInstance()).get(propertyName);
      }
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      Map map = (Map) getWrappedInstance();

      if (map instanceof TypedMap &&
         java.util.Map.class.isAssignableFrom(((TypedMap) map).getType(nestedProperty))) {
         return new MapWrapper((Map) map.get(nestedProperty),
            parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty);
      } else {
         return super.createNestedWrapper(parentPath, nestedProperty);
      }
   }

   protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty) {
      return new MixedBeanWrapper(propertyValue, nestedProperty);
   }


   public void setPropertyValue(String propertyName, Object value) throws BeansException {

      if (!(getWrappedInstance() instanceof Map)) {
         throw new FatalBeanException(getWrappedInstance().getClass() +
            ": bean is not a map, BeanWrapperImpl might be a better choice");
      }

      Map map = (Map) getWrappedInstance();

      if (isNestedProperty(propertyName)) {
         super.setPropertyValue(propertyName, value);
         return;
      }

      if (!(map instanceof TypedMap) ||
         ((TypedMap) map).getType(propertyName) == null)
         return;

      map.put(propertyName, value);
   }

   public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
      return null;
   }

   public Class getPropertyType(String propertyName) throws BeansException {
      if (!(getWrappedInstance() instanceof TypedMap)) return String.class;
      return ((TypedMap) getWrappedInstance()).getType(propertyName);
   }


}
