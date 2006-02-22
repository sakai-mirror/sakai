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
package org.sakaiproject.metaobj.worksite.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;

import java.util.*;

public class WorksiteManagerImpl implements WorksiteManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private IdManager idManager = null;

   public List getUserSites() {
      return getUserSites(null);
   }

   public List getUserSites(Map properties) {
      // process all the sites
      List mySites = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
            null, null, properties, org.sakaiproject.service.legacy.site.SiteService.SortType.NONE, null);

      if (mySites.size() > 0) {
         Collections.sort(mySites);
      }

      return mySites;
   }

   public Id getCurrentWorksiteId() {
      String id = PortalService.getCurrentSiteId();

      if (id != null) {
         return getIdManager().getId(id);
      }
      return null;
   }

   public List getSiteTools(String toolId, Site site) {
      List tools = new ArrayList();

      List pages = site.getPages();

      for (Iterator i = pages.iterator(); i.hasNext();) {
         SitePage page = (SitePage) i.next();

         for (Iterator j = page.getTools().iterator(); j.hasNext();) {
            ToolConfiguration tool = (ToolConfiguration) j.next();
            if (toolId == null) {
               tools.add(tool);
            }
            else if (toolId.equals(tool.getTool().getId())) {
               tools.add(tool);
            }
         }
      }

      return tools;
   }

   public Site getSite(String siteId) {
      try {
         return SiteService.getSite(siteId);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public AuthzGroup getSiteRealm(String siteId) {
      AuthzGroup siteRealm = null;
      try {
         siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               siteId);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return siteRealm;
   }

   public ToolConfiguration getTool(String id) {
      return SiteService.findTool(id);
   }

   public boolean isUserInSite(String siteId) {
      return SiteService.allowAccessSite(siteId);
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}
