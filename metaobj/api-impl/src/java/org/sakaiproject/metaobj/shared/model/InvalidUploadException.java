/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/InvalidUploadException.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InvalidUploadException extends OspException {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String fieldName;

   /**
    *
    */
   public InvalidUploadException(String fieldName) {
      this.fieldName = fieldName;
   }

   /**
    * @param cause
    */
   public InvalidUploadException(Throwable cause, String fieldName) {
      super(cause);
      this.fieldName = fieldName;
   }

   /**
    * @param message
    */
   public InvalidUploadException(String message, String fieldName) {
      super(message);
      this.fieldName = fieldName;
   }

   /**
    * @param message
    * @param cause
    */
   public InvalidUploadException(String message, Throwable cause, String fieldName) {
      super(message, cause);
      this.fieldName = fieldName;
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }
}
