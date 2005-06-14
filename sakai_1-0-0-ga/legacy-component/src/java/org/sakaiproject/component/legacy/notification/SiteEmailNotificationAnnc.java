/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/SiteEmailNotificationAnnc.java,v 1.8 2004/07/19 17:12:34 dgs.umich.edu Exp $
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

// package
package org.sakaiproject.component.legacy.notification;

// imports
import java.util.Iterator;

import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeader;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.FormattedText;

/**
* <p>SiteEmailNotificationAnnc fills the notification message with details from the announcement message that triggered
* the notification event.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class SiteEmailNotificationAnnc
	extends SiteEmailNotification
{
	/**
	* Construct.
	*/
	public SiteEmailNotificationAnnc() {}

	/**
	* Construct.
	*/
	public SiteEmailNotificationAnnc(String siteId)
	{
		super(siteId);

	}	// SiteEmailNotificationAnnc

	/**
	 * Get the additional permission function ability string needed for the resource that is the target of the
	 * notification - users who get notified need to have this ability with this resource, too.
	 * @return The additional ability string needed for a user to receive notification.
	 */
	protected String getResourceAbility()
	{
		return AnnouncementService.SECURE_ANNC_READ;
	}

	/**
	* Make a new one like me.
	* @return A new action just like me.
	*/
	public NotificationAction getClone()
	{
		SiteEmailNotificationAnnc clone = new SiteEmailNotificationAnnc();
		clone.set(this);

		return clone;

	}	// getClone

	/**
	* Do the notification.
	* @param notification The notification responding to the event.
	* @param event The event that matched criteria to cause the notification.
	*/
	public void notify(Notification notification, Event event)
	{
		// get the message
		Reference ref = new Reference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getResource();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// skip drafts
		if (hdr.getDraft()) return;

		super.notify(notification, event);

	}	// notify

	/**
	* Get the from address for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the from address for the email.
	*/
	protected String getFrom(Event event)
	{
		// get the message
		Reference ref = new Reference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getResource();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// make it from the message's creator
		return hdr.getFrom().getEmail();

	}	// getFrom

	/**
	* Get the subject for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the subject for the email.
	*/
	public String getSubject(Event event)
	{
		// get the message
		Reference ref = new Reference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getResource();
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
		catch (Exception ignore) {}

		// use the message's subject
		return "[ " + title + " - Announcement ]   " + hdr.getSubject();

	}	// getSubject

	/**
	* Get the message for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the message for the email.
	*/
	protected String getMessage(Event event)
	{
		StringBuffer buf = new StringBuffer();

		// get the message
		Reference ref = new Reference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getResource();
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
		catch (Exception ignore) {}

		// use the message's body
		// %%% JANDERSE convert to plaintext - email is currently sent plaintext only,
		// so text formatting that may be present in the message should be removed.
		buf.append(FormattedText.convertFormattedTextToPlaintext(msg.getBody()) + "\n");

		// add any attachments
		ReferenceVector attachments = hdr.getAttachments();
		if (attachments.size() > 0)
		{
			buf.append("\n" + "Attachments:\n");
			for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext(); )
			{
				Reference attachment = (Reference) iAttachments.next();
				String attachmentTitle = attachment.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				buf.append("\n" + attachmentTitle);
				buf.append("\n" + attachment.getUrl() + "\n");
			}
		}

		return buf.toString();

	}	// getMessage

}   // SiteEmailNotificationAnnc

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/notification/SiteEmailNotificationAnnc.java,v 1.8 2004/07/19 17:12:34 dgs.umich.edu Exp $
*
**********************************************************************************/
