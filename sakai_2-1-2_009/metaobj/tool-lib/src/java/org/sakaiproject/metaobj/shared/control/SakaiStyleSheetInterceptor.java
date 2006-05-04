/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SakaiStyleSheetInterceptor extends HandlerInterceptorAdapter {
   protected WorksiteManager worksiteManager;
   private static Log M_log = LogFactory.getLog(SakaiStyleSheetInterceptor.class);

   public SakaiStyleSheetInterceptor() {
   }

   public void postHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler,
                          ModelAndView modelAndView) throws Exception {
      // code borrowed from sakai's VmServlet.setVmStdRef() method

      // form the skin based on the current site, and the defaults as configured
      //String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-shared/css/");
      String skinRoot = ServerConfigurationService.getString("skin.repo", "/library/skin");
      String skin = ServerConfigurationService.getString("skin.default", "default");

      Id siteId = getWorksiteManager().getCurrentWorksiteId();

      if (siteId != null) {
         String siteSkin = SiteService.getSiteSkin(siteId.getValue());

         if (siteSkin != null) {
            skin = siteSkin;
         }

         request.setAttribute("sakai_skin_base", skinRoot + "/tool_base.css");
         request.setAttribute("sakai_skin", skinRoot + "/" + skin + "/tool.css");

         //TODO figure out if this is still needed
         // form the portal root for the skin - removing the .css and adding "portalskins" before
         int pos = skin.indexOf(".css");
         if (pos != -1) {
            skin = skin.substring(0, pos);
         }

         request.setAttribute("sakai_portalskin", skinRoot + "portalskins" + "/" + skin + "/");
         request.setAttribute("sakai_skin_id", skin);
      }
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
}
