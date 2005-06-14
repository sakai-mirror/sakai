/**********************************************************************************
*
* $Header: /cvs/sakai/embedded/src/java/org/sakaiproject/portal/sedna/SednaServlet.java,v 1.18 2004/10/11 00:33:01 ggolden.umich.edu Exp $
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

package org.sakaiproject.portal.sedna;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.vm.VmServlet;

/**
 * <p>SednaServlet is ...</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.18 $
 */
public class SednaServlet extends VmServlet
{
	/** The "face" (vm file) to use. */
	protected String m_face = "portal3";

	/**
	 * Initialize the servlet.
	 * @param config The servlet config.
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
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		doGet(req, res);
	}

	/**
	 * Handle a get - respond with the requested portal display
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		// parse the request
		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);

		// look for /group/<site id>/js_pane/<page id>, or
		//          /user/<user id>/page/<page id>
		//          /site/<site id>/page/<page id>
		// to use if ?site= and ?page= are missing
		String path = params.getPath();
		String pathSite = null;
		String pathPage = null;
		if ((path != null) && (path.length() > 0))
		{
			try
			{
				String[] parts = StringUtil.split(path, "/");
				if (("group".equalsIgnoreCase(parts[1])) || ("site".equalsIgnoreCase(parts[1])))
				{
					pathSite = parts[2];
				}
				if ("user".equalsIgnoreCase(parts[1]))
				{
					// Note: assumes the "~" that the site service is responsible for -ggolden
					pathSite = "~" + parts[2];
				}
				if (("js_pane".equalsIgnoreCase(parts[3])) || ("page".equalsIgnoreCase(parts[3])))
				{
					pathPage = parts[4];
				}
			}
			catch (Throwable t)
			{
			}
		}

		try
		{
			// who's the user?
			String userId = UsageSessionService.getSessionUserId();
			boolean loggedIn = (userId != null);
			setVmReference("loggedIn", Boolean.valueOf(loggedIn), req);

			// which site - use the myWorkspace or gateway if missing
			String siteId = params.getString("site");

			// if no site param, but it was recognized in the path, use it
			if (siteId == null)
			{
				siteId = pathSite;
			}

			// if no site specified, use a default
			if (siteId == null)
			{
				// if logged in, send them to their worksite
				if (loggedIn)
				{
					siteId = SiteService.getUserSiteId(userId);
				}
				// otherwise send them to the gateway
				else
				{
					siteId = ServerConfigurationService.getGatewaySiteId();
				}
			}

			// get the page - optional
			String pageId = params.getString("page");

			// if not specified in the param, try the path
			if (pageId == null)
			{
				pageId = pathPage;
			}

			// get the site - if missing or not accessable, use the error site
			Site site = null;
			try
			{
				site = SiteService.getSiteVisit(siteId);
			}
			catch (IdUnusedException e)
			{
				site = SiteService.getSiteVisit(SiteService.SITE_ERROR);
			}
			catch (PermissionException e)
			{
				// if not permitted, and the user is not logged in, go to login
				if (!loggedIn)
				{
					respondRedirectToLogin(req, res, siteId, pageId);
					return;
				}

				// otherwise just error
				site = SiteService.getSiteVisit(SiteService.SITE_ERROR);
			}

			setVmReference("site", site, req);

			// access the session state for this site
			SessionState state = UsageSessionService.getSessionState("portal." + site.getId());

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

			// if no page in request, or an invalid one, use the first page of the site
			if (page == null)
			{
				page = (SitePage) site.getPages().get(0);
			}

			// store the page last visited for this site - use state keyed by site
			state.setAttribute("page", page.getId());

			setVmReference("page", page, req);

			setVmReference("config", ServerConfigurationService.getInstance(), req);
			setVmReference("validator", new Validator(), req);

			// get the bottomnav
			String[] bottomnav = ServerConfigurationService.getStrings("bottomnav");
			if (bottomnav != null)
			{
				setVmReference("bottomnav", bottomnav, req);
			}

			// pick up minimal configuration (changes the top site nav height)
			Boolean minimal = Boolean.valueOf(ServerConfigurationService.getBoolean("top.minimal", false));
			setVmReference("minimal", minimal, req);
		}
		catch (Exception e)
		{
		}

		// get the "face" - vm file to use
		String face = params.getString("face");
		if (face == null)
		{
			face = m_face;
		}

		// set standard no-cache headers
		setNoCacheHeaders(res);

		includeVm("/vm/" + face + ".vm", req, res);
	}

	/**
	 * Make a redirect to the login url.
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 * @param siteId The site id from the request.
	 * @param pageId The page id from the request.
	 */
	protected void respondRedirectToLogin(HttpServletRequest req, HttpServletResponse res, String siteId, String pageId)
	{
		// Note: the dependency on the class name and REDIRECT string from chef-tool's LoginServlet

		// some state for login
		SessionState state = UsageSessionService.getSessionState("org.sakaiproject.tool.authn.LoginServlet");

		// form the redirect after auth
		String url = ServerConfigurationService.getPortalUrl();
		if (siteId != null)
		{
			url += "/site/" + siteId;
		}

		if (pageId != null)
		{
			url += "/page/" + pageId;
		}

		// store the url to return to after login in the session for redirect
		state.setAttribute("AuthnRedirect", url);

		// redirect to our login place
		try
		{
			res.sendRedirect(ServerConfigurationService.getLoginUrl());
		}
		catch (IOException ignore)
		{
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/embedded/src/java/org/sakaiproject/portal/sedna/SednaServlet.java,v 1.18 2004/10/11 00:33:01 ggolden.umich.edu Exp $
*
**********************************************************************************/
