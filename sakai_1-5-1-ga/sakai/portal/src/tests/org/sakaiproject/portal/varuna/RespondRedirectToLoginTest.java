/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/RespondRedirectToLoginTest.java,v 1.3 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
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
public class RespondRedirectToLoginTest extends GenericPathTest {
    
   
    //////////// tests
    
    // test that stay in same context.  E.g. if come as /site stay that way.
    
    // /varuna/site/SITEA then the redirect URL is the same
    // /varuna/page/SITEA
    
    /*
     * test that the new url is same as the old one.
     */
    
    // Check that the three separate elements are captured and stored
    // correctly.
    
    public void testSiteRedirectUrl() {
        bodySiteRedirectUrl("ASITE",null,null, false);        
    }
    
    public void testPageRedirectUrl() {
        bodySiteRedirectUrl(null,"APAGE",null, false);        
    }
    
    public void testToolRedirectUrl() {
        bodySiteRedirectUrl(null,null,"ATOOL", false);        
    }
    
    public void testSitePageRedirectUrl() {
        bodySiteRedirectUrl("ASITE","APAGE",null, false);        
    }
    
    public void testPageToolRedirectUrl() {
        bodySiteRedirectUrl(null,"APAGE","ATOOL", false);        
    }
    
    public void testSitePageToolRedirectUrl() {
        bodySiteRedirectUrl("ASITE","APAGE","ATOOL", false);        
    }
    
    ///////// test direct urls
    
    public void testDirectSiteRedirectUrl() {
        bodySiteRedirectUrl("ASITE",null,null, true);        
    }
    
    public void testDirectPageRedirectUrl() {
        bodySiteRedirectUrl(null,"APAGE",null, true);        
    }
    
    public void testDirectToolRedirectUrl() {
        bodySiteRedirectUrl(null,null,"ATOOL", true);        
    }
    
    public void testDirectSitePageRedirectUrl() {
        bodySiteRedirectUrl("ASITE","APAGE",null, true);        
    }
    
    public void testDirectPageToolRedirectUrl() {
        bodySiteRedirectUrl(null,"APAGE","ATOOL", true);        
    }
    
    public void testDirectSitePageToolRedirectUrl() {
        bodySiteRedirectUrl("ASITE","APAGE","ATOOL", true);        
    }
    
    public void bodySiteRedirectUrl(String siteId, String pageId, String toolId, 
            boolean direct) {
        // don't bother about session state 
        // use fixed value for portalurl
        // don't bother about redirect itself.
        String portalUrl = "/mock/PORTALURL";
        String loginUrl = "/mock/LOGINURL";
        
        String expectedUrl = portalUrl;
        
        if(siteId != null) {
            expectedUrl += "/site/"+siteId;
        }
        
        if (pageId != null) {
            expectedUrl += "/page/"+pageId;
        }
        if (toolId != null) {
            expectedUrl += "/tool/"+toolId;
        }
        
        VarunaServlet varuna = createVaruna();
        
        // setup the session 
        mockUsageSessionService.expects(once())
        .method("getSessionState")
        .with(eq(varuna.sessionStateAttributeName))
        .will(returnValue(mockSessionState.proxy()));
        
        // mock the portal url
        mockServerConfigurationService.expects(once())
        .method("getPortalUrl")
        .will(returnValue(portalUrl));
        
        setUrlExpectation(expectedUrl);
        
        // mock the loginUrl value
        // stub as may not be called depending on how mock the sendRedirect
        mockServerConfigurationService.stubs()
        .method("getLoginUrl")
        .will(returnValue(loginUrl));
      
        // check that the login url is sent on.
        mockHttpServletResponse.expects(once())
        .method("sendRedirect")
        .with(eq(loginUrl));
        
        varuna.respondRedirectToLogin(req,res,siteId,pageId, toolId,direct);
        
        /*
         *  no need to assert anything here. 'redirect' doesn't
         * return anything and the mocks will check things.
         */
        
    }
  
    ///// utilities
    /**
     * Set the appropriate url.
     * @param siteId
     * @param portalUrl
     */
    private void setUrlExpectation(String expectedUrl) {
        // the attribute set in the session should match the 
        // portal url
        
        mockSessionState.expects(once())
        .method("setAttribute")
        .with(eq("AuthnRedirect"),eq(expectedUrl));
    }
    
}  

