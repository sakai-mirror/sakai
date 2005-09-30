/**********************************************************************************
 *
 * $Header: /cvs/sakai2/sample-tools/browser/src/java/org/sakaiproject/tool/sample/BrowserTool.java,v 1.12 2005/05/12 01:38:53 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.util.web.Web;

/**
 * <p>
 * Sakai browser sample tool.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class BrowserTool extends HttpServlet
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BrowserTool.class);

	/** Helper tool for options. */
	protected static final String OPTIONS_HELPER = "sakai.tool_config.helper";

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Browser";
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

		m_items.put("001", new BrowserItem("001", "v1", "001-one", "001-two"));
		m_items.put("002", new BrowserItem("002", "v1", "002-one", "002-two"));
		m_items.put("003", new BrowserItem("003", "v1", "003-one", "003-two"));

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

		// read our local resource
		try
		{
			URL u = getServletContext().getResource("/browser/sample.txt");
			M_log.debug("url: " + u.toString());
		}
		catch (Throwable t)
		{
			M_log.debug("getting url throws: " + t);
		}

		try
		{
			InputStream in = getServletContext().getResourceAsStream("/browser/sample.txt");
			String x = new BufferedReader(new InputStreamReader(in)).readLine();
			M_log.debug("read from context file: " + x);
		}
		catch (Throwable t)
		{
			M_log.debug("getting resource stream throws: " + t);
		}

		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// get the Tool session
		ToolSession toolSession = SessionManager.getCurrentToolSession();

		// get the Tool
		Placement placement = ToolManager.getCurrentPlacement();

		// fragment or not?
		boolean fragment = Boolean.TRUE.toString().equals(req.getAttribute(Tool.FRAGMENT));

		// get our response writer
		PrintWriter out = res.getWriter();

		if (!fragment)
		{
			// start our complete document
			out.println(headHtml);
		}

		showSession(out, true, placement.getContext());

		// get the path
		String path = req.getPathInfo();
		if (path == null) path = "/";

		// 0 parts means the path was just "/", otherwise parts[0] = "", parts[1] = item id, parts[2] if present is "edit"...
		String[] parts = path.split("/");

		// for nothing on the path, give list
		if (parts.length == 0)
		{
			sendList(out, req, res);
		}

		// if in /options mode
		else if ((parts.length >= 2) && (parts[1].equals("options")))
		{
			// setup for the helper if needed
			if (toolSession.getAttribute(OPTIONS_HELPER + Tool.HELPER_DONE_URL) == null)
			{
				// my tool, to edit
				toolSession.setAttribute(OPTIONS_HELPER + ".placement", placement);

				// where to go after
				toolSession.setAttribute(OPTIONS_HELPER + Tool.HELPER_DONE_URL, Web.returnUrl(req, null));
			}

			// map the request to the helper, leaving the path after ".../options" for the helper
			ActiveTool tool = ActiveToolManager.getActiveTool(OPTIONS_HELPER);
			String context = req.getContextPath() + req.getServletPath() + Web.makePath(parts, 1, 2);
			String toolPath = Web.makePath(parts, 2, parts.length);
			tool.help(req, res, context, toolPath);
		}

		// for only an item id on the path, view that item
		else if (parts.length == 2)
		{
			sendView(out, req, res, parts[1]);
		}

		// for an item and /edit on the path, edit that item
		else if ((parts.length == 3) && ("edit".equals(parts[2])))
		{
			sendEdit(out, req, res, parts[1]);
		}

		else
		{
			M_log.warn("doGet: invalid path: " + path);
			out.println("<H1>Unknown request</H1>");
			out.println("<p>" + path + "</p>");
		}

		if (!fragment)
		{
			// close the complete document
			out.println(tailHtml);
		}
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

		String redirect = null;

		// get the path
		String path = req.getPathInfo();
		if (path == null) path = "/";

		// 0 parts means the path was just "/", otherwise parts[0] = "", parts[1] = item id, parts[2] if present is "edit"...
		String[] parts = path.split("/");

		// if in /options mode
		if ((parts.length >= 1) && (parts[1].equals("options")))
		{
			// go into helper, mapped mode
			ActiveTool tool = ActiveToolManager.getActiveTool(OPTIONS_HELPER);
			String context = req.getContextPath() + req.getServletPath() + Web.makePath(parts, 1, 2);
			String toolPath = Web.makePath(parts, 2, parts.length);
			tool.help(req, res, context, toolPath);
		}

		// for an item and /edit on the path, edit that item
		else if ((parts.length == 3) && ("edit".equals(parts[2])))
		{
			// here comes the data back from the form... these fields will be present, blank if not filled in
			String one = req.getParameter("one").trim();
			String two = req.getParameter("two").trim();

			// here's our context - the item and version
			String id = req.getParameter("item").trim();
			String version = req.getParameter("version").trim();

			// one of these will be there, one null, depending on how the submit was done
			String submit = req.getParameter("submit");
			String cancel = req.getParameter("cancel");

			if (submit != null)
			{
				BrowserItem item = getItem(id);
				item.one = one;
				item.two = two;
			}

			// respond with a redirect to view mode
			redirect = Web.returnUrl(req, "/" + id);
		}

		// invalid post path
		else
		{
			redirect = Web.returnUrl(req, req.getPathInfo());
			M_log.warn("doPost: invalid path: " + path);
		}

		if (redirect != null)
		{
			res.sendRedirect(res.encodeRedirectURL(redirect));
		}
	}

	protected void sendList(PrintWriter out, HttpServletRequest req, HttpServletResponse res)
	{
		out.println("<H1>Items</H1>");
		Collection items = getItems();
		for (Iterator i = items.iterator(); i.hasNext();)
		{
			BrowserItem item = (BrowserItem) i.next();
			out.println("<p><a href=\"" + res.encodeURL(Web.returnUrl(req, "/" + item.id)) + "\" title=\"View\">" + item.id + " "
					+ item.one + " " + item.two + "</a></p>");
		}

		// get the Tool
		Placement placement = ToolManager.getCurrentPlacement();

		out.println ("<p><table><tr><td>tool id</td><td>" + placement.getTool().getId() + "</td></tr>");
		out.println("<tr><td>title</td><td>" + placement.getTitle() + "</td></tr>");
		out.println("<tr><td>description</td><td>" + placement.getTool().getDescription() + "</td></tr>");
		Properties regConfig = placement.getTool().getRegisteredConfig();
		out.println("<tr><td>registered</td><td>");
		for (Enumeration e = regConfig.propertyNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			out.println(name + "=" + regConfig.getProperty(name) + " ");
		}
		out.println("</td></tr>");
		Properties placementConfig = placement.getPlacementConfig();
		out.println("<tr><td>placement</td><td>");
		for (Enumeration e = placementConfig.propertyNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			out.println(name + "=" + placementConfig.getProperty(name) + " ");
		}
		out.println("</td></tr>");
		Properties config = placement.getConfig();
		out.println("<tr><td>config</td><td>");
		for (Enumeration e = config.propertyNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			out.println(name + "=" + config.getProperty(name) + " ");
		}
		out.println("</td></tr></table>");
	}

	protected void sendView(PrintWriter out, HttpServletRequest req, HttpServletResponse res, String id)
	{
		BrowserItem item = getItem(id);

		out.println("<H1>View " + item.id + "</H1>");
		out.println("<p>This is item " + item.id + ".</p>");
		out.println("<p>one: " + item.one + ".</p>");
		out.println("<p>two: " + item.two + ".</p>");
		out.println("<p><a href=\"" + res.encodeURL(Web.returnUrl(req, "/" + item.id + "/" + "edit")) + "\" title=\"Edit\">"
				+ "edit" + "</a></p>");
		out.println("<p><a href=\"" + res.encodeURL(Web.returnUrl(req, null)) + "\" title=\"List\">" + "list" + "</a></p>");
	}

	protected void sendEdit(PrintWriter out, HttpServletRequest req, HttpServletResponse res, String id)
	{
		final String editHtml = "<form name=\"form1\" method=\"post\" action=\"ACTION\">"
				+ "one<input name=\"one\" id =\"one\" type=\"text\" value=\"ONE\"><br />"
				+ "two<input name=\"two\" id=\"two\" type=\"text\" value=\"TWO\"><br />"
				+ "<input name=\"item\" type=\"hidden\" id=\"item\" value=\"ITEM\">"
				+ "<input name=\"version\" type=\"hidden\" id=\"item\" value=\"VERSION\">"
				+ "<input name=\"submit\" type=\"submit\" id=\"submit\" value=\"Ok\">"
				+ "<input name=\"cancel\" type=\"submit\" id=\"cancel\" value=\"Cancel\"></form>";

		BrowserItem item = getItem(id);

		out.println("<H1>Edit " + item.id + "</H1>");
		out.println("<p>This is item " + item.id + ".  Edit me ...</p>");

		// add our return URL to our form
		String returnUrl = res.encodeURL(Web.returnUrl(req, req.getPathInfo()));
		String html = editHtml.replaceAll("ACTION", res.encodeURL(returnUrl));

		// and the item and version
		html = html.replaceAll("ITEM", item.id);
		html = html.replaceAll("VERSION", "000");

		// and the values
		html = html.replaceAll("ONE", item.one);
		html = html.replaceAll("TWO", item.two);

		// write the form
		out.println(html);
	}

	protected Map m_items = new HashMap();

	protected Collection getItems()
	{
		return m_items.values();
	}

	protected BrowserItem getItem(String id)
	{
		return (BrowserItem) m_items.get(id);
	}

	public class BrowserItem
	{
		public String id;

		public String version;

		public String one;

		public String two;

		public BrowserItem(String id, String version, String one, String two)
		{
			this.id = id;
			this.version = version;
			this.one = one;
			this.two = two;
		}
	}

	protected void showSession(PrintWriter out, boolean html, String context)
	{
		// get the current user session information
		Session s = SessionManager.getCurrentSession();
		if (s == null)
		{
			out.println("no session established");
			if (html) out.println("<br />");
		}
		else
		{
			out.println("session: " + s.getId()
				+ " user id: " + s.getUserId()
				+ " user enterprise id: " + s.getUserEid()
				+ " started: " + DateFormat.getDateInstance().format(new Date(s.getCreationTime()))
				+ " accessed: " + DateFormat.getDateInstance().format(new Date(s.getLastAccessedTime()))
				+ " inactive after: " + s.getMaxInactiveInterval());
			if (html) out.println("<br />");
		}
		
		ToolSession ts = SessionManager.getCurrentToolSession();
		if (ts == null)
		{
			out.println("no tool session established");
			if (html) out.println("<br />");
		}
		else
		{
			out.println("tool session: " + ts.getId()
				+ " started: " + DateFormat.getDateInstance().format(new Date(ts.getCreationTime()))
				+ " accessed: " + DateFormat.getDateInstance().format(new Date(ts.getLastAccessedTime())));
			if (html) out.println("<br />");
		}
		
		if (context != null)
		{
			out.println("placement context: " + context);
			if (html) out.println("<br />");
		}
		else
		{
			out.println("no placement context");
			if (html) out.println("<br />");
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/sample-tools/browser/src/java/org/sakaiproject/tool/sample/BrowserTool.java,v 1.12 2005/05/12 01:38:53 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
