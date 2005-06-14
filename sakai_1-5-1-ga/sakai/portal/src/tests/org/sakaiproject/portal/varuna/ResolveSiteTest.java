/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/ResolveSiteTest.java,v 1.3 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 10, 2004
 *
 * Junit / jmock tests for the varuna short url 
 * handler.  This tests the resolveSite method.
 * 
 */
package org.sakaiproject.portal.varuna; 

import org.jmock.*;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;


/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResolveSiteTest  extends GenericPathTest {
   
 
    /**
     * @param name
     */
    public ResolveSiteTest(String name) {
        super(name);
    }
    
    // find 'aSite' right away
    public void testResolveSiteExists() {
        boolean loggedIn = false;
        boolean expectPermissionException = false;
        // set up an expectation for the mockSiteService
        mockSiteService.expects(once()) 
                .method("getSiteVisit") 
                .with(eq("aSite")) 
                .will(returnValue((Site) mockSite.proxy()));

        Site site = runResolveSite("aSite", loggedIn, expectPermissionException);
       Site right = (Site) mockSite.proxy();
        assertEquals("wrong site returned", site, right);
    }
    
    /////// permission errors
    // should get null site back if there is no permission 
    // and not logged in.
    public void testResolveSiteNotPermittedAnon() {

        boolean loggedIn = false;
        boolean expectPermissionException = false;
        
        throwPermissionException("aSite");
        Site site = runResolveSite("aSite",loggedIn, expectPermissionException);
       
        assertNull("null site via permission exception",site);
    }
    
    // if permission problem but logged in then
    // should get error site.
    public void testResolveSiteNotPermittedLoggedIn() {
        boolean loggedIn = true;
        boolean expectPermissionException = false;
        
        throwPermissionException("aSite");
        
        // setup so that second call can return the error site
        Site errorSite = (Site) mockErrorSite.proxy();
        setupErrorSiteReturn(errorSite);
        
       Site site = runResolveSite("aSite", loggedIn, expectPermissionException);
        
        assertEquals("should return error site", errorSite, site);
        
    }
    
    ///////////
    // Test IdUnused conditions  All of these fail to find
    // a site on first lookup
    
    
    /// Find user site after failing to find site
    public void testResolveSiteCheckUserFound() {
        boolean loggedIn = false;
        boolean expectPermissionException = false;
        
    //    Site site = null;
        // set up an expectation for the mockSiteService so that first 
        // call will throw IdUnused exception.
        throwIdUnused("aSite");
        
        // second call will return a found site
        mockSiteService.expects(once()) // one call
        .method("getSiteVisit") // of method getSiteVisit
        .with(eq("~aSite")) // with argument string aSite
        // returns a proxy mockSite.
        .will(returnValue(mockSite.proxy()));
        
        Site site = runResolveSite("aSite", loggedIn, expectPermissionException);
        assertEquals("find user site ~xx",site,mockSite.proxy());
    }
    
    // fail to find user after failing to find site.
    public void testResolveSiteCheckUserNone() {
    //    Site site = null;
        boolean loggedIn = false;
        boolean expectPermissionException = false;
        
        // set up an expectation for the mockSiteService so that first 
        // call will throw IdUnused exception.
        
        // can't find this site
        throwIdUnused("aSite");
        // can't find site as user site
        throwIdUnused("~aSite");
        
        // return the error site
        mockErrorSite = new Mock(Site.class);
        Site errorSite = (Site) mockErrorSite.proxy();      
        setupErrorSiteReturn(errorSite);

        Site site = runResolveSite("aSite", loggedIn, expectPermissionException);
        assertEquals("return error site if site and ~site don't exist.",
                errorSite,site);
    }
    
    
    // Found second site, but permission exception.
    public void testResolveSiteCheckUserPermissionException() {
        boolean loggedIn = true;
        boolean expectPermissionException = true;
        
        // can't find this site
        throwIdUnused("aSite");
        // not allowed to see this site
        throwPermissionException("~aSite");
        
        // return the error site
        mockErrorSite = new Mock(Site.class);
        Site errorSite = (Site) mockErrorSite.proxy();
        setupErrorSiteReturn(errorSite);
        
        Site site = runResolveSite("aSite", loggedIn, expectPermissionException);
        assertEquals("return error site if no site and no permission for ~site.",
                errorSite,site);
        
    }
    
    ////////////////////////////////////////////
    
    /**
     * @param siteId
     * @param loggedin TODO
     * @param expectPermissionException TODO
     * This will allocation a varuna object, run it, and capture 
     * exceptions.  In general those exceptions should be captured
     * by resolveSite and are errors, but it is possible for 
     * resolveSite to generate a PermissionException so we can check
     * for that here.
     * @return
     */
    private Site runResolveSite(String siteId, boolean loggedin, boolean expectPermissionException) {
        
        VarunaServlet varuna = createVaruna();
        
        Site site = null;
        
        try {
            site = varuna.resolveSiteAndPermission(loggedin, siteId);
        } catch (PermissionException e) {
            System.out.println("in rRS perm ex");
            if (!expectPermissionException)
                fail("permission exception caught in runResolveSite");
        } catch (IdUnusedException e) {
            fail("IdUnusedException caught in runResolveSite");
        } catch (Exception e) {
            fail("exception: caught in runResolveSite" + e.getCause());
        }
        
        return site;
    }
    
    /**
     * @param errorSite
     */
    private void setupErrorSiteReturn(Site errorSite) {
        mockSiteService.expects(once()) // one call
        .method("getSiteVisit") // of method getSiteVisit
        .with(eq(SiteService.SITE_ERROR)) // with string site id
        // returns the error mockSite
        .will(returnValue(errorSite));
    }
    
    /**
     * @param siteId TODO
     * 
     */
    
    private void throwException(String siteId,Throwable t) {
        mockSiteService.expects(once()) // one call
        .method("getSiteVisit") // of method getSiteVisit
        .with(eq(siteId)) // specifying this site
        .will(throwException(t));
    }
    
    private void throwIdUnused(String siteId) {
        throwException(siteId,new IdUnusedException("OK EXPECTED: jmock: IdUnused from throwIdUnused"));
    }
    
    private void throwPermissionException(String siteId) {
        throwException(siteId,
                new PermissionException("OK EXPECTED: jmock: PermissionException from throwPermissionException",
                        null, null));
    }
    
}
