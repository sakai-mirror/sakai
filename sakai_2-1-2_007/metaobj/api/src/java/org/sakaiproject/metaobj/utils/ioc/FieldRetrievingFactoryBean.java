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


import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

/**
 * FactoryBean which retrieves a static or non-static field value.
 * Typically used for retrieving public static final constants.
 * <p/>
 * <p>Usage example:
 * <p/>
 * <pre>
 * // standard definition for exposing a static field, specifying the "staticField" property
 * &lt;bean id="myField" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"&gt;
 *   &lt;property name="staticField"&gt;&lt;value&gt;java.sql.Connection.TRANSACTION_SERIALIZABLE&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * <p/>
 * // convenience version that specifies a static field pattern as bean name
 * &lt;bean id="java.sql.Connection.TRANSACTION_SERIALIZABLE" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"/&gt;</pre>
 * </pre>
 *
 * @author Juergen Hoeller
 * @see #setStaticField
 * @since 1.1
 */
public class FieldRetrievingFactoryBean implements FactoryBean, BeanNameAware, InitializingBean {

   private Class targetClass;

   private Object targetObject;

   private String targetField;

   private String staticField;

   private String beanName;

   // the field we will retrieve
   private Field fieldObject;


   /**
    * Set the target class on which the field is defined.
    * Only necessary when the target field is static; else,
    * a target object needs to be specified anyway.
    *
    * @see #setTargetObject
    * @see #setTargetField
    */
   public void setTargetClass(Class targetClass) {
      this.targetClass = targetClass;
   }

   /**
    * Return the target class on which the field is defined.
    */
   public Class getTargetClass() {
      return targetClass;
   }

   /**
    * Set the target object on which the field is defined.
    * Only necessary when the target field is not static;
    * else, a target class is sufficient.
    *
    * @see #setTargetClass
    * @see #setTargetField
    */
   public void setTargetObject(Object targetObject) {
      this.targetObject = targetObject;
   }

   /**
    * Return the target object on which the field is defined.
    */
   public Object getTargetObject() {
      return targetObject;
   }

   /**
    * Set the name of the field to be retrieved.
    * Refers to either a static field or a non-static field,
    * depending on a target object being set.
    *
    * @see #setTargetClass
    * @see #setTargetObject
    */
   public void setTargetField(String targetField) {
      this.targetField = targetField;
   }

   /**
    * Return the name of the field to be retrieved.
    */
   public String getTargetField() {
      return targetField;
   }

   /**
    * Set a fully qualified static field name to retrieve,
    * e.g. "example.MyExampleClass.MY_EXAMPLE_FIELD".
    * Convenient alternative to specifying targetClass and targetField.
    *
    * @see #setTargetClass
    * @see #setTargetField
    */
   public void setStaticField(String staticField) {
      this.staticField = staticField;
   }

   /**
    * The bean name of this FieldRetrievingFactoryBean will be interpreted
    * as "staticField" pattern, if neither "targetClass" nor "targetObject"
    * nor "targetField" have been specified.
    * This allows for concise bean definitions with just an id/name.
    */
   public void setBeanName(String beanName) {
      this.beanName = beanName;
   }


   public void afterPropertiesSet() throws ClassNotFoundException, NoSuchFieldException {
      if (this.targetClass != null && this.targetObject != null) {
         throw new IllegalArgumentException("Specify either targetClass or targetObject, not both");
      }

      if (this.targetClass == null && this.targetObject == null) {
         if (this.targetField != null) {
            throw new IllegalArgumentException("Specify targetClass or targetObject in combination with targetField");
         }

         // If no other property specified, consider bean name as static field expression.
         if (this.staticField == null) {
            this.staticField = this.beanName;
         }

         // try to parse static field into class and field
         int lastDotIndex = this.staticField.lastIndexOf('.');
         if (lastDotIndex == -1 || lastDotIndex == this.staticField.length()) {
            throw new IllegalArgumentException("staticField must be a fully qualified class plus method name: " +
                  "e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
         }
         String className = this.staticField.substring(0, lastDotIndex);
         String fieldName = this.staticField.substring(lastDotIndex + 1);
         this.targetClass = ClassUtils.forName(className);
         this.targetField = fieldName;
      }

      else if (this.targetField == null) {
         // either targetClass or targetObject specified
         throw new IllegalArgumentException("targetField is required");
      }

      // try to get the exact method first
      Class targetClass = (this.targetObject != null) ? this.targetObject.getClass() : this.targetClass;
      this.fieldObject = targetClass.getField(this.targetField);
   }


   public Object getObject() throws IllegalAccessException {
      if (this.targetObject != null) {
         // instance field
         return this.fieldObject.get(this.targetObject);
      }
      else {
         // class field
         return this.fieldObject.get(null);
      }
   }

   public Class getObjectType() {
      return this.fieldObject.getType();
   }

   public boolean isSingleton() {
      return true;
   }

}

