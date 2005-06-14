package org.sakaiproject.component.common.authorization;

import java.util.Iterator;

import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.agent.Group;
import org.sakaiproject.api.common.authorization.AuthorizationManager;
import org.sakaiproject.api.common.authorization.DefaultPermissions;
import org.sakaiproject.api.common.authorization.Permissions;
import org.sakaiproject.api.common.authorization.PermissionsManager;
import org.sakaiproject.api.common.authorization.PermissionsMask;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.superstructure.NodeAware;
import org.sakaiproject.api.common.superstructure.SuperStructureManager;
import org.sakaiproject.api.common.type.TypeManager;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.agent.GroupBean;
import org.sakaiproject.component.junit.spring.ApplicationContextBaseTest;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class AuthzIntegrationTest extends ApplicationContextBaseTest
{
  private UuidManager idManager;
  private SuperStructureManager superStructureManager;
  private TypeManager typeManager;
  private AgentGroupManager agentGroupManager;
  private PermissionsManager permissionsManager;
  private AuthorizationManager authorizationManager;

  public AuthzIntegrationTest()
  {
    super();
    init();
  }

  public AuthzIntegrationTest(String name)
  {
    super(name);
    init();
  }

  private void init()
  {
    idManager = (UuidManager) getApplicationContext().getBean(
        UuidManager.class.getName());
    superStructureManager = (SuperStructureManager) getApplicationContext()
        .getBean(SuperStructureManager.class.getName());
    agentGroupManager = (AgentGroupManager) getApplicationContext().getBean(
        AgentGroupManager.class.getName());
    permissionsManager = (PermissionsManager) getApplicationContext().getBean(
        "org.sakaiproject.api.common.authorization.DefaultPermissionsManager");
    authorizationManager = (AuthorizationManagerImpl) getApplicationContext()
        .getBean(AuthorizationManager.class.getName());
    typeManager = (TypeManager) getApplicationContext().getBean(
        TypeManager.class.getName());
  }

  public void testAuthZIntegration()
  {
    // create resource hierarchy
    Node iu = superStructureManager.getRootNode();

    Node iub = superStructureManager.createNode(idManager.createUuid(), iu,
        superStructureManager.getOrganizationalUnitNodeType(), "IUB",
        "IU Bloomington");

    Node iupui = superStructureManager.createNode(idManager.createUuid(), iu,
        superStructureManager.getOrganizationalUnitNodeType(), "IUPUI", null);

    Node users = superStructureManager.createNode(idManager.createUuid(), iu,
        superStructureManager.getOrganizationalUnitNodeType(), "Users", null);

    Node comm = superStructureManager.createNode(idManager.createUuid(), iupui,
        superStructureManager.getOrganizationalUnitNodeType(), "COMM",
        "Communications");

    Node r110 = superStructureManager
        .createNode(idManager.createUuid(), comm, superStructureManager
            .getOrganizationalUnitNodeType(), "R110", "Speech");

    Node section13567 = superStructureManager.createNode(
        idManager.createUuid(), r110, superStructureManager
            .getOrganizationalUnitNodeType(), "13567", "Section 13567");

    Node sectionResource = superStructureManager.createNode(idManager
        .createUuid(), section13567, superStructureManager
        .getOrganizationalUnitNodeType(), "someResource",
        "someResource description");

    // create agent/group hierarchy    
    Group iuGroup = agentGroupManager.createGroup(agentGroupManager
        .getDefaultContainer(), "IU");
    Group iubGroup = agentGroupManager.createGroup(((GroupBean) iuGroup)
        .getNode(), "IUB");
    Group iupuiGroup = agentGroupManager.createGroup(((GroupBean) iuGroup)
        .getNode(), "IUPUI");
    Group commGroup = agentGroupManager.createGroup(((GroupBean) iupuiGroup)
        .getNode(), "COMM");
    Group r110Group = agentGroupManager.createGroup(((GroupBean) commGroup)
        .getNode(), "R110");
    Group section13567Group = agentGroupManager.createGroup(
        ((NodeAware) commGroup).getNode(), "section13567");
    Group section13567InstructorsGroup = agentGroupManager.createGroup(
        ((NodeAware) section13567Group).getNode(), "instructors");
    Agent facultyOfRecord = agentGroupManager.createAgent(
        ((NodeAware) section13567InstructorsGroup).getNode(),
        "facultyOfRecord", "Faculty of Record");
    Agent teachersAssistant = agentGroupManager.createAgent(
        ((NodeAware) section13567InstructorsGroup).getNode(),
        "teachersAssistant", "TA");

    // create group associations    
    iuGroup.getMembers().add(iubGroup);
    iuGroup.getMembers().add(iupuiGroup);
    iupuiGroup.getMembers().add(commGroup);
    commGroup.getMembers().add(r110Group);
    r110Group.getMembers().add(section13567Group);
    section13567Group.getMembers().add(section13567InstructorsGroup);
    section13567InstructorsGroup.getMembers().add(facultyOfRecord);
    section13567InstructorsGroup.getMembers().add(teachersAssistant);

    // create permissions
    PermissionsMask readMask = new PermissionsMask();
    readMask.put(DefaultPermissions.READ, Boolean.TRUE);
    Permissions readPermissions = permissionsManager.getPermissions(readMask);

    // create some authorizations
    authorizationManager.createAuthorization(iuGroup.getUuid(), readPermissions
        .getUuid(), iu.getUuid());

    boolean b = authorizationManager.isAuthorized(facultyOfRecord.getUuid(),
        permissionsManager, readPermissions, section13567.getUuid());
    assertTrue(b);

    PermissionsMask denyReadMask = new PermissionsMask();
    denyReadMask.put(DefaultPermissions.READ, Boolean.FALSE);
    Permissions denyReadPermissions = permissionsManager
        .getPermissions(denyReadMask);

    authorizationManager.createAuthorization(teachersAssistant.getUuid(),
        denyReadPermissions.getUuid(), sectionResource.getUuid());

    boolean c = authorizationManager.isAuthorized(teachersAssistant.getUuid(),
        permissionsManager, readPermissions, section13567.getUuid());
    assertTrue(c);

    boolean d = authorizationManager.isAuthorized(teachersAssistant.getUuid(),
        permissionsManager, readPermissions, sectionResource.getUuid());
    assertFalse(d);

    // print hierarchy
    System.out.println("");
    printChildren(iu, 0);
    System.out.println("");
  }

  public void printChildren(Node currentNode, int level)
  {

    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < level - 1; i++)
    {
      sb.append((char) 179);
      sb.append("    ");
    }

    if (level != 0)
    {
      sb.append((char) 195);
      sb.append((char) 196);
      sb.append((char) 196);
      sb.append((char) 196);
      sb.append((char) 196);
    }

    sb.append(currentNode.getDisplayName());
    System.out.println(sb);

    Iterator i = currentNode.getChildren().iterator();
    while (i.hasNext())
    {
      printChildren((Node) i.next(), level + 1);
    }
  }

}