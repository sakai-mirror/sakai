/**********************************************************************************
*
* $Header: /cvs/sakai/tunnel/src/java/org/sakaiproject/tunnel/TunnelServlet.java,v 1.10 2004/08/20 20:50:30 ggolden.umich.edu Exp $
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

package org.sakaiproject.tunnel;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.vm.ComponentServlet;

/**
 * <p>Tunneling / Dispatching servlet.</p>
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.10 $
 */
public class TunnelServlet extends HttpServlet
{
	/** redirect context (optional). */
	protected String m_redirectContext = null;

	/** redirect path (optional). */
	protected String m_redirectPath = null;

	/** log? */
	protected boolean m_logEnabled = false;

	/**
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Tunnel Servlet";
	}

	/**
	 * Initialize the servlet.
	 * @param config The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		m_redirectContext = config.getInitParameter("redirect-context");
		m_redirectPath = config.getInitParameter("redirect-path");
		m_logEnabled = "true".equals(config.getInitParameter("log"));

		// we can be configured to send to a context whatever the path is with no extra path prefix
		if ((m_redirectContext != null) && (m_redirectPath == null))
		{
			m_redirectPath = "";
		}
	}

	/**
	 * Forward the request to the context and path specified in the request,
	 * Send the current session in a special request attribute.
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException
	{
		try
		{
			// Note: the query parameters are alreay in the request, and need not be added here.
			// 		In fact, don't mess with the parameters at all!
			//		That's why this is not a velocity:o.s.vm.ComponentServlet with it's ParameterParser
			// get the path and query string:
			String path = req.getPathInfo();
			if (path == null)
			{
				path = "";
			}
			String context = null;

			// if we are configured with a redirect context and path, use that
			if (m_redirectContext != null)
			{
				context = m_redirectContext;
				path = m_redirectPath + path;
			}

			// else - else figure it from the path of the request
			else
			{
				// there may not be a trailing "/"
				int pos = path.indexOf('/',1);
				if (pos == -1)
				{
					context = path;
					path = "/";
				}

				// if there's a slash and a path following
				else
				{
					context = path.substring(0, pos);
					path = path.substring(pos);
				}
			}

			// get the session of this context into a special Sakai request attribute
			HttpSession session = req.getSession(true);
			req.setAttribute(ComponentServlet.GLOBAL_SESSION_ATTR, session);

			if (m_logEnabled)
			{
				log("Tunnel to: " + context + " " + path + " " + session.getId());
			}

			// get a dispatcher to the services context
			ServletContext servicesContext = getServletContext().getContext(context);
			if (servicesContext != null)
			{
				RequestDispatcher dispatcher = servicesContext.getRequestDispatcher(path);
				if (dispatcher != null)
				{
					dispatcher.forward(req, res);
				}
			}
		}
		
		catch (Throwable t)
		{
			log("service exception: ", t);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/tunnel/src/java/org/sakaiproject/tunnel/TunnelServlet.java,v 1.10 2004/08/20 20:50:30 ggolden.umich.edu Exp $
*
**********************************************************************************/
