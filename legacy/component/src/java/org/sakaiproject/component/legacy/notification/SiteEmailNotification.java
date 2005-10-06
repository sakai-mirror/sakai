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
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.cover.AliasService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
* <p>SiteEmailNotification sends email to the portal access permitted users of a site.
* The mail header to: field will show the site's best email address, as will the reply-to header.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class SiteEmailNotification
	extends EmailNotification
{
	/**
	* Construct.
	*/
	public SiteEmailNotification() {}

	/**
	* Construct.
	* @param siteId The id of the site whose users will get a mailing.
	*/
	public SiteEmailNotification(String siteId)
	{
		super(siteId);

	}	// SiteEmailNotification

	/**
	 * Get the additional permission function ability string needed for the resource that is the target of the
	 * notification - users who get notified need to have this ability with this resource, too.
	 * @return The additional ability string needed for a user to receive notification.
	 */
	protected String getResourceAbility()
	{
		return null;
	}

	/**
	* Make a new one like me.
	* @return A new action just like me.
	*/
	public NotificationAction getClone()
	{
		SiteEmailNotification clone = new SiteEmailNotification();
		clone.set(this);

		return clone;

	}	// getClone

	/**
	* Get the list of User objects who are eligible to receive the notification email.
	* @param event The event that matched criteria to cause the notification.
	* @return the list of User objects who are eligible to receive the notification email.
	*/
	protected List getRecipients(Event event)
	{
		// get the resource reference
		Reference ref = EntityManager.newReference(event.getResource());

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// if the site is published, use the list of users who can SITE_VISIT the site,
		// else use the list of users who can SITE_VISIT_UNP the site.
		try
		{
			Site site = SiteService.getSite(siteId);
			String ability = SiteService.SITE_VISIT;
			if (!site.isPublished())
			{
				ability = SiteService.SITE_VISIT_UNPUBLISHED;
			}

			// get the list of users
			List users = SecurityService.unlockUsers(ability, SiteService.siteReference(siteId));

			// get the list of users who have the appropriate access to the resource
			if (getResourceAbility() != null)
			{
				List users2 = SecurityService.unlockUsers(getResourceAbility(), ref.getReference());

				// find intersection of users and user2
				users.retainAll(users2);
			}

			return users;
		}
		catch (Exception any)
		{
			return new Vector();
		}

	}	// getRecipients

	/**
	* Get the to: header string for the email - the real recipients will be getTo(), but this will show up
	* in the message.
	* @param event The event that matched criteria to cause the notification.
	* @return the to: header for the email.
	*/
	protected String getTo(Event event)
	{
		// get the resource reference
		Reference ref = EntityManager.newReference(event.getResource());

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();

		// make the to: field look like the email address of the site
		
		// select the site's main mail channel alias, or the site's alias, or the site id
		String siteMailId = siteId;

		// first check aliases for the site's main email channel		
		String channelRef = MailArchiveService.channelReference(siteId, SiteService.MAIN_CONTAINER);
		List aliases = AliasService.getAliases(channelRef, 1, 1);
		if (aliases.isEmpty())
		{
			// next try aliases for the site
			String siteRef = SiteService.siteReference(siteId);
			aliases = AliasService.getAliases(siteRef, 1, 1);
		}
		
		// if there was an alias
		if (!aliases.isEmpty())
		{
			siteMailId = ((Alias) aliases.get(0)).getId();
		}

		return siteMailId + " <" + siteMailId + "@" + ServerConfigurationService.getServerName() + ">";

	}	// getTo

}   // SiteEmailNotification



