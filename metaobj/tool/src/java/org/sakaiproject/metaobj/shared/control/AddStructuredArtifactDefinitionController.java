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
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/AddStructuredArtifactDefinitionController.java,v 1.5 2005/07/05 20:17:50 jellis Exp $
 * $Revision: 1.5 $
 * $Date: 2005/07/05 20:17:50 $
 */


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
