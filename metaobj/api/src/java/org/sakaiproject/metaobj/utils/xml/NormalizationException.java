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
package org.sakaiproject.metaobj.utils.xml;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:28:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class NormalizationException extends RuntimeException {

   public static final String INVALID_LENGTH_ERROR_CODE = "Value {0} must be {1} characters";
   public static final String INVALID_LENGTH_TOO_LONG_ERROR_CODE = "Value {0} must be less than {1} characters";
   public static final String INVALID_LENGTH_TOO_SHORT_ERROR_CODE = "Value {0} must be at least {1} characters";
   public static final String INVALID_PATTERN_MATCH_ERROR_CODE = "Value {0} must match {1}";
   public static final String NOT_IN_ENUMERATION_ERROR_CODE = "Value {0} must be in enueration";

   public static final String DATE_TOO_LATE_ERROR_CODE = "Value {0} must be before {1}";
   public static final String DATE_TOO_EARLY_ERROR_CODE = "Value {0} must be after {1}";
   public static final String DATE_AFTER_ERROR_CODE = "Value {0} must be not be after {1}";
   public static final String DATE_BEFORE_ERROR_CODE = "Value {0} must be not be before {1}";
   public static final String DATE_INVALID_ERROR_CODE = "Value {0} must fit {1}";

   public static final String REQIRED_FIELD_ERROR_CODE = "Required value";

   public static final String TOO_LARGE_INCLUSIVE_ERROR_CODE = "Value {0} must be less than or equal to {1}";
   public static final String TOO_SMALL_INCLUSIVE_ERROR_CODE = "Value {0} must be more than or equal {1}";
   public static final String TOO_LARGE_ERROR_CODE = "Value {0} must be less than {1}";
   public static final String TOO_SMALL_ERROR_CODE = "Value {0} must be more than {1}";
   public static final String TOO_MANY_DIGITS_ERROR_CODE = "Value {0} must be less than {1} digits";
   public static final String INVALID_DECIMAL_NUMBER_ERROR_CODE = "Value {0} must be a decimal number";
   public static final String INVALID_NUMBER_ERROR_CODE = "Value {0} must be a number";

   public static final String INVALID_TYPE_ERROR_CODE = "Object {0} should be class {1}";

   public static final String INVALID_URI = "Value {0} is a malformed URI";

   private String errorCode;
   private Object[] errorInfo;

   /**
    * Constructs a new runtime exception with <code>null</code> as its
    * detail message.  The cause is not initialized, and may subsequently be
    * initialized by a call to {@link #initCause}.
    */
   public NormalizationException() {
      super();
   }

   /**
    * Constructs a new runtime exception with the specified detail message.
    * The cause is not initialized, and may subsequently be initialized by a
    * call to {@link #initCause}.
    *
    * @param message the detail message. The detail message is saved for
    *                later retrieval by the {@link #getMessage()} method.
    */
   public NormalizationException(String message) {
      super(message);
   }

   /**
    * Constructs a new runtime exception with the specified detail message and
    * cause.  <p>Note that the detail message associated with
    * <code>cause</code> is <i>not</i> automatically incorporated in
    * this runtime exception's detail message.
    *
    * @param message the detail message (which is saved for later retrieval
    *                by the {@link #getMessage()} method).
    * @param cause   the cause (which is saved for later retrieval by the
    *                {@link #getCause()} method).  (A <tt>null</tt> value is
    *                permitted, and indicates that the cause is nonexistent or
    *                unknown.)
    * @since 1.4
    */
   public NormalizationException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a new runtime exception with the specified cause and a
    * detail message of <tt>(cause==null ? null : cause.toString())</tt>
    * (which typically contains the class and detail message of
    * <tt>cause</tt>).  This constructor is useful for runtime exceptions
    * that are little more than wrappers for other throwables.
    *
    * @param cause the cause (which is saved for later retrieval by the
    *              {@link #getCause()} method).  (A <tt>null</tt> value is
    *              permitted, and indicates that the cause is nonexistent or
    *              unknown.)
    * @since 1.4
    */
   public NormalizationException(Throwable cause) {
      super(cause);
   }

   public NormalizationException(String message, String errorCode, Object[] errorInfo) {
      super(message);
      this.errorCode = errorCode;
      this.errorInfo = errorInfo;
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

}
