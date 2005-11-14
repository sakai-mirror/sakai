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

package org.sakaiproject.component.legacy.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeader;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.notification.SiteEmailNotification;

/**
 * <p>
 * SiteEmailNotificationAnnc fills the notification message and headers with details from the announcement message that triggered the notification event.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class SiteEmailNotificationAnnc extends SiteEmailNotification
{
	private static ResourceBundle rb = ResourceBundle.getBundle("siteemaanc");

	/**
	 * Construct.
	 */
	public SiteEmailNotificationAnnc()
	{
	}

	/**
	 * Construct.
	 */
	public SiteEmailNotificationAnnc(String siteId)
	{
		super(siteId);
	}

	/**
	 * @inheritDoc
	 */
	protected String getResourceAbility()
	{
		return AnnouncementService.SECURE_ANNC_READ;
	}

	/**
	 * @inheritDoc
	 */
	public NotificationAction getClone()
	{
		SiteEmailNotificationAnnc clone = new SiteEmailNotificationAnnc();
		clone.set(this);

		return clone;
	}

	/**
	 * @inheritDoc
	 */
	public void notify(Notification notification, Event event)
	{
		// get the message
		Reference ref = EntityManager.newReference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// skip drafts
		if (hdr.getDraft()) return;

		super.notify(notification, event);
	}

	/**
	 * @inheritDoc
	 */
	protected String getMessage(Event event)
	{
		StringBuffer buf = new StringBuffer();
		String newline = "<br />\n";

		// get the message
		Reference ref = EntityManager.newReference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// get a site title
		String title = siteId;
		try
		{
			Site site = SiteService.getSite(siteId);
			title = site.getTitle();
		}
		catch (Exception ignore)
		{
		}

		// Now build up the message text.
		buf.append(rb.getString("An_announcement_has_been"));
		if (AnnouncementService.SECURE_ANNC_ADD.equals(event.getEvent()))
		{
			buf.append(" " + rb.getString("added"));
		}
		else
		{
			buf.append(" " + rb.getString("updated"));
		}
		buf.append(" " + rb.getString("in_the") + " \"");
		buf.append(title);
		buf.append("\" " + rb.getString("site_at"));
		buf.append(" " + ServerConfigurationService.getString("ui.service", "Sakai"));
		buf.append(" (<a href=\"");
		buf.append(ServerConfigurationService.getPortalUrl());
		buf.append("\">");
		buf.append(ServerConfigurationService.getPortalUrl());
		buf.append("</a>)");
		buf.append(newline);
		buf.append(newline);
		buf.append(newline);
		buf.append(rb.getString("Subject") + ": ");
		buf.append(hdr.getSubject());
		buf.append(newline);
		buf.append(newline);
		buf.append(rb.getString("From") + ": ");
		buf.append(hdr.getFrom().getDisplayName());
		buf.append(newline);
		buf.append(newline);
		buf.append(rb.getString("Date") + ": ");
		buf.append(hdr.getDate().toStringLocalFull());
		buf.append(newline);
		buf.append(newline);
		buf.append(" --- " + rb.getString("Message") + " ---");
		buf.append(newline);
		buf.append(newline);
		buf.append(msg.getBody());
		buf.append(newline);
		buf.append(newline);

		// add any attachments
		List attachments = hdr.getAttachments();
		if (attachments.size() > 0)
		{
			buf.append(newline + rb.getString("Attachments") + newline);
			for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext();)
			{
				Reference attachment = (Reference) iAttachments.next();
				String attachmentTitle = attachment.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				buf.append("<a href=\"" + attachment.getUrl() + "\">" + attachmentTitle + "</a>" + newline);
			}
		}

		return buf.toString();
	}

	/**
	 * @inheritDoc
	 */
	protected List getHeaders(Event e)
	{
		List rv = new ArrayList(2);

		// Set the content type of the message body to HTML
		rv.add("Content-Type: text/html");

		// set the subject
		rv.add("Subject: " + getSubject(e));

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	protected String getTag(String newline, String title)
	{
		// tag the message
		String rv = newline + rb.getString("separator") + newline + rb.getString("this") + " "
				+ ServerConfigurationService.getString("ui.service", "Sakai") + " ("
				+ ServerConfigurationService.getServerUrl() + ") " + rb.getString("forthe") + " " + title
				+ " " + rb.getString("site") + newline + rb.getString("youcan") + newline;
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	protected boolean isBodyHTML(Event e)
	{
		return true;
	}

	/**
	 * Format the announcement notification subject line.
	 * 
	 * @param event
	 *        The event that matched criteria to cause the notification.
	 * @return the announcement notification subject line.
	 */
	protected String getSubject(Event event)
	{
		// get the message
		Reference ref = EntityManager.newReference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// get a site title
		String title = siteId;
		try
		{
			Site site = SiteService.getSite(siteId);
			title = site.getTitle();
		}
		catch (Exception ignore)
		{
		}

		// use the message's subject
		return "[ " + title + " - " + rb.getString("Announcement") + " ]   " + hdr.getSubject();
	}
}
