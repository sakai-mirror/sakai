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

import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

public class PublishStructuredArtifactDefinitionController extends AbstractStructuredArtifactDefinitionController implements LoadObjectController {
   public final static String SITE_PUBLISH_ACTION = "site_publish";
   public final static String GLOBAL_PUBLISH_ACTION = "global_publish";
   public final static String SUGGEST_GLOBAL_PUBLISH_ACTION = "suggest_global_publish";

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;
      if (sad.getAction().equals(SITE_PUBLISH_ACTION)) {
         sad.setSiteState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
         checkPermission(SharedFunctionConstants.PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }
      if (sad.getAction().equals(GLOBAL_PUBLISH_ACTION)) {
         sad.setGlobalState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
         sad.setSiteId(null);
         checkPermission(SharedFunctionConstants.PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }
      if (sad.getAction().equals(SUGGEST_GLOBAL_PUBLISH_ACTION)) {
         sad.setGlobalState(StructuredArtifactDefinitionBean.STATE_WAITING_APPROVAL);
         checkPermission(SharedFunctionConstants.SUGGEST_GLOBAL_PUBLISH_ARTIFACT_DEF);
         try {
            getStructuredArtifactDefinitionManager().save(sad);
         }
         catch (PersistenceException e) {
            errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
                  e.getDefaultMessage());
         }
      }

      return prepareListView(request, sad.getId().getValue());
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      return prepareListView(request, null);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) incomingModel;
      String action = sad.getAction();
      sad = getStructuredArtifactDefinitionManager().loadHome(sad.getId());
      sad.setAction(action);
      return sad;
   }

}
