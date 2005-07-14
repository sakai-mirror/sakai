/*
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/SakaiStyleSheetInterceptor.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SakaiStyleSheetInterceptor extends HandlerInterceptorAdapter {
   protected WorksiteManager worksiteManager;
   private static Log M_log = LogFactory.getLog(SakaiStyleSheetInterceptor.class);

   public SakaiStyleSheetInterceptor(){
   }

   public void postHandle(  HttpServletRequest request,
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

         if (siteSkin!=null) {
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
