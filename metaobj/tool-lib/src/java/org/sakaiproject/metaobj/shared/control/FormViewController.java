/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.control;

import org.jdom.Document;
import org.jdom.Element;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.service.legacy.filepicker.ResourceEditingHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 7, 2006
 * Time: 11:34:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormViewController implements Controller {

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      String formId = (String) session.get(ResourceEditingHelper.ATTACHMENT_ID);

      Tool tool = ToolManager.getCurrentTool();
      String url = (String) session.get(tool.getId() + Tool.HELPER_DONE_URL);
      session.remove(tool.getId() + Tool.HELPER_DONE_URL);

      Element root = getStructuredArtifactDefinitionManager().createFormViewXml(formId, url);

      if (session.get(ResourceEditingHelper.CUSTOM_CSS) != null) {
         Element uri = new Element("uri");
         uri.setText((String) session.get(ResourceEditingHelper.CUSTOM_CSS));
         root.getChild("css").addContent(uri);
      }


      Document formDoc = new Document(root);
      return new ModelAndView("success", XsltView.VIEW_DOCUMENT, formDoc);
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
}
