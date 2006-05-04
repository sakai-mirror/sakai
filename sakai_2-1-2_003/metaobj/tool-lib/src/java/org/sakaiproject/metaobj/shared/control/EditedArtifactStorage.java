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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

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
      setCurrentElement((ElementBean) elementBeanStack.peek());
      setCurrentSchemaNode(getCurrentElement().getCurrentSchema());
   }

   public void popCurrentPath() {
      pathStack.pop();
      setCurrentPath((String) pathStack.peek());
   }
}
