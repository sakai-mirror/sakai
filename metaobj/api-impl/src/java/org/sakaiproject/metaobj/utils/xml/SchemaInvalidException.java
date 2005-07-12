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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/SchemaInvalidException.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.utils.xml;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 12:24:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaInvalidException extends RuntimeException {

   /**
    * Constructs a new runtime exception with <code>null</code> as its
    * detail message.  The cause is not initialized, and may subsequently be
    * initialized by a call to {@link #initCause}.
    */
   public SchemaInvalidException() {
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
   public SchemaInvalidException(String message) {
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
   public SchemaInvalidException(String message, Throwable cause) {
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
   public SchemaInvalidException(Throwable cause) {
      super(cause);
   }


}
