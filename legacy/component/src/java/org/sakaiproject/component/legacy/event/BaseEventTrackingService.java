/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.component.legacy.event;

// imports
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>BaseEventTrackingService is the base implmentation for the EventTracking CHEF event tracking service.</p>
* <p>The event tracking service provides activity event tracking and monitoring.
* Objects act as event generators, posting events to the service.  Other objects act
* as event monitors, and are notified by the service when certain events occur.
* Events posted are also stored in event archives by the service.</p>
* <p>Each event is represented by a core Event object</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision:
*/
public abstract class BaseEventTrackingService
	implements EventTrackingService
{
	/** An observable object helper. */
	protected MyObservable m_observableHelper = new MyObservable();

	/** An observable object helper for see-it-first priority observers. */
	protected MyObservable m_priorityObservableHelper = new MyObservable();

	/** An observable object helper for see-only-local-events observers. */
	protected MyObservable m_localObservableHelper = new MyObservable();

	/*******************************************************************************
	* Observable implementation
	*******************************************************************************/

	/**
	* Extend Observable to "public"ize setChanges, so we can set it.
	* Why a helper object? Cause the service (which is observable) already 'extends' TurbineBaseService,
	* and cannot also 'extend' Observable.
	*/
	protected class MyObservable
		extends Observable
	{
		public void setChanged() { super.setChanged(); }

	}	// MyObservable

	/*******************************************************************************
	* Event post / flow - override
	*******************************************************************************/

	/**
	* Cause this new event to get to wherever it has to go for persistence, etc.
	* @param event The new event to post.
	*/
	protected abstract void postEvent(Event event);

	/*******************************************************************************
	* Observer notification
	*******************************************************************************/

	/**
	* Send notification about a new event to observers.
	* @param event The event to send notification about.
	* @param local True if the event originated on this server, false if it came from another server.
	*/
	protected void notifyObservers(Event event, boolean local)
	{
		// %%% inline like this, or on a new thread?

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + " Notification - Event: " + event);
	
		// first, notify all priority observers
		m_priorityObservableHelper.setChanged();
		m_priorityObservableHelper.notifyObservers(event);

		// notify the normal observers
		m_observableHelper.setChanged();
		m_observableHelper.notifyObservers(event);

		// if the event is local, notify local observers
		if (local)
		{
			m_localObservableHelper.setChanged();
			m_localObservableHelper.notifyObservers(event);
		}

	}	// notifyObservers
	
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service. */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this + ".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* EventTracking implementation
	*******************************************************************************/

	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify)
	{
		return new BaseEvent(event, resource, modify, NotificationService.NOTI_OPTIONAL);

	}	// newEvent

	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @param priority The Event's notification priority.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify, int priority)
	{
		return new BaseEvent(event, resource, modify, priority);

	}	// newEvent

	/**
	* Post an event
	* @param event The event object (created with newEvent()).
	* Note: the current session user will be used as the user responsible for the event.
	*/
	public void post(Event event)
	{
		// get the session id or user id
		String id = UsageSessionService.getSessionId();
		if (id != null)
		{
			((BaseEvent) event).setSessionId(id);
			postEvent(event);
		}

		// post for the session "thread" user
		else
		{
			id = SessionManager.getCurrentSessionUserId();
			if (id == null)
			{
				id = "?";
			}

			((BaseEvent) event).setUserId(id);
			postEvent(event);
		}

	}	// post

	/**
	* Post an event on behalf of a user's session
	* @param event The event object (created with newEvent()).
	* @param session The usage session object of the user session responsible for the event.
	*/
	public void post(Event event, UsageSession session)
	{
		String id = "?";
		if (session != null) id = session.getId();

		((BaseEvent) event).setSessionId(id);
		postEvent(event);

	}	// post

	/**
	* Post an event on behalf of a user.
	* @param event The event object (created with newEvent()).
	* @param user The User object of the user responsible for the event.
	*/
	public void post(Event event, User user)
	{
		String id = "?";
		if (user != null) id = user.getId();

		((BaseEvent) event).setUserId(id);
		postEvent(event);

	}	// post

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* @param observer The class observing.
	*/
	public void addObserver(Observer observer)
	{
		// keep this observer in one list only
		m_priorityObservableHelper.deleteObserver(observer);
		m_localObservableHelper.deleteObserver(observer);

		m_observableHelper.addObserver(observer);

	}	// addObserver

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Priority observers get notified first, before normal observers.
	* @param observer The class observing.
	*/
	public void addPriorityObserver(Observer observer)
	{
		// keep this observer in one list only
		m_observableHelper.deleteObserver(observer);
		m_localObservableHelper.deleteObserver(observer);

		m_priorityObservableHelper.addObserver(observer);

	}	// addPriorityObserver

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Local observers get notified only of event generated on this application server, not on those generated elsewhere.
	* @param observer The class observing.
	*/
	public void addLocalObserver(Observer observer)
	{
		// keep this observer in one list only
		m_observableHelper.deleteObserver(observer);
		m_priorityObservableHelper.deleteObserver(observer);

		m_localObservableHelper.addObserver(observer);

	}	// addLocalObserver

	/**
	* Delete an observer of events.
	* @param observer The class observing to delete.
	*/
	public void deleteObserver(Observer observer)
	{
		m_observableHelper.deleteObserver(observer);
		m_priorityObservableHelper.deleteObserver(observer);
		m_localObservableHelper.deleteObserver(observer);

	}	// deleteObserver

	/*******************************************************************************
	* Event implementation
	*******************************************************************************/

	/**
	* <p>BaseEvent is the implementation of the core Event interface.</p>
	* <p>Event objects are posted to the EventTracking service, and may be listened for.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
  * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
	* @version 1.0
	* @see org.chefproject.core.Event
	*/
	protected class BaseEvent
		implements Event, Serializable
	{
		/**
     * Be a good Serializable citizen
     */
    private static final long serialVersionUID = 3690761674282252600L;

    /** The Event's sequence number. */
		protected long m_seq = 0;

		/** The Event's id string. */
		protected String m_id = "";

		/** The Event's resource reference string. */
		protected String m_resource = "";

		/** The Event's session id string. May be null.*/
		protected String m_session = null;

		/** The Event's user id string. May be null.*/
		protected String m_user = null;

		/** The Event's modify flag (true if the event caused a resource modification). */
		protected boolean m_modify = false;

		/** The Event's notification priority. */
		protected int m_priority = NotificationService.NOTI_OPTIONAL;

		/** Event creation time. */
		protected Time m_time = null;
		/**
		* Access the event id string
		* @return The event id string.
		*/
		public String getEvent() { return m_id; }

		/**
		* Access the resource reference.
		* @return The resource reference string.
		*/
		public String getResource() { return m_resource; }

		/**
		* Access the UsageSession id.  If null, check for a User id.
		* @return The UsageSession id string.
		*/
		public String getSessionId() { return m_session; }
	
		/**
		* Access the User id.  If null, check for a session id.
		* @return The User id string.
		*/
		public String getUserId() { return m_user; }

		/**
		* Is this event one that caused a modify to the resource, or just an access.
		* @return true if the event caused a modify to the resource, false if it was just an access.
		*/
		public boolean getModify() { return m_modify; }

		/**
		* Access the event's notification priority.
		* @return The event's notification priority.
		*/
		public int getPriority() { return m_priority; }

		/**
		* Construct
		* @param event The Event id.
		* @param resource The resource id.
		* @param modify If the event caused a modify, true, if it was just an access, false.
		* @param priority The Event's notification priority.
		*/
		public BaseEvent(String event, String resource, boolean modify, int priority)
		{
			setEvent(event);
			setResource(resource);
			m_modify = modify;
			m_priority = priority;

		}	// BaseEvent

		/**
		* Construct
		* @param seq The event sequence number.
		* @param event The Event id.
		* @param resource The resource id.
		* @param modify If the event caused a modify, true, if it was just an access, false.
		* @param priority The Event's notification priority.
		*/
		public BaseEvent(long seq, String event, String resource, boolean modify, int priority)
		{
			this(event, resource, modify, priority);
			m_seq = seq;

		}	// BaseEvent

		/**
		* Set the event id.
		* @param id The event id string.
		*/
		protected void setEvent(String id)
		{
			if (id != null)
			{
				m_id = id;
			}
			else
			{
				m_id = "";
			}

		}	// setId

		/**
		* Set the resource id.
		* @param id The resource id string.
		*/
		protected void setResource(String id)
		{
			if (id != null)
			{
				m_resource = id;
			}
			else
			{
				m_resource = "";
			}

		}	// setResource

		/**
		* Set the session id.
		* @param id The session id string.
		*/
		protected void setSessionId(String id)
		{
			if ((id != null) && (id.length() > 0))
			{
				m_session = id;
			}
			else
			{
				m_session = null;
			}

		}	// setSessionId

		/**
		* Set the user id.
		* @param id The user id string.
		*/
		protected void setUserId(String id)
		{
			if ((id != null) && (id.length() > 0))
			{
				m_user = id;
			}
			else
			{
				m_user = null;
			}

		}	// setUserId

		/**
		* @return A representation of this event's values as a string.
		*/
		public String toString()
		{
			return m_seq + ":" + getEvent() + "@" + getResource() + "[" + (getModify() ? "m" : "a") + ", "
					+ getPriority() + "]";

		}	// toString

	}	// BaseEvent

}	// BaseEventTrackingService



