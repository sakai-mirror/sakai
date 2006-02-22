/**********************************************************************************
 * $URL$
 * $Id$
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
package org.sakaiproject.metaobj.shared.model;

import org.jdom.Element;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 19, 2004
 * Time: 2:14:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifact extends ElementBean implements Artifact {
   private Agent owner = null;
   private Id id = null;
   private StructuredArtifactHomeInterface home = null;
   private IdManager idManager = null;

   private static String ID_PARAMETER_NAME = "id";
   private static String ARTIFACT_ID_PARAMETER_NAME = "artifactId";
   private static String DISPLAY_NAME_PARAMETER_NAME = "displayName";
   private String displayName;

   public StructuredArtifact(String elementName, SchemaNode currentSchema) {
      super(elementName, currentSchema);
   }

   public StructuredArtifact() {
   }

   public StructuredArtifact(Element baseElement, SchemaNode currentSchema) {
      super(baseElement, currentSchema, true);
   }

   public Agent getOwner() {
      return owner;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public void setId(Object id) {
      if (id instanceof Id) {
         this.id = (Id) id;
      }
      else if (id != null) {
         this.id = getIdManager().getId(id.toString());
      }
      else {
         this.id = null;
      }
   }

   public Object get(Object key) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         return getId();
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return getDisplayName();
      }
      else {
         return super.get(key);
      }
   }

   public Object put(Object key, Object value) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         setId(value);
         return null;
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         setDisplayName((String) value);
         return null;
      }
      else {
         return super.put(key, value);
      }
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof StructuredArtifact)) {
         return false;
      }
      StructuredArtifact in = (StructuredArtifact) other;
      return getId().equals(in.getId());
   }

   public int hashCode() {
      if (id == null) {
         return 0;
      }
      return this.id.hashCode();
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome homeObject) {
      home = (StructuredArtifactHomeInterface) homeObject;
      this.setCurrentSchema(home.getSchema().getChild(home.getRootNode()));
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public Class getType(String key) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         return Id.class;
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return String.class;
      }
      else {
         return super.getType(key);
      }
   }

   public IdManager getIdManager() {
      if (idManager == null) {
         idManager = (IdManager) ComponentManager.getInstance().get("idManager");
      }
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String toString() {
      return "Artifact: id=" + getId();
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }
}
