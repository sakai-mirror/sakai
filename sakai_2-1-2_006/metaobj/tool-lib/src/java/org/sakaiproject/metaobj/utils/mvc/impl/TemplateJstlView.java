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
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.sakaiproject.metaobj.utils.mvc.intf.CommonModelController;
import org.sakaiproject.util.java.ResourceLoader;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 30, 2004
 * Time: 8:29:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateJstlView extends JstlView {

   private String rightMenu = null;
   private String leftMenu = null;
   private String header = null;
   private String footer = null;
   private String body = null;
   private String template = null;
   private String title = null;

   private String defaultTemplateDefName = "defaultTemplateDef";
   private String commonModelControllerName = "commonModelController";

   /**
    * Prepares the view given the specified model, merging it with static
    * attributes and a RequestContext attribute, if necessary.
    * Delegates to renderMergedOutputModel for the actual rendering.
    *
    * @see #renderMergedOutputModel
    */
   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

      // Added to be able to conform to the locale in JSTL jsp's
      ResourceLoader rb = new ResourceLoader();
      model.put("locale", rb.getLocale().toString());

      SimpleBeanWrapper mapWrapper = (SimpleBeanWrapper)
            getWebApplicationContext().getBean(defaultTemplateDefName);

      Map defaultTemplateDef = (Map) mapWrapper.getWrappedBean();

      addComponent("_rightMenu", rightMenu, request, defaultTemplateDef);
      addComponent("_leftMenu", leftMenu, request, defaultTemplateDef);
      addComponent("_header", header, request, defaultTemplateDef);
      addComponent("_footer", footer, request, defaultTemplateDef);
      addComponent("_body", body, request, defaultTemplateDef);
      addComponent("_title", title, request, defaultTemplateDef);
      template = addComponent("_template", template, request, defaultTemplateDef);

      CommonModelController controller =
            (CommonModelController) getWebApplicationContext().getBean(commonModelControllerName);

      controller.fillModel(model, request, response);

      this.setUrl(template);

      super.render(model, request, response);

   }

   protected String addComponent(String menuTag, String menuName, HttpServletRequest request, Map defaultTemplateDef) {

      if (menuName == null) {
         menuName = (String) defaultTemplateDef.get(menuTag);
      }

      if (menuName != null) {
         request.setAttribute(menuTag, menuName);
      }

      return menuName;
   }


   public String getRightMenu() {
      return rightMenu;
   }

   public void setRightMenu(String rightMenu) {
      this.rightMenu = rightMenu;
   }

   public String getLeftMenu() {
      return leftMenu;
   }

   public void setLeftMenu(String leftMenu) {
      this.leftMenu = leftMenu;
   }

   public String getHeader() {
      return header;
   }

   public void setHeader(String header) {
      this.header = header;
   }

   public String getFooter() {
      return footer;
   }

   public void setFooter(String footer) {
      this.footer = footer;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public String getDefaultTemplateDefName() {
      return defaultTemplateDefName;
   }

   public void setDefaultTemplateDefName(String defaultTemplateDefName) {
      this.defaultTemplateDefName = defaultTemplateDefName;
   }

   public String getTemplate() {
      return template;
   }

   public void setTemplate(String template) {
      this.template = template;
   }

   public String getCommonModelControllerName() {
      return commonModelControllerName;
   }

   public void setCommonModelControllerName(String commonModelControllerName) {
      this.commonModelControllerName = commonModelControllerName;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

}
