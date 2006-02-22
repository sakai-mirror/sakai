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

import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 3:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactDefinitionValidator implements Validator {

   public static final String PICK_SCHEMA_ACTION = "pickSchema";
   public static final String PICK_TRANSFORM_ACTION = "pickTransform";

   public boolean supports(Class clazz) {
      return (StructuredArtifactDefinitionBean.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      if (obj instanceof StructuredArtifactDefinitionBean) {
         StructuredArtifactDefinitionBean artifactHome = (StructuredArtifactDefinitionBean) obj;

         if (PICK_SCHEMA_ACTION.equals(artifactHome.getFilePickerAction()) ||
               PICK_TRANSFORM_ACTION.equals(artifactHome.getFilePickerAction())) {
            return;
         }

         if ((artifactHome.getSchemaFile() == null ||
               artifactHome.getSchemaFile().getValue() == null ||
               artifactHome.getSchemaFile().getValue().length() == 0)
               && artifactHome.getSchema() == null) {
            errors.rejectValue("schemaFile", "errors.required", "required");
         }
         if (artifactHome.getDocumentRoot() == null ||
               artifactHome.getDocumentRoot().length() == 0) {
            errors.rejectValue("documentRoot", "errors.required", "required");
         }
         if (artifactHome.getType() == null ||
               artifactHome.getType().getDescription() == null ||
               artifactHome.getType().getDescription().length() == 0) {
            errors.rejectValue("description", "errors.required", "required");
         }
      }
   }
}
