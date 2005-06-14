/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Setup.java,v 1.9 2005/02/16 03:40:35 janderse.umich.edu Exp $
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
import javax.servlet.http.Cookie;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>Setup is ...</p>

 * Design Notes: Generally the method of authentication is to catch the user when we
 * encounter them, and either though container or form-based identity, sakai code calls
 * login.  This establishes a login identity and associates a Sakai Session with
 * shared HttpSession.  The Sakai Session ID is also placed as a root cookie when the 
 * Sakai Portal page is displayed.  Normally for each successing request, we condition the 
 * Session to make sure that it is OK.  If this webapp is sharing http session, 
 * then the Sakai Session is already there and the http session is all set.  If the webapp 
 * is not sharing the session, but the browser has cookies, then the http session for this web
 * app is conditioned by looking up and using the Sakai Session via the cookie.  If all of that 
 * fails, and the webapp has authenticated the user (typically via basic authentication), then
 * we simply start a lightweight session for them using their User ID.  We do not do the heavy 
 * lifting present in login because this may be done many times per minute for things like
 * web-services and webdav.  Ideally these will come up with a way (cookie or otherwise) to 
 * store httpsession or the sakai session Id.  But in a pinch, userId will have to do. 
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.9 $
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
	 * Get/Create the appropriate Http Session for this request. First cheeck for the
	 * single shared session - if this is not present, use the local session from 
	 * the current webapp.
	 * @param req The servlet request.
	 * @return  the "best" HttpSession for this webapp
	 */
	public static HttpSession getHttpSession(HttpServletRequest req)
	{
		HttpSession hsession = null;
		try {
			hsession = (HttpSession) req.getAttribute(GLOBAL_SESSION_ATTR);
		}
		catch (Throwable t) {  // In case of class case error
			hsession = null;
		}

		// Take/create the current WebApp's session
		if (hsession == null)
		{
			hsession = req.getSession(true);
		}
		return hsession;
	}

	/**
	 * Condition the session for the current request in this webapp as best 
	 * we can with whatever information we can find.  In priority order,
	 * 
	 * (1) Hope that this shares a session and the user is already 
	 *     logged in from before
	 * (2) Hope that we have a cookie which has the session ID.
	 * (3) Put the user's Id into the session, so at least basic getSessionUserId works
	 * (4) If the user's browser/client seems to handle persistent http session, 
	 *     we start a session and use http session to keep track of it.
	 * 
	 * This does no security, the servlet must insure security before calling
	 * this method.  This method assumes that userId is the proper User Id.
	 * 
	 * @param req The servlet request.
	 * @param userId
	 * @return true if there is a valid Sakai Session in the http Session
	 */
	private static boolean conditionSession(HttpServletRequest req, HttpSession hsession, String userId)
	{	
	    UsageSession usageSession = null;

	    // If the httpSession already has a Sakai Session within it, we are done    
	    try
	    {
	    		usageSession = (UsageSession) hsession.getAttribute(UsageSessionService.USAGE_SESSION_KEY);
	    		if ( usageSession != null ) return true;
	    	}
	    catch (IllegalStateException e)
	    {
			// Logger.warn("Setup.conditionSession: Error retrieving Sakai Session from http session");
	    		usageSession = null;
	    	}

	    // Since the HttpSession did not have a Sakai Session, check to see if there
	    // is a root cookie stored from the Portal Java code.  If this is found, we
	    // we retrieve the session from Session Storage and set it into HttpSession.
	    // This is the likely path for a servlet which does not share session with the
	    // rest of Sakai, or if for some reason, the Tomcat Session is lost between 
	    // requests.

	    Cookie sakaiSessionCookie = null;
	    Cookie[] cookies = req.getCookies();
	    if (cookies != null)
	    {
		      for (int i = 0; i < cookies.length; i++)
		      {
			        Cookie c = cookies[i];
			        String name = c.getName();
			
			        if (name.equals(UsageSessionService.SAKAI_SESSION_COOKIE) )
			        {
				          sakaiSessionCookie = c;
				      	  // System.out.println("sakaiSessionCookie:" + sakaiSessionCookie.getPath() + " "
				          //  + sakaiSessionCookie.getValue());
			        }
		      }
	    }
	    
	    // If we found a cookie, then use it to retrieve the existing session
	    // One scenario here is that we have logged out, our session is gone, but the
	    // cookie is still present - this is not a problem.  I will simply not find
	    // the session specified by the cookie or the session will be marked as closed.
	    // Logout should clear the cookie to be maximally clean.
	    
	    if ( sakaiSessionCookie != null )
	    {
	    		String fromCookie = (String) sakaiSessionCookie.getValue();

	    		usageSession = UsageSessionService.getSession(fromCookie);
	    		if ( usageSession != null && !usageSession.isClosed() ) {
				// System.out.println("Retrieved Sakai UsageSession from Storage  "+usageSession.getId());
				hsession.setAttribute(UsageSessionService.USAGE_SESSION_KEY, usageSession);
				return true;
	    		}
	    		else
	    		{
	    			// System.out.println("Unable to load SakaiSession from Cookie "+fromCookie);
	    		}
	    }
		
	    // If we do not have a userId, there is no further purpose in this effort
		if ( userId == null ) return false;
		
		/* Note: A http session is considered to be "new" if it has been created by the server, 
		 * but the client has not yet acknowledged joining the session. For example,  
		 * if the server supported only cookie-based sessions and the client had completely 
		 * disabled the use of cookies, then calls to HttpServletRequest.getSession() would 
		 * always return "new" sessions
		 */
		
		// If the session is new, we simply drop the userId into the thread so 
		// getSessionUserId works.  If the client can accept the Http Session ID,
		// then isNew() will be false next time and we sill start a real session.
		// If the client cannot accept the hsession, then isNew() will always be
		// true so we will never start a real session.
		//
		// If isNew() is false, and we start a session, storing it in Http
		// Session, then on the third and later requests, we will never get to
		// this code - because a session will be present up request three
		// and beyond.
		
		// Scenario without cookies: isNew() is always true, so we drop down to this code
		// and always do the "light" session.
	
		// Scenario with cookies: Request 1: isNew() is true, we set the userId in 
		// threadlocal (loght version) but httpsession now exists.  Request 2: http
		// session is Old, but has no Sakai session, so we still fall down to this
		// code, but with isNew false, we create a real Sakai Session and put it into 
		// http session.  Request 3: http session exists and there is a Sakai Session
		// in the http session so we never get back to this code.

		if ( hsession.isNew() )
		{
			// System.out.println("Placing user in Thread user="+userId);
			CurrentService.setInThread(UsageSessionService.USAGE_SESSION_KEY, userId);				
		}
		else
		{
			// System.out.println("conditionSession starting session user="+userId);
			UsageSessionService.startSession(userId, req.getRemoteAddr(), req.getHeader("user-agent"));
		}
		return true;
	}

	/**
	 * Setup fully for a service request.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @param parseMultipart Whether to parse mime multipart (file uploads) - set to false if a non-Sakai filter will be taking care of file uploads.
	 * @return true if this method has done something to the response such as a redirect
	 * the servlet should not continue further and false if processing can continue normally.
	 */
	public static boolean setup(HttpServletRequest req, HttpServletResponse resp, boolean parseMultipart) throws Exception
	{
		return doSetup(req, resp, null, true, parseMultipart);
	}

	/**
	 * Setup minimally for a service request.
	 * Skip creating parameter parse, usage session and it's related issues.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @return true if this method has done something to the response such as a redirect
	 * the servlet should not continue further and false if processing can continue normally.
	 */
	public static boolean setupMinimally(HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		return doSetup(req, resp, null, false, true);
	}

	/**
	 * Setup minimally for a service request when the userId is known to the web app.
	 * Skip creating parameter parse, usage session and it's related issues.  This should not
	 * be used except for servlets that protect themselves via something like Basic 
	 * Authentication such as web services or webdav.  This should not be used as a substitute
	 * for the standard Sakai login.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @param userId The known user Id.
	 * @return true if this method has done something to the response such as a redirect
	 * the servlet should not continue further and false if processing can continue normally.	 
	 */
	
	public static boolean setupMinimallyForUser(HttpServletRequest req, HttpServletResponse resp, String userId) throws Exception
	{
		return doSetup(req, resp, userId, false, true);
	}	
	
	/**
	 * Setup for a service request.
	 * @param req The servlet request.
	 * @param resp The servlet response.
	 * @param full If true, do it all, else skip parameter parsing and usage session related tasks.
	 * @param parseMultipart Whether to parse mime multipart (file uploads) - set to false if a non-Sakai filter will be taking care of file uploads.
	 * @return true if this method has done something to the response such as a redirect
	 * the servlet should not continue further and false if processing can continue normally.
	 */
	protected static boolean doSetup(HttpServletRequest req, HttpServletResponse resp, String userId, boolean full, boolean parseMultipart) throws Exception
	{
		// set the session up for current thread access
		// Note: component:org.sakaiproject.component.framework.componentsServlet has matching code.
		// Note: Make sure that the session is in the Thread before calling *any* UsageSesison
		//       methods.  UsageSession works with the session in the current Thread.
		
		HttpSession hsession = getHttpSession(req);
		CurrentService.setInThread(HttpSession.class.getName(), hsession);
		CurrentService.setInThread(HttpServletRequest.class.getName(), req);
		CurrentService.setInThread(HttpServletResponse.class.getName(), resp);

		// Set up the session as best we can - we ignore the return value
		// Callers can simply check if the Session is working to determine
		// success or failure in conditioning the Session.
		conditionSession(req, hsession,userId);

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
			ParameterParser parser = new ParameterParser(req, parseMultipart);

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

			// update the user's externally provided realm definitions
			RealmService.refreshUser(user.getId());

			// post the login event
			EventTrackingService.post(
					EventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));

			return true;
		}
		catch (IdUnusedException e)
		{
			// Logger.warn("Setup.login: no user: " + userId);
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
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Setup.java,v 1.9 2005/02/16 03:40:35 janderse.umich.edu Exp $
*
**********************************************************************************/