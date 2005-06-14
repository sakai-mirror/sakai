package org.sakaiproject.tool.assessment.data.dao.assessment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.tool.assessment.data.ifc.PublishingTargetIfc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PublishingTarget implements PublishingTargetIfc {
  
  private SiteService siteService;
  private Logger log;
  private org.sakaiproject.service.framework.component.ComponentManager cm;
  private static String LOGGER_SERVICE = "org.sakaiproject.service.framework.log.Logger";
  private static String SITE_SERVICE = "org.sakaiproject.service.legacy.site.SiteService";
                          
  private HashMap map = new HashMap();
    
  public PublishingTarget() {
    cm = ComponentManager.getInstance();
    log = (org.sakaiproject.service.framework.log.Logger) cm.get(LOGGER_SERVICE);
    siteService = (org.sakaiproject.service.legacy.site.SiteService) cm.get(SITE_SERVICE);
  }
      
  public HashMap getTargets()
  {
    
     map = new HashMap();
     map.put("Anonymous Users", "ANONYMOUS_USERS");
     map.put("Authenticated Users", "AUTHENTICATED_USERS");
          
     if (siteService != null){ // sorry, throw nullpointer here, so put in a check -daisyf 
       List siteList = siteService.getSites(SiteService.SelectionType.ACCESS,
 				null, null, null, SiteService.SortType.TITLE_ASC, null);
             
       Site se = null;
       for (Iterator i = siteList.iterator(); i.hasNext();){
         se = (Site) i.next();
         //map.put(se.getId().toLowerCase(), se.getId());
         map.put(se.getTitle().toLowerCase(), se.getId());
       }
     }              
     return map;
  }
  
  /**
   * @return Returns the siteService.
   */
  public SiteService getSiteService()
  {
    return siteService;
  }
  /**
   * @param siteService The siteService to set.
   */
  public void setSiteService(SiteService siteService)
  {
    this.siteService = siteService;
  }
  /**
   * @return Returns the log.
   */
  public Logger getLog()
  {
    return log;
  }
  /**
   * @param log The log to set.
   */
  public void setLog(Logger log)
  {
    this.log = log;
  }
}
