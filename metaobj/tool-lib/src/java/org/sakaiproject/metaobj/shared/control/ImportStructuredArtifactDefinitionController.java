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
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.metaobj.shared.model.FormUploadForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImportStructuredArtifactDefinitionController extends AddStructuredArtifactDefinitionController
      implements Controller, Validator {

	   private SessionManager sessionManager;
	   private ContentHostingService contentHosting = null;
	   private EntityManager entityManager;

	   public Object formBackingObject(Map request, Map session, Map application) {

		   FormUploadForm backingObject = new FormUploadForm();
	      return backingObject;
	   }
	/*
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      return prepareListView(request, null);
   }
*/
   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

	   FormUploadForm templateForm = (FormUploadForm)requestModel;
      if(templateForm == null)
    	  return new ModelAndView("success");
      if (templateForm.getSubmitAction() != null && templateForm.getSubmitAction().equals("pickImport")) {
         if (templateForm.getUploadedForm() != null && templateForm.getUploadedForm().length() > 0) {
            Reference ref;
            List files = new ArrayList();
            String ids[] = templateForm.getUploadedForm().split(",");
            for(int i = 0; i < ids.length; i++) {
	            try {
		                String id = ids[i];
		                id = getContentHosting().resolveUuid(id);
		                String rid = getContentHosting().getResource(id).getReference();
		            	ref = getEntityManager().newReference(rid);
		                files.add(ref);
	            } catch (PermissionException e) {
	               logger.error("", e);
	            } catch (IdUnusedException e) {
	               logger.error("", e);
	            } catch (TypeException e) {
	               logger.error("", e);
	            }
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         return new ModelAndView("pickImport");
      }
      else {
    	 String view = "success";
    	 if(templateForm.getUploadedForm().length() > 0) {
	         String ids[] = templateForm.getUploadedForm().split(",");
	         for(int i = 0; i < ids.length; i++) {
		        try {
	                String id = ids[i];
	                if(!getStructuredArtifactDefinitionManager().importSADResource(
	                		getWorksiteManager().getCurrentWorksiteId(), id)) {
	                    errors.rejectValue("uploadedForm", "error.format", "File format not recognized");
	                    
	                    view = "failed";
	                }
		        } catch (InvalidUploadException e) {
		           logger.warn("Failed uploading template", e);
		           errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                   view = "failed";
		        } catch (Exception e) {
		           logger.error("Failed importing template", e);
                   view = "failed";
		        }
	         }
    	 }
         Map model = new Hashtable();
    	 return new ModelAndView(view, model);
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
	   FormUploadForm templateForm = (FormUploadForm)command;
      Map model = new HashMap();
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() >= 1) {
        	String ids = "";
        	String names = "";
        	
        	for(Iterator iter = refs.iterator(); iter.hasNext(); ) {
	            Reference ref = (Reference)iter.next();
	    		String nodeId = getContentHosting().getUuid(ref.getId());
	    		String id = getContentHosting().resolveUuid(nodeId);
	    		
	    		ContentResource resource = null;
	    		try {
				resource = getContentHosting().getResource(id);
	    		} catch(PermissionException pe) {}
	    		catch(TypeException pe) {}
    			catch(IdUnusedException pe) {}
	    		
	            
	            if(ids.length() > 0)
	            	ids += ",";
	            ids += nodeId;
	            names += resource.getProperties().getProperty(
	                        resource.getProperties().getNamePropDisplayName()) + " ";
        	}
            templateForm.setUploadedForm(ids);
            model.put("name", names);
         }
         else {
            templateForm.setUploadedForm(null);
         }
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
              ComponentManager.get("org.sakaiproject.metaobj.shared.ContentResourceFilter.formUploadStyleFile"));
      return model;
   }

   public boolean supports(Class clazz) {
      return (FormUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
	   FormUploadForm templateForm = (FormUploadForm) obj;
      if (templateForm.getUploadedForm() == null && templateForm.isValidate()){
         errors.rejectValue("uploadedForm", "error.required", "required");
      }
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}


