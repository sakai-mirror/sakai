/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

//package org.sakaiproject.component.common.hierarchy;
//
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//import org.sakaiproject.api.common.superstructure.Node;
//import org.sakaiproject.api.common.superstructure.SuperStructureManager;
//import org.sakaiproject.api.common.type.Type;
//import org.sakaiproject.api.common.type.TypeManager;
//import org.sakaiproject.component.common.superstructure.NodeImpl;
//import org.sakaiproject.component.junit.spring.ApplicationContextBaseTest;
//
///**
// * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
// * @version $Id$
// */
//public class HierarchyTree extends ApplicationContextBaseTest
//{
//  private SuperStructureManager hierarchyManager; // dep inj
//  private TypeManager typeManager; // dep inj
//
//  private long uuid = 0L;
//  private org.sakaiproject.api.common.type.Type pType;
//
//  /**
//   * 
//   */
//  public HierarchyTree()
//  {
//    super();
//    init();
//  }
//
//  /**
//   * @param name
//   */
//  public HierarchyTree(String name)
//  {
//    super(name);
//    init();
//  }
//
//  private void init()
//  {
//    hierarchyManager = (SuperStructureManager) getApplicationContext().getBean(
//        SuperStructureManager.class.getName());
//    typeManager = (TypeManager) getApplicationContext().getBean(
//        TypeManager.class.getName());
//    pType = typeManager.createType("org.sakaiproject", "hierarchy", "Node",
//        "Generic Node", "Generic Node", "mimetype");
//  }
//
//  public void testCreateHierarchy()
//  {
//    // create parent root node
//    Node root = new NodeImpl();
//    String id = "" + uuid++;
//    root.setUuid(id);
//    root.setReferenceName(id);
//    root.setDescription("ROOT");
//    root.setType(pType);
//    hierarchyManager.saveNode(root);
//
//    // create model hierarchy
//    createHierarchy(root, 5, 10); // ~100,000 nodes (i.e. 10^5)
//
//    // create file system root (child of root node)
//    Node fileSystemRoot = new NodeImpl();
//    id = "" + uuid++;
//    fileSystemRoot.setUuid(id);
//    fileSystemRoot.setReferenceName("/");
//    fileSystemRoot.setDescription("File System Root");
//    fileSystemRoot.setType(pType);
//    fileSystemRoot.setParent(root);
//    hierarchyManager.saveNode(fileSystemRoot);
//
//    // create model file system (100,000 users * 100 avg files per user)
//    //    createHierarchy(root, 7, 10); // ~10,000,000 nodes (i.e. 10^7)
//  }
//
//  private void createHierarchy(Node root, int depth, int childrenPerNode)
//  {
//    double totalNodes = 1;
//    for (int i = depth; i > 0; i--)
//    {
//      totalNodes += Math.pow(childrenPerNode, i);
//    }
//
//    int halt = 0;
//
//    /*
//     * Seed the tree with test data
//     */
//    System.out.println(new Date() + " : begin tree creation");
//    long begin = System.currentTimeMillis();
//
//    Set rootSet = new HashSet();
//    rootSet.add(root);
//    recursiveInsert(rootSet, depth, childrenPerNode, halt);
//
//    long end = System.currentTimeMillis();
//    long elapsed = (end - begin) / 1000;
//    System.out.println(new Date() + " : end tree creation");
//    System.out.println("duration= " + elapsed + "sec.");
//    double insertsPerSecond = 0L;
//    if (elapsed > 0)
//    {
//      insertsPerSecond = totalNodes / elapsed;
//    }
//    else
//    {
//      insertsPerSecond = totalNodes;
//    }
//    System.out.println("total nodes inserted: " + totalNodes);
//    System.out.println("inserts per second: " + insertsPerSecond);
//  }
//
//  private void recursiveInsert(Set parents, int depth, int avgChildren, int halt)
//  {
//    if (halt == depth) return;
//    Set childSet = new HashSet();
//    for (Iterator iter = parents.iterator(); iter.hasNext();)
//    {
//      NodeImpl element = (NodeImpl) iter.next();
//      childSet.addAll(createChildren(element, avgChildren, pType));
//    }
//    halt++;
//    recursiveInsert(childSet, depth, avgChildren, halt);
//  }
//
//  private Set createChildren(Node parent, int children, Type pType)
//  {
//    Set ret = new HashSet(children);
//    for (int i = 0; i < children; i++)
//    {
//      String idi = "" + uuid++;
//      Node qi = new NodeImpl();
//      qi.setParent(parent);
//      qi.setUuid(idi);
//      qi.setReferenceName(idi);
//      qi.setDescription(idi);
//      qi.setType(pType);
//      hierarchyManager.saveNode(qi);
//      ret.add(qi);
//    }
//    return ret;
//  }
//
//  //  public void testInsertPerformance()
//  //  {
//  //    Node parent = hierarchyManager.getNode(new Long("1"));
//  //
//  //    long begin = System.currentTimeMillis();
//  //    int i = 0;
//  //    // loop
//  //    while (i < 10000)
//  //    {
//  //      i++;
//  //      String idi = "" + uuid++;
//  //      Node qi = new NodeImpl();
//  //      qi.setParent(parent);
//  //      qi.setUuid(idi);
//  //      qi.setReferenceName(idi);
//  //      qi.setDescription(idi);
//  //      qi.setType(pType);
//  //      hierarchyManager.saveNode(qi);
//  //    }
//  //    long end = System.currentTimeMillis();
//  //    long elapsed = (end - begin) / 1000;
//  //    long rate = i / elapsed;
//  //    System.out.println(i + " inserts; duration= " + elapsed + "sec.");
//  //    System.out.println("Rate=" + rate + "/sec.");
//  //  }
//
//}