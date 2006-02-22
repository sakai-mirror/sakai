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
      id = (String) in.readObject();
   }

   public String getValue() {
      return id;
   }

   public String toString() {
      return getValue();
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof IdImpl)) {
         return false;
      }
      if (!(other instanceof Id)) {
         return false;
      }
      return getValue().equals(((IdImpl) other).getValue());

   }


   public void setValue(String id) {
      this.id = id;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      if (id == null) {
         return 0;
      }
      return this.id.hashCode();
   }
}
