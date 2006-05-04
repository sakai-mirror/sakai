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
package org.sakaiproject.metaobj.shared.mgt.impl;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.time.Time;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Sep 14, 2005
 * Time: 10:08:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResourceHome implements ReadableObjectHome, PresentableObjectHome {

   private HomeFactory homeFactory;
   private IdManager idManager;

   public Type getType() {
      return new Type(getIdManager().getId("fileArtifact"), "Uploaded File");
   }

   public String getExternalType() {
      return "fileArtifact";
   }

   public Artifact load(Id id) throws PersistenceException {
      throw new UnsupportedOperationException("not implemented");
   }

   public Artifact createInstance() {
      throw new UnsupportedOperationException("not implemented");
   }

   public void prepareInstance(Artifact object) {
      throw new UnsupportedOperationException("not implemented");
   }

   public Artifact createSample() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      throw new UnsupportedOperationException("not implemented");
   }

   public boolean isInstance(Artifact testObject) {
      throw new UnsupportedOperationException("not implemented");
   }

   public void refresh() {
      throw new UnsupportedOperationException("not implemented");
   }

   public String getExternalUri(Id artifactId, String name) {
      throw new UnsupportedOperationException("not implemented");
   }

   public InputStream getStream(Id artifactId) {
      throw new UnsupportedOperationException("not implemented");
   }

   public boolean isSystemOnly() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Class getInterface() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Element getArtifactAsXml(Artifact art) {
      ContentResourceArtifact artifact = (ContentResourceArtifact) art;
      Element root = new Element("artifact");

      root.addContent(getMetadata(artifact));

      String type = artifact.getBase().getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);

      if (type == null) {
         addFileContent(artifact, root);
      }
      else {
         addStructuredObjectContent(type, artifact, root);
      }

      return root;
   }

   protected void addStructuredObjectContent(String type, ContentResourceArtifact artifact, Element root) {
      Element data = new Element("structuredData");
      Element baseElement = null;

      byte[] bytes = null;
      try {
         bytes = artifact.getBase().getContent();
      }
      catch (ServerOverloadException e) {
         throw new RuntimeException(e);
      }
      SAXBuilder builder = new SAXBuilder();
      Document doc = null;

      try {
         doc = builder.build(new ByteArrayInputStream(bytes));
      }
      catch (JDOMException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

      baseElement = (Element) doc.getRootElement().detach();

      data.addContent(baseElement);
      root.addContent(data);

      StructuredArtifactHomeInterface home = (StructuredArtifactHomeInterface) getHomeFactory().getHome(type);

      Element schemaData = new Element("schema");
      schemaData.addContent(createInstructions(home));
      schemaData.addContent(addAnnotations(home.getRootSchema()));
      root.addContent(schemaData);
   }

   protected Element createInstructions(StructuredArtifactHomeInterface home) {
      Element instructions = new Element("instructions");
      instructions.setContent(new CDATA(home.getInstruction()));
      return instructions;
   }

   protected Element addAnnotations(SchemaNode schema) {
      Element schemaElement = new Element("element");
      schemaElement.setAttribute("name", schema.getName());
      if (schema.getType() != null && schema.getType().getBaseType() != null) {
         schemaElement.setAttribute("type", schema.getType().getBaseType());
      }
      schemaElement.setAttribute("minOccurs", schema.getMinOccurs() + "");
      schemaElement.setAttribute("maxOccurs", schema.getMaxOccurs() + "");
      Element annotation = schema.getSchemaElement().getChild("annotation", schema.getSchemaElement().getNamespace());

      if (annotation != null) {
         schemaElement.addContent(annotation.detach());
      }

      List children = schema.getChildren();
      Element childElement = new Element("children");
      boolean found = false;
      for (Iterator i = children.iterator(); i.hasNext();) {
         childElement.addContent(addAnnotations((SchemaNode) i.next()));
         found = true;
      }

      if (found) {
         schemaElement.addContent(childElement);
      }

      return schemaElement;
   }

   protected void addFileContent(ContentResourceArtifact artifact, Element root) {
      Element fileData = new Element("fileArtifact");
      Element uri = new Element("uri");
      uri.addContent(artifact.getBase().getUrl());
      fileData.addContent(uri);

      root.addContent(fileData);
   }

   protected Element getMetadata(ContentResourceArtifact art) {
      Element root = new Element("metaData");
      root.addContent(createNode("id", art.getId().getValue()));
      root.addContent(createNode("displayName", art.getDisplayName()));

      Element type = new Element("type");
      root.addContent(type);

      type.addContent(createNode("id", "file"));
      type.addContent(createNode("description", "file"));

      Element repositoryNode = new Element("repositoryNode");
      root.addContent(repositoryNode);

      Date created = getDate(art.getBase(),
            art.getBase().getProperties().getNamePropCreationDate());
      if (created != null) {
         repositoryNode.addContent(createNode("created",
               created.toString()));
      }

      Date modified = getDate(art.getBase(),
            art.getBase().getProperties().getNamePropModifiedDate());
      if (modified != null) {
         repositoryNode.addContent(createNode("modified",
               modified.toString()));
      }

      repositoryNode.addContent(createNode("size", "" +
            art.getBase().getContentLength()));

      Element mimeType = new Element("mimeType");
      repositoryNode.addContent(mimeType);
      String mimeTypeString = art.getBase().getContentType();
      MimeType mime = new MimeType(mimeTypeString);
      mimeType.addContent(createNode("primary", mime.getPrimaryType()));
      mimeType.addContent(createNode("sub", mime.getSubType()));

      return root;
   }

   protected Element createNode(String name, String value) {
      Element newNode = new Element(name);
      newNode.addContent(value);
      return newNode;
   }

   protected Date getDate(ContentResource resource, String propName) {
      try {
         Time time = resource.getProperties().getTimeProperty(propName);
         return new Date(time.getTime());
      }
      catch (EmptyException e) {
         return null;
      }
      catch (TypeException e) {
         throw new RuntimeException(e);
      }
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

}
