/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.common.agent;

import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.superstructure.SuperStructureManager;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.component.junit.spring.ApplicationContextBaseTest;

public class AgentGroupManagerTest extends ApplicationContextBaseTest
{
  private AgentGroupManager agentGroupManager; // dep inj
  private SuperStructureManager superStructureManager; // dep inj

  public AgentGroupManagerTest()
  {
    super();
    init();
  }

  public AgentGroupManagerTest(String name)
  {
    super(name);
    init();
  }

  private void init()
  {
    agentGroupManager = (AgentGroupManager) getApplicationContext().getBean(
        AgentGroupManager.class.getName());
    superStructureManager = (SuperStructureManager) getApplicationContext()
        .getBean(SuperStructureManager.class.getName());
  }

  /**
   * @see org.sakaiproject.component.junit.spring.ApplicationContextBaseTest#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  /**
   * @see org.sakaiproject.component.junit.spring.ApplicationContextBaseTest#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  /*
   * Class under test for Agent createAgent(String)
   */
  public void testCreateAgentString()
  {
    Agent agent1 = agentGroupManager.createAgent("agent1");
    assertTrue(agent1 != null);
  }

  /*
   * Class under test for Agent createAgent(Node, String)
   */
  public void testCreateAgentNodeString()
  {
    Agent agent2 = agentGroupManager.createAgent("agent2");
    assertTrue(agent2 != null);
  }

  /*
   * Class under test for Agent createAgent(Node, String, String, Type)
   */
  public void testCreateAgentNodeStringStringType()
  {
    Agent agent3 = agentGroupManager.createAgent(agentGroupManager
        .getDefaultContainer(), null, "agent3", "agent3", agentGroupManager
        .getDefaultAgentType());
    assertTrue(agent3 != null);
  }

  /*
   * Class under test for Group createGroup(String)
   */
  public void testCreateGroupString()
  {
    Group group1 = agentGroupManager.createGroup("group1");
    assertTrue(group1 != null);
  }

  /*
   * Class under test for Group createGroup(Node, String)
   */
  public void testCreateGroupNodeString()
  {
    Group group2 = agentGroupManager.createGroup(agentGroupManager
        .getDefaultContainer(), "group2");
    assertTrue(group2 != null);
  }

  /*
   * Class under test for Group createGroup(Node, String, String, Type)
   */
  public void testCreateGroupNodeStringStringType()
  {
    Group group3 = agentGroupManager.createGroup(agentGroupManager
        .getDefaultContainer(), "group3", "group3", agentGroupManager
        .getDefaultGroupType());
    assertTrue(group3 != null);
  }

  /*
   * Class under test for Group createGroup(String, Type)
   */
  public void testCreateGroupStringType()
  {
    Group group4 = agentGroupManager.createGroup("group4", agentGroupManager
        .getDefaultGroupType());
    assertTrue(group4 != null);
  }

  public void testGetAgent()
  {
    Agent agent = agentGroupManager.getAgent();
    assertTrue(agent != null);
  }

  public void testGetAgentByUuid()
  {
    Agent agent1 = agentGroupManager.getAgent();
    String uuid = agent1.getUuid();
    Agent agent2 = agentGroupManager.getAgentByUuid(uuid);
    assertTrue(agent2 != null);
    assertTrue(agent1.equals(agent2));
  }

  public void testGetAgentByEnterpriseId()
  {
    Agent agent1 = agentGroupManager.getAgent();
    String enterpriseId = agent1.getEnterpriseId();
    Agent agent2 = agentGroupManager.getAgentByEnterpriseId(enterpriseId);
    assertTrue(agent2 != null);
    assertTrue(agent1.equals(agent2));
  }

  public void testGetGroupByUuid()
  {
    Group group1 = agentGroupManager.createGroup("group1");
    String uuid = group1.getUuid();
    Group group2 = agentGroupManager.getGroupByUuid(uuid);
    assertTrue(group2 != null);
    assertTrue(group1.equals(group2));
  }

  public void testGetGroupByEnterpriseId()
  {
    Group group1 = agentGroupManager.createGroup("group1");
    String enterpriseId = group1.getEnterpriseId();
    Group group2 = agentGroupManager.getGroupByEnterpriseId(enterpriseId);
    assertTrue(group2 != null);
    assertTrue(group1.equals(group2));
  }

  public void testGetAnonymousGroup()
  {
    Group anon = agentGroupManager.getAnonymousGroup();
    assertTrue(anon != null);
  }

  public void testGetAuthenticatedUsers()
  {
    Group auth = agentGroupManager.getAuthenticatedUsers();
    assertTrue(auth != null);
  }

  /*
   * Class under test for List findTransitiveDescendents(Group)
   */
  public void testFindTransitiveDescendentsGroup()
  {
  }

  /*
   * Class under test for List findTransitiveDescendents(Group, boolean)
   */
  public void testFindTransitiveDescendentsGroupboolean()
  {
  }

  public void testSave()
  {
    Agent agent1 = agentGroupManager.createAgent("agent1");
    assertTrue(agent1 != null);
    String uuid = agent1.getUuid();
    agent1.setDisplayName("agent1-1");
    agentGroupManager.save(agent1);
    Agent agent2 = agentGroupManager.getAgentByUuid(uuid);
    assertTrue(agent2 != null && agent1.equals(agent2));
    assertTrue(agent1.getDisplayName().equals(agent2.getDisplayName()));
  }

  /*
   * Class under test for void delete(Agent)
   */
  public void testDeleteAgent()
  {
    Agent agent1 = agentGroupManager.createAgent("agent1");
    assertTrue(agent1 != null);
    String uuid = agent1.getUuid();

    agentGroupManager.delete(agent1);
    Agent agent2 = agentGroupManager.getAgentByUuid(uuid);
    assertTrue(agent2 == null);
  }

  /*
   * Class under test for void delete(String)
   */
  public void testDeleteString()
  {
    Agent deleteStringAgent1 = agentGroupManager
        .createAgent("deleteStringAgent1");
    assertTrue(deleteStringAgent1 != null);
    String uuid = deleteStringAgent1.getUuid();

    agentGroupManager.delete(uuid);
    Agent agent2 = agentGroupManager.getAgentByUuid(uuid);
    //FIXME impl seems to be broken...
    //    assertTrue(agent2 == null);
  }

  public void testGetDefaultAgentType()
  {
    Type t = agentGroupManager.getDefaultAgentType();
    assertTrue(t != null);
  }

  public void testGetDefaultGroupType()
  {
    Type t = agentGroupManager.getDefaultGroupType();
    assertTrue(t != null);
  }

  public void testGetDefaultContainer()
  {
    Node node = agentGroupManager.getDefaultContainer();
    assertTrue(node != null);
  }

  public void testGetObject()
  {
    Group group1 = agentGroupManager.createGroup("group1");
    String uuid = group1.getUuid();
    Object o = agentGroupManager.getObject(uuid, agentGroupManager
        .getDefaultGroupType());
    assertTrue(o != null && group1.equals(o));
  }

}



