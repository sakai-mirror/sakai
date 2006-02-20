/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation,
 *                  University of Cambridge
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

package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.notification.SiteEmailNotification;

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;

/**
 * <p>
 * SiteEmailNotificationRWiki fills the notification message and headers with details from the content change that triggered the notification event.
 * </p>
 * 
 * @author Sakai Software Development Team
 * @author ieb
 */
public class SiteEmailNotificationRWiki extends SiteEmailNotification
{
	private Logger logger = org.sakaiproject.service.framework.log.cover.Logger.getInstance();

	/**
	 * Construct.
	 */
	public SiteEmailNotificationRWiki()
	{
	}

	/**
	 * Construct.
	 */
	public SiteEmailNotificationRWiki(String siteId)
	{
		super(siteId);
	}
	/**
	 * @inheritDoc
	 */
	protected List getRecipients(Event event)
	{
		// get the resource reference
		Reference ref = EntityManager.newReference(event.getResource());

		// use either the configured site, or if not configured, the site (context) of the resource
		String siteId = (getSite() != null) ? getSite() : ref.getContext();
		String sitestart = Entity.SEPARATOR+"site"+Entity.SEPARATOR;
		if ( siteId.startsWith(sitestart) ) {
			siteId = siteId.substring(sitestart.length());
			int nextslash = siteId.indexOf(Entity.SEPARATOR);
			if ( nextslash != -1 ) {
				siteId = siteId.substring(0,nextslash);
			}
		}

		logger.info("Finding recipients for "+siteId);
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
			logger.info("Got "+users.size()+" for site "+siteId);

			// get the list of users who have the appropriate access to the resource
			if (getResourceAbility() != null)
			{
				List users2 = SecurityService.unlockUsers(getResourceAbility(), ref.getReference());

				// find intersection of users and user2
				users.retainAll(users2);
			}
			
			logger.info("Got "+users.size()+" for site "+siteId+" after filter");

			return users;
		}
		catch (Exception any)
		{
			logger.info(" Failed to find list of recipients ",any);
			return new Vector();
		}
	}

	/**
	 * @inheritDoc
	 */
	protected String getResourceAbility()
	{
		return null;
//		return RWikiSecurityService.SECURE_READ;
	}

	/**
	 * @inheritDoc
	 */
	public NotificationAction getClone()
	{
		SiteEmailNotificationRWiki clone = new SiteEmailNotificationRWiki();
		clone.set(this);

		return clone;
	}

	/**
	 * @inheritDoc
	 */
	protected String getMessage(Event event)
	{
		// get the content & properties
		Reference ref = EntityManager.newReference(event.getResource());
		ResourceProperties props = ref.getProperties();
		

		// get the function
		String function = event.getEvent();

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

		// get the URL and resource name.
		StringBuffer buf = new StringBuffer();
		String url = ref.getUrl();
		
		
		String pageName = props.getProperty(RWikiEntity.RP_NAME);
		String realm = props.getProperty(RWikiEntity.RP_REALM);
		String localName = NameHelper.localizeName(pageName,realm);
		String user = props.getProperty(RWikiEntity.RP_USER);
		String moddate = props.getProperty(RWikiEntity.RP_VERSION);
		
		String messageFormat = 
			" The wiki page {0} in site {1} has been modified on {2} by {3} \n{4}\n";
		
		return MessageFormat.format(messageFormat, new Object[] {localName,title,moddate,user,url});
	}

	/**
	 * @inheritDoc
	 */
	protected List getHeaders(Event e)
	{
		List rv = new ArrayList(1);
		Reference ref = EntityManager.newReference(e.getResource());
		ResourceProperties props = ref.getProperties();

		String pageName = props.getProperty(RWikiEntity.RP_NAME);
		String realm = props.getProperty(RWikiEntity.RP_REALM);
		String localName = NameHelper.localizeName(pageName,realm);
		
		String messageFormat = 
			" The wiki page {0} has been modified";
		// the Subject
		rv.add("Subject: " + MessageFormat.format(messageFormat, new Object[] {localName}));

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	protected String getTag(String newline, String title)
	{
		// tag the message
		String rv = "\n------ this "
		+ ServerConfigurationService.getString("ui.service", "Sakai") + " ("
		+ ServerConfigurationService.getPortalUrl() + ") for the " + title
		+ " site \n You can ";
/*
 		String rv = newline + "------" + newline + rb.getString("this") + " "
				+ ServerConfigurationService.getString("ui.service", "Sakai") + " ("
				+ ServerConfigurationService.getPortalUrl() + ") " + rb.getString("forthe") + " " + title
				+ " " + rb.getString("site") + newline + rb.getString("youcan") + newline;
*/
		return rv;
	}


}
