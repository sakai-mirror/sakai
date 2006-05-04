/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/notification/EmailNotification.java,v 1.5 2005/06/06 18:46:45 ggolden.umich.edu Exp $
*
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.ResourceBundle;

import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.email.cover.EmailService;
import org.sakaiproject.service.legacy.digest.DigestMessage;
import org.sakaiproject.service.legacy.digest.cover.DigestService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.Notification;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.util.java.StringUtil;
import org.w3c.dom.Element;

/**
* <p>EmailNotification is the notification helper that handles the act of message (email) based notify,
* site related, with user preferences.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class EmailNotification
	implements NotificationAction
{
	private ResourceBundle rb = ResourceBundle.getBundle("emailnotification");
	/** The related site id. */
	protected String m_siteId = null;

	
	/**
	* Construct.
	*/
	public EmailNotification()
	{
	}	// EmailNotification

	/**
	* Construct.
	* @param siteId The related site id.
	*/
	public EmailNotification(String siteId)
	{
		m_siteId = siteId;

	}	// EmailNotification

	/**
	* Set from an xml element.
	* @param el The xml element.
	*/
	public void set(Element el)
	{
		m_siteId = StringUtil.trimToNull(el.getAttribute("site"));

	}	// set

	/**
	* Set from another.
	* @param other The other to copy.
	*/
	public void set(NotificationAction other)
	{
		EmailNotification eOther = (EmailNotification) other;
		m_siteId = eOther.m_siteId;

	}	// set

	/**
	* Make a new one like me.
	* @return A new action just like me.
	*/
	public NotificationAction getClone()
	{
		EmailNotification clone = new EmailNotification();
		clone.set(this);

		return clone;

	}	// getClone

	/**
	* Fill this xml element with the attributes.
	* @param el The xml element.
	*/
	public void toXml(Element el)
	{
		if (m_siteId != null) el.setAttribute("site", m_siteId);

	}	// toXml

	/**
	* Do the notification.
	* @param notification The notification responding to the event.
	* @param event The event that matched criteria to cause the notification.
	*/
	public void notify(Notification notification, Event event)
	{
		// ignore events marked for no notification
		if 	(event.getPriority() == NotificationService.NOTI_NONE) return;

		// get the list of potential recipients
		List recipients = getRecipients(event);

		// filter to actual immediate recipients
		List immediate = immediateRecipients(recipients, notification, event);

		// and the list of digest recipients
		List digest = digestRecipients(recipients, notification, event);

		// we may be done
		if ((immediate.size() == 0) && (digest.size() == 0)) return;

		// get the email elements
		String message = getMessage(event);
		String from = getFrom(event);
		String headerTo = getTo(event);
		String replyTo = getReplyTo(event);
		String subject = getSubject(event);
		List additionalHeaders = getAdditionalHeaders(event);
		boolean isBodyHTML = isBodyHTML(event);
		String newline = (isBodyHTML) ? "<br />\n" : "\n";

		// for the immediates
		if (immediate.size() > 0)
		{
			// get a site title
			// use either the configured site, or if not configured, the site (context) of the resource
			Reference ref = new Reference(event.getResource());
			Resource r = ref.getResource();
			String title = (getSite() != null) ? getSite() : ref.getContext();
			org.sakaiproject.service.legacy.site.SiteService siteService = org.sakaiproject.service.legacy.site.cover.SiteService.getInstance();
			try
			{
				org.sakaiproject.service.legacy.site.Site site = siteService.getSite(title);
				title = site.getTitle();
			}
			catch (Exception ignore) {}

			// tag the message
			String messageForImmediates = message
						+ newline + rb.getString("separator") + newline 
						+ rb.getString("this")
						+ " "
						+ ServerConfigurationService.getString("ui.service","Sakai")
						+ " "
						+ rb.getString("par1")
						+ ServerConfigurationService.getServerUrl()
						+ rb.getString("par2") 
						+ " "
						+ rb.getString("forthe") + " "
						+ title						+ " " + rb.getString("site") + newline
						+ rb.getString("youcan") + newline;

			// send to each immediate - one at a time
			// NOTE: sending to them all at once caused problems - some SMTP servers have a to: limit which we exceeded,
			//       and if there's a bad email address in there it could cancel the entire mailing. -ggolden
			if (from != null && from.length() > 0)
			{
				for (Iterator ii = immediate.iterator(); ii.hasNext();)
				{
					User user = (User) ii.next();
					String email = user.getEmail();
					if ((email != null) && (email.length() > 0))
					{
						// email = user.getDisplayName() + "<" + user.getEmail() + ">";
					    EmailService.send(from, email, subject, messageForImmediates, headerTo, replyTo, additionalHeaders);
					}
				}
			}
		}

		// for the digesters
		if (digest.size() > 0)
		{
			// modify the message to add missing parts (no tag - this is added at the end of the digest)
			String messageForDigest = "From: " + from + "\n"
					+ "Date: " + TimeService.newTime().toStringLocalFullZ() + "\n"
					+ "To: " + headerTo + "\n"
					+ "Subject: " + subject + "\n"
					+ "\n" + message;

			for (Iterator iDigests = digest.iterator(); iDigests.hasNext();)
			{
				User user = (User) iDigests.next();

				// digest the message
				DigestMessage msg = new DigestMessage(user.getId(), subject, messageForDigest);
				DigestService.digest(msg);
			}
		}

	}	// notify

	/**
	* Get the from address for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the from address for the email.
	*/
	protected String getFrom(Event event)
	{
		return null;

	}	// getFrom

	/**
	* Get the subject for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the subject for the email.
	*/
	public String getSubject(Event event)
	{
		return "";

	}	// getSubject

	/**
	* Get the message for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the message for the email.
	*/
	protected String getMessage(Event event)
	{
		return "";

	}	// getMessage
	
	/**
	* Get additional headers for the email (List of String)
	* @param event The event that matched criteria to cause the notification.
	* @return the additional headers for the email.
	*/
	
	protected List getAdditionalHeaders(Event event)
	{
		return null;
	}
	
	/**
	 * Return true if the body of the email message should be sent as HTML.
	 * If this returns true, getAdditionalHeaders() should also return
	 * a "Content-Type: text/html" header of some kind.
	 * @param event The event that matched criteria to cause the notification.
	 * @return whether the body of the email message should be sent as HTML.
	 */
	protected boolean isBodyHTML(Event event)
	{
	    return false;
	}

	/**
	* Get the to: header string for the email - the real recipients will be getTo(), but this will show up
	* in the message.
	* @param event The event that matched criteria to cause the notification.
	* @return the to: header for the email.
	*/
	protected String getTo(Event event)
	{
		return null;

	}	// getTo

	/**
	* Get the reply-to: header string for the email.
	* @param event The event that matched criteria to cause the notification.
	* @return the reply-to: header string for the email.
	*/
	protected String getReplyTo(Event event)
	{
		return null;

	}	// getReplyTo

	/**
	* Get the list of User objects who are eligible to receive the notification email.
	* @param event The event that matched criteria to cause the notification.
	* @return the list of User objects who are eligible to receive the notification email.
	*/
	protected List getRecipients(Event event)
	{
		return new Vector();

	}	// getRecipients

	/**
	* Get the site id this notification is related to.
	* @return The site id this notification is related to.
	*/
	protected String getSite()
	{
		return m_siteId;

	}	// getSite

	/**
	* Filter the recipients Users into the list of those who get this one immediately.
	* Combine the event's notification priority with the user's notification profile.
	* @param recipients The List (User) of potential recipients.
	* @param notification The notification responding to the event.
	* @param event The event that matched criteria to cause the notification.
	* @return The List (User) of immediate recipients.
	*/
	protected List immediateRecipients(List recipients, Notification notification, Event event)
	{
		int priority = event.getPriority();

		// required notification is sent to all
		if (priority == NotificationService.NOTI_REQUIRED)
		{
			return recipients;
		}

		List rv = new Vector();
		for (Iterator iUsers = recipients.iterator(); iUsers.hasNext();)
		{
			User user = (User) iUsers.next();

			// get the user's priority preference for this event
			int option = getOption(user, notification, event);

			// if immediate is the option, or there is no option, select this user
			// Note: required and none priority are already handled, so we know it's optional here.
			if (isImmediateDeliveryOption(option, notification))
			{
				rv.add(user);
			}
		}

		return rv;

	}	// immediateRecipients

	/**
	* Filter the preference option based on the notification resource type
	* @param option The preference option.
	* @param notification The notification responding to the event.
	* @return A boolean value which tells if the User is one of immediate recipients.
	*/
	protected boolean isImmediateDeliveryOption(int option, Notification notification)
	{
		if (option == NotificationService.PREF_IMMEDIATE)
		{
			return true;
		}
		else
			if (option == NotificationService.PREF_NONE)
			{
				String type = new Reference(notification.getResourceFilter()).getType();
				if (type != null)
				{
					if (type.equals(MailArchiveService.SERVICE_NAME))
					{
						return true;
					}
				}
			}
		return false;
	} // isImmediateDeliveryOption

	/**
	* Filter the recipients Users into the list of those who get this one by digest.
	* Combine the event's notification priority with the user's notification profile.
	* @param recipients The List (User) of potential recipients.
	* @param notification The notification responding to the event.
	* @param event The event that matched criteria to cause the notification.
	* @return The List (User) of digest recipients.
	*/
	protected List digestRecipients(List recipients, Notification notification, Event event)
	{
		List rv = new Vector();

		int priority = event.getPriority();

		// priority notification is sent to all (i.e. no digests)
		if (priority == NotificationService.NOTI_REQUIRED)
		{
			return rv;
		}

		for (Iterator iUsers = recipients.iterator(); iUsers.hasNext();)
		{
			User user = (User) iUsers.next();

			// get the user's priority preference for this event
			int option = getOption(user, notification, event);

			// if digest is the option, select this user
			if (option == NotificationService.PREF_DIGEST)
			{
				rv.add(user);
			}
		}

		return rv;

	}	// digestRecipients

	/**
	* Get the user's notification option for this... one of the NotificationService's PREF_ settings
	*/
	protected int getOption(User user, Notification notification, Event event)
	{
		String priStr = Integer.toString(event.getPriority());


		Preferences prefs = PreferencesService.getPreferences(user.getId());

		// get the user's preference for this notification
		ResourceProperties props = prefs.getProperties(NotificationService.PREFS_NOTI + notification.getId());
		try
		{
			int option = (int) props.getLongProperty(priStr);
			if (option != NotificationService.PREF_NONE) return option;
		}
		catch (Throwable ignore) {}

		// try the preference for the site from which resources are being watched for this notification
		// Note: the getSite() is who is notified, not what we are watching; that's based on the notification filter -ggolden
		String siteId = new Reference(notification.getResourceFilter()).getContext();
		if (siteId != null)
		{
			props = prefs.getProperties(NotificationService.PREFS_SITE + siteId);
			try
			{
				int option = (int) props.getLongProperty(priStr);
				if (option != NotificationService.PREF_NONE) return option;
			}
			catch (Throwable ignore) {}
		}

		// try the default
		props = prefs.getProperties(NotificationService.PREFS_DEFAULT);
		try
		{
			int option = (int) props.getLongProperty(priStr);
			if (option != NotificationService.PREF_NONE) return option;
		}
		catch (Throwable ignore) {}
		
		//try the preference for the resource type service responsibile for resources of this notification
		String type = new Reference(notification.getResourceFilter()).getType();
		if (type != null)
		{
			props = prefs.getProperties(NotificationService.PREFS_TYPE + type);
			try
			{
				int option = (int) props.getLongProperty(Integer.toString(NotificationService.NOTI_OPTIONAL));
				if (option != NotificationService.PREF_NONE) return option;
			}
			catch (EmptyException e)
			{
				return NotificationService.PREF_IMMEDIATE;
			}
			catch (Throwable ignore) {}
		}
		
		// nothing defined...
		return NotificationService.PREF_NONE;

	}	// getOption

}   // EmailNotification

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/notification/EmailNotification.java,v 1.5 2005/06/06 18:46:45 ggolden.umich.edu Exp $
*
**********************************************************************************/
