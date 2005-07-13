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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/mgt/home/StructuredArtifactHome.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.mgt.home;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StreamableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.BeanFactory;
import org.sakaiproject.metaobj.utils.Config;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.intf.WorksiteAware;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 17, 2004
 * Time: 2:52:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactHome extends XmlElementHome
   implements StructuredArtifactHomeInterface, WorksiteAware,
   ApplicationContextAware, Comparable, StreamableObjectHome {

   protected final static org.apache.commons.logging.Log logger =
      org.apache.commons.logging.LogFactory.getLog(StructuredArtifactHome.class);

   private boolean modifiable = false;
   private PresentableObjectHome repositoryHelper;
   private IdManager idManager;
   private String siteId;

   private static final MessageFormat format =
   new MessageFormat("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL={0}/member/viewArtifact.osp?artifactId={1}&artifactType={2}&pid={3}\">");

   public Artifact store(Artifact object) throws PersistenceException {
      Id id = object.getId();

      if (id == null) {
         return addArtifact(object);
      } else {
         return updateArtifact(object);
      }
   }
 
   protected Artifact updateArtifact(Artifact object) throws PersistenceException {
      /** todo
      NodeMetadata node = getNodeMetadataService().getNode(object.getId());
      node.setName(object.getDisplayName());
      getNodeMetadataService().store(node);

      long size = getStreamStore().store(node, RepositoryNode.TECH_MD_TYPE, getInfoStream(object));
      node.setSize(size);

      getNodeMetadataService().store(node);
               */

      return object;
   }

   public Artifact load(Id id) throws PersistenceException {
      /** todo
      NodeMetadata node = getNodeMetadataService().getNode(id);
      SAXBuilder builder = new SAXBuilder();

      try {
         Document doc = builder.build(
            getStreamStore().load(node, RepositoryNode.TECH_MD_TYPE));

         StructuredArtifact xmlObject =
            new StructuredArtifact(doc.getRootElement(), getSchema().getChild(getRootNode()));

         xmlObject.setId(id);
         xmlObject.setDisplayName(node.getName());
         xmlObject.setHome(this);
         xmlObject.setOwner(node.getOwner());

         return xmlObject;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
       */
      return null;
   }


   public void remove(Artifact object) throws PersistenceException {
      /**
      getStreamStore().delete(getNodeMetadata(object.getId()));
      getNodeMetadataService().delete(object.getId());
       */
   }

   public Artifact cloneArtifact(Artifact copy, String newName) throws PersistenceException {
      /** todo
      NodeMetadata oldMetadata = getNodeMetadataService().getNode(copy.getId());
      String origName = oldMetadata.getName();
      oldMetadata.setName(newName);
      NodeMetadata newMetadata = getNodeMetadataService().copyNode(oldMetadata);
      oldMetadata.setName(origName);
      getStreamStore().copyStreams(getNodeMetadataService().getNode(copy.getId()), newMetadata);

      return new LightweightArtifact(this, newMetadata);
       */
      return null;
   }

   protected Artifact addArtifact(Artifact object) throws PersistenceException {
      /**
      NodeMetadata node = getNodeMetadataService().createNode(
         object.getDisplayName(), this.getType());

      long size = getStreamStore().store(node, RepositoryNode.TECH_MD_TYPE, getInfoStream(object));
      node.setSize(size);
      getNodeMetadataService().store(node);
      return new LightweightArtifact(this, node);
       */
      return null;
   }

   /**
   protected InputStream getFileStream(StructuredArtifact xmlObject) {
      StringBuffer sb = new StringBuffer();

      format.format(new Object[]{getHostBaseUrl(), xmlObject.getId().getValue(),
                                 getType().getId().getValue(),
                                 getWorksiteManager().getCurrentWorksiteId() },
         sb, new FieldPosition(0));
      logger.debug("redirecting to: " + sb.toString());
      InputStream is = new ByteArrayInputStream(sb.toString().getBytes());

      return is;
   }
    **/

   protected InputStream getInfoStream(Artifact object) throws PersistenceException {

      XMLOutputter outputter = new XMLOutputter();
      StructuredArtifact xmlObject = (StructuredArtifact) object;
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      try {
         Format format = Format.getPrettyFormat();
         outputter.setFormat(format);
         outputter.output(xmlObject.getBaseElement(),
            os);
         return new ByteArrayInputStream(os.toByteArray());
      } catch (IOException e) {
         throw new PersistenceException(e, "Unable to write object", null, null);
      }
   }


   public String getHostBaseUrl() {
      return Config.getInstance().getProperties().getProperty("baseUrl");
   }

   public Element getArtifactAsXml(Artifact art) {
      StructuredArtifact sa = (StructuredArtifact) art;

      Element root = new Element("artifact");
      root.addContent(getRepositoryHelper().getArtifactAsXml(art));

      Element data = new Element("structuredData");
      Element baseElement = (Element)sa.getBaseElement().detach();
      data.addContent(baseElement);
      root.addContent(data);

      Element schemaData = new Element("schema");
      schemaData.addContent(addAnnotations(getRootSchema()));
      root.addContent(schemaData);

      return root;
   }

   protected Element addAnnotations(SchemaNode schema) {
      Element schemaElement = new Element("element");
      schemaElement.setAttribute("name", schema.getName());
      Element annotation = schema.getSchemaElement().getChild(
         "annotation", schema.getSchemaElement().getNamespace());

      if (annotation != null) {
         schemaElement.addContent(annotation.detach());
      }

      List children = schema.getChildren();
      Element childElement = new Element("children");
      boolean found = false;
      for (Iterator i=children.iterator();i.hasNext();) {
         childElement.addContent(addAnnotations((SchemaNode)i.next()));
         found = true;
      }

      if (found) {
         schemaElement.addContent(childElement);
      }

      return schemaElement;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public boolean isModifiable() {
      return modifiable;
   }

   /**
    *
    * @return true if SAD is global (available to all worksites)
    */
   public boolean isGlobal(){
      return (getSiteId() == null || getSiteId().length() == 0);
   }

   public PresentableObjectHome getRepositoryHelper() {
      return repositoryHelper;
   }

   public void setRepositoryHelper(PresentableObjectHome repositoryHelper) {
      this.repositoryHelper = repositoryHelper;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      setIdManager((IdManager)applicationContext.getBean("idManager"));
      setRepositoryHelper((PresentableObjectHome)applicationContext.getBean("repositoryHelper"));
   }

   public int compareTo(Object o) {
      StructuredArtifactHome that = (StructuredArtifactHome) o;
      return this.getType().getDescription().toLowerCase().compareTo(that.getType().getDescription().toLowerCase());
   }

   public InputStream getStream(Id artifactId) {
      try {
         StructuredArtifact artifact = (StructuredArtifact) load(artifactId);
         return getInfoStream(artifact);
      } catch (PersistenceException e) {
         throw  new RuntimeException(e);
      }
   }

   public boolean isSystemOnly() {
      return false;
   }

   protected WorksiteManager getWorksiteManager() {
      return (WorksiteManager) BeanFactory.getInstance().getBean(WorksiteManager.class.getName());
   }
 
}
