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

public class OspException
      extends RuntimeException {
   public static final String CREATE_FAILED = "Create failed ";
   public static final String CREATE_STREAM_FAILED = "Create stream failed ";
   public static final String DELETE_FAILED = "Delete failed";
   public static final String MOVE_FAILED = "Move failed ";
   public static final String RENAME_FAILED = "Rename failed ";
   public static final String WRITE_STREAM_FAILED = "Create stream failed ";

   /**
    *
    */
   public OspException() {
      super();
   }

   /**
    * @param cause
    */
   public OspException(Throwable cause) {
      super(cause);
   }

   /**
    * @param message
    */
   public OspException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public OspException(String message, Throwable cause) {
      super(message, cause);
   }

}

