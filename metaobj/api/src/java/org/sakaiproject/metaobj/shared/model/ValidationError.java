package org.sakaiproject.metaobj.shared.model;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 2:51:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This object contains information reguarding an error on an individual field
 * that occurs during validation
 *
 * @see ElementBean
 */
public class ValidationError {

   private String fieldName;
   private String errorCode;
   private Object[] errorInfo;
   private String defaultMessage;

   /**
    * Construct a ValidationError with all the required parameters
    *
    * @param fieldName      the name of the field within this element.  if the field
    *                       is from a nested element, the parent field name will be prepended with a "."
    * @param errorCode      Code that is suitable for dereferencing into a properties file for
    *                       i8n purposes.  errorCode will contain the proper formatting for use as a default message.
    *                       for instance, "Value {1} for field {0} must match pattern {2}".  With the error info, this could be
    *                       used by a message formater.
    * @param errorInfo      an array of information related to the error.
    * @param defaultMessage the fields applied to the error code.
    */
   public ValidationError(String fieldName, String errorCode, Object[] errorInfo, String defaultMessage) {
      this.fieldName = fieldName;
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
      this.defaultMessage = defaultMessage;
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public String getErrorCode() {
      return errorCode;
   }

   public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
   }

   public Object[] getErrorInfo() {
      return errorInfo;
   }

   public void setErrorInfo(Object[] errorInfo) {
      this.errorInfo = errorInfo;
   }

   public String getDefaultMessage() {
      return defaultMessage;
   }

   public void setDefaultMessage(String defaultMessage) {
      this.defaultMessage = defaultMessage;
   }
}
