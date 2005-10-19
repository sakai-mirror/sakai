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
import org.sakaiproject.service.legacy.site.Group;
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

	protected static final String siteId = "xxx-group-test";

	protected static final String user1Id = "xxx-group-test-user-1";

	protected static final String user2Id = "xxx-group-test-user-2";

	protected static final String user3Id = "xxx-group-test-user-3";

	protected static final String user4Id = "xxx-group-test-user-4";

	protected static final String siteIdNg = "xxx-group-test-ng";

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Site Group and Group Aware Test Servlet";
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
			out.println("\n\nGrouped Site Tests\n");
			test(out, siteId);

			out.println("\n\nSite / Group serialization Tests\n");
			checkSerialization(siteId, out);
		}
		finally
		{
			// make sure to switch back to the authenticated user id
			s.setUserId(currentUserId);
		}

		try
		{
			out.println("\n\nNon-Grouped Site Tests\n");
			test(out, siteIdNg);
		}
		finally
		{
			// make sure to switch back to the authenticated user id
			s.setUserId(currentUserId);
		}

		// ok, not really a group test...
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
	 * Check the group access lists for the current user.
	 * 
	 * @param channel
	 * @param out
	 */
	protected void checkAccess(MessageChannel channel, PrintWriter out)
	{
		// check the message service's group / permission access
		Collection c = channel.getGroupsAllowAddMessage();
		out.println("groups with add permission:");
		for (Iterator i = c.iterator(); i.hasNext();)
		{
			out.println("  - " + i.next());
		}

		c = channel.getGroupsAllowGetMessage();
		out.println("groups with get permission:");
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

	protected void checkGroupManip(Group group, PrintWriter out)
	{
		// mods via the authz group service
		try
		{
			// edit the group azg
			AuthzGroup azg = AuthzGroupService.getAuthzGroup(group.getReference());
			azg.addMember(user1Id, "maintain", true, false);

			AuthzGroupService.save(azg);
			out.println(" ** group azg modified via azg");
		}
		catch (Throwable t)
		{
			out.println(" *** group azg modified via azg: " + t);
		}

		// mods directly with the group
		try
		{
			// edit the group1 azg
			group.addMember(user1Id, "maintain", true, false);

			SiteService.save(group.getContainingSite());
			out.println(" ** group azg modified via site");
		}
		catch (Throwable t)
		{
			out.println(" *** group azg modified via site: " + t);
		}
	}

	protected void checkSerialization(String sid, PrintWriter out)
	{
		try
		{
			Site site = SiteService.getSite(sid);
			Group group = (Group) site.getGroups().iterator().next();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(site);
			oos.writeObject(group);

			oos.close();
			out.println(" ** serialization done");

			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);

			Site site2 = (Site) ois.readObject();
			Group group2 = (Group) ois.readObject();

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
			// create a site w/ groups
			s.setUserId(user1Id);
			out.println(" ** creating as: " + user1Id);

			Site site = SiteService.addSite(siteId, (String) null);

			// write some properties
			site.getProperties().addProperty("test", "site");
			site.getProperties().addProperty("test2", "site-2");

			// make a group
			Group group1 = site.addGroup();
			group1.setTitle("group one");
			group1.setDescription("this is group one");
			group1.getProperties().addProperty("test-group1", "in group one");
			group1.getProperties().addProperty("test-group1-2", "in group one, again");
			group1.addMember(user1Id, "maintain", true, false);
			group1.addMember(user2Id, "access", true, false);

			String group1Id = group1.getId();

			// make another group
			Group group2 = site.addGroup();
			group2.setTitle("group two");
			group2.setDescription("this is group two");
			group2.getProperties().addProperty("test-group2", "in group two");
			group2.getProperties().addProperty("test-group2-2", "in group two, again");
			group2.addMember(user1Id, "maintain", true, false);
			group2.addMember(user3Id, "access", true, false);

			String group2Id = group2.getId();

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

			// make a message for each group
			try
			{
				msg = channel.addAnnouncementMessage("subject-1", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.GROUPED);
				edit.getHeaderEdit().addGroup(group1);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble with annc group 1: " + t);
			}

			try
			{
				msg = channel.addAnnouncementMessage("subject-2", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.GROUPED);
				edit.getHeaderEdit().addGroup(group2);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble annc with group 2: " + t);
			}

			// make a message for both groups
			try
			{
				msg = channel.addAnnouncementMessage("subject-1&2", false, null, "this is an announcement");
				MessageEdit edit = channel.editMessage(msg.getId());
				edit.getHeaderEdit().setAccess(MessageHeader.MessageAccess.GROUPED);
				edit.getHeaderEdit().addGroup(group1);
				edit.getHeaderEdit().addGroup(group2);
				channel.commitMessage(edit);
			}
			catch (Throwable t)
			{
				out.println(" ** trouble with annc group 1&2: " + t);
			}

			// have a site.maintain'er (user4) create another group
			s.setUserId(user4Id);

			site = SiteService.getSite(siteId);
			Group group4 = site.addGroup();
			group4.setTitle("group4");
			SiteService.save(site);

			String group4Id = group4.getId();

			// and edit each group via the authzGroupService or site
			try
			{
				AuthzGroup groupAzg = AuthzGroupService.getAuthzGroup(SiteService.siteGroupReference(siteId, group1Id));
				groupAzg.addRole("test1");
				AuthzGroupService.save(groupAzg);
				out.println(" ** test1 role added to group 1 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with group 1 via azg: " + t);
			}
			try
			{
				site = SiteService.getSite(siteId);
				Group s1 = site.getGroup(group1Id);
				s1.addRole("test1");
				SiteService.save(site);
				out.println(" ** test1 role added to group 1 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with group 1 via site: " + t);
			}

			try
			{
				site = SiteService.getSite(siteId);
				Group s2 = site.getGroup(group2Id);
				s2.addRole("test2");
				SiteService.save(site);
				out.println(" ** test2 role added to group 2 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with group 2 via site: " + t);
			}
			try
			{
				AuthzGroup groupAzg = AuthzGroupService.getAuthzGroup(SiteService.siteGroupReference(siteId, group2Id));
				groupAzg.addRole("test2");
				AuthzGroupService.save(groupAzg);
				out.println(" ** test2 role added to group 2 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with group 2 via azg: " + t);
			}

			try
			{
				AuthzGroup groupAzg = AuthzGroupService.getAuthzGroup(SiteService.siteGroupReference(siteId, group4Id));
				groupAzg.addRole("test4");
				AuthzGroupService.save(groupAzg);
				out.println(" ** test4 role added to group 4 by user 4 via azg");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " trouble with group 4 via azg: " + t);
			}
			try
			{
				site = SiteService.getSite(siteId);
				Group g4 = site.getGroup(group4Id);
				g4.addRole("test4");
				SiteService.save(site);
				out.println(" ** test4 role added to group 4 by user 4 via site");
			}
			catch (Throwable t)
			{
				out.println(" ** " + user4Id + " (unexpected) trouble with group 4 via site: " + t);
			}

			// back to user 1
			s.setUserId(user1Id);

			// create a site w/o groups
			site = SiteService.addSite(siteIdNg, (String) null);
			site.getProperties().addProperty("test", "site no groups");
			site.getProperties().addProperty("test2", "site-2 no groups");
			SiteService.save(site);

			AuthzGroup azgNs = AuthzGroupService.getAuthzGroup(SiteService.siteReference(siteIdNg));
			// Note: creating as user1Id, this is not need: azg.addMember(user1Id, "maintain", true, false);
			azgNs.addMember(user4Id, "maintain", true, false);
			azgNs.addMember(user2Id, "access", true, false);
			azgNs.addMember(user3Id, "access", true, false);
			AuthzGroupService.save(azgNs);

			AnnouncementChannelEdit channelNs = AnnouncementService.addAnnouncementChannel(AnnouncementService.channelReference(
					siteIdNg, "main"));
			AnnouncementService.commitChannel(channelNs);
			AnnouncementMessage msgNs = channelNs.addAnnouncementMessage("subject NG", false, null, "this is an announcement");

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
			for (Iterator i = site.getGroups().iterator(); i.hasNext();)
			{
				Group group = (Group) i.next();
				out.println("    Group: " + group.getId() + " " + group.getTitle());
				azg = AuthzGroupService.getAuthzGroup(group.getReference());
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
			for (Iterator i = site.getGroups().iterator(); i.hasNext();)
			{
				Group group = (Group) i.next();
				out.println("    Group: " + group.getId() + " " + group.getTitle());
				for (Iterator m = group.getMembers().iterator(); m.hasNext();)
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

		// try to get a group by id and ref
		try
		{
			Site site = SiteService.getSite(sid);
			Group group = (Group) site.getGroups().iterator().next();
			String groupId = group.getId();
			String groupRef = group.getReference();

			Group group2 = site.getGroup(groupId);
			if (group2 == null) out.println(" ** error: cannot find group by id: " + groupId + " in site: " + sid);
			if (group2 != group)
				out.println(" ** error: group by id: " + groupId + " in site: " + sid + " not matching initial group by ==");

			Group group3 = site.getGroup(groupRef);
			if (group3 == null) out.println(" ** error: cannot find group by ref: " + groupRef + " in site: " + sid);
			if (group3 != group)
				out.println(" ** error: group by ref: " + groupRef + " in site: " + sid + " not matching initial group by ==");

			Group group4 = site.getGroup(sid);
			if (group4 != null) out.println(" ** error: found bogus group by id: " + sid + " in site: " + sid);

			out.println("site.getGroup() by id or ref working.");
		}
		catch (Throwable t)
		{
			out.println("Group id/ref access: " + t);
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

			// user 4 has site maintain, nothing in the groups
			s.setUserId(user4Id);
			out.println("\nfor user: " + user4Id);
			checkAccess(channel, out);
		}
		catch (Throwable t)
		{
			out.println("Annc channel/msg access: " + t);
		}

		// group manipulation tests
		try
		{
			s.setUserId(currentUserId);

			Site site = SiteService.getSite(sid);
			Group group = (Group) site.getGroups().iterator().next();

			out.println(" ** group manip test on group: " + group.getTitle());

			s.setUserId(user1Id);
			out.println("\nfor user: " + user1Id + "(should have access)");
			checkGroupManip(group, out);

			s.setUserId(user2Id);
			out.println("\nfor user: " + user2Id + "(should not have access)");
			checkGroupManip(group, out);

			s.setUserId(user3Id);
			out.println("\nfor user: " + user3Id + "(should not have access)");
			checkGroupManip(group, out);

			s.setUserId(user4Id);
			out.println("\nfor user: " + user4Id + "(should have access)");
			checkGroupManip(group, out);
		}
		catch (Throwable t)
		{
			out.println("Group manip: " + t);
		}
	}
}
