/**********************************************************************************
 *
 * $Header: /cvs/sakai/portal/src/java/org/sakaiproject/portal/varuna/VarunaServlet.java,v 1.21 2005/02/16 03:39:47 janderse.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

package org.sakaiproject.portal.varuna;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.service.framework.log.Logger;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.framework.session.UsageSessionService;
/* above are done */
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Validator;
import org.sakaiproject.vm.VmServlet;
import org.sakaiproject.util.Setup;

/**
 * <p>
 * VarunaServlet is ...
 * </p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.21 $
 */
public class VarunaServlet extends VmServlet
{
    /** Dependency: logging service */
    protected Logger m_logger = null;

    /**
     * 
     * @return Returns the logger implementation.  This will
     * self-inject from the static cover if the logger is not yet set.  
     * This approach makes testing easier by avoiding the dependence on
     * static covers.
     * 
     */
    public Logger getLogger()
    {
        if (m_logger == null) 
        {
            setLogger((Logger)
                    org.sakaiproject.service.framework.log.cover.Logger.getInstance());
        }
        return m_logger;
    }
   
    /**
     * @param m_logger The m_logger to set.
     */
    public void setLogger(Logger logger)
    {
        m_logger = logger;
    }
    
    protected ServerConfigurationService m_serverConfigurationService = null;
    
    public ServerConfigurationService getServerConfigurationService()
    {
        if (m_serverConfigurationService == null) 
        {
            setServerConfigurationService((ServerConfigurationService)
                    org.sakaiproject.service.framework.config.cover.ServerConfigurationService.getInstance());
        }
        return m_serverConfigurationService;
    }
   
    /**
     * @param m_ServerConfigurationService The m_ServerConfigurationService to set.
     */
    public void setServerConfigurationService(ServerConfigurationService ServerConfigurationService)
    {
        m_serverConfigurationService = ServerConfigurationService;
    }
    
    
    /** Dependency: logging service */
    protected UsageSessionService m_usageSessionService = null;

    /**
     * 
     * @return Returns the UsageSessionService implementation.  This will
     * self-inject from the static cover if the UsageSessionService is not yet set.  
     * This approach makes testing easier by avoiding the dependence on
     * static covers.
     * 
     */
    public UsageSessionService getUsageSessionService()
    {
        if (m_usageSessionService == null) 
        {
            setUsageSessionService((UsageSessionService)
             org.sakaiproject.service.framework.session.cover.UsageSessionService.getInstance());
        }
        return m_usageSessionService;
    }
   
    /**
     * @param m_UsageSessionService The m_UsageSessionService to set.
     */
    public void setUsageSessionService(UsageSessionService UsageSessionService)
    {
        m_usageSessionService = UsageSessionService;
    }
    
    /** Dependency: logging service */
    protected SiteService m_siteService = null;

    /**
     * 
     * @return Returns the SiteService implementation.  This will
     * self-inject from the static cover if the SiteService is not yet set.  
     * This approach makes testing easier by avoiding the dependence on
     * static covers.
     * 
     */
    public SiteService getSiteService()
    {
        if (m_siteService == null) 
        {
            setSiteService((SiteService) 
                    org.sakaiproject.service.legacy.site.cover.SiteService.getInstance());
        }
        return m_siteService;
    }
   
    /**
     * @param m_SiteService The m_SiteService to set.
     */
    public void setSiteService(SiteService SiteService)
    {
        m_siteService = SiteService;
    }
    
    // A new copy is created for each invocation. We create one here so that
    // one is always available for testing, even when testing single methods.

    PortalUrl url = new PortalUrl(SiteService.SITE_ERROR);

    /** ********************************** */

    /** The "face" (vm file) to use. */
    protected boolean debug_parseDisplayPath = false;

    protected String m_face = "portal3";

    protected String sessionStateAttributeName = "org.sakaiproject.tool.authn.LoginServlet";

    /**
     * Initialize the servlet.
     * 
     * @param config
     *            The servlet config.
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        String face = config.getInitParameter("face");
        if ((face != null) && (face.trim().length() > 0))
        {
            m_face = face.trim();
        }
    }

    /**
     * Handle a post - just like a get.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException
    {
        doGet(req, res);
    }

    /**
     * Handle a get - respond with the requested portal display
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException
    {

        Site site = null;
        String siteId = null;
        int displayLevel = 0;

        try
        {
            Setup.setup(req, res, true);
        }
        catch (Exception e1)
        {
            getLogger().warn("error in setup");
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("error in setup", e1);
            }
        }

        // parse the request
        ParameterParser params = (ParameterParser) req
                .getAttribute(ATTR_PARAMS);

        url = new PortalUrl(SiteService.URL_ERROR);

        try
        {

            displayLevel = url.parseDisplayPath(req, params);

            // set display level as a request attribute.

            req.setAttribute("displayPrefix", url.getDisplayPrefix());

            // set the level of context that should be shown on the output page.
            setVmReference("displayLevel", new Integer(displayLevel), req);

            // Determine if should see the portal level branding from Sakai.
            // it is only possible to get the full site nav if displaying the
            // whole
            // portal.

            Boolean minimal = Boolean.FALSE;

            if (displayLevel <= 1)
            {
                minimal = Boolean.valueOf(getServerConfigurationService()
                        .getBoolean("top.minimal", false));
            }
            else
            {
                minimal = Boolean.TRUE;
            }

            req.setAttribute("minimal", minimal);

            setVmReference("minimal", minimal, req);

            ////// deal with user.
            // who's the user?
            String userId = getUsageSessionService().getSessionUserId();

            boolean loggedIn = (userId != null);
            setVmReference("loggedIn", Boolean.valueOf(loggedIn), req);

            // set siteId
            siteId = findSiteId(params, userId, loggedIn);

            // get the page - optional
            String pageId = params.getString("page");

            // if not specified in the param, try the path
            if (pageId == null)
            {
                pageId = url.getPathPageId();
            }

            String tid = url.getPathToolId();

            // Is there a specific site which the user can access?
            // if missing or not accessable, use the error site

            site = resolveSiteAndPermission(loggedIn, siteId);

            // If site is null that means that need to redirect (for login) to
            // resolve the site. The redirect will come back to this
            // servlet after authentication.
            if (site == null)
            {
                respondRedirectToLogin(req, res, (displayLevel <= 3 ? siteId
                        : null), (displayLevel <= 4 ? pageId : null),
                        (displayLevel <= 5 ? tid : null), url.direct);
                return;
            }

            String sessionStateKey = params.getPath();
            SitePage statePage = null;
            // Get any stored session state for this site.

            // need a site id.
            if (site != null)
            {
                sessionStateKey = site.getId();
            }
            else
            {
                // get it from the page?
                if (pageId != null)
                {
                    statePage = getSiteService().findPage(pageId);
                    sessionStateKey = statePage.getContainingSite().getId();
                }
                else
                {
                    if (tid != null)
                    {
                        sessionStateKey = (getToolConfigFromPid(tid))
                                .getSiteId();
                    }
                    else
                    {
                        getLogger().warn("useless url");
                        throw new Exception("userless url");
                    }
                }
            }

            SessionState state = getUsageSessionService().getSessionState("portal."
                    + sessionStateKey);

            // all right! we have a valid site. so send that on.
            setVmReference("site", site, req);

            // Fill out the rest of the page and tool information
            // It might be an empty site, and there is nothing
            // to do here in that case.
            //         if (pageId != null || tid != null || site != null)
            if (site != null)
            {
                fillSitePageInfo(req, site, pageId, tid, state);
            }

            setupPresenceAndBottomNav(req);
            
            // Retrieve the user's portal refresh preference and set into VM for usage
            if ( loggedIn ) 
            {
                Preferences prefs = PreferencesService.getPreferences( userId);
        			int i = getIntegerPref(PortalService.SERVICE_NAME, PortalService.PREF_REFRESH,prefs);
	    			setVmReference("suppressRefresh", Boolean.valueOf(i == PortalService.PREF_REFRESH_SUPPRESS), req);
            }
            
            // Set the global session cookie
            if ( loggedIn )
            {
            		setSakaiSessionCookie(req, res); 
            }
            
        }
        catch (Exception e)
        {
            getLogger().warn("error handling url");
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("error handling url", e);
            }

            site = getUrlErrorSite();
        }

        // get the "face" - vm file to use
        String face = params.getString("face");
        if (face == null)
        {
            face = m_face;
        }

        // set standard no-cache headers
        setNoCacheHeaders(res);

        // With the new context urls there might not be an explict siteId in the
        // parameter parser. Set one here if have one.
        if (siteId != null)
        {
            req.setAttribute("siteId", siteId);
        }

        includeVm("/vm/" + face + ".vm", req, res);
    }

    /**
     * Fill in the page (default if necessary) and the tools, generating the
     * list of tools. This assumes that it won't be called unless there is a
     * valid page and/or tool.
     * 
     * @param req
     * @param site
     * @param pageId
     * @param tid
     * @param state
     */
    private void fillSitePageInfo(HttpServletRequest req, Site site,
            String pageId, String tid, SessionState state)
    {
        // get the site page
        SitePage page = null;
        if (pageId != null)
        {
            page = site.getPage(pageId);
        }

        // if no page in request, try the page last visited for this site
        if (page == null)
        {
            pageId = (String) state.getAttribute("page");
            page = site.getPage(pageId);
        }

        // if no page in request, or an invalid one, use the first page of
        // the site (if there are any pages).
        if (page == null && site != null && !site.getPages().isEmpty())
        {
            page = (SitePage) site.getPages().get(0);
        }

        // store the page last visited for this site so can return
        // to it later. - use state keyed by site
        if (page != null)
        {
            state.setAttribute("page", page.getId());
            setVmReference("page", page, req);
        }

        if (tid == null && page != null)
        {
            // don't have a specific tool id so setup all the
            // tools for this page.
            // compute a set of tool ids for this page &tool=x&tool=y...
            StringBuffer tools = new StringBuffer();
            for (Iterator i = page.getTools().iterator(); i.hasNext();)
            {
                ToolConfiguration tool = (ToolConfiguration) i.next();
                tools.append("&tool=");
                tools.append(tool.getId());
            }
            if (tools.length() > 0)
            {
                setVmReference("tools", tools.toString(), req);
            }
        }
        else if (tid != null)
        {
            // have a specific tool in mind. so get the tool conf
            // and pass it on.
            ToolConfiguration tc = getToolConfigFromPid(tid);
            setVmReference("toolIdConfig", tc, req);
        }
        else
        {
            // have no page and no tid so can't do anything.
        }
    }

    /**
     * @param req
     */
    private void setupPresenceAndBottomNav(HttpServletRequest req)
    {
        // is presence display enabled?
        boolean showPresence = getServerConfigurationService().getBoolean(
                "display.users.present", true);

        setVmReference("showPresence", Boolean.valueOf(showPresence), req);

        // setup the objects the bottom nav needs to access.
    //    setVmReference("config", getServerConfigurationService().getInstance(), req);
        setVmReference("config", getServerConfigurationService(), req);
        setVmReference("validator", new Validator(), req);

        // get the bottomnav
        String[] bottomnav = getServerConfigurationService().getStrings("bottomnav");
        if (bottomnav != null)
        {
            setVmReference("bottomnav", bottomnav, req);
        }

        // deal with bottom copyright

        setBottomCopyright(req);
    }

    /*
     * setBottomCopyright - Add the bottom copyright information to the vm
     * context.
     */

    private void setBottomCopyright(HttpServletRequest req)
    {

        // get a new config string from the config service for the bottom
        // copyright text

        String bottomCopyrightText = getServerConfigurationService()
                .getString("bottom.copyrighttext");

        // Put it in a vm variable. If this doesn't have a value in the
        // vm context the copyright line will be dropped from the output.

        if (bottomCopyrightText != null)
        {
            setVmReference("bottomcopyrighttext", bottomCopyrightText, req);
        }

    }

    /**
     * Find a site id. It might be specified in the path or might be a default.
     * 
     * @param params
     * @param userId
     * @param loggedIn
     * @return
     */
    protected String findSiteId(ParameterParser params, String userId,
            boolean loggedIn)
    {
        // which site - use the myWorkspace or gateway if missing
        String siteId = params.getString("site");

        // if no site param, but it was recognized in the path, use it
        if (siteId == null)
        {
            siteId = url.getPathSiteId();
        }

        // if no site specified, use a default
        if (siteId == null || siteId.length() == 0)
        {
            // if logged in, send them to their worksite
            if (loggedIn)
            {
                siteId = getSiteService().getUserSiteId(userId);
            }
            // otherwise send them to the gateway
            else
            {
                siteId = getServerConfigurationService().getGatewaySiteId();
            }
        }
        return siteId;
    }

    /*
     * resolve site: get site from site id. if ok then return it. If got
     * permission error then return null if user is logged in. Otherwise return
     * error site (but handle error site lookup problems)
     * 
     * if site did not exist then try to get "~"+siteid. If exists then return
     * else if id error return error site else if permission error then return
     * null for that site?
     */

    /**
     * @param loggedIn
     * @param siteId
     * @param site
     * @return
     * @throws PermissionException
     * @throws IdUnusedException
     */
    // make sure that the site exists and that the user has permission to see
    // it.
    // If they don't have permission then return the site as null.
    protected Site resolveSiteAndPermission(boolean loggedIn, String siteId)
            throws PermissionException, IdUnusedException
    {
        // see if can visit the site

        Site site = null;
        try
        {
            site = getSiteService().getSiteVisit(siteId);
        }

        /// site exists but user doesn't have permission
        catch (PermissionException e)
        {
            // if they aren't logged in give them the chance.
            if (!loggedIn)
            {
                site = null;
            }
            else
            {
                site = getErrorSite();
            }
        }

        // site doesn't exist so check for a user site (unless
        // this is a user site.
        catch (IdUnusedException e)
        {
            if (!siteId.substring(0, 1).equals("~"))
                return (resolveSiteAndPermission(loggedIn, "~" + siteId));
            else
            {
                site = getErrorSite();
            }
        }
        return site;
    }

    /**
     * @return
     */
    private Site getErrorSite()
    {
        Site site = null;
        // If you can't get the error site things are baaad.
        try
        {
            site = getSiteService().getSiteVisit(getSiteService().SITE_ERROR);
        }
        catch (IdUnusedException e1)
        {
            getLogger().error("error site unknown", e1);
            throw new RuntimeException(e1);
        }
        catch (PermissionException e1)
        {
            getLogger().error("no permission to get error site", e1);
            throw new RuntimeException(e1);
        }
        return site;
    }

    /**
     * @return
     */
    private Site getUrlErrorSite()
    {
        Site site = null;
        // If you can't get the error site things are baaad.
        try
        {
            site = getSiteService().getSiteVisit(getSiteService().URL_ERROR);
        }
        catch (IdUnusedException e1)
        {
            getLogger().error("getUlrErrorSite: urlError site unknown!", e1);
            site = getErrorSite();
        }
        catch (PermissionException e1)
        {
            getLogger().error("getUrlErrorSite: no permission to get site", e1);
            throw new RuntimeException(e1);
        }
        return site;
    }

    /**
     * Make a redirect to the login url.
     * 
     * @param req
     *            HttpServletRequest object with the client request.
     * @param res
     *            HttpServletResponse object back to the client.
     * @param siteId
     *            The site id from the request.
     * @param pageId
     *            The page id from the request.
     * @param tid
     *            The specific id for the placemement of this tool.
     */

    // This url should have all the elements specified in it.
    protected void respondRedirectToLogin(HttpServletRequest req,
            HttpServletResponse res, String siteId, String pageId, String tid,
            boolean direct)
    {
        // Note: the dependency on the class name and REDIRECT string from
        // chef-tool's LoginServlet

        SessionState state = getUsageSessionService()
                .getSessionState(sessionStateAttributeName);

        // form the redirect after auth
        String url = getServerConfigurationService().getPortalUrl();

        if (siteId != null)
        {
            url += "/site/" + siteId;
        }

        if (pageId != null)
        {
            url += "/page/" + pageId;
        }

        if (tid != null)
        {
            url += "/tool/" + tid;
        }

        // store the url to return to after login in the session for redirect
        state.setAttribute("AuthnRedirect", url);

        // redirect to our login place
        try
        {
            res.sendRedirect(getServerConfigurationService().getLoginUrl());
        }
        catch (IOException i)
        {
            getLogger().warn("error redirecting to login");
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("error redirecting to login", i);
            }
        }
    }

    /*
     *  
     */
    public ToolConfiguration getToolConfigFromPid(String pid)
    {
        ToolConfiguration tool = null;
        if (pid != null)
        {
            tool = getSiteService().findTool(pid);
            if (tool != null)
            {
                return tool;
            }
        }
        return null;
    }
   
    	/**
    	* Retrieve a preference and convert it to an integer
    	* @param pres_base The name of the group of properties (i.e. a service name)
    	* @param type The particular property
    	* @param prefs The full set of preferences for the current logged in user.
    	*/
    	protected int getIntegerPref(String pref_base, String type, Preferences prefs)
    	{
    		ResourceProperties props = prefs.getProperties(pref_base);
    		int i = 0;
    		try
    		{
    			i = (int) props.getLongProperty(type);
    		}
    		catch (Throwable ignore) {};
    		return i;
    	}	// getIntegerPref
    	
    	/** 
	 * Set the System-wide Sakai Session Cookie for use from servlet filters and 
    	 * other non-TPP tools.
    	 * 
    	 * Note: This cookie should not be used within standard Sakai tools as 
    	 * the framework takes care of all necessary session management.
    	 */
    	
    	private void setSakaiSessionCookie(HttpServletRequest req, HttpServletResponse res) 
    	{
    		    Cookie sakaiSessionCookie  = null;
    		    
    		    if ( m_usageSessionService == null ) return;

    		    String sessionId = m_usageSessionService.getSessionId();
    		    
    		    // System.out.println("Setting "+UsageSessionService.SAKAI_SESSION_COOKIE+":"+sessionId);
    		    sakaiSessionCookie = new Cookie(UsageSessionService.SAKAI_SESSION_COOKIE, sessionId);
    		    sakaiSessionCookie.setPath("/");
    		    if ( sessionId == null ) 
		    {
    		    		sakaiSessionCookie.setMaxAge(0);  // delete cookie
		    } 
 		    else
		    {
 		    		sakaiSessionCookie.setMaxAge(-1);  // Until browser close
		    }
    		    res.addCookie(sakaiSessionCookie);	
    	}

}

/*******************************************************************************
 * 
 * $Header:
 * /cvs/sakai/portal/src/java/org/sakaiproject/portal/varuna/VarunaServlet.java,v
 * 1.15 2005/01/06 22:01:26 dlhaines.umich.edu Exp $
 *  
 ******************************************************************************/
