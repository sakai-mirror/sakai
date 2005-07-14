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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/TemplateJstlView.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.springframework.web.servlet.view.JstlView;
import org.sakaiproject.metaobj.utils.mvc.intf.CommonModelController;

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
