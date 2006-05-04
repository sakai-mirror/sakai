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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.PropertyEditor;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 1:50:42 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BeanWrapperBase extends BeanWrapperImpl {
   protected final Log logger = LogFactory.getLog(getClass());
   protected List customEditors = new ArrayList();

   /**
    * The nested path of the object
    */
   private String nestedPath = "";

   /* Map with cached nested BeanWrappers */
   private Map nestedBeanWrappers;


   public BeanWrapperBase() {
   }

   /**
    * Create new BeanWrapperImpl for the given object.
    *
    * @param object object wrapped by this BeanWrapper.
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public BeanWrapperBase(Object object) throws BeansException {
      super.setWrappedInstance(object);
   }


   /**
    * Create new BeanWrapperImpl for the given object,
    * registering a nested path that the object is in.
    *
    * @param object     object wrapped by this BeanWrapper.
    * @param nestedPath the nested path of the object
    * @param rootObject the root object at the top of the path
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public BeanWrapperBase(Object object, String nestedPath, Object rootObject) throws BeansException {
      super(object, nestedPath, rootObject);
      this.nestedPath = nestedPath;
   }

   public Object getPropertyValue(String propertyName) throws BeansException {
      if (isNestedProperty(propertyName)) {
         BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
         return nestedBw.getPropertyValue(getFinalPath(propertyName));
      }
      else {
         return super.getPropertyValue(propertyName);
      }
   }

   /**
    * Recursively navigate to return a BeanWrapper for the nested property path.
    *
    * @param propertyPath property property path, which may be nested
    * @return a BeanWrapper for the target bean
    */
   protected BeanWrapperImpl getBeanWrapperForPropertyPath(String propertyPath) {
      int pos = propertyPath.indexOf(NESTED_PROPERTY_SEPARATOR);
      // Handle nested properties recursively
      if (pos > -1) {
         String nestedProperty = propertyPath.substring(0, pos);
         String nestedPath = propertyPath.substring(pos + 1);
         logger.debug("Navigating to nested property '" + nestedProperty + "' of property path '" + propertyPath + "'");
         BeanWrapperImpl nestedBw = getNestedBeanWrapper(nestedProperty);

         if (nestedBw instanceof BeanWrapperBase) {
            return ((BeanWrapperBase) nestedBw).getBeanWrapperForPropertyPath(nestedPath);
         }
         else {
            return nestedBw;
         }
      }
      else {
         return this;
      }
   }

   /**
    * Retrieve a BeanWrapper for the given nested property.
    * Create a new one if not found in the cache.
    * <p>Note: Caching nested BeanWrappers is necessary now,
    * to keep registered custom editors for nested properties.
    *
    * @param nestedProperty property to create the BeanWrapper for
    * @return the BeanWrapper instance, either cached or newly created
    */
   protected BeanWrapperImpl getNestedBeanWrapper(String nestedProperty) {
      if (this.nestedBeanWrappers == null) {
         this.nestedBeanWrappers = new HashMap();
      }

      BeanWrapperImpl nextWrapper = (BeanWrapperImpl) nestedBeanWrappers.get(nestedProperty);

      if (nextWrapper == null) {
         nextWrapper = createNestedWrapper(nestedPath, nestedProperty);
         ;
         nestedBeanWrappers.put(nestedProperty, nextWrapper);
         return nextWrapper;
      }
      else if (nextWrapper instanceof BeanWrapperBase) {
         return ((BeanWrapperBase) nextWrapper).getBeanWrapperForPropertyPath(nestedProperty);
      }
      else {
         return nextWrapper;
      }
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      BeanWrapperImpl nextWrapper = null;

      nextWrapper = constructWrapper(this.getPropertyValue(nestedProperty),
            parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty);

      nestedBeanWrappers.put(nestedProperty, nextWrapper);

      for (Iterator i = customEditors.iterator(); i.hasNext();) {
         CustomEditorHolder holder = (CustomEditorHolder) i.next();
         nextWrapper.registerCustomEditor(holder.requiredType, holder.propertyPath,
               holder.propertyEditor);
      }

      return nextWrapper;
   }

   abstract protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty);

   public void registerCustomEditor(Class requiredType, String propertyPath, PropertyEditor propertyEditor) {
      CustomEditorHolder holder = new CustomEditorHolder();
      holder.requiredType = requiredType;
      holder.propertyPath = propertyPath;
      holder.propertyEditor = propertyEditor;

      customEditors.add(holder);

      super.registerCustomEditor(requiredType, propertyPath, propertyEditor);
   }

   public void setPropertyValue(String propertyName, Object value) throws BeansException {

      if (isNestedProperty(propertyName)) {
         BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
         nestedBw.setPropertyValue(getFinalPath(propertyName), value);
         return;
      }
      else {
         super.setPropertyValue(propertyName, value);
      }
   }

   /**
    * Is the property nested? That is, does it contain the nested
    * property separator (usually ".").
    *
    * @param path property path
    * @return boolean is the property nested
    */
   protected boolean isNestedProperty(String path) {
      return path.indexOf(NESTED_PROPERTY_SEPARATOR) != -1;
   }

   /**
    * Get the last component of the path. Also works if not nested.
    *
    * @param nestedPath property path we know is nested
    * @return last component of the path (the property on the target bean)
    */
   protected String getFinalPath(String nestedPath) {
      String finalPath = nestedPath.substring(nestedPath.lastIndexOf(NESTED_PROPERTY_SEPARATOR) + 1);
      if (logger.isDebugEnabled() && !nestedPath.equals(finalPath)) {
         logger.debug("Final path in nested property value '" + nestedPath + "' is '" + finalPath + "'");
      }
      return finalPath;
   }

   public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
      if (propertyPath != null && isNestedProperty(propertyPath)) {
         BeanWrapperImpl bw = getBeanWrapperForPropertyPath(propertyPath);
         return bw.findCustomEditor(requiredType, getFinalPath(propertyPath));
      }
      else {
         return super.findCustomEditor(requiredType, propertyPath);
      }
   }

   private class CustomEditorHolder {
      public Class requiredType;
      public String propertyPath;
      public PropertyEditor propertyEditor;
   }

}
