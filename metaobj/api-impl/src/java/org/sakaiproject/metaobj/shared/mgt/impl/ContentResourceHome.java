package org.sakaiproject.metaobj.shared.mgt.impl;

import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.TypeException;
import org.jdom.Element;

import java.util.Collection;
import java.util.Date;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Sep 14, 2005
 * Time: 10:08:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResourceHome implements ReadableObjectHome, PresentableObjectHome {

   public Type getType() {
      throw new UnsupportedOperationException("not implemented");
   }

   public String getExternalType() {
      throw new UnsupportedOperationException("not implemented");
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
      ContentResourceArtifact artifact = (ContentResourceArtifact)art;
      Element root = new Element("artifact");

      root.addContent(getMetadata(artifact));

      Element fileData = new Element("fileArtifact");
      Element uri = new Element("uri");
      uri.addContent(artifact.getBase().getUrl());
      fileData.addContent(uri);

      root.addContent(fileData);

      return root;
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


}
