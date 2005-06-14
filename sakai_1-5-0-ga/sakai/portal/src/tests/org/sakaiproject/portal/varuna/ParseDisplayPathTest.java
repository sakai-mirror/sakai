/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/ParseDisplayPathTest.java,v 1.3 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 11, 2004
 *
 * These are jmock junit tests for the new short url parser.  They 
 * rely on Aspectj aspects to capture the outside calls and return
 * the mock values instead.
 *
 */
package org.sakaiproject.portal.varuna;

import org.sakaiproject.util.ParameterParser;

/**
 * @author haines
 *
 */

public class ParseDisplayPathTest extends GenericPathTest {
 
    
    public void testPathTestGeneralVarunaSiteTrailingSlash() {
        bodyPathTestGeneral("/portal/site/",false,1,"","",null);
    }
    
    public void testPathTestGeneralVarunaSitePage() {
        bodyPathTestGeneral("/portal/site/ASITE/page",false,1,"ASITE",null,null);
    }
    
    public void testPathTestGeneralVarunaSitePageTrailingSlash() {
        bodyPathTestGeneral("/portal/site/ASITE/page/",false,1,"ASITE",null,null);
    }
    
    public void testPathTestGeneralVarunaSitePageAPAGE() {
        bodyPathTestGeneral("/portal/site/ASITE/page/APAGE",false,1,"ASITE","APAGE",null);
    }
    
    public void testPathTestGeneralVarunaPageAPAGE() {
        bodyPathTestGeneral("/portal/page/APAGE",false,1,null,"APAGE",null);
    }
    
    
    //	template to see if parse the prefix of a url correctly.
    public void bodyPathTestGeneral(String path, 
            boolean direct, int expectedDisplayLevel, 
            String siteId, String pageId, String toolId) {
        
        int displayLevel = -1;
        
        String defaultSiteId = "SITEFOR"+pageId;
        String defaultPageId = "PAGEFOR"+toolId;
        
        //  for top level 
        setupPath(path);
        
        PortalUrl portalurl = createPortalUrl();
        
        displayLevel = portalurl.parseDisplayPath(req, 
                (ParameterParser) pp);
        
        assertEquals("wrong displayLevel",expectedDisplayLevel,displayLevel);
        assertEquals("missed direct",direct,portalurl.direct);
        assertEquals("bad error site",defaultErrorSite,portalurl.errorSiteId);
        assertEquals("bad site",siteId,portalurl.pathSiteId);
    }

    public void testPathSiteGeneral() {
        bodyPathSite("/portal/site",1);
    }
    public void testPathGallery() {
        bodyPathSite("/portal/gallery",1);
    }
    
    
    public void testPathSiteSpecific() {
        bodyPathSiteSpecific("/portal/site","SITEA");
    }
    
    public void testPathGroupSpecific() {
        bodyPathSiteSpecific("/portal/group","SITEA");
    }
    
    public void testPathSiteSpecificShort() {
        bodyPathSiteSpecific("/site","SITEA");
    }
    
    public void testPathGroupSpecificShort() {
        bodyPathSiteSpecific("/group","SITEA");
    }
    
    public void testPathSiteGeneralShort() {
        bodyPathSite("/site",3);
    }
    public void testPathGalleryShort() {
        bodyPathSite("/gallery",2);
    }
    

    public void testPathVarunaSlash() {
        bodyPathSite("/portal/",1);
    }
    
    public void testPathVarunaSlashSpace() {
        bodyPathSite("/portal/ ",1);
    }
    
    // Check that the site is extracted from the path correctly.
    public void bodyPathSiteSpecific(String path, String site) {
        
        setupPath(path+"/"+site);

        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong site captured",site,portalurl.getPathSiteId());
        assertEquals("bad error site",defaultErrorSite,portalurl.errorSiteId);
    }
    
    // check that the correct display level is extracted
    public void bodyPathSite(String path,int expectedDisplayLevel) {
        
        // what value will come out for minimal?
        boolean minimalValue = false;
        
        // this is the path to be parsed
        setupPath(path);
   
        // run the method

        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong displayLevel",expectedDisplayLevel,displayLevel);
        assertEquals("bad error site",defaultErrorSite,portalurl.errorSiteId);
    }
    
    // skipping the /user processing.  This is not required but is currently in the 
    // code.
   
    /*
     * Test that the page element can be obtained from the url.
     */
    public void testPathPageSpecific() {
        int expectedDisplayLevel = 1;
        String path = "/portal/page";
        String page = "TESTPAGE";
        setupPath(path+"/"+page);
        
        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong displayLevel",expectedDisplayLevel,displayLevel);
        assertEquals("wrong page captured",page,portalurl.getPathPageId());
        assertEquals("wrong site captured",null,portalurl.getPathSiteId());
        assertEquals("bad error site",defaultErrorSite,portalurl.errorSiteId);
    }
    
    /*
     * Test that the correct tool element can be obtained from the url.
     */
    public void testPathToolSpecific() {
        int expectedDisplayLevel = 1;
        String path = "/portal/tool";
        String tool = "TESTTOOL";
        setupPath(path+"/"+tool);
 
        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong displayLevel",expectedDisplayLevel,displayLevel);
        assertEquals("wrong tool captured",tool,portalurl.getPathToolId());
    }
    
    
    ////////////////////////////////
    /*
     * test logger message for thrown error
     */
    
    // This url is fine, but this is to see what happens if throw an error.
    public void testParsePathExceptionLogger() {
        
        int expectedDisplayLevel = 4;
        String path = "/portal/pageSSS";
        String urlErrorSite = "!urlError";
        
        setupPath(path);
        mockLoggerSetupWarn("error parsing url: path: ["+path+"]");
 
        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong pathSite for url error",urlErrorSite,portalurl.getPathSiteId());
      
    }
    
    ////////////////////////////////
    /*
     * test logger message for thrown error with a varity of bad
     * urls.
     */
    
    public void testParsePathLogParseErrorA() {
        bodyParsePathLogParseError("/icky/url");
    }
    
    public void testParsePathLogParseErrorB() {
        bodyParsePathLogParseError("/vickyl");
    }
    
    public void testParsePathLogParseErrorC() {
        bodyParsePathLogParseError("/vicky/");
    }
    
    public void testParsePathLogParseErrorF() {
        bodyParsePathLogParseError("/portal/siteX");
    }
    
    public void testParsePathLogParseErrorG() {
        bodyParsePathLogParseError("/portal/slkdjf/~admin");
    }
    public void testParsePathLogParseErrorH() {
        bodyParsePathLogParseError("/portal/slkdjf/~admin/");
    }
 
    
    public void bodyParsePathLogParseError(String path) {
        
        int expectedDisplayLevel = 1;
        
        setupPath(path);
        mockLoggerSetupWarn("error parsing url: path: ["+path+"]");

        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong display level",expectedDisplayLevel,displayLevel);
      
    }
    
    //////////////// test /varuna /direct processing
    // ie trim out the initial element and track that it was there.
    
   
    
    // check that the correct display level is extracted
    // ensure that get the right values and know it was direct.
    public void testFindDirectSite(String path,int expectedDisplayLevel) {
        
        path = "/direct/site/~admin";
        
        // this is the path to be parsed
        setupPath(path);
               
        // run the method

        PortalUrl portalurl = createPortalUrl();
        int displayLevel = portalurl.parseDisplayPath(null, pp);
        assertEquals("wrong displayLevel",expectedDisplayLevel,displayLevel);
    }
    
    
    
    /////////////////////////////  utility routines
    
    /**
     * @param urlLevel TODO
     * Set the url display level value that was extracted
     * from the url.  If nothing is specified the value is 0.
     * 
     */
    private void urlDisplayLevel(int urlLevel) {
        //
        //        // set the url displayLevel response
        //        // display level from url (default to 0)
        mockParameterParser.expects(once()) 
        .method("getInt") 
        .will(returnValue(urlLevel));
    }
   
}  
