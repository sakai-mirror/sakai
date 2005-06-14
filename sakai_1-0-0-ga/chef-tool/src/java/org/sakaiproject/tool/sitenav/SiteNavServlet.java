/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitenav/SiteNavServlet.java,v 1.19 2004/10/11 00:33:02 ggolden.umich.edu Exp $
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
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.VmServlet;
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
* @version $Revision: 1.19 $
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
			loggedOut = processActionLogout(req);

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
	protected boolean processActionLogout(HttpServletRequest req)
	{
		if ("logout".equals(((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("action")))
		{
			Setup.logout();
			return true;
		}

		return false;
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

			// pick up minimal configuration
			Boolean minimal = Boolean.valueOf(ServerConfigurationService.getBoolean("top.minimal", false));
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
		List mySites = SiteService.getAllowedSites(true, false, null);

		if (mySites.size() > SITE_TABS_DISPLAYED)
		{
			int remove = mySites.size() - SITE_TABS_DISPLAYED;
			List moreSites = new Vector();
			for (int i = 0; i < remove; i++)
			{
				Site site = (Site) mySites.get(SITE_TABS_DISPLAYED);
				if (site.getId().equals(siteId))
				{
					setVmReference("extra", site, req);					
				}
				else
				{
					moreSites.add(site);
				}
				mySites.remove(SITE_TABS_DISPLAYED);
			}

			setVmReference("more", moreSites, req);
		}

		setVmReference("sites", mySites, req);

		setVmReference("portal", ServerConfigurationService.getPortalUrl(), req);
		
		// pick up minimal configuration
		Boolean minimal = Boolean.valueOf(ServerConfigurationService.getBoolean("top.minimal", false));
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
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/sitenav/SiteNavServlet.java,v 1.19 2004/10/11 00:33:02 ggolden.umich.edu Exp $
*
**********************************************************************************/
