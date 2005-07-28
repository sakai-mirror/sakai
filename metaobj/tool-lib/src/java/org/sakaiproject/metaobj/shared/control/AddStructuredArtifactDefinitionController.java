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

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;

import java.util.Map;

/**
 * @author chmaurer
 */
public class AddStructuredArtifactDefinitionController extends AbstractStructuredArtifactDefinitionController
      implements CustomCommandController, FormController {

   public Object formBackingObject(Map request, Map session, Map application) {

      //check to see if you have create permissions
      checkPermission(SharedFunctionConstants.CREATE_ARTIFACT_DEF);

      StructuredArtifactDefinitionBean backingObject = new StructuredArtifactDefinitionBean();
      backingObject.setOwner(getAuthManager().getAgent());
      return backingObject;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;

      String action = "";
      Object actionObj = request.get("action");
      if (actionObj instanceof String) {
         action = (String)actionObj;
      }
      else if (actionObj instanceof String[]) {
         action = ((String[])actionObj)[0];
      }

      if (request.get("systemOnly") == null) {
         sad.setSystemOnly(false);
      }

      if (sad.getSchemaFile() != null){
         try {
            getStructuredArtifactDefinitionManager().validateSchema(sad);
         } catch (Exception e) {
            logger.warn("", e);
            String errorMessage = "error reading schema file: " + e.getMessage();
            sad.setSchemaFile(null);
            errors.rejectValue("schemaFile", errorMessage, errorMessage);
            return new ModelAndView("failure");
         }
      }

      if (action.equals("schema")) {
         return new ModelAndView("failure");
      }

      try {
         if (!getStructuredArtifactDefinitionManager().isGlobal()){
            sad.setSiteId(getWorksiteManager().getCurrentWorksiteId().getValue());
         }

         save(sad, errors);
      } catch (AuthorizationFailedException e){
         throw e;
      } catch (PersistenceException e){
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      } catch (Exception e) {
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
      if (sad.getSchemaFile() != null){
         base.put("elements", getStructuredArtifactDefinitionManager().getRootElements(sad));
      }
      return base;
   }



}
