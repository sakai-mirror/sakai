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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/mgt/impl/StructuredArtifactDefinitionManagerImpl.java,v 1.3 2005/07/06 18:50:30 jellis Exp $
 * $Revision: 1.3 $
 * $Date: 2005/07/06 18:50:30 $
 */


package org.sakaiproject.metaobj.shared.mgt.impl;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.mgt.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactDefinition;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.utils.BeanFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.*;


/**
 * @author chmaurer
 * @author jbush
 */
public class StructuredArtifactDefinitionManagerImpl extends HibernateDaoSupport
   implements StructuredArtifactDefinitionManager, DuplicatableToolService {

   private AuthorizationFacade authzManager = null;
   private IdManager idManager;
   private ArtifactFinderManager artifactFinderManager;
   private WorksiteManager worksiteManager;
   private ContentHostingService contentHosting;

   public StructuredArtifactDefinitionManagerImpl() {
      int blah = 1;
   }

   public Map getHomes() {
      Map returnMap = new HashMap();
      List list = findHomes();
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next();
         returnMap.put(sad.getId().getValue(), sad);
      }

      return returnMap;
   }

   /**
    * @param worksiteId
    * @return a map with all worksite and global homes
    */
   public Map getWorksiteHomes(Id worksiteId) {
      Map returnMap = new HashMap();
      List list = findGlobalHomes();
      list.addAll(findHomes(worksiteId));
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next();
         returnMap.put(sad.getId().getValue(), sad);
      }

      return returnMap;
   }

   /**
    *
    * @return list of published sads or sads owned by current user
    */
   public List findHomes() {
      String query = "from StructuredArtifactDefinitionBean where owner = ? or siteState = ?  or globalState = ?";
      Object[] params = new Object[]{getAuthManager().getAgent().getId().getValue(),
                                     new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED)};
      return getHibernateTemplate().find(query, params);
   }

   /**
    *
    * @return list of all published globals or global sad owned by current user or waiting for approval
    */
   public List findGlobalHomes() {
      String query = "from StructuredArtifactDefinitionBean where ((siteId is null and (globalState = ? or owner = ?)) or globalState = 1)";
      Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     getAuthManager().getAgent().getId().getValue()};
      return getHibernateTemplate().find(query, params);
   }

   /**
    * @param currentWorksiteId
    * @return list of globally published sads or published sad in currentWorksiteId or sads in
    *         currentWorksiteId owned by current user
    */
   public List findHomes(Id currentWorksiteId) {
      String query = "from StructuredArtifactDefinitionBean where globalState = ? or (siteId = ? and (owner = ? or siteState = ? )) ";
      Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     currentWorksiteId.getValue(),
                                     getAuthManager().getAgent().getId().getValue(),
                                     new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED)};
      return getHibernateTemplate().find(query, params);
   }

   public StructuredArtifactDefinitionBean loadHome(String type) {
      return loadHome(getIdManager().getId(type));
   }

   public StructuredArtifactDefinitionBean loadHome(Id id) {
         return (StructuredArtifactDefinitionBean) getHibernateTemplate().get(
            StructuredArtifactDefinitionBean.class, id);
   }

   public StructuredArtifactDefinitionBean loadHomeByExternalType(String externalType, Id worksiteId) {
      List homes = (List) getHibernateTemplate().find(
         "from StructuredArtifactDefinitionBean a where externalType=? AND " +
         "(globalState=? OR siteId=?)", new Object[]{
            externalType, new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
            worksiteId.getValue()});

      if (homes.size() == 0) return null;

      if (homes.size() == 1) {
         return (StructuredArtifactDefinitionBean) homes.get(0);
      }
      else {
         for (Iterator i=homes.iterator();i.hasNext();) {
            StructuredArtifactDefinitionBean def = (StructuredArtifactDefinitionBean)i.next();
            if (def.getSiteId() != null) {
               if (def.getSiteId().equals(worksiteId.getValue())) {
                  return def;
               }
            }
         }
         return (StructuredArtifactDefinitionBean) homes.get(0);
      }
   }

   public StructuredArtifactDefinitionBean save(StructuredArtifactDefinitionBean bean) {
      if (!sadExists(bean)){
	      bean.setModified(new Date(System.currentTimeMillis()));

         boolean loadSchema = false;

         StructuredArtifactDefinition sad = null;
         try {
            if (bean.getId() == null) {
               loadSchema = true;
               loadNode(bean);
               bean.setCreated(new Date(System.currentTimeMillis()));
            } else if (bean.getSchemaFile() != null){
               loadSchema = true;
               loadNode(bean);
               sad = new StructuredArtifactDefinition(bean);
               updateExistingArtifacts(sad);
            }
         }
         catch (Exception e) {
            throw new OspException("Invlaid schema", e);
         }

         sad = new StructuredArtifactDefinition(bean);
         bean.setExternalType(sad.getExternalType());
         getHibernateTemplate().saveOrUpdateCopy(bean);
   	} else {
   		throw new PersistenceException("Form name {0} exists", new Object[]{bean.getDescription()}, "description");
   	}
      return bean;
   }


   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public boolean isGlobal() {
      ToolConfiguration toolConfig =
         getWorksiteManager().getTool(PortalService.getCurrentToolId());
      String isGlobal = null;
      
      try {
      isGlobal = toolConfig.getConfig().getProperty(TOOL_GLOBAL_SAD);
      }
      catch(NullPointerException e) {
         this.logger.warn("Handle when toolConfig comes back null in Mercury");
      }
      if (isGlobal == null) {
         return false;
      } else {
         return (isGlobal.equals("true"));
      }
   }

   public Collection getRootElements(StructuredArtifactDefinitionBean sad) {
      try {
         SchemaNode node = loadNode(sad);
         return node.getRootChildren();
      }
      catch (Exception e) {
         throw new OspException("Invalid schema.", e);
      }
   }

   public void validateSchema(StructuredArtifactDefinitionBean sad) {
      SchemaNode node = null;

      try {
         node = loadNode(sad);
      }
      catch (Exception e) {
         throw new OspException("Invlid schema file.", e);
      }

      if (node == null) {
         throw new OspException("Invlid schema file.");
      }
   }

   public StructuredArtifactHomeInterface convertToHome(StructuredArtifactDefinitionBean sad) {
      return new StructuredArtifactDefinition(sad);
   }

   protected SchemaNode loadNode(StructuredArtifactDefinitionBean sad)
         throws TypeException, IdUnusedException, PermissionException {
      if (sad.getSchemaFile() != null) {
         ContentResource resource = getContentHosting().getResource(sad.getSchemaFile().getValue());
         sad.setSchema(resource.getContent());
      }

      if (sad.getSchema() == null) {
         return null;
      }

      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(new ByteArrayInputStream(sad.getSchema()));
   }

   protected boolean sadExists(StructuredArtifactDefinitionBean sad) throws PersistenceException{
      return false;
      /*
      Map wkstHomes = getWorksiteHomes(getWorksiteManager().getCurrentWorksiteId());
      Collection lstHomes = wkstHomes.values();

      for (Iterator i = lstHomes.iterator();i.hasNext();){
      	StructuredArtifactDefinitionBean curSAD = (StructuredArtifactDefinitionBean)i.next();
      	if(curSAD.getDescription().equalsIgnoreCase(sad.getDescription()) &&
            !curSAD.getId().equals(sad.getId())){
	      	String entryWID = curSAD.getSiteId();

            if (entryWID == null && isGlobal()) {
               return true;
            }
            else if (entryWID != null) {
               return true;
            }
      	}
      }
		return false;
      */
   }

   /**
    *
    * @param sad
    * @param artifact
    * @throws OspException if artifact doesn't validate
    */
   protected void validateAfterTransform(StructuredArtifactDefinition sad, StructuredArtifact artifact) throws OspException {
      //TODO figure out how to do the validator
//      StructuredArtifactValidator validator = new StructuredArtifactValidator();
//      artifact.setHome(sad);
//      Errors artifactErrors = new BindExceptionBase(artifact, "bean");
//      validator.validate(artifact, artifactErrors);
//      if (artifactErrors.getErrorCount() > 0) {
//         StringBuffer buf = new StringBuffer();
//         for (Iterator i=artifactErrors.getAllErrors().iterator();i.hasNext();){
//            ObjectError error = (ObjectError) i.next();
//            buf.append(error.toString() + " ");
//         }
//         throw new OspException(buf.toString());
//      }
   }

   protected void saveAll(StructuredArtifactDefinition sad, Collection artifacts){
      for (Iterator i = artifacts.iterator(); i.hasNext();) {
         StructuredArtifact artifact = (StructuredArtifact) i.next();
         try {
            sad.store(artifact);
         } catch (PersistenceException e) {
            logger.error("problem saving artifact with id " + artifact.getId().getValue() + ":" + e);
         }
      }
   }

   /**
    * Uses the submitted xsl file to transform the existing artifacts into the schema.
    * This process puts the artifact home into system only start while is does its work.
    * This is necessary so that users won't be able to update artifacts while this is going on.
    * The system transforms every object in memory and validates before writing any artifact back out.
    * This way if something fails the existing data will stay intact.
    *
    * TODO possible memory issues
    * TODO all this work need to be atomic
    *
    * @param sad
    * @throws OspException
    */
   protected void updateExistingArtifacts(StructuredArtifactDefinition sad) throws OspException {

      //if we don't have an xsl file and don't need one, return
      if (!sad.getRequiresXslFile()) {
         return;
      }

      if (sad.getRequiresXslFile() && (sad.getXslConversionFileId() == null || sad.getXslConversionFileId().getValue().length() == 0)) {
         throw new OspException("xsl conversion file required");
      }

      // put artifact home in system only state while we do this work.
      // this along with repository authz prevents someone from updating an artifact
      // while this is going on
      StructuredArtifactDefinitionBean currentHome = this.loadHome(sad.getId());
      boolean originalSystemOnlyState = currentHome.isSystemOnly();
      currentHome.setSystemOnly(true);
      getHibernateTemplate().saveOrUpdate(currentHome);

      boolean finished = false;
      String type = sad.getType().getId().getValue();
      ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(type);
      Collection artifacts = artifactFinder.findByType(type);
      Collection modifiedArtifacts = new ArrayList();

      // perform xsl transformations on existing artifacts
      try {
         for (Iterator i = artifacts.iterator(); i.hasNext();) {
            StructuredArtifact artifact = (StructuredArtifact) i.next();
            try {
               transform(sad, artifact);
               validateAfterTransform(sad, artifact);
               // don't persist yet, in case error is found in some other artifact
               modifiedArtifacts.add(artifact);
            } catch (TransformerException e) {
               throw new OspException("problem transforming item with id=" + artifact.getId().getValue(), e);
            } catch (IOException e) {
               throw new OspException(e);
            } catch (JDOMException e) {
               throw new OspException("problem with xsl file: " + e.getMessage(), e);
            }
         }
         finished = true;
      } finally {
         // reset systemOnly state back to whatever if was
         // but only if there was an error
         if (!originalSystemOnlyState && !finished){
            currentHome.setSystemOnly(false);
            getHibernateTemplate().saveOrUpdate(currentHome);
         }
      }

      // since all artifacts validated go ahead and persist changes
      saveAll(sad, modifiedArtifacts);
   }

   protected Element getStructuredArtifactRootElement(StructuredArtifactDefinition sad, StructuredArtifact artifact) {
      return sad.getArtifactAsXml(artifact).getChild("structuredData").getChild(sad.getRootNode());
   }

   protected void transform(StructuredArtifactDefinition sad, StructuredArtifact artifact) throws IOException, TransformerException, JDOMException {
      /* todo transform
      logger.debug("transforming artifact " + artifact.getId().getValue() + " owned by " + artifact.getOwner().getDisplayName());
      JDOMResult result = new JDOMResult();
      SAXBuilder builder = new SAXBuilder();
      Document xslDoc = builder.build(sad.getXslConversionFileStream());
      Transformer transformer = TransformerFactory.newInstance().newTransformer(new JDOMSource(xslDoc));
      Element rootElement = getStructuredArtifactRootElement(sad, artifact);

      transformer.transform(new JDOMSource(rootElement), result);

      artifact.setBaseElement((Element) result.getResult().get(0));
      */
   }

   public AuthenticationManager getAuthManager(){
      return (AuthenticationManager) BeanFactory.getInstance().getBean("authManager");
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public void importResources(ToolConfiguration fromTool, ToolConfiguration toTool, List resourceIds) {
      // select all this worksites forms and create them for the new worksite
      Map homes = getWorksiteHomes(getIdManager().getId(fromTool.getSiteId()));

      for (Iterator i=homes.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         StructuredArtifactDefinitionBean bean = (StructuredArtifactDefinitionBean)entry.getValue();

         if (fromTool.getSiteId().equals(bean.getSiteId())) {
            bean.setSiteId(toTool.getSiteId());
            bean.setId(null);
            getHibernateTemplate().saveOrUpdateCopy(bean);
         }
      }

      // check for presentations that may need to be updated.
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

}
