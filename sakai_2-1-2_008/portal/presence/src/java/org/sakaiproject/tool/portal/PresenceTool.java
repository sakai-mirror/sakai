/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.portal;

// imports
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

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
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.util.courier.PresenceObservingCourier;
import org.sakaiproject.util.java.ResourceLoader;
import org.sakaiproject.util.web.Web;

/**
 * <p>
 * Presence is an tool which presents an auto-updating user presence list.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Id$
 */
public class PresenceTool extends HttpServlet
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(PresenceTool.class);

	/** Tool state attribute where the observer is stored. */
	protected static final String ATTR_OBSERVER = "observer";

	/** Common Tool ID for the Sakai Chat Tool */
	protected static final String CHAT_COMMON_ID = "sakai.chat";

	/** Tool state attribute where the chat observer is stored. */
	protected static final String ATTR_CHAT_OBSERVER = "chat_observer";

   /** Localized messages **/
	ResourceLoader rb = new ResourceLoader("org.sakaiproject.tool.portal.bundle.Messages");
	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		M_log.info("destroy()");

		super.destroy();
	}

	/**
	 * Respond to access requests.
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
		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// get the current tool session, where we store our observer
		ToolSession toolSession = SessionManager.getCurrentToolSession();

		// get the current tool placement
		Placement placement = ToolManager.getCurrentPlacement();

		// location is just placement
		String location = placement.getId();

		// refresh our presence at the location
		PresenceService.setPresence(location);

		// make sure we have an observing watching for presence change at location
		PresenceObservingCourier observer = (PresenceObservingCourier) toolSession.getAttribute(ATTR_OBSERVER);
		if (observer == null)
		{
			// setup an observer to notify us when presence at this location changes
			observer = new PresenceObservingCourier(location);
			toolSession.setAttribute(ATTR_OBSERVER, observer);
		}

		// get the list of users at the location
		List users = PresenceService.getPresentUsers(location);

		// get SiteId from the current placement and retrieve site
		String siteId = placement.getContext();

                Site site = null;
		ToolConfiguration toolConfig = null;
		List chatUsers = null;

		if ( siteId != null ) {
                	try
                	{
                        	site = SiteService.getSiteVisit(siteId);
                	}       
                	catch (Exception e)
                	{
                        	// No problem - leave site null
                	}
		}

		if ( site != null ) 
		{
                        toolConfig = site.getToolForCommonId(CHAT_COMMON_ID);
		}

		if ( toolConfig != null ) {
			String chatLocation = toolConfig.getId();
			chatUsers = PresenceService.getPresentUsers(chatLocation);

			PresenceObservingCourier chatObserver = (PresenceObservingCourier) toolSession.getAttribute(ATTR_CHAT_OBSERVER);
                	if (chatObserver == null)
                	{
				// Monitor presense changes at chatLocation and deliver them to this window's location with
				// no sub window (null)
                        	chatObserver = new PresenceObservingCourier(location,null,chatLocation);
                        	toolSession.setAttribute(ATTR_CHAT_OBSERVER, chatObserver);
                	}
		}

		// start the response
		PrintWriter out = startResponse(req, res, "presence");

		sendAutoUpdate(out, req, placement.getId(), placement.getContext());
		sendPresence(out, users, chatUsers);

		// end the response
		endResponse(out);
	}

	/**
	 * End the response
	 * 
	 * @param out
	 * @throws IOException
	 */
	protected void endResponse(PrintWriter out) throws IOException
	{
		out.println("</body></html>");
	}

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Presence Tool";
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
	 * Send the HTML / Javascript to invoke an automatic update
	 * 
	 * @param out
	 * @param req
	 * @param placementId
	 * @param context
	 */
	protected void sendAutoUpdate(PrintWriter out, HttpServletRequest req, String placementId, String context)
	{
		// set the refresh of the courier to 1/2 the presence timeout value
		Web.sendAutoUpdate(out, req, placementId, PresenceService.getTimeout() / 2);
//		out.println("<div id=\"update_a\" style=\"display:block\"> a </div>");
//		out.println("<div id=\"update_b\" style=\"display:none\"> b </div>");
	}

	/**
	 * Format the list of users
	 * 
	 * @param out
	 * @param users
	 */
	protected void sendPresence(PrintWriter out, List users, List chatUsers)
	{
 		String chatIcon  = ServerConfigurationService.getString("presence.inchat.icon", null);

		out.println("<ul class=\"presenceList\">");
		if ( users == null ) {
			out.println("<!-- Presence empty -->");
			out.println("</ul>");
			return;
		}

		for (Iterator i = users.iterator(); i.hasNext();)
		{
			User u = (User) i.next();
			boolean inChat = false;
			if ( chatUsers != null ) 
			{
				String userId = u.getId();
				for (Iterator j = chatUsers.iterator();j.hasNext(); ) 
				{
					User chatUser = (User) j.next();
					if ( userId != null && userId.equals(chatUser.getId()) )
					{
						inChat = true;
					}
				}
			} 

			if ( inChat ) 
			{
            String msg = rb.getString("inchat");
				out.print("<li class=\"inChat\">");
				out.print("<span title=\"" + msg + "\">");
				out.print(Web.escapeHtml(u.getDisplayName()));
				if ( chatIcon != null ) 
				{
//					out.print(" <img height=10px width=10px src=\""+chatIcon+"\">");
				}
			}
			else
			{
            String msg = rb.getString("insite");
				out.print("<li>");
				out.print("<span title=\"" + msg + "\">"); 
				out.print(Web.escapeHtml(u.getDisplayName()));
			}
			out.println("</span></li>");
		}

		out.println("</ul>");
	}

	/**
	 * Start the response.
	 * 
	 * @param req
	 * @param res
	 * @param title
	 * @param skin
	 * @return
	 * @throws IOException
	 */
	protected PrintWriter startResponse(HttpServletRequest req, HttpServletResponse res, String title) throws IOException
	{
		// headers
		res.setContentType("text/html; charset=UTF-8");
		res.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		res.addDateHeader("Last-Modified", System.currentTimeMillis());
		res.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		res.addHeader("Pragma", "no-cache");

		// get the writer
		PrintWriter out = res.getWriter();

		// form the head
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">" + "  <head>"
				+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");

		// send the portal set-up head
		String head = (String) req.getAttribute("sakai.html.head");
		if (head != null)
		{
			out.println(head);
		}

		out.println("</head>");

		// Note: we ignore the portal set-up body onload

		// start the body
		out.println("<body>");

		return out;
	}
}



