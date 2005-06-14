/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/authn/LoginServlet.java,v 1.14 2004/09/30 20:20:29 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.authn;

// imports
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Setup;
import org.sakaiproject.util.StringUtil;

/**
* <p>LoginServlet handles user login.</p>
* <p>When a request comes in, if the user has container auth, or is already logged in, process the redirect
* as setup in the session.</p>
* <p>If the user is not already logged in, and not container authenticated, then check for incoming parameters
* that indicate a login screen response - login and redirect if valid.</p>
* <p>Otherwise, presend the login interface.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.14 $
*/
public class LoginServlet extends VmServlet
{
	/** HTTP State attribute for the redirect. */
	public final static String REDIRECT = "AuthnRedirect";

	/** error template. */
	protected final static String ERROR_VM = "/vm/authn/error.vm";

	/** login template. */
	protected final static String LOGIN_VM = "login";

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
		// some state for login
		SessionState state = UsageSessionService.getSessionState(LoginServlet.class.getName());

		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);

		try
		{
			// if not yet logged in, and we are taking container authentication, try to login
			User user = UserDirectoryService.getCurrentUser();
			boolean loggedIn = !(user.equals(UserDirectoryService.getAnonymousUser()));

			// try container authentication
			boolean containerAuthentication = ServerConfigurationService.getBoolean("container.auth", false);
			if ((!loggedIn) && (containerAuthentication))
			{
				String userId = req.getRemoteUser();
				if (userId != null)
				{
					if (Logger.isDebugEnabled())
						Logger.debug(this +": Logging in from remoteUser: " + userId);

					loggedIn = Setup.login(userId, req);
				}
			}

			// if (still) not logged in yet, check for login response return
			if (!loggedIn)
			{
				processLogin(req, res);
			}

			// update
			user = UserDirectoryService.getCurrentUser();
			loggedIn = !(user.equals(UserDirectoryService.getAnonymousUser()));

			// if still not logged in, send the login screen
			if (!loggedIn)
			{
				doLogin(req, res);
			}
			
			// otherwise send the redirect
			else
			{
				String redirect = null;
				
				if (state != null)
				{
					redirect = (String) state.getAttribute(REDIRECT);
					state.removeAttribute(REDIRECT);
				}

				// just in case we have no redirect, send them home
				if (redirect == null)
				{
					redirect = ServerConfigurationService.getUserHomeUrl();
				}

				res.sendRedirect(redirect);
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", this +".dispatch(): exception: ", e);
			respondError(req, res, e.toString());
		}
	}

	/**
	 * Make the "error" response for problems processing the request.
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 * @param ref The request reference string.
	 * @param msg The error message.
	 */
	protected void respondError(HttpServletRequest req, HttpServletResponse res, String msg)
	{
		try
		{
			setVmReference("msg", msg, req);
			includeVm(ERROR_VM, req, res);
		}
		catch (Throwable t)
		{
			Logger.warn(this +".respondError(): ", t);
		}

	}

	/**
	 * Process a login interface response, if present.
	 * Login the user if possible.
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 */
	protected void processLogin(HttpServletRequest req, HttpServletResponse res)
	{
		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);
		String name = StringUtil.trimToNull(params.getString("username"));
		String pw = StringUtil.trimToZero(params.getString("password"));

		// if from the login attempt
		if ((name != null) && (pw.length() > 0))
		{
			// authenticate
			User user = UserDirectoryService.authenticate(name, pw);
			if (user == null)
			{
				// in error
				String userIdText = ServerConfigurationService.getBoolean("email-for-user-id", true) ? "email" : "user id";
				setVmReference("alertMessage", "Invalid " + userIdText + " or password", req);

				return;
			}

			// login the user
			Setup.login(user.getId(), req);
		}
	}

	/**
	 * Setup a login UI output
	 * @param req HttpServletRequest object with the client request.
	 * @param res HttpServletResponse object back to the client.
	 */
	protected void doLogin(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userIdText = ServerConfigurationService.getBoolean("email-for-user-id", true) ? "email" : "user id";
			setVmReference("userid_text", userIdText, req);

			// pick our interFACE
			String face = ((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString("face");
			if (face == null)
			{
				face = LOGIN_VM;
			}
			includeVm("/vm/authn/" + face + ".vm", req, res);
		}
		catch (Exception e)
		{
			Logger.warn(this +".respondLogin(): ", e);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/authn/LoginServlet.java,v 1.14 2004/09/30 20:20:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
