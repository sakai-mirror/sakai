package org.sakaiproject.tool.assessment.facade;
import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import test.org.sakaiproject.tool.assessment.jsf.TestBackingBean;

import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

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
/*    String agentS = "admin";
    TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
    System.out.println("Bean = " + bean.getProp1());
    if (bean != null && !bean.getProp1().equals("prop1"))
      agentS = bean.getProp1();
    return agentS;*/
    return UserDirectoryService.getCurrentUser().getId();
  }

  public static String getDisplayName(String agentS){
    if ("admin".equals(agentS))
      return "Administrator";
    else if (agentS.equals("rachel"))
      return "Rachel Gollub";
    else if (agentS.equals("marith"))
      return "Margaret Petit";
    else
      return "Dr. Who";
  }

  public String getFirstName()
  {
    if ("admin".equals(agentString))
      return "Samigo";
    else if (agentString.equals("rachel"))
      return "Rachel";
    else if (agentString.equals("marith"))
      return "Margaret";
    else
      return "Dr.";
  }

  public String getLastName()
  {
    if ("admin".equals(agentString))
      return "Administrator";
    else if (agentString.equals("rachel"))
      return "Gollub";
    else if (agentString.equals("marith"))
      return "Petit";
    else
      return "Who";
  }

  public String getRole()
  {
    return "Student";
  }

  public String getIdString()
  {
    return agentString;
  }

  public static void createAnonymous(){
    TestBackingBean bean = (TestBackingBean) ContextUtil.lookupBean("bean");
    bean.setProp1("anonymous_"+(new java.util.Date()).getTime());
  }

 }
