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
package org.sakaiproject.metaobj.shared.model;

import java.text.MessageFormat;

public class PersistenceException extends OspException {

   private String errorCode;
   private Object[] errorInfo;
   private String field;


   public PersistenceException(String errorCode, Object[] errorInfo,
                               String field) {
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(String message, String errorCode,
                               Object[] errorInfo, String field) {
      super(message);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(String message, Throwable cause,
                               String errorCode, Object[] errorInfo, String field) {
      super(message, cause);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }

   public PersistenceException(Throwable cause, String errorCode,
                               Object[] errorInfo, String field) {
      super(cause);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.field = field;
   }


   public String getErrorCode() {
      return errorCode;
   }

   public Object[] getErrorInfo() {
      return errorInfo;
   }

   public String getField() {
      return field;
   }

   public String getDefaultMessage() {
      return MessageFormat.format(getErrorCode(), getErrorInfo());
   }
}
