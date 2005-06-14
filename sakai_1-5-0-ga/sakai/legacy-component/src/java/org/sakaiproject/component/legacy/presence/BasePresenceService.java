/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/BasePresenceService.java,v 1.25 2004/11/25 02:26:08 janderse.umich.edu Exp $
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
package org.sakaiproject.component.legacy.presence;

// imports
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.presence.PresenceService;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.util.StringUtil;

/**
* <p>Implements the PresenceService, all but a Storage model.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.25 $
*/
public abstract class BasePresenceService implements PresenceService
{
	/** SessionState key. */
	protected final String M_key = this.getClass().getName();

	/** Storage. */
	protected Storage m_storage = null;

	/**
	* Allocate a new storage object.
	* @return A new storage object.
	*/
	protected abstract Storage newStorage();

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: UsageSessionService */
	protected UsageSessionService m_usageSessionService = null;

	/**
	 * Dependency: UsageSessionService.
	 * @param service The UsageSessionService.
	 */
	public void setUsageSessionService(UsageSessionService service)
	{
		m_usageSessionService = service;
	}

	/** Dependency: UserDirectoryService */
	protected UserDirectoryService m_userDirectoryService = null;

	/**
	 * Dependency: UserDirectoryService.
	 * @param service The UserDirectoryService.
	 */
	public void setUserDirectoryService(UserDirectoryService service)
	{
		m_userDirectoryService = service;
	}

	/** Dependency: EventTrackingService */
	protected EventTrackingService m_eventTrackingService = null;

	/**
	 * Dependency: EventTrackingService.
	 * @param service The EventTrackingService.
	 */
	public void setEventTrackingService(EventTrackingService service)
	{
		m_eventTrackingService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			// storage
			m_storage = newStorage();

			m_logger.info(this+ ".init()");

		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/**
	 * Returns to uninitialized state.
	 */
	public void destroy()
	{
		m_storage = null;
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* PresenceService implementation
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String presenceReference(String id)
	{
		return REFERENCE_ROOT + Resource.SEPARATOR + id;

	} // presenceReference

	/**
	 * {@inheritDoc}
	 */
	protected String presenceId(String ref)
	{
		String start = presenceReference("");
		int i = ref.indexOf(start);
		if (i == -1)
			return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // presenceId

	/**
	 * {@inheritDoc}
	 */
	public void setPresence(String locationId)
	{
		if (locationId == null)
			return;
		
		if (!checkPresence(locationId, true))
		{
			UsageSession curSession = m_usageSessionService.getSession();

			// update the storage
			m_storage.setPresence(curSession.getId(), locationId);

			// generate the event
			Event event = m_eventTrackingService.newEvent(EVENT_PRESENCE, presenceReference(locationId), true);
			m_eventTrackingService.post(event, curSession);

			// stash in state for auto-cleanup
			SessionState state = m_usageSessionService.getSessionState(M_key);
			Presence p = new Presence(curSession, locationId);
			state.setAttribute(locationId, p);
		}

		// retire any expired presence
		checkPresenceForExpiration();

	} // setPresence

	/**
	 * {@inheritDoc}
	 */
	public void removePresence(String locationId)
	{
		if (locationId == null)
			return;

		if (checkPresence(locationId, false))
		{
			UsageSession curSession = m_usageSessionService.getSession();

			// tell maintenance
			m_storage.removePresence(curSession.getId(), locationId);

			// generate the event
			Event event = m_eventTrackingService.newEvent(EVENT_ABSENCE, presenceReference(locationId), true);
			m_eventTrackingService.post(event, curSession);

			// remove from state
			SessionState state = m_usageSessionService.getSessionState(M_key);
			Presence p = (Presence) state.getAttribute(locationId);
			if (p != null)
			{
				p.deactivate();
				state.removeAttribute(locationId);
			}
		}

	} // removePresence

	/**
	 * {@inheritDoc}
	 */
	public List getPresence(String locationId)
	{
		// get the sessions at this location
		List sessions = m_storage.getSessions(locationId);

		// sort
		Collections.sort(sessions);

		return sessions;

	} // getPresence

	/**
	 * {@inheritDoc}
	 */
	public List getPresentUsers(String locationId)
	{
		// get the sessions
		List sessions = m_storage.getSessions(locationId);

		// form a list of user ids
		List userIds = new Vector();
		for (Iterator i = sessions.iterator(); i.hasNext();)
		{
			UsageSession s = (UsageSession) i.next();

			if (!userIds.contains(s.getUserId()))
			{
				userIds.add(s.getUserId());
			}
		}

		// get the users for these ids
		List users = m_userDirectoryService.getUsers(userIds);
			
		// sort
		Collections.sort(users);

		return users;
	}

	/**
	 * {@inheritDoc}
	 */
	public List getLocations()
	{
		List locations = m_storage.getLocations();

		Collections.sort(locations);

		return locations;

	} // getLocations

	/**
	 * {@inheritDoc}
	 */
	public String locationId(String site, String page, String tool)
	{
		return locElement(site) + "/" + locElement(page) + "/" + locElement(tool);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] parseLocation(String location)
	{
		String[] parsed = StringUtil.split(location, "/");
		String[] rv = new String[3];
		rv[0] = "*".equals(parsed[0]) ? null : parsed[0];
		rv[1] = "*".equals(parsed[1]) ? null : parsed[1];
		rv[2] = "*".equals(parsed[2]) ? null : parsed[2];

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLocationDescription(String location)
	{
		String rv = "";
		String[] loc = parseLocation(location);

		// is it a site?
		Site site = null;
		try
		{
			site = SiteService.getSite(loc[0]);
		}
		catch (IdUnusedException ignore)
		{
		}

		// if we found the site
		if (site != null)
		{
			rv = site.getTitle() + " (" + site.getId() + ")";

			// for page locations, add the page description
			if (loc[1] != null)
			{
				SitePage p = site.getPage(loc[1]);
				if (p != null)
				{
					rv += " - " + p.getTitle() /* + " (" + p.getId() + ")" */;
				}
				
			}
			// for tool locations, get a tool description, too
			if (loc[2] != null)
			{
				ToolConfiguration t = site.getTool(loc[2]);
				if (t != null)
				{
					rv += " - " + t.getTitle() /* + " (" + t.getId() + ")" */;
				}
			}
		}

		return rv;
	}

	/**
	 * Check if the current session is present at the location - optionally refreshing it
	 * @param locationId The location to check.
	 * @param refresh If true, refresh the timeout on the presence if found
	 * @return True if the current session is present at that location, false if not.
	 */
	protected boolean checkPresence(String locationId, boolean refresh)
	{
		SessionState state = m_usageSessionService.getSessionState(M_key);
		Presence p = (Presence) state.getAttribute(locationId);
		
		if ((p != null) && refresh)
		{
			p.setActive();
		}

		return (p != null);
	}

	/**
	 * Check current session presences and remove any expired ones
	 */
	protected void checkPresenceForExpiration()
	{
		SessionState state = m_usageSessionService.getSessionState(M_key);
		List locations = state.getAttributeNames();
		for (Iterator i = locations.iterator(); i.hasNext();)
		{
			String location = (String) i.next();

			Presence p = (Presence) state.getAttribute(location);
			if (p.isExpired())
			{
				state.removeAttribute(location);
			}
		}
	}

	/**
	 * Form a part of a location string - use a "*" if it's not present
	 * @param el the part.
	 * @return the part, or "*" if it's null.
	 */
	protected String locElement(String el)
	{
		if (el != null) return el;
		return "*";
	}

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		 * Add this session id's presence at this location, if not already there.
		 * @param sessionId The session id.
		 * @param locationId The location id.
		 */
		void setPresence(String sessionId, String locationId);

		/**
		 * Remove this sessions id's presence at this location.
		 * @param sessionId The session id.
		 * @param locationId The location id.
		 */
		void removePresence(String sessionId, String locationId);

		/**
		 * Access the List of UsageSessions present at this location.
		 * @param locationId The location id.
		 * @return The List of sessions (UsageSession) present at this location.
		 */
		List getSessions(String locationId);

		/**
		 * Access the List of all known location ids.
		 * @return The List (String) of all known locations.
		 */
		List getLocations();
	}

	/*******************************************************************************
	* Presence
	*******************************************************************************/

	protected class Presence implements SessionStateBindingListener
	{
		/** The session. */
		protected UsageSession m_session = null;

		/** The location id. */
		protected String m_locationId = null;

		/** If true, process the unbound. */
		protected boolean m_active = true;

		/** Time to expire. */
		protected long m_expireTime = 0;

		public Presence(UsageSession session, String locationId)
		{
			m_session = session;
			m_locationId = locationId;
			m_expireTime = System.currentTimeMillis() + (m_usageSessionService.getSessionLostTimeout() * 1000);
		}

		public void deactivate()
		{
			m_active = false;
		}

		/**
		 * Reset the timeout based on current activity
		 */
		public void setActive()
		{
			m_expireTime = System.currentTimeMillis() + (m_usageSessionService.getSessionLostTimeout() * 1000);
		}

		/**
		 * Has this presence timed out?
		 * @return true if expired, false if not.
		 */
		public boolean isExpired()
		{
			return System.currentTimeMillis() > m_expireTime;
		}

		/**
		 * {@inheritDoc}
		 */
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_active)
			{
				m_storage.removePresence(m_session.getId(), m_locationId);

				// generate the event
				Event event = m_eventTrackingService.newEvent(EVENT_ABSENCE, presenceReference(m_locationId), true);
				m_eventTrackingService.post(event, m_session);
			}
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/BasePresenceService.java,v 1.25 2004/11/25 02:26:08 janderse.umich.edu Exp $
*
**********************************************************************************/
