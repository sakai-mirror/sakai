/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/FindSiteIdTest.java,v 1.3 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 11, 2004
 *
 * These are jmock junit tests for the new short url parser.  
 *
 */
package org.sakaiproject.portal.varuna;


/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//public class VarunaServletFindSiteIdTest extends MockObjectTestCase {
public class FindSiteIdTest extends GenericPathTest {
    
    // rule is that all mocks are in the MockInstances static class.
    // and all proxies are local. Mocks being in the static class allows
    // aspects to get at them.  Anyone who needs a proxy can get at it
    // through the mock.
    
   // ParameterParser pp = null;
    
    String gatewaySite = "mockGateway";
    String userSite = "mockUserSite";
    String parameterSite = "mockParameterSite";
    String pathSite = "mockPathSite";
    
    public FindSiteIdTest() {
        super();
    }
    
    public FindSiteIdTest(String name) {
        super(name);
    }
    
    //////////// tests
    
    //////// path was set as parameter
    public void testParmsNotNull() {
        
        // no site specified as parameter   
        mockParameterParser
        .expects(
                once()) 
                .method("getString") 
                .with(eq("site")) 
                .will(returnValue(parameterSite));
         
        // no call to SiteService
        
      //  VarunaServlet varuna = new VarunaServlet();
        VarunaServlet varuna = createVaruna();
        
        String foundSite = varuna.findSiteId(pp,"nobody",false);
        
        assertEquals("parameter site not returned",parameterSite,foundSite);
    }
    
    
    // no parameter path and not logged in
    public void testNullNotLoggedIn() {
        
        // no site specified as parameter   
        mockParameterParser
        .expects(
                once()) 
                .method("getString") 
                .with(eq("site")) 
                .will(returnValue(null));
        
        // no call to SiteService
        
        // server config return gateway site
        mockServerConfigurationService
        .expects(
                once())
                .method("getGatewaySiteId")
                .will(returnValue(gatewaySite));
        
        VarunaServlet varuna = createVaruna();
        
        String foundSite = varuna.findSiteId(pp,"nobody",false);
        
        assertEquals("gateway site not returned",gatewaySite,foundSite);
    }
    
    
    /// no parameter path but logged in
    public void testNullLoggedIn() {
        
        // no site specified as parameter   
        mockParameterParser
        .expects(
                once()) 
                .method("getString") 
                .with(eq("site")) 
                .will(returnValue(null));
        
        // site service returns site
        
       mockSiteService
        .expects(once()) 
        .method("getUserSiteId") 
        .with(eq("somebody")) 
        .will(returnValue(userSite));
        
        // server config return gateway site
//        MockInstances.mockServerConfigurationService
//        .expects(
//                once())
//                .method("getGatewaySiteId")
//                .will(
//                        returnValue(gatewaySite));
        
        VarunaServlet varuna = createVaruna();
        
        String foundSite = varuna.findSiteId(pp,"somebody",true);
        
        assertEquals("user site not returned",userSite,foundSite);
    }
    
    /// no parameter path but pathSite
    public void testNullPathSite() {
        
        // no site specified as parameter   
        mockParameterParser
        .expects(
                once()) 
                .method("getString") 
                .with(eq("site")) 
                .will(
                        returnValue(null));
        
        VarunaServlet varuna = createVaruna();
        varuna.url.setPathSiteId(pathSite);
        
        String foundSite = varuna.findSiteId(pp,"somebody",true);
        
        assertEquals("path site not returned",pathSite,foundSite);
    }
    
    // test that stay in same context.  E.g. if come as /site stay that way.
    
    // /varuna/site/SITEA then the redirect URL is the same
    // /varuna/page/SITEA
    
    /*
     * test that the new url is same as the old one.
     */
    
    // Check that the three separate elements are captured and stored
    // correctly.
   
}  

