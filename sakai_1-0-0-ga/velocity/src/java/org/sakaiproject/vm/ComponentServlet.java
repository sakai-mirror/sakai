/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/ComponentServlet.java,v 1.30 2004/10/01 19:29:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.vm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.service.framework.component.ComponentManager;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.util.Setup;

/**
* <p>ComponentServlet is an HttpServlet that can use Sakai components (by covered locator).<p>
* <p>Extension classes can use the <code>getComponentManager</code> call to access the component manager.</p>
* <p>Registration for "current" (thread based) access to session (...) is handled here, too.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.30 $
*/
public abstract class ComponentServlet extends HttpServlet
{
	/** DEBUG requests taking more than this long (ms). */
	public static final long DEBUG_THRESHOLD = 0;

	/** Session attribute holding the cross-webapp session. */
	public static final String GLOBAL_SESSION_ATTR = Setup.GLOBAL_SESSION_ATTR;

	/** This request's parsed parameters */
	protected final static String ATTR_PARAMS = Setup.ATTR_PARAMS;

	/** This request's return URL root */
	protected final static String ATTR_RETURN_URL = Setup.ATTR_RETURN_URL;

	/**
	 * Access the CHEF Component Manager.
	 * @return the ComponentManager.
	 */
	public ComponentManager getComponentManager()
	{
		return org.sakaiproject.service.framework.component.cover.ComponentManager.getInstance();
	}

	/**
	 * Override service, adding CurrentService binding of the HttpSession into the current (request) thread.
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException
	{
		// for debug
		long startTime = 0;

		try
		{
			CurrentService.startThread("REQUEST");

			try
			{
				if (Setup.setup(req, resp))
					return;
			}
			catch (Throwable any)
			{
				// if there's any exception in setup, return an error
				resp.sendError(400);
				return;
			}

			super.service(req, resp);
		}
		catch (Throwable t)
		{
			log("service exception: ", t);
		}
		finally
		{
			// clear out any current access bindings
			CurrentService.clearInThread();
		}
	}

	/**
	 * Send a redirect so our parent ends up at the url, via javascript.
	 * @param url The redirect url
	 */
	protected void sendParentRedirect(HttpServletResponse resp, String url)
	{
		try
		{
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			out.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			out.println("<html><head></head><body>");
			out.println("<script type=\"text/javascript\" language=\"JavaScript\">");
			out.println("if (parent)\n" + "{\n\tparent.location.replace('" + url + "');\n}\n");
			out.println("</script>");
			out.println("</body></html>");
		}
		catch (IOException e)
		{
		}
		//		resp.sendRedirect(ServerConfigurationService.getLoggedOutUrl());
	}

	// set standard no-cache headers
	protected void setNoCacheHeaders(HttpServletResponse resp)
	{
		resp.setContentType("text/html; charset=UTF-8");
		// some old date
		resp.addHeader("Expires", "Mon, 01 Jan 2001 00:00:00 GMT");
		// TODO: do we need this?  adding a date header is expensive contention for the date formatter, ours or Tomcats.
		//resp.addDateHeader("Last-Modified", System.currentTimeMillis());
		resp.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		resp.addHeader("Pragma", "no-cache");
	}

	//	private void debug(HttpServletRequest req, HttpSession hsession)
	//	{
	//		log(
	//			":Request: "
	//				+ req.getContextPath()
	//				+ " "
	//				+ req.getPathInfo()
	//				+ " "
	//				+ req.getQueryString()
	//				+ " globalSession: "
	//				+ hsession.getId());
	//
	//		Cookie[] c = req.getCookies();
	//		if (c != null)
	//		{
	//			for (int i = 0; i < c.length; i++)
	//			{
	//				log(
	//					":Request: "
	//						+ req.getContextPath()
	//						+ " "
	//						+ req.getPathInfo()
	//						+ " cookie: "
	//						+ c[i].getName()
	//						+ " = "
	//						+ c[i].getValue());
	//			}
	//		}
	//	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/ComponentServlet.java,v 1.30 2004/10/01 19:29:26 ggolden.umich.edu Exp $
*
**********************************************************************************/
