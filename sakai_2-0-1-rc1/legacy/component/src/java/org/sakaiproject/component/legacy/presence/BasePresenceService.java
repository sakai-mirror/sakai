/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/presence/BasePresenceService.java,v 1.6 2005/05/12 18:52:43 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.presence;

// imports
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.presence.PresenceService;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.user.UserDirectoryService;

/**
* <p>Implements the PresenceService, all but a Storage model.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public abstract class BasePresenceService implements PresenceService
{
	/** SessionState key. */
	protected final static String SESSION_KEY = "sakai.presence.service";

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

	/** Configuration: milliseconds till a non-refreshed presence entry times out. */
	protected int m_timeout = 60000;

	/**
	 * Configuration: SECONDS till a non-refreshed presence entry times out.
	 * @param value timeout seconds.
	 */
	public void setTimeoutSeconds(String value)
	{
		try
		{
			m_timeout = Integer.parseInt(value) * 1000;
		}
		catch (Exception ignore)
		{
		}
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
			// presence relates a usage session (the current one) with a location
			UsageSession curSession = m_usageSessionService.getSession();

			// update the storage
			m_storage.setPresence(curSession.getId(), locationId);

			// generate the event
			Event event = m_eventTrackingService.newEvent(EVENT_PRESENCE, presenceReference(locationId), true);
			m_eventTrackingService.post(event, curSession);

			// create a presence for tracking

			// bind a presence tracking object to the sakai session for auto-cleanup when logout or inactivity invalidates the sakai session
			Session session = SessionManager.getCurrentSession();
			ToolSession ts = session.getToolSession(SESSION_KEY);
			Presence p = new Presence(curSession, locationId);
			ts.setAttribute(locationId, p);
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
			Session session = SessionManager.getCurrentSession();
			ToolSession ts = session.getToolSession(SESSION_KEY);
			Presence p = (Presence) ts.getAttribute(locationId);
			if (p != null)
			{
				p.deactivate();
				ts.removeAttribute(locationId);
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
		// TODO: remove
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLocationDescription(String location)
	{
		// TODO: get a description for a placement!
		return "location: " + location;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTimeout()
	{
		return m_timeout / 1000;
	}

	/**
	 * Check if the current session is present at the location - optionally refreshing it
	 * @param locationId The location to check.
	 * @param refresh If true, refresh the timeout on the presence if found
	 * @return True if the current session is present at that location, false if not.
	 */
	protected boolean checkPresence(String locationId, boolean refresh)
	{
		Session session = SessionManager.getCurrentSession();
		ToolSession ts = session.getToolSession(SESSION_KEY);
		Presence p = (Presence) ts.getAttribute(locationId);
		
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
		Session session = SessionManager.getCurrentSession();
		ToolSession ts = session.getToolSession(SESSION_KEY);
		Enumeration locations = ts.getAttributeNames();
		while (locations.hasMoreElements())
		{
			String location = (String) locations.nextElement();

			Presence p = (Presence) ts.getAttribute(location);
			if (p.isExpired())
			{
				ts.removeAttribute(location);
			}
		}
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

	protected class Presence implements SessionBindingListener
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
			m_expireTime = System.currentTimeMillis() + m_timeout;
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
			m_expireTime = System.currentTimeMillis() + m_timeout;
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
		public void valueBound(SessionBindingEvent event)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		public void valueUnbound(SessionBindingEvent evt)
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

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/presence/BasePresenceService.java,v 1.6 2005/05/12 18:52:43 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
