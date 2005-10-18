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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import org.sakaiproject.service.legacy.authzGroup.Member;
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

	protected static final String siteId = "xxx-section-test";

	protected static final String user1Id = "xxx-section-test-user-1";

	protected static final String user2Id = "xxx-section-test-user-2";

	protected static final String user3Id = "xxx-section-test-user-3";

	protected static final String user4Id = "xxx-section-test-user-4";

	protected static final String siteIdNs = "xxx-section-test-ns";

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

		Session s = SessionManager.getCurrentSession();
		String currentUserId = s.getUserId();

		// create the test data
		setup(out);

		try
		{
			out.println("\n\nSectioned Site Tests\n");
			test(out, siteId);

			out.println("\n\nSite / Section serialization Tests\n");
			checkSerialization(siteId, out);
		}
		finally
		{
			// make sure to switch back to the authenticated user id
			s.setUserId(currentUserId);
		}

		try
		{
			out.println("\n\nNon-Sectioned Site Tests\n");
			test(out, siteIdNs);
		}
		finally
		{
			// make sure to switch back to the authenticated user id
			s.setUserId(currentUserId);
		}

		// ok, not really a section test...
		try
		{
			out.println("\n\nAuthzGroup getRole Tests");

			String azg = SiteService.siteReference(siteId);
			String role = AuthzGroupService.getUserRole(user1Id, azg);
			out.println("getUserRole() : user: " + user1Id + " role: " + role + " in AuthzGroup: " + azg);

			Collection users = new Vector();
			users.add(user1Id);
			users.add(user2Id);
			users.add("ggolden");
			users.add(user3Id);
			users.add(user4Id);
			Map roles = AuthzGroupService.getUsersRole(users, azg);
			out.println("getUsersRole()");
			if (roles != null)
			{
				for (Iterator i = roles.keySet().iterator(); i.hasNext();)
				{
					String user = (String) i.next();
					role = (String) roles.get(user);
					out.println(" ** user: " + user + " role: " + role);
				}
			}
			else
			{
				out.println("null");
			}
		}
		catch (Throwable t)
		{
			out.println("getting channel messages: " + t);
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

	protected void checkSectionManip(Section section, PrintWriter out)
	{
		// mods via the authz group service
		try
		{
			// edit the section1 azg
			AuthzGroup azg = AuthzGroupService.getAuthzGroup(section.getReference());
			azg.addMember(user1Id, "maintain", true, false);

			AuthzGroupService.save(azg);
			out.println(" ** section azg modified via azg");
		}
		catch (Throwable t)
		{
			out.println(" *** section azg modified via azg: " + t);
		}

		// mods directly with the section
		try
		{
			// edit the section1 azg
			section.addMember(user1Id, "maintain", true, false);

			SiteService.save(section.getContainingSite());
			out.println(" ** section azg modified via site");
		}
		catch (Throwable t)
		{
			out.println(" *** section azg modified via site: " + t);
		}
	}

	protected void checkSerialization(String sid, PrintWriter out)
	{
		try
		{
			Site site = SiteService.getSite(sid);
			Section section = (Section) site.getSections().iterator().next();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(site);
			oos.writeObject(section);

			oos.close();
			out.println(" ** serialization done");

			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);

			Site site2 = (Site) ois.readObject();
			Section section2 = (Section) ois.readObject();

			out.print(" ** restoration done");
		}
		catch (Throwable t)
		{
			out.println("checking serialization: " + t);
		}
	}

	protected void setup(PrintWriter out)
	{
		Session s = SessionManager.getCurrentSession();
		String currentUserId = s.getUserId();

		try
		{
			// create some users
			UserDirectoryService.addUser(user1Id, "one", "user", "", "", "maintain", null);
			UserDirectoryService.addUser(user2Id, "two", "user", "", "", "", null);
			UserDirectoryService.addUser(user3Id, "three", "user", "", "", "", null);
			UserDirectoryService.addUser(user4Id, "four", "user", "", "", "maintain", null);

			// to test that someone other than admin can do this, lets be user1
			// create a site w/ sections
			s.setUserId(user1Id);
			out.println(" ** creating as: " + user1Id);

			Site site = SiteService.addSite(siteId, (String) null);

			// write some properties
			site.getProperties().addProperty("test", "site");
			site.getProperties().addProperty("test2", "site-2");

			// make a section
			Section section1 = site.addSection();
			section1.setTitle("section one");
			section1.setDescription("this is section one");
			section1.getProperties().addProperty("test-section1", "in section one");
			section1.getProperties().addProperty("test-section1-2", "in section one, again");
			section1.addMember(user1Id, "maintain", true, false);
			section1.addMember(user2Id, "access", true, false);

			String section1Id = section1.getId();

			// make another section
			Section section2 = site.addSection();
			section2.setTitle("section two");
			section2.setDescription("this is section two");
			section2.getProperties().addProperty("test-section2", "in section two");
			section2.getProperties().addProperty("test-section2-2", "in section two, again");
			section2.addMember(user1Id, "maintain", true, false);
			section2.addMember(user3Id, "access", true, false);

			String section2Id = section2.getId();

			// Note: creating as user1Id, this is not need: azg.addMember(user1Id, "maintain", true, false);
			site.addMember(user4Id, "maintain", true, false);
			site.addMember(user2Id, "access", true, false);
			site.addMember(user3Id, "access", true, false);

			// save the site
			SiteService.save(site);

			// make an announcement in the site
			AnnouncementChannelEdit channel = AnnouncementService.addAnnouncementChannel(AnnouncementService.channelReference(
					siteId, "main"));
			AnnouncementService.commitChannel(channel);

			AnnouncementMessage msg = channel.addAnnouncementMessage("subject", false, null, "this is an announcement");

			// make a message for each section
			try
			{
				msg = channel.addAnnouncementMessage("subject-1", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
				edit.getHeaderEdit().addSection(section1);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble with annc section 1: " + t);
			}

			try
			{
				msg = channel.addAnnouncementMessage("subject-2", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
				edit.getHeaderEdit().addSection(section2);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble annc with section 2: " + t);
			}

			// make a message for both sections
			try
			{
				msg = channel.addAnnouncementMessage("subject-1&2", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.SECTIONED);
				edit.getHeaderEdit().addSection(section1);
				edit.getHeaderEdit().addSection(section2);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble with annc section 1&2: " + t);
			}

			// have a site.maintain'er (user4) create another section
			s.setUserId(user4Id);

			site = SiteService.getSite(siteId);
			Section section4 = site.addSection();
			section4.setTitle("section4");
			SiteService.save(site);

			String section4Id = section4.getId();

			// and edit each section via the authzGroupService or site
			try
			{
				AuthzGroup sectionAzg = AuthzGroupService.getAuthzGroup(SiteService.siteSectionReference(siteId, section1Id));
				sectionAzg.addRole("test1");
				AuthzGroupService.save(sectionAzg);
				out.println(" ** test1 role added to section 1 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with section 1 via azg: " + t);
			}
			try
			{
				site = SiteService.getSite(siteId);
				Section s1 = site.getSection(section1Id);
				s1.addRole("test1");
				SiteService.save(site);
				out.println(" ** test1 role added to section 1 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with section 1 via site: " + t);
			}

			try
			{
				site = SiteService.getSite(siteId);
				Section s2 = site.getSection(section2Id);
				s2.addRole("test2");
				SiteService.save(site);
				out.println(" ** test2 role added to section 2 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with section 2 via site: " + t);
			}
			try
			{
				AuthzGroup sectionAzg = AuthzGroupService.getAuthzGroup(SiteService.siteSectionReference(siteId, section2Id));
				sectionAzg.addRole("test2");
				AuthzGroupService.save(sectionAzg);
				out.println(" ** test2 role added to section 2 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with section 2 via azg: " + t);
			}
			
			try
			{
				AuthzGroup sectionAzg = AuthzGroupService.getAuthzGroup(SiteService.siteSectionReference(siteId, section4Id));
				sectionAzg.addRole("test4");
				AuthzGroupService.save(sectionAzg);
				out.println(" ** test4 role added to section 4 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with section 4 via azg: " + t);
			}
			try
			{
				site = SiteService.getSite(siteId);
				Section s4 = site.getSection(section4Id);
				s4.addRole("test4");
				SiteService.save(site);
				out.println(" ** test4 role added to section 4 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " (unexpected) trouble with section 4 via site: " + t);
			}

			// back to user 1
			s.setUserId(user1Id);

			// create a site w/o sections
			site = SiteService.addSite(siteIdNs, (String)  null);
			site.getProperties().addProperty("test", "site no sections");
			site.getProperties().addProperty("test2", "site-2 no sections");
			SiteService.save(site);

			AuthzGroup azgNs = AuthzGroupService.getAuthzGroup(SiteService.siteReference(siteIdNs));
			// Note: creating as user1Id, this is not need: azg.addMember(user1Id, "maintain", true, false);
			azgNs.addMember(user4Id, "maintain", true, false);
			azgNs.addMember(user2Id, "access", true, false);
			azgNs.addMember(user3Id, "access", true, false);
			AuthzGroupService.save(azgNs);

			AnnouncementChannelEdit channelNs = AnnouncementService.addAnnouncementChannel(AnnouncementService.channelReference(siteIdNs, "main"));
			AnnouncementService.commitChannel(channelNs);
			AnnouncementMessage msgNs = channelNs.addAnnouncementMessage("subject NS", false, null, "this is an announcement");

			out.println("setup complete.");
		}
		catch (Throwable t)
		{
			out.println(t);
		}
		finally
		{
			s.setUserId(currentUserId);
		}
	}

	protected void test(PrintWriter out, String sid)
	{
		Session s = SessionManager.getCurrentSession();
		String currentUserId = s.getUserId();

		try
		{
			Site site = SiteService.getSite(sid);
			out.println("Site: " + site.getId() + " " + site.getTitle());
			out.println(" ** membership via azg");
			AuthzGroup azg = AuthzGroupService.getAuthzGroup(site.getReference());
			for (Iterator m = azg.getMembers().iterator(); m.hasNext();)
			{
				Member mbr = (Member) m.next();
				out.println("    Member: " + mbr.getUserId() + " " + mbr.getRole().getId());
			}
			for (Iterator i = site.getSections().iterator(); i.hasNext();)
			{
				Section section = (Section) i.next();
				out.println("    Section: " + section.getId() + " " + section.getTitle());
				azg = AuthzGroupService.getAuthzGroup(section.getReference());
				for (Iterator m = azg.getMembers().iterator(); m.hasNext();)
				{
					Member mbr = (Member) m.next();
					out.println("        Member: " + mbr.getUserId() + " " + mbr.getRole().getId());
				}
			}
			
			out.println("\n ** membership via site");
			for (Iterator m = site.getMembers().iterator(); m.hasNext();)
			{
				Member mbr = (Member) m.next();
				out.println("    Member: " + mbr.getUserId() + " " + mbr.getRole().getId());
			}
			for (Iterator i = site.getSections().iterator(); i.hasNext();)
			{
				Section section = (Section) i.next();
				out.println("    Section: " + section.getId() + " " + section.getTitle());
				for (Iterator m = section.getMembers().iterator(); m.hasNext();)
				{
					Member mbr = (Member) m.next();
					out.println("        Member: " + mbr.getUserId() + " " + mbr.getRole().getId());
				}
			}
		}
		catch (Throwable t)
		{
			out.println("site display: " + t);
		}

		// try to get a section by id and ref
		try
		{
			Site site = SiteService.getSite(sid);
			Section section = (Section) site.getSections().iterator().next();
			String sectionId = section.getId();
			String sectionRef = section.getReference();

			Section section2 = site.getSection(sectionId);
			if (section2 == null) out.println(" ** error: cannot find section by id: " + sectionId + " in site: " + sid);
			if (section2 != section)
				out.println(" ** error: section by id: " + sectionId + " in site: " + sid + " not matching initial section by ==");

			Section section3 = site.getSection(sectionRef);
			if (section3 == null) out.println(" ** error: cannot find section by ref: " + sectionRef + " in site: " + sid);
			if (section3 != section)
				out
						.println(" ** error: section by ref: " + sectionRef + " in site: " + sid
								+ " not matching initial section by ==");

			Section section4 = site.getSection(sid);
			if (section4 != null) out.println(" ** error: found bogus section by id: " + sid + " in site: " + sid);

			out.println("site.getSection() by id or ref working.");
		}
		catch (Throwable t)
		{
			out.println("Section id/ref access: " + t);
		}

		// get the channel
		try
		{
			AnnouncementChannel channel = AnnouncementService.getAnnouncementChannel(AnnouncementService.channelReference(sid,
					"main"));
			checkAccess(channel, out);

			// switch to the users and see what they can do
			s.setUserId(user1Id);
			out.println("\nfor user: " + user1Id);
			checkAccess(channel, out);

			s.setUserId(user2Id);
			out.println("\nfor user: " + user2Id);
			checkAccess(channel, out);

			s.setUserId(user3Id);
			out.println("\nfor user: " + user3Id);
			checkAccess(channel, out);

			// user 4 has site maintain, nothing in the sections
			s.setUserId(user4Id);
			out.println("\nfor user: " + user4Id);
			checkAccess(channel, out);
		}
		catch (Throwable t)
		{
			out.println("Annc channel/msg access: " + t);
		}

		// section manipulation tests
		try
		{
			s.setUserId(currentUserId);

			Site site = SiteService.getSite(sid);
			Section section = (Section) site.getSections().iterator().next();
			
			out.println(" ** section manip test on section: " + section.getTitle());

			s.setUserId(user1Id);
			out.println("\nfor user: " + user1Id + "(should have access)");
			checkSectionManip(section, out);

			s.setUserId(user2Id);
			out.println("\nfor user: " + user2Id + "(should not have access)");
			checkSectionManip(section, out);

			s.setUserId(user3Id);
			out.println("\nfor user: " + user3Id + "(should not have access)");
			checkSectionManip(section, out);

			s.setUserId(user4Id);
			out.println("\nfor user: " + user4Id + "(should have access)");
			checkSectionManip(section, out);
		}
		catch (Throwable t)
		{
			out.println("Section manip: " + t);
		}
	}
}
