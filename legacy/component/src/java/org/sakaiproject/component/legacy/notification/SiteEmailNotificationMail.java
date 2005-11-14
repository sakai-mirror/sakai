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
import org.sakaiproject.service.legacy.email.MailArchiveMessage;
import org.sakaiproject.service.legacy.email.MailArchiveMessageHeader;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.notification.SiteEmailNotification;

/**
 * <p>
 * SiteEmailNotificationMail fills the notification message and headers with details from the email message that triggered the notification event.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class SiteEmailNotificationMail extends SiteEmailNotification
{
	// borrow from announcement's notification class
	private static ResourceBundle rb = ResourceBundle.getBundle("siteemaanc");

	/**
	 * Construct.
	 */
	public SiteEmailNotificationMail()
	{
	}

	/**
	 * Construct.
	 */
	public SiteEmailNotificationMail(String siteId)
	{
		super(siteId);
	}

	/**
	 * @inheritDoc
	 */
	protected String getResourceAbility()
	{
		return MailArchiveService.SECURE_MAIL_READ;
	}

	/**
	 * @inheritDoc
	 */
	public NotificationAction getClone()
	{
		SiteEmailNotificationMail clone = new SiteEmailNotificationMail();
		clone.set(this);

		return clone;
	}

	/**
	 * @inheritDoc
	 */
	protected String getMessage(Event event)
	{
		StringBuffer buf = new StringBuffer();

		// get the message
		Reference ref = EntityManager.newReference(event.getResource());
		MailArchiveMessage msg = (MailArchiveMessage) ref.getEntity();
		MailArchiveMessageHeader hdr = (MailArchiveMessageHeader) msg.getMailArchiveHeader();

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

		// use the message's body
		// %%% JANDERSE convert to plaintext - email is currently sent plaintext only,
		// so text formatting that may be present in the message should be removed.
		buf.append(FormattedText.convertFormattedTextToPlaintext(msg.getBody()));

		// add any attachments
		List attachments = hdr.getAttachments();
		if (attachments.size() > 0)
		{
			buf.append("\n" + "Attachments:\n");
			for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext();)
			{
				Reference attachment = (Reference) iAttachments.next();
				String attachmentTitle = attachment.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				buf.append("\n" + attachmentTitle);
				buf.append("\n" + attachment.getUrl() + "\n");
			}
		}

		return buf.toString();
	}

	/**
	 * @inheritDoc
	 */
	protected List getHeaders(Event event)
	{
		// send most of the headers from the original message, removing some
		Reference ref = EntityManager.newReference(event.getResource());
		MailArchiveMessage msg = (MailArchiveMessage) ref.getEntity();
		MailArchiveMessageHeader hdr = (MailArchiveMessageHeader) msg.getMailArchiveHeader();
		List headers = hdr.getMailHeaders();

		List filteredHeaders = new ArrayList();
		String innerContentType = null;
		String outerContentType = null;
		String contentType = null;

		for (int i = 0; i < headers.size(); i++)
		{
			String headerStr = (String) headers.get(i);

			if (headerStr.startsWith("Return-Path") || headerStr.startsWith("Mime-Version")
					|| headerStr.startsWith("Content-Transfer-Encoding")) continue;

			if (headerStr.startsWith(MailArchiveService.HEADER_INNER_CONTENT_TYPE + ": ")) innerContentType = headerStr;
			if (headerStr.startsWith(MailArchiveService.HEADER_OUTER_CONTENT_TYPE + ": ")) outerContentType = headerStr;

			if (!headerStr.startsWith("Content-Type: "))
			{
				filteredHeaders.add(headerStr);
			}
			else
			{
				contentType = headerStr;
			}
		}

		if (innerContentType != null)
		{
			// use the content type of the inner email message body
			filteredHeaders.add(innerContentType.replaceAll(MailArchiveService.HEADER_INNER_CONTENT_TYPE, "Content-Type"));
		}
		else if (outerContentType != null)
		{
			// use the content type from the outer message (content type as set in the email originally)
			filteredHeaders.add(outerContentType.replaceAll(MailArchiveService.HEADER_OUTER_CONTENT_TYPE, "Content-Type"));
		}
		else if (contentType != null)
		{
			// Oh well, use the plain old Content-Type header
			filteredHeaders.add(contentType);
		}

		return filteredHeaders;
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
	protected boolean headerToRecipient()
	{
		return false;
	}
}
