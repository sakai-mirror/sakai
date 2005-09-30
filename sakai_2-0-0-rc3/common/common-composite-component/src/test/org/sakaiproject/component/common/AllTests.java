//package org.sakaiproject.component.common;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//
//import org.sakaiproject.component.common.agent.AgentGroupManagerTest;
//import org.sakaiproject.component.common.authorization.AuthorizationTest;
//import org.sakaiproject.component.common.authorization.AuthzIntegrationTest;
//import org.sakaiproject.component.common.hierarchy.HierarchyManagerTest;
//
///**
// * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
// * @version $Id: AllTests.java,v 1.1 2005/05/11 15:38:42 lance.indiana.edu Exp $
// */
//public class AllTests
//{
//
//  public static Test suite()
//  {
//    TestSuite suite = new TestSuite(
//        "Test for org.sakaiproject.component.common.*");
//
//    suite.addTestSuite(AgentGroupManagerTest.class);
//    suite.addTestSuite(AuthorizationTest.class);
//    suite.addTestSuite(AuthzIntegrationTest.class);
//    suite.addTestSuite(HierarchyManagerTest.class);
//    //    suite.addTestSuite(SiteGroupManagerTest.class);
//    //    suite.addTestSuite(SiteHierarchyManagerTest.class);
//
//    return suite;
//  }
//}