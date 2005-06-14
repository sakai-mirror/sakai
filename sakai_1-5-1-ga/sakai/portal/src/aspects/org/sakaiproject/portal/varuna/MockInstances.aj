/*
 * $Header$
 * 
 * Created on Nov 11, 2004
 *
 * This holds static references to jmock mocks so that they
 * can be shared between the junit tests and the debugging aspects.
 * The junit tests setup the mocks and the aspects insert them around 
 * actual calls.  This avoids issues of needing to mock classes and, worse,
 * needing to mock classes and mock interfaces in the same module.
 * 
 */
package org.sakaiproject.portal.varuna;

import org.jmock.Mock;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.vm.VmServletInterface;

/*
* Currently these mock covers need to be defined in an aspect even 
* if they don't explicitly reference any AOP.  This is because the aspects are 
* compiled first by Maven and so this class must exist or that compilation will
* fail.
*/

/**
 * @author haines
 * Create static variables to hold mocks for these services.  This is necessary 
 * since the test files need to set values for the mocks, but the aspects need
 * the modified mocks for use in advice.  Needed some way to allow tests to 
 * create and setup the mocks and the advice to be able to invoke them.
 */
public class MockInstances {
        static Mock mockSiteService = null;
        static Mock mockSitePage = null;
        static Mock mockSite = null;
        static Mock mockErrorSite = null;
        static Mock mockServerConfigurationService = null;
        static Mock mockParameterParser = null;
        static Mock mockVmServletInterface = null;
        static Mock mockUsageSessionService = null;
        static Mock mockSessionState = null;
        static Mock mockHttpServletResponse = null;
        static Mock mockStringUtil = null;
        static Mock mockLogger = null;
        static Mock mockPortalService = null;
        static Mock mockToolConfiguration = null;
        static boolean pleaseMockStringUtil = false;
}