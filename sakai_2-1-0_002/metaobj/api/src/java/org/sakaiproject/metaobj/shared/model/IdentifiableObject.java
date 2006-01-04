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

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: May 15, 2004
 * Time: 1:55:47 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class IdentifiableObject {
   private Id id;
   protected final Log logger = LogFactory.getLog(this.getClass());

   public boolean equals(Object in) {
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (!this.getClass().isAssignableFrom(in.getClass())) return false;
      if (this.getId() == null && ((IdentifiableObject) in).getId() == null ) return true;
      if (this.getId() == null || ((IdentifiableObject) in).getId() == null ) return false;
      return this.getId().equals(((IdentifiableObject) in).getId());
   }

   public int hashCode() {
      return (id != null ? id.hashCode() : 0);
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }
}
