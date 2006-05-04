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
package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.springframework.validation.Errors;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 3:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactValidator extends XmlValidator {


   public boolean supports(Class clazz) {
      if (super.supports(clazz)) {
         return true;
      }
      //return (StructuredArtifact.class.isAssignableFrom(clazz));
      return true;
   }

   public void validate(Object obj, Errors errors) {
      validateInternal(obj, errors);
      super.validate(obj, errors);
   }

   protected void validateInternal(Object obj, Errors errors) {
//      if (obj instanceof StructuredArtifact) {
//         StructuredArtifact artifact = (StructuredArtifact) obj;
//
//         if (artifact.getDisplayName() == null ||
//            artifact.getDisplayName().length() == 0) {
//            errors.rejectValue("displayName", "required value {0}", new Object[]{"displayName"},
//               "required value displayName");
//         }
//      }
   }

   public void validate(Object obj, Errors errors, boolean checkListNumbers) {
      validateInternal(obj, errors);
      super.validate(obj, errors, checkListNumbers);
   }

   protected void validateDisplayName(ElementBean elementBean, Errors errors) {
//      if (elementBean instanceof StructuredArtifact) {
//
//         String displayName = (String)elementBean.get("displayName");
//
//         if (getFileNameValidator() != null && displayName != null) {
//            if (!getFileNameValidator().validFileName(displayName)) {
//               errors.rejectValue("displayName", "Invalid display name {0}",
//                  new Object[]{displayName},
//                  MessageFormat.format("Invalid display name {0}", new Object[]{displayName}));
//            }
//         }
//      }
   }
}
