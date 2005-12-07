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
import java.util.List;
import java.util.ResourceBundle;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationAction;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.notification.SiteEmailNotification;

/**
 * <p>
 * SiteEmailNotificationContent fills the notification message and headers with details from the content change that triggered the notification event.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class SiteEmailNotificationContent extends SiteEmailNotification
{
	private static ResourceBundle rb = ResourceBundle.getBundle("siteemacon");

	/**
	 * Construct.
	 */
	public SiteEmailNotificationContent()
	{
	}

	/**
	 * Construct.
	 */
	public SiteEmailNotificationContent(String siteId)
	{
		super(siteId);
	}

	/**
	 * @inheritDoc
	 */
	protected String getResourceAbility()
	{
		return ContentHostingService.EVENT_RESOURCE_READ;
	}

	/**
	 * @inheritDoc
	 */
	public NotificationAction getClone()
	{
		SiteEmailNotificationContent clone = new SiteEmailNotificationContent();
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
		String resourceName = props.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);

		// get the resource copyright alert property
		boolean copyrightAlert = props.getProperty(ResourceProperties.PROP_COPYRIGHT_ALERT) != null ? true : false;

		// Now build up the message text.
		if (ContentHostingService.EVENT_RESOURCE_ADD.equals(function))
		{
			buf.append(rb.getString("anewres"));
		}
		else
		{
			buf.append(rb.getString("anewres2"));
		}
		buf.append(" ");
		buf.append(rb.getString("the"));
		buf.append(" \"");
		buf.append(title);
		buf.append("\" ");
		buf.append(rb.getString("sitat"));
		buf.append(" ");
		buf.append(ServerConfigurationService.getString("ui.service", "Sakai"));
		buf.append(" (");
		buf.append(ServerConfigurationService.getPortalUrl());
		buf.append(")\n");

		// add location
		String path = constructPath(ref.getReference());
		buf.append("\n" + rb.getString("locsit") + " \"" + title + "\" > " + rb.getString("reso") + " " + path + " > " + resourceName);
		if (copyrightAlert)
		{
			buf.append(" (c)");
		}
		buf.append("\n");

		// resource description
		String description = props.getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION);
		if ((description != null) && (description.length() > 0))
		{
			buf.append("\n" + rb.getString("descrip") + " " + description + "\n");
		}

		// add a reference to the resource
		buf.append("\n" + rb.getString("resour") + " " + resourceName);
		if (copyrightAlert)
		{
			buf.append(" (c)");
		}
		buf.append(" " + url);
		buf.append("\n");

		return buf.toString();
	}

	/**
	 * @inheritDoc
	 */
	protected List getHeaders(Event e)
	{
		List rv = new ArrayList(1);

		// the Subject
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
				+ ServerConfigurationService.getPortalUrl() + ") " + rb.getString("forthe") + " " + title
				+ " " + rb.getString("site") + newline + rb.getString("youcan") + newline;
		return rv;
	}

	/**
	 * Form a "Bread Crumb" style path showing the folders in which this referenced resource lives.
	 * 
	 * @param ref
	 *        The reference string to the resource.
	 * @return The path string for this resource.
	 */
	protected String constructPath(String ref)
	{
		StringBuffer buf = new StringBuffer();

		// expect the ref to be /content/group/site/folder/folder2/folderEtc/file.ext
		String[] parts = StringUtil.split(ref, Entity.SEPARATOR);

		// 0 is null, 1 is "content", 2 is "group" or whatever, 3 is the site, the last is the file name
		if (parts.length > 4)
		{
			// grow this collection id as we descend into the collections
			String root = Entity.SEPARATOR + parts[2] + Entity.SEPARATOR + parts[3] + Entity.SEPARATOR;

			// take all the collection parts
			for (int i = 4; i < parts.length - 1; i++)
			{
				buf.append(" > ");
				String collectionId = parts[i];
				root = root + collectionId + Entity.SEPARATOR;
				try
				{
					// get the display name
					ContentCollection collection = ContentHostingService.getCollection(root);
					buf.append(collection.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME));
				}
				catch (Exception any)
				{
					// use the id if there's a problem
					buf.append(collectionId);
				}
			}
		}

		return buf.toString();
	}

	/**
	 * Get the subject for the email.
	 * 
	 * @param event
	 *        The event that matched criteria to cause the notification.
	 * @return the subject for the email.
	 */
	protected String getSubject(Event event)
	{
		Reference ref = EntityManager.newReference(event.getResource());
		Entity r = ref.getEntity();
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

		// use the message's subject
		String resourceName = props.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
		return "[ " + title + " - "
				+ (ContentHostingService.EVENT_RESOURCE_ADD.equals(function) ? rb.getString("new") : rb.getString("chan"))
				+ " " + rb.getString("reso2") + " ] " + resourceName;
	}
}
