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

// package
package org.sakaiproject.component.legacy.notification;

// imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeader;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
* <p>SiteEmailNotificationAnnc fills the notification message with details from the announcement message that triggered
* the notification event.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/


public class SiteEmailNotificationAnnc
	extends SiteEmailNotification
{
	private static ResourceBundle rb = ResourceBundle.getBundle("siteemaanc");
	
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
		Reference ref = EntityManager.newReference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
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
		Reference ref = EntityManager.newReference(event.getResource());
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		// make it from the message's creator
		return hdr.getFrom().getEmail();
		// return "" + hdr.getFrom().getDisplayName() + "<" + hdr.getFrom().getEmail() + ">";
	}	// getFrom

	/**
	* Get the subject for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the subject for the email.
	*/
	public String getSubject(Event event)
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
		catch (Exception ignore) {}

		// use the message's subject
		return "[ " + title + " - " + rb.getString("ann") + " ]   " + hdr.getSubject();

	}	// getSubject

	/**
	* Get the message for the email - the HTML text of the announcement.
	* @param event The event that matched criteria to cause the notification.
	* @return the message for the email.
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
		catch (Exception ignore) {}

		// use the message's body as HTML
		buf.append(msg.getBody() + newline);

		// add any attachments
		List attachments = hdr.getAttachments();
		if (attachments.size() > 0)
		{
			buf.append(newline + rb.getString("att") + newline);
			for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext(); )
			{
				Reference attachment = (Reference) iAttachments.next();
				String attachmentTitle = attachment.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
				buf.append("<a href=\"" + attachment.getUrl() + "\">" + attachmentTitle + "</a>" + newline);
			}
		}

		return buf.toString();

	}	// getMessage

	/** Returns "Content-Type: text/html" - Announcements are sent as HTML formatted email messages */
	public List getAdditionalHeaders(Event e)
	{
	    // Set the content type of the message body to HTML
	    List ret = new ArrayList(1);
	    ret.add("Content-Type: text/html");
	    return ret;
	}
	
	/** Returns true - Announcements are sent as HTML formatted email messages */
	public boolean isBodyHTML(Event e)
	{
	    return true;
	}
	
}   // SiteEmailNotificationAnnc



