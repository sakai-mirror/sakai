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

import org.springframework.beans.BeanWrapper;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Map;

public class BindExceptionBase extends BindException {

   private BeanWrapper wrapper = null;

   public BindExceptionBase(Object target, String name) {
      super(target, name);
   }

   protected BeanWrapper getBeanWrapper() {
      if (wrapper == null) {
         if (getTarget() instanceof Map) {
            wrapper = new MapWrapper(getTarget());
         }
         else {
            wrapper = new MixedBeanWrapper(getTarget());
         }
      }
      return wrapper;
   }

   public String[] resolveMessageCodes(String errorCode, String field) {
      String fixedField = fixedField(field);
      Class fieldType = this.getBeanWrapper().getPropertyType(fixedField);
      if (fieldType == null) {
         fieldType = String.class;
      }
      return this.getMessageCodesResolver().resolveMessageCodes(errorCode,
            this.getObjectName(), fixedField, fieldType);
   }

   public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
      String fixedField = fixedField(field);
      Object newVal = getBeanWrapper().getPropertyValue(fixedField);
      if (newVal == null) {
         newVal = "";
      }
      FieldError fe = new FieldError(this.getObjectName(), fixedField, newVal, false,
            resolveMessageCodes(errorCode, field), errorArgs, defaultMessage);
      addError(fe);
   }


}
