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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/StructuredArtifact.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.shared.model;

import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

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
      } else if (id != null) {
         this.id = getIdManager().getId(id.toString());
      } else {
         this.id = null;
      }
   }

   public Object get(Object key) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         return getId();
      } else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return getDisplayName();
      } else {
         return super.get(key);
      }
   }

   public Object put(Object key, Object value) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         setId(value);
         return null;
      } else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         setDisplayName((String) value);
         return null;
      } else {
         return super.put(key, value);
      }
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof StructuredArtifact)) return false;
      StructuredArtifact in = (StructuredArtifact) other;
      return getId().equals(in.getId());
   }

   public int hashCode() {
      if (id == null) return 0;
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
      } else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return String.class;
      } else {
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
