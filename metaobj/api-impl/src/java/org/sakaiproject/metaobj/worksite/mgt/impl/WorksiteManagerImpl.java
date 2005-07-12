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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/worksite/mgt/impl/WorksiteManagerImpl.java,v 1.3 2005/07/05 20:15:18 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.worksite.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;

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

      if (mySites.size() > 0)
      {
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

      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePage page = (SitePage)i.next();

         for (Iterator j=page.getTools().iterator();j.hasNext();) {
            ToolConfiguration tool = (ToolConfiguration)j.next();
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
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public Realm getSiteRealm(String siteId) {
      Realm siteRealm = null;
      try {
         siteRealm = RealmService.getRealm("/site/" +
                  siteId);
      } catch (IdUnusedException e) {
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
