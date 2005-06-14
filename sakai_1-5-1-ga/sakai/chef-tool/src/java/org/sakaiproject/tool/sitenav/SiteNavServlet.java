/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitenav/SiteNavServlet.java,v 1.26 2005/02/14 08:30:31 csev.umich.edu Exp $
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

// package
package org.sakaiproject.tool.sitenav;

// imports
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Setup;
import org.sakaiproject.util.delivery.ParentLocationDelivery;

/**
* <p>SiteNaveServlet handles login, logout and site navigation.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.26 $
*/
public class SiteNavServlet extends VmServlet
{
	/** The number of site tabs displayed (the rest are in the more select). */
	protected final static int SITE_TABS_DISPLAYED = 4;

	/**
	 * initialize the servlet
	 *
	 * @param config the servlet config parameter
	 * @exception ServletException in case of difficulties
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	/**
	 * respond to an HTTP GET request
	 *
	 * @param req HttpServletRequest object with the client request
	 * @param res HttpServletResponse object back to the client
	 * @exception ServletException in case of difficulties
	 * @exception IOException in case of difficulties
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		dispatch(req, res);
	}

	/**
	 * respond to an HTTP POST request
	 *
	 * @param req HttpServletRequest object with the client request
	 * @param res HttpServletResponse object back to the client
	 * @exception ServletException in case of difficulties
	 * @exception IOException in case of difficulties
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		dispatch(req, res);
	}

	/**
	 * handle get and post communication from the user
	 * @param req HttpServletRequest object with the client request
	 * @param res HttpServletResponse object back to the client
	 */
	public void dispatch(HttpServletRequest req, HttpServletResponse res)
	{
		// check for the loged in status
		boolean loggedIn = (UsageSessionService.getSessionUserId() != null);
		boolean loggedOut = false;

		// if not yet logged in, try to login
		if (!loggedIn)
		{
			// attempt a login from request parameters
			processActionLogin(req);

			// refresh
			loggedIn = (UsageSessionService.getSessionUserId() != null);

			// if newly logged in, do a parent redirect to the user home
			if (loggedIn)
			{
				sendParentRedirect(res, ServerConfigurationService.getUserHomeUrl());
				return;
			}
		}

		// if not, see if we have a logout request
		else
		{
			loggedOut = processActionLogout(req, res);

			// refresh
			loggedIn = (UsageSessionService.getSessionUserId() != null);
		}

		if (loggedOut)
		{
			try
			{
				// redirect to the logged out url
				// This works because the target of the request for the logout button is the parent.
				res.sendRedirect(ServerConfigurationService.getLoggedOutUrl());
				return;
			}
			catch (IOException e)
			{
			}
		}
		// if the current usage session is not yet logged in, present the banner login form
		if (!loggedIn)
		{
			doLogin(req, res);
		}

		// if logged in, present the site nav
		else
		{
			doSiteNav(req, res);
		}
	}

	/**
	 * Attempt to login from request parameters.
	 * @param req HttpServletRequest object with the client request.
	 */
	protected void processActionLogin(HttpServletRequest req)
	{
		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);
		// if there are username and password parameters, authenticate
		String name = params.getString("username");
		String pw = params.getString("password");
		if ((name != null) && (pw != null))
		{
			// authenticate
			User user = UserDirectoryService.authenticate(name, pw);
			if (user != null)
			{
				Setup.login(user.getId(), req);
			}

			// send a parent redirect to the portal, to get to myWorkspace...
			CourierService.deliver(
				new ParentLocationDelivery(
					PortalService.getCurrentClientWindowId(null),
					ServerConfigurationService.getPortalUrl()));
		}
	}

	/**
	 * Attempt to logout, if requested.
	 * @param req HttpServletRequest object with the client request.
	 */
	protected boolean processActionLogout(HttpServletRequest req, HttpServletResponse res)
	{
		if ("logout".equals(((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("action")))
		{
			Setup.logout();
			clearSessionCookie(res);
			return true;
		}

		return false;
	}

	/**
	 * Clear the cookie orginally set by the portal.
	 * @param res The response object
	 */
	private void clearSessionCookie(HttpServletResponse res)
	{
	    Cookie sakaiSessionCookie  = null;
	    sakaiSessionCookie = new Cookie(UsageSessionService.SAKAI_SESSION_COOKIE, "");
	    sakaiSessionCookie.setPath("/");
	    sakaiSessionCookie.setMaxAge(0);  // delete cookie
	    res.addCookie(sakaiSessionCookie);	
	}
	
	/**
	 * Present a login response.
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 */
	protected void doLogin(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userIdText = ServerConfigurationService.getBoolean("email-for-user-id", true) ? "email" : "user id";
			setVmReference("userid_text", userIdText, req);

			Boolean topLogin = Boolean.valueOf(ServerConfigurationService.getBoolean("top.login", true));
			setVmReference("top_login", topLogin, req);

			String loginUrl = ServerConfigurationService.getLoginUrl();
			setVmReference("login_url", loginUrl, req);
		    
		    Boolean minimal = (Boolean) req.getAttribute("minimal");
		    if (minimal == null) {
		        String booleanParameter = req.getParameter("minimal");
		        Boolean minValue =null;
		        if (booleanParameter != null && "true".equals(booleanParameter)){
		            minValue = new Boolean("true");
		        }
		        else {
			        minValue = new Boolean("false");
		        }
		        minimal = minValue;
		    }
			
			/* This is handled by the portal code and can't be overridden here.
			 * But it isn't propagated?
			// pick up minimal configuration
			Boolean minimal = Boolean.valueOf(ServerConfigurationService.getBoolean("top.minimal", false));
			*/
			setVmReference("minimal", minimal, req);

			includeVm("/vm/sitenav/login.vm", req, res);
		}
		catch (Exception e)
		{
			Logger.warn(this +".doLogin(): ", e);
		}
	}

	/**
	 * Present a site nav response.
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 */
	protected void doSiteNav(HttpServletRequest req, HttpServletResponse res)
	{
		String siteId = ((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("site");
		if (	(siteId == null)
			||	(	SiteService.isUserSite(siteId)
				&&	(SiteService.getSiteUserId(siteId).equals(UsageSessionService.getSessionUserId()))))
		{
			setVmReference("curMyWorkspace",Boolean.TRUE, req);
			siteId = null;
		}

		if (siteId != null)
		{
			setVmReference("cursite", siteId, req);
		}

		// collect accessable sites
		List mySites = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
				null, null, null, org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC, null);

		if (mySites.size() > SITE_TABS_DISPLAYED)
		{
			int remove = mySites.size() - SITE_TABS_DISPLAYED;
			List moreSites = new Vector();
			for (int i = 0; i < remove; i++)
			{
				Site site = (Site) mySites.get(SITE_TABS_DISPLAYED);

				// add to more unless it's the current site (it will get an extra tag)
				if (!site.getId().equals(siteId))
				{
					moreSites.add(site);
				}

				// remove from the display list
				mySites.remove(SITE_TABS_DISPLAYED);
			}

			setVmReference("more", moreSites, req);
		}

		setVmReference("sites", mySites, req);

		setVmReference("portal", ServerConfigurationService.getPortalUrl(), req);

		// if the list of mySites that will be displayed (not counting 'more') does not contain siteId, make it the extra
		if (siteId != null)
		{
			boolean extra = true;
			for (Iterator i = mySites.iterator(); i.hasNext();)
			{
				Site site = (Site) i.next();
				if (site.getId().equals(siteId))
				{
					extra = false;
					break;
				}
			}

			if (extra)
			{
				try
				{
					Site site = SiteService.getSite(siteId);
					setVmReference("extra", site, req);
				}
				catch (IdUnusedException e)
				{
					Logger.warn(this + ".doSiteNav: cur site not found: " + siteId);
				}
			}
		}

		// pick up minimal configuration
	//	Boolean minimal= new Boolean(((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("minimal"));
		String min = ((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("minimal");
		if (min == null) {
		    min = "false";	    
		}
		
		Boolean minimal = new Boolean(min);

		setVmReference("minimal", minimal, req);
	
		// set standard no-cache headers
		setNoCacheHeaders(res);

		try
		{
			includeVm("/vm/sitenav/sitenav.vm", req, res);
		}
		catch (Exception e)
		{
			Logger.warn(this +".doSiteNav(): ", e);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitenav/SiteNavServlet.java,v 1.26 2005/02/14 08:30:31 csev.umich.edu Exp $
*
**********************************************************************************/
