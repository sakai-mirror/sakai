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
package org.sakaiproject.metaobj.shared.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.mgt.HttpAccessBase;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.service.legacy.entity.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 1:25:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaobjHttpAccess extends HttpAccessBase {

   protected final transient Log logger = LogFactory.getLog(getClass());

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private String xsltLocation = "/org/sakaiproject/metaobj/shared/control/formView.xslt";
   private Templates templates;

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
                            Collection copyrightAcceptedRefs) throws PermissionException, IdUnusedException,
         ServerOverloadException, CopyrightException {
      res.setContentType("text/html; charset=UTF-8");
      res.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      res.addDateHeader("Last-Modified", System.currentTimeMillis());
      res.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      res.addHeader("Pragma", "no-cache");

      Element root = getStructuredArtifactDefinitionManager().createFormViewXml(ref.getEntity().getId(), null);
      Document doc = new Document(root);

      try {
         Transformer transformer = templates.newTransformer();
         transformer.clearParameters();
         transformer.transform(new JDOMSource(doc), new StreamResult(res.getOutputStream()));
      }
      catch (TransformerException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   protected void checkSource(Reference ref, ReferenceParser parser)
         throws PermissionException, IdUnusedException, ServerOverloadException, CopyrightException {

   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public void init() {

      try {
         URL url = getClass().getResource(getXsltLocation());
         String urlPath = url.toString();
         String systemId = urlPath.substring(0, urlPath.lastIndexOf('/') + 1);

         templates = TransformerFactory.newInstance().newTemplates(new StreamSource(url.openStream(), systemId));
      }
      catch (TransformerConfigurationException e) {
         logger.error("org.sakaiproject.metaobj.shared.mgt.impl.MetaobjHttpAccess:init", e);
      }
      catch (IOException e) {
         logger.error("org.sakaiproject.metaobj.shared.mgt.impl.MetaobjHttpAccess:init", e);
      }

   }

   public String getXsltLocation() {
      return xsltLocation;
   }

   public void setXsltLocation(String xsltLocation) {
      this.xsltLocation = xsltLocation;
   }
}
