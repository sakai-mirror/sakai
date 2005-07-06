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
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/EditStructuredArtifactDefinitionController.java,v 1.4 2005/06/30 17:34:21 chmaurer Exp $
 * $Revision: 1.4 $
 * $Date: 2005/06/30 17:34:21 $
 */


package org.sakaiproject.metaobj.shared.control;

import org.springframework.validation.Errors;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
//import org.sakaiproject.metaobj.shared.mgt.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import java.util.Map;

public class EditStructuredArtifactDefinitionController extends AddStructuredArtifactDefinitionController implements LoadObjectController{
   //private ArtifactFinderManager artifactFinderManager;

//TODO removed reference to ArtifactFinderManager
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      StructuredArtifactDefinitionBean home = (StructuredArtifactDefinitionBean) incomingModel;
      home = getStructuredArtifactDefinitionManager().loadHome(home.getId());
      return home;
   }

   protected void save(StructuredArtifactDefinitionBean sad, Errors errors){
      //check to see if you have edit permissions
      checkPermission(SharedFunctionConstants.EDIT_ARTIFACT_DEF);

      //TODO verify user is system admin, if editting global SAD

      /*
      todo this should all be done on the server
      // check only if new xsd has been submitted
      if (sad.getSchemaFile() != null){

         String type = sad.getType().getId().getValue();
         ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(type);
         Collection artifacts = artifactFinder.findByType(type);
         StructuredArtifactValidator validator = new StructuredArtifactValidator();

         // validate every artifact against new xsd to determine
         // whether or not an xsl conversion file is necessary
         for (Iterator i = artifacts.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (obj instanceof StructuredArtifact) {
               StructuredArtifact artifact = (StructuredArtifact)obj;
               artifact.setHome(sad);
               Errors artifactErrors = new BindExceptionBase(artifact, "bean");
               validator.validate(artifact, artifactErrors);
               if (artifactErrors.getErrorCount() > 0){
                  if (sad.getXslConversionFileId() == null ||
                        sad.getXslConversionFileId().getValue().length() == 0) {

                     errors.rejectValue("xslConversionFileId",
                           "xsl file required to convert existing artifacts",
                           "xsl file required to convert existing artifacts");

                     for (Iterator x=artifactErrors.getAllErrors().iterator();x.hasNext();){
                        ObjectError error = (ObjectError) x.next();
                        logger.error(error.toString());
                        errors.rejectValue("xslConversionFileId",error.toString(),error.toString());
                     }

                     return;

                  } else {
                     sad.setRequiresXslFile(true);
                     break;
                  }
               }
            }
         }
      }
      */

      try{
      	getStructuredArtifactDefinitionManager().save(sad);
      } catch (PersistenceException e){
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      }
   }

   //public ArtifactFinderManager getArtifactFinderManager() {
   //   return artifactFinderManager;
   //}

   //public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
   //   this.artifactFinderManager = artifactFinderManager;
   //}
}
