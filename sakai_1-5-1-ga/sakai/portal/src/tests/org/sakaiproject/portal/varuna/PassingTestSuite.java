/*
 * 
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/PassingTestSuite.java,v 1.3 2004/12/15 19:26:48 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.portal.varuna;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PassingTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "tests now expected to always pass");
        
        suite.addTestSuite(ParseDisplayPathTest.class);
        suite.addTestSuite(ResolveSiteTest.class);
        suite.addTestSuite(RespondRedirectToLoginTest.class);
        suite.addTestSuite(FindSiteIdTest.class);
        
        return suite;
    }
}
