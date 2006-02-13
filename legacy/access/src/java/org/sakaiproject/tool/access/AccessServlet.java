/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.access;

// imports
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.ToolException;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.ResourceLoader;
import org.sakaiproject.util.web.Web;

/**
 * <p>
 * Access is a servlet that provides a portal to entity access by URL for Sakai.<br />
 * The servlet takes the requests and dispatches to the appropriate EntityProducer for the response.<br />
 * Any error handling is done here.<br />
 * If the user has not yet logged in and need to for permission, the login process is handled here, too.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class AccessServlet extends VmServlet
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(AccessServlet.class);

	/** Resource bundle using current language locale */
	protected static ResourceLoader rb = new ResourceLoader("access");

	/** stream content requests if true, read all into memory and send if false. */
	protected static final boolean STREAM_CONTENT = true;

	/** The chunk size used when streaming (100k). */
	protected static final int STREAM_BUFFER_SIZE = 102400;

	/** delimiter for form multiple values */
	protected static final String FORM_VALUE_DELIMETER = "^";

	/** set to true when init'ed. */
	protected boolean m_ready = false;

	/** copyright path -- MUST have same value as ResourcesAction.COPYRIGHT_PATH */
	protected static final String COPYRIGHT_PATH = Entity.SEPARATOR + "copyright";

	/** Path used when forcing the user to accept the copyright agreement .*/
	protected static final String COPYRIGHT_REQUIRE = Entity.SEPARATOR + "require";

	/** Path used when the user has accepted the copyright agreement .*/
	protected static final String COPYRIGHT_ACCEPT = Entity.SEPARATOR + "accept";

	/** Ref accepted, request parameter for COPYRIGHT_ACCEPT request. */
	protected static final String COPYRIGHT_ACCEPT_REF = "ref";

	/** Return URL, request parameter for COPYRIGHT_ACCEPT request. */
	protected static final String COPYRIGHT_ACCEPT_URL = "url";

	/** Session attribute holding copyright-accepted references (a collection of Strings). */
	protected static final String COPYRIGHT_ACCEPTED_REFS_ATTR = "Access.Copyright.Accepted";

	/** init thread - so we don't wait in the actual init() call */
	public class AccessServletInit extends Thread
	{
		/**
		 * construct and start the init activity
		 */
		public AccessServletInit()
		{
			m_ready = false;
			start();
		}

		/**
		 * run the init
		 */
		public void run()
		{
			m_ready = true;
		}
	}

	/**
	 * initialize the AccessServlet servlet
	 * 
	 * @param config
	 *        the servlet config parameter
	 * @exception ServletException
	 *            in case of difficulties
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		startInit();
	}

	/**
	 * Start the initialization process
	 */
	public void startInit()
	{
		new AccessServletInit();
	}

	/**
	 * respond to an HTTP GET request
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request
	 * @param res
	 *        HttpServletResponse object back to the client
	 * @exception ServletException
	 *            in case of difficulties
	 * @exception IOException
	 *            in case of difficulties
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// catch the login helper requests
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 2) && ((parts[1].equals("login"))))
		{
			doLogin(req, res, null);
		}

		else
		{
			dispatch(req, res);
		}
	}

	/**
	 * respond to an HTTP POST request; only to handle the login process
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request
	 * @param res
	 *        HttpServletResponse object back to the client
	 * @exception ServletException
	 *            in case of difficulties
	 * @exception IOException
	 *            in case of difficulties
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// catch the login helper posts
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 2) && ((parts[1].equals("login"))))
		{
			doLogin(req, res, null);
		}

		else
		{
			sendError(res, HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * handle get and post communication from the user
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request
	 * @param res
	 *        HttpServletResponse object back to the client
	 */
	public void dispatch(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);

		// get the path info
		String path = params.getPath();
		if (path == null) path = "";

		if (!m_ready)
		{
			sendError(res, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return;
		}

		// send the sample copyright screen
		if (COPYRIGHT_PATH.equals(path))
		{
			respondCopyrightAlertDemo(req, res);
			return;
		}

		// send the real copyright screen for some entity (encoded in the request parameter)
		if (COPYRIGHT_REQUIRE.equals(path))
		{
			String acceptedRef = req.getParameter(COPYRIGHT_ACCEPT_REF);
			String returnPath = req.getParameter(COPYRIGHT_ACCEPT_URL);

			Reference aRef = EntityManager.newReference(acceptedRef);

			// send the copyright agreement interface
			Entity entity = aRef.getEntity();
			if (entity == null)
			{
				sendError(res, HttpServletResponse.SC_NOT_FOUND);
			}

			setVmReference("validator", new Validator(), req);
			setVmReference("resource", entity, req);
			setVmReference("tlang",rb,req);

			String acceptPath = Web.returnUrl(req, COPYRIGHT_ACCEPT + "?" + COPYRIGHT_ACCEPT_REF + "=" + aRef.getReference()
							+ "&" + COPYRIGHT_ACCEPT_URL + "=" + returnPath);

			setVmReference("accept", acceptPath, req);
			includeVm("/vm/access/copyrightAlert.vm", req, res);
			return;		
		}

		// make sure we have a collection for accepted copyright agreements
		Collection accepted = (Collection) SessionManager.getCurrentSession().getAttribute(COPYRIGHT_ACCEPTED_REFS_ATTR);
		if (accepted == null)
		{
			accepted = new Vector();
			SessionManager.getCurrentSession().setAttribute(COPYRIGHT_ACCEPTED_REFS_ATTR, accepted);
		}

		// for accepted copyright, mark it and redirect to the entity's access URL
		if (COPYRIGHT_ACCEPT.equals(path))
		{
			String acceptedRef = req.getParameter(COPYRIGHT_ACCEPT_REF);
			Reference aRef = EntityManager.newReference(acceptedRef);

			// save this with the session's other accepted refs
			accepted.add(aRef.getReference());
			
			// redirect to the original URL
			String returnPath = req.getParameter(COPYRIGHT_ACCEPT_URL);

			try
			{
				res.sendRedirect(Web.returnUrl(req, returnPath));
			}
			catch (IOException e){}
			return;
		}
		
		// pre-process the path
		String origPath = path;
		path = preProcessPath(path, req);

		// what is being requested?
		Reference ref = EntityManager.newReference(path);

		// get the incoming information
		AccessServletInfo info = newInfo(req);

		// let the entity producer handle it
		try
		{
			// make sure we have a valid reference with an entity producer we can talk to
			EntityProducer service = ref.getEntityProducer();
			if (service == null) throw new IdUnusedException(ref.getReference());

			// get the producer's HttpAccess helper, it might not support one
			HttpAccess access = service.getHttpAccess();
			if (access == null) throw new IdUnusedException(ref.getReference());

			// let the helper do the work
			access.handleAccess(req, res, ref, accepted);
		}
		catch (IdUnusedException e)
		{
			// the request was not valid in some way
			sendError(res, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		catch (PermissionException e)
		{
			// the end user does not have permission - offer a login if there is no user id yet established
			// if not permitted, and the user is the anon user, let them login
			if (SessionManager.getCurrentSessionUserId() == null)
			{
				doLogin(req, res, origPath);
				return;
			}

			// otherwise reject the request
			sendError(res, HttpServletResponse.SC_FORBIDDEN);
		}

		catch (ServerOverloadException e)
		{
			M_log.info("dispatch(): ref: " + ref.getReference() + e);
			sendError(res, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		
		catch (CopyrightException e)
		{
			// redirect to the copyright agreement interface for this entity
			try
			{
				// TODO: send back using a form of the request URL, encoding the real reference, and the requested reference
				// Note: refs / requests with servlet parameters (?x=y...) are NOT supported -ggolden
				String redirPath = COPYRIGHT_REQUIRE + "?" + COPYRIGHT_ACCEPT_REF + "=" + ref.getEntity().getReference()
								+ "&" + COPYRIGHT_ACCEPT_URL + "=" + req.getPathInfo();
				res.sendRedirect(Web.returnUrl(req, redirPath));
			}
			catch (IOException ee){}
			return;
		}

		catch (Throwable e)
		{
			M_log.warn("dispatch(): exception: ", e);
			sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		finally
		{
			// log
			if (M_log.isDebugEnabled())
				M_log.debug("from:" + req.getRemoteAddr() + " path:" + params.getPath() + " options: " + info.optionsString()
						+ " time: " + info.getElapsedTime());
		}
	}

	/**
	 * Make any changes needed to the path before final "ref" processing.
	 * 
	 * @param path
	 *        The path from the request.
	 * @req The request object.
	 * @return The path to use to make the Reference for further processing.
	 */
	protected String preProcessPath(String path, HttpServletRequest req)
	{
		return path;
	}

	/**
	 * Make the Sample Copyright Alert response.
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request.
	 * @param res
	 *        HttpServletResponse object back to the client.
	 */
	protected void respondCopyrightAlertDemo(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		// the context wraps our real vm attribute set
		setVmReference("validator", new Validator(), req);
		setVmReference("sample", Boolean.TRUE.toString(), req);
		setVmReference("tlang", rb, req);
		includeVm("/vm/access/copyrightAlert.vm", req, res);
	}

	/**
	 * Make a redirect to the login url.
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request.
	 * @param res
	 *        HttpServletResponse object back to the client.
	 * @param path
	 *        The current request path, set ONLY if we want this to be where to redirect the user after successfull login
	 */
	protected void doLogin(HttpServletRequest req, HttpServletResponse res, String path) throws ToolException
	{
		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// set the return path for after login if needed (Note: in session, not tool session, special for Login helper)
		if (path != null)
		{
			// where to go after
			session.setAttribute(Tool.HELPER_DONE_URL, Web.returnUrl(req, path));
		}

		// check that we have a return path set; might have been done earlier
		if (session.getAttribute(Tool.HELPER_DONE_URL) == null)
		{
			M_log.warn("doLogin - proceeding with null HELPER_DONE_URL");
		}

		// map the request to the helper, leaving the path after ".../options" for the helper
		ActiveTool tool = ActiveToolManager.getActiveTool("sakai.login");
		String context = req.getContextPath() + req.getServletPath() + "/login";
		tool.help(req, res, context, "/login");
	}

	/** create the info */
	protected AccessServletInfo newInfo(HttpServletRequest req)
	{
		return new AccessServletInfo(req);
	}

	protected void sendError(HttpServletResponse res, int code)
	{
		try
		{
			res.sendError(code);
		}
		catch (Throwable t)
		{
			M_log.warn("sendError: " + t);
		}
	}

	public class AccessServletInfo
	{
		// elapsed time start
		protected long m_startTime = System.currentTimeMillis();

		public long getStartTime()
		{
			return m_startTime;
		}

		public long getElapsedTime()
		{
			return System.currentTimeMillis() - m_startTime;
		}

		// all properties from the request
		protected Properties m_options = null;

		/** construct from the req */
		public AccessServletInfo(HttpServletRequest req)
		{
			m_options = new Properties();
			String type = req.getContentType();

			Enumeration e = req.getParameterNames();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String[] values = req.getParameterValues(key);
				if (values.length == 1)
				{
					m_options.put(key, values[0]);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < values.length; i++)
					{
						buf.append(values[i] + FORM_VALUE_DELIMETER);
					}
					m_options.put(key, buf.toString());
				}
			}
		}

		/** return the m_options as a string - obscure any "password" fields */
		public String optionsString()
		{
			StringBuffer buf = new StringBuffer(1024);
			Enumeration e = m_options.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				Object o = m_options.getProperty(key);
				if (o instanceof String)
				{
					buf.append(key);
					buf.append("=");
					if (key.equals("password"))
					{
						buf.append("*****");
					}
					else
					{
						buf.append(o.toString());
					}
					buf.append("&");
				}
			}

			return buf.toString();
		}
	}
}
