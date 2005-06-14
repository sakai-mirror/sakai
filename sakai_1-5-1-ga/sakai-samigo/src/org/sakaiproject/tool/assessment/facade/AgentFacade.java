package org.sakaiproject.tool.assessment.facade;
import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import test.org.sakaiproject.tool.assessment.jsf.TestBackingBean;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import javax.servlet.*;
import javax.servlet.http.*;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * SectionFacade implements SectionDataIfc that encapsulates our out of bound (OOB)
 * agreement.
 * @author not attributable
 * @version 1.0
 */
public class AgentFacade implements Serializable {

  AgentImpl agent;
  String agentString;

  public AgentFacade(String agentId)
  {
    agent = new AgentImpl(agentId, null, new IdImpl(agentId));
    agentString = agentId;
  }

  public static AgentImpl getAgent(){
    AgentImpl agent = new AgentImpl("Administrator", null, new IdImpl("admin"));
    return agent;
  }

  public static String getAgentString(){
    String agentS="";
    // this is anonymous user sign 'cos sakai don't know about them
    if (UserDirectoryService.getCurrentUser().getId()==null || ("").equals(UserDirectoryService.getCurrentUser().getId())){
      TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
      //System.out.println("Bean = " + bean.getProp1());
      if (bean != null && !bean.getProp1().equals("prop1"))
        agentS = bean.getProp1();
    }
    else {
      agentS = UserDirectoryService.getCurrentUser().getId();
      // please see SAM-323
      TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
      bean.setProp1(agentS);
    }
    //System.out.println("** getAgentString() ="+agentS);
    return agentS;
  }

  public static String getAgentString(HttpServletRequest req, HttpServletResponse res){
    String agentS="";
    // this is a sign that an unauthenticated person is trying to access the application
    // 'cos sakai don't know about them
    if (UserDirectoryService.getCurrentUser().getId()==null || ("").equals(UserDirectoryService.getCurrentUser().getId())){
      TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBeanFromExternalServlet(
        "bean", req, res);
      if (bean != null && !bean.getProp1().equals("prop1"))
        agentS = bean.getProp1();
    }
    else {
      agentS = UserDirectoryService.getCurrentUser().getId();
      // please see SAM-323
      TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
      bean.setProp1(agentS);
    }
    System.out.println("** getAgentString() ="+agentS);
    return agentS;
  }

  // should phrase out this one
  public static String getDisplayName(String agentS){
    return UserDirectoryService.getCurrentUser().getDisplayName();
  }

  public String getDisplayName(){
    String s="";   
    try{
      s=UserDirectoryService.getUser(this.agentString).getDisplayName();
    }
    catch(Exception e){
      System.out.println(e.getMessage());
    }
    return s;
  }

  public String getFirstName()
  {
    String s="";   
    try{
      s=UserDirectoryService.getUser(this.agentString).getFirstName();
    }
    catch(Exception e){
      System.out.println(e.getMessage());
    }
    return s;
  }

  public String getLastName()
  {
    String s="";   
    try{
      s=UserDirectoryService.getUser(this.agentString).getLastName();
    }
    catch(Exception e){
      System.out.println(e.getMessage());
    }
    return s;
  }

  public String getRole()
  {
    String role = "anonymous_access";
    String thisSiteId = PortalService.getCurrentSiteId();
    String realmName = "/site/" + thisSiteId;
    if (thisSiteId == null)
      return role;

    try
    {
      Realm siteRealm = RealmService.getRealm(realmName);
      Role currentUserRole = siteRealm.getUserRole(agentString);
      role = currentUserRole.getId();
      //System.out.println(realmName + ":" + role);
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }
    return role;
  }

  public String getIdString()
  {
    return agentString;
  }

  public static String getCurrentSiteId(){
    // access via url => users does not login via any sites
    String currentSiteId = null;
    DeliveryBean delivery = (DeliveryBean) ContextUtil.lookupBean("delivery");
    if (!delivery.getAccessViaUrl())
      currentSiteId = org.sakaiproject.service.framework.portal.cover.PortalService.getCurrentSiteId();
    return currentSiteId;
  }

  // this method should live somewhere else
  public static void createAnonymous(){
    TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
    String agentS = "anonymous_"+(new java.util.Date()).getTime();
    //System.out.println("create anonymous ="+agentS);
    bean.setProp1(agentS);
  }

  public static String getCurrentSiteName(){
    // access via url => users does not login via any sites
    String currentSiteName = null;
    DeliveryBean delivery = (DeliveryBean) ContextUtil.lookupBean("delivery");
    if (!delivery.getAccessViaUrl()){
      try{ 
        currentSiteName = SiteService.getSite(getCurrentSiteId()).getTitle();
      }
      catch (Exception e){
        System.out.println(e.getMessage());
      }
    }
    return currentSiteName;
  }

  // should phrase out this one
  public static String getDisplayNameByAgentId(String agentId){
    String name = null;
    try{
      name = UserDirectoryService.getUser(agentId).getDisplayName();
    }
    catch (Exception e){
      System.out.println(e.getMessage());
    }
    return name;
  }


 }
