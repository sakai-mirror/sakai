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
 * $URL$
 * $Revision$
 * $Date$
 */
/*
 * Created on May 4, 2004
 */
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author rpembry
 */
public class IdImpl implements Id {
   static final long serialVersionUID = 5143985783577804880L;

   protected final transient Log logger = LogFactory.getLog(IdImpl.class);

   private String id;
   //TODO: support Type better
   private transient Type type;

   public IdImpl() {
   }

   public IdImpl(String id, Type type) {
      this.id = id;
      this.type = type;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.model.Id#getType()
    */
   public Type getType() {
      return type;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeObject(id);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      id = (String)in.readObject();   
   }

   public String getValue() {
      return id;
   }

   public String toString() {
      return getValue();
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof IdImpl)) return false;
      if (!(other instanceof Id)) return false;
      return getValue().equals(((IdImpl) other).getValue());

   }


   public void setValue(String id) {
      this.id = id;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      if (id == null) return 0;
      return this.id.hashCode();
   }
}
