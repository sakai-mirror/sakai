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

import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 11:11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Type implements Serializable {

   private Id id;
   private String description;
   private boolean systemOnly = false;

   public Type() {
   }

   public Type(Id id) {
      setId(id);
   }

   public Type(Id id, String description) {
      setDescription(description);
      setId(id);
   }

   public Type(Id id, String description, boolean systemOnly) {
      setId(id);
      this.description = description;
      this.systemOnly = systemOnly;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
      if (getDescription() == null) {
         setDescription(id.getValue());
      }
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isSystemOnly() {
      return systemOnly;
   }

   public void setSystemOnly(boolean systemOnly) {
      this.systemOnly = systemOnly;
   }

}
