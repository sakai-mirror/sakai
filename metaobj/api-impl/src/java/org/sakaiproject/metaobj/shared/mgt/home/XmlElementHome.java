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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/mgt/home/XmlElementHome.java,v 1.2 2005/07/06 18:50:30 jellis Exp $
 * $Revision: 1.2 $
 * $Date: 2005/07/06 18:50:30 $
 */
package org.sakaiproject.metaobj.shared.mgt.home;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.service.framework.portal.cover.PortalService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 1:22:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlElementHome implements StructuredArtifactHomeInterface, InitializingBean, ResourceLoaderAware {

   private SchemaNode schema = null;
   private String rootNode = null;
   private Date schemaDate = null;
   protected final Log logger = LogFactory.getLog(getClass());
   private File homeDirectory = null;
   private String schemaFileName;
   private Type type = null;
   private String typeId = null;
   private IdManager idManager = null;
   public static final String XSD_DIR = "xsd";
   public static final String XML_HOME_PATH = "xmlHome";
   private ResourceLoader resourceLoader;
   /**
    * help information supplied to the user when creating an instance of this xmlelement
    */
   private String instruction;


   public XmlElementHome() {
   }

   public XmlElementHome(String rootNode) {
      this.rootNode = rootNode;
   }

   public SchemaNode getSchema() {
      if (schema == null) {
         File schemaFile = getSchemaFile(schemaFileName);
         schema = SchemaFactory.getInstance().getSchema(schemaFile);
         schemaDate = new Date(schemaFile.lastModified());
      }
      return schema;
   }

   public String getDocumentRoot() {
      return null;
   }

   protected File getSchemaFile(String schemaFileName) {
      return new File(this.pathToWebInf() + File.separator + XSD_DIR + File.separator + schemaFileName);
   }

   public void setSchema(SchemaNode schema) {
      this.schema = schema;
   }

   public Artifact store(Artifact object) throws PersistenceException {
      String id = (String) object.getId().getValue();

      File objectFile = null;

      if (id == null) {
         try {
            objectFile = File.createTempFile(rootNode, ".xml", homeDirectory);
         } catch (IOException e) {
            logger.error("",e);
            throw new OspException(e);
         }
      } else {
         objectFile = new File(homeDirectory, id);
         if (objectFile.exists()) {
            objectFile.delete();
         }
      }

      XMLOutputter outputter = new XMLOutputter();
      StructuredArtifact xmlObject = (StructuredArtifact) object;

      xmlObject.setId(objectFile.getName());

      try {
         Format format = Format.getPrettyFormat();
         outputter.setFormat(format);
         outputter.output(xmlObject.getBaseElement(), new FileOutputStream(objectFile));
      } catch (IOException e) {
         logger.error("",e);
         throw new OspException(e);
      }

      return object;
   }

   public void remove(Artifact object) throws PersistenceException {
      File objectFile = null;

      objectFile = new File(homeDirectory, object.getId().getValue());

      objectFile.delete();
   }

   public Artifact store(String displayName, String contentType, Type type,
                         InputStream in) throws PersistenceException {
      // todo complete
      return null;
   }

   public Artifact update(Artifact object, InputStream in) throws PersistenceException {
      return null;//todo
   }

   public Type getType() {
      return type;
   }

   public String getExternalType() {
      return getSchema().getTargetNamespace().getURI() + "?" + getRootNode();
   }

   public void setType(Type type) {
      this.type = type;
   }

   public Artifact load(Id id) throws PersistenceException {
      return load(id.getValue());
   }

   protected Artifact load(String id) throws PersistenceException {
      File objectFile = new File(homeDirectory, id);

      SAXBuilder builder = new SAXBuilder();

      try {
         Document doc = builder.build(objectFile);

         StructuredArtifact xmlObject =
            new StructuredArtifact(doc.getRootElement(), getSchema().getChild(rootNode));

         xmlObject.setId(id);

         xmlObject.setHome(this);

         return xmlObject;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public Artifact createInstance() {
      StructuredArtifact instance = new StructuredArtifact(rootNode, getSchema().getChild(rootNode));
      prepareInstance(instance);
      return instance;
   }

   public void prepareInstance(Artifact object) {
      object.setHome(this);
      StructuredArtifact xmlObject = (StructuredArtifact) object;
      xmlObject.getBaseElement().setName(rootNode);
   }

   public Artifact createSample() {
      return createInstance();
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      // really just list all here for now...
      String[] files = homeDirectory.list();

      List returnedList = new ArrayList();

      for (int i = 0; i < files.length; i++) {
         try {
            returnedList.add(load(files[i]));
         } catch (PersistenceException e) {
            throw new FinderException();
         }
      }

      return returnedList;
   }

   public boolean isInstance(Artifact testObject) {
      return (testObject instanceof StructuredArtifact);
   }

   public void refresh() {
      schema = null;
      getSchema();
   }

   public String getExternalUri(Id artifactId, String name) {
      //http://johnellis.rsmart.com:8080/osp/member/viewNode.osp?pid=1107451588272-643&nodeId=48D2AFE5A98453AD673579E14405607C
      return "viewNode.osp?pid=" + PortalService.getCurrentToolId() +
         "&nodeId=" + artifactId.getValue();
   }

   public InputStream getStream(Id artifactId) {
      // todo ... implement this
      return null;
   }

   public boolean isSystemOnly() {
      return false;
   }

   public Class getInterface() {
      return StructuredArtifactHomeInterface.class;
   }

   public String getRootNode() {
      return rootNode;
   }

   public void setRootNode(String rootNode) {
      this.rootNode = rootNode;
   }

   public Date getModified() {
      return schemaDate;
   }

   public void setModified(Date schemaDate) {
      this.schemaDate = schemaDate;
   }

   public String getSchemaFileName() {
      return schemaFileName;
   }

   public void setSchemaFileName(String schemaFileName) {
      this.schemaFileName = schemaFileName;
   }

   /**
    * Invoked by a BeanFactory after it has set all bean properties supplied
    * (and satisfied BeanFactoryAware and ApplicationContextAware).
    * <p>This method allows the bean instance to perform initialization only
    * possible when all bean properties have been set and to throw an
    * exception in the event of misconfiguration.
    *
    * @throws Exception in the event of misconfiguration (such
    *                   as failure to set an essential property) or if initialization fails.
    */
   public void afterPropertiesSet() throws Exception {
      homeDirectory = new File(pathToWebInf(), XML_HOME_PATH + File.separator + rootNode);

      if (!homeDirectory.exists()) {
         homeDirectory.mkdirs();
      }

      getSchema();
      getType().setId(getIdManager().getId(getTypeId()));
   }

   protected String pathToWebInf(){
      try {
         return resourceLoader.getResource("WEB-INF").getFile().getCanonicalPath();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String getTypeId() {
      return typeId;
   }

   public void setTypeId(String typeId) {
      this.typeId = typeId;
   }

   public void setResourceLoader(ResourceLoader resourceLoader) {
      this.resourceLoader = resourceLoader;
   }

   public String getInstruction() {
      return instruction;
   }

   public void setInstruction(String instruction) {
      this.instruction = instruction;
   }

   public SchemaNode getRootSchema(){
      return getSchema().getChild(getRootNode());
   }

   public String getSiteId() {
      return null;
   }

   public Artifact cloneArtifact(Artifact copy, String newName) throws PersistenceException {
      return null;
   }

   public Element getArtifactAsXml(Artifact art) {
      return null;
   }
}
