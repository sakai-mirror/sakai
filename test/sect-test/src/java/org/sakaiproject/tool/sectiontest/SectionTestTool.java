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

package org.sakaiproject.tool.sectiontest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.announcement.AnnouncementChannel;
import org.sakaiproject.service.legacy.announcement.AnnouncementChannelEdit;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageEdit;
import org.sakaiproject.service.legacy.message.MessageHeader;
import org.sakaiproject.service.legacy.site.Section;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>
 * Sakai test servlet.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class SectionTestTool extends HttpServlet
{
	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Section Test Servlet";
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
		log(this + ".init()");
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		log(this + ".destroy()");

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
		PrintWriter out = res.getWriter();

		out.println(getServletInfo());

		String siteId = "xxx-section-test";
		String user1Id = "xxx-section-test-user-1";
		String user2Id = "xxx-section-test-user-2";
		String user3Id = "xxx-section-test-user-3";
		try
		{
			// create a site
			Site site = SiteService.addSite(siteId);

			// write some properties
			site.getProperties().addProperty("test", "site");
			site.getProperties().addProperty("test2", "site-2");

			// make a section
			Section section1 = site.addSection();
			section1.setTitle("section one");
			section1.setDescription("this is section one");
			section1.getProperties().addProperty("test-section1", "in section one");
			section1.getProperties().addProperty("test-section1-2", "in section one, again");

			String section1Id = section1.getId();

			// make another section
			Section section2 = site.addSection();
			section2.setTitle("section two");
			section2.setDescription("this is section two");
			section2.getProperties().addProperty("test-section2", "in section two");
			section2.getProperties().addProperty("test-section2-2", "in section two, again");

			String section2Id = section2.getId();

			// save the site
			SiteService.save(site);

			// create some users
			UserDirectoryService.addUser(user1Id, "one", "user", "", "", "", null);
			UserDirectoryService.addUser(user2Id, "two", "user", "", "", "", null);
			UserDirectoryService.addUser(user3Id, "three", "user", "", "", "", null);

			// edit the site azg
			AuthzGroup azg = AuthzGroupService.getAuthzGroup(SiteService.siteReference(siteId));
			azg.addMember(user1Id, "maintain", true, false);
			azg.addMember(user2Id, "access", true, false);
			azg.addMember(user3Id, "access", true, false);

			AuthzGroupService.save(azg);

			// edit the section1 azg
			azg = AuthzGroupService.getAuthzGroup(SiteService.siteSectionReference(siteId, section1Id));
			azg.addMember(user1Id, "maintain", true, false);
			azg.addMember(user2Id, "access", true, false);

			AuthzGroupService.save(azg);

			// edit the section2 azg
			azg = AuthzGroupService.getAuthzGroup(SiteService.siteSectionReference(siteId, section2Id));
			azg.addMember(user1Id, "maintain", true, false);
			azg.addMember(user3Id, "access", true, false);

			AuthzGroupService.save(azg);

			// make an announcement in the site
			AnnouncementChannelEdit channel = AnnouncementService.addAnnouncementChannel(AnnouncementService.channelReference(
					siteId, "main"));
			AnnouncementService.commitChannel(channel);

			AnnouncementMessage msg = channel.addAnnouncementMessage("subject", false, null, "this is an announcement");

			// make a message for each section
			msg = channel.addAnnouncementMessage("subject-1", false, null, "this is an announcement");
			MessageEdit edit = channel.editMessage(msg.getId());
			edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
			edit.getHeaderEdit().addSection(section1);
			channel.commitMessage(edit);

			msg = channel.addAnnouncementMessage("subject-2", false, null, "this is an announcement");
			edit = channel.editMessage(msg.getId());
			edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
			edit.getHeaderEdit().addSection(section2);
			channel.commitMessage(edit);

			// make a message for both sections
			msg = channel.addAnnouncementMessage("subject-1&2", false, null, "this is an announcement");
			edit = channel.editMessage(msg.getId());
			edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
			edit.getHeaderEdit().addSection(section1);
			edit.getHeaderEdit().addSection(section2);
			channel.commitMessage(edit);

			out.println("setup complete.");
		}
		catch (Throwable t)
		{
			out.println(t);
		}

		// get the channel
		try
		{
			AnnouncementChannel channel = AnnouncementService.getAnnouncementChannel(AnnouncementService.channelReference(siteId,
					"main"));
			checkAccess(channel, out);

			// switch to the users and see what they can do
			Session s = SessionManager.getCurrentSession();
			String currentUserId = s.getUserId();
			try
			{
				s.setUserId(user1Id);
				out.println("\nfor user: " + user1Id);
				checkAccess(channel, out);

				s.setUserId(user2Id);
				out.println("\nfor user: " + user2Id);
				checkAccess(channel, out);

				s.setUserId(user3Id);
				out.println("\nfor user: " + user3Id);
				checkAccess(channel, out);
			}
			finally
			{
				// make sure to switch back to the authenticated user id
				s.setUserId(currentUserId);
			}
		}
		catch (Throwable t)
		{
			out.println(t);
		}
	}

	/**
	 * Check the section access lists for the current user.
	 * 
	 * @param channel
	 * @param out
	 */
	protected void checkAccess(MessageChannel channel, PrintWriter out)
	{
		// check the message service's section / permission access
		Collection c = channel.getSectionsAllowAddMessage();
		out.println("sections with add permission:");
		for (Iterator i = c.iterator(); i.hasNext();)
		{
			out.println("  - " + i.next());
		}

		c = channel.getSectionsAllowGetMessage();
		out.println("sections with get permission:");
		for (Iterator i = c.iterator(); i.hasNext();)
		{
			out.println("  - " + i.next());
		}

		try
		{
			boolean allowed = channel.allowGetMessages();
			out.println("channel.allowGetMessages: " + allowed);

			List messages = channel.getMessages(null, true);
			for (Iterator i = messages.iterator(); i.hasNext();)
			{
				AnnouncementMessage message = (AnnouncementMessage) i.next();
				out.println(" ** message: " + message.getHeader().getId() + " " + message.getAnnouncementHeader().getSubject());
			}
		}
		catch (Throwable t)
		{
			out.println("getting channel messages: " + t);
		}
	}
}
