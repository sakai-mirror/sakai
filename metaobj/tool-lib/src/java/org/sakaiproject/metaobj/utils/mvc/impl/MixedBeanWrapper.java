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

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 3:14:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MixedBeanWrapper extends BeanWrapperBase {
   public MixedBeanWrapper() {
   }

   /**
    * Create new BeanWrapperImpl for the given object.
    *
    * @param object object wrapped by this BeanWrapper.
    * @throws org.springframework.beans.BeansException
    *          if the object cannot be wrapped by a BeanWrapper
    */
   public MixedBeanWrapper(Object object) throws BeansException {
      super(object);
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
   public MixedBeanWrapper(Object object, String nestedPath, Object rootObject) throws BeansException {
      super(object, nestedPath, rootObject);
   }

   protected BeanWrapperImpl createNestedWrapper(String parentPath, String nestedProperty) {
      Class type = getPropertyType(nestedProperty);

      if (java.util.Map.class.isAssignableFrom(type)) {
         return new MapWrapper((Map) getPropertyValue(nestedProperty),
               parentPath + NESTED_PROPERTY_SEPARATOR + nestedProperty, getWrappedInstance());
      }
      else {
         return super.createNestedWrapper(parentPath, nestedProperty);
      }
   }

   protected BeanWrapperBase constructWrapper(Object propertyValue, String nestedProperty) {
      return new MixedBeanWrapper(propertyValue, nestedProperty, getWrappedInstance());
   }

}
