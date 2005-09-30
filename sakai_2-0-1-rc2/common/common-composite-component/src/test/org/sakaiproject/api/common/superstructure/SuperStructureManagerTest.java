/***********************************************************************************
 *
 * $Header: $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.common.superstructure;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.junit.spring.ApplicationContextBaseTest;

public class SuperStructureManagerTest extends ApplicationContextBaseTest
{
  private SuperStructureManager superStructureManager; // dep inj
  private UuidManager uuidManager; //dep inj

  private final int depth = 7;

  public SuperStructureManagerTest()
  {
    super();
    init();
  }

  public SuperStructureManagerTest(String name)
  {
    super(name);
    init();
  }

  private void init()
  {
    superStructureManager = (SuperStructureManager) getApplicationContext()
        .getBean(SuperStructureManager.class.getName());
    uuidManager = (UuidManager) getApplicationContext().getBean(
        UuidManager.class.getName());
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

  public void testGetRootNode()
  {
    Node rootNode = superStructureManager.getRootNode();
    assertTrue(rootNode != null);
  }

  public void testGetOrganizationNodeType()
  {
    Type oType = superStructureManager.getOrganizationNodeType();
    assertTrue(oType != null);
  }

  public void testGetOrganizationalUnitNodeType()
  {
    Type ouType = superStructureManager.getOrganizationalUnitNodeType();
    assertTrue(ouType != null);
  }

  public void testCreateNode()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName1";
    final String description = "description1";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);
    assertTrue(node1.getReferenceUuid() != null
        && node1.getReferenceUuid().length() > 0);
    assertTrue(node1.getParent().equals(superStructureManager.getRootNode()));
    assertTrue(node1.getType().equals(
        superStructureManager.getOrganizationalUnitNodeType()));
    assertTrue(node1.getDisplayName().equals(displayName));
    assertTrue(node1.getDescription().equals(description));
  }

  /*
   * Class under test for Node getNode(String)
   */
  public void testGetNodeString()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName2";
    final String description = "description2";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    Node node2 = superStructureManager.getNode(node1.getUuid());
    assertTrue(node2 != null && node1.equals(node2));
  }

  /*
   * Class under test for Node getNode(String, boolean)
   */
  public void testGetNodeStringboolean()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName2";
    final String description = "description2";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    Node node2 = superStructureManager.getNode(node1.getUuid(), true);
    assertTrue(node2 != null && node1.equals(node2));
  }

  /*
   * Class under test for Node getReferencedNode(String)
   */
  public void testGetReferencedNodeString()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName3";
    final String description = "description3";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    Node node2 = superStructureManager.getReferencedNode(referenceUuid);
    assertTrue(node2 != null && node1.equals(node2));
  }

  /*
   * Class under test for Node getReferencedNode(String, boolean)
   */
  public void testGetReferencedNodeStringboolean()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName3";
    final String description = "description3";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    Node node2 = superStructureManager.getReferencedNode(referenceUuid, true);
    assertTrue(node2 != null && node1.equals(node2));
  }

  public void testSaveNode()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    String displayName = "displayName4";
    String description = "description4";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    displayName = displayName + "-mod";
    description = description + "-mod";
    node1.setDisplayName(displayName);
    node1.setDescription(description);
    superStructureManager.saveNode(node1);

    Node node2 = superStructureManager.getReferencedNode(referenceUuid);
    assertTrue(node2 != null && node1.equals(node2));
    assertTrue(node2.getDisplayName().equals(displayName));
    assertTrue(node2.getDescription().equals(description));
  }

  public void testDeleteNode()
  {
    final String referenceUuid = uuidManager.createUuid();
    assertTrue(referenceUuid != null && referenceUuid.length() > 0);
    final String displayName = "displayName5";
    final String description = "description5";

    Node node1 = superStructureManager.createNode(referenceUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), displayName, description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);

    Node node2 = superStructureManager.getReferencedNode(referenceUuid);
    assertTrue(node2 != null && node1.equals(node2));

    superStructureManager.deleteNode(node2);
    Node node3 = superStructureManager.getReferencedNode(referenceUuid);
    assertTrue(node3 == null);
  }

  public void testMoveNode()
  {
    final String node1RefUuid = uuidManager.createUuid();
    final String parent2RefUuid = uuidManager.createUuid();
    final String displayName = "displayName6";
    final String description = "description6";

    Node parent1 = superStructureManager.getRootNode();
    Node parent2 = superStructureManager.createNode(parent2RefUuid,
        superStructureManager.getRootNode(), superStructureManager
            .getOrganizationalUnitNodeType(), "parent2", null);
    Node node1 = superStructureManager.createNode(node1RefUuid, parent1,
        superStructureManager.getOrganizationalUnitNodeType(), displayName,
        description);
    assertTrue(node1 != null);
    assertTrue(node1.getUuid() != null && node1.getUuid().length() > 0);
    assertTrue(node1.getParent().equals(parent1));

    superStructureManager.moveNode(node1, parent2);
    assertTrue(node1.getParent().equals(parent2));

    Node node2 = superStructureManager.getReferencedNode(node1RefUuid);
    assertTrue(node2 != null && node1.equals(node2));
    assertTrue(node2.getParent().equals(parent2));
  }

  public void testGetTransitiveChildren()
  {
    //TODO implement this unit test; not currently critical to our success
  }

  /*
   * Class under test for List getTransitiveParents(Node)
   */
  public void testGetTransitiveParentsNode()
  {
    List refNodes = createSimpleTree(depth);

    // verify we created the right size tree
    assertTrue(refNodes.size() == depth + 1);
    for (int i = 0; i <= depth; i++)
    {
      Node child = (Node) refNodes.get(i);
      List parentNodes = superStructureManager.getTransitiveParents(child);

      // "self" should be in the List.
      assertTrue(parentNodes.contains(child));

      // test results by slowly walking the parent tree manually
      Node testChild = child;
      while (testChild.getParent() != null)
      {
        Node parent = testChild.getParent();
        assertTrue(parentNodes.contains(testChild));
        // recurse
        testChild = testChild.getParent();
      }
    }
  }

  /**
   * Creates a simple tree; only one child per parent (narrow).
   * 
   * @param depth
   * @return
   */
  private List createSimpleTree(int depth)
  {
    List list = new ArrayList(depth);
    Node parent = superStructureManager.getRootNode();
    list.add(parent);
    for (int i = 0; i < depth; i++)
    {
      Node child = superStructureManager.createNode(uuidManager.createUuid(),
          parent, superStructureManager.getOrganizationalUnitNodeType(),
          "simpleTree-" + (i + 1), null);
      list.add(child);
      parent = child;
    }
    return list;
  }

}
