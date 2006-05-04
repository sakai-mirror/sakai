/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.util.web.Web;

/**
 * <p>
 * This is a sample helper tool; asks a question and confirms the answer.
 * </p>
 * <p>
 * Initial setup: *.question- the question to show <br>
 * *.doneUrl - the URL to redirect to when completed. <br>
 * *.canceleUrl - the URL to redirect to if canceled. <br>
 * </p>
 * <p>
 * Result: *.answer- the answer
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class QaTool extends HttpServlet
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(QaTool.class);

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai QA Helper";
	}

	/**
	 * Initialize the servlet.
	 * 
	 * @param config
	 *        The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		M_log.info("init()");
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		M_log.info("destroy()");

		super.destroy();
	}

	/**
	 * Respond to requests.
	 * 
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		final String headHtml = "<html><head><title>Sakai Login</title></head><body>";
		final String tailHtml = "</body></html>";

		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// get the Tool session
		ToolSession toolSession = SessionManager.getCurrentToolSession();

		// get my tool registration
		Tool tool = (Tool) req.getAttribute(Tool.TOOL);

		// fragment or not?
		boolean fragment = Boolean.TRUE.toString().equals(req.getAttribute(Tool.FRAGMENT));

		if (!fragment)
		{
			// set our response type
			res.setContentType("text/html; charset=UTF-8");
		}

		// get our response writer
		PrintWriter out = res.getWriter();

		if (!fragment)
		{
			// start our complete document
			out.println(headHtml);
		}

		// get the path
		String path = req.getPathInfo();
		if (path == null) path = "/";
		String[] parts = path.split("/");

		// for path = / show the question
		if (parts.length == 0)
		{
			sendQuestion(req, res, tool, toolSession, out);
		}

		// for path = /confirm show confirm
		else if ((parts.length == 2) && (parts[1].equals("confirm")))
		{
			sendConfirm(req, res, tool, toolSession, out);
		}

		else
		{
			out.println("<p>Invalid Path: " + req.getPathInfo() + "</p>");
		}

		if (!fragment)
		{
			// close the complete document
			out.println(tailHtml);
		}
	}

	protected void sendQuestion(HttpServletRequest req, HttpServletResponse res, Tool tool, ToolSession toolSession, PrintWriter out)
	{
		final String questionHtml = "<form name=\"form1\" method=\"post\" action=\"ACTION\">"
				+ "QUESTION<input name=\"a\" id =\"a\" type=\"text\"><br />"
				+ "<input name=\"submit\" type=\"submit\" id=\"submit\" value=\"Ok\">"
				+ "<input name=\"cancel\" type=\"submit\" id=\"cancel\" value=\"Cancel\"></form>";

		// put in the question
		String question = (String) toolSession.getAttribute(tool.getId() + ".question");
		if (question == null) question = "?";

		String html = questionHtml.replaceAll("QUESTION", question);

		// put in the URL
		String returnUrl = res.encodeURL(Web.returnUrl(req, req.getPathInfo()));
		html = html.replaceAll("ACTION", res.encodeURL(returnUrl));

		out.println(html);
	}

	protected void sendConfirm(HttpServletRequest req, HttpServletResponse res, Tool tool, ToolSession toolSession, PrintWriter out)
	{
		final String confirmHtml = "<form name=\"form1\" method=\"post\" action=\"ACTION\">" + "Are you sure?"
				+ "<input name=\"submit\" type=\"submit\" id=\"submit\" value=\"Ok\">"
				+ "<input name=\"cancel\" type=\"submit\" id=\"cancel\" value=\"Cancel\"></form>";

		// put in the URL
		String returnUrl = res.encodeURL(Web.returnUrl(req, req.getPathInfo()));
		String html = confirmHtml.replaceAll("ACTION", res.encodeURL(returnUrl));

		out.println(html);
	}

	/**
	 * Respond to data posting requests.
	 * 
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// get the Tool session
		ToolSession toolSession = SessionManager.getCurrentToolSession();

		// get my tool registration
		Tool tool = (Tool) req.getAttribute(Tool.TOOL);

		String redirect = null;

		// get the path
		String path = req.getPathInfo();
		if (path == null) path = "/";
		String[] parts = path.split("/");

		// for path = / show the question
		if (parts.length == 0)
		{
			redirect = postQuestion(req, res, tool, toolSession);
		}

		// for path = /confirm show confirm
		else if ((parts.length == 2) && (parts[1].equals("confirm")))
		{
			redirect = postConfirm(req, res, tool, toolSession);
		}

		// invalid path
		else
		{
			redirect = Web.returnUrl(req, null);
			M_log.warn("doPost: invalid path: " + req.getPathInfo());
		}

		// respond with a redirect
		res.sendRedirect(res.encodeRedirectURL(redirect));
	}

	protected String postQuestion(HttpServletRequest req, HttpServletResponse res, Tool tool, ToolSession toolSession)
	{
		String rv = null;

		// here comes the data back from the form... these fields will be present, blank if not filled in
		String a = req.getParameter("a").trim();

		// one of these will be there, one null, depending on how the submit was done
		String submit = req.getParameter("submit");
		String cancel = req.getParameter("cancel");

		// submit
		if (submit != null)
		{
			// store the answer
			toolSession.setAttribute(tool.getId() + ".answer", a);

			// move to confirm mode
			rv = Web.returnUrl(req, "/confirm");
		}

		// cancel
		else
		{
			// done - redirect to the cancel URL
			rv = (String) toolSession.getAttribute(tool.getId() + ".cancelUrl");

			// cleanup
			toolSession.removeAttribute(tool.getId() + ".answer");
		}

		return rv;
	}

	protected String postConfirm(HttpServletRequest req, HttpServletResponse res, Tool tool, ToolSession toolSession)
	{
		String rv = null;

		// one of these will be there, one null, depending on how the submit was done
		String submit = req.getParameter("submit");
		String cancel = req.getParameter("cancel");

		// if canceled, remove the answer
		if (cancel != null)
		{
			toolSession.removeAttribute(tool.getId() + ".answer");

			// return to the question
			rv = Web.returnUrl(req, null);
		}

		// submit
		else
		{
			// done - redirect to the done URL
			rv = (String) toolSession.getAttribute(tool.getId() + ".doneUrl");
		}

		return rv;
	}
}



