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

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author chmaurer
 */
public class AddStructuredArtifactDefinitionController extends AbstractStructuredArtifactDefinitionController
      implements CustomCommandController, FormController, LoadObjectController {

   protected static final String SAD_SESSION_TAG =
         "org.sakaiproject.metaobj.shared.control.AddStructuredArtifactDefinitionController.sad";
   private SessionManager sessionManager;

   public Object formBackingObject(Map request, Map session, Map application) {

      //check to see if you have create permissions
      checkPermission(SharedFunctionConstants.CREATE_ARTIFACT_DEF);

      StructuredArtifactDefinitionBean backingObject = new StructuredArtifactDefinitionBean();
      backingObject.setOwner(getAuthManager().getAgent());
      return backingObject;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(SAD_SESSION_TAG) != null) {
         return session.remove(SAD_SESSION_TAG);
      }
      else {
         return incomingModel;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;

      if (StructuredArtifactDefinitionValidator.PICK_SCHEMA_ACTION.equals(sad.getFilePickerAction()) ||
            StructuredArtifactDefinitionValidator.PICK_TRANSFORM_ACTION.equals(sad.getFilePickerAction())) {
         session.put(SAD_SESSION_TAG, sad);
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
               ComponentManager.get("org.sakaiproject.service.legacy.content.ContentResourceFilter.metaobjFile"));
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, new Integer(1));
         return new ModelAndView("pickSchema");
      }

      String action = "";
      Object actionObj = request.get("action");
      if (actionObj instanceof String) {
         action = (String) actionObj;
      }
      else if (actionObj instanceof String[]) {
         action = ((String[]) actionObj)[0];
      }

      if (request.get("systemOnly") == null) {
         sad.setSystemOnly(false);
      }

      if (sad.getSchemaFile() != null) {
         try {
            getStructuredArtifactDefinitionManager().validateSchema(sad);
         }
         catch (Exception e) {
            logger.warn("", e);
            String errorMessage = "error reading schema file: " + e.getMessage();
            sad.setSchemaFile(null);
            errors.rejectValue("schemaFile", errorMessage, errorMessage);
            return new ModelAndView("failure");
         }
      }

      try {
         if (!getStructuredArtifactDefinitionManager().isGlobal()) {
            sad.setSiteId(getWorksiteManager().getCurrentWorksiteId().getValue());
         }

         save(sad, errors);
      }
      catch (AuthorizationFailedException e) {
         throw e;
      }
      catch (PersistenceException e) {
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      }
      catch (Exception e) {
         logger.warn("", e);
         String errorMessage = "error transforming or saving artifacts: " + e.getMessage();
         errors.rejectValue("xslConversionFileId", errorMessage, errorMessage);
         sad.setXslConversionFileId(null);
         return new ModelAndView("failure");
      }

      if (errors.getErrorCount() > 0) {
         return new ModelAndView("failure");
      }

      return prepareListView(request, sad.getId().getValue());
   }

   protected void save(StructuredArtifactDefinitionBean sad, Errors errors) {
      //check to see if you have create permissions
      checkPermission(SharedFunctionConstants.CREATE_ARTIFACT_DEF);

      getStructuredArtifactDefinitionManager().save(sad);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map base = super.referenceData(request, command, errors);
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) command;

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List) session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Reference ref = (Reference) refs.get(0);

         if (StructuredArtifactDefinitionValidator.PICK_SCHEMA_ACTION.equals(sad.getFilePickerAction())) {
            sad.setSchemaFile(getIdManager().getId(ref.getId()));
            sad.setSchemaFileName(ref.getProperties().getProperty(ref.getProperties().getNamePropDisplayName()));
         }
         else if (StructuredArtifactDefinitionValidator.PICK_TRANSFORM_ACTION.equals(sad.getFilePickerAction())) {
            sad.setXslConversionFileId(getIdManager().getId(ref.getId()));
            sad.setXslFileName(ref.getProperties().getProperty(ref.getProperties().getNamePropDisplayName()));
         }
      }

      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      if (sad.getSchemaFile() != null) {
         try {
            base.put("elements", getStructuredArtifactDefinitionManager().getRootElements(sad));
         }
         catch (Exception e) {
            String errorMessage = "error reading schema file: " + e.getMessage();
            sad.setSchemaFile(null);
            sad.setSchemaFileName(null);
            errors.rejectValue("schemaFile", errorMessage, errorMessage);
         }
      }
      return base;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

}
