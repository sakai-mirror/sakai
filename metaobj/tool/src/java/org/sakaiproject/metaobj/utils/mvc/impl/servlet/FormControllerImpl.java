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
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/servlet/FormControllerImpl.java,v 1.3 2005/06/30 20:32:40 jellis Exp $
 * $Revision: 1.3 $
 * $Date: 2005/06/30 20:32:40 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl.servlet;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.sakaiproject.metaobj.utils.mvc.impl.HttpServletHelper;
import org.sakaiproject.metaobj.utils.mvc.impl.ControllerFilterManager;
import org.sakaiproject.metaobj.utils.mvc.intf.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 25, 2004
 * Time: 3:45:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormControllerImpl extends SimpleFormController {

   private Controller controller = null;
   private Map screenMappings = null;
   private ServletRequestDataBinder servletRequestMapDataBinder = null;
   private String homeName;
   private List customTypedEditors = new ArrayList();
   private String formMethod;
   private String[] requiredFields = null;
   private Collection filters;

   protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                   Object command, BindException errors) throws Exception {

      Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
      Map session = HttpServletHelper.getInstance().createSessionMap(request);
      Map application = HttpServletHelper.getInstance().createApplicationMap(request);

      ModelAndView returnedMv;

      if (controller instanceof CancelableController &&
              ((CancelableController) controller).isCancel(requestMap)){
         returnedMv = ((CancelableController) controller).processCancel(requestMap, session,
            application, command, errors);
      } else {
         returnedMv = controller.handleRequest(command, requestMap, session, application, errors);
      }

      if (errors.hasErrors()) {
         logger.debug("Form submission errors: " + errors.getErrorCount());
         HttpServletHelper.getInstance().reloadApplicationMap(request, application);
         HttpServletHelper.getInstance().reloadSessionMap(request, session);
         HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
         return showForm(request, response, errors);
      }

      if (returnedMv.getViewName() != null) {
         // should get from mappings
         String mappedView = (String) screenMappings.get(returnedMv.getViewName());

         if (mappedView == null) {
            mappedView = returnedMv.getViewName();
         }

         //getControllerFilterManager().processFilters(requestMap, session, application, returnedMv, mappedView);

         returnedMv =  new ModelAndView(mappedView, returnedMv.getModel());
      }

      HttpServletHelper.getInstance().reloadApplicationMap(request, application);
      HttpServletHelper.getInstance().reloadSessionMap(request, session);
      HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);

      return returnedMv;
   }

   protected Map referenceData(HttpServletRequest request, Object command, Errors errors) {
      if (getController() instanceof FormController){
         Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
         Map referenceData = ((FormController) getController()).referenceData(requestMap, command, errors);
         HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
         return referenceData;
      }
      return null;
   }

   protected boolean isFormSubmission(HttpServletRequest request) {
      if (getFormMethod() != null &&
         getFormMethod().equalsIgnoreCase("get") &&
         request.getMethod().equalsIgnoreCase("get")) {
         return true;
      }
      if (getFormMethod() != null &&
         getFormMethod().equalsIgnoreCase("post") &&
         request.getMethod().equalsIgnoreCase("post")) {
         return true;
      }
      return super.isFormSubmission(request);
   }

   protected Object formBackingObject(HttpServletRequest request) throws Exception {
      Map requestMap = HttpServletHelper.getInstance().createRequestMap(request);
      Map session = HttpServletHelper.getInstance().createSessionMap(request);
      Map application = HttpServletHelper.getInstance().createApplicationMap(request);

      Object lightObject = null;

      if (controller instanceof CustomCommandController) {
         lightObject = ((CustomCommandController)controller).formBackingObject(requestMap, session, application);
      } else {
         lightObject = super.formBackingObject(request);
      }

      Object returned = lightObject;

      if (controller instanceof LoadObjectController) {
         // need to bind variables to fill in lightweight object
         // then pass object to real control to fill in
         // this will get the info from the backing store
         ServletRequestDataBinder binder = createBinder(request, lightObject);
         binder.bind(request);

         returned = ((LoadObjectController) controller).fillBackingObject(lightObject, requestMap, session, application);
      }

         /*
      if (controller instanceof ContextAwareController){
         ((ContextAwareController) controller).addContexts(getHelpManager().getActiveContexts(session), requestMap, getFormView());
      } else {
         getHelpManager().addContexts(session, getFormView());
      }
           */
      //getControllerFilterManager().processFilters(requestMap, session, application, null, getFormView());

      HttpServletHelper.getInstance().reloadApplicationMap(request, application);
      HttpServletHelper.getInstance().reloadSessionMap(request, session);
      HttpServletHelper.getInstance().reloadRequestMap(request, requestMap);
      return returned;
   }


   protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
      ServletRequestDataBinder binder = null;
      binder = new ServletRequestBeanDataBinder(command, getCommandName());
      initBinder(request, binder);
      return binder;
   }

   /**
    * Set up a custom property editor for the application's date format.
    */
   protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      for (Iterator i = getCustomTypedEditors().iterator(); i.hasNext();) {
         TypedPropertyEditor editor = (TypedPropertyEditor) i.next();
         binder.registerCustomEditor(editor.getType(), editor);
      }

      if (getRequiredFields() != null) {
         binder.setRequiredFields(getRequiredFields());
      }
   }

   public Controller getController() {
      return controller;
   }

   public void setController(Controller controller) {
      this.controller = controller;
   }

   public Map getScreenMappings() {
      return screenMappings;
   }

   public void setScreenMappings(Map screenMappings) {
      this.screenMappings = screenMappings;
   }

   public ServletRequestDataBinder getServletRequestMapDataBinder() {
      return servletRequestMapDataBinder;
   }

   public void setServletRequestMapDataBinder(ServletRequestDataBinder servletRequestMapDataBinder) {
      this.servletRequestMapDataBinder = servletRequestMapDataBinder;
   }

   public String getHomeName() {
      return homeName;
   }

   public void setHomeName(String homeName) {
      this.homeName = homeName;
   }

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

   public String getFormMethod() {
      return formMethod;
   }

   public void setFormMethod(String formMethod) {
      this.formMethod = formMethod;
   }

   public String[] getRequiredFields() {
      return requiredFields;
   }

   public void setRequiredFields(String[] requiredFields) {
      this.requiredFields = requiredFields;
   }

   protected ControllerFilterManager getControllerFilterManager()  {
      return (ControllerFilterManager) ComponentManager.getInstance().get("controllerFilterManager");
   }
}
