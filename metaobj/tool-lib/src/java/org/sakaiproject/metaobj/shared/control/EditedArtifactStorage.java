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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/EditedArtifactStorage.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.jdom.Element;

import java.util.Stack;

public class EditedArtifactStorage {
   protected final Log logger = LogFactory.getLog(getClass());

   public static final String STORED_ARTIFACT_FLAG =
      "org_theospi_storedArtifactCall";
   public static final String EDITED_ARTIFACT_STORAGE_SESSION_KEY =
      "org_theospi_editedArtifact";

   private SchemaNode rootSchemaNode = null;
   private SchemaNode currentSchemaNode = null;
   private StructuredArtifact rootArtifact = null;
   private ElementBean currentElement = null;
   private String currentPath = null;
   private ReadableObjectHome home;
   private Stack elementBeanStack = new Stack();
   private Stack pathStack = new Stack();
   private Element oldParentElement = null;

   public EditedArtifactStorage(SchemaNode rootSchemaNode, StructuredArtifact rootArtifact) {
      this.rootSchemaNode = rootSchemaNode;
      this.currentSchemaNode = rootSchemaNode;
      this.rootArtifact = rootArtifact;
      this.currentElement = rootArtifact;
      elementBeanStack.push(rootArtifact);
      pathStack.push("");
      setHome(rootArtifact.getHome());
   }

   public SchemaNode getRootSchemaNode() {
      return rootSchemaNode;
   }

   protected void setRootSchemaNode(SchemaNode rootSchemaNode) {
      this.rootSchemaNode = rootSchemaNode;
   }

   public SchemaNode getCurrentSchemaNode() {
      return currentSchemaNode;
   }

   protected void setCurrentSchemaNode(SchemaNode currentSchemaNode) {
      this.currentSchemaNode = currentSchemaNode;
   }

   public StructuredArtifact getRootArtifact() {
      return rootArtifact;
   }

   protected void setRootArtifact(StructuredArtifact rootArtifact) {
      this.rootArtifact = rootArtifact;
   }

   public ElementBean getCurrentElement() {
      return currentElement;
   }

   protected void setCurrentElement(ElementBean currentElement) {
      this.currentElement = currentElement;
   }

   public String getCurrentPath() {
      return currentPath;
   }

   protected void setCurrentPath(String currentPath) {
      this.currentPath = currentPath;
   }

   public void pushCurrentElement(ElementBean newBean) {
      oldParentElement = (Element) rootArtifact.getBaseElement().clone();
      setCurrentElement(newBean);
      elementBeanStack.push(newBean);
      setCurrentSchemaNode(newBean.getCurrentSchema());
   }

   public void pushCurrentPath(String s) {
      setCurrentPath(s);
      pathStack.push(s);
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   protected void setHome(ReadableObjectHome home) {
      this.home = home;
   }

   public void popCurrentElement() {
      popCurrentElement(false);
   }

   public void popCurrentElement(boolean cancel) {
      if (cancel) {
         rootArtifact.setBaseElement(oldParentElement);
      }
      elementBeanStack.pop();
      setCurrentElement((ElementBean)elementBeanStack.peek());
      setCurrentSchemaNode(getCurrentElement().getCurrentSchema());
   }

   public void popCurrentPath() {
      pathStack.pop();
      setCurrentPath((String)pathStack.peek());
   }
}
