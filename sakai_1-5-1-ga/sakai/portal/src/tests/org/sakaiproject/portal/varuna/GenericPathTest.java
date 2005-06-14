/*
 * Created on Jan 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.portal.varuna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionState;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenericPathTest extends MockObjectTestCase
{

    protected Mock mockLogger = null;
    protected Logger logger = null;
    protected Mock mockParameterParser = null;
    protected ParameterParser pp = null;
    protected Mock mockSiteService = null;
    protected SiteService ss = null;
    protected Mock mockSitePage = null;
    protected SitePage sp = null;
    protected Mock mockSite = null;
    protected Site s = null;
    protected Mock mockToolConfiguration = null;
    protected ToolConfiguration tc = null;
    protected Mock mockServerConfigurationService = null;
    protected ServerConfigurationService scs = null;
    protected Mock mockUsageSessionService = null;
    protected UsageSessionService uss = null;
    protected Mock mockErrorSite = null;
    protected Site errorSite = null;
    protected Mock mockSessionState = null;
    protected SessionState sessionState = null;
 
    
    Mock mockHttpServletRequest = null;
    protected HttpServletRequest req = null;
    Mock mockHttpServletResponse = null;
    protected HttpServletResponse res = null;
    protected final String defaultErrorSite = "!urlError";
    final String defaultPage = "DEFAULT_PAGE";
    final String defaultSite = "DEFAULT_SITE";
    final String defaultTool = "DEFAULT_TOOL";

    public GenericPathTest() {
        super();
    }
    
    public GenericPathTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
    
        mockParameterParser = new Mock(ParameterParser.class);
        pp = (ParameterParser) mockParameterParser.proxy();
        
        mockSiteService = new Mock(SiteService.class);
        ss = (SiteService) mockSiteService.proxy();
    
        mockLogger = new Mock(Logger.class);
        logger = (Logger) mockLogger.proxy();
    
        mockHttpServletRequest = new Mock(HttpServletRequest.class);
        req = (HttpServletRequest) mockHttpServletRequest.proxy();
        
        mockHttpServletResponse = new Mock(HttpServletResponse.class);
        res = (HttpServletResponse) mockHttpServletResponse.proxy();
        
        
        mockSitePage = new Mock(SitePage.class);
         sp = (SitePage) mockSitePage.proxy();
        
        mockSite = new Mock(Site.class);
         s = (Site) mockSite.proxy();
        
        mockToolConfiguration = new Mock(ToolConfiguration.class);
         tc = (ToolConfiguration) mockToolConfiguration.proxy();
  
        mockServerConfigurationService = new Mock(ServerConfigurationService.class);
         scs = (ServerConfigurationService) mockServerConfigurationService.proxy();
    
        mockUsageSessionService = new Mock(UsageSessionService.class);
         uss = (UsageSessionService) mockUsageSessionService.proxy();
         
         mockErrorSite = new Mock(Site.class);
         Site errorSite = (Site) mockErrorSite.proxy();

         mockSessionState = new Mock(SessionState.class);
         SessionState sessionState = (SessionState) mockSessionState.proxy();
    }

    protected void tearDown() throws Exception
    {
    
        mockParameterParser.verify();
        mockParameterParser = null;
        
        mockSiteService.verify();
        mockSiteService = null;
    
        mockLogger.verify();
        mockLogger = null;
    
        mockHttpServletRequest.verify();
        mockHttpServletRequest = null;
       
        mockHttpServletResponse.verify();
        mockHttpServletResponse = null;
 
        mockSitePage.verify();
        mockSitePage = null;
        
        mockToolConfiguration.verify();
        mockToolConfiguration = null;
        

        mockServerConfigurationService.verify();
        mockServerConfigurationService = null;
    
        mockUsageSessionService.verify();
        mockUsageSessionService = null;
        
        mockErrorSite.verify();
        mockErrorSite = null;
        
        mockSessionState.verify();
        mockSessionState = null;
        
        super.tearDown();
    }

    protected PortalUrl createPortalUrl()
    {
        mockLoggerSetup();
        PortalUrl pu = new PortalUrl(defaultErrorSite);
        pu.setLogger(logger);
        return (pu);
    }
    
    protected VarunaServlet createVaruna()
    {
        mockLoggerSetup();
        VarunaServlet v = new VarunaServlet();
        v.setLogger(logger);
        v.setServerConfigurationService(scs);
        v.setSiteService(ss);
        v.setUsageSessionService(uss); 
        return (v);
    
    }

    void mockLoggerSetup()
    {  
        // setup up so that can find the site page for a page
        mockLogger.stubs().method("isDebugEnabled").will(returnValue(false));
    }
    
    void mockLoggerSetupWarn(String msg)
    {  
        // setup up so that can find the site page for a page
        mockLogger.stubs().method("warn").with(stringContains(msg));
    }

    protected void setupPath(String path)
    {
        mockParameterParser.expects(once()) 
        .method("getPath") 
        .will(returnValue(path));
    }

}
