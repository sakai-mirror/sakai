/*
 * 
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/AllTests.java,v 1.4 2004/12/15 19:26:48 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 12, 2004
 *
 * Create wrapper for all the tests.  They are separated into 
 * expected to pass and still need work so that it is easy to 
 * look at the junit output and see if there is a regression problem.
 * 
 */
package org.sakaiproject.portal.varuna;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author haines
 *
 */
public class AllTests {
    
    public static Test suite() {
        TestSuite suite = new TestSuite(
                "All tests for VarunaServlet");
        
        // To add a suite of tests get the class
        // containing the suite to return it's test suite.
        suite.addTest(PassingTestSuite.suite());
        
        // Call local method to return a TTD test suite.
        suite.addTest(thingsToDo());
        
        //$JUnit-END$
        
        return suite;
    }
    
    // create test suite for things that still need to happen
    public static Test thingsToDo() {
        TestSuite suite = new TestSuite(
                "tests that require work");
   
   //     suite.addTestSuite(TTDTest.class);
        
        return suite;
    }
    
}
