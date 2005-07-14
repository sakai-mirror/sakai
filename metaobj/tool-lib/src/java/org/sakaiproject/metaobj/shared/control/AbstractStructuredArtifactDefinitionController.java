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
 * $URL
 * $Revision
 * $Date
 */

package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.springframework.web.servlet.ModelAndView;
//import org.sakaiproject.metaobj.repository.RepositoryManager;
//import org.sakaiproject.metaobj.repository.mgt.FileArtifactFinder;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;

import java.util.*;

abstract public class AbstractStructuredArtifactDefinitionController extends AbstractFormController {
   protected final Log logger = LogFactory.getLog(getClass());
   private HomeFactory homeFactory;
   private AuthenticationManager authManager;
   //private FileArtifactFinder fileArtifactFinder;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   //private RepositoryManager repositoryManager;
   private IdManager idManager;
   private AuthorizationFacade authzManager = null;
   private WorksiteManager worksiteManager = null;
   private String toolId;
   private ListScrollIndexer listScrollIndexer;
   
   //TODO removed references to repositoryManager and fileArtifactFinder 

   public void checkPermission(String function) throws AuthorizationFailedException{
      if (getStructuredArtifactDefinitionManager().isGlobal()) {
         getAuthzManager().checkPermission(function, getIdManager().getId(StructuredArtifactDefinitionManager.GLOBAL_SAD_QUALIFIER));
      } else {
         getAuthzManager().checkPermission(function, getIdManager().getId(PortalService.getCurrentToolId()));
      }

   }

   protected Boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
            getIdManager().getId(PortalService.getCurrentSiteId())));
   }

   protected ModelAndView prepareListView(Map request, String recentId){
       Map model = new HashMap();
       String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
       model.put("isMaintainer", isMaintainer());
       model.put("worksite", getWorksiteManager().getSite(worksiteId));
       model.put("sites", getUserSites());
       ToolConfiguration tool = getWorksiteManager().getTool(PortalService.getCurrentToolId());
       model.put("tool", tool);

       boolean global = getStructuredArtifactDefinitionManager().isGlobal();
       model.put("isGlobal", new Boolean(global));

       if (global) {
          model.put("authZqualifier",getIdManager().getId(StructuredArtifactDefinitionManager.GLOBAL_SAD_QUALIFIER));
       } else {
          if (tool != null)
             model.put("authZqualifier",getIdManager().getId(tool.getId()));
          else
             model.put("authZqualifier",getIdManager().getId(PortalService.getCurrentToolId()));
       }

      List types;
      if (getStructuredArtifactDefinitionManager().isGlobal()){
         types = getStructuredArtifactDefinitionManager().findGlobalHomes();
      } else {
         types = getStructuredArtifactDefinitionManager().findHomes(getWorksiteManager().getCurrentWorksiteId());
      }

      Collections.sort(types);
      List typesList = new ArrayList(types);
      if (recentId != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getTypeIndex(typesList,
            recentId));
         model.put("newFormId", recentId);
      }
      
      types = getListScrollIndexer().indexList(request, model, typesList);

      model.put("types", types);
      
      return new ModelAndView("success", model);
   }

   protected int getTypeIndex(List typesList, String recentId) {
      for (int i=0;i<typesList.size();i++) {
         StructuredArtifactDefinitionBean home = (StructuredArtifactDefinitionBean)typesList.get(i);
         if (home.getType().getId().getValue().equals(recentId)) {
            return i;
         }
      }

      return 0;
   }

   /**
    *
    * @return collection of site ids user belongs to, as Strings
    */
   protected Map getUserSites() {
      Collection sites = getWorksiteManager().getUserSites();
      Map userSites = new HashMap();
      for (Iterator i=sites.iterator();i.hasNext();){
         Site site = (Site)i.next();
         userSites.put(site.getId(), site);
      }
      return userSites;
   }
   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   /**
    * @return Returns the homeFactory.
    */
   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   /**
    * @param homeFactory The homeFactory to set.
    */
   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   /**
    * @return Returns the fileArtifactFinder.
    */
   //public FileArtifactFinder getFileArtifactFinder() {
   //   return fileArtifactFinder;
   //}

   /**
    * @param fileArtifactFinder The fileArtifactFinder to set.
    */
   //public void setFileArtifactFinder(FileArtifactFinder fileArtifactFinder) {
   //   this.fileArtifactFinder = fileArtifactFinder;
   //}

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   /**
    * @return Returns the repositoryManager.
    */
   //public RepositoryManager getRepositoryManager() {
   //   return repositoryManager;
   //}

   /**
    * @param repositoryManager The repositoryManager to set.
    */
   //public void setRepositoryManager(RepositoryManager repositoryManager) {
   //   this.repositoryManager = repositoryManager;
   //}

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

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public String getToolId() {
      return toolId;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }
}
