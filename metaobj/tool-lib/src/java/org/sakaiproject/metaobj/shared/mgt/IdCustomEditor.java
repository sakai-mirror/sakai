/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;

import java.beans.PropertyEditorSupport;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 2:23:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {

   private IdManager idManager = null;


   /**
    * Parse the Date from the given text, using the specified DateFormat.
    */
   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(null);
      }
      else {
         setValue(getIdManager().getId(text));
      }
   }

   /**
    * Format the Date as String, using the specified DateFormat.
    */
   public String getAsText() {
      if (!(getValue() instanceof Id)) {
         return null;
      }
      Id value = (Id) getValue();
      return value.getValue();
   }


   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public Class getType() {
      return Id.class;
   }
}
