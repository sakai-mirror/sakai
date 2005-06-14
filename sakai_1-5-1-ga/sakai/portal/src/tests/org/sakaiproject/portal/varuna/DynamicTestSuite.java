/*
 *How build test cases dynamically?
 * something to do with anyonymous classes.
 * nothing here has been tested.
 *  
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/DynamicTestSuite.java,v 1.1 2004/11/20 21:30:25 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 12, 2004
 *
 * Create tests on the fly?
 * 
 */
package org.sakaiproject.portal.varuna;

import junit.framework.*;
//import junit.framework.TestSuite;
import org.jmock.*;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DynamicTestSuite extends MockObjectTestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "tests now expected to always pass");
        
   //     suite.addTest(testOpenPass());
        
     //   suite.addTest(testNullPathSite);
        
//        suite.addTestSuite(VarunaServletParseDisplayPathTest.class);
//        suite.addTestSuite(VarunaServletResolveSiteTest.class);
//        suite.addTestSuite(VarunaServletRespondRedirectToLoginTest.class);
//        suite.addTestSuite(VarunaServletFindSiteIdTest.class);
//        
        return suite;
    }
    
    
    // creates a test case to add
//    TestCase test= new MathTest("add") {
//        public void runTest() {
//            testAdd();
//        }
// };
// test.run();
// 
//    
    
    public class TestMyAdd extends TestCase  {
        void testOne() {
            assertTrue(1==1);
        }
    }
    
    //
// 
//    public Test testOpenPass() {
//        Test a = new Test () extends MockObjectTestCase {
//            int b = 1;
//   //         assertTrue(1==1);
//        };
//        return a;
//    }
    
    public void testOpenFail() {
        fail();
    }
    
//    /// no parameter path but pathSite
//    public void testNullPathSite() {
//        
//        // no site specified as parameter   
//        MockInstances.mockParameterParser
//        .expects(
//                once()) 
//                .method("getString") 
//                .with(eq("site")) 
//                .will(
//                        returnValue(null));
//        
//        VarunaServlet varuna = new VarunaServlet();
//        varuna.pathSite = pathSite;
//        
//        String foundSite = varuna.findSiteId(null,"somebody",true);
//        
//        assertEquals("path site not returned",pathSite,foundSite);
//    }
    public void main() {
        (new TestMyAdd()).run();
    }
    
}
