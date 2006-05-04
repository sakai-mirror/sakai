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
package org.sakaiproject.metaobj.shared.mgt.home;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StreamableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
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
      }
      else {
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
       NodeMetadata node = getNodeMetadataService().getNode(
       object.getDisplayName(), this.getType());

       long size = getStreamStore().store(node, RepositoryNode.TECH_MD_TYPE, getInfoStream(object));
       node.setSize(size);
       getNodeMetadataService().store(node);
       return new LightweightArtifact(this, node);
       */
      return null;
   }

   /**
    * protected InputStream getFileStream(StructuredArtifact xmlObject) {
    * StringBuffer sb = new StringBuffer();
    * <p/>
    * format.format(new Object[]{getHostBaseUrl(), xmlObject.getId().getValue(),
    * getType().getId().getValue(),
    * getWorksiteManager().getCurrentWorksiteId() },
    * sb, new FieldPosition(0));
    * logger.debug("redirecting to: " + sb.toString());
    * InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
    * <p/>
    * return is;
    * }
    */

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
      }
      catch (IOException e) {
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
      Element baseElement = (Element) sa.getBaseElement().detach();
      data.addContent(baseElement);
      root.addContent(data);

      Element schemaData = new Element("schema");
      schemaData.addContent(createInstructions());
      schemaData.addContent(addAnnotations(getRootSchema()));
      root.addContent(schemaData);

      return root;
   }

   protected Element createInstructions() {
      Element instructions = new Element("instructions");
      instructions.setContent(new CDATA(getInstruction()));
      return instructions;
   }

   protected Element addAnnotations(SchemaNode schema) {
      Element schemaElement = new Element("element");
      schemaElement.setAttribute("name", schema.getName());
      schemaElement.setAttribute("type", schema.getType().getBaseType());
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
    * @return true if SAD is global (available to all worksites)
    */
   public boolean isGlobal() {
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
      setIdManager((IdManager) applicationContext.getBean("idManager"));
      setRepositoryHelper((PresentableObjectHome) applicationContext.getBean("repositoryHelper"));
   }

   public int compareTo(Object o) {
      StructuredArtifactHome that = (StructuredArtifactHome) o;
      return this.getType().getDescription().toLowerCase().compareTo(that.getType().getDescription().toLowerCase());
   }

   public InputStream getStream(Id artifactId) {
      try {
         StructuredArtifact artifact = (StructuredArtifact) load(artifactId);
         return getInfoStream(artifact);
      }
      catch (PersistenceException e) {
         throw  new RuntimeException(e);
      }
   }

   public boolean isSystemOnly() {
      return false;
   }

   protected WorksiteManager getWorksiteManager() {
      return (WorksiteManager) ComponentManager.getInstance().get(WorksiteManager.class.getName());
   }

}
