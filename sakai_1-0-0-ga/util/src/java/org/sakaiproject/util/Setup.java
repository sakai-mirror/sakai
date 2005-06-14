/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Setup.java,v 1.3 2004/09/30 20:20:56 ggolden.umich.edu Exp $
*
***********************************************************************************
@license@
**********************************************************************************/

package org.sakaiproject.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>Setup is ...</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.3 $
 */
public class Setup
{
	/** Session attribute holding the cross-webapp session. */
	public static final String GLOBAL_SESSION_ATTR = "org.sakaiproject.global.session";

	/** This request's parsed parameters */
	public final static String ATTR_PARAMS = "sakai.wrapper.params";

	/** This request's return URL root */
	public final static String ATTR_RETURN_URL = "sakai.wrapper.return.url";

	/**
	 * Setup fully for a service request.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @return true if we have handled things, false if more processing is required.
	 */
	public static boolean setup(HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		return doSetup(req, resp, true);
	}

	/**
	 * Setup minimally for a service request.
	 * Skip creating parameter parse, usage session and it's related issues.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @return true if we have handled things, false if more processing is required.
	 */
	public static boolean setupMinimally(HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		return doSetup(req, resp, false);
	}

	/**
	 * Setup for a service request.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @param full If true, do it all, else skip parameter parsing and usage session related tasks.
	 * @return true if we have handled things, false if more processing is required.
	 */
	protected static boolean doSetup(HttpServletRequest req, HttpServletResponse resp, boolean full) throws Exception
	{
		// set the session up for current thread access
		// Note: component:org.sakaiproject.component.framework.componentsServlet has matching code.
		HttpSession hsession = (HttpSession) req.getAttribute(GLOBAL_SESSION_ATTR);
		if (hsession == null)
		{
			hsession = req.getSession(true);
		}

		CurrentService.setInThread(HttpSession.class.getName(), hsession);
		CurrentService.setInThread(HttpServletRequest.class.getName(), req);
		CurrentService.setInThread(HttpServletResponse.class.getName(), resp);

		// form a return url root with transport, server dns and port, to allow multiple server ids
		// and the response to be to the request's naming convention
		// put in the request and the current service
		String rootReturnUrl = computeReturnUrl(req);
		req.setAttribute(ATTR_RETURN_URL, rootReturnUrl);
		CurrentService.setInThread(ATTR_RETURN_URL, rootReturnUrl);

		// debug(req, hsession);

		if (full)
		{
			// parse the parameters of the request, considering Unicode issues, into a ParameterParser
			ParameterParser parser = new ParameterParser(req);

			// make this available from the req as an attribute, as well as directly in via the CurrentService
			req.setAttribute(ATTR_PARAMS, parser);
			CurrentService.setInThread(ParameterParser.class.getName(), parser);

			// determine if this is automatic or user instigated
			boolean auto = "courier".equals(parser.getString("auto"));

			// grab the session so we can check for a closed one
			UsageSession session = UsageSessionService.getSession();

			// was this open?
			boolean wasOpen = ((session != null) && (!session.isClosed()));

			// inform of activity in the session
			UsageSessionService.setSessionActive(auto);

			// if we have just closed the session, complete the logout
			if ((session != null) && session.isClosed() && wasOpen)
			{
				logout();
			}

			// set the HTTP Session timeout
			hsession.setMaxInactiveInterval(UsageSessionService.getSessionLostTimeout());

			// if the session was just closed, redirect
			if ((session != null) && session.isClosed() && wasOpen)
			{
				sendTopRedirect(resp, ServerConfigurationService.getLoggedOutUrl());
				return true;
			}
		}

		return false;
	}

	// form a return url root with transport, server dns and port, to allow multiple server ids
	// and the response to be to the request's naming convention
	// put in the request and the current service
	public static String computeReturnUrl(HttpServletRequest req)
	{
		String transport = req.getScheme();
		int port = req.getServerPort();
		String dns = req.getServerName();

		String rv = transport + "://" + dns;

		// add port only if non-standard
		if (!(	
				("http".equalsIgnoreCase(transport) && port == 80)
			||	("https".equalsIgnoreCase(transport) && port == 443)))
		{
			rv += ":" + port;
		}
		
		return rv;
	}

	/**
	 * Send a redirect so our "top" ends up at the url, via javascript.
	 * @param url The redirect url
	 */
	public static void sendTopRedirect(HttpServletResponse resp, String url)
	{
		try
		{
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			out.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			out.println("<html><head></head><body>");
			out.println("<script type=\"text/javascript\" language=\"JavaScript\">");
			out.println("if (parent)\n\tif (parent.courier)\n" + "\t{\n\t\tparent.location.replace('" + url + "');\n\t}\n");
			out.println(
				"if (parent)\n\tif(parent.parent)\n\t\tif (parent.parent.courier)\n"
					+ "\t\t{\n\t\t\tparent.parent.location.replace('"
					+ url
					+ "');\n\t\t}\n");
			out.println("</script>");
			out.println("</body></html>");
		}
		catch (IOException e)
		{
		}
		//		resp.sendRedirect(ServerConfigurationService.getLoggedOutUrl());
	}

	/**
	 * Do the book-keeping associated with a user login that has already been authenticated.
	 * @param userId The user id.
	 * @return true if all went well, false if not (may fail if the userId is not a valid User)
	 */
	public static boolean login(String userId, HttpServletRequest req)
	{
		// get the User
		try
		{
			User user = UserDirectoryService.getUser(userId);

			// establish the user's session
			UsageSession session = UsageSessionService.startSession(user.getId(), req.getRemoteAddr(), req.getHeader("user-agent"));

			// post the login event
			EventTrackingService.post(
					EventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));

			return true;
		}
		catch (IdUnusedException e)
		{
			Logger.warn("Setup.login: no user: " + userId);
			return false;
		}
	}
	
	/**
	 * Do the book-keeping associated with logging out the current user.
	 */
	public static void logout()
	{
		logoutEvent(null);

		// close the session
		UsageSessionService.closeSession();

		UserDirectoryService.destroyAuthentication();

		// kill the container's HTTP session
		HttpSession hsession = (HttpSession) CurrentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			hsession.invalidate();
		}
	}
	
	/**
	 * Generate the logout event.
	 */
	public static void logoutEvent(UsageSession session)
	{
		if (session == null)
		{
			// generate a logout event (current session)
			EventTrackingService.post(
					EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
		}
		else
		{
			// generate a logout event (this session)
			EventTrackingService.post(
					EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true), session);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Setup.java,v 1.3 2004/09/30 20:20:56 ggolden.umich.edu Exp $
*
**********************************************************************************/